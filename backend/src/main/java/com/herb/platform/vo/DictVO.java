package com.herb.platform.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 数据字典响应VO
 */
@Data
@ApiModel("数据字典信息")
public class DictVO {

    @ApiModelProperty("ID")
    private Long id;

    @ApiModelProperty("字典类型")
    private String dictType;

    @ApiModelProperty("字典标签")
    private String dictLabel;

    @ApiModelProperty("字典值")
    private String dictValue;

    @ApiModelProperty("排序")
    private Integer sortOrder;

    @ApiModelProperty("状态")
    private Integer status;

    @ApiModelProperty("备注")
    private String remark;

    @ApiModelProperty("创建时间")
    private LocalDateTime createTime;
}
