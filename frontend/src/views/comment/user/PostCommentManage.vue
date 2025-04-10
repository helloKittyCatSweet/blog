<script setup>
import { ref, onMounted } from "vue";
import { ElMessage, ElMessageBox } from "element-plus";
import { formatDate } from "@/utils/date.js";
import PageContainer from "@/components/PageContainer.vue";
import { findByAuthor, deleteById } from "@/api/post/comment";
import CommentTable from "../component/CommentTable.vue";
import { useComment } from "../component/useComment.js";

const {
  pagination,
  commentList,
  loading,
  selectedIds,
  searchForm,
  titleOptions,
  dateShortcuts,
  replyDialogVisible,
  currentReplies,
  getReplyCount,
  handleExport,
  handleDelete,
  handleBatchDelete,
  handleViewReplies,
  handleSearch,
  resetSearch,
  handleSelectionChange,
  fetchComments,
} = useComment(findByAuthor);

// 初始化加载
onMounted(() => {
  fetchComments();
});
</script>

<template>
  <PageContainer title="评论管理">
    <template #actions>
      <el-button type="primary" @click="handleExport">导出评论</el-button>
      <el-button type="danger" :disabled="!selectedIds.length" @click="handleBatchDelete">
        批量删除
      </el-button>
    </template>

    <CommentTable
      :data="commentList"
      :loading="loading"
      :pagination="pagination"
      :get-reply-count="getReplyCount"
      :current-replies="currentReplies"
      :search-form="searchForm"
      :title-options="titleOptions"
      :date-shortcuts="dateShortcuts"
      v-model:reply-dialog-visible="replyDialogVisible"
      @selection-change="handleSelectionChange"
      @delete="handleDelete"
      @view-replies="handleViewReplies"
      @page-change="
        (page) => {
          pagination.current = page;
          fetchComments();
        }
      "
      @size-change="
        (size) => {
          pagination.size = size;
          pagination.current = 1;
          fetchComments();
        }
      "
      @search="handleSearch"
      @reset="resetSearch"
    />
  </PageContainer>
</template>
