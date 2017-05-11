<template>
  <div>
    <md-whiteframe md-elevation="6" class="global-frame">
      <md-progress md-indeterminate v-if="showProgress"></md-progress>
      <form novalidate @submit.stop.prevent="submit">
        <md-input-container>
          <label>Name</label>
          <md-input v-model="userData.name" disabled></md-input>
        </md-input-container>
  
        <md-input-container>
          <label>Select username (email)</label>
          <md-select name="emails" id="email" v-model="selectedEmail">
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
  
        <md-input-container md-has-password>
          <label>Type LDAP Password</label>
          <md-input type="password"></md-input>
        </md-input-container>
  
        <md-input-container>
          <label>Repeat LDAP Password</label>
          <md-input type="password"></md-input>
        </md-input-container>
  
        <md-checkbox class="md-primary" v-if="showSaveGSuitePasswordCheckbox()" v-model="userData.saveGSuitePassword">Save GSuite Password</md-checkbox>
        <br/>
        <md-button class="md-raised md-primary" @click.native="sendData">Create LDAP Profile</md-button>
      </form>
    </md-whiteframe>
  </div>
</template>

<script>
// import { FormWizard, TabContent } from 'vue-form-wizard'
// import 'vue-form-wizard/dist/vue-form-wizard.min.css'
export default {
  name: 'Home',
  data() {
    return {
      initialValue: 'My initial value',
      userData: { email: '', name: '', role: '', saveGSuitePassword: false },
      showProgress: true,
      selectedEmail: ''
    }
  },
  // components: {
  //   FormWizard,
  //   TabContent
  // },
  created: function () {
    this.setUserDetail()
  },
  methods: {
    setUserDetail() {
      this.showProgress = true
      var _this = this
      this.$http.get(this.$apiPrefix + '/xit/user/detail').then(function (response) {
        console.info('User Detail. Status: OK, Body: ' + Object.keys(response.data))
        _this.userData = response.data
        _this.setSelectedEmail(_this.userData.emails)
        _this.showProgress = false
      }).catch(function (error) {
        console.error('Cannot authentication user. Status: ' + error.response.status)
        if (_this.$isProduction) _this.$auth.logout()
        else {
          _this.userData = { emails: [{ email: 'user@example.com', primary: true }, { email: 'user2@example.com', primary: false }], name: 'George Soros', role: 'INTERNAL', saveGSuitePassword: true, groups: [{ name: 'Group1', email: 'group1@example.com' }, { name: 'Group2', email: 'group2@example.com' }] }
          _this.setSelectedEmail(_this.userData.emails)
        }
        _this.showProgress = false
      })
    },
    sendData: function (event) {
      alert('Data akože odoslane! ' + this)
    },
    showSaveGSuitePasswordCheckbox() {
      return this.userData.role === 'INTERNAL'
    },
    showGroups() {
      var groups = this.userData.groups
      return (typeof groups !== 'undefined') && groups.length > 0
    },
    setSelectedEmail(emails) {
      for (var i = 0; i < emails.length; i++) {
        if (emails[i].primary) {
          this.selectedEmail = emails[i].email
        }
      }
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
</style>
