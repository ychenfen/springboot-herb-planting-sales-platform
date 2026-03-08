package com.herb.platform.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.herb.platform.entity.Crop;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * 作物Mapper接口
 */
@Mapper
public interface CropMapper extends BaseMapper<Crop> {

    /**
     * 根据地块ID查询作物数量
     */
    @Select("SELECT COUNT(*) FROM herb_crop WHERE field_id = #{fieldId} AND deleted = 0")
    int countByFieldId(@Param("fieldId") Long fieldId);

    /**
     * 根据用户ID查询作物数量
     */
    @Select("SELECT COUNT(*) FROM herb_crop WHERE user_id = #{userId} AND deleted = 0")
    int countByUserId(@Param("userId") Long userId);

    /**
     * 根据用户ID统计生长中的作物数量
     */
    @Select("SELECT COUNT(*) FROM herb_crop WHERE user_id = #{userId} AND status = 1 AND deleted = 0")
    int countGrowingByUserId(@Param("userId") Long userId);
}
