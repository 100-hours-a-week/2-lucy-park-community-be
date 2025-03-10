package com.example.community.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations("file:" + System.getProperty("user.dir") + "/uploads/")
                .setCachePeriod(3600); // 캐시 기간 설정

        registry.addResourceHandler("/public/**")
                .addResourceLocations("classpath:/static/"); // 정적 리소스 추가
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOriginPatterns("*") // 모든 도메인 허용 (배포 후 수정 필요)
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS") // OPTIONS 추가
                .allowCredentials(true);
    }
}
