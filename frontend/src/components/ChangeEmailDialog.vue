<script setup>
import { ref, watch } from "vue";
import { ElMessage } from "element-plus";
import { send, verify } from "@/api/user/security/emailVerification";

const props = defineProps({
  modelValue: Boolean,
  currentEmail: String,
});

const emit = defineEmits(["update:modelValue", "success"]);

const formData = ref({
  email: "",
  code: "",
  currentEmail: props.currentEmail, // 将当前邮箱地址传递给子组件
});

// 监听 currentEmail 的变化
watch(
  () => props.currentEmail,
  (newEmail) => {
    formData.value.currentEmail = newEmail;
  },
  { immediate: true }
);

const countdown = ref(0);
const buttonStatus = ref("发送验证码");
const buttonDisabled = ref(false);

// 添加邮箱验证方法
const validateEmail = () => {
  if (!formData.value.email) return;
  
  if (formData.value.email === formData.value.currentEmail) {
    ElMessage.error("新邮箱不能与当前邮箱相同");
    formData.value.email = ""; // 清空输入
    return false;
  }
  return true;
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

const sendCode = async () => {
  if (!formData.value.email) {
    ElMessage.error("请输入新邮箱地址");
    return;
  }

  try {
    const response = await send(formData.value.email);
    if (response.data.status === 200) {
      ElMessage.success("验证码已发送至邮箱");
      startCountdown();
    }
  } catch (error) {
    ElMessage.error("验证码发送失败");
  }
};

const handleSubmit = async () => {
  if (!formData.value.email || !formData.value.code) {
    ElMessage.error("请填写完整信息");
    return;
  }

  try {
    const response = await verify({
      email: formData.value.email,
      code: formData.value.code,
    });

    if (response.data === true) {
      emit("success", formData.value.email);
      emit("update:modelValue", false);
      formData.value = { email: "", code: "" };
      ElMessage.success("邮箱验证成功");
    } else {
      ElMessage.error("验证码错误");
    }
  } catch (error) {
    ElMessage.error("验证失败");
  }
};
</script>

<template>
  <el-dialog
    title="更换邮箱"
    :model-value="modelValue"
    @update:model-value="emit('update:modelValue', $event)"
    width="400px"
    @close="emit('update:modelValue', false)"
  >
    <el-form :model="formData" label-width="100px">
      <el-form-item label="当前邮箱">
        <el-input v-model="formData.currentEmail" disabled></el-input>
      </el-form-item>

      <el-form-item label="新邮箱">
        <div style="display: flex; gap: 10px">
          <el-input 
            v-model="formData.email" 
            placeholder="请输入新邮箱"
            @blur="validateEmail"
          ></el-input>
          <el-button :disabled="buttonDisabled" @click="sendCode" type="primary">{{
            buttonStatus
          }}</el-button>
        </div>
      </el-form-item>

      <el-form-item label="验证码">
        <el-input v-model="formData.code" placeholder="请输入验证码"></el-input>
      </el-form-item>
    </el-form>

    <template #footer>
      <el-button @click="emit('update:modelValue', false)">取消</el-button>
      <el-button type="primary" @click="handleSubmit">确认更换</el-button>
    </template>
  </el-dialog>
</template>
