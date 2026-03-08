package com.herb.platform.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.herb.platform.entity.Dict;
import org.apache.ibatis.annotations.Mapper;

/**
 * 数据字典Mapper
 */
@Mapper
public interface DictMapper extends BaseMapper<Dict> {
}
