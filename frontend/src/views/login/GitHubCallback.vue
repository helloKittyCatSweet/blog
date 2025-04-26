<script setup>
import { onMounted } from 'vue';
import { ElMessage } from 'element-plus';
import { Loading } from '@element-plus/icons-vue';

onMounted(async () => {
  // 获取授权码
  const code = new URLSearchParams(window.location.search).get('code');
  // 获取状态码（防止CSRF攻击）
  const state = new URLSearchParams(window.location.search).get('state');

  if (code && state) {
    try {
      // 向父窗口发送授权码
      window.opener.postMessage({
        type: 'github-oauth-callback',
        code: code,
        state: state
      }, window.location.origin);

      ElMessage.success('授权成功，正在关闭窗口...');
      // 短暂延迟后关闭窗口，让用户看到成功提示
      setTimeout(() => window.close(), 1000);
    } catch (error) {
      console.error('处理 GitHub 回调失败:', error);
      ElMessage.error('GitHub 授权失败');
      setTimeout(() => window.close(), 2000);
    }
  } else if (new URLSearchParams(window.location.search).get('error')) {
    // 用户拒绝授权的情况
    ElMessage.error('您已拒绝授权');
    setTimeout(() => window.close(), 2000);
  }
});
</script>

<template>
  <div class="github-callback">
    <el-card class="callback-card">
      <template #header>
        <div class="card-header">
          <h2>GitHub 授权</h2>
        </div>
      </template>
      <div class="card-content">
        <el-icon class="is-loading"><Loading /></el-icon>
        <p>正在处理 GitHub 授权结果，请稍候...</p>
      </div>
    </el-card>
  </div>
</template>

<style scoped>
.github-callback {
  height: 100vh;
  display: flex;
  justify-content: center;
  align-items: center;
  background-color: #f5f5f5;
}

.callback-card {
  width: 400px;
  text-align: center;
  border-radius: 8px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
}

.card-header {
  background-color: #f8f9fa;
}

.card-content {
  padding: 30px 20px;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 20px;
}

.is-loading {
  font-size: 24px;
  color: var(--el-color-primary);
  animation: rotate 1s linear infinite;
}

@keyframes rotate {
  from { transform: rotate(0deg); }
  to { transform: rotate(360deg); }
}
</style>