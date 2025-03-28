<script setup>
import { RouterView } from "vue-router";
import zh from "element-plus/es/locale/lang/zh-cn.mjs";
import { useUserStore } from "./stores";
import { onMounted, ref } from "vue";

const isLoading = ref(true);
const userStore = useUserStore();

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
</script>

<template>
  <div></div>
  <!-- 国际化处理 -->
  <el-config-provider :locale="zh">
    <router-view />
  </el-config-provider>
</template>

<style scoped></style>
