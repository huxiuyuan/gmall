package com.atguigu.gmall.index.utils;

import com.google.common.util.concurrent.ThreadFactoryBuilder;

import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

/**
 * @author huXiuYuan
 * @Description：
 * @date 2024/7/13 14:17
 */
public class ScheduledExecutorUtil {

    private static final ScheduledThreadPoolExecutor EXECUTOR;

    static {
        // 创建线程工厂
        ThreadFactory threadFactory = new ThreadFactoryBuilder()
                .setNameFormat("scheduled-pool-%d")
                .build();

        // 创建ScheduledThreadPoolExecutor
        EXECUTOR = new ScheduledThreadPoolExecutor(2, threadFactory);
    }

    /**
     * 调度一个任务定期执行。
     *
     * @param command 要执行的任务
     * @param initialDelay 初始延迟时间
     * @param period 执行间隔
     * @param unit 时间单位
     * @return ScheduledFuture 用于取消任务
     */
    public static ScheduledFuture<?> scheduleAtFixedRate(Runnable command, long initialDelay, long period, TimeUnit unit) {
        return EXECUTOR.scheduleAtFixedRate(command, initialDelay, period, unit);
    }

    /**
     * 取消一个已调度的任务。
     *
     * @param scheduledFuture 要取消的任务
     */
    public static void cancelScheduledFuture(ScheduledFuture<?> scheduledFuture) {
        scheduledFuture.cancel(false);
    }

    /**
     * 关闭线程池，应在应用程序退出时调用。
     */
    public static void shutdown() {
        EXECUTOR.shutdown();
    }
}
