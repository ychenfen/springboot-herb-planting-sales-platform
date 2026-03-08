package com.herb.platform.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.herb.platform.annotation.RequireUserType;
import com.herb.platform.common.Constants;
import com.herb.platform.common.Result;
import com.herb.platform.dto.ConfigDTO;
import com.herb.platform.service.ConfigService;
import com.herb.platform.vo.ConfigVO;
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
 * 系统配置控制器
 */
@Api(tags = "系统配置")
@RestController
@RequestMapping("/system/config")
@Validated
@RequireUserType(Constants.USER_TYPE_ADMIN)
@RequiredArgsConstructor
public class ConfigController {

    private final ConfigService configService;

    @ApiOperation("分页查询配置")
    @GetMapping("/page")
    public Result<IPage<ConfigVO>> page(
            @ApiParam("配置键") @RequestParam(required = false) String configKey,
            @ApiParam("配置类型") @RequestParam(required = false) String configType,
            @ApiParam("页码") @RequestParam(defaultValue = "1") @Min(value = 1, message = "页码必须大于0") int pageNum,
            @ApiParam("每页数量") @RequestParam(defaultValue = "10") @Min(value = 1, message = "每页数量必须大于0") @Max(value = 100, message = "每页数量不能超过100") int pageSize) {
        return Result.success(configService.page(configKey, configType, pageNum, pageSize));
    }

    @ApiOperation("获取所有配置类型")
    @GetMapping("/types")
    public Result<List<String>> listTypes() {
        return Result.success(configService.listTypes());
    }

    @ApiOperation("获取配置详情")
    @GetMapping("/{id}")
    public Result<ConfigVO> getById(@PathVariable Long id) {
        return Result.success(configService.getById(id));
    }

    @ApiOperation("创建配置")
    @PostMapping
    public Result<Void> create(@Valid @RequestBody ConfigDTO dto) {
        configService.create(dto);
        return Result.success();
    }

    @ApiOperation("更新配置")
    @PutMapping
    public Result<Void> update(@Valid @RequestBody ConfigDTO dto) {
        configService.update(dto);
        return Result.success();
    }

    @ApiOperation("删除配置")
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        configService.delete(id);
        return Result.success();
    }
}
