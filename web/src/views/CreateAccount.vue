<template>
  <div class="md-layout-item">
    <form novalidate @submit.prevent="validateForm">
      <md-card>
        <md-progress-bar v-if="showProgress" />
        <md-card-header>
          <div class="md-title">{{ $t("message.createLdapAccount") }}</div>
        </md-card-header>
        <md-card-content>
          <md-field>
            <label>{{ $t("message.name") }}</label>
            <md-input v-model="userData.name" disabled />
          </md-field>

          <md-field>
            <label for="formData.email">{{ $t("message.selectUsername") }}</label>
            <md-select v-model="formData.email" name="email" id="email" md-dense>
              <md-option v-for="email in userData.emails" :key="email" :value="email">
                {{ email }}
              </md-option>
            </md-select>
          </md-field>

          <md-field>
            <label>{{ $t("message.role") }}</label>
            <md-input v-model="userData.role" disabled />
          </md-field>

          <md-list class="md-double-line md-dense" v-if="showGroups()">
            <md-subheader>{{ $t("message.groups") }}</md-subheader>
            <md-list-item v-for="group in userData.groups" :key="group.email" disabled>
              <md-icon class="md-primary">group</md-icon>

              <div class="md-list-item-text">
                <span>{{ group.name }}</span>
                <span>{{ group.email }}</span>
              </div>
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
          <md-button type="submit" class="md-raised md-primary">{{ $t("message.createLdapAccount") }}</md-button>
        </md-card-content>
      </md-card>
    </form>
  </div>
</template>

<script>
export default {
  name: "CreateAccount",
  data: () => ({
    showProgress: true,
    formData: {
      email: null,
      password: "",
      confirmPassword: "",
      saveGSuitePassword: false
    }
  }),
  created: function() {
    this.setAccountDetail();
  },
  methods: {
    setAccountDetail() {
      this.showProgress = true;
      var _this = this;
      this.$http
        .get(this.$apiPrefix + "/identity/account/prepare")
        .then(function(response) {
          console.info(
            "Account prepare data. Status: OK, Body: " +
              Object.keys(response.data)
          );
          _this.userData = response.data;
          _this.processFormData(_this.userData);
          _this.showProgress = false;
        })
        .catch(function(error) {
          console.error(
            "Cannot authentication user. Status: " + error.response.status
          );
          _this.notifyError(error.response);
          if (_this.$production) _this.$auth.logout();
          else {
            _this.userData = {
              email: "jara@cimrman.cz",
              emails: ["jara@cimrman.cz", "jaroslav.cimrman@cimrman.cz"],
              name: "Jaroslav Cimrman",
              role: "INTERNAL",
              saveGSuitePassword: true,
              groups: [{ name: "Lezici", email: "lezici@cimrman.cz" }]
            };
            _this.processFormData(_this.userData);
          }
          _this.showProgress = false;
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
          // Create acccount
          _this.$http
            .post(_this.$apiPrefix + "/identity/account", _this.formData)
            .then(function(response) {
              console.info("Account created!" + response.data);
              // Synchronize groups
              _this.$http
                .put(_this.$apiPrefix + "/identity/account/groups")
                .then(function(response) {
                  console.info("Groups synchronized!" + response.data);
                  _this.showProgress = false;
                  _this.notifySuccess("Account successfully created!");
                  _this.$router.push("/");
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
        .catch(function(e) {});
    },
    showSaveGSuitePasswordCheckbox() {
      return this.userData.role === "INTERNAL";
    },
    showGroups() {
      var groups = this.userData.groups;
      return typeof groups !== "undefined" && groups.length > 0;
    },
    processFormData(userData) {
      this.formData.email = userData.email;
      this.formData.saveGSuitePassword = userData.saveGSuitePassword;
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
</style>
