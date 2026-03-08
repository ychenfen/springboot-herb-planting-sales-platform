package com.herb.platform.aspect;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.herb.platform.entity.Log;
import com.herb.platform.mapper.LogMapper;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import org.springframework.validation.BindingResult;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;
import java.time.temporal.Temporal;
import java.util.*;

/**
 * 操作日志切面
 */
@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class SysLogAspect {

    private static final int MAX_CONTENT_LENGTH = 2000;
    private static final Set<String> SENSITIVE_KEYS = new HashSet<>(Arrays.asList(
            "password", "oldPassword", "newPassword", "confirmPassword"
    ));

    private final LogMapper logMapper;
    private final ObjectMapper objectMapper;

    @Pointcut("within(com.herb.platform.controller..*)")
    public void controllerPointcut() {
    }

    @Around("controllerPointcut()")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
        HttpServletRequest request = getRequest();
        if (!shouldLog(request)) {
            return joinPoint.proceed();
        }

        long startTime = System.currentTimeMillis();
        Object result = null;
        Exception error = null;

        try {
            result = joinPoint.proceed();
            return result;
        } catch (Exception ex) {
            error = ex;
            throw ex;
        } finally {
            try {
                saveLog(joinPoint, request, result, error, System.currentTimeMillis() - startTime);
            } catch (Exception ex) {
                log.warn("Failed to save sys log: {}", ex.getMessage());
            }
        }
    }

    private boolean shouldLog(HttpServletRequest request) {
        if (request == null) {
            return false;
        }
        String method = request.getMethod();
        return "POST".equalsIgnoreCase(method)
                || "PUT".equalsIgnoreCase(method)
                || "DELETE".equalsIgnoreCase(method)
                || "PATCH".equalsIgnoreCase(method);
    }

    private void saveLog(ProceedingJoinPoint joinPoint, HttpServletRequest request, Object result,
                         Exception error, long executeTime) {
        Log logEntity = new Log();

        String operation = resolveOperation(joinPoint);
        if (operation == null && request != null) {
            operation = request.getRequestURI();
        }

        logEntity.setOperation(operation);
        logEntity.setMethod(resolveMethodName(joinPoint));
        logEntity.setParams(buildParams(request, joinPoint.getArgs()));
        logEntity.setIp(resolveIp(request));
        logEntity.setUserAgent(request != null ? request.getHeader("User-Agent") : null);
        logEntity.setExecuteTime((int) executeTime);
        logEntity.setStatus(error == null ? 1 : 0);
        logEntity.setResult(error == null ? buildResult(result) : null);
        logEntity.setErrorMsg(error != null ? truncate(error.getMessage()) : null);

        if (request != null) {
            Object userId = request.getAttribute("userId");
            Object username = request.getAttribute("username");
            if (userId instanceof Long) {
                logEntity.setUserId((Long) userId);
            }
            if (username instanceof String) {
                logEntity.setUsername((String) username);
            }
        }

        logMapper.insert(logEntity);
    }

    private String resolveOperation(ProceedingJoinPoint joinPoint) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        ApiOperation apiOperation = method.getAnnotation(ApiOperation.class);
        if (apiOperation != null && apiOperation.value() != null && !apiOperation.value().isEmpty()) {
            return apiOperation.value();
        }
        return null;
    }

    private String resolveMethodName(ProceedingJoinPoint joinPoint) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        return signature.getDeclaringTypeName() + "." + signature.getName();
    }

    private String buildParams(HttpServletRequest request, Object[] args) {
        Map<String, Object> params = new LinkedHashMap<>();

        if (request != null && request.getParameterMap() != null && !request.getParameterMap().isEmpty()) {
            Map<String, Object> query = new LinkedHashMap<>();
            for (Map.Entry<String, String[]> entry : request.getParameterMap().entrySet()) {
                String key = entry.getKey();
                String[] value = entry.getValue();
                Object val = value == null ? null : (value.length == 1 ? value[0] : Arrays.asList(value));
                if (isSensitiveKey(key)) {
                    val = "***";
                }
                query.put(key, val);
            }
            params.put("query", query);
        }

        List<Object> body = sanitizeArgs(args);
        if (!body.isEmpty()) {
            params.put("body", body);
        }

        if (params.isEmpty()) {
            return null;
        }

        try {
            return truncate(objectMapper.writeValueAsString(params));
        } catch (Exception e) {
            return truncate(params.toString());
        }
    }

    private List<Object> sanitizeArgs(Object[] args) {
        if (args == null || args.length == 0) {
            return Collections.emptyList();
        }
        List<Object> list = new ArrayList<>();
        for (Object arg : args) {
            if (arg == null || isIgnorableArg(arg)) {
                continue;
            }
            list.add(sanitizeObject(arg));
        }
        return list;
    }

    private boolean isIgnorableArg(Object arg) {
        return arg instanceof HttpServletRequest
                || arg instanceof HttpServletResponse
                || arg instanceof BindingResult
                || arg instanceof MultipartFile
                || arg instanceof MultipartFile[];
    }

    private Object sanitizeObject(Object arg) {
        if (arg == null) {
            return null;
        }
        if (isSimpleValue(arg)) {
            return arg;
        }
        if (arg instanceof Map) {
            return sanitizeMap((Map<?, ?>) arg);
        }
        if (arg instanceof Collection) {
            List<Object> list = new ArrayList<>();
            for (Object item : (Collection<?>) arg) {
                list.add(sanitizeObject(item));
            }
            return list;
        }
        if (arg.getClass().isArray()) {
            int length = java.lang.reflect.Array.getLength(arg);
            List<Object> list = new ArrayList<>(length);
            for (int i = 0; i < length; i++) {
                list.add(sanitizeObject(java.lang.reflect.Array.get(arg, i)));
            }
            return list;
        }
        try {
            Map<String, Object> map = objectMapper.convertValue(arg, new TypeReference<Map<String, Object>>() {});
            return sanitizeMap(map);
        } catch (IllegalArgumentException ex) {
            return String.valueOf(arg);
        }
    }

    private Map<String, Object> sanitizeMap(Map<?, ?> map) {
        Map<String, Object> sanitized = new LinkedHashMap<>();
        if (map == null) {
            return sanitized;
        }
        for (Map.Entry<?, ?> entry : map.entrySet()) {
            String key = String.valueOf(entry.getKey());
            Object value = entry.getValue();
            if (isSensitiveKey(key)) {
                sanitized.put(key, "***");
            } else {
                sanitized.put(key, sanitizeObject(value));
            }
        }
        return sanitized;
    }

    private boolean isSensitiveKey(String key) {
        if (key == null) {
            return false;
        }
        String normalized = key.toLowerCase(Locale.ROOT);
        return SENSITIVE_KEYS.stream().anyMatch(normalized::contains);
    }

    private boolean isSimpleValue(Object arg) {
        return arg instanceof CharSequence
                || arg instanceof Number
                || arg instanceof Boolean
                || arg instanceof Enum
                || arg instanceof Date
                || arg instanceof Temporal;
    }

    private String buildResult(Object result) {
        if (result == null) {
            return null;
        }
        try {
            return truncate(objectMapper.writeValueAsString(result));
        } catch (Exception e) {
            return truncate(String.valueOf(result));
        }
    }

    private String truncate(String value) {
        if (value == null || value.length() <= MAX_CONTENT_LENGTH) {
            return value;
        }
        return value.substring(0, MAX_CONTENT_LENGTH);
    }

    private HttpServletRequest getRequest() {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        return attributes != null ? attributes.getRequest() : null;
    }

    private String resolveIp(HttpServletRequest request) {
        if (request == null) {
            return null;
        }
        String ip = request.getHeader("X-Forwarded-For");
        if (ip != null && !ip.isEmpty() && !"unknown".equalsIgnoreCase(ip)) {
            return ip.split(",")[0].trim();
        }
        ip = request.getHeader("X-Real-IP");
        if (ip != null && !ip.isEmpty() && !"unknown".equalsIgnoreCase(ip)) {
            return ip;
        }
        return request.getRemoteAddr();
    }
}
