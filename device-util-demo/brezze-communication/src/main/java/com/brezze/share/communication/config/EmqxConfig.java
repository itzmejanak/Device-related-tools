package com.brezze.share.communication.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * EMQX Management API Configuration
 * Binds emqx.api.* properties from application.yml
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "emqx.api")
public class EmqxConfig {
    private String url;
    private String key;
    private String secret;
}
