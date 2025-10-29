import * as api from './api'

export default {
  // 获取设备详情
  getDevice: (params) => {
    return api.get('/communication/ybt/check-all',params)
  },
  //SN号弹出
  popupSN(params){
    return api.post('/communication/ybt/test-util/popup_sn', params)
  },
  //SN号弹出
  popupHole(params){
    return api.post('/communication/ybt/test-util/openLock', params)
  },
  //统一设备指令接口
  sendCmd(params){
    return api.post('/communication/ybt/send', params)
  },
}
