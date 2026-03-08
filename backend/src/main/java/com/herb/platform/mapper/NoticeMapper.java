package com.herb.platform.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.herb.platform.entity.Notice;
import org.apache.ibatis.annotations.Mapper;

/**
 * 系统通知Mapper接口
 */
@Mapper
public interface NoticeMapper extends BaseMapper<Notice> {
}
