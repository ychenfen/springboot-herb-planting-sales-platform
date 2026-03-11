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
import com.herb.platform.vo.OrderTrackVO;
import com.herb.platform.vo.OrderVO;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Order service implementation.
 */
@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderMapper orderMapper;
    private final SupplyMapper supplyMapper;
    private final UserMapper userMapper;

    @Override
    public IPage<OrderVO> page(Long userId, Integer userType, String orderNo, Integer orderStatus, int pageNum, int pageSize) {
        Page<Order> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<Order> wrapper = new LambdaQueryWrapper<>();

        if (userType == Constants.USER_TYPE_FARMER) {
            wrapper.eq(Order::getSellerId, userId);
        } else if (userType == Constants.USER_TYPE_MERCHANT || userType == Constants.USER_TYPE_USER) {
            wrapper.eq(Order::getBuyerId, userId);
        }

        if (StringUtils.hasText(orderNo)) {
            wrapper.like(Order::getOrderNo, orderNo);
        }
        if (orderStatus != null) {
            wrapper.eq(Order::getOrderStatus, orderStatus);
        }
        wrapper.orderByDesc(Order::getCreateTime);
        return orderMapper.selectPage(page, wrapper).convert(this::convertToVO);
    }

    @Override
    public OrderVO getById(Long userId, Integer userType, Long id) {
        Order order = orderMapper.selectById(id);
        if (order == null) {
            throw new BusinessException(ResponseCode.DATA_NOT_FOUND);
        }

        boolean isAdmin = userType != null && userType == Constants.USER_TYPE_ADMIN;
        boolean isParticipant = order.getBuyerId().equals(userId) || order.getSellerId().equals(userId);
        if (!isAdmin && !isParticipant) {
            throw new BusinessException(ResponseCode.FORBIDDEN, "无权查看该订单");
        }
        return convertToVO(order);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createOrder(Long buyerId, OrderDTO dto) {
        Supply supply = supplyMapper.selectById(dto.getSupplyId());
        if (supply == null) {
            throw new BusinessException("供应信息不存在");
        }
        if (dto.getQuantity() == null || dto.getQuantity().compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessException("购买数量必须大于0");
        }
        if (supply.getStatus() != Constants.SUPPLY_STATUS_ACTIVE) {
            throw new BusinessException("该商品已下架或售罄");
        }
        if (supply.getPrice() == null) {
            throw new BusinessException("供应价格未配置");
        }
        if (supply.getRemainingQuantity() == null || supply.getRemainingQuantity().compareTo(dto.getQuantity()) < 0) {
            throw new BusinessException("库存不足");
        }

        BigDecimal appliedUnitPrice = resolveUnitPrice(supply, dto.getQuantity());

        Order order = new Order();
        order.setOrderNo(orderMapper.generateOrderNo());
        order.setSupplyId(supply.getId());
        order.setSellerId(supply.getUserId());
        order.setBuyerId(buyerId);
        order.setHerbName(supply.getHerbName());
        order.setHerbVariety(supply.getHerbVariety());
        order.setQuantity(dto.getQuantity());
        order.setUnitPrice(appliedUnitPrice);
        order.setTotalAmount(appliedUnitPrice.multiply(dto.getQuantity()));
        order.setOrderStatus(Constants.ORDER_STATUS_PENDING);
        order.setPaymentStatus(0);
        order.setDeliveryAddress(dto.getDeliveryAddress());
        order.setDeliveryPhone(dto.getDeliveryPhone());
        order.setDeliveryName(dto.getDeliveryName());
        order.setRemark(dto.getRemark());
        order.setCreateTime(LocalDateTime.now());
        order.setUpdateTime(LocalDateTime.now());
        orderMapper.insert(order);

        int rows = supplyMapper.decrementRemainingQuantity(supply.getId(), dto.getQuantity());
        if (rows == 0) {
            throw new BusinessException("库存不足");
        }

        Supply updatedSupply = supplyMapper.selectById(supply.getId());
        if (updatedSupply.getRemainingQuantity().compareTo(BigDecimal.ZERO) <= 0) {
            updatedSupply.setStatus(Constants.SUPPLY_STATUS_SOLD_OUT);
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
        order.setPaymentStatus(1);
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

        Supply supply = supplyMapper.selectById(order.getSupplyId());
        if (supply != null) {
            supply.setRemainingQuantity(supply.getRemainingQuantity().add(order.getQuantity()));
            if (supply.getStatus() == Constants.SUPPLY_STATUS_SOLD_OUT) {
                supply.setStatus(Constants.SUPPLY_STATUS_ACTIVE);
            }
            supplyMapper.updateById(supply);
        }
    }

    private OrderVO convertToVO(Order order) {
        OrderVO vo = new OrderVO();
        BeanUtils.copyProperties(order, vo);
        vo.setOrderStatusName(getOrderStatusName(order.getOrderStatus()));
        vo.setPaymentStatusName(getPaymentStatusName(order.getPaymentStatus()));
        vo.setTrackingNodes(buildTrackingNodes(order));

        User seller = userMapper.selectById(order.getSellerId());
        if (seller != null) {
            vo.setSellerName(seller.getRealName() != null ? seller.getRealName() : seller.getUsername());
        }

        User buyer = userMapper.selectById(order.getBuyerId());
        if (buyer != null) {
            vo.setBuyerName(buyer.getRealName() != null ? buyer.getRealName() : buyer.getUsername());
        }

        Supply supply = supplyMapper.selectById(order.getSupplyId());
        if (supply != null
                && supply.getWholesalePrice() != null
                && supply.getWholesaleMinQuantity() != null
                && order.getQuantity() != null
                && order.getQuantity().compareTo(supply.getWholesaleMinQuantity()) >= 0) {
            vo.setPricingMode("批发价");
        } else {
            vo.setPricingMode("标准价");
        }
        return vo;
    }

    private BigDecimal resolveUnitPrice(Supply supply, BigDecimal quantityKg) {
        if (supply.getWholesalePrice() != null
                && supply.getWholesaleMinQuantity() != null
                && quantityKg.compareTo(supply.getWholesaleMinQuantity()) >= 0) {
            return supply.getWholesalePrice();
        }
        return supply.getPrice();
    }

    private List<OrderTrackVO> buildTrackingNodes(Order order) {
        List<OrderTrackVO> tracks = new ArrayList<>();
        tracks.add(buildTrack(1, "下单成功", "买家已提交订单，等待卖家确认", order.getCreateTime()));
        if (order.getConfirmTime() != null) {
            tracks.add(buildTrack(2, "卖家已确认", "卖家已确认接单，正在备货", order.getConfirmTime()));
        }
        if (order.getDeliveryTime() != null) {
            String logistics = "";
            if (StringUtils.hasText(order.getLogisticsCompany()) || StringUtils.hasText(order.getLogisticsNo())) {
                logistics = "，物流: " + defaultText(order.getLogisticsCompany()) + " " + defaultText(order.getLogisticsNo());
            }
            tracks.add(buildTrack(3, "商品已发货", "物流信息已更新" + logistics, order.getDeliveryTime()));
        }
        if (order.getCompleteTime() != null) {
            tracks.add(buildTrack(4, "交易已完成", "买家已确认收货", order.getCompleteTime()));
        }
        if (order.getCancelTime() != null) {
            tracks.add(buildTrack(5, "订单已取消", defaultText(order.getCancelReason(), "订单已取消"), order.getCancelTime()));
        }
        return tracks;
    }

    private OrderTrackVO buildTrack(Integer status, String title, String description, LocalDateTime time) {
        OrderTrackVO track = new OrderTrackVO();
        track.setStatus(status);
        track.setTitle(title);
        track.setDescription(description);
        track.setTime(time);
        return track;
    }

    private String defaultText(String value) {
        return value == null ? "" : value.trim();
    }

    private String defaultText(String value, String fallback) {
        return StringUtils.hasText(value) ? value : fallback;
    }

    private String getOrderStatusName(Integer status) {
        if (status == null) {
            return "";
        }
        switch (status) {
            case 1:
                return "待确认";
            case 2:
                return "待发货";
            case 3:
                return "待收货";
            case 4:
                return "已完成";
            case 5:
                return "已取消";
            case 6:
                return "已退款";
            default:
                return "";
        }
    }

    private String getPaymentStatusName(Integer status) {
        if (status == null) {
            return "";
        }
        switch (status) {
            case 0:
                return "未支付";
            case 1:
                return "已支付";
            case 2:
                return "已退款";
            default:
                return "";
        }
    }
}
