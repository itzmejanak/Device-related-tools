package com.brezze.share.communication.controller;

import com.brezze.share.utils.common.enums.hint.Hint;
import com.brezze.share.utils.common.number.NumberUtil;
import com.brezze.share.utils.common.result.Rest;
import com.brezze.share.utils.common.string.StringUtil;
import com.github.xiaoymin.knife4j.annotations.ApiSupport;
import io.swagger.annotations.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Api(tags = "公共接口")
@ApiSupport(author = "penf", order = 1)
@Slf4j
@RestController
@RequestMapping("")
public class BindingController {

    @ApiOperation(httpMethod = "POST", value = "Bind station SN and IMEI", notes = "")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "scanNo", value = "Station SN", dataType = "string", required = true, paramType = "query"),
            @ApiImplicitParam(name = "imei", value = "Station IMEI", dataType = "string", required = true, paramType = "query"),
    })
    @PostMapping("/communication/common/brezze-test-util/cabinets/bind")
    public Rest bind(@RequestParam String scanNo,
                     @RequestParam String imei
    ) {
        if (StringUtil.isEmpty(scanNo)
                || StringUtil.isEmpty(imei)) {
            return Rest.failure(Hint.BAD_PARAMETER);
        }
        if (!NumberUtil.isNumeric(imei) && imei.length() != 15) {
            return Rest.failure(Hint.COMMUNICATION_DEVICE_ERROR_IMEI_FORMAT);
        }
        //1. Check the device SN to see if it already exists. If it does, an error message will be returned.
        if (true) {
            return Rest.failure(Hint.API_DEVICE_HAS_BIND);
        }

        //2. Check the device IMEI to see if the SN number has been bound. If it has, an error message will be returned.
        if (true) {
            return Rest.failure(Hint.API_DEVICE_BIND_REPEAT);
        }

        //3. If no data is found based on IMEI, create a new device and save it. If there is data, update the device SN
        return Rest.failure(Hint.SUCCESS);
    }

    @ApiOperation(httpMethod = "POST", value = "Unbind station SN", notes = "")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "scanNo", value = "Station SN", dataType = "string", required = true, paramType = "query"),
            @ApiImplicitParam(name = "imei", value = "Station IMEI", dataType = "string", required = true, paramType = "query"),
    })
    @PostMapping("/communication/common/brezze-test-util/cabinets/unbind")
    public Rest unbind(@RequestParam String scanNo,
                       @RequestParam String imei
    ) {
        if (StringUtil.isEmpty(scanNo) && StringUtil.isEmpty(imei)) {
            return Rest.failure(Hint.BAD_PARAMETER);
        }
        //1. Check whether the device exists, use the IMEI parameter first, if the IMEI is empty, use the device SN parameter

        //2. When the device does not exist, an error message will be displayed
        if (true) {
            return Rest.failure(Hint.COMMUNICATION_DEVICE_NOT_EXISTS);
        }

        //3. When the device exists, unbind the device SN number
        return Rest.success();
    }
}
