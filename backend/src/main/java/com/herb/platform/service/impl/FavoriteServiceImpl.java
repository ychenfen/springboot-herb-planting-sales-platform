package com.herb.platform.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.herb.platform.common.ResponseCode;
import com.herb.platform.dto.FavoriteDTO;
import com.herb.platform.entity.Demand;
import com.herb.platform.entity.Favorite;
import com.herb.platform.entity.Supply;
import com.herb.platform.entity.User;
import com.herb.platform.exception.BusinessException;
import com.herb.platform.mapper.DemandMapper;
import com.herb.platform.mapper.FavoriteMapper;
import com.herb.platform.mapper.SupplyMapper;
import com.herb.platform.mapper.UserMapper;
import com.herb.platform.service.FavoriteService;
import com.herb.platform.vo.DemandVO;
import com.herb.platform.vo.FavoriteVO;
import com.herb.platform.vo.SupplyVO;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 收藏服务实现类
 */
@Service
@RequiredArgsConstructor
public class FavoriteServiceImpl implements FavoriteService {

    private static final int TYPE_SUPPLY = 1;
    private static final int TYPE_DEMAND = 2;

    private final FavoriteMapper favoriteMapper;
    private final SupplyMapper supplyMapper;
    private final DemandMapper demandMapper;
    private final UserMapper userMapper;

    @Override
    public IPage<FavoriteVO> pageByUser(Long userId, Integer targetType, int pageNum, int pageSize) {
        if (userId == null) {
            throw new BusinessException(ResponseCode.PARAM_ERROR, "用户ID不能为空");
        }

        Page<Favorite> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<Favorite> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Favorite::getUserId, userId);
        if (targetType != null) {
            wrapper.eq(Favorite::getTargetType, targetType);
        }
        wrapper.orderByDesc(Favorite::getCreateTime);
        IPage<Favorite> favoritePage = favoriteMapper.selectPage(page, wrapper);

        List<Favorite> records = favoritePage.getRecords();
        Map<Long, SupplyVO> supplyMap = buildSupplyMap(records);
        Map<Long, DemandVO> demandMap = buildDemandMap(records);

        return favoritePage.convert(favorite -> {
            FavoriteVO vo = new FavoriteVO();
            BeanUtils.copyProperties(favorite, vo);
            vo.setTargetTypeName(getTargetTypeName(favorite.getTargetType()));
            if (favorite.getTargetType() != null && favorite.getTargetType() == TYPE_SUPPLY) {
                vo.setSupply(supplyMap.get(favorite.getTargetId()));
            } else if (favorite.getTargetType() != null && favorite.getTargetType() == TYPE_DEMAND) {
                vo.setDemand(demandMap.get(favorite.getTargetId()));
            }
            return vo;
        });
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void add(Long userId, FavoriteDTO dto) {
        if (userId == null) {
            throw new BusinessException(ResponseCode.PARAM_ERROR, "用户ID不能为空");
        }
        validateTargetParam(dto.getTargetType(), dto.getTargetId());
        validateTargetExists(dto.getTargetType(), dto.getTargetId());

        LambdaQueryWrapper<Favorite> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Favorite::getUserId, userId);
        wrapper.eq(Favorite::getTargetType, dto.getTargetType());
        wrapper.eq(Favorite::getTargetId, dto.getTargetId());
        if (favoriteMapper.selectCount(wrapper) > 0) {
            throw new BusinessException(ResponseCode.DATA_EXISTS, "已收藏");
        }

        Favorite favorite = new Favorite();
        favorite.setUserId(userId);
        favorite.setTargetType(dto.getTargetType());
        favorite.setTargetId(dto.getTargetId());
        favoriteMapper.insert(favorite);

        if (dto.getTargetType() == TYPE_SUPPLY) {
            supplyMapper.incrementFavoriteCount(dto.getTargetId());
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void remove(Long userId, Integer targetType, Long targetId) {
        if (userId == null) {
            throw new BusinessException(ResponseCode.PARAM_ERROR, "用户ID不能为空");
        }
        validateTargetParam(targetType, targetId);

        LambdaQueryWrapper<Favorite> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Favorite::getUserId, userId);
        wrapper.eq(Favorite::getTargetType, targetType);
        wrapper.eq(Favorite::getTargetId, targetId);

        Favorite favorite = favoriteMapper.selectOne(wrapper);
        if (favorite == null) {
            throw new BusinessException(ResponseCode.DATA_NOT_FOUND, "收藏记录不存在");
        }
        favoriteMapper.deleteById(favorite.getId());

        if (targetType == TYPE_SUPPLY) {
            supplyMapper.decrementFavoriteCount(targetId);
        }
    }

    private void validateTargetParam(Integer targetType, Long targetId) {
        if (targetType == null || targetId == null) {
            throw new BusinessException(ResponseCode.PARAM_ERROR, "收藏参数不能为空");
        }
        if (targetType != TYPE_SUPPLY && targetType != TYPE_DEMAND) {
            throw new BusinessException(ResponseCode.PARAM_ERROR, "收藏类型不合法");
        }
    }

    private void validateTargetExists(Integer targetType, Long targetId) {
        if (targetType == TYPE_SUPPLY) {
            Supply supply = supplyMapper.selectById(targetId);
            if (supply == null) {
                throw new BusinessException(ResponseCode.DATA_NOT_FOUND, "供应信息不存在");
            }
        } else {
            Demand demand = demandMapper.selectById(targetId);
            if (demand == null) {
                throw new BusinessException(ResponseCode.DATA_NOT_FOUND, "需求信息不存在");
            }
        }
    }

    private Map<Long, SupplyVO> buildSupplyMap(List<Favorite> records) {
        List<Long> ids = records.stream()
                .filter(f -> f.getTargetType() != null && f.getTargetType() == TYPE_SUPPLY)
                .map(Favorite::getTargetId)
                .filter(Objects::nonNull)
                .distinct()
                .collect(Collectors.toList());
        if (ids.isEmpty()) {
            return Collections.emptyMap();
        }
        List<Supply> supplies = supplyMapper.selectBatchIds(ids);
        Map<Long, User> userMap = buildUserMap(supplies.stream()
                .map(Supply::getUserId)
                .filter(Objects::nonNull)
                .distinct()
                .collect(Collectors.toList()));
        Map<Long, SupplyVO> map = new HashMap<>();
        for (Supply supply : supplies) {
            SupplyVO vo = new SupplyVO();
            BeanUtils.copyProperties(supply, vo);
            vo.setStatusName(getSupplyStatusName(supply.getStatus()));
            User user = userMap.get(supply.getUserId());
            if (user != null) {
                vo.setUsername(user.getRealName() != null ? user.getRealName() : user.getUsername());
            }
            vo.setIsFavorite(1);
            map.put(supply.getId(), vo);
        }
        return map;
    }

    private Map<Long, DemandVO> buildDemandMap(List<Favorite> records) {
        List<Long> ids = records.stream()
                .filter(f -> f.getTargetType() != null && f.getTargetType() == TYPE_DEMAND)
                .map(Favorite::getTargetId)
                .filter(Objects::nonNull)
                .distinct()
                .collect(Collectors.toList());
        if (ids.isEmpty()) {
            return Collections.emptyMap();
        }
        List<Demand> demands = demandMapper.selectBatchIds(ids);
        Map<Long, User> userMap = buildUserMap(demands.stream()
                .map(Demand::getUserId)
                .filter(Objects::nonNull)
                .distinct()
                .collect(Collectors.toList()));
        Map<Long, DemandVO> map = new HashMap<>();
        for (Demand demand : demands) {
            DemandVO vo = new DemandVO();
            BeanUtils.copyProperties(demand, vo);
            vo.setStatusName(getDemandStatusName(demand.getStatus()));
            User user = userMap.get(demand.getUserId());
            if (user != null) {
                vo.setUsername(user.getRealName() != null ? user.getRealName() : user.getUsername());
            }
            vo.setIsFavorite(1);
            map.put(demand.getId(), vo);
        }
        return map;
    }

    private Map<Long, User> buildUserMap(List<Long> userIds) {
        if (userIds == null || userIds.isEmpty()) {
            return Collections.emptyMap();
        }
        List<User> users = userMapper.selectBatchIds(userIds);
        return users.stream().collect(Collectors.toMap(User::getId, u -> u, (a, b) -> a));
    }

    private String getTargetTypeName(Integer type) {
        if (type == null) {
            return "";
        }
        switch (type) {
            case TYPE_SUPPLY:
                return "供应信息";
            case TYPE_DEMAND:
                return "需求信息";
            default:
                return "";
        }
    }

    private String getSupplyStatusName(Integer status) {
        if (status == null) return "";
        switch (status) {
            case 1: return "供应中";
            case 2: return "已售罄";
            case 3: return "已下架";
            default: return "";
        }
    }

    private String getDemandStatusName(Integer status) {
        if (status == null) return "";
        switch (status) {
            case 1: return "采购中";
            case 2: return "已完成";
            case 3: return "已取消";
            default: return "";
        }
    }
}
