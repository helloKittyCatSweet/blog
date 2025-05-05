<script setup>
import { View, ChatRound, Star, Pointer } from "@element-plus/icons-vue";
import { onMounted, defineProps, ref } from "vue";
import { useRouter } from "vue-router";
import { BLOG_USER_DETAIL_PATH } from "@/constants/routes/blog";
import { useUserStore, usePostStore } from "@/stores";
import { ElMessage } from "element-plus";
import { addViews } from "@/api/post/post.js";

const props = defineProps({
  posts: {
    type: Array,
    required: true,
    default: () => [],
  },
  showKeyword: {
    type: Boolean,
    default: false,
  },
});

/**
 * 处理作者点击事件
 */

const router = useRouter();
const userStore = useUserStore();

const handleAuthorClick = (event, userId) => {
  event.stopPropagation();
  if (userId) {
    // 检查用户是否已登录
    if (!userStore.isLoggedIn) {
      ElMessage.warning("请先登录");
      router.push({
        path: "/login",
        query: {
          redirect: router.currentRoute.value.fullPath,
        },
      });
      return;
    }

    // 已登录则正常跳转
    router.push({
      path: BLOG_USER_DETAIL_PATH.replace(":id", userId),
      query: {
        redirect: router.currentRoute.value.fullPath,
      },
    });
  }
};

/**
 * 封面随机图片处理
 */

// 添加图片错误处理函数
const handleImageError = (event) => {
  event.target.src = getRandomCover();
};

const defaultCoverImages = [
  "/covers/book_cover.jpg",
  "/covers/frame_cover.jpg",
  "/covers/picture_cover.jpg",
  "/covers/shell_cover.jpg",
  "/covers/star_cover.jpg",
];

const getRandomCover = () => {
  const randomIndex = Math.floor(Math.random() * defaultCoverImages.length);
  return defaultCoverImages[randomIndex];
};

const postStore = usePostStore();

/**
 * 点击文章跳转
 */
 const handlePostClick = async (post) => {
  try {
    // 先更新浏览量
    const response = await addViews(post.id);
    if (response?.data?.status === 200) {
      const currentViews = postStore.getPostViews(post.id);
      postStore.updatePostViews(post.id, currentViews + 1);
    }
  } catch (error) {
    console.error("更新阅读量失败:", error);
  }
  // 无论更新浏览量是否成功，都进行跳转
  router.push(`/post/${post.id}`);
};


// 计算属性获取最新的浏览量
const getLatestViews = (postId) => {
  return postStore.getPostViews(postId)
}
</script>

<template>
  <el-empty
    v-if="!posts || posts.length === 0"
    description="暂无文章"
    :image-size="120"
  />
  <div v-else class="posts-list">
    <el-card v-for="post in posts" :key="post.id" class="post-item">
      <div class="post-content">
        <div class="post-main">
          <h3 class="post-title" @click="handlePostClick(post)">
            <span
              v-if="showKeyword"
              v-html="post.highlightTitle || post.title"
            ></span>
            <span v-else>{{ post.title }}</span>
          </h3>
          <p class="post-abstract" @click="handlePostClick(post)">
            <span
              v-if="showKeyword"
              v-html="post.highlightContent || post.excerpt"
            ></span>
            <span v-else>{{ post.excerpt }}</span>
          </p>
          <div class="post-info">
            <div class="post-meta"  >
              <span
                class="post-author"
                @click.stop="handleAuthorClick($event, post.userId)"
              >
                {{ post.author }}
              </span>
              <span class="post-date">{{ post.createTime }}</span>
            </div>
            <div class="post-tags">
              <el-tag
                v-if="post.category?.categoryId"
                size="small"
                type="success"
                class="category-tag"
              >
                {{ post.category.name }}
              </el-tag>
              <el-tag
                v-for="tag in post.tags"
                :key="tag"
                size="small"
                effect="plain"
                class="tag"
              >
                {{ tag }}
              </el-tag>
            </div>
          </div>
        </div>
        <div class="post-thumbnail" @click="handlePostClick(post)">
          <el-image
            :src="post.cover || getRandomCover()"
            :alt="post.title"
            fit="cover"
            loading="lazy"
            @error="handleImageError"
          />
        </div>
      </div>
      <div class="post-footer" @click="handlePostClick(post)">
        <div class="post-stats">
          <span title="浏览量">
            <el-icon><View /></el-icon>
            {{ getLatestViews(post.id) }}
          </span>
          <span title="评论数">
            <el-icon><ChatRound /></el-icon>
            {{ post.comments }}
          </span>
          <span title="收藏数">
            <el-icon><Star /></el-icon>
            {{ post.favorites }}
          </span>
          <span title="点赞数">
            <el-icon><Pointer /></el-icon>
            {{ post.likes }}
          </span>
        </div>
      </div>
    </el-card>
  </div>
</template>

<style scoped>
.posts-list {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.post-item {
  width: 100%;
  cursor: pointer;
  transition: all 0.3s ease;
  border: none;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
}

.post-item:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.post-content {
  display: flex;
  gap: 24px;
  padding: 20px;
  align-items: flex-start;
}

.post-main {
  flex: 1;
  min-width: 0;
  display: flex;
  flex-direction: column;
  max-width: calc(100% - 224px);
}

.post-title {
  font-size: 20px;
  font-weight: 600;
  margin: 0 0 16px;
  color: var(--el-text-color-primary);
  line-height: 1.4;
}

.post-abstract {
  font-size: 14px;
  line-height: 1.8;
  color: var(--el-text-color-regular);
  margin: 0 0 16px;
  display: -webkit-box;
  -webkit-line-clamp: 3;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.post-info {
  margin-top: auto;
  display: flex;
  justify-content: space-between;
  align-items: center;
  flex-wrap: wrap;
  gap: 12px;
}

.post-meta {
  display: flex;
  align-items: center;
  gap: 8px;
  color: var(--el-text-color-secondary);
  font-size: 13px;
}

.post-author {
  color: var(--el-color-primary);
  cursor: pointer;
}

.post-author:hover {
  color: var(--el-color-primary-light-3);
  text-decoration: underline;
}

.post-date {
  color: var(--el-text-color-secondary);
}

.post-date::before {
  content: "•";
  margin: 0 8px;
}

.post-tags {
  display: flex;
  gap: 8px;
  flex-wrap: wrap;
}

.category-tag {
  margin-right: 4px;
}

.post-thumbnail {
  width: 200px;
  height: 150px;
  overflow: hidden;
  border-radius: 8px;
  flex-shrink: 0;
}

.post-thumbnail img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform 0.3s ease;
}

.post-thumbnail:hover img {
  transform: scale(1.05);
}

.post-footer {
  padding: 12px 20px;
  border-top: 1px solid var(--el-border-color-lighter);
  background-color: var(--el-bg-color-page);
}

.post-stats {
  display: flex;
  gap: 24px;
  color: var(--el-text-color-secondary);
  font-size: 14px;
}

.post-stats span {
  display: flex;
  align-items: center;
  gap: 4px;
}

@media screen and (max-width: 768px) {
  .post-content {
    flex-direction: column;
  }

  .post-thumbnail {
    width: 100%;
    height: 200px;
    order: -1;
  }

  .post-main {
    max-width: 100%;
  }
}
</style>
