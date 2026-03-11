package com.herb.platform.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * Order tracking node.
 */
@Data
@ApiModel("订单追踪节点")
public class OrderTrackVO {

    @ApiModelProperty("状态值")
    private Integer status;
    @ApiModelProperty("标题")
    private String title;
    @ApiModelProperty("说明")
    private String description;
    @ApiModelProperty("时间")
    private LocalDateTime time;
}
