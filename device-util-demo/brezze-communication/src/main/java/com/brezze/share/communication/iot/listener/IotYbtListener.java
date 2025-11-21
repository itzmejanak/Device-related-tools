package com.brezze.share.communication.iot.listener;

import cn.hutool.json.JSONUtil;
import com.brezze.share.communication.utils.SpringContextUtil;
import com.brezze.share.utils.common.constant.RedisKeyCst;
import com.brezze.share.utils.common.constant.TimeCst;
import com.brezze.share.utils.common.json.GsonUtil;
import com.brezze.share.utils.common.oo.ybt.message.*;
import com.brezze.share.utils.common.oo.ybt.serialport.SerialPortData;
import com.brezze.share.utils.common.oo.ybt.serialport.SerialPortException;
import com.brezze.share.utils.common.redis.RedisUtil;
import com.brezze.share.utils.common.string.ByteUtils;
import com.brezze.share.utils.common.thread.ThreadPoolUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.jms.Message;
import javax.jms.MessageListener;

@Slf4j
@Component
public class IotYbtListener implements MessageListener {

    @Override
    public void onMessage(Message message) {
        try {
            //1.收到消息之后一定要ACK
            // 推荐做法：创建Session选择Session.AUTO_ACKNOWLEDGE，这里会自动ACK。
            // 其他做法：创建Session选择Session.CLIENT_ACKNOWLEDGE，这里一定要调message.acknowledge()来ACK。
            // message.acknowledge();
            //2.建议异步处理收到的消息，确保onMessage函数里没有耗时逻辑。
            // 如果业务处理耗时过程过长阻塞住线程，可能会影响SDK收到消息后的正常回调。
            ThreadPoolUtil.getExecutorService().submit(() -> processMessage(message));
        } catch (Exception e) {
            log.error("submit task occurs exception ", e);
        }
    }

    /**
     * 在这里处理您收到消息后的具体业务逻辑。
     */
    private void processMessage(Message message) {
        try {
            byte[] body = message.getBody(byte[].class);
            String topic = message.getStringProperty("topic");
            String messageId = message.getStringProperty("messageId");
            String content;
            ReceiveIndexCheck receiveIndexCheck;
            if (topic.contains("/as/mqtt/status")) {
                // {"lastTime":"2020-05-20 16:25:43.196","utcLastTime":"2020-05-20T08:25:43.196Z","clientIp":"116.25.40.101","utcTime":"2020-05-20T08:25:43.205Z","time":"2020-05-20 16:25:43.205","productKey":"a1WiP9SggIH","deviceName":"968856456515268","status":"online"}
                content = new String(body);
                log.info("\nreceive message: "
                        + "\n   topic = " + topic
                        + "\n   messageId = " + messageId
                        + "\n   content = " + content);
                log.info("\n--------------------->YBT机柜-[],设备状态: {}", GsonUtil.toJson(content));

            } else {
                content = ByteUtils.to16Hexs(body);
                log.info("\nreceive message: "
                        + "\n   topic = " + topic
                        + "\n   messageId = " + messageId
                        + "\n   content = " + content);
                int cmd = SerialPortData.checkCMD(body);
                String deviceName = getDeviceName(topic);
                switch (cmd) {
                    case 0x10:
                        //check  check_all
                        ReceiveUpload receiveUpload = new ReceiveUpload(body);
                        log.info("\n--------------------->YBT机柜-[{}],设备心跳上报: {}", deviceName, GsonUtil.toJson(receiveUpload));

                        //缓存详情数据-测试工具使用
                        RedisUtil redisUtil = SpringContextUtil.getBean(RedisUtil.class);
                        log.info("Message body:{}", body);
                        if (null != redisUtil) {
                            redisUtil.set(RedisKeyCst.CABINET_MESSAGE_CACHE_CHECK + deviceName, ByteUtils.to16Hexs(body), TimeCst.TIME_SECOND_1DAYS);
                        }
                        break;
                    case 0x21:
                        //popup by position
                        ReceivePopupIndex receivePopupIndex = new ReceivePopupIndex(body);
                        log.info("\n--------------------->YBT机柜-[{}],孔位弹出: {}", deviceName, GsonUtil.toJson(receivePopupIndex));
                        break;
                    case 0x31:
                        //popup by sn
                        ReceivePopupSN receivePopupSN = new ReceivePopupSN(body);
                        log.info("\n--------------------->YBT机柜-[{}],弹出SN: {}", deviceName, GsonUtil.toJson(receivePopupSN));

                        break;
                    case 0X40:
                        //充电宝归还
                        ReceiveReturn receiveReturn = new ReceiveReturn(body);
                        log.info("\n--------------------->YBT机柜-[{}],充电宝归还: {}", deviceName, GsonUtil.toJson(receiveReturn));
                        break;
                    case 0X7A:
                        //MQTT心跳上报
                        log.info("\n--------------------->YBT机柜-[{}],MQTT心跳上报: {}", deviceName, content);
                        break;
                    case 0xB1:
                        //检查版本
                        log.info("\n--------------------->YBT机柜-[{}],检查版本: {}", deviceName, content);
                        break;
                    case 0x7D:
                        //PING网络延迟
                        log.info("\n--------------------->YBT机柜-[{}],PING网络延迟: {}", deviceName, content);
                        break;
                    case 0x34:
                        //获取APN信息
                        log.info("\n--------------------->YBT机柜-[{}],获取APN信息: {}", deviceName, content);
                        break;
                    case 0x28:
                        //场景： 机器断电的时候客户把充电宝归还，等开机上电后 机器不会以当前时间上报归还充电宝，所以新加状态变化指令
                        //当孔位有充电宝并且状态出现变化的时候 机器会上报一条28指令 提示状态变化
                        receiveIndexCheck = new ReceiveIndexCheck(body);
                        log.info("\n--------------------->YBT机柜-[{}],孔位异常，设备自检（机芯发送）: {}", deviceName, GsonUtil.toJson(receiveIndexCheck.getPowerbank()));
                        break;
                    case 0x29:
                        //场景： 机器断电的时候客户把充电宝归还，等开机上电后 机器不会以当前时间上报归还充电宝，所以新加状态变化指令
                        //当孔位有充电宝并且状态出现变化的时候 机器会上报一条28指令 提示状态变化
                        receiveIndexCheck = new ReceiveIndexCheck(body);
                        log.info("\n--------------------->YBT机柜-[{}],孔位异常，设备自检（后台返回）: {}", deviceName, GsonUtil.toJson(receiveIndexCheck.getPowerbank()));
                        break;
                    case 0x14:
                        log.info("\n--------------------->YBT机柜-[{}],联网成功后，机柜主动上报所有信息: {}", deviceName, content);
                        break;
                    case 0x16:
                        log.info("\n--------------------->YBT机柜-[{}],租借之前，查询机柜所有信息: {}", deviceName, content);
                        break;
                    case 0x18:
                        log.info("\n--------------------->YBT机柜-[{}],后台设置4G上报心跳频率: {}", deviceName, content);
                        break;
                    case 0x1A:
                        log.info("\n--------------------->YBT机柜-[{}],查询版本信息: {}", deviceName, content);
                        break;
                    case 0x1E:
                        log.info("\n--------------------->YBT机柜-[{}],轮循充配置: {}", deviceName, content);
                        break;
                    case 0x20:
                        ReceivePopupIndex receivePopupIndex1 = new ReceivePopupIndex(body);
                        log.info("\n--------------------->YBT机柜-[{}],通过孔位控制租借移动电源: {}", deviceName, GsonUtil.toJson(receivePopupIndex1));
                        break;
                    case 0x26:
                        log.info("\n--------------------->YBT机柜-[{}],设置自检间隔: {}", deviceName, content);
                        break;
                    case 0x2A:
                        log.info("\n--------------------->YBT机柜-[{}],设置电磁阀检测: {}", deviceName, content);
                        break;
                    case 0x2C:
                        log.info("\n--------------------->YBT机柜-[{}],设置区域码: {}", deviceName, content);
                        break;
                    case 0x2E:
                        log.info("\n--------------------->YBT机柜-[{}],设置头码: {}", deviceName, content);
                        break;
                    case 0x30:
                        log.info("\n--------------------->YBT机柜-[{}],通过SN号控制租借移动电源: {}", deviceName, content);
                        break;
                    case 0x32:
                        log.info("\n--------------------->YBT机柜-[{}],获取机芯当前配置（测试机芯通信命令）: {}", deviceName, content);
                        break;
                    case 0x36:
                        log.info("\n--------------------->YBT机柜-[{}],配置所有信息: {}", deviceName, content);
                        break;
                    case 0x38:
                        log.info("\n--------------------->YBT机柜-[{}],配置锁孔: {}", deviceName, content);
                        break;
                    case 0x3A:
                        log.info("\n--------------------->YBT机柜-[{}],FOTA升级模块: {}", deviceName, content);
                        break;
                    case 0x70:
                        log.info("\n--------------------->YBT机柜-[{}],复位转接板: {}", deviceName, content);
                        break;
                    case 0x72:
                        log.info("\n--------------------->YBT机柜-[{}],复位4G板: {}", deviceName, content);
                        break;
                    case 0x7C:
                        log.info("\n--------------------->YBT机柜-[{}],PING网络延迟（嘟嘟）: {}", deviceName, content);
                        break;
                    case 0x80:
                        log.info("\n--------------------->YBT机柜-[{}],复位机芯: {}", deviceName, content);
                        break;
                    case 0xA0:
                        log.info("\n--------------------->YBT机柜-[{}],获取网络时间: {}", deviceName, content);
                        break;
                    case 0xA2:
                        log.info("\n--------------------->YBT机柜-[{}],获取转接板日志信息: {}", deviceName, content);
                        break;
                    case 0xA4:
                        log.info("\n--------------------->YBT机柜-[{}],获取转接板日志数据: {}", deviceName, content);
                        break;
                    case 0xB0:
                        log.info("\n--------------------->YBT机柜-[{}],网络升级程序: {}", deviceName, content);
                        break;
                    case 0xB4:
                        log.info("\n--------------------->YBT机柜-[{}],EC20升级机芯: {}", deviceName, content);
                        break;
                    case 0xB6:
                        log.info("\n--------------------->YBT机柜-[{}],EC20升级机芯: {}", deviceName, content);
                        break;
                    case 0xB8:
                        log.info("\n--------------------->YBT机柜-[{}],EC20升级机芯: {}", deviceName, content);
                        break;
                    case 0xC0:
                        log.info("\n--------------------->YBT机柜-[{}],上传本地日志: {}", deviceName, content);
                        break;
                    case 0xE0:
                        log.info("\n--------------------->YBT机柜-[{}],主机更新从机（与后台无关）: {}", deviceName, content);
                        break;
                    case 0xF0:
                        log.info("\n--------------------->YBT机柜-[{}],控制机芯联网时候闪灯: {}", deviceName, content);
                        break;
                    case 0xD1:
                        log.info("\n--------------------->YBT机柜-[{}],上报机芯sn号: {}", deviceName, content);
                        break;
                    case 0xCF:
                        ReceiveWifi receiveWifi = new ReceiveWifi(body);
                        log.info("\n--------------------->YBT机柜-[{}],获取附近WIFI热点: {}", deviceName, JSONUtil.toJsonStr(receiveWifi.getName()));
                        break;
                    default:
                        log.info("\n--------------------->YBT机柜-[{}],未知命令数据上报: {}", deviceName, content);
                        break;
                }
            }
        } catch (Exception e) {
            log.error("processMessage occurs error ", e);
        }
    }

    private String getDeviceName(String topic) {
        String[] arr = topic.split("/");
        if (arr.length < 3) {
            return "";
        }
        return arr[2];
    }


    public static void main(String[] args) throws SerialPortException {
//        0xa800c510100000003a3a010000000000000000ffff00000080020000000000000000ffff00000080030000000000000000ffff00000080040000000000000000ffff00000080050000000000000000ffff00000080060000000000000000ffff00000080600000003a3a1f0000000000000000ffff00000080200000000000000000ffff00000080210000000000000000ffff00000080220000000000000000ffff00000080230000000000000000ffff00000080240000000000000000ffff0000008065
        String hexStr = "A8 00 38 10 10 03 00 00 03 FF 01 00 00 00 00 00 00 00 00 FF FF 00 00 00 80 02 00 00 00 00 00 00 00 00 FF FF 00 00 00 80 03 00 00 00 00 00 00 00 00 FF FF 00 00 00 80 7B";
//        byte[] body = new byte[]{-88, 0, -59, 16, 16, 0, 0, 0, 58, 58, 1, 0, 0, 0, 0, 0, 0, 0, 0, -1, -1, 0, 0, 0, -128, 2, 0, 0, 0, 0, 0, 0, 0, 0, -1, -1, 0, 0, 0, -128, 3, 0, 0, 0, 0, 0, 0, 0, 0, -1, -1, 0, 0, 0, -128, 4, 0, 0, 0, 0, 0, 0, 0, 0, -1, -1, 0, 0, 0, -128, 5, 0, 0, 0, 0, 0, 0, 0, 0, -1, -1, 0, 0, 0, -128, 6, 0, 0, 0, 0, 0, 0, 0, 0, -1, -1, 0, 0, 0, -128, 96, 0, 0, 0, 58, 58, 31, 0, 0, 0, 0, 0, 0, 0, 0, -1, -1, 0, 0, 0, -128, 32, 0, 0, 0, 0, 0, 0, 0, 0, -1, -1, 0, 0, 0, -128, 33, 0, 0, 0, 0, 0, 0, 0, 0, -1, -1, 0, 0, 0, -128, 34, 0, 0, 0, 0, 0, 0, 0, 0, -1, -1, 0, 0, 0, -128, 35, 0, 0, 0, 0, 0, 0, 0, 0, -1, -1, 0, 0, 0, -128, 36, 0, 0, 0, 0, 0, 0, 0, 0, -1, -1, 0, 0, 0, -128, 101};
        byte[] body = ByteUtils.toBytes(hexStr);
        System.out.println(ByteUtils.to16Hexs(body));
        ReceiveUpload receiveUpload = new ReceiveUpload(body);
        System.out.println(GsonUtil.toJson(receiveUpload));
    }
}
