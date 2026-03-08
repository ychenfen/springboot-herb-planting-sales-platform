package com.herb.platform.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 供应信息响应VO
 */
@Data
@ApiModel("供应信息")
public class SupplyVO {

    @ApiModelProperty("供应ID")
    private Long id;

    @ApiModelProperty("发布者ID")
    private Long userId;

    @ApiModelProperty("发布者名称")
    private String username;

    @ApiModelProperty("关联作物ID")
    private Long cropId;

    @ApiModelProperty("药材名称")
    private String herbName;

    @ApiModelProperty("药材品种")
    private String herbVariety;

    @ApiModelProperty("质量等级")
    private String qualityGrade;

    @ApiModelProperty("供应数量(kg)")
    private BigDecimal supplyQuantity;

    @ApiModelProperty("剩余数量(kg)")
    private BigDecimal remainingQuantity;

    @ApiModelProperty("价格(元/kg)")
    private BigDecimal price;

    @ApiModelProperty("价格可议")
    private Integer priceNegotiable;

    @ApiModelProperty("产地")
    private String productionArea;

    @ApiModelProperty("采收日期")
    private LocalDate harvestDate;

    @ApiModelProperty("储存条件")
    private String storageCondition;

    @ApiModelProperty("产品描述")
    private String description;

    @ApiModelProperty("产品图片")
    private String images;

    @ApiModelProperty("认证证书")
    private String certification;

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

    @ApiModelProperty("收藏次数")
    private Integer favoriteCount;

    @ApiModelProperty("是否已收藏")
    private Integer isFavorite;

    @ApiModelProperty("是否置顶")
    private Integer isTop;

    @ApiModelProperty("创建时间")
    private LocalDateTime createTime;
}
