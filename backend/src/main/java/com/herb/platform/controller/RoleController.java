package com.herb.platform.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.herb.platform.annotation.RequireUserType;
import com.herb.platform.common.Constants;
import com.herb.platform.common.Result;
import com.herb.platform.dto.RoleDTO;
import com.herb.platform.service.RoleService;
import com.herb.platform.vo.RoleVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import java.util.List;

/**
 * 角色管理控制器
 */
@Api(tags = "角色管理")
@RestController
@RequestMapping("/system/role")
@Validated
@RequireUserType(Constants.USER_TYPE_ADMIN)
@RequiredArgsConstructor
public class RoleController {

    private final RoleService roleService;

    @ApiOperation("分页查询角色")
    @GetMapping("/page")
    public Result<IPage<RoleVO>> page(
            @ApiParam("角色名称") @RequestParam(required = false) String roleName,
            @ApiParam("状态") @RequestParam(required = false) Integer status,
            @ApiParam("页码") @RequestParam(defaultValue = "1") @Min(value = 1, message = "页码必须大于0") int pageNum,
            @ApiParam("每页数量") @RequestParam(defaultValue = "10") @Min(value = 1, message = "每页数量必须大于0") @Max(value = 100, message = "每页数量不能超过100") int pageSize) {
        return Result.success(roleService.page(roleName, status, pageNum, pageSize));
    }

    @ApiOperation("获取所有角色")
    @GetMapping("/list")
    public Result<List<RoleVO>> listAll() {
        return Result.success(roleService.listAll());
    }

    @ApiOperation("获取角色详情")
    @GetMapping("/{id}")
    public Result<RoleVO> getById(@PathVariable Long id) {
        return Result.success(roleService.getById(id));
    }

    @ApiOperation("创建角色")
    @PostMapping
    public Result<Long> create(@Valid @RequestBody RoleDTO dto) {
        return Result.success(roleService.create(dto));
    }

    @ApiOperation("更新角色")
    @PutMapping
    public Result<Void> update(@Valid @RequestBody RoleDTO dto) {
        roleService.update(dto);
        return Result.success();
    }

    @ApiOperation("删除角色")
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        roleService.delete(id);
        return Result.success();
    }

    @ApiOperation("分配权限")
    @PutMapping("/{id}/permissions")
    public Result<Void> assignPermissions(
            @PathVariable Long id,
            @RequestBody List<Long> permissionIds) {
        roleService.assignPermissions(id, permissionIds);
        return Result.success();
    }
}
