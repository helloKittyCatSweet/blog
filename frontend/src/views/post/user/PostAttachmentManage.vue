<script setup>
import { ref, onMounted } from "vue";
import { ElMessage, ElMessageBox } from "element-plus";
import { useRoute, useRouter } from "vue-router";
import { Delete, Download, Upload, Plus } from "@element-plus/icons-vue";
import PageContainer from "@/components/PageContainer.vue";
import { formatDateTime } from "@/utils/format";

const route = useRoute();
const router = useRouter();
const loading = ref(false);
const postId = ref(route.params.id);

// 附件列表数据
const attachmentList = ref([]);

// 上传对话框
const uploadVisible = ref(false);
const uploadLoading = ref(false);

// 获取附件列表
const getAttachmentList = async () => {
  loading.value = true;
  try {
    // TODO: 调用获取附件列表API
    loading.value = false;
  } catch (error) {
    ElMessage.error("获取附件列表失败");
    loading.value = false;
  }
};

// 删除附件
const handleDelete = (row) => {
  ElMessageBox.confirm("确认删除该附件吗？", "警告", {
    confirmButtonText: "确定",
    cancelButtonText: "取消",
    type: "warning",
  }).then(async () => {
    try {
      // TODO: 调用删除附件API
      ElMessage.success("删除成功");
      getAttachmentList();
    } catch (error) {
      ElMessage.error("删除失败");
    }
  });
};

// 下载附件
const handleDownload = (row) => {
  try {
    window.open(row.url, "_blank");
  } catch (error) {
    ElMessage.error("下载失败");
  }
};

// 上传附件
const handleUploadSuccess = (response) => {
  if (response.status === 200) {
    ElMessage.success("上传成功");
    getAttachmentList();
  } else {
    ElMessage.error("上传失败");
  }
  uploadVisible.value = false;
};

// 格式化文件大小
const formatFileSize = (size) => {
  if (size < 1024) {
    return size + " B";
  } else if (size < 1024 * 1024) {
    return (size / 1024).toFixed(2) + " KB";
  } else if (size < 1024 * 1024 * 1024) {
    return (size / (1024 * 1024)).toFixed(2) + " MB";
  } else {
    return (size / (1024 * 1024 * 1024)).toFixed(2) + " GB";
  }
};

onMounted(() => {
  if (postId.value) {
    getAttachmentList();
  } else {
    ElMessage.error("缺少文章ID参数");
    router.push("/user/post/list");
  }
});
</script>

<template>
  <page-container title="文章附件管理">
    <el-card v-loading="loading">
      <template #header>
        <div class="card-header">
          <span>附件列表</span>
          <el-button type="primary" :icon="Plus" @click="uploadVisible = true">
            上传附件
          </el-button>
        </div>
      </template>

      <el-table :data="attachmentList" border stripe>
        <template #empty>
          <el-empty description="暂无附件" />
        </template>
        <el-table-column type="index" label="序号" width="80" align="center" />
        <el-table-column prop="fileName" label="文件名" show-overflow-tooltip />
        <el-table-column prop="fileType" label="类型" width="120">
          <template #default="{ row }">
            <el-tag>{{ row.fileType }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="fileSize" label="大小" width="120">
          <template #default="{ row }">
            {{ formatFileSize(row.fileSize) }}
          </template>
        </el-table-column>
        <el-table-column prop="uploadTime" label="上传时间" width="180">
          <template #default="{ row }">
            {{ formatDateTime(row.uploadTime) }}
          </template>
        </el-table-column>
        <el-table-column label="操作" width="150" align="center">
          <template #default="{ row }">
            <el-button
              type="primary"
              :icon="Download"
              circle
              @click="handleDownload(row)"
              title="下载"
            />
            <el-button
              type="danger"
              :icon="Delete"
              circle
              @click="handleDelete(row)"
              title="删除"
            />
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <!-- 上传对话框 -->
    <el-dialog
      v-model="uploadVisible"
      title="上传附件"
      width="500px"
      :destroy-on-close="true"
    >
      <el-upload
        class="upload-demo"
        drag
        action="/api/attachment/upload"
        :data="{ postId }"
        :on-success="handleUploadSuccess"
        :on-error="() => ElMessage.error('上传失败')"
        multiple
      >
        <el-icon class="el-icon--upload"><Upload /></el-icon>
        <div class="el-upload__text">将文件拖到此处，或<em>点击上传</em></div>
        <template #tip>
          <div class="el-upload__tip">支持任意类型文件，单个文件不超过50MB</div>
        </template>
      </el-upload>
    </el-dialog>
  </page-container>
</template>

<style scoped>
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.upload-demo {
  text-align: center;
}
</style>
