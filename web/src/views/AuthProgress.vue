<template>
  <div class="md-layout-item">
    <md-card>
      <md-card-header>
        <div class="md-title">{{ $t("message.authProgress") }}</div>
      </md-card-header>
      <md-card-content class="progress">
        <md-progress-spinner md-mode="indeterminate" v-if="showProgress" />
      </md-card-content>
    </md-card>
  </div>
</template>

<script>
export default {
  name: 'AuthProgress',
  data() {
    return {
      showProgress: false
    };
  },
  created: function() {
    this.showProgress = true;
  },
  destroyed: function() {
    this.showProgress = false;
  }
};
</script>

<style>
.progress {
  text-align: center;
  width: 100%;
}
</style>
