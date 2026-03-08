package com.herb.platform.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 登录响应VO
 */
@Data
@ApiModel("登录响应")
public class LoginVO {

    @ApiModelProperty("访问令牌")
    private String token;

    @ApiModelProperty("用户ID")
    private Long userId;

    @ApiModelProperty("用户名")
    private String username;

    @ApiModelProperty("真实姓名")
    private String realName;

    @ApiModelProperty("头像")
    private String avatar;

    @ApiModelProperty("用户类型: 1-种植户 2-采购商 3-管理员")
    private Integer userType;

    @ApiModelProperty("用户类型名称")
    private String userTypeName;
}
