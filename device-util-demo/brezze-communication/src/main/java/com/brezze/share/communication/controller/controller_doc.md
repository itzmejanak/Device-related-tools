# Documentation for Directory: controller

**Path:** com.brezze.share.communication.controller

**Total Java files:** 7

**Output mode:** brief

## Summary

- **Files:** 7
- **Classes:** 7
- **Methods:** 43

## BindingController.java

### Classes

#### BindingController

### Methods

- `Rest bind(String scanNo, String imei, String vietqr)`
- `Rest unbind(String scanNo, String imei)`

## CommonController.java

### Classes

#### CommonController

### Methods

- `Rest<SysConfigPreAuthAmountDTO> getPreAuthAmount(HttpServletRequest request, String uuid)`

## EmqxController.java

### Classes

#### EmqxController

### Methods

- `void event(String body)`
- `void message(String data)`
- `String getDeviceName(String topic)`
- `void sendMQ(Object data, String routingKey)`

## MqttCmdController.java

### Classes

#### MqttCmdController

### Methods

- `Rest detail(CmdYbtREQ req)`
- `Rest detailAll(CmdYbtREQ req, HttpServletRequest request)`
- `Rest openLock(CmdYbtREQ req, HttpServletRequest request)`
- `Rest rentByPbNo(CmdYbtREQ req, HttpServletRequest request)`
- `Rest mcuReset(CmdYbtREQ req, HttpServletRequest request)`
- `Rest restart(CmdYbtREQ req, HttpServletRequest request)`
- `Rest loadAd(CmdYbtREQ req)`
- `Rest pushVersionPublish(CmdYbtREQ req)`
- `Rest pushVersionTest(CmdYbtREQ req)`
- `Rest pushVersionHolesite(CmdYbtREQ req)`
- `Rest uploadAll(CmdYbtREQ req)`
- `Rest setWifi(CmdYbtREQ req)`
- `Rest volume(CmdYbtREQ req)`
- `Rest setAudio(CmdYbtREQ req)`
- `Rest setMode(CmdYbtREQ req)`
- `Rest<ReceiveWifi> getWifiList(CmdYbtREQ req)`

## OrderController.java

### Classes

#### OrderController

### Methods

- `Rest getOrderInfoViaEmail(HttpServletRequest request, String orderNo, String email)`

## UserController.java

### Classes

#### UserController

### Methods

- `Rest<ConnectTokenDTO> getConnectToken(HttpServletRequest request, String locationId)`
- `Rest<RentDTO> capturedPayment(HttpServletRequest request, String paymentIntentId, String paymentMethod, String cabinetNo)`

## YbtController.java

### Classes

#### YbtController

### Methods

- `void androidDevice(String host, String simUUID, String simMobile, String uuid, String deviceId, String sign, HttpServletRequest request, HttpServletResponse response)`
- `void mcuDevice(String host, String simUUID, String simMobile, String uuid, String deviceId, String sign, HttpServletRequest request, HttpServletResponse response)`
- `void uploadDetail(HttpServletRequest request, HttpServletResponse response, String host, byte[] bytes, String rentboxSN, String sign, String signal, Integer io, String CRC16, String IAP16, String dRotationEnable, String dRotationNumber, String dRotationRefer, String dMotorEnable)`
- `void orderReturn(String rentboxSN, String singleSN, String hole, String sign, Integer power, HttpServletResponse response, HttpServletRequest request)`
- `void getConfig(HttpServletRequest request, HttpServletResponse response, String host, String rentboxSN, String dRotationRefer, String dHeadConfig, String dRotationNumber, String dRotationEnable, String dMotorEnable, String dAreaConfig, String sign, String body)`
- `void getMcuReleaseVersion(String appUuid, String deviceUuid, String sign, HttpServletResponse response)`
- `void getMcuTestVersion(String appUuid, String deviceUuid, String sign, HttpServletResponse response)`
- `void getChipReleaseVersion(String appUuid, String deviceUuid, String sign, HttpServletResponse response)`
- `void getChipTestVersion(String appUuid, String deviceUuid, String sign, HttpServletResponse response)`
-  `void getAdvertList(HttpServletRequest request, HttpServletResponse response, String uuid, String position, String sign)`
- `void sendCmd(HttpServletResponse response, CmdYbtREQ req)`
- `void checkAll(HttpServletResponse response, String scanNo)`
- `void rentByPbNoOrdered(HttpServletResponse response, CmdYbtREQ req)`
- `void openLockOrdered(HttpServletResponse response, CmdYbtREQ req)`