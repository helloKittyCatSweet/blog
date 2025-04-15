<script setup>
import { ref, onMounted, computed } from "vue";
import { useRoute, useRouter } from "vue-router";
import { ElMessage } from "element-plus";

import PageContainer from "@/components/PageContainer.vue";
import PostHeader from "@/components/blog/post/PostHeader.vue";
import PostInteraction from "@/components/blog/post/PostInteraction.vue";
import PostComment from "@/components/blog/post/PostComment.vue";
import PostExport from "@/components/blog/post/PostExport.vue";

import { MdPreview } from "md-editor-v3";
import "md-editor-v3/lib/style.css";

import { findById } from "@/api/post/post.js";
import { findPostExplicit } from "@/api/user/userActivity.js";
import { useUserStore } from "@/stores/modules/user.js";
import { LOGIN_PATH } from "@/constants/routes/base.js";

const route = useRoute();
const router = useRouter();
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
        userId: postData.post.userId,
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
            tagName: tag.name,
          })) || [],
        author: postData.author,
        summary: postData.post.abstractContent,
        createdTime: postData.post.createdAt,
        updatedTime: postData.post.updatedAt,
        viewCount: postData.post.views || 0,
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
        interactionState.value.isLiked = activityData !== null;
        if (activityData) {
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
    await getPostDetail(post.value.postId);
  }
};

/**
 * 抽屉和评论数量
 */
// 添加抽屉相关状态
const drawerVisible = ref(false);
const showAllComments = ref(false);

// 添加计算属性来控制显示的评论数量
const displayedComments = computed(() => {
  return showAllComments.value ? null : 5;
});
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
      <div class="post-interaction">
        <!-- 登录则可互动 -->
        <template v-if="userStore.isLoggedIn">
          <post-interaction
            v-model:is-liked="interactionState.isLiked"
            v-model:is-favorited="interactionState.isFavorited"
            :post-id="post.postId"
            :title="post.title"
            :like-activity-id="interactionState.likeActivityId"
            :favorite-activity-id="interactionState.favoriteActivityId"
            @refresh="getPostDetail(post.postId)"
          />
        </template>
        <!-- 未登录时显示提示 -->
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
          :display-limit="displayedComments"
          :show-view-more="true"
          @refresh="refreshPost"
        />

        <!-- 添加查看更多按钮 -->
        <div class="view-more-comments" v-if="!showAllComments">
          <el-button type="primary" text @click="drawerVisible = true">
            查看全部评论
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
          :show-view-more="false"
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
}
</style>
