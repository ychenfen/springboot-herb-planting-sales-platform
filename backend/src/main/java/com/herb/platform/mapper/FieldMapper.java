package com.herb.platform.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.herb.platform.entity.Field;
import com.herb.platform.vo.FieldVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

/**
 * 地块Mapper接口
 */
@Mapper
public interface FieldMapper extends BaseMapper<Field> {

    /**
     * 根据用户ID查询地块数量
     */
    @Select("SELECT COUNT(*) FROM herb_field WHERE user_id = #{userId} AND deleted = 0")
    int countByUserId(@Param("userId") Long userId);

    /**
     * 检查地块编码是否存在
     */
    @Select("SELECT COUNT(*) FROM herb_field WHERE field_code = #{fieldCode} AND deleted = 0")
    int countByFieldCode(@Param("fieldCode") String fieldCode);

    /**
     * 生成地块编码
     */
    @Select("SELECT CONCAT('FLD', DATE_FORMAT(NOW(), '%Y%m%d'), LPAD(IFNULL(MAX(SUBSTRING(field_code, 12)), 0) + 1, 4, '0')) FROM herb_field WHERE field_code LIKE CONCAT('FLD', DATE_FORMAT(NOW(), '%Y%m%d'), '%')")
    String generateFieldCode();
}
