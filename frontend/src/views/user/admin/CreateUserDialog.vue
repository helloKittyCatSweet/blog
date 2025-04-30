<script setup>
import { ref, watch } from 'vue';
import { ElMessage } from 'element-plus';
import { register } from '@/api/user/user';

const props = defineProps({
  visible: {
    type: Boolean,
    required: true
  }
});

const emit = defineEmits(['update:visible', 'created']);

const formRef = ref(null);
const form = ref({
  username: '',
  email: '',
  password: '',
});

const rules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
    { min: 5, max: 20, message: '用户名长度在5-20个字符之间', trigger: 'blur' }
  ],
  email: [
    { required: true, message: '请输入邮箱地址', trigger: 'blur' },
    { type: 'email', message: '请输入正确的邮箱地址', trigger: 'blur' }
  ]
};

const handleClose = () => {
  dialogVisible.value = false;
  form.value = {
    username: '',
    email: '',
    password: '',
  };
  if (formRef.value) {
    formRef.value.resetFields();
  }
};

const handleSubmit = async () => {
  if (!formRef.value) return;

  await formRef.value.validate(async (valid) => {
    if (valid) {
      try {
        const userData = { ...form.value };
        if (!userData.password) {
          userData.password = '123456';
        }

        const res = await register(userData);
        if (res.data.status === 200) {
          ElMessage.success('创建用户成功');
          emit('created');
          handleClose();
        }
      } catch (error) {
        ElMessage.error(error.response?.data?.message || '创建用户失败');
      }
    }
  });
};

const dialogVisible = ref(props.visible);

// 监听 props 变化
watch(() => props.visible, (newVal) => {
  dialogVisible.value = newVal;
});

// 监听本地状态变化
watch(dialogVisible, (newVal) => {
  if (newVal !== props.visible) {
    emit('update:visible', newVal);
  }
});

</script>

<template>
  <el-dialog
    v-model="dialogVisible"
    title="创建用户"
    width="500px"
    @close="handleClose"
  >
    <el-form
      ref="formRef"
      :model="form"
      :rules="rules"
      label-width="80px"
    >
      <el-form-item label="用户名" prop="username">
        <el-input v-model="form.username" />
      </el-form-item>
      <el-form-item label="邮箱" prop="email">
        <el-input v-model="form.email" />
      </el-form-item>
      <el-form-item label="密码">
        <el-input
          v-model="form.password"
          type="password"
          placeholder="不填则默认为123456"
        />
      </el-form-item>
    </el-form>
    <template #footer>
      <span class="dialog-footer">
        <el-button @click="handleClose">取消</el-button>
        <el-button type="primary" @click="handleSubmit">确定</el-button>
      </span>
    </template>
  </el-dialog>
</template>