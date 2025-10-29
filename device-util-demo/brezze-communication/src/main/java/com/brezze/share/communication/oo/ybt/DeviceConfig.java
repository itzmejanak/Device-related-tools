package com.brezze.share.communication.oo.ybt;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
public class DeviceConfig implements Serializable {
    private static final long serialVersionUID = 4359709211352400086L;

    private String sn;
    private String productKey;
    private String deviceName;
    private String deviceSecret;
    public String host;
    public int port;
    private String iotId;
    private String iotToken;
    private LocalDateTime createdTime;
    private LocalDateTime overdueTime;
}
