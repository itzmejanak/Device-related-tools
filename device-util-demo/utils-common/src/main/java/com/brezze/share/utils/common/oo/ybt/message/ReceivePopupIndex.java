package com.brezze.share.utils.common.oo.ybt.message;

import com.brezze.share.utils.common.oo.ybt.serialport.SerialPortData;
import com.brezze.share.utils.common.oo.ybt.serialport.SerialPortError;
import com.brezze.share.utils.common.oo.ybt.serialport.SerialPortException;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
public class ReceivePopupIndex extends SerialPortData {
    private int pinboardIndex;
    private int powerbankIndex;
    private int status;

    public ReceivePopupIndex(byte[] bytes) throws SerialPortException {
        super(bytes);
        if(super.getCmd() != 0X21){
            throw new SerialPortException(SerialPortError.CMD);
        }

        int[] data = getData();

        this.pinboardIndex = data[0];
        this.powerbankIndex = data[1];
        this.status = data[2];
    }

    public int getPinboardIndex() {
        return pinboardIndex;
    }

    public void setPinboardIndex(int pinboardIndex) {
        this.pinboardIndex = pinboardIndex;
    }

    public int getPowerbankIndex() {
        return powerbankIndex;
    }

    public void setPowerbankIndex(int powerbankIndex) {
        this.powerbankIndex = powerbankIndex;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}
