package com.herb.platform.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 地块响应VO
 */
@Data
@ApiModel("地块信息")
public class FieldVO {

    @ApiModelProperty("地块ID")
    private Long id;

    @ApiModelProperty("所属用户ID")
    private Long userId;

    @ApiModelProperty("所属用户名")
    private String username;

    @ApiModelProperty("地块名称")
    private String fieldName;

    @ApiModelProperty("地块编码")
    private String fieldCode;

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

    @ApiModelProperty("面积(亩)")
    private BigDecimal area;

    @ApiModelProperty("土壤类型")
    private String soilType;

    @ApiModelProperty("灌溉方式")
    private String irrigationType;

    @ApiModelProperty("地块描述")
    private String description;

    @ApiModelProperty("地块图片")
    private String images;

    @ApiModelProperty("状态: 0-休耕 1-在用 2-整改中")
    private Integer status;

    @ApiModelProperty("状态名称")
    private String statusName;

    @ApiModelProperty("创建时间")
    private LocalDateTime createTime;
}
