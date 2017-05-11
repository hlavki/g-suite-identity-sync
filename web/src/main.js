// The Vue build version to load with the `import` command
// (runtime-only or standalone) has been set in webpack.base.conf with an alias.
import Vue from 'vue'
import App from './App'
import router from './router'
import VueMaterial from 'vue-material'
import 'vue-material/dist/vue-material.css'
import AuthPlugin from './plugins/auth'
import axios from 'axios'
import VeeValidate from 'vee-validate'

Vue.config.productionTip = false
Vue.use(VueMaterial)
Vue.use(VeeValidate)

var isProduction = false
var apiPrefix
var setupAPI = function () {
  switch (process.env.NODE_ENV) {
    case 'production':
      apiPrefix = '/cxf'
      isProduction = true
      break
    default:
      apiPrefix = '/cxf'
  }
}
setupAPI()

Vue.use(AuthPlugin, {
  router: router,
  http: axios,
  isProduction: isProduction,
  apiPrefix: apiPrefix
})
Vue.material.registerTheme('default', {
  primary: 'green'
})
Vue.prototype.$http = axios
Vue.prototype.$apiPrefix = apiPrefix
Vue.prototype.$isProduction = isProduction

/* eslint-disable no-new */
new Vue({
  el: '#app',
  router,
  template: '<App/>',
  components: { App }
})

