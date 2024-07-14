package com.atguigu.gmall.index.annotation;

import java.lang.annotation.*;

/**
 * @author huXiuYuan
 * @Description：缓存注解
 * @date 2024/7/13 0:15
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface GmallCache {

    /**
     * 缓存前缀，默认gmall:
     *
     * @return
     */
    String prefix() default "gmall:";

    /**
     * 缓存过期时间，默认60，单位：分钟
     *
     * @return
     */
    int timeout() default 60;

    /**
     * 缓存时间随机值范围，默认值10
     * 为了防止缓存雪崩，给缓存时间添加随机值
     *
     * @return
     */
    int random() default 10;

    /**
     * 分布式锁前缀，默认gmall:lock:
     * 为了防止缓存击穿，给缓存添加分布式锁
     *
     * @return
     */
    String lock() default "gmall:lock:";
}
