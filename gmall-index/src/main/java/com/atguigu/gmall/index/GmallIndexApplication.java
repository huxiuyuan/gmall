package com.atguigu.gmall.index;

import com.atguigu.gmall.index.utils.ScheduledExecutorUtil;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@EnableFeignClients
@SpringBootApplication
public class GmallIndexApplication {

    public static void main(String[] args) {
        SpringApplication.run(GmallIndexApplication.class, args);

        // 添加一个关闭钩子来确保线程池在JVM关闭时被关闭
        Runtime.getRuntime().addShutdownHook(new Thread(ScheduledExecutorUtil::shutdown));
    }

}
