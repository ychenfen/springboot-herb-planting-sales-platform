package com.herb.platform.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 订单实体类
 */
@Data
@TableName("herb_order")
public class Order implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Long id;

    private String orderNo;

    private Long supplyId;

    private Long sellerId;

    private Long buyerId;

    private String herbName;

    private String herbVariety;

    private BigDecimal quantity;

    private BigDecimal unitPrice;

    private BigDecimal totalAmount;

    private Integer orderStatus;

    private Integer paymentStatus;

    private String paymentMethod;

    private String deliveryAddress;

    private String deliveryPhone;

    private String deliveryName;

    private String logisticsCompany;

    private String logisticsNo;

    private LocalDateTime confirmTime;

    private LocalDateTime deliveryTime;

    private LocalDateTime completeTime;

    private LocalDateTime cancelTime;

    private String cancelReason;

    private String remark;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    @TableLogic
    private Integer deleted;
}
