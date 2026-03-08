package com.herb.platform.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.herb.platform.entity.FarmRecord;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 农事记录Mapper接口
 */
@Mapper
public interface FarmRecordMapper extends BaseMapper<FarmRecord> {

    /**
     * 根据作物ID查询农事记录数量
     */
    @Select("SELECT COUNT(*) FROM herb_farm_record WHERE crop_id = #{cropId} AND deleted = 0")
    int countByCropId(@Param("cropId") Long cropId);

    /**
     * 根据作物ID查询最近的农事记录
     */
    @Select("SELECT * FROM herb_farm_record WHERE crop_id = #{cropId} AND deleted = 0 ORDER BY activity_date DESC, activity_time DESC LIMIT #{limit}")
    List<FarmRecord> selectRecentByCropId(@Param("cropId") Long cropId, @Param("limit") int limit);
}
