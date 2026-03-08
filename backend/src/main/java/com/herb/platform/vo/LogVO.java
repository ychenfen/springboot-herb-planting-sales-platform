package com.herb.platform.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 系统日志响应VO
 */
@Data
@ApiModel("系统日志信息")
public class LogVO {

    @ApiModelProperty("ID")
    private Long id;

    @ApiModelProperty("用户ID")
    private Long userId;

    @ApiModelProperty("用户名")
    private String username;

    @ApiModelProperty("操作")
    private String operation;

    @ApiModelProperty("方法名")
    private String method;

    @ApiModelProperty("参数")
    private String params;

    @ApiModelProperty("IP地址")
    private String ip;

    @ApiModelProperty("User-Agent")
    private String userAgent;

    @ApiModelProperty("返回结果")
    private String result;

    @ApiModelProperty("执行耗时(ms)")
    private Integer executeTime;

    @ApiModelProperty("状态: 0-失败 1-成功")
    private Integer status;

    @ApiModelProperty("状态名称")
    private String statusName;

    @ApiModelProperty("错误信息")
    private String errorMsg;

    @ApiModelProperty("创建时间")
    private LocalDateTime createTime;
}
