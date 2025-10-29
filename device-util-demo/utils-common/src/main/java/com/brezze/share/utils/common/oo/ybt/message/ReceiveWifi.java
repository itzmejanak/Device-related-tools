package com.brezze.share.utils.common.oo.ybt.message;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONArray;
import com.brezze.share.utils.common.oo.ybt.serialport.SerialPortData;
import com.brezze.share.utils.common.oo.ybt.serialport.SerialPortError;
import com.brezze.share.utils.common.oo.ybt.serialport.SerialPortException;
import com.brezze.share.utils.common.string.ByteUtils;

import java.util.List;

public class ReceiveWifi extends SerialPortData {

    private List<String> name;

    public ReceiveWifi(byte[] bytes) throws SerialPortException {
        super(bytes);
        if(super.getCmd() != 0XCF){
            throw new SerialPortException(SerialPortError.CMD);
        }
        int[] data = getData();
        //转ASCII
        String wifiStr = StrUtil.str(ByteUtils.toBytes(data), "ASCII");
        JSONArray jsonArray = new JSONArray(wifiStr);
        name = jsonArray.toList(String.class);
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
