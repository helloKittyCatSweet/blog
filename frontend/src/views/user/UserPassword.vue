<script setup>
import { ref } from "vue";
import { useRouter } from "vue-router";
import { useUserStore } from "@/stores/modules/user";
import { ElMessage, ElMessageBox } from "element-plus";
import { resetPassword, verifyPassword } from "@/api/user/user";
import { Lock, Key, Check, Refresh } from "@element-plus/icons-vue";

const pwdForm = ref({
  old_pwd: "",
  new_pwd: "",
  re_pwd: "",
});

const checkOldSame = (rule, value, cb) => {
  if (value === pwdForm.value.old_pwd) {
    cb(new Error("原密码和新密码不能一样!"));
  } else {
    cb();
  }
};

const checkNewSame = (rule, value, cb) => {
  if (value !== pwdForm.value.new_pwd) {
    cb(new Error("新密码和确认再次输入的新密码不一样!"));
  } else {
    cb();
  }
};

// 添加密码强度检查
const checkPasswordStrength = (rule, value, cb) => {
  // 密码必须包含数字、字母和特殊字符
  const hasNumber = /\d/.test(value);
  const hasLetter = /[a-zA-Z]/.test(value);
  const hasSpecial = /[!@#$%^&*(),.?":{}|<>]/.test(value);

  if (!hasNumber || !hasLetter || !hasSpecial) {
    cb(new Error("密码必须包含数字、字母和特殊字符"));
  } else {
    cb();
  }
};

const rules = {
  // 原密码
  old_pwd: [
    { required: true, message: "请输入密码", trigger: "blur" },
    {
      pattern: /^\S{6,15}$/,
      message: "密码长度必须是6-15位的非空字符串",
      trigger: "blur",
    },
  ],
  // 新密码
  new_pwd: [
    { required: true, message: "请输入新密码", trigger: "blur" },
    {
      pattern: /^\S{6,15}$/,
      message: "密码长度必须是6-15位的非空字符串",
      trigger: "blur",
    },
    { validator: checkOldSame, trigger: "blur" },
    { validator: checkPasswordStrength, trigger: "blur" }, // 添加密码强度检查
  ],
  // 确认新密码
  re_pwd: [
    { required: true, message: "请再次确认新密码", trigger: "blur" },
    {
      pattern: /^\S{6,15}$/,
      message: "密码长度必须是6-15位的非空字符串",
      trigger: "blur",
    },
    { validator: checkNewSame, trigger: "blur" },
  ],
};

// 添加密码强度指示器数据
const passwordStrength = ref(0);
const getPasswordStrength = (password) => {
  let strength = 0;
  if (/\d/.test(password)) strength += 1;
  if (/[a-zA-Z]/.test(password)) strength += 1;
  if (/[!@#$%^&*(),.?":{}|<>]/.test(password)) strength += 1;
  return strength;
};

// 监听密码变化
const handlePasswordInput = () => {
  passwordStrength.value = getPasswordStrength(pwdForm.value.new_pwd);
};

const formRef = ref();
const router = useRouter();
const userStore = useUserStore();
const onSubmit = async () => {
  const valid = await formRef.value.validate();
  if (valid) {
    try {
      // 先验证原密码是否正确
      const isValid = await verifyPassword({
        userId: userStore.user.id,
        password: pwdForm.value.old_pwd,
      });
      console.log(isValid);

      if (!isValid) {
        ElMessage.error("原密码验证失败");
        return;
      }

      // 修改新密码
      await resetPassword({
        userId: userStore.user.id,
        password: pwdForm.value.new_pwd,
      });

      ElMessage.success("密码修改成功，请重新登录");

      // 退出前确认
      await ElMessageBox.confirm(
        "密码修改成功，需要重新登录，是否立即跳转到登录页？",
        "提示",
        {
          confirmButtonText: "确定",
          cancelButtonText: "取消",
          type: "warning",
        }
      );

      // 清除用户信息并跳转
      userStore.setToken("");
      userStore.setUser({});
      router.push("/login");
    } catch (error) {
      if (error.message) {
        ElMessage.error(error.message);
      } else {
        ElMessage.error("密码修改失败，请重试");
      }
    }
  }
};

const onReset = () => {
  formRef.value.resetFields();
};
</script>
<template>
  <page-container title="重置密码">
    <div class="password-container">
      <el-row justify="center">
        <el-col :span="12">
          <el-card class="password-card" shadow="hover">
            <template #header>
              <div class="card-header">
                <el-icon class="header-icon"><Lock /></el-icon>
                <span>修改密码</span>
              </div>
            </template>

            <el-form
              :model="pwdForm"
              :rules="rules"
              ref="formRef"
              label-width="100px"
              size="large"
              @keyup.enter="onSubmit"
            >
              <el-form-item label="原密码" prop="old_pwd">
                <el-input v-model="pwdForm.old_pwd" show-password :prefix-icon="Key">
                </el-input>
              </el-form-item>

              <el-form-item label="新密码" prop="new_pwd">
                <el-input
                  v-model="pwdForm.new_pwd"
                  show-password
                  :prefix-icon="Lock"
                  @input="handlePasswordInput"
                >
                </el-input>
                <!-- 密码强度指示器 -->
                <div class="strength-indicator">
                  <div class="strength-text">密码强度：</div>
                  <div class="strength-bars">
                    <div
                      v-for="n in 3"
                      :key="n"
                      class="strength-bar"
                      :class="[
                        n <= passwordStrength ? 'active' : '',
                        n <= passwordStrength ? `level-${passwordStrength}` : '',
                      ]"
                    ></div>
                  </div>
                </div>
              </el-form-item>

              <el-form-item label="确认新密码" prop="re_pwd">
                <el-input v-model="pwdForm.re_pwd" show-password :prefix-icon="Lock">
                </el-input>
              </el-form-item>

              <el-form-item>
                <div class="button-group">
                  <el-button
                    type="primary"
                    @click="onSubmit"
                    :icon="Check"
                    class="submit-btn"
                  >
                    修改密码
                  </el-button>
                  <el-button @click="onReset" :icon="Refresh" class="reset-btn">
                    重置
                  </el-button>
                </div>
              </el-form-item>
            </el-form>
          </el-card>
        </el-col>
      </el-row>
    </div>
  </page-container>
</template>

<style lang="scss" scoped>
.password-container {
  padding: 20px;
  min-height: 100%;

  .password-card {
    backdrop-filter: blur(10px);
    transition: all 0.3s ease;

    &:hover {
      transform: translateY(-5px);
      box-shadow: 0 8px 16px rgba(0, 0, 0, 0.1);
    }

    .card-header {
      display: flex;
      align-items: center;
      gap: 10px;
      font-size: 18px;
      color: var(--el-color-primary);

      .header-icon {
        font-size: 24px;
      }
    }
  }

  .strength-indicator {
    margin-top: 8px;
    display: flex;
    align-items: center;
    gap: 10px;

    .strength-text {
      font-size: 14px;
      color: #666;
    }

    .strength-bars {
      display: flex;
      gap: 5px;

      .strength-bar {
        width: 40px;
        height: 4px;
        background: #eee;
        border-radius: 2px;
        transition: all 0.3s ease;

        &.active {
          &.level-1 {
            background: #13ce66;
          }
          &.level-2 {
            background: #f7ba2a;
          }
          &.level-3 {
            background: #ff4949;
          }
        }
      }
    }
  }

  .button-group {
    display: flex;
    justify-content: center;
    gap: 20px;
    margin-top: 20px;

    .submit-btn,
    .reset-btn {
      min-width: 120px;
      transition: all 0.3s ease;

      &:hover {
        transform: translateY(-2px);
      }
    }

    .submit-btn {
      background: linear-gradient(
        45deg,
        var(--el-color-primary),
        var(--el-color-primary-light-3)
      );
      border: none;

      &:hover {
        background: linear-gradient(
          45deg,
          var(--el-color-primary-dark-2),
          var(--el-color-primary)
        );
      }
    }
  }
}

// 输入框动画
:deep(.el-input__inner) {
  transition: all 0.3s ease;

  &:focus {
    transform: translateX(5px);
  }
}
</style>
