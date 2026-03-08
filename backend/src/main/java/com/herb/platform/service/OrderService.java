package com.herb.platform.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.herb.platform.dto.OrderDTO;
import com.herb.platform.vo.OrderVO;

/**
 * 订单服务接口
 */
public interface OrderService {

    /**
     * 分页查询订单
     */
    IPage<OrderVO> page(Long userId, Integer userType, Integer orderStatus, int pageNum, int pageSize);

    /**
     * 根据ID查询订单详情
     */
    OrderVO getById(Long id);

    /**
     * 创建订单
     */
    Long createOrder(Long buyerId, OrderDTO dto);

    /**
     * 卖家确认订单
     */
    void confirmOrder(Long sellerId, Long orderId);

    /**
     * 卖家发货
     */
    void deliverOrder(Long sellerId, Long orderId, String logisticsCompany, String logisticsNo);

    /**
     * 买家确认收货
     */
    void completeOrder(Long buyerId, Long orderId);

    /**
     * 取消订单
     */
    void cancelOrder(Long userId, Long orderId, String cancelReason);
}
