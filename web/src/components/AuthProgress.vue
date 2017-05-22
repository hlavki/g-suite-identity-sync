<template>
  <div>
    <md-whiteframe md-elevation="6" class="global-frame">
      <md-subheader>Authentication & authorizing your user profile</md-subheader>
      <div class="progress">
        <md-spinner :md-size="70" :md-stroke="2" md-indeterminate v-if="showProgress"></md-spinner>
      </div>
    </md-whiteframe>
  </div>
</template>

<script>
export default {
  name: 'AuthProgress',
  data() {
    return {
      showProgress: false
    }
  },
  created: function () {
    this.showProgress = true
  },
  destroyed: function () {
    this.showProgress = false
  }
}
</script>

<style>
.progress {
  text-align: center;
  width: 100%;
}
</style>
