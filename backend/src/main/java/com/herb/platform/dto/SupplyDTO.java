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
 * 供应信息请求DTO
 */
@Data
@ApiModel("供应信息请求")
public class SupplyDTO {

    @ApiModelProperty("供应ID（编辑时必填）")
    private Long id;

    @ApiModelProperty("关联作物ID")
    private Long cropId;

    @NotBlank(message = "药材名称不能为空")
    @ApiModelProperty(value = "药材名称", required = true)
    private String herbName;

    @ApiModelProperty("药材品种")
    private String herbVariety;

    @ApiModelProperty("质量等级")
    private String qualityGrade;

    @NotNull(message = "供应数量不能为空")
    @DecimalMin(value = "0.01", message = "供应数量必须大于0")
    @ApiModelProperty(value = "供应数量(kg)", required = true)
    private BigDecimal supplyQuantity;

    @DecimalMin(value = "0", message = "价格不能小于0")
    @ApiModelProperty("价格(元/kg)")
    private BigDecimal price;

    @ApiModelProperty("价格可议: 0-不可议 1-可议")
    private Integer priceNegotiable;

    @ApiModelProperty("产地")
    private String productionArea;

    @ApiModelProperty("采收日期")
    private LocalDate harvestDate;

    @ApiModelProperty("储存条件")
    private String storageCondition;

    @ApiModelProperty("产品描述")
    private String description;

    @ApiModelProperty("产品图片(JSON数组)")
    private String images;

    @ApiModelProperty("认证证书(JSON数组)")
    private String certification;

    @ApiModelProperty("联系人")
    private String contactName;

    @ApiModelProperty("联系电话")
    private String contactPhone;

    @ApiModelProperty("微信号")
    private String contactWechat;
}
