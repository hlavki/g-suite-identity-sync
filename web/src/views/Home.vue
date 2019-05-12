<template>
  <div class="md-layout-item md-size-50 md-small-size-100">
    <form novalidate @submit.prevent="validateForm">
      <md-card>
        <md-card-header>
          <md-progress-bar md-mode="indeterminate" v-if="showProgress" />
          <div class="md-title">{{ $t("message.ldapAccount") }}</div>
        </md-card-header>
        <md-card-content>
          <md-field>
            <label>{{ $t("message.username") }}</label>
            <md-input v-model="accountData.username" disabled />
          </md-field>

          <md-field>
            <label>{{ $t("message.name") }}</label>
            <md-input v-model="accountData.name" disabled />
          </md-field>

          <md-list class="md-dense">
            <md-subheader>{{ $t("message.emails") }}</md-subheader>
            <md-list-item v-for="email in accountData.emails" :key="email" disabled>
              <md-icon>email</md-icon>
              <span class="md-list-item-text">{{ email }}</span>
            </md-list-item>
          </md-list>

          <md-field :class="{'md-invalid': errors.has('password')}" md-has-password>
            <label for="password">{{ $t("message.ldapPassword") }}</label>
            <md-input
              v-model="formData.password"
              name="password"
              type="password"
              v-validate="'required|min:8|password'"
              ref="password"
              required
            />
            <span class="md-error">{{errors.first('password')}}</span>
          </md-field>

          <md-field :class="{'md-invalid': errors.has('password-confirm')}">
            <label>{{ $t("message.confirmLdapPassword") }}</label>
            <md-input
              v-model="formData.confirmPassword"
              name="password-confirm"
              type="password"
              v-validate="'required|confirmed:password'"
              data-vv-as="password"
              required
            />
            <span class="md-error">{{errors.first('password-confirm')}}</span>
          </md-field>

          <md-switch class="md-primary" v-if="showSaveGSuitePasswordCheckbox()" v-model="formData.saveGSuitePassword">
            {{ $t("message.syncGSuitePassword") }}
          </md-switch>
          <br />
          <md-button type="submit" class="md-raised md-primary">{{ $t("message.updateLdapAccount") }}</md-button>
        </md-card-content>
      </md-card>
    </form>
  </div>
</template>

<script>
export default {
  name: "Home",
  data: () => ({
    accountData: { username: "", name: "", role: "" },
    showProgress: false,
    formData: { password: "", confirmPassword: "", saveGSuitePassword: false }
  }),
  created: function() {
    this.setAccountDetail();
  },
  methods: {
    setAccountDetail() {
      var _this = this;
      this.$http
        .get(this.$apiPrefix + "/identity/account")
        .then(function(response) {
          console.info(
            "Account Detail. Status: OK, Body: " + Object.keys(response.data)
          );
          _this.accountData = response.data;
          _this.processFormData(_this.accountData);
          _this.showProgress = false;
        })
        .catch(function(error) {
          _this.showProgress = false;
          if (error.response.status === 404) {
            if (_this.$production) _this.$router.push("/create-account");
            else {
              _this.$router.push("/");
              _this.accountData = {
                username: "george@xit.camp",
                name: "George Soros",
                emails: ["george.soros@xit.camp", "georgo@xit.camp"],
                role: "INTERNAL"
              };
            }
          } else if (error.response.status === 401) {
            _this.$auth.logout();
          } else {
            _this.notifyError(error.response);
          }
          console.error("LDAP Account not found: " + error.response.status);
        });
    },
    showSaveGSuitePasswordCheckbox() {
      return this.accountData.role === "INTERNAL";
    },
    processFormData(accountData) {
      this.formData.saveGSuitePassword = accountData.saveGSuitePassword;
    },
    notifyError(response) {
      let data = response.data;
      let message = typeof data === "object" ? data.message : data;
      this.$swal({
        type: "error",
        title: "Error Occured",
        text: response.status === 404 ? "Resource not found!" : message
      });
    },
    notifySuccess(titleText) {
      this.$swal({
        type: "success",
        title: titleText
      });
    },
    validateForm() {
      var _this = this;
      this.$validator
        .validateAll()
        .then(res => {
          if (!res) {
            // Catch errors
            console.warn("Form Invalid: " + _this.errors);
            return;
          }
          _this.showProgress = true;
          console.info("Valid. Creating account");
          _this.$http
            .put(_this.$apiPrefix + "/identity/account", _this.formData)
            .then(function(response) {
              console.info("Account updated!" + response.data);
              // Synchronize groups
              _this.$http
                .put(_this.$apiPrefix + "/identity/account/groups")
                .then(function(response) {
                  console.info("Groups synchronized!" + response.data);
                  _this.notifySuccess("Account successfully updated!");
                  _this.showProgress = false;
                  _this.setAccountDetail();
                })
                .catch(function(error) {
                  console.warn("Error while synchronize groups! " + error);
                  _this.showProgress = false;
                  _this.notifyError(error.response);
                });
            })
            .catch(function(error) {
              console.warn("Error while creating account! " + error);
              _this.showProgress = false;
              _this.notifyError(error.response);
            });
        })
        .catch(function(e) {
          // Catch errors
          console.warn("Form Invalid: " + e);
        });
    }
  }
};
</script>

<style>
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
