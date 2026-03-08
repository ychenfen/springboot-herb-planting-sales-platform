package com.herb.platform.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * 收藏DTO
 */
@Data
@ApiModel("收藏请求参数")
public class FavoriteDTO {

    @NotNull(message = "收藏类型不能为空")
    @ApiModelProperty("收藏类型: 1-供应信息 2-需求信息")
    private Integer targetType;

    @NotNull(message = "目标ID不能为空")
    @ApiModelProperty("目标ID")
    private Long targetId;
}
