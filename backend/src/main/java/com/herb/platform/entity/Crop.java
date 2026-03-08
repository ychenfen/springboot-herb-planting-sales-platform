package com.herb.platform.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 作物实体类
 */
@Data
@TableName("herb_crop")
public class Crop implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long userId;

    private Long fieldId;

    private String cropName;

    private String cropVariety;

    private LocalDate plantDate;

    private LocalDate expectedHarvestDate;

    private LocalDate actualHarvestDate;

    private BigDecimal plantArea;

    private BigDecimal expectedYield;

    private BigDecimal actualYield;

    private String growthStage;

    private String growthStatus;

    private String description;

    private String images;

    private Integer status;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    @TableLogic
    private Integer deleted;
}
