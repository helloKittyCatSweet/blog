<script setup>
import { ref } from 'vue';
import { ElMessage } from 'element-plus';
import { CopyDocument } from '@element-plus/icons-vue';

const rssUrl = ref('https://freeshare.com/rss');

const copyRssUrl = () => {
  navigator.clipboard.writeText(rssUrl.value)
    .then(() => {
      ElMessage.success('RSS地址已复制到剪贴板');
    })
    .catch(() => {
      ElMessage.error('复制失败，请手动复制');
    });
};
</script>

<template>
  <div class="rss-container">
    <div class="rss-content">
      <h1>RSS 订阅</h1>

      <div class="description">
        <p>通过 RSS 订阅 FreeShare，及时获取最新文章更新。</p>
      </div>

      <div class="subscription-box">
        <h2>订阅方式</h2>

        <div class="rss-url-box">
          <div class="url-display">
            <span>RSS 订阅地址：</span>
            <code>{{ rssUrl }}</code>
          </div>
          <el-button
            type="primary"
            :icon="CopyDocument"
            @click="copyRssUrl"
          >
            复制地址
          </el-button>
        </div>

        <div class="instructions">
          <h3>使用说明</h3>
          <ol>
            <li>复制上方的 RSS 订阅地址</li>
            <li>打开您的 RSS 阅读器（如 Feedly、Inoreader 等）</li>
            <li>添加新的订阅源，粘贴订阅地址</li>
            <li>完成订阅，开始接收更新</li>
          </ol>
        </div>

        <div class="recommended-readers">
          <h3>推荐的 RSS 阅读器</h3>
          <ul>
            <li>
              <a href="https://feedly.com" target="_blank" rel="noopener noreferrer">
                Feedly - 网页版和移动端
              </a>
            </li>
            <li>
              <a href="https://www.inoreader.com" target="_blank" rel="noopener noreferrer">
                Inoreader - 功能强大的阅读器
              </a>
            </li>
            <li>
              <a href="https://reederapp.com" target="_blank" rel="noopener noreferrer">
                Reeder - macOS 和 iOS 平台
              </a>
            </li>
          </ul>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
.rss-container {
  width: 800px;
  margin: 0 auto;
  padding: 2rem;
  position: relative;
  z-index: 2;
}

.rss-content {
  background-color: var(--el-bg-color);
  border-radius: 8px;
  padding: 2rem;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
  position: relative;
  z-index: 2;
}

h1 {
  color: var(--el-text-color-primary);
  margin-bottom: 1.5rem;
  font-size: 2rem;
}

.description {
  color: var(--el-text-color-regular);
  font-size: 1.1rem;
  margin-bottom: 2rem;
}

.subscription-box {
  background-color: var(--el-bg-color-page);
  border-radius: 6px;
  padding: 1.5rem;
}

h2 {
  color: var(--el-text-color-primary);
  margin-bottom: 1.5rem;
  font-size: 1.5rem;
}

.rss-url-box {
  background-color: var(--el-bg-color);
  border-radius: 6px;
  padding: 1rem;
  margin-bottom: 2rem;
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 1rem;
}

.url-display {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  flex: 1;
}

code {
  background-color: var(--el-bg-color-page);
  padding: 0.3rem 0.6rem;
  border-radius: 4px;
  font-family: monospace;
  color: var(--el-text-color-primary);
}

h3 {
  color: var(--el-text-color-primary);
  margin: 1.5rem 0 1rem;
  font-size: 1.2rem;
}

.instructions ol,
.recommended-readers ul {
  padding-left: 1.5rem;
  color: var(--el-text-color-regular);
  line-height: 1.6;
}

.instructions li,
.recommended-readers li {
  margin-bottom: 0.5rem;
}

.recommended-readers a {
  color: var(--el-color-primary);
  text-decoration: none;
  transition: color 0.3s;
}

.recommended-readers a:hover {
  color: var(--el-color-primary-light-3);
}
</style>