package com.herb.platform.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.herb.platform.common.Constants;
import com.herb.platform.common.ResponseCode;
import com.herb.platform.dto.LoginDTO;
import com.herb.platform.dto.RegisterDTO;
import com.herb.platform.dto.UpdatePasswordDTO;
import com.herb.platform.dto.UpdateProfileDTO;
import com.herb.platform.entity.Role;
import com.herb.platform.entity.User;
import com.herb.platform.entity.UserRole;
import com.herb.platform.exception.BusinessException;
import com.herb.platform.mapper.RoleMapper;
import com.herb.platform.mapper.UserMapper;
import com.herb.platform.mapper.UserRoleMapper;
import com.herb.platform.service.AuthService;
import com.herb.platform.service.TokenStoreService;
import com.herb.platform.utils.JwtUtil;
import com.herb.platform.utils.PasswordUtil;
import com.herb.platform.vo.LoginVO;
import com.herb.platform.vo.UserVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * Authentication service implementation.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserMapper userMapper;
    private final RoleMapper roleMapper;
    private final UserRoleMapper userRoleMapper;
    private final JwtUtil jwtUtil;
    private final PasswordUtil passwordUtil;
    private final TokenStoreService tokenStoreService;

    @Override
    public LoginVO login(LoginDTO loginDTO, String ip) {
        User user = userMapper.selectByUsername(loginDTO.getUsername());
        if (user == null) {
            throw new BusinessException(ResponseCode.ACCOUNT_NOT_EXIST);
        }

        if (!passwordUtil.verify(loginDTO.getPassword(), user.getPassword())) {
            throw new BusinessException(ResponseCode.LOGIN_FAILED);
        }

        if (user.getStatus() == Constants.USER_STATUS_DISABLED) {
            throw new BusinessException(ResponseCode.ACCOUNT_DISABLED);
        }

        String token = jwtUtil.generateToken(user.getId(), user.getUsername(), user.getUserType());
        tokenStoreService.set(Constants.REDIS_TOKEN_PREFIX + user.getId(),
                token, Constants.TOKEN_EXPIRE_TIME, TimeUnit.SECONDS);

        user.setLastLoginTime(LocalDateTime.now());
        user.setLastLoginIp(ip);
        userMapper.updateById(user);

        LoginVO loginVO = new LoginVO();
        loginVO.setToken(token);
        loginVO.setUserId(user.getId());
        loginVO.setUsername(user.getUsername());
        loginVO.setRealName(user.getRealName());
        loginVO.setAvatar(user.getAvatar());
        loginVO.setUserType(user.getUserType());
        loginVO.setUserTypeName(getUserTypeName(user.getUserType()));
        return loginVO;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void register(RegisterDTO registerDTO) {
        if (registerDTO.getConfirmPassword() != null
                && !registerDTO.getPassword().equals(registerDTO.getConfirmPassword())) {
            throw new BusinessException("两次输入的密码不一致");
        }
        if (userMapper.countByUsername(registerDTO.getUsername()) > 0) {
            throw new BusinessException(ResponseCode.USER_EXISTS);
        }
        if (registerDTO.getPhone() != null && userMapper.countByPhone(registerDTO.getPhone()) > 0) {
            throw new BusinessException(ResponseCode.PHONE_EXISTS);
        }

        User user = new User();
        user.setUsername(registerDTO.getUsername());
        user.setPassword(passwordUtil.encrypt(registerDTO.getPassword()));
        user.setRealName(registerDTO.getRealName());
        user.setPhone(registerDTO.getPhone());
        user.setEmail(registerDTO.getEmail());
        user.setUserType(registerDTO.getUserType());
        user.setStatus(Constants.USER_STATUS_NORMAL);
        user.setCreateTime(LocalDateTime.now());
        user.setUpdateTime(LocalDateTime.now());
        userMapper.insert(user);

        assignDefaultRole(user.getId(), user.getUserType());
        log.info("User registered: {}", user.getUsername());
    }

    @Override
    public UserVO getCurrentUser(Long userId) {
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException(ResponseCode.ACCOUNT_NOT_EXIST);
        }

        UserVO userVO = new UserVO();
        BeanUtils.copyProperties(user, userVO);
        userVO.setUserTypeName(getUserTypeName(user.getUserType()));
        userVO.setStatusName(user.getStatus() != null && user.getStatus() == 1 ? "正常" : "禁用");
        fillUserRoles(userVO, user.getId());
        return userVO;
    }

    @Override
    public void logout(Long userId) {
        tokenStoreService.delete(Constants.REDIS_TOKEN_PREFIX + userId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updatePassword(Long userId, UpdatePasswordDTO dto) {
        User user = userMapper.selectById(userId);
        if (user == null || user.getDeleted() == 1) {
            throw new BusinessException(ResponseCode.ACCOUNT_NOT_EXIST);
        }
        if (!passwordUtil.verify(dto.getOldPassword(), user.getPassword())) {
            throw new BusinessException(ResponseCode.OLD_PASSWORD_ERROR);
        }
        if (!dto.getNewPassword().equals(dto.getConfirmPassword())) {
            throw new BusinessException("两次输入的密码不一致");
        }

        user.setPassword(passwordUtil.encrypt(dto.getNewPassword()));
        user.setUpdateTime(LocalDateTime.now());
        userMapper.updateById(user);
        tokenStoreService.delete(Constants.REDIS_TOKEN_PREFIX + userId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateProfile(Long userId, UpdateProfileDTO dto) {
        User user = userMapper.selectById(userId);
        if (user == null || user.getDeleted() == 1) {
            throw new BusinessException(ResponseCode.ACCOUNT_NOT_EXIST);
        }

        if (dto.getPhone() != null && !dto.getPhone().equals(user.getPhone())) {
            LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(User::getPhone, dto.getPhone());
            wrapper.eq(User::getDeleted, 0);
            wrapper.ne(User::getId, userId);
            if (userMapper.selectCount(wrapper) > 0) {
                throw new BusinessException(ResponseCode.PHONE_EXISTS);
            }
        }

        if (dto.getRealName() != null) {
            user.setRealName(dto.getRealName());
        }
        if (dto.getPhone() != null) {
            user.setPhone(dto.getPhone());
        }
        if (dto.getEmail() != null) {
            user.setEmail(dto.getEmail());
        }
        if (dto.getAvatar() != null) {
            user.setAvatar(dto.getAvatar());
        }

        user.setUpdateTime(LocalDateTime.now());
        userMapper.updateById(user);
    }

    private String getUserTypeName(Integer userType) {
        if (userType == null) {
            return "";
        }
        switch (userType) {
            case Constants.USER_TYPE_FARMER:
                return "种植户";
            case Constants.USER_TYPE_MERCHANT:
                return "商家";
            case Constants.USER_TYPE_ADMIN:
                return "管理员";
            case Constants.USER_TYPE_USER:
                return "普通用户";
            default:
                return "";
        }
    }

    private void assignDefaultRole(Long userId, Integer userType) {
        List<String> roleCodes;
        if (userType == null) {
            return;
        }
        switch (userType) {
            case Constants.USER_TYPE_FARMER:
                roleCodes = Arrays.asList("ROLE_FARMER");
                break;
            case Constants.USER_TYPE_MERCHANT:
                roleCodes = Arrays.asList("ROLE_MERCHANT", "ROLE_BUYER");
                break;
            case Constants.USER_TYPE_ADMIN:
                roleCodes = Arrays.asList("ROLE_ADMIN");
                break;
            case Constants.USER_TYPE_USER:
                roleCodes = Arrays.asList("ROLE_USER");
                break;
            default:
                roleCodes = null;
        }
        if (roleCodes == null || roleCodes.isEmpty()) {
            return;
        }

        Role role = roleMapper.selectOne(new LambdaQueryWrapper<Role>()
                .in(Role::getRoleCode, roleCodes)
                .eq(Role::getDeleted, 0)
                .eq(Role::getStatus, 1)
                .last("LIMIT 1"));
        if (role == null) {
            throw new BusinessException(ResponseCode.DATA_NOT_FOUND, "默认角色不存在");
        }

        UserRole userRole = new UserRole();
        userRole.setUserId(userId);
        userRole.setRoleId(role.getId());
        userRoleMapper.insert(userRole);
    }

    private void fillUserRoles(UserVO userVO, Long userId) {
        List<Role> roles = roleMapper.selectByUserId(userId);
        if (roles != null && !roles.isEmpty()) {
            userVO.setRoleIds(roles.stream().map(Role::getId).collect(Collectors.toList()));
            userVO.setRoleNames(roles.stream().map(Role::getRoleName).collect(Collectors.joining(",")));
        }
    }
}
