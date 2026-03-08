package com.herb.platform.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.herb.platform.entity.Log;
import org.apache.ibatis.annotations.Mapper;

/**
 * 系统日志Mapper接口
 */
@Mapper
public interface LogMapper extends BaseMapper<Log> {
}
