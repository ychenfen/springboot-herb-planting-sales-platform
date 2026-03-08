package com.herb.platform.service;

import com.herb.platform.vo.DashboardVO;
import com.herb.platform.vo.RankingVO;
import com.herb.platform.vo.StatChartVO;

import java.util.List;

/**
 * 统计分析服务接口
 */
public interface StatisticsService {

    /**
     * 获取仪表盘数据
     */
    DashboardVO getDashboard(Long userId, Integer userType);

    /**
     * 获取订单趋势统计
     */
    StatChartVO getOrderTrend(Long userId, Integer userType, Integer days);

    /**
     * 获取供需趋势统计
     */
    StatChartVO getSupplyDemandTrend(Long userId, Integer userType, Integer days);

    /**
     * 获取药材销量排行
     */
    List<RankingVO> getHerbSalesRanking(Long userId, Integer userType, Integer limit);

    /**
     * 获取种植户产量排行
     */
    List<RankingVO> getFarmerYieldRanking(Long userId, Integer userType, Integer limit);

    /**
     * 获取作物品种分布
     */
    StatChartVO getCropDistribution(Long userId, Integer userType);

    /**
     * 获取用户类型分布
     */
    StatChartVO getUserTypeDistribution();

    /**
     * 获取地区分布统计
     */
    StatChartVO getRegionDistribution(Long userId, Integer userType);
}
