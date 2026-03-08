package com.herb.platform.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.herb.platform.dto.DictDTO;
import com.herb.platform.vo.DictVO;

import java.util.List;

/**
 * 数据字典服务接口
 */
public interface DictService {

    /**
     * 分页查询
     */
    IPage<DictVO> page(String dictType, Integer status, int pageNum, int pageSize);

    /**
     * 根据类型获取字典列表
     */
    List<DictVO> listByType(String dictType);

    /**
     * 获取所有字典类型
     */
    List<String> listTypes();

    /**
     * 创建字典
     */
    void create(DictDTO dto);

    /**
     * 更新字典
     */
    void update(DictDTO dto);

    /**
     * 删除字典
     */
    void delete(Long id);
}
