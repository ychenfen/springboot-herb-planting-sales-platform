package com.herb.platform.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.herb.platform.dto.OrderDTO;
import com.herb.platform.vo.OrderVO;

/**
 * Order service.
 */
public interface OrderService {

    IPage<OrderVO> page(Long userId, Integer userType, String orderNo, Integer orderStatus, int pageNum, int pageSize);

    OrderVO getById(Long userId, Integer userType, Long id);

    Long createOrder(Long buyerId, OrderDTO dto);

    void confirmOrder(Long sellerId, Long orderId);

    void deliverOrder(Long sellerId, Long orderId, String logisticsCompany, String logisticsNo);

    void completeOrder(Long buyerId, Long orderId);

    void cancelOrder(Long userId, Long orderId, String cancelReason);
}
