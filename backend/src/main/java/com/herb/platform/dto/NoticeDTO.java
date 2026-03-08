package com.herb.platform.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * 系统通知DTO
 */
@Data
@ApiModel("系统通知请求参数")
public class NoticeDTO {

    @ApiModelProperty("ID")
    private Long id;

    @NotNull(message = "接收用户不能为空")
    @ApiModelProperty("接收用户ID")
    private Long userId;

    @NotNull(message = "通知类型不能为空")
    @ApiModelProperty("通知类型: 1-系统通知 2-订单通知 3-评论通知 4-其他")
    private Integer noticeType;

    @NotBlank(message = "通知标题不能为空")
    @ApiModelProperty("标题")
    private String title;

    @NotBlank(message = "通知内容不能为空")
    @ApiModelProperty("内容")
    private String content;

    @ApiModelProperty("链接地址")
    private String linkUrl;
}
