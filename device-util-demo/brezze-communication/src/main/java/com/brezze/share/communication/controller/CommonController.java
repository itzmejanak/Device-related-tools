package com.brezze.share.communication.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.brezze.share.communication.oo.dto.SysConfigPreAuthAmountDTO;
import com.brezze.share.utils.common.enums.hint.Hint;
import com.brezze.share.utils.common.result.Rest;
import com.brezze.share.utils.common.string.StringUtil;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;

import io.swagger.annotations.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("")
public class CommonController {

    @ApiOperationSupport(order = 11)
    @ApiOperation(httpMethod = "GET", produces = "application/json", value = "获取预授权配置", notes = "设备上线之后会请求")
    @ApiImplicitParams({

    })
    @GetMapping("/api/common/config/pre_auth_amount")
    public Rest<SysConfigPreAuthAmountDTO> getPreAuthAmount(HttpServletRequest request) {
        String deviceMac = request.getHeader("devicemac");
        SysConfigPreAuthAmountDTO dto = new SysConfigPreAuthAmountDTO();
        dto.setValue("1000");
        dto.setComment("");
        dto.setKeyName("pre-auth-amount");
        dto.setCurrency("EUR");
        return Rest.success(dto);
    }
}
