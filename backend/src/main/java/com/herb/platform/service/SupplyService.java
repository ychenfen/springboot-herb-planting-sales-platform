package com.herb.platform.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.herb.platform.dto.SupplyDTO;
import com.herb.platform.vo.SupplyPricingVO;
import com.herb.platform.vo.SupplyVO;

import java.math.BigDecimal;

/**
 * Supply service.
 */
public interface SupplyService {

    IPage<SupplyVO> pageMarket(Long userId, String herbName, String qualityGrade, Integer status, int pageNum, int pageSize);

    IPage<SupplyVO> pageMySupply(Long userId, String herbName, Integer status, int pageNum, int pageSize);

    SupplyVO getById(Long id);

    SupplyPricingVO calculatePricing(Long id, BigDecimal quantityKg);

    void add(Long userId, SupplyDTO dto);

    void update(Long userId, SupplyDTO dto);

    void offline(Long userId, Long id);

    void delete(Long userId, Long id);
}
