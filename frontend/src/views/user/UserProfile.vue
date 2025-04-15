<script setup>
import { useUserStore } from "@/stores";
import { ref, onMounted } from "vue";
import { ElMessage } from "element-plus";
import PageContainer from "@/components/PageContainer.vue";
import {
  update,
  findUserById,
  uploadAvatar,
  getSignature,
  uploadSignature,
  generateDefaultSignature,
} from "@/api/user/user.js";
import { Clock, Timer } from "@element-plus/icons-vue";
import { GENDER_OPTIONS } from "@/constants/user-constants";
import { Upload } from "@element-plus/icons-vue";
import ChangeEmailDialog from "@/components/ChangeEmailDialog.vue";

const userStore = useUserStore();
const {
  user: { username, nickname, email, id, address, bio, gender, phone, avatar, birthday },
} = userStore;

const userInfo = ref({
  userId: userStore.user.id,
  username: username || "",
  nickname: nickname || "",
  email: email || "",
  avatar: avatar || null,
  gender: gender || 2, // 保密
  birthday: birthday || "",
  phone: phone || "",
  address: address || "",
  introduction: bio || "",
  lastLoginTime: "",
  tags: [], // 用户标签
  signature: "", // 签名字段
});

/**
 * 处理标签
 */
const tagInputValue = ref("");

const handleTagClose = (tag) => {
  userInfo.value.tags = userInfo.value.tags.filter((item) => item !== tag);
};

const handleTagInput = () => {
  const value = tagInputValue.value?.trim(); // 添加 trim 处理
  if (!value) return; // 空值直接返回

  if (!userInfo.value.tags?.includes(value)) {
    if ((userInfo.value.tags?.length || 0) >= 5) {
      ElMessage.warning("最多添加5个标签");
      return;
    }
    // 确保 tags 是数组
    if (!Array.isArray(userInfo.value.tags)) {
      userInfo.value.tags = [];
    }
    userInfo.value.tags.push(value);
    tagInputValue.value = "";
  }
};

/**
 * 加载用户数据
 */
const loadUserInfo = async () => {
  try {
    const { data } = await findUserById(userStore.user.id);
    if (data.status === 200) {
      // 确保 tags 是数组
      const userData = {
        ...data.data,
        tags: data.data.tags || [],
      };
      Object.assign(userInfo.value, userData);

      // 加载签名
      try {
        const signatureResponse = await getSignature(userStore.user.id);
        if (signatureResponse.data.status === 200) {
          userInfo.value.signature = signatureResponse.data.data;
        }
      } catch (error) {
        console.error("获取签名失败:", error);
      }
      console.log("当前用户信息：", userInfo.value); // 添加这行来查看数据
    }
  } catch (error) {
    ElMessage.error("加载用户数据失败");
  }
};

onMounted(() => {
  loadUserInfo();
});

const rules = {
  nickname: [
    { pattern: /^\S{2,10}$/, message: "昵称必须是2-10位的非空字符串", trigger: "blur" },
  ],
  email: [{ type: "email", message: "邮箱格式不正确", trigger: "blur" }],
  phone: [{ pattern: /^1[3-9]\d{9}$/, message: "手机号格式不正确", trigger: "blur" }],
};

/**
 * 修改用户信息
 */
const formRef = ref();
const onSubmit = async () => {
  const valid = await formRef.value.validate();
  if (valid) {
    try {
      console.log(userInfo.value);
      await update(userInfo.value);
      loadUserInfo();
      ElMessage.success("修改成功");
    } catch (error) {
      ElMessage.error("修改失败");
    }
  }
};

/**
 * 头像上传
 */
const handleAvatarClick = () => {
  const input = document.createElement("input");
  input.type = "file";
  input.accept = "image/jpeg,image/png,image/jpg";
  input.onchange = async (e) => {
    const file = e.target.files[0];
    if (file) {
      if (file.size > 5 * 1024 * 1024) {
        ElMessage.warning("文件大小不能超过5MB");
        return;
      }
      try {
        const formData = new FormData();
        formData.append("file", file); // Make sure the name is "file"
        formData.append("userId", userStore.user.id);
        const { data } = await uploadAvatar(formData); // Just pass the formData
        if (data.status === 200) {
          await loadUserInfo();
          ElMessage.success("头像更新成功");
        }
      } catch (error) {
        console.error(error);
        ElMessage.error("头像上传失败");
      }
    }
  };
  input.click();
};

/**
 * 更换邮箱
 */
const showEmailDialog = ref(false);

const handleEmailChange = async (newEmail) => {
  try {
    userInfo.value.email = newEmail;
    await update(userInfo.value);
    await loadUserInfo();
    ElMessage.success("邮箱更新成功");
  } catch (error) {
    ElMessage.error("邮箱更新失败");
  }
};

/**
 * 签名上传
 */
const handleSignatureClick = () => {
  const input = document.createElement("input");
  input.type = "file";
  input.accept = "image/jpeg,image/png,image/jpg";
  input.onchange = async (e) => {
    const file = e.target.files[0];
    if (file) {
      if (file.size > 2 * 1024 * 1024) {
        ElMessage.warning("签名图片大小不能超过2MB");
        return;
      }
      try {
        const formData = new FormData();
        formData.append("file", file);
        formData.append("userId", userStore.user.id);
        const { data } = await uploadSignature(formData);
        if (data.status === 200) {
          // 重新获取签名
          const signatureResponse = await getSignature(userStore.user.id);
          if (signatureResponse.data.status === 200) {
            // 添加时间戳以避免缓存
            userInfo.value.signature = `${
              signatureResponse.data.data
            }?t=${new Date().getTime()}`;
          }
          ElMessage.success("签名更新成功");
        }
      } catch (error) {
        console.error(error);
        ElMessage.error("签名上传失败");
      }
    }
  };
  input.click();
};

// 添加生成默认签名的方法
const generateSignature = async () => {
  try {
    const { data } = await generateDefaultSignature(userStore.user.id);
    if (data.status === 200) {
      // 重新获取签名并添加时间戳
      const signatureResponse = await getSignature(userStore.user.id);
      if (signatureResponse.data.status === 200) {
        userInfo.value.signature = `${
          signatureResponse.data.data
        }?t=${new Date().getTime()}`;
      }
      ElMessage.success("默认签名生成成功");
    }
  } catch (error) {
    console.error(error);
    ElMessage.error("默认签名生成失败");
  }
};
</script>

<template>
  <page-container title="个人资料">
    <el-row :gutter="40">
      <el-col :span="16">
        <el-card class="form-card" shadow="never">
          <el-form
            :model="userInfo"
            :rules="rules"
            ref="formRef"
            label-width="120px"
            size="large"
          >
            <el-form-item label="登录名称">
              <el-input v-model="userInfo.username" disabled></el-input>
            </el-form-item>
            <el-form-item label="用户昵称" prop="nickname">
              <el-input v-model="userInfo.nickname" placeholder="请输入昵称"></el-input>
            </el-form-item>
            <el-form-item label="性别" prop="gender">
              <el-radio-group v-model="userInfo.gender">
                <el-radio
                  v-for="option in GENDER_OPTIONS"
                  :key="option.value"
                  :value="option.value"
                >
                  {{ option.label }}
                </el-radio>
              </el-radio-group>
            </el-form-item>
            <el-form-item label="出生日期" prop="birthday">
              <el-date-picker
                v-model="userInfo.birthday"
                type="date"
                placeholder="选择出生日期"
                style="width: 100%"
              />
            </el-form-item>
            <el-form-item label="联系电话" prop="phone">
              <el-input v-model="userInfo.phone" placeholder="请输入手机号"></el-input>
            </el-form-item>
            <el-form-item label="电子邮箱" prop="email">
              <div style="display: flex; gap: 10px">
                <el-input
                  v-model="userInfo.email"
                  placeholder="请输入邮箱"
                  disabled
                ></el-input>
                <el-button type="primary" @click="showEmailDialog = true"
                  >更换邮箱</el-button
                >
              </div>
            </el-form-item>

            <el-form-item label="居住地址" prop="address">
              <el-input v-model="userInfo.address" placeholder="请输入地址"></el-input>
            </el-form-item>
            <el-form-item label="个人简介" prop="introduction">
              <el-input
                v-model="userInfo.introduction"
                type="textarea"
                :rows="4"
                placeholder="介绍一下自己吧"
              ></el-input>
            </el-form-item>
            <el-form-item label="个人标签">
              <div style="display: flex; flex-wrap: wrap; gap: 8px">
                <el-tag
                  v-for="tag in userInfo.tags || []"
                  :key="tag"
                  closable
                  @close="handleTagClose(tag)"
                >
                  {{ tag }}
                </el-tag>
                <el-input
                  v-if="(userInfo.tags || []).length < 5"
                  v-model="tagInputValue"
                  placeholder="输入标签按回车确认"
                  @keyup.enter="handleTagInput"
                  style="width: 180px"
                />
              </div>
            </el-form-item>
            <el-form-item>
              <el-button type="primary" @click="onSubmit">保存修改</el-button>
              <el-button @click="formRef.resetFields()">重置</el-button>
            </el-form-item>
          </el-form>
          <!-- 添加弹窗组件 -->
          <ChangeEmailDialog
            v-model="showEmailDialog"
            :current-email="userInfo.email"
            @success="handleEmailChange"
          />
        </el-card>
      </el-col>
      <el-col :span="8">
        <el-card class="user-info-card" shadow="never">
          <div class="user-info-header">
            <div class="avatar-container" @click="handleAvatarClick">
              <el-avatar
                :size="120"
                :src="userInfo.avatar || ''"
                class="avatar"
                fit="cover"
              >
                {{ userInfo.nickname?.charAt(0) || userInfo.username?.charAt(0) }}
              </el-avatar>
              <div class="avatar-hover">
                <el-icon><Upload /></el-icon>
                <span>更换头像</span>
              </div>
            </div>
            <h3 class="username">{{ userInfo.nickname || userInfo.username }}</h3>
          </div>
          <div class="user-info-content">
            <div class="info-item">
              <el-icon><Clock /></el-icon>
              <span class="label">注册时间</span>
              <span class="value">{{ userInfo.createdAt || "-" }}</span>
            </div>
            <div class="info-item">
              <el-icon><Timer /></el-icon>
              <span class="label">上次登录</span>
              <span class="value">{{ userInfo.lastLoginTime || "-" }}</span>
            </div>
            <div class="signature-container">
              <h4 class="signature-title">个人签名</h4>
              <div class="signature-content">
                <template v-if="userInfo.signature">
                  <img
                    :src="userInfo.signature"
                    alt="个人签名"
                    class="signature-image"
                    @click="handleSignatureClick"
                  />
                  <div class="signature-actions">
                    <el-button type="primary" link @click="handleSignatureClick"
                      >更换签名</el-button
                    >
                    <el-button type="primary" link @click="generateSignature"
                      >重新生成</el-button
                    >
                  </div>
                </template>
                <div v-else class="signature-placeholder">
                  <el-button type="primary" @click="handleSignatureClick"
                    >上传签名</el-button
                  >
                  <el-button @click="generateSignature">使用默认签名</el-button>
                </div>
              </div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>
  </page-container>
</template>

<style lang="scss" scoped>
.form-card {
  margin-bottom: 20px;
  background-color: var(--el-bg-color);
  border: none;

  :deep(.el-form-item__label) {
    font-weight: 500;
  }
}

.user-info-card {
  background-color: var(--el-bg-color);
  border: none;

  .user-info-header {
    display: flex;
    flex-direction: column;
    align-items: center;
    padding: 30px 20px;
    margin: -20px -20px 0; // 补偿card的padding
    border-bottom: 1px solid var(--el-border-color-lighter);
    background-color: var(--el-color-primary-light-9);
    border-radius: var(--el-card-border-radius) var(--el-card-border-radius) 0 0;

    .username {
      margin-top: 16px;
      font-size: 18px;
      font-weight: 600;
      color: var(--el-text-color-primary);
    }
  }

  .user-info-content {
    padding: 20px;

    .info-item {
      display: flex;
      align-items: center;
      margin-bottom: 16px;
      font-size: 14px;

      .el-icon {
        margin-right: 8px;
        font-size: 16px;
        color: var(--el-text-color-secondary);
      }

      .label {
        color: var(--el-text-color-secondary);
        margin-right: 8px;
      }

      .value {
        color: var(--el-text-color-primary);
        flex: 1;
        text-align: right;
      }
    }
  }

  .avatar-container {
    position: relative;
    cursor: pointer;

    .avatar {
      border: 4px solid white;
      box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
      background-color: var(--el-color-primary-light-9);
      color: var(--el-text-color-secondary);
      font-size: 48px;
    }

    .avatar-hover {
      position: absolute;
      top: 0;
      left: 50%;
      transform: translateX(-50%);
      width: 120px;
      height: 120px;
      border-radius: 50%;
      background: rgba(0, 0, 0, 0.5);
      display: flex;
      flex-direction: column;
      justify-content: center;
      align-items: center;
      color: white;
      opacity: 0;
      transition: opacity 0.3s;

      .el-icon {
        font-size: 24px;
        margin-bottom: 8px;
      }
    }

    &:hover .avatar-hover {
      opacity: 1;
    }
  }
}

.el-tag {
  margin-right: 8px;
  margin-bottom: 8px;
}

.tag-container {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  align-items: center;
}

.tag-input {
  width: 240px;
  margin-left: 8px;
  vertical-align: bottom;
}

.signature-container {
  margin-top: 20px;
  cursor: pointer;

  .signature-title {
    font-size: 14px;
    color: var(--el-text-color-secondary);
    margin-bottom: 10px;
  }

  .signature-content {
    border: 1px dashed var(--el-border-color);
    border-radius: 4px;
    padding: 10px;
    min-height: 60px;
    display: flex;
    align-items: center;
    justify-content: center;
    transition: all 0.3s;

    &:hover {
      border-color: var(--el-color-primary);
      background-color: var(--el-color-primary-light-9);
    }
  }

  .signature-image {
    max-width: 100%;
    max-height: 100px;
    object-fit: contain;
  }

  .signature-placeholder {
    display: flex;
    flex-direction: column;
    align-items: center;
    color: var(--el-text-color-secondary);

    .el-icon {
      font-size: 24px;
      margin-bottom: 8px;
    }
  }
}

.signature-content {
  position: relative;

  .signature-actions {
    display: none;
    position: absolute;
    bottom: 0;
    left: 0;
    right: 0;
    background: rgba(255, 255, 255, 0.9);
    padding: 8px;
    text-align: center;
  }

  &:hover .signature-actions {
    display: flex;
    justify-content: center;
    gap: 16px;
  }
}

.signature-placeholder {
  display: flex;
  gap: 16px;
  justify-content: center;
  align-items: center;
}
</style>
