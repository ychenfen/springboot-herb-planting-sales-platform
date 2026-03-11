package com.herb.platform.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * Herb encyclopedia entity.
 */
@Data
@TableName("herb_knowledge")
public class HerbKnowledge implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Long id;
    private String herbName;
    private String herbAlias;
    private String herbCategory;
    private String plantingSeason;
    private String diseaseType;
    private String keywordTags;
    private String summary;
    private String plantingPoints;
    private String diseasePrevention;
    private String ruralValue;
    private String content;
    private String suitableRegion;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    @TableLogic
    private Integer deleted;
}
