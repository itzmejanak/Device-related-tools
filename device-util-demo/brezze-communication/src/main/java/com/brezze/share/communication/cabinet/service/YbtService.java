package com.brezze.share.communication.cabinet.service;

import com.brezze.share.communication.oo.dto.ConnectTokenDTO;
import com.brezze.share.communication.oo.dto.DeviceDetailDTO;
import com.brezze.share.utils.common.oo.ybt.req.CmdYbtREQ;

public interface YbtService {

    void checkAll(String deviceName);

    DeviceDetailDTO getCheckMessage(String scaNo) throws Exception;

    void popUpByPbNo(String deviceName, String pbNo);

    void popUpByPbNo(CmdYbtREQ req);

    void popUpByIndex(String deviceName, Integer pos, Integer io);

    void popUpByIndex(CmdYbtREQ req);

    void sendCmd(String deviceName, String data);

    void sendLocationId(String deviceName, String locationId);

    ConnectTokenDTO connectToken(String locationId);
}
