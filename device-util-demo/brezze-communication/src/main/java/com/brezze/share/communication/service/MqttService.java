package com.brezze.share.communication.service;

import lombok.extern.slf4j.Slf4j;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Slf4j
@Service
public class MqttService {

    @Resource
    private MqttClient mqttClient;

    @Value("${productKey:powerbank}")
    private String productKey;

    @Value("${topicType:true}")
    private boolean topicType;

    public void publish(String deviceName, String payload) {
        try {
            String topic = buildTopic(deviceName);
            MqttMessage message = new MqttMessage(payload.getBytes());
            message.setQos(0);
            message.setRetained(false);
            
            log.info("Publishing to topic: {} | payload: {}", topic, payload);
            mqttClient.publish(topic, message);
            log.info("Message published successfully");
        } catch (Exception e) {
            log.error("Failed to publish MQTT message to device: {}", deviceName, e);
        }
    }

    private String buildTopic(String deviceName) {
        if (topicType) {
            return String.format("/%s/%s/user/get", productKey, deviceName);
        } else {
            return String.format("/%s/%s/get", productKey, deviceName);
        }
    }
}
