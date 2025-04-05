<script setup>
import { ref, onMounted } from "vue";
import { ElMessage, ElMessageBox } from "element-plus";
import { Search, Delete, View, ChatLineRound } from "@element-plus/icons-vue";
import PageContainer from "@/components/PageContainer.vue";
import { formatDateTime } from "@/utils/format";

const loading = ref(false);
const tableData = ref([]);
const total = ref(0);

// 搜索条件
const searchForm = ref({
  keyword: "",
  type: "all", // all-全部评论, received-收到的评论, sent-发出的评论
  currentPage: 1,
  pageSize: 10,
});

// 回复对话框
const replyDialogVisible = ref(false);
const replyForm = ref({
  content: "",
  parentCommentId: null,
  postId: null,
});

// 获取评论列表
const getCommentList = async () => {
  loading.value = true;
  try {
    // TODO: 调用获取评论列表API
    loading.value = false;
  } catch (error) {
    ElMessage.error("获取评论列表失败");
    loading.value = false;
  }
};

// 删除评论
const handleDelete = (row) => {
  ElMessageBox.confirm("确认删除该评论吗？删除后将无法恢复。", "警告", {
    confirmButtonText: "确定",
    cancelButtonText: "取消",
    type: "warning",
  }).then(async () => {
    try {
      // TODO: 调用删除评论API
      ElMessage.success("删除成功");
      getCommentList();
    } catch (error) {
      ElMessage.error("删除失败");
    }
  });
};

// 回复评论
const handleReply = (row) => {
  replyForm.value = {
    content: "",
    parentCommentId: row.commentId,
    postId: row.postId,
  };
  replyDialogVisible.value = true;
};

// 提交回复
const submitReply = async () => {
  if (!replyForm.value.content.trim()) {
    ElMessage.warning("请输入回复内容");
    return;
  }
  try {
    // TODO: 调用回复评论API
    ElMessage.success("回复成功");
    replyDialogVisible.value = false;
    getCommentList();
  } catch (error) {
    ElMessage.error("回复失败");
  }
};

// 查看文章
const handleViewPost = (row) => {
  window.open(`/post/${row.postId}`, "_blank");
};

onMounted(() => {
  getCommentList();
});
</script>

<template>
  <page-container title="评论管理">
    <!-- 搜索栏 -->
    <el-card class="search-card">
      <el-row :gutter="20">
        <el-col :span="6">
          <el-input
            v-model="searchForm.keyword"
            placeholder="搜索评论内容"
            clearable
            @keyup.enter="getCommentList"
          >
            <template #suffix>
              <el-icon class="el-input__icon"><Search /></el-icon>
            </template>
          </el-input>
        </el-col>
        <el-col :span="6">
          <el-select
            v-model="searchForm.type"
            placeholder="评论类型"
            @change="getCommentList"
          >
            <el-option label="全部评论" value="all" />
            <el-option label="收到的评论" value="received" />
            <el-option label="发出的评论" value="sent" />
          </el-select>
        </el-col>
      </el-row>
    </el-card>

    <!-- 评论列表 -->
    <el-card class="comment-card">
      <el-table v-loading="loading" :data="tableData" border stripe row-key="commentId">
        <template #empty>
          <el-empty description="暂无评论" />
        </template>

        <el-table-column label="评论内容" min-width="300">
          <template #default="{ row }">
            <div class="comment-content">
              <div class="comment-info">
                <span class="username">{{ row.user?.username }}</span>
                <span class="time">{{ formatDateTime(row.createdAt) }}</span>
              </div>
              <div class="content">{{ row.content }}</div>
              <div v-if="row.parentComment" class="reply-info">
                <el-tag size="small" type="info"
                  >回复 @{{ row.parentComment.user?.username }}</el-tag
                >
                <div class="reply-content">{{ row.parentComment.content }}</div>
              </div>
            </div>
          </template>
        </el-table-column>

        <el-table-column prop="post.title" label="文章" show-overflow-tooltip width="200">
          <template #default="{ row }">
            <el-link type="primary" @click="handleViewPost(row)">
              {{ row.post?.title || "文章已删除" }}
            </el-link>
          </template>
        </el-table-column>

        <el-table-column label="点赞数" width="100" align="center">
          <template #default="{ row }">
            <el-tag type="success">{{ row.likes || 0 }}</el-tag>
          </template>
        </el-table-column>

        <el-table-column label="操作" width="150" align="center">
          <template #default="{ row }">
            <el-button
              type="primary"
              :icon="ChatLineRound"
              circle
              @click="handleReply(row)"
              title="回复"
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

      <!-- 分页 -->
      <div class="pagination">
        <el-pagination
          v-model:current-page="searchForm.currentPage"
          v-model:page-size="searchForm.pageSize"
          :total="total"
          :page-sizes="[10, 20, 50, 100]"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="
            (val) => {
              searchForm.pageSize = val;
              getCommentList();
            }
          "
          @current-change="
            (val) => {
              searchForm.currentPage = val;
              getCommentList();
            }
          "
        />
      </div>
    </el-card>

    <!-- 回复对话框 -->
    <el-dialog v-model="replyDialogVisible" title="回复评论" width="500px">
      <el-form>
        <el-form-item>
          <el-input
            v-model="replyForm.content"
            type="textarea"
            :rows="4"
            placeholder="请输入回复内容"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="replyDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="submitReply">回复</el-button>
        </span>
      </template>
    </el-dialog>
  </page-container>
</template>

<style scoped>
.search-card {
  margin-bottom: 20px;
}

.comment-card {
  margin-bottom: 20px;
}

.pagination {
  margin-top: 20px;
  text-align: right;
}

.comment-content {
  padding: 8px 0;
}

.comment-info {
  margin-bottom: 8px;
}

.username {
  font-weight: bold;
  margin-right: 10px;
}

.time {
  color: #999;
  font-size: 12px;
}

.content {
  line-height: 1.5;
  margin-bottom: 8px;
}

.reply-info {
  background-color: #f5f7fa;
  padding: 8px;
  border-radius: 4px;
  margin-top: 8px;
}

.reply-content {
  color: #666;
  margin-top: 8px;
  font-size: 13px;
}
</style>
