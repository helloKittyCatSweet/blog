<script setup>
import { RouterView } from "vue-router";
import { ElConfigProvider, ElLoading } from "element-plus";
import zhCn from "element-plus/es/locale/lang/zh-cn";
import { useUserStore, useSettingsStore, useThemeStore } from "./stores";
import { computed, onMounted, ref, provide } from "vue";
import { useRouter } from "vue-router";

const isLoading = ref(true);
const userStore = useUserStore();
const settingsStore = useSettingsStore();
const themeStore = useThemeStore();

onMounted(async () => {
  try {
    // 如果有token，尝试获取用户信息
    if (userStore.getToken()) {
      userStore.getUser();
    }
    // 初始化主题
    themeStore.setTheme(themeStore.currentTheme || "light");
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

/**
 * 全局加载
 */

const globalLoading = ref(false);
provide("globalLoading", globalLoading);

const router = useRouter();

// 添加全局路由守卫
router.beforeEach((to, from, next) => {
  globalLoading.value = true;
  next();
});

router.afterEach(() => {
  globalLoading.value = false;
});
</script>

<template>
  <el-config-provider :locale="zhCn">
    <div id="app" :class="currentTheme">
      <router-view v-if="!isLoading" />
      <div
        v-else
        class="loading-container"
        v-loading="true"
        element-loading-fullscreen
      ></div>
      <!-- 添加全局加载状态 -->
      <div v-if="globalLoading" class="global-loading-mask">
        <el-loading></el-loading>
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

.global-loading-mask {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background-color: rgba(255, 255, 255, 0.7);
  z-index: 9999;
  display: flex;
  justify-content: center;
  align-items: center;
}
</style>
