package com.atguigu.gmall.index;

import org.junit.jupiter.api.Test;
import org.redisson.api.RBloomFilter;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;

@SpringBootTest
class GmallIndexApplicationTests {

    @Autowired
    private RedisTemplate redisTemplate; // RedisTemplate默认使用jdk提供的二进制序列化方式，难以调试

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    private RedissonClient redissonClient;

    @Test
    void contextLoads() {
        RBloomFilter<Object> bf = this.redissonClient.getBloomFilter("bf");
        // 期望插入元素数量 | 误判率
        bf.tryInit(20, 0.3);
        bf.add("1");
        bf.add("2");
        bf.add("3");
        bf.add("4");
        bf.add("5");
        bf.add("6");
        bf.add("7");
        System.out.println(bf.contains("1"));
        System.out.println(bf.contains("3"));
        System.out.println(bf.contains("5"));
        System.out.println(bf.contains("7"));
        System.out.println(bf.contains("8"));
        System.out.println(bf.contains("9"));
        System.out.println(bf.contains("10"));
        System.out.println(bf.contains("11"));
        System.out.println(bf.contains("12"));
        System.out.println(bf.contains("13"));
        System.out.println(bf.contains("14"));
        System.out.println(bf.contains("15"));
        System.out.println(bf.contains("16"));
        System.out.println(bf.contains("17"));
        System.out.println(bf.contains("18"));
        System.out.println(bf.contains("19"));
    }

}
