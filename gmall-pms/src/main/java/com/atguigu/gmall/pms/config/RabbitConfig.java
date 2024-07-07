package com.atguigu.gmall.pms.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;

/**
 * @author huXiuYuan
 * @Description：RabbitMq生产者确认消息配置
 * @date 2024/7/6 21:21
 *
 * Configuration注解：在Spring容器启动后，会调用RabbitConfig的无参构造方法，初始化RabbitConfig对象
 */
@Slf4j
@Configuration
public class RabbitConfig {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    /**
     * PostConstruct注解：在构造方法执行之后执行
     * 所以在Spring容器启动后，会初始化RabbitConfig对象，进而执行 init 方法设置 RabbitTemplate 的 消息到达交换机 和 消息到达队列 的 回调
     */
    @PostConstruct
    public void init() {
        // 确认消息是否到达交换机 无论消息是否到达都会触发
        this.rabbitTemplate.setConfirmCallback((correlationData, ack, cause) -> {
            if (!ack) {
                log.info("消息没有到达交换机，原因：{}", cause);
            }
        });
        // 确认消息是否到达队列 只有消息没有到达队列才会触发
        this.rabbitTemplate.setReturnCallback((message, replyCode, replyText, exchange, routingKey) -> {
            log.error("消息没有到达队列，交换机：{}，路由键：{}，消息内容：{}，回调状态码：{}，回调文本：{}", exchange, routingKey, new String(message.getBody()), replyCode, replyText);
        });
    }
}
