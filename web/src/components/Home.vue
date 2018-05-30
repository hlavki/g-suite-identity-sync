<template>
  <div class="md-layout">
    <md-card class="md-layout-item md-size-40">
      <md-progress-bar md-mode="indeterminate" v-if="showProgress"/>
      <md-card-header>
        <div class="md-title">LDAP Account info <span class="md-gray">[created]</span></div>
      </md-card-header>
      <md-card-content>
        <md-field>
          <label>Username</label>
          <md-input v-model="accountData.username" disabled></md-input>
        </md-field>

        <md-field>
          <label>Name</label>
          <md-input v-model="accountData.name" disabled></md-input>
        </md-field>

          <md-list class="md-dense">
            <md-subheader>Emails</md-subheader>
            <md-list-item v-for="email in accountData.emails" :key="email" disabled>
              <md-icon>email</md-icon>
                <span class="md-list-item-text">{{ email }}</span>
            </md-list-item>
          </md-list>

        <md-field :class="{'md-invalid': errors.has('password')}" md-has-password>
          <label for="password">Type LDAP Password</label>
          <md-input v-model="formData.password" name="password" type="password" v-validate="'required|min:8|password'" required/>
          <span class="md-error">{{errors.first('password')}}</span>
        </md-field>

        <md-field :class="{'md-invalid': errors.has('password-confirm')}">
          <label>Confirm LDAP Password</label>
          <md-input v-model="formData.confirmPassword" name="password-confirm" type="password" v-validate="'required|confirmed:password'" required/>
          <span class="md-error">{{errors.first('password-confirm')}}</span>
        </md-field>

        <md-switch class="md-primary" v-if="showSaveGSuitePasswordCheckbox()" v-model="formData.saveGSuitePassword">Synchronize GSuite Password</md-switch>
        <br/>
        <md-button class="md-raised md-primary" @click.native="sendData">Update LDAP Password</md-button>
      </md-card-content>
    </md-card>
  </div>
</template>

<script>
export default {
  name: 'Home',
  data: () => ({
    accountData: { username: '', name: '', role: '' },
    showProgress: false,
    formData: { password: '', confirmPassword: '', saveGSuitePassword: false }
  }),
  created: function() {
    this.setAccountDetail();
  },
  methods: {
    setAccountDetail() {
      var _this = this;
      this.$http
        .get(this.$apiPrefix + '/identity/account')
        .then(function(response) {
          console.info(
            'Account Detail. Status: OK, Body: ' + Object.keys(response.data)
          );
          _this.accountData = response.data;
          _this.processFormData(_this.accountData);
          _this.showProgress = false;
        })
        .catch(function(error) {
          _this.showProgress = false;
          if (error.response.status === 404) {
            if (_this.$isProduction) _this.$router.push('/create-account');
            else {
              _this.$router.push('/create-account');
              _this.accountData = {
                username: 'george@xit.camp',
                name: 'George Soros',
                emails: ['george.soros@xit.camp', 'georgo@xit.camp'],
                role: 'INTERNAL'
              };
            }
          } else if (error.response.status === 401) {
            _this.$auth.logout();
          } else {
            _this.notifyError({ message: error.response.data });
          }
          console.error('LDAP Account not found: ' + error.response.status);
        });
    },
    showSaveGSuitePasswordCheckbox() {
      return this.accountData.role === 'INTERNAL';
    },
    processFormData(accountData) {
      this.formData.saveGSuitePassword = accountData.saveGSuitePassword;
    },
    sendData: function(event) {
      var _this = this;
      this.$validator
        .validateAll()
        .then(function(response) {
          _this.showProgress = true;
          console.info('Valid. Creating account');
          _this.$http
            .put(_this.$apiPrefix + '/identity/account', _this.formData)
            .then(function(response) {
              console.info('Account updated!' + response.data);
              _this.setAccountDetail();
              _this.notifyAccountUpdated();
              // Synchronize groups
              _this.$http
                .put(_this.$apiPrefix + '/identity/account/groups')
                .then(function(response) {
                  console.info('Groups synchronized!' + response.data);
                  _this.showProgress = false;
                  _this.notifyGroupsSynchronized();
                })
                .catch(function(error) {
                  console.warn('Error while synchronize groups! ' + error);
                  _this.showProgress = false;
                  var msgData = error.response.data;
                  _this.notifyError({
                    message:
                      typeof msgData === 'object' ? msgData.message : msgData
                  });
                });
            })
            .catch(function(error) {
              console.warn('Error while creating account! ' + error);
              _this.showProgress = false;
              var msgData = error.response.data;
              _this.notifyError({
                message: typeof msgData === 'object' ? msgData.message : msgData
              });
            });
        })
        .catch(function(e) {
          // Catch errors
          console.warn('Form Invalid: ' + e);
        });
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
};
</script>

<style>
.global-frame {
  max-width: 600px;
  margin: 5px;
  padding-left: 10px;
  padding-right: 5px;
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

.md-gray {
  color: gray;
  font-size: 70%;
}
</style>
