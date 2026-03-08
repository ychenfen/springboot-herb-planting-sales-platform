package com.herb.platform.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 用户信息VO
 */
@Data
@ApiModel("用户信息")
public class UserVO {

    @ApiModelProperty("用户ID")
    private Long id;

    @ApiModelProperty("用户名")
    private String username;

    @ApiModelProperty("真实姓名")
    private String realName;

    @ApiModelProperty("手机号")
    private String phone;

    @ApiModelProperty("邮箱")
    private String email;

    @ApiModelProperty("头像")
    private String avatar;

    @ApiModelProperty("用户类型: 1-种植户 2-采购商 3-管理员")
    private Integer userType;

    @ApiModelProperty("用户类型名称")
    private String userTypeName;

    @ApiModelProperty("状态: 0-禁用 1-正常")
    private Integer status;

    @ApiModelProperty("状态名称")
    private String statusName;

    @ApiModelProperty("最后登录时间")
    private LocalDateTime lastLoginTime;

    @ApiModelProperty("创建时间")
    private LocalDateTime createTime;

    @ApiModelProperty("角色ID列表")
    private java.util.List<Long> roleIds;

    @ApiModelProperty("角色名称")
    private String roleNames;
}
