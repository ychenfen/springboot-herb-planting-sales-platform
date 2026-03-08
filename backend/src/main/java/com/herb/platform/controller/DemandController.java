package com.herb.platform.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.herb.platform.annotation.RequireUserType;
import com.herb.platform.common.Constants;
import com.herb.platform.common.Result;
import com.herb.platform.dto.DemandDTO;
import com.herb.platform.service.DemandService;
import com.herb.platform.vo.DemandVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

/**
 * 需求信息控制器
 */
@Api(tags = "需求信息管理")
@RestController
@RequestMapping("/demand")
@Validated
@RequiredArgsConstructor
public class DemandController {

    private final DemandService demandService;

    @ApiOperation("需求大厅-分页查询")
    @GetMapping("/market")
    public Result<IPage<DemandVO>> pageMarket(
            @ApiParam("药材名称") @RequestParam(required = false) String herbName,
            @ApiParam("状态") @RequestParam(required = false) Integer status,
            @ApiParam("页码") @RequestParam(defaultValue = "1") @Min(value = 1, message = "页码必须大于0") int pageNum,
            @ApiParam("每页数量") @RequestParam(defaultValue = "10") @Min(value = 1, message = "每页数量必须大于0") @Max(value = 100, message = "每页数量不能超过100") int pageSize,
            HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        IPage<DemandVO> page = demandService.pageMarket(userId, herbName, status, pageNum, pageSize);
        return Result.success(page);
    }

    @ApiOperation("我的需求-分页查询")
    @GetMapping("/my")
    @RequireUserType(Constants.USER_TYPE_BUYER)
    public Result<IPage<DemandVO>> pageMyDemand(
            @ApiParam("药材名称") @RequestParam(required = false) String herbName,
            @ApiParam("状态") @RequestParam(required = false) Integer status,
            @ApiParam("页码") @RequestParam(defaultValue = "1") @Min(value = 1, message = "页码必须大于0") int pageNum,
            @ApiParam("每页数量") @RequestParam(defaultValue = "10") @Min(value = 1, message = "每页数量必须大于0") @Max(value = 100, message = "每页数量不能超过100") int pageSize,
            HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        IPage<DemandVO> page = demandService.pageMyDemand(userId, herbName, status, pageNum, pageSize);
        return Result.success(page);
    }

    @ApiOperation("查询需求详情")
    @GetMapping("/{id}")
    public Result<DemandVO> getById(@PathVariable Long id) {
        DemandVO demandVO = demandService.getById(id);
        return Result.success(demandVO);
    }

    @ApiOperation("发布需求信息")
    @PostMapping
    @RequireUserType(Constants.USER_TYPE_BUYER)
    public Result<Void> add(@Validated @RequestBody DemandDTO dto, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        demandService.add(userId, dto);
        return Result.success();
    }

    @ApiOperation("更新需求信息")
    @PutMapping
    @RequireUserType(Constants.USER_TYPE_BUYER)
    public Result<Void> update(@Validated @RequestBody DemandDTO dto, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        demandService.update(userId, dto);
        return Result.success();
    }

    @ApiOperation("取消需求")
    @PutMapping("/{id}/cancel")
    @RequireUserType(Constants.USER_TYPE_BUYER)
    public Result<Void> cancel(@PathVariable Long id, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        demandService.cancel(userId, id);
        return Result.success();
    }

    @ApiOperation("删除需求信息")
    @DeleteMapping("/{id}")
    @RequireUserType(Constants.USER_TYPE_BUYER)
    public Result<Void> delete(@PathVariable Long id, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        demandService.delete(userId, id);
        return Result.success();
    }
}
