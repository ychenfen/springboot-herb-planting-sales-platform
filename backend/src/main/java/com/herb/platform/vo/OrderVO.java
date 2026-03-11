package com.herb.platform.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Order response.
 */
@Data
@ApiModel("订单信息")
public class OrderVO {

    @ApiModelProperty("订单ID")
    private Long id;
    @ApiModelProperty("订单编号")
    private String orderNo;
    @ApiModelProperty("供应信息ID")
    private Long supplyId;
    @ApiModelProperty("卖家ID")
    private Long sellerId;
    @ApiModelProperty("卖家名称")
    private String sellerName;
    @ApiModelProperty("买家ID")
    private Long buyerId;
    @ApiModelProperty("买家名称")
    private String buyerName;
    @ApiModelProperty("药材名称")
    private String herbName;
    @ApiModelProperty("药材品种")
    private String herbVariety;
    @ApiModelProperty("数量(kg)")
    private BigDecimal quantity;
    @ApiModelProperty("单价(元/kg)")
    private BigDecimal unitPrice;
    @ApiModelProperty("总金额(元)")
    private BigDecimal totalAmount;
    @ApiModelProperty("定价模式")
    private String pricingMode;
    @ApiModelProperty("订单状态")
    private Integer orderStatus;
    @ApiModelProperty("订单状态名称")
    private String orderStatusName;
    @ApiModelProperty("支付状态")
    private Integer paymentStatus;
    @ApiModelProperty("支付状态名称")
    private String paymentStatusName;
    @ApiModelProperty("支付方式")
    private String paymentMethod;
    @ApiModelProperty("配送地址")
    private String deliveryAddress;
    @ApiModelProperty("收货电话")
    private String deliveryPhone;
    @ApiModelProperty("收货人")
    private String deliveryName;
    @ApiModelProperty("物流公司")
    private String logisticsCompany;
    @ApiModelProperty("物流单号")
    private String logisticsNo;
    @ApiModelProperty("确认时间")
    private LocalDateTime confirmTime;
    @ApiModelProperty("发货时间")
    private LocalDateTime deliveryTime;
    @ApiModelProperty("完成时间")
    private LocalDateTime completeTime;
    @ApiModelProperty("取消时间")
    private LocalDateTime cancelTime;
    @ApiModelProperty("取消原因")
    private String cancelReason;
    @ApiModelProperty("备注")
    private String remark;
    @ApiModelProperty("创建时间")
    private LocalDateTime createTime;
    @ApiModelProperty("订单追踪")
    private List<OrderTrackVO> trackingNodes;
}
