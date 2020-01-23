import Vue from 'vue'
import App from './App.vue'
import router from './router'
import AuthPlugin from './auth'
import VueMaterial from 'vue-material'
import 'vue-material/dist/vue-material.min.css'
import 'vue-material/dist/theme/default.css'
import VeeValidate from 'vee-validate'
import axios from 'axios'
import VueSweetalert2 from 'vue-sweetalert2'
import 'sweetalert2/dist/sweetalert2.min.css';

Vue.config.productionTip = false
Vue.use(VueMaterial)
Vue.use(VueSweetalert2)
Vue.use(VeeValidate)

import i18n from './lang'

VeeValidate.Validator.extend('password', {
  getMessage: field => 'Invalid password! It must contain 1 uppercase, 1 lowercase, 1 number, and one punctation.', // eslint-disable-line no-unused-vars
  validate: value => {
    var strongRegex = new RegExp('^(?=.*\\d)(?=.*[a-z])(?=.*[A-Z])(?=.*[!\\?@#\\$%\\^&\\*_\\.,:;\\[\\]\\(\\){}<>\'"\\+\\-=~`])[0-9a-zA-Z!\\?@#\\$%\\^&\\*_\\.,:;\\[\\]\\(\\){}<>\'"\\+\\-=~`]{8,}$')
    return strongRegex.test(value)
  }
})

Vue.prototype.$http = axios;
Vue.prototype.$apiPrefix = '/cxf';
Vue.prototype.$production = (process.env.NODE_ENV === 'production')

Vue.use(AuthPlugin, {
  router: router,
  http: axios,
  apiPrefix: Vue.prototype.$apiPrefix,
  production: Vue.prototype.$production
})

new Vue({
  router,
  i18n,
  render: h => h(App)
}).$mount('#app')
