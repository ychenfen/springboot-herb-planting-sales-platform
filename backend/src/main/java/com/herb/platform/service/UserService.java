package com.herb.platform.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.herb.platform.dto.UserDTO;
import com.herb.platform.vo.UserVO;

/**
 * 用户管理服务接口
 */
public interface UserService {

    /**
     * 分页查询用户
     */
    IPage<UserVO> page(String username, String phone, Integer userType, Integer status, int pageNum, int pageSize);

    /**
     * 根据ID获取用户
     */
    UserVO getById(Long id);

    /**
     * 创建用户
     */
    Long create(UserDTO dto);

    /**
     * 更新用户
     */
    void update(UserDTO dto);

    /**
     * 删除用户
     */
    void delete(Long id);

    /**
     * 更新用户状态
     */
    void updateStatus(Long id, Integer status);

    /**
     * 重置密码
     */
    void resetPassword(Long id, String newPassword);

    /**
     * 分配角色
     */
    void assignRoles(Long userId, java.util.List<Long> roleIds);
}
