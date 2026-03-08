package com.herb.platform.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.herb.platform.common.Constants;
import com.herb.platform.common.ResponseCode;
import com.herb.platform.dto.OrderDTO;
import com.herb.platform.entity.Order;
import com.herb.platform.entity.Supply;
import com.herb.platform.entity.User;
import com.herb.platform.exception.BusinessException;
import com.herb.platform.mapper.OrderMapper;
import com.herb.platform.mapper.SupplyMapper;
import com.herb.platform.mapper.UserMapper;
import com.herb.platform.service.OrderService;
import com.herb.platform.vo.OrderVO;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 订单服务实现类
 */
@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderMapper orderMapper;
    private final SupplyMapper supplyMapper;
    private final UserMapper userMapper;

    @Override
    public IPage<OrderVO> page(Long userId, Integer userType, Integer orderStatus, int pageNum, int pageSize) {
        Page<Order> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<Order> wrapper = new LambdaQueryWrapper<>();

        // 根据用户类型查询
        if (userType == Constants.USER_TYPE_FARMER) {
            wrapper.eq(Order::getSellerId, userId);
        } else if (userType == Constants.USER_TYPE_BUYER) {
            wrapper.eq(Order::getBuyerId, userId);
        } else {
            // 管理员可以查看所有
        }

        if (orderStatus != null) {
            wrapper.eq(Order::getOrderStatus, orderStatus);
        }
        wrapper.orderByDesc(Order::getCreateTime);

        IPage<Order> orderPage = orderMapper.selectPage(page, wrapper);
        return orderPage.convert(this::convertToVO);
    }

    @Override
    public OrderVO getById(Long id) {
        Order order = orderMapper.selectById(id);
        if (order == null) {
            throw new BusinessException(ResponseCode.DATA_NOT_FOUND);
        }
        return convertToVO(order);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createOrder(Long buyerId, OrderDTO dto) {
        // 查询供应信息
        Supply supply = supplyMapper.selectById(dto.getSupplyId());
        if (supply == null) {
            throw new BusinessException("供应信息不存在");
        }
        if (dto.getQuantity() == null || dto.getQuantity().compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessException("购买数量必须大于0");
        }
        if (supply.getStatus() != 1) {
            throw new BusinessException("该商品已下架或售罄");
        }
        if (supply.getPrice() == null) {
            throw new BusinessException("供应价格未设置，无法下单");
        }
        if (supply.getRemainingQuantity() == null || supply.getRemainingQuantity().compareTo(dto.getQuantity()) < 0) {
            throw new BusinessException("库存不足");
        }

        // 创建订单
        Order order = new Order();
        order.setOrderNo(orderMapper.generateOrderNo());
        order.setSupplyId(supply.getId());
        order.setSellerId(supply.getUserId());
        order.setBuyerId(buyerId);
        order.setHerbName(supply.getHerbName());
        order.setHerbVariety(supply.getHerbVariety());
        order.setQuantity(dto.getQuantity());
        order.setUnitPrice(supply.getPrice());
        order.setTotalAmount(supply.getPrice().multiply(dto.getQuantity()));
        order.setOrderStatus(Constants.ORDER_STATUS_PENDING);
        order.setPaymentStatus(0);
        order.setDeliveryAddress(dto.getDeliveryAddress());
        order.setDeliveryPhone(dto.getDeliveryPhone());
        order.setDeliveryName(dto.getDeliveryName());
        order.setRemark(dto.getRemark());
        order.setCreateTime(LocalDateTime.now());
        order.setUpdateTime(LocalDateTime.now());

        orderMapper.insert(order);

        // 扣减库存
        int rows = supplyMapper.decrementRemainingQuantity(supply.getId(), dto.getQuantity());
        if (rows == 0) {
            throw new BusinessException("库存不足");
        }

        // 检查是否售罄
        Supply updatedSupply = supplyMapper.selectById(supply.getId());
        if (updatedSupply.getRemainingQuantity().compareTo(BigDecimal.ZERO) <= 0) {
            updatedSupply.setStatus(2); // 已售罄
            supplyMapper.updateById(updatedSupply);
        }

        return order.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void confirmOrder(Long sellerId, Long orderId) {
        Order order = orderMapper.selectById(orderId);
        if (order == null) {
            throw new BusinessException(ResponseCode.DATA_NOT_FOUND);
        }
        if (!order.getSellerId().equals(sellerId)) {
            throw new BusinessException(ResponseCode.FORBIDDEN, "无权操作此订单");
        }
        if (order.getOrderStatus() != Constants.ORDER_STATUS_PENDING) {
            throw new BusinessException("订单状态不正确");
        }

        order.setOrderStatus(Constants.ORDER_STATUS_CONFIRMED);
        order.setConfirmTime(LocalDateTime.now());
        order.setUpdateTime(LocalDateTime.now());
        orderMapper.updateById(order);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deliverOrder(Long sellerId, Long orderId, String logisticsCompany, String logisticsNo) {
        Order order = orderMapper.selectById(orderId);
        if (order == null) {
            throw new BusinessException(ResponseCode.DATA_NOT_FOUND);
        }
        if (!order.getSellerId().equals(sellerId)) {
            throw new BusinessException(ResponseCode.FORBIDDEN, "无权操作此订单");
        }
        if (order.getOrderStatus() != Constants.ORDER_STATUS_CONFIRMED) {
            throw new BusinessException("订单状态不正确");
        }

        order.setOrderStatus(Constants.ORDER_STATUS_DELIVERING);
        order.setLogisticsCompany(logisticsCompany);
        order.setLogisticsNo(logisticsNo);
        order.setDeliveryTime(LocalDateTime.now());
        order.setUpdateTime(LocalDateTime.now());
        orderMapper.updateById(order);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void completeOrder(Long buyerId, Long orderId) {
        Order order = orderMapper.selectById(orderId);
        if (order == null) {
            throw new BusinessException(ResponseCode.DATA_NOT_FOUND);
        }
        if (!order.getBuyerId().equals(buyerId)) {
            throw new BusinessException(ResponseCode.FORBIDDEN, "无权操作此订单");
        }
        if (order.getOrderStatus() != Constants.ORDER_STATUS_DELIVERING) {
            throw new BusinessException("订单状态不正确");
        }

        order.setOrderStatus(Constants.ORDER_STATUS_COMPLETED);
        order.setPaymentStatus(1); // 已支付
        order.setCompleteTime(LocalDateTime.now());
        order.setUpdateTime(LocalDateTime.now());
        orderMapper.updateById(order);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void cancelOrder(Long userId, Long orderId, String cancelReason) {
        Order order = orderMapper.selectById(orderId);
        if (order == null) {
            throw new BusinessException(ResponseCode.DATA_NOT_FOUND);
        }
        if (!order.getBuyerId().equals(userId) && !order.getSellerId().equals(userId)) {
            throw new BusinessException(ResponseCode.FORBIDDEN, "无权操作此订单");
        }
        if (order.getOrderStatus() >= Constants.ORDER_STATUS_DELIVERING) {
            throw new BusinessException("订单已发货，无法取消");
        }

        order.setOrderStatus(Constants.ORDER_STATUS_CANCELLED);
        order.setCancelReason(cancelReason);
        order.setCancelTime(LocalDateTime.now());
        order.setUpdateTime(LocalDateTime.now());
        orderMapper.updateById(order);

        // 恢复库存
        Supply supply = supplyMapper.selectById(order.getSupplyId());
        if (supply != null) {
            supply.setRemainingQuantity(supply.getRemainingQuantity().add(order.getQuantity()));
            if (supply.getStatus() == 2) {
                supply.setStatus(1);
            }
            supplyMapper.updateById(supply);
        }
    }

    private OrderVO convertToVO(Order order) {
        OrderVO vo = new OrderVO();
        BeanUtils.copyProperties(order, vo);
        vo.setOrderStatusName(getOrderStatusName(order.getOrderStatus()));
        vo.setPaymentStatusName(getPaymentStatusName(order.getPaymentStatus()));

        User seller = userMapper.selectById(order.getSellerId());
        if (seller != null) {
            vo.setSellerName(seller.getRealName() != null ? seller.getRealName() : seller.getUsername());
        }

        User buyer = userMapper.selectById(order.getBuyerId());
        if (buyer != null) {
            vo.setBuyerName(buyer.getRealName() != null ? buyer.getRealName() : buyer.getUsername());
        }

        return vo;
    }

    private String getOrderStatusName(Integer status) {
        if (status == null) return "";
        switch (status) {
            case 1: return "待确认";
            case 2: return "已确认";
            case 3: return "配送中";
            case 4: return "已完成";
            case 5: return "已取消";
            case 6: return "已退款";
            default: return "";
        }
    }

    private String getPaymentStatusName(Integer status) {
        if (status == null) return "";
        switch (status) {
            case 0: return "未支付";
            case 1: return "已支付";
            case 2: return "已退款";
            default: return "";
        }
    }
}
