<template>
  <div>
    <md-card class="md-layout-item md-size-40 md-small-size-100">
      <md-progress-bar md-mode="indeterminate" v-if="showProgress" />
      <md-card-header>
        <div class="md-title">Settings</div>
      </md-card-header>
      <md-card-content>
        <md-button class="md-raised md-primary" @click.native="synchronizeGroups">Synchronize Groups to LDAP</md-button>
        <md-button class="md-raised md-primary" @click.native="synchronizeUsers">Synchronize GSuite User Attributes</md-button>
      </md-card-content>
    </md-card>
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
    };
  },
  created: function() {
    if (!this.$auth.userInfo.amAdmin) {
      this.$auth.logout();
    }
  },
  methods: {
    synchronizeGroups: function(event) {
      var _this = this;
      this.$validator
        .validateAll()
        .then(function(response) {
          _this.showProgress = true;
          console.info('Synchronizing all Gsuite groups to LDAP');
          _this.$http
            .put(_this.$apiPrefix + '/identity/admin/sync/groups')
            .then(function(response) {
              console.info('All GSuite groups synchronized');
              _this.notifyGroupsSynchronized();
              _this.showProgress = false;
            })
            .catch(function(error) {
              console.warn('Error while synchronizing groups! ' + error);
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
    },
    synchronizeUsers: function(event) {
      var _this = this;
      this.$validator
        .validateAll()
        .then(function(response) {
          _this.showProgress = true;
          console.info('Synchronizing Gsuite user attributes to LDAP');
          _this.$http
            .put(_this.$apiPrefix + '/identity/admin/sync/users')
            .then(function(response) {
              console.info('All GSuite user attributes synchronized');
              _this.notifyUsersSynchronized();
              _this.showProgress = false;
            })
            .catch(function(error) {
              console.warn(
                'Error while synchronizing user attributes! ' + error
              );
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
    notifyGroupsSynchronized: {
      title: 'Group synchronization',
      message: 'All GSuite groups synchronized.',
      type: 'success',
      timeout: 5000
    },
    notifyUsersSynchronized: {
      title: 'User attributes synchronization',
      message: 'All GSuite user attributes synchronized.',
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
</style>
