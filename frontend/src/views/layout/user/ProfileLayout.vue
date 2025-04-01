<script setup>
import { useRoute } from "vue-router";

const route = useRoute();
</script>

<template>
  <div class="message-layout">
    <router-view v-slot="{ Component }">
      <component :is="Component" />
    </router-view>
  </div>
</template>

<style lang="scss" scoped>
.message-layout {
  height: 100%;
  width: 100%;
}
</style>
