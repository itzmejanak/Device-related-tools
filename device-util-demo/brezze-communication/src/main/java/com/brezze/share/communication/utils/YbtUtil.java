package com.brezze.share.communication.utils;

import com.aliyuncs.iot.model.v20180120.QueryDeviceDetailResponse;
import com.brezze.share.communication.oo.ybt.DeviceConfig;
import com.brezze.share.utils.common.json.GsonUtil;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;

import java.time.LocalDateTime;
import java.util.*;

public class YbtUtil {

    /**
     * 获取签名
     *
     * @param map 签名参数
     * @return
     */
    public static String getSign(Map<String, Object> map) {
        return getSign(map, "|");
    }

    /**
     * 获取签名
     *
     * @param map 签名参数
     * @return
     */
    public static String getSign(Map<String, Object> map, String separator) {
        if (map == null) {
            return "";
        }

        Map orderedMap = MapUtils.orderedMap(map);
        List<String> list = new ArrayList<>();
        for (Object key : orderedMap.keySet()) {
            if ("sign".equals(key)) {
                continue;
            }
            String value = map.get(key) == null ? "" : map.get(key).toString();
            list.add(key + "=" + value);
        }
        //KEY升序
        Collections.sort(list);
        String str = StringUtils.join(list, separator);
        return DigestUtils.md5Hex(str);
    }

    /**
     * 获取签名
     *
     * @param bean 签名参数
     * @return
     */
    public static String getSign(Object bean) {
        if (bean == null) {
            return "";
        }

        Map map = GsonUtil.beanToMap(bean);
        String str = getSign(map);
        return str;
    }

    public static DeviceConfig getDeviceInfo(String accessKey, String accessSecret, String regionId, String productKey, String deviceName) throws Exception {
        DeviceConfig iotDeviceConfig = new DeviceConfig();
        //设备详细信息
        QueryDeviceDetailResponse query = IotUtil.getDeviceInfo(accessKey, accessSecret, regionId, productKey, deviceName);
        //阿里云注册设备
        if (query == null || (!query.getSuccess() && "iot.device.NotExistedDevice".equals(query.getCode()))) {
            IotUtil.registerDevice(accessKey, accessSecret, regionId, productKey, deviceName);
            //新激活的设备
            query = IotUtil.getDeviceInfo(accessKey, accessSecret, regionId, productKey, deviceName);
        }
        if (query == null) {
            throw new Exception("Device Error");
        }
        //设备详细信息
        QueryDeviceDetailResponse.Data data = query.getData();
        //获取新的账号，密码
        iotDeviceConfig.setSn(deviceName);
        iotDeviceConfig.setProductKey(productKey);
        iotDeviceConfig.setDeviceName(deviceName);
        iotDeviceConfig.setDeviceSecret(data.getDeviceSecret());
        iotDeviceConfig.setHost(String.format(IotUtil.MQTT_URL, productKey, regionId));
        iotDeviceConfig.setPort(IotUtil.MQTT_PORT);
        iotDeviceConfig.setCreatedTime(LocalDateTime.now());
        //获取设备授权参数
        Map map = IotUtil.getMqttAccount(regionId, productKey, data.getDeviceSecret(), deviceName);
        iotDeviceConfig.setIotId(MapUtils.getString(map, "iotId"));
        iotDeviceConfig.setIotToken(MapUtils.getString(map, "iotToken"));
        return iotDeviceConfig;
    }

    public static DeviceConfig getDeviceInfo(String accessKey, String accessSecret, String regionId, String iotInstanceId, String productKey, String deviceName) throws Exception {
        DeviceConfig iotDeviceConfig = new DeviceConfig();
        //设备详细信息
        QueryDeviceDetailResponse query = IotUtil.getDeviceInfo(accessKey, accessSecret, regionId, iotInstanceId, productKey, deviceName);
        //阿里云注册设备
        if (query == null || (!query.getSuccess() && "iot.device.NotExistedDevice".equals(query.getCode()))) {
            IotUtil.registerDevice(accessKey, accessSecret, regionId, iotInstanceId, productKey, deviceName);
            //新激活的设备
            query = IotUtil.getDeviceInfo(accessKey, accessSecret, regionId, iotInstanceId, productKey, deviceName);
        }
        if (query == null) {
            throw new Exception("Device Error");
        }
        //设备详细信息
        QueryDeviceDetailResponse.Data data = query.getData();
        //获取新的账号，密码
        iotDeviceConfig.setSn(deviceName);
        iotDeviceConfig.setProductKey(productKey);
        iotDeviceConfig.setDeviceName(deviceName);
        iotDeviceConfig.setDeviceSecret(data.getDeviceSecret());
        iotDeviceConfig.setHost(String.format(IotUtil.MQTT_URL, productKey, regionId));
        iotDeviceConfig.setPort(IotUtil.MQTT_PORT);
        iotDeviceConfig.setCreatedTime(LocalDateTime.now());
        //获取设备授权参数
        Map map = IotUtil.getMqttAccount(regionId, productKey, data.getDeviceSecret(), deviceName);
        iotDeviceConfig.setIotId(MapUtils.getString(map, "iotId"));
        iotDeviceConfig.setIotToken(MapUtils.getString(map, "iotToken"));
        return iotDeviceConfig;
    }

    public static DeviceConfig getDeviceInfoEmqx(String host, String accessKey, String accessSecret, String productKey, String deviceName) throws Exception {
        Boolean flag = EmqxUtil.getDeviceInfo(host, accessKey, accessSecret, deviceName);
        if (!flag) {
            flag = EmqxUtil.registerDevice(host, accessKey, accessSecret, deviceName);
        }
        if (!flag) {
            throw new Exception("Device Error");
        }
        //获取新的账号，密码
        DeviceConfig iotDeviceConfig = new DeviceConfig();
        iotDeviceConfig.setSn(deviceName);
        iotDeviceConfig.setProductKey(productKey);
        iotDeviceConfig.setDeviceName(deviceName);
        iotDeviceConfig.setDeviceSecret("");
        iotDeviceConfig.setHost(host);
        iotDeviceConfig.setPort(IotUtil.MQTT_PORT);
        iotDeviceConfig.setCreatedTime(LocalDateTime.now());
        //获取设备授权参数
        iotDeviceConfig.setIotId(deviceName);
        iotDeviceConfig.setIotToken(deviceName);
        return iotDeviceConfig;
    }

    /**
     * 获取版本号
     *
     * @param versionInt 版本号
     * @return
     */
    public static String getVersion(Integer versionInt) {
        String baseNum = "1000";
        String prefix = "V";
        return String.format("%s%d", prefix, Long.parseLong(baseNum) + versionInt);
    }
}
