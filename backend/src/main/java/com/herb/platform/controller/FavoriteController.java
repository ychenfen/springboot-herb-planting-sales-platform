package com.herb.platform.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.herb.platform.annotation.RequireUserType;
import com.herb.platform.common.Constants;
import com.herb.platform.common.Result;
import com.herb.platform.dto.FavoriteDTO;
import com.herb.platform.service.FavoriteService;
import com.herb.platform.vo.FavoriteVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

/**
 * 收藏控制器
 */
@Api(tags = "收藏管理")
@RestController
@RequestMapping("/favorite")
@Validated
@RequireUserType({Constants.USER_TYPE_FARMER, Constants.USER_TYPE_BUYER})
@RequiredArgsConstructor
public class FavoriteController {

    private final FavoriteService favoriteService;

    @ApiOperation("分页查询收藏")
    @GetMapping("/page")
    public Result<IPage<FavoriteVO>> page(
            @ApiParam("收藏类型") @RequestParam(required = false) Integer targetType,
            @ApiParam("页码") @RequestParam(defaultValue = "1") @Min(value = 1, message = "页码必须大于0") int pageNum,
            @ApiParam("每页数量") @RequestParam(defaultValue = "10") @Min(value = 1, message = "每页数量必须大于0") @Max(value = 100, message = "每页数量不能超过100") int pageSize,
            HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        return Result.success(favoriteService.pageByUser(userId, targetType, pageNum, pageSize));
    }

    @ApiOperation("新增收藏")
    @PostMapping
    public Result<Void> add(@Valid @RequestBody FavoriteDTO dto, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        favoriteService.add(userId, dto);
        return Result.success();
    }

    @ApiOperation("取消收藏")
    @DeleteMapping
    public Result<Void> remove(
            @ApiParam("收藏类型") @RequestParam Integer targetType,
            @ApiParam("目标ID") @RequestParam Long targetId,
            HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        favoriteService.remove(userId, targetType, targetId);
        return Result.success();
    }
}
