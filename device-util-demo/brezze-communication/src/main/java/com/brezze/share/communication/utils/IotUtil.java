package com.brezze.share.communication.utils;

import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.exceptions.ServerException;
import com.aliyuncs.http.HttpResponse;
import com.aliyuncs.iot.model.v20180120.*;
import com.aliyuncs.profile.DefaultProfile;
import com.brezze.share.utils.common.json.GsonUtil;
import com.brezze.share.utils.common.string.StringUtil;
import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.MapUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpStatus;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.springframework.util.Base64Utils;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Slf4j
public class IotUtil {


    public static final String MQTT_URL = "%s.iot-as-mqtt.%s.aliyuncs.com";
    public static final int MQTT_PORT = 1883;

    /**
     * 充电宝电量阈值，电量达到该值后自动上报全量信息
     */
    public static final String POWER_BANK_THRESHOLD = "60";

    public static final String TOPIC_GET = "/%s/%s/user/get";

    /**
     * 注册设备
     *
     * @param accessKey
     * @param accessSecret
     * @param regionId
     * @param productKey
     * @param deviceName
     * @return
     */
    public static RegisterDeviceResponse registerDevice(String accessKey, String accessSecret, String regionId, String productKey, String deviceName) {
        DefaultProfile profile = DefaultProfile.getProfile(regionId, accessKey, accessSecret);
        DefaultAcsClient client = new DefaultAcsClient(profile);
        // 创建请求接口
        RegisterDeviceRequest request = new RegisterDeviceRequest();
        // 待注册设备所隶属的产品的Key，即产品的唯一标识符。
        request.setProductKey(productKey);
        // 为待注册的设备命名。如果不传入该参数，则由系统随机生成设备名称。
        request.setDeviceName(deviceName);
        // 为待注册的设备设置备注名称。如果不传入该参数，系统不会为设备生成备注名称
        request.setNickname(deviceName);

        // LoRaWAN设备的DevEUI。
        // 创建LoRaWAN设备时，该参数必传。
        // request.setDevEui("<your-DevEui>");
        // LoRaWAN设备的PIN Code，用于校验DevEUI的合法性。
        // 创建LoRaWAN设备时，该参数必传。
        // request.setPinCode("<your-PinCode>");

        try {
            // 发起请求并获取返回值
            RegisterDeviceResponse response = client.getAcsResponse(request);
            // 处理业务逻辑
            log.info("aliyun-注册设备：{}", new Gson().toJson(response));
            return response;
        } catch (ServerException e) {
            log.error("registerDevice: ", e);
        } catch (ClientException e) {
            log.error("ErrCode:" + e.getErrCode());
            log.error("ErrMsg:" + e.getErrMsg());
            log.error("RequestId:" + e.getRequestId());
        }

        return null;
    }

    public static RegisterDeviceResponse registerDevice(String accessKey, String accessSecret, String regionId, String iotInstanceId, String productKey, String deviceName) {
        DefaultProfile profile = DefaultProfile.getProfile(regionId, accessKey, accessSecret);
        DefaultAcsClient client = new DefaultAcsClient(profile);
        // 创建请求接口
        RegisterDeviceRequest request = new RegisterDeviceRequest();
        request.setIotInstanceId(iotInstanceId);
        // 待注册设备所隶属的产品的Key，即产品的唯一标识符。
        request.setProductKey(productKey);
        // 为待注册的设备命名。如果不传入该参数，则由系统随机生成设备名称。
        request.setDeviceName(deviceName);
        // 为待注册的设备设置备注名称。如果不传入该参数，系统不会为设备生成备注名称
        request.setNickname(deviceName);

        // LoRaWAN设备的DevEUI。
        // 创建LoRaWAN设备时，该参数必传。
        // request.setDevEui("<your-DevEui>");
        // LoRaWAN设备的PIN Code，用于校验DevEUI的合法性。
        // 创建LoRaWAN设备时，该参数必传。
        // request.setPinCode("<your-PinCode>");

        try {
            // 发起请求并获取返回值
            RegisterDeviceResponse response = client.getAcsResponse(request);
            // 处理业务逻辑
            log.info("aliyun-注册设备：{}", new Gson().toJson(response));
            return response;
        } catch (ServerException e) {
            log.error("registerDevice: ", e);
        } catch (ClientException e) {
            log.error("ErrCode:" + e.getErrCode());
            log.error("ErrMsg:" + e.getErrMsg());
            log.error("RequestId:" + e.getRequestId());
        }

        return null;
    }

    /**
     * 调用设备物模型指定服务
     *
     * @param accessKey
     * @param accessSecret
     * @param regionId
     * @param productKey   产品key
     * @param deviceName   设备key
     * @param identifier   指定服务标识符
     * @param params       数据参数
     * @return
     */
    public static Boolean invokeThingService(String accessKey, String accessSecret, String regionId, String productKey, String deviceName, String identifier, Map params) {
        DefaultProfile profile = DefaultProfile.getProfile(regionId, accessKey, accessSecret);
        DefaultAcsClient client = new DefaultAcsClient(profile);
        // 创建请求接口
        InvokeThingServiceRequest request = new InvokeThingServiceRequest();
        // 系统规定参数。取值：InvokeThingService。
        request.setActionName("InvokeThingService");
        // 公共实例不传此参数；您购买的实例需传入实例ID
        // request.setIotInstanceId("");
        // 待注册设备所隶属的产品的Key，即产品的唯一标识符。
        request.setProductKey(productKey);
        // 为待注册的设备命名。如果不传入该参数，则由系统随机生成设备名称。
        request.setDeviceName(deviceName);


        // 服务的标识符
        request.setIdentifier(identifier);
        // 要启用服务的入参信息，数据格式为JSON String，例如Args={"param1":1}。若此参数为空时，需传入 Args={} 。
        request.setArgs(params == null ? "{}" : GsonUtil.toJson(params));

        try {
            // 发起请求并获取返回值
            HttpResponse response = client.doAction(request);
            log.info("\n    ------->Aliyun IOT,调用设备物模型指定服务" +
                            "\n    产品-{}, 设备-{}, 标识符-{}" +
                            "\n    数据-{}" +
                            "\n    结果-{}",
                    productKey,
                    deviceName,
                    identifier,
                    params,
                    response.isSuccess()
            );
            return response.isSuccess();
        } catch (ServerException e) {
            log.error("", e);
        } catch (ClientException e) {
            log.error("ErrCode:" + e.getErrCode());
            log.error("ErrMsg:" + e.getErrMsg());
            log.error("RequestId:" + e.getRequestId());
        }

        return false;
    }


    /**
     * 设置设备属性
     *
     * @param accessKey
     * @param accessSecret
     * @param regionId
     * @param productKey   产品key
     * @param deviceName   设备key
     * @param params       属性参数
     * @return
     */
    public static Boolean setDeviceProperty(String accessKey, String accessSecret, String regionId, String productKey, String deviceName, Map params) {
        DefaultProfile profile = DefaultProfile.getProfile(regionId, accessKey, accessSecret);
        DefaultAcsClient client = new DefaultAcsClient(profile);
        // 创建请求接口
        SetDevicePropertyRequest request = new SetDevicePropertyRequest();
        // 系统规定参数。取值：SetDeviceProperty。
        request.setActionName("SetDeviceProperty");
        // 公共实例不传此参数；您购买的实例需传入实例ID
        // request.setIotInstanceId("");
        // 待注册设备所隶属的产品的Key，即产品的唯一标识符。
        request.setProductKey(productKey);
        // 为待注册的设备命名。如果不传入该参数，则由系统随机生成设备名称。
        request.setDeviceName(deviceName);


        // 要设置的属性信息，数据格式为 JSON String。属性组成为属性标识符key:属性值value，多个属性用英文逗号隔开。
        // 例如，设置智能灯的如下两个属性：
        // 标识符为Switch的开关属性，数据类型为Bool，设置值为1（开）；
        // 标识符为Color的灯颜色属性，数据类型为String，设置值为blue。
        // 那么，Items={"Switch":1,"Color":"blue"}
        request.setItems(params == null ? "{}" : GsonUtil.toJson(params));

        try {
            // 发起请求并获取返回值
            HttpResponse response = client.doAction(request);
            log.info("\n    ------->Aliyun IOT,设置设备属性" +
                            "\n    产品-{}, 设备-{}" +
                            "\n    数据-{}" +
                            "\n    结果-{}",
                    productKey,
                    deviceName,
                    params,
                    response.isSuccess()
            );
            return response.isSuccess();
        } catch (ServerException e) {
            log.error("", e);
        } catch (ClientException e) {
            log.error("ErrCode:" + e.getErrCode());
            log.error("ErrMsg:" + e.getErrMsg());
            log.error("RequestId:" + e.getRequestId());
        }

        return false;
    }

    /**
     * 获取设备信息
     *
     * @param accessKey
     * @param accessSecret
     * @param regionId
     * @param deviceName
     * @return
     * @throws ClientException
     */
    public static QueryDeviceDetailResponse getDeviceInfo(String accessKey, String accessSecret, String regionId, String productKey, String deviceName) {
        DefaultProfile profile = DefaultProfile.getProfile(regionId, accessKey, accessSecret);
        DefaultAcsClient client = new DefaultAcsClient(profile);
        // 创建请求接口
        QueryDeviceDetailRequest request = new QueryDeviceDetailRequest();
        // // 要查询的设备ID。
        // // 如果传入该参数，则无需传入 ProductKey和 DeviceName。
        // // IotId作为设备唯一标识符，和 ProductKey与 DeviceName组合是一一对应的关系。
        // // 如果您同时传入 IotId和 ProductKey与 DeviceName组合，则以 IotId为准。
        // request.setIotId("iotId");

        // 指定要查询的设备的名称。
        // 说明 如果传入该参数，需同时传入 ProductKey。
        request.setDeviceName(deviceName);

        // 要查询的设备所隶属的产品Key。
        request.setProductKey(productKey);

        // 共享实例用户不传此参数；仅独享实例用户需传入实例ID。
        // request.setIotInstanceId("");

        try {
            // 发起请求并获取返回值
            QueryDeviceDetailResponse response = client.getAcsResponse(request);
            // 处理业务逻辑
            log.info("aliyun-获取设备信息：{}", new Gson().toJson(response));
            return response;
        } catch (ServerException e) {
            log.error("getDeviceInfo: ", e);
        } catch (ClientException e) {
            log.error("ErrCode:" + e.getErrCode());
            log.error("ErrMsg:" + e.getErrMsg());
            log.error("RequestId:" + e.getRequestId());
        }

        return null;
    }

    public static QueryDeviceDetailResponse getDeviceInfo(String accessKey, String accessSecret, String regionId, String iotInstanceId, String productKey, String deviceName) {
        DefaultProfile profile = DefaultProfile.getProfile(regionId, accessKey, accessSecret);
        DefaultAcsClient client = new DefaultAcsClient(profile);
        // 创建请求接口
        QueryDeviceDetailRequest request = new QueryDeviceDetailRequest();
        request.setIotInstanceId(iotInstanceId);
        // // 要查询的设备ID。
        // // 如果传入该参数，则无需传入 ProductKey和 DeviceName。
        // // IotId作为设备唯一标识符，和 ProductKey与 DeviceName组合是一一对应的关系。
        // // 如果您同时传入 IotId和 ProductKey与 DeviceName组合，则以 IotId为准。
        // request.setIotId("iotId");

        // 指定要查询的设备的名称。
        // 说明 如果传入该参数，需同时传入 ProductKey。
        request.setDeviceName(deviceName);

        // 要查询的设备所隶属的产品Key。
        request.setProductKey(productKey);

        // 共享实例用户不传此参数；仅独享实例用户需传入实例ID。
        // request.setIotInstanceId("");

        try {
            // 发起请求并获取返回值
            QueryDeviceDetailResponse response = client.getAcsResponse(request);
            // 处理业务逻辑
            log.info("aliyun-获取设备信息：{}", new Gson().toJson(response));
            return response;
        } catch (ServerException e) {
            log.error("getDeviceInfo: ", e);
        } catch (ClientException e) {
            log.error("ErrCode:" + e.getErrCode());
            log.error("ErrMsg:" + e.getErrMsg());
            log.error("RequestId:" + e.getRequestId());
        }

        return null;
    }

    /**
     * 获取产品下的设备列表
     *
     * @param accessKey
     * @param accessSecret
     * @param regionId
     * @param productKey
     * @return
     */
    public static QueryDeviceResponse getDeviceList(String accessKey, String accessSecret, String regionId, String productKey) {
        DefaultProfile profile = DefaultProfile.getProfile(regionId, accessKey, accessSecret);
        DefaultAcsClient client = new DefaultAcsClient(profile);
        // 创建请求接口
        QueryDeviceRequest request = new QueryDeviceRequest();
        // 分页显示第几页
        request.setCurrentPage(1);
        // 分页指定每页显示条数
        request.setPageSize(1);
        // 共享实例用户不传此参数；仅独享实例用户需传入实例ID。
        // request.setIotInstanceId("");
        // 要查询的设备所隶属的产品Key。
        request.setProductKey(productKey);

        try {
            // 发起请求并获取返回值
            QueryDeviceResponse response = client.getAcsResponse(request);
            //
            // 处理业务逻辑
            log.info(new Gson().toJson(response));
            return response;
        } catch (ServerException e) {
            log.error("getDeviceList: ", e);
        } catch (ClientException e) {
            log.error("ErrCode:" + e.getErrCode());
            log.error("ErrMsg:" + e.getErrMsg());
            log.error("RequestId:" + e.getRequestId());
        }

        return null;
    }

    /**
     * 批量获取设备状态
     *
     * @param accessKey
     * @param accessSecret
     * @param regionId
     * @param productKey
     * @param deviceNameList
     * @return
     */
    public static BatchGetDeviceStateResponse getBatchDeviceStatus(String accessKey, String accessSecret, String regionId, String productKey, List<String> deviceNameList) {
        DefaultProfile profile = DefaultProfile.getProfile(regionId, accessKey, accessSecret);
        DefaultAcsClient client = new DefaultAcsClient(profile);
        // 创建请求接口
        BatchGetDeviceStateRequest request = new BatchGetDeviceStateRequest();
        // 要查看运行状态的设备所隶属的产品Key。
        // 说明 如果传入该参数，需同时传入 DeviceNames。
        request.setProductKey(productKey);
        // 组建DeviceNames参数   要查看运行状态的设备的名称
        // 如果传入该参数，需同时传入ProductKey。单次查询最多50个设备。
        log.info(deviceNameList.toString());
        request.setDeviceNames(deviceNameList);
        // request.setDeviceNames(deviceNameList);

        // 组建IotIds参数  要查看运行状态的设备ID列表。
        // 如果传入该参数，则无需传入 ProductKey和 DeviceName。
        // IotId作为设备唯一标识符，与 ProductKey 和 DeviceName组合是一一对应的关系。
        // 如果您同时传入 IotId和 ProductKey与 DeviceName组合，则以 IotId为准。
        // List<String> iotIdList = new ArrayList<String>();
        // iotIdList.add("yourIotIds_1");
        // iotIdList.add("yourIotIds_2");
        // iotIdList.add("yourIotIds_3");
        // iotIdList.add("yourIotIds_4");
        // iotIdList.add("yourIotIds_5");
        // request.setIotIds(iotIdList);

        try {
            // 发起请求并获取返回值
            BatchGetDeviceStateResponse response = client.getAcsResponse(request);
            // 处理业务逻辑
            log.info(new Gson().toJson(response));
            return response;
        } catch (ServerException e) {
            log.error("getBatchDeviceStatus: ", e);
        } catch (ClientException e) {
            log.error("ErrCode:" + e.getErrCode());
            log.error("ErrMsg:" + e.getErrMsg());
            log.error("RequestId:" + e.getRequestId());
        }

        return null;
    }

    public static BatchGetDeviceStateResponse getBatchDeviceStatus(String accessKey, String accessSecret, String regionId, String iotInstanceId, String productKey, List<String> deviceNameList) {
        DefaultProfile profile = DefaultProfile.getProfile(regionId, accessKey, accessSecret);
        DefaultAcsClient client = new DefaultAcsClient(profile);
        // 创建请求接口
        BatchGetDeviceStateRequest request = new BatchGetDeviceStateRequest();
        request.setIotInstanceId(iotInstanceId);
        // 要查看运行状态的设备所隶属的产品Key。
        // 说明 如果传入该参数，需同时传入 DeviceNames。
        request.setProductKey(productKey);
        // 组建DeviceNames参数   要查看运行状态的设备的名称
        // 如果传入该参数，需同时传入ProductKey。单次查询最多50个设备。
        log.info(deviceNameList.toString());
        request.setDeviceNames(deviceNameList);
        // request.setDeviceNames(deviceNameList);

        // 组建IotIds参数  要查看运行状态的设备ID列表。
        // 如果传入该参数，则无需传入 ProductKey和 DeviceName。
        // IotId作为设备唯一标识符，与 ProductKey 和 DeviceName组合是一一对应的关系。
        // 如果您同时传入 IotId和 ProductKey与 DeviceName组合，则以 IotId为准。
        // List<String> iotIdList = new ArrayList<String>();
        // iotIdList.add("yourIotIds_1");
        // iotIdList.add("yourIotIds_2");
        // iotIdList.add("yourIotIds_3");
        // iotIdList.add("yourIotIds_4");
        // iotIdList.add("yourIotIds_5");
        // request.setIotIds(iotIdList);

        try {
            // 发起请求并获取返回值
            BatchGetDeviceStateResponse response = client.getAcsResponse(request);
            // 处理业务逻辑
            log.info(new Gson().toJson(response));
            return response;
        } catch (ServerException e) {
            log.error("getBatchDeviceStatus: ", e);
        } catch (ClientException e) {
            log.error("ErrCode:" + e.getErrCode());
            log.error("ErrMsg:" + e.getErrMsg());
            log.error("RequestId:" + e.getRequestId());
        }

        return null;
    }


    public static Map getMqttAccount(String regionId, String productKey, String deviceSecret, String deviceName) throws Exception {
        //公共参数签名
        String sb = "clientId" + deviceName +
                "deviceName" + deviceName +
                "productKey" + productKey;

        String sign = CryptoUtil.hmacMd5(sb, deviceSecret);
        //创建httpclient对象
        CloseableHttpClient client = HttpClients.createDefault();
        HttpPost httpPost = new HttpPost("https://iot-auth." + regionId + ".aliyuncs.com/auth/devicename");
        //装填参数
        List<BasicNameValuePair> nvps = new ArrayList<>();
        nvps.add(new BasicNameValuePair("productKey", productKey));
        nvps.add(new BasicNameValuePair("sign", sign));
        nvps.add(new BasicNameValuePair("deviceName", deviceName));
        nvps.add(new BasicNameValuePair("clientId", deviceName));
        nvps.add(new BasicNameValuePair("signmethod", "hmacmd5"));
        //设置参数到请求对象中
        httpPost.setEntity(new UrlEncodedFormEntity(nvps, HTTP.UTF_8));
        //设置header信息
        httpPost.setHeader("Content-type", "application/x-www-form-urlencoded");
        httpPost.setHeader("User-Agent", "Mozilla/4.0 (compatible; MSIE 5.0; Windows NT; DigExt)");
        //执行请求操作，并拿到结果（同步阻塞）
        CloseableHttpResponse response = client.execute(httpPost);
        //获取结果实体
        HttpEntity entity = response.getEntity();
        String body = null;
        if (entity != null) {
            //按指定编码转换结果实体为String类型
            body = EntityUtils.toString(entity, HTTP.UTF_8);
        }
        EntityUtils.consume(entity);

        //释放链接
        response.close();
        int statusCode = response.getStatusLine().getStatusCode();
        if (statusCode != HttpStatus.SC_OK) {
            throw new ClientException("IOT CONNECT ERROR " + statusCode);
        }
        LinkedHashMap result = GsonUtil.cast(body, LinkedHashMap.class);
        Map data = MapUtils.getMap(result, "data");
        String iotId = MapUtils.getString(data, "iotId");
        String iotToken = MapUtils.getString(data, "iotToken");
        return MapUtils.getMap(result, "data");
    }


    /**
     * 发布get主题消息
     *
     * @param accessKey
     * @param accessSecret
     * @param regionId
     * @param data
     * @param deviceName
     */
    public static Boolean pubGetTopicMsg(String accessKey, String accessSecret, String regionId, String data, String productKey, String deviceName) {
        return pubTopicMsg(
                accessKey, accessSecret, regionId,
                data,
                String.format(TOPIC_GET, productKey, deviceName),
                productKey
        );
    }

    public static Boolean pubGetTopicMsg(String accessKey, String accessSecret, String regionId, String iotInstanceId, String data, String productKey, String deviceName) {
        return pubTopicMsg(
                accessKey, accessSecret, regionId, iotInstanceId,
                data,
                String.format(TOPIC_GET, productKey, deviceName),
                productKey
        );
    }


    /**
     * 发布消息
     *
     * @param accessKey
     * @param accessSecret
     * @param regionId
     * @param data
     * @param topic
     */
    public static Boolean pubTopicMsg(String accessKey, String accessSecret, String regionId, String data, String topic, String productKey) {
        if (StringUtil.isEmpty(productKey) || StringUtil.isEmpty(topic) || StringUtil.isEmpty(data)) {
            log.info("pubTopicMsg: invalid params");
            return false;
        }
        //设置client的参数
        DefaultProfile profile = DefaultProfile.getProfile(regionId, accessKey, accessSecret);
        IAcsClient client = new DefaultAcsClient(profile);

        PubRequest request = new PubRequest();
        request.setQos(0);
        log.info("aliyun publish topic: {}", topic);
        request.setTopicFullName(topic);
        request.setProductKey(productKey);
        //设置消息的内容，一定要用base64编码，否则乱码
        request.setMessageContent(Base64Utils.encodeToString(data.getBytes()));
        try {
            PubResponse response = client.getAcsResponse(request);
            log.info("pub result: {}", GsonUtil.toJson(response));
            return response.getSuccess();
        } catch (Exception e) {
            log.error("pubTopic ", e);
        }

        return false;
    }

    public static Boolean pubTopicMsg(String accessKey, String accessSecret, String regionId, String iotInstanceId, String data, String topic, String productKey) {
        if (StringUtil.isEmpty(productKey) || StringUtil.isEmpty(topic) || StringUtil.isEmpty(data)) {
            log.info("pubTopicMsg: invalid params");
            return false;
        }
        //设置client的参数
        DefaultProfile profile = DefaultProfile.getProfile(regionId, accessKey, accessSecret);
        IAcsClient client = new DefaultAcsClient(profile);

        PubRequest request = new PubRequest();
        request.setIotInstanceId(iotInstanceId);
        request.setQos(0);
        log.info("aliyun publish topic: {}", topic);
        request.setTopicFullName(topic);
        request.setProductKey(productKey);
        //设置消息的内容，一定要用base64编码，否则乱码
        request.setMessageContent(Base64Utils.encodeToString(data.getBytes()));
        try {
            PubResponse response = client.getAcsResponse(request);
            log.info("pub result: {}", GsonUtil.toJson(response));
            return response.getSuccess();
        } catch (Exception e) {
            log.error("pubTopic ", e);
        }

        return false;
    }

    /**
     * 创建产品主题
     *
     * @param accessKey
     * @param accessSecret
     * @param regionId
     * @param productKey
     * @param topicShortName
     * @param operation
     * @param iotInstanceId
     * @param desc
     * @return
     */
    public static Boolean createProductTopic(String accessKey, String accessSecret, String regionId, String productKey, String topicShortName, String operation, String iotInstanceId, String desc) {
        CreateProductTopicRequest request = new CreateProductTopicRequest();
        request.setOperation(operation);
        request.setProductKey(productKey);
        request.setTopicShortName(topicShortName);
        request.setIotInstanceId(iotInstanceId);
        request.setDesc(desc);
        //设置client的参数
        DefaultProfile profile = DefaultProfile.getProfile(regionId, accessKey, accessSecret);
        IAcsClient client = new DefaultAcsClient(profile);
        try {
            CreateProductTopicResponse response = client.getAcsResponse(request);
            log.info("createProductTopic result: {}", GsonUtil.toJson(response));
            return response.getSuccess();
        } catch (Exception e) {
            log.error("createProductTopic", e);
        }
        return false;
    }

    /**
     * 更新产品主题
     *
     * @param accessKey
     * @param accessSecret
     * @param regionId
     * @param topicId
     * @param topicShortName
     * @param operation
     * @param iotInstanceId
     * @param desc
     * @return
     */
    public static Boolean updateProductTopic(String accessKey, String accessSecret, String regionId, String topicId, String topicShortName, String operation, String iotInstanceId, String desc) {
        UpdateProductTopicRequest request = new UpdateProductTopicRequest();
        request.setTopicId(topicId);
        request.setOperation(operation);
        request.setTopicShortName(topicShortName);
        request.setIotInstanceId(iotInstanceId);
        request.setDesc(desc);
        //设置client的参数
        DefaultProfile profile = DefaultProfile.getProfile(regionId, accessKey, accessSecret);
        IAcsClient client = new DefaultAcsClient(profile);
        try {
            UpdateProductTopicResponse response = client.getAcsResponse(request);
            log.info("queryProductTopic result: {}", GsonUtil.toJson(response));
            return response.getSuccess();
        } catch (Exception e) {
            log.error("queryProductTopic", e);
        }
        return false;
    }

    /**
     * 查询产品主题信息
     *
     * @param accessKey
     * @param accessSecret
     * @param regionId
     * @param productKey
     * @return
     */
    public static List<QueryProductTopicResponse.ProductTopicInfo> queryProductTopic(String accessKey, String accessSecret, String regionId, String productKey) {
        return queryProductTopic(accessKey, accessSecret, regionId, productKey, null);
    }

    /**
     * 查询产品主题信息
     *
     * @param accessKey
     * @param accessSecret
     * @param regionId
     * @param productKey
     * @param iotInstanceId
     * @return
     */
    public static List<QueryProductTopicResponse.ProductTopicInfo> queryProductTopic(String accessKey, String accessSecret, String regionId, String productKey, String iotInstanceId) {
        QueryProductTopicRequest request = new QueryProductTopicRequest();
        request.setProductKey(productKey);
        //设置client的参数
        DefaultProfile profile = DefaultProfile.getProfile(regionId, accessKey, accessSecret);
        IAcsClient client = new DefaultAcsClient(profile);
        try {
            QueryProductTopicResponse response = client.getAcsResponse(request);
            log.info("queryProductTopic result: {}", GsonUtil.toJson(response));
            return response.getData();
        } catch (Exception e) {
            log.error("queryProductTopic", e);
        }
        return new ArrayList<>();
    }

    /**
     * 删除产品主题
     *
     * @param accessKey
     * @param accessSecret
     * @param regionId
     * @param topicId
     * @param iotInstanceId
     * @return
     */
    public static Boolean deleteProductTopic(String accessKey, String accessSecret, String regionId, String topicId, String iotInstanceId) {
        DeleteProductTopicRequest request = new DeleteProductTopicRequest();
        request.setTopicId(topicId);
        request.setIotInstanceId(iotInstanceId);
        //设置client的参数
        DefaultProfile profile = DefaultProfile.getProfile(regionId, accessKey, accessSecret);
        IAcsClient client = new DefaultAcsClient(profile);
        try {
            DeleteProductTopicResponse response = client.getAcsResponse(request);
            log.info("deleteProductTopic result: {}", GsonUtil.toJson(response));
            return response.getSuccess();
        } catch (Exception e) {
            log.error("deleteProductTopic", e);
        }
        return false;
    }
}

