package com.herb.platform.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * Disease recognition response.
 */
@Data
@ApiModel("病虫害识别结果")
public class DiseaseRecognitionVO {

    @ApiModelProperty("上传文件名")
    private String fileName;
    @ApiModelProperty("药材名称")
    private String herbName;
    @ApiModelProperty("识别结果")
    private List<HerbDiseaseVO> matches;
}
