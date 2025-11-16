package com.brezze.share.communication.cabinet.service;

import com.brezze.share.utils.common.oo.dto.AdvertDTO;

import java.util.List;

/**
 * <p>
 * 机柜广告表 服务类
 * </p>
 *
 * @author Cc
 * @since 2021-12-07
 */
public interface AdvertService {

    /**
     * Get default ads
     * When the device has no advertisements
     *
     * @return
     */
    List<AdvertDTO> getDefault();

    /**
     * Get device ads
     *
     * @param deviceName device IMEI
     * @return
     */
    List<AdvertDTO> getListByImei(String deviceName);
}
