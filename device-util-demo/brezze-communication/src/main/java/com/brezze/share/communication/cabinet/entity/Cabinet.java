package com.brezze.share.communication.cabinet.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 * Station
 * </p>
 *
 * @author Cc
 * @since 2021-11-15
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("bz_cabinet")
public class Cabinet extends BaseEntity<Cabinet> {

    private static final long serialVersionUID = 1L;

    /**
     * Station SN
     */
    @TableField("cabinet_no")
    private String cabinetNo;

    /**
     * IMEI
     */
    @TableField("imei")
    private String imei;

    /**
     * status: 0-offline 1-online 3-unactive
     */
    @TableField("state")
    private Integer state;

    /**
     * Agent Id
     */
    @TableField("agent_id")
    private Integer agentId;

    /**
     * Merchant id
     */
    @TableField("branch_id")
    private Integer branchId;

    /**
     * Merchant bind time
     */
    @TableField("bind_time")
    private LocalDateTime bindTime;

    /**
     * sim card iccid
     */
    @TableField("sim")
    private String sim;

    /**
     * firmware Version
     */
    @TableField("firmware_version")
    private String firmwareVersion;

    /**
     * online IP address
     */
    @TableField("login_ip")
    private String loginIp;

    /**
     * remark
     */
    @TableField("remark")
    private String remark;

    /**
     * signal
     */
    @TableField("`signal`")
    private Integer signal;

    /**
     * Screen：0-No 1-Yes
     */
    @TableField("is_screen")
    private Integer isScreen;

    /**
     * last Online Time
     */
    @TableField("last_online_time")
    private LocalDateTime lastOnlineTime;

    /**
     * Signal Level：0 - Weak 1 - Medium 2 - Strong
     * {@link com.brezze.share.communication.utils.StringUtil}
     */
    @TableField("signal_level")
    private String signalLevel;

    /**
     * network Type：4G, WIFI
     */
    @TableField("network_type")
    private String networkType;

    /**
     * stripe location ID
     */
    @TableField("location_id")
    private String locationId;

    /**
     * Wifi SSID
     */
    @TableField("wifi_ssid")
    private String wifiSsid;

    /**
     * Wifi password
     */
    @TableField("wifi_password")
    private String wifiPassword;

    /**
     * volume
     */
    @TableField("volume")
    private Integer volume;

    /**
     * VIETQR
     */
    @TableField("vietqr")
    private String vietqr;

    /**
     * Pinboard Soft Version
     */
    @TableField("pinboard_soft_version")
    private String pinboardSoftVersion;

    /**
     * Pinboard Hard Version
     */
    @TableField("pinboard_hard_version")
    private String pinboardHardVersion;

    @Override
    protected Serializable pkVal() {
        return null;
    }

}
