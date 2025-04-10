<script setup>
import { onMounted } from "vue";
import PageContainer from "@/components/PageContainer.vue";
import { findByUserId } from "@/api/post/comment";
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
} = useComment(findByUserId);

// 初始化加载
onMounted(() => {
  fetchComments();
});
</script>

<template>
  <PageContainer title="我的评论">
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
