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
 * 作物请求DTO
 */
@Data
@ApiModel("作物请求")
public class CropDTO {

    @ApiModelProperty("作物ID（编辑时必填）")
    private Long id;

    @NotNull(message = "地块不能为空")
    @ApiModelProperty(value = "地块ID", required = true)
    private Long fieldId;

    @NotBlank(message = "作物名称不能为空")
    @ApiModelProperty(value = "作物名称", required = true)
    private String cropName;

    @ApiModelProperty("品种")
    private String cropVariety;

    @ApiModelProperty("种植日期")
    private LocalDate plantDate;

    @ApiModelProperty("预计收获日期")
    private LocalDate expectedHarvestDate;

    @ApiModelProperty("实际收获日期")
    private LocalDate actualHarvestDate;

    @DecimalMin(value = "0", message = "种植面积不能小于0")
    @ApiModelProperty("种植面积(亩)")
    private BigDecimal plantArea;

    @DecimalMin(value = "0", message = "预计产量不能小于0")
    @ApiModelProperty("预计产量(kg)")
    private BigDecimal expectedYield;

    @DecimalMin(value = "0", message = "实际产量不能小于0")
    @ApiModelProperty("实际产量(kg)")
    private BigDecimal actualYield;

    @ApiModelProperty("生长阶段")
    private String growthStage;

    @ApiModelProperty("生长状况")
    private String growthStatus;

    @ApiModelProperty("作物描述")
    private String description;

    @ApiModelProperty("作物图片(JSON数组)")
    private String images;

    @ApiModelProperty("状态: 1-生长中 2-已收获 3-已销售 4-已废弃")
    private Integer status;
}
