package com.brezze.share.communication.cabinet.service.impl;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONUtil;
import com.brezze.share.communication.cabinet.service.CabinetService;
import com.brezze.share.communication.cabinet.service.YbtService;
import com.brezze.share.communication.oo.dto.ConnectTokenDTO;
import com.brezze.share.communication.oo.dto.DeviceDetailDTO;
import com.brezze.share.communication.oo.dto.DeviceStatusDTO;
import com.brezze.share.communication.oo.ybt.VersionInfo;
import com.brezze.share.communication.utils.EmqxUtil;
import com.brezze.share.communication.utils.YbtUtil;
import com.brezze.share.communication.utils.pay.StripePay;
import com.brezze.share.utils.common.constant.RedisKeyCst;
import com.brezze.share.utils.common.constant.mq.YbtMQCst;
import com.brezze.share.utils.common.date.DateUtil;
import com.brezze.share.utils.common.enums.hint.Hint;
import com.brezze.share.utils.common.exception.BrezException;
import com.brezze.share.utils.common.oo.dto.PopUpDTO;
import com.brezze.share.utils.common.oo.ybt.message.ReceiveUpload;
import com.brezze.share.utils.common.oo.ybt.message.ReceiveWifi;
import com.brezze.share.utils.common.oo.ybt.req.CmdYbtREQ;
import com.brezze.share.utils.common.redis.RedisUtil;
import com.brezze.share.utils.common.string.ByteUtils;
import com.brezze.share.utils.common.string.StringUtil;
import com.stripe.model.terminal.ConnectionToken;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@Slf4j
@Service
public class YbtServiceImpl implements YbtService {

    @Autowired
    private RedisUtil redisUtil;
    @Autowired
    private RabbitTemplate rabbitTemplate;
    @Autowired
    private CabinetService cabinetService;
    @Autowired
    private com.brezze.share.communication.config.MqttConfig mqttConfig;
    @Autowired
    private com.brezze.share.communication.config.EmqxConfig emqxConfig;
    @Autowired
    private com.brezze.share.communication.config.ProductConfig productConfig;

    @Override
    public void deviceStatus(DeviceStatusDTO dto) {
        if ("online".equals(dto.getStatus())) {
            sendLocationId(dto.getDeviceName(), "location id ");
        }
    }

    @Override
    public void checkAll(String deviceName) {
        String topicMsg = "{\"cmd\":\"check_all\"}";
        this.sendCmd(deviceName, topicMsg);
    }

    @Override
    public DeviceDetailDTO getDeviceDetail(String deviceName) throws Exception {
        //1.
        String imei = cabinetService.getImei(deviceName);
        return getCheckMessage(imei);
    }

    @Override
    public DeviceDetailDTO getCheckMessage(String deviceName) throws Exception {
        String key = RedisKeyCst.CABINET_MESSAGE_CACHE_CHECK + deviceName;
        redisUtil.del(key);
        //发送查询详情指令
        checkAll(deviceName);
        //在一定时间内循环查询缓存数据
        byte[] data = getMessageCache(key);
        ReceiveUpload receiveUpload = new ReceiveUpload(data);
        DeviceDetailDTO dto = new DeviceDetailDTO();
        dto.setDeviceName(deviceName);
        dto.setData(receiveUpload);
        return dto;
    }

    @Override
    public void popUpByPbNo(String deviceName, String pbNo) {
        //判断是否存在操作记录
        String topicMsg = "{\"cmd\":\"popup_sn\",\"data\":\"" + pbNo + "\"}";
        this.sendCmd(deviceName, topicMsg);
    }

    @Override
    public void popUpByPbNo(CmdYbtREQ req) {
        //判断是否存在操作记录
        Object obj = redisUtil.get(RedisKeyCst.CABINET_POP_UP_INTERVAL + req.getCabinetNo());
        int interval = 5;
        LocalDateTime lastTime;
        if (obj != null) {
            PopUpDTO dto = (PopUpDTO) obj;
            lastTime = dto.getLastTime();
        } else {
            lastTime = LocalDateTime.now();
        }
        LocalDateTime popUpTime = DateUtil.addTime(lastTime, ChronoUnit.SECONDS, interval);
        PopUpDTO popUp = new PopUpDTO();
        popUp.setLastTime(popUpTime);
        //延迟消费时间=弹出时间-当前时间
        int delayTime = Math.toIntExact(DateUtil.getTwoTimeDiffSecond(LocalDateTime.now(), popUpTime));
        //延迟发送弹出指令
        rabbitTemplate.convertAndSend(YbtMQCst.EXCHANGE_DELAY, YbtMQCst.ROUTING_DELAY_PB_POP, req, message -> {
            message.getMessageProperties().setDelay(delayTime * 1000);
            return message;
        });
        redisUtil.set(RedisKeyCst.CABINET_POP_UP_INTERVAL + req.getCabinetNo(), popUp, delayTime);
    }

    @Override
    public void popUpByIndex(String deviceName, Integer pos, Integer io) {
        //发送MQ消息
        String topicMsg = "{\"cmd\":\"popup\",\"data\":\"" + pos + "\",\"io\":\"" + io + "\"}";
        this.sendCmd(deviceName, topicMsg);
    }

    @Override
    public void popUpByIndex(CmdYbtREQ req) {
        //判断是否存在操作记录
        Object obj = redisUtil.get(RedisKeyCst.CABINET_POP_UP_INTERVAL + req.getCabinetNo());
        int interval = 5;
        LocalDateTime lastTime;
        if (obj != null) {
            PopUpDTO dto = (PopUpDTO) obj;
            lastTime = dto.getLastTime();
        } else {
            lastTime = LocalDateTime.now();
        }
        LocalDateTime popUpTime = DateUtil.addTime(lastTime, ChronoUnit.SECONDS, interval);
        PopUpDTO popUp = new PopUpDTO();
        popUp.setLastTime(popUpTime);
        //延迟消费时间=弹出时间-当前时间
        int delayTime = Math.toIntExact(DateUtil.getTwoTimeDiffSecond(LocalDateTime.now(), popUpTime));
        rabbitTemplate.convertAndSend(YbtMQCst.EXCHANGE_DELAY, YbtMQCst.ROUTING_DELAY_PB_OPEN_LOCK, req, message -> {
            message.getMessageProperties().setDelay(delayTime * 1000);
            return message;
        });
        redisUtil.set(RedisKeyCst.CABINET_POP_UP_INTERVAL + req.getCabinetNo(), popUp, delayTime);
    }

    @Override
    public void checkSign(Object valid, String sign) throws Exception {
        if (!YbtUtil.getSign(valid).equals(sign)) {
            throw new Exception("ERROR SIGN");
        }
    }

    @Override
    public void sendCmd(String deviceName, String data) {
        // Use configuration from application.yml instead of hardcoded values
        EmqxUtil.pubTopicMsg(
            emqxConfig.getUrl(), 
            emqxConfig.getKey(), 
            emqxConfig.getSecret(), 
            productConfig.getProductKey(), 
            deviceName, 
            data
        );
    }

    @Override
    public void sendLocationId(String deviceName, String locationId) {
        //设备上线时发送
        if (StringUtil.isEmpty(locationId)) {
            return;
        }
        //发送MQ消息
        String topicMsg = "{\"cmd\":\"location\",\"data\":\"" + locationId + "\"}";
        sendCmd(deviceName, topicMsg);
    }

    @Override
    public ConnectTokenDTO connectToken(String locationId) {
        ConnectionToken connectionToken = StripePay.createConnectionToken(StripePay.API_KEY, locationId);
        ConnectTokenDTO dto = new ConnectTokenDTO();
        dto.setSecret(connectionToken.getSecret());
        return dto;
    }

    @Override
    public String getVersionInfo(String appUuid, Boolean releaseModel) {
        //Retrieve from database
        //todo Find the upgrade package based on the unique version identifier appUuid,
        // and distinguish between the live package and the test package based on releaseModel.

        Object obj = redisUtil.get("versionInfo");
        VersionInfo versionInfo = JSONUtil.toBean(JSONUtil.toJsonStr(obj), VersionInfo.class);

        //Format: Firmware Name (provided by the manufacturer), Firmware Size, Download Link
        //MoudleEC20_DUV1029_0x6E34_53164.bin,53164,http://sharingweb.oss-cn-shenzhen.aliyuncs.com/apps/4db87503609bee2fe2a7b10b84da2929.bin
        if (ObjectUtil.equals(appUuid, "00000000000000000000000000000000")) {
            if (releaseModel) {
                return versionInfo.getMcuRelease();
            } else {
                return versionInfo.getMcuTest();
            }
        }
        if (ObjectUtil.equals(appUuid, "00000000000000000000000000000001")) {
            if (releaseModel) {
                return versionInfo.getChipRelease();
            } else {
                return versionInfo.getChipTest();
            }
        }
        return "";
    }

    @Override
    public void getWifi(String deviceName) {
        String topicMsg = "{\"cmd\":\"getWifi\"}";
        sendCmd(deviceName, topicMsg);
    }

    @Override
    public ReceiveWifi getWifiMessage(String deviceName) {
        String key = RedisKeyCst.CABINET_MESSAGE_CACHE_WIFI + deviceName;
        redisUtil.del(key);
        //发送查询详情指令
        getWifi(deviceName);
        //在一定时间内循环查询缓存数据
        try {
            byte[] data = getMessageCache(key);
            ReceiveWifi receiveWifi = new ReceiveWifi(data);
            return receiveWifi;
        } catch (Exception e) {
            log.error("获取wifi名称异常：", e);
        }
        return null;
    }

    private byte[] getMessageCache(String key) {
        byte[] bytes = null;
        for (int i = 0; i < 30; i++) {
            Object data = null;
            try {
                Thread.sleep(500);
                data = redisUtil.get(key);
            } catch (InterruptedException e) {
                log.error("获取缓存数据异常：", e);
            }
            if (data != null) {
                bytes = ByteUtils.toBytes(String.valueOf(data));
                log.info("Message body cache:{}", bytes);
                redisUtil.del(key);
                break;
            }
        }
        if (null == bytes) {
            throw new BrezException(Hint.FAILURE);
        }
        return bytes;
    }
}
