<script setup>
import { ref } from "vue";
import { useRouter, useRoute } from "vue-router";
import { useUserStore } from "@/stores/modules/user.js";
import { Search } from "@element-plus/icons-vue";
import { ElMessageBox } from "element-plus";
import AppFooter from "@/components/layout/AppFooter.vue";
import UserDropdown from "@/components/user/UserDropdown.vue";
import { USER_PROFILE_PATH, USER_PASSWORD_PATH } from "@/constants/routes/user.js";
import { LOGIN_PATH, CONTROL_PANEL_PATH } from "@/constants/routes/base.js";
import SearchBar from "@/components/blog/search/SearchBar.vue";
import FavoriteDropdown from "@/components/blog/favorite/FavoriteDropdown.vue";

const router = useRouter();
const route = useRoute();

const userStore = useUserStore();

const goToLogin = () => {
  // 将当前路由信息保存到 query 参数中
  router.push({
    path: LOGIN_PATH,
    query: {
      redirect: route.fullPath,
    },
  });
};

// 用户下拉菜单处理函数
const handleUserCommand = async (command) => {
  switch (command) {
    case "profile":
      router.push(USER_PROFILE_PATH);
      break;
    case "password":
      router.push(USER_PASSWORD_PATH);
      break;
    case "logout":
      try {
        await ElMessageBox.confirm("确认要退出登录吗？", "提示", {
          confirmButtonText: "确认",
          cancelButtonText: "取消",
          type: "warning",
        });
        userStore.removeToken();
        userStore.setUser({});
        router.push("/login");
      } catch {
        // 用户取消操作
      }
      break;
  }
};
</script>

<template>
  <div class="blog-layout">
    <header class="blog-header">
      <div class="header-content">
        <div class="logo">
          <router-link to="/">
            <img src="@/assets/logo.png" alt="FreeShare" class="logo-image" />
          </router-link>
        </div>
        <nav class="nav-menu">
          <router-link to="/" :class="{ active: route.path === '/' }">首页</router-link>
          <router-link to="/posts" :class="{ active: route.path.startsWith('/posts') }"
            >文章</router-link
          >
          <router-link
            to="/categories"
            :class="{ active: route.path.startsWith('/categories') }"
            >分类</router-link
          >
          <router-link to="/tags" :class="{ active: route.path.startsWith('/tags') }"
            >标签</router-link
          >
          <FavoriteDropdown v-if="userStore.isLoggedIn"/>
          <router-link to="/about" :class="{ active: route.path.startsWith('/about') }"
            >关于</router-link
          >
          <router-link
            :to="CONTROL_PANEL_PATH"
            :class="{ active: route.path.startsWith('/control') }"
            v-if="userStore.isLoggedIn"
            >控制台</router-link
          >
        </nav>
        <div class="header-actions">
          <SearchBar class="search-bar" />
          <template v-if="userStore.isLoggedIn">
            <UserDropdown @command="handleUserCommand" />
          </template>
          <template v-else>
            <el-button type="primary" @click="goToLogin">登录</el-button>
          </template>
        </div>
      </div>
    </header>

    <main class="blog-main">
      <router-view v-slot="{ Component }">
        <transition name="fade" mode="out-in">
          <component :is="Component" />
        </transition>
      </router-view>
    </main>

    <AppFooter />
  </div>
</template>

<style scoped>
.blog-layout {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
}

.blog-header {
  background-color: var(--el-bg-color);
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  position: sticky;
  top: 0;
  z-index: 100;
}

.header-content {
  max-width: 1200px;
  margin: 0 auto;
  padding: 1rem;
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.logo h1 {
  margin: 0;
  font-size: 1.5rem;
  color: var(--el-text-color-primary);
}

.nav-menu {
  display: flex;
  gap: 2rem;
}

.nav-menu a {
  color: var(--el-text-color-regular);
  text-decoration: none;
  font-size: 1rem;
  transition: color 0.3s;
}

.nav-menu a:hover,
.nav-menu a.router-link-active {
  color: var(--el-color-primary);
}

.header-actions {
  display: flex;
  gap: 1rem;
  align-items: center;
}

.blog-main {
  flex: 1;
  max-width: 1200px;
  margin: 2rem auto;
  padding: 0 1rem;
}

.blog-footer {
  background-color: var(--el-bg-color-page);
  padding: 2rem 0;
  margin-top: 4rem;
}

.footer-content {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 1rem;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.footer-links {
  display: flex;
  gap: 1.5rem;
}

.footer-links a {
  color: var(--el-text-color-secondary);
  text-decoration: none;
  transition: color 0.3s;
}

.footer-links a:hover {
  color: var(--el-color-primary);
}

.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.3s ease;
}

.fade-enter-from,
.fade-leave-to {
  opacity: 0;
}

.logo-link {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  text-decoration: none;
}
.logo {
  display: flex;
  align-items: center;
}

.logo-image {
  width: 200px; /* 调整为更大的尺寸 */
  height: 40px;
  object-fit: contain; /* 确保图片不会被压缩变形 */
}
.logo h1 {
  margin: 0;
  font-size: 1.5rem;
  color: var(--el-text-color-primary);
}

.nav-menu a.active {
  color: var(--el-color-primary);
  font-weight: 500;
}

.nav-menu a:hover {
  color: var(--el-color-primary-light-3);
}

.header-actions .search-bar {
  width: 300px;
}
</style>
