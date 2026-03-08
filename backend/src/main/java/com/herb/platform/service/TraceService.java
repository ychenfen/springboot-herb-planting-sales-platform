package com.herb.platform.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.herb.platform.dto.TraceDTO;
import com.herb.platform.dto.TraceNodeDTO;
import com.herb.platform.vo.TraceVO;

/**
 * 溯源服务接口
 */
public interface TraceService {

    /**
     * 分页查询溯源信息
     */
    IPage<TraceVO> page(Long userId, Long cropId, String herbName, Integer status, int pageNum, int pageSize);

    /**
     * 根据ID获取溯源信息
     */
    TraceVO getById(Long id);

    /**
     * 根据溯源码获取溯源信息（公开接口）
     */
    TraceVO getByTraceCode(String traceCode);

    /**
     * 创建溯源信息
     */
    Long create(Long userId, TraceDTO dto);

    /**
     * 更新溯源信息
     */
    void update(Long userId, TraceDTO dto);

    /**
     * 删除溯源信息
     */
    void delete(Long userId, Long id);

    /**
     * 发布溯源信息
     */
    void publish(Long userId, Long id);

    /**
     * 添加溯源节点
     */
    void addNode(Long userId, TraceNodeDTO dto);

    /**
     * 更新溯源节点
     */
    void updateNode(Long userId, TraceNodeDTO dto);

    /**
     * 删除溯源节点
     */
    void deleteNode(Long userId, Long nodeId);

    /**
     * 增加扫码次数
     */
    void incrementScanCount(Long traceId);

    /**
     * 生成二维码
     */
    String generateQRCode(Long traceId);
}
