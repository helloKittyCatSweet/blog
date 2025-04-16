<script setup>
import { ref, defineProps, defineEmits, computed, onMounted, onUnmounted } from "vue";
import { ElMessage } from "element-plus";
import { FontAwesomeIcon } from "@fortawesome/vue-fontawesome";
import { verify, send } from "@/api/user/security/emailVerification";
import { resetPassword, existsByEmail, findUserByEmail } from "@/api/user/user";

const props = defineProps({
  modelValue: {
    type: Boolean,
    required: true,
  },
});

const emit = defineEmits(["update:modelValue"]);

const resetStep = ref(1);
const resetForm = ref({
  email: "",
  code: "",
  newPassword: "",
  confirmPassword: "",
});

const resetFormRef = ref();

// 倒计时相关
const countdown = ref(0);
const buttonStatus = ref("发送验证码");
const buttonDisabled = ref(false);

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
  code: [{ required: true, message: "请输入验证码", trigger: "blur" }],
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

const startCountdown = () => {
  countdown.value = 60;
  buttonDisabled.value = true;
  buttonStatus.value = `剩余 ${countdown.value} 秒`;

  const intervalId = setInterval(() => {
    if (countdown.value > 0) {
      countdown.value--;
      buttonStatus.value = `剩余 ${countdown.value} 秒`;
    } else {
      clearInterval(intervalId);
      buttonDisabled.value = false;
      buttonStatus.value = "发送验证码";
    }
  }, 1000);
};

const sendCodeToEmail = async () => {
  if (resetForm.value.email.trim() === "") {
    ElMessage.error("请输入邮箱地址");
    return;
  }
  try {
    const existsResponse = await existsByEmail(resetForm.value.email);
    if (!existsResponse.data) {
      ElMessage.error("该邮箱未注册，请先注册");
      return;
    }

    const response = await send(resetForm.value.email);
    if (response.data.status === 200) {
      ElMessage.success("验证码已发送至邮箱，请注意查收");
      startCountdown();
    } else {
      ElMessage.error("验证码发送失败，请稍后再试");
    }
  } catch (error) {
    console.error("Error:", error);
    ElMessage.error("验证码发送失败");
  }
};

const handleResetPassword = async () => {
  try {
    await resetFormRef.value.validate();

    if (resetStep.value === 1) {
      try {
        const verifyResult = await verify({
          email: resetForm.value.email,
          code: resetForm.value.code,
        });

        if (verifyResult.data === true) {
          resetStep.value = 2;
          ElMessage.success("邮箱验证成功");
        } else {
          throw new Error("验证码错误");
        }
      } catch (error) {
        ElMessage.error(error.message || "邮箱验证失败");
        return;
      }
    } else {
      const userResponse = await findUserByEmail(resetForm.value.email);
      if (!userResponse.data?.data?.userId) {
        throw new Error("用户信息获取失败");
      }

      const response = await resetPassword({
        userId: userResponse.data.data.userId,
        password: resetForm.value.newPassword,
      });

      if (response.data.status === 200) {
        ElMessage.success("密码重置成功");
        emit("success");
        handleClose();
      } else {
        throw new Error(response.data.message || "密码重置失败");
      }
    }
  } catch (error) {
    console.error("操作失败:", error);
    ElMessage.error(error.message || "操作失败");
  }
};

const handleClose = () => {
  resetStep.value = 1;
  resetForm.value = {
    email: "",
    code: "",
    newPassword: "",
    confirmPassword: "",
  };
  if (resetFormRef.value) {
    resetFormRef.value.resetFields();
  }
  emit("update:modelValue", false);
};

const getDialogTitle = computed(() => {
  return resetStep.value === 1 ? "验证邮箱" : "重置密码";
});

const getButtonText = computed(() => {
  return resetStep.value === 1 ? "下一步" : "确认重置";
});
</script>

<template>
  <el-dialog
    :title="getDialogTitle"
    :model-value="modelValue"
    @update:model-value="(val) => emit('update:modelValue', val)"
    width="520px"
    @close="handleClose"
    class="reset-password-dialog"
  >
    <el-form
      ref="resetFormRef"
      :model="resetForm"
      :rules="resetRules"
      label-width="100px"
      size="large"
    >
      <template v-if="resetStep === 1">
        <el-form-item label="邮箱地址" prop="email">
          <div class="flex-input-button">
            <el-input
              v-model="resetForm.email"
              placeholder="请输入邮箱地址"
              class="flex-grow"
              size="large"
            >
              <template #prefix>
                <font-awesome-icon :icon="['fas', 'envelope']" />
              </template>
            </el-input>
            <el-button
              :disabled="buttonDisabled"
              @click="sendCodeToEmail"
              class="button flex-shrink"
              type="primary"
              size="large"
            >
              {{ buttonStatus }}
            </el-button>
          </div>
        </el-form-item>
        <el-form-item label="验证码" prop="code">
          <el-input
            v-model="resetForm.code"
            placeholder="请输入验证码"
            size="large"
          ></el-input>
        </el-form-item>
      </template>

      <template v-else>
        <el-form-item label="新密码" prop="newPassword">
          <el-input
            v-model="resetForm.newPassword"
            type="password"
            show-password
            placeholder="请输入新密码"
            size="large"
          ></el-input>
        </el-form-item>
        <el-form-item label="确认密码" prop="confirmPassword">
          <el-input
            v-model="resetForm.confirmPassword"
            type="password"
            show-password
            placeholder="请再次输入新密码"
            size="large"
          ></el-input>
        </el-form-item>
      </template>
    </el-form>

    <template #footer>
      <span class="dialog-footer">
        <el-button @click="handleClose" size="large">取消</el-button>
        <el-button type="primary" @click="handleResetPassword" size="large">
          {{ getButtonText }}
        </el-button>
      </span>
    </template>
  </el-dialog>
</template>

<style lang="scss" scoped>
.reset-password-dialog {
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

.flex-input-button {
  display: flex;
  gap: 16px;

  .flex-grow {
    flex: 1;
  }

  .flex-shrink {
    flex-shrink: 0;
    width: 130px;
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
