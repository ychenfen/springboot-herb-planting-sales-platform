package com.herb.platform.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 溯源信息实体类
 */
@Data
@TableName("herb_trace")
public class Trace implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Long id;

    private String traceCode;

    private Long cropId;

    private Long userId;

    private String herbName;

    private String batchNo;

    private String productionArea;

    private LocalDate plantDate;

    private LocalDate harvestDate;

    private String qualityStandard;

    private String qualityReport;

    private String certification;

    private String qrCodeUrl;

    private Integer status;

    private Integer scanCount;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    @TableLogic
    private Integer deleted;
}
