package com.herb.platform.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.herb.platform.common.ResponseCode;
import com.herb.platform.dto.FieldDTO;
import com.herb.platform.entity.Field;
import com.herb.platform.exception.BusinessException;
import com.herb.platform.mapper.CropMapper;
import com.herb.platform.mapper.FieldMapper;
import com.herb.platform.service.FieldService;
import com.herb.platform.vo.FieldVO;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 地块服务实现类
 */
@Service
@RequiredArgsConstructor
public class FieldServiceImpl implements FieldService {

    private final FieldMapper fieldMapper;
    private final CropMapper cropMapper;

    @Override
    public List<FieldVO> list(Long userId) {
        LambdaQueryWrapper<Field> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Field::getUserId, userId);
        wrapper.eq(Field::getStatus, 1); // 只返回在用的地块
        wrapper.orderByDesc(Field::getCreateTime);
        List<Field> fields = fieldMapper.selectList(wrapper);
        return fields.stream().map(this::convertToVO).collect(Collectors.toList());
    }

    @Override
    public IPage<FieldVO> page(Long userId, String fieldName, Integer status, int pageNum, int pageSize) {
        Page<Field> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<Field> wrapper = new LambdaQueryWrapper<>();

        if (userId != null) {
            wrapper.eq(Field::getUserId, userId);
        }
        if (StringUtils.hasText(fieldName)) {
            wrapper.like(Field::getFieldName, fieldName);
        }
        if (status != null) {
            wrapper.eq(Field::getStatus, status);
        }
        wrapper.orderByDesc(Field::getCreateTime);

        IPage<Field> fieldPage = fieldMapper.selectPage(page, wrapper);

        return fieldPage.convert(this::convertToVO);
    }

    @Override
    public FieldVO getById(Long id) {
        Field field = fieldMapper.selectById(id);
        if (field == null) {
            throw new BusinessException(ResponseCode.DATA_NOT_FOUND);
        }
        return convertToVO(field);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void add(Long userId, FieldDTO dto) {
        Field field = new Field();
        BeanUtils.copyProperties(dto, field);
        field.setUserId(userId);
        field.setFieldCode(generateFieldCode());
        field.setStatus(dto.getStatus() != null ? dto.getStatus() : 1);
        field.setCreateTime(LocalDateTime.now());
        field.setUpdateTime(LocalDateTime.now());
        fieldMapper.insert(field);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(Long userId, FieldDTO dto) {
        if (dto.getId() == null) {
            throw new BusinessException(ResponseCode.PARAM_ERROR, "地块ID不能为空");
        }

        Field field = fieldMapper.selectById(dto.getId());
        if (field == null) {
            throw new BusinessException(ResponseCode.DATA_NOT_FOUND);
        }
        if (!field.getUserId().equals(userId)) {
            throw new BusinessException(ResponseCode.FORBIDDEN, "无权操作此地块");
        }

        BeanUtils.copyProperties(dto, field, "id", "userId", "fieldCode", "createTime");
        field.setUpdateTime(LocalDateTime.now());
        fieldMapper.updateById(field);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(Long userId, Long id) {
        Field field = fieldMapper.selectById(id);
        if (field == null) {
            throw new BusinessException(ResponseCode.DATA_NOT_FOUND);
        }
        if (!field.getUserId().equals(userId)) {
            throw new BusinessException(ResponseCode.FORBIDDEN, "无权操作此地块");
        }

        // 检查是否有关联的作物
        if (cropMapper.countByFieldId(id) > 0) {
            throw new BusinessException("该地块下存在作物，无法删除");
        }

        fieldMapper.deleteById(id);
    }

    private FieldVO convertToVO(Field field) {
        FieldVO vo = new FieldVO();
        BeanUtils.copyProperties(field, vo);
        vo.setStatusName(getStatusName(field.getStatus()));
        return vo;
    }

    private String getStatusName(Integer status) {
        if (status == null) return "";
        switch (status) {
            case 0: return "休耕";
            case 1: return "在用";
            case 2: return "整改中";
            default: return "";
        }
    }

    private String generateFieldCode() {
        String code = fieldMapper.generateFieldCode();
        return code != null ? code : "FLD" + System.currentTimeMillis();
    }
}
