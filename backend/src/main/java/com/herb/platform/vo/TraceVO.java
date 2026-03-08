package com.herb.platform.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 溯源信息响应VO
 */
@Data
@ApiModel("溯源信息")
public class TraceVO {

    @ApiModelProperty("溯源ID")
    private Long id;

    @ApiModelProperty("溯源码")
    private String traceCode;

    @ApiModelProperty("作物ID")
    private Long cropId;

    @ApiModelProperty("作物名称")
    private String cropName;

    @ApiModelProperty("创建者ID")
    private Long userId;

    @ApiModelProperty("创建者名称")
    private String username;

    @ApiModelProperty("药材名称")
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

    @ApiModelProperty("认证证书")
    private String certification;

    @ApiModelProperty("二维码图片URL")
    private String qrCodeUrl;

    @ApiModelProperty("状态")
    private Integer status;

    @ApiModelProperty("状态名称")
    private String statusName;

    @ApiModelProperty("扫码次数")
    private Integer scanCount;

    @ApiModelProperty("创建时间")
    private LocalDateTime createTime;

    @ApiModelProperty("溯源节点列表")
    private List<TraceNodeVO> nodes;
}
