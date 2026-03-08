package com.herb.platform.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.herb.platform.dto.FarmRecordDTO;
import com.herb.platform.vo.FarmRecordVO;

import java.util.List;

/**
 * 农事记录服务接口
 */
public interface FarmRecordService {

    /**
     * 分页查询农事记录
     */
    IPage<FarmRecordVO> page(Long userId, Long cropId, String activityType, int pageNum, int pageSize);

    /**
     * 根据ID查询农事记录
     */
    FarmRecordVO getById(Long id);

    /**
     * 根据作物ID查询农事记录列表
     */
    List<FarmRecordVO> listByCropId(Long cropId);

    /**
     * 新增农事记录
     */
    void add(Long userId, FarmRecordDTO dto);

    /**
     * 更新农事记录
     */
    void update(Long userId, FarmRecordDTO dto);

    /**
     * 删除农事记录
     */
    void delete(Long userId, Long id);
}
