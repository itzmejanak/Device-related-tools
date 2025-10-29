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
    //以下配置需要定死
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
            log.info("EMQX创建设备信息：{},{}", deviceName, responseEntity.getBody());
            return true;
        } catch (HttpClientErrorException hce) {
            log.error("EMQX创建设备信息：{},{}", deviceName, hce.getResponseBodyAsString());
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
            log.info("EMQX获取设备信息：{},{}", deviceName, responseEntity.getBody());
            return true;
        } catch (HttpClientErrorException hce) {
            log.error("EMQX获取设备信息：{},{}", deviceName, hce.getResponseBodyAsString());
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
        //设置消息的内容，一定要用base64编码，否则乱码
        params.put("payload", Base64Utils.encodeToString(data.getBytes()));
        try {
            ResponseEntity<String> responseEntity = HttpUtil.post(url, headers, params, String.class);
            log.info("EMQX发送主题消息：{}", responseEntity.getBody());
            return true;
        } catch (HttpClientErrorException hce) {
            log.error("EMQX发送主题消息：{}", hce.getResponseBodyAsString());
        }
        return false;
    }
}
