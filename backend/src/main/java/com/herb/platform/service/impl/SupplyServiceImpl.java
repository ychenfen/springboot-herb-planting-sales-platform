package com.herb.platform.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.herb.platform.common.Constants;
import com.herb.platform.common.ResponseCode;
import com.herb.platform.dto.SupplyDTO;
import com.herb.platform.entity.Crop;
import com.herb.platform.entity.Favorite;
import com.herb.platform.entity.Supply;
import com.herb.platform.entity.Trace;
import com.herb.platform.entity.User;
import com.herb.platform.exception.BusinessException;
import com.herb.platform.mapper.CropMapper;
import com.herb.platform.mapper.FavoriteMapper;
import com.herb.platform.mapper.SupplyMapper;
import com.herb.platform.mapper.TraceMapper;
import com.herb.platform.mapper.UserMapper;
import com.herb.platform.service.SupplyService;
import com.herb.platform.vo.SupplyPricingVO;
import com.herb.platform.vo.SupplyVO;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Supply service implementation.
 */
@Service
@RequiredArgsConstructor
public class SupplyServiceImpl implements SupplyService {

    private final SupplyMapper supplyMapper;
    private final UserMapper userMapper;
    private final FavoriteMapper favoriteMapper;
    private final TraceMapper traceMapper;
    private final CropMapper cropMapper;

    @Override
    public IPage<SupplyVO> pageMarket(Long userId, String herbName, String qualityGrade, Integer status, int pageNum, int pageSize) {
        Page<Supply> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<Supply> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Supply::getStatus, status != null ? status : Constants.SUPPLY_STATUS_ACTIVE);
        if (StringUtils.hasText(herbName)) {
            wrapper.like(Supply::getHerbName, herbName);
        }
        if (StringUtils.hasText(qualityGrade)) {
            wrapper.eq(Supply::getQualityGrade, qualityGrade);
        }
        wrapper.orderByDesc(Supply::getIsTop);
        wrapper.orderByDesc(Supply::getCreateTime);

        IPage<SupplyVO> result = supplyMapper.selectPage(page, wrapper).convert(this::convertToVO);
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

        IPage<SupplyVO> result = supplyMapper.selectPage(page, wrapper).convert(this::convertToVO);
        fillFavoriteFlags(userId, result);
        return result;
    }

    @Override
    public SupplyVO getById(Long id) {
        Supply supply = supplyMapper.selectById(id);
        if (supply == null) {
            throw new BusinessException(ResponseCode.DATA_NOT_FOUND);
        }
        supplyMapper.incrementViewCount(id);
        return convertToVO(supply);
    }

    @Override
    public SupplyPricingVO calculatePricing(Long id, BigDecimal quantityKg) {
        Supply supply = supplyMapper.selectById(id);
        if (supply == null) {
            throw new BusinessException(ResponseCode.DATA_NOT_FOUND);
        }
        if (supply.getPrice() == null) {
            throw new BusinessException(ResponseCode.PARAM_ERROR, "标准价格未配置");
        }
        if (quantityKg == null || quantityKg.compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessException(ResponseCode.PARAM_ERROR, "数量必须大于0");
        }
        return buildPricing(supply, quantityKg);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void add(Long userId, SupplyDTO dto) {
        validateWholesaleFields(dto);

        Supply supply = new Supply();
        BeanUtils.copyProperties(dto, supply);
        supply.setUserId(userId);
        supply.setRemainingQuantity(dto.getSupplyQuantity());
        supply.setStatus(Constants.SUPPLY_STATUS_ACTIVE);
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
        validateWholesaleFields(dto);

        Supply supply = supplyMapper.selectById(dto.getId());
        if (supply == null) {
            throw new BusinessException(ResponseCode.DATA_NOT_FOUND);
        }
        if (!supply.getUserId().equals(userId)) {
            throw new BusinessException(ResponseCode.FORBIDDEN, "无权操作此供应信息");
        }

        BigDecimal soldQuantity = safeValue(supply.getSupplyQuantity()).subtract(safeValue(supply.getRemainingQuantity()));
        if (dto.getSupplyQuantity().compareTo(soldQuantity) < 0) {
            throw new BusinessException(ResponseCode.PARAM_ERROR, "新供应量不能小于已售数量");
        }
        BigDecimal newRemaining = dto.getSupplyQuantity().subtract(soldQuantity);

        BeanUtils.copyProperties(dto, supply, "id", "userId", "status", "viewCount", "favoriteCount", "createTime", "remainingQuantity");
        supply.setRemainingQuantity(newRemaining);
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
        supply.setStatus(Constants.SUPPLY_STATUS_OFF_SHELF);
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
        vo.setPricePerGram(calculatePerGram(supply.getPrice()));
        vo.setPricePerJin(calculatePerJin(supply.getPrice()));

        User user = userMapper.selectById(supply.getUserId());
        if (user != null) {
            vo.setUsername(user.getRealName() != null ? user.getRealName() : user.getUsername());
        }

        Crop crop = supply.getCropId() == null ? null : cropMapper.selectById(supply.getCropId());
        if (crop != null) {
            vo.setPlantDate(crop.getPlantDate());
        }

        Trace trace = findTraceBySupply(supply);
        if (trace != null) {
            vo.setTraceId(trace.getId());
            vo.setTraceCode(trace.getTraceCode());
            vo.setBatchNo(trace.getBatchNo());
            vo.setPlantDate(trace.getPlantDate() != null ? trace.getPlantDate() : vo.getPlantDate());
            vo.setQualityStandard(trace.getQualityStandard());
            vo.setQualityReport(trace.getQualityReport());
            vo.setQualityCheckStatus(StringUtils.hasText(trace.getQualityReport()) || StringUtils.hasText(trace.getQualityStandard())
                    ? "已质检" : "待补充");
        } else {
            vo.setQualityCheckStatus("无溯源数据");
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
                .collect(Collectors.toList());
        if (ids.isEmpty()) {
            return;
        }

        LambdaQueryWrapper<Favorite> wrapper = new LambdaQueryWrapper<>();
        wrapper.select(Favorite::getTargetId);
        wrapper.eq(Favorite::getUserId, userId);
        wrapper.eq(Favorite::getTargetType, 1);
        wrapper.in(Favorite::getTargetId, ids);
        Set<Long> favoriteIds = favoriteMapper.selectList(wrapper).stream()
                .map(Favorite::getTargetId)
                .filter(id -> id != null)
                .collect(Collectors.toSet());
        for (SupplyVO vo : page.getRecords()) {
            vo.setIsFavorite(favoriteIds.contains(vo.getId()) ? 1 : 0);
        }
    }

    private Trace findTraceBySupply(Supply supply) {
        if (supply.getCropId() == null) {
            return null;
        }
        LambdaQueryWrapper<Trace> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Trace::getCropId, supply.getCropId());
        wrapper.eq(Trace::getDeleted, 0);
        wrapper.orderByDesc(Trace::getCreateTime);
        wrapper.last("LIMIT 1");
        return traceMapper.selectOne(wrapper);
    }

    private SupplyPricingVO buildPricing(Supply supply, BigDecimal quantityKg) {
        SupplyPricingVO pricing = new SupplyPricingVO();
        pricing.setSupplyId(supply.getId());
        pricing.setQuantityKg(quantityKg.setScale(3, RoundingMode.HALF_UP));
        pricing.setBasePrice(supply.getPrice());
        pricing.setWholesaleMinQuantity(supply.getWholesaleMinQuantity());

        boolean wholesaleApplied = supply.getWholesalePrice() != null
                && supply.getWholesaleMinQuantity() != null
                && quantityKg.compareTo(supply.getWholesaleMinQuantity()) >= 0;
        pricing.setWholesaleApplied(wholesaleApplied);
        pricing.setAppliedUnitPrice(wholesaleApplied ? supply.getWholesalePrice() : supply.getPrice());
        pricing.setTotalAmount(pricing.getAppliedUnitPrice().multiply(quantityKg).setScale(2, RoundingMode.HALF_UP));
        return pricing;
    }

    private void validateWholesaleFields(SupplyDTO dto) {
        boolean hasWholesalePrice = dto.getWholesalePrice() != null;
        boolean hasWholesaleMinQuantity = dto.getWholesaleMinQuantity() != null;
        if (hasWholesalePrice != hasWholesaleMinQuantity) {
            throw new BusinessException(ResponseCode.PARAM_ERROR, "请同时填写批发价和起批量");
        }
    }

    private BigDecimal calculatePerGram(BigDecimal pricePerKg) {
        if (pricePerKg == null) {
            return null;
        }
        return pricePerKg.divide(BigDecimal.valueOf(1000), 4, RoundingMode.HALF_UP);
    }

    private BigDecimal calculatePerJin(BigDecimal pricePerKg) {
        if (pricePerKg == null) {
            return null;
        }
        return pricePerKg.divide(BigDecimal.valueOf(2), 2, RoundingMode.HALF_UP);
    }

    private BigDecimal safeValue(BigDecimal value) {
        return value == null ? BigDecimal.ZERO : value;
    }

    private String getStatusName(Integer status) {
        if (status == null) {
            return "";
        }
        switch (status) {
            case 1:
                return "供应中";
            case 2:
                return "已售罄";
            case 3:
                return "已下架";
            default:
                return "";
        }
    }
}
