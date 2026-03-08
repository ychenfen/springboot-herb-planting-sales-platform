package com.herb.platform.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.herb.platform.common.ResponseCode;
import com.herb.platform.dto.PermissionDTO;
import com.herb.platform.entity.Permission;
import com.herb.platform.entity.RolePermission;
import com.herb.platform.exception.BusinessException;
import com.herb.platform.mapper.PermissionMapper;
import com.herb.platform.mapper.RolePermissionMapper;
import com.herb.platform.service.PermissionService;
import com.herb.platform.vo.PermissionVO;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 权限管理服务实现类
 */
@Service
@RequiredArgsConstructor
public class PermissionServiceImpl implements PermissionService {

    private final PermissionMapper permissionMapper;
    private final RolePermissionMapper rolePermissionMapper;

    @Override
    public List<PermissionVO> listTree() {
        LambdaQueryWrapper<Permission> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Permission::getDeleted, 0);
        wrapper.orderByAsc(Permission::getSortOrder);
        List<Permission> permissions = permissionMapper.selectList(wrapper);

        List<PermissionVO> vos = permissions.stream().map(this::convertToVO).collect(Collectors.toList());
        return buildTree(vos, 0L);
    }

    @Override
    public List<PermissionVO> listAll() {
        LambdaQueryWrapper<Permission> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Permission::getDeleted, 0);
        wrapper.orderByAsc(Permission::getSortOrder);
        List<Permission> permissions = permissionMapper.selectList(wrapper);
        return permissions.stream().map(this::convertToVO).collect(Collectors.toList());
    }

    @Override
    public List<PermissionVO> listByUserId(Long userId) {
        List<Permission> permissions = permissionMapper.selectByUserId(userId);
        return permissions.stream().map(this::convertToVO).collect(Collectors.toList());
    }

    @Override
    public List<PermissionVO> listByRoleId(Long roleId) {
        List<Permission> permissions = permissionMapper.selectByRoleId(roleId);
        return permissions.stream().map(this::convertToVO).collect(Collectors.toList());
    }

    @Override
    public List<PermissionVO> getUserMenus(Long userId) {
        List<Permission> permissions = permissionMapper.selectByUserId(userId);
        // 只返回菜单类型的权限
        List<PermissionVO> menus = permissions.stream()
                .filter(p -> p.getPermissionType() != null && p.getPermissionType() == 1)
                .map(this::convertToVO)
                .collect(Collectors.toList());
        return buildTree(menus, 0L);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void create(PermissionDTO dto) {
        LambdaQueryWrapper<Permission> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Permission::getPermissionCode, dto.getPermissionCode());
        wrapper.eq(Permission::getDeleted, 0);
        if (permissionMapper.selectCount(wrapper) > 0) {
            throw new BusinessException(ResponseCode.DATA_EXISTS, "权限编码已存在");
        }

        Permission permission = new Permission();
        BeanUtils.copyProperties(dto, permission);
        if (permission.getParentId() == null) {
            permission.setParentId(0L);
        }
        if (permission.getStatus() == null) {
            permission.setStatus(1);
        }
        if (permission.getSortOrder() == null) {
            permission.setSortOrder(0);
        }
        permissionMapper.insert(permission);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(PermissionDTO dto) {
        if (dto.getId() == null) {
            throw new BusinessException(ResponseCode.PARAM_ERROR, "权限ID不能为空");
        }
        Permission permission = permissionMapper.selectById(dto.getId());
        if (permission == null || permission.getDeleted() == 1) {
            throw new BusinessException(ResponseCode.DATA_NOT_FOUND);
        }

        if (StringUtils.hasText(dto.getPermissionCode())
                && !dto.getPermissionCode().equals(permission.getPermissionCode())) {
            LambdaQueryWrapper<Permission> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(Permission::getPermissionCode, dto.getPermissionCode());
            wrapper.eq(Permission::getDeleted, 0);
            wrapper.ne(Permission::getId, dto.getId());
            if (permissionMapper.selectCount(wrapper) > 0) {
                throw new BusinessException(ResponseCode.DATA_EXISTS, "权限编码已存在");
            }
        }

        if (dto.getParentId() != null && dto.getParentId().equals(dto.getId())) {
            throw new BusinessException(ResponseCode.PARAM_ERROR, "父权限不能为自身");
        }

        BeanUtils.copyProperties(dto, permission, "id", "createTime", "deleted");
        if (permission.getParentId() == null) {
            permission.setParentId(0L);
        }
        if (permission.getStatus() == null) {
            permission.setStatus(1);
        }
        if (permission.getSortOrder() == null) {
            permission.setSortOrder(0);
        }
        permissionMapper.updateById(permission);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(Long id) {
        Permission permission = permissionMapper.selectById(id);
        if (permission == null || permission.getDeleted() == 1) {
            throw new BusinessException(ResponseCode.DATA_NOT_FOUND);
        }

        LambdaQueryWrapper<Permission> childWrapper = new LambdaQueryWrapper<>();
        childWrapper.eq(Permission::getParentId, id);
        childWrapper.eq(Permission::getDeleted, 0);
        if (permissionMapper.selectCount(childWrapper) > 0) {
            throw new BusinessException(ResponseCode.OPERATION_FAILED, "存在子权限，无法删除");
        }

        LambdaQueryWrapper<RolePermission> rpWrapper = new LambdaQueryWrapper<>();
        rpWrapper.eq(RolePermission::getPermissionId, id);
        rolePermissionMapper.delete(rpWrapper);

        permissionMapper.deleteById(id);
    }

    /**
     * 构建树形结构
     */
    private List<PermissionVO> buildTree(List<PermissionVO> permissions, Long parentId) {
        List<PermissionVO> tree = new ArrayList<>();
        Map<Long, List<PermissionVO>> childrenMap = permissions.stream()
                .filter(p -> p.getParentId() != null)
                .collect(Collectors.groupingBy(PermissionVO::getParentId));

        for (PermissionVO permission : permissions) {
            if (parentId.equals(permission.getParentId())) {
                permission.setChildren(buildTree(permissions, permission.getId()));
                tree.add(permission);
            }
        }
        return tree;
    }

    private PermissionVO convertToVO(Permission permission) {
        PermissionVO vo = new PermissionVO();
        BeanUtils.copyProperties(permission, vo);
        vo.setPermissionTypeName(getPermissionTypeName(permission.getPermissionType()));
        return vo;
    }

    private String getPermissionTypeName(Integer type) {
        if (type == null) return "";
        switch (type) {
            case 1: return "菜单";
            case 2: return "按钮";
            case 3: return "接口";
            default: return "";
        }
    }
}
