package com.brezze.share.communication.listener;

import cn.hutool.json.JSONUtil;
import com.brezze.share.utils.common.constant.RedisKeyCst;
import com.brezze.share.utils.common.constant.TimeCst;
import com.brezze.share.utils.common.json.GsonUtil;
import com.brezze.share.utils.common.oo.ybt.message.*;
import com.brezze.share.utils.common.oo.ybt.serialport.SerialPortData;
import com.brezze.share.utils.common.redis.RedisUtil;
import com.brezze.share.utils.common.string.ByteUtils;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.paho.client.mqttv3.IMqttMessageListener;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

@Slf4j
@Component
public class MqttMessageListener implements IMqttMessageListener {

    @Resource
    private RedisUtil redisUtil;
    
    @Resource
    private org.eclipse.paho.client.mqttv3.MqttClient mqttClient;
    
    @Value("${productKey:powerbank}")
    private String productKey;

    @PostConstruct
    public void subscribe() {
        try {
            String topic = String.format("/%s/+/user/update", productKey);
            mqttClient.subscribe(topic, this);
            log.info("✅ Subscribed to MQTT topic: {}", topic);
        } catch (Exception e) {
            log.error("❌ Failed to subscribe to MQTT topic", e);
        }
    }

    @Override
    public void messageArrived(String topic, MqttMessage mqttMessage) {
        try {
            byte[] body = mqttMessage.getPayload();
            String deviceName = extractDeviceName(topic);
            String content;
            
            if (topic.contains("/as/mqtt/status")) {
                content = new String(body);
                log.info("Device status: {} | {}", deviceName, content);
            } else {
                content = ByteUtils.to16Hexs(body);
                int cmd = SerialPortData.checkCMD(body);
                
                switch (cmd) {
                    case 0x10:
                        ReceiveUpload receiveUpload = new ReceiveUpload(body);
                        log.info("Device heartbeat: {} | {}", deviceName, GsonUtil.toJson(receiveUpload));
                        redisUtil.set(RedisKeyCst.CABINET_MESSAGE_CACHE_CHECK + deviceName, 
                            ByteUtils.to16Hexs(body), TimeCst.TIME_SECOND_1DAYS);
                        break;
                    case 0x21:
                        ReceivePopupIndex receivePopupIndex = new ReceivePopupIndex(body);
                        log.info("Popup by position: {} | {}", deviceName, GsonUtil.toJson(receivePopupIndex));
                        break;
                    case 0x31:
                        ReceivePopupSN receivePopupSN = new ReceivePopupSN(body);
                        log.info("Popup by SN: {} | {}", deviceName, GsonUtil.toJson(receivePopupSN));
                        break;
                    case 0X40:
                        ReceiveReturn receiveReturn = new ReceiveReturn(body);
                        log.info("Powerbank return: {} | {}", deviceName, GsonUtil.toJson(receiveReturn));
                        break;
                    case 0X7A:
                        log.info("MQTT heartbeat: {} | {}", deviceName, content);
                        break;
                    case 0xCF:
                        ReceiveWifi receiveWifi = new ReceiveWifi(body);
                        log.info("WiFi scan: {} | {}", deviceName, JSONUtil.toJsonStr(receiveWifi.getName()));
                        break;
                    default:
                        log.info("Unknown command: {} | cmd: 0x{} | {}", deviceName, 
                            Integer.toHexString(cmd), content);
                        break;
                }
            }
        } catch (Exception e) {
            log.error("Error processing MQTT message from topic: {}", topic, e);
        }
    }

    private String extractDeviceName(String topic) {
        String[] parts = topic.split("/");
        return parts.length >= 3 ? parts[2] : "unknown";
    }
}
