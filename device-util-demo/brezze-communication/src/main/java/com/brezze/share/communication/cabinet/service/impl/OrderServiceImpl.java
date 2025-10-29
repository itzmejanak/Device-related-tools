package com.brezze.share.communication.cabinet.service.impl;

import com.brezze.share.communication.cabinet.service.OrderService;
import com.brezze.share.communication.cabinet.service.YbtService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDateTime;

@Service
public class OrderServiceImpl implements OrderService {

    @Resource
    private YbtService ybtService;

    @Override
    public void sendOrderInfoViaEmail(String orderNo, String email) {
        //TODO 自实现邮件发送及内容
    }

    @Override
    public void sendReceiptUrl(String orderNo, LocalDateTime startTime, Integer useDuration) {
        String receiptUrl = "收据页面地址，示例：https://xxx.xxx.xxx/receipt/#/email";
        String data = String.format("%s?orderNo=%s&startTime=%s&useDuration=%s", receiptUrl, orderNo, startTime, useDuration);
        String topicMsg = "{\"cmd\":\"order_pos\",\"data\":\"" + data + "\"}";
        //TODO 根据订单信息查询租借设备的IMEI
        String deviceName = "";
        ybtService.sendCmd(deviceName, topicMsg);
    }

    @Override
    public String terminalRentHandler(String cabinetNo, String paymentIntentId) {
        String orderNo = "";
        try {
            //TODO 自实现租借订单业务逻辑
        } catch (Exception be) {
            //TODO 取消预授权订单
            throw be;
        }
        return orderNo;
    }
}
