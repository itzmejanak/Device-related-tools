package com.brezze.share.communication.controller;

import com.brezze.share.communication.cabinet.service.OrderService;
import com.brezze.share.communication.cabinet.service.YbtService;
import com.brezze.share.communication.oo.dto.ConnectTokenDTO;
import com.brezze.share.communication.oo.dto.RentDTO;
import com.brezze.share.utils.common.result.Rest;

import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("")
public class UserController {

    @Resource
    private YbtService ybtService;
    @Resource
    private OrderService orderService;

    @ApiOperationSupport(order = 25)
    @ApiOperation(httpMethod = "GET", value = "获取stripe终端连接秘钥", notes = "")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "locationId", value = "locationId", dataType = "String", required = false, paramType = "query"),
    })
    @GetMapping("/api/users/stripe/terminal/connect-token")
    public Rest<ConnectTokenDTO> getConnectToken(HttpServletRequest request,
                                                 @RequestParam(required = false) String locationId
    ) {
        //请求头内获取设备SN号
        String deviceMac = request.getHeader("devicemac");
        //从设备信息中获取locationId

        ConnectTokenDTO dto = ybtService.connectToken(locationId);
        return Rest.success(dto);
    }

    @ApiOperationSupport(order = 26)
    @ApiOperation(httpMethod = "GET", value = "接收已授权的支付", notes = "")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "paymentIntentId", value = "支付意图ID", dataType = "string", required = true, paramType = "query"),
            @ApiImplicitParam(name = "paymentMethod", value = "支付方式ID", dataType = "string", required = true, paramType = "query"),
            @ApiImplicitParam(name = "cabinetNo", value = "机柜扫码编号", dataType = "string", required = true, paramType = "query"),
    })
    @GetMapping("/api/users/payment/captured")
    public Rest<RentDTO> capturedPayment(HttpServletRequest request,
                                         @RequestParam(required = false) String paymentIntentId,
                                         @RequestParam(required = false) String paymentMethod,
                                         @RequestParam(required = false) String cabinetNo
    ) {
        String orderNo = orderService.terminalRentHandler(cabinetNo, paymentIntentId);
        RentDTO dto = new RentDTO();
        dto.setOrderNo(orderNo);
        return Rest.success(dto);
    }
}
