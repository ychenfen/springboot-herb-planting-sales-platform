package com.herb.platform.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 供应信息实体类
 */
@Data
@TableName("herb_supply")
public class Supply implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long userId;

    private Long cropId;

    private String herbName;

    private String herbVariety;

    private String qualityGrade;

    private BigDecimal supplyQuantity;

    private BigDecimal remainingQuantity;

    private BigDecimal price;

    private Integer priceNegotiable;

    private String productionArea;

    private LocalDate harvestDate;

    private String storageCondition;

    private String description;

    private String images;

    private String certification;

    private String contactName;

    private String contactPhone;

    private String contactWechat;

    private Integer status;

    private Integer viewCount;

    private Integer favoriteCount;

    private Integer isTop;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    @TableLogic
    private Integer deleted;
}
