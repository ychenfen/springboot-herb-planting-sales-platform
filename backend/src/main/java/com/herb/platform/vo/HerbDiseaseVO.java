package com.herb.platform.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * Herb disease response.
 */
@Data
@ApiModel("病虫害图文案例")
public class HerbDiseaseVO {

    @ApiModelProperty("ID")
    private Long id;
    @ApiModelProperty("药材名称")
    private String herbName;
    @ApiModelProperty("病症名称")
    private String diseaseName;
    @ApiModelProperty("病害类型")
    private String diseaseType;
    @ApiModelProperty("症状关键词")
    private String symptomKeywords;
    @ApiModelProperty("症状描述")
    private String symptomDescription;
    @ApiModelProperty("防治方案")
    private String preventionPlan;
    @ApiModelProperty("示例图片")
    private String imageUrl;
    @ApiModelProperty("高发季节")
    private String season;
    @ApiModelProperty("严重程度")
    private String severityLevel;
    @ApiModelProperty("特征标签")
    private String featureTag;
    @ApiModelProperty("匹配分数")
    private Double matchScore;
    @ApiModelProperty("匹配原因")
    private String matchReason;
}
