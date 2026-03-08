package com.herb.platform.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.herb.platform.vo.LogVO;

import java.time.LocalDateTime;

/**
 * 系统日志服务接口
 */
public interface LogService {

    /**
     * 分页查询
     */
    IPage<LogVO> page(String username, String operation, Integer status,
                      LocalDateTime startTime, LocalDateTime endTime,
                      int pageNum, int pageSize);

    /**
     * 根据ID获取日志
     */
    LogVO getById(Long id);

    /**
     * 删除日志
     */
    void delete(Long id);

    /**
     * 清空日志
     */
    void clear();
}
