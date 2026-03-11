package com.herb.platform.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDate;

/**
 * Calendar stage response.
 */
@Data
@ApiModel("种植日历节点")
public class HerbCalendarStageVO {

    @ApiModelProperty("阶段名称")
    private String stageName;
    @ApiModelProperty("作业类型")
    private String actionType;
    @ApiModelProperty("开始日期")
    private LocalDate startDate;
    @ApiModelProperty("结束日期")
    private LocalDate endDate;
    @ApiModelProperty("提醒日期")
    private LocalDate reminderDate;
    @ApiModelProperty("操作窗口")
    private String operationWindow;
    @ApiModelProperty("阶段提示")
    private String stageTips;
    @ApiModelProperty("排序")
    private Integer sortOrder;
}
