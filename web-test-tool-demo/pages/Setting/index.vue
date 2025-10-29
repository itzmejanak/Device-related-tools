<template>
  <div class="container">
    <div class="user_head">
      <img
        src="@/assets/img/publicImg/ic_more@2x.png"
        alt=""
        @click="goToInputRack"
      />
      <div class="title" @click="goToInputRack">返回</div>
    </div>
    <div class="tab_set">
      <div
        class="tab_title"
        :class="{ active1: item.isCheck }"
        @click="switchTab(item.id, item.value)"
        v-for="item in tabName"
        :key="item.id"
      >
        {{ item.label }}
      </div>
    </div>
    <div
      class="imei"
      v-show="
        (tabValue == 1 && list1.length > 0) ||
          (tabValue == 2 && list2.length > 0) ||
          tabValue == 4
      "
    >
      IMEI:{{ imeis }}
    </div>
    <div class="imei" v-show="tabValue == 1 && list1.length > 0">
      <span style="margin-right:15px">孔位数：{{ online1 }}/{{ hole1 }}</span
      ><span>异常孔位：{{ abnormal1 }}</span>
    </div>
    <div class="imei" v-show="tabValue == 2 && list2.length > 0">
      <span style="margin-right:15px">孔位数：{{ online2 }}/{{ hole2 }}</span
      ><span>异常孔位：{{ abnormal2 }}</span>
    </div>
    <div class="device">
      <div v-show="tabValue == 1">
        <div
          class="content1"
          :class="{ active2: item.snAsInt == 0 }"
          @click="popupSn(item)"
          v-for="item in list1"
          :key="item.index"
        >
          <div class="content_div">
            <div class="content_sn">SN：{{ item.snAsString }}</div>
            <div>
              <span style="margin-right:15px">孔位：{{ item.index }}</span
              ><span>状态：0x{{ item.status2 }}</span>
            </div>
            <div>
              <span style="margin-right:15px">电量：{{ item.power }}</span
              ><span>温度：{{ item.temp }}</span>
            </div>
            <div>
              <span style="margin-right:15px">电流：{{ item.current }}</span
              ><span>电压：{{ item.voltage }}</span>
            </div>
            <div>电芯电压：{{ item.batteryVol }}</div>
            <div>软件版本：{{ item.softVersion }}</div>
            <div>区域码：{{ item.area }}</div>
            <div>
              微动开关：<span style="color:#005bff">{{ item.switch2 }}</span>
            </div>
            <div>
              电磁阀开关：<span style="color:#005bff">{{ item.switch1 }}</span>
            </div>
            <div>{{ item.snAsInt == 0 ? 'NO' : 'OK' }}</div>
          </div>
        </div>
      </div>
      <div v-show="tabValue == 2">
        <div
          class="content1"
          :class="{ active2: item.snAsInt == 0 }"
          @click="popupSn(item)"
          v-for="item in list2"
          :key="item.index"
        >
          <div class="content_div">
            <div class="content_sn">SN:{{ item.snAsString }}</div>
            <div>
              <span style="margin-right:15px">孔位：{{ item.index }}</span
              ><span>状态：0x{{ item.status2 }}</span>
            </div>
            <div>
              <span style="margin-right:15px">电量：{{ item.power }}</span
              ><span>温度：{{ item.temp }}</span>
            </div>
            <div>
              <span style="margin-right:15px">电流：{{ item.current }}</span
              ><span>电压：{{ item.voltage }}V</span>
            </div>
            <div>电芯电压：{{ item.batteryVol }}</div>
            <div>软件版本：{{ item.softVersion }}</div>
            <div>区域码：{{ item.area }}</div>
            <div>
              微动开关：<span style="color:#005bff">{{ item.switch2 }}</span>
            </div>
            <div>
              电磁阀开关：<span style="color:#005bff">{{ item.switch1 }}</span>
            </div>
            <div>{{ item.snAsInt == 0 ? 'NO' : 'OK' }}</div>
          </div>
        </div>
      </div>

      <div v-show="tabValue == 4">
        <div
          class="content2"
          v-for="item in holeSite"
          :key="item.value"
          @click="popupHole(item.value, 0)"
        >
          {{ item.value }}
        </div>
        <div class="lines" v-show="list2.length > 0"></div>
      </div>

      <div v-show="tabValue == 4 && list2.length > 0">
        <div
          class="content2"
          v-for="item in holeSite"
          :key="item.value"
          @click="popupHole(item.value, 1)"
        >
          {{ item.value }}
        </div>
      </div>
      <div
        v-show="tabValue == 3 || (tabValue == 2 && list2.length == 0)"
        class="blank_div"
      >
        暂无数据...
      </div>
    </div>

    <van-overlay :show="showMask" z-index="99999999">
      <div class="wrapper">
        <van-loading type="spinner" color="#0094ff" vertical
          >刷新中...</van-loading
        >
      </div>
    </van-overlay>
  </div>
</template>

<script>
import Device from '../../assets/js/api/device'
export default {
  data () {
    return {
      showMask: false, // loading遮罩层
      tabName: [
        { value: 1, id: 1, label: '串口1', isCheck: true },
        { value: 2, id: 2, label: '串口2', isCheck: false },
        { value: 3, id: 3, label: '常用命令', isCheck: false },
        { value: 4, id: 4, label: '孔位弹', isCheck: false },
        { value: 1, id: 5, label: '刷新', isCheck: false }
      ], // tab栏
      tabValue: '1', // tab值
      holeSite: [], //孔位按钮
      scanNo: '', // 扫码机柜SN
      list1: [], // 串口1列表
      list2: [], // 串口2列表
      hole: 0, // 孔位总数
      hole1: 0, // 孔位总数
      online1: 0, // 在线孔位
      abnormal1: 0, // 异常孔位
      hole2: 0, // 孔位总数
      online2: 0, // 在线孔位
      abnormal2: 0, // 异常孔位
      imeis: '', // IMEI码
      datas: {
        // 充电宝数据样例
        data: [8, 0, 0, 0, 0, 0, 0, 0, 0, 255, 255, 0, 0, 0, 128],
        pinboardIndex: 16,
        index: 8, //孔位
        status: 0, // 状态 二进制字节转成16进制
        undefined1: 0,
        batteryVol: 0, // 电芯电压/10
        area: 0, //区域码
        sn: [0, 0, 0, 0],
        power: 0, // 电量
        temp: 0, // 温度
        voltage: 0, // 电压/10
        current: 0, // 电流/10
        softVersion: 0, // 软件版本 V1 +
        hardVersion: 0, // 硬件版本
        snAsInt: 0, // sn
        snAsString: '0', //sn
        putaway: false,
        message: 'NONE'
      },

      showCommandDialog: false,
      currentCommandType: '',
      commandForm: {
        param1: '',
        param2: ''
      },
      commandTypes: {
        wifi: {
          title: 'WiFi设置',
          fields: [
            { label: 'WiFi名称', key: 'param1', placeholder: '请输入WiFi名称', type: 'text' },
            { label: 'WiFi密码', key: 'param2', placeholder: '请输入WiFi密码', type: 'password' }
          ],
          required: ['param1', 'param2']
        },
        priority: {
          title: '网络优先级',
          fields: [
            {
              label: '优先级',
              key: 'param1',
              type: 'radio',
              options: [
                { label: 'WiFi优先', value: 'wifi' },
                { label: '4G优先', value: '4g' }
              ]
            }
          ],
          required: ['param1']
        },
        sound: {
          title: '声音设置',
          fields: [
            {
              label: '音量大小',
              key: 'param1',
              type: 'slider',
              min: 0,
              max: 100,
              step: 1
            }
          ],
          required: ['param1']
        },
        version: {
          title: '获取版本',
          noForm: true // 无需表单的指令
        }
      }
    }
  },
  mounted () {
    // this.scanNo = '10120000'
    this.scanNo = this.$route.query.sno
    if (!this.scanNo) {
      this.goToInputRack()
    }
    this.getData()
    this.getDevice()
  },
  methods: {
    // 获取详情
    getDevice () {
      this.list1 = []
      this.list2 = []
      this.hole = 0
      this.online1 = 0
      this.abnormal1 = 0
      this.online2 = 0
      this.abnormal2 = 0
      this.showMask = true
      Device.getDevice({ scanNo: this.scanNo }).then(res => {
        if (res.code == 0) {
          res.data.data.pinboards.map(item => {
            return {
              ...item,
              status2: 0,
              switch1: '闭合',
              switch2: '闭合'
            }
          })
          this.hole = res.data.data.hole
          this.imeis = res.data.deviceName
          let getpinboards = res.data.data.pinboards
          let lists = res.data.data.powerbanks
          getpinboards.forEach(item => {
            lists.forEach(items => {
              if (item.io === 0 && item.index === items.pinboardIndex) {
                this.list1.push(items)
              }
              if (item.io === 1 && item.index === items.pinboardIndex) {
                this.list2.push(items)
              }
            })
          })
          this.hole1 = this.list1.length
          this.hole2 = this.list2.length
          this.list1.forEach(item => {
            if (item.snAsInt > 0) {
              this.online1 += 1
            }
            if (item.snAsInt === 0) {
              item.snAsString = '--'
            }
            if (item.status > 1) {
              this.abnormal1 += 1
            }
            item.status2 = item.status.toString(16).toUpperCase()
            if (item.status2.length === 1) {
              item.status2 = '0' + item.status2
            }
            if (
              item.softVersion > 0 &&
              item.softVersion.toString().length > 2
            ) {
              item.softVersion = 'V1' + item.softVersion
            } else if (
              item.softVersion > 0 &&
              item.softVersion.toString().length > 1
            ) {
              item.softVersion = 'V10' + item.softVersion
            }
            if (item.solenoidValveSwitch === 0) {
              item.switch1 = '闭合'
            } else {
              item.switch1 = '断开'
            }
            if (item.microSwitch === 0) {
              item.switch2 = '闭合'
            } else {
              item.switch2 = '断开'
            }
            item.current = (item.current / 10).toFixed(1)
            item.voltage = (item.voltage / 10).toFixed(1)
            item.batteryVol = (item.batteryVol / 10).toFixed(1)
          })
          this.list2.forEach(item => {
            if (item.snAsInt > 0) {
              this.online2 += 1
            }
            if (item.snAsInt === 0) {
              item.snAsString = '--'
            }
            if (item.status > 1) {
              this.abnormal2 += 1
            }
            item.status2 = item.status.toString(16).toUpperCase()
            if (item.status2.length === 1) {
              item.status2 = '0' + item.status2
            }
            if (
              item.softVersion > 0 &&
              item.softVersion.toString().length > 2
            ) {
              item.softVersion = 'V1' + item.softVersion
            } else if (
              item.softVersion > 0 &&
              item.softVersion.toString().length > 1
            ) {
              item.softVersion = 'V10' + item.softVersion
            }
            if (item.solenoidValveSwitch === 0) {
              item.switch1 = '闭合'
            } else {
              item.switch1 = '断开'
            }
            if (item.microSwitch === 0) {
              item.switch2 = '闭合'
            } else {
              item.switch2 = '断开'
            }
            item.current = (item.current / 10).toFixed(1)
            item.voltage = (item.voltage / 10).toFixed(1)
            item.batteryVol = (item.batteryVol / 10).toFixed(1)
          })
        }
        this.showMask = false
      })
    },
    //生成孔位
    getData () {
      let arr = []
      for (let i = 0; i < 36; i++) {
        arr.push({ value: `${i + 1}` })
      }
      this.holeSite = arr
    },
    // 返回扫码页
    goToInputRack () {
      this.$router.push({
        name: 'Start'
      })
    },
    //切换tab栏
    switchTab (id, value) {
      this.tabName.forEach(item => {
        if (id === item.id) {
          item.isCheck = true
        } else {
          item.isCheck = false
        }
      })
      this.tabValue = value
      if (id === 5) {
        this.tabName = this.$options.data.call(this).tabName
        this.getDevice()
      }
    },
    // SN弹出
    popupSn (item) {
      if (item.snAsInt != 0) {
        const params = {
          pbNo: item.snAsInt,
          cabinetNo: this.imeis
        }
        Device.popupSN(params).then(res => {
          if (res.code == 0) {
            this.global.toast(res.message)
            item.snAsInt = 0
            // this.getDevice()
          } else {
            this.global.toast(res.message)
          }
        })
      }
    },
    //孔位弹出
    popupHole (item, e) {
      const params = {
        io: e,
        cabinetNo: this.imeis,
        pos: item
      }
      Device.popupHole(params).then(res => {
        if (res.code == 0) {
          // this.getDevice()
          this.global.toast(res.message)
        } else {
          this.global.toast(res.message)
        }
      })
    },

    // 组装请求参数
    buildCommandData() {
      const { currentCommandType, commandForm, deviceVersion } = this;
      const base = { cabinetNo: this.imeis || this.scanNo };

      switch(currentCommandType) {
        case 'setWifi':
          return {
            ...base,
            data: JSON.stringify({
              cmd: "setWifi",
              username: commandForm.param1,
              password: commandForm.param2
            })
          };
        case 'set_wifi':
          return {
            ...base,
            data: JSON.stringify({
              cmd: "set_wifi",
              name: commandForm.param1,
              password: commandForm.param2
            })
          };
        case 'priority':
          return {
            ...base,
            data: JSON.stringify({
              cmd: "setMode",
              data: commandForm.param1
            })
          };

        case 'sound':
          return {
            ...base,
            data: JSON.stringify({
              cmd: "volume",
              data: commandForm.param1.toString()
            })
          };

        case 'version':
          return {
            ...base,
            data: JSON.stringify({
              cmd: "get_version"
            })
          };

        default:
          return base;
      }
    },

    // 提交设备指令
    async submitDeviceCommand() {
      try {
        const commandConfig = this.commandTypes[this.currentCommandType];

        // 验证必填字段
        if (commandConfig.required) {
          for (const field of commandConfig.required) {
            if (!this.commandForm[field]) {
              this.global.toast(`请输入${commandConfig.fields.find(f => f.key === field).label}`);
              return;
            }
          }
        }

        this.showMask = true;
        const params = this.buildCommandData();
        const res = await Device.sendCommand(params);

        if (res.code === 0) {
          this.global.toast(`${commandConfig.title}成功`);
          // 如果是获取版本，处理返回数据
          if (this.currentCommandType === 'version') {
            console.log('版本信息:', res.data);
          }
        } else {
          this.global.toast(res.message || `${commandConfig.title}失败`);
        }
      } catch (error) {
        this.global.toast('网络错误，请重试');
        console.error('指令发送错误:', error);
      } finally {
        this.showMask = false;
        this.showCommandDialog = false;
      }
    },

    // 直接执行的指令（无需表单）
    executeDirectCommand(type) {
      this.currentCommandType = type;
      this.submitDeviceCommand();
    }
  }
}
</script>
<style lang="scss" scoped>
.container {
  width: 100%;
  height: 100%;
  position: relative;

  .user_head {
    height: 6.6rem;
    width: 100%;
    display: flex;
    align-items: center;
    // position: fixed;
    border-bottom: 1px solid #000;

    img {
      width: 1.5rem;
      height: 3.6rem;
      margin-left: 2rem;
    }

    .title {
      margin-left: 15px;
      font-size: 25px;
      color: #000;
    }
  }
  .tab_set {
    width: calc(100% - 32px);
    margin-left: 16px;
    background-color: #c4c4c4;
    border-radius: 5px;
    height: 50px;
    line-height: 50px;
    margin-top: 20px;
    display: flex;
    align-items: center;
    justify-content: center;
    .tab_title {
      width: 20%;
      text-align: center;
      border-right: 1px solid #eeeeee;
      font-size: 16px;
    }
    .active1 {
      background: #f1f3f4;
    }
  }
  .imei {
    text-align: center;
    font-size: 16px;
    margin-top: 15px;
    font-weight: 600;
    color: #000;
  }
  .device {
    margin: 0 6px;
    margin-top: 10px;
    .content1 {
      border-radius: 5px;
      float: left;
      width: calc(45.3%);
      margin-left: 10px;
      margin-bottom: 10px;
      background: #3fdd86;
      border: 1px solid #999999;
      .content_div {
        padding: 20px 0 30px 10px;
        div {
          margin-bottom: 6px;
          font-size: 13px;
        }
        .content_sn {
          font-weight: 600;
        }
      }
    }
    .active2 {
      background: #ffffff;
    }

    .content2 {
      border-radius: 5px;
      float: left;
      // width: calc(13.3%);
      position: relative;
      width: calc(21.3%);
      height: 35px;
      line-height: 35px;
      margin-left: 10.5px;
      margin-bottom: 15px;
      background: #1aad19;
      font-size: 14px;
      color: #fff;
      text-align: center;
    }
    .lines {
      position: absolute;
      width: calc(100% - 32px) !important;
      margin-left: 10px;
      top: 57.7%;
      width: 100%;
      height: 1px;
      border-bottom: 1px solid #9a9a9a;
    }
    .blank_div {
      text-align: center;
      font-size: 14px;
      margin-top: 20px;
      font-size: 500;
    }
  }
  .wrapper {
    display: flex;
    align-items: center;
    justify-content: center;
    height: 100%;
  }

  .command-buttons {
    padding: 20px;

    .wifi-button {
      margin-bottom: 15px;
    }
  }

  /* 确保弹框内容有适当间距 */
  .van-dialog {
    .van-field {
      margin: 15px;
    }
  }
}
</style>
