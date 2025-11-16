package com.brezze.share.communication.cabinet.service.impl;

import com.brezze.share.communication.cabinet.service.AdvertService;
import com.brezze.share.utils.common.oo.dto.AdvertDTO;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * 机柜广告表 服务实现类
 * </p>
 *
 * @author Cc
 * @since 2021-12-07
 */
@Service
public class AdvertServiceImpl implements AdvertService {

    @Override
    public List<AdvertDTO> getDefault() {
        //Retrieve the default ad from the database.
        return getAdExample();
    }

    @Override
    public List<AdvertDTO> getListByImei(String deviceName) {
        //Retrieve the ads configured for the device from the database.
//        List<AdvertDTO> dtoList = this.baseMapper.getListByImei(deviceName);
        List<AdvertDTO> dtoList = new ArrayList<>();
        if (dtoList.isEmpty()) {
            dtoList = getDefault();
        }

        return dtoList;
    }

    /**
     * Ad demo
     *
     * @return
     */
    public List<AdvertDTO> getAdExample() {
        List<AdvertDTO> dtoList = new ArrayList<>();
        AdvertDTO dto = AdvertDTO.builder()
                .title("")
                .fileType(1)
                .url1("http://sharingweb.oss-cn-shenzhen.aliyuncs.com/images/2588e922e8d4e68173081511adced4e0.png")
                .url2("http://sharingweb.oss-cn-shenzhen.aliyuncs.com/images/2588e922e8d4e68173081511adced4e0.png")
                .url3("")
                .forward("")
                .playTime(5)
                .weight(0)
                .screenBrightness(0)
                .guuid(null)
                .build();
        dtoList.add(dto);
        return dtoList;
    }
}
