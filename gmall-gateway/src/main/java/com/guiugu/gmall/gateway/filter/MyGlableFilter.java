package com.guiugu.gmall.gateway.filter;

import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * @author huXiuYuan
 * @Description：全局过滤器
 * @date 2024/7/24 21:41
 *
 * 使用@Order注解或者实现org.springframework.core.Ordered接口都可以指定过滤器优先级
 */
@Order(1) // 数字越小, 优先级越高
@Component
public class MyGlableFilter implements GlobalFilter {
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        System.out.println("我是全局过滤器，会拦截所有经过网关的请求!");
        // 放行
        return chain.filter(exchange);
    }
}
