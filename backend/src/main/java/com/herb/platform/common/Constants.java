package com.herb.platform.common;

/**
 * 系统常量类
 */
public class Constants {

    private Constants() {}

    // ==================== 用户相关 ====================
    /** 用户类型：种植户 */
    public static final int USER_TYPE_FARMER = 1;
    /** 用户类型：采购商 */
    public static final int USER_TYPE_BUYER = 2;
    /** 用户类型：管理员 */
    public static final int USER_TYPE_ADMIN = 3;

    /** 用户状态：禁用 */
    public static final int USER_STATUS_DISABLED = 0;
    /** 用户状态：正常 */
    public static final int USER_STATUS_NORMAL = 1;

    // ==================== Token相关 ====================
    /** Token请求头 */
    public static final String TOKEN_HEADER = "Authorization";
    /** Token前缀 */
    public static final String TOKEN_PREFIX = "Bearer ";
    /** Token在Redis中的前缀 */
    public static final String REDIS_TOKEN_PREFIX = "token:";
    /** Token默认过期时间（秒）：7天 */
    public static final long TOKEN_EXPIRE_TIME = 7 * 24 * 60 * 60;

    // ==================== 缓存相关 ====================
    /** 用户信息缓存前缀 */
    public static final String REDIS_USER_PREFIX = "user:";
    /** 权限缓存前缀 */
    public static final String REDIS_PERMISSION_PREFIX = "permission:";
    /** 验证码缓存前缀 */
    public static final String REDIS_CAPTCHA_PREFIX = "captcha:";
    /** 验证码过期时间（秒） */
    public static final long CAPTCHA_EXPIRE_TIME = 5 * 60;

    // ==================== 分页相关 ====================
    /** 默认页码 */
    public static final int DEFAULT_PAGE_NUM = 1;
    /** 默认每页数量 */
    public static final int DEFAULT_PAGE_SIZE = 10;
    /** 最大每页数量 */
    public static final int MAX_PAGE_SIZE = 100;

    // ==================== 订单相关 ====================
    /** 订单状态：待确认 */
    public static final int ORDER_STATUS_PENDING = 1;
    /** 订单状态：已确认 */
    public static final int ORDER_STATUS_CONFIRMED = 2;
    /** 订单状态：配送中 */
    public static final int ORDER_STATUS_DELIVERING = 3;
    /** 订单状态：已完成 */
    public static final int ORDER_STATUS_COMPLETED = 4;
    /** 订单状态：已取消 */
    public static final int ORDER_STATUS_CANCELLED = 5;
    /** 订单状态：已退款 */
    public static final int ORDER_STATUS_REFUNDED = 6;

    // ==================== 供应/需求状态 ====================
    /** 供应状态：供应中 */
    public static final int SUPPLY_STATUS_ACTIVE = 1;
    /** 供应状态：已售罄 */
    public static final int SUPPLY_STATUS_SOLD_OUT = 2;
    /** 供应状态：已下架 */
    public static final int SUPPLY_STATUS_OFF_SHELF = 3;

    /** 需求状态：采购中 */
    public static final int DEMAND_STATUS_ACTIVE = 1;
    /** 需求状态：已完成 */
    public static final int DEMAND_STATUS_COMPLETED = 2;
    /** 需求状态：已取消 */
    public static final int DEMAND_STATUS_CANCELLED = 3;

    // ==================== 文件上传相关 ====================
    /** 允许上传的图片类型 */
    public static final String[] ALLOWED_IMAGE_TYPES = {"jpg", "jpeg", "png", "gif", "webp"};
    /** 允许上传的文件类型 */
    public static final String[] ALLOWED_FILE_TYPES = {"jpg", "jpeg", "png", "gif", "webp", "pdf", "doc", "docx", "xls", "xlsx"};
    /** 最大文件大小（MB） */
    public static final int MAX_FILE_SIZE = 10;

    // ==================== 逻辑删除 ====================
    /** 未删除 */
    public static final int NOT_DELETED = 0;
    /** 已删除 */
    public static final int DELETED = 1;
}
