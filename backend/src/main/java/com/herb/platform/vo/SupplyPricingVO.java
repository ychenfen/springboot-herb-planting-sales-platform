package com.herb.platform.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;

/**
 * Supply pricing preview.
 */
@Data
@ApiModel("供应计价预览")
public class SupplyPricingVO {

    @ApiModelProperty("供应ID")
    private Long supplyId;
    @ApiModelProperty("数量(kg)")
    private BigDecimal quantityKg;
    @ApiModelProperty("基础单价(元/kg)")
    private BigDecimal basePrice;
    @ApiModelProperty("实际应用单价(元/kg)")
    private BigDecimal appliedUnitPrice;
    @ApiModelProperty("是否启用批发价")
    private Boolean wholesaleApplied;
    @ApiModelProperty("起批量(kg)")
    private BigDecimal wholesaleMinQuantity;
    @ApiModelProperty("总金额(元)")
    private BigDecimal totalAmount;
}
