<script setup>
import { User, Lock } from "@element-plus/icons-vue";
import { ref, watch, nextTick, onMounted, onUnmounted } from "vue";
import { ElMessage } from "element-plus";
import { useUserStore, useSettingsStore, useThemeStore } from "@/stores";
import { useRouter, useRoute } from "vue-router";
import { FontAwesomeIcon } from "@fortawesome/vue-fontawesome";
import ResetPasswordDialog from "@/components/ResetPasswordDialog.vue";
import OAuthPasswordDialog from "@/components/OAuthPasswordDialog.vue";

import { findByUserId } from "@/api/user/userSetting.js";

// api
import { getCaptcha, checkCaptcha } from "@/api/user/security/captcha.js";
import { login, register } from "@/api/user/user";
import { verify, send } from "@/api/user/security/emailVerification";
import { CONTROL_PANEL_PATH } from "@/constants/routes/base";
import { getGithubLoginUrl, handleGithubCallback } from "@/api/auth/oauth";

const isRegister = ref(false);

const form = ref();

// 注册用于提交的form数据对象
const formModel = ref({
  username: "",
  password: "",
  repassword: "",
  captcha: "", // 验证码
  email: "", // 邮箱
});
// 整个表单的校验规则
// 1.非空校验 required:true message消息提示，trigger触发校验的时机 blur change
// 2.长度校验 min:xx, max:xx
// 3.正则校验 pattern：正则规则 \S 非空字符
// 4.自定义校验 => 自己写逻辑校验（校验函数）
//   validator:(rule, value, callback)
//   (1) rule  当前校验规则相关的信息
//   (2) value 所校验的表单元素目前的表单值
//   (3) callback 无论成功还是失败都需要callback回调
//       - callback() 校验成功
//       - callback(new Error(错误信息)) 校验失败

const rules = {
  username: [
    { required: true, message: "请输入用户名", trigger: "blur" },
    { min: 5, max: 20, message: "用户名必须是5-20位的字符", trigger: "blur" },
  ], // 失焦的时候检验
  password: [
    { required: true, message: "请输入密码", trigger: "blur" },
    { pattern: /^\S{6,15}$/, message: "密码必须是6-15位的非空字符", trigger: "blur" },
  ],
  repassword: [
    { required: true, message: "请输入密码", trigger: "blur" },
    { pattern: /^\S{6,15}$/, message: "密码必须是6-15位的非空字符", trigger: "blur" },
    {
      validator: (rule, value, callback) => {
        // 判断 value 和当前 form 中收集的password是否一致
        if (value !== formModel.value.password) {
          callback(new Error("两次输入密码不一致"));
        } else {
          callback(); // 就算校验成功，也需要callback
        }
      },
      trigger: "blur",
    },
  ],
  captcha: [{ required: true, message: "请输入验证码", trigger: "blur" }],
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
  emailCheck: [{ required: true, message: "请输入验证码", trigger: "blur" }],
};

/**
 * 验证码逻辑
 */

// 生成验证码逻辑
const captchaUrl = ref("");

const generateCaptcha = async () => {
  try {
    const response = await getCaptcha();
    if (captchaUrl.value) {
      window.URL.revokeObjectURL(captchaUrl.value);
    }

    const blob = new Blob([response.data], { type: response.headers["content-type"] });
    const url = window.URL.createObjectURL(blob);
    captchaUrl.value = url;
  } catch (error) {
    ElMessage.error("验证码加载失败");
    console.error("Error:", error);
  }
};

// 校验验证码
const verifyCaptcha = async () => {
  // console.log("开始校验验证码");
  try {
    // console.log("发送验证码校验请求，验证码:", formModel.value.captcha);
    // console.log("当前sessionId:", getCurrentSessionId()); // 添加这行
    const response = await checkCaptcha(formModel.value.captcha);
    // console.log("验证码校验响应:", response);
    return response.data.status === 200;
  } catch (error) {
    console.error("验证码校验错误:", error);
    if (error.message === "Session ID not found") {
      ElMessage({
        message: "验证码已失效，请刷新验证码",
        type: "error",
        offset: 80,
      });
      await generateCaptcha(); // 自动刷新验证码
    } else if (error.response?.data?.message) {
      ElMessage({
        message: error.response.data.message,
        type: "error",
        offset: 80,
      });
    } else {
      ElMessage({
        message: "验证码校验失败",
        type: "error",
        offset: 80,
      });
    }
    return false;
  }
};

// 设置自动刷新验证码
let captchaTimer;
// 添加记住登录状态的响应式变量
const rememberMe = ref(false);
const githubLoading = ref(false);

onMounted(async () => {
  // 检查是否是GitHub回调
  const code = new URLSearchParams(window.location.search).get("code");
  if (code) {
    try {
      // 先尝试直接登录（针对已注册用户）
      oauthLoading.value = true; // 显示加载动画
      ElMessage({
        message: "正在处理GitHub登录...",
        type: "info",
        offset: 200,
      });
      const res = await handleGithubCallback(code);
      if (res.data.status === 200) {
        const userData = res.data.data;
        if (userData.isNewUser) {
          // 新用户需要设置密码
          oauthData.value = {
            code,
            email: userData.email || "",
            username: userData.username || "",
          };
          oauthDialogVisible.value = true;
        } else {
          // 已注册用户直接登录
          await handleOAuthLogin(userData);
        }
      }
    } catch (error) {
      console.error("GitHub登录回调处理失败:", error);
      ElMessage.error("GitHub登录失败");
    }
  }

  // 初始生成验证码
  generateCaptcha();

  // 设置定时器，每四分三十秒刷新一次验证码
  captchaTimer = setInterval(() => {
    generateCaptcha();
  }, 270000); // 四分三十秒 = 270000毫秒;

  // 添加键盘事件监听
  window.addEventListener("keydown", handleKeydown);

  // 使用 requestIdleCallback 处理自动登录
  if (window.requestIdleCallback) {
    window.requestIdleCallback(autoLogin);
  } else {
    // 降级处理
    setTimeout(autoLogin, 0);
  }
});

const oauthDialogVisible = ref(false);
const oauthData = ref({});

const handleOAuthLogin = async (userData) => {
  try {
    userStore.setToken(userData.token);
    userStore.setUser({
      id: userData.id,
      username: userData.username,
      avatar: userData.avatar,
      roles: userData.roles,
      authorities: userData.authorities,
    });
    await router.replace(CONTROL_PANEL_PATH);
    ElMessage.success("登录成功");
  } catch (error) {
    console.error("登录失败:", error);
    ElMessage.error("登录失败");
  }
};

// 组件卸载时清除定时器
onUnmounted(() => {
  if (captchaTimer) {
    clearInterval(captchaTimer);
  }
  // 清理最后一个验证码
  if (captchaUrl.value) {
    window.URL.revokeObjectURL(captchaUrl.value);
  }
  // 移除键盘事件监听
  window.removeEventListener("keydown", handleKeydown);
});

/**
 * 监听回车按键
 */
const handleKeydown = (event) => {
  if (event.key === "Enter") {
    if (isRegister.value) {
      registerUser();
    } else {
      loginUser();
    }
  }
};
/**
 * 页面切换，重置表单
 */

// 切换的时候，重置表单内容
watch(isRegister, () => {
  formModel.value = {
    username: "",
    password: "",
    repassword: "",
    captcha: "",
    email: "",
    emailCheck: "", // 发到邮箱的验证码
  };
  // 切换时重新生成验证码
  generateCaptcha();
});

/**
 * 为自动登录添加 加密解密工具函数
 */
const encryptPassword = (password) => {
  return window.btoa(encodeURIComponent(password)); // 使用 base64 加密
};

const decryptPassword = (encrypted) => {
  return decodeURIComponent(window.atob(encrypted)); // 解密 base64
};

// 添加防抖函数
const debounce = (fn, delay) => {
  let timer = null;
  return function (...args) {
    if (timer) clearTimeout(timer);
    timer = setTimeout(() => fn.apply(this, args), delay);
  };
};

// 添加防抖函数和异步存储函数
const saveLoginInfo = debounce(async (username, password, remember) => {
  if (remember) {
    // 提前准备数据
    const loginData = JSON.stringify({
      username,
      password: encryptPassword(password),
      remember: true,
    });

    // 使用 requestIdleCallback 在浏览器空闲时执行
    if (window.requestIdleCallback) {
      window.requestIdleCallback(() => {
        localStorage.setItem("loginInfo", loginData);
      });
    } else {
      // 降级处理
      setTimeout(() => {
        localStorage.setItem("loginInfo", loginData);
      }, 50);
    }
  } else {
    localStorage.removeItem("loginInfo");
  }
}, 100);

// 添加自动登录函数
const autoLogin = async () => {
  try {
    // 提前获取数据
    const savedLoginInfo = localStorage.getItem("loginInfo");
    if (!savedLoginInfo) return;

    const { username, password, remember } = JSON.parse(savedLoginInfo);
    if (!remember) return;

    // 使用 Promise.all 并行处理
    await Promise.all([
      (async () => {
        formModel.value.username = username;
        formModel.value.password = decryptPassword(password);
        rememberMe.value = true;
      })(),
      form.value?.validate(),
    ]);

    await loginUser(true);
  } catch (error) {
    console.error("自动登录失败:", error);
    clearLoginInfo();
  }
};

/**
 * 获取主题设置
 */
const loadUserTheme = async (userId) => {
  const settingsStore = useSettingsStore();
  const themeStore = useThemeStore();

  try {
    const { data } = await findByUserId(userId);
    settingsStore.setSettings({
      theme: data.theme,
      notifications: data.notifications,
      githubAccount: data.githubAccount,
      csdnAccount: data.CSDNAccount || "",
      bilibiliAccount: data.BiliBiliAccount || "",
    });
    themeStore.setTheme(data.theme || "light");
  } catch (error) {
    console.error("加载主题设置失败:", error);
    // 使用默认主题
    settingsStore.setTheme("light");
    themeStore.setTheme("light");
  }
};

/**
 * 登录逻辑
 */
const route = useRoute();
const userStore = useUserStore();
const router = useRouter();
const loginUser = async (isAutoLogin = false) => {
  try {
    // 确保表单实例存在
    if (!form.value) {
      throw new Error("表单实例不存在");
    }

    await form.value.validate();

    // 如果不是自动登录，需要验证验证码
    if (!isAutoLogin) {
      const captchaResult = await verifyCaptcha(formModel.value.captcha);
      if (!captchaResult) return;
    }

    const res = await login({
      username: formModel.value.username,
      password: formModel.value.password,
    });

    if (res.data.status !== 200) {
      handleLoginError(res.data.status);
      return;
    }

    // 如果选择了记住我，保存登录信息
    if (rememberMe.value) {
      saveLoginInfo(formModel.value.username, formModel.value.password, true);
    } else {
      // 如果没有选择记住我，清除之前可能存储的信息
      localStorage.removeItem("loginInfo");
    }

    // 1. 先同步更新用户状态
    const userData = res.data.data;
    // console.log("userData:", userData.token);
    userStore.setToken(userData.token);
    userStore.setUser({
      // 推荐使用setUser批量更新
      id: userData.id,
      username: userData.username,
      avatar: userData.avatar,
      roles: userData.roles,
      authorities: userData.authorities,
    });

    // 2. 设置并持久化主题
    loadUserTheme(userData.id);

    // 2. 确保Pinia状态更新完成
    await nextTick();

    // 3. 显示成功提示（短暂延迟确保UI更新）
    await new Promise((resolve) => {
      ElMessage({
        message: "登录成功",
        type: "success",
        offset: 80,
        onClose: resolve,
      });
    });

    // 3. 添加调试信息
    console.log("登录状态:", {
      token: userStore.user.token,
      user: userStore.user,
      roles: userStore.user?.roles,
      targetPath: CONTROL_PANEL_PATH,
    });

    // 4. 执行跳转
    const redirect = route.query.redirect;
    if (redirect && typeof redirect === "string") {
      // 检查是否存在循环重定向
      if (redirect.includes("/login")) {
        // 如果重定向到登录页，直接跳转到控制面板
        await router.replace(CONTROL_PANEL_PATH);
      } else {
        await router.replace(redirect);
      }
    } else {
      await router.replace(CONTROL_PANEL_PATH);
    }
  } catch (error) {
    console.error("登录错误:", error);
    if (isAutoLogin) {
      clearLoginInfo(); // 自动登录失败，清除登录信息
    }
    ElMessage({
      message: error.response?.data?.message || "登录失败",
      type: "error",
      offset: 80,
    });
  }
};

// 添加退出登录时清除记住登录状态的方法
const clearLoginInfo = () => {
  localStorage.removeItem("loginInfo");
  rememberMe.value = false;
  formModel.value.username = "";
  formModel.value.password = "";
};

const handleLoginError = (status) => {
  const messages = {
    401: "用户名或密码错误",
    403: "用户已被禁用",
    429: "尝试次数过多，请稍后再试",
  };
  ElMessage({
    message: messages[status] || "登录失败，请稍后重试",
    type: "error",
    offset: 80,
  });
};

/**
 * 邮箱检验：给邮箱发验证码
 */
const countdown = ref(0); // 倒计时时间
const buttonStatus = ref("发送验证码"); // 按钮状态
const buttonDisabled = ref(false); // 按钮是否禁用

/**
 * 改变发送验证码button状态
 */
const startCountdown = () => {
  countdown.value = 60; // 设置倒计时为60秒
  buttonDisabled.value = true; // 禁用按钮
  buttonStatus.value = `剩余 ${countdown.value} 秒`; // 更新按钮状态

  const intervalId = setInterval(() => {
    if (countdown.value > 0) {
      countdown.value--; // 每秒递减
      buttonStatus.value = `剩余 ${countdown.value} 秒`; // 更新按钮状态
    } else {
      clearInterval(intervalId); // 停止计时器
      buttonDisabled.value = false; // 启用按钮
      buttonStatus.value = "发送验证码"; // 恢复按钮状态
    }
  }, 1000);
};

const sendCodeToEmail = async () => {
  if (formModel.value.email.trim() === "") {
    ElMessage.error("请输入邮箱地址");
    return;
  }
  try {
    const response = await send(formModel.value.email);
    if (response.data.status === 200) {
      ElMessage.success("验证码已发送至邮箱，请注意查收");
      startCountdown(); // 开始倒计时
    } else {
      ElMessage.error("验证码发送失败，请稍后再试");
    }
  } catch (error) {
    console.error("Error:", error);
    ElMessage.error("验证码发送失败");
  }
};

/**
 * 检验输入的邮箱验证码是否正确
 */
const verifyEmailCode = async () => {
  if (formModel.value.emailCheck.trim() === "" || formModel.value.email.trim() === "") {
    ElMessage.error("请输入邮箱或邮箱验证码");
    return;
  }
  const response = await verify({
    email: formModel.value.email,
    code: formModel.value.emailCheck,
  });
  if (response.data == false) {
    ElMessage.error("验证码错误，请重新输入");
    return;
  } else {
    ElMessage.success("邮箱验证成功");
    return;
  }
};

/**
 * 注册逻辑
 */

// 注册成功之前，先进行校验，校验成功->请求，校验失败->自动提示
const registerUser = async () => {
  await form.value.validate();
  // console.log("开始注册请求");

  // 检验邮箱验证码是否正确
  if (!verifyEmailCode()) {
    ElMessage.error("邮箱验证码错误，请重新输入");
    return;
  }

  // 检验图片验证码是否正确
  if (!verifyCaptcha()) {
    ElMessage.error("验证码错误，请重新输入");
    return;
  }

  const response = await register({
    username: formModel.value.username,
    password: formModel.value.password,
    email: formModel.value.email,
  });

  if (response.data.status === 200) {
    ElMessage.success("注册成功");
    isRegister.value = false; // 切换到登录
  } else {
    ElMessage.error(response.data.message);
  }
};

// 添加重置密码的响应式变量
const resetDialogVisible = ref(false);

/**
 * Github
 */
// 添加加载状态控制
const oauthLoading = ref(false);

// 添加 GitHub 登录相关逻辑
const handleGithubLogin = async () => {
  try {
    githubLoading.value = true;
    oauthLoading.value = true; // 显示加载动画
    ElMessage({
      message: "正在跳转到 GitHub 授权页面...",
      type: "info",
      duration: 2000,
    });

    const res = await getGithubLoginUrl();
    if (res.data.status === 200) {
      window.location.href = res.data.data;
    } else {
      ElMessage.error("获取GitHub登录地址失败");
    }
  } catch (error) {
    console.error("GitHub登录失败:", error);
    ElMessage.error("获取GitHub登录地址失败");
  } finally {
    githubLoading.value = false;
  }
};

// 添加处理密码设置成功的回调
const handlePasswordSet = async (userData) => {
  try {
    if (userData) {
      ElMessage({
        message: "正在处理登录信息...",
        type: "info",
        duration: 2000,
      });
      await handleOAuthLogin(userData);
      oauthDialogVisible.value = false;
    }
  } catch (error) {
    console.error("处理密码设置失败:", error);
    ElMessage.error("设置密码后登录失败");
  }
};
</script>

<template>
  <!--
      1.结构相关
       el-row表示一行，一行分成24份
       el-col表示一列
       （1）:span="12" 代表在一行中，占12份（50%）
       （2）:span="6" 代表在一行中，占6份（25%）
       （1）:offset="3" 代表在一行中，左侧margin份数

       el-form 整个表单组件
       el-form-item 表单的一行（一个表单域）
       el-input 表单元素（输入框）

      2.校验相关
      （1）el-form => :model="ruleForm" 绑定的整个form的数据对象
      （2）el-form => :rules="rules"    绑定的整个rules规则对象
      （3）表单元素 =>  v-model="ruleForm.xxx" 给表单元素绑定form的子属性
       (4) el-form-item => prop配置生效的是哪个校验规则（和rules中的字段对应）
  -->
  <!-- 在最外层添加加载遮盖层 -->
  <div class="loading-overlay" v-if="oauthLoading">
    <img src="@/assets/loading.gif" alt="加载中..." class="loading-gif" />
    <p class="loading-text">正在处理您的登录请求...</p>
  </div>

  <el-row class="login-page">
    <el-col :span="12" class="bg"></el-col>
    <el-col :span="7" :offset="2" class="form">
      <!-- 注册相关表单 -->
      <el-form
        :model="formModel"
        :rules="rules"
        ref="form"
        size="large"
        autocomplete="off"
        v-if="isRegister"
        @keyup.enter="isRegister ? registerUser() : loginUser()"
      >
        <el-form-item>
          <h1>注册</h1>
        </el-form-item>
        <!-- 用户名 -->
        <el-form-item prop="username">
          <el-input
            v-model="formModel.username"
            :prefix-icon="User"
            placeholder="请输入用户名"
          ></el-input>
        </el-form-item>
        <!-- 密码 -->
        <el-form-item prop="password">
          <el-input
            v-model="formModel.password"
            :prefix-icon="Lock"
            type="password"
            show-password
            placeholder="请输入密码"
          ></el-input>
        </el-form-item>
        <!-- 再次输入密码 -->
        <el-form-item prop="repassword">
          <el-input
            v-model="formModel.repassword"
            :prefix-icon="Lock"
            type="password"
            show-password
            placeholder="请输入再次密码"
          ></el-input>
        </el-form-item>
        <!-- 邮箱 -->
        <el-form-item prop="email">
          <div class="flex-input-button">
            <!--添加类以使用flex增长-->
            <el-input
              v-model="formModel.email"
              placeholder="请输入邮箱地址"
              type="email"
              class="flex-grow"
            >
              <!-- 使用 Font Awesome 图标 -->
              <template #prefix>
                <font-awesome-icon :icon="['fas', 'envelope']" />
              </template>
            </el-input>
            <!--添加类以使用flex缩小-->
            <el-button
              :disabled="buttonDisabled"
              @click="sendCodeToEmail"
              class="button flex-shrink"
              type="primary"
              auto-insert-space
            >
              {{ buttonStatus }}
            </el-button>
          </div>
        </el-form-item>
        <!-- 邮箱验证码 -->
        <el-form-item prop="emailCheck">
          <el-input
            v-model="formModel.emailCheck"
            placeholder="请输入邮箱验证码"
          ></el-input>
        </el-form-item>
        <!-- 验证码 -->
        <el-form-item prop="captcha">
          <div style="display: flex; align-items: center">
            <el-input
              v-model="formModel.captcha"
              placeholder="请输入图片验证码"
              style="width: 60%"
            ></el-input>
            <img
              :src="captchaUrl"
              alt="验证码"
              @click="generateCaptcha"
              style="cursor: pointer; width: 40%; margin-left: 10px"
            />
          </div>
        </el-form-item>
        <!-- 注册按钮 -->
        <el-form-item>
          <el-button
            @click="registerUser"
            class="button"
            type="primary"
            auto-insert-space
          >
            注册
          </el-button>
        </el-form-item>
        <!-- 返回登录按钮 -->
        <el-form-item class="flex">
          <el-link type="info" :underline="false" @click="isRegister = false">
            ← 返回
          </el-link>
        </el-form-item>
      </el-form>

      <!-- 登录相关表单 -->
      <el-form
        :model="formModel"
        :rules="rules"
        ref="form"
        size="large"
        autocomplete="off"
        v-else
      >
        <el-form-item class="centered-title">
          <h1>登录</h1>
        </el-form-item>
        <!-- 用户名 -->
        <el-form-item prop="username">
          <el-input
            v-model="formModel.username"
            :prefix-icon="User"
            placeholder="请输入用户名"
          ></el-input>
        </el-form-item>
        <!-- 密码 -->
        <el-form-item prop="password">
          <el-input
            v-model="formModel.password"
            name="password"
            :prefix-icon="Lock"
            type="password"
            show-password
            placeholder="请输入密码"
          ></el-input>
        </el-form-item>
        <!-- 验证码 -->
        <el-form-item prop="captcha">
          <div style="display: flex; align-items: center">
            <el-input
              v-model="formModel.captcha"
              placeholder="请输入验证码"
              style="width: 60%"
            ></el-input>
            <img
              :src="captchaUrl"
              alt="验证码"
              @click="generateCaptcha"
              style="cursor: pointer; width: 40%; margin-left: 10px"
            />
          </div>
        </el-form-item>
        <!-- 记住我 -->
        <el-form-item class="flex">
          <div class="flex">
            <el-checkbox v-model="rememberMe">记住我</el-checkbox>
            <el-link type="primary" :underline="false" @click="resetDialogVisible = true"
              >忘记密码？</el-link
            >
            <ResetPasswordDialog
              v-model="resetDialogVisible"
              @success="handlePasswordSet"
            />
          </div>
        </el-form-item>

        <!-- 添加分割线和社交登录区域 -->
        <el-divider>社交账号登录</el-divider>

        <el-form-item class="social-login">
          <div class="github-container">
            <el-button
              @click="handleGithubLogin"
              class="github-button"
              type="default"
              auto-insert-space
              :loading="githubLoading"
            >
              <font-awesome-icon :icon="['fab', 'github']" />
              GitHub 登录
            </el-button>
          </div>

          <OAuthPasswordDialog
            v-model="oauthDialogVisible"
            :oauth-data="oauthData"
            @success="handlePasswordSet"
          />
        </el-form-item>

        <!-- 登录 -->
        <el-form-item>
          <el-button @click="loginUser" class="button" type="primary" auto-insert-space
            >登录</el-button
          >
        </el-form-item>
        <!-- 返回注册 -->
        <el-form-item class="flex">
          <el-link type="info" :underline="false" @click="isRegister = true">
            注册 →
          </el-link>
        </el-form-item>
      </el-form>
    </el-col>
  </el-row>
</template>

<style lang="scss" scoped>
.login-page {
  height: 100vh;
  background-color: #fff;

  .bg {
    background: url("@/assets/login_bg.png") no-repeat center / cover;
    border-radius: 0 20px 20px 0;
  }

  .form {
    display: flex;
    flex-direction: column;
    justify-content: center;
    user-select: none;

    .title {
      margin: 0 auto;
    }

    .button {
      width: 100%;
    }

    .flex {
      width: 100%;
      display: flex;
      justify-content: space-between;
    }

    .centered-title {
      display: flex;
      justify-content: center; /* 水平居中 */
      align-items: center; /* 垂直居中 */
      text-align: center; /* 文字居中 */
    }

    .flex-input-button {
      display: flex;
      align-items: center;

      .flex-grow {
        flex-grow: 20; /* 输入框占据剩余空间 */
        width: 180%; /* 输入框宽度增加 */
      }

      .flex-shrink {
        flex-shrink: 1; /* 按钮不缩小 */
        margin-left: 20px; /* *按钮距离输入框的距离*/
      }
    }
  }

  .social-login {
    display: flex;
    justify-content: center;
    align-items: center;
    margin-top: 20px;
    width: 100%;

    .github-container {
      display: flex;
      justify-content: center;
      width: 100%;
    }

    .github-button {
      width: 60%; // 设置按钮宽度为父容器的60%
      padding: 12px 24px; // 增加内边距使按钮更大
      display: flex;
      justify-content: center;
      align-items: center;
      gap: 8px; // 图标和文字之间的间距

      .fa-github {
        font-size: 18px; // 增大图标尺寸
      }
    }
  }
}

.loading-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background-color: rgba(255, 255, 255, 0.9);
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  z-index: 9999;
  backdrop-filter: blur(5px);

  .loading-gif {
    width: 120px;
    height: 120px;
    margin-bottom: 20px;
  }

  .loading-text {
    font-size: 16px;
    color: #606266;
    margin-top: 16px;
  }
}
</style>
