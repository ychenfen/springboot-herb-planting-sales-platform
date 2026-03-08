package com.herb.platform.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.herb.platform.dto.CropDTO;
import com.herb.platform.vo.CropVO;

/**
 * 作物服务接口
 */
public interface CropService {

    /**
     * 分页查询作物
     */
    IPage<CropVO> page(Long userId, Long fieldId, String cropName, Integer status, int pageNum, int pageSize);

    /**
     * 根据ID查询作物
     */
    CropVO getById(Long id);

    /**
     * 新增作物
     */
    void add(Long userId, CropDTO dto);

    /**
     * 更新作物
     */
    void update(Long userId, CropDTO dto);

    /**
     * 删除作物
     */
    void delete(Long userId, Long id);

    /**
     * 更新作物状态
     */
    void updateStatus(Long userId, Long id, Integer status);
}
