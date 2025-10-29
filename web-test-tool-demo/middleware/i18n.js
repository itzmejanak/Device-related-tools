export default function ({isHMR, app, store, route, params, req, error, redirect}) {
  if (isHMR) { // ignore if called from hot module replacement
    return;
  }
  // console.log(app)
  if (req && route.name) {
    let locale
    // check if the locale cookie is set
    if (req.headers.cookie) {
      const cookies = req.headers.cookie.split('; ').map(stringCookie => stringCookie.split('='))
      const cookie = cookies.find(cookie => cookie[0] === 'locale')
      if (cookie) {
        locale = cookie[1]
      }
    }

    // set default value
    if (!locale) {
      locale = app.i18n.fallbackLocale
    }

    store.commit('SET_LANG', locale)
    app.i18n.locale = store.state.locale
  }
}
