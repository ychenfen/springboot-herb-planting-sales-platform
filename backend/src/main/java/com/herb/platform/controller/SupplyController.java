package com.herb.platform.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.herb.platform.annotation.RequireUserType;
import com.herb.platform.common.Constants;
import com.herb.platform.common.Result;
import com.herb.platform.dto.SupplyDTO;
import com.herb.platform.service.SupplyService;
import com.herb.platform.vo.SupplyPricingVO;
import com.herb.platform.vo.SupplyVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import java.math.BigDecimal;

/**
 * Supply controller.
 */
@Api(tags = "供应信息管理")
@RestController
@RequestMapping("/supply")
@Validated
@RequiredArgsConstructor
public class SupplyController {

    private final SupplyService supplyService;

    @ApiOperation("供应大厅分页查询")
    @GetMapping("/market")
    public Result<IPage<SupplyVO>> pageMarket(
            @ApiParam("药材名称") @RequestParam(required = false) String herbName,
            @ApiParam("质量等级") @RequestParam(required = false) String qualityGrade,
            @ApiParam("状态") @RequestParam(required = false) Integer status,
            @ApiParam("页码") @RequestParam(defaultValue = "1") @Min(1) int pageNum,
            @ApiParam("每页数量") @RequestParam(defaultValue = "10") @Min(1) @Max(100) int pageSize,
            HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        return Result.success(supplyService.pageMarket(userId, herbName, qualityGrade, status, pageNum, pageSize));
    }

    @ApiOperation("我的供应分页查询")
    @GetMapping("/my")
    @RequireUserType(Constants.USER_TYPE_FARMER)
    public Result<IPage<SupplyVO>> pageMySupply(
            @ApiParam("药材名称") @RequestParam(required = false) String herbName,
            @ApiParam("状态") @RequestParam(required = false) Integer status,
            @ApiParam("页码") @RequestParam(defaultValue = "1") @Min(1) int pageNum,
            @ApiParam("每页数量") @RequestParam(defaultValue = "10") @Min(1) @Max(100) int pageSize,
            HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        return Result.success(supplyService.pageMySupply(userId, herbName, status, pageNum, pageSize));
    }

    @ApiOperation("查询供应详情")
    @GetMapping("/{id}")
    public Result<SupplyVO> getById(@PathVariable Long id) {
        return Result.success(supplyService.getById(id));
    }

    @ApiOperation("智能计价预览")
    @GetMapping("/{id}/pricing")
    public Result<SupplyPricingVO> calculatePricing(@PathVariable Long id, @RequestParam BigDecimal quantity) {
        return Result.success(supplyService.calculatePricing(id, quantity));
    }

    @ApiOperation("发布供应信息")
    @PostMapping
    @RequireUserType(Constants.USER_TYPE_FARMER)
    public Result<Void> add(@Validated @RequestBody SupplyDTO dto, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        supplyService.add(userId, dto);
        return Result.success();
    }

    @ApiOperation("更新供应信息")
    @PutMapping
    @RequireUserType(Constants.USER_TYPE_FARMER)
    public Result<Void> update(@Validated @RequestBody SupplyDTO dto, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        supplyService.update(userId, dto);
        return Result.success();
    }

    @ApiOperation("下架供应信息")
    @PutMapping("/{id}/offline")
    @RequireUserType(Constants.USER_TYPE_FARMER)
    public Result<Void> offline(@PathVariable Long id, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        supplyService.offline(userId, id);
        return Result.success();
    }

    @ApiOperation("删除供应信息")
    @DeleteMapping("/{id}")
    @RequireUserType(Constants.USER_TYPE_FARMER)
    public Result<Void> delete(@PathVariable Long id, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        supplyService.delete(userId, id);
        return Result.success();
    }
}
