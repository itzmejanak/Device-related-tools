package com.brezze.share.communication.controller;

import cn.hutool.core.util.ArrayUtil;
import cn.hutool.json.JSONUtil;
import com.brezze.share.communication.cabinet.service.YbtService;
import com.brezze.share.communication.oo.dto.DeviceStatusDTO;
import com.brezze.share.communication.oo.emqx.EMQXEvent;
import com.brezze.share.communication.oo.emqx.EMQXMessage;
import com.brezze.share.communication.oo.ybt.DeviceConfig;
import com.brezze.share.communication.oo.ybt.HttpResult;
import com.brezze.share.communication.utils.EmqxUtil;
import com.brezze.share.communication.utils.YbtUtil;
import com.brezze.share.utils.common.date.DateUtil;
import com.brezze.share.utils.common.enums.hint.Hint;
import com.brezze.share.utils.common.json.GsonUtil;
import com.brezze.share.utils.common.oo.ybt.message.*;
import com.brezze.share.utils.common.oo.ybt.req.CmdYbtREQ;
import com.brezze.share.utils.common.oo.ybt.serialport.SerialPortData;
import com.brezze.share.utils.common.result.Rest;
import com.brezze.share.utils.common.string.ByteUtils;
import com.brezze.share.utils.common.string.StringUtil;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import com.github.xiaoymin.knife4j.annotations.ApiSupport;

import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@ApiSupport(order = 9)
@Slf4j
@RestController
public class YbtController {

    @Resource
    private YbtService ybtService;

    /**
     * /api/iot/client/con GET Android device authentication
     * /api/iot/client/con POST MCU screenless device authentication
     *
     * @param simUUID   SIM card ICCID
     * @param simMobile SIM card phone number
     * @param uuid      Hardware unique identifier IMEI
     * @param deviceId  System platform unique ID, default 0
     * @param sign      MD5 signature
     * @param response
     */
    @ApiOperationSupport(order = 2)
    @ApiOperation(httpMethod = "POST", produces = "application/json", value = "Device authentication", notes = "MCU screenless device authentication, device request interface")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "simUUID", value = "SIM card ICCID", dataType = "string", required = false, paramType = "query"),
            @ApiImplicitParam(name = "simMobile", value = "SIM card phone number", dataType = "string", required = false, paramType = "query"),
            @ApiImplicitParam(name = "uuid", value = "Hardware unique identifier IMEI", dataType = "string", required = true, paramType = "query"),
            @ApiImplicitParam(name = "deviceId", value = "System platform unique ID, default 0", dataType = "string", required = true, paramType = "query"),
            @ApiImplicitParam(name = "sign", value = "MD5 signature", dataType = "string", required = true, paramType = "query"),
    })
    @GetMapping(value = "/api/iot/client/con")
    public void androidDevice(@RequestHeader(required = false) String host,
                              @RequestParam(required = false, defaultValue = "") String simUUID,
                              @RequestParam(required = false, defaultValue = "") String simMobile,
                              @RequestParam String uuid,
                              @RequestParam(defaultValue = "0") String deviceId,
                              @RequestParam String sign,
                              HttpServletRequest request,
                              HttpServletResponse response
    ) {
        HttpResult httpResult = new HttpResult();
        //校验签名
        Map map = new HashMap();
        map.put("simUUID", simUUID);
        map.put("simMobile", simMobile);
        map.put("uuid", uuid);
        map.put("deviceId", deviceId);
        try {
            String hardwareVersion = null;
            if (request.getContentLength() > 0) {
                byte[] bytes = IOUtils.readFully(request.getInputStream(), request.getContentLength());
                String body = new String(bytes, StandardCharsets.UTF_8);
                log.info("POSTDATA:{}", body);
                List<NameValuePair> params = URLEncodedUtils.parse(body, StandardCharsets.UTF_8);
                for (NameValuePair param : params) {
                    //MCU硬件版本号
                    if ("hardware".equals(param.getName())) {
                        hardwareVersion = param.getValue();
                    }
                }
            }
            DeviceConfig config = null;
            config = YbtUtil.getDeviceInfoEmqx(EmqxUtil.HOST, EmqxUtil.USERNAME, EmqxUtil.PASSWORD, EmqxUtil.PRODUCT_KEY, uuid);
            String[] arrStr = new String[]{config.getDeviceName(),
                    config.getProductKey(),
                    config.getHost(), String.valueOf(config.getPort()),
                    config.getIotId(), config.getIotToken(),
                    String.valueOf(System.currentTimeMillis())};
            String data = StringUtils.join(arrStr, ",");
            httpResult.setData(data);
        } catch (Exception e) {
            e.printStackTrace();
        }
        //获取设备秘钥，返回成功数据
        printResponse(response, GsonUtil.toJson(httpResult));
    }

    /**
     * /api/iot/client/con GET Android device authentication
     * /api/iot/client/con POST MCU screenless device authentication
     *
     * @param simUUID   SIM card ICCID
     * @param simMobile SIM card phone number
     * @param uuid      Hardware unique identifier IMEI
     * @param deviceId  System platform unique ID, default 0
     * @param sign      MD5 signature
     * @param response
     */
    @ApiOperationSupport(order = 2)
    @ApiOperation(httpMethod = "POST", produces = "application/json", value = "Device authentication", notes = "MCU screenless device authentication, device request interface")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "simUUID", value = "SIM card ICCID", dataType = "string", required = false, paramType = "query"),
            @ApiImplicitParam(name = "simMobile", value = "SIM card phone number", dataType = "string", required = false, paramType = "query"),
            @ApiImplicitParam(name = "uuid", value = "Hardware unique identifier IMEI", dataType = "string", required = true, paramType = "query"),
            @ApiImplicitParam(name = "deviceId", value = "System platform unique ID, default 0", dataType = "string", required = true, paramType = "query"),
            @ApiImplicitParam(name = "sign", value = "MD5 signature", dataType = "string", required = true, paramType = "query"),
    })
    @PostMapping(value = "/api/iot/client/con")
    public void mcuDevice(@RequestHeader(required = false) String host,
                          @RequestParam(required = false, defaultValue = "") String simUUID,
                          @RequestParam(required = false, defaultValue = "") String simMobile,
                          @RequestParam String uuid,
                          @RequestParam(defaultValue = "0") String deviceId,
                          @RequestParam String sign,
                          HttpServletRequest request,
                          HttpServletResponse response
    ) {

        HttpResult httpResult = new HttpResult();
        //校验签名
        Map map = new HashMap();
        map.put("simUUID", simUUID);
        map.put("simMobile", simMobile);
        map.put("uuid", uuid);
        map.put("deviceId", deviceId);
        try {
            String hardwareVersion = null;
            if (request.getContentLength() > 0) {
                byte[] bytes = IOUtils.readFully(request.getInputStream(), request.getContentLength());
                String body = new String(bytes, StandardCharsets.UTF_8);
                log.info("POSTDATA:{}", body);
                List<NameValuePair> params = URLEncodedUtils.parse(body, StandardCharsets.UTF_8);
                for (NameValuePair param : params) {
                    //MCU硬件版本号
                    if ("hardware".equals(param.getName())) {
                        hardwareVersion = param.getValue();
                    }
                }
            }
            DeviceConfig config = YbtUtil.getDeviceInfoEmqx(EmqxUtil.HOST, EmqxUtil.USERNAME, EmqxUtil.PASSWORD, EmqxUtil.PRODUCT_KEY, uuid);
            String[] arrStr = new String[]{config.getDeviceName(),
                    config.getProductKey(),
                    config.getHost(), String.valueOf(config.getPort()),
                    config.getIotId(), config.getIotToken(),
                    String.valueOf(System.currentTimeMillis())};
            String data = StringUtils.join(arrStr, ",");
            httpResult.setData(data);
        } catch (Exception e) {
            response.setStatus(HttpStatus.SC_INTERNAL_SERVER_ERROR);
            httpResult.setCode(response.getStatus());
            httpResult.setMsg(e.toString());
            log.error("设备[{}]鉴权异常", uuid, e);
        }
        //返回成功数据
        printResponse(response, GsonUtil.toJson(httpResult));
    }

    @ApiOperationSupport(order = 10)
    @ApiOperation(httpMethod = "POST", produces = "application/json", value = "Device reporting configuration", notes = "Device reporting configuration, device request interface")
    @PostMapping(value = "/api/rentbox/config/data")
    public void getConfig(HttpServletRequest request,
                          HttpServletResponse response,
                          @RequestHeader(required = false) String host,
                          @RequestParam(required = false) String rentboxSN,
                          @RequestParam(required = false) String dRotationRefer,
                          @RequestParam(required = false) String dHeadConfig,
                          @RequestParam(required = false) String dRotationNumber,
                          @RequestParam(required = false) String dRotationEnable,
                          @RequestParam(required = false) String dMotorEnable,
                          @RequestParam(required = false) String dAreaConfig,
                          @RequestParam(required = false) String sign,
                          @RequestBody(required = false) String body
    ) {
        log.info("设备配置信息上报：{}", request.getParameterMap());
        log.info("设备配置信息上报：{}", body);
        HttpResult httpResult = new HttpResult();
        Map<String, Object> map = new HashMap<>();
        map.put("dRotationRefer", "15");
        map.put("dReturnLocked", "0");
        map.put("dHeadConfig", "43");
        map.put("dRotationNumber", "4");
        map.put("dRotationEnable", "1");
        map.put("dMotorEnable", "1");
        map.put("dAreaConfig", "07");
        httpResult.setData(GsonUtil.toJson(map));
        //返回成功数据
        printResponse(response, GsonUtil.toJson(httpResult));
    }

    @ApiOperationSupport(order = 11)
    @ApiOperation(httpMethod = "POST", produces = "application/json", value = "Device report", notes = "Device report, device request interface")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "bytes", value = "Power bank data", dataType = "string", required = true, paramType = "query"),
            @ApiImplicitParam(name = "rentboxSN", value = "Device number (IMEI)", dataType = "string", required = true, paramType = "query"),
            @ApiImplicitParam(name = "signal", value = "Signal value", dataType = "String", required = false, paramType = "query"),
            @ApiImplicitParam(name = "CRC16", value = "Program CRC check code", dataType = "String", required = false, paramType = "query"),
            @ApiImplicitParam(name = "IAP16", value = "Program underlying check code", dataType = "String", required = false, paramType = "query"),
            @ApiImplicitParam(name = "dRotationEnable", value = "Configuration parameter", dataType = "String", required = false, paramType = "query"),
            @ApiImplicitParam(name = "dRotationNumber", value = "Configuration parameter", dataType = "String", required = false, paramType = "query"),
            @ApiImplicitParam(name = "dRotationRefer", value = "Configuration parameter", dataType = "String", required = false, paramType = "query"),
            @ApiImplicitParam(name = "dMotorEnable", value = "Configuration parameters", dataType = "String", required = false, paramType = "query"),
    })
    @PostMapping(value = "/api/rentbox/upload/data")
    public void uploadDetail(@RequestHeader(required = false) String host,
                             @RequestBody byte[] bytes,
                             @RequestParam String rentboxSN,
                             @RequestParam String sign,
                             @RequestParam(required = false) String signal,
                             @RequestParam(required = false) Integer io,
                             @RequestParam(required = false) String CRC16,
                             @RequestParam(required = false) String IAP16,
                             @RequestParam(required = false) String dRotationEnable,
                             @RequestParam(required = false) String dRotationNumber,
                             @RequestParam(required = false) String dRotationRefer,
                             @RequestParam(required = false) String dMotorEnable,
                             HttpServletResponse response, HttpServletRequest request) {

        //返回成功数据
        HttpResult httpResult = new HttpResult();
        try {
            //校验签名
            Map<String, String> map = new HashMap<>();
            map.put("rentboxSN", rentboxSN);
            map.put("signal", signal);
            if (io != null) {
                map.put("io", io.toString());
            }
            ReceiveUpload receiveUpload = new ReceiveUpload(bytes);
            log.info(GsonUtil.toJson(receiveUpload));

        } catch (Exception e) {
            log.error("整机上报异常：", e);
            response.setStatus(HttpStatus.SC_INTERNAL_SERVER_ERROR);
            httpResult.setCode(response.getStatus());
            httpResult.setMsg(e.toString());
        }
        printResponse(response, GsonUtil.toJson(httpResult));
    }

    @ApiOperationSupport(order = 33)
    @ApiOperation(httpMethod = "POST", produces = "application/json", value = "发送指令-通用", notes = "")
    @PostMapping(value = "/send")
    public Rest sendCmd(@RequestBody CmdYbtREQ req
    ) {
        if (StringUtil.isEmpty(req.getCabinetNo()) || StringUtil.isEmpty(req.getData())) {
            return Rest.failure(Hint.BAD_PARAMETER);
        }
        ybtService.sendCmd(req.getCabinetNo(), req.getData());
        //返回成功数据
        return Rest.success();
    }

    @ApiOperationSupport(order = 99)
    @ApiOperation(httpMethod = "GET", produces = "application/json", value = "Get Station Detail", notes = "")
    @GetMapping(value = "/communication/ybt/check-all")
    public void checkAll(HttpServletResponse response, String scanNo) throws Exception {
        Rest rest = Rest.success(ybtService.getCheckMessage(scanNo));
        printResponse(response, GsonUtil.toJson(rest));
    }

    @ApiOperationSupport(order = 100)
    @ApiOperation(httpMethod = "POST", produces = "application/json", value = "pop up by SN", notes = "")
    @PostMapping(value = "/communication/ybt/test-util/popup_sn")
    public void rentByPbNoOrdered(HttpServletResponse response,
                                  @RequestBody CmdYbtREQ req
    ) {
        Rest rest = Rest.success();
        ybtService.popUpByPbNo(req);
        //返回成功数据
        printResponse(response, GsonUtil.toJson(rest));
    }

    @ApiOperationSupport(order = 101)
    @ApiOperation(httpMethod = "POST", produces = "application/json", value = "pop up by position", notes = "")
    @PostMapping(value = "/communication/ybt/test-util/openLock")
    public void openLockOrdered(HttpServletResponse response,
                                @RequestBody CmdYbtREQ req
    ) {
        Rest rest = Rest.success();

        ybtService.popUpByIndex(req);
        //返回成功数据
        printResponse(response, GsonUtil.toJson(rest));
    }

    @PostMapping(value = "/api/emqx/event")
    public void event(@RequestBody String body) {
        EMQXEvent event = JSONUtil.toBean(body, EMQXEvent.class);
        // 过滤非上线、下线消息
        if (!ArrayUtil.containsAny(new String[]{"client.disconnected", "client.connected"}, event.getEvent())) {
            return;
        }
        String status = "";
        switch (event.getEvent()) {
            case "client.connected":
                status = "online";
                break;
            case "client.disconnected":
                status = "offline";
                break;
        }
        DeviceStatusDTO deviceStatusDTO = new DeviceStatusDTO();
        LocalDateTime lastTime = DateUtil.longToLocalDateTime(event.getTimestamp());
        LocalDateTime utcTime = DateUtil.conversionTimezone(lastTime, "UTC");
        String lastTimeStr = DateUtil.format(lastTime, DateUtil.FORMAT_PATTERN8);
        String utcTimeStr = DateUtil.format(utcTime, DateUtil.RFC3339_TIME_PATTERN2);
        deviceStatusDTO.setLastTime(lastTimeStr);
        deviceStatusDTO.setUtcLastTime(utcTimeStr);
        deviceStatusDTO.setClientIp(event.getPeername());
        deviceStatusDTO.setUtcTime(utcTimeStr);
        deviceStatusDTO.setTime(lastTimeStr);
        deviceStatusDTO.setProductKey(EmqxUtil.PRODUCT_KEY);
        deviceStatusDTO.setDeviceName(event.getClientid());
        deviceStatusDTO.setStatus(status);
        log.info("\n--------------------->YBT cabinet-[{}],Station status: {}", deviceStatusDTO.getDeviceName(), GsonUtil.toJson(deviceStatusDTO));
        // 发送设备状态变更mq
    }

    @PostMapping(value = "/api/emqx/message")
    public void message(@RequestBody String data) {
        EMQXMessage message = JSONUtil.toBean(data, EMQXMessage.class);

        // 过滤非消息投递事件
        if (!"message.publish".equals(message.getEvent())) {
            return;
        }
        // 过滤非消息投递事件
        if (message.getUsername() == null || "undefined".equalsIgnoreCase(message.getUsername())) {
            return;
        }
        try {
            byte[] body = Base64.decodeBase64(message.getPayload());
            String topic = message.getTopic();
            String messageId = message.getId();
            String content;
            if (topic.contains("/as/mqtt/status")) {
                // {"lastTime":"2020-05-20 16:25:43.196","utcLastTime":"2020-05-20T08:25:43.196Z","clientIp":"116.25.40.101","utcTime":"2020-05-20T08:25:43.205Z","time":"2020-05-20 16:25:43.205","productKey":"a1WiP9SggIH","deviceName":"968856456515268","status":"online"}
                content = new String(body);
                log.info("\nreceive message: "
                        + "\n   topic = " + topic
                        + "\n   messageId = " + messageId
                        + "\n   content = " + content);
                DeviceStatusDTO deviceStatusDTO = GsonUtil.jsonToBean(content, DeviceStatusDTO.class);
                log.info("\n--------------------->YBT cabinet-[{}],Station status: {}", deviceStatusDTO.getDeviceName(), GsonUtil.toJson(deviceStatusDTO));
            } else {
                content = ByteUtils.to16Hexs(body);
                log.info("\nreceive message: "
                        + "\n   topic = " + topic
                        + "\n   messageId = " + messageId
                        + "\n   content = " + content);
                int cmd = SerialPortData.checkCMD(body);
                String deviceName = getDeviceName(topic);
                switch (cmd) {
                    case 0x10:
                        //check check_all
                        ReceiveUpload receiveUpload = new ReceiveUpload(body);
                        log.info("\n--------------------->YBT cabinet-[{}], device heartbeat report: {}", deviceName, GsonUtil.toJson(receiveUpload));
                        break;
                    case 0x21:
                        //popup by position
                        ReceivePopupIndex receivePopupIndex = new ReceivePopupIndex(body);
                        log.info("\n--------------------->YBT cabinet-[{}], hole position popup: {}", deviceName, GsonUtil.toJson(receivePopupIndex));
                        break;
                    case 0x31:
                        //popup by sn
                        ReceivePopupSN receivePopupSN = new ReceivePopupSN(body);
                        log.info("\n--------------------->YBT cabinet-[{}], popup SN: {}", deviceName, GsonUtil.toJson(receivePopupSN));
                        break;
                    case 0X40:
                        //Power bank returned
                        ReceiveReturn receiveReturn = new ReceiveReturn(body);
                        log.info("\n--------------------->YBT cabinet-[{}],Power bank returned: {}", deviceName, GsonUtil.toJson(receiveReturn));
                        break;
                    case 0X7A:
                        //MQTT heartbeat report
                        log.info("\n--------------------->YBT cabinet-[{}],MQTT heartbeat report: {}", deviceName, content);
                        break;
                    case 0xB1:
                        //Version check
                        log.info("\n--------------------->YBT cabinet-[{}],Version check: {}", deviceName, content);
                        break;
                    case 0x7D:
                        //PING network delay
                        log.info("\n--------------------->YBT Cabinet-[{}], PING network delay: {}", deviceName, content);
                        break;
                    case 0x34:
                        //Get APN information
                        log.info("\n--------------------->YBT Cabinet-[{}], Get APN information: {}", deviceName, content);
                        break;
                    case 0xA8:
                        //Device self-test report
                        log.info("\n--------------------->YBT Cabinet-[{}], Port abnormality, device self-test report: {}", deviceName, content);
                        break;
                    case 0x28:
                        //Scenario: The customer returns a power bank when the machine is powered off. After the machine is powered on, the machine will not report the return of the power bank with the current time. Therefore, a new status change command is added.
                        //When a power bank is in a port and its status changes, the machine will report a 28 command to indicate the status change.
                        ReceiveIndexCheck receiveIndexCheck = new ReceiveIndexCheck(body);
                        log.info("\n--------------------->YBT Cabinet-[{}], Status change reported: {}", deviceName, GsonUtil.toJson(receiveIndexCheck.getPowerbank()));
                        break;
                    default:
                        log.info("\n--------------------->YBT Cabinet-[{}], Unknown command data reported: {}", deviceName, content);
                        break;
                }
            }
        } catch (Exception e) {
            log.error("processMessage occurs error ", e);
        }
    }

    private void printResponse(HttpServletResponse response, String content) {
        log.info("Response content：{}", content);
        PrintWriter writer = null;
        try {
            response.setContentType("application/json;charset=utf-8");
            writer = response.getWriter();
            writer.write(content);
            writer.flush();
        } catch (Exception e) {
            log.error("返回异常：", e);
        } finally {
            if (writer != null) {
                writer.close();
            }
        }
    }

    private String getDeviceName(String topic) {
        String[] arr = topic.split("/");
        if (arr.length < 3) {
            return "";
        }
        return arr[2];
    }
}
