package com.herb.platform.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.herb.platform.entity.Demand;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

/**
 * 需求信息Mapper接口
 */
@Mapper
public interface DemandMapper extends BaseMapper<Demand> {

    /**
     * 增加浏览次数
     */
    @Update("UPDATE herb_demand SET view_count = view_count + 1 WHERE id = #{id}")
    int incrementViewCount(@Param("id") Long id);
}
