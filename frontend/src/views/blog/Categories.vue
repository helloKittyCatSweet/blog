<script setup>
import { ref, onMounted, watch, computed, nextTick } from 'vue'
import { ElMessage, ElLoading, ElAutocomplete } from 'element-plus'
import { Search, View, Star, Reading } from '@element-plus/icons-vue'
import { findAllNoPage } from '@/api/common/category'
import { findByCategory, findAll as findAllPosts } from '@/api/post/post'
import { searchCategorySuggestions, searchPosts } from '@/api/search/es'
import PostListItem from '@/components/blog/post/PostListItem.vue'
import { useRouter } from 'vue-router'

const router = useRouter()

const searchQuery = ref('')
const categoriesTree = ref([])
const selectedCategory = ref(null)
const categoryPosts = ref([])
const sortBy = ref('newest')
const loading = ref(false)
const defaultExpandedKeys = ref([])

// 提取的数据转换方法
const transformPostData = (rawData) => {
  if (!Array.isArray(rawData)) {
    console.warn('rawData is not an array, returning empty array')
    return []
  }
  return rawData.map((item) => ({
    id: item.post.postId,
    title: item.post.title,
    content: item.post.content,
    cover: item.post.coverImage,
    excerpt: item.post.abstractContent || item.post.content?.substring(0, 200) + '...',
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
    favorites: item.post.favorites || 0,
    createTime: item.post.createdAt,
    author: item.author,
    userId: item.post.userId,

    highlightTitle: item.post.title,
    highlightContent: item.post.content,
  }))
}

// 加载文章，根据是否有选中分类决定加载分类文章还是全部文章
const loadPosts = async () => {
  if (!selectedCategory.value?.categoryId) {
    categoryPosts.value = []
    total.value = 0
    return
  }

  loading.value = true
  try {
    let response
    // 组合搜索：当前分类 + 搜索关键词
    if (searchQuery.value) {
      response = await searchPosts(`@${selectedCategory.value.name}@ ${searchQuery.value}`, {
        page: currentPage.value - 1,
        size: pageSize.value,
        sorts:
          sortBy.value === 'newest'
            ? 'createTime,desc'
            : sortBy.value === 'views'
            ? 'viewCount,desc'
            : 'likeCount,desc',
      })
    } else {
      // 仅按分类搜索
      response = await findByCategory(selectedCategory.value.name, {
        page: currentPage.value - 1,
        size: pageSize.value,
        sorts:
          sortBy.value === 'newest'
            ? 'createdAt,desc'
            : sortBy.value === 'views'
            ? 'views,desc'
            : 'likes,desc',
      })
    }

    if (response.data?.status === 200) {
      const data = response.data.data
      if (data.content) {
        categoryPosts.value = transformPostData(data.content)
        total.value = data.totalElements
      } else {
        categoryPosts.value = transformPostData(data)
        total.value = data.length
      }
    }
  } catch (error) {
    console.error('加载文章失败:', error)
    ElMessage.error('加载文章失败')
    categoryPosts.value = []
    total.value = 0
  } finally {
    loading.value = false
  }
}

// 添加一个递归函数来扁平化分类树
const flattenCategoryTree = (items) => {
  const flattenedCategories = []
  const flatten = (items) => {
    for (const item of items) {
      flattenedCategories.push(item.category)
      if (item.children?.length > 0) {
        flatten(item.children)
      }
    }
  }
  flatten(items)
  return flattenedCategories
}

// 修改路由监听器，只处理路由变化时的情况
watch(
  () => router.currentRoute.value.query.category,
  async (newCategory) => {
    if (categoriesTree.value.length > 0) {
      if (!newCategory) {
        // 如果没有分类参数，选择第一个分类并加载文章
        selectedCategory.value = categoriesTree.value[0].category
        await loadPosts()
      } else {
        // 有分类参数时的处理
        const decodedCategoryName = decodeURIComponent(newCategory)
        const flattenedCategories = flattenCategoryTree(categoriesTree.value)
        const matchedCategory = flattenedCategories.find(
          (category) => category.name === decodedCategoryName
        )

        if (matchedCategory) {
          selectedCategory.value = matchedCategory
          await loadPosts()
        }
      }
    }
  }
)

// 修改 loadCategories 函数
const loadCategories = async () => {
  loading.value = true
  try {
    const response = await findAllNoPage()
    if (response.data?.status === 200) {
      // 检查并处理数据结构
      const content = response.data.data
      categoriesTree.value = content.map((item) => ({
        category: {
          categoryId: item.category.categoryId,
          name: item.category.name,
          useCount: item.category.useCount || 0,
          description: item.category.description || '',
        },
        label: item.category.name,
        children: (item.children || []).map((child) => ({
          category: {
            categoryId: child.category.categoryId,
            name: child.category.name,
            useCount: child.category.useCount || 0,
            description: child.category.description || '',
          },
          label: child.category.name,
          children: child.children || [],
        })),
      }))

      // 设置默认展开的节点
      if (categoriesTree.value.length > 0) {
        defaultExpandedKeys.value = [categoriesTree.value[0].category.categoryId]
      }

      // 处理选择逻辑
      const categoryParam = router.currentRoute.value.query.category
      if (categoryParam && categoriesTree.value.length > 0) {
        const decodedCategoryName = decodeURIComponent(categoryParam)
        const flattenedCategories = flattenCategoryTree(categoriesTree.value)
        const matchedCategory = flattenedCategories.find(
          (category) => category.name === decodedCategoryName
        )
        selectedCategory.value = matchedCategory || categoriesTree.value[0].category
      } else if (categoriesTree.value.length > 0) {
        selectedCategory.value = categoriesTree.value[0].category
      }

      // 在 nextTick 中设置当前选中节点
      await nextTick()
      if (treeRef.value && selectedCategory.value) {
        treeRef.value.setCurrentKey(selectedCategory.value.categoryId)
        // 确保选中状态被应用
        treeRef.value.store.currentNode = treeRef.value.store.nodesMap[selectedCategory.value.categoryId]
      }

      // 加载文章
      if (selectedCategory.value) {
        await loadPosts()
      } else if (categoriesTree.value.length === 0) {
        ElMessage.warning('暂无分类数据')
      }
    }
  } catch (error) {
    console.error('加载分类失败:', error)
    ElMessage.error('加载分类失败')
  } finally {
    loading.value = false
  }
}

// 修改 handleCategoryClick 函数
const handleCategoryClick = async (data) => {
  if (data.category.categoryId === selectedCategory.value?.categoryId) {
    return; // 如果点击的是当前选中的分类，不做任何操作
  }
  selectedCategory.value = data.category;
  // 更新 URL 参数
  router.push({
    query: { category: data.category.name },
  });
  await loadPosts();
};

// 修改 viewPosts 函数
const viewPosts = async (data) => {
  if (data.category.categoryId === selectedCategory.value?.categoryId) {
    return; // 如果点击的是当前选中的分类，不做任何操作
  }
  selectedCategory.value = data.category;
  // 更新 URL 参数
  router.push({
    query: { category: data.category.name },
  });
  await loadPosts();
};

// 搜索处理
const handleSearch = async (selectedItem) => {
  // 统一获取查询内容
  let query =
    typeof selectedItem === 'string' ? selectedItem : selectedItem?.value || searchQuery.value

  if (!query) {
    if (selectedCategory.value) {
      await loadPosts()
    } else {
      await loadCategories()
    }
    return
  }

  loading.value = true
  try {
    // 检查是否是分类（自动补全选择时）
    const isCategorySuggestion = categoriesTree.value.some(
      (item) =>
        item.category.name === query ||
        item.children?.some((child) => child.category.name === query)
    )

    if (isCategorySuggestion) {
      // 处理分类选择
      const matchedCategory = categoriesTree.value.find(
        (item) =>
          item.category.name === query ||
          item.children?.some((child) => child.category.name === query)
      )
      if (matchedCategory) {
        selectedCategory.value = matchedCategory.category
        await loadPosts()
      }
    } else {
      // 处理手动输入的搜索（构建完整查询）
      let finalQuery = query

      // 如果已选分类但查询中未指定@分类，自动附加分类条件
      if (selectedCategory.value && !query.includes('@')) {
        finalQuery = `@${selectedCategory.value.name}@ ${query}`
      }
      // 如果输入的是纯标签（以#开头），保持原样
      else if (query.startsWith('#')) {
        finalQuery = query
      }
      // 其他情况直接使用原查询

      console.log('Final search query:', finalQuery) // 调试用

      const response = await searchPosts(finalQuery, 0, 10)
      if (response.data?.status === 200) {
        categoryPosts.value = transformPostData(response.data.data.content)
        // 保持当前分类选中状态
      }
    }
  } catch (error) {
    console.error('搜索失败:', error)
    ElMessage.error('搜索失败')
  } finally {
    loading.value = false
  }
}

// 补全搜索建议
const querySearchAsync = async (queryString, cb) => {
  if (queryString) {
    try {
      const response = await searchCategorySuggestions(queryString)
      console.log('搜索建议：', response.data?.data, '查询内容：', queryString)

      if (response.data?.status === 200) {
        // 处理字符串数组的情况
        const suggestions = response.data.data.map((item) => ({
          value: typeof item === 'string' ? item : item.category?.name || item.name || '',
          ...(typeof item === 'object' ? item : {}), // 保留原始对象属性（如果有）
        }))
        cb(suggestions)
      } else {
        cb([]) // 状态码不是200时返回空数组
      }
    } catch (error) {
      console.error('获取搜索建议失败:', error)
      ElMessage.error('获取搜索建议失败')
      cb([])
    }
  } else {
    cb([])
  }
}

/**
 * 搜索框监视器
 */
watch(searchQuery, (newVal) => {
  if (!newVal || newVal.trim() === '') {
    if (selectedCategory?.categoryId) {
      // 有选中的分类，获取对应文章
      loadPosts()
    } else {
      // 没有选中的分类，重新加载所有分类
      loadCategories()
    }
  }
})

/**
 * 分页
 */
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)

const handleSizeChange = (val) => {
  pageSize.value = val
  currentPage.value = 1 // 切换每页显示数量时重置为第一页
  loadPosts()
}

const handleCurrentChange = (val) => {
  currentPage.value = val
  // 滚动到页面顶部
  window.scrollTo({
    top: 0,
    behavior: 'smooth',
  })
  loadPosts()
}

onMounted(() => {
  loadCategories().then(() => {
    // 确保在加载完成后设置选中状态
    nextTick(() => {
      if (treeRef.value && selectedCategory.value) {
        treeRef.value.setCurrentKey(selectedCategory.value.categoryId)
      }
    })
  })
})

// 添加 watch 监听排序变化
watch(sortBy, async () => {
  if (selectedCategory.value) {
    await loadPosts()
  }
})

const treeRef = ref(null)
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
                <el-icon>
                  <Reading />
                </el-icon>
                分类浏览
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
                    <el-icon>
                      <Search />
                    </el-icon>
                  </template>
                </el-autocomplete>
                <el-button type="primary" @click="handleSearch" size="large">搜索</el-button>
              </div>
            </div>
          </template>

          <div class="categories-tree-container">
            <el-empty v-if="categoriesTree.length === 0" description="暂无分类" />
            <el-tree
              v-else
              ref="treeRef"
              :data="categoriesTree"
              :props="{
                children: 'children',
                label: (data) => data.category.name,
              }"
              node-key="category.categoryId"
              :highlight-current="true"
              :current-node-key="selectedCategory?.categoryId"
              :default-expanded-keys="defaultExpandedKeys"
              :expand-on-click-node="false"
              @node-click="handleCategoryClick"
              v-loading="loading"
            >
              <template #default="{ node, data }">
                <div
                  class="custom-tree-node"
                  :class="{ 'is-selected': data.category.categoryId === selectedCategory?.categoryId }"
                >
                  <div class="node-content">
                    <el-tag
                      size="large"
                      :type="data.category.categoryId === selectedCategory?.categoryId ? 'primary' : 'info'"
                      :effect="data.category.categoryId === selectedCategory?.categoryId ? 'dark' : 'light'"
                      class="category-tag"
                    >
                      {{ data.category.name }}
                      <span class="tag-count">{{ data.category.useCount || 0 }}</span>
                    </el-tag>
                  </div>
                  <div class="node-actions">
                    <el-button
                      type="primary"
                      link
                      @click.stop="viewPosts(data)"
                    >
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

          <el-empty v-if="categoryPosts.length === 0" description="暂无文章" :image-size="120" />

          <div v-else class="posts-list">
            <PostListItem :posts="categoryPosts" :show-keyword="false" />

            <div class="pagination-container">
              <el-pagination
                v-model:current-page="currentPage"
                v-model:page-size="pageSize"
                :page-sizes="[10, 20, 50]"
                :total="total"
                layout="total, sizes, prev, pager, next, jumper"
                @size-change="handleSizeChange"
                @current-change="handleCurrentChange"
              />
            </div>
          </div>
        </el-card>

        <el-card shadow="hover" class="welcome-card" v-else>
          <template #header>
            <h2>
              <el-icon>
                <Reading />
              </el-icon>
              欢迎使用分类浏览
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
    display: flex;
    flex-direction: column;

    :deep(.el-card__header) {
      background: linear-gradient(135deg, #f6f8fa, #e9ecef);
      border-bottom: 1px solid #e0e3e8;
      padding: 20px;
      border-radius: 12px 12px 0 0;
      flex-shrink: 0;
    }

    :deep(.el-card__body) {
      flex: 1;
      padding: 0;
      overflow: auto;
      display: flex;
      flex-direction: column;
    }

    .card-header {
      display: flex;
      flex-direction: column;
      gap: 16px;

      h3 {
        margin: 0;
        font-size: 20px;
        color: #2c3e50;
        display: flex;
        align-items: center;
        gap: 10px;
        font-weight: 600;

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
          box-shadow: 0 1px 4px rgba(0, 0, 0, 0.08);
        }
      }
    }

    .categories-tree-container {
      padding: 16px;
      flex: 1;
      overflow: auto;

      .el-empty {
        height: 100%;
        display: flex;
        flex-direction: column;
        justify-content: center;
      }

      :deep(.el-tree) {
        background: none;
      }

      :deep(.el-tree-node) {
        margin: 12px 0;
      }

      :deep(.el-tree-node__content) {
        min-height: 30px;
        padding: 4px;
        border-radius: 6px;
        transition: all 0.3s ease;
        background: none;

        &:hover {
          background: none;
        }
      }

      .custom-tree-node {
        flex: 1;
        display: flex;
        align-items: center;
        justify-content: space-between;
        padding: 4px;
        width: 100%;
        transition: all 0.2s ease;

        .node-content {
          display: flex;
          align-items: center;
          gap: 12px;

          .category-tag {
            transition: all 0.3s ease;
            padding: 8px 16px;
            font-size: 14px;
            border-radius: 16px;
            box-shadow: 0 2px 6px rgba(0, 0, 0, 0.05);
            border: 1px solid rgba(0, 0, 0, 0.05);
            cursor: pointer;

            &:hover {
              box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
              transform: translateY(-2px);
            }

            .tag-count {
              margin-left: 4px;
              font-size: 12px;
              opacity: 0.8;
              background-color: rgba(255, 255, 255, 0.3);
              padding: 2px 6px;
              border-radius: 8px;
            }
          }
        }

        .node-actions {
          opacity: 0;
          transition: opacity 0.3s ease;
        }

        &:hover .node-actions {
          opacity: 1;
        }
      }
    }
  }
}

.right-content {
  flex: 1;
  min-width: 0;
  display: flex;
  flex-direction: column;

  .posts-card,
  .welcome-card {
    height: 100%;
    border-radius: 12px;
    border: none;
    box-shadow: 0 4px 12px rgba(0, 0, 0, 0.08);
    display: flex;
    flex-direction: column;

    :deep(.el-card__header) {
      background: linear-gradient(135deg, #f6f8fa, #e9ecef);
      border-bottom: 1px solid #e0e3e8;
      padding: 20px;
      border-radius: 12px 12px 0 0;
      flex-shrink: 0;
    }

    :deep(.el-card__body) {
      flex: 1;
      overflow: auto;
      padding: 24px;
    }

    .posts-header {
      display: flex;
      justify-content: space-between;
      align-items: center;
      flex-wrap: wrap;
      gap: 16px;

      .category-header {
        display: flex;
        align-items: center;
        gap: 12px;
        flex-wrap: wrap;

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
            box-shadow: 0 1px 4px rgba(0, 0, 0, 0.08);
          }
        }
      }
    }
  }

  .welcome-card {
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

      .el-empty {
        :deep(.el-empty__description) {
          font-size: 16px;
          color: #5e6d82;
        }
      }

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
}

.posts-list {
  .pagination {
    margin-top: 20px;
    display: flex;
    justify-content: center;
  }
}

.pagination-container {
  margin-top: 20px;
  display: flex;
  justify-content: center;
}
</style>
