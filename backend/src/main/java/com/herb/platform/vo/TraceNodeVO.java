package com.herb.platform.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 溯源节点响应VO
 */
@Data
@ApiModel("溯源节点信息")
public class TraceNodeVO {

    @ApiModelProperty("节点ID")
    private Long id;

    @ApiModelProperty("溯源ID")
    private Long traceId;

    @ApiModelProperty("节点类型")
    private String nodeType;

    @ApiModelProperty("节点类型名称")
    private String nodeTypeName;

    @ApiModelProperty("节点名称")
    private String nodeName;

    @ApiModelProperty("节点时间")
    private LocalDateTime nodeTime;

    @ApiModelProperty("操作人")
    private String operator;

    @ApiModelProperty("操作人ID")
    private Long operatorId;

    @ApiModelProperty("地点")
    private String location;

    @ApiModelProperty("描述")
    private String description;

    @ApiModelProperty("图片")
    private String images;

    @ApiModelProperty("扩展数据")
    private String dataJson;

    @ApiModelProperty("排序")
    private Integer sortOrder;

    @ApiModelProperty("创建时间")
    private LocalDateTime createTime;
}
