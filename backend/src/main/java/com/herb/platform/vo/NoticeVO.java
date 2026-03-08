package com.herb.platform.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 系统通知响应VO
 */
@Data
@ApiModel("系统通知信息")
public class NoticeVO {

    @ApiModelProperty("ID")
    private Long id;

    @ApiModelProperty("接收用户ID")
    private Long userId;

    @ApiModelProperty("用户名")
    private String username;

    @ApiModelProperty("真实姓名")
    private String realName;

    @ApiModelProperty("通知类型")
    private Integer noticeType;

    @ApiModelProperty("通知类型名称")
    private String noticeTypeName;

    @ApiModelProperty("标题")
    private String title;

    @ApiModelProperty("内容")
    private String content;

    @ApiModelProperty("链接地址")
    private String linkUrl;

    @ApiModelProperty("是否已读: 0-未读 1-已读")
    private Integer isRead;

    @ApiModelProperty("阅读状态")
    private String readStatusName;

    @ApiModelProperty("阅读时间")
    private LocalDateTime readTime;

    @ApiModelProperty("创建时间")
    private LocalDateTime createTime;
}
