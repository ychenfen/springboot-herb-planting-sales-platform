package com.herb.platform.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.herb.platform.entity.Config;
import org.apache.ibatis.annotations.Mapper;

/**
 * 系统配置Mapper接口
 */
@Mapper
public interface ConfigMapper extends BaseMapper<Config> {
}
