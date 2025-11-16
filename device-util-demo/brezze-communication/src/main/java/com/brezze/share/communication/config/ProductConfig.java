package com.brezze.share.communication.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

/**
 * Product Configuration
 * Binds productKey and topicType from application.yml
 */
@Data
@Configuration
public class ProductConfig {
    
    @Value("${productKey:powerbank}")
    private String productKey;
    
    @Value("${topicType:true}")
    private Boolean topicType;
}
