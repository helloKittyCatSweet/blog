<script setup>
import { ref, onMounted, watch, computed } from "vue";
import { ElMessage, ElLoading, ElAutocomplete } from "element-plus";
import { Search, View, Star, Reading } from "@element-plus/icons-vue";
import { findAll } from "@/api/common/category";
import { findByCategory, findAll as findAllPosts } from "@/api/post/post";
import { searchCategorySuggestions, searchPosts } from "@/api/search/es";
import PostListItem from "@/components/blog/post/PostListItem.vue";

const searchQuery = ref("");
const categoriesTree = ref([]);
const selectedCategory = ref(null);
const categoryPosts = ref([]);
const sortBy = ref("newest");
const loading = ref(false);

// 加载所有分类
const loadCategories = async () => {
  loading.value = true;
  try {
    const response = await findAll();
    if (response.data?.status === 200) {
      categoriesTree.value = response.data.data.map((item) => ({
        ...item,
        label: item.category.name,
        children: item.children?.map((child) => ({
          ...child,
          label: child.category.name,
        })),
      }));

      // 如果有分类且当前没有选中的分类，自动选中第一个
      if (categoriesTree.value.length > 0) {
        if (!selectedCategory.value) {
          selectedCategory.value = categoriesTree.value[0].category;
        }
        await loadPosts(); // 无论是否选中新分类，都重新加载文章
      } else {
        ElMessage.warning("暂无分类数据");
      }
    }
  } catch (error) {
    console.error("加载分类失败:", error);
    ElMessage.error("加载分类失败");
  } finally {
    loading.value = false;
  }
};

// 处理分类点击
const handleCategoryClick = async (data) => {
  selectedCategory.value = data.category;
  await loadPosts();
};

// 查看文章列表
const viewPosts = (data) => {
  selectedCategory.value = data.category;
  loadPosts();
};

// 提取的数据转换方法
const transformPostData = (rawData) => {
  if (!Array.isArray(rawData)) {
    console.warn("rawData is not an array, returning empty array");
    return [];
  }
  return rawData.map((item) => ({
    id: item.post.postId,
    title: item.post.title,
    content: item.post.content,
    cover: item.post.coverImage,
    excerpt: item.post.abstractContent || item.post.content?.substring(0, 200) + "...",
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
};

// 搜索处理
const handleSearch = async (selectedItem) => {
  // 统一获取查询内容
  let query =
    typeof selectedItem === "string"
      ? selectedItem
      : selectedItem?.value || searchQuery.value;

  if (!query) {
    if (selectedCategory.value) {
      await loadPosts();
    } else {
      await loadCategories();
    }
    return;
  }

  loading.value = true;
  try {
    // 检查是否是分类（自动补全选择时）
    const isCategorySuggestion = categoriesTree.value.some(
      (item) =>
        item.category.name === query ||
        item.children?.some((child) => child.category.name === query)
    );

    if (isCategorySuggestion) {
      // 处理分类选择
      const matchedCategory = categoriesTree.value.find(
        (item) =>
          item.category.name === query ||
          item.children?.some((child) => child.category.name === query)
      );
      if (matchedCategory) {
        selectedCategory.value = matchedCategory.category;
        await loadPosts();
      }
    } else {
      // 处理手动输入的搜索（构建完整查询）
      let finalQuery = query;

      // 如果已选分类但查询中未指定@分类，自动附加分类条件
      if (selectedCategory.value && !query.includes("@")) {
        finalQuery = `@${selectedCategory.value.name}@ ${query}`;
      }
      // 如果输入的是纯标签（以#开头），保持原样
      else if (query.startsWith("#")) {
        finalQuery = query;
      }
      // 其他情况直接使用原查询

      console.log("Final search query:", finalQuery); // 调试用

      const response = await searchPosts(finalQuery, 0, 10);
      if (response.data?.status === 200) {
        categoryPosts.value = transformPostData(response.data.data.content);
        // 保持当前分类选中状态
      }
    }
  } catch (error) {
    console.error("搜索失败:", error);
    ElMessage.error("搜索失败");
  } finally {
    loading.value = false;
  }
};

// 补全搜索建议
const querySearchAsync = async (queryString, cb) => {
  if (queryString) {
    try {
      const response = await searchCategorySuggestions(queryString);
      if (response.data?.status === 200) {
        const suggestions = response.data.data.map((item) => ({
          value: item.category.name,
          ...item,
        }));
        cb(suggestions);
      }
    } catch (error) {
      ElMessage.error("获取搜索建议失败");
      cb([]);
    }
  } else {
    cb([]);
  }
};

// 加载文章，根据是否有选中分类决定加载分类文章还是全部文章
const loadPosts = async () => {
  if (!selectedCategory.value?.categoryId) {
    categoryPosts.value = [];
    total.value = 0;
    return;
  }

  loading.value = true;
  try {
    let response;
    // 组合搜索：当前分类 + 搜索关键词
    if (searchQuery.value) {
      response = await searchPosts(
        `@${selectedCategory.value.name}@ ${searchQuery.value}`,
        currentPage.value - 1,
        pageSize.value
      );
    } else {
      // 仅按分类搜索
      response = await findByCategory(
        selectedCategory.value.name,
        currentPage.value - 1,
        pageSize.value
      );
    }

    if (response.data?.status === 200) {
      const data = response.data.data;
      if (data.content) {
        categoryPosts.value = transformPostData(data.content);
        total.value = data.totalElements;
      } else {
        categoryPosts.value = transformPostData(data);
        total.value = data.length;
      }

      if (categoryPosts.value.length === 0) {
        ElMessage.info("该分类下暂无文章");
      }
    }
  } catch (error) {
    console.error("加载文章失败:", error);
    ElMessage.error("加载文章失败");
    categoryPosts.value = [];
    total.value = 0;
  } finally {
    loading.value = false;
  }
};

onMounted(() => {
  loadCategories();
});

/**
 * 搜索框监视器
 */
watch(searchQuery, (newVal) => {
  if (!newVal || newVal.trim() === "") {
    if (selectedCategory?.categoryId) {
      // 有选中的分类，获取对应文章
      loadPosts();
    } else {
      // 没有选中的分类，重新加载所有分类
      loadCategories();
    }
  }
});

/**
 * 排序计算属性
 */
const sortedPosts = computed(() => {
  if (!categoryPosts.value.length) return [];

  return [...categoryPosts.value].sort((a, b) => {
    switch (sortBy.value) {
      case "newest":
        return new Date(b.createTime) - new Date(a.createTime);
      case "views":
        return b.views - a.views;
      case "likes":
        return b.likes - a.likes;
      default:
        return 0;
    }
  });
});

/**
 * 分页
 */
const currentPage = ref(1);
const pageSize = ref(10);
const total = ref(0);
</script>

<template>
  <div class="app-container">
    <div class="categories-wrapper" v-loading="loading">
      <!-- 左侧分类和搜索 -->
      <div class="left-sidebar">
        <el-card shadow="hover" class="category-card">
          <template #header>
            <div class="card-header">
              <h3>
                <el-icon><Reading /></el-icon> 分类浏览
              </h3>
              <div style="display: flex; gap: 10px">
                <el-autocomplete
                  v-model="searchQuery"
                  :fetch-suggestions="querySearchAsync"
                  placeholder="搜索分类..."
                  class="search-input"
                  clearable
                  @select="handleSearch"
                  @keyup.enter="handleSearch"
                  @clear="handleSearch"
                  size="large"
                >
                  <template #prefix>
                    <el-icon><Search /></el-icon>
                  </template>
                </el-autocomplete>
                <el-button @click="handleSearch" type="primary" size="large"
                  >搜索</el-button
                >
              </div>
            </div>
          </template>

          <div class="categories-tree-container">
            <el-tree
              :data="categoriesTree"
              :props="{
                children: 'children',
                label: (data) => data.category.name,
              }"
              node-key="category.categoryId"
              :expand-on-click-node="false"
              @node-click="handleCategoryClick"
              v-loading="loading"
            >
              <template #default="{ node, data }">
                <div class="custom-tree-node">
                  <div class="node-content">
                    <el-tag size="small" effect="plain" class="category-tag">
                      {{ data.category.name }}
                    </el-tag>
                    <span class="post-count">{{ data.category.useCount || 0 }}</span>
                  </div>
                  <div class="node-actions">
                    <el-button type="primary" link @click.stop="viewPosts(data)">
                      查看
                    </el-button>
                  </div>
                </div>
              </template>
            </el-tree>
          </div>
        </el-card>
      </div>

      <!-- 右侧文章列表 -->
      <div class="right-content">
        <el-card
          shadow="hover"
          class="posts-card"
          v-if="selectedCategory?.categoryId || categoryPosts.length > 0"
        >
          <template #header>
            <div class="posts-header">
              <div class="category-header">
                <h2 class="category-title">
                  {{ selectedCategory?.name }}
                </h2>
                <span class="posts-total">{{ categoryPosts.length }} 篇文章</span>
              </div>
              <div class="sort-options">
                <el-select
                  v-model="sortBy"
                  placeholder="排序方式"
                  size="large"
                  style="width: 140px"
                >
                  <el-option label="最新发布" value="newest" />
                  <el-option label="最多浏览" value="views" />
                  <el-option label="最多点赞" value="likes" />
                </el-select>
              </div>
            </div>
          </template>

          <el-empty
            v-if="categoryPosts.length === 0"
            description="暂无文章"
            :image-size="120"
          />

          <div v-else class="posts-list">
            <PostListItem :posts="sortedPosts" :show-keyword="false" />

            <el-pagination
              v-if="total > 0"
              v-model:current-page="currentPage"
              v-model:page-size="pageSize"
              :total="total"
              :page-sizes="[10, 20, 50]"
              layout="total, sizes, prev, pager, next"
              @size-change="loadPosts"
              @current-change="loadPosts"
            />
          </div>
        </el-card>

        <el-card shadow="hover" class="welcome-card" v-else>
          <template #header>
            <h2>
              <el-icon><Reading /></el-icon> 欢迎使用分类浏览
            </h2>
          </template>
          <div class="welcome-content">
            <el-empty description="请从左侧选择一个分类" :image-size="150">
              <p class="welcome-tip">点击分类查看相关文章，或使用搜索框查找特定分类</p>
            </el-empty>
          </div>
        </el-card>
      </div>
    </div>
  </div>
</template>

<style lang="scss" scoped>
.app-container {
  display: flex;
  justify-content: center;
  padding: 30px;
  background-color: #f8fafc;
  min-height: calc(100vh - 60px);
}

.categories-wrapper {
  width: 100%;
  width: 1200px;
  display: flex;
  gap: 24px;
}

.left-sidebar {
  width: 320px;
  min-width: 320px;

  .category-card {
    height: 100%;
    border-radius: 12px;
    border: none;
    box-shadow: 0 4px 12px rgba(0, 0, 0, 0.08);

    :deep(.el-card__header) {
      background: linear-gradient(135deg, #f6f8fa, #e9ecef);
      border-bottom: 1px solid #e0e3e8;
      padding: 20px;
      border-radius: 12px 12px 0 0;
    }

    :deep(.el-card__body) {
      padding: 0;
      height: calc(100% - 68px);
      overflow: auto;
    }

    .card-header {
      display: flex;
      flex-direction: column;
      gap: 16px;
      padding: auto;

      h3 {
        margin: 0;
        font-size: 20px;
        color: #2c3e50;
        display: flex;
        align-items: center;
        gap: 10px;

        .el-icon {
          color: #409eff;
          font-size: 24px;
        }
      }

      .search-input {
        width: 100%;

        :deep(.el-input__wrapper) {
          border-radius: 20px;
          padding: 0 15px;
          height: 40px;
        }
      }
    }

    .categories-tree-container {
      padding: 15px;

      :deep(.el-tree-node) {
        margin: 12px 0; // 增加节点间距
      }

      :deep(.el-tree-node__content) {
        min-height: 30px; // 设置最小高度
        padding: 8px 0; // 增加内部padding
      }

      :deep(.custom-tree-node) {
        flex: 1;
        display: flex;
        align-items: center;
        justify-content: space-between;
        padding: 8px; // 增加内部padding
        width: 100%;

        .node-content {
          display: flex;
          align-items: center;
          gap: 12px;

          .category-tag {
            font-size: 14px;
            border-radius: 6px;
            background-color: #f0f7ff;
            border-color: #d0e3ff;
            color: #409eff;
            padding: 8px 16px; // 增加标签内部间距
            height: auto;
            line-height: 1.4;
          }

          .post-count {
            background-color: #f0f2f5;
            color: #7a8ba9;
            font-size: 13px;
            padding: 6px 14px; // 增加计数器内部间距
            border-radius: 10px;
            font-weight: 500;
          }
        }
      }
    }
  }
}

.right-content {
  flex: 1;
  min-width: 0;

  .posts-card,
  .welcome-card {
    height: 100%;
    border-radius: 12px;
    border: none;
    box-shadow: 0 4px 12px rgba(0, 0, 0, 0.08);

    :deep(.el-card__header) {
      background: linear-gradient(135deg, #f6f8fa, #e9ecef);
      border-bottom: 1px solid #e0e3e8;
      padding: 20px;
      border-radius: 12px 12px 0 0;
    }

    :deep(.el-card__body) {
      height: calc(100% - 68px);
      overflow: auto;
      padding: 24px;
    }

    .posts-header {
      display: flex;
      justify-content: space-between;
      align-items: center;

      .category-header {
        display: flex;
        align-items: baseline;
        gap: 12px;

        .category-title {
          margin: 0;
          font-size: 24px;
          color: #2c3e50;
          font-weight: 600;
          white-space: nowrap;
          overflow: hidden;
          text-overflow: ellipsis;
          max-width: 500px;
        }

        .posts-total {
          font-size: 16px;
          color: #7a8ba9;
          font-weight: 500;
          flex-shrink: 0;
        }
      }

      .sort-options {
        .el-select {
          :deep(.el-input__wrapper) {
            border-radius: 20px;
            height: 40px;
          }
        }
      }
    }

    .posts-grid {
      display: grid;
      grid-template-columns: repeat(auto-fill, minmax(320px, 1fr));
      gap: 20px;

      .post-card {
        background: white;
        border-radius: 10px;
        overflow: hidden;
        box-shadow: 0 4px 12px rgba(0, 0, 0, 0.05);
        transition: all 0.3s ease;
        cursor: pointer;
        border: 1px solid #eaeef2;

        &:hover {
          transform: translateY(-5px);
          box-shadow: 0 8px 24px rgba(0, 0, 0, 0.12);
          border-color: #d0e3ff;

          .post-title {
            color: #409eff;
          }
        }

        .post-cover {
          height: 180px;
          overflow: hidden;

          .cover-image {
            width: 100%;
            height: 100%;
            transition: transform 0.3s;
          }
        }

        &:hover .cover-image {
          transform: scale(1.05);
        }

        .post-info {
          padding: 18px;

          .post-title {
            margin: 0 0 12px;
            font-size: 18px;
            font-weight: 600;
            line-height: 1.4;
            color: #2c3e50;
            display: -webkit-box;
            -webkit-line-clamp: 2;
            -webkit-box-orient: vertical;
            overflow: hidden;
            transition: color 0.2s;
          }

          .post-summary {
            margin: 0 0 14px;
            font-size: 15px;
            color: #5e6d82;
            line-height: 1.6;
            display: -webkit-box;
            -webkit-line-clamp: 3;
            -webkit-box-orient: vertical;
            overflow: hidden;
          }

          .post-footer {
            display: flex;
            justify-content: space-between;
            align-items: center;
            font-size: 13px;
            color: #7a8ba9;

            .post-stats {
              display: flex;
              gap: 16px;

              .stat-item {
                display: flex;
                align-items: center;
                gap: 6px;

                .el-icon {
                  font-size: 16px;
                  color: #99a9bf;
                }
              }
            }
          }
        }
      }
    }
  }

  .welcome-card {
    display: flex;
    flex-direction: column;

    :deep(.el-card__header) {
      h2 {
        display: flex;
        align-items: center;
        gap: 12px;
        margin: 0;
        font-size: 24px;

        .el-icon {
          color: #409eff;
          font-size: 28px;
        }
      }
    }

    .welcome-content {
      flex: 1;
      display: flex;
      flex-direction: column;
      align-items: center;
      justify-content: center;
      padding: 30px 0;

      .welcome-tip {
        margin-top: 20px;
        color: #7a8ba9;
        font-size: 16px;
        text-align: center;
        max-width: 400px;
        line-height: 1.6;
      }
    }
  }
}

@media (max-width: 1200px) {
  .categories-wrapper {
    max-width: 100%;
    padding: 0 20px;
  }
}

@media (max-width: 992px) {
  .app-container {
    padding: 20px;
  }

  .categories-wrapper {
    flex-direction: column;
    gap: 20px;
  }

  .left-sidebar {
    width: 100%;
    min-width: auto;
  }

  .right-content {
    width: 100%;
  }

  .posts-grid {
    grid-template-columns: repeat(auto-fill, minmax(280px, 1fr)) !important;
  }
}
.posts-list {
  .pagination {
    margin-top: 20px;
    display: flex;
    justify-content: center;
  }
}
</style>
