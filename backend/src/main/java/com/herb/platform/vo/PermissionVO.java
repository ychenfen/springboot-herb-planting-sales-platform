package com.herb.platform.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 权限响应VO
 */
@Data
@ApiModel("权限信息")
public class PermissionVO {

    @ApiModelProperty("权限ID")
    private Long id;

    @ApiModelProperty("权限名称")
    private String permissionName;

    @ApiModelProperty("权限编码")
    private String permissionCode;

    @ApiModelProperty("权限类型")
    private Integer permissionType;

    @ApiModelProperty("权限类型名称")
    private String permissionTypeName;

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

    @ApiModelProperty("状态")
    private Integer status;

    @ApiModelProperty("创建时间")
    private LocalDateTime createTime;

    @ApiModelProperty("子权限列表")
    private List<PermissionVO> children;
}
