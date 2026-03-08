package com.herb.platform.controller;

import com.herb.platform.common.Result;
import com.herb.platform.dto.LoginDTO;
import com.herb.platform.dto.RegisterDTO;
import com.herb.platform.dto.UpdatePasswordDTO;
import com.herb.platform.dto.UpdateProfileDTO;
import com.herb.platform.service.AuthService;
import com.herb.platform.vo.LoginVO;
import com.herb.platform.vo.UserVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

/**
 * 认证控制器
 */
@Api(tags = "认证管理")
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @ApiOperation("用户登录")
    @PostMapping("/login")
    public Result<LoginVO> login(@Validated @RequestBody LoginDTO loginDTO, HttpServletRequest request) {
        String ip = getClientIp(request);
        LoginVO loginVO = authService.login(loginDTO, ip);
        return Result.success(loginVO);
    }

    @ApiOperation("用户注册")
    @PostMapping("/register")
    public Result<Void> register(@Validated @RequestBody RegisterDTO registerDTO) {
        authService.register(registerDTO);
        return Result.success();
    }

    @ApiOperation("获取当前用户信息")
    @GetMapping("/info")
    public Result<UserVO> getCurrentUser(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        UserVO userVO = authService.getCurrentUser(userId);
        return Result.success(userVO);
    }

    @ApiOperation("退出登录")
    @PostMapping("/logout")
    public Result<Void> logout(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        authService.logout(userId);
        return Result.success();
    }

    @ApiOperation("修改密码")
    @PutMapping("/password")
    public Result<Void> updatePassword(@Validated @RequestBody UpdatePasswordDTO dto, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        authService.updatePassword(userId, dto);
        return Result.success();
    }

    @ApiOperation("更新个人信息")
    @PutMapping("/profile")
    public Result<Void> updateProfile(@Validated @RequestBody UpdateProfileDTO dto, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        authService.updateProfile(userId, dto);
        return Result.success();
    }

    private String getClientIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("X-Real-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        // 多个代理时，取第一个IP
        if (ip != null && ip.contains(",")) {
            ip = ip.split(",")[0].trim();
        }
        return ip;
    }
}
