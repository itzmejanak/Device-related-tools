// import router from '@/router'

/*
* 格式化金额
* number：要格式化的数字
* decimals：保留几位小数
* decPoint：小数点符号
* thousandsSep：千分位符号
* roundTag:舍入参数 "ceil"向上取,"floor"向下取,"round" 四舍五入
* */
export const formatAmount = (number, decimals = 2, decPoint = '.', thousandsSep = ',', roundTag = 'round') => {
  number = (number + '').replace(/[^0-9+-Ee.]/g, '')
  roundTag = roundTag || 'ceil' // "ceil","floor","round"
  var n = !isFinite(+number) ? 0 : +number
  var prec = !isFinite(+decimals) ? 0 : Math.abs(decimals)
  var s = ''
  var toFixedFix = function (n, prec) {
    var k = Math.pow(10, prec)
    console.log()

    return '' + parseFloat(Math[roundTag](parseFloat((n * k).toFixed(prec * 2))).toFixed(prec * 2)) / k
  }
  s = (prec ? toFixedFix(n, prec) : '' + Math.round(n)).split('.')
  var re = /(-?\d+)(\d{3})/
  while (re.test(s[0])) {
    s[0] = s[0].replace(re, '$1' + thousandsSep + '$2')
  }

  if ((s[1] || '').length < prec) {
    s[1] = s[1] || ''
    s[1] += new Array(prec - s[1].length + 1).join('0')
  }
  return s.join(decPoint)
}

// console.log(formatAmountOrigin(2, 2, '.', ','))// "2.00"
// console.log(formatAmountOrigin(3.7, 2, '.', ','))// "3.70"
// console.log(formatAmountOrigin(3, 0, '.', ',')) // "3"
// console.log(formatAmountOrigin(9.0312, 2, '.', ','))// "9.03"
// console.log(formatAmountOrigin(9.00, 2, '.', ','))// "9.00"
// console.log(formatAmountOrigin(39.715001, 2, '.', ',', 'floor')) // "39.71"
// console.log(formatAmountOrigin(9.7, 2, '.', ','))// "9.70"
// console.log(formatAmountOrigin(39.7, 2, '.', ','))// "39.70"
// console.log(formatAmountOrigin(9.70001, 2, '.', ','))// "9.71"
// console.log(formatAmountOrigin(39.70001, 2, '.', ','))// "39.71"
// console.log(formatAmountOrigin(9996.03, 2, '.', ','))// "9996.03"
// console.log(formatAmountOrigin(1.797, 3, '.', ',', 'floor'))// "1.797"

/**
 * 跳转登录页
 * 携带当前页面路由，以期在登录页面完成登录后返回当前页面
 */
export const toLogin = (fullPath = true) => {
  if (fullPath) {
    console.log(`toLogin... ====> fullPath: ${router.currentRoute.fullPath}, isLoginPath: ${router.currentRoute.fullPath.startsWith('/login')}`)
  }
  router.replace({
    path: '/login',
    query: {
      redirect: fullPath ? (router.currentRoute.fullPath.startsWith('/login') ? '' : router.currentRoute.fullPath) : ''
    }
  }).then(r => {
    console.log(r)
  })
}

/**
 * 获取查询字符串
 * @param key
 * @returns {*}
 */
export const getQueryString = (key) => {
  // 获取URL中?之后的字符
  var str = location.search
  console.log(str)
  str = str.substring(1, str.length)

  // 以&分隔字符串，获得类似name=xiaoli这样的元素数组
  var arr = str.split('&')
  // eslint-disable-next-line no-new-object
  var obj = new Object()

  // 将每一个数组元素以=分隔并赋给obj对象
  for (var i = 0; i < arr.length; i++) {
    // eslint-disable-next-line camelcase
    var tmp_arr = arr[i].split('=')
    obj[decodeURIComponent(tmp_arr[0])] = decodeURIComponent(tmp_arr[1])
  }
  return obj[key]
}

/**
 * 图片格式化
 * @param prefix
 * @param val
 * @param defaultVal
 * @returns {string|*}
 */
export const imgFormat = (prefix, val, defaultVal) => {
  // console.log(`val: ${val}, defaultVal: ${defaultVal}`)
  if (val && val !== '') {
    if (val.startsWith('http')) {
      return val
    } else {
      return prefix + val
    }
  } else {
    return defaultVal || ''
  }
}

/**
 * 给对象赋值
 * @param target
 * @param source
 */
export const assign = (target, source) => {
  Object.assign(target, source)
}

/**
 * 给对象赋值(仅赋值目标对象有的属性)
 * @param target
 * @param source
 * @param number2str 数字类型转字符串类型
 * @param zero2empty
 */
export const assignOnlyTarget = (target, source, number2str, zero2empty) => {
  if (target && source) {
    const targetKeys = Object.keys(target)
    const sourceKeys = Object.keys(source)
    targetKeys.forEach(t => {
      sourceKeys.forEach(s => {
        if (s === t) {
          if (number2str) {
            target[t] = (typeof source[s] === 'number' && !isNaN(source[s])) ? String(zero2empty ? (source[s] === 0 ? '' : source[s]) : source[s]) : source[s]
          } else {
            target[t] = source[s]
          }
        }
      })
    })
  }
}

/**
 * 下载txt文件
 * @param filename
 * @param text
 */
export const downloadTxt = (filename, text) => {
  var pom = document.createElement('a')
  pom.setAttribute('href', 'data:text/plain;charset=utf-8,' + encodeURIComponent(text))
  pom.setAttribute('download', filename)
  if (document.createEvent) {
    var event = document.createEvent('MouseEvents')
    event.initEvent('click', true, true)
    pom.dispatchEvent(event)
  } else {
    pom.click()
  }
}

/**
 * url转base64
 * 原理： 利用canvas.toDataURL的API转化成base64
 * @param url
 * @returns {Promise<unknown>}
 */
export const urlToBase64 = (url) => {
  return new Promise((resolve, reject) => {
    const image = new Image()
    image.onload = function () {
      const canvas = document.createElement('canvas')
      canvas.width = this.naturalWidth
      canvas.height = this.naturalHeight
      // 将图片插入画布并开始绘制
      canvas.getContext('2d').drawImage(image, 0, 0)
      // result
      const result = canvas.toDataURL('image/png')
      resolve(result)
    }
    // CORS 策略，会存在跨域问题https://stackoverflow.com/questions/20424279/canvas-todataurl-securityerror
    image.setAttribute('crossOrigin', 'Anonymous')
    image.src = url
    // 图片加载失败的错误处理
    image.onerror = () => {
      reject(new Error('图片流异常'))
    }
  })
}

// 图片转成blob
export const dataURItoBlob = (dataURI) => {
  var byteString = atob(dataURI.split(',')[1]);
  var mimeString = dataURI.split(',')[0].split(':')[1].split(';')[0];
  var ab = new ArrayBuffer(byteString.length);
  var ia = new Uint8Array(ab);
  for (var i = 0; i < byteString.length; i++) {
    ia[i] = byteString.charCodeAt(i);
  }
  return new Blob([ab], {type: mimeString});
}

/**
 * base64转blob
 * 原理：利用URL.createObjectURL为blob对象创建临时的URL
 * @param b64data
 * @param contentType
 * @param sliceSize
 * @returns {Promise<unknown>}
 */
export const base64ToBlob = ({
  b64data = '',
  contentType = '',
  sliceSize = 512
} = {}) => {
  return new Promise((resolve, reject) => {
    // 使用 atob() 方法将数据解码
    const byteCharacters = atob(b64data)
    const byteArrays = []
    for (let offset = 0; offset < byteCharacters.length; offset += sliceSize) {
      const slice = byteCharacters.slice(offset, offset + sliceSize)
      const byteNumbers = []
      for (let i = 0; i < slice.length; i++) {
        byteNumbers.push(slice.charCodeAt(i))
      }
      // 8 位无符号整数值的类型化数组。内容将初始化为 0。
      // 如果无法分配请求数目的字节，则将引发异常。
      byteArrays.push(new Uint8Array(byteNumbers))
    }
    let result = new Blob(byteArrays, {
      type: contentType
    })
    result = Object.assign(result, {
      // jartto: 这里一定要处理一下 URL.createObjectURL
      preview: URL.createObjectURL(result),
      name: 'example.png'
    })
    resolve(result)
  })
}

/**
 * blob转base64
 * 原理：利用fileReader的readAsDataURL，将blob转为base64
 * @param blob
 * @returns {Promise<unknown>}
 */
export const blobToBase64 = (blob) => {
  return new Promise((resolve, reject) => {
    const fileReader = new FileReader()
    fileReader.onload = (e) => {
      resolve(e.target.result)
    }
    // readAsDataURL
    fileReader.readAsDataURL(blob)
    fileReader.onerror = () => {
      reject(new Error('文件流异常'))
    }
  })
}

/**
 * url转blob
 * @param url
 * @returns {Promise<minimist.Opts.unknown>}
 */
export const urlToBlob = (url) => {
  return base64ToBlob(urlToBase64(url))
}

/**
 * 复制文本到粘贴板
 * @param text
 */
export const copyText = (text) => {
  // js创建一个input输入框
  const input = document.createElement('input')
  // 将需要复制的文本赋值到创建的input输入框中
  input.value = text.toString()
  // 将输入框暂时创建到实例里面
  document.body.appendChild(input)
  // 选中输入框中的内容
  input.select()
  // 执行复制操作
  if (document.execCommand('Copy')) {
    console.log(`copy success! val: ${text}`)
  } else {
    console.log('copy failure!')
  }
  // 最后删除实例中临时创建的input输入框，完成复制操作
  document.body.removeChild(input)
}

/**
 * 获取当前月份的第一天
 * @returns {string}
 */
export const getCurrentMonthFirst = () => {
  const now = new Date()
  const nowMonth = now.getMonth()
  const nowYear = now.getFullYear()
  // 本月的开始时间
  return new Date(nowYear, nowMonth, 1)
}

/**
 * 获取当前月份的最后一天
 * @returns {string}
 */
export const getCurrentMonthLast = () => {
  const now = new Date()
  const nowMonth = now.getMonth()
  const nowYear = now.getFullYear()
  // 本月的结束时间
  return new Date(nowYear, nowMonth + 1, 0)
}

/**
 * 比较时间1字符串大于或等于时间2字符串
 *
 * 22:00, 10:00 > true
 * 22:00, 22:00 = true
 * 10:00, 22:00 < false
 * @param time1
 * @param time2
 */
export const timeStrGTE = (time1, time2) => {
  let res = false
  if (time1 !== null && time2 !== null) {
    const s1 = time1.split(':')
    const s2 = time2.split(':')
    if (Number(s1[0]) > Number(s2[0])) {
      res = true
    } else if (Number(s1[0]) === Number(s2[0])) {
      res = Number(s1[1]) >= Number(s2[1])
    }
  }

  return res
}

/**
 * 比较时间1字符串大于2字符串
 *
 * 22:00, 10:00 > true
 * 22:00, 22:00 = true
 * 10:00, 22:00 < false
 * @param time1
 * @param time2
 */
export const timeStrGT = (time1, time2) => {
  let res = false
  if (time1 !== null && time2 !== null) {
    const s1 = time1.split(':')
    const s2 = time2.split(':')
    if (Number(s1[0]) > Number(s2[0])) {
      res = true
    } else if (Number(s1[0]) === Number(s2[0])) {
      res = Number(s1[1]) > Number(s2[1])
    }
  }

  return res
}

/**
 * 比较时间1字符串等于时间2字符串
 *
 * 22:00, 10:00 > true
 * 22:00, 22:00 = true
 * 10:00, 22:00 < false
 * @param time1
 * @param time2
 */
export const timeStrEQ = (time1, time2) => {
  let res = false
  if (time1 !== null && time2 !== null) {
    const s1 = time1.split(':')
    const s2 = time2.split(':')
    res = Number(s1[0]) === Number(s2[0]) && Number(s1[1]) === Number(s2[1])
  }

  return res
}

/**
 * 比较时间1字符串小于于或等于时间2字符串
 *
 * 22:00, 10:00 > true
 * 22:00, 22:00 = true
 * 10:00, 22:00 < false
 * @param time1
 * @param time2
 */
export const timeStrLTE = (time1, time2) => {
  let res = false
  if (time1 !== null && time2 !== null) {
    const s1 = time1.split(':')
    const s2 = time2.split(':')
    if (Number(s1[0]) < Number(s2[0])) {
      res = true
    } else if (Number(s1[0]) === Number(s2[0])) {
      res = Number(s1[1]) <= Number(s2[1])
    }
  }

  return res
}

/**
 * 比较时间1字符串小于于时间2字符串
 *
 * 22:00, 10:00 > true
 * 22:00, 22:00 = true
 * 10:00, 22:00 < false
 * @param time1
 * @param time2
 */
export const timeStrLT = (time1, time2) => {
  let res = false
  if (time1 !== null && time2 !== null) {
    const s1 = time1.split(':')
    const s2 = time2.split(':')
    if (Number(s1[0]) < Number(s2[0])) {
      res = true
    } else if (Number(s1[0]) === Number(s2[0])) {
      res = Number(s1[1]) < Number(s2[1])
    }
  }

  return res
}

/**
 * 函数防抖 (只执行最后一次点击)
 * @param fn
 * @param delay
 * @returns {Function}
 * @constructor
 */
 export const debounce = (fn, delay = 1000) => {
  let timer;
  return function () {
      let args = arguments;
      if(timer){
          clearTimeout(timer);
      }
      timer = setTimeout(() => {
          timer = null;
          fn.apply(this, args);
      }, delay);
  }
};
