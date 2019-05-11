<template>
  <div class="md-layout-item">
    <md-card>
      <md-progress-bar md-mode="indeterminate" v-if="showProgress" />
      <md-card-header>
        <div class="md-title">{{ $t("message.settings.title") }}</div>
      </md-card-header>
      <md-card-content>
        <md-card class="md-layout-item md-small-size-100">
          <md-card-heder>
            <div class="md-subheading">{{ $t("message.settings.syncSection") }}</div>
          </md-card-heder>
          <md-card-content/>
          <md-card-actions>
            <md-button class="md-raised md-primary" @click.native="synchronizeGroups">{{ $t("message.settings.syncGroupsButton") }}</md-button>
            <md-button class="md-raised md-primary" @click.native="synchronizeUsers">{{ $t("message.settings.syncUsersButton") }}</md-button>
          </md-card-actions>
        </md-card>
      </md-card-content>
    </md-card>
  </div>
</template>

<script>
export default {
  name: 'Settings',
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
  .md-card {
    padding: 8px;
    margin: 8px;
    vertical-align: top;
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
