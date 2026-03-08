package com.herb.platform.common;

import lombok.Getter;

/**
 * 响应码枚举
 */
@Getter
public enum ResponseCode {

    SUCCESS(200, "操作成功"),
    FAILED(500, "操作失败"),

    // 参数错误 400xx
    PARAM_ERROR(40001, "参数错误"),
    PARAM_MISSING(40002, "缺少必要参数"),
    PARAM_INVALID(40003, "参数格式不正确"),

    // 认证错误 401xx
    UNAUTHORIZED(40101, "未登录或登录已过期"),
    TOKEN_EXPIRED(40102, "Token已过期"),
    TOKEN_INVALID(40103, "Token无效"),
    LOGIN_FAILED(40104, "用户名或密码错误"),
    ACCOUNT_DISABLED(40105, "账号已被禁用"),
    ACCOUNT_NOT_EXIST(40106, "账号不存在"),

    // 权限错误 403xx
    FORBIDDEN(40301, "没有操作权限"),
    ACCESS_DENIED(40302, "访问被拒绝"),

    // 资源错误 404xx
    NOT_FOUND(40401, "资源不存在"),
    DATA_NOT_FOUND(40402, "数据不存在"),

    // 业务错误 500xx
    BUSINESS_ERROR(50001, "业务处理失败"),
    USER_EXISTS(50002, "用户已存在"),
    PHONE_EXISTS(50003, "手机号已被注册"),
    OLD_PASSWORD_ERROR(50004, "原密码错误"),
    DATA_EXISTS(50005, "数据已存在"),
    OPERATION_FAILED(50006, "操作执行失败"),

    // 系统错误 600xx
    SYSTEM_ERROR(60001, "系统异常"),
    DATABASE_ERROR(60002, "数据库异常"),
    REDIS_ERROR(60003, "缓存服务异常"),
    FILE_UPLOAD_ERROR(60004, "文件上传失败");

    private final int code;
    private final String message;

    ResponseCode(int code, String message) {
        this.code = code;
        this.message = message;
    }
}
