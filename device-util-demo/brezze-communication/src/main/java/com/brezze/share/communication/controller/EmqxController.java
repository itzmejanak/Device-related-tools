package com.brezze.share.communication.controller;

import cn.hutool.core.util.ArrayUtil;
import cn.hutool.json.JSONUtil;
import com.brezze.share.communication.oo.dto.DeviceStatusDTO;
import com.brezze.share.communication.oo.emqx.EMQXEvent;
import com.brezze.share.communication.oo.emqx.EMQXMessage;
import com.brezze.share.communication.utils.EmqxUtil;
import com.brezze.share.utils.common.constant.RedisKeyCst;
import com.brezze.share.utils.common.constant.TimeCst;
import com.brezze.share.utils.common.constant.mq.YbtMQCst;
import com.brezze.share.utils.common.date.DateUtil;
import com.brezze.share.utils.common.json.GsonUtil;
import com.brezze.share.utils.common.oo.ybt.message.*;
import com.brezze.share.utils.common.oo.ybt.serialport.SerialPortData;
import com.brezze.share.utils.common.redis.RedisUtil;
import com.brezze.share.utils.common.string.ByteUtils;
import com.github.xiaoymin.knife4j.annotations.ApiSupport;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

@ApiSupport(order = 10)
@Slf4j
@RestController
public class EmqxController {

    @Autowired
    private RedisUtil redisUtil;
    
    @Autowired
    private com.brezze.share.communication.config.ProductConfig productConfig;

    @PostMapping(value = "/api/emqx/event")
    public void event(@RequestBody String body) {
        EMQXEvent event = JSONUtil.toBean(body, EMQXEvent.class);
        // 过滤非上线、下线消息
        if (!ArrayUtil.containsAny(new String[]{"client.disconnected", "client.connected"}, event.getEvent())) {
            return;
        }
        String status = "";
        switch (event.getEvent()) {
            case "client.connected":
                status = "online";
                break;
            case "client.disconnected":
                status = "offline";
                break;
        }
        DeviceStatusDTO deviceStatusDTO = new DeviceStatusDTO();
        LocalDateTime lastTime = DateUtil.longToLocalDateTime(event.getTimestamp());
        LocalDateTime utcTime = DateUtil.conversionTimezone(lastTime, "UTC");
        String lastTimeStr = DateUtil.format(lastTime, DateUtil.FORMAT_PATTERN8);
        String utcTimeStr = DateUtil.format(utcTime, DateUtil.RFC3339_TIME_PATTERN2);
        deviceStatusDTO.setLastTime(lastTimeStr);
        deviceStatusDTO.setUtcLastTime(utcTimeStr);
        deviceStatusDTO.setClientIp(event.getPeername());
        deviceStatusDTO.setUtcTime(utcTimeStr);
        deviceStatusDTO.setTime(lastTimeStr);
        deviceStatusDTO.setProductKey(productConfig.getProductKey());
        deviceStatusDTO.setDeviceName(event.getClientid());
        deviceStatusDTO.setStatus(status);
        log.info("\n--------------------->YBT机柜-[{}],设备状态: {}", deviceStatusDTO.getDeviceName(), GsonUtil.toJson(deviceStatusDTO));
        // 发送设备状态变更mq
        sendMQ(deviceStatusDTO, YbtMQCst.ROUTING_DEVICE_STATUS);
    }

    @PostMapping(value = "/api/emqx/message")
    public void message(@RequestBody String data) {
        EMQXMessage message = JSONUtil.toBean(data, EMQXMessage.class);

        // 过滤非消息投递事件
        if (!"message.publish".equals(message.getEvent())) {
            return;
        }
        // 过滤非消息投递事件
        if (message.getUsername() == null || "undefined".equalsIgnoreCase(message.getUsername())) {
            return;
        }
        try {
            byte[] body = Base64.decodeBase64(message.getPayload());
            String topic = message.getTopic();
            String messageId = message.getId();
            String content;
            content = ByteUtils.to16Hexs(body);
            log.info("\nreceive message: "
                    + "\n   topic = " + topic
                    + "\n   messageId = " + messageId
                    + "\n   content = " + content);
            int cmd = SerialPortData.checkCMD(body);
            String deviceName = getDeviceName(topic);
            switch (cmd) {
                case 0x10:
                    //check  check_all
                    ReceiveUpload receiveUpload = new ReceiveUpload(body);
                    log.info("\n--------------------->YBT机柜-[{}],设备心跳上报: {}", deviceName, GsonUtil.toJson(receiveUpload));

                    //缓存详情数据-测试工具使用
                    log.info("Message body:{}", body);
                    if (null != redisUtil) {
                        redisUtil.set(RedisKeyCst.CABINET_MESSAGE_CACHE_CHECK + deviceName, ByteUtils.to16Hexs(body), TimeCst.TIME_SECOND_1DAYS);
                    }
                    break;
                case 0x21:
                    //popup by position
                    ReceivePopupIndex receivePopupIndex = new ReceivePopupIndex(body);
                    log.info("\n--------------------->YBT机柜-[{}],孔位弹出: {}", deviceName, GsonUtil.toJson(receivePopupIndex));
                    //Processing business based on status

                    break;
                case 0x31:
                    //popup by sn
                    ReceivePopupSN receivePopupSN = new ReceivePopupSN(body);
                    log.info("\n--------------------->YBT机柜-[{}],弹出SN: {}", deviceName, GsonUtil.toJson(receivePopupSN));
                    //Processing business based on status

                    break;
                case 0X40:
                    //Power bank returned
                    ReceiveReturn receiveReturn = new ReceiveReturn(body);
                    log.info("\n--------------------->YBT机柜-[{}],充电宝归还: {}", deviceName, GsonUtil.toJson(receiveReturn));
                    if (receiveReturn.getStatus() == 1 || receiveReturn.getStatus() == 3) {
                        //Processing business based on status
                    }
                    break;
                case 0X7A:
                    //MQTT心跳上报
                    log.info("\n--------------------->YBT机柜-[{}],MQTT心跳上报: {}", deviceName, content);
                    break;
                case 0xB1:
                    //检查版本
                    log.info("\n--------------------->YBT机柜-[{}],检查版本: {}", deviceName, content);
                    break;
                case 0x7D:
                    //PING网络延迟
                    log.info("\n--------------------->YBT机柜-[{}],PING网络延迟: {}", deviceName, content);
                    break;
                case 0x34:
                    //获取APN信息
                    log.info("\n--------------------->YBT机柜-[{}],获取APN信息: {}", deviceName, content);
                    break;
                case 0xA8:
                    //设备自检上报
                    log.info("\n--------------------->YBT机柜-[{}],孔位异常 设备自检上报: {}", deviceName, content);
                    break;
                case 0x28:
                    //场景： 机器断电的时候客户把充电宝归还，等开机上电后 机器不会以当前时间上报归还充电宝，所以新加状态变化指令
                    //当孔位有充电宝并且状态出现变化的时候 机器会上报一条28指令 提示状态变化
                    ReceiveIndexCheck receiveIndexCheck = new ReceiveIndexCheck(body);
                    log.info("\n--------------------->YBT机柜-[{}],状态变化上报: {}", deviceName, GsonUtil.toJson(receiveIndexCheck.getPowerbank()));

                    break;
                case 0xCF:
                    //Find nearby Wi-Fi and hotspots
                    ReceiveWifi receiveWifi = new ReceiveWifi(body);
                    log.info("\n--------------------->YBT机柜-[{}],获取附近WIFI热点: {}", deviceName, GsonUtil.toJson(receiveWifi));
                    //存到缓存里面方便其他服务获取
                    if (null != redisUtil) {
                        redisUtil.set(RedisKeyCst.CABINET_MESSAGE_CACHE_WIFI + deviceName, content, TimeCst.TIME_SECOND_1DAYS);
                    }
                    break;
                default:
                    log.info("\n--------------------->YBT机柜-[{}],未知命令数据上报: {}", deviceName, content);
                    break;
            }
        } catch (Exception e) {
            log.error("processMessage occurs error ", e);
        }
    }

    private String getDeviceName(String topic) {
        String[] arr = topic.split("/");
        if (arr.length < 3) {
            return "";
        }
        return arr[2];
    }

    /**
     * 发送MQ消息
     *
     * @param data       数据
     * @param routingKey 路由
     */
    private void sendMQ(Object data, String routingKey) {
//        AmqpTemplate amqpTemplate = SpringContextUtil.getBean(MQCst.RABBIT_TEMPLATE);
//        amqpTemplate.convertAndSend(YbtMQCst.EXCHANGE_CABINET, routingKey, data);
    }
}
