package com.herb.platform.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 仪表盘数据VO
 */
@Data
@ApiModel("仪表盘数据")
public class DashboardVO {

    @ApiModelProperty("用户总数")
    private Long userCount;

    @ApiModelProperty("种植户数量")
    private Long farmerCount;

    @ApiModelProperty("采购商数量")
    private Long buyerCount;

    @ApiModelProperty("地块总数")
    private Long fieldCount;

    @ApiModelProperty("作物总数")
    private Long cropCount;

    @ApiModelProperty("生长中作物数")
    private Long growingCropCount;

    @ApiModelProperty("供应信息数")
    private Long supplyCount;

    @ApiModelProperty("需求信息数")
    private Long demandCount;

    @ApiModelProperty("订单总数")
    private Long orderCount;

    @ApiModelProperty("今日订单数")
    private Long todayOrderCount;

    @ApiModelProperty("订单总金额")
    private BigDecimal totalOrderAmount;

    @ApiModelProperty("今日订单金额")
    private BigDecimal todayOrderAmount;

    @ApiModelProperty("溯源信息数")
    private Long traceCount;

    @ApiModelProperty("今日扫码次数")
    private Long todayScanCount;
}
