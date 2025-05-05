<script setup>
import { ref, onMounted, computed } from "vue";
import { useRoute, useRouter } from "vue-router";
import { ElMessage } from "element-plus";
import { Warning, Download } from "@element-plus/icons-vue";
import { Document } from "@element-plus/icons-vue";

import PageContainer from "@/components/PageContainer.vue";
import PostHeader from "@/components/blog/post/PostHeader.vue";
import PostInteraction from "@/components/blog/post/PostInteraction.vue";
import PostComment from "@/components/blog/post/PostComment.vue";
import PostExport from "@/components/blog/post/PostExport.vue";
import PostReport from "@/components/blog/post/PostReport.vue";

import { MdPreview } from "md-editor-v3";
import "md-editor-v3/lib/style.css";

import { findById } from "@/api/post/post.js";
import { findPostExplicit } from "@/api/user/userActivity.js";
import { useUserStore } from "@/stores/modules/user.js";
import { usePostStore } from "@/stores/modules/post.js";
import { LOGIN_PATH } from "@/constants/routes/base.js";
import { BLOG_USER_DETAIL_PATH } from "@/constants/routes/blog";

const route = useRoute();
const router = useRouter();
const loading = ref(false);

const postStore = usePostStore();

// 文章数据
const post = ref({
  postId: null,
  title: "",
  content: "",
  coverImage: "",
  isPublished: false,
  isDraft: true,
  visibility: "PUBLIC",
  version: 1,
  category: null,
  tags: [],
  author: "",
  summary: "",
  createdTime: "",
  updatedTime: "",
  viewCount: 0,
  attachments: [], // 添加附件数组
});

// 获取文章详情
const getPostDetail = async (id) => {
  loading.value = true;
  try {
    const response = await findById(id);
    if (response.data.status === 200) {
      const postData = response.data.data;
      post.value = {
        postId: postData.post.postId,
        userId: postData.post.userId,
        title: postData.post.title,
        content: postData.post.content,
        coverImage: postData.post.coverImage,
        isPublished: postData.post.isPublished,
        isDraft: postData.post.isDraft,
        visibility: postData.post.visibility,
        version: postData.post.version,
        category: postData.category?.categoryId
          ? {
              categoryId: postData.category.categoryId,
              categoryName: postData.category.name,
              description: postData.category.description,
            }
          : null,
        tags:
          postData.tags
            ?.filter((tag) => tag.tagId && tag.name)
            .map((tag) => ({
              tagId: tag.tagId,
              tagName: tag.name,
            })) || [],
        author: postData.author,
        summary: postData.post.abstractContent,
        createdTime: postData.post.createdAt,
        updatedTime: postData.post.updatedAt,
        viewCount: postData.post.views || 0,
        attachments: postData.attachments || [], // 初始化附件数组
      };
      // 更新 store 中的浏览量
      postStore.updatePostViews(id, post.value.viewCount)
    }
  } catch (error) {
    ElMessage.error("获取文章详情失败");
  } finally {
    loading.value = false;
  }
};

const userStore = useUserStore();

// 添加交互状态
const interactionState = ref({
  isLiked: false,
  isFavorited: false,
  likeActivityId: null, // 点赞获得id
  favoriteActivityId: null, // 收藏获得id
});

// 添加附件列表状态
const attachmentsLoading = ref(false);

// 格式化文件名 - 去掉时间戳
const formatFileName = (name) => {
  const match = name.match(/tmp\d+-?(.*)/);
  return match ? match[1] : name;
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

// 下载附件
const handleDownload = (attachment) => {
  try {
    window.open(attachment.url, "_blank");
  } catch (error) {
    ElMessage.error("下载失败");
  }
};

onMounted(async () => {
  const postId = route.params.id;
  if (postId) {
    await getPostDetail(postId);
    await refreshPost();

    // 设置默认的交互状态
    interactionState.value = {
      isLiked: false,
      isFavorited: false,
      likeActivityId: null,
      favoriteActivityId: null,
    };

    // 只在登录状态下获取交互数据
    if (userStore.isLoggedIn) {
      try {
        const response = await findPostExplicit(userStore.user.id, postId, "LIKE");
        const activityData = response.data.data;
        // 只有当存在有效的点赞记录（包含activityId）时才标记为已点赞
        interactionState.value.isLiked = activityData?.activityId != null;
        if (activityData?.activityId) {
          interactionState.value.likeActivityId = activityData.activityId;
        }

        const favoriteResponse = await findPostExplicit(
          userStore.user.id,
          postId,
          "FAVORITE"
        );
        const favoriteData = favoriteResponse.data.data;
        interactionState.value.isFavorited = favoriteData?.activityId != null;
        if (favoriteData?.activityId) {
          interactionState.value.favoriteActivityId = favoriteData.activityId;
        }
      } catch (error) {
        console.error("获取交互状态失败:", error);
        // 出错时保持默认值
      }
    }
  }
});

/**
 * 登录跳转
 */
const goToLogin = () => {
  // 将当前路由信息保存到 query 参数中
  router.push({
    path: LOGIN_PATH,
    query: {
      redirect: route.fullPath,
    },
  });
};

/**
 * 评论
 */

// 刷新文章和评论
const refreshPost = async () => {
  if (post.value.postId) {
    // 刷新交互状态
    if (userStore.isLoggedIn) {
      try {
        // 重新获取点赞状态
        const response = await findPostExplicit(
          userStore.user.id,
          post.value.postId,
          "LIKE"
        );
        const activityData = response.data.data;
        interactionState.value.isLiked = activityData?.activityId != null;
        interactionState.value.likeActivityId = activityData?.activityId || null;

        // 重新获取收藏状态
        const favoriteResponse = await findPostExplicit(
          userStore.user.id,
          post.value.postId,
          "FAVORITE"
        );
        const favoriteData = favoriteResponse.data.data;
        interactionState.value.isFavorited = favoriteData?.activityId != null;
        interactionState.value.favoriteActivityId = favoriteData?.activityId || null;
      } catch (error) {
        console.error("刷新交互状态失败:", error);
      }
    }
    // 最后刷新文章详情
    await getPostDetail(post.value.postId);
  }
};

/**
 * 抽屉和评论数量
 */
// 添加抽屉相关状态
const drawerVisible = ref(false);

// 添加评论数量状态
const commentCount = ref(0);

// 更新评论数量的方法
const updateCommentCount = (count) => {
  commentCount.value = count;
};

/**
 * 用户名点击处理函数
 */
const handleAuthorClick = (userId) => {
  if (userId) {
    router.push({
      path: BLOG_USER_DETAIL_PATH.replace(":id", userId),
      query: {
        redirect: route.fullPath, // 添加当前页面路径作为返回地址
      },
    });
  }
};

// 添加时间格式化函数
const formatDateTime = (dateTimeString) => {
  if (!dateTimeString) return '-';
  const date = new Date(dateTimeString);
  const year = date.getFullYear();
  const month = String(date.getMonth() + 1).padStart(2, '0');
  const day = String(date.getDate()).padStart(2, '0');
  const hour = String(date.getHours()).padStart(2, '0');
  const minute = String(date.getMinutes()).padStart(2, '0');
  return `${year}-${month}-${day} ${hour}:${minute}`;
};
</script>

<template>
  <page-container>
    <el-card v-loading="loading" class="post-view">
      <!-- 添加导出按钮 -->
      <post-export v-if="userStore.isLoggedIn" :post="post" :author="post.author" />
      <!-- 文章头部信息 -->
      <post-header
        :title="post.title"
        :author="post.author"
        :created-time="post.createdTime"
        :view-count="post.viewCount"
        :category="post.category?.categoryId ? post.category : null"
        :tags="post.tags.length > 0 ? post.tags : null"
        :user-id="post.userId"
        @click-author="handleAuthorClick"
      />

      <!-- 文章封面 -->
      <div v-if="post.coverImage" class="post-cover">
        <el-image :src="post.coverImage" fit="cover" />
      </div>

      <!-- 文章摘要 -->
      <div v-if="post.summary" class="post-summary">
        <div class="summary-title">摘要</div>
        <div class="summary-content">{{ post.summary }}</div>
      </div>

      <!-- 在文章内容后，交互区域前添加附件展示区域 -->
      <div v-if="post.attachments.length > 0 && userStore.isLoggedIn" class="post-attachments">
        <div class="attachments-header">
          <h3>文章附件</h3>
          <span class="attachment-count">共 {{ post.attachments.length }} 个附件</span>
        </div>
        <el-table
          :data="post.attachments"
          v-loading="attachmentsLoading"
          style="width: 100%"
          class="attachment-table"
        >
          <el-table-column prop="attachmentName" label="文件名" min-width="300" show-overflow-tooltip>
            <template #default="{ row }">
              <div class="file-name">
                <el-icon class="file-icon"><Document /></el-icon>
                <span>{{ formatFileName(row.attachmentName) }}</span>
              </div>
            </template>
          </el-table-column>
          <el-table-column prop="attachmentType" label="类型" width="150" show-overflow-tooltip>
            <template #default="{ row }">
              <el-tag size="small">{{ row.attachmentType.split('/').pop().toUpperCase() }}</el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="size" label="大小" width="120">
            <template #default="{ row }">
              {{ formatFileSize(row.size) }}
            </template>
          </el-table-column>
          <el-table-column prop="createdTime" label="上传时间" width="180">
            <template #default="{ row }">
              {{ formatDateTime(row.createdTime) }}
            </template>
          </el-table-column>
          <el-table-column label="操作" width="100" fixed="right">
            <template #default="{ row }">
              <el-button
                type="primary"
                :icon="Download"
                circle
                @click="handleDownload(row)"
                title="下载"
              />
            </template>
          </el-table-column>
        </el-table>
      </div>

      <!-- 文章内容 -->
      <div class="post-content">
        <md-preview
          :modelValue="post.content"
          :showCodeRowNumber="true"
          language="zh-CN"
        />
      </div>

      <!-- 在文章内容后添加交互区域 -->
      <div class="post-interaction">
        <template v-if="userStore.isLoggedIn">
          <div class="interaction-wrapper">
            <div class="interaction-left">
              <post-interaction
                v-model:is-liked="interactionState.isLiked"
                v-model:is-favorited="interactionState.isFavorited"
                :post-id="post.postId"
                :title="post.title"
                v-model:like-activity-id="interactionState.likeActivityId"
                v-model:favorite-activity-id="interactionState.favoriteActivityId"
                @refresh="refreshPost"
              />
            </div>
            <div class="interaction-right">
              <post-report
                v-if="post.postId"
                :post-id="post.postId"
                :title="post.title"
              />
            </div>
          </div>
        </template>
        <template v-else>
          <div class="interaction-login-tip">
            <el-tooltip content="请登录后操作" placement="top" effect="light">
              <div class="interaction-buttons">
                <el-button type="primary" plain @click="goToLogin">
                  登录后即可点赞、收藏和评论
                </el-button>
              </div>
            </el-tooltip>
          </div>
        </template>
      </div>

      <!-- 评论组件区域 -->
      <div class="post-comments-section">
        <post-comment
          v-if="post.postId"
          :post-id="post.postId"
          :title="post.title"
          :user-id="post.userId"
          :display-limit="5"
          :show-view-more="true"
          @refresh="refreshPost"
          @update:comment-count="updateCommentCount"
          @view-all="drawerVisible = true"
        />

        <!-- 添加查看更多按钮 -->
        <div class="view-more-comments" v-if="!drawerVisible && commentCount > 5">
          <el-button type="primary" text @click="drawerVisible = true">
            查看全部评论（{{ commentCount }}条）
          </el-button>
        </div>
      </div>

      <!-- 添加评论抽屉 -->
      <el-drawer v-model="drawerVisible" title="全部评论" direction="rtl" size="60%">
        <post-comment
          v-if="post.postId"
          :post-id="post.postId"
          :title="post.title"
          :user-id="post.userId"
          :display-limit="null"
          @refresh="refreshPost"
        />
      </el-drawer>

      <!-- 文章底部信息 -->
      <div class="post-footer">
        <div class="update-time">最后更新于：{{ post.updatedTime }}</div>
      </div>
    </el-card>
  </page-container>
</template>

<style scoped>
.post-view {
  width: 900px;
  margin: 0 auto;
}

.post-cover {
  margin-bottom: 24px;
  border-radius: 8px;
  overflow: hidden;
}

.post-cover :deep(.el-image) {
  width: 100%;
  max-height: 400px;
}

.post-summary {
  margin-bottom: 24px;
  padding: 16px;
  background-color: var(--el-fill-color-lighter);
  border-radius: 4px;
}

.summary-title {
  font-weight: bold;
  margin-bottom: 8px;
  color: var(--el-text-color-primary);
}

.summary-content {
  color: var(--el-text-color-regular);
  line-height: 1.6;
}

.post-content {
  margin-bottom: 24px;
}

.post-content :deep(.md-preview) {
  background-color: transparent;
  border: none;
}

.post-footer {
  padding-top: 16px;
  border-top: 1px solid var(--el-border-color-lighter);
  color: var(--el-text-color-secondary);
  font-size: 14px;
}

.update-time {
  text-align: right;
}

.post-interaction {
  margin: 24px 0;
  padding: 16px;
  border-radius: 8px;
  background-color: var(--el-fill-color-lighter);
}

.interaction-login-tip {
  display: flex;
  justify-content: center;
  align-items: center;
}

.interaction-buttons {
  display: flex;
  gap: 16px;
  justify-content: center;
}

.post-comments-section {
  margin: 24px 0;
}

.view-more-comments {
  text-align: center;
  margin-top: 16px;
  padding: 8px 0;
  border-top: 1px solid var(--el-border-color-lighter);
}

/* 自定义抽屉样式 */
:deep(.el-drawer__header) {
  margin-bottom: 16px;
  padding: 16px 24px;
  border-bottom: 1px solid var(--el-border-color-lighter);
}

:deep(.el-drawer__body) {
  padding: 0 24px;
  height: calc(100% - 60px);
  overflow-y: auto;
}

.post-interaction {
  margin: 24px 0;
  padding: 20px;
  border-radius: 12px;
  background-color: var(--el-fill-color-lighter);
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.05);
  transition: all 0.3s ease;
}

.post-interaction:hover {
  box-shadow: 0 4px 16px 0 rgba(0, 0, 0, 0.1);
}

.interaction-wrapper {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 20px;
}

.interaction-left {
  flex-grow: 1;
}

.interaction-right {
  display: flex;
  align-items: center;
  padding-left: 20px;
  border-left: 1px solid var(--el-border-color-lighter);
}

.interaction-login-tip {
  display: flex;
  justify-content: center;
  align-items: center;
  padding: 16px;
}

.interaction-buttons {
  display: flex;
  gap: 16px;
  justify-content: center;
}

.interaction-buttons .el-button {
  transition: all 0.3s ease;
}

.interaction-buttons .el-button:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.post-attachments {
  margin: 24px 0;
  padding: 20px;
  border-radius: 12px;
  background-color: var(--el-fill-color-lighter);
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.05);
}

.attachments-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
}

.attachments-header h3 {
  margin: 0;
  font-size: 18px;
  color: var(--el-text-color-primary);
}

.attachment-count {
  color: var(--el-text-color-secondary);
  font-size: 14px;
}

.attachment-table {
  border-radius: 8px;
  overflow: hidden;
}

.file-name {
  display: flex;
  align-items: center;
  gap: 8px;
}

.file-icon {
  color: var(--el-text-color-secondary);
}
</style>
