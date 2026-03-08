package com.herb.platform.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

/**
 * 农事记录响应VO
 */
@Data
@ApiModel("农事记录信息")
public class FarmRecordVO {

    @ApiModelProperty("记录ID")
    private Long id;

    @ApiModelProperty("作物ID")
    private Long cropId;

    @ApiModelProperty("作物名称")
    private String cropName;

    @ApiModelProperty("操作人ID")
    private Long userId;

    @ApiModelProperty("操作人名称")
    private String username;

    @ApiModelProperty("活动类型")
    private String activityType;

    @ApiModelProperty("活动类型名称")
    private String activityTypeName;

    @ApiModelProperty("活动日期")
    private LocalDate activityDate;

    @ApiModelProperty("活动时间")
    private LocalTime activityTime;

    @ApiModelProperty("活动详情")
    private String activityDetail;

    @ApiModelProperty("使用材料")
    private String materialsUsed;

    @ApiModelProperty("用量")
    private String dosage;

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

    @ApiModelProperty("图片")
    private String images;

    @ApiModelProperty("创建时间")
    private LocalDateTime createTime;
}
