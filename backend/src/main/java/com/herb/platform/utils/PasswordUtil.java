package com.herb.platform.utils;

import cn.hutool.crypto.digest.BCrypt;
import org.springframework.stereotype.Component;

/**
 * 密码工具类（使用BCrypt加密）
 */
@Component
public class PasswordUtil {

    /**
     * 加密密码
     */
    public String encrypt(String password) {
        return BCrypt.hashpw(password, BCrypt.gensalt());
    }

    /**
     * 验证密码
     */
    public boolean verify(String rawPassword, String encryptedPassword) {
        if (rawPassword == null || encryptedPassword == null) {
            return false;
        }
        try {
            return BCrypt.checkpw(rawPassword, encryptedPassword);
        } catch (Exception e) {
            return false;
        }
    }
}
