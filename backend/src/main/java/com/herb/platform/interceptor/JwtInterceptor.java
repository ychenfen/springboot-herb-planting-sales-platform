package com.herb.platform.interceptor;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.herb.platform.common.Constants;
import com.herb.platform.common.ResponseCode;
import com.herb.platform.common.Result;
import com.herb.platform.utils.JwtUtil;
import com.herb.platform.utils.RedisUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * JWT认证拦截器
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class JwtInterceptor implements HandlerInterceptor {

    private final JwtUtil jwtUtil;
    private final ObjectMapper objectMapper;
    private final RedisUtil redisUtil;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // OPTIONS请求直接放行
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            return true;
        }

        // 获取token
        String authHeader = request.getHeader(Constants.TOKEN_HEADER);
        String token = jwtUtil.extractToken(authHeader);

        if (token == null) {
            sendError(response, ResponseCode.UNAUTHORIZED);
            return false;
        }

        // 验证token
        if (!jwtUtil.validateToken(token)) {
            sendError(response, ResponseCode.TOKEN_EXPIRED);
            return false;
        }

        // 将用户信息存入request
        Long userId = jwtUtil.getUserIdFromToken(token);
        if (userId == null) {
            sendError(response, ResponseCode.TOKEN_INVALID);
            return false;
        }

        String tokenKey = Constants.REDIS_TOKEN_PREFIX + userId;
        Object cachedToken = redisUtil.get(tokenKey);
        if (cachedToken == null || !token.equals(String.valueOf(cachedToken))) {
            sendError(response, ResponseCode.TOKEN_INVALID);
            return false;
        }

        String username = jwtUtil.getUsernameFromToken(token);
        Integer userType = jwtUtil.getUserTypeFromToken(token);

        request.setAttribute("userId", userId);
        request.setAttribute("username", username);
        request.setAttribute("userType", userType);

        return true;
    }

    private void sendError(HttpServletResponse response, ResponseCode responseCode) throws IOException {
        response.setContentType("application/json;charset=UTF-8");
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        Result<Void> result = Result.failed(responseCode);
        response.getWriter().write(objectMapper.writeValueAsString(result));
    }
}
