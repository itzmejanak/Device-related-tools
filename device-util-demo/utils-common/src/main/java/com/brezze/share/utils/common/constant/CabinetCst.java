package com.brezze.share.utils.common.constant;

/**
 * CabinetCst
 *
 * @Author penf
 * @Description 机柜常量
 * @Date 2020/11/14 16:49
 */
public class CabinetCst {


    /**
     * 机柜状态: 0-离线
     */
    public static final int OFFLINE = 0;
    /**
     * 机柜状态: 1-在线
     */
    public static final int ONLINE = 1;
    /**
     * 机柜状态: 2-已迁移
     */
    public static final int MIGRATED = 2;
    /**
     * 机柜状态: 3-未激活
     */
    public static final int UNACTIVE = 3;


    /**
     * check-all使用场景
     */
    public interface CheckScene {
        /**
         * 租借
         */
        String RENTAL = "rental";

        /**
         * 通用
         */
        String COMMON = "common";
    }
}
