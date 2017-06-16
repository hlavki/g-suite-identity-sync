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
  
      <md-whiteframe class="group-frame">
        <md-subheader style="color: rgba(0,0,0,.38);">Emails</md-subheader>
        <md-list>
          <md-list-item v-for="email in accountData.emails" :key="email" disabled>
            <md-icon>email</md-icon>
            <div class="md-list-text-container">
              <span>{{ email }}</span>
            </div>
          </md-list-item>
        </md-list>
      </md-whiteframe>
  
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
  
      <md-checkbox class="md-primary" v-if="showSaveGSuitePasswordCheckbox()" v-model="formData.saveGSuitePassword">Synchronize GSuite Password</md-checkbox>
      <br/>
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
      formData: { password: '', confirmPassword: '', saveGSuitePassword: false }
    }
  },
  created: function () {
    this.setAccountDetail()
  },
  methods: {
    setAccountDetail() {
      // this.showProgress = true
      var _this = this
      this.$http.get(this.$apiPrefix + '/xit/account').then(function (response) {
        console.info('Account Detail. Status: OK, Body: ' + Object.keys(response.data))
        _this.accountData = response.data
        _this.processFormData(_this.accountData)
        _this.showProgress = false
      }).catch(function (error) {
        _this.showProgress = false
        if (error.response.status === 404) {
          if (_this.$isProduction) _this.$router.push('/create-account')
          else {
            _this.accountData = { username: 'george@xit.camp', name: 'George Soros', emails: ['george.soros@xit.camp', 'georgo@xit.camp'], role: 'INTERNAL' }
          }
        } else if (error.response.status === 401) {
          _this.$auth.logout()
        } else {
          _this.notifyError({ message: error.response.data })
        }
        console.error('LDAP Account not found: ' + error.response.status)
      })
    },
    showSaveGSuitePasswordCheckbox() {
      return this.accountData.role === 'INTERNAL'
    },
    processFormData(userData) {
      this.formData.saveGSuitePassword = accountData.saveGSuitePassword
    },
    sendData: function (event) {
      var _this = this
      this.$validator
        .validateAll()
        .then(function (response) {
          _this.showProgress = true
          console.info('Valid. Creating account')
          _this.$http.put(_this.$apiPrefix + '/xit/account', _this.formData).then(function (response) {
            console.info('Account updated!' + response.data)
            _this.setAccountDetail()
            _this.notifyAccountUpdated()
            // Synchronize groups
            _this.$http.put(_this.$apiPrefix + '/xit/account/groups').then(function (response) {
              console.info('Groups synchronized!' + response.data)
              _this.showProgress = false
              _this.notifyGroupsSynchronized()
            }).catch(function (error) {
              console.warn('Error while synchronize groups! ' + error)
              _this.showProgress = false
              var msgData = error.response.data
              _this.notifyError({ message: typeof (msgData) === 'object' ? msgData.message : msgData })
            })
          }).catch(function (error) {
            console.warn('Error while creating account! ' + error)
            _this.showProgress = false
            var msgData = error.response.data
            _this.notifyError({ message: typeof (msgData) === 'object' ? msgData.message : msgData })
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
      type: 'success',
      timeout: 5000
    },
    notifyGroupsSynchronized: {
      title: 'Group synchronization',
      message: 'All groups synchronized.',
      type: 'success',
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
</style>
