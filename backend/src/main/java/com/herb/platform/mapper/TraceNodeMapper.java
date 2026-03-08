package com.herb.platform.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.herb.platform.entity.TraceNode;
import org.apache.ibatis.annotations.Mapper;

/**
 * 溯源节点Mapper
 */
@Mapper
public interface TraceNodeMapper extends BaseMapper<TraceNode> {
}
