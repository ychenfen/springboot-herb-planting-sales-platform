package com.herb.platform.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

/**
 * 地块请求DTO
 */
@Data
@ApiModel("地块请求")
public class FieldDTO {

    @ApiModelProperty("地块ID（编辑时必填）")
    private Long id;

    @NotBlank(message = "地块名称不能为空")
    @ApiModelProperty(value = "地块名称", required = true)
    private String fieldName;

    @ApiModelProperty("地理位置")
    private String location;

    @ApiModelProperty("省份")
    private String province;

    @ApiModelProperty("城市")
    private String city;

    @ApiModelProperty("区县")
    private String district;

    @ApiModelProperty("详细地址")
    private String address;

    @ApiModelProperty("经度")
    private BigDecimal longitude;

    @ApiModelProperty("纬度")
    private BigDecimal latitude;

    @DecimalMin(value = "0", message = "面积不能小于0")
    @ApiModelProperty("面积(亩)")
    private BigDecimal area;

    @ApiModelProperty("土壤类型")
    private String soilType;

    @ApiModelProperty("灌溉方式")
    private String irrigationType;

    @ApiModelProperty("地块描述")
    private String description;

    @ApiModelProperty("地块图片(JSON数组)")
    private String images;

    @ApiModelProperty("状态: 0-休耕 1-在用 2-整改中")
    private Integer status;
}
