package com.brezze.share.communication.cabinet.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.brezze.share.communication.cabinet.entity.Cabinet;
import com.brezze.share.communication.cabinet.mapper.CabinetMapper;
import com.brezze.share.communication.cabinet.service.CabinetService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;


/**
 * <p>
 * 机柜服务实现类
 * </p>
 *
 * @author penf
 * @since 2021-03-22
 */
@Slf4j
@Service
public class CabinetServiceImpl extends ServiceImpl<CabinetMapper, Cabinet> implements CabinetService {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Override
    public String getImei(String deviceName) {
        LambdaQueryWrapper<Cabinet> queryWrapper = new LambdaQueryWrapper<>();

        queryWrapper.eq(Cabinet::getCabinetNo, deviceName)
                    .or()
                    .eq(Cabinet::getImei, deviceName);

        Cabinet cabinet = getOne(queryWrapper);

        if (cabinet == null) {
            log.warn("No cabinet found for deviceName: {}", deviceName);
            return null;
        }

        return cabinet.getImei();
    }
}
