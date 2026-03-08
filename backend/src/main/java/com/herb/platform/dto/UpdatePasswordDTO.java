package com.herb.platform.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

/**
 * 修改密码请求DTO
 */
@Data
@ApiModel("修改密码请求")
public class UpdatePasswordDTO {

    @NotBlank(message = "原密码不能为空")
    @ApiModelProperty(value = "原密码", required = true)
    private String oldPassword;

    @NotBlank(message = "新密码不能为空")
    @Size(min = 6, max = 20, message = "密码长度为6-20个字符")
    @ApiModelProperty(value = "新密码", required = true)
    private String newPassword;

    @NotBlank(message = "确认密码不能为空")
    @ApiModelProperty(value = "确认密码", required = true)
    private String confirmPassword;
}
