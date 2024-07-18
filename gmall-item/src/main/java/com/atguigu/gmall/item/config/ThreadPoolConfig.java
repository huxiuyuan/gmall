package com.atguigu.gmall.item.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.*;

/**
 * @author huXiuYuan
 * @Description：线程池配置
 * @date 2024/7/17 21:45
 */
@Configuration
public class ThreadPoolConfig {

    @Bean
    public ExecutorService executorService(
            // 核心线程数
            @Value("${threadPool.corePoolSize}") Integer corePoolSize,
            // 最大线程数
            @Value("${threadPool.maximumPoolSize}") Integer maximumPoolSize,
            // 线程空闲时存活时间
            @Value("${threadPool.keepAliveTime}") Integer keepAliveTime,
            // 阻塞队列大小
            @Value("${threadPool.blockingQueueSize}") Integer blockingQueueSize
    ) {
        return new ThreadPoolExecutor(corePoolSize,
                maximumPoolSize,
                keepAliveTime,
                TimeUnit.SECONDS,
                new ArrayBlockingQueue<>(blockingQueueSize),
                // 线程工程
                Executors.defaultThreadFactory(),
                // 拒绝策略 当线程池中的任务数量超过其最大限制时，这些策略决定了如何处理额外的任务请求。
                // AbortPolicy 当提交一个新的任务到一个已满的队列时，如果使用此策略，将会抛出一个RejectedExecutionException异常，并且当前线程将被中断。
                // CallerRunsPolicy 如果线程池无法接受新的任务，那么这个策略会让调用者所在的线程执行这个任务。
                // DiscardOldestPolicy 当任务队列已满并且无法接受新任务时，此策略会从队列中移除最旧的任务（即最早添加的任务），然后尝试再次将新任务添加到队列中。
                // DiscardPolicy 当任务队列已满时，它会默默地丢弃新提交的任务，不抛出任何异常，也不通知调用者。
                new ThreadPoolExecutor.AbortPolicy());
    }
}
