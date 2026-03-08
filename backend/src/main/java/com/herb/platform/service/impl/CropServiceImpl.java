package com.herb.platform.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.herb.platform.common.ResponseCode;
import com.herb.platform.dto.CropDTO;
import com.herb.platform.entity.Crop;
import com.herb.platform.entity.Field;
import com.herb.platform.exception.BusinessException;
import com.herb.platform.mapper.CropMapper;
import com.herb.platform.mapper.FieldMapper;
import com.herb.platform.mapper.FarmRecordMapper;
import com.herb.platform.service.CropService;
import com.herb.platform.vo.CropVO;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;

/**
 * 作物服务实现类
 */
@Service
@RequiredArgsConstructor
public class CropServiceImpl implements CropService {

    private final CropMapper cropMapper;
    private final FieldMapper fieldMapper;
    private final FarmRecordMapper farmRecordMapper;

    @Override
    public IPage<CropVO> page(Long userId, Long fieldId, String cropName, Integer status, int pageNum, int pageSize) {
        Page<Crop> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<Crop> wrapper = new LambdaQueryWrapper<>();

        if (userId != null) {
            wrapper.eq(Crop::getUserId, userId);
        }
        if (fieldId != null) {
            wrapper.eq(Crop::getFieldId, fieldId);
        }
        if (StringUtils.hasText(cropName)) {
            wrapper.like(Crop::getCropName, cropName);
        }
        if (status != null) {
            wrapper.eq(Crop::getStatus, status);
        }
        wrapper.orderByDesc(Crop::getCreateTime);

        IPage<Crop> cropPage = cropMapper.selectPage(page, wrapper);

        return cropPage.convert(this::convertToVO);
    }

    @Override
    public CropVO getById(Long id) {
        Crop crop = cropMapper.selectById(id);
        if (crop == null) {
            throw new BusinessException(ResponseCode.DATA_NOT_FOUND);
        }
        return convertToVO(crop);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void add(Long userId, CropDTO dto) {
        // 验证地块是否存在且属于当前用户
        Field field = fieldMapper.selectById(dto.getFieldId());
        if (field == null) {
            throw new BusinessException("地块不存在");
        }
        if (!field.getUserId().equals(userId)) {
            throw new BusinessException(ResponseCode.FORBIDDEN, "无权在此地块种植");
        }

        Crop crop = new Crop();
        BeanUtils.copyProperties(dto, crop);
        crop.setUserId(userId);
        crop.setStatus(dto.getStatus() != null ? dto.getStatus() : 1);
        crop.setCreateTime(LocalDateTime.now());
        crop.setUpdateTime(LocalDateTime.now());
        cropMapper.insert(crop);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(Long userId, CropDTO dto) {
        if (dto.getId() == null) {
            throw new BusinessException(ResponseCode.PARAM_ERROR, "作物ID不能为空");
        }

        Crop crop = cropMapper.selectById(dto.getId());
        if (crop == null) {
            throw new BusinessException(ResponseCode.DATA_NOT_FOUND);
        }
        if (!crop.getUserId().equals(userId)) {
            throw new BusinessException(ResponseCode.FORBIDDEN, "无权操作此作物");
        }

        BeanUtils.copyProperties(dto, crop, "id", "userId", "createTime");
        crop.setUpdateTime(LocalDateTime.now());
        cropMapper.updateById(crop);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(Long userId, Long id) {
        Crop crop = cropMapper.selectById(id);
        if (crop == null) {
            throw new BusinessException(ResponseCode.DATA_NOT_FOUND);
        }
        if (!crop.getUserId().equals(userId)) {
            throw new BusinessException(ResponseCode.FORBIDDEN, "无权操作此作物");
        }

        // 检查是否有关联的农事记录
        if (farmRecordMapper.countByCropId(id) > 0) {
            throw new BusinessException("该作物下存在农事记录，无法删除");
        }

        cropMapper.deleteById(id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateStatus(Long userId, Long id, Integer status) {
        Crop crop = cropMapper.selectById(id);
        if (crop == null) {
            throw new BusinessException(ResponseCode.DATA_NOT_FOUND);
        }
        if (!crop.getUserId().equals(userId)) {
            throw new BusinessException(ResponseCode.FORBIDDEN, "无权操作此作物");
        }

        crop.setStatus(status);
        crop.setUpdateTime(LocalDateTime.now());
        cropMapper.updateById(crop);
    }

    private CropVO convertToVO(Crop crop) {
        CropVO vo = new CropVO();
        BeanUtils.copyProperties(crop, vo);
        vo.setStatusName(getStatusName(crop.getStatus()));

        // 获取地块名称
        Field field = fieldMapper.selectById(crop.getFieldId());
        if (field != null) {
            vo.setFieldName(field.getFieldName());
        }

        return vo;
    }

    private String getStatusName(Integer status) {
        if (status == null) return "";
        switch (status) {
            case 1: return "生长中";
            case 2: return "已收获";
            case 3: return "已销售";
            case 4: return "已废弃";
            default: return "";
        }
    }
}
