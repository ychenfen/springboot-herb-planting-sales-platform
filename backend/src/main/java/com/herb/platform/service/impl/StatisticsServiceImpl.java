package com.herb.platform.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.herb.platform.common.Constants;
import com.herb.platform.entity.*;
import com.herb.platform.mapper.*;
import com.herb.platform.service.StatisticsService;
import com.herb.platform.vo.DashboardVO;
import com.herb.platform.vo.RankingVO;
import com.herb.platform.vo.StatChartVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 统计分析服务实现类
 */
@Service
@RequiredArgsConstructor
public class StatisticsServiceImpl implements StatisticsService {

    private final UserMapper userMapper;
    private final FieldMapper fieldMapper;
    private final CropMapper cropMapper;
    private final SupplyMapper supplyMapper;
    private final DemandMapper demandMapper;
    private final OrderMapper orderMapper;
    private final TraceMapper traceMapper;

    @Override
    public DashboardVO getDashboard(Long userId, Integer userType) {
        DashboardVO vo = new DashboardVO();
        boolean isAdmin = isAdmin(userType);
        boolean isFarmer = isFarmer(userType);
        boolean isBuyer = isBuyer(userType);

        // 用户统计（仅管理员展示）
        if (isAdmin) {
            vo.setUserCount(userMapper.selectCount(new LambdaQueryWrapper<User>().eq(User::getDeleted, 0)));
            vo.setFarmerCount(userMapper.selectCount(new LambdaQueryWrapper<User>()
                    .eq(User::getUserType, 1).eq(User::getDeleted, 0)));
            vo.setBuyerCount(userMapper.selectCount(new LambdaQueryWrapper<User>()
                    .in(User::getUserType, Constants.USER_TYPE_MERCHANT, Constants.USER_TYPE_USER)
                    .eq(User::getDeleted, 0)));
        }

        // 地块和作物统计
        if (isAdmin || isFarmer) {
            LambdaQueryWrapper<Field> fieldWrapper = new LambdaQueryWrapper<>();
            LambdaQueryWrapper<Crop> cropWrapper = new LambdaQueryWrapper<>();
            if (isFarmer && userId != null) {
                fieldWrapper.eq(Field::getUserId, userId);
                cropWrapper.eq(Crop::getUserId, userId);
            }
            vo.setFieldCount(fieldMapper.selectCount(fieldWrapper));
            vo.setCropCount(cropMapper.selectCount(cropWrapper));
            vo.setGrowingCropCount(cropMapper.selectCount(
                    cropWrapper.clone().eq(Crop::getStatus, 1)));
        } else {
            vo.setFieldCount(0L);
            vo.setCropCount(0L);
            vo.setGrowingCropCount(0L);
        }

        // 供需统计
        if (isAdmin) {
            vo.setSupplyCount(supplyMapper.selectCount(new LambdaQueryWrapper<>()));
            vo.setDemandCount(demandMapper.selectCount(new LambdaQueryWrapper<>()));
        } else if (isFarmer && userId != null) {
            vo.setSupplyCount(supplyMapper.selectCount(new LambdaQueryWrapper<Supply>()
                    .eq(Supply::getUserId, userId)));
            vo.setDemandCount(0L);
        } else if (isBuyer && userId != null) {
            vo.setSupplyCount(0L);
            vo.setDemandCount(demandMapper.selectCount(new LambdaQueryWrapper<Demand>()
                    .eq(Demand::getUserId, userId)));
        } else {
            vo.setSupplyCount(0L);
            vo.setDemandCount(0L);
        }

        // 订单统计
        LambdaQueryWrapper<Order> orderWrapper = new LambdaQueryWrapper<>();
        if (userType != null && userId != null) {
            if (userType == Constants.USER_TYPE_FARMER) {
                orderWrapper.eq(Order::getSellerId, userId);
            } else if (userType == Constants.USER_TYPE_MERCHANT || userType == Constants.USER_TYPE_USER) {
                orderWrapper.eq(Order::getBuyerId, userId);
            }
        }

        List<Order> orders = orderMapper.selectList(orderWrapper);
        vo.setOrderCount((long) orders.size());

        BigDecimal totalAmount = orders.stream()
                .map(Order::getTotalAmount)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        vo.setTotalOrderAmount(totalAmount);

        // 今日订单
        LocalDateTime todayStart = LocalDate.now().atStartOfDay();
        long todayOrderCount = orders.stream()
                .filter(o -> o.getCreateTime() != null && o.getCreateTime().isAfter(todayStart))
                .count();
        vo.setTodayOrderCount(todayOrderCount);

        BigDecimal todayAmount = orders.stream()
                .filter(o -> o.getCreateTime() != null && o.getCreateTime().isAfter(todayStart))
                .map(Order::getTotalAmount)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        vo.setTodayOrderAmount(todayAmount);

        // 溯源统计
        if (isAdmin) {
            vo.setTraceCount(traceMapper.selectCount(new LambdaQueryWrapper<>()));
        } else if (userId != null) {
            vo.setTraceCount(traceMapper.selectCount(new LambdaQueryWrapper<Trace>()
                    .eq(Trace::getUserId, userId)));
        } else {
            vo.setTraceCount(0L);
        }

        // 今日扫码次数（简化实现）
        vo.setTodayScanCount(0L);

        return vo;
    }

    @Override
    public StatChartVO getOrderTrend(Long userId, Integer userType, Integer days) {
        if (days == null || days <= 0) {
            days = 7;
        }
        boolean isAdmin = isAdmin(userType);

        StatChartVO vo = new StatChartVO();
        List<String> dates = new ArrayList<>();
        List<Object> orderCounts = new ArrayList<>();
        List<Object> orderAmounts = new ArrayList<>();

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM-dd");
        LocalDate today = LocalDate.now();

        for (int i = days - 1; i >= 0; i--) {
            LocalDate date = today.minusDays(i);
            dates.add(date.format(formatter));

            LocalDateTime startOfDay = date.atStartOfDay();
            LocalDateTime endOfDay = date.plusDays(1).atStartOfDay();

            LambdaQueryWrapper<Order> wrapper = new LambdaQueryWrapper<>();
            wrapper.ge(Order::getCreateTime, startOfDay);
            wrapper.lt(Order::getCreateTime, endOfDay);
            if (!isAdmin && userId != null) {
                if (isFarmer(userType)) {
                    wrapper.eq(Order::getSellerId, userId);
                } else if (isBuyer(userType)) {
                    wrapper.eq(Order::getBuyerId, userId);
                }
            }
            List<Order> dayOrders = orderMapper.selectList(wrapper);

            orderCounts.add(dayOrders.size());

            BigDecimal dayAmount = dayOrders.stream()
                    .map(Order::getTotalAmount)
                    .filter(Objects::nonNull)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            orderAmounts.add(dayAmount);
        }

        vo.setDates(dates);
        vo.setDataList(Arrays.asList(
                new StatChartVO.ChartData("订单数", orderCounts),
                new StatChartVO.ChartData("订单金额", orderAmounts)
        ));

        return vo;
    }

    @Override
    public StatChartVO getSupplyDemandTrend(Long userId, Integer userType, Integer days) {
        if (days == null || days <= 0) {
            days = 7;
        }
        boolean isAdmin = isAdmin(userType);
        boolean isFarmer = isFarmer(userType);
        boolean isBuyer = isBuyer(userType);

        StatChartVO vo = new StatChartVO();
        List<String> dates = new ArrayList<>();
        List<Object> supplyCounts = new ArrayList<>();
        List<Object> demandCounts = new ArrayList<>();

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM-dd");
        LocalDate today = LocalDate.now();

        for (int i = days - 1; i >= 0; i--) {
            LocalDate date = today.minusDays(i);
            dates.add(date.format(formatter));

            LocalDateTime startOfDay = date.atStartOfDay();
            LocalDateTime endOfDay = date.plusDays(1).atStartOfDay();

            Long supplyCount;
            if (isAdmin) {
                supplyCount = supplyMapper.selectCount(new LambdaQueryWrapper<Supply>()
                        .ge(Supply::getCreateTime, startOfDay)
                        .lt(Supply::getCreateTime, endOfDay));
            } else if (isFarmer && userId != null) {
                supplyCount = supplyMapper.selectCount(new LambdaQueryWrapper<Supply>()
                        .eq(Supply::getUserId, userId)
                        .ge(Supply::getCreateTime, startOfDay)
                        .lt(Supply::getCreateTime, endOfDay));
            } else {
                supplyCount = 0L;
            }
            supplyCounts.add(supplyCount);

            Long demandCount;
            if (isAdmin) {
                demandCount = demandMapper.selectCount(new LambdaQueryWrapper<Demand>()
                        .ge(Demand::getCreateTime, startOfDay)
                        .lt(Demand::getCreateTime, endOfDay));
            } else if (isBuyer && userId != null) {
                demandCount = demandMapper.selectCount(new LambdaQueryWrapper<Demand>()
                        .eq(Demand::getUserId, userId)
                        .ge(Demand::getCreateTime, startOfDay)
                        .lt(Demand::getCreateTime, endOfDay));
            } else {
                demandCount = 0L;
            }
            demandCounts.add(demandCount);
        }

        vo.setDates(dates);
        vo.setDataList(Arrays.asList(
                new StatChartVO.ChartData("供应发布", supplyCounts),
                new StatChartVO.ChartData("需求发布", demandCounts)
        ));

        return vo;
    }

    @Override
    public List<RankingVO> getHerbSalesRanking(Long userId, Integer userType, Integer limit) {
        if (limit == null || limit <= 0) {
            limit = 10;
        }
        boolean isAdmin = isAdmin(userType);

        // 按药材名称统计已完成订单的销量
        LambdaQueryWrapper<Order> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Order::getOrderStatus, 4); // 已完成
        if (!isAdmin && userId != null) {
            if (isFarmer(userType)) {
                wrapper.eq(Order::getSellerId, userId);
            } else if (isBuyer(userType)) {
                wrapper.eq(Order::getBuyerId, userId);
            }
        }
        List<Order> orders = orderMapper.selectList(wrapper);

        Map<String, BigDecimal> salesMap = new HashMap<>();
        for (Order order : orders) {
            String herbName = order.getHerbName();
            if (herbName != null) {
                salesMap.merge(herbName,
                        order.getTotalAmount() != null ? order.getTotalAmount() : BigDecimal.ZERO,
                        BigDecimal::add);
            }
        }

        List<RankingVO> rankings = new ArrayList<>();
        int rank = 1;
        List<Map.Entry<String, BigDecimal>> sortedEntries = salesMap.entrySet().stream()
                .sorted(Map.Entry.<String, BigDecimal>comparingByValue().reversed())
                .limit(limit)
                .collect(Collectors.toList());

        for (Map.Entry<String, BigDecimal> entry : sortedEntries) {
            rankings.add(new RankingVO(rank++, null, entry.getKey(), entry.getValue(), "元"));
        }

        return rankings;
    }

    @Override
    public List<RankingVO> getFarmerYieldRanking(Long userId, Integer userType, Integer limit) {
        if (limit == null || limit <= 0) {
            limit = 10;
        }
        boolean isAdmin = isAdmin(userType);
        boolean isFarmer = isFarmer(userType);
        if (!isAdmin && !isFarmer) {
            return Collections.emptyList();
        }

        // 统计种植户已收获作物产量
        LambdaQueryWrapper<Crop> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Crop::getStatus, 2); // 已收获
        if (!isAdmin && userId != null) {
            wrapper.eq(Crop::getUserId, userId);
        }
        List<Crop> crops = cropMapper.selectList(wrapper);

        Map<Long, BigDecimal> yieldMap = new HashMap<>();
        for (Crop crop : crops) {
            Long cropUserId = crop.getUserId();
            BigDecimal yield = crop.getActualYield() != null ? crop.getActualYield() : BigDecimal.ZERO;
            yieldMap.merge(cropUserId, yield, BigDecimal::add);
        }

        List<RankingVO> rankings = new ArrayList<>();
        int rank = 1;
        List<Map.Entry<Long, BigDecimal>> sortedEntries = yieldMap.entrySet().stream()
                .sorted(Map.Entry.<Long, BigDecimal>comparingByValue().reversed())
                .limit(limit)
                .collect(Collectors.toList());

        for (Map.Entry<Long, BigDecimal> entry : sortedEntries) {
            User user = userMapper.selectById(entry.getKey());
            String name = user != null ?
                    (user.getRealName() != null ? user.getRealName() : user.getUsername()) : "未知";
            rankings.add(new RankingVO(rank++, entry.getKey(), name, entry.getValue(), "kg"));
        }

        return rankings;
    }

    @Override
    public StatChartVO getCropDistribution(Long userId, Integer userType) {
        StatChartVO vo = new StatChartVO();
        boolean isAdmin = isAdmin(userType);
        boolean isFarmer = isFarmer(userType);
        if (!isAdmin && !isFarmer) {
            vo.setDates(Collections.emptyList());
            vo.setDataList(Collections.singletonList(
                    new StatChartVO.ChartData("作物数量", Collections.emptyList())
            ));
            return vo;
        }

        // 按作物名称分组统计
        LambdaQueryWrapper<Crop> wrapper = new LambdaQueryWrapper<>();
        if (!isAdmin && userId != null) {
            wrapper.eq(Crop::getUserId, userId);
        }
        List<Crop> crops = cropMapper.selectList(wrapper);
        Map<String, Long> countMap = crops.stream()
                .filter(c -> c.getCropName() != null)
                .collect(Collectors.groupingBy(Crop::getCropName, Collectors.counting()));

        List<String> names = new ArrayList<>(countMap.keySet());
        List<Object> values = names.stream().map(countMap::get).collect(Collectors.toList());

        vo.setDates(names);
        vo.setDataList(Collections.singletonList(
                new StatChartVO.ChartData("作物数量", values)
        ));

        return vo;
    }

    @Override
    public StatChartVO getUserTypeDistribution() {
        StatChartVO vo = new StatChartVO();

        Long farmerCount = userMapper.selectCount(new LambdaQueryWrapper<User>()
                .eq(User::getUserType, Constants.USER_TYPE_FARMER).eq(User::getDeleted, 0));
        Long merchantCount = userMapper.selectCount(new LambdaQueryWrapper<User>()
                .eq(User::getUserType, Constants.USER_TYPE_MERCHANT).eq(User::getDeleted, 0));
        Long adminCount = userMapper.selectCount(new LambdaQueryWrapper<User>()
                .eq(User::getUserType, Constants.USER_TYPE_ADMIN).eq(User::getDeleted, 0));
        Long normalUserCount = userMapper.selectCount(new LambdaQueryWrapper<User>()
                .eq(User::getUserType, Constants.USER_TYPE_USER).eq(User::getDeleted, 0));

        vo.setDates(Arrays.asList("种植户", "商家", "管理员", "普通用户"));
        vo.setDataList(Collections.singletonList(
                new StatChartVO.ChartData("用户数量",
                        Arrays.asList(farmerCount, merchantCount, adminCount, normalUserCount))
        ));

        return vo;
    }

    @Override
    public StatChartVO getRegionDistribution(Long userId, Integer userType) {
        StatChartVO vo = new StatChartVO();
        boolean isAdmin = isAdmin(userType);
        boolean isFarmer = isFarmer(userType);
        if (!isAdmin && !isFarmer) {
            vo.setDates(Collections.emptyList());
            vo.setDataList(Collections.singletonList(
                    new StatChartVO.ChartData("地块数量", Collections.emptyList())
            ));
            return vo;
        }

        LambdaQueryWrapper<Field> wrapper = new LambdaQueryWrapper<>();
        if (!isAdmin && userId != null) {
            wrapper.eq(Field::getUserId, userId);
        }
        List<Field> fields = fieldMapper.selectList(wrapper);
        Map<String, Long> countMap = fields.stream()
                .filter(f -> f.getProvince() != null)
                .collect(Collectors.groupingBy(Field::getProvince, Collectors.counting()));

        List<String> provinces = new ArrayList<>(countMap.keySet());
        List<Object> values = provinces.stream().map(countMap::get).collect(Collectors.toList());

        vo.setDates(provinces);
        vo.setDataList(Collections.singletonList(
                new StatChartVO.ChartData("地块数量", values)
        ));

        return vo;
    }

    private boolean isAdmin(Integer userType) {
        return userType != null && userType == Constants.USER_TYPE_ADMIN;
    }

    private boolean isFarmer(Integer userType) {
        return userType != null && userType == Constants.USER_TYPE_FARMER;
    }

    private boolean isBuyer(Integer userType) {
        return userType != null && (userType == Constants.USER_TYPE_MERCHANT || userType == Constants.USER_TYPE_USER);
    }
}
