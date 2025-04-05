<template>
  <div class="blog-layout">
    <header class="blog-header">
      <div class="header-content">
        <div class="logo">
          <router-link to="/">
            <h1>FreeShare</h1>
          </router-link>
        </div>
        <nav class="nav-menu">
          <router-link to="/">首页</router-link>
          <router-link to="/posts">文章</router-link>
          <router-link to="/categories">分类</router-link>
          <router-link to="/tags">标签</router-link>
          <router-link to="/about">关于</router-link>
        </nav>
        <div class="header-actions">
          <el-input
            v-model="searchKey"
            placeholder="搜索文章..."
            prefix-icon="Search"
            @keyup.enter="handleSearch"
          />
          <el-button type="primary" @click="goToLogin">登录</el-button>
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

    <footer class="blog-footer">
      <div class="footer-content">
        <div class="footer-info">
          <p>© 2024 Blog Name. All rights reserved.</p>
        </div>
        <div class="footer-links">
          <a href="#">GitHub</a>
          <a href="#">RSS</a>
          <a href="#">联系我</a>
        </div>
      </div>
    </footer>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { Search } from '@element-plus/icons-vue'

const router = useRouter()
const searchKey = ref('')

const handleSearch = () => {
  if (searchKey.value.trim()) {
    router.push({
      path: '/posts',
      query: { search: searchKey.value.trim() }
    })
  }
}

const goToLogin = () => {
  router.push('/login')
}
</script>

<style scoped>
.blog-layout {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
}

.blog-header {
  background-color: var(--el-bg-color);
  box-shadow: 0 2px 8px rgba(0,0,0,0.1);
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
</style>