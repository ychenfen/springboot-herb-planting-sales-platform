package com.herb.platform.config;

import com.herb.platform.interceptor.AccessInterceptor;
import com.herb.platform.interceptor.JwtInterceptor;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

/**
 * Web MVC配置类
 */
@Configuration
@RequiredArgsConstructor
public class WebMvcConfig implements WebMvcConfigurer {

    private final JwtInterceptor jwtInterceptor;
    private final AccessInterceptor accessInterceptor;

    @Value("${herb.file.access-path:/uploads}")
    private String accessPath;

    @Value("${herb.file.storage-path:./uploads}")
    private String storagePath;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        String accessPattern = normalizeAccessPath(accessPath) + "/**";
        registry.addInterceptor(jwtInterceptor)
                .addPathPatterns("/**")
                .excludePathPatterns(
                        // 登录注册相关
                        "/auth/login",
                        "/auth/register",
                        // 公开接口
                        "/public/**",
                        // 溯源查询（公开）
                        "/trace/query/**",
                        "/trace/public/**",
                        // 静态资源
                        "/static/**",
                        accessPattern,
                        // Swagger相关
                        "/doc.html",
                        "/swagger-resources/**",
                        "/webjars/**",
                        "/v3/api-docs/**",
                        "/swagger-ui/**",
                        // Druid监控
                        "/druid/**",
                        // 错误页面
                        "/error"
                );
        registry.addInterceptor(accessInterceptor)
                .addPathPatterns("/**");
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        String normalizedAccessPath = normalizeAccessPath(accessPath);
        // 配置静态资源映射
        registry.addResourceHandler("/static/**")
                .addResourceLocations("classpath:/static/");

        // 配置上传文件访问路径
        registry.addResourceHandler(normalizedAccessPath + "/**")
                .addResourceLocations(toFileUri(storagePath));

        // Swagger相关资源
        registry.addResourceHandler("/doc.html")
                .addResourceLocations("classpath:/META-INF/resources/");
        registry.addResourceHandler("/webjars/**")
                .addResourceLocations("classpath:/META-INF/resources/webjars/");
    }

    @Override
    public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
        // 配置字符串转换器使用UTF-8
        StringHttpMessageConverter stringConverter = new StringHttpMessageConverter(StandardCharsets.UTF_8);
        converters.add(0, stringConverter);

        // 配置JSON转换器使用UTF-8
        MappingJackson2HttpMessageConverter jsonConverter = new MappingJackson2HttpMessageConverter();
        jsonConverter.setDefaultCharset(StandardCharsets.UTF_8);
        converters.add(1, jsonConverter);
    }

    private String normalizeAccessPath(String path) {
        if (path == null || path.trim().isEmpty()) {
            return "/uploads";
        }
        String normalized = path.trim();
        if (!normalized.startsWith("/")) {
            normalized = "/" + normalized;
        }
        if (normalized.endsWith("/")) {
            normalized = normalized.substring(0, normalized.length() - 1);
        }
        return normalized;
    }

    private String toFileUri(String path) {
        Path resolved = Paths.get(path).toAbsolutePath().normalize();
        String uri = resolved.toUri().toString();
        return uri.endsWith("/") ? uri : uri + "/";
    }
}
