package com.herb.platform.interceptor;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.herb.platform.annotation.RequireUserType;
import com.herb.platform.common.ResponseCode;
import com.herb.platform.common.Result;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;

/**
 * 用户类型访问控制拦截器
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class AccessInterceptor implements HandlerInterceptor {

    private final ObjectMapper objectMapper;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (!(handler instanceof HandlerMethod)) {
            return true;
        }

        HandlerMethod handlerMethod = (HandlerMethod) handler;
        RequireUserType requireUserType = getRequireUserType(handlerMethod);
        if (requireUserType == null) {
            return true;
        }

        Object userTypeObj = request.getAttribute("userType");
        if (!(userTypeObj instanceof Integer)) {
            sendError(response, HttpStatus.UNAUTHORIZED, ResponseCode.UNAUTHORIZED, "未登录或登录已过期");
            return false;
        }

        int userType = (Integer) userTypeObj;
        boolean allowed = Arrays.stream(requireUserType.value()).anyMatch(t -> t == userType);
        if (!allowed) {
            log.warn("访问被拒绝: userType={}, uri={}", userType, request.getRequestURI());
            sendError(response, HttpStatus.FORBIDDEN, ResponseCode.FORBIDDEN, "没有操作权限");
            return false;
        }

        return true;
    }

    private RequireUserType getRequireUserType(HandlerMethod handlerMethod) {
        RequireUserType methodAnnotation = handlerMethod.getMethodAnnotation(RequireUserType.class);
        if (methodAnnotation != null) {
            return methodAnnotation;
        }
        return handlerMethod.getBeanType().getAnnotation(RequireUserType.class);
    }

    private void sendError(HttpServletResponse response, HttpStatus status, ResponseCode responseCode, String message) throws IOException {
        response.setContentType("application/json;charset=UTF-8");
        response.setStatus(status.value());
        Result<Void> result = Result.failed(responseCode, message);
        response.getWriter().write(objectMapper.writeValueAsString(result));
    }
}
