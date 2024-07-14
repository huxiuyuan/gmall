package com.atguigu.gmall.index.aspect;

import com.alibaba.fastjson.JSON;
import com.atguigu.gmall.index.annotation.GmallCache;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.aspectj.lang.reflect.MethodSignature;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.Random;
import java.util.concurrent.TimeUnit;

/**
 * @author huXiuYuan
 * @Description：AOP切面类，对使用GmallCache注解方法进行增强
 * @date 2024/7/13 20:22
 * <p>
 * 使用 自定义注解 + AOP 封装缓存注解
 */
@Aspect
@Component
public class GmallCacheAspect {

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Autowired
    private RedissonClient redissonClient;

    /**
     * 环绕通知
     * 1.方法必须返回Object
     * 2.方法必须要有ProceedingJoinPoint参数
     * 3.方法必须抛出Throwable异常
     * 4.必须手动执行目标方法
     *
     * 获取目标方法参数：joinPoint.getArgs()
     * 获取目标对象：joinPoint.getTarget()
     * 获取目标方法对象：MethodSignature signature = (MethodSignature) joinPoint.getSignature();
     *                Method method = signature.getMethod();
     *
     * @param joinPoint
     * @return
     * @throws Throwable
     */
    @Around("@annotation(com.atguigu.gmall.index.annotation.GmallCache)")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
        // 获取目标方法的签名
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        // 获取目标方法对象
        Method method = signature.getMethod();
        // 获取目标方法指定注解
        GmallCache gmallCache = method.getAnnotation(GmallCache.class);
        // 拼接参数列表
        String argsJoin = StringUtils.join(joinPoint.getArgs(), ",");

        // 1.先查询缓存，如果缓存命中则直接返回
        // 拼接缓存的key
        String key = gmallCache.prefix() + argsJoin;
        String json = this.redisTemplate.opsForValue().get(key);
        if (StringUtils.isNotBlank(json)) {
            return JSON.parseObject(json, method.getReturnType());
        }

        // 2.添加分布式锁，防止缓存击穿
        // 拼接分布式锁的key
        String lock = gmallCache.lock() + argsJoin;
        RLock fairLock = this.redissonClient.getFairLock(lock);
        fairLock.lock();
        Object proceed;

        try {
            // 3.再次查询缓存，因为获取锁的过程中，可能有其他请求将数据放入缓存中
            json = this.redisTemplate.opsForValue().get(key);
            if (StringUtils.isNotBlank(json)) {
                return JSON.parseObject(json, method.getReturnType());
            }

            // 4.执行目标方法
            proceed = joinPoint.proceed(joinPoint.getArgs());

            // 5.将目标执行执行结果放入缓存(缓存雪崩)
            // 根据过期时间+过期时间随机值生成一个随机的过期时间
            int timeout = gmallCache.timeout() + new Random().nextInt(gmallCache.random());
            this.redisTemplate.opsForValue().set(key, JSON.toJSONString(proceed), timeout, TimeUnit.MINUTES);
        } finally {
            // 6.释放分布式锁
            fairLock.unlock();
        }

        return proceed;
    }

    @Pointcut("execution(* com.atguigu.gmall.index.service.impl.*.*(..))")
    public void pointcut() {
    }

    /**
     * 前置通知
     */
    @Before("pointcut()")
    public void before() {
        System.out.println("2前置通知");
    }

    /**
     * 返回后通知
     */
    @AfterReturning("pointcut()")
    public void afterReturning() {
        System.out.println("3返回后通知");
    }

    /**
     * 返回后通知
     */
    @AfterThrowing("pointcut()")
    public void afterThrowing() {
        System.out.println("异常后通知");
    }

    /**
     * 最终通知
     */
    @After("pointcut()")
    public void after() {
        System.out.println("4最终通知");
    }
}
