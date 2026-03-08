package com.herb.platform.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.herb.platform.entity.Order;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * 订单Mapper接口
 */
@Mapper
public interface OrderMapper extends BaseMapper<Order> {

    /**
     * 生成订单编号
     */
    @Select("SELECT CONCAT('ORD', DATE_FORMAT(NOW(), '%Y%m%d%H%i%s'), LPAD(FLOOR(RAND() * 10000), 4, '0'))")
    String generateOrderNo();

    /**
     * 根据卖家ID统计订单数
     */
    @Select("SELECT COUNT(*) FROM herb_order WHERE seller_id = #{sellerId} AND deleted = 0")
    int countBySellerId(@Param("sellerId") Long sellerId);

    /**
     * 根据买家ID统计订单数
     */
    @Select("SELECT COUNT(*) FROM herb_order WHERE buyer_id = #{buyerId} AND deleted = 0")
    int countByBuyerId(@Param("buyerId") Long buyerId);
}
