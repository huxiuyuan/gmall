package com.atguigu.gmall.index;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;

@SpringBootTest
class GmallIndexApplicationTests {

    @Autowired
    private RedisTemplate redisTemplate; // 默认使用jdk提供的二进制序列化方式，难以调试

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Test
    void contextLoads() {
        this.stringRedisTemplate.opsForValue().set("name", "柳岩");
        System.out.println(this.stringRedisTemplate.opsForValue().get("name"));
    }

}
