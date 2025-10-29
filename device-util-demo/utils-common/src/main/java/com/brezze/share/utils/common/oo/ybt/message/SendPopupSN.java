package com.brezze.share.utils.common.oo.ybt.message;

import com.brezze.share.utils.common.oo.ybt.serialport.SerialPortData;
import org.apache.commons.lang3.ArrayUtils;

public class SendPopupSN extends SerialPortData {
    public SendPopupSN(int pinboardIndex, int[] powerbankSN) {
        super(0X30, ArrayUtils.addAll(new int[]{pinboardIndex}, powerbankSN));
    }
}
