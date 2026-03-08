package com.herb.platform.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.herb.platform.common.ResponseCode;
import com.herb.platform.dto.DemandDTO;
import com.herb.platform.entity.Demand;
import com.herb.platform.entity.Favorite;
import com.herb.platform.entity.User;
import com.herb.platform.exception.BusinessException;
import com.herb.platform.mapper.DemandMapper;
import com.herb.platform.mapper.FavoriteMapper;
import com.herb.platform.mapper.UserMapper;
import com.herb.platform.service.DemandService;
import com.herb.platform.vo.DemandVO;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;

/**
 * 需求信息服务实现类
 */
@Service
@RequiredArgsConstructor
public class DemandServiceImpl implements DemandService {

    private final DemandMapper demandMapper;
    private final UserMapper userMapper;
    private final FavoriteMapper favoriteMapper;

    @Override
    public IPage<DemandVO> pageMarket(Long userId, String herbName, Integer status, int pageNum, int pageSize) {
        Page<Demand> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<Demand> wrapper = new LambdaQueryWrapper<>();

        wrapper.eq(Demand::getStatus, status != null ? status : 1);
        if (StringUtils.hasText(herbName)) {
            wrapper.like(Demand::getHerbName, herbName);
        }
        wrapper.orderByDesc(Demand::getIsTop);
        wrapper.orderByDesc(Demand::getCreateTime);

        IPage<Demand> demandPage = demandMapper.selectPage(page, wrapper);
        IPage<DemandVO> result = demandPage.convert(this::convertToVO);
        fillFavoriteFlags(userId, result);
        return result;
    }

    @Override
    public IPage<DemandVO> pageMyDemand(Long userId, String herbName, Integer status, int pageNum, int pageSize) {
        Page<Demand> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<Demand> wrapper = new LambdaQueryWrapper<>();

        wrapper.eq(Demand::getUserId, userId);
        if (StringUtils.hasText(herbName)) {
            wrapper.like(Demand::getHerbName, herbName);
        }
        if (status != null) {
            wrapper.eq(Demand::getStatus, status);
        }
        wrapper.orderByDesc(Demand::getCreateTime);

        IPage<Demand> demandPage = demandMapper.selectPage(page, wrapper);
        IPage<DemandVO> result = demandPage.convert(this::convertToVO);
        fillFavoriteFlags(userId, result);
        return result;
    }

    @Override
    public DemandVO getById(Long id) {
        Demand demand = demandMapper.selectById(id);
        if (demand == null) {
            throw new BusinessException(ResponseCode.DATA_NOT_FOUND);
        }
        demandMapper.incrementViewCount(id);
        return convertToVO(demand);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void add(Long userId, DemandDTO dto) {
        Demand demand = new Demand();
        BeanUtils.copyProperties(dto, demand);
        demand.setUserId(userId);
        demand.setStatus(1); // 采购中
        demand.setViewCount(0);
        demand.setIsTop(0);
        demand.setCreateTime(LocalDateTime.now());
        demand.setUpdateTime(LocalDateTime.now());
        demandMapper.insert(demand);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(Long userId, DemandDTO dto) {
        if (dto.getId() == null) {
            throw new BusinessException(ResponseCode.PARAM_ERROR, "需求ID不能为空");
        }

        Demand demand = demandMapper.selectById(dto.getId());
        if (demand == null) {
            throw new BusinessException(ResponseCode.DATA_NOT_FOUND);
        }
        if (!demand.getUserId().equals(userId)) {
            throw new BusinessException(ResponseCode.FORBIDDEN, "无权操作此需求信息");
        }

        BeanUtils.copyProperties(dto, demand, "id", "userId", "status", "viewCount", "createTime");
        demand.setUpdateTime(LocalDateTime.now());
        demandMapper.updateById(demand);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void cancel(Long userId, Long id) {
        Demand demand = demandMapper.selectById(id);
        if (demand == null) {
            throw new BusinessException(ResponseCode.DATA_NOT_FOUND);
        }
        if (!demand.getUserId().equals(userId)) {
            throw new BusinessException(ResponseCode.FORBIDDEN, "无权操作此需求信息");
        }

        demand.setStatus(3); // 已取消
        demand.setUpdateTime(LocalDateTime.now());
        demandMapper.updateById(demand);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(Long userId, Long id) {
        Demand demand = demandMapper.selectById(id);
        if (demand == null) {
            throw new BusinessException(ResponseCode.DATA_NOT_FOUND);
        }
        if (!demand.getUserId().equals(userId)) {
            throw new BusinessException(ResponseCode.FORBIDDEN, "无权操作此需求信息");
        }

        demandMapper.deleteById(id);
    }

    private DemandVO convertToVO(Demand demand) {
        DemandVO vo = new DemandVO();
        BeanUtils.copyProperties(demand, vo);
        vo.setStatusName(getStatusName(demand.getStatus()));

        User user = userMapper.selectById(demand.getUserId());
        if (user != null) {
            vo.setUsername(user.getRealName() != null ? user.getRealName() : user.getUsername());
        }

        return vo;
    }

    private void fillFavoriteFlags(Long userId, IPage<DemandVO> page) {
        if (userId == null || page == null || page.getRecords() == null || page.getRecords().isEmpty()) {
            return;
        }
        java.util.List<Long> ids = page.getRecords().stream()
                .map(DemandVO::getId)
                .filter(id -> id != null)
                .collect(java.util.stream.Collectors.toList());
        if (ids.isEmpty()) {
            return;
        }
        LambdaQueryWrapper<Favorite> wrapper = new LambdaQueryWrapper<>();
        wrapper.select(Favorite::getTargetId);
        wrapper.eq(Favorite::getUserId, userId);
        wrapper.eq(Favorite::getTargetType, 2);
        wrapper.in(Favorite::getTargetId, ids);
        java.util.List<Favorite> favorites = favoriteMapper.selectList(wrapper);
        java.util.Set<Long> favoriteIds = favorites.stream()
                .map(Favorite::getTargetId)
                .filter(id -> id != null)
                .collect(java.util.stream.Collectors.toSet());
        for (DemandVO vo : page.getRecords()) {
            vo.setIsFavorite(favoriteIds.contains(vo.getId()) ? 1 : 0);
        }
    }

    private String getStatusName(Integer status) {
        if (status == null) return "";
        switch (status) {
            case 1: return "采购中";
            case 2: return "已完成";
            case 3: return "已取消";
            default: return "";
        }
    }
}
