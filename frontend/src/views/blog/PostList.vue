<script setup>
import { ref, reactive, onMounted, watch } from "vue";
import { useRoute, useRouter } from "vue-router";
import { View, ChatRound, Star, Search } from "@element-plus/icons-vue";
import { ElMessage } from "element-plus";
import { findAll as getAllTags } from "@/api/common/tag";
import { findAll as getAllCategories } from "@/api/common/category";
import { findAll as getAllPosts } from "@/api/post/post.js";
import { searchPosts } from "@/api/search/es";
import { BLOG_USER_DETAIL_PATH } from "@/constants/routes/blog";
import SearchBar from "@/components/blog/search/SearchBar.vue";

/**
 * 文章封面默认值
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
const handleImageError = (event) => {
  event.target.src = getRandomCover();
};

const route = useRoute();
const router = useRouter();

// 分页数据
const currentPage = ref(1);
const pageSize = ref(10);
const total = ref(0);
const loading = ref(false);

// 文章列表数据
const posts = ref([]);
const categories = ref([]);
const tags = ref([]);

// 搜索过滤
const filter = reactive({
  sort: "newest",
  keyword: route.query.search || "",
});

// 监听路由参数变化
watch(
  () => route.query,
  (query) => {
    // 只在路由参数中有 search 时才更新 keyword
    if (query.search) {
      filter.keyword = query.search;
    } else {
      filter.keyword = "";
    }
    fetchPosts();
  }
);

// 添加搜索处理函数
const handleSearch = () => {
  // 如果搜索框为空，重置路由参数
  if (!filter.keyword?.trim()) {
    router.replace({
      query: {
        ...route.query,
        search: undefined,
      },
    });
  } else {
    // 有搜索关键词时，更新路由参数
    router.replace({
      query: {
        ...route.query,
        search: filter.keyword,
      },
    });
  }
  // 重置分页并获取数据
  currentPage.value = 1;
  fetchPosts();
};

// 获取文章列表
const fetchPosts = async () => {
  loading.value = true;
  try {
    let response;

    if (filter.keyword?.trim()) {
      // 有搜索条件时使用搜索接口
      response = await searchPosts(filter.keyword, currentPage.value - 1, pageSize.value);
      if (response.data?.status === 200) {
        posts.value = response.data.data.content.map((item) => ({
          id: item.post.postId,
          title: item.post.title,
          content: item.post.content,
          cover: item.post.coverImage || getRandomCover(),
          excerpt:
            item.post.abstractContent || item.post.content?.substring(0, 200) + "...",
          category: item.category?.categoryId
            ? {
                categoryId: item.category.categoryId,
                name: item.category.name,
              }
            : null,
          tags: item.tags?.map((tag) => tag.name) || [],
          views: item.post.views || 0,
          likes: item.post.likes || 0,
          comments: item.comments?.length || 0,
          createTime: item.post.createdAt,
          author: item.author,
          userId: item.post.userId,

          highlightTitle: item.post.title,
          highlightContent: item.post.content,
        }));
        total.value = response.data.data.totalElements || 0;
      }
    } else {
      // 无搜索条件时使用获取所有文章接口
      response = await getAllPosts({
        pageNum: currentPage.value,
        pageSize: pageSize.value,
        orderBy: filter.sort,
      });
      if (response.data?.status === 200) {
        posts.value = response.data.data.map((item) => ({
          id: item.post.postId,
          title: item.post.title,
          content: item.post.content,
          cover: item.post.coverImage || getRandomCover(),
          excerpt:
            item.post.abstractContent || item.post.content?.substring(0, 200) + "...",
          category: item.category?.categoryId
            ? {
                categoryId: item.category.categoryId,
                name: item.category.name,
              }
            : null,
          tags: item.tags?.map((tag) => tag.name) || [],
          views: item.post.views || 0,
          likes: item.post.likes || 0,
          comments: item.comments?.length || 0,
          createTime: item.post.createdAt,
          author: item.author,
          userId: item.post.userId,
        }));
        total.value = response.data.total || posts.value.length;
      }
    }
  } catch (error) {
    console.error("获取文章列表失败:", error);
    ElMessage.error("获取文章列表失败");
  } finally {
    loading.value = false;
  }
};

// 获取分类和标签
const fetchCategoriesAndTags = async () => {
  try {
    const [categoriesRes, tagsRes] = await Promise.all([
      getAllCategories(),
      getAllTags(),
    ]);

    if (categoriesRes.data?.status === 200) {
      categories.value = categoriesRes.data.data.filter(
        (category) => category.categoryId && category.name
      );
    }
    if (tagsRes.data?.status === 200) {
      tags.value = tagsRes.data.data.filter((tag) => tag.tagId && tag.name);
    }
  } catch (error) {
    console.error("获取分类和标签失败:", error);
    ElMessage.error("获取分类和标签失败");
  }
};

// 处理分页
const handleSizeChange = (val) => {
  pageSize.value = val;
  fetchPosts();
};

const handleCurrentChange = (val) => {
  currentPage.value = val;
  fetchPosts();
};

// 跳转到文章详情
const goToPost = (postId) => {
  router.push(`/post/${postId}`);
};

onMounted(() => {
  fetchCategoriesAndTags();
  fetchPosts();
});

/**
 * 处理作者点击
 */
const handleAuthorClick = (event, userId) => {
  event.stopPropagation(); // 阻止事件冒泡，避免触发文章卡片的点击
  if (userId) {
    router.push({
      path: BLOG_USER_DETAIL_PATH.replace(":id", userId),
      query: {
        redirect: route.fullPath,
      },
    });
  }
};
</script>

<template>
  <div class="posts-container">
    <!-- 搜索过滤区 -->
    <div class="search-section">
      <el-card shadow="never" class="search-card">
        <div class="search-content">
          <div class="search-wrapper">
            <SearchBar
              class="search-bar"
              v-model="filter.keyword"
              @search="handleSearch"
              placeholder="搜索文章、作者或关键词..."
            />
            <el-button
              type="primary"
              size="large"
              class="search-button"
              @click="handleSearch"
            >
              <el-icon><Search /></el-icon>
              搜索
            </el-button>
          </div>
          <el-select
            v-model="filter.sort"
            placeholder="排序方式"
            class="sort-select"
            size="large"
          >
            <el-option label="最新发布" value="newest" />
            <el-option label="最多浏览" value="views" />
            <el-option label="最多点赞" value="likes" />
          </el-select>
        </div>
      </el-card>
    </div>

    <!-- 文章列表 -->
    <div class="posts-list" v-loading="loading">
      <el-empty v-if="posts.length === 0" description="暂无文章" />

      <el-card
        v-else
        v-for="post in posts"
        :key="post.id"
        class="post-item"
        @click="goToPost(post.id)"
      >
        <div class="post-content">
          <div class="post-main">
            <h3 class="post-title">
              <span
                v-if="filter.keyword"
                v-html="post.highlightTitle || post.title"
              ></span>
              <span v-else>{{ post.title }}</span>
            </h3>
            <p class="post-abstract">
              <span
                v-if="filter.keyword"
                v-html="post.highlightContent || post.excerpt"
              ></span>
              <span v-else>{{ post.excerpt }}</span>
            </p>
            <div class="post-info">
              <div class="post-meta">
                <span
                  class="post-author"
                  @click.stop="(e) => handleAuthorClick(e, post.userId)"
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
          <div class="post-thumbnail" v-if="post.cover">
            <el-image
              :src="post.cover"
              :alt="post.title"
              fit="cover"
              loading="lazy"
              @error="handleImageError"
            />
          </div>
        </div>
        <div class="post-footer">
          <div class="post-stats">
            <span title="浏览量">
              <el-icon><View /></el-icon>
              {{ post.views }}
            </span>
            <span title="评论数">
              <el-icon><ChatRound /></el-icon>
              {{ post.comments }}
            </span>
            <span title="点赞数">
              <el-icon><Star /></el-icon>
              {{ post.likes }}
            </span>
          </div>
        </div>
      </el-card>
    </div>

    <!-- 分页 -->
    <div class="pagination">
      <el-pagination
        v-model:current-page="currentPage"
        v-model:page-size="pageSize"
        :page-sizes="[10, 20, 30, 50]"
        :total="total"
        layout="total, sizes, prev, pager, next, jumper"
        @size-change="handleSizeChange"
        @current-change="handleCurrentChange"
      />
    </div>
  </div>
</template>

<style scoped>
.posts-container {
  width: 900px;
  margin: 0 auto;
  padding: 20px;
}

.search-section {
  margin-bottom: 32px;
  margin-left: auto;
  margin-right: auto;
}

.search-card {
  background-color: var(--el-bg-color);
  border-radius: 12px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.04);
}

.search-content {
  padding: 32px;
  display: flex;
  gap: 20px;
  align-items: center;
}

.search-wrapper {
  flex: 1;
  display: flex;
  gap: 16px;
  align-items: center;
}

.search-bar {
  flex: 1;
  font-size: 16px;
}

.search-bar :deep(.el-input__wrapper) {
  padding: 4px 16px;
  height: 48px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.04);
}

.search-bar :deep(.el-input__inner) {
  font-size: 16px;
}

.search-button {
  height: 48px;
  padding: 0 24px;
  font-size: 16px;
}

.sort-select {
  width: 140px;
}

.sort-select :deep(.el-input__wrapper) {
  height: 48px;
}

.posts-list {
  display: flex;
  flex-direction: column;
  gap: 20px;
  margin: 0 auto;
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

.pagination {
  margin-top: 32px;
  display: flex;
  justify-content: center;
  margin-left: auto;
  margin-right: auto;
}

:deep(.highlight) {
  color: var(--el-color-primary);
  font-weight: bold;
  padding: 0 2px;
}

@media screen and (max-width: 768px) {
  .posts-container {
    padding: 16px;
  }

  .search-content {
    padding: 24px;
    flex-direction: column;
  }

  .search-wrapper {
    width: 100%;
  }

  .sort-select {
    width: 100%;
  }

  .post-content {
    flex-direction: column;
  }

  .post-thumbnail {
    width: 100%;
    height: 200px;
    order: -1;
  }
}

.search-section {
  margin-bottom: 40px;
  margin-left: auto;
  margin-right: auto;
}

.search-card {
  background-color: var(--el-bg-color);
  border-radius: 16px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08);
  border: none;
  transition: all 0.3s ease;
}

.search-card:hover {
  box-shadow: 0 6px 24px rgba(0, 0, 0, 0.12);
}

.search-content {
  padding: 24px 32px;
  display: flex;
  gap: 20px;
  align-items: center;
}

.search-wrapper {
  flex: 1;
  display: flex;
  gap: 12px;
  align-items: center;
  position: relative;
}

.search-bar {
  flex: 1;
  font-size: 16px;
}

.search-bar :deep(.el-input__wrapper) {
  padding: 4px 16px;
  height: 52px;
  background-color: var(--el-bg-color);
  border: 1px solid var(--el-border-color-light);
  border-radius: 12px;
  box-shadow: none;
  transition: all 0.3s cubic-bezier(0.25, 0.8, 0.25, 1);
}

.search-bar :deep(.el-input__wrapper:hover) {
  border-color: var(--el-color-primary-light-3);
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
}

.search-bar :deep(.el-input__wrapper.is-focus) {
  border-color: var(--el-color-primary);
  box-shadow: 0 2px 12px var(--el-color-primary-light-7);
}

.search-bar :deep(.el-input__inner) {
  font-size: 16px;
  color: var(--el-text-color-primary);
  font-weight: 500;
}

.search-bar :deep(.el-input__inner::placeholder) {
  color: var(--el-text-color-placeholder);
  opacity: 0.8;
  font-weight: normal;
}

.search-button {
  height: 52px;
  padding: 0 28px;
  font-size: 16px;
  font-weight: 500;
  border-radius: 12px;
  background-color: var(--el-color-primary);
  border: none;
  transition: all 0.3s ease;
  box-shadow: 0 2px 8px var(--el-color-primary-light-5);
}

.search-button:hover {
  background-color: var(--el-color-primary-light-2);
  transform: translateY(-1px);
  box-shadow: 0 4px 12px var(--el-color-primary-light-5);
}

.search-button:active {
  transform: translateY(0);
}

.sort-select {
  width: 150px;
}

.sort-select :deep(.el-input__wrapper) {
  height: 52px;
  border-radius: 12px;
  background-color: var(--el-bg-color);
  border: 1px solid var(--el-border-color-light);
  transition: all 0.3s ease;
}

.sort-select :deep(.el-input__wrapper:hover) {
  border-color: var(--el-color-primary-light-3);
}

.sort-select :deep(.el-input__wrapper.is-focus) {
  border-color: var(--el-color-primary);
  box-shadow: 0 0 0 1px var(--el-color-primary-light-5);
}

@media screen and (max-width: 768px) {
  .search-content {
    padding: 20px;
    flex-direction: column;
    gap: 16px;
  }

  .search-wrapper {
    width: 100%;
  }

  .sort-select {
    width: 100%;
  }

  .search-button {
    width: 100%;
  }
}
</style>
