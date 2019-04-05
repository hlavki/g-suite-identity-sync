
import Vue from 'vue'
import VueI18n from 'vue-i18n'

const DEFAULT_LOCALE = 'en-US'

Vue.use(VueI18n)

const i18n = new VueI18n({
    locale: navigator.language, // set locale
    fallbackLocale: 'en-US',
    messages: {
      'en-US': require('./locales/en-US.json'),
      'sk-SK': require('./locales/sk.json'),
      'sk': require('./locales/sk.json')
    }
})

export default i18n
