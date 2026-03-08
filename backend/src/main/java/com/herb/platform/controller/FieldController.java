package com.herb.platform.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.herb.platform.annotation.RequireUserType;
import com.herb.platform.common.Constants;
import com.herb.platform.common.Result;
import com.herb.platform.dto.FieldDTO;
import com.herb.platform.service.FieldService;
import com.herb.platform.vo.FieldVO;
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
 * 地块控制器
 */
@Api(tags = "地块管理")
@RestController
@RequestMapping("/field")
@Validated
@RequireUserType(Constants.USER_TYPE_FARMER)
@RequiredArgsConstructor
public class FieldController {

    private final FieldService fieldService;

    @ApiOperation("获取地块列表(下拉选择用)")
    @GetMapping("/list")
    public Result<List<FieldVO>> list(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        List<FieldVO> list = fieldService.list(userId);
        return Result.success(list);
    }

    @ApiOperation("分页查询地块")
    @GetMapping("/page")
    public Result<IPage<FieldVO>> page(
            @ApiParam("地块名称") @RequestParam(required = false) String fieldName,
            @ApiParam("状态") @RequestParam(required = false) Integer status,
            @ApiParam("页码") @RequestParam(defaultValue = "1") @Min(value = 1, message = "页码必须大于0") int pageNum,
            @ApiParam("每页数量") @RequestParam(defaultValue = "10") @Min(value = 1, message = "每页数量必须大于0") @Max(value = 100, message = "每页数量不能超过100") int pageSize,
            HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        IPage<FieldVO> page = fieldService.page(userId, fieldName, status, pageNum, pageSize);
        return Result.success(page);
    }

    @ApiOperation("查询地块详情")
    @GetMapping("/{id}")
    public Result<FieldVO> getById(@PathVariable Long id) {
        FieldVO fieldVO = fieldService.getById(id);
        return Result.success(fieldVO);
    }

    @ApiOperation("新增地块")
    @PostMapping
    public Result<Void> add(@Validated @RequestBody FieldDTO dto, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        fieldService.add(userId, dto);
        return Result.success();
    }

    @ApiOperation("更新地块")
    @PutMapping
    public Result<Void> update(@Validated @RequestBody FieldDTO dto, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        fieldService.update(userId, dto);
        return Result.success();
    }

    @ApiOperation("删除地块")
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        fieldService.delete(userId, id);
        return Result.success();
    }
}
