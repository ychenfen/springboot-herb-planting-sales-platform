package com.herb.platform.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 需求信息响应VO
 */
@Data
@ApiModel("需求信息")
public class DemandVO {

    @ApiModelProperty("需求ID")
    private Long id;

    @ApiModelProperty("发布者ID")
    private Long userId;

    @ApiModelProperty("发布者名称")
    private String username;

    @ApiModelProperty("药材名称")
    private String herbName;

    @ApiModelProperty("药材品种")
    private String herbVariety;

    @ApiModelProperty("质量要求")
    private String qualityRequirement;

    @ApiModelProperty("需求数量(kg)")
    private BigDecimal demandQuantity;

    @ApiModelProperty("目标价格(元/kg)")
    private BigDecimal targetPrice;

    @ApiModelProperty("价格区间")
    private String priceRange;

    @ApiModelProperty("需求日期")
    private LocalDate demandDate;

    @ApiModelProperty("截止日期")
    private LocalDate expiryDate;

    @ApiModelProperty("收货地址")
    private String deliveryAddress;

    @ApiModelProperty("需求描述")
    private String description;

    @ApiModelProperty("联系人")
    private String contactName;

    @ApiModelProperty("联系电话")
    private String contactPhone;

    @ApiModelProperty("微信号")
    private String contactWechat;

    @ApiModelProperty("状态")
    private Integer status;

    @ApiModelProperty("状态名称")
    private String statusName;

    @ApiModelProperty("浏览次数")
    private Integer viewCount;

    @ApiModelProperty("是否已收藏")
    private Integer isFavorite;

    @ApiModelProperty("是否置顶")
    private Integer isTop;

    @ApiModelProperty("创建时间")
    private LocalDateTime createTime;
}
