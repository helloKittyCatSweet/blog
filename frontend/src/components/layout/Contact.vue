<script setup>
import { ref } from 'vue';
import { ElMessage } from 'element-plus';
import { Message, Link } from '@element-plus/icons-vue';
import { sendContactMessage } from '@/api/user/contact'

const form = ref({
  name: '',
  email: '',
  subject: '',
  message: ''
});

const rules = {
  name: [{ required: true, message: '请输入您的姓名', trigger: 'blur' }],
  email: [
    { required: true, message: '请输入您的邮箱', trigger: 'blur' },
    { type: 'email', message: '请输入正确的邮箱地址', trigger: 'blur' }
  ],
  subject: [{ required: true, message: '请输入主题', trigger: 'blur' }],
  message: [{ required: true, message: '请输入消息内容', trigger: 'blur' }]
};

const formRef = ref(null);
const loading = ref(false);

const submitForm = async () => {
  if (!formRef.value) return;

  try {
    // 1. 表单验证
    await formRef.value.validate();
    loading.value = true;

    // 2. 发送请求
    const response = await sendContactMessage(form.value);

    // 3. 处理响应
    if (response.data && response.data.status === 200) {
      ElMessage.success('消息已发送，我们会尽快回复您！');
      formRef.value.resetFields();
    } else {
      const errorMsg = response.data?.message || '发送失败，请稍后重试';
      ElMessage.error(errorMsg);
    }
  } catch (error) {
    console.error('发送消息失败:', error);
    // 4. 错误分类处理
    if (error.code === 'ECONNABORTED') {
      ElMessage.error('请求超时，请稍后重试');
    } else if (error.response) {
      ElMessage.error(error.response.data?.message || '服务器响应错误');
    } else if (error.request) {
      ElMessage.error('网络连接失败，请检查网络设置');
    } else {
      ElMessage.error('发送失败，请稍后重试');
    }
  } finally {
    loading.value = false;
  }
};
</script>

<template>
  <div class="contact-container">
    <div class="contact-content">
      <h1>联系我</h1>

      <div class="description">
        <p>如果您有任何问题、建议或合作意向，请随时与我联系。</p>
      </div>

      <div class="contact-box">
        <div class="contact-form">
          <h2>发送消息</h2>
          <el-form
            ref="formRef"
            :model="form"
            :rules="rules"
            label-position="top"
          >
            <el-form-item label="姓名" prop="name">
              <el-input v-model="form.name" placeholder="请输入您的姓名" />
            </el-form-item>

            <el-form-item label="邮箱" prop="email">
              <el-input v-model="form.email" placeholder="请输入您的邮箱" />
            </el-form-item>

            <el-form-item label="主题" prop="subject">
              <el-input v-model="form.subject" placeholder="请输入消息主题" />
            </el-form-item>

            <el-form-item label="消息" prop="message">
              <el-input
                v-model="form.message"
                type="textarea"
                :rows="4"
                placeholder="请输入消息内容"
              />
            </el-form-item>

            <el-form-item>
              <div class="button-container">
                <el-button
                  type="primary"
                  :loading="loading"
                  @click="submitForm"
                  class="submit-button"
                >
                  发送消息
                </el-button>
              </div>
            </el-form-item>
          </el-form>
        </div>

        <div class="contact-info">
          <h2>其他联系方式</h2>

          <div class="info-list">
            <div class="info-item">
              <el-icon><Message /></el-icon>
              <span>邮箱：contact@freeshare.com</span>
            </div>

            <div class="social-links">
              <h3>社交媒体</h3>
              <div class="links-list">
                <a href="https://github.com/freeshare" target="_blank" rel="noopener noreferrer">
                  <el-button :icon="Link">GitHub</el-button>
                </a>
                <a href="https://twitter.com/freeshare" target="_blank" rel="noopener noreferrer">
                  <el-button :icon="Link">Twitter</el-button>
                </a>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
.contact-container {
  max-width: 1000px;
  margin: 0 auto;
  padding: 2rem;
  position: relative;  /* 添加相对定位 */
  z-index: 1000;      /* 添加z-index确保在背景之上 */
}

.contact-content {
  background-color: var(--el-bg-color);
  border-radius: 8px;
  padding: 2rem;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
  position: relative;  /* 添加相对定位 */
  z-index: 1;         /* 确保内容在背景之上 */
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

.contact-box {
  display: grid;
  grid-template-columns: 3fr 2fr;
  gap: 2rem;
}

@media (max-width: 768px) {
  .contact-box {
    grid-template-columns: 1fr;
  }
}

.contact-form,
.contact-info {
  background-color: var(--el-bg-color-page);
  border-radius: 6px;
  padding: 1.5rem;
  position: relative;  /* 添加相对定位 */
  z-index: 2;         /* 确保表单和信息在背景之上 */
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);  /* 添加轻微阴影 */
}

h2 {
  color: var(--el-text-color-primary);
  margin-bottom: 1.5rem;
  font-size: 1.5rem;
}

.info-list {
  display: flex;
  flex-direction: column;
  gap: 1.5rem;
}

.info-item {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  color: var(--el-text-color-regular);
}

.social-links {
  margin-top: 1.5rem;
}

h3 {
  color: var(--el-text-color-primary);
  margin-bottom: 1rem;
  font-size: 1.2rem;
}

.links-list {
  display: flex;
  gap: 1rem;
  flex-wrap: wrap;
}

.el-button {
  width: 120px;
}

/* 发送按钮容器样式 */
.button-container {
  display: flex;
  justify-content: center;
  width: 100%;
}

/* 发送按钮样式 */
.submit-button {
  background: #409eff !important;  /* 使用Element Plus的主要蓝色 */
  color: white !important;
  border: none !important;
  font-weight: 500;
  transition: all 0.3s ease;
  min-width: 120px;  /* 确保按钮有最小宽度 */
}

.submit-button:hover {
  background: #66b1ff !important;  /* 更亮的蓝色 */
  transform: translateY(-2px);
}

.submit-button:active {
  background: #3a8ee6 !important;  /* 稍深的蓝色 */
  transform: translateY(0);
}

.submit-button.is-loading {
  background: #409eff !important;
  opacity: 0.9;
}
</style>