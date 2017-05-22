<template>
  <div>
    <md-whiteframe md-elevation="6" class="global-frame">
      <md-subheader>LDAP Account info (Account already created)</md-subheader>
      <md-progress md-indeterminate v-if="showProgress"></md-progress>
      <md-input-container>
        <label>Username</label>
        <md-input v-model="accountData.username" disabled></md-input>
      </md-input-container>
  
      <md-input-container>
        <label>Name</label>
        <md-input v-model="accountData.name" disabled></md-input>
      </md-input-container>
  
      <md-input-container>
        <label>Primary email</label>
        <md-input v-model="accountData.email" disabled></md-input>
      </md-input-container>
  
      <md-input-container :class="{'md-input-invalid': errors.has('password')}" md-has-password>
        <label for="password">Type LDAP Password</label>
        <md-input v-model="formData.password" name="password" type="password" v-validate data-vv-name="password" data-vv-rules="required|min:8|password" required></md-input>
        <span class="md-error">{{errors.first('password')}}</span>
      </md-input-container>
  
      <md-input-container :class="{'md-input-invalid': errors.has('password-confirm')}">
        <label>Confirm LDAP Password</label>
        <md-input v-model="formData.confirmPassword" name="password-confirm" type="password" v-validate data-vv-name="password-confirm" data-vv-rules="required|confirmed:password" required></md-input>
        <span class="md-error">{{errors.first('password-confirm')}}</span>
      </md-input-container>
  
      <md-checkbox class="md-primary" v-if="showSaveGSuitePasswordCheckbox()" v-model="formData.saveGSuitePassword">Sync GSuite Password</md-checkbox>
      <br/>
      <div v-if="error" class="error-label">
        <label>OMG: [{{ error.code }}] {{ error.message }}</label>
        <br/>
      </div>
      <md-button class="md-raised md-primary" @click.native="sendData">Update LDAP Password</md-button>
  
    </md-whiteframe>
  </div>
</template>

<script>
export default {
  name: 'Home',
  data() {
    return {
      accountData: { username: '', name: '', role: '' },
      showProgress: false,
      formData: { password: '', confirmPassword: '', saveGSuitePassword: true }
    }
  },
  created: function () {
    this.setAccountDetail()
  },
  methods: {
    clearError() {
      this.error = undefined
      this.message = undefined
    },
    setAccountDetail() {
      this.clearError()
      var _this = this
      this.$http.get(this.$apiPrefix + '/xit/account').then(function (response) {
        console.info('Account Detail. Status: OK, Body: ' + Object.keys(response.data))
        _this.accountData = response.data
      }).catch(function (error) {
        if (error.response.status === 404) {
          if (_this.$isProduction) _this.$router.push('/create-account')
          else {
            _this.accountData = { username: 'george@xit.camp', name: 'George Soros', email: 'george.soros@xit.camp', role: 'INTERNAL' }
          }
        } else if (error.response.status === 401) {
          _this.$auth.logout()
        } else {
          _this.error = error.response.data
        }
        console.error('LDAP Account not found: ' + error.response.status)
      })
    },
    showSaveGSuitePasswordCheckbox() {
      return this.accountData.role === 'INTERNAL'
    },
    sendData: function (event) {
      this.clearError()
      var _this = this
      this.$validator
        .validateAll()
        .then(function (response) {
          _this.showProgress = true
          console.info('Valid. Creating account')
          _this.$http.put(_this.$apiPrefix + '/xit/account', _this.formData).then(function (response) {
            console.info('Account updated!' + response.data)
            _this.showProgress = false
            _this.setAccountDetail()
            _this.notifyAccountCreated()
          }).catch(function (error) {
            console.warn('Error while creating account! ' + error)
            _this.showProgress = false
            _this.notifyError({ message: error.response.data })
          })
        }).catch(function (e) {
          // Catch errors
          console.warn('Form Invalid: ' + e)
        })
    }
  },
  notifications: {
    notifyAccountUpdated: {
      title: 'Account updated',
      message: 'User account successfully updated.',
      type: 'info',
      timeout: 5000
    },
    notifyError: {
      title: 'Error Occured',
      type: 'error',
      timeout: 5000
    }
  }
}
</script>

<style>
.global-frame {
  max-width: 600px;
  margin: 5px;
  padding-left: 10px;
  padding-right: 5px;
}

.group-frame {
  margin-right: 5px;
  padding-left: 0px;
  padding-right: 5px;
  margin-bottom: 20px;
}

.error-label {
  background-color: hotpink;
  color: black;
  padding: 5px;
}

.msg-label {
  background-color: darkseagreen;
  color: black;
  padding: 5px;
}

.mini-toastr {
  left: 12px;
}
</style>
