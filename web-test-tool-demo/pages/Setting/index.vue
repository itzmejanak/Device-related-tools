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

      <!-- 常用命令界面 -->
      <div v-show="tabValue == 3" class="command-container">
        <!-- 自定义命令输入框 - 置顶 -->
        <div class="command-input-section">
          <h3>自定义命令</h3>
          <van-field
            v-model="customCommand"
            type="textarea"
            placeholder='请输入JSON格式的命令，例如：{"cmd":"get_version"}'
            rows="3"
            autosize
            class="command-textarea"
          />
          <van-button
            type="primary"
            block
            @click="sendCustomCommand"
            :disabled="!isValidJson"
            class="send-command-btn"
          >
            发送命令
          </van-button>
          <div v-if="customCommand && !isValidJson" class="error-tip">
            JSON格式不正确，请检查
          </div>
        </div>

        <div class="command-section">
          <h3>WiFi设置</h3>
          <div class="command-buttons">
            <van-button
              class="command-button"
              type="primary"
              block
              @click="showWifiDialog('business')"
            >
              商务版设置WiFi
            </van-button>
            <van-button
              class="command-button"
              type="primary"
              block
              @click="showWifiDialog('popular')"
            >
              普及版设置WiFi
            </van-button>
          </div>
        </div>

        <div class="command-section">
          <h3>网络优先级</h3>
          <div class="command-buttons">
            <van-button
              class="command-button"
              type="primary"
              block
              @click="sendPriorityCommand('wifi')"
            >
              设置WiFi优先
            </van-button>
            <van-button
              class="command-button"
              type="primary"
              block
              @click="sendPriorityCommand('4g')"
            >
              设置4G优先
            </van-button>
          </div>
        </div>

        <div class="command-section">
          <h3>声音设置</h3>
          <div class="command-buttons">
            <van-field
              v-model="soundVolume"
              type="number"
              placeholder="请输入音量(0-100)"
              label="音量"
            />
            <van-button
              class="command-button"
              type="primary"
              block
              @click="sendSoundCommand"
            >
              设置音量
            </van-button>
          </div>
        </div>

        <div class="command-section">
          <h3>漫游设置</h3>
          <div class="command-buttons">
            <van-button
              class="command-button"
              type="primary"
              block
              @click="sendRoamingCommand('open')"
            >
              开启漫游
            </van-button>
            <van-button
              class="command-button"
              type="primary"
              block
              @click="sendRoamingCommand('close')"
            >
              关闭漫游
            </van-button>
          </div>
        </div>

        <div class="command-section">
          <h3>机芯数据上报</h3>
          <div class="command-buttons">
            <van-button
              class="command-button"
              type="primary"
              block
              @click="sendBufferUploadCommand('true')"
            >
              开启上报
            </van-button>
            <van-button
              class="command-button"
              type="primary"
              block
              @click="sendBufferUploadCommand('false')"
            >
              关闭上报
            </van-button>
          </div>
        </div>

        <div class="command-section">
          <h3>WiFi开关（带屏）</h3>
          <div class="command-buttons">
            <van-button
              class="command-button"
              type="primary"
              block
              @click="sendWifiSwitchCommand('open')"
            >
              开启WiFi
            </van-button>
            <van-button
              class="command-button"
              type="primary"
              block
              @click="sendWifiSwitchCommand('close')"
            >
              关闭WiFi
            </van-button>
          </div>
        </div>

        <div class="command-section">
          <h3>APN设置</h3>
          <div class="command-buttons">
            <van-field
              v-model="apnSetting"
              placeholder="请输入APN"
              label="APN"
            />
            <van-button
              class="command-button"
              type="primary"
              block
              @click="sendApnCommand"
            >
              设置APN
            </van-button>
          </div>
        </div>

        <div class="command-section">
          <h3>日志设置</h3>
          <div class="command-buttons">
            <van-button
              class="command-button"
              type="primary"
              block
              @click="sendLogCommand('open')"
            >
              开启本地日志
            </van-button>
            <van-button
              class="command-button"
              type="primary"
              block
              @click="sendLogCommand('close')"
            >
              关闭本地日志
            </van-button>
            <van-button
              class="command-button"
              type="primary"
              block
              @click="sendCommand('upload_app_log')"
            >
              上传APP日志
            </van-button>
          </div>
        </div>

        <div class="command-section">
          <h3>系统命令</h3>
          <div class="command-buttons">
            <van-button
              class="command-button"
              type="primary"
              block
              @click="sendCommand('get_version')"
            >
              获取APK版本
            </van-button>
            <van-button
              class="command-button"
              type="primary"
              block
              @click="sendCommand('apk_restart')"
            >
              APK重启
            </van-button>
            <van-button
              class="command-button"
              type="primary"
              block
              @click="sendCommand('upload_screen_capture')"
            >
              屏幕截图
            </van-button>
            <van-button
              class="command-button"
              type="primary"
              block
              @click="sendCommand('reboot')"
            >
              机柜重启
            </van-button>
            <van-button
              class="command-button"
              type="primary"
              block
              @click="sendCommand('upload_all')"
            >
              HTTP整机上报
            </van-button>
            <van-button
              class="command-button"
              type="primary"
              block
              @click="sendCommand('push_version_publish')"
            >
              下发升级
            </van-button>
            <van-button
              class="command-button"
              type="primary"
              block
              @click="sendCommand('load_ad')"
            >
              更新广告
            </van-button>
          </div>
        </div>
      </div>

      <div
        v-show="tabValue == 2 && list2.length == 0"
        class="blank_div"
      >
        暂无数据...
      </div>
    </div>

    <!-- WiFi设置弹窗 -->
    <van-dialog
      v-model="showWifiDialogFlag"
      :title="wifiDialogTitle"
      show-cancel-button
      @confirm="confirmWifiSetting"
    >
      <van-field
        v-model="wifiForm.name"
        placeholder="请输入WiFi名称"
        label="WiFi名称"
      />
      <van-field
        v-model="wifiForm.password"
        placeholder="请输入WiFi密码"
        label="WiFi密码"
        type="password"
      />
    </van-dialog>

    <van-overlay :show="showMask" z-index="99999999">
      <div class="wrapper">
        <van-loading type="spinner" color="#0094ff" vertical
        >刷新中...
        </van-loading
        >
      </div>
    </van-overlay>
  </div>
</template>

<script>
import Device from '../../assets/js/api/device'

export default {
  data() {
    return {
      showMask: false, // loading遮罩层
      tabName: [
        {value: 1, id: 1, label: '串口1', isCheck: true},
        {value: 2, id: 2, label: '串口2', isCheck: false},
        {value: 3, id: 3, label: '常用命令', isCheck: false},
        {value: 4, id: 4, label: '孔位弹', isCheck: false},
        {value: 1, id: 5, label: '刷新', isCheck: false}
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

      // 自定义命令输入框
      customCommand: '',

      // 常用命令相关数据
      showWifiDialogFlag: false,
      wifiDialogType: '', // 'business' 或 'popular'
      wifiForm: {
        name: '',
        password: ''
      },
      soundVolume: '100',
      apnSetting: '',

      datas: {
        // 充电宝数据样例
        data: [8, 0, 0, 0, 0, 0, 0, 0, 0, 255, 255, 0, 0, 0, 128],
        pinboardIndex: 16,
        index: 8, //孔位
        status: 0, // 状态 二进制字节转成16进制
        undefined1: 0,
        undefined2: 0,
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
      }
    }
  },
  computed: {
    wifiDialogTitle() {
      return this.wifiDialogType === 'business' ? '商务版设置WiFi' : '普及版设置WiFi'
    },

    // 检查JSON格式是否有效
    isValidJson() {
      if (!this.customCommand.trim()) {
        return false
      }
      try {
        JSON.parse(this.customCommand)
        return true
      } catch (error) {
        return false
      }
    }
  },
  mounted() {
    this.scanNo = this.$route.query.sno
    if (!this.scanNo) {
      this.goToInputRack()
    }
    this.getData()
    this.getDevice()
  },
  methods: {
    // 获取详情
    getDevice() {
      this.list1 = []
      this.list2 = []
      this.hole = 0
      this.online1 = 0
      this.abnormal1 = 0
      this.online2 = 0
      this.abnormal2 = 0
      this.showMask = true
      Device.getDevice({scanNo: this.scanNo}).then(res => {
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
              if (item.io == 0 && item.index == items.pinboardIndex) {
                this.list1.push(items)
              }
              if (item.io == 1 && item.index == items.pinboardIndex) {
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
            if (item.snAsInt == 0) {
              item.snAsString = '--'
            }
            if (item.status > 1) {
              this.abnormal1 += 1
            }
            item.status2 = item.status.toString(16).toUpperCase()
            if (item.status2.length == 1) {
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
            if (item.solenoidValveSwitch == 0) {
              item.switch1 = '闭合'
            } else {
              item.switch1 = '断开'
            }
            if (item.microSwitch == 0) {
              item.switch2 = '闭合'
            } else {
              item.switch2 = '断开'
            }
            item.current = (item.current / 10).toFixed(1)
            item.voltage = (item.voltage / 10).toFixed(1)
            if (item.batteryVol > 0){
              item.batteryVol = ((item.batteryVol + 200) / 100).toFixed(1)
            }
          })
          this.list2.forEach(item => {
            if (item.snAsInt > 0) {
              this.online2 += 1
            }
            if (item.snAsInt == 0) {
              item.snAsString = '--'
            }
            if (item.status > 1) {
              this.abnormal2 += 1
            }
            item.status2 = item.status.toString(16).toUpperCase()
            if (item.status2.length == 1) {
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
            if (item.solenoidValveSwitch == 0) {
              item.switch1 = '闭合'
            } else {
              item.switch1 = '断开'
            }
            if (item.microSwitch == 0) {
              item.switch2 = '闭合'
            } else {
              item.switch2 = '断开'
            }
            item.current = (item.current / 10).toFixed(1)
            item.voltage = (item.voltage / 10).toFixed(1)
            if (item.batteryVol > 0){
              item.batteryVol = ((item.batteryVol + 200) / 100).toFixed(1)
            }
          })
        }
        this.showMask = false
      })
    },
    //生成孔位
    getData() {
      let arr = []
      for (let i = 0; i < 36; i++) {
        arr.push({value: `${i + 1}`})
      }
      this.holeSite = arr
    },
    // 返回扫码页
    goToInputRack() {
      this.$router.push({
        name: 'Start'
      })
    },
    //切换tab栏
    switchTab(id, value) {
      this.tabName.forEach(item => {
        if (id == item.id) {
          item.isCheck = true
        } else {
          item.isCheck = false
        }
      })
      this.tabValue = value
      if (id == 5) {
        this.tabName = this.$options.data.call(this).tabName
        this.getDevice()
      }
    },
    // SN弹出
    popupSn(item) {
      if (item.snAsInt != 0) {
        const params = {
          pbNo: item.snAsInt,
          cabinetNo: this.imeis
        }
        Device.popupSN(params).then(res => {
          if (res.code == 0) {
            this.global.toast(res.message)
            item.snAsInt = 0
          } else {
            this.global.toast(res.message)
          }
        })
      }
    },
    //孔位弹出
    popupHole(item, e) {
      const params = {
        io: e,
        cabinetNo: this.imeis,
        pos: item
      }
      Device.popupHole(params).then(res => {
        if (res.code == 0) {
          this.global.toast(res.message)
        } else {
          this.global.toast(res.message)
        }
      })
    },

    // 发送自定义命令
    sendCustomCommand() {
      if (!this.isValidJson) {
        this.global.toast('请输入有效的JSON格式命令')
        return
      }

      if (!this.imeis && !this.scanNo) {
        this.global.toast('设备信息不存在')
        return
      }

      this.showMask = true
      const params = {
        cabinetNo: this.imeis || this.scanNo,
        data: this.customCommand
      }

      Device.sendCmd(params).then(res => {
        if (res.code == 0) {
          this.global.toast('命令发送成功')
          this.customCommand = '' // 清空输入框
        } else {
          this.global.toast(res.message || '命令发送失败')
        }
      }).catch(error => {
        this.global.toast('网络错误，请重试')
        console.error('自定义命令发送错误:', error)
      }).finally(() => {
        this.showMask = false
      })
    },

    // 常用命令相关方法
    showWifiDialog(type) {
      this.wifiDialogType = type
      this.wifiForm.name = ''
      this.wifiForm.password = ''
      this.showWifiDialogFlag = true
    },

    confirmWifiSetting() {
      if (!this.wifiForm.name || !this.wifiForm.password) {
        this.global.toast('请输入WiFi名称和密码')
        return
      }

      let cmdData = {}
      if (this.wifiDialogType === 'business') {
        cmdData = {
          cmd: "set_wifi",
          name: this.wifiForm.name,
          password: this.wifiForm.password
        }
      } else {
        cmdData = {
          cmd: "setWifi",
          username: this.wifiForm.name,
          password: this.wifiForm.password
        }
      }

      this.sendCommandData(cmdData, 'WiFi设置')
    },

    sendPriorityCommand(mode) {
      const cmdData = {
        cmd: "setMode",
        data: mode
      }
      this.sendCommandData(cmdData, `设置${mode.toUpperCase()}优先`)
    },

    sendSoundCommand() {
      if (!this.soundVolume) {
        this.global.toast('请输入音量值')
        return
      }
      const volume = parseInt(this.soundVolume)
      if (volume < 0 || volume > 100) {
        this.global.toast('音量值应在0-100之间')
        return
      }

      const cmdData = {
        cmd: "volume",
        data: volume.toString()
      }
      this.sendCommandData(cmdData, '声音设置')
    },

    sendRoamingCommand(action) {
      const cmdData = {
        cmd: action === 'open' ? "open_roam" : "close_roam"
      }
      this.sendCommandData(cmdData, `${action === 'open' ? '开启' : '关闭'}漫游`)
    },

    sendApnCommand() {
      if (!this.apnSetting) {
        this.global.toast('请输入APN')
        return
      }
      const cmdData = {
        cmd: "set_apn",
        data: this.apnSetting
      }
      this.sendCommandData(cmdData, 'APN设置')
    },

    sendLogCommand(action) {
      const cmdData = {
        cmd: action === 'open' ? "open_log_recording" : "close_log_recording"
      }
      this.sendCommandData(cmdData, `${action === 'open' ? '开启' : '关闭'}本地日志`)
    },

    sendBufferUploadCommand(action) {
      const cmdData = {
        cmd: "buffer_upload",
        data: action === 'true' ? "true" : "false"
      }
      this.sendCommandData(cmdData, `${action === 'true' ? '开启' : '关闭'}机芯数据上报`)
    },

    sendWifiSwitchCommand(action) {
      const cmdData = {
        cmd: action === 'open' ? "open_wifi" : "close_wifi"
      }
      this.sendCommandData(cmdData, `${action === 'true' ? '开启' : '关闭'}WiFi`)
    },

    sendCommand(cmdType) {
      let cmdData = {}
      switch (cmdType) {
        case 'get_version':
          cmdData = { cmd: "get_version" }
          break
        case 'upload_app_log':
          cmdData = { cmd: "upload_app_log" }
          break
        case 'apk_restart':
          cmdData = { cmd: "apk_restart" }
          break
        case 'upload_screen_capture':
          cmdData = { cmd: "upload_screen_capture" }
          break
        case 'reboot':
          cmdData = { cmd: "reboot" }
          break
        case 'upload_all':
          cmdData = { cmd: "upload_all" }
          break
        case 'push_version_publish':
          cmdData = { cmd: "push_version_publish" }
          break
        case 'load_ad':
          cmdData = { cmd: "load_ad" }
          break
      }
      this.sendCommandData(cmdData, this.getCommandName(cmdType))
    },

    getCommandName(cmdType) {
      const names = {
        'get_version': '获取APK版本',
        'upload_app_log': '上传APP日志',
        'apk_restart': 'APK重启',
        'upload_screen_capture': '屏幕截图',
        'reboot': '机柜重启',
        'upload_all': 'HTTP整机上报',
        'push_version_publish': '下发升级',
        'load_ad': '更新广告'
      }
      return names[cmdType] || '命令'
    },

    sendCommandData(cmdData, commandName) {
      if (!this.imeis && !this.scanNo) {
        this.global.toast('设备信息不存在')
        return
      }

      this.showMask = true
      const params = {
        cabinetNo: this.imeis || this.scanNo,
        data: JSON.stringify(cmdData)
      }

      Device.sendCmd(params).then(res => {
        if (res.code == 0) {
          this.global.toast(`${commandName}成功`)
        } else {
          this.global.toast(res.message || `${commandName}失败`)
        }
      }).catch(error => {
        this.global.toast('网络错误，请重试')
        console.error('指令发送错误:', error)
      }).finally(() => {
        this.showMask = false
      })
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
      font-weight: 500;
    }

    // 常用命令样式
    .command-container {
      padding: 10px;

      // 自定义命令输入框样式 - 置顶
      .command-input-section {
        margin-bottom: 20px;
        border: 1px solid #e0e0e0;
        border-radius: 8px;
        padding: 15px;
        background: #f9f9f9;

        h3 {
          margin: 0 0 15px 0;
          font-size: 16px;
          color: #333;
          border-bottom: 1px solid #e0e0e0;
          padding-bottom: 8px;
        }

        .command-textarea {
          margin-bottom: 10px;

          ::v-deep .van-field__body {
            textarea {
              font-family: monospace;
              font-size: 14px;
            }
          }
        }

        .send-command-btn {
          margin-bottom: 5px;
        }

        .error-tip {
          color: #ee0a24;
          font-size: 12px;
          text-align: center;
        }
      }

      .command-section {
        margin-bottom: 20px;
        border: 1px solid #e0e0e0;
        border-radius: 8px;
        padding: 15px;
        background: #f9f9f9;

        h3 {
          margin: 0 0 15px 0;
          font-size: 16px;
          color: #333;
          border-bottom: 1px solid #e0e0e0;
          padding-bottom: 8px;
        }

        .command-buttons {
          .command-button {
            margin-bottom: 10px;
          }

          .command-button:last-child {
            margin-bottom: 0;
          }

          .van-field {
            margin-bottom: 10px;
          }
        }
      }
    }
  }

  .wrapper {
    display: flex;
    align-items: center;
    justify-content: center;
    height: 100%;
  }
}

/* 弹窗样式调整 */
.van-dialog {
  .van-field {
    margin: 15px;
  }
}
</style>