<template>
  <div class="md-layout-item md-size-50 md-small-size-100">
    <form novalidate @submit.prevent="validateForm">
      <md-card>
        <md-progress-bar md-mode="indeterminate" v-if="showProgress"/>
        <md-card-header>
          <div class="md-title">{{ $t("message.install.title") }}</div>
        </md-card-header>
        <md-card-content>
          <md-field :class="messageClass">
            <label>{{ $t("message.install.domain") }}</label>
            <md-input name="domain" id="domain" v-model="data.domain" disabled />
          </md-field>
          <md-field :class="messageClass">
            <label>{{ $t("message.install.adminAccount") }}</label>
            <md-input name="adminAccount" id="adminAccount" v-model="data.userInfo.email" disabled />
          </md-field>
          <md-field :class="messageClass">
            <label>{{ $t("message.install.serviceAccountJson") }}</label>
            <md-textarea v-model="data.serviceAccount" required/>
            <span class="md-helper-text">{{ $t("message.install.serviceAccountJsonHelp") }}</span>
          </md-field>
        </md-card-content>
        <md-card-actions>
          <md-button type="submit" class="md-raised md-primary">{{ $t("message.install.serviceAccountSetup") }}</md-button>
        </md-card-actions>
      </md-card>
    </form>
  </div>
</template>

<script>
export default {
  name: "Install",
  data() {
    return {
      data: { domain: "", serviceKey: "", userInfo: {}, serviceAccount: "" },
      showProgress: false
    }
  },
  created: function() {
    this.setAccountDetail()
  },
  methods: {
    setAccountDetail() {
      this.showProgress = true
      var _this = this
      this.$http
        .get(this.$apiPrefix + "/setup/google/settings")
        .then(function(response) {
          console.info(
            "Account prepare data. Status: OK, Body: " +
              Object.keys(response.data)
          )
          _this.data = response.data
          _this.showProgress = false
        })
        .catch(function(error) {
          console.error(
            "Cannot authentication user. Status: " + error.response.status
          )
          _this.notifyError(error.response)
          _this.showProgress = false
        })
    },
    validateForm() {
      var _this = this
      this.$validator
        .validateAll()
        .then(res => {
          if (!res) {
            // Catch errors
            console.warn("Form Invalid: " + _this.errors)
            return
          }
          _this.showProgress = true
          console.info("Valid. Installing")
          // Create acccount
          let serviceAccountData = JSON.parse(_this.data.serviceAccount)
          _this.$http
            .put(_this.$apiPrefix + "/setup/google/settings/service-account", serviceAccountData)
            .then(function(response) {
              console.info("Service account installed!")
              _this.showProgress = false
              _this.$swal({
                type: "success",
                text: _this.$t("message.install.serviceAccountSuccess"),
                onClose: () => {
                  _this.$router.push("/")
                }
              })
            })
            .catch(function(error) {
              console.warn("Error while installing service account! " + error)
              _this.showProgress = false
              _this.notifyError(error.response)
            })
        })
        .catch(function(e) {})
    },
    notifyError(response) {
      let data = response.data
      let message = typeof data === "object" ? data.message : data
      this.$swal({
        type: "error",
        title: "Error Occured",
        text: response.status === 404 ? "Resource not found!" : message
      })
    }
  }
}
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
