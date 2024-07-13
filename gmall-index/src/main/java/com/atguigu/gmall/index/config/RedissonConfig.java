package com.atguigu.gmall.index.config;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author huXiuYuan
 * @Description：Redisson配置类
 * @date 2024/7/13 16:46
 */
@Configuration
public class RedissonConfig {

    @Bean
    public RedissonClient redissonClient() {
        Config config = new Config();
        config.useSingleServer().setAddress("redis://192.168.0.101:6379").setPassword("123456");
        return Redisson.create(config);
    }
}
