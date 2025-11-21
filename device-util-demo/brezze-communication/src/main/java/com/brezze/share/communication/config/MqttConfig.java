package com.brezze.share.communication.config;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Data
@Configuration
@ConfigurationProperties(prefix = "mqtt")
public class MqttConfig {
    private String broker;
    private int port;
    private String username;
    private String password;
    private String clientId;
    private boolean ssl;

    @Bean
    public MqttClient mqttClient() throws MqttException {
        String serverUri = (ssl ? "ssl://" : "tcp://") + broker + ":" + port;
        MqttClient client = new MqttClient(serverUri, clientId);
        
        MqttConnectOptions options = new MqttConnectOptions();
        options.setUserName(username);
        options.setPassword(password.toCharArray());
        options.setCleanSession(true);
        options.setAutomaticReconnect(true);
        options.setConnectionTimeout(10);
        options.setKeepAliveInterval(60);
        
        log.info("Connecting to MQTT broker: {}", serverUri);
        client.connect(options);
        log.info("MQTT client connected successfully");
        
        return client;
    }
}
