package com.herb.platform.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 溯源节点实体类
 */
@Data
@TableName("herb_trace_node")
public class TraceNode implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long traceId;

    private String nodeType;

    private String nodeName;

    private LocalDateTime nodeTime;

    private String operator;

    private Long operatorId;

    private String location;

    private String description;

    private String images;

    private String dataJson;

    private Integer sortOrder;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    @TableLogic
    private Integer deleted;
}
