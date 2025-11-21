package com.brezze.share.communication.controller;

import com.brezze.share.communication.cabinet.service.CabinetService;
import com.brezze.share.communication.cabinet.service.YbtService;
import com.brezze.share.utils.common.json.GsonUtil;
import com.brezze.share.utils.common.oo.ybt.req.CmdYbtREQ;
import com.brezze.share.utils.common.result.Rest;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import com.github.xiaoymin.knife4j.annotations.ApiSupport;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;

@ApiSupport(order = 9)
@Slf4j
@RestController
public class YbtController {

    @Resource
    private YbtService ybtService;
    
    @Resource
    private CabinetService cabinetService;

    @ApiOperationSupport(order = 99)
    @ApiOperation(httpMethod = "GET", produces = "application/json", value = "Get Station Detail", notes = "")
    @GetMapping(value = "/communication/ybt/check-all")
    public void checkAll(HttpServletResponse response, String scanNo) throws Exception {

        // Get IMEI from cabinet_no or use scanNo directly if not found
        String imei = cabinetService.getImei(scanNo);
        String deviceName = (imei != null) ? imei : scanNo;

        Rest rest = Rest.success(ybtService.getCheckMessage(deviceName));

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
