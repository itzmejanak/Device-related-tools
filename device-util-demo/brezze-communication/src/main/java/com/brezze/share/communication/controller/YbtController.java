package com.brezze.share.communication.controller;

import com.brezze.share.communication.cabinet.service.AdvertService;
import com.brezze.share.communication.cabinet.service.CabinetService;
import com.brezze.share.communication.cabinet.service.YbtService;
import com.brezze.share.communication.oo.ybt.DeviceConfig;
import com.brezze.share.communication.oo.ybt.HttpResult;
import com.brezze.share.communication.utils.EmqxUtil;
import com.brezze.share.communication.utils.YbtUtil;
import com.brezze.share.utils.common.enums.hint.Hint;
import com.brezze.share.utils.common.json.GsonUtil;
import com.brezze.share.utils.common.oo.ybt.message.ReceiveUpload;
import com.brezze.share.utils.common.oo.ybt.req.CmdYbtREQ;
import com.brezze.share.utils.common.result.Rest;
import com.brezze.share.utils.common.string.StringUtil;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import com.github.xiaoymin.knife4j.annotations.ApiSupport;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@ApiSupport(order = 9)
@Slf4j
@RestController
public class YbtController {

    @Autowired
    private YbtService ybtService;
    @Autowired
    private CabinetService cabinetService;
    @Autowired
    private AdvertService advertService;

    /**
     * /api/iot/client/con	GET	    Android设备鉴权
     * /api/iot/client/con	POST	MCU无屏设备鉴权
     *
     * @param simUUID   SIM卡ICCID
     * @param simMobile SIM卡手机号
     * @param uuid      硬件唯一标识IMEI
     * @param deviceId  系统平台的唯一ID，默认0
     * @param sign      MD5签名
     * @param response
     */
    @ApiOperationSupport(order = 1)
    @ApiOperation(httpMethod = "GET", produces = "application/json", value = "设备鉴权", notes = "Android设备鉴权，设备请求接口")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "simUUID", value = "SIM卡ICCID", dataType = "string", required = false, paramType = "query"),
            @ApiImplicitParam(name = "simMobile", value = "SIM卡手机号", dataType = "string", required = false, paramType = "query"),
            @ApiImplicitParam(name = "uuid", value = "硬件唯一标识IMEI", dataType = "string", required = true, paramType = "query"),
            @ApiImplicitParam(name = "deviceId", value = "系统平台的唯一ID，默认0", dataType = "string", required = true, paramType = "query"),
            @ApiImplicitParam(name = "sign", value = "MD5签名", dataType = "string", required = true, paramType = "query"),
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
            ybtService.checkSign(map, sign);
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
        //获取设备秘钥，返回成功数据
        printResponse(response, GsonUtil.toJson(httpResult));
    }

    /**
     * /api/iot/client/con	GET	    Android设备鉴权
     * /api/iot/client/con	POST	MCU无屏设备鉴权
     *
     * @param simUUID   SIM卡ICCID
     * @param simMobile SIM卡手机号
     * @param uuid      硬件唯一标识IMEI
     * @param deviceId  系统平台的唯一ID，默认0
     * @param sign      MD5签名
     * @param response
     */
    @ApiOperationSupport(order = 2)
    @ApiOperation(httpMethod = "POST", produces = "application/json", value = "设备鉴权", notes = "MCU无屏设备鉴权，设备请求接口")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "simUUID", value = "SIM卡ICCID", dataType = "string", required = false, paramType = "query"),
            @ApiImplicitParam(name = "simMobile", value = "SIM卡手机号", dataType = "string", required = false, paramType = "query"),
            @ApiImplicitParam(name = "uuid", value = "硬件唯一标识IMEI", dataType = "string", required = true, paramType = "query"),
            @ApiImplicitParam(name = "deviceId", value = "系统平台的唯一ID，默认0", dataType = "string", required = true, paramType = "query"),
            @ApiImplicitParam(name = "sign", value = "MD5签名", dataType = "string", required = true, paramType = "query"),
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
        Map<String, String> map = new HashMap<>();
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
            ybtService.checkSign(map, sign);
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

    @ApiOperationSupport(order = 11)
    @ApiOperation(httpMethod = "POST", produces = "application/json", value = "整机上报", notes = "整机上报，设备请求接口")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "bytes", value = "充电宝数据", dataType = "string", required = true, paramType = "query"),
            @ApiImplicitParam(name = "rentboxSN", value = "设备编号(IMEI)", dataType = "string", required = true, paramType = "query"),
            @ApiImplicitParam(name = "sign", value = "MD5签名", dataType = "String", required = true, paramType = "query"),
            @ApiImplicitParam(name = "signal", value = "信号值", dataType = "String", required = false, paramType = "query"),
            @ApiImplicitParam(name = "CRC16", value = "程序CRC校验码", dataType = "String", required = false, paramType = "query"),
            @ApiImplicitParam(name = "IAP16", value = "程序底层校验码", dataType = "String", required = false, paramType = "query"),
            @ApiImplicitParam(name = "dRotationEnable", value = "配置参数", dataType = "String", required = false, paramType = "query"),
            @ApiImplicitParam(name = "dRotationNumber", value = "配置参数", dataType = "String", required = false, paramType = "query"),
            @ApiImplicitParam(name = "dRotationRefer", value = "配置参数", dataType = "String", required = false, paramType = "query"),
            @ApiImplicitParam(name = "dMotorEnable", value = "配置参数", dataType = "String", required = false, paramType = "query"),
    })
    @PostMapping(value = "/api/rentbox/upload/data")
    public void uploadDetail(HttpServletRequest request,
                             HttpServletResponse response,
                             @RequestHeader(required = false) String host,
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
                             @RequestParam(required = false) String dMotorEnable) {

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

    @ApiOperationSupport(order = 12)
    @ApiOperation(httpMethod = "GET", produces = "application/json", value = "归还充电宝上报", notes = "归还充电宝上报，设备请求接口")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "rentboxSN", value = "柜机唯一标识IMEI", dataType = "string", required = true, paramType = "query"),
            @ApiImplicitParam(name = "singleSN", value = "充电宝唯一标识", dataType = "string", required = true, paramType = "query"),
            @ApiImplicitParam(name = "hole", value = "暂未实现", dataType = "string", required = true, paramType = "query"),
            @ApiImplicitParam(name = "sign", value = "MD5签名", dataType = "string", required = true, paramType = "query"),
            @ApiImplicitParam(name = "power", value = "归还电量", dataType = "int", required = false, paramType = "query"),
    })
    @GetMapping(value = "/api/rentbox/order/return")
    public void orderReturn(@RequestParam String rentboxSN,
                            @RequestParam String singleSN,
                            @RequestParam String hole,
                            @RequestParam String sign,
                            @RequestParam(required = false) Integer power,
                            HttpServletResponse response, HttpServletRequest request) {
        HttpResult httpResult = new HttpResult();
        try {
            //校验签名
            Map<String, String> map = new HashMap<>();
            map.put("rentboxSN", rentboxSN);
            map.put("singleSN", singleSN);
            map.put("hole", hole);
            map.put("sign", sign);
            ybtService.checkSign(map, sign);
            cabinetService.powerBankReturn(rentboxSN, singleSN, Integer.parseInt(hole), power);
        } catch (Exception e) {
            response.setStatus(HttpStatus.SC_INTERNAL_SERVER_ERROR);
            httpResult.setCode(response.getStatus());
            httpResult.setMsg(e.toString());
            log.error("归还充电宝上报异常：", e);
        }
        printResponse(response, GsonUtil.toJson(httpResult));
    }

    @ApiOperationSupport(order = 10)
    @ApiOperation(httpMethod = "POST", produces = "application/json", value = "设备上报配置", notes = "设备上报配置，设备请求接口")
    @RequestMapping(value = "/api/rentbox/config/data", method = {RequestMethod.GET, RequestMethod.POST})
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
        //Store the body's JSON data in the database.
        log.info("设备配置信息上报：{}", body);
        HttpResult httpResult = new HttpResult();
        Map<String, Object> map = new HashMap<>();
        map.put("dRotationRefer", "15");
        map.put("dReturnLocked", "0");
        map.put("dHeadConfig", "43");
        map.put("dRotationNumber", "4");
        map.put("dRotationEnable", "1");
        map.put("dMotorEnable", "1");
        map.put("dAreaConfig", "343");
        httpResult.setData(GsonUtil.toJson(map));
        //返回成功数据
        printResponse(response, GsonUtil.toJson(httpResult));
    }

    @ApiOperationSupport(order = 15)
    @ApiOperation(httpMethod = "GET", produces = "application/json", value = "获取MCU升级（正式）数据", notes = "仅支持不带屏幕设备，4G模块MCU在线升级。（设备请求接口）")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "appUuid", value = "版本唯一标识", dataType = "string", required = true, paramType = "query"),
            @ApiImplicitParam(name = "deviceUuid", value = "设备唯一标识", dataType = "string", required = false, paramType = "query"),
            @ApiImplicitParam(name = "sign", value = "MD5签名", dataType = "string", required = true, paramType = "query"),
    })
    @GetMapping(value = "/api/iot/app/version/publish/mcu")
    public void getMcuReleaseVersion(@RequestParam String appUuid,
                                     @RequestParam(required = false, defaultValue = "") String deviceUuid,
                                     @RequestParam String sign,
                                     HttpServletResponse response) {

        HttpResult httpResult = new HttpResult();
        try {
            //校验签名
            Map<String, String> map = new HashMap<>();
            map.put("appUuid", appUuid);
            map.put("deviceUuid", deviceUuid);
            map.put("sign", sign);
            ybtService.checkSign(map, sign);
            String data = ybtService.getVersionInfo(appUuid, true);
            httpResult.setData(data);
        } catch (Exception e) {
            response.setStatus(HttpStatus.SC_INTERNAL_SERVER_ERROR);
            httpResult.setCode(response.getStatus());
            httpResult.setMsg(e.toString());
            log.error("MCU升级（正式）异常：", e);
        }
        printResponse(response, GsonUtil.toJson(httpResult));
    }

    @ApiOperationSupport(order = 16)
    @ApiOperation(httpMethod = "GET", produces = "application/json", value = "获取MCU升级（测试）数据", notes = "仅支持不带屏幕设备，4G模块MCU在线升级。（设备请求接口）")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "appUuid", value = "版本唯一标识", dataType = "string", required = true, paramType = "query"),
            @ApiImplicitParam(name = "deviceUuid", value = "设备唯一标识", dataType = "string", required = false, paramType = "query"),
            @ApiImplicitParam(name = "sign", value = "MD5签名", dataType = "string", required = true, paramType = "query"),
    })
    @GetMapping(value = "/api/iot/app/version/test/mcu")
    public void getMcuTestVersion(@RequestParam String appUuid,
                                  @RequestParam(required = false, defaultValue = "") String deviceUuid,
                                  @RequestParam String sign,
                                  HttpServletResponse response) {

        HttpResult httpResult = new HttpResult();
        try {
            //校验签名
            Map<String, String> map = new HashMap<>();
            map.put("appUuid", appUuid);
            map.put("deviceUuid", deviceUuid);
            map.put("sign", sign);
            ybtService.checkSign(map, sign);
            String data = ybtService.getVersionInfo(appUuid, false);
            httpResult.setData(data);
        } catch (Exception e) {
            response.setStatus(HttpStatus.SC_INTERNAL_SERVER_ERROR);
            httpResult.setCode(response.getStatus());
            httpResult.setMsg(e.toString());
            log.error("MCU升级（测试）异常：", e);
        }
        printResponse(response, GsonUtil.toJson(httpResult));
    }

    @ApiOperationSupport(order = 17)
    @ApiOperation(httpMethod = "GET", produces = "application/json", value = "获取机芯地址（正式）数据", notes = "获取机芯升级数据（设备请求接口）")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "appUuid", value = "版本唯一标识", dataType = "string", required = true, paramType = "query"),
            @ApiImplicitParam(name = "deviceUuid", value = "设备唯一标识", dataType = "string", required = false, paramType = "query"),
            @ApiImplicitParam(name = "sign", value = "MD5签名", dataType = "string", required = true, paramType = "query"),
    })
    @GetMapping(value = "/api/iot/app/version/publish/chip")
    public void getChipReleaseVersion(@RequestParam String appUuid,
                                      @RequestParam(required = false, defaultValue = "") String deviceUuid,
                                      @RequestParam String sign,
                                      HttpServletResponse response) {

        HttpResult httpResult = new HttpResult();
        try {
            //校验签名
            Map<String, String> map = new HashMap<>();
            map.put("appUuid", appUuid);
            map.put("deviceUuid", deviceUuid);
            map.put("sign", sign);
            ybtService.checkSign(map, sign);
            String data = ybtService.getVersionInfo(appUuid, true);
            httpResult.setData(data);
        } catch (Exception e) {
            response.setStatus(HttpStatus.SC_INTERNAL_SERVER_ERROR);
            httpResult.setCode(response.getStatus());
            httpResult.setMsg(e.toString());
            log.error("机芯地址（正式）异常：", e);
        }
        printResponse(response, GsonUtil.toJson(httpResult));
    }

    @ApiOperationSupport(order = 18)
    @ApiOperation(httpMethod = "GET", produces = "application/json", value = "获取机芯地址（测试）数据", notes = "获取机芯升级数据（设备请求接口）")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "appUuid", value = "版本唯一标识", dataType = "string", required = true, paramType = "query"),
            @ApiImplicitParam(name = "deviceUuid", value = "设备唯一标识", dataType = "string", required = false, paramType = "query"),
            @ApiImplicitParam(name = "sign", value = "MD5签名", dataType = "string", required = true, paramType = "query"),
    })
    @GetMapping(value = "/api/iot/app/version/test/chip")
    public void getChipTestVersion(@RequestParam String appUuid,
                                   @RequestParam(required = false, defaultValue = "") String deviceUuid,
                                   @RequestParam String sign,
                                   HttpServletResponse response) {

        HttpResult httpResult = new HttpResult();
        try {
            //校验签名
            Map<String, String> map = new HashMap<>();
            map.put("appUuid", appUuid);
            map.put("deviceUuid", deviceUuid);
            map.put("sign", sign);
            ybtService.checkSign(map, sign);
            String data = ybtService.getVersionInfo(appUuid, false);
            httpResult.setData(data);
        } catch (Exception e) {
            response.setStatus(HttpStatus.SC_INTERNAL_SERVER_ERROR);
            httpResult.setCode(response.getStatus());
            httpResult.setMsg(e.toString());
            log.error("机芯地址（测试）异常：", e);
        }
        printResponse(response, GsonUtil.toJson(httpResult));
    }

    @ApiOperationSupport(order = 19)
    @ApiOperation(httpMethod = "GET", produces = "application/json", value = "获取广告列表", notes = "Android程序更新图片、视频广告。（设备请求接口）")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "uuid", value = "设备编号(IMEI)", dataType = "string", required = true, paramType = "query"),
            @ApiImplicitParam(name = "position", value = "充电宝唯一标识", dataType = "string", required = true, paramType = "query"),
            @ApiImplicitParam(name = "sign", value = "MD5签名", dataType = "string", required = true, paramType = "query"),
    })
    @GetMapping(value = "/api/advert/rentbox/distribute/list")
    public void getAdvertList(HttpServletRequest request,
                              HttpServletResponse response,
                              @RequestParam String uuid,
                              @RequestParam String position,
                              @RequestParam String sign
    ) {
        HttpResult httpResult = new HttpResult();
        try {
            //校验签名
            Map<String, String> map = new HashMap<>();
            map.put("uuid", uuid);
            map.put("position", position);
            map.put("sign", sign);
            ybtService.checkSign(map, sign);
            httpResult.setData(advertService.getListByImei(uuid));
        } catch (Exception e) {
            response.setStatus(HttpStatus.SC_INTERNAL_SERVER_ERROR);
            httpResult.setCode(response.getStatus());
            httpResult.setMsg(e.toString());
            log.error("获取广告列表异常：", e);
        }
        printResponse(response, GsonUtil.toJson(httpResult));
    }

    @ApiOperationSupport(order = 33)
    @ApiOperation(httpMethod = "POST", produces = "application/json", value = "发送指令-通用", notes = "")
    @PostMapping(value = "/communication/ybt/send")
    public void sendCmd(HttpServletResponse response,
                        @RequestBody CmdYbtREQ req
    ) {
        Rest rest = Rest.success();
        if (StringUtil.isEmpty(req.getCabinetNo()) || StringUtil.isEmpty(req.getData())) {
            rest = Rest.failure(Hint.BAD_PARAMETER);
        }
        ybtService.sendCmd(req.getCabinetNo(), req.getData());
        //返回成功数据
        printResponse(response, GsonUtil.toJson(rest));
    }

    /********************************************Test tool interface****************************************************/
    @ApiOperationSupport(order = 99)
    @ApiOperation(httpMethod = "GET", produces = "application/json", value = "Get Station Detail", notes = "")
    @GetMapping(value = "/communication/ybt/check-all")
    public void checkAll(HttpServletResponse response, String scanNo) throws Exception {

        Rest rest = Rest.success(ybtService.getDeviceDetail(scanNo));
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
    /********************************************Test tool interface****************************************************/

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
}
