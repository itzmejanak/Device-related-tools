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
      // 方法3: 直接判断是否为15或18位纯数字
      else {
        const isNumber = /^\d+$/.test(result)
        const validLength = result.length === 15 || result.length === 18
        if (isNumber && validLength) {
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
          msg = '错误：此浏览器不支持 Stream API'
        } else if (error.name === 'InsecureContextError') {
          msg = '错误：仅在安全环境中才允许访问相机'
        } else {
          msg = `ERROR: ${'相机异常'} (${error.name})`
        }
        this.global.toast(msg)
      }
    }
  }
}
</script>

<style lang="scss" scoped>
.container {
  width: 100%;
  position: relative;
  // background: #1b232e !important;
  z-index: 999999;

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
  .scanIcon {
    margin: 10.5rem auto 0;
    width: 29.4rem;
    height: 29.4rem;
    opacity: 1;
    position: relative;
    // background: url("assets/img/scan/Vector@2x (3).png") no-repeat center;
    // background-size: cover;
    // border: 0.1rem dashed #fff;
  }
  // .qr-scanner {
    // background: url('assets/img/scan/Vector@2x (3).png') no-repeat center;
    // background-size: cover;
    // border: 0.1rem dashed #fff;
    // background-image: linear-gradient(
    //     0deg,
    //     transparent 24%,
    //     rgba(32, 255, 77, 0.1) 25%,
    //     rgba(32, 255, 77, 0.1) 26%,
    //     transparent 27%,
    //     transparent 74%,
    //     rgba(32, 255, 77, 0.1) 75%,
    //     rgba(32, 255, 77, 0.1) 76%,
    //     transparent 77%,
    //     transparent
    //   ),
    //   linear-gradient(
    //     90deg,
    //     transparent 24%,
    //     rgba(32, 255, 77, 0.1) 25%,
    //     rgba(32, 255, 77, 0.1) 26%,
    //     transparent 27%,
    //     transparent 74%,
    //     rgba(32, 255, 77, 0.1) 75%,
    //     rgba(32, 255, 77, 0.1) 76%,
    //     transparent 77%,
    //     transparent
    //   );
    // position: absolute;
    // left: 50%;
    // top: 50%;
    // transform: translate(-50%, -50%);
    // width: 213px;
    // height: 213px;
    // position: absolute;
    // background-color: #fff;
  // }

  .qr-scanner .box {
    width: 29.4rem;
    height: 29.4rem;
    position: absolute;
    left: 50%;
    // top: 30%;
    margin-top: 50%;
    transform: translate(-50%, -50%);
    background-color: transparent !important;
    overflow: hidden;
    border: 0.1rem solid rgba(0, 255, 51, 0.2);
  }

  .qr-scanner .line {
    height: calc(100% - 2px);
    width: 100%;
    background: linear-gradient(180deg, rgba(0, 255, 51, 0) 43%, #1aad19 211%);
    border-bottom: 3px solid #1aad19;
    transform: translateY(-100%);
    animation: radar-beam 2s infinite alternate;
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

    border: 0.2rem solid transparent;
  }

  .qr-scanner .box:after,
  .qr-scanner .box:before {
    top: 0;
    border-top-color: #1aad19;
  }

  .qr-scanner .angle:after,
  .qr-scanner .angle:before {
    bottom: 0;
    border-bottom-color: #1aad19;
  }

  .qr-scanner .box:before,
  .qr-scanner .angle:before {
    left: 0;
    border-left-color: #1aad19;
  }

  .qr-scanner .box:after,
  .qr-scanner .angle:after {
    right: 0;
    border-right-color: #1aad19;
  }

  @keyframes radar-beam {
    0% {
      transform: translateY(-100%);
    }

    100% {
      transform: translateY(0);
    }
  }
}
</style>