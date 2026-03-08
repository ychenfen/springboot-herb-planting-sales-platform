package com.herb.platform.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.herb.platform.common.ResponseCode;
import com.herb.platform.dto.DictDTO;
import com.herb.platform.entity.Dict;
import com.herb.platform.exception.BusinessException;
import com.herb.platform.mapper.DictMapper;
import com.herb.platform.service.DictService;
import com.herb.platform.vo.DictVO;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 数据字典服务实现类
 */
@Service
@RequiredArgsConstructor
public class DictServiceImpl implements DictService {

    private final DictMapper dictMapper;

    @Override
    public IPage<DictVO> page(String dictType, Integer status, int pageNum, int pageSize) {
        Page<Dict> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<Dict> wrapper = new LambdaQueryWrapper<>();

        if (StringUtils.hasText(dictType)) {
            wrapper.eq(Dict::getDictType, dictType);
        }
        if (status != null) {
            wrapper.eq(Dict::getStatus, status);
        }
        wrapper.orderByAsc(Dict::getDictType);
        wrapper.orderByAsc(Dict::getSortOrder);

        IPage<Dict> dictPage = dictMapper.selectPage(page, wrapper);
        return dictPage.convert(this::convertToVO);
    }

    @Override
    public List<DictVO> listByType(String dictType) {
        LambdaQueryWrapper<Dict> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Dict::getDictType, dictType);
        wrapper.eq(Dict::getStatus, 1);
        wrapper.orderByAsc(Dict::getSortOrder);
        List<Dict> dicts = dictMapper.selectList(wrapper);
        return dicts.stream().map(this::convertToVO).collect(Collectors.toList());
    }

    @Override
    public List<String> listTypes() {
        LambdaQueryWrapper<Dict> wrapper = new LambdaQueryWrapper<>();
        wrapper.select(Dict::getDictType);
        wrapper.groupBy(Dict::getDictType);
        wrapper.orderByAsc(Dict::getDictType);
        List<Dict> dicts = dictMapper.selectList(wrapper);
        return dicts.stream().map(Dict::getDictType).collect(Collectors.toList());
    }

    @Override
    public void create(DictDTO dto) {
        Dict dict = new Dict();
        BeanUtils.copyProperties(dto, dict);
        dict.setStatus(dto.getStatus() != null ? dto.getStatus() : 1);
        dict.setSortOrder(dto.getSortOrder() != null ? dto.getSortOrder() : 0);
        dictMapper.insert(dict);
    }

    @Override
    public void update(DictDTO dto) {
        if (dto.getId() == null) {
            throw new BusinessException(ResponseCode.PARAM_ERROR, "字典ID不能为空");
        }
        Dict dict = dictMapper.selectById(dto.getId());
        if (dict == null) {
            throw new BusinessException(ResponseCode.DATA_NOT_FOUND);
        }
        BeanUtils.copyProperties(dto, dict, "id", "createTime");
        dictMapper.updateById(dict);
    }

    @Override
    public void delete(Long id) {
        Dict dict = dictMapper.selectById(id);
        if (dict == null) {
            throw new BusinessException(ResponseCode.DATA_NOT_FOUND);
        }
        dictMapper.deleteById(id);
    }

    private DictVO convertToVO(Dict dict) {
        DictVO vo = new DictVO();
        BeanUtils.copyProperties(dict, vo);
        return vo;
    }
}
