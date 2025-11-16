package com.brezze.share.communication.cabinet.mq;

import com.brezze.share.communication.cabinet.service.YbtService;
import com.brezze.share.communication.oo.dto.DeviceStatusDTO;
import com.brezze.share.utils.common.constant.mq.YbtMQCst;
import com.brezze.share.utils.common.json.GsonUtil;
import com.brezze.share.utils.common.oo.ybt.req.CmdYbtREQ;
import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Slf4j
@Component
public class MQYbt {

    @Autowired
    private YbtService ybtService;

    @RabbitListener(
            bindings = @QueueBinding(
                    exchange = @Exchange(value = YbtMQCst.EXCHANGE_CABINET),
                    value = @Queue(value = YbtMQCst.QUEUE_DEVICE_STATUS, durable = "true"),
                    key = YbtMQCst.ROUTING_DEVICE_STATUS
            ), ackMode = "MANUAL"
    )
    public void deviceStatus(Channel channel, Message message, @Payload DeviceStatusDTO data) {
        try {
            log.info("消费易佰特设备状态消息,param: {}", GsonUtil.toJson(data));
            ybtService.deviceStatus(data);
            log.info("消费易佰特设备状态消息,done!");
        } catch (Exception e) {
            log.error("消费易佰特设备状态消息,抛异常：", e);
        } finally {
            // 无论成功或失败都进行消息应答，避免消息堆积而内存溢出
            try {
                channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
                log.info("消息应答：id：{}", message.getMessageProperties().getDeliveryTag());
            } catch (IOException e) {
                log.error("消息应答：id：{}，异常：{}", message.getMessageProperties().getDeliveryTag(), e);
            }
        }
    }

    /**
     * 消费易佰特机柜SN号延迟弹出消息
     *
     * @param channel
     * @param message
     * @param data
     */
    @RabbitListener(queues = YbtMQCst.QUEUE_DELAY_PB_POP, ackMode = "MANUAL")
    public void popUpPbNoDelay(Channel channel, Message message, @Payload CmdYbtREQ data) {
        try {
            log.info("消费易佰特机柜充电宝延迟弹出消息,param: {}", GsonUtil.toJson(data));
            ybtService.popUpByPbNo(data.getCabinetNo(), data.getPbNo());
            log.info("消费易佰特机柜充电宝延迟弹出消息,done!");
        } catch (Exception e) {
            log.error("消费易佰特机柜充电宝延迟弹出消息，异常：", e);
        } finally {
            // 无论成功或失败都进行消息应答，避免消息堆积而内存溢出
            try {
                channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
                log.info("消息应答：id：{}", message.getMessageProperties().getDeliveryTag());
            } catch (IOException e) {
                log.error("消息应答：id：{}，异常：{}", message.getMessageProperties().getDeliveryTag(), e);
            }
        }
    }

    /**
     * 消费易佰特机柜孔位延迟弹出消息
     *
     * @param channel
     * @param message
     * @param data
     */
    @RabbitListener(queues = YbtMQCst.QUEUE_DELAY_PB_OPEN_LOCK, ackMode = "MANUAL")
    public void popUpPosDelay(Channel channel, Message message, @Payload CmdYbtREQ data) {
        try {
            log.info("消费易佰特机柜孔位延迟弹出消息,param: {}", GsonUtil.toJson(data));
            ybtService.popUpByIndex(data.getCabinetNo(), data.getPos(), data.getIo());
            log.info("消费易佰特机柜孔位延迟弹出消息,done!");
        } catch (Exception e) {
            log.error("消费易佰特机柜孔位延迟弹出消息，异常：", e);
        } finally {
            // 无论成功或失败都进行消息应答，避免消息堆积而内存溢出
            try {
                channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
                log.info("消息应答：id：{}", message.getMessageProperties().getDeliveryTag());
            } catch (IOException e) {
                log.error("消息应答：id：{}，异常：{}", message.getMessageProperties().getDeliveryTag(), e);
            }
        }
    }
}
