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
                <path fill="#000000" d="M19,3H5C3.89,3 3,3.89 3,5V9H5V5H19V19H5V15H3V19A2,2 0 0,0 5,21H19A2,2 0 0,0 21,19V5C21,3.89 20.1,3 19,3M10.08,15.58L11.5,17L16.5,12L11.5,7L10.08,8.41L12.67,11H3V13H12.67L10.08,15.58Z" />
              </svg>
            </md-icon>
            <span>Sign In</span>
          </md-list-item>
          <md-list-item v-if="$auth.loggedIn" @click.native="routeTo('/')">
            <md-icon>home</md-icon>
            <span>Home</span>
          </md-list-item>
          <md-list-item v-if="isAdmin()" @click.native="routeTo('/settings')">
            <md-icon>settings</md-icon>
            <span>Settings</span>
          </md-list-item>
          <md-list-item v-if="$auth.loggedIn" @click.native="routeTo('/about')">
            <md-icon>help</md-icon>
            <span>About</span>
          </md-list-item>
          <md-list-item v-if="$auth.loggedIn" @click.native="signOut()">
            <md-icon>
              <svg style="width:24px;height:24px" viewBox="0 0 24 24">
                <path fill="#000000" d="M14.08,15.59L16.67,13H7V11H16.67L14.08,8.41L15.5,7L20.5,12L15.5,17L14.08,15.59M19,3A2,2 0 0,1 21,5V9.67L19,7.67V5H5V19H19V16.33L21,14.33V19A2,2 0 0,1 19,21H5C3.89,21 3,20.1 3,19V5C3,3.89 3.89,3 5,3H19Z" />
              </svg>
            </md-icon>
            <span>Sign Out</span>
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
    },
    signOut() {
      console.info('Signing out...')
      this.$refs.sidebar.toggle()
      this.$auth.logout()
    },
    isAdmin() {
      return this.$auth.loggedIn && this.$auth.userInfo.amAdmin
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
