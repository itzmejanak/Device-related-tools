import Vue from 'vue'
import {Toast} from 'vant'

const global = {
  install(Vue) {
    Vue.prototype.global = {
      // 获取终端的相关信息
      terminal: {
        // 辨别移动终端类型
        platform: function () {
          const u = navigator.userAgent, app = navigator.appVersion;
          return {
            //IE内核
            windows: u.indexOf('Windows') > -1,
            //opera内核
            presto: u.indexOf('Presto') > -1,
            //苹果、谷歌内核
            webKit: u.indexOf('AppleWebKit') > -1,
            //火狐内核
            gecko: u.indexOf('Gecko') > -1 && u.indexOf('KHTML') == -1,
            //是否为移动终端
            mobile: !!u.match(/AppleWebKit.*Mobile.*/) || !!u.match(/AppleWebKit/),
            //ios终端
            ios: !!u.match(/\(i[^;]+;( U;)? CPU.+Mac OS X/),
            //android终端
            android: u.indexOf('Android') > -1 || u.indexOf('Linux') > -1,
            //是否iPad
            iPad: u.indexOf('iPad') > -1,
            //是否为iPhone或者QQHD浏览器
            iPhone: u.indexOf('iPhone') > -1,
            //是否为mac系统
            Mac: u.indexOf('Macintosh') > -1,
            //是否web应该程序，没有头部与底部
            webApp: u.indexOf('Safari') == -1
          }
        }(),
        // 辨别移动终端的语言：zh-cn、en-us、ko-kr、ja-jp...
        language: (navigator.browserLanguage || navigator.language).toLowerCase(),
        // 手机品牌判断对象
        brand: function () {
          const u = navigator.userAgent.toLowerCase()
          console.log(u)
          return {
            isIphone: u.match(/iphone/i) == "iphone",
            isHuawei: u.match(/huawei/i) == "huawei",
            isHonor: u.match(/honor/i) == "honor",
            isOppo: u.match(/oppo/i) == "oppo",
            isOppoR15: u.match(/pacm00/i) == "pacm00",
            isVivo: u.match(/vivo/i) == "vivo",
            isXiaomi: u.match(/mi\s/i) == "mi ",
            isXiaomi2s: u.match(/mix\s/i) == "mix ",
            isRedmi: u.match(/redmi/i) == "redmi",
            isSamsung: u.match(/sm-/i) == "sm-",
          }
        }()
      },

      // Toast提示
      toast: function (message, duration = 3000) {
        Toast({
          message, duration
        })
      },

      // 获取URL查询字符串
      getSearchString: function (key, url) {
        // 获取URL查询字符串
        let str = url ? url.substring(url.indexOf('?')) : location.search;
        console.log(`query:${str}`)
        if (!str) {
          return ''
        }
        str = str.substring(1, str.length);
        // 以&分隔字符串，获得类似name=xiaoli这样的元素数组
        const arr = str.split("&");
        const obj = new Object();
        // 将每一个数组元素以=分隔并赋给obj对象
        for (let i = 0; i < arr.length; i++) {
          const tmp_arr = arr[i].split("=");
          obj[decodeURIComponent(tmp_arr[0])] = decodeURIComponent(tmp_arr[1]);
        }
        return obj[key] ? obj[key] : '';
      },

      /**
       * 获取页面元素的位置的方法.这个方法最终返回的是一个矩形对象,包括四个属性:left top right bottom
       {
          top: '元素顶部相对于视口顶部的距离',
          bottom: '元素底部相对于视口顶部的距离',
          left: '元素左边相对于视口左边的距离',
          right: '元素右边相对于视口左边的距离',
          height: '元素高度',
          width: '元素宽度'
       }
       ie9以下浏览器只支持 getBoundingClientRect 方法的 top 、bottom、right、left属性；
       ie9 和其它浏览器支持 getBoundingClientRect 方法 有6个属性 top 、bottom、right、left、width和height
       * @param el
       * @returns {{top: number, left: number, bottom: number, width: (number|number), right: number, height: (number|number)}}
       */
      getElBound: function (el) {
        if (el) {
          const {top, bottom, left, right, height, width} = el.getBoundingClientRect()
          return {
            top,
            bottom,
            left,
            right,
            height: height || bottom - top,
            width: width || right - left
          }
        }
      },

      /**
       * 判断元素是否进入视口可视区域
       * @param el
       * @returns {boolean}
       */
      isIn: function (el) {
        const bound = getElBound(el);
        return bound.top <= window.innerHeight;
      },

      /**
       * 768尺寸
       * @returns {boolean}
       */
      is768: function () {
        return window.matchMedia("(max-width: 768px)").matches
      },

      /**
       * 1440尺寸
       * @returns {boolean}
       */
      is1440: function () {
        return window.matchMedia("(max-width: 1440px)").matches
      },

      /**
       * 1920尺寸
       * @returns {boolean}
       */
      is1920: function () {
        return window.matchMedia("(max-width: 1920px)").matches
      },

      /**
       * 获取 cookie 的某个值
       */
      getCookie: function(name) {
        let cookieValue = null;
        if (document.cookie && document.cookie !== '') {
            let cookies = document.cookie.split(';');
            for (let i = 0; i < cookies.length; i++) {
                let cookie = cookies[i].trim();
                // 判断这个cookie的参数名是不是我们想要的
                if (cookie.substring(0, name.length + 1) === (name + '=')) {
                    cookieValue = decodeURIComponent(cookie.substring(name.length + 1));
                    break;
                }
            }
        }
        return cookieValue;
      }
    }
  }
}

Vue.use(global)
