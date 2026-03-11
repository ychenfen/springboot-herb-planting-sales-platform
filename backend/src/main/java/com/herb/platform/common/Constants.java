package com.herb.platform.common;

/**
 * System constants.
 */
public class Constants {

    private Constants() {
    }

    // ==================== User ====================
    public static final int USER_TYPE_FARMER = 1;
    public static final int USER_TYPE_MERCHANT = 2;
    public static final int USER_TYPE_BUYER = USER_TYPE_MERCHANT;
    public static final int USER_TYPE_ADMIN = 3;
    public static final int USER_TYPE_USER = 4;

    public static final int USER_STATUS_DISABLED = 0;
    public static final int USER_STATUS_NORMAL = 1;

    // ==================== Token ====================
    public static final String TOKEN_HEADER = "Authorization";
    public static final String TOKEN_PREFIX = "Bearer ";
    public static final String REDIS_TOKEN_PREFIX = "token:";
    public static final long TOKEN_EXPIRE_TIME = 7 * 24 * 60 * 60;

    // ==================== Cache ====================
    public static final String REDIS_USER_PREFIX = "user:";
    public static final String REDIS_PERMISSION_PREFIX = "permission:";
    public static final String REDIS_CAPTCHA_PREFIX = "captcha:";
    public static final long CAPTCHA_EXPIRE_TIME = 5 * 60;

    // ==================== Page ====================
    public static final int DEFAULT_PAGE_NUM = 1;
    public static final int DEFAULT_PAGE_SIZE = 10;
    public static final int MAX_PAGE_SIZE = 100;

    // ==================== Order ====================
    public static final int ORDER_STATUS_PENDING = 1;
    public static final int ORDER_STATUS_CONFIRMED = 2;
    public static final int ORDER_STATUS_DELIVERING = 3;
    public static final int ORDER_STATUS_COMPLETED = 4;
    public static final int ORDER_STATUS_CANCELLED = 5;
    public static final int ORDER_STATUS_REFUNDED = 6;

    // ==================== Supply / Demand ====================
    public static final int SUPPLY_STATUS_ACTIVE = 1;
    public static final int SUPPLY_STATUS_SOLD_OUT = 2;
    public static final int SUPPLY_STATUS_OFF_SHELF = 3;

    public static final int DEMAND_STATUS_ACTIVE = 1;
    public static final int DEMAND_STATUS_COMPLETED = 2;
    public static final int DEMAND_STATUS_CANCELLED = 3;

    // ==================== File ====================
    public static final String[] ALLOWED_IMAGE_TYPES = {"jpg", "jpeg", "png", "gif", "webp"};
    public static final String[] ALLOWED_FILE_TYPES = {
            "jpg", "jpeg", "png", "gif", "webp", "pdf", "doc", "docx", "xls", "xlsx"
    };
    public static final int MAX_FILE_SIZE = 10;

    // ==================== Delete ====================
    public static final int NOT_DELETED = 0;
    public static final int DELETED = 1;
}
