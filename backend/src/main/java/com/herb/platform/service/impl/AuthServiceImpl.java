package com.herb.platform.service.impl;

import com.herb.platform.common.Constants;
import com.herb.platform.common.ResponseCode;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.herb.platform.dto.LoginDTO;
import com.herb.platform.dto.RegisterDTO;
import com.herb.platform.dto.UpdatePasswordDTO;
import com.herb.platform.dto.UpdateProfileDTO;
import com.herb.platform.entity.Role;
import com.herb.platform.entity.UserRole;
import com.herb.platform.entity.User;
import com.herb.platform.exception.BusinessException;
import com.herb.platform.mapper.RoleMapper;
import com.herb.platform.mapper.UserRoleMapper;
import com.herb.platform.mapper.UserMapper;
import com.herb.platform.service.AuthService;
import com.herb.platform.utils.JwtUtil;
import com.herb.platform.utils.PasswordUtil;
import com.herb.platform.utils.RedisUtil;
import com.herb.platform.vo.LoginVO;
import com.herb.platform.vo.UserVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * 认证服务实现类
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
    private final RedisUtil redisUtil;

    @Override
    public LoginVO login(LoginDTO loginDTO, String ip) {
        // 查询用户
        User user = userMapper.selectByUsername(loginDTO.getUsername());
        if (user == null) {
            throw new BusinessException(ResponseCode.ACCOUNT_NOT_EXIST);
        }

        // 验证密码
        if (!passwordUtil.verify(loginDTO.getPassword(), user.getPassword())) {
            throw new BusinessException(ResponseCode.LOGIN_FAILED);
        }

        // 检查用户状态
        if (user.getStatus() == Constants.USER_STATUS_DISABLED) {
            throw new BusinessException(ResponseCode.ACCOUNT_DISABLED);
        }

        // 生成Token
        String token = jwtUtil.generateToken(user.getId(), user.getUsername(), user.getUserType());

        // 存入Redis
        String tokenKey = Constants.REDIS_TOKEN_PREFIX + user.getId();
        redisUtil.set(tokenKey, token, Constants.TOKEN_EXPIRE_TIME, TimeUnit.SECONDS);

        // 更新登录信息
        user.setLastLoginTime(LocalDateTime.now());
        user.setLastLoginIp(ip);
        userMapper.updateById(user);

        // 构建返回对象
        LoginVO loginVO = new LoginVO();
        loginVO.setToken(token);
        loginVO.setUserId(user.getId());
        loginVO.setUsername(user.getUsername());
        loginVO.setRealName(user.getRealName());
        loginVO.setAvatar(user.getAvatar());
        loginVO.setUserType(user.getUserType());
        loginVO.setUserTypeName(getUserTypeName(user.getUserType()));

        log.info("用户登录成功: username={}, ip={}", user.getUsername(), ip);
        return loginVO;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void register(RegisterDTO registerDTO) {
        // 验证确认密码
        if (registerDTO.getConfirmPassword() != null
                && !registerDTO.getPassword().equals(registerDTO.getConfirmPassword())) {
            throw new BusinessException("两次输入的密码不一致");
        }

        // 检查用户名是否存在
        if (userMapper.countByUsername(registerDTO.getUsername()) > 0) {
            throw new BusinessException(ResponseCode.USER_EXISTS);
        }

        // 检查手机号是否存在
        if (registerDTO.getPhone() != null && userMapper.countByPhone(registerDTO.getPhone()) > 0) {
            throw new BusinessException(ResponseCode.PHONE_EXISTS);
        }

        // 创建用户
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
        log.info("用户注册成功: username={}", user.getUsername());
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
        String tokenKey = Constants.REDIS_TOKEN_PREFIX + userId;
        redisUtil.delete(tokenKey);
        log.info("用户退出登录: userId={}", userId);
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

        // 密码修改后使旧Token失效
        String tokenKey = Constants.REDIS_TOKEN_PREFIX + userId;
        redisUtil.delete(tokenKey);
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
            case Constants.USER_TYPE_BUYER:
                return "采购商";
            case Constants.USER_TYPE_ADMIN:
                return "管理员";
            default:
                return "";
        }
    }

    private void assignDefaultRole(Long userId, Integer userType) {
        String roleCode;
        if (userType == null) {
            return;
        }
        switch (userType) {
            case Constants.USER_TYPE_FARMER:
                roleCode = "ROLE_FARMER";
                break;
            case Constants.USER_TYPE_BUYER:
                roleCode = "ROLE_BUYER";
                break;
            case Constants.USER_TYPE_ADMIN:
                roleCode = "ROLE_ADMIN";
                break;
            default:
                roleCode = null;
        }
        if (roleCode == null) {
            return;
        }

        Role role = roleMapper.selectOne(new LambdaQueryWrapper<Role>()
                .eq(Role::getRoleCode, roleCode)
                .eq(Role::getDeleted, 0)
                .eq(Role::getStatus, 1));
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
