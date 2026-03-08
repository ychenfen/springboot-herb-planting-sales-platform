package com.herb.platform.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.herb.platform.dto.FavoriteDTO;
import com.herb.platform.vo.FavoriteVO;

/**
 * 收藏服务接口
 */
public interface FavoriteService {

    /**
     * 分页查询收藏
     */
    IPage<FavoriteVO> pageByUser(Long userId, Integer targetType, int pageNum, int pageSize);

    /**
     * 新增收藏
     */
    void add(Long userId, FavoriteDTO dto);

    /**
     * 取消收藏
     */
    void remove(Long userId, Integer targetType, Long targetId);
}
