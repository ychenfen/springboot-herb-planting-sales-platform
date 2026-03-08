package com.herb.platform.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.herb.platform.dto.NoticeDTO;
import com.herb.platform.vo.NoticeVO;

/**
 * 系统通知服务接口
 */
public interface NoticeService {

    /**
     * 分页查询（管理员）
     */
    IPage<NoticeVO> page(Long userId, Integer noticeType, Integer isRead, String title, int pageNum, int pageSize);

    /**
     * 分页查询（当前用户）
     */
    IPage<NoticeVO> pageByUser(Long userId, Integer noticeType, Integer isRead, String title, int pageNum, int pageSize);

    /**
     * 获取通知详情
     */
    NoticeVO getById(Long id, Long userId);

    /**
     * 创建通知
     */
    void create(NoticeDTO dto);

    /**
     * 更新通知
     */
    void update(NoticeDTO dto);

    /**
     * 删除通知
     */
    void delete(Long id);

    /**
     * 标记已读
     */
    void markRead(Long id, Long userId);

    /**
     * 全部标记已读
     */
    void markAllRead(Long userId);

    /**
     * 未读数量
     */
    long countUnread(Long userId);
}
