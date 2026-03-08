package com.herb.platform.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.herb.platform.annotation.RequireUserType;
import com.herb.platform.common.Constants;
import com.herb.platform.common.Result;
import com.herb.platform.dto.NoticeDTO;
import com.herb.platform.service.NoticeService;
import com.herb.platform.vo.NoticeVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

/**
 * 系统通知控制器（管理员）
 */
@Api(tags = "系统通知")
@RestController
@RequestMapping("/system/notice")
@Validated
@RequireUserType(Constants.USER_TYPE_ADMIN)
@RequiredArgsConstructor
public class SystemNoticeController {

    private final NoticeService noticeService;

    @ApiOperation("分页查询通知")
    @GetMapping("/page")
    public Result<IPage<NoticeVO>> page(
            @ApiParam("用户ID") @RequestParam(required = false) Long userId,
            @ApiParam("通知类型") @RequestParam(required = false) Integer noticeType,
            @ApiParam("是否已读") @RequestParam(required = false) Integer isRead,
            @ApiParam("标题") @RequestParam(required = false) String title,
            @ApiParam("页码") @RequestParam(defaultValue = "1") @Min(value = 1, message = "页码必须大于0") int pageNum,
            @ApiParam("每页数量") @RequestParam(defaultValue = "10") @Min(value = 1, message = "每页数量必须大于0") @Max(value = 100, message = "每页数量不能超过100") int pageSize) {
        return Result.success(noticeService.page(userId, noticeType, isRead, title, pageNum, pageSize));
    }

    @ApiOperation("获取通知详情")
    @GetMapping("/{id}")
    public Result<NoticeVO> getById(@PathVariable Long id) {
        return Result.success(noticeService.getById(id, null));
    }

    @ApiOperation("创建通知")
    @PostMapping
    public Result<Void> create(@Valid @RequestBody NoticeDTO dto) {
        noticeService.create(dto);
        return Result.success();
    }

    @ApiOperation("更新通知")
    @PutMapping
    public Result<Void> update(@Valid @RequestBody NoticeDTO dto) {
        noticeService.update(dto);
        return Result.success();
    }

    @ApiOperation("删除通知")
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        noticeService.delete(id);
        return Result.success();
    }
}
