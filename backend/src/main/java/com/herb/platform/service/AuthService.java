package com.herb.platform.service;

import com.herb.platform.dto.LoginDTO;
import com.herb.platform.dto.RegisterDTO;
import com.herb.platform.dto.UpdatePasswordDTO;
import com.herb.platform.dto.UpdateProfileDTO;
import com.herb.platform.vo.LoginVO;
import com.herb.platform.vo.UserVO;

/**
 * 认证服务接口
 */
public interface AuthService {

    /**
     * 用户登录
     */
    LoginVO login(LoginDTO loginDTO, String ip);

    /**
     * 用户注册
     */
    void register(RegisterDTO registerDTO);

    /**
     * 获取当前用户信息
     */
    UserVO getCurrentUser(Long userId);

    /**
     * 退出登录
     */
    void logout(Long userId);

    /**
     * 修改密码
     */
    void updatePassword(Long userId, UpdatePasswordDTO dto);

    /**
     * 更新个人信息
     */
    void updateProfile(Long userId, UpdateProfileDTO dto);
}
