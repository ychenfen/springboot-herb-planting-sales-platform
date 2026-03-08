package com.herb.platform.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.herb.platform.common.ResponseCode;
import com.herb.platform.dto.ConfigDTO;
import com.herb.platform.entity.Config;
import com.herb.platform.exception.BusinessException;
import com.herb.platform.mapper.ConfigMapper;
import com.herb.platform.service.ConfigService;
import com.herb.platform.vo.ConfigVO;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 系统配置服务实现类
 */
@Service
@RequiredArgsConstructor
public class ConfigServiceImpl implements ConfigService {

    private final ConfigMapper configMapper;

    @Override
    public IPage<ConfigVO> page(String configKey, String configType, int pageNum, int pageSize) {
        Page<Config> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<Config> wrapper = new LambdaQueryWrapper<>();

        if (StringUtils.hasText(configKey)) {
            wrapper.like(Config::getConfigKey, configKey);
        }
        if (StringUtils.hasText(configType)) {
            wrapper.eq(Config::getConfigType, configType);
        }
        wrapper.orderByAsc(Config::getConfigKey);

        IPage<Config> configPage = configMapper.selectPage(page, wrapper);
        return configPage.convert(this::convertToVO);
    }

    @Override
    public List<String> listTypes() {
        LambdaQueryWrapper<Config> wrapper = new LambdaQueryWrapper<>();
        wrapper.select(Config::getConfigType);
        wrapper.isNotNull(Config::getConfigType);
        wrapper.groupBy(Config::getConfigType);
        wrapper.orderByAsc(Config::getConfigType);
        List<Config> configs = configMapper.selectList(wrapper);
        return configs.stream()
                .map(Config::getConfigType)
                .filter(StringUtils::hasText)
                .collect(Collectors.toList());
    }

    @Override
    public ConfigVO getById(Long id) {
        Config config = configMapper.selectById(id);
        if (config == null) {
            throw new BusinessException(ResponseCode.DATA_NOT_FOUND);
        }
        return convertToVO(config);
    }

    @Override
    public void create(ConfigDTO dto) {
        LambdaQueryWrapper<Config> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Config::getConfigKey, dto.getConfigKey());
        if (configMapper.selectCount(wrapper) > 0) {
            throw new BusinessException(ResponseCode.DATA_EXISTS, "配置键已存在");
        }
        Config config = new Config();
        BeanUtils.copyProperties(dto, config);
        configMapper.insert(config);
    }

    @Override
    public void update(ConfigDTO dto) {
        if (dto.getId() == null) {
            throw new BusinessException(ResponseCode.PARAM_ERROR, "配置ID不能为空");
        }

        Config config = configMapper.selectById(dto.getId());
        if (config == null) {
            throw new BusinessException(ResponseCode.DATA_NOT_FOUND);
        }

        if (StringUtils.hasText(dto.getConfigKey()) && !dto.getConfigKey().equals(config.getConfigKey())) {
            LambdaQueryWrapper<Config> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(Config::getConfigKey, dto.getConfigKey());
            wrapper.ne(Config::getId, dto.getId());
            if (configMapper.selectCount(wrapper) > 0) {
                throw new BusinessException(ResponseCode.DATA_EXISTS, "配置键已存在");
            }
        }

        BeanUtils.copyProperties(dto, config, "id", "createTime");
        configMapper.updateById(config);
    }

    @Override
    public void delete(Long id) {
        Config config = configMapper.selectById(id);
        if (config == null) {
            throw new BusinessException(ResponseCode.DATA_NOT_FOUND);
        }
        configMapper.deleteById(id);
    }

    private ConfigVO convertToVO(Config config) {
        ConfigVO vo = new ConfigVO();
        BeanUtils.copyProperties(config, vo);
        return vo;
    }
}
