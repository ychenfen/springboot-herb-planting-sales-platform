package com.herb.platform.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.herb.platform.common.ResponseCode;
import com.herb.platform.dto.FarmRecordDTO;
import com.herb.platform.entity.Crop;
import com.herb.platform.entity.FarmRecord;
import com.herb.platform.exception.BusinessException;
import com.herb.platform.mapper.CropMapper;
import com.herb.platform.mapper.FarmRecordMapper;
import com.herb.platform.service.FarmRecordService;
import com.herb.platform.vo.FarmRecordVO;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 农事记录服务实现类
 */
@Service
@RequiredArgsConstructor
public class FarmRecordServiceImpl implements FarmRecordService {

    private final FarmRecordMapper farmRecordMapper;
    private final CropMapper cropMapper;

    @Override
    public IPage<FarmRecordVO> page(Long userId, Long cropId, String activityType, int pageNum, int pageSize) {
        Page<FarmRecord> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<FarmRecord> wrapper = new LambdaQueryWrapper<>();

        if (userId != null) {
            wrapper.eq(FarmRecord::getUserId, userId);
        }
        if (cropId != null) {
            wrapper.eq(FarmRecord::getCropId, cropId);
        }
        if (StringUtils.hasText(activityType)) {
            wrapper.eq(FarmRecord::getActivityType, activityType);
        }
        wrapper.orderByDesc(FarmRecord::getActivityDate);
        wrapper.orderByDesc(FarmRecord::getActivityTime);

        IPage<FarmRecord> recordPage = farmRecordMapper.selectPage(page, wrapper);

        return recordPage.convert(this::convertToVO);
    }

    @Override
    public FarmRecordVO getById(Long id) {
        FarmRecord record = farmRecordMapper.selectById(id);
        if (record == null) {
            throw new BusinessException(ResponseCode.DATA_NOT_FOUND);
        }
        return convertToVO(record);
    }

    @Override
    public List<FarmRecordVO> listByCropId(Long cropId) {
        LambdaQueryWrapper<FarmRecord> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(FarmRecord::getCropId, cropId);
        wrapper.orderByDesc(FarmRecord::getActivityDate);
        wrapper.orderByDesc(FarmRecord::getActivityTime);

        List<FarmRecord> records = farmRecordMapper.selectList(wrapper);
        return records.stream().map(this::convertToVO).collect(Collectors.toList());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void add(Long userId, FarmRecordDTO dto) {
        // 验证作物是否存在且属于当前用户
        Crop crop = cropMapper.selectById(dto.getCropId());
        if (crop == null) {
            throw new BusinessException("作物不存在");
        }
        if (!crop.getUserId().equals(userId)) {
            throw new BusinessException(ResponseCode.FORBIDDEN, "无权为此作物添加记录");
        }

        FarmRecord record = new FarmRecord();
        BeanUtils.copyProperties(dto, record);
        record.setUserId(userId);
        record.setCreateTime(LocalDateTime.now());
        record.setUpdateTime(LocalDateTime.now());
        farmRecordMapper.insert(record);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(Long userId, FarmRecordDTO dto) {
        if (dto.getId() == null) {
            throw new BusinessException(ResponseCode.PARAM_ERROR, "记录ID不能为空");
        }

        FarmRecord record = farmRecordMapper.selectById(dto.getId());
        if (record == null) {
            throw new BusinessException(ResponseCode.DATA_NOT_FOUND);
        }
        if (!record.getUserId().equals(userId)) {
            throw new BusinessException(ResponseCode.FORBIDDEN, "无权操作此记录");
        }

        BeanUtils.copyProperties(dto, record, "id", "userId", "cropId", "createTime");
        record.setUpdateTime(LocalDateTime.now());
        farmRecordMapper.updateById(record);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(Long userId, Long id) {
        FarmRecord record = farmRecordMapper.selectById(id);
        if (record == null) {
            throw new BusinessException(ResponseCode.DATA_NOT_FOUND);
        }
        if (!record.getUserId().equals(userId)) {
            throw new BusinessException(ResponseCode.FORBIDDEN, "无权操作此记录");
        }

        farmRecordMapper.deleteById(id);
    }

    private FarmRecordVO convertToVO(FarmRecord record) {
        FarmRecordVO vo = new FarmRecordVO();
        BeanUtils.copyProperties(record, vo);
        vo.setActivityTypeName(getActivityTypeName(record.getActivityType()));

        // 获取作物名称
        Crop crop = cropMapper.selectById(record.getCropId());
        if (crop != null) {
            vo.setCropName(crop.getCropName());
        }

        return vo;
    }

    private String getActivityTypeName(String activityType) {
        if (activityType == null) return "";
        switch (activityType) {
            case "sow": return "播种";
            case "fertilize": return "施肥";
            case "water": return "浇水";
            case "weed": return "除草";
            case "spray": return "施药";
            case "harvest": return "收获";
            case "prune": return "修剪";
            case "inspect": return "巡检";
            default: return activityType;
        }
    }
}
