<script setup>
import { ref, onMounted } from "vue";
import { useRoute } from "vue-router";
import { ElMessage } from "element-plus";
import PageContainer from "@/components/PageContainer.vue";
import PostHeader from "@/components/blog/post/PostHeader.vue";
import PostInteraction from "@/components/blog/post/PostInteraction.vue";

import { User, Clock, View, ChatLineRound, Star, Pointer } from "@element-plus/icons-vue";
import { MdPreview } from "md-editor-v3";
import "md-editor-v3/lib/style.css";

import { findById } from "@/api/post/post.js";
import { findExplicit as findExplicitUserActivity } from "@/api/user/userActivity.js";
import { useUserStore } from "@/stores/modules/user.js";

const route = useRoute();
const loading = ref(false);

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
        title: postData.post.title,
        content: postData.post.content,
        coverImage: postData.post.coverImage,
        isPublished: postData.post.isPublished,
        isDraft: postData.post.isDraft,
        visibility: postData.post.visibility,
        version: postData.post.version,
        category: {
          categoryId: postData.category?.categoryId,
          categoryName: postData.category?.name, // 修改这里，使用 name 而不是 categoryName
          description: postData.category?.description,
        },
        tags:
          postData.tags?.map((tag) => ({
            tagId: tag.tagId,
            tagName: tag.name, // 修改这里，使用 name 而不是 tagName
          })) || [],
        author: postData.author,
        summary: postData.post.abstractContent, // 修改这里，使用 abstractContent
        createdTime: postData.post.createdAt, // 修改这里，使用 createdAt
        updatedTime: postData.post.updatedAt, // 修改这里，使用 updatedAt
        viewCount: postData.post.views || 0, // 修改这里，使用 views
      };
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

onMounted(async () => {
  const postId = route.params.id;
  if (postId) {
    await getPostDetail(postId);
    // 只有在用户已登录的情况下才获取状态
    if (userStore.isLoggedIn && userStore.user.id) {
      // 获取点赞状态
      try {
        const response = await findExplicitUserActivity(
          userStore.user.id,
          postId,
          "LIKE"
        );
        const activityData = response.data.data;
        interactionState.value.isLiked = activityData !== null;
        if (activityData) {
          interactionState.value.likeActivityId = activityData.activityId;
        }

        // 获取收藏状态
        const favoriteResponse = await findExplicitUserActivity(
          userStore.user.id,
          postId,
          "FAVORITE"
        );
        // console.log("获取用户收藏状态：", favoriteResponse.data.data);
        const favoriteData = favoriteResponse.data.data;
        // 只有当数据存在且至少有 activityId 时才认为是已收藏
        interactionState.value.isFavorited = favoriteData?.activityId != null;
        if (favoriteData?.activityId) {
          interactionState.value.favoriteActivityId = favoriteData.activityId;
        } else {
          // 重置收藏状态
          interactionState.value.isFavorited = false;
          interactionState.value.favoriteActivityId = null;
        }
      } catch (error) {
        console.error("获取点赞状态失败:", error);
      }
    }
  }
});
</script>

<template>
  <page-container>
    <el-card v-loading="loading" class="post-view">
      <!-- 文章头部信息 -->
      <post-header
        :title="post.title"
        :author="post.author"
        :created-time="post.createdTime"
        :view-count="post.viewCount"
        :category="post.category"
        :tags="post.tags"
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

      <!-- 文章内容 -->
      <div class="post-content">
        <md-preview
          :modelValue="post.content"
          :showCodeRowNumber="true"
          language="zh-CN"
        />
      </div>

      <!-- 在文章内容后添加交互区域 -->
      <!-- 只在用户登录时显示交互区域 -->
      <post-interaction
        v-if="userStore.isLoggedIn"
        v-model:is-liked="interactionState.isLiked"
        v-model:is-favorited="interactionState.isFavorited"
        :post-id="post.postId"
        :title="post.title"
        :like-activity-id="interactionState.likeActivityId"
        :favorite-activity-id="interactionState.favoriteActivityId"
        @refresh="getPostDetail(post.postId)"
      />

      <!-- 未登录时显示提示 -->
      <div v-else class="post-interaction">
        <el-alert
          title="登录后即可点赞、收藏和评论"
          type="info"
          :closable="false"
          center
        />
      </div>

      <!-- 文章底部信息 -->
      <div class="post-footer">
        <div class="update-time">最后更新于：{{ post.updatedTime }}</div>
      </div>
    </el-card>
  </page-container>
</template>

<style scoped>
.post-view {
  max-width: 900px;
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
</style>
