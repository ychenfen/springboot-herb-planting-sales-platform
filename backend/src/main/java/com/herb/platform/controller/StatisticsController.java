package com.herb.platform.controller;

import com.herb.platform.annotation.RequireUserType;
import com.herb.platform.common.Constants;
import com.herb.platform.common.Result;
import com.herb.platform.service.StatisticsService;
import com.herb.platform.vo.DashboardVO;
import com.herb.platform.vo.RankingVO;
import com.herb.platform.vo.StatChartVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * 统计分析控制器
 */
@Api(tags = "统计分析")
@RestController
@RequestMapping("/statistics")
@RequiredArgsConstructor
public class StatisticsController {

    private final StatisticsService statisticsService;

    @ApiOperation("获取仪表盘数据")
    @GetMapping("/dashboard")
    public Result<DashboardVO> getDashboard(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        Integer userType = (Integer) request.getAttribute("userType");
        return Result.success(statisticsService.getDashboard(userId, userType));
    }

    @ApiOperation("获取订单趋势")
    @GetMapping("/order-trend")
    public Result<StatChartVO> getOrderTrend(
            @ApiParam("天数") @RequestParam(defaultValue = "7") Integer days,
            HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        Integer userType = (Integer) request.getAttribute("userType");
        return Result.success(statisticsService.getOrderTrend(userId, userType, days));
    }

    @ApiOperation("获取供需趋势")
    @GetMapping("/supply-demand-trend")
    public Result<StatChartVO> getSupplyDemandTrend(
            @ApiParam("天数") @RequestParam(defaultValue = "7") Integer days,
            HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        Integer userType = (Integer) request.getAttribute("userType");
        return Result.success(statisticsService.getSupplyDemandTrend(userId, userType, days));
    }

    @ApiOperation("获取药材销量排行")
    @GetMapping("/herb-sales-ranking")
    public Result<List<RankingVO>> getHerbSalesRanking(
            @ApiParam("数量限制") @RequestParam(defaultValue = "10") Integer limit,
            HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        Integer userType = (Integer) request.getAttribute("userType");
        return Result.success(statisticsService.getHerbSalesRanking(userId, userType, limit));
    }

    @ApiOperation("获取种植户产量排行")
    @GetMapping("/farmer-yield-ranking")
    @RequireUserType({Constants.USER_TYPE_ADMIN, Constants.USER_TYPE_FARMER})
    public Result<List<RankingVO>> getFarmerYieldRanking(
            @ApiParam("数量限制") @RequestParam(defaultValue = "10") Integer limit,
            HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        Integer userType = (Integer) request.getAttribute("userType");
        return Result.success(statisticsService.getFarmerYieldRanking(userId, userType, limit));
    }

    @ApiOperation("获取作物品种分布")
    @GetMapping("/crop-distribution")
    @RequireUserType({Constants.USER_TYPE_ADMIN, Constants.USER_TYPE_FARMER})
    public Result<StatChartVO> getCropDistribution(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        Integer userType = (Integer) request.getAttribute("userType");
        return Result.success(statisticsService.getCropDistribution(userId, userType));
    }

    @ApiOperation("获取用户类型分布")
    @GetMapping("/user-type-distribution")
    @RequireUserType(Constants.USER_TYPE_ADMIN)
    public Result<StatChartVO> getUserTypeDistribution() {
        return Result.success(statisticsService.getUserTypeDistribution());
    }

    @ApiOperation("获取地区分布")
    @GetMapping("/region-distribution")
    @RequireUserType({Constants.USER_TYPE_ADMIN, Constants.USER_TYPE_FARMER})
    public Result<StatChartVO> getRegionDistribution(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        Integer userType = (Integer) request.getAttribute("userType");
        return Result.success(statisticsService.getRegionDistribution(userId, userType));
    }
}
