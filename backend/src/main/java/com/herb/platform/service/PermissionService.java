package com.herb.platform.service;

import com.herb.platform.dto.PermissionDTO;
import com.herb.platform.vo.PermissionVO;

import java.util.List;

/**
 * 权限管理服务接口
 */
public interface PermissionService {

    /**
     * 获取所有权限（树形结构）
     */
    List<PermissionVO> listTree();

    /**
     * 获取所有权限（平铺）
     */
    List<PermissionVO> listAll();

    /**
     * 根据用户ID获取权限
     */
    List<PermissionVO> listByUserId(Long userId);

    /**
     * 根据角色ID获取权限
     */
    List<PermissionVO> listByRoleId(Long roleId);

    /**
     * 获取用户菜单
     */
    List<PermissionVO> getUserMenus(Long userId);

    /**
     * 创建权限
     */
    void create(PermissionDTO dto);

    /**
     * 更新权限
     */
    void update(PermissionDTO dto);

    /**
     * 删除权限
     */
    void delete(Long id);
}
