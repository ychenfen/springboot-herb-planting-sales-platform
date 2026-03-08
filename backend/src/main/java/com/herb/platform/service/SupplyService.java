package com.herb.platform.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.herb.platform.dto.SupplyDTO;
import com.herb.platform.vo.SupplyVO;

/**
 * 供应信息服务接口
 */
public interface SupplyService {

    /**
     * 分页查询供应信息（市场）
     */
    IPage<SupplyVO> pageMarket(Long userId, String herbName, String qualityGrade, Integer status, int pageNum, int pageSize);

    /**
     * 分页查询我的供应
     */
    IPage<SupplyVO> pageMySupply(Long userId, String herbName, Integer status, int pageNum, int pageSize);

    /**
     * 根据ID查询供应详情
     */
    SupplyVO getById(Long id);

    /**
     * 发布供应信息
     */
    void add(Long userId, SupplyDTO dto);

    /**
     * 更新供应信息
     */
    void update(Long userId, SupplyDTO dto);

    /**
     * 下架供应信息
     */
    void offline(Long userId, Long id);

    /**
     * 删除供应信息
     */
    void delete(Long userId, Long id);
}
