package com.herb.platform.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

/**
 * Herb calendar response.
 */
@Data
@ApiModel("种植日历")
public class HerbCalendarVO {

    @ApiModelProperty("作物ID")
    private Long cropId;
    @ApiModelProperty("药材名称")
    private String herbName;
    @ApiModelProperty("播种日期")
    private LocalDate plantDate;
    @ApiModelProperty("预计采收日期")
    private LocalDate expectedHarvestDate;
    @ApiModelProperty("日历节点")
    private List<HerbCalendarStageVO> stages;
    @ApiModelProperty("近期提醒")
    private List<CalendarReminderVO> reminders;
}
