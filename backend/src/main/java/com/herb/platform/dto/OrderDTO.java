package com.herb.platform.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.math.BigDecimal;

/**
 * 订单请求DTO
 */
@Data
@ApiModel("订单请求")
public class OrderDTO {

    @NotNull(message = "供应信息不能为空")
    @ApiModelProperty(value = "供应信息ID", required = true)
    private Long supplyId;

    @NotNull(message = "购买数量不能为空")
    @DecimalMin(value = "0.01", message = "购买数量必须大于0")
    @ApiModelProperty(value = "数量(kg)", required = true)
    private BigDecimal quantity;

    @NotBlank(message = "收货地址不能为空")
    @ApiModelProperty("收货地址")
    private String deliveryAddress;

    @NotBlank(message = "收货电话不能为空")
    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "收货电话格式不正确")
    @ApiModelProperty("收货电话")
    private String deliveryPhone;

    @NotBlank(message = "收货人不能为空")
    @ApiModelProperty("收货人")
    private String deliveryName;

    @ApiModelProperty("备注")
    private String remark;
}
