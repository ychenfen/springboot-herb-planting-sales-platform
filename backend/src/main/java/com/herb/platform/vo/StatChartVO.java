package com.herb.platform.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 统计图表数据VO
 */
@Data
@ApiModel("统计图表数据")
public class StatChartVO {

    @ApiModelProperty("日期列表")
    private List<String> dates;

    @ApiModelProperty("数据列表")
    private List<ChartData> dataList;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ChartData {
        @ApiModelProperty("名称")
        private String name;

        @ApiModelProperty("数值列表")
        private List<Object> values;
    }
}
