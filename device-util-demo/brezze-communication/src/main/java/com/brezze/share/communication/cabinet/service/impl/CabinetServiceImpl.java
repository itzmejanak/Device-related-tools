package com.brezze.share.communication.cabinet.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.brezze.share.communication.cabinet.entity.Cabinet;
import com.brezze.share.communication.cabinet.mapper.CabinetMapper;
import com.brezze.share.communication.cabinet.service.CabinetService;
import com.brezze.share.utils.common.constant.mq.YbtMQCst;
import com.brezze.share.utils.common.oo.dto.Ybt2RDR;
import com.brezze.share.utils.common.string.StringUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
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
        queryWrapper.eq(Cabinet::getCabinetNo, deviceName).or()
                .eq(Cabinet::getImei, deviceName);
        Cabinet cabinet = getOne(queryWrapper);
        if (null == cabinet) {
            return deviceName;
        }
        return cabinet.getImei();
    }

    @Override
    public void powerBankReturn(String deviceName, String pbNo, Integer pos, Integer bc) {
        if (StringUtil.isEmpty(pbNo)) {
            return;
        }
        Ybt2RDR pbReturnDr = new Ybt2RDR();
        pbReturnDr.setDeviceName(deviceName)
                .setPbNo(pbNo)
                .setBc(bc)
                .setPos(pos);
        rabbitTemplate.convertAndSend(YbtMQCst.EXCHANGE_CABINET, YbtMQCst.ROUTING_PB_RETURN, pbReturnDr);
    }
}
