package com.brezze.share.communication.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * MQTT Configuration
 * Binds mqtt.* properties from application.yml
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "mqtt")
public class MqttConfig {
    private String broker;
    private Integer port;
    private String username;
    private String password;
    private String clientId;
    private Boolean ssl;
}
