package com.brezze.share.communication.utils;

import cn.hutool.core.util.StrUtil;
import com.brezze.share.utils.common.http.HttpUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.Base64Utils;
import org.springframework.web.client.HttpClientErrorException;

import java.util.HashMap;
import java.util.Map;

@Slf4j
public class EmqxUtil {

    public static final String BASIC_URL = "http://%s:18083";
    public static final String USERNAME = "";
    public static final String PASSWORD = "";
    public static final String PRODUCT_KEY = "powerbank";
    public static final String HOST = "";
    // The following configurations are fixed values
    public static final String TOPIC_GET = "/%s/%s/user/get";

    public static Boolean registerDevice(String host, String username, String password, String deviceName) {
        String url = StrUtil.format("{}/api/v5/authentication/{}/users",
                String.format(BASIC_URL, host), "password_based:built_in_database");
        HttpHeaders headers = new HttpHeaders();
        headers.setBasicAuth(username, password);
        headers.setContentType(MediaType.APPLICATION_JSON);
        Map<String, Object> params = new HashMap<>();
        params.put("user_id", deviceName);
        params.put("password", deviceName);
        try {
            ResponseEntity<String> responseEntity = HttpUtil.post(url, headers, params, String.class);
            log.info("EMQX device creation: {}, {}", deviceName, responseEntity.getBody());
            return true;
        } catch (HttpClientErrorException hce) {
            log.error("EMQX device creation failed: {}, {}", deviceName, hce.getResponseBodyAsString());
        }
        return false;
    }

    public static Boolean getDeviceInfo(String host, String username, String password, String deviceName) {
        String url = StrUtil.format("{}/api/v5/authentication/{}/users/{}",
                String.format(BASIC_URL, host), "password_based:built_in_database", deviceName);
        HttpHeaders headers = new HttpHeaders();
        headers.setBasicAuth(username, password);
        try {
            ResponseEntity<String> responseEntity = HttpUtil.get(url, headers, String.class);
            log.info("EMQX device info retrieval: {}, {}", deviceName, responseEntity.getBody());
            return true;
        } catch (HttpClientErrorException hce) {
            log.error("EMQX device info retrieval failed: {}, {}", deviceName, hce.getResponseBodyAsString());
        }
        return false;
    }

    public static Boolean pubTopicMsg(String host, String username, String password, String productKey, String deviceName, String data) {
        return pubTopicMsg(String.format(BASIC_URL, host), username, password, String.format(TOPIC_GET, productKey, deviceName), data);
    }

    public static Boolean pubTopicMsg(String basicUrl, String username, String password, String topic, String data) {
        String url = StrUtil.format("{}/api/v5/publish",
                basicUrl);
        HttpHeaders headers = new HttpHeaders();
        headers.setBasicAuth(username, password);
        headers.setContentType(MediaType.APPLICATION_JSON);
        Map<String, Object> params = new HashMap<>();
        params.put("payload_encoding", "base64");
        params.put("topic", topic);
        params.put("qos", "1");
        // Message content must be base64 encoded to prevent garbled text
        params.put("payload", Base64Utils.encodeToString(data.getBytes()));
        try {
            ResponseEntity<String> responseEntity = HttpUtil.post(url, headers, params, String.class);
            log.info("EMQX topic message published successfully: topic={}, response={}", topic, responseEntity.getBody());
            return true;
        } catch (HttpClientErrorException hce) {
            log.error("EMQX topic message publish failed (HTTP error): topic={}, status={}, error={}", 
                topic, hce.getStatusCode(), hce.getResponseBodyAsString());
        } catch (Exception e) {
            log.error("EMQX topic message publish failed (network error): topic={}, url={}, error={}", 
                topic, url, e.getMessage());
        }
        return false;
    }
}