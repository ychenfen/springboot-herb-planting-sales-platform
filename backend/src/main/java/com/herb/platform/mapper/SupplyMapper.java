package com.herb.platform.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.herb.platform.entity.Supply;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

/**
 * 供应信息Mapper接口
 */
@Mapper
public interface SupplyMapper extends BaseMapper<Supply> {

    /**
     * 增加浏览次数
     */
    @Update("UPDATE herb_supply SET view_count = view_count + 1 WHERE id = #{id}")
    int incrementViewCount(@Param("id") Long id);

    /**
     * 更新剩余数量
     */
    @Update("UPDATE herb_supply SET remaining_quantity = remaining_quantity - #{quantity} WHERE id = #{id} AND remaining_quantity >= #{quantity}")
    int decrementRemainingQuantity(@Param("id") Long id, @Param("quantity") java.math.BigDecimal quantity);

    /**
     * 增加收藏次数
     */
    @Update("UPDATE herb_supply SET favorite_count = favorite_count + 1 WHERE id = #{id}")
    int incrementFavoriteCount(@Param("id") Long id);

    /**
     * 减少收藏次数
     */
    @Update("UPDATE herb_supply SET favorite_count = IF(favorite_count > 0, favorite_count - 1, 0) WHERE id = #{id}")
    int decrementFavoriteCount(@Param("id") Long id);
}
