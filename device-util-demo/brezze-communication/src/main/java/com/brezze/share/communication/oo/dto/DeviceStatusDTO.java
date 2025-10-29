package com.brezze.share.communication.oo.dto;

import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * DeviceStatusDTO
 *
 * @author penf
 * @description
 * @date 2020/05/20 16:35
 */
@Data
@Accessors(chain = true)
public class DeviceStatusDTO implements Serializable {

    /**
     * lastTime : 2020-05-20 16:25:43.196
     * utcLastTime : 2020-05-20T08:25:43.196Z
     * clientIp : 116.25.40.101
     * utcTime : 2020-05-20T08:25:43.205Z
     * time : 2020-05-20 16:25:43.205
     * productKey : a1WiP9SggIH
     * deviceName : 968856456515268
     * status : online|offline
     */

    /**
     * 状态最后变更时间
     * 注:需要根据此时间排序，判断设备最终状态
     */
    private String lastTime;
    /**
     * UTC状态最后变更时间
     */
    private String utcLastTime;
    /**
     * 设备公网出口IP
     */
    private String clientIp;
    /**
     * UTC时间点
     */
    private String utcTime;
    /**
     * 此条消息发送的时间点
     */
    private String time;
    /**
     * 设备所属产品的唯一标识
     */
    private String productKey;
    /**
     * 设备名称
     */
    private String deviceName;
    /**
     * 设备状态，online上线，offline离线
     */
    private String status;

}
