package com.brezze.share.communication.cabinet.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.brezze.share.communication.cabinet.entity.Cabinet;


/**
 * <p>
 * 机柜服务类
 * </p>
 *
 * @author penf
 * @since 2021-03-22
 */
public interface CabinetService extends IService<Cabinet> {

    /**
     * Obtain the device's IMEI
     *
     * Modify according to your actual database structure
     *
     *
     *
     * @param deviceName Device SN/Device IMEI
     * @return Device IMEI
     */
    String getImei(String deviceName);

    void powerBankReturn(String deviceName, String pbNo, Integer pos, Integer bc);
}
