package com.herb.platform.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * 需求信息请求DTO
 */
@Data
@ApiModel("需求信息请求")
public class DemandDTO {

    @ApiModelProperty("需求ID（编辑时必填）")
    private Long id;

    @NotBlank(message = "药材名称不能为空")
    @ApiModelProperty(value = "药材名称", required = true)
    private String herbName;

    @ApiModelProperty("药材品种")
    private String herbVariety;

    @ApiModelProperty("质量要求")
    private String qualityRequirement;

    @NotNull(message = "需求数量不能为空")
    @DecimalMin(value = "0.01", message = "需求数量必须大于0")
    @ApiModelProperty(value = "需求数量(kg)", required = true)
    private BigDecimal demandQuantity;

    @DecimalMin(value = "0", message = "目标价格不能小于0")
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
}
