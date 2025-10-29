import axios from 'axios'
import Vue from 'vue'

// 免登录校验uri
const ignoreURI = [
  '/manage/login',
  '/manage/common'
]

const instance = axios.create({
  // 基础请求URL
  baseURL: process.env.BASE_URL,
  // 响应超时的时间
  timeout: 30000,
  // 跨域
  withCredentials: true
})

// 请求拦截
instance.interceptors.request.use(
  config => {
    config.headers['brezze-language']= 'ZH'
    return config
  },
  error => {
    // do something with request error
    // console.log(error)
    return Promise.reject(error)
  }
)

// 响应拦截
instance.interceptors.response.use(
  res => {
    // console.log(res)
      return res.data
  },
  error => {
    // console.log('响应拦截错误', error)
    const { response } = error
    // console.log(response)
    if (response) {
      // 请求已发出，但是不在2xx的范围
      errorHandle(response.status, response.statusText)
      return Promise.reject(response)
    } else {
      return Promise.reject(error)
    }
  }
)

/**
 * get请求
 * @param uri
 * @param params
 * @param headers 请求头参数
 * @returns {Promise<unknown>}
 */
const get = (uri, params, headers) => {
  let paramsData
  if (params) {
    // 拷贝对象
    paramsData = JSON.parse(JSON.stringify(params))
    // console.log(paramsData)
    if (paramsData) {
      // 处理时间范围参数
      if (paramsData.rangeTime && paramsData.rangeTime.length === 2) {
        paramsData.rangeTime = paramsData.rangeTime.join(',')
      }
    }
  }
  return new Promise((resolve, reject) => {
    instance.get(uri, {
      headers: headers,
      params: paramsData
    })
      .then(res => {
        resolve(res)
      })
      .catch(res => {
        reject(res)
      })
  })
}

/**
 * post请求
 * @param uri     请求路径
 * @param params  请求参数
 * @param headers 请求头参数
 * @returns {Promise<unknown>}
 */
const post = (uri, params, headers) => {
  if (isObject(headers)) {
    headers['Content-Type'] = 'application/json; charset=UTF-8'
  } else {
    headers = { 'Content-Type': 'application/json; charset=UTF-8' }
  }
  return new Promise((resolve, reject) => {
    instance.post(uri, params, {
      headers: headers
    })
      .then(res => {
        resolve(res)
      })
      .catch(res => {
        reject(res)
      })
  })
}

/**
 * patch请求
 * @param uri     请求路径
 * @param params  请求参数
 * @param headers 请求头参数
 * @returns {Promise<unknown>}
 */
const patch = (uri, params, headers) => {
  if (isObject(headers)) {
    headers['Content-Type'] = 'application/json; charset=UTF-8'
  } else {
    headers = { 'Content-Type': 'application/json; charset=UTF-8' }
  }
  return new Promise((resolve, reject) => {
    instance.patch(uri, params, {
      headers: headers
    })
      .then(res => {
        resolve(res)
      })
      .catch(res => {
        reject(res)
      })
  })
}

/**
 * put请求
 * @param uri     请求路径
 * @param params  请求参数
 * @param headers 请求头参数
 * @returns {Promise<unknown>}
 */
const put = (uri, params, headers) => {
  if (isObject(headers)) {
    headers['Content-Type'] = 'application/json; charset=UTF-8'
  } else {
    headers = { 'Content-Type': 'application/json; charset=UTF-8' }
  }
  headers['Content-Type'] = 'application/json; charset=UTF-8'
  return new Promise((resolve, reject) => {
    instance.put(uri, params, {
      headers: headers
    })
      .then(res => {
        resolve(res)
      })
      .catch(res => {
        reject(res)
      })
  })
}

/**
 * delete请求
 * @param uri     请求路径
 * @param params  请求参数
 * @param headers 请求头参数
 * @returns {Promise<unknown>}
 */
const del = (uri, params, headers) => {
  return new Promise((resolve, reject) => {
    instance.delete(uri, {
      headers: headers,
      params: params
    })
      .then(res => {
        resolve(res)
      })
      .catch(res => {
        reject(res)
      })
  })
}

/**
 * 上传文件
 * @param params  请求参数
 * @param headers 请求头参数
 * @returns {Promise<unknown>}
 */
const uploadFile = (params, headers) => {
  // console.log('上传文件====>', params, headers)
  if (isObject(headers)) {
    headers['Content-Type'] = 'multipart/form-data;'
  } else {
    headers = { 'Content-Type': 'multipart/form-data;' }
  }
  let formData
  if (params instanceof FormData) {
    formData = params
  } else {
    formData = new FormData()
    formData.append('files', params.file)
  }
  return new Promise((resolve, reject) => {
    instance.request({
      url: '/manage/common/files/upload',
      method: 'post',
      headers: headers,
      data: formData
    })
      .then(res => {
        resolve(res)
      })
      .catch(res => {
        reject(res)
      })
  })
}

/**
 * 请求失败后的错误统一处理
 * @param {Number} status 请求失败的状态码
 */
const errorHandle = (status, message) => {
  // 状态码判断
  switch (status) {
    // 401: 未登录状态，跳转登录页
    case 401:
      // Vue.prototype.$tip({
      //   type: 'error',
      //   // message: message
      //   message: 'Login has expired, please login again'
      // })
      setTimeout(() => {
        Vue.prototype.$store.removeUserInfo()
        Vue.prototype.$util.toLogin()
      }, 2000)
      break
    // 403 token过期, 清除用户信息,并跳转登录页
    case 403:
      // Vue.prototype.$tip({
      //   type: 'error',
      //   // message: message
      //   message: 'Login expired, please log in again'
      // })
      // setTimeout(() => {
      //   Vue.prototype.$store.removeUserInfo()
      //   Vue.prototype.$util.toLogin()
      // }, 2000)
      break
    // 404请求不存在
    case 404:
      // Vue.prototype.$tip({
      //   type: 'error',
      //   message: '404 request does not exist'
      // })
      break
    default:
      // Vue.prototype.$tip({
      //   type: 'error',
      //   message: message
      // })
      console.log(message)
  }
}

// 是否存在忽略URI
function isIgnoreURI (uri) {
  for (let i = 0; i < ignoreURI.length; i++) {
    if (uri.startsWith(ignoreURI[i])) {
      return true
    }
  }
  return false
}

/**
 * 是否是object对象
 * @param val
 * @returns {boolean}
 */
function isObject (val) {
  return val && Object.prototype.toString.call(val) === '[object Object]'
}

// 获取菜单栏标题
function getMenuTitle () {
  let title = null
  // const userInfo = Vue.prototype.$store.getUserInfo()
  // const chooseMenu = Vue.prototype.$store.getChooseMenu()
  // userInfo.menuList.forEach(item => {
  //   item.child.forEach(childItem => {
  //     if (childItem.defaultUrl === chooseMenu.defaultUrl) {
  //       title = childItem.title
  //     }
  //   })
  // })

  return title
}

export {
  get,
  post,
  put,
  del,
  uploadFile
}
