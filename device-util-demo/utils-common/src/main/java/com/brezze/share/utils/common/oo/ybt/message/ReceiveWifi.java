package com.brezze.share.utils.common.oo.ybt.message;

import com.brezze.share.utils.common.oo.ybt.serialport.SerialPortData;
import com.brezze.share.utils.common.oo.ybt.serialport.SerialPortError;
import com.brezze.share.utils.common.oo.ybt.serialport.SerialPortException;
import com.brezze.share.utils.common.string.ByteUtils;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author :xiefujian
 * @description :
 * @date :2024-10-14 14:33:17
 **/
public class ReceiveWifi extends SerialPortData {

    private List<String> name;

    public ReceiveWifi(byte[] bytes) throws SerialPortException {
        super(bytes);
        if (super.getCmd() != 0XCF) {
            throw new SerialPortException(SerialPortError.CMD);
        }
        int[] data = getData();
        String wifiStr = new String(ByteUtils.toBytes(data));
        String nameStr = wifiStr.substring(1, wifiStr.length() - 1);
        String[] nameArray = nameStr.split(",");
        name = Arrays.stream(nameArray).filter(element -> element.startsWith("\"") && element.endsWith("\"")).map(element -> element.substring(1, element.length() - 1)).collect(Collectors.toList());
    }

    public ReceiveWifi(int cmd, int[] data) {
        super(cmd, data);
    }

    public List<String> getName() {
        return name;
    }

    public void setName(List<String> name) {
        this.name = name;
    }
}
