package com.herb.platform.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.herb.platform.common.Result;
import com.herb.platform.service.KnowledgeService;
import com.herb.platform.vo.CalendarReminderVO;
import com.herb.platform.vo.DiseaseRecognitionVO;
import com.herb.platform.vo.HerbCalendarVO;
import com.herb.platform.vo.HerbDiseaseVO;
import com.herb.platform.vo.HerbKnowledgeVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import java.time.LocalDate;
import java.util.List;

/**
 * Knowledge controller.
 */
@Api(tags = "种植知识")
@RestController
@RequestMapping("/knowledge")
@Validated
@RequiredArgsConstructor
public class KnowledgeController {

    private final KnowledgeService knowledgeService;

    @ApiOperation("中药材百科分页检索")
    @GetMapping("/page")
    public Result<IPage<HerbKnowledgeVO>> pageKnowledge(
            @ApiParam("关键词") @RequestParam(required = false) String keyword,
            @ApiParam("药材分类") @RequestParam(required = false) String herbCategory,
            @ApiParam("种植季节") @RequestParam(required = false) String plantingSeason,
            @ApiParam("病害类型") @RequestParam(required = false) String diseaseType,
            @ApiParam("页码") @RequestParam(defaultValue = "1") @Min(1) int pageNum,
            @ApiParam("每页数量") @RequestParam(defaultValue = "10") @Min(1) @Max(100) int pageSize) {
        return Result.success(knowledgeService.pageKnowledge(keyword, herbCategory, plantingSeason, diseaseType, pageNum, pageSize));
    }

    @ApiOperation("病虫害图文库")
    @GetMapping("/diseases")
    public Result<List<HerbDiseaseVO>> listDiseases(
            @ApiParam("药材名称") @RequestParam(required = false) String herbName,
            @ApiParam("病害类型") @RequestParam(required = false) String diseaseType,
            @ApiParam("关键词") @RequestParam(required = false) String keyword) {
        return Result.success(knowledgeService.listDiseases(herbName, diseaseType, keyword));
    }

    @ApiOperation("病虫害相似识别")
    @PostMapping(value = "/disease/identify", consumes = "multipart/form-data")
    public Result<DiseaseRecognitionVO> identifyDisease(
            @ApiParam("药材名称") @RequestParam(required = false) String herbName,
            @RequestPart("file") MultipartFile file) {
        return Result.success(knowledgeService.identifyDisease(herbName, file));
    }

    @ApiOperation("种植日历")
    @GetMapping("/calendar")
    public Result<HerbCalendarVO> getCalendar(
            @ApiParam("作物ID") @RequestParam(required = false) Long cropId,
            @ApiParam("药材名称") @RequestParam(required = false) String herbName,
            @ApiParam("播种日期") @RequestParam(required = false) String plantDate,
            HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        LocalDate parsedPlantDate = plantDate == null || plantDate.trim().isEmpty() ? null : LocalDate.parse(plantDate);
        return Result.success(knowledgeService.getCalendar(userId, cropId, herbName, parsedPlantDate));
    }

    @ApiOperation("近期农事提醒")
    @GetMapping("/calendar/reminders")
    public Result<List<CalendarReminderVO>> getUpcomingReminders(
            @ApiParam("未来天数") @RequestParam(defaultValue = "15") @Min(1) @Max(60) int days,
            HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        return Result.success(knowledgeService.getUpcomingReminders(userId, days));
    }
}
