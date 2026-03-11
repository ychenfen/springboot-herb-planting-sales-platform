package com.herb.platform.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.herb.platform.common.ResponseCode;
import com.herb.platform.dto.UserDTO;
import com.herb.platform.entity.Role;
import com.herb.platform.entity.User;
import com.herb.platform.entity.UserRole;
import com.herb.platform.exception.BusinessException;
import com.herb.platform.mapper.RoleMapper;
import com.herb.platform.mapper.UserMapper;
import com.herb.platform.mapper.UserRoleMapper;
import com.herb.platform.service.UserService;
import com.herb.platform.vo.UserVO;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.stream.Collectors;

/**
 * User service implementation.
 */
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserMapper userMapper;
    private final RoleMapper roleMapper;
    private final UserRoleMapper userRoleMapper;
    private final com.herb.platform.utils.PasswordUtil passwordUtil;

    @Override
    public IPage<UserVO> page(String username, String phone, Integer userType, Integer status, int pageNum, int pageSize) {
        Page<User> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(username)) {
            wrapper.like(User::getUsername, username);
        }
        if (StringUtils.hasText(phone)) {
            wrapper.like(User::getPhone, phone);
        }
        if (userType != null) {
            wrapper.eq(User::getUserType, userType);
        }
        if (status != null) {
            wrapper.eq(User::getStatus, status);
        }
        wrapper.eq(User::getDeleted, 0);
        wrapper.orderByDesc(User::getCreateTime);
        return userMapper.selectPage(page, wrapper).convert(this::convertToVO);
    }

    @Override
    public UserVO getById(Long id) {
        User user = userMapper.selectById(id);
        if (user == null || user.getDeleted() == 1) {
            throw new BusinessException(ResponseCode.DATA_NOT_FOUND);
        }
        return convertToVO(user);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long create(UserDTO dto) {
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getUsername, dto.getUsername());
        wrapper.eq(User::getDeleted, 0);
        if (userMapper.selectCount(wrapper) > 0) {
            throw new BusinessException("用户名已存在");
        }

        if (StringUtils.hasText(dto.getPhone())) {
            LambdaQueryWrapper<User> phoneWrapper = new LambdaQueryWrapper<>();
            phoneWrapper.eq(User::getPhone, dto.getPhone());
            phoneWrapper.eq(User::getDeleted, 0);
            if (userMapper.selectCount(phoneWrapper) > 0) {
                throw new BusinessException("手机号已存在");
            }
        }

        User user = new User();
        BeanUtils.copyProperties(dto, user);
        String password = StringUtils.hasText(dto.getPassword()) ? dto.getPassword() : "123456";
        user.setPassword(passwordUtil.encrypt(password));
        user.setStatus(dto.getStatus() != null ? dto.getStatus() : 1);
        userMapper.insert(user);

        if (dto.getRoleIds() != null && !dto.getRoleIds().isEmpty()) {
            assignRoles(user.getId(), dto.getRoleIds());
        }
        return user.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(UserDTO dto) {
        if (dto.getId() == null) {
            throw new BusinessException(ResponseCode.PARAM_ERROR, "用户ID不能为空");
        }

        User user = userMapper.selectById(dto.getId());
        if (user == null || user.getDeleted() == 1) {
            throw new BusinessException(ResponseCode.DATA_NOT_FOUND);
        }

        if (StringUtils.hasText(dto.getUsername()) && !dto.getUsername().equals(user.getUsername())) {
            LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(User::getUsername, dto.getUsername());
            wrapper.eq(User::getDeleted, 0);
            wrapper.ne(User::getId, dto.getId());
            if (userMapper.selectCount(wrapper) > 0) {
                throw new BusinessException("用户名已存在");
            }
        }

        if (StringUtils.hasText(dto.getPhone()) && !dto.getPhone().equals(user.getPhone())) {
            LambdaQueryWrapper<User> phoneWrapper = new LambdaQueryWrapper<>();
            phoneWrapper.eq(User::getPhone, dto.getPhone());
            phoneWrapper.eq(User::getDeleted, 0);
            phoneWrapper.ne(User::getId, dto.getId());
            if (userMapper.selectCount(phoneWrapper) > 0) {
                throw new BusinessException("手机号已存在");
            }
        }

        BeanUtils.copyProperties(dto, user, "id", "password", "createTime", "deleted");
        userMapper.updateById(user);

        if (dto.getRoleIds() != null) {
            assignRoles(user.getId(), dto.getRoleIds());
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(Long id) {
        User user = userMapper.selectById(id);
        if (user == null) {
            throw new BusinessException(ResponseCode.DATA_NOT_FOUND);
        }
        user.setDeleted(1);
        userMapper.updateById(user);

        LambdaQueryWrapper<UserRole> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserRole::getUserId, id);
        userRoleMapper.delete(wrapper);
    }

    @Override
    public void updateStatus(Long id, Integer status) {
        User user = userMapper.selectById(id);
        if (user == null || user.getDeleted() == 1) {
            throw new BusinessException(ResponseCode.DATA_NOT_FOUND);
        }
        user.setStatus(status);
        userMapper.updateById(user);
    }

    @Override
    public void resetPassword(Long id, String newPassword) {
        User user = userMapper.selectById(id);
        if (user == null || user.getDeleted() == 1) {
            throw new BusinessException(ResponseCode.DATA_NOT_FOUND);
        }
        String password = StringUtils.hasText(newPassword) ? newPassword : "123456";
        user.setPassword(passwordUtil.encrypt(password));
        userMapper.updateById(user);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void assignRoles(Long userId, List<Long> roleIds) {
        LambdaQueryWrapper<UserRole> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserRole::getUserId, userId);
        userRoleMapper.delete(wrapper);

        if (roleIds != null && !roleIds.isEmpty()) {
            for (Long roleId : roleIds) {
                UserRole userRole = new UserRole();
                userRole.setUserId(userId);
                userRole.setRoleId(roleId);
                userRoleMapper.insert(userRole);
            }
        }
    }

    private UserVO convertToVO(User user) {
        UserVO vo = new UserVO();
        BeanUtils.copyProperties(user, vo);
        vo.setUserTypeName(getUserTypeName(user.getUserType()));
        vo.setStatusName(user.getStatus() == 1 ? "正常" : "禁用");

        List<Role> roles = roleMapper.selectByUserId(user.getId());
        if (roles != null && !roles.isEmpty()) {
            vo.setRoleIds(roles.stream().map(Role::getId).collect(Collectors.toList()));
            vo.setRoleNames(roles.stream().map(Role::getRoleName).collect(Collectors.joining(",")));
        }
        return vo;
    }

    private String getUserTypeName(Integer userType) {
        if (userType == null) {
            return "";
        }
        switch (userType) {
            case 1:
                return "种植户";
            case 2:
                return "商家";
            case 3:
                return "管理员";
            case 4:
                return "普通用户";
            default:
                return "";
        }
    }
}
