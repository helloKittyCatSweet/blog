<script setup>
import { ref, onMounted } from "vue";
import { ElMessage, ElMessageBox } from "element-plus";
import { formatDate } from "@/utils/date.js";
import PageContainer from "@/components/PageContainer.vue";
import { findAll } from "@/api/post/comment";

// 搜索表单
const searchForm = ref({
  content: "",
  postId: "",
  userId: "",
  dateRange: [],
});

// 分页数据
const pagination = ref({
  current: 1,
  size: 10,
  total: 0,
});

// 评论列表
const commentList = ref([]);
const loading = ref(false);
const selectedIds = ref([]);

// 回复对话框
const replyDialogVisible = ref(false);
const currentReplies = ref([]);

// 获取评论列表
const fetchComments = async () => {
  try {
    loading.value = true;
    const params = {
      ...searchForm.value,
      page: pagination.value.current,
      size: pagination.value.size,
      startDate: searchForm.value.dateRange?.[0],
      endDate: searchForm.value.dateRange?.[1],
    };

    const res = await findAll();
    commentList.value = res.data.records;
    pagination.value.total = res.data.total;
  } catch (error) {
    console.error("获取评论列表失败:", error);
    ElMessage.error("获取评论列表失败");
  } finally {
    loading.value = false;
  }
};

// 搜索
const handleSearch = () => {
  pagination.value.current = 1;
  fetchComments();
};

// 重置搜索
const resetSearch = () => {
  searchForm.value = {
    content: "",
    postId: "",
    userId: "",
    dateRange: [],
  };
  handleSearch();
};

// 选择项变化
const handleSelectionChange = (selection) => {
  selectedIds.value = selection.map((item) => item.commentId);
};

// 批量删除
const handleBatchDelete = async () => {
  try {
    await ElMessageBox.confirm("确定要删除选中的评论吗？", "提示", {
      type: "warning",
    });

    await commentApi.batchDeleteComment(selectedIds.value);
    ElMessage.success("删除成功");
    fetchComments();
    selectedIds.value = [];
  } catch (error) {
    if (error !== "cancel") {
      ElMessage.error("删除失败");
    }
  }
};

// 单个删除
const handleDelete = async (id) => {
  try {
    await ElMessageBox.confirm("确定要删除这条评论吗？", "提示", {
      type: "warning",
    });

    await commentApi.deleteComment(id);
    ElMessage.success("删除成功");
    fetchComments();
  } catch (error) {
    if (error !== "cancel") {
      ElMessage.error("删除失败");
    }
  }
};

// 查看回复
const handleViewReplies = (comment) => {
  currentReplies.value = comment.replies;
  replyDialogVisible.value = true;
};

// 导出评论
const handleExport = async () => {
  try {
    loading.value = true;
    const params = {
      ...searchForm.value,
      startDate: searchForm.value.dateRange?.[0],
      endDate: searchForm.value.dateRange?.[1],
    };

    await commentApi.exportComments(params);
    ElMessage.success("导出任务已开始，请稍后查看下载");
  } catch (error) {
    console.error("导出失败:", error);
    ElMessage.error("导出失败");
  } finally {
    loading.value = false;
  }
};

// 初始化加载
onMounted(() => {
  fetchComments();
});
</script>

<template>
  <PageContainer title="用户个人设置管理">
    <template #actions>
      <el-button type="primary" @click="handleExport">导出用户行为</el-button>
      <el-button type="danger" :disabled="!selectedIds.length" @click="handleBatchDelete">
        批量删除
      </el-button>
    </template>

    <!-- 搜索区域 -->
    <el-card shadow="never" class="search-card">
      <el-form :model="searchForm" inline>
        <el-form-item label="评论内容">
          <el-input v-model="searchForm.content" placeholder="请输入评论内容" clearable />
        </el-form-item>

        <el-form-item label="文章ID">
          <el-input v-model="searchForm.postId" placeholder="请输入文章ID" clearable />
        </el-form-item>

        <el-form-item label="用户ID">
          <el-input v-model="searchForm.userId" placeholder="请输入用户ID" clearable />
        </el-form-item>

        <el-form-item label="日期范围">
          <el-date-picker
            v-model="searchForm.dateRange"
            type="daterange"
            range-separator="至"
            start-placeholder="开始日期"
            end-placeholder="结束日期"
            value-format="YYYY-MM-DD"
          />
        </el-form-item>

        <el-form-item>
          <el-button type="primary" @click="handleSearch">搜索</el-button>
          <el-button @click="resetSearch">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <!-- 评论表格 -->
    <el-table
      v-loading="loading"
      :data="commentList"
      border
      stripe
      @selection-change="handleSelectionChange"
    >
      <el-table-column type="selection" width="50" align="center" />

      <el-table-column prop="commentId" label="ID" width="80" align="center" />

      <el-table-column label="评论内容" min-width="200">
        <template #default="{ row }">
          <div class="comment-content">
            {{ row.content }}
            <el-tag v-if="row.parentCommentId" size="small" type="info">回复</el-tag>
          </div>
        </template>
      </el-table-column>

      <el-table-column label="关联信息" width="180">
        <template #default="{ row }">
          <div class="related-info">
            <div>文章: {{ row.post?.title || `ID: ${row.postId}` }}</div>
            <div>用户: {{ row.user?.username || `ID: ${row.userId}` }}</div>
          </div>
        </template>
      </el-table-column>

      <el-table-column prop="likes" label="点赞数" width="100" align="center" />

      <el-table-column prop="createdAt" label="评论时间" width="160" align="center">
        <template #default="{ row }">
          {{ formatDate(row.createdAt) }}
        </template>
      </el-table-column>

      <el-table-column label="操作" width="180" align="center" fixed="right">
        <template #default="{ row }">
          <el-button
            size="small"
            @click="handleViewReplies(row)"
            v-if="row.replies?.length"
          >
            查看回复({{ row.replies.length }})
          </el-button>
          <el-button size="small" type="danger" @click="handleDelete(row.commentId)">
            删除
          </el-button>
        </template>
      </el-table-column>
    </el-table>

    <!-- 分页 -->
    <div class="pagination">
      <el-pagination
        v-model:current-page="pagination.current"
        v-model:page-size="pagination.size"
        :total="pagination.total"
        :page-sizes="[10, 20, 50, 100]"
        layout="total, sizes, prev, pager, next, jumper"
        @size-change="fetchComments"
        @current-change="fetchComments"
      />
    </div>

    <!-- 回复对话框 -->
    <el-dialog v-model="replyDialogVisible" title="评论回复" width="60%">
      <el-table :data="currentReplies" border>
        <el-table-column prop="content" label="回复内容" />
        <el-table-column prop="user.username" label="回复用户" width="150" />
        <el-table-column prop="createdAt" label="回复时间" width="160">
          <template #default="{ row }">
            {{ formatDate(row.createdAt) }}
          </template>
        </el-table-column>
        <el-table-column label="操作" width="100">
          <template #default="{ row }">
            <el-button size="small" type="danger" @click="handleDelete(row.commentId)">
              删除
            </el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-dialog>
  </PageContainer>
</template>

<style scoped>
.search-card {
  margin-bottom: 20px;
}

.comment-content {
  word-break: break-word;
  line-height: 1.5;
}

.related-info {
  font-size: 12px;
  color: var(--el-text-color-secondary);
  line-height: 1.5;
}

.pagination {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}
</style>
