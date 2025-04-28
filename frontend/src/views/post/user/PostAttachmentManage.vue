<script setup>
import { ref, onMounted } from "vue";
import { ElMessage, ElMessageBox } from "element-plus";
import { useRoute, useRouter } from "vue-router";
import { Delete, Download, Upload, Plus } from "@element-plus/icons-vue";
import PageContainer from "@/components/PageContainer.vue";
import { formatDateTime } from "@/utils/format";
import { findAttachmentsByUserId, deleteAttachment, findByUserId } from "@/api/post/post";
import { USER_POST_EDIT_PATH } from "@/constants/routes/user";
import { useUserStore } from "@/stores/modules/user";

const route = useRoute();
const router = useRouter();
const loading = ref(false);

/**
 * 文章列表
 */
// 新增响应式数据
const articleList = ref([]); // 存储文章列表
const articleDialogVisible = ref(false);

const userStore = useUserStore();

// 获取用户文章列表
const getArticleList = async () => {
  try {
    const response = await findByUserId(userStore.user.id);
    if (response.data?.status === 200) {
      console.log("getArticleList:", response.data.data);
      articleList.value = response.data.data.map((item) => ({
        title: item.post.title || item.postTitle,
        postId: item.post.postId.toString(),
        createdTime: item.post.createdTime,
      }));
    }
  } catch (error) {
    ElMessage.error("获取文章列表失败");
  }
};

// 修改上传按钮点击事件
const openUploadDialog = () => {
  articleDialogVisible.value = true;
  getArticleList(); // 打开对话框时获取文章列表
};

// 跳转到文章编辑页面
const goToEditPage = (row) => {
  console.log("row:", row);
  articleDialogVisible.value = false;
  router.push(`${USER_POST_EDIT_PATH}/${row.postId}`);
};

/**
 * 附件列表
 */

// 附件列表数据
const attachmentList = ref([]);

// 分页
const currentPage = ref(1);
const pageSize = ref(10);
const total = ref(0);

// 获取附件列表
const getAttachmentList = async () => {
  loading.value = true;
  try {
    const response = await findAttachmentsByUserId({
      page: currentPage.value - 1,  // 后端分页从0开始
      size: pageSize.value,
      sort: ['createdTime,desc']  // 默认按创建时间降序
    });
    if (response.data?.status === 200) {
      attachmentList.value = response.data.data.content.map((item) => ({
        id: item.attachmentId,
        name: item.attachmentName,
        type: item.attachmentType,
        url: item.attachmentUrl,
        size: item.size || 0, // 需要后端返回文件大小
        uploadTime: item.createdTime,
        postId: item.postId,
        postTitle: item.postTitle,
      }));
      total.value = response.data.data.totalElements; // 设置总条数
    }
    loading.value = false;
  } catch (error) {
    ElMessage.error("获取附件列表失败");
    loading.value = false;
  }
};

// 添加分页处理函数
const handleSizeChange = (val) => {
  pageSize.value = val;
  currentPage.value = 1; // 切换每页显示数量时重置为第一页
  getAttachmentList();
};

const handleCurrentChange = (val) => {
  currentPage.value = val;
  getAttachmentList();
};

// 删除附件
const handleDelete = (row) => {
  ElMessageBox.confirm("确认删除该附件吗？", "警告", {
    confirmButtonText: "确定",
    cancelButtonText: "取消",
    type: "warning",
  }).then(async () => {
    try {
      console.log(row);
      await deleteAttachment(row.id);
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
  getAttachmentList();
});

/**
 * 文件名格式化
 */
// 新增文件类型映射
const fileTypeMap = {
  "image/": "图片文件",
  "text/": "文本文件",
  "application/pdf": "PDF文件",
  "application/msword": "Word文档",
  "application/vnd.openxmlformats-officedocument.wordprocessingml.document": "Word文档",
  "application/vnd.ms-excel": "Excel文件",
  "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet": "Excel文件",
  "application/vnd.ms-powerpoint": "PPT文件",
  "application/vnd.openxmlformats-officedocument.presentationml.presentation": "PPT文件",
  "application/zip": "压缩文件",
  "application/x-rar-compressed": "压缩文件",
};

// 修改文件名显示 - 去掉时间戳
const formatFileName = (name) => {
  // 匹配类似 "tmp11764715529206642371-文件名" 的格式
  const match = name.match(/tmp\d+-?(.*)/);
  return match ? match[1] : name;
};

// 修改文件类型显示
const formatFileType = (type) => {
  if (!type) return "未知类型";

  // 查找匹配的类型描述
  const matchedType = Object.entries(fileTypeMap).find(([key]) => type.startsWith(key));

  return matchedType ? `${type}\n(${matchedType[1]})` : type;
};
</script>

<template>
  <page-container title="文章附件管理">
    <el-card v-loading="loading">
      <template #header>
        <div class="card-header">
          <span>附件列表</span>
          <el-button type="primary" :icon="Plus" @click="openUploadDialog">
            上传附件
          </el-button>
        </div>
      </template>

      <el-table :data="attachmentList" border stripe>
        <template #empty>
          <el-empty description="暂无附件" />
        </template>
        <el-table-column type="index" label="序号" width="80" align="center" />
        <el-table-column prop="name" label="文件名" show-overflow-tooltip>
          <template #default="{ row }">
            {{ formatFileName(row.name) }}
          </template>
        </el-table-column>
        <el-table-column label="所属文章" width="200">
          <template #default="{ row }">
            <el-tooltip :content="`文章ID: ${row.postId}`" placement="top">
              <span>{{ row.postTitle }}</span>
            </el-tooltip>
          </template>
        </el-table-column>
        <el-table-column prop="type" label="类型" width="150">
          <template #default="{ row }">
            <el-tooltip :content="row.type || '未知'" placement="top">
              <el-tag
                style="
                  white-space: pre-line;
                  height: auto;
                  min-height: 32px;
                  line-height: 1.5;
                  display: flex;
                  align-items: center;
                  justify-content: center;
                "
              >
                {{ formatFileType(row.type) }}
              </el-tag>
            </el-tooltip>
          </template>
        </el-table-column>
        <el-table-column prop="size" label="大小" width="120">
          <template #default="{ row }">
            {{ formatFileSize(row.size) }}
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

      <!-- 添加分页器 -->
      <div class="pagination-container">
        <el-pagination
          v-model:current-page="currentPage"
          v-model:page-size="pageSize"
          :page-sizes="[10, 20, 50]"
          :total="total"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
        />
      </div>
    </el-card>

    <!-- 上传对话框 -->
    <el-dialog v-model="articleDialogVisible" title="选择文章" width="600px">
      <el-table :data="articleList" @row-click="goToEditPage">
        <el-table-column prop="title" label="文章标题" />
        <el-table-column prop="createdTime" label="创建时间" width="180">
          <template #default="{ row }">
            {{ formatDateTime(row.createdTime) }}
          </template>
        </el-table-column>
        <el-table-column label="操作" width="120">
          <template #default="{ row }">
            <el-button type="primary" link @click="goToEditPage(row.postId)">
              选择
            </el-button>
          </template>
        </el-table-column>
      </el-table>
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

.pagination-container {
  margin-top: 20px;
  display: flex;
  justify-content: center;
}
</style>
