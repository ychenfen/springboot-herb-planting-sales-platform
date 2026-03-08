package com.herb.platform.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.herb.platform.dto.DemandDTO;
import com.herb.platform.vo.DemandVO;

/**
 * 需求信息服务接口
 */
public interface DemandService {

    /**
     * 分页查询需求信息（市场）
     */
    IPage<DemandVO> pageMarket(Long userId, String herbName, Integer status, int pageNum, int pageSize);

    /**
     * 分页查询我的需求
     */
    IPage<DemandVO> pageMyDemand(Long userId, String herbName, Integer status, int pageNum, int pageSize);

    /**
     * 根据ID查询需求详情
     */
    DemandVO getById(Long id);

    /**
     * 发布需求信息
     */
    void add(Long userId, DemandDTO dto);

    /**
     * 更新需求信息
     */
    void update(Long userId, DemandDTO dto);

    /**
     * 取消需求
     */
    void cancel(Long userId, Long id);

    /**
     * 删除需求信息
     */
    void delete(Long userId, Long id);
}
