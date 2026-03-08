package com.herb.platform.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.herb.platform.dto.RoleDTO;
import com.herb.platform.vo.RoleVO;

import java.util.List;

/**
 * 角色管理服务接口
 */
public interface RoleService {

    /**
     * 分页查询角色
     */
    IPage<RoleVO> page(String roleName, Integer status, int pageNum, int pageSize);

    /**
     * 获取所有角色列表
     */
    List<RoleVO> listAll();

    /**
     * 根据ID获取角色
     */
    RoleVO getById(Long id);

    /**
     * 创建角色
     */
    Long create(RoleDTO dto);

    /**
     * 更新角色
     */
    void update(RoleDTO dto);

    /**
     * 删除角色
     */
    void delete(Long id);

    /**
     * 分配权限
     */
    void assignPermissions(Long roleId, List<Long> permissionIds);
}
