package com.herb.platform.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.herb.platform.common.ResponseCode;
import com.herb.platform.dto.NoticeDTO;
import com.herb.platform.entity.Notice;
import com.herb.platform.entity.User;
import com.herb.platform.exception.BusinessException;
import com.herb.platform.mapper.NoticeMapper;
import com.herb.platform.mapper.UserMapper;
import com.herb.platform.service.NoticeService;
import com.herb.platform.vo.NoticeVO;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 系统通知服务实现类
 */
@Service
@RequiredArgsConstructor
public class NoticeServiceImpl implements NoticeService {

    private final NoticeMapper noticeMapper;
    private final UserMapper userMapper;

    @Override
    public IPage<NoticeVO> page(Long userId, Integer noticeType, Integer isRead, String title, int pageNum, int pageSize) {
        Page<Notice> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<Notice> wrapper = buildWrapper(userId, noticeType, isRead, title);
        wrapper.orderByDesc(Notice::getCreateTime);
        IPage<Notice> noticePage = noticeMapper.selectPage(page, wrapper);
        return convertPage(noticePage);
    }

    @Override
    public IPage<NoticeVO> pageByUser(Long userId, Integer noticeType, Integer isRead, String title, int pageNum, int pageSize) {
        if (userId == null) {
            throw new BusinessException(ResponseCode.PARAM_ERROR, "用户ID不能为空");
        }
        return page(userId, noticeType, isRead, title, pageNum, pageSize);
    }

    @Override
    public NoticeVO getById(Long id, Long userId) {
        Notice notice = noticeMapper.selectById(id);
        if (notice == null) {
            throw new BusinessException(ResponseCode.DATA_NOT_FOUND);
        }
        if (userId != null && !userId.equals(notice.getUserId())) {
            throw new BusinessException(ResponseCode.ACCESS_DENIED, "无权查看该通知");
        }
        return convertToVO(notice, null);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void create(NoticeDTO dto) {
        User user = userMapper.selectById(dto.getUserId());
        if (user == null || user.getDeleted() != null && user.getDeleted() == 1) {
            throw new BusinessException(ResponseCode.DATA_NOT_FOUND, "用户不存在");
        }
        Notice notice = new Notice();
        BeanUtils.copyProperties(dto, notice);
        notice.setIsRead(0);
        notice.setReadTime(null);
        noticeMapper.insert(notice);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(NoticeDTO dto) {
        if (dto.getId() == null) {
            throw new BusinessException(ResponseCode.PARAM_ERROR, "通知ID不能为空");
        }
        Notice notice = noticeMapper.selectById(dto.getId());
        if (notice == null) {
            throw new BusinessException(ResponseCode.DATA_NOT_FOUND);
        }
        User user = userMapper.selectById(dto.getUserId());
        if (user == null || user.getDeleted() != null && user.getDeleted() == 1) {
            throw new BusinessException(ResponseCode.DATA_NOT_FOUND, "用户不存在");
        }
        BeanUtils.copyProperties(dto, notice, "isRead", "readTime", "createTime");
        noticeMapper.updateById(notice);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(Long id) {
        Notice notice = noticeMapper.selectById(id);
        if (notice == null) {
            throw new BusinessException(ResponseCode.DATA_NOT_FOUND);
        }
        noticeMapper.deleteById(id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void markRead(Long id, Long userId) {
        Notice notice = noticeMapper.selectById(id);
        if (notice == null) {
            throw new BusinessException(ResponseCode.DATA_NOT_FOUND);
        }
        if (userId != null && !userId.equals(notice.getUserId())) {
            throw new BusinessException(ResponseCode.ACCESS_DENIED, "无权操作该通知");
        }
        if (notice.getIsRead() != null && notice.getIsRead() == 1) {
            return;
        }
        notice.setIsRead(1);
        notice.setReadTime(LocalDateTime.now());
        noticeMapper.updateById(notice);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void markAllRead(Long userId) {
        if (userId == null) {
            throw new BusinessException(ResponseCode.PARAM_ERROR, "用户ID不能为空");
        }
        Notice update = new Notice();
        update.setIsRead(1);
        update.setReadTime(LocalDateTime.now());

        LambdaQueryWrapper<Notice> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Notice::getUserId, userId);
        wrapper.eq(Notice::getIsRead, 0);
        noticeMapper.update(update, wrapper);
    }

    @Override
    public long countUnread(Long userId) {
        if (userId == null) {
            return 0;
        }
        LambdaQueryWrapper<Notice> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Notice::getUserId, userId);
        wrapper.eq(Notice::getIsRead, 0);
        return noticeMapper.selectCount(wrapper);
    }

    private LambdaQueryWrapper<Notice> buildWrapper(Long userId, Integer noticeType, Integer isRead, String title) {
        LambdaQueryWrapper<Notice> wrapper = new LambdaQueryWrapper<>();
        if (userId != null) {
            wrapper.eq(Notice::getUserId, userId);
        }
        if (noticeType != null) {
            wrapper.eq(Notice::getNoticeType, noticeType);
        }
        if (isRead != null) {
            wrapper.eq(Notice::getIsRead, isRead);
        }
        if (StringUtils.hasText(title)) {
            wrapper.like(Notice::getTitle, title);
        }
        return wrapper;
    }

    private IPage<NoticeVO> convertPage(IPage<Notice> noticePage) {
        List<Notice> records = noticePage.getRecords();
        Map<Long, User> userMap = new HashMap<>();
        if (records != null && !records.isEmpty()) {
            List<Long> userIds = records.stream()
                    .map(Notice::getUserId)
                    .filter(Objects::nonNull)
                    .distinct()
                    .collect(Collectors.toList());
            if (!userIds.isEmpty()) {
                List<User> users = userMapper.selectBatchIds(userIds);
                userMap = users.stream().collect(Collectors.toMap(User::getId, u -> u, (a, b) -> a));
            }
        }

        Map<Long, User> finalUserMap = userMap;
        return noticePage.convert(notice -> convertToVO(notice, finalUserMap));
    }

    private NoticeVO convertToVO(Notice notice, Map<Long, User> userMap) {
        NoticeVO vo = new NoticeVO();
        BeanUtils.copyProperties(notice, vo);
        vo.setNoticeTypeName(resolveTypeName(notice.getNoticeType()));
        vo.setReadStatusName(notice.getIsRead() != null && notice.getIsRead() == 1 ? "已读" : "未读");
        if (userMap != null && notice.getUserId() != null) {
            User user = userMap.get(notice.getUserId());
            if (user != null) {
                vo.setUsername(user.getUsername());
                vo.setRealName(user.getRealName());
            }
        }
        return vo;
    }

    private String resolveTypeName(Integer type) {
        if (type == null) {
            return "";
        }
        switch (type) {
            case 1:
                return "系统通知";
            case 2:
                return "订单通知";
            case 3:
                return "评论通知";
            case 4:
                return "其他通知";
            default:
                return "其他";
        }
    }
}
