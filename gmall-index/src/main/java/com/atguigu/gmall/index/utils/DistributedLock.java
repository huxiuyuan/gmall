package com.atguigu.gmall.index.utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

/**
 * @author huXiuYuan
 * @Description：分布式锁
 * @date 2024/7/13 0:15
 *
 * 由于spring中的bean是单例的(@scoop默认是singleton)，所以不同的请求获取不同的锁，后面的会把前面的scheduledFuture覆盖掉
 * 所以使用@Scope("prototype")，每次获取Bean时，Spring都会创建一个新的实例。
 */
@Scope("prototype")
@Component
public class DistributedLock {

    @Autowired
    private StringRedisTemplate redisTemplate;

    private ScheduledFuture<?> scheduledFuture;

    /**
     * 加锁
     *
     * @param lockName 锁的名称
     * @param uuid     UUID
     * @param expire   过期时间(秒)
     * @return 加锁结果
     */
    public Boolean tryLock(String lockName, String uuid, Integer expire) {
        String script = "if redis.call('exists', KEYS[1]) == 0 or redis.call('hexists', KEYS[1], ARGV[1]) == 1 " +
                "then " +
                "   redis.call('hincrby', KEYS[1], ARGV[1], 1) " +
                "   redis.call('expire', KEYS[1], ARGV[2]) " +
                "   return 1 " +
                "else " +
                "   return 0 end";
        Boolean flag = this.redisTemplate.execute(new DefaultRedisScript<>(script, Boolean.class), Arrays.asList(lockName), uuid, expire.toString());
        if (Boolean.FALSE.equals(flag)) {
            // 如果没有获取到锁，递归重试
            try {
                Thread.sleep(50);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            tryLock(lockName, uuid, expire);
        } else {
            // 如果获取到锁，开启一个定时任务，重置过期时间
            this.renewExpire(lockName, uuid, expire);
        }
        return true;
    }

    /**
     * 释放锁
     *
     * @param lockName 锁的名称
     * @param uuid     UUID
     */
    public void unLock(String lockName, String uuid) {
        String script = "if redis.call('hexists', KEYS[1], ARGV[1]) == 0 " +
                "   then " +
                "   return nil " +
                "elseif redis.call('hincrby', KEYS[1], ARGV[1], -1) == 0 " +
                "   then " +
                "   return redis.call('del', KEYS[1]) " +
                "else " +
                "   return 0 " +
                "end";
        // 由于底层原因nil 无法直接转换为 Integer，会产生java.lang.IllegalStateException: null，所以使用Long接收redis的返回值
        Long flag = this.redisTemplate.execute(new DefaultRedisScript<>(script, Long.class), Arrays.asList(lockName), uuid);
        if (flag == null) {
            // 如果锁不存在或者锁不属于当前县城
            throw new RuntimeException("要释放的锁不存在或者不属于当前线程!");
        } else if (flag == 1) {
            // 如果锁删除成功, 取消这个定时任务
            ScheduledExecutorUtil.cancelScheduledFuture(this.scheduledFuture);
        }
    }

    /**
     * 重置过期时间
     *
     * @param lockName 锁的名称
     * @param uuid     UUID
     * @param expire   过期时间(秒)
     */
    public void renewExpire(String lockName, String uuid, Integer expire) {
        String script = "if redis.call('hexists', KEYS[1], ARGV[1]) == 1 " +
                "   then " +
                "   return redis.call('expire', KEYS[1], ARGV[2]) " +
                "else " +
                "   return 0 " +
                "end";
        // 定时器: 每隔 expire / 3 秒执行一次
        this.scheduledFuture = ScheduledExecutorUtil.scheduleAtFixedRate(() -> redisTemplate.execute(new DefaultRedisScript<>(script, Boolean.class), Arrays.asList(lockName), uuid, expire.toString()), expire / 3, expire / 3, TimeUnit.SECONDS);
    }
}
