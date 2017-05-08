<template>
  <div id="app" class="container">
    <nav>
      <md-toolbar class="md-dense">
        <md-button class="md-icon-button" @click.native="toggleSidenav">
          <md-icon>menu</md-icon>
        </md-button>
        <h2 class="md-title">{{ titleMsg }}</h2>
      </md-toolbar>
      <md-sidenav class="md-left" ref="sidebar" @open="open()" @close="close()">
        <md-toolbar class="md-dense">
          <!--<div class="md-toolbar-container">
                  <h3 class="md-title">Menu</h3>
                </div>-->
          <md-list v-if="$auth.loggedIn" class="md-transparent">
            <md-list-item class="md-avatar-list">
              <md-avatar class="md-large">
                <img v-bind:src="$auth.userInfo.imageUri" alt="People" />
              </md-avatar>
            </md-list-item>
  
            <md-list-item>
              <div class="md-list-text-container">
                <span>{{ $auth.userInfo.name }}</span>
                <span>{{ $auth.userInfo.email }}</span>
              </div>
            </md-list-item>
          </md-list>
        </md-toolbar>
  
        <md-list>
          <md-list-item v-if="!$auth.loggedIn" @click.native="routeTo('/sign-in')">
            <md-icon>
              <svg style="width:24px;height:24px" viewBox="0 0 24 24">
                <path fill="#000000" d="M10,17.25V14H3V10H10V6.75L15.25,12L10,17.25M8,2H17A2,2 0 0,1 19,4V20A2,2 0 0,1 17,22H8A2,2 0 0,1 6,20V16H8V20H17V4H8V8H6V4A2,2 0 0,1 8,2Z" />
              </svg>
            </md-icon>
            <span>Sign In</span>
          </md-list-item>
          <md-list-item v-if="$auth.loggedIn" @click.native="routeTo('/')">
            <md-icon>home</md-icon>
            <span>Home</span>
          </md-list-item>
          <md-list-item v-if="$auth.loggedIn" @click.native="routeTo('/about')">
            <md-icon>help</md-icon>
            <span>About</span>
          </md-list-item>
        </md-list>
      </md-sidenav>
    </nav>
    <router-view></router-view>
  </div>
</template>


<script>
export default {
  name: 'app',
  data() {
    return {
      titleMsg: 'xIT Access Accout Manager'
    }
  },
  methods: {
    toggleSidenav() {
      this.$refs.sidebar.toggle()
    },
    open() {
      console.log('Opened menu')
    },
    close() {
      console.log('Closed menu')
    },
    routeTo(ref) {
      console.info('Routing to ' + ref)
      this.$router.push(ref)
      this.$refs.sidebar.toggle()
    }
  }
}
</script>

<style>
#app {}

.main-content {
  padding: 16px;
}
</style>
