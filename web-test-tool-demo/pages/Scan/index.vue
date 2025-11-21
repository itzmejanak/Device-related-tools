<template>
  <div class="container">
    <div class="user_head">
      <img
        src="@/assets/img/publicImg/ic_more@2x.png"
        alt=""
        @click="goToInputRack()"
      />
      <div class="title" @click="goToInputRack()">返回</div>
    </div>
    <div class="scanIcon">
      <qrcode-stream @decode="onDecode" @init="onInit">
        <div>
          <div class="qr-scanner">
            <div class="box">
              <div class="line"></div>
              <div class="angle"></div>
            </div>
          </div>
        </div>
      </qrcode-stream>
    </div>
  </div>
</template>

<script>
import { QrcodeStream } from 'vue-qrcode-reader'
export default {
  components: {
    QrcodeStream
  },
  data () {
    return {}
  },
  mounted () {},
  methods: {
    // 返回
    goToInputRack () {
      console.log(77);
      this.$router.push({
        name: 'Start'
      })
    },
    // 解码
    onDecode (result) {
      console.log('扫描结果:', result)
      let sno = ''

      // 方法1: 从URL参数中获取sno
      if (result.includes('=')) {
        sno = result.split('=').pop()
      }
      // 方法2: 从URL路径中获取最后一部分
      else if (result.includes('/')) {
        sno = result.split('/').pop()
      }
      // 方法3: 接受任何8-18位字符（支持字母数字组合，如PB10000001）
      else {
        if (result.length >= 8 && result.length <= 18) {
          sno = result
        }
      }

      // 清理可能的额外参数（如?号后的内容）
      if (sno.includes('?')) {
        sno = sno.split('?')[0]
      }

      console.log('提取的sno:', sno)

      if (sno) {
        this.$router.push({
          name: 'Setting',
          query: {
            sno: sno
          }
        })
      } else {
        this.global.toast('获取失败，请重新扫码')
        this.goToInputRack()
      }
    },
    async onInit (promise) {
      try {
        await promise
      } catch (error) {
        let msg = ''
        if (error.name === 'NotAllowedError') {
          msg = '错误：您需要授予相机访问权限'
        } else if (error.name === 'NotFoundError') {
          msg = '错误：此设备上没有摄像头'
        } else if (error.name === 'NotSupportedError') {
          msg = '错误：需要安全上下文（HTTPS、localhost）'
        } else if (error.name === 'NotReadableError') {
          msg = '错误：相机是否已在使用中？'
        } else if (error.name === 'OverconstrainedError') {
          msg = '错误：相机不可用'
        } else if (error.name === 'StreamApiNotSupportedError') {
          msg = '错误：此浏览器不支持流API'
        }
        this.global.toast(msg)
        this.goToInputRack()
      }
    }
  }
}
</script>

<style scoped>
.container {
  width: 100%;
  height: 100vh;
  background: #000;
}
.user_head {
  width: 100%;
  height: 1.2rem;
  display: flex;
  align-items: center;
  padding: 0 0.3rem;
  box-sizing: border-box;
}
.user_head img {
  width: 0.4rem;
  height: 0.4rem;
}
.user_head .title {
  font-size: 0.36rem;
  font-weight: 500;
  color: #fff;
  margin-left: 0.2rem;
}
.scanIcon {
  width: 100%;
  height: calc(100vh - 1.2rem);
  display: flex;
  justify-content: center;
  align-items: center;
}
.qr-scanner {
  width: 5rem;
  height: 5rem;
  position: relative;
}
.qr-scanner .box {
  width: 100%;
  height: 100%;
  position: relative;
  overflow: hidden;
  border: 0.1rem solid rgba(0, 255, 51, 0.2);
  background: url('~@/assets/img/scan/scan_bg.png') no-repeat center center;
  background-size: 100% 100%;
}
.qr-scanner .line {
  height: calc(100% - 2px);
  width: 100%;
  background: linear-gradient(180deg, rgba(0, 255, 51, 0) 43%, #00ff33 211%);
  border-bottom: 3px solid #00ff33;
  transform: translateY(-100%);
  animation: radar-beam 2s infinite;
  animation-timing-function: cubic-bezier(0.53, 0, 0.43, 0.99);
  animation-delay: 1.4s;
}
.qr-scanner .box:after,
.qr-scanner .box:before,
.qr-scanner .angle:after,
.qr-scanner .angle:before {
  content: '';
  display: block;
  position: absolute;
  width: 3vw;
  height: 3vw;
  border: 0.1rem solid transparent;
}
.qr-scanner .box:after,
.qr-scanner .box:before {
  top: 0;
  border-top-color: #00ff33;
}
.qr-scanner .angle:after,
.qr-scanner .angle:before {
  bottom: 0;
  border-bottom-color: #00ff33;
}
.qr-scanner .box:before,
.qr-scanner .angle:before {
  left: 0;
  border-left-color: #00ff33;
}
.qr-scanner .box:after,
.qr-scanner .angle:after {
  right: 0;
  border-right-color: #00ff33;
}
@keyframes radar-beam {
  0% {
    transform: translateY(-100%);
  }
  100% {
    transform: translateY(0);
  }
}
</style>
