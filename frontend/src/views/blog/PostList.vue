<script setup>
import { ref, reactive, onMounted, watch } from "vue";
import { useRoute, useRouter } from "vue-router";
import { View, ChatRound, Star, Search } from "@element-plus/icons-vue";
import { ElMessage } from "element-plus";
import { formatDateTime } from "@/utils/format";
import { findAll as getAllTags } from "@/api/common/tag";
import { findAll as getAllCategories } from "@/api/common/category";
import { searchPosts, findByKeysInTitle } from "@/api/post/post";

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
  category: "",
  tag: "",
  sort: "newest",
  keyword: route.query.search || "",
});

// 监听路由参数变化
watch(
  () => route.query,
  (query) => {
    if (query.category) filter.category = query.category;
    if (query.tag) filter.tag = query.tag;
    if (query.search) filter.keyword = query.search;
    fetchPosts();
  }
);

// 获取文章列表
const fetchPosts = async () => {
  loading.value = true;
  try {
    const params = {
      page: currentPage.value,
      size: pageSize.value,
      categoryId: filter.category,
      tagId: filter.tag,
      keyword: filter.keyword,
      sort: filter.sort,
    };

    const response = await searchPosts(params);
    if (response.data?.status === 200) {
      posts.value = response.data.data.map((item) => ({
        id: item.post.postId,
        title: item.post.title,
        content: item.post.content,
        cover: item.post.coverImage,
        excerpt: item.post.content.substring(0, 200) + "...",
        category: item.category?.name || "未分类",
        categoryId: item.category?.categoryId,
        tags: item.tags?.map((tag) => tag.name) || [],
        views: item.post.views || 0,
        likes: item.post.likes || 0,
        comments: item.post.comments || 0,
        createTime: item.post.createdTime,
        author: item.author,
      }));
      total.value = response.data.total || posts.value.length;
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
      categories.value = categoriesRes.data.data;
    }
    if (tagsRes.data?.status === 200) {
      tags.value = tagsRes.data.data;
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

// 处理搜索
const handleSearch = () => {
  currentPage.value = 1;
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
</script>

<template>
  <div class="posts-container">
    <!-- 搜索过滤区 -->
    <div class="filter-section">
      <el-card shadow="never" class="filter-card">
        <div class="search-bar">
          <el-input
            v-model="filter.keyword"
            placeholder="搜索文章..."
            clearable
            @keyup.enter="handleSearch"
          >
            <template #prefix>
              <el-icon><Search /></el-icon>
            </template>
          </el-input>
          <el-button type="primary" @click="handleSearch">搜索</el-button>
        </div>

        <div class="filter-options">
          <el-select v-model="filter.category" placeholder="选择分类" clearable>
            <el-option
              v-for="category in categories"
              :key="category.categoryId"
              :label="category.name"
              :value="category.categoryId"
            />
          </el-select>

          <el-select v-model="filter.tag" placeholder="选择标签" clearable>
            <el-option
              v-for="tag in tags"
              :key="tag.tagId"
              :label="tag.name"
              :value="tag.tagId"
            />
          </el-select>

          <el-select v-model="filter.sort" placeholder="排序方式">
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
        class="post-card"
        @click="goToPost(post.id)"
      >
        <div class="post-layout">
          <!-- 文章封面 -->
          <div class="post-cover" v-if="post.cover">
            <el-image :src="post.cover" :alt="post.title" fit="cover" loading="lazy" />
          </div>

          <!-- 文章内容 -->
          <div class="post-content">
            <div class="post-header">
              <h2 class="post-title">{{ post.title }}</h2>
              <div class="post-meta">
                <el-tag size="small" type="success">{{ post.category }}</el-tag>
                <span class="post-date">{{ formatDateTime(post.createTime) }}</span>
              </div>
            </div>

            <p class="post-excerpt">{{ post.excerpt }}</p>

            <div class="post-footer">
              <div class="post-tags">
                <el-tag
                  v-for="tag in post.tags"
                  :key="tag"
                  size="small"
                  effect="plain"
                  class="post-tag"
                >
                  {{ tag }}
                </el-tag>
              </div>

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
  max-width: 1200px;
  margin: 0 auto;
  padding: 20px;
}

.filter-section {
  margin-bottom: 24px;
}

.filter-card {
  background-color: var(--el-bg-color);
}

.search-bar {
  display: flex;
  gap: 16px;
  margin-bottom: 16px;
}

.filter-options {
  display: flex;
  gap: 16px;
}

.post-card {
  margin-bottom: 20px;
  cursor: pointer;
  transition: all 0.3s ease;
}

.post-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.post-layout {
  display: flex;
  gap: 20px;
}

.post-cover {
  flex: 0 0 280px;
  height: 180px;
  overflow: hidden;
  border-radius: 8px;
}

.post-cover img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform 0.3s ease;
}

.post-cover img:hover {
  transform: scale(1.05);
}

.post-content {
  flex: 1;
  display: flex;
  flex-direction: column;
}

.post-header {
  margin-bottom: 12px;
}

.post-title {
  margin: 0 0 12px;
  font-size: 1.5rem;
  color: var(--el-text-color-primary);
  line-height: 1.4;
}

.post-meta {
  display: flex;
  align-items: center;
  gap: 12px;
}

.post-date {
  color: var(--el-text-color-secondary);
  font-size: 14px;
}

.post-excerpt {
  color: var(--el-text-color-regular);
  font-size: 14px;
  line-height: 1.6;
  margin: 12px 0;
  display: -webkit-box;
  -webkit-line-clamp: 3;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.post-footer {
  margin-top: auto;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.post-tags {
  display: flex;
  gap: 8px;
  flex-wrap: wrap;
}

.post-tag {
  transition: all 0.3s ease;
}

.post-tag:hover {
  transform: translateY(-2px);
}

.post-stats {
  display: flex;
  gap: 16px;
  color: var(--el-text-color-secondary);
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
}

@media (max-width: 768px) {
  .filter-options {
    flex-direction: column;
  }

  .post-layout {
    flex-direction: column;
  }

  .post-cover {
    flex: none;
    height: 200px;
  }
}
</style>
