package com.herb.platform.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.herb.platform.annotation.RequireUserType;
import com.herb.platform.common.Constants;
import com.herb.platform.common.Result;
import com.herb.platform.service.LogService;
import com.herb.platform.vo.LogVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import java.time.LocalDateTime;

/**
 * 系统日志控制器
 */
@Api(tags = "系统日志")
@RestController
@RequestMapping("/system/log")
@Validated
@RequireUserType(Constants.USER_TYPE_ADMIN)
@RequiredArgsConstructor
public class LogController {

    private final LogService logService;

    @ApiOperation("分页查询日志")
    @GetMapping("/page")
    public Result<IPage<LogVO>> page(
            @ApiParam("用户名") @RequestParam(required = false) String username,
            @ApiParam("操作") @RequestParam(required = false) String operation,
            @ApiParam("状态") @RequestParam(required = false) Integer status,
            @ApiParam("开始时间") @RequestParam(required = false)
            @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime startTime,
            @ApiParam("结束时间") @RequestParam(required = false)
            @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime endTime,
            @ApiParam("页码") @RequestParam(defaultValue = "1") @Min(value = 1, message = "页码必须大于0") int pageNum,
            @ApiParam("每页数量") @RequestParam(defaultValue = "10") @Min(value = 1, message = "每页数量必须大于0") @Max(value = 100, message = "每页数量不能超过100") int pageSize) {
        return Result.success(logService.page(username, operation, status, startTime, endTime, pageNum, pageSize));
    }

    @ApiOperation("获取日志详情")
    @GetMapping("/{id}")
    public Result<LogVO> getById(@PathVariable Long id) {
        return Result.success(logService.getById(id));
    }

    @ApiOperation("删除日志")
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        logService.delete(id);
        return Result.success();
    }

    @ApiOperation("清空日志")
    @DeleteMapping("/clear")
    public Result<Void> clear() {
        logService.clear();
        return Result.success();
    }
}
