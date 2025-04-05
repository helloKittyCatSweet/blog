<script setup>
import { ref, onMounted } from "vue";
import { useRoute, useRouter } from "vue-router";
import { ElMessage } from "element-plus";
import PageContainer from "@/components/PageContainer.vue";
import MdEditor from "md-editor-v3";
import "md-editor-v3/lib/style.css";

const route = useRoute();
const router = useRouter();
const loading = ref(false);

// 表单数据
const formRef = ref(null);
const form = ref({
  postId: null,
  title: "",
  content: "",
  coverImage: "",
  isPublished: false,
  isDraft: true,
  visibility: "PUBLIC",
});

// 表单校验规则
const rules = {
  title: [
    { required: true, message: "请输入文章标题", trigger: "blur" },
    { min: 2, max: 100, message: "长度在 2 到 100 个字符", trigger: "blur" },
  ],
  content: [{ required: true, message: "请输入文章内容", trigger: "blur" }],
};

// 获取文章详情
const getPostDetail = async (id) => {
  loading.value = true;
  try {
    // TODO: 调用获取文章详情API
    loading.value = false;
  } catch (error) {
    ElMessage.error("获取文章详情失败");
    loading.value = false;
  }
};

// 保存文章
const handleSave = async (isDraft = true) => {
  if (!formRef.value) return;
  await formRef.value.validate(async (valid) => {
    if (valid) {
      try {
        form.value.isDraft = isDraft;
        form.value.isPublished = !isDraft;
        // TODO: 调用保存API
        ElMessage.success(isDraft ? "保存草稿成功" : "发布成功");
        router.push("/user/post/list");
      } catch (error) {
        ElMessage.error(isDraft ? "保存草稿失败" : "发布失败");
      }
    }
  });
};

// 上传封面
const handleUploadSuccess = (res) => {
  form.value.coverImage = res.url;
};

onMounted(() => {
  const postId = route.params.id;
  if (postId) {
    getPostDetail(postId);
  }
});
</script>

<template>
  <page-container :title="form.postId ? '编辑文章' : '写文章'">
    <el-card v-loading="loading">
      <el-form ref="formRef" :model="form" :rules="rules" label-width="100px">
        <el-form-item label="文章标题" prop="title">
          <el-input v-model="form.title" placeholder="请输入文章标题" />
        </el-form-item>

        <el-form-item label="文章封面">
          <el-upload
            class="cover-uploader"
            action="/api/upload"
            :show-file-list="false"
            :on-success="handleUploadSuccess"
          >
            <img v-if="form.coverImage" :src="form.coverImage" class="cover" />
            <el-icon v-else class="cover-uploader-icon"><Plus /></el-icon>
          </el-upload>
        </el-form-item>

        <el-form-item label="可见性">
          <el-radio-group v-model="form.visibility">
            <el-radio label="PUBLIC">公开</el-radio>
            <el-radio label="PRIVATE">私密</el-radio>
          </el-radio-group>
        </el-form-item>

        <el-form-item label="文章内容" prop="content">
          <md-editor v-model="form.content" style="height: 500px" />
        </el-form-item>

        <el-form-item>
          <el-button type="primary" @click="handleSave(false)">发布文章</el-button>
          <el-button @click="handleSave(true)">保存草稿</el-button>
          <el-button @click="router.back()">返回</el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </page-container>
</template>

<style scoped>
.cover-uploader {
  width: 178px;
  height: 178px;
  border: 1px dashed var(--el-border-color);
  border-radius: 6px;
  cursor: pointer;
  position: relative;
  overflow: hidden;
}

.cover-uploader:hover {
  border-color: var(--el-color-primary);
}

.cover-uploader-icon {
  font-size: 28px;
  color: #8c939d;
  width: 178px;
  height: 178px;
  text-align: center;
  line-height: 178px;
}

.cover {
  width: 178px;
  height: 178px;
  display: block;
}
</style>
