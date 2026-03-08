package com.herb.platform.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.util.List;

/**
 * 用户DTO
 */
@Data
@ApiModel("用户请求参数")
public class UserDTO {

    @ApiModelProperty("用户ID")
    private Long id;

    @NotBlank(message = "用户名不能为空")
    @ApiModelProperty("用户名")
    private String username;

    @ApiModelProperty("密码")
    private String password;

    @ApiModelProperty("真实姓名")
    private String realName;

    @ApiModelProperty("手机号")
    private String phone;

    @ApiModelProperty("邮箱")
    private String email;

    @ApiModelProperty("头像")
    private String avatar;

    @ApiModelProperty("用户类型")
    private Integer userType;

    @ApiModelProperty("状态")
    private Integer status;

    @ApiModelProperty("角色ID列表")
    private List<Long> roleIds;
}
