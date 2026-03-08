package com.herb.platform.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.herb.platform.annotation.RequireUserType;
import com.herb.platform.common.Constants;
import com.herb.platform.common.Result;
import com.herb.platform.dto.TraceDTO;
import com.herb.platform.dto.TraceNodeDTO;
import com.herb.platform.service.TraceService;
import com.herb.platform.vo.TraceVO;
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
 * 溯源管理控制器
 */
@Api(tags = "溯源管理")
@RestController
@RequestMapping("/trace")
@Validated
@RequiredArgsConstructor
public class TraceController {

    private final TraceService traceService;

    @ApiOperation("分页查询溯源信息")
    @GetMapping("/page")
    @RequireUserType(Constants.USER_TYPE_FARMER)
    public Result<IPage<TraceVO>> page(
            HttpServletRequest request,
            @ApiParam("作物ID") @RequestParam(required = false) Long cropId,
            @ApiParam("药材名称") @RequestParam(required = false) String herbName,
            @ApiParam("状态") @RequestParam(required = false) Integer status,
            @ApiParam("页码") @RequestParam(defaultValue = "1") @Min(value = 1, message = "页码必须大于0") int pageNum,
            @ApiParam("每页数量") @RequestParam(defaultValue = "10") @Min(value = 1, message = "每页数量必须大于0") @Max(value = 100, message = "每页数量不能超过100") int pageSize) {
        Long userId = (Long) request.getAttribute("userId");
        return Result.success(traceService.page(userId, cropId, herbName, status, pageNum, pageSize));
    }

    @ApiOperation("获取溯源详情")
    @GetMapping("/{id}")
    @RequireUserType(Constants.USER_TYPE_FARMER)
    public Result<TraceVO> getById(@PathVariable Long id) {
        return Result.success(traceService.getById(id));
    }

    @ApiOperation("根据溯源码查询（公开接口）")
    @GetMapping("/public/{traceCode}")
    public Result<TraceVO> getByTraceCode(@PathVariable String traceCode) {
        return Result.success(traceService.getByTraceCode(traceCode));
    }

    @ApiOperation("创建溯源信息")
    @PostMapping
    @RequireUserType(Constants.USER_TYPE_FARMER)
    public Result<Long> create(HttpServletRequest request, @Valid @RequestBody TraceDTO dto) {
        Long userId = (Long) request.getAttribute("userId");
        return Result.success(traceService.create(userId, dto));
    }

    @ApiOperation("更新溯源信息")
    @PutMapping
    @RequireUserType(Constants.USER_TYPE_FARMER)
    public Result<Void> update(HttpServletRequest request, @Valid @RequestBody TraceDTO dto) {
        Long userId = (Long) request.getAttribute("userId");
        traceService.update(userId, dto);
        return Result.success();
    }

    @ApiOperation("删除溯源信息")
    @DeleteMapping("/{id}")
    @RequireUserType(Constants.USER_TYPE_FARMER)
    public Result<Void> delete(HttpServletRequest request, @PathVariable Long id) {
        Long userId = (Long) request.getAttribute("userId");
        traceService.delete(userId, id);
        return Result.success();
    }

    @ApiOperation("发布溯源信息")
    @PostMapping("/{id}/publish")
    @RequireUserType(Constants.USER_TYPE_FARMER)
    public Result<Void> publish(HttpServletRequest request, @PathVariable Long id) {
        Long userId = (Long) request.getAttribute("userId");
        traceService.publish(userId, id);
        return Result.success();
    }

    @ApiOperation("添加溯源节点")
    @PostMapping("/node")
    @RequireUserType(Constants.USER_TYPE_FARMER)
    public Result<Void> addNode(HttpServletRequest request, @Valid @RequestBody TraceNodeDTO dto) {
        Long userId = (Long) request.getAttribute("userId");
        traceService.addNode(userId, dto);
        return Result.success();
    }

    @ApiOperation("更新溯源节点")
    @PutMapping("/node")
    @RequireUserType(Constants.USER_TYPE_FARMER)
    public Result<Void> updateNode(HttpServletRequest request, @Valid @RequestBody TraceNodeDTO dto) {
        Long userId = (Long) request.getAttribute("userId");
        traceService.updateNode(userId, dto);
        return Result.success();
    }

    @ApiOperation("删除溯源节点")
    @DeleteMapping("/node/{nodeId}")
    @RequireUserType(Constants.USER_TYPE_FARMER)
    public Result<Void> deleteNode(HttpServletRequest request, @PathVariable Long nodeId) {
        Long userId = (Long) request.getAttribute("userId");
        traceService.deleteNode(userId, nodeId);
        return Result.success();
    }

    @ApiOperation("生成二维码")
    @PostMapping("/{id}/qrcode")
    @RequireUserType(Constants.USER_TYPE_FARMER)
    public Result<String> generateQRCode(@PathVariable Long id) {
        return Result.success(traceService.generateQRCode(id));
    }
}
