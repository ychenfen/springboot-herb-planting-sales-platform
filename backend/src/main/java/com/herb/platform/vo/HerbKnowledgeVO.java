package com.herb.platform.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * Herb knowledge response.
 */
@Data
@ApiModel("中药材知识百科")
public class HerbKnowledgeVO {

    @ApiModelProperty("ID")
    private Long id;
    @ApiModelProperty("药材名称")
    private String herbName;
    @ApiModelProperty("别名")
    private String herbAlias;
    @ApiModelProperty("药材分类")
    private String herbCategory;
    @ApiModelProperty("适宜季节")
    private String plantingSeason;
    @ApiModelProperty("常见病害类型")
    private String diseaseType;
    @ApiModelProperty("关键词")
    private String keywordTags;
    @ApiModelProperty("摘要")
    private String summary;
    @ApiModelProperty("种植要点")
    private String plantingPoints;
    @ApiModelProperty("病害防治")
    private String diseasePrevention;
    @ApiModelProperty("乡村振兴价值")
    private String ruralValue;
    @ApiModelProperty("详细内容")
    private String content;
    @ApiModelProperty("适宜区域")
    private String suitableRegion;
}
