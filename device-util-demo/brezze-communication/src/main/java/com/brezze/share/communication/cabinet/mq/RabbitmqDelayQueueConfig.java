package com.brezze.share.communication.cabinet.mq;

import com.brezze.share.utils.common.constant.mq.YbtMQCst;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.CustomExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class RabbitmqDelayQueueConfig {

    @Bean
    public CustomExchange delayExchange() {
        Map<String, Object> args = new HashMap<>();
        args.put("x-delayed-type", "direct");
        return new CustomExchange(YbtMQCst.EXCHANGE_DELAY, "x-delayed-message", true, false, args);
    }

    @Bean
    public Queue delayQueueYbtPopUpPbNo() {
        return new Queue(YbtMQCst.QUEUE_DELAY_PB_POP, true);
    }

    @Bean
    public Binding bindingDelayYbtPopUpPbNo() {
        return BindingBuilder.bind(delayQueueYbtPopUpPbNo()).to(delayExchange()).with(YbtMQCst.ROUTING_DELAY_PB_POP).noargs();
    }

    @Bean
    public Queue delayQueueYbtPopUpPos() {
        return new Queue(YbtMQCst.QUEUE_DELAY_PB_OPEN_LOCK, true);
    }

    @Bean
    public Binding bindingDelayYbtPopUpPos() {
        return BindingBuilder.bind(delayQueueYbtPopUpPos()).to(delayExchange()).with(YbtMQCst.ROUTING_DELAY_PB_OPEN_LOCK).noargs();
    }
}
