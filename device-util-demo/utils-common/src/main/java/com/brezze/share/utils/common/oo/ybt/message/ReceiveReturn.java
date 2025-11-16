package com.brezze.share.utils.common.oo.ybt.message;

import com.brezze.share.utils.common.oo.ybt.serialport.SerialPortData;
import com.brezze.share.utils.common.oo.ybt.serialport.SerialPortError;
import com.brezze.share.utils.common.oo.ybt.serialport.SerialPortException;
import com.brezze.share.utils.common.string.ByteUtils;

public class ReceiveReturn extends SerialPortData {
    private int pinboardIndex;
    private int hole;
    private int area;
    private int[] sn;
    /**
     * 0x01	归还成功
     * 0x02	归还失败，有按键检测，无通讯	把他弹出去
     * 0x03	归还成功，无按键检测，有通讯   然后这个槽口的状态也标识为 按键损坏
     *
     *
     * 0x06	归还时候马达未归位
     *
     * 0x00	异常失败
     */
    private int status;
    private int snAsInt;
    private String snAsString;
    private int version;
    private Integer power;

    public int getHole() {
        return hole;
    }

    public String getSnAsString() {
        return snAsString;
    }

    public ReceiveReturn(byte[] bytes) throws SerialPortException {
        super(bytes);
        if(super.getCmd() != 0X40){
            throw new SerialPortException(SerialPortError.CMD);
        }
        int[] data = getData();

        pinboardIndex = data[0];
        hole = data[1];
        area = data[2];

        sn = new int[]{data[3], data[4], data[5], data[6]};
        snAsInt = ByteUtils.getJavaInt(sn);
        snAsString = String.valueOf(snAsInt);
        status = data[7];

        if (bytes.length == 15) {
            version = data[8];
            power = data[9];
        }
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public Integer getPower() {
        return power;
    }
}
