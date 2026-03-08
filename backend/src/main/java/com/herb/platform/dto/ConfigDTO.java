package com.herb.platform.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * 系统配置DTO
 */
@Data
@ApiModel("系统配置请求参数")
public class ConfigDTO {

    @ApiModelProperty("ID")
    private Long id;

    @NotBlank(message = "配置键不能为空")
    @ApiModelProperty("配置键")
    private String configKey;

    @ApiModelProperty("配置值")
    private String configValue;

    @ApiModelProperty("配置类型")
    private String configType;

    @ApiModelProperty("描述")
    private String description;
}
