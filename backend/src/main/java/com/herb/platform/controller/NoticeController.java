package com.herb.platform.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.herb.platform.common.Result;
import com.herb.platform.service.NoticeService;
import com.herb.platform.vo.NoticeVO;
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
 * 通知中心控制器
 */
@Api(tags = "通知中心")
@RestController
@RequestMapping("/notice")
@Validated
@RequiredArgsConstructor
public class NoticeController {

    private final NoticeService noticeService;

    @ApiOperation("分页查询当前用户通知")
    @GetMapping("/page")
    public Result<IPage<NoticeVO>> page(
            @ApiParam("通知类型") @RequestParam(required = false) Integer noticeType,
            @ApiParam("是否已读") @RequestParam(required = false) Integer isRead,
            @ApiParam("标题") @RequestParam(required = false) String title,
            @ApiParam("页码") @RequestParam(defaultValue = "1") @Min(value = 1, message = "页码必须大于0") int pageNum,
            @ApiParam("每页数量") @RequestParam(defaultValue = "10") @Min(value = 1, message = "每页数量必须大于0") @Max(value = 100, message = "每页数量不能超过100") int pageSize,
            HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        return Result.success(noticeService.pageByUser(userId, noticeType, isRead, title, pageNum, pageSize));
    }

    @ApiOperation("获取通知详情")
    @GetMapping("/{id}")
    public Result<NoticeVO> getById(@PathVariable Long id, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        return Result.success(noticeService.getById(id, userId));
    }

    @ApiOperation("未读通知数量")
    @GetMapping("/unread-count")
    public Result<Long> countUnread(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        return Result.success(noticeService.countUnread(userId));
    }

    @ApiOperation("标记通知已读")
    @PutMapping("/{id}/read")
    public Result<Void> markRead(@PathVariable Long id, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        noticeService.markRead(id, userId);
        return Result.success();
    }

    @ApiOperation("全部标记已读")
    @PutMapping("/read-all")
    public Result<Void> markAllRead(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        noticeService.markAllRead(userId);
        return Result.success();
    }
}
