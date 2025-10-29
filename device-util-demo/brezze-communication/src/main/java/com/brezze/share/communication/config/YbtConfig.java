package com.brezze.share.communication.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "ybt")
public class YbtConfig {
    private boolean enable;
    private String accessKey;
    private String accessSecret;
    private String uid;
    private String iotInstanceId;
    private String regionId;
    private String productKey;
    private String consumerGroupId;
}
