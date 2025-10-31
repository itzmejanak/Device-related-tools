package com.brezze.share.communication.cabinet.service;

import java.time.LocalDateTime;

public interface OrderService {

    /**
     * 发送订单信息邮件
     *
     * @param orderNo 租借订单号
     * @param email   邮箱
     */
    void sendOrderInfoViaEmail(String orderNo, String email);

    /**
     * 租借、归还成功时发送收据消息，屏幕会显示二维码
     *
     * @param orderNo     租借订单号
     * @param startTime   开始时间/结束时间
     * @param useDuration 使用时长（归还时赋值，单位：秒）
     */
    void sendReceiptUrl(String orderNo, LocalDateTime startTime, Integer useDuration);

    /**
     * 终端租借处理器
     *
     * @param cabinetNo       设备SN
     * @param paymentIntentId 支付意图ID
     */
    String terminalRentHandler(String cabinetNo, String paymentIntentId);
}
