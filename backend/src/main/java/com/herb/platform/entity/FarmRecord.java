package com.herb.platform.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

/**
 * 农事记录实体类
 */
@Data
@TableName("herb_farm_record")
public class FarmRecord implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long cropId;

    private Long userId;

    private String activityType;

    private LocalDate activityDate;

    private LocalTime activityTime;

    private String activityDetail;

    private String materialsUsed;

    private String dosage;

    private BigDecimal cost;

    private String weather;

    private String temperature;

    private String humidity;

    private String remark;

    private String images;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    @TableLogic
    private Integer deleted;
}
