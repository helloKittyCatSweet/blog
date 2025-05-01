<script setup>
import { ref, defineProps, defineEmits } from "vue";
import { ElMessage } from "element-plus";
import { FontAwesomeIcon } from "@fortawesome/vue-fontawesome";
import { completeGithubRegistration } from "@/api/auth/oauth";

const props = defineProps({
  modelValue: {
    type: Boolean,
    required: true,
  },
  oauthData: {
    type: Object,
    required: true,
  },
});

const emit = defineEmits(["update:modelValue", "success"]);

const resetForm = ref({
  email: "",
  newPassword: "",
  confirmPassword: "",
});

const resetFormRef = ref();

const resetRules = {
  email: [
    { required: true, message: "请输入邮箱地址", trigger: "blur" },
    {
      validator: (rule, value, callback) => {
        const emailRegex = /^[a-zA-Z0-9._-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,6}$/;
        if (value === "") {
          callback(new Error("请输入邮箱地址"));
        } else if (!emailRegex.test(value)) {
          callback(new Error("请输入有效的邮箱地址"));
        } else {
          callback();
        }
      },
      trigger: "blur",
    },
  ],
  newPassword: [
    { required: true, message: "请输入新密码", trigger: "blur" },
    { pattern: /^\S{6,15}$/, message: "密码必须是6-15位的非空字符", trigger: "blur" },
  ],
  confirmPassword: [
    { required: true, message: "请确认新密码", trigger: "blur" },
    {
      validator: (rule, value, callback) => {
        if (value !== resetForm.value.newPassword) {
          callback(new Error("两次输入密码不一致"));
        } else {
          callback();
        }
      },
      trigger: "blur",
    },
  ],
};

const handleSetPassword = async () => {
  try {
    await resetFormRef.value.validate();

    // 添加空值检查
    if (!props.oauthData?.redisState) {
      throw new Error("授权信息无效，请重新登录");
    }

    // 调用完成注册的API，传递正确的参数结构
    const registrationData = {
      password: resetForm.value.newPassword,
      email: resetForm.value.email,
      state: props.oauthData.redisState
    };

    const response = await completeGithubRegistration(registrationData);

    if (response.data.status === 200) {
      ElMessage.success("注册成功");
      emit("success", response.data.data);
      handleClose();
    } else {
      throw new Error(response.data.message || "注册失败");
    }
  } catch (error) {
    console.error("注册失败:", error);
    ElMessage.error(error.message || "注册失败");
  }
};

const handleClose = () => {
  resetForm.value = {
    email: "",
    newPassword: "",
    confirmPassword: "",
  };
  if (resetFormRef.value) {
    resetFormRef.value.resetFields();
  }
  emit("update:modelValue", false);
};
</script>

<template>
  <el-dialog
    title="设置账户密码"
    :model-value="modelValue"
    @update:model-value="(val) => emit('update:modelValue', val)"
    width="520px"
    @close="handleClose"
    class="oauth-password-dialog"
  >
    <el-form
      ref="resetFormRef"
      :model="resetForm"
      :rules="resetRules"
      label-width="100px"
      size="large"
    >
      <el-form-item label="邮箱地址" prop="email">
        <el-input v-model="resetForm.email" placeholder="请输入邮箱地址" size="large">
          <template #prefix>
            <font-awesome-icon :icon="['fas', 'envelope']" />
          </template>
        </el-input>
      </el-form-item>
      <el-form-item label="新密码" prop="newPassword">
        <el-input
          v-model="resetForm.newPassword"
          type="password"
          show-password
          placeholder="请输入密码"
          size="large"
        />
      </el-form-item>
      <el-form-item label="确认密码" prop="confirmPassword">
        <el-input
          v-model="resetForm.confirmPassword"
          type="password"
          show-password
          placeholder="请再次输入密码"
          size="large"
        />
      </el-form-item>
    </el-form>

    <template #footer>
      <span class="dialog-footer">
        <el-button @click="handleClose" size="large">取消</el-button>
        <el-button type="primary" @click="handleSetPassword" size="large">
          确认设置
        </el-button>
      </span>
    </template>
  </el-dialog>
</template>

<style lang="scss" scoped>
.oauth-password-dialog {
  :deep(.el-dialog) {
    border-radius: 12px;
    box-shadow: 0 8px 24px rgba(0, 0, 0, 0.15);

    .el-dialog__header {
      margin: 0;
      padding: 24px 24px 12px;
      text-align: center;
      border-bottom: 1px solid #f0f0f0;

      .el-dialog__title {
        font-size: 24px;
        font-weight: 600;
        color: #303133;
      }
    }

    .el-dialog__body {
      padding: 32px 40px;
    }

    .el-form-item {
      margin-bottom: 28px;
    }

    // 修改表单项间距
    .el-form-item {
      margin-bottom: 32px; // 增加底部间距

      &:last-child {
        margin-bottom: 24px; // 最后一项底部间距稍小一些
      }

      // 为每个输入框添加内部间距
      .el-input__wrapper {
        margin: 8px 0; // 输入框上下增加间距
      }
    }
  }
}

.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 16px;
  padding: 12px 0;

  .el-button {
    min-width: 120px;
    height: 40px;
    font-weight: 500;
  }
}

:deep(.el-input) {
  .el-input__wrapper {
    padding: 0 16px;
    height: 44px;
    line-height: 44px;
    box-shadow: 0 0 0 1px #dcdfe6;
    transition: all 0.3s;

    &:hover {
      box-shadow: 0 0 0 1px #c0c4cc;
    }

    &.is-focus {
      box-shadow: 0 0 0 1px var(--el-color-primary) !important;
    }
  }

  .el-input__inner {
    height: 44px;
    line-height: 44px;
    font-size: 15px;
  }
}

:deep(.el-button) {
  transition: all 0.3s;

  &:hover {
    transform: translateY(-1px);
    box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  }

  &:active {
    transform: translateY(0);
  }
}
</style>
