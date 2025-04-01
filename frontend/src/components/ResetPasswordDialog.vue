<script setup>
import { ref, onMounted, onUnmounted, defineProps, defineEmits, nextTick } from "vue";
import { ElMessage } from "element-plus";
import { FontAwesomeIcon } from "@fortawesome/vue-fontawesome";
import { verify, send } from "@/api/user/security/emailVerification";
import { resetPassword, existsByEmail, findUserByEmail } from "@/api/user/user";
import user from "@/router/modules/user";

const props = defineProps({
  modelValue: {
    type: Boolean,
    required: true,
  },
});

const emit = defineEmits(["update:modelValue", "success"]);

const resetStep = ref(1);
const resetForm = ref({
  email: "",
  code: "",
  newPassword: "",
  confirmPassword: "",
});

const resetFormRef = ref();

// 复用倒计时相关逻辑
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
    // 先检查邮箱是否存在
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
      const verifyResult = await verify({
        email: resetForm.value.email,
        code: resetForm.value.code,
      });

      if (verifyResult.data === true) {
        resetStep.value = 2;
        ElMessage.success("邮箱验证成功");
      } else {
        ElMessage.error("验证码错误");
      }
    } else {
      // 获取用户id
      const userResponse = await findUserByEmail(resetForm.value.email);
      if (!userResponse.data || !userResponse.data.data) {
        ElMessage.error("用户信息获取失败");
        return;
      }
      console.log("id:", userResponse.data.data.userId);
      console.log("newPassword:", resetForm.value.newPassword);

      const response = await resetPassword({
        userId: userResponse.data.data.userId,
        password: resetForm.value.newPassword,
      });

      if (response.data.status === 200) {
        ElMessage.success("密码重置成功");
        emit("success");
        handleClose();
      } else {
        ElMessage.error(response.data.message || "密码重置失败");
      }
    }
  } catch (error) {
    console.error("重置密码错误:", error);
    ElMessage.error(error.response?.data?.message || "操作失败");
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
  emit("update:modelValue", false);
};

// 修改挂载方式，将事件监听器添加到对话框元素上
const dialog = ref(null);
const dialogContent = ref(null); // 新增对话框内容区域引用

// 修改后的回车键处理
const handleKeydown = (event) => {
  if (event.key === "Enter") {
    event.stopImmediatePropagation(); // 使用 stopImmediatePropagation 更彻底
    event.preventDefault();
    handleResetPassword();
  }
};

// 修改挂载方式
onMounted(() => {
  nextTick(() => {
    if (dialog.value) {
      // 正确获取 Element Plus 对话框的内容区域
      const dialogEl = dialog.value;
      const contentEl =
        dialogEl.$el?.querySelector?.(".el-dialog__body") ||
        dialogEl.$el?.querySelector?.(".el-dialog") ||
        dialogEl.$el;

      if (contentEl) {
        dialogContent.value = contentEl;
        contentEl.addEventListener("keydown", handleKeydown, { capture: true });
      }
    }
  });
});

onUnmounted(() => {
  if (dialogContent.value) {
    dialogContent.value.removeEventListener("keydown", handleKeydown, { capture: true });
  }
});
</script>

<template>
  <el-dialog
    ref="dialog"
    :title="resetStep === 1 ? '验证邮箱' : '重置密码'"
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
              @keydown.enter.stop.prevent="handleResetPassword"
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
            @keydown.enter.stop.prevent="handleResetPassword"
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
            @keydown.enter.stop.prevent="handleResetPassword"
          ></el-input>
        </el-form-item>
        <el-form-item label="确认密码" prop="confirmPassword">
          <el-input
            v-model="resetForm.confirmPassword"
            type="password"
            show-password
            placeholder="请再次输入新密码"
            size="large"
            @keydown.enter.stop.prevent="handleResetPassword"
          ></el-input>
        </el-form-item>
      </template>
    </el-form>

    <template #footer>
      <span class="dialog-footer">
        <el-button @click="handleClose" size="large">取消</el-button>
        <el-button type="primary" @click="handleResetPassword" size="large">
          {{ resetStep === 1 ? "下一步" : "确认重置" }}
        </el-button>
      </span>
    </template>
  </el-dialog>
</template>

<style lang="scss" scoped>
.reset-password-dialog {
  :deep(.el-dialog__body) {
    outline: none; // 移除焦点轮廓
  }

  :deep(.el-dialog) {
    border-radius: 8px;

    .el-dialog__header {
      margin: 0;
      padding: 20px 20px 10px;
      text-align: center;

      .el-dialog__title {
        font-size: 24px;
        font-weight: 500;
      }
    }

    .el-dialog__body {
      padding: 30px 40px;
    }

    .el-form-item {
      margin-bottom: 25px;
    }
  }
}

.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
  padding: 10px 0;

  .el-button {
    min-width: 100px;
  }
}

.flex-input-button {
  display: flex;
  gap: 12px;

  .flex-grow {
    flex: 1;
  }

  .flex-shrink {
    flex-shrink: 0;
    width: 120px; // 固定发送验证码按钮的宽度
  }
}

// 添加输入框样式
:deep(.el-input) {
  .el-input__wrapper {
    padding: 1px 15px;
    height: 42px;
    line-height: 42px;
  }

  .el-input__inner {
    height: 42px;
    line-height: 42px;
  }
}
</style>
