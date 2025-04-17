<script setup>
import { ref, onMounted } from "vue";
import { useRouter } from "vue-router";
import { ElMessage } from "element-plus";
import { Search, View, Star } from "@element-plus/icons-vue";
import {
  findAll,
  findCategoryByNameLike,
  findDescendantsByParentName,
} from "@/api/common/category";
import { findByCategory } from "@/api/post/post";
import { formatDate } from "@/utils/date";

const router = useRouter();
const searchQuery = ref("");
const categoriesTree = ref([]);
const selectedCategory = ref(null);
const categoryPosts = ref([]);
const sortBy = ref("newest");

const defaultProps = {
  children: "children",
  label: "name",
};

// 加载所有分类
const loadCategories = async () => {
  try {
    const response = await findAll();
    if (response.data?.status === 200) {
      // 使用相同的数据结构
      categoriesTree.value = response.data.data.map((item) => ({
        ...item,
        label: item.category.name,
        children: item.children?.map((child) => ({
          ...child,
          label: child.category.name,
        })),
      }));
    }
  } catch (error) {
    ElMessage.error("加载分类失败");
  }
};

// 构建分类树
const buildCategoryTree = (categories) => {
  const tree = [];
  const map = {};

  // 首先创建所有节点的映射
  categories.forEach((category) => {
    map[category.id] = {
      ...category,
      children: [],
    };
  });

  // 构建树结构
  categories.forEach((category) => {
    const node = map[category.id];
    if (category.parentId) {
      const parent = map[category.parentId];
      if (parent) {
        parent.children.push(node);
      }
    } else {
      tree.push(node);
    }
  });

  return tree;
};

// 处理分类点击
const handleCategoryClick = async (data) => {
  selectedCategory.value = data.category;
  await loadCategoryPosts(data.category.categoryId);
};

// 加载分类下的文章
const loadCategoryPosts = async (categoryId) => {
  try {
    const response = await findByCategory(categoryId);
    if (response.data?.status === 200) {
      categoryPosts.value = response.data.data;
    }
  } catch (error) {
    ElMessage.error("加载文章失败");
  }
};

// 查看文章列表
const viewPosts = (data) => {
  selectedCategory.value = data.category;
  loadCategoryPosts(data.category.categoryId);
};

// 搜索处理
const handleSearch = async () => {
  if (!searchQuery.value) {
    await loadCategories();
    return;
  }
  try {
    const response = await findCategoryByNameLike(searchQuery.value);
    if (response.data?.status === 200) {
      categoriesTree.value = buildCategoryTree(response.data.data);
    }
  } catch (error) {
    ElMessage.error("搜索失败");
  }
};

onMounted(() => {
  loadCategories();
});
</script>

<template>
  <page-container class="categories-container">
    <el-row :gutter="20" class="full-height">
      <!-- 分类概览卡片 -->
      <el-col :span="6" class="left-sidebar">
        <el-card shadow="never" class="category-overview">
          <template #header>
            <div class="card-header">
              <h3>分类概览</h3>
              <el-input
                v-model="searchQuery"
                placeholder="搜索分类"
                class="search-input"
                clearable
                @input="handleSearch"
              >
                <template #prefix>
                  <el-icon><Search /></el-icon>
                </template>
              </el-input>
            </div>
          </template>

          <!-- 分类树形展示 -->
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
            >
              <template #default="{ node, data }">
                <div class="custom-tree-node">
                  <div class="node-content">
                    <span class="category-name">{{ data.category.name }}</span>
                    <span class="post-count">({{ data.category.postCount || 0 }})</span>
                  </div>
                  <div class="node-actions">
                    <el-button-group>
                      <el-button type="primary" link @click.stop="viewPosts(data)">
                        查看文章
                      </el-button>
                    </el-button-group>
                  </div>
                </div>
              </template>
            </el-tree>
          </div>
        </el-card>
      </el-col>

      <!-- 分类文章列表 -->
      <el-col :span="18" class="main-content">
        <el-card v-if="selectedCategory" shadow="never" class="posts-list">
          <template #header>
            <div class="card-header">
              <h3>{{ selectedCategory.name }} 的文章</h3>
              <div class="header-actions">
                <el-select v-model="sortBy" placeholder="排序方式" size="small">
                  <el-option label="最新发布" value="newest" />
                  <el-option label="最多浏览" value="views" />
                  <el-option label="最多点赞" value="likes" />
                </el-select>
              </div>
            </div>
          </template>

          <el-empty v-if="categoryPosts.length === 0" description="暂无文章" />

          <div v-else class="posts-grid">
            <el-card
              v-for="post in categoryPosts"
              :key="post.id"
              class="post-card"
              shadow="hover"
              @click="router.push(`/post/${post.id}`)"
            >
              <div class="post-cover" v-if="post.cover">
                <el-image :src="post.cover" fit="cover" />
              </div>
              <div class="post-info">
                <h4 class="post-title">{{ post.title }}</h4>
                <p class="post-summary">{{ post.summary }}</p>
                <div class="post-meta">
                  <span class="post-date">{{ formatDate(post.createdAt) }}</span>
                  <div class="post-stats">
                    <span title="浏览量">
                      <el-icon><View /></el-icon>
                      {{ post.views }}
                    </span>
                    <span title="点赞数">
                      <el-icon><Star /></el-icon>
                      {{ post.likes }}
                    </span>
                  </div>
                </div>
              </div>
            </el-card>
          </div>
        </el-card>
        <el-empty v-else description="请选择一个分类" />
      </el-col>
    </el-row>
  </page-container>
</template>

<style lang="scss" scoped>
.category-overview {
  margin-bottom: 20px;

  .card-header {
    display: flex;
    justify-content: space-between;
    align-items: center;

    h3 {
      margin: 0;
      font-size: 18px;
    }

    .search-input {
      width: 250px;
    }
  }
}

.custom-tree-node {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding-right: 8px;

  .node-content {
    display: flex;
    align-items: center;
    gap: 8px;

    .category-name {
      font-size: 14px;
    }

    .post-count {
      color: var(--el-text-color-secondary);
      font-size: 12px;
    }
  }
}

.posts-list {
  .card-header {
    display: flex;
    justify-content: space-between;
    align-items: center;

    h3 {
      margin: 0;
      font-size: 18px;
    }
  }

  .posts-grid {
    display: grid;
    grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
    gap: 20px;
    padding: 20px 0;

    .post-card {
      cursor: pointer;
      transition: all 0.3s ease;

      &:hover {
        transform: translateY(-5px);
      }

      .post-cover {
        height: 160px;
        overflow: hidden;

        .el-image {
          width: 100%;
          height: 100%;
          object-fit: cover;
        }
      }

      .post-info {
        padding: 12px;

        .post-title {
          margin: 0 0 8px;
          font-size: 16px;
          font-weight: 500;
          line-height: 1.4;
        }

        .post-summary {
          margin: 0 0 12px;
          font-size: 14px;
          color: var(--el-text-color-secondary);
          line-height: 1.5;
          display: -webkit-box;
          -webkit-line-clamp: 2;
          -webkit-box-orient: vertical;
          overflow: hidden;
        }

        .post-meta {
          display: flex;
          justify-content: space-between;
          align-items: center;
          font-size: 12px;
          color: var(--el-text-color-secondary);

          .post-stats {
            display: flex;
            gap: 12px;

            span {
              display: flex;
              align-items: center;
              gap: 4px;
            }
          }
        }
      }
    }
  }
}

.categories-container {
  height: calc(100vh - 180px);
  margin: -2rem;
  padding: 2rem;
}

.full-height {
  height: 100%;
}

.left-sidebar {
  height: 100%;
  .category-overview {
    height: 100%;
    display: flex;
    flex-direction: column;

    :deep(.el-card__body) {
      flex: 1;
      overflow: auto;
    }
  }
}

.main-content {
  height: 100%;
  .posts-list {
    height: 100%;
    display: flex;
    flex-direction: column;

    :deep(.el-card__body) {
      flex: 1;
      overflow: auto;
    }
  }
}
</style>
