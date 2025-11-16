package com.brezze.share.communication.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.brezze.share.communication.cabinet.entity.BaseEntity;
import com.brezze.share.communication.cabinet.entity.Cabinet;
import com.brezze.share.communication.cabinet.service.CabinetService;
import com.brezze.share.utils.common.constant.CabinetCst;
import com.brezze.share.utils.common.enums.hint.Hint;
import com.brezze.share.utils.common.number.NumberUtil;
import com.brezze.share.utils.common.result.Rest;
import com.brezze.share.utils.common.string.StringUtil;
import com.github.xiaoymin.knife4j.annotations.ApiSupport;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Api(tags = "Binding interface")
@ApiSupport(author = "penf", order = 1)
@Slf4j
@RestController
@RequestMapping("")
public class BindingController {

    @Autowired
    private CabinetService cabinetService;

    @ApiOperation(httpMethod = "POST", value = "Bind station SN and IMEI", notes = "")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "scanNo", value = "Station SN", dataType = "string", required = true, paramType = "query"),
            @ApiImplicitParam(name = "imei", value = "Station IMEI", dataType = "string", required = true, paramType = "query"),
    })
    @PostMapping("/communication/common/brezze-test-util/cabinets/bind")
    public Rest bind(@RequestParam String scanNo,
                     @RequestParam String imei,
                     @RequestParam(required = false) String vietqr
    ) {
        if (StringUtil.isEmpty(scanNo)
                || StringUtil.isEmpty(imei)) {
            return Rest.failure(Hint.BAD_PARAMETER);
        }
        if (!NumberUtil.isNumeric(imei) && imei.length() != 15) {
            return Rest.failure(Hint.COMMUNICATION_DEVICE_ERROR_IMEI_FORMAT);
        }
        LambdaQueryWrapper<Cabinet> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Cabinet::getCabinetNo, scanNo);
        List<Cabinet> cabinetList = cabinetService.list(wrapper);
        // Have bind
        if (!cabinetList.isEmpty()) {
            return Rest.failure(Hint.API_DEVICE_HAS_BIND);
        }
        LambdaQueryWrapper<Cabinet> cabinetWrapper = new LambdaQueryWrapper<>();
        cabinetWrapper.eq(Cabinet::getImei, imei);
        Cabinet cabinet = cabinetService.getOne(cabinetWrapper);
        if (cabinet == null) {
            cabinet = new Cabinet();
            cabinet.setImei(imei)
                    .setCabinetNo(scanNo)
                    .setState(CabinetCst.UNACTIVE)
                    .setVietqr(vietqr);
            cabinetService.saveOrUpdate(cabinet);
            return Rest.failure(Hint.SUCCESS);
        }
        if (StringUtil.isNotEmpty(cabinet.getCabinetNo()) && !cabinet.getCabinetNo().equalsIgnoreCase(cabinet.getImei())) {
            return Rest.failure(Hint.API_DEVICE_BIND_REPEAT);
        }
        cabinet.setCabinetNo(scanNo);
        cabinet.setVietqr(vietqr);
        cabinetService.saveOrUpdate(cabinet);
        return Rest.success();
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
        LambdaQueryWrapper<Cabinet> cabinetWrapper = new LambdaQueryWrapper<>();
        if (StringUtil.isNotEmpty(imei)) {
            if (!NumberUtil.isNumeric(imei) && imei.length() != 15) {
                return Rest.failure(Hint.COMMUNICATION_DEVICE_ERROR_IMEI_FORMAT);
            }
            cabinetWrapper.eq(Cabinet::getImei, imei);
        } else {
            cabinetWrapper.eq(Cabinet::getCabinetNo, scanNo);
        }
        List<Cabinet> cabinetList = cabinetService.list(cabinetWrapper);
        if (cabinetList.isEmpty()) {
            return Rest.failure(Hint.COMMUNICATION_DEVICE_NOT_EXISTS);
        }
        LambdaUpdateWrapper<Cabinet> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.set(Cabinet::getCabinetNo, cabinetList.get(0).getImei())
                .set(Cabinet::getVietqr, null)
                .eq(BaseEntity::getId, cabinetList.get(0).getId());
        cabinetService.update(updateWrapper);
        return Rest.success();
    }
}
