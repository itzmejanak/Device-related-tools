package com.brezze.share.communication.controller;

import com.brezze.share.communication.cabinet.service.OrderService;
import com.brezze.share.utils.common.result.Rest;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import com.github.xiaoymin.knife4j.annotations.ApiSupport;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@Slf4j
@Validated
@ApiSupport(order = 4)
@RestController
@RequestMapping("")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @ApiOperationSupport(order = 26)
    @ApiOperation(httpMethod = "POST", value = "Get order information", notes = "the order QR code displayed on the device.")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "orderNo", value = "order no", dataType = "string", required = true, paramType = "query"),
            @ApiImplicitParam(name = "email", value = "email", dataType = "string", required = true, paramType = "query"),
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
