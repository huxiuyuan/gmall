package com.guiugu.gmall.gateway.filter;

import com.atguigu.gmall.common.utils.IpUtils;
import com.atguigu.gmall.common.utils.JwtUtils;
import com.guiugu.gmall.gateway.config.JwtProperties;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpCookie;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.MultiValueMap;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * @author huXiuYuan
 * @Description：身份认证局部过滤器
 * @date 2024/7/25 20:09
 */
@EnableConfigurationProperties(JwtProperties.class)
@Component
public class AuthGatewayFilterFactory extends AbstractGatewayFilterFactory<AuthGatewayFilterFactory.PathConfig> {

    @Autowired
    private JwtProperties properties;

    /**
     * 重写无参构造方法，调用父类的有参构造方法
     */
    public AuthGatewayFilterFactory() {
        super(PathConfig.class);
    }

    @Override
    public GatewayFilter apply(PathConfig config) {
        return new GatewayFilter() {
            @Override
            public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
                // ServerHttpRequest 等价于 HttpServletRequest
                ServerHttpRequest request = exchange.getRequest();
                ServerHttpResponse response = exchange.getResponse();

                // 1.判断当前请求是否在拦截名单中，如果不在直接放行
                //   拦截名单
                List<String> paths = config.paths;
                //   当前请求路径
                String currentPath = request.getURI().getPath();
                //   如果拦截名单不为空 并且 不以拦截名单开头的路径 则直接放行
                if (!CollectionUtils.isEmpty(paths) && paths.stream().noneMatch(path -> StringUtils.startsWith(currentPath, path))) {
                    return chain.filter(exchange);
                }

                // 2.从cookie(同步请求)或者头信息(异步请求)中获取token信息，只要任何一个里面有，都认为有
                String token = request.getHeaders().getFirst("token");
                //   如果头信息中没有携带，则尝试从cookie中获取
                if (StringUtils.isBlank(token)) {
                    MultiValueMap<String, HttpCookie> cookies = request.getCookies();
                    if (!CollectionUtils.isEmpty(cookies) && cookies.containsKey(properties.getCookieName())) {
                        HttpCookie httpCookie = cookies.getFirst(properties.getCookieName());
                        if (httpCookie != null) {
                            token = httpCookie.getValue();
                        }
                    }
                }

                // 3.对token判空，如果依然为空，重定向到登录页面，请求结束
                if (StringUtils.isBlank(token)) {
                    response.setStatusCode(HttpStatus.SEE_OTHER);
                    // 设置重定向地址为登录页面，returnUrl为当前请求地址
                    response.getHeaders().add(HttpHeaders.LOCATION, "http://sso.gmall.com/toLogin.html?returnUrl=" + request.getURI());
                    return response.setComplete();
                }

                // 4.解析token信息，如果出现异常(过期/伪造)，重定向到登录页面，请求结束
                try {
                    Map<String, Object> map = JwtUtils.getInfoFromToken(token, properties.getPublicKey());
                    // 5.获取载荷中的ip地址，判断是否和当前请求的ip地址一致，如果不一致则重定向到登录页面，请求结束
                    //   载荷中的IP地址
                    String ip = map.get("ip").toString();
                    //   当前请求IP地址
                    String currentIp = IpUtils.getIpAddressAtGateway(request);
                    if (!StringUtils.equals(ip, currentIp)) {
                        throw new RuntimeException("授权IP和请求IP不一致!");
                    }

                    // 6.把解析出的用户信息传递给后续服务
                    request.mutate().header("userId", map.get("userId").toString())
                            .header("userName", map.get("userName").toString()).build();
                    exchange.mutate().request(request).build();

                    // 7.放行
                    return chain.filter(exchange);
                } catch (Exception e) {
                    response.setStatusCode(HttpStatus.SEE_OTHER);
                    // 设置重定向地址为登录页面，returnUrl为当前请求地址
                    response.getHeaders().add(HttpHeaders.LOCATION, "http://sso.gmall.com/toLogin.html?returnUrl=" + request.getURI());
                    return response.setComplete();
                }
            }
        };
    }

    /**
     * 重写shortcutFieldOrder方法，指定接收字段类型
     * @return
     */
    @Override
    public ShortcutType shortcutType() {
        return ShortcutType.GATHER_LIST;
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
     * 自定义静态内部类，并定义接收参数的字段
     */
    @Data
    public static class PathConfig {
        private List<String> paths;
    }
}
