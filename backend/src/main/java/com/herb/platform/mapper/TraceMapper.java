package com.herb.platform.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.herb.platform.entity.Trace;
import org.apache.ibatis.annotations.Mapper;

/**
 * 溯源信息Mapper
 */
@Mapper
public interface TraceMapper extends BaseMapper<Trace> {
}
