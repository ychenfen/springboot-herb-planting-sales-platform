package com.herb.platform.common;

import lombok.Data;

import java.io.Serializable;

/**
 * 统一返回结果类
 */
@Data
public class Result<T> implements Serializable {

    private static final long serialVersionUID = 1L;

    private int code;
    private String message;
    private T data;
    private long timestamp;

    public Result() {
        this.timestamp = System.currentTimeMillis();
    }

    public Result(int code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
        this.timestamp = System.currentTimeMillis();
    }

    public static <T> Result<T> success() {
        return new Result<>(ResponseCode.SUCCESS.getCode(), ResponseCode.SUCCESS.getMessage(), null);
    }

    public static <T> Result<T> success(T data) {
        return new Result<>(ResponseCode.SUCCESS.getCode(), ResponseCode.SUCCESS.getMessage(), data);
    }

    public static <T> Result<T> success(String message, T data) {
        return new Result<>(ResponseCode.SUCCESS.getCode(), message, data);
    }

    public static <T> Result<T> failed() {
        return new Result<>(ResponseCode.FAILED.getCode(), ResponseCode.FAILED.getMessage(), null);
    }

    public static <T> Result<T> failed(String message) {
        return new Result<>(ResponseCode.FAILED.getCode(), message, null);
    }

    public static <T> Result<T> failed(ResponseCode responseCode) {
        return new Result<>(responseCode.getCode(), responseCode.getMessage(), null);
    }

    public static <T> Result<T> failed(ResponseCode responseCode, String message) {
        return new Result<>(responseCode.getCode(), message, null);
    }

    public static <T> Result<T> failed(int code, String message) {
        return new Result<>(code, message, null);
    }

    public boolean isSuccess() {
        return this.code == ResponseCode.SUCCESS.getCode();
    }
}
