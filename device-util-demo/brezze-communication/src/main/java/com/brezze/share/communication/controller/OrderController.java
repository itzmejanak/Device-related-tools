package com.brezze.share.communication.controller;

import com.brezze.share.communication.cabinet.service.OrderService;
import com.brezze.share.utils.common.result.Rest;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import com.github.xiaoymin.knife4j.annotations.ApiSupport;
import io.swagger.annotations.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

@Slf4j
@Validated
@ApiSupport(order = 4)
@RestController
@RequestMapping("")
public class OrderController {

    @Resource
    private OrderService orderService;

    @ApiOperationSupport(order = 26)
    @ApiOperation(httpMethod = "POST", value = "获取订单信息", notes = "用户扫设备上显示的订单二维码")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "orderNo", value = "订单号", dataType = "string", required = true, paramType = "query"),
            @ApiImplicitParam(name = "email", value = "邮箱", dataType = "string", required = true, paramType = "query"),
    })
    @PostMapping("/api/orders/email")
    public Rest getOrderInfoViaEmail(HttpServletRequest request,
                                     @RequestParam String orderNo,
                                     @RequestParam String email
    ) {
        orderService.sendOrderInfoViaEmail(orderNo, email);
        return Rest.success();
    }

}
