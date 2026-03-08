package com.herb.platform.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.herb.platform.common.ResponseCode;
import com.herb.platform.dto.SupplyDTO;
import com.herb.platform.entity.Supply;
import com.herb.platform.entity.Favorite;
import com.herb.platform.entity.User;
import com.herb.platform.exception.BusinessException;
import com.herb.platform.mapper.FavoriteMapper;
import com.herb.platform.mapper.SupplyMapper;
import com.herb.platform.mapper.UserMapper;
import com.herb.platform.service.SupplyService;
import com.herb.platform.vo.SupplyVO;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;

/**
 * 供应信息服务实现类
 */
@Service
@RequiredArgsConstructor
public class SupplyServiceImpl implements SupplyService {

    private final SupplyMapper supplyMapper;
    private final UserMapper userMapper;
    private final FavoriteMapper favoriteMapper;

    @Override
    public IPage<SupplyVO> pageMarket(Long userId, String herbName, String qualityGrade, Integer status, int pageNum, int pageSize) {
        Page<Supply> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<Supply> wrapper = new LambdaQueryWrapper<>();

        // 默认只查询供应中的
        wrapper.eq(Supply::getStatus, status != null ? status : 1);
        if (StringUtils.hasText(herbName)) {
            wrapper.like(Supply::getHerbName, herbName);
        }
        if (StringUtils.hasText(qualityGrade)) {
            wrapper.eq(Supply::getQualityGrade, qualityGrade);
        }
        wrapper.orderByDesc(Supply::getIsTop);
        wrapper.orderByDesc(Supply::getCreateTime);

        IPage<Supply> supplyPage = supplyMapper.selectPage(page, wrapper);
        IPage<SupplyVO> result = supplyPage.convert(this::convertToVO);
        fillFavoriteFlags(userId, result);
        return result;
    }

    @Override
    public IPage<SupplyVO> pageMySupply(Long userId, String herbName, Integer status, int pageNum, int pageSize) {
        Page<Supply> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<Supply> wrapper = new LambdaQueryWrapper<>();

        wrapper.eq(Supply::getUserId, userId);
        if (StringUtils.hasText(herbName)) {
            wrapper.like(Supply::getHerbName, herbName);
        }
        if (status != null) {
            wrapper.eq(Supply::getStatus, status);
        }
        wrapper.orderByDesc(Supply::getCreateTime);

        IPage<Supply> supplyPage = supplyMapper.selectPage(page, wrapper);
        IPage<SupplyVO> result = supplyPage.convert(this::convertToVO);
        fillFavoriteFlags(userId, result);
        return result;
    }

    @Override
    public SupplyVO getById(Long id) {
        Supply supply = supplyMapper.selectById(id);
        if (supply == null) {
            throw new BusinessException(ResponseCode.DATA_NOT_FOUND);
        }
        // 增加浏览次数
        supplyMapper.incrementViewCount(id);
        return convertToVO(supply);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void add(Long userId, SupplyDTO dto) {
        Supply supply = new Supply();
        BeanUtils.copyProperties(dto, supply);
        supply.setUserId(userId);
        supply.setRemainingQuantity(dto.getSupplyQuantity());
        supply.setStatus(1); // 供应中
        supply.setViewCount(0);
        supply.setFavoriteCount(0);
        supply.setIsTop(0);
        supply.setCreateTime(LocalDateTime.now());
        supply.setUpdateTime(LocalDateTime.now());
        supplyMapper.insert(supply);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(Long userId, SupplyDTO dto) {
        if (dto.getId() == null) {
            throw new BusinessException(ResponseCode.PARAM_ERROR, "供应ID不能为空");
        }

        Supply supply = supplyMapper.selectById(dto.getId());
        if (supply == null) {
            throw new BusinessException(ResponseCode.DATA_NOT_FOUND);
        }
        if (!supply.getUserId().equals(userId)) {
            throw new BusinessException(ResponseCode.FORBIDDEN, "无权操作此供应信息");
        }

        BeanUtils.copyProperties(dto, supply, "id", "userId", "status", "viewCount", "favoriteCount", "createTime");
        supply.setUpdateTime(LocalDateTime.now());
        supplyMapper.updateById(supply);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void offline(Long userId, Long id) {
        Supply supply = supplyMapper.selectById(id);
        if (supply == null) {
            throw new BusinessException(ResponseCode.DATA_NOT_FOUND);
        }
        if (!supply.getUserId().equals(userId)) {
            throw new BusinessException(ResponseCode.FORBIDDEN, "无权操作此供应信息");
        }

        supply.setStatus(3); // 已下架
        supply.setUpdateTime(LocalDateTime.now());
        supplyMapper.updateById(supply);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(Long userId, Long id) {
        Supply supply = supplyMapper.selectById(id);
        if (supply == null) {
            throw new BusinessException(ResponseCode.DATA_NOT_FOUND);
        }
        if (!supply.getUserId().equals(userId)) {
            throw new BusinessException(ResponseCode.FORBIDDEN, "无权操作此供应信息");
        }

        supplyMapper.deleteById(id);
    }

    private SupplyVO convertToVO(Supply supply) {
        SupplyVO vo = new SupplyVO();
        BeanUtils.copyProperties(supply, vo);
        vo.setStatusName(getStatusName(supply.getStatus()));

        // 获取用户名
        User user = userMapper.selectById(supply.getUserId());
        if (user != null) {
            vo.setUsername(user.getRealName() != null ? user.getRealName() : user.getUsername());
        }

        return vo;
    }

    private void fillFavoriteFlags(Long userId, IPage<SupplyVO> page) {
        if (userId == null || page == null || page.getRecords() == null || page.getRecords().isEmpty()) {
            return;
        }
        List<Long> ids = page.getRecords().stream()
                .map(SupplyVO::getId)
                .filter(id -> id != null)
                .collect(java.util.stream.Collectors.toList());
        if (ids.isEmpty()) {
            return;
        }
        LambdaQueryWrapper<Favorite> wrapper = new LambdaQueryWrapper<>();
        wrapper.select(Favorite::getTargetId);
        wrapper.eq(Favorite::getUserId, userId);
        wrapper.eq(Favorite::getTargetType, 1);
        wrapper.in(Favorite::getTargetId, ids);
        List<Favorite> favorites = favoriteMapper.selectList(wrapper);
        java.util.Set<Long> favoriteIds = favorites.stream()
                .map(Favorite::getTargetId)
                .filter(id -> id != null)
                .collect(java.util.stream.Collectors.toSet());
        for (SupplyVO vo : page.getRecords()) {
            vo.setIsFavorite(favoriteIds.contains(vo.getId()) ? 1 : 0);
        }
    }

    private String getStatusName(Integer status) {
        if (status == null) return "";
        switch (status) {
            case 1: return "供应中";
            case 2: return "已售罄";
            case 3: return "已下架";
            default: return "";
        }
    }
}
