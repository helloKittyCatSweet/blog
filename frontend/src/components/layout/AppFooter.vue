<template>
  <footer class="app-footer">
    <div class="footer-content">
      <div class="footer-info">
        <p>FreeShare ©2025 Created by helloKittyCatSweet</p>
      </div>
      <div class="footer-links">
        <a href="https://github.com/helloKittyCatSweet" target="_blank">GitHub</a>
        <a href="/rss" target="_blank">RSS</a>
        <a href="/contact">联系我</a>
      </div>
    </div>
  </footer>
</template>

<style scoped>
.app-footer {
  background-color: var(--el-bg-color-page);
  padding: 1.5rem 0;
  margin-top: auto;
  position: relative;  /* 添加相对定位 */
  z-index: 1000;      /* 添加z-index确保在背景之上 */
  box-shadow: 0 -2px 12px rgba(0, 0, 0, 0.1);  /* 添加阴影效果 */
}

.footer-content {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 1rem;
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-size: 14px;
  color: var(--el-text-color-secondary);
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
</style>
