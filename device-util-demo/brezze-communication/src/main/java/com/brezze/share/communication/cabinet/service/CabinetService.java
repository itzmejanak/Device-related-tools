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
     * Get IMEI by device name (cabinet_no or imei)
     * @param deviceName cabinet_no or imei
     * @return IMEI or null if not found
     */
    String getImei(String deviceName);

}
