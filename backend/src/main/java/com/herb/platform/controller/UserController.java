package com.herb.platform.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.herb.platform.annotation.RequireUserType;
import com.herb.platform.common.Constants;
import com.herb.platform.common.Result;
import com.herb.platform.dto.UserDTO;
import com.herb.platform.service.UserService;
import com.herb.platform.vo.UserVO;
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
 * 用户管理控制器
 */
@Api(tags = "用户管理")
@RestController
@RequestMapping("/system/user")
@Validated
@RequireUserType(Constants.USER_TYPE_ADMIN)
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @ApiOperation("分页查询用户")
    @GetMapping("/page")
    public Result<IPage<UserVO>> page(
            @ApiParam("用户名") @RequestParam(required = false) String username,
            @ApiParam("手机号") @RequestParam(required = false) String phone,
            @ApiParam("用户类型") @RequestParam(required = false) Integer userType,
            @ApiParam("状态") @RequestParam(required = false) Integer status,
            @ApiParam("页码") @RequestParam(defaultValue = "1") @Min(value = 1, message = "页码必须大于0") int pageNum,
            @ApiParam("每页数量") @RequestParam(defaultValue = "10") @Min(value = 1, message = "每页数量必须大于0") @Max(value = 100, message = "每页数量不能超过100") int pageSize) {
        return Result.success(userService.page(username, phone, userType, status, pageNum, pageSize));
    }

    @ApiOperation("获取用户详情")
    @GetMapping("/{id}")
    public Result<UserVO> getById(@PathVariable Long id) {
        return Result.success(userService.getById(id));
    }

    @ApiOperation("创建用户")
    @PostMapping
    public Result<Long> create(@Valid @RequestBody UserDTO dto) {
        return Result.success(userService.create(dto));
    }

    @ApiOperation("更新用户")
    @PutMapping
    public Result<Void> update(@Valid @RequestBody UserDTO dto) {
        userService.update(dto);
        return Result.success();
    }

    @ApiOperation("删除用户")
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        userService.delete(id);
        return Result.success();
    }

    @ApiOperation("更新用户状态")
    @PutMapping("/{id}/status")
    public Result<Void> updateStatus(
            @PathVariable Long id,
            @ApiParam("状态") @RequestParam Integer status) {
        userService.updateStatus(id, status);
        return Result.success();
    }

    @ApiOperation("重置密码")
    @PutMapping("/{id}/password")
    public Result<Void> resetPassword(
            @PathVariable Long id,
            @ApiParam("新密码") @RequestParam(required = false) String newPassword) {
        userService.resetPassword(id, newPassword);
        return Result.success();
    }

    @ApiOperation("分配角色")
    @PutMapping("/{id}/roles")
    public Result<Void> assignRoles(
            @PathVariable Long id,
            @RequestBody List<Long> roleIds) {
        userService.assignRoles(id, roleIds);
        return Result.success();
    }
}
