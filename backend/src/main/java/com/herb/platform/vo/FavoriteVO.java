package com.herb.platform.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 收藏响应VO
 */
@Data
@ApiModel("收藏信息")
public class FavoriteVO {

    @ApiModelProperty("收藏ID")
    private Long id;

    @ApiModelProperty("用户ID")
    private Long userId;

    @ApiModelProperty("收藏类型")
    private Integer targetType;

    @ApiModelProperty("收藏类型名称")
    private String targetTypeName;

    @ApiModelProperty("目标ID")
    private Long targetId;

    @ApiModelProperty("创建时间")
    private LocalDateTime createTime;

    @ApiModelProperty("供应信息")
    private SupplyVO supply;

    @ApiModelProperty("需求信息")
    private DemandVO demand;
}
