<script setup>
import { ref, onMounted, watch, computed } from "vue";
import { ElMessage } from "element-plus";
import PageContainer from "@/components/PageContainer.vue";
import { useUserStore, useThemeStore, useSettingsStore } from "@/stores";
import { save, findByUserId } from "@/api/user/userSetting";
import { Check } from "@element-plus/icons-vue";

const userStore = useUserStore();
const themeStore = useThemeStore();
const settingsStore = useSettingsStore();

// 设置表单数据
const settingForm = ref({
  theme: settingsStore.settings.theme,
  githubAccount: settingsStore.settings.githubAccount,
  CSDNAccount: settingsStore.settings.CSDNAccount,
  BiliBiliAccount: settingsStore.settings.BiliBiliAccount,
});

const validateGithub = (rule, value, callback) => {
  if (!value) {
    callback();
    return;
  }
  if (!/^[a-zA-Z0-9](?:[a-zA-Z0-9]|-(?=[a-zA-Z0-9])){0,38}$/.test(value)) {
    callback(new Error("GitHub账号格式不正确"));
  } else {
    callback();
  }
};

const validateCSDN = (rule, value, callback) => {
  if (!value) {
    callback();
    return;
  }
  if (!/^[a-zA-Z0-9_-]{4,38}$/.test(value)) {
    callback(new Error("CSDN账号格式不正确"));
  } else {
    callback();
  }
};

const validateBilibili = (rule, value, callback) => {
  if (!value) {
    callback();
    return;
  }
  if (!/^[a-zA-Z0-9_-]{3,20}$/.test(value)) {
    callback(new Error("哔哩哔哩账号格式不正确"));
  } else {
    callback();
  }
};

const rules = {
  githubAccount: [{ validator: validateGithub, trigger: "blur" }],
  CSDNAccount: [{ validator: validateCSDN, trigger: "blur" }],
  BiliBiliAccount: [{ validator: validateBilibili, trigger: "blur" }],
};

const settingId = ref(0);
const settingFormRef = ref(null);
const loading = ref(false);

// 原始数据记录（与表单结构一致）
const originalForm = ref({
  theme: settingsStore.settings.theme,
  githubAccount: settingsStore.settings.githubAccount,
  CSDNAccount: settingsStore.settings.CSDNAccount,
  BiliBiliAccount: settingsStore.settings.BiliBiliAccount,
});

// 加载用户设置
const loadUserSettings = async () => {
  try {
    const { data } = await findByUserId(userStore.user.id);
    settingId.value = data.settingId;
    settingForm.value = {
      theme: data.theme || settingsStore.settings.theme,
      githubAccount: data.githubAccount || settingsStore.settings.githubAccount,
      CSDNAccount: data.CSDNAccount || settingsStore.settings.CSDNAccount,
      BiliBiliAccount: data.BiliBiliAccount || settingsStore.settings.BiliBiliAccount,
    };
    originalForm.value = { ...settingForm.value };
  } catch (error) {
    ElMessage.error("加载设置失败");
  }
};

// 保存设置
const handleSave = async () => {
  if (!settingFormRef.value) return;

  try {
    loading.value = true;
    await settingFormRef.value.validate();

    if (!userStore.user?.id) {
      ElMessage.warning("用户信息未加载，请稍后再试");
      return;
    }

    const response = await save({
      settingId: settingId.value,
      userId: userStore.user.id,
      theme: settingForm.value.theme,
      githubAccount: settingForm.value.githubAccount,
      CSDNAccount: settingForm.value.CSDNAccount,
      BiliBiliAccount: settingForm.value.BiliBiliAccount,
    });

    if (response.data.status === 200) {
      // 更新 settings store
      settingsStore.setSettings({
        theme: settingForm.value.theme,
        githubAccount: settingForm.value.githubAccount,
        CSDNAccount: settingForm.value.CSDNAccount,
        BiliBiliAccount: settingForm.value.BiliBiliAccount,
      });

      // 保存成功后更新原始表单数据
      originalForm.value = JSON.parse(JSON.stringify(settingForm.value));

      ElMessage.success("设置保存成功");
    } else {
      ElMessage.error("保存失败：" + response.data.message);
    }
  } catch (error) {
    if (error?.message) {
      ElMessage.error(`保存失败：${error.message}`);
    } else {
      ElMessage.error("保存失败，请稍后重试");
    }
  } finally {
    loading.value = false;
  }
};

// 主题常量
const THEMES = [
  { value: "light", label: "浅色主题", color: "#ffffff" },
  { value: "dark", label: "深色主题", color: "#1a1a1a" },
  { value: "blue", label: "蓝色主题", color: "#409EFF" },
  { value: "pink", label: "粉色主题", color: "#FF69B4" },
  { value: "green", label: "绿色主题", color: "#67C23A" },
];

// 主题切换函数
const changeTheme = (theme) => {
  // 保存到 store
  settingsStore.setTheme(theme);
  themeStore.setTheme(theme);

  // 保存到 localStorage
  localStorage.setItem('app-theme', theme);

  // 更新根元素的主题类名
  const html = document.documentElement;
  // 移除所有主题相关的类
  THEMES.forEach(t => html.classList.remove(`theme-${t.value}`));
  // 添加新主题的类
  html.classList.add(`theme-${theme}`);

  // 更新 element-plus 的主题
  if (theme === 'dark') {
    html.classList.add('dark');
  } else {
    html.classList.remove('dark');
  }
};

// 监听主题变化
watch(
  () => settingForm.value.theme,
  (newTheme) => {
    changeTheme(newTheme);
  }
);

// 在 onMounted 中初始化主题
onMounted(() => {
  // 确保用户信息已加载后再加载设置
  if (userStore.user?.id) {
    loadUserSettings();
  } else {
    ElMessage.warning("正在加载用户信息，请稍后");
  }
});

// 添加重置方法
const handleReset = () => {
  settingFormRef.value?.resetFields();
  loadUserSettings();
};

// 表单变化检测
const isFormChanged = computed(() => {
  return Object.keys(originalForm.value).some(
    (key) => settingForm.value[key] !== originalForm.value[key]
  );
});

// 添加刷新页面的方法
const handlePageRefresh = () => {
  window.location.reload();
};
</script>

<template>
  <PageContainer title="个人设置" @refresh="handlePageRefresh">
    <el-card class="setting-card">
      <el-form
        :model="settingForm"
        :rules="rules"
        ref="settingFormRef"
        label-width="120px"
      >
        <!-- 主题设置 -->
        <el-form-item label="主题设置" class="theme-setting-item">
          <!-- 主题预览卡片 -->
          <div class="theme-cards">
            <div
              v-for="theme in THEMES"
              :key="theme.value"
              class="theme-card"
              :class="{ active: settingForm.theme === theme.value }"
              @click="settingForm.theme = theme.value"
            >
              <div
                class="theme-preview"
                :style="{
                  backgroundColor: theme.color,
                  color: theme.value === 'dark' ? '#fff' : '#000',
                }"
              >
                <el-icon v-if="settingForm.theme === theme.value"><Check /></el-icon>
              </div>
              <div class="theme-label">{{ theme.label }}</div>
            </div>
          </div>

          <!-- 保留原有的单选按钮组 -->
          <el-radio-group v-model="settingForm.theme" class="theme-radio-group">
            <el-radio-button
              v-for="theme in THEMES"
              :key="theme.value"
              :value="theme.value"
            >
              <div class="theme-option">
                <div
                  class="color-preview"
                  :style="{ backgroundColor: theme.color }"
                ></div>
                {{ theme.label }}
              </div>
            </el-radio-button>
          </el-radio-group>
        </el-form-item>

        <!-- 社交账号设置 -->
        <el-divider content-position="left">社交账号绑定</el-divider>

        <el-form-item label="GitHub" for="github" prop="githubAccount">
          <el-input
            v-model="settingForm.githubAccount"
            placeholder="请输入GitHub账号"
            clearable
          >
            <template #prefix>
              <el-icon><i class="fab fa-github"></i></el-icon>
            </template>
          </el-input>
        </el-form-item>

        <el-form-item label="CSDN" for="csdn" prop="CSDNAccount">
          <el-input
            v-model="settingForm.CSDNAccount"
            placeholder="请输入CSDN账号"
            clearable
          >
            <template #prefix>
              <el-icon><i class="fas fa-code"></i></el-icon>
            </template>
          </el-input>
        </el-form-item>

        <el-form-item label="哔哩哔哩" for="bilibili" prop="BiliBiliAccount">
          <el-input
            v-model="settingForm.BiliBiliAccount"
            placeholder="请输入哔哩哔哩账号"
            clearable
          >
            <template #prefix>
              <el-icon><i class="fas fa-video"></i></el-icon>
            </template>
          </el-input>
        </el-form-item>

        <!-- 保存按钮 -->
        <el-form-item>
          <el-button
            type="primary"
            :loading="loading"
            @click="handleSave"
            :disabled="!isFormChanged || loading"
            >{{ loading ? "保存中..." : "保存设置" }}</el-button
          >
          <el-button
            type="primary"
            @click="handleReset"
            :disabled="!isFormChanged || loading"
            >重置</el-button
          >
        </el-form-item>
      </el-form>
    </el-card>
  </PageContainer>
</template>

<style lang="scss" scoped>
.setting-card {
  max-width: 1000px;
  margin: 0 auto;

  :deep(.el-form-item) {
    margin-bottom: 24px;
  }

  :deep(.el-divider) {
    margin: 32px 0;

    .el-divider__text {
      font-size: 16px;
      font-weight: 500;
    }
  }

  .el-input {
    max-width: 400px;
  }
}

.theme-cards {
  display: flex;
  gap: 16px;
  margin-bottom: 20px;
  flex-wrap: wrap; /* 允许换行 */
}

.setting-card :deep(.theme-card) {
  cursor: pointer;
  text-align: center;
  transition: all 0.3s !important;
  border-radius: 8px !important;
  overflow: hidden;
  border: 2px solid var(--el-border-color-light);
  background: var(--el-bg-color);

  &:hover {
    transform: translateY(-2px) !important;
    border-color: var(--el-color-primary) !important;
    box-shadow: 0 2px 12px 0 var(--el-color-primary-light-5) !important;
  }

  &.active {
    border-color: var(--el-color-primary) !important;
    box-shadow: 0 0 0 2px var(--el-color-primary-light-3) !important;
  }
}

.setting-card :deep(.theme-preview) {
  width: 120px;
  height: 80px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #fff;
  font-size: 20px;
  transition: all 0.3s !important;
  position: relative;
  overflow: hidden;

  &::after {
    content: '';
    position: absolute;
    top: 0;
    left: 0;
    right: 0;
    bottom: 0;
    background-color: var(--el-color-primary-light-8) !important;
    opacity: 0;
    transition: opacity 0.3s !important;
  }

  &:hover::after {
    opacity: 1 !important;
  }
}

.setting-card :deep(.theme-label) {
  padding: 8px;
  font-size: 14px;
  background: var(--el-bg-color);
  color: var(--el-text-color-primary);
  border-top: 1px solid var(--el-border-color-lighter);
}

.setting-card :deep(.theme-radio-group) {
  margin-top: 16px;
  display: block;

  .el-radio-button__inner {
    display: flex !important;
    align-items: center !important;
    gap: 8px !important;
    padding: 8px 16px !important;
    transition: all 0.3s !important;

    &:hover {
      color: var(--el-color-primary) !important;
      background-color: var(--el-color-primary-light-9) !important;
      border-color: var(--el-color-primary) !important;
    }
  }

  .el-radio-button__orig-radio:checked + .el-radio-button__inner {
    color: #fff !important;
    background-color: var(--el-color-primary) !important;
    border-color: var(--el-color-primary) !important;
    box-shadow: -1px 0 0 0 var(--el-color-primary) !important;
  }
}

.theme-setting-item {
  :deep(.el-form-item__label) {
    height: 12px;
    line-height: 100px;
    padding-top: 0;
  }
}

.theme-option {
  display: flex;
  align-items: center;
  gap: 8px;

  .color-preview {
    width: 16px;
    height: 16px;
    border-radius: 4px;
    border: 1px solid var(--el-border-color-light);
    transition: all 0.3s !important;

    &:hover {
      border-color: var(--el-color-primary) !important;
      box-shadow: 0 0 0 1px var(--el-color-primary-light-5) !important;
    }
  }
}
</style>
