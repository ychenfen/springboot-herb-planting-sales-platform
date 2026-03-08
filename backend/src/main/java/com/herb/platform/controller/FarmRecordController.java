package com.herb.platform.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.herb.platform.annotation.RequireUserType;
import com.herb.platform.common.Constants;
import com.herb.platform.common.Result;
import com.herb.platform.dto.FarmRecordDTO;
import com.herb.platform.service.FarmRecordService;
import com.herb.platform.vo.FarmRecordVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import java.util.List;

/**
 * 农事记录控制器
 */
@Api(tags = "农事记录管理")
@RestController
@RequestMapping("/farm-record")
@Validated
@RequireUserType(Constants.USER_TYPE_FARMER)
@RequiredArgsConstructor
public class FarmRecordController {

    private final FarmRecordService farmRecordService;

    @ApiOperation("分页查询农事记录")
    @GetMapping("/page")
    public Result<IPage<FarmRecordVO>> page(
            @ApiParam("作物ID") @RequestParam(required = false) Long cropId,
            @ApiParam("活动类型") @RequestParam(required = false) String activityType,
            @ApiParam("页码") @RequestParam(defaultValue = "1") @Min(value = 1, message = "页码必须大于0") int pageNum,
            @ApiParam("每页数量") @RequestParam(defaultValue = "10") @Min(value = 1, message = "每页数量必须大于0") @Max(value = 100, message = "每页数量不能超过100") int pageSize,
            HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        IPage<FarmRecordVO> page = farmRecordService.page(userId, cropId, activityType, pageNum, pageSize);
        return Result.success(page);
    }

    @ApiOperation("查询农事记录详情")
    @GetMapping("/{id}")
    public Result<FarmRecordVO> getById(@PathVariable Long id) {
        FarmRecordVO recordVO = farmRecordService.getById(id);
        return Result.success(recordVO);
    }

    @ApiOperation("根据作物ID查询农事记录列表")
    @GetMapping("/crop/{cropId}")
    public Result<List<FarmRecordVO>> listByCropId(@PathVariable Long cropId) {
        List<FarmRecordVO> list = farmRecordService.listByCropId(cropId);
        return Result.success(list);
    }

    @ApiOperation("新增农事记录")
    @PostMapping
    public Result<Void> add(@Validated @RequestBody FarmRecordDTO dto, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        farmRecordService.add(userId, dto);
        return Result.success();
    }

    @ApiOperation("更新农事记录")
    @PutMapping
    public Result<Void> update(@Validated @RequestBody FarmRecordDTO dto, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        farmRecordService.update(userId, dto);
        return Result.success();
    }

    @ApiOperation("删除农事记录")
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        farmRecordService.delete(userId, id);
        return Result.success();
    }
}
