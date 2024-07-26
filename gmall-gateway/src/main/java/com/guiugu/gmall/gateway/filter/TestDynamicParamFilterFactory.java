package com.guiugu.gmall.gateway.filter;

import lombok.Data;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.List;

/**
 * @author huXiuYuan
 * @Description：不固定参数局部过滤器
 * @date 2024/7/25 20:01
 */
//@Component
public class TestDynamicParamFilterFactory extends AbstractGatewayFilterFactory<TestDynamicParamFilterFactory.KeyValueConfig> {

    /**
     * 重写无参构造方法，调用父类的有参构造方法
     */
    public TestDynamicParamFilterFactory() {
        super(TestDynamicParamFilterFactory.KeyValueConfig.class);
    }

    /**
     * 重写shortcutFieldOrder方法，指定接收字段顺序
     * @return
     */
    @Override
    public List<String> shortcutFieldOrder() {
        return Arrays.asList("paths");
    }

    /**
     * 重写shortcutFieldOrder方法，指定接收字段类型
     * @return
     */
    @Override
    public ShortcutType shortcutType() {
        return ShortcutType.GATHER_LIST;
    }

    @Override
    public GatewayFilter apply(TestDynamicParamFilterFactory.KeyValueConfig config) {
        return new GatewayFilter() {
            @Override
            public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
                System.out.println("我是局部过滤器，只能拦截经过特定路由的请求! paths = " + config.paths);
                // 放行
                return chain.filter(exchange);
            }
        };
    }

    /**
     * 自定义静态内部类，并定义接收参数的字段
     */
    @Data
    public static class KeyValueConfig {
        private List<String> paths;
    }
}