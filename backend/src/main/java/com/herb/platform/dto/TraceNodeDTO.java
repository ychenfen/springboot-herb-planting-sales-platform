package com.herb.platform.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

/**
 * 溯源节点请求DTO
 */
@Data
@ApiModel("溯源节点请求")
public class TraceNodeDTO {

    @ApiModelProperty("节点ID（编辑时必填）")
    private Long id;

    @NotNull(message = "溯源信息不能为空")
    @ApiModelProperty(value = "溯源ID", required = true)
    private Long traceId;

    @NotBlank(message = "节点类型不能为空")
    @ApiModelProperty(value = "节点类型", required = true)
    private String nodeType;

    @NotBlank(message = "节点名称不能为空")
    @ApiModelProperty(value = "节点名称", required = true)
    private String nodeName;

    @NotNull(message = "节点时间不能为空")
    @ApiModelProperty(value = "节点时间", required = true)
    private LocalDateTime nodeTime;

    @ApiModelProperty("操作人")
    private String operator;

    @ApiModelProperty("地点")
    private String location;

    @ApiModelProperty("描述")
    private String description;

    @ApiModelProperty("图片(JSON数组)")
    private String images;

    @ApiModelProperty("扩展数据(JSON)")
    private String dataJson;
}
