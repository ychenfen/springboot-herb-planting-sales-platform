package com.herb.platform.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;

/**
 * 溯源信息请求DTO
 */
@Data
@ApiModel("溯源信息请求")
public class TraceDTO {

    @ApiModelProperty("溯源ID（编辑时必填）")
    private Long id;

    @NotNull(message = "作物不能为空")
    @ApiModelProperty(value = "作物ID", required = true)
    private Long cropId;

    @NotBlank(message = "药材名称不能为空")
    @ApiModelProperty(value = "药材名称", required = true)
    private String herbName;

    @ApiModelProperty("批次号")
    private String batchNo;

    @ApiModelProperty("产地")
    private String productionArea;

    @ApiModelProperty("种植日期")
    private LocalDate plantDate;

    @ApiModelProperty("采收日期")
    private LocalDate harvestDate;

    @ApiModelProperty("质量标准")
    private String qualityStandard;

    @ApiModelProperty("质检报告URL")
    private String qualityReport;

    @ApiModelProperty("认证证书(JSON数组)")
    private String certification;
}
