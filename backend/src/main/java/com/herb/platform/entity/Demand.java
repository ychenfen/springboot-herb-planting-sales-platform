package com.herb.platform.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 需求信息实体类
 */
@Data
@TableName("herb_demand")
public class Demand implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long userId;

    private String herbName;

    private String herbVariety;

    private String qualityRequirement;

    private BigDecimal demandQuantity;

    private BigDecimal targetPrice;

    private String priceRange;

    private LocalDate demandDate;

    private LocalDate expiryDate;

    private String deliveryAddress;

    private String description;

    private String contactName;

    private String contactPhone;

    private String contactWechat;

    private Integer status;

    private Integer viewCount;

    private Integer isTop;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    @TableLogic
    private Integer deleted;
}
