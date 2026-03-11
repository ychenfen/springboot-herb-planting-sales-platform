package com.herb.platform.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

/**
 * Register request.
 */
@Data
@ApiModel("注册请求")
public class RegisterDTO {

    @NotBlank(message = "用户名不能为空")
    @Size(min = 4, max = 20, message = "用户名长度为4-20个字符")
    @ApiModelProperty(value = "用户名", required = true)
    private String username;

    @NotBlank(message = "密码不能为空")
    @Size(min = 6, max = 20, message = "密码长度为6-20个字符")
    @ApiModelProperty(value = "密码", required = true)
    private String password;

    @ApiModelProperty("确认密码")
    private String confirmPassword;

    @ApiModelProperty("真实姓名")
    private String realName;

    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "手机号格式不正确")
    @ApiModelProperty("手机号")
    private String phone;

    @Email(message = "邮箱格式不正确")
    @ApiModelProperty("邮箱")
    private String email;

    @NotNull(message = "用户类型不能为空")
    @Min(value = 1, message = "用户类型不正确")
    @Max(value = 4, message = "用户类型不正确")
    @ApiModelProperty(value = "用户类型: 1-种植户 2-商家 3-管理员 4-普通用户", required = true)
    private Integer userType;
}
