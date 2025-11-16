package com.brezze.share.utils.common.oo.dto;

import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * @author :xiefujian
 * @description :易佰特充电宝租借、归还数据上报对象
 * @date :2021-11-24 17:59:09
 **/
@Data
@Accessors(chain = true)
public class Ybt2RDR implements Serializable {

    /**
     * 设备IMEI
     */
    private String deviceName;
    /**
     * 卡槽编号
     */
    private Integer pos;
    /**
     * 电池编号
     */
    private String pbNo;
    /**
     * 电池电量
     */
    private Integer bc;
    /**
     * 重试次数
     */
    private int times;
    /**
     * 状态
     */
    private int status;
}
