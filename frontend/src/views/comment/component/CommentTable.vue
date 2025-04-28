<script setup>
import { ref } from "vue";
import { formatDate } from "@/utils/date.js";
import { useRouter } from "vue-router";
import { BLOG_POST_DETAIL_PATH } from "@/constants/routes/blog";

const props = defineProps({
  data: {
    type: Array,
    required: true,
  },
  loading: Boolean,
  pagination: Object,
  getReplyCount: Function,
  currentReplies: {
    type: Array,
    default: () => [],
  },
  replyDialogVisible: {
    type: Boolean,
    default: false,
  },
  searchForm: {
    type: Object,
    required: true,
  },
  titleOptions: {
    type: Array,
    default: () => [],
  },
  dateShortcuts: {
    type: Array,
    default: () => [],
  },
});

const router = useRouter();

const goToPost = (postId) => {
  if (postId) {
    router.push(BLOG_POST_DETAIL_PATH.replace(':id', postId));
  }
};

const emit = defineEmits([
  "selection-change",
  "delete",
  "view-replies",
  "page-change",
  "size-change",
  "update:replyDialogVisible",
  "search",
  "reset",
  "update:searchForm",
]);
</script>

<template>
  <!-- 搜索区域 -->
  <el-card shadow="never" class="search-card">
    <el-form :model="searchForm" inline>
      <el-form-item label="评论内容">
        <el-input
          v-model="searchForm.content"
          placeholder="请输入评论内容"
          clearable
          @keyup.enter="emit('search')"
        />
      </el-form-item>

      <el-form-item label="文章标题">
        <el-select
          v-model="searchForm.title"
          placeholder="请选择文章"
          clearable
          style="width: 240px"
        >
          <el-option
            v-for="item in titleOptions"
            :key="item.value"
            :label="item.label"
            :value="item.value"
          />
        </el-select>
      </el-form-item>

      <el-form-item label="日期范围">
        <el-date-picker
          v-model="searchForm.dateRange"
          type="daterange"
          range-separator="至"
          start-placeholder="开始日期"
          end-placeholder="结束日期"
          value-format="YYYY-MM-DD"
          :shortcuts="dateShortcuts"
        />
      </el-form-item>

      <el-form-item>
        <el-button type="primary" @click="emit('search')">搜索</el-button>
        <el-button @click="emit('reset')">重置</el-button>
      </el-form-item>
    </el-form>
  </el-card>
  <el-table
    v-loading="loading"
    :data="data"
    border
    stripe
    @selection-change="(selections) => emit('selection-change', selections)"
  >
    <el-table-column type="selection" width="50" align="center" />
    <el-table-column
      type="index"
      label="序号"
      width="60"
      align="center"
      :index="(index) => (pagination.current - 1) * pagination.size + index + 1"
    />

    <!-- 评论内容列 -->
    <el-table-column label="评论内容" min-width="300">
      <template #default="{ row }">
        <div class="comment-content">
          <div class="comment-header">
            <span class="username">{{ row.username }}</span>
            <span class="article-title" @click="goToPost(row.postId)" :class="{ clickable: row.postId }">《{{ row.title }}》</span>
          </div>
          <div class="comment-text">
            <span v-if="row.content.length <= 50 || row.isExpanded">
              {{ row.content }}
            </span>
            <span v-else>{{ row.content.slice(0, 50) }}...</span>
            <el-button
              v-if="row.content.length > 50"
              type="primary"
              link
              @click="row.isExpanded = !row.isExpanded"
            >
              {{ row.isExpanded ? "收起" : "展开" }}
            </el-button>
            <span v-if="row.parentId" class="reply-info">
              【回复：{{ row.parentContent }}】
            </span>
          </div>
        </div>
      </template>
    </el-table-column>

    <el-table-column prop="likes" label="点赞数" width="100" align="center" />

    <el-table-column prop="createdAt" label="评论时间" width="160" align="center">
      <template #default="{ row }">
        {{ formatDate(row.createdAt) }}
      </template>
    </el-table-column>

    <!-- 操作列 -->
    <el-table-column label="操作" width="160" align="center">
      <template #default="{ row }">
        <div class="operation-buttons">
          <el-button
            v-if="getReplyCount(row.commentId) > 0"
            size="small"
            type="primary"
            plain
            @click="emit('view-replies', row)"
          >
            回复({{ getReplyCount(row.commentId) }})
          </el-button>
          <el-button
            size="small"
            type="danger"
            plain
            @click="emit('delete', row.commentId)"
          >
            删除
          </el-button>
        </div>
      </template>
    </el-table-column>
  </el-table>

  <!-- 分页 -->
  <div class="pagination">
    <el-pagination
      v-model:current-page="pagination.current"
      v-model:page-size="pagination.size"
      :page-sizes="pagination.pageSizes"
      :total="pagination.total"
      :layout="pagination.layout"
      background
      @size-change="$emit('size-change', $event)"
      @current-change="$emit('page-change', $event)"
    />
  </div>

  <!-- 回复对话框 -->
  <el-dialog
    :model-value="replyDialogVisible"
    title="评论回复"
    width="60%"
    @update:modelValue="(val) => emit('update:replyDialogVisible', val)"
  >
    <el-empty v-if="!currentReplies.length" description="暂无回复" />
    <el-table v-else :data="currentReplies" border>
      <el-table-column prop="content" label="回复内容">
        <template #default="{ row }">
          <div class="reply-content" :style="{ paddingLeft: row.level * 20 + 'px' }">
            <span v-if="row.level > 0" class="reply-indicator">↳</span>
            {{ row.content }}
          </div>
        </template>
      </el-table-column>
      <el-table-column prop="username" label="回复用户" width="150" />
      <el-table-column prop="createdAt" label="回复时间" width="160">
        <template #default="{ row }">
          {{ formatDate(row.createdAt) }}
        </template>
      </el-table-column>
      <el-table-column label="操作" width="100">
        <template #default="{ row }">
          <el-button
            size="small"
            type="danger"
            @click="
              () => {
                emit('delete', row.commentId);
                emit('update:replyDialogVisible', false);
                getReplyCount(row.parentId);
              }
            "
          >
            删除
          </el-button>
        </template>
      </el-table-column>
    </el-table>
  </el-dialog>
</template>

<style scoped>
.search-card {
  margin-bottom: 20px;
}

.comment-content {
  word-break: break-word;
  line-height: 1.5;
}

.comment-header {
  margin-bottom: 4px;
  font-size: 13px;
}

.username {
  color: var(--el-color-primary);
  margin-right: 8px;
}

.article-title {
  color: var(--el-text-color-regular);
}

.comment-text {
  color: var(--el-text-color-primary);
  display: flex;
  align-items: flex-start;
  gap: 8px;
  flex-wrap: wrap;
}

.reply-info {
  color: var(--el-text-color-secondary);
  margin-left: 8px;
}

.pagination {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}

.reply-content {
  display: flex;
  align-items: center;
}

.reply-indicator {
  color: var(--el-text-color-secondary);
  margin-right: 8px;
  font-size: 16px;
}

.operation-buttons {
  display: flex;
  gap: 4px;
  justify-content: center;
  flex-wrap: wrap;
}

.operation-buttons .el-button {
  margin: 0;
  min-width: 70px;
}
</style>

<style lang="scss" scoped>
.comment-content {
  word-break: break-word;
  line-height: 1.5;

  .comment-header {
    margin-bottom: 4px;
    font-size: 13px;

    .username {
      color: var(--el-color-primary);
      margin-right: 8px;
    }

    .article-title {
      color: var(--el-text-color-regular);

      &.clickable {
        cursor: pointer;
        color: var(--el-color-primary);
        transition: all 0.3s ease;

        &:hover {
          color: var(--el-color-primary-light-3);
          text-decoration: underline;
        }
      }
    }
  }
}

.pagination-container {
  margin-top: 20px;
  display: flex;
  justify-content: center;
}
</style>
