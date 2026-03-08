package com.herb.platform.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.herb.platform.common.ResponseCode;
import com.herb.platform.entity.Log;
import com.herb.platform.exception.BusinessException;
import com.herb.platform.mapper.LogMapper;
import com.herb.platform.service.LogService;
import com.herb.platform.vo.LogVO;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;

/**
 * 系统日志服务实现类
 */
@Service
@RequiredArgsConstructor
public class LogServiceImpl implements LogService {

    private final LogMapper logMapper;

    @Override
    public IPage<LogVO> page(String username, String operation, Integer status,
                             LocalDateTime startTime, LocalDateTime endTime,
                             int pageNum, int pageSize) {
        Page<Log> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<Log> wrapper = new LambdaQueryWrapper<>();

        if (StringUtils.hasText(username)) {
            wrapper.like(Log::getUsername, username);
        }
        if (StringUtils.hasText(operation)) {
            wrapper.like(Log::getOperation, operation);
        }
        if (status != null) {
            wrapper.eq(Log::getStatus, status);
        }
        if (startTime != null && endTime != null) {
            wrapper.between(Log::getCreateTime, startTime, endTime);
        } else if (startTime != null) {
            wrapper.ge(Log::getCreateTime, startTime);
        } else if (endTime != null) {
            wrapper.le(Log::getCreateTime, endTime);
        }
        wrapper.orderByDesc(Log::getCreateTime);

        IPage<Log> logPage = logMapper.selectPage(page, wrapper);
        return logPage.convert(this::convertToVO);
    }

    @Override
    public LogVO getById(Long id) {
        Log logEntity = logMapper.selectById(id);
        if (logEntity == null) {
            throw new BusinessException(ResponseCode.DATA_NOT_FOUND);
        }
        return convertToVO(logEntity);
    }

    @Override
    public void delete(Long id) {
        Log logEntity = logMapper.selectById(id);
        if (logEntity == null) {
            throw new BusinessException(ResponseCode.DATA_NOT_FOUND);
        }
        logMapper.deleteById(id);
    }

    @Override
    public void clear() {
        logMapper.delete(new LambdaQueryWrapper<>());
    }

    private LogVO convertToVO(Log logEntity) {
        LogVO vo = new LogVO();
        BeanUtils.copyProperties(logEntity, vo);
        if (logEntity.getStatus() != null) {
            vo.setStatusName(logEntity.getStatus() == 1 ? "成功" : "失败");
        }
        return vo;
    }
}
