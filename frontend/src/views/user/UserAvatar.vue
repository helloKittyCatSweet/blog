<script setup>
import { ref } from "vue";
import { Plus, Upload } from "@element-plus/icons-vue";
import { useUserStore } from "@/stores";
// import { userUploadAvatarService } from "@/api/user";
import { ElMessage } from "element-plus";

const userStore = useUserStore();

const uploadRef = ref();
const imgUrl = ref(userStore.user.user_pic);
const onUploadFile = (file) => {
  // 基于FileReader读取图片文件内容做预览
  const reader = new FileReader();
  reader.readAsDataURL(file.raw);
  reader.onload = () => {
    imgUrl.value = reader.result;
  };
};

const uploadAvatar = async () => {
  // 发送请求更新头像
  await userUploadAvatarService(imgUrl.value);
  // usestore重新渲染
  await userStore.getUser();
  // 提示用户
  ElMessage({
    message: "头像上传成功",
    type: "success",
  });
};
</script>

<template>
  <page-container title="更换头像">
    <el-row>
      <el-col :span="12">
        <el-upload
          ref="uploadRef"
          class="avatar-uploader"
          :auto-upload="false"
          :show-file-list="false"
          :on-change="onUploadFile"
        >
          <img v-if="imgUrl" :src="imgUrl" class="avatar" />
          <img v-else src="@/assets/avatar.jpg" width="278" />
        </el-upload>
        <br />
        <el-button
          @click="uploadRef.$el.querySelector('input').click()"
          type="primary"
          :icon="Plus"
          size="large"
          >选择图片</el-button
        >
        <el-button type="success" :icon="Upload" size="large" @="uploadAvatar">
          上传头像
        </el-button>
      </el-col>
    </el-row>
  </page-container>
</template>

<style lang="scss" scoped>
.avatar-uploader {
  :deep() {
    .avatar {
      width: 278px;
      height: 278px;
      display: block;
    }
    .el-upload {
      border: 1px dashed var(--el-border-color);
      border-radius: 6px;
      cursor: pointer;
      position: relative;
      overflow: hidden;
      transition: var(--el-transition-duration-fast);
    }
    .el-upload:hover {
      border-color: var(--el-color-primary);
    }
    .el-icon.avatar-uploader-icon {
      font-size: 28px;
      color: #8c939d;
      width: 278px;
      height: 278px;
      text-align: center;
    }
  }
}
</style>
