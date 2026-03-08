package com.herb.platform.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * 权限DTO
 */
@Data
@ApiModel("权限请求参数")
public class PermissionDTO {

    @ApiModelProperty("ID")
    private Long id;

    @NotBlank(message = "权限名称不能为空")
    @ApiModelProperty("权限名称")
    private String permissionName;

    @NotBlank(message = "权限编码不能为空")
    @ApiModelProperty("权限编码")
    private String permissionCode;

    @NotNull(message = "权限类型不能为空")
    @ApiModelProperty("权限类型: 1-菜单 2-按钮 3-接口")
    private Integer permissionType;

    @ApiModelProperty("父权限ID")
    private Long parentId;

    @ApiModelProperty("路由路径")
    private String path;

    @ApiModelProperty("组件路径")
    private String component;

    @ApiModelProperty("图标")
    private String icon;

    @ApiModelProperty("排序")
    private Integer sortOrder;

    @ApiModelProperty("状态: 0-禁用 1-正常")
    private Integer status;
}
