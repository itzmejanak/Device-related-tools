const envConfig = require('dotenv').config({
  path: `.env${process.env.ENV ? `.${process.env.ENV}` : ''}`
})

export default {
  router: {
    // 使用 'hash' 主要是为了适配以相对路径打开的静态站点， 必须使用 'hash' 否则路由跳转不生效
    mode: 'hash',
    // 使用 './' 主要是为了适配以相对路径打开的静态站点
    base: process.env.NODE_ENV === 'production' ? './' : '/',

    //路由中间件
    middleware: ['i18n']
  },
  // Target: https://go.nuxtjs.dev/config-target
  target: 'static',
  // Global page headers: https://go.nuxtjs.dev/config-head
  head: {
    title: '机柜测试工具',
    htmlAttrs: {
      lang: 'en'
    },
    meta: [
      {charset: 'utf-8'},
      {name: 'viewport', content: 'width=device-width, initial-scale=1.0, maximum-scale=1.0, user-scalable=no'},
      {hid: 'description', name: 'description', content: ''}
    ],
    link: [
      {rel: 'icon', type: 'image/x-icon', href: 'sebei.png'}
    ],
    script: []
  },

  // Global CSS: https://go.nuxtjs.dev/config-css
  css: [
    {src: '@/assets/css/common.scss'},
    {src: '@/assets/css/animation.scss'},
    {src: 'element-ui/lib/theme-chalk/index.css'},
    {src: 'node_modules/swiper/css/swiper.css'},
    {src: 'vant/lib/index.less'},
  ],

  // Plugins to run before rendering page: https://go.nuxtjs.dev/config-plugins
  plugins: [
    {src: '@/plugins/common', ssr: false},
    {src: '@/plugins/i18n.js', ssr: true},
    {src: '@/plugins/element-ui', ssr: false},
    {src: '@/plugins/vue-awesome-swiper', ssr: false},
    {src: '@/plugins/vant-ui', ssr: false},
    {src: '@/plugins/vue-qrcode-reader', ssr: false},
  ],

  // Auto import components: https://go.nuxtjs.dev/config-components
  components: true,

  // Modules for dev and build (recommended): https://go.nuxtjs.dev/config-modules
  buildModules: [],

  // Modules: https://go.nuxtjs.dev/config-modules
  modules: [],

  // Build Configuration: https://go.nuxtjs.dev/config-build
  build: {
    transpile: [
      /^element-ui/,
    ],
    vendor: ['vue-i18n'],
    /*
    ** You can extend webpack config here
    */
    extend(config, ctx) {

    }
  },
  // 环境配置
  env: {
    ...process.env,
    ...envConfig.parsed
  },

  // 静态站点打包配置: https://www.nuxtjs.cn/api/configuration-generate
  // 当运行 nuxt generate 命令或在编码中调用 nuxt.generate() 时，Nuxt.js 会使用 generate 属性的配置ss
  generate: {
    dir: 'dist',
  },


}
