package com.space.utils;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

/**
 * rabbitmq 配置类.
 */
@Configuration
public class RabbitMQConfig {
    //queue
    public static final String WORK_QUEUE = "space.queue";
    //exchange
    public static final String WORK_DIRECTEXCHANGE = "space.directExchange";
    //routing
    public static final String WORK_DIRECTROUTING = "space.directRouting";

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        return rabbitTemplate;
    }

    // Queue
    @Bean
    public Queue directQueue() {
        Map<String, Object> argsMap = new HashMap<String, Object>();
        argsMap.put("x-max-priority", 5);
        Queue queue = new Queue(WORK_QUEUE, true, false, false, argsMap);
        return queue;
    }

    //Direct交换机
    @Bean
    DirectExchange directExchange() {
        return new DirectExchange(WORK_DIRECTEXCHANGE, true, false);
    }

    //绑定
    @Bean
    Binding bindingDirect() {
        return BindingBuilder.bind(directQueue()).to(directExchange()).with(WORK_DIRECTROUTING);
    }
}
