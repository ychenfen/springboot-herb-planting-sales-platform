package com.herb.platform.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDate;

/**
 * Calendar reminder response.
 */
@Data
@ApiModel("农事提醒")
public class CalendarReminderVO {

    @ApiModelProperty("作物ID")
    private Long cropId;
    @ApiModelProperty("作物名称")
    private String cropName;
    @ApiModelProperty("药材名称")
    private String herbName;
    @ApiModelProperty("阶段名称")
    private String stageName;
    @ApiModelProperty("作业类型")
    private String actionType;
    @ApiModelProperty("提醒日期")
    private LocalDate reminderDate;
    @ApiModelProperty("距离提醒天数")
    private Long daysUntil;
    @ApiModelProperty("提醒内容")
    private String stageTips;
}
