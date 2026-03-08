package com.herb.platform.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 作物响应VO
 */
@Data
@ApiModel("作物信息")
public class CropVO {

    @ApiModelProperty("作物ID")
    private Long id;

    @ApiModelProperty("种植户ID")
    private Long userId;

    @ApiModelProperty("种植户名称")
    private String username;

    @ApiModelProperty("地块ID")
    private Long fieldId;

    @ApiModelProperty("地块名称")
    private String fieldName;

    @ApiModelProperty("作物名称")
    private String cropName;

    @ApiModelProperty("品种")
    private String cropVariety;

    @ApiModelProperty("种植日期")
    private LocalDate plantDate;

    @ApiModelProperty("预计收获日期")
    private LocalDate expectedHarvestDate;

    @ApiModelProperty("实际收获日期")
    private LocalDate actualHarvestDate;

    @ApiModelProperty("种植面积(亩)")
    private BigDecimal plantArea;

    @ApiModelProperty("预计产量(kg)")
    private BigDecimal expectedYield;

    @ApiModelProperty("实际产量(kg)")
    private BigDecimal actualYield;

    @ApiModelProperty("生长阶段")
    private String growthStage;

    @ApiModelProperty("生长状况")
    private String growthStatus;

    @ApiModelProperty("作物描述")
    private String description;

    @ApiModelProperty("作物图片")
    private String images;

    @ApiModelProperty("状态")
    private Integer status;

    @ApiModelProperty("状态名称")
    private String statusName;

    @ApiModelProperty("创建时间")
    private LocalDateTime createTime;
}
