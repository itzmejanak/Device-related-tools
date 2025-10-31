package com.brezze.share.utils.common.oo.ybt.message;

import com.brezze.share.utils.common.oo.ybt.serialport.SerialPortData;
import com.brezze.share.utils.common.oo.ybt.serialport.SerialPortError;
import com.brezze.share.utils.common.oo.ybt.serialport.SerialPortException;
import com.brezze.share.utils.common.string.ByteUtils;
import io.swagger.annotations.ApiModel;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ArrayUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

@ApiModel(value = "ReceiveUpload", description = "设备详情")
@Slf4j
public class ReceiveUpload extends SerialPortData {
    List<Pinboard> pinboards = new ArrayList<>();
    List<Powerbank> powerbanks = new ArrayList<>();

    public List<Pinboard> getPinboards() {
        return pinboards;
    }

    public List<Powerbank> getPowerbanks() {
        return powerbanks;
    }

    public ReceiveUpload(byte[] bytes) throws SerialPortException {
        super(bytes);
        if (super.getCmd() != 0X10) {
            log.info("ReceiveUpload：0X10", ByteUtils.to16Hexs(bytes));
            throw new SerialPortException(SerialPortError.CMD);
        }
        int[] data = getData();
        int hole;
        if (data[1] == 255 && data[2] == 255) {
            hole = getHole(bytes, data);
        } else {
            hole = getHole(bytes);
        }
        boolean areaType = false;
        int frontArea = 0;
        if (data[2] != 0 && data[2] != 11 && data[2] != 255) {
            areaType = true;
            frontArea = data[2];
        }
        int pinboardCount;
        int pinboardAndPowerbank = (hole * 15 + 6);
        boolean type = false;
        if (bytes.length - 5 == 192) {
            if (data[1] == 255 && data[2] == 255 && data[66] != 05) {
                type = true;
            }
        }

        if (data[1] == 255 && data[2] == 255) {
            double d = Double.valueOf(data.length) / Double.valueOf(pinboardAndPowerbank);
            pinboardCount = (int) Math.ceil(d);
        } else {
            pinboardCount = data.length / pinboardAndPowerbank;
            int symmetry = data.length % pinboardAndPowerbank;
            if (symmetry > 0) {
                pinboardCount = pinboardCount + 1;
            }
        }
        for (int i = 0; i < pinboardCount; i++) {

            if (type && i == 2) {
                continue;
            }
            int[] pinboardData = ArrayUtils.subarray(data, (i * pinboardAndPowerbank), 6 + (i * pinboardAndPowerbank));
            Pinboard pinboard = new Pinboard(pinboardData);
            pinboards.add(pinboard);
            if (type) {
                hole = i == 1 ? 8 : hole;
            }
            for (int j = 0; j < hole; j++) {
                int[] powerbankData = ArrayUtils.subarray(data, (6 + (i * pinboardAndPowerbank) + (j * 15)), (21 + (i * pinboardAndPowerbank) + (j * 15)));
                if (powerbankData != null && powerbankData.length > 0) {
                    Powerbank powerbank = new Powerbank(powerbankData, pinboard.getIndex(), areaType, frontArea);
                    powerbanks.add(powerbank);
                }
            }
        }

        Collections.sort(powerbanks, Comparator.comparingInt(Powerbank::getIndex));

    }

    /**
     * 获取机芯孔位数量
     *
     * @return
     */
    public int getHole() {
        return ReceiveUpload.getHole(getBytes());
    }

    /**
     * 获取机芯孔位数量
     *
     * @return
     */
    public static int getHole(byte[] bytes) {
        //排除头部4字节，尾部校验1字节
        float size = bytes.length - 5;


        //机芯公共字节6  孔位字节15
        Float pinboard = size / (6 + 15 * 2);   //机芯数量

        //单机芯（2口）
        if (pinboard == 1) {
            return 2;
        }

        //机芯公共字节6  孔位字节15
//        pinboard = size / (6 + 15 * 1);   //机芯数量
//        //单机芯（1口）
//        if (pinboard % pinboard.intValue() == 0) {
//            return 1;
//        }


        //多机芯（5口）
        pinboard = size / (6 + 15 * 5);
        if (pinboard % pinboard.intValue() == 0) {
            return 5;
        }

        //多机芯（6口）
        pinboard = size / (6 + 15 * 6);
        if (pinboard % pinboard.intValue() == 0) {
            return 6;
        }

        //多机芯（8口）
        pinboard = size / (6 + 15 * 8);
        if (pinboard % pinboard.intValue() == 0) {
            return 8;
        }

        //多机芯（4口）
        pinboard = size / (6 + 15 * 4);
        if (pinboard % pinboard.intValue() == 0) {
            return 4;
        }
        //多机芯（3口）
        pinboard = size / (6 + 15 * 3);
        if (pinboard % pinboard.intValue() == 0) {
            if (pinboard == 2) {
                //判断是否是普及版：4+2
                if (bytes[4 + 6 + 15 * 4] < 4) {
                    return 4;
                }
            }
            return 3;
        }


        return 0;
    }

    /**
     * 获取机芯孔位数量
     *
     * @return
     */
    public static int getHole(byte[] bytes, int[] data) {
        //排除头部4字节，尾部校验1字节
        float size = bytes.length - 5;

        //机芯公共字节6  孔位字节15
        Float pinboard = size / (6 + 15 * 2);   //机芯数量

        if (size == 192 && data[1] == 255 && data[2] == 255) {
            if (data[66] == 05) {
                return 8;
            } else {
                return 4;
            }
        }
        //单机芯（2口）
        if (pinboard == 1) {
            return 2;
        }

        //多机芯（5口）
        pinboard = size / (6 + 15 * 5);
        if (pinboard % pinboard.intValue() == 0) {
            return 5;
        }

        //多机芯（6口）
        pinboard = size / (6 + 15 * 6);
        if (pinboard % pinboard.intValue() == 0) {
            return 6;
        }

        //多机芯（8口）
        pinboard = size / (6 + 15 * 8);
        if (pinboard % pinboard.intValue() == 0) {
            return 8;
        }

        //多机芯（4口）
        pinboard = size / (6 + 15 * 4);
        if (pinboard % pinboard.intValue() == 0) {
            return 4;
        }

        //多机芯（3口）
        pinboard = size / (6 + 15 * 3);
        if (pinboard % pinboard.intValue() == 0) {
            return 3;
        }
        return 0;
    }

    /**
     * 获取正常的充电宝（不限电量）
     *
     * @return
     */
    public List<Powerbank> getNormalPowerbanks() {
        List<Powerbank> data = new ArrayList<>();
        for (Powerbank item : powerbanks) {
            if (item.getSnAsInt() > 0 && item.getStatus() == 0X01) {
                data.add(item);
            }
        }
        return data;
    }

    /**
     * 获取正常且电量在指定电量以上的充电宝
     *
     * @return
     */
//    public List<Powerbank> getNormalPowerbanksByPower(int limitPower) throws HttpCodeException{
//        List<Powerbank> result = new ArrayList<>();
//        //正常的充电宝
//        List<Powerbank> data = getNormalPowerbanks();
//        if (CollectionUtils.isEmpty(data)) {
//            return result;
//        }
//        for (Powerbank item : data) {
//            if (item.getPower() >= limitPower && item.getPower() <= 100) {
//                result.add(item);
//            }
//        }
//        if (CollectionUtils.isEmpty(result)) {
//            throw new HttpCodeException(HttpCode.POWER_TOO_LOW);
//        }
//        return result;
//    }
    public Powerbank getPowerbankByPower() {
        List<Powerbank> data = getNormalPowerbanks();
        if (data.size() == 0) {
            return null;
        }
        Collections.shuffle(data); // 混乱排序
        return data.get(0);
    }

    //获取PowerBankSN号
    public ArrayList<String> getPowerBankSN(List<Powerbank> powerbanks) {
        ArrayList<String> powerBanks = new ArrayList<>();
        for (Powerbank powerBank : powerbanks) {
            powerBanks.add(powerBank.getSnAsString());
        }
        return powerBanks;
    }

}
