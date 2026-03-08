package com.herb.platform.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.herb.platform.dto.ConfigDTO;
import com.herb.platform.vo.ConfigVO;

import java.util.List;

/**
 * 系统配置服务接口
 */
public interface ConfigService {

    /**
     * 分页查询
     */
    IPage<ConfigVO> page(String configKey, String configType, int pageNum, int pageSize);

    /**
     * 获取所有配置类型
     */
    List<String> listTypes();

    /**
     * 根据ID获取配置
     */
    ConfigVO getById(Long id);

    /**
     * 创建配置
     */
    void create(ConfigDTO dto);

    /**
     * 更新配置
     */
    void update(ConfigDTO dto);

    /**
     * 删除配置
     */
    void delete(Long id);
}
