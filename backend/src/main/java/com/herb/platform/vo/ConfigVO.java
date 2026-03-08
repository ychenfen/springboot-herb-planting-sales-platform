package com.herb.platform.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 系统配置响应VO
 */
@Data
@ApiModel("系统配置信息")
public class ConfigVO {

    @ApiModelProperty("ID")
    private Long id;

    @ApiModelProperty("配置键")
    private String configKey;

    @ApiModelProperty("配置值")
    private String configValue;

    @ApiModelProperty("配置类型")
    private String configType;

    @ApiModelProperty("描述")
    private String description;

    @ApiModelProperty("创建时间")
    private LocalDateTime createTime;

    @ApiModelProperty("更新时间")
    private LocalDateTime updateTime;
}
