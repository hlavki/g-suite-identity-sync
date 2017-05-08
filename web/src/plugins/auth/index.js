// https://forum.vuejs.org/t/vue2-authentication-authorization-examples-tutorials/1604/12
const AuthPlugin = {
  install(Vue, options) {
    Vue.prototype.$auth = new Vue({
      data: function () {
        return {
          loggedIn: false,
          userInfo: undefined
        }
      },
      created: function () {
        ApplyRouteGuard.call(this, options.router)
      },
      methods: {
        login() {
          if (!options.isProduction) {
            this.loggedIn = true
            this.userInfo = { givenName: 'Gabriel Hakan', familyName: 'Hakan', email: 'g.hakan@xit.camp', imageUri: 'https://encrypted-tbn3.gstatic.com/images?q=tbn:ANd9GcQ8TNF1mAw0VymIYZlqOUxktqmdgRyS5ylHtXeHyQyso8103P4sZA' }
          } else {
            var _this = this
            options.http.get(options.apiPrefix + '/xit/user/info').then(function (response) {
              console.info('Status: OK, Body: ' + Object.keys(response.data))
              _this.loggedIn = true
              _this.userInfo = response.data
              _this.$emit('loggedIn')
              options.router.push('/')
            }).catch(function (error) {
              console.error('Cannot authentication user. Status: ' + error.response.status)
              _this.logout()
            })
          }
        },
        logout() {
          this.loggedIn = false
          this.userInfo = undefined
          options.router.push('/sign-in')
        }
      }
    })
  }
}

export default AuthPlugin

function ApplyRouteGuard(router) {
  router.beforeEach((to, from, next) => {
    let route = to.matched.find(e => e.meta.auth != null)
    if (route) {
      let auth = route.meta.auth
      if (auth && !this.loggedIn) {
        console.log('Access denied - only for authenticated users:', route.path)
        this.login()
      } else if (!auth && this.loggedIn) {
        console.log('Hide from authenticated users - only for guests:', route.path)
        // TODO: disable, hide somehow
      } else {
        console.log('Appropriate route, user/guset has rights to visit:', route.path)
      }
    }
    next()
  })
}
