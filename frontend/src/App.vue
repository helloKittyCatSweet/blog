<script setup>
import { RouterView } from "vue-router";
import zh from "element-plus/es/locale/lang/zh-cn.mjs";
import { useUserStore, useSettingsStore } from "./stores";
import { computed, onMounted, ref } from "vue";

const isLoading = ref(true);
const userStore = useUserStore();
const settingsStore = useSettingsStore();

onMounted(async () => {
  try {
    // 如果有token，尝试获取用户信息
    if (userStore.getToken()) {
      await userStore.getUser();
    }
  } catch (error) {
    console.error("获取用户信息失败:", error);
    // 可能需要清除 token 并跳转到登录页
    userStore.clear();
    router.push("/login");
  } finally {
    isLoading.value = false;
  }
});

const currentTheme = computed(() => `theme-${settingsStore.getTheme()}`);
</script>

<template>
  <el-config-provider :locale="zh">
    <div id="app" :class="currentTheme">
      <router-view v-if="!isLoading" />
      <div v-else class="loading-container">
        <el-loading :fullscreen="true" />
      </div>
    </div>
  </el-config-provider>
</template>

<style>
#app {
  min-height: 100vh;
  transition: background-color 0.3s, color 0.3s;
}

.loading-container {
  height: 100vh;
  display: flex;
  justify-content: center;
  align-items: center;
}
</style>
