package com.space.utils;


import org.springframework.amqp.AmqpException;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * rabbitmq 帮助类.
 */
@Component
public class RabbitMQUtils {

    private static RabbitTemplate rabbitTemplate;

    @Autowired
    public RabbitMQUtils(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    /**
     * 发送消息
     *
     * @param msg
     * @throws AmqpException
     */
    public static void sendMsg(String msg) throws AmqpException {
        try {
            rabbitTemplate.convertAndSend(RabbitMQConfig.WORK_DIRECTEXCHANGE, RabbitMQConfig.WORK_DIRECTROUTING, msg);
        } catch (AmqpException e) {
            throw new AmqpException("RabbitMQ发送消息异常", e);
        }
    }
}
