package com.herb.platform.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;

/**
 * 农事记录请求DTO
 */
@Data
@ApiModel("农事记录请求")
public class FarmRecordDTO {

    @ApiModelProperty("记录ID（编辑时必填）")
    private Long id;

    @NotNull(message = "作物不能为空")
    @ApiModelProperty(value = "作物ID", required = true)
    private Long cropId;

    @NotBlank(message = "活动类型不能为空")
    @ApiModelProperty(value = "活动类型", required = true)
    private String activityType;

    @NotNull(message = "活动日期不能为空")
    @ApiModelProperty(value = "活动日期", required = true)
    private LocalDate activityDate;

    @ApiModelProperty("活动时间")
    private LocalTime activityTime;

    @ApiModelProperty("活动详情")
    private String activityDetail;

    @ApiModelProperty("使用材料")
    private String materialsUsed;

    @ApiModelProperty("用量")
    private String dosage;

    @DecimalMin(value = "0", message = "成本不能小于0")
    @ApiModelProperty("成本(元)")
    private BigDecimal cost;

    @ApiModelProperty("天气情况")
    private String weather;

    @ApiModelProperty("温度")
    private String temperature;

    @ApiModelProperty("湿度")
    private String humidity;

    @ApiModelProperty("备注")
    private String remark;

    @ApiModelProperty("图片(JSON数组)")
    private String images;
}
