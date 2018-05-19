// The Vue build version to load with the `import` command
// (runtime-only or standalone) has been set in webpack.base.conf with an alias.
import Vue from 'vue'
import App from './App'
import router from './router'
import VueMaterial from 'vue-material'
import 'vue-material/dist/vue-material.min.css'
import 'vue-material/dist/theme/default.css'
import AuthPlugin from './plugins/auth'
import axios from 'axios'
import VeeValidate from 'vee-validate'
import VueNotifications from 'vue-notifications'
import miniToastr from 'mini-toastr'

Vue.config.productionTip = false
Vue.use(VueMaterial)
Vue.use(VeeValidate)

// Setup API prefix (TODO: asi by sa zislo upratat rest clienta do jednej triedy)
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

// Notifications
miniToastr.init({
  style: {
    '.mini-toastr': {
      position: 'fixed',
      'z-index': 99999,
      left: '12px',
      top: '12px'
    },
    '.mini-toastr__notification': {
      cursor: 'pointer',
      padding: '12px 18px',
      margin: '0 0 6px 0',
      'background-color': '#000',
      opacity: 0.8,
      color: '#fff',
      'border-radius': '3px',
      'box-shadow': '#3c3b3b 0 0 12px',
      width: '500px',
      '&.-error': {
        'background-color': '#D5122B'
      },
      '&.-warn': {
        'background-color': '#F5AA1E'
      },
      '&.-success': {
        'background-color': '#7AC13E'
      },
      '&.-info': {
        'background-color': '#4196E1'
      },
      '&:hover': {
        opacity: 1,
        'box-shadow': '#000 0 0 12px'
      }
    },
    '.mini-toastr-notification__title': {
      'font-weight': '500'
    },
    '.mini-toastr-notification__message': {
      display: 'inline-block',
      'vertical-align': 'middle',
      width: '240px',
      padding: '0 12px'
    }
  }
})

function toast({ title, message, type, timeout, cb }) {
  return miniToastr[type](message, title, timeout, cb)
}

Vue.use(VueNotifications, {
  success: toast,
  error: toast,
  info: toast,
  warn: toast
})

VeeValidate.Validator.extend('password', {
  getMessage: field => 'Invalid password! It must contain 1 uppercase, 1 lowercase, 1 number, and one punctation.',
  validate: value => {
    var strongRegex = new RegExp('^(?=.*\\d)(?=.*[a-z])(?=.*[A-Z])(?=.*[!\\?@#\\$%\\^&\\*_\\.,:;\\[\\]\\(\\){}<>\'"\\+\\-=~`])[0-9a-zA-Z!\\?@#\\$%\\^&\\*_\\.,:;\\[\\]\\(\\){}<>\'"\\+\\-=~`]{8,}$')
    return strongRegex.test(value)
  }
})

Vue.use(AuthPlugin, {
  router: router,
  http: axios,
  isProduction: isProduction,
  apiPrefix: apiPrefix
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
