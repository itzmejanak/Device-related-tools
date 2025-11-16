package com.brezze.share.communication.controller;

import com.brezze.share.communication.oo.dto.SysConfigPreAuthAmountDTO;
import com.brezze.share.utils.common.result.Rest;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@Slf4j
@RestController
@RequestMapping("")
public class CommonController {

    @ApiOperationSupport(order = 11)
    @ApiOperation(httpMethod = "GET", produces = "application/json", value = "Get pre-authorization configuration", notes = "Device requests after going online")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "uuid", value = "Station IMEI", dataType = "string", required = true, paramType = "query"),
    })
    @GetMapping("/api/common/config/pre_auth_amount")
    public Rest<SysConfigPreAuthAmountDTO> getPreAuthAmount(HttpServletRequest request,
                                                            String uuid
    ) {
        String deviceMac = request.getHeader("devicemac");
        SysConfigPreAuthAmountDTO dto = new SysConfigPreAuthAmountDTO();
        dto.setValue("1000");
        dto.setComment("");
        dto.setKeyName("pre-auth-amount");
        dto.setCurrency("EUR");
        return Rest.success(dto);
    }
}
