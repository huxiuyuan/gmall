package com.guiugu.gmall.gateway.filter;

import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * @author huXiuYuan
 * @Description：测试局部过滤器
 * @date 2024/7/24 21:58
 *
 * 局部过滤器可以实现GatewayFilterFactory接口或者继承AbstractGatewayFilterFactory抽象类
 * 局部过滤器类名必须以GatewayFilterFactory结尾
 *  使用时
 *      - id: index-route
 *       uri: lb://index-service
 *       predicates:
 *         - Host=gmall.com,www.gmall.com
 *       filters:
 *         - AddResponseHeader=xxx, yyy
 *         - Test
 */
@Component
public class TestGatewayFilterFactory extends AbstractGatewayFilterFactory<Object> {
    @Override
    public GatewayFilter apply(Object config) {
        return new GatewayFilter() {
            @Override
            public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
                System.out.println("我是局部过滤器，只能拦截经过特定路由的请求!");
                // 放行
                return chain.filter(exchange);
            }
        };
    }
}
