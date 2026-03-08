package com.herb.platform.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.util.List;

/**
 * 角色DTO
 */
@Data
@ApiModel("角色请求参数")
public class RoleDTO {

    @ApiModelProperty("角色ID")
    private Long id;

    @NotBlank(message = "角色名称不能为空")
    @ApiModelProperty("角色名称")
    private String roleName;

    @NotBlank(message = "角色编码不能为空")
    @ApiModelProperty("角色编码")
    private String roleCode;

    @ApiModelProperty("角色描述")
    private String description;

    @ApiModelProperty("状态")
    private Integer status;

    @ApiModelProperty("权限ID列表")
    private List<Long> permissionIds;
}
