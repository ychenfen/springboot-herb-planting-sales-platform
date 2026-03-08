package com.herb.platform.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * 数据字典DTO
 */
@Data
@ApiModel("数据字典请求参数")
public class DictDTO {

    @ApiModelProperty("ID")
    private Long id;

    @NotBlank(message = "字典类型不能为空")
    @ApiModelProperty("字典类型")
    private String dictType;

    @NotBlank(message = "字典标签不能为空")
    @ApiModelProperty("字典标签")
    private String dictLabel;

    @NotBlank(message = "字典值不能为空")
    @ApiModelProperty("字典值")
    private String dictValue;

    @ApiModelProperty("排序")
    private Integer sortOrder;

    @ApiModelProperty("状态")
    private Integer status;

    @ApiModelProperty("备注")
    private String remark;
}
