package com.brezze.share.communication.cabinet.service;

import com.brezze.share.communication.oo.dto.ConnectTokenDTO;
import com.brezze.share.communication.oo.dto.DeviceDetailDTO;
import com.brezze.share.communication.oo.dto.DeviceStatusDTO;
import com.brezze.share.utils.common.oo.ybt.message.ReceiveWifi;
import com.brezze.share.utils.common.oo.ybt.req.CmdYbtREQ;

public interface YbtService {

    /**
     * 消息上下线消息
     *
     * @param dto
     */
    void deviceStatus(DeviceStatusDTO dto);

    /**
     * 查询明细
     *
     * @param deviceName
     */
    void checkAll(String deviceName);

    DeviceDetailDTO getDeviceDetail(String deviceName) throws Exception;

    /**
     * 测试工具-查询明细
     *
     * @param scaNo
     * @return
     * @throws Exception
     */
    DeviceDetailDTO getCheckMessage(String scaNo) throws Exception;

    /**
     * 充电宝SN弹出
     *
     * @param deviceName
     * @param pbNo
     */
    void popUpByPbNo(String deviceName, String pbNo);

    /**
     * 充电宝SN弹出-延迟消息
     *
     * @param req
     */
    void popUpByPbNo(CmdYbtREQ req);

    /**
     * 孔位弹出
     *
     * @param deviceName
     * @param pos
     * @param io
     */
    void popUpByIndex(String deviceName, Integer pos, Integer io);

    /**
     * 孔位弹出-延迟消息
     *
     * @param req
     */
    void popUpByIndex(CmdYbtREQ req);

    void checkSign(Object valid, String sign) throws Exception;

    /**
     * 设备指令下发
     *
     * @param deviceName
     * @param data
     */
    void sendCmd(String deviceName, String data);

    /**
     * POS相关-下发location ID
     *
     * @param deviceName
     * @param locationId
     */
    void sendLocationId(String deviceName, String locationId);

    /**
     * 获取stripe连接token
     *
     * @param locationId
     * @return
     */
    ConnectTokenDTO connectToken(String locationId);

    String getVersionInfo(String appUuid, Boolean releaseModel);

    void getWifi(String deviceName);

    ReceiveWifi getWifiMessage(String deviceName);
}
