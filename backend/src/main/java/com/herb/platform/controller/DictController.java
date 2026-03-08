package com.herb.platform.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.herb.platform.annotation.RequireUserType;
import com.herb.platform.common.Constants;
import com.herb.platform.common.Result;
import com.herb.platform.dto.DictDTO;
import com.herb.platform.service.DictService;
import com.herb.platform.vo.DictVO;
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
 * 数据字典控制器
 */
@Api(tags = "数据字典")
@RestController
@RequestMapping("/system/dict")
@Validated
@RequireUserType(Constants.USER_TYPE_ADMIN)
@RequiredArgsConstructor
public class DictController {

    private final DictService dictService;

    @ApiOperation("分页查询字典")
    @GetMapping("/page")
    public Result<IPage<DictVO>> page(
            @ApiParam("字典类型") @RequestParam(required = false) String dictType,
            @ApiParam("状态") @RequestParam(required = false) Integer status,
            @ApiParam("页码") @RequestParam(defaultValue = "1") @Min(value = 1, message = "页码必须大于0") int pageNum,
            @ApiParam("每页数量") @RequestParam(defaultValue = "10") @Min(value = 1, message = "每页数量必须大于0") @Max(value = 100, message = "每页数量不能超过100") int pageSize) {
        return Result.success(dictService.page(dictType, status, pageNum, pageSize));
    }

    @ApiOperation("根据类型获取字典列表")
    @GetMapping("/type/{dictType}")
    public Result<List<DictVO>> listByType(@PathVariable String dictType) {
        return Result.success(dictService.listByType(dictType));
    }

    @ApiOperation("获取所有字典类型")
    @GetMapping("/types")
    public Result<List<String>> listTypes() {
        return Result.success(dictService.listTypes());
    }

    @ApiOperation("创建字典")
    @PostMapping
    public Result<Void> create(@Valid @RequestBody DictDTO dto) {
        dictService.create(dto);
        return Result.success();
    }

    @ApiOperation("更新字典")
    @PutMapping
    public Result<Void> update(@Valid @RequestBody DictDTO dto) {
        dictService.update(dto);
        return Result.success();
    }

    @ApiOperation("删除字典")
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        dictService.delete(id);
        return Result.success();
    }
}
