<script setup>
import { CaretBottom, User, Crop, EditPen, SwitchButton, Switch } from "@element-plus/icons-vue";
import { useUserStore } from "@/stores";
import { markRaw } from "vue";
import { useRouter } from "vue-router";
import { LOGIN_PATH } from "@/constants/routes/base";

const userStore = useUserStore();
const router = useRouter();

// 图标注册
const UserIcon = markRaw(User);
const CropIcon = markRaw(Crop);
const EditPenIcon = markRaw(EditPen);
const SwitchButtonIcon = markRaw(SwitchButton);
const CaretBottomIcon = markRaw(CaretBottom);

const emit = defineEmits(["command"]);

const handleCommand = (command) => {
  if (command === 'switch') {
    // 清除自动登录信息
    localStorage.removeItem("loginInfo");
    // 跳转到登录页
    router.push(LOGIN_PATH);
  } else {
    emit("command", command);
  }
};
</script>

<template>
  <div class="user-dropdown">
    <div>
      <strong>{{ userStore.username }}</strong>
    </div>

    <el-dropdown placement="bottom-end" @command="handleCommand">
      <span class="el-dropdown__box">
        <el-avatar :size="32" :src="userStore.user.avatar || ''" class="avatar">
          {{ userStore.user.nickname?.charAt(0) || userStore.user.username?.charAt(0) }}
        </el-avatar>
        <el-icon>
          <CaretBottom />
        </el-icon>
      </span>
      <template #dropdown>
        <el-dropdown-menu>
          <el-dropdown-item command="profile" :icon="UserIcon">基本资料</el-dropdown-item>

          <el-dropdown-item command="password" :icon="EditPenIcon"
            >重置密码</el-dropdown-item
          >
          <el-dropdown-item command="switch" :icon="Switch">切换用户</el-dropdown-item>
          <el-dropdown-item command="logout" :icon="SwitchButtonIcon"
            >退出登录</el-dropdown-item
          >
        </el-dropdown-menu>
      </template>
    </el-dropdown>
  </div>
</template>

<style lang="scss" scoped>
.user-dropdown {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 20px;

  .el-dropdown__box {
    display: flex;
    align-items: center;
    cursor: pointer;

    .el-icon {
      color: #999;
      margin-left: 10px;
    }

    &:active,
    &:focus {
      outline: none;
    }
  }
}
</style>
