package com.herb.platform.service.impl;

import com.herb.platform.common.Constants;
import com.herb.platform.entity.Order;
import com.herb.platform.exception.BusinessException;
import com.herb.platform.mapper.OrderMapper;
import com.herb.platform.mapper.SupplyMapper;
import com.herb.platform.mapper.UserMapper;
import com.herb.platform.vo.OrderVO;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatcher;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OrderServiceImplTest {

    @Mock
    private OrderMapper orderMapper;

    @Mock
    private SupplyMapper supplyMapper;

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private OrderServiceImpl orderService;

    @Test
    void getByIdAllowsAdminToViewAnyOrder() {
        Order order = buildOrder();
        when(orderMapper.selectById(1L)).thenReturn(order);

        OrderVO result = orderService.getById(99L, Constants.USER_TYPE_ADMIN, 1L);

        assertEquals(1L, result.getId());
        assertEquals("ORD-001", result.getOrderNo());
    }

    @Test
    void getByIdRejectsNonParticipant() {
        when(orderMapper.selectById(1L)).thenReturn(buildOrder());

        assertThrows(BusinessException.class, () -> orderService.getById(99L, Constants.USER_TYPE_BUYER, 1L));
    }

    @Test
    void deliverOrderTrimsLogisticsInfoBeforePersist() {
        Order order = buildOrder();
        order.setOrderStatus(Constants.ORDER_STATUS_CONFIRMED);
        when(orderMapper.selectById(1L)).thenReturn(order);

        orderService.deliverOrder(10L, 1L, " 顺丰速运 ", " SF123456 ");

        verify(orderMapper).updateById(argThat(matchesDeliveredOrder()));
    }

    private ArgumentMatcher<Order> matchesDeliveredOrder() {
        return updated ->
                updated != null
                        && updated.getOrderStatus() == Constants.ORDER_STATUS_DELIVERING
                        && "顺丰速运".equals(updated.getLogisticsCompany())
                        && "SF123456".equals(updated.getLogisticsNo());
    }

    private Order buildOrder() {
        Order order = new Order();
        order.setId(1L);
        order.setOrderNo("ORD-001");
        order.setSellerId(10L);
        order.setBuyerId(20L);
        order.setOrderStatus(Constants.ORDER_STATUS_PENDING);
        return order;
    }
}
