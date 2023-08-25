package com.space;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.PostConstruct;


@EnableRabbit
@SpringBootTest
@RunWith(SpringRunner.class)
public class TestRabbit {

    @Autowired
    private RabbitAdmin rabbitAdmin;


    /**
     * rabbitAdmin来添加队列和交换机，以及队列和交换机的绑定
     */
    @Test
    public void testAdmin() {
        rabbitAdmin.declareExchange(new TopicExchange("test.topic.exchange", false, false, null));
        rabbitAdmin.declareExchange(new DirectExchange("test.direct.exchange", false, false, null));
        rabbitAdmin.declareExchange(new FanoutExchange("test.fanout.exchange", false, false, null));

        rabbitAdmin.declareQueue(new Queue("topic.queue", false, false, false, null));
        rabbitAdmin.declareQueue(new Queue("direct.queue", false, false, false, null));
        rabbitAdmin.declareQueue(new Queue("fanout.queue", false, false, false, null));

        // 将一个queue绑定到一个exchange
        rabbitAdmin.declareBinding(new Binding(
                "direct.queue",  // 目标：队列名
                Binding.DestinationType.QUEUE,  // 绑定目标类型：队列
                "test.direct.exchange",  // 交换机名称
                "direct.key",  // 路由key
                null  // 扩展参数
        ));

        // 将一个交换机绑定到另一个交换机(消息流转topic.exchange->fanout.exchange)
        rabbitAdmin.declareBinding(new Binding(
                "test.fanout.exchange",  // 目标：交换机名
                Binding.DestinationType.EXCHANGE,  // 绑定目标类型：交换机
                "test.topic.exchange", // 发起绑定的交换机
                "test", //路由key
                null
        ));

        // fanout.queue绑定到test.fanout.exchange
        rabbitAdmin.declareBinding(new Binding(
                "fanout.queue",  // 目标：fanout.queue
                Binding.DestinationType.QUEUE,  // 绑定类型:队列
                "test.fanout.exchange",  // 绑定到的exchange
                "",  // 应为是fanout类型exchange所以不需要routingKey
                null));

        //发送消息
        //正常的消息流转 从test.direct.exchange-》direct.queue
        rabbitAdmin.getRabbitTemplate().convertAndSend("test.direct.exchange", "direct.key", "直连交换机消息111");
        //消息先到test.topic.exchange-》test.fanout.exchange-》fanout.queue
        rabbitAdmin.getRabbitTemplate().convertAndSend("test.topic.exchange", "test", "多级流转消息2222");
    }

    /**
     * rabbitTemplate 发送不同的消息
     */
    @Test
    public void testTempMsg() {
        // 1、第一种方式使用admire来进行发送消息
        rabbitAdmin.getRabbitTemplate().convertAndSend("xjh", "xjh-queue", "直连交换机消息222");
        // 2、第二种方式使用template
        // rabbitTemplate.convertAndSend("xjh","xjh-queue","好无聊啊");
    }

    @Test
    @PostConstruct  //当RabbitConfiguration这个配置类加载完成之后
    public void initrabbitTemple() {
        RabbitTemplate rabbitTemplate = rabbitAdmin.getRabbitTemplate();


        rabbitTemplate.setConfirmCallback(new RabbitTemplate.ConfirmCallback() {
            //  CorrelationData correlationData,消息队列的唯一ID
            // boolean b,是否接受成功
            // String s，失败的原因
            @Override
            public void confirm(CorrelationData correlationData, boolean b, String s) {
                System.out.println("correlationData===>" + correlationData + "===>是否接受成功" + b + "=====>失败的原因" + s);
            }
        });
    }
}
