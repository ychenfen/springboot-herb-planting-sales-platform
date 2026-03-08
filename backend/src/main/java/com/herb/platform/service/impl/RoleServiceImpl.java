package com.herb.platform.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.herb.platform.common.ResponseCode;
import com.herb.platform.dto.RoleDTO;
import com.herb.platform.entity.Permission;
import com.herb.platform.entity.Role;
import com.herb.platform.entity.RolePermission;
import com.herb.platform.exception.BusinessException;
import com.herb.platform.mapper.PermissionMapper;
import com.herb.platform.mapper.RoleMapper;
import com.herb.platform.mapper.RolePermissionMapper;
import com.herb.platform.service.RoleService;
import com.herb.platform.vo.RoleVO;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 角色管理服务实现类
 */
@Service
@RequiredArgsConstructor
public class RoleServiceImpl implements RoleService {

    private final RoleMapper roleMapper;
    private final PermissionMapper permissionMapper;
    private final RolePermissionMapper rolePermissionMapper;

    @Override
    public IPage<RoleVO> page(String roleName, Integer status, int pageNum, int pageSize) {
        Page<Role> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<Role> wrapper = new LambdaQueryWrapper<>();

        if (StringUtils.hasText(roleName)) {
            wrapper.like(Role::getRoleName, roleName);
        }
        if (status != null) {
            wrapper.eq(Role::getStatus, status);
        }
        wrapper.eq(Role::getDeleted, 0);
        wrapper.orderByAsc(Role::getId);

        IPage<Role> rolePage = roleMapper.selectPage(page, wrapper);
        return rolePage.convert(this::convertToVO);
    }

    @Override
    public List<RoleVO> listAll() {
        LambdaQueryWrapper<Role> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Role::getDeleted, 0);
        wrapper.eq(Role::getStatus, 1);
        wrapper.orderByAsc(Role::getId);
        List<Role> roles = roleMapper.selectList(wrapper);
        return roles.stream().map(this::convertToVO).collect(Collectors.toList());
    }

    @Override
    public RoleVO getById(Long id) {
        Role role = roleMapper.selectById(id);
        if (role == null || role.getDeleted() == 1) {
            throw new BusinessException(ResponseCode.DATA_NOT_FOUND);
        }
        RoleVO vo = convertToVO(role);
        // 获取权限ID列表
        List<Permission> permissions = permissionMapper.selectByRoleId(id);
        if (permissions != null) {
            vo.setPermissionIds(permissions.stream().map(Permission::getId).collect(Collectors.toList()));
        }
        return vo;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long create(RoleDTO dto) {
        // 检查角色编码是否存在
        LambdaQueryWrapper<Role> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Role::getRoleCode, dto.getRoleCode());
        wrapper.eq(Role::getDeleted, 0);
        if (roleMapper.selectCount(wrapper) > 0) {
            throw new BusinessException("角色编码已存在");
        }

        Role role = new Role();
        BeanUtils.copyProperties(dto, role);
        role.setStatus(dto.getStatus() != null ? dto.getStatus() : 1);
        roleMapper.insert(role);

        // 分配权限
        if (dto.getPermissionIds() != null && !dto.getPermissionIds().isEmpty()) {
            assignPermissions(role.getId(), dto.getPermissionIds());
        }

        return role.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(RoleDTO dto) {
        if (dto.getId() == null) {
            throw new BusinessException(ResponseCode.PARAM_ERROR, "角色ID不能为空");
        }

        Role role = roleMapper.selectById(dto.getId());
        if (role == null || role.getDeleted() == 1) {
            throw new BusinessException(ResponseCode.DATA_NOT_FOUND);
        }

        // 检查角色编码是否重复
        if (StringUtils.hasText(dto.getRoleCode()) && !dto.getRoleCode().equals(role.getRoleCode())) {
            LambdaQueryWrapper<Role> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(Role::getRoleCode, dto.getRoleCode());
            wrapper.eq(Role::getDeleted, 0);
            wrapper.ne(Role::getId, dto.getId());
            if (roleMapper.selectCount(wrapper) > 0) {
                throw new BusinessException("角色编码已存在");
            }
        }

        BeanUtils.copyProperties(dto, role, "id", "createTime", "deleted");
        roleMapper.updateById(role);

        // 更新权限
        if (dto.getPermissionIds() != null) {
            assignPermissions(role.getId(), dto.getPermissionIds());
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(Long id) {
        Role role = roleMapper.selectById(id);
        if (role == null) {
            throw new BusinessException(ResponseCode.DATA_NOT_FOUND);
        }
        // 逻辑删除
        role.setDeleted(1);
        roleMapper.updateById(role);

        // 删除角色权限关联
        LambdaQueryWrapper<RolePermission> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(RolePermission::getRoleId, id);
        rolePermissionMapper.delete(wrapper);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void assignPermissions(Long roleId, List<Long> permissionIds) {
        // 删除原有权限
        LambdaQueryWrapper<RolePermission> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(RolePermission::getRoleId, roleId);
        rolePermissionMapper.delete(wrapper);

        // 添加新权限
        if (permissionIds != null && !permissionIds.isEmpty()) {
            for (Long permissionId : permissionIds) {
                RolePermission rp = new RolePermission();
                rp.setRoleId(roleId);
                rp.setPermissionId(permissionId);
                rolePermissionMapper.insert(rp);
            }
        }
    }

    private RoleVO convertToVO(Role role) {
        RoleVO vo = new RoleVO();
        BeanUtils.copyProperties(role, vo);
        vo.setStatusName(role.getStatus() == 1 ? "正常" : "禁用");
        return vo;
    }
}
