# IoT Demo Application

The code in this project is primarily for reference. 
Please modify it according to your actual project structure and database. 
You can synchronize the demo's interfaces or logic to your actual project.

## Environment

- Java JDK 1.8
- Redis
- Database: MySQL or other
- RabbitMQ: Requires the rabbitmq_delayed_message_exchange delay plugin
- EMQX: Install open-source version 5.4.1 / 5.9.1 / Purchase EMQX cloud service

### Environment Configuration

Open ports on the server
- **RabbitMQ** 15672
- **EMQX（open-source version）** 1883 / 18083

#### RabbitMQ
Add virtual-host; console address is as follows  
http://your-server-IP:15672/#/vhosts

#### EMQX
If you installed the open-source version of EMQX 5.4.1 / 5.9.1, 
please refer to the attached EMQX guide.docx for configuration instructions.

### Project Configuration

- application-test.yml

    - datasource
    ```yml
    datasource:
      driver-class-name: com.mysql.cj.jdbc.Driver
      url: jdbc:mysql://127.0.0.1:3306/db_sharing_test?useSSL=false&characterEncoding=utf-8&allowMultiQueries=true&serverTimezone=GMT%2B8
      username: your-username
      password: your-password
    ```

    - redis
    ```yml
    redis:
        host: 127.0.0.1
        port: 6379
        password: your-password
        database: 0
    ```

    - rabbitmq
    ```yml
    rabbitmq:
        addresses: 127.0.0.1
        port: 5672
        username: your-login-username
        password: your-login-password
        virtual-host: /your-virtual-host
    ```

- [EmqxUtil](#com/brezze/share/communication/utils/EmqxUtil.java)
```java
    public static final String BASIC_URL = "http://%s:emqx port";
    public static final String USERNAME = "your-username";
    public static final String PASSWORD = "your-password";
    public static final String PRODUCT_KEY = "powerbank";
    public static final String HOST = "emqx host";
```

## API Endpoints

### Device API ( YbtController )
- `GET /api/iot/client/con` - Android device authentication
- `POST /api/iot/client/con` - MCU device authentication
- `POST /api/rentbox/upload/data` - Whole device report
- `GET /api/rentbox/order/return` - Return power bank report
- `GET/POST /api/rentbox/config/data` - Device configuration report
- `GET /api/iot/app/version/publish/mcu` - Get MCU release version
- `GET /api/iot/app/version/test/mcu` - Get MCU test version
- `GET /api/iot/app/version/publish/chip` - Get chip release version
- `GET /api/iot/app/version/test/chip` - Get chip test version
- `GET /api/advert/rentbox/distribute/list` - Get advertisement list

### Device Commands ( MqttCmdController )
- `POST /communication/ybt/check` - Partial query (query 5 power banks with highest power)
- `POST /communication/ybt/check-all` - Full query (query whole device data)
- `POST /communication/ybt/openLock` - Pop up by slot
- `POST /communication/ybt/popup_sn` - Pop up by power bank SN
- `POST /communication/ybt/reset-mcu` - Reset MCU
- `POST /communication/ybt/restart` - Restart cabinet
- `POST /communication/ybt/load-ad` - Update advertisement
- `POST /communication/ybt/push-version-publish` - Push Android or 4G module release upgrade
- `POST /communication/ybt/push-version-test` - Push Android or 4G module test upgrade
- `POST /communication/ybt/push-version-holesite` - Push holesite release upgrade
- `POST /communication/ybt/upload-all` - HTTP whole device report
- `POST /communication/ybt/set-wifi` - Set WiFi (popular version cabinet)
- `POST /communication/ybt/volume` - Set network priority (popular version cabinet)
- `POST /communication/ybt/setAudio` - Set audio (popular version cabinet)
- `POST /communication/ybt/setMode` - Set network mode (popular version cabinet)
- `POST /communication/ybt/wifi` - Get WiFi list (popular version cabinet)

### Binding station SN and IMEI ( BindingController )
- `POST /communication/common/brezze-test-util/cabinets/bind` - Bind station SN and IMEI
- `POST /communication/common/brezze-test-util/cabinets/unbind` - Unbind station SN

### Test Tools ( YbtController )
- `POST /communication/ybt/send` - Send command (general)
- `GET /communication/ybt/check-all` - Get station detail (test tool)
- `POST /communication/ybt/test-util/popup_sn` - Pop up by power bank SN (test tool)
- `POST /communication/ybt/test-util/openLock` - Pop up by slot (test tool)

## Deployment
- Modify the JDK_PATH and BASE_PATH paths in the script
- Run the script file server-boot.sh

```bash
sh server-boot.sh restart communication
```


device-util-demo.zip
web-cabinet-bind.zip
web-test-tool-demo.zip
[EMQX guide.docx](https://docs.google.com/document/d/1-6Sux_fhJDB7onOax436J0DihLtZSm0O/edit?usp=drive_link&ouid=112010168511677759995&rtpof=true&sd=true)
[Document.docx](https://docs.google.com/document/d/1rzpTREzZrVeXuyf6OqkreZ1FyIonm1iR/edit?usp=drive_link&ouid=112010168511677759995&rtpof=true&sd=true)

## Binding tools

A QR code image with the device's serial number link will be affixed to the outside of the device.
The system backend needs to map each device's serial number (SN) to its IMEI, therefore a binding tool needs to be provided to the factory so that workers can immediately enter the information into the backend system after assembling the equipment.

Project：web-cabinet-bind.zip

Code language: HTML, JS

[Binding tool interface](com/brezze/share/communication/controller/BindingController.java)

### Function Introduction
It enables the interception of device SN and device IMEI, and binds them through the interface.

Device serial number (SN) extraction rules are as follows:

- If `=` exists: Extract the content after the last `=`.
- If `=` does not exist, but `/` exists: Extract the content after the last `/`.
- Pure numbers: Extract the entire text.

Device IMEI interception, interception rules are as follows:

  * The binding tool must be compatible with the following IMEI rules: Scanning the IMEI number QR code will produce one of the following three results:
  * First: 863597070962890;MPY23KI0W005847;94706C5C5072 (The string contains a semicolon; extract the first 15 digits)
  * Second: M322M16EHD091204096 864601068508480 (The string does not contain a semicolon; extract the last 15 digits)
  * Third: https://s.dudubox.com/sw'member/factory/scan.html?uuid=861766049773702 (The string does not contain a semicolon; extract the last 15 digits)
  * The binding tool must also be compatible with the WIFI module. For binding, the MAC address must be intercepted: This will be displayed when scanning the code. *;MPN23LP05002679;441A847D6121;;;441A847D5560;
  * Then you need to extract the MAC address <441A847D6121> from the middle for the binding operation. Thank you!

### Configuration
```js
var baseUrl = "https://your-domain/communication";
var bindUrl = baseUrl + "/common/brezze-test-util/cabinets/bind";
var unbindUrl = baseUrl + "/common/brezze-test-util/cabinets/unbind";
```

### Deploy
After extracting the compressed file, rename the project web-cabinet-bind to another name, such as binding.

Deploy the project to nginx (or another static resource management) and provide the access link.

## Testing tools

This tool is designed to help production personnel perform functional tests on the equipment by scanning the device's SN/IMEI after assembly, thereby verifying whether the equipment is functioning properly.

Project：web-test-tool-demo.zip

Framework: VUE2

Main test functions
- View device details
- Test power bank SN pop-up

### Configuration
- Modify .env.prod

```vue
BASE_URL=https://your-domain
```

If the API path changes, you need to modify the path in the device.js

File path: assets/js/api/device.js

```js
export default {
  // Get device details
  getDevice: (params) => {
    return api.get('/communication/ybt/check-all',params)
  },
  //Pop up by power bank SN
  popupSN(params){
    return api.post('/communication/ybt/test-util/popup_sn', params)
  },
  //Pop up by slot
  popupHole(params){
    return api.post('/communication/ybt/test-util/openLock', params)
  },
  //Send command (general)
  sendCmd(params){
    return api.post('/communication/ybt/send', params)
  },
}
```

### Build package and Deploy

```bash
npm run generate
```

Rename the project name `dist` to something else, such as `factory`.

Deploy the project to the nginx directory (or another static resource management), and provide the access link.

Like https://your-domain/factory/#/











