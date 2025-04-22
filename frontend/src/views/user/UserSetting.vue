<script setup>
import { ref, onMounted, watch, computed, nextTick } from "vue";
import { ElMessage } from "element-plus";
import PageContainer from "@/components/PageContainer.vue";
import { useUserStore, useThemeStore, useSettingsStore } from "@/stores";
import { save, findByUserId } from "@/api/user/userSetting";
import { Check } from "@element-plus/icons-vue";
import { useRouter } from "vue-router";

const router = useRouter();

const userStore = useUserStore();
const themeStore = useThemeStore();
const settingsStore = useSettingsStore();

// 设置表单数据
const settingForm = ref({
  theme: "light",
  notifications: true,
  githubAccount: "",
  csdnAccount: "",
  bilibiliAccount: "",
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
  csdnAccount: [{ validator: validateCSDN, trigger: "blur" }],
  bilibiliAccount: [{ validator: validateBilibili, trigger: "blur" }],
};

const settingId = ref(0);
const settingFormRef = ref(null);
const loading = ref(false);

// 加载用户设置
const loadUserSettings = async () => {
  try {
    const { data } = await findByUserId(userStore.user.id);
    settingId.value = data.settingId;
    settingForm.value = {
      theme: data.theme || "light",
      notifications: data.notifications ?? true,
      githubAccount: data.githubAccount || "",
      csdnAccount: data.CSDNAccount || "",
      bilibiliAccount: data.BiliBiliAccount || "",
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

    await save({
      settingId: settingId.value,
      userId: userStore.user.id,
      theme: settingForm.value.theme,
      notifications: settingForm.value.notifications,
      githubAccount: settingForm.value.githubAccount,
      CSDNAccount: settingForm.value.csdnAccount,
      BiliBiliAccount: settingForm.value.bilibiliAccount,
    });

    // 保存成功后立即应用主题
    changeTheme(settingForm.value.theme);
    // 保存成功后更新原始表单数据
    originalForm.value = JSON.parse(JSON.stringify(settingForm.value));

    ElMessage.success("设置保存成功");
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
  settingsStore.setTheme(theme);
  themeStore.setTheme(theme);
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
  // 从 store 获取当前主题
  settingForm.value.theme = themeStore.currentTheme;

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

// 原始数据记录（与表单结构一致）
const originalForm = ref({
  theme: "light",
  notifications: true,
  githubAccount: "",
  csdnAccount: "",
  bilibiliAccount: "",
});

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

        <!-- 通知设置 -->
        <el-form-item label="消息通知" for="notifications">
          <el-switch
            v-model="settingForm.notifications"
            active-text="开启"
            inactive-text="关闭"
          />
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

        <el-form-item label="CSDN" for="csdn" prop="csdnAccount">
          <el-input
            v-model="settingForm.csdnAccount"
            placeholder="请输入CSDN账号"
            clearable
          >
            <template #prefix>
              <el-icon><i class="fas fa-code"></i></el-icon>
            </template>
          </el-input>
        </el-form-item>

        <el-form-item label="哔哩哔哩" for="bilibili" prop="bilibiliAccount">
          <el-input
            v-model="settingForm.bilibiliAccount"
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

.theme-option {
  display: flex;
  align-items: center;
  gap: 8px;

  .color-preview {
    width: 16px;
    height: 16px;
    border-radius: 4px;
    border: 1px solid #dcdfe6;
  }
}

.theme-cards {
  display: flex;
  gap: 16px;
  margin-bottom: 20px;
  flex-wrap: wrap; /* 允许换行 */
}

.theme-card {
  cursor: pointer;
  text-align: center;
  transition: all 0.3s;
  border-radius: 8px;
  overflow: hidden;
  border: 2px solid var(--el-border-color-light);
  background: var(--el-bg-color);

  &:hover {
    transform: translateY(-2px);
    box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
  }

  &.active {
    border-color: var(--el-color-primary);
  }
}

.theme-preview {
  width: 120px;
  height: 80px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #fff;
  font-size: 20px;
  transition: all 0.3s;
}

.theme-label {
  padding: 8px;
  font-size: 14px;
  background: var(--el-bg-color);
}

.theme-radio-group {
  margin-top: 16px;
  display: block;
}

.theme-setting-item {
  :deep(.el-form-item__label) {
    height: 12px;
    line-height: 100px;
    padding-top: 0;
  }
}
</style>
