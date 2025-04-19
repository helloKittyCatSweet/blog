<script setup>
import { ref, reactive, onMounted, watch } from "vue";
import { useRoute, useRouter } from "vue-router";
import { View, ChatRound, Star, Search } from "@element-plus/icons-vue";
import { ElMessage } from "element-plus";
import { findAll as getAllPosts } from "@/api/post/post.js";
import { searchPosts } from "@/api/search/es";

import SearchBar from "@/components/blog/search/SearchBar.vue";
import PostListItem from "@/components/blog/post/PostListItem.vue";

const route = useRoute();
const router = useRouter();

// 分页数据
const currentPage = ref(1);
const pageSize = ref(10);
const total = ref(0);
const loading = ref(false);

// 文章列表数据
const posts = ref([]);

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
          cover: item.post.coverImage,
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
          cover: item.post.coverImage,
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

// 处理分页
const handleSizeChange = (val) => {
  pageSize.value = val;
  fetchPosts();
};

const handleCurrentChange = (val) => {
  currentPage.value = val;
  fetchPosts();
};

onMounted(() => {
  fetchPosts();
});
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
      <PostListItem :posts="posts" :show-keyword="!!filter.keyword" />
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
