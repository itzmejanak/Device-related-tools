package com.brezze.share.communication.controller;

import cn.hutool.json.JSONUtil;
import com.brezze.share.communication.cabinet.service.YbtService;
import com.brezze.share.utils.common.enums.hint.Hint;
import com.brezze.share.utils.common.oo.ybt.message.ReceiveWifi;
import com.brezze.share.utils.common.oo.ybt.req.CmdYbtREQ;
import com.brezze.share.utils.common.result.Rest;
import com.brezze.share.utils.common.string.StringUtil;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import com.github.xiaoymin.knife4j.annotations.ApiSupport;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.LinkedHashMap;

@ApiSupport(order = 10)
@Slf4j
@RestController
public class MqttCmdController {

    @Autowired
    private YbtService ybtService;

    @ApiOperationSupport(order = 3)
    @ApiOperation(httpMethod = "POST", produces = "application/json", value = "Partial query", notes = "Query 5 power banks with highest power")
    @PostMapping(value = "/communication/ybt/check")
    public Rest detail(@RequestBody CmdYbtREQ req
    ) {
        if (StringUtil.isEmpty(req.getCabinetNo())) {
            return Rest.failure(Hint.BAD_PARAMETER);
        }
        String topicMsg = "{\"cmd\":\"check\"}";
        ybtService.sendCmd(req.getCabinetNo(), topicMsg);
        //返回成功数据
        return Rest.success();
    }

    @ApiOperationSupport(order = 4)
    @ApiOperation(httpMethod = "POST", produces = "application/json", value = "Full query", notes = "Query whole single cabinet data")
    @PostMapping(value = "/communication/ybt/check-all")
    public Rest detailAll(@RequestBody CmdYbtREQ req, HttpServletRequest request
    ) {
        if (StringUtil.isEmpty(req.getCabinetNo())) {
            return Rest.failure(Hint.BAD_PARAMETER);
        }
        String topicMsg = "{\"cmd\":\"check_all\"}";
        ybtService.sendCmd(req.getCabinetNo(), topicMsg);
        //返回成功数据
        return Rest.success();
    }

    @ApiOperationSupport(order = 5)
    @ApiOperation(httpMethod = "POST", produces = "application/json", value = "Open lock by position", notes = "")
    @PostMapping(value = "/communication/ybt/openLock")
    public Rest openLock(@RequestBody CmdYbtREQ req, HttpServletRequest request
    ) {
        if (StringUtil.isEmpty(req.getCabinetNo()) ||
                req.getPos() == null ||
                req.getIo() == null) {
            return Rest.failure(Hint.BAD_PARAMETER);
        }
        ybtService.popUpByIndex(req);
        //返回成功数据
        return Rest.success();
    }


    @ApiOperationSupport(order = 6)
    @ApiOperation(httpMethod = "POST", produces = "application/json", value = "Pop up by SN number", notes = "")
    @PostMapping(value = "/communication/ybt/popup_sn")
    public Rest rentByPbNo(@RequestBody CmdYbtREQ req, HttpServletRequest request
    ) {
        if (StringUtil.isEmpty(req.getCabinetNo()) ||
                StringUtil.isEmpty(req.getPbNo())) {
            return Rest.failure(Hint.BAD_PARAMETER);
        }
        ybtService.popUpByPbNo(req.getCabinetNo(), req.getPbNo());
        //返回成功数据
        return Rest.success();
    }

    @ApiOperationSupport(order = 7)
    @ApiOperation(httpMethod = "POST", produces = "application/json", value = "Reset MCU", notes = "")
    @PostMapping(value = "/communication/ybt/reset-mcu")
    public Rest mcuReset(@RequestBody CmdYbtREQ req, HttpServletRequest request
    ) {
        if (StringUtil.isEmpty(req.getCabinetNo())) {
            return Rest.failure(Hint.BAD_PARAMETER);
        }
        String topicMsg = "{\"cmd\":\"reset_mcu\"}";
        ybtService.sendCmd(req.getCabinetNo(), topicMsg);
        //返回成功数据
        return Rest.success();
    }


    @ApiOperationSupport(order = 9)
    @ApiOperation(httpMethod = "POST", produces = "application/json", value = "Restart cabinet", notes = "")
    @PostMapping(value = "/communication/ybt/restart")
    public Rest restart(@RequestBody CmdYbtREQ req, HttpServletRequest request
    ) {
        if (StringUtil.isEmpty(req.getCabinetNo())) {
            return Rest.failure(Hint.BAD_PARAMETER);
        }
        String topicMsg = "{\"cmd\":\"reboot\"}";
        ybtService.sendCmd(req.getCabinetNo(), topicMsg);
        //返回成功数据
        return Rest.success();
    }

    @ApiOperationSupport(order = 20)
    @ApiOperation(httpMethod = "POST", produces = "application/json", value = "Update advertisement", notes = "")
    @PostMapping(value = "/communication/ybt/load-ad")
    public Rest loadAd(@RequestBody CmdYbtREQ req
    ) {
        if (StringUtil.isEmpty(req.getCabinetNo())) {
            return Rest.failure(Hint.BAD_PARAMETER);
        }
        String topicMsg = "{\"cmd\":\"load_ad\"}";
        ybtService.sendCmd(req.getCabinetNo(), topicMsg);
        //返回成功数据
        return Rest.success();
    }

    @ApiOperationSupport(order = 21)
    @ApiOperation(httpMethod = "POST", produces = "application/json", value = "Push Android or 4G module upgrade", notes = "")
    @PostMapping(value = "/communication/ybt/push-version-publish")
    public Rest pushVersionPublish(@RequestBody CmdYbtREQ req
    ) {
        if (StringUtil.isEmpty(req.getCabinetNo())) {
            return Rest.failure(Hint.BAD_PARAMETER);
        }
        String topicMsg = "{\"cmd\":\"push_version_publish\"}";
        ybtService.sendCmd(req.getCabinetNo(), topicMsg);
        //返回成功数据
        return Rest.success();
    }

    @ApiOperationSupport(order = 22)
    @ApiOperation(httpMethod = "POST", produces = "application/json", value = "Push Android or 4G module test upgrade", notes = "")
    @PostMapping(value = "/communication/ybt/push-version-test")
    public Rest pushVersionTest(@RequestBody CmdYbtREQ req
    ) {
        if (StringUtil.isEmpty(req.getCabinetNo())) {
            return Rest.failure(Hint.BAD_PARAMETER);
        }
        String topicMsg = "{\"cmd\":\"push_version_test\"}";
        ybtService.sendCmd(req.getCabinetNo(), topicMsg);
        //返回成功数据
        return Rest.success();
    }

    @ApiOperationSupport(order = 24)
    @ApiOperation(httpMethod = "POST", produces = "application/json", value = "Push holesite release upgrade", notes = "")
    @PostMapping(value = "/communication/ybt/push-version-holesite")
    public Rest pushVersionHolesite(@RequestBody CmdYbtREQ req
    ) {
        if (StringUtil.isEmpty(req.getCabinetNo())) {
            return Rest.failure(Hint.BAD_PARAMETER);
        }
        String topicMsg = "{\"cmd\":\"push_version_holesite\"}";
        ybtService.sendCmd(req.getCabinetNo(), topicMsg);
        //返回成功数据
        return Rest.success();
    }

    @ApiOperationSupport(order = 26)
    @ApiOperation(httpMethod = "POST", produces = "application/json", value = "Push the whole cabinet report", notes = "")
    @PostMapping(value = "/communication/ybt/upload-all")
    public Rest uploadAll(@RequestBody CmdYbtREQ req
    ) {
        if (StringUtil.isEmpty(req.getCabinetNo())) {
            return Rest.failure(Hint.BAD_PARAMETER);
        }
        String topicMsg = "{\"cmd\":\"upload_all\"}";
        ybtService.sendCmd(req.getCabinetNo(), topicMsg);
        //返回成功数据
        return Rest.success();
    }

    @ApiOperationSupport(order = 27)
    @ApiOperation(httpMethod = "POST", produces = "application/json", value = "Set WiFi", notes = "普及版机柜")
    @PostMapping(value = "/communication/ybt/set-wifi")
    public Rest setWifi(@RequestBody CmdYbtREQ req
    ) {
        if (StringUtil.isEmpty(req.getCabinetNo())) {
            return Rest.failure(Hint.BAD_PARAMETER);
        }
        if (StringUtil.isEmpty(req.getPassword())) {
            req.setPassword("");
        }
        LinkedHashMap<String, String> params = new LinkedHashMap<>();
        params.put("cmd", "setWifi");
        params.put("username", req.getUsername());
        params.put("password", req.getPassword());
        String topicMsg = JSONUtil.toJsonStr(params);
        ybtService.sendCmd(req.getCabinetNo(), topicMsg);
        //返回成功数据
        return Rest.success();
    }

    @ApiOperationSupport(order = 28)
    @ApiOperation(httpMethod = "POST", produces = "application/json", value = "Set volume", notes = "普及版机柜")
    @PostMapping(value = "/communication/ybt/volume")
    public Rest volume(@RequestBody CmdYbtREQ req
    ) {
        if (StringUtil.isEmpty(req.getCabinetNo())) {
            return Rest.failure(Hint.BAD_PARAMETER);
        }
        if (StringUtil.isEmpty(req.getPassword())) {
            req.setPassword("");
        }
        //{"cmd":"volume","data":"70"}
        // data value: 0~100
        LinkedHashMap<String, String> params = new LinkedHashMap<>();
        params.put("cmd", "volume");
        params.put("data", req.getData());
        String topicMsg = JSONUtil.toJsonStr(params);
        ybtService.sendCmd(req.getCabinetNo(), topicMsg);
        //返回成功数据
        return Rest.success();
    }

    @ApiOperationSupport(order = 28)
    @ApiOperation(httpMethod = "POST", produces = "application/json", value = "Set audio", notes = "普及版机柜")
    @PostMapping(value = "/communication/ybt/setAudio")
    public Rest setAudio(@RequestBody CmdYbtREQ req
    ) {
        if (StringUtil.isEmpty(req.getCabinetNo())) {
            return Rest.failure(Hint.BAD_PARAMETER);
        }
        if (StringUtil.isEmpty(req.getPassword())) {
            req.setPassword("");
        }
        /*
          {"cmd":"setAudio","data":"https://s.besiter.com.cn/sw/audio/en.json"}

          json data:
          {
              "code": 0,
              "data": {
                  "welcome.mp3": "Full HTTP audio URL",
                  "LeaseFailed.mp3": "Full HTTP audio URL",
                  "LeaseSucceed.mp3": "Full HTTP audio URL",
                  "ReturnFailed.mp3": "Full HTTP audio URL",
                  "ReturnSucceed.mp3": "Full HTTP audio URL"
              }
          }
         */
        LinkedHashMap<String, String> params = new LinkedHashMap<>();
        params.put("cmd", "setAudio");
        //data value: wifi/4G
        params.put("data", req.getData());
        String topicMsg = JSONUtil.toJsonStr(params);
        ybtService.sendCmd(req.getCabinetNo(), topicMsg);
        //返回成功数据
        return Rest.success();
    }

    @ApiOperationSupport(order = 28)
    @ApiOperation(httpMethod = "POST", produces = "application/json", value = "Set network priority", notes = "普及版机柜")
    @PostMapping(value = "/communication/ybt/setMode")
    public Rest setMode(@RequestBody CmdYbtREQ req
    ) {
        if (StringUtil.isEmpty(req.getCabinetNo())) {
            return Rest.failure(Hint.BAD_PARAMETER);
        }
        if (StringUtil.isEmpty(req.getPassword())) {
            req.setPassword("");
        }
        /*
        {"cmd":"setMode","data":"4g"} //优先4G工作
        {"cmd":"setMode","data":"wifi"} //优先WIFI工作
         */
        LinkedHashMap<String, String> params = new LinkedHashMap<>();
        params.put("cmd", "setMode");
        params.put("data", req.getData());
        String topicMsg = JSONUtil.toJsonStr(params);
        ybtService.sendCmd(req.getCabinetNo(), topicMsg);
        //返回成功数据
        return Rest.success();
    }

    @ApiOperationSupport(order = 104)
    @ApiOperation(httpMethod = "POST", produces = "application/json", value = "Get WiFi list", notes = "")
    @PostMapping(value = "/communication/ybt/wifi")
    public Rest<ReceiveWifi> getWifiList(@RequestBody CmdYbtREQ req) throws Exception {
        return Rest.success(ybtService.getWifiMessage(req.getCabinetNo()));
    }
}
