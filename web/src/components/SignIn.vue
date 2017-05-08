<template>
  <div class="hello">
    <div class="main-content">
      <!--<google-sign-in-button /><br/>-->
      <!-- Sign-in button -->
      <!--<button class="google-sign-in" />-->
      <md-button class="md-raised md-primary" v-on:click.native="signIn"><img class="google-logo" src="../assets/google.png">Sign In</md-button>
    </div>
  </div>
</template>

<script>
export default {
  name: 'SignIn',
  methods: {
    signIn: function (event) {
      location.href = this.$apiPrefix + '/oidc/rp'
    }
  }
}
</script>

<!-- Add "scoped" attribute to limit CSS to this component only -->
<style scoped>
h1,
h2 {
  font-weight: normal;
}

ul {
  list-style-type: none;
  padding: 0;
}

li {
  display: inline-block;
  margin: 0 10px;
}

a {
  color: #42b983;
}

.google-sign-in {
  background-image: url('../assets/google-sign-in.png');
  background-color: transparent;
  width: 191px;
  height: 46px;
  border: none;
}

.google-logo {
  margin: -4px 0px 0px -5px;
  padding-right: 10px;
}
</style>
