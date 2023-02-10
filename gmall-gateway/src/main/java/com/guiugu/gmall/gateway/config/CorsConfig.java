package com.guiugu.gmall.gateway.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsWebFilter;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;

/**
 * @Title CorsConfig
 * @Author HuXiuYuan
 * @Date 2021/11/15 22:55
 */
@Configuration
public class CorsConfig {

    @Bean
    public CorsWebFilter corsWebFilter() {
        CorsConfiguration configuration = new CorsConfiguration();
        // 允许哪些域名跨域访问：*-代表允许所有域名跨域访问，但是不能携带cookie
        configuration.addAllowedOrigin("http://manager.gmall.com");
        configuration.addAllowedOrigin("http://localhost:1000");
        // 是否允许携带cookie
        configuration.setAllowCredentials(true);
        // 允许所有请求方法跨域访问 get post 等
        configuration.addAllowedMethod("*");
        // 允许携带任意头信息
        configuration.addAllowedHeader("*");

        UrlBasedCorsConfigurationSource configurationSource = new UrlBasedCorsConfigurationSource();
        configurationSource.registerCorsConfiguration("/**",configuration);
        return new CorsWebFilter(configurationSource);
    }
}
