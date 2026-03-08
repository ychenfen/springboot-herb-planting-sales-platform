package com.herb.platform;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 中药材种植与销售服务平台 - 主启动类
 *
 * @author System
 * @date 2025-10-29
 */
@SpringBootApplication
@MapperScan("com.herb.platform.mapper")
public class HerbPlatformApplication {

    public static void main(String[] args) {
        SpringApplication.run(HerbPlatformApplication.class, args);
        System.out.println("========================================");
        System.out.println("中药材种植与销售服务平台启动成功！");
        System.out.println("API文档地址: http://localhost:8080/api/doc.html");
        System.out.println("Druid监控: http://localhost:8080/api/druid");
        System.out.println("========================================");
    }
}
