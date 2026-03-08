package com.herb.platform.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.herb.platform.dto.FieldDTO;
import com.herb.platform.vo.FieldVO;

import java.util.List;

/**
 * 地块服务接口
 */
public interface FieldService {

    /**
     * 获取用户地块列表
     */
    List<FieldVO> list(Long userId);

    /**
     * 分页查询地块
     */
    IPage<FieldVO> page(Long userId, String fieldName, Integer status, int pageNum, int pageSize);

    /**
     * 根据ID查询地块
     */
    FieldVO getById(Long id);

    /**
     * 新增地块
     */
    void add(Long userId, FieldDTO dto);

    /**
     * 更新地块
     */
    void update(Long userId, FieldDTO dto);

    /**
     * 删除地块
     */
    void delete(Long userId, Long id);
}
