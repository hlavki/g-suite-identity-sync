<template>
  <div>
    <md-whiteframe md-elevation="6" class="global-frame">
      <md-subheader>Create LDAP Account</md-subheader>
      <md-progress md-indeterminate v-if="showProgress"></md-progress>
      <form novalidate @submit.stop.prevent="submit">
        <md-input-container>
          <label>Name</label>
          <md-input v-model="userData.name" disabled></md-input>
        </md-input-container>
  
        <md-input-container>
          <label>Select username (email)</label>
          <md-select name="emails" id="email" v-model="formData.email" required>
            <md-option v-for="email in userData.emails" :key="email.email" v-bind:value="email.email">
              {{ email.email }}
            </md-option>
          </md-select>
        </md-input-container>
  
        <md-input-container>
          <label>Role</label>
          <md-input v-model="userData.role" disabled></md-input>
        </md-input-container>
  
        <md-whiteframe v-if="showGroups()" class="group-frame">
          <md-subheader style="color: rgba(0,0,0,.38);">Groups</md-subheader>
          <md-list class="md-double-line md-dense">
            <md-list-item v-for="group in userData.groups" :key="group.email" disabled>
              <md-icon class="md-primary">group</md-icon>
  
              <div class="md-list-text-container">
                <span>{{ group.name }}</span>
                <span>{{ group.email }}</span>
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
  
        <md-checkbox class="md-primary" v-if="showSaveGSuitePasswordCheckbox()" v-model="formData.saveGSuitePassword">Sync GSuite Password</md-checkbox>
        <br/>
        <md-button class="md-raised md-primary" @click.native="sendData">Create LDAP Profile</md-button>
  
      </form>
    </md-whiteframe>
  </div>
</template>

<script>
export default {
  name: 'CreateAccount',
  data() {
    return {
      userData: { email: '', name: '', role: '', password: '' },
      showProgress: true,
      formData: { email: '', password: '', confirmPassword: '', saveGSuitePassword: false }
    }
  },
  created: function () {
    this.setAccountDetail()
  },
  methods: {
    setAccountDetail() {
      this.showProgress = true
      var _this = this
      this.$http.get(this.$apiPrefix + '/xit/account/prepare').then(function (response) {
        console.info('Account prepare data. Status: OK, Body: ' + Object.keys(response.data))
        _this.userData = response.data
        _this.processFormData(_this.userData)
        _this.showProgress = false
      }).catch(function (error) {
        console.error('Cannot authentication user. Status: ' + error.response.status)
        _this.notifyError({ message: error.response.data })
        if (_this.$isProduction) _this.$auth.logout()
        else {
          _this.userData = { emails: [{ email: 'user@example.com', primary: false }, { email: 'user2@example.com', primary: true }], name: 'George Soros', role: 'INTERNAL', saveGSuitePassword: true, groups: [{ name: 'Group1', email: 'group1@example.com' }, { name: 'Group2', email: 'group2@example.com' }] }
          _this.processFormData(_this.userData)
        }
        _this.showProgress = false
      })
    },
    sendData: function (event) {
      var _this = this
      this.$validator
        .validateAll()
        .then(function (response) {
          _this.showProgress = true
          console.info('Valid. Creating account')
          // Create acccount
          _this.$http.post(_this.$apiPrefix + '/xit/account', _this.formData).then(function (response) {
            console.info('Account created!' + response.data)
            _this.showProgress = false
            _this.$router.push('/')
            _this.notifyAccountCreated()
            // Synchronize groups
            _this.$http.put(_this.$apiPrefix + '/xit/account/groups').then(function (response) {
              console.info('Groups synchronized!' + response.data)
              _this.notifyGroupsSynchronized()
            }).catch(function (error) {
              console.warn('Error while synchronize groups! ' + error)
              _this.showProgress = false
              _this.notifyError({ message: error.response.data })
            })
          }).catch(function (error) {
            console.warn('Error while creating account! ' + error)
            _this.showProgress = false
            _this.notifyError({ message: error.response.data })
          })
        }).catch(function (e) {
          // Catch errors
          console.warn('Form Invalid: ' + e)
        })
    },
    showSaveGSuitePasswordCheckbox() {
      return this.userData.role === 'INTERNAL'
    },
    showGroups() {
      var groups = this.userData.groups
      return (typeof groups !== 'undefined') && groups.length > 0
    },
    processFormData(userData) {
      for (var i = 0; i < userData.emails.length; i++) {
        if (userData.emails[i].primary) {
          this.formData.email = userData.emails[i].email
        }
      }
      this.formData.saveGSuitePassword = userData.saveGSuitePassword
    }
  },
  notifications: {
    notifyAccountCreated: {
      title: 'Account creation',
      message: 'User account successfully created.',
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
</style>
