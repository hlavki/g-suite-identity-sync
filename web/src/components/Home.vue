<template>
  <!--<form-wizard color="#4caf50" title="">
                                      <tab-content title="Personal details">
                                        My first tab content
                                      </tab-content>
                                      <tab-content title="Set LDAP Password">
                                        My second tab content
                                      </tab-content>
                                      <tab-content title="Confirm">
                                        Yuhuuu! This seems pretty damn simple
                                      </tab-content>
                                    </form-wizard>-->
  <div>
    <md-whiteframe md-elevation="6" style="max-width: 600px; margin: 5px; padding-left:10px; padding-right: 5px;">
      <form novalidate @submit.stop.prevent="submit">
        <md-input-container>
          <label>Name</label>
          <md-input v-model="userData.name" disabled></md-input>
        </md-input-container>
  
        <md-input-container>
          <label>Username (E-mail)</label>
          <md-input v-model="userData.email" disabled></md-input>
        </md-input-container>
  
        <md-input-container>
          <label>Role</label>
          <md-input v-model="userData.role" disabled></md-input>
        </md-input-container>
  
        <md-input-container md-has-password>
          <label>Type LDAP Password</label>
          <md-input type="password"></md-input>
        </md-input-container>
  
        <md-input-container>
          <label>Repeat LDAP Password</label>
          <md-input type="password"></md-input>
        </md-input-container>
  
        <md-checkbox class="md-primary" v-if="userData.role === 'INTERNAL'" v-model="userData.saveGSuitePassword">Save GSuite Password</md-checkbox>
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
      userData: { email: '', name: '', role: 'INTERNAL', saveGSuitePassword: true }
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
      var _this = this
      this.$http.get(this.$apiPrefix + '/xit/user/detail').then(function (response) {
        console.info('User Detail. Status: OK, Body: ' + Object.keys(response.data))
        _this.userData = response.data
      }).catch(function (error) {
        console.error('Cannot authentication user. Status: ' + error.response.status)
        if (_this.$isProduction) _this.$auth.logout()
      })
    },
    sendData: function (event) {
      alert('Data akože odoslane! ' + this)
    }
  }
}
</script>
