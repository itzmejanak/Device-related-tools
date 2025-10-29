import Vue from 'vue'
import VueI18n from 'vue-i18n'

Vue.use(VueI18n)

export default ({ app, store }) => {
  app.i18n = new VueI18n({
    locale: store.state.locale,
    fallbackLocale: 'RU', // 默认为俄文
    messages: {
      'ZH': require('@/assets/i18n/zh.json'),
      'EN': require('@/assets/i18n/en.json'),
      'RU': require('@/assets/i18n/ru.json')
    }
  })

  app.i18n.path = (link) => {
    // // 如果是默认语言，就省略
    // if (app.i18n.locale === app.i18n.fallbackLocale) {
    //   return `/${link}`
    // }
    // return `/${link}?lang=/${app.i18n.locale}`

    // 如果是默认语言，就省略
    if (app.i18n.locale === app.i18n.fallbackLocale) {
      return `/${link}`
    }
    return `/${app.i18n.locale}/${link}`
  }
}
