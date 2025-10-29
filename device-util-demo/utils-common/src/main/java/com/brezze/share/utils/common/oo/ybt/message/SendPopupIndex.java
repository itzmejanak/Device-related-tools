package com.brezze.share.utils.common.oo.ybt.message;

import com.brezze.share.utils.common.oo.ybt.serialport.SerialPortData;

public class SendPopupIndex extends SerialPortData {
    public SendPopupIndex(int pinboardIndex, int powerbankIndex) {
        super(0X20, new int[]{pinboardIndex, powerbankIndex});
    }
}
