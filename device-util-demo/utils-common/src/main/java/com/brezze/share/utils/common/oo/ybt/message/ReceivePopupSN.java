package com.brezze.share.utils.common.oo.ybt.message;

import com.brezze.share.utils.common.oo.ybt.serialport.SerialPortData;
import com.brezze.share.utils.common.oo.ybt.serialport.SerialPortError;
import com.brezze.share.utils.common.oo.ybt.serialport.SerialPortException;
import com.brezze.share.utils.common.string.ByteUtils;

public class ReceivePopupSN extends SerialPortData {

    private int pinboardIndex;
    private int[] sn;
    /**
     * 0x01	弹出成功
     * 0x00	弹出失败
     * 0x02	防盗协议通信不成功
     * 0x03	该充电宝不存在
     * 0x04	指令超时
     */
    private int status;
    private int snAsInt;
    private String snAsString;

    public ReceivePopupSN(byte[] bytes) throws SerialPortException {
        super(bytes);
        if(super.getCmd() != 0X31){
            throw new SerialPortException(SerialPortError.CMD);
        }

        int[] data = getData();

        pinboardIndex = data[0];
        sn = new int[]{data[1], data[2], data[3], data[4]};
        snAsInt = ByteUtils.getJavaInt(sn);
        snAsString = String.valueOf(snAsInt);
        status = data[5];
    }

    public int getSnAsInt() {
        return snAsInt;
    }

    public String getSnAsString() {
        return snAsString;
    }

    public int getStatus() {
        return status;
    }
}
