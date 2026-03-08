package com.herb.platform.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.herb.platform.entity.UserRole;
import org.apache.ibatis.annotations.Mapper;

/**
 * 用户角色关联Mapper
 */
@Mapper
public interface UserRoleMapper extends BaseMapper<UserRole> {
}
