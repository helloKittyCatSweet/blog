<script setup>
import { ref, onMounted } from "vue";
import { useRouter } from "vue-router";
import { View, Star, ArrowRight, Folder } from "@element-plus/icons-vue";
import { useUserStore } from "@/stores/modules/user";

import { formatDate } from "@/utils/date";
import { findAll as getAllPosts, addViews } from "@/api/post/post.js";
import { findAll as getAllCategories } from "@/api/common/category.js";
import { findAll as getAllTags } from "@/api/common/tag.js";
import {
  getHotArticleRecommendation,
  getPersonalizedRecommendation,
} from "@/api/post/recommendation";

import PostStats from "@/components/post/PostStats.vue";
import UserRecommendCard from "@/components/blog/recommend/UserRecommendCard.vue";

import {
  BLOG_POST_DETAIL_PATH,
  BLOG_POSTS_PATH,
  BLOG_CATEGORIES_PATH,
  BLOG_TAGS_PATH,
} from "@/constants/routes/blog.js";

const router = useRouter();
const loading = ref(false);
const userStore = useUserStore();

// 文章数据
const featuredPosts = ref([]);
const latestPosts = ref([]);
const categories = ref([]);
const tags = ref([]);

// 分页参数
const postPageParams = ref({
  page: 0,
  size: 10,
  sort: "createdAt,desc"
});

const categoryPageParams = ref({
  page: 0,
  size: 10,
  sort: "useCount,desc"
});

const tagPageParams = ref({
  page: 0,
  size: 50,
  sort: "weight,desc"
});

// 获取文章列表
const getPostList = async () => {
  loading.value = true;
  try {
    const response = await getAllPosts();
    if (response?.data?.status === 200) {
      console.log("response.data:", response.data);
      const { content } = response.data.data;
      if (!content || !Array.isArray(content)) {
        console.error("Invalid content in response:", response.data);
        latestPosts.value = [];
        return;
      }
      
      const posts = content.map((item) => ({
        postId: item.post.postId,
        title: item.post.title,
        content: item.post.content,
        coverImage: item.post.coverImage || getRandomCover(),
        views: item.post.views || 0,
        likes: item.post.likes || 0,
        favorites: item.post.favorites || 0,
        createdAt: item.post.createdAt,
        updatedAt: item.post.updatedAt,
        category: item.category?.categoryId
          ? {
              categoryId: item.category.categoryId,
              name: item.category.name,
            }
          : null,
        tags: item.tags || [],
        author: item.author || "匿名",
        excerpt: item.post.abstractContent || item.post.content?.substring(0, 200) + "...",
        comments: item.comments || [],
        attachments: item.attachments || []
      }));

      // 直接使用返回的文章列表
      latestPosts.value = posts;

      // 获取推荐文章（按照浏览量排序）
      featuredPosts.value = [...posts]
        .sort((a, b) => b.views - a.views)
        .slice(0, 3);
    } else {
      console.error("Failed to fetch posts:", response.data);
      latestPosts.value = [];
      featuredPosts.value = [];
    }
  } catch (error) {
    console.error("获取文章列表失败:", error);
    latestPosts.value = [];
    featuredPosts.value = [];
  } finally {
    loading.value = false;
  }
};

// 获取分类列表
const getCategories = async () => {
  try {
    const response = await getAllCategories();
    if (response?.data?.status === 200) {
      const { content } = response.data.data;
      if (!content) {
        console.error("No content in response:", response.data.data);
        categories.value = [];
        return;
      }
      
      // 处理分类数据，将嵌套结构展平
      const flattenCategories = [];
      const processCategory = (categoryData) => {
        if (categoryData.category) {
          flattenCategories.push({
            categoryId: categoryData.category.categoryId,
            name: categoryData.category.name,
            description: categoryData.category.description,
            parentCategoryId: categoryData.category.parentCategoryId,
            useCount: categoryData.category.useCount || 0,
          });
        }
        // 递归处理子分类
        if (categoryData.children && categoryData.children.length > 0) {
          categoryData.children.forEach((child) => processCategory(child));
        }
      };

      content.forEach((category) => processCategory(category));
      categories.value = flattenCategories;
    }
  } catch (error) {
    console.error("获取分类列表失败:", error);
    categories.value = [];
  }
};

// 获取标签列表
const getTags = async () => {
  try {
    const response = await getAllTags();
    if (response?.data?.status === 200) {
      const { content } = response.data.data;
      if (!content) {
        console.error("No content in response:", response.data.data);
        tags.value = [];
        return;
      }
      
      tags.value = content.map((tag) => ({
        ...tag,
        size: Math.floor(Math.random() * 8) + 12, // 12-20px的随机字体大小
      }));
    }
  } catch (error) {
    console.error("获取标签列表失败:", error);
    tags.value = [];
  }
};

onMounted(() => {
  // 检查用户是否登录
  if (!userStore.isLoggedIn) {
    getHotArticleRecommendation()
      .then((res) => {
        featuredPosts.value = res.data.data;
        console.log("faeturedPosts:", featuredPosts.value);
      })
      .catch((error) => {
        console.error("获取热门文章推荐失败:", error);
      });
  } else {
    getPersonalizedRecommendation()
      .then((res) => {
        featuredPosts.value = res.data.data;
      })
      .catch((error) => {
        console.error("获取个性化文章推荐失败:", error);
      });
  }
  getPostList();
  getCategories();
  getTags();
});

/**
 * 文章默认封面图数组
 */
const defaultCoverImages = [
  "/covers/book_cover.jpg",
  "/covers/frame_cover.jpg",
  "/covers/picture_cover.jpg",
  "/covers/shell_cover.jpg",
  "/covers/star_cover.jpg",
];

// 获取随机封面图
const getRandomCover = () => {
  const randomIndex = Math.floor(Math.random() * defaultCoverImages.length);
  return defaultCoverImages[randomIndex];
};

// 添加图片错误处理函数
const handleImageError = (event, post) => {
  event.target.src = getRandomCover();
};

// 文章点击处理函数
const handlePostClick = async (postId) => {
  try {
    await addViews(postId);
  } catch (error) {
    console.error("更新阅读量失败:", error);
  }
  router.push(BLOG_POST_DETAIL_PATH.replace(":id", postId));
};
</script>

<template>
  <div class="home">
    <!-- 左侧推荐文章 -->
    <aside class="left-sidebar">
      <el-card class="featured-card">
        <template #header>
          <div class="card-header">
            <span>推荐文章</span>
          </div>
        </template>
        <div class="featured-list">
          <div
            v-for="post in featuredPosts"
            :key="post.postId"
            class="featured-item"
            @click="handlePostClick(post.postId)"
          >
            <div class="featured-title">{{ post.title }}</div>
            <div class="featured-meta">
              <span>{{ formatDate(post.createdAt) }}</span>
              <span>{{ post.views }} 阅读</span>
            </div>
          </div>
        </div>
      </el-card>
    </aside>

    <!-- 中间主要文章列表 -->
    <main class="main-content">
      <div class="posts-list">
        <el-card
          v-for="post in latestPosts"
          :key="post.postId"
          class="post-item"
          @click="handlePostClick(post.postId)"
        >
          <div class="post-content">
            <div class="post-main">
              <h3 class="post-title">{{ post.title }}</h3>
              <p class="post-abstract">{{ post.excerpt }}</p>
              <div class="post-info">
                <div class="post-meta">
                  <el-avatar
                    :size="24"
                    :src="post.author?.avatar"
                    class="author-avatar"
                    >{{ post.author?.nickname?.charAt(0) }}</el-avatar
                  >
                  <span class="author-name">{{ post.author || "匿名" }}</span>
                  <span class="post-date">{{ formatDate(post.createdAt) }}</span>
                </div>
                <div class="post-tags">
                  <el-tag
                    v-if="post.category?.categoryId"
                    size="small"
                    type="success"
                    effect="light"
                    class="category-tag"
                  >
                    {{ post.category.name }}
                  </el-tag>
                  <el-tag
                    v-for="tag in post.tags"
                    :key="tag.tagId"
                    size="small"
                    effect="plain"
                    class="tag"
                  >
                    {{ tag.name }}
                  </el-tag>
                </div>
              </div>
            </div>
            <div class="post-thumbnail" v-if="post.coverImage">
              <img
                :src="post.coverImage"
                alt="缩略图"
                @error="handleImageError($event, post)"
              />
            </div>
          </div>
          <div class="post-footer">
            <post-stats
              :views="post.views || 0"
              :likes="post.likes || 0"
              :favorites="post.favorites || 0"
              size="small"
            />
          </div>
        </el-card>
      </div>
    </main>

    <!-- 右侧分类和标签 -->
    <aside class="right-sidebar">
      <UserRecommendCard v-if="userStore.isLoggedIn" />
      <el-card class="category-card">
        <template #header>
          <div class="card-header">
            <span>分类</span>
            <router-link :to="BLOG_CATEGORIES_PATH" class="more-link">更多</router-link>
          </div>
        </template>
        <div class="category-list">
          <router-link
            v-for="category in categories"
            :key="category.categoryId"
            :to="{
              path: BLOG_CATEGORIES_PATH,
              query: { category: category.name }
            }"
            class="category-item"
          >
            <div class="category-info">
              <el-icon><Folder /></el-icon>
              <span class="category-name">{{ category.name }}</span>
            </div>
            <span class="category-count">{{ category.useCount }}</span>
          </router-link>
        </div>
      </el-card>
      <!-- 标签 -->
      <el-card class="tag-card">
        <template #header>
          <div class="card-header">
            <span>标签云</span>
            <router-link :to="BLOG_TAGS_PATH" class="more-link">更多</router-link>
          </div>
        </template>
        <div class="tag-cloud">
          <router-link
            v-for="tag in tags"
            :key="tag.tagId"
            :to="{
              path: BLOG_TAGS_PATH,
              query: { tag: tag.name }
            }"
            class="tag-item"
            :style="{ fontSize: `${tag.size}px` }"
          >
            {{ tag.name }}
          </router-link>
        </div>
      </el-card>
    </aside>
  </div>
</template>

<style scoped>
.home {
  display: grid;
  grid-template-columns: 280px minmax(0, 1fr) 280px;
  gap: 24px;
  max-width: 1600px;
  margin: 0 auto;
  padding: 20px;
}

.left-sidebar,
.right-sidebar {
  position: sticky;
  top: 80px;
  height: fit-content;
}

.featured-card {
  margin-bottom: 20px;
}

.featured-list {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.featured-item {
  cursor: pointer;
  padding: 12px;
  border-radius: 4px;
  transition: all 0.3s ease;
}

.featured-item:hover {
  background-color: var(--el-bg-color-page);
  transform: translateX(4px);
}

.featured-title {
  font-size: 15px;
  font-weight: 500;
  line-height: 1.4;
  margin-bottom: 8px;
  color: var(--el-text-color-primary);
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.featured-meta {
  display: flex;
  justify-content: space-between;
  font-size: 12px;
  color: var(--el-text-color-secondary);
}

.main-content {
  min-width: 0;
}

/* 保持原有的其他样式 */

/* 响应式设计 */
@media screen and (max-width: 1400px) {
  .home {
    grid-template-columns: 240px 1fr 240px;
  }
}

@media screen and (max-width: 1200px) {
  .home {
    grid-template-columns: 1fr 240px;
  }
  .left-sidebar {
    display: none;
  }
}

@media screen and (max-width: 768px) {
  .home {
    grid-template-columns: 1fr;
  }
  .right-sidebar {
    display: none;
  }
}

/* 调整文章封面图片尺寸 */
.post-thumbnail {
  width: 200px;
  height: 150px; /* 固定高度 */
  overflow: hidden;
  border-radius: 8px;
  flex-shrink: 0; /* 防止图片被压缩 */
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

/* 右侧边栏样式 */
.right-sidebar {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.category-card,
.tag-card {
  background-color: var(--el-bg-color);
  border-radius: 8px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 12px 16px;
  border-bottom: 1px solid var(--el-border-color-light);
}

.more-link {
  font-size: 13px;
  color: var(--el-text-color-secondary);
  text-decoration: none;
}

.more-link:hover {
  color: var(--el-color-primary);
}

.category-list {
  display: flex;
  flex-direction: column;
  gap: 8px;
  padding: 12px;
}

.category-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 8px 12px;
  text-decoration: none;
  color: var(--el-text-color-regular);
  border-radius: 4px;
  transition: all 0.3s ease;
}

.category-item:hover {
  background-color: var(--el-color-primary-light-9);
  color: var(--el-color-primary);
}

.tag-cloud {
  padding: 16px;
  display: flex;
  flex-wrap: wrap;
  gap: 12px;
}

.tag-item {
  color: var(--el-text-color-regular);
  text-decoration: none;
  transition: all 0.3s ease;
  line-height: 1.2;
}

.tag-item:hover {
  color: var(--el-color-primary);
  transform: scale(1.05);
}

/* 调整响应式布局中的右侧边栏 */
@media screen and (max-width: 1200px) {
  .right-sidebar {
    width: 240px;
  }
}

.post-item {
  margin-bottom: 20px;
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
}

.post-main {
  flex: 1;
  min-width: 0;
  display: flex;
  flex-direction: column;
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

.author-avatar {
  border: 2px solid var(--el-color-primary-light-8);
}

.author-name {
  font-weight: 500;
}

.post-date {
  color: var(--el-text-color-secondary);
  &::before {
    content: "•";
    margin: 0 8px;
  }
}

.post-tags {
  display: flex;
  gap: 8px;
  flex-wrap: wrap;
}

.category-tag {
  margin-right: 4px;
}

.post-footer {
  padding: 12px 20px;
  border-top: 1px solid var(--el-border-color-lighter);
  background-color: var(--el-bg-color-page);
}

.category-list {
  padding: 16px;
}

.category-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 10px 16px;
  margin-bottom: 8px;
  border-radius: 6px;
  background-color: var(--el-bg-color-page);
  transition: all 0.3s ease;
  text-decoration: none;
  color: var(--el-text-color-regular);
}

.category-item:hover {
  background-color: var(--el-color-primary-light-9);
  transform: translateX(4px);
}

.category-info {
  display: flex;
  align-items: center;
  gap: 8px;
}

.category-name {
  font-size: 14px;
}

.category-count {
  font-size: 13px;
  color: var(--el-text-color-secondary);
  background-color: var(--el-bg-color);
  padding: 2px 8px;
  border-radius: 10px;
  min-width: 24px;
  text-align: center;
}
</style>
