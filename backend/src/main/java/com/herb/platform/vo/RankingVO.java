package com.herb.platform.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * 排行榜数据VO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@ApiModel("排行榜数据")
public class RankingVO {

    @ApiModelProperty("排名")
    private Integer rank;

    @ApiModelProperty("ID")
    private Long id;

    @ApiModelProperty("名称")
    private String name;

    @ApiModelProperty("数值")
    private BigDecimal value;

    @ApiModelProperty("单位")
    private String unit;
}
