package com.brezze.share.utils.common.constant.mq;

public class YbtMQCst {

    private static final String PREFIX = "ybt.";

    /**********************************************交换器**************************************************************/
    /**
     * 机柜交换器
     */
    public static final String EXCHANGE_CABINET = PREFIX + "cabinet";
    /**
     * 机柜交换器
     */
    public static final String EXCHANGE_DELAY = PREFIX + "delay";
    /**********************************************交换器**************************************************************/
    /**********************************************队列****************************************************************/

    /**
     * 设备状态
     */
    public static final String QUEUE_DEVICE_STATUS = PREFIX + "device.status";
    /**
     * 充电宝弹出-充电宝编号延迟弹出
     */
    public static final String QUEUE_DELAY_PB_POP = PREFIX + "delay.pb.pop";
    /**
     * 充电宝弹出-孔位延迟弹出
     */
    public static final String QUEUE_DELAY_PB_OPEN_LOCK = PREFIX + "delay.pb.open.lock";
    /**
     * 充电宝归还
     */
    public static final String QUEUE_PB_RETURN = PREFIX + "pb.return";
    /**********************************************队列****************************************************************/

    /**********************************************路由****************************************************************/
    /**
     * 设备状态
     */
    public static final String ROUTING_DEVICE_STATUS = PREFIX + "device.status";
    /**
     * 充电宝弹出-充电宝编号延迟弹出
     */
    public static final String ROUTING_DELAY_PB_POP = PREFIX + "delay.pb.pop";
    /**
     * 充电宝弹出-孔位延迟弹出
     */
    public static final String ROUTING_DELAY_PB_OPEN_LOCK = PREFIX + "delay.pb.open.lock";
    /**
     * 充电宝归还
     */
    public static final String ROUTING_PB_RETURN = PREFIX + "pb.return";
    /**********************************************路由****************************************************************/
}
