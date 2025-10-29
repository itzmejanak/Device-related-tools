
// export const state = () => ({
//   locales: [
//     {code: 'ZH', label: '简体中文'},
//     {code: 'EN', label: 'English'},
//     {code: 'RU', label: 'русский'},
//   ],
//   locale: 'EN',
//   isOpen: true,
//   orderNum: '',
// })
export const state = {
    locales: [
      {code: 'ZH', label: '简体中文'},
      {code: 'EN', label: 'English'},
      {code: 'RU', label: 'русский'},
    ],
    locale: 'ZH',
  }
  
  export const mutations = {
    SET_LANG(state, locale) {
      if (state.locales.find(item => item.code === locale)) {
        state.locale = locale
      }
    },
   
  }
  