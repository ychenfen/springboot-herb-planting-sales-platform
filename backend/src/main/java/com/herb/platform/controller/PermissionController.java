package com.herb.platform.controller;

import com.herb.platform.annotation.RequireUserType;
import com.herb.platform.common.Constants;
import com.herb.platform.common.Result;
import com.herb.platform.dto.PermissionDTO;
import com.herb.platform.service.PermissionService;
import com.herb.platform.vo.PermissionVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.List;

/**
 * 权限管理控制器
 */
@Api(tags = "权限管理")
@RestController
@RequestMapping("/system/permission")
@RequiredArgsConstructor
public class PermissionController {

    private final PermissionService permissionService;

    @ApiOperation("获取权限树")
    @GetMapping("/tree")
    @RequireUserType(Constants.USER_TYPE_ADMIN)
    public Result<List<PermissionVO>> listTree() {
        return Result.success(permissionService.listTree());
    }

    @ApiOperation("获取所有权限")
    @GetMapping("/list")
    @RequireUserType(Constants.USER_TYPE_ADMIN)
    public Result<List<PermissionVO>> listAll() {
        return Result.success(permissionService.listAll());
    }

    @ApiOperation("获取当前用户菜单")
    @GetMapping("/menus")
    public Result<List<PermissionVO>> getUserMenus(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        return Result.success(permissionService.getUserMenus(userId));
    }

    @ApiOperation("获取当前用户权限")
    @GetMapping("/user")
    public Result<List<PermissionVO>> getUserPermissions(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        return Result.success(permissionService.listByUserId(userId));
    }

    @ApiOperation("创建权限")
    @PostMapping
    @RequireUserType(Constants.USER_TYPE_ADMIN)
    public Result<Void> create(@Valid @RequestBody PermissionDTO dto) {
        permissionService.create(dto);
        return Result.success();
    }

    @ApiOperation("更新权限")
    @PutMapping
    @RequireUserType(Constants.USER_TYPE_ADMIN)
    public Result<Void> update(@Valid @RequestBody PermissionDTO dto) {
        permissionService.update(dto);
        return Result.success();
    }

    @ApiOperation("删除权限")
    @DeleteMapping("/{id}")
    @RequireUserType(Constants.USER_TYPE_ADMIN)
    public Result<Void> delete(@PathVariable Long id) {
        permissionService.delete(id);
        return Result.success();
    }
}
