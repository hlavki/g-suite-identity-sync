<template>
  <div>
    Zobrazim detaily konta z LDAPu</div>
</template>

<script>
export default {
  name: 'Home',
  data() {
    return {
    }
  },
  created: function () {
    this.setAccountDetail()
  },
  methods: {
    setAccountDetail() {
      var _this = this
      this.$http.get(this.$apiPrefix + '/xit/account').then(function (response) {
        console.info('Account Detail. Status: OK, Body: ' + Object.keys(response.data))
      }).catch(function (error) {
        if (error.response.status === 404) {
          _this.$router.push('/create-account')
        } else {
          _this.$auth.logout()
        }
        console.error('LDAP Account not found: ' + error.response.status)
      })
    }
  }
}
</script>
