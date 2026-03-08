package com.herb.platform.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.herb.platform.annotation.RequireUserType;
import com.herb.platform.common.Constants;
import com.herb.platform.common.Result;
import com.herb.platform.dto.CropDTO;
import com.herb.platform.service.CropService;
import com.herb.platform.vo.CropVO;
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
 * 作物控制器
 */
@Api(tags = "作物管理")
@RestController
@RequestMapping("/crop")
@Validated
@RequireUserType(Constants.USER_TYPE_FARMER)
@RequiredArgsConstructor
public class CropController {

    private final CropService cropService;

    @ApiOperation("分页查询作物")
    @GetMapping("/page")
    public Result<IPage<CropVO>> page(
            @ApiParam("地块ID") @RequestParam(required = false) Long fieldId,
            @ApiParam("作物名称") @RequestParam(required = false) String cropName,
            @ApiParam("状态") @RequestParam(required = false) Integer status,
            @ApiParam("页码") @RequestParam(defaultValue = "1") @Min(value = 1, message = "页码必须大于0") int pageNum,
            @ApiParam("每页数量") @RequestParam(defaultValue = "10") @Min(value = 1, message = "每页数量必须大于0") @Max(value = 100, message = "每页数量不能超过100") int pageSize,
            HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        IPage<CropVO> page = cropService.page(userId, fieldId, cropName, status, pageNum, pageSize);
        return Result.success(page);
    }

    @ApiOperation("查询作物详情")
    @GetMapping("/{id}")
    public Result<CropVO> getById(@PathVariable Long id) {
        CropVO cropVO = cropService.getById(id);
        return Result.success(cropVO);
    }

    @ApiOperation("新增作物")
    @PostMapping
    public Result<Void> add(@Validated @RequestBody CropDTO dto, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        cropService.add(userId, dto);
        return Result.success();
    }

    @ApiOperation("更新作物")
    @PutMapping
    public Result<Void> update(@Validated @RequestBody CropDTO dto, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        cropService.update(userId, dto);
        return Result.success();
    }

    @ApiOperation("删除作物")
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        cropService.delete(userId, id);
        return Result.success();
    }

    @ApiOperation("更新作物状态")
    @PutMapping("/{id}/status/{status}")
    public Result<Void> updateStatus(@PathVariable Long id, @PathVariable Integer status, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        cropService.updateStatus(userId, id, status);
        return Result.success();
    }
}
