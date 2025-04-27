<script setup>
import { ref, onMounted, computed, watch, onBeforeUnmount } from "vue";
import { ElMessage } from "element-plus";
import { Search, CircleClose, Star } from "@element-plus/icons-vue";
import { findAll } from "@/api/common/tag";
import PostListItem from "@/components/blog/post/PostListItem.vue";
import { searchTagSuggestions, searchPosts } from "@/api/search/es.js";
import { findByTags } from "@/api/post/post";
import { useRouter } from "vue-router";

const router = useRouter();

// 添加路由历史状态监听
window.addEventListener('popstate', () => {
  cleanup();
});

const searchQuery = ref("");
const tags = ref([]);
const selectedTags = ref([]);
const tagPosts = ref([]);
const sortBy = ref("newest");
const loading = ref(false);

// 添加计算属性用于过滤标签
const filteredTags = computed(() => {
  if (!searchQuery.value) {
    return tags.value;
  }
  return tags.value.filter((tag) =>
    tag.name.toLowerCase().includes(searchQuery.value.toLowerCase())
  );
});

// 修改 loadTags 函数
const loadTags = async () => {
  loading.value = true;
  try {
    const response = await findAll();
    if (response.data?.status === 200) {
      tags.value = response.data.data.content;

      // 在标签数据加载完成后处理选择
      const tagParam = router.currentRoute.value.query.tag;
      if (tagParam) {
        // 如果URL中有标签参数，可能是单个标签或逗号分隔的多个标签
        const tagNames = decodeURIComponent(tagParam).split(',').filter(Boolean);
        const matchedTags = tags.value.filter(tag => tagNames.includes(tag.name));

        if (matchedTags.length > 0) {
          selectedTags.value = matchedTags.map(tag => tag.tagId);
          await loadTagPosts();
        }
      } else if (tags.value.length > 0 && !selectedTags.value.length) {
        // 仅在首次加载且没有任何选中标签时，默认选择第一个
        selectedTags.value = [tags.value[0].tagId];
        await loadTagPosts();
      }

      if (tags.value.length === 0) {
        ElMessage.warning("暂无标签数据");
      }
    }
  } catch (error) {
    ElMessage.error("加载标签失败");
  } finally {
    loading.value = false;
  }
};

// 修改路由监听器
watch(
  () => router.currentRoute.value.query.tag,
  async (newTag) => {
    if (tags.value.length > 0) {
      if (!newTag) {
        // 如果URL中没有标签参数，清空选择
        selectedTags.value = [];
        tagPosts.value = [];
      } else {
        // 处理多个标签的情况
        const tagNames = decodeURIComponent(newTag).split(',').filter(Boolean);
        const matchedTags = tags.value.filter(tag => tagNames.includes(tag.name));

        if (matchedTags.length > 0) {
          selectedTags.value = matchedTags.map(tag => tag.tagId);
          await loadTagPosts();
        }
      }
    }
  }
);

// 修改 handleTagClick 函数
const handleTagClick = async (tag) => {
  const index = selectedTags.value.indexOf(tag.tagId);
  if (index > -1) {
    // 如果已选中，取消选中
    selectedTags.value.splice(index, 1);
  } else {
    // 如果未选中，添加到选中列表
    selectedTags.value.push(tag.tagId);
  }

  // 统一处理URL更新
  if (selectedTags.value.length > 0) {
    const tagNames = tags.value
      .filter(t => selectedTags.value.includes(t.tagId))
      .map(t => t.name);
    router.push({
      query: { tag: tagNames.join(',') }
    });
  } else {
    router.push({ query: {} });
  }

  await loadTagPosts();
};

const selectedTagObjects = computed(() => {
  // Use tag.tagId instead of tag.id
  return tags.value.filter((tag) => selectedTags.value.includes(tag.tagId));
});

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

// 加载标签下的文章
const loadTagPosts = async () => {
  if (selectedTags.value.length === 0) {
    tagPosts.value = [];
    return;
  }

  loading.value = true;
  try {
    // Changed: Use findByTags API with tag names array
    const tagNames = selectedTagObjects.value.map((tag) => tag.name);
    const response = await findByTags(tagNames, {
      page: currentPage.value - 1,
      size: pageSize.value,
      sorts: ["createdAt,desc"]
    });

    if (response.data?.status === 200) {
      const data = response.data.data.content;
      // 如果是分页数据
      if (data.content) {
        tagPosts.value = transformPostData(data.content);
        total.value = data.totalElements; // 设置总数
      } else {
        tagPosts.value = transformPostData(data);
        total.value = data.length; // 如果是普通数组，使用长度作为总数
      }
    }
  } catch (error) {
    console.error("loadTagPosts error:", error);
    ElMessage.error("加载文章失败");
  } finally {
    loading.value = false;
  }
};

// 获取标签类型
const getTagType = (weight) => {
  if (weight >= 8) return "danger";
  if (weight >= 5) return "warning";
  if (weight >= 3) return "success";
  return "info";
};

// 搜索处理
const handleSearch = async (selectedItem) => {
  // 统一获取查询内容（支持从建议项选择或直接输入）
  const query =
    typeof selectedItem === "string"
      ? selectedItem
      : selectedItem?.value || searchQuery.value;

  if (!query) {
    if (selectedTags.value.length > 0) {
      await loadTagPosts();
    } else {
      await loadTags();
    }
    return;
  }

  loading.value = true;
  try {
    // 检查是否是标签
    const isTag = tags.value.some((tag) => tag.name === query);

    if (isTag) {
      // 标签搜索逻辑
      const matchedTag = tags.value.find((tag) => tag.name === query);
      if (matchedTag && !selectedTags.value.includes(matchedTag.tagId)) {
        selectedTags.value.push(matchedTag.tagId);
        await loadTagPosts();
      }
    } else {
      let finalQuery = query;
      // 如果已选择标签，在搜索内容前面加上标签搜索条件
      if (selectedTags.value.length > 0 && !query.includes("#")) {
        const tagNames = selectedTagObjects.value.map((tag) => tag.name);
        finalQuery = `#${tagNames.join(",")}# ${query}`;
      }
      // 如果输入的是纯分类(以#开头)，保持原样
      else if (query.startsWith("#")) {
        finalQuery = query;
      }

      console.log("Final query:", finalQuery);

      // 非标签内容，执行全文搜索
      const response = await searchPosts(finalQuery, 0, 10);
      console.log("response:", response);
      if (response.data?.status === 200) {
        tagPosts.value = transformPostData(response.data.data.content);
        console.log("Tag posts loaded:", tagPosts.value);
      }
    }
  } catch (error) {
    console.error("搜索失败:", error);
    ElMessage.error("搜索失败");
  }
};

onMounted(() => {
  loadTags();
});

// 添加标签搜索建议函数
const querySearchAsync = async (queryString, cb) => {
  if (queryString) {
    try {
      const response = await searchTagSuggestions(queryString); // 专门获取标签建议
      if (response.data?.status === 200) {
        const suggestions = response.data.data.map((tagName) => ({
          value: tagName,
          label: tagName,
        }));
        cb(suggestions);
      }
    } catch (error) {
      console.error("获取标签建议失败:", error);
      ElMessage.error("获取搜索建议失败");
      cb([]);
    }
  } else {
    cb([]);
  }
};

// 修改清空标签的函数
const clearSelectedTags = () => {
  selectedTags.value = [];
  // 清除URL参数
  router.push({ query: {} });
  tagPosts.value = []; // 清空文章列表
};

/**
 * 搜索框监视器
 */
watch(searchQuery, (newVal) => {
  if (!newVal || newVal.trim() === "") {
    if (selectedTags.value.length > 0) {
      // 有选中的标签，获取对应文章
      loadTagPosts();
    } else {
      // 没有选中的标签，重新加载所有标签
      loadTags();
    }
  }
});

/**
 * 排序计算属性
 */
const sortedPosts = computed(() => {
  if (!tagPosts.value.length) return [];

  return [...tagPosts.value].sort((a, b) => {
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

// 清理函数
const cleanup = () => {
  selectedTags.value = [];
  tagPosts.value = [];
  searchQuery.value = "";
  // 清除URL中的参数
  if (router.currentRoute.value.query.tag) {
    router.replace({
      query: {}
    });
  }
};

// 在组件卸载前清理状态
onBeforeUnmount(() => {
  cleanup();
});

// 修改移除单个标签的函数
const removeTag = async (tagToRemove) => {
  const index = selectedTags.value.indexOf(tagToRemove.tagId);
  if (index > -1) {
    selectedTags.value.splice(index, 1);

    // 统一处理URL更新
    if (selectedTags.value.length > 0) {
      const tagNames = tags.value
        .filter(t => selectedTags.value.includes(t.tagId))
        .map(t => t.name);
      router.push({
        query: { tag: tagNames.join(',') }
      });
    } else {
      router.push({ query: {} });
    }

    await loadTagPosts();
  }
};
</script>

<template>
  <!-- 模板部分保持不变 -->
  <div class="app-container">
    <div class="tags-wrapper" v-loading="loading">
      <!-- 左侧标签云 -->
      <div class="left-sidebar">
        <el-card shadow="hover" class="tags-card">
          <template #header>
            <div class="card-header">
              <h3>
                <el-icon><Collection /></el-icon> 标签云
              </h3>
              <div style="display: flex; gap: 10px">
                <el-autocomplete
                  v-model="searchQuery"
                  :fetch-suggestions="querySearchAsync"
                  placeholder="搜索标签..."
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
                <el-button type="primary" @click="handleSearch" size="large">
                  搜索
                </el-button>
              </div>
            </div>
          </template>

          <div class="tags-container">
            <el-empty v-if="tags.length === 0" description="暂无标签" />
            <div class="tags-group">
              <div
                v-for="tag in filteredTags"
                :key="tag.tagId"
                class="tag-item"
                @click="handleTagClick(tag)"
              >
                <el-tag
                  :type="getTagType(tag.weight)"
                  :effect="selectedTags.includes(tag.tagId) ? 'dark' : 'light'"
                  size="large"
                >
                  {{ tag.name }}
                  <span class="tag-count">{{ tag.useCount || 0 }}</span>
                </el-tag>
              </div>
            </div>
          </div>
        </el-card>
      </div>

      <!-- 右侧文章列表 -->
      <div class="right-content">
        <el-card
          shadow="hover"
          class="posts-card"
          v-if="selectedTags.length > 0 || tagPosts.length > 0"
        >
          <template #header>
            <div class="posts-header">
              <div class="tags-header">
                <h2 class="tags-title">
                  <el-tag
                    v-for="tag in selectedTagObjects"
                    :key="tag.tagId"
                    :type="getTagType(tag.weight)"
                    size="large"
                    closable
                    @close="removeTag(tag)"
                  >
                    {{ tag.name }}
                  </el-tag>
                </h2>
                <span class="posts-total">{{ tagPosts.length }} 篇文章</span>
              </div>
              <div class="header-controls">
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
                <el-button
                  type="danger"
                  @click="clearSelectedTags"
                  :icon="CircleClose"
                  plain
                  size="large"
                >
                  清空标签
                </el-button>
              </div>
            </div>
          </template>

          <PostListItem :posts="sortedPosts" :show-keyword="false" />

          <el-pagination
            v-if="total > 0"
            v-model:current-page="currentPage"
            v-model:page-size="pageSize"
            :total="total"
            :page-sizes="[10, 20, 50]"
            layout="total, sizes, prev, pager, next"
            @size-change="loadTagPosts"
            @current-change="loadTagPosts"
          />
        </el-card>

        <el-card shadow="hover" class="welcome-card" v-else>
          <template #header>
            <h2>
              <el-icon><Collection /></el-icon> 欢迎使用标签浏览
            </h2>
          </template>
          <div class="welcome-content">
            <el-empty description="请从左侧选择标签" :image-size="150">
              <p class="welcome-tip">点击标签查看相关文章，可多选标签筛选文章</p>
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

.tags-wrapper {
  width: 100%;
  width: 1200px;
  display: flex;
  gap: 24px;
}

.left-sidebar {
  width: 320px;
  min-width: 320px;

  .tags-card {
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

    .tags-container {
      padding: 16px;
      flex: 1;
      overflow: auto;

      .el-empty {
        height: 100%;
        display: flex;
        flex-direction: column;
        justify-content: center;
      }
    }

    .tags-group {
      display: flex;
      flex-wrap: wrap;
      gap: 12px;
      align-content: flex-start;
    }

    .tag-item {
      cursor: pointer;
      transition: all 0.2s ease;

      &:hover {
        transform: translateY(-2px);
      }

      .el-tag {
        transition: all 0.3s ease;
        padding: 8px 16px;
        font-size: 14px;
        border-radius: 16px;
        box-shadow: 0 2px 6px rgba(0, 0, 0, 0.05);
        border: 1px solid rgba(0, 0, 0, 0.05);

        &:hover {
          box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
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

      .tags-header {
        display: flex;
        align-items: center;
        gap: 12px;
        flex-wrap: wrap;

        .tags-title {
          margin: 0;
          display: flex;
          align-items: center;
          gap: 8px;
          flex-wrap: wrap;

          .el-tag {
            cursor: pointer;
            transition: all 0.2s ease;
            border-radius: 16px;
            padding: 8px 16px;

            &:hover {
              opacity: 0.9;
              box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
            }
          }
        }

        .posts-total {
          font-size: 16px;
          color: #7a8ba9;
          font-weight: 500;
          flex-shrink: 0;
        }
      }

      .header-controls {
        display: flex;
        align-items: center;
        gap: 12px;
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
  .tags-wrapper {
    max-width: 100%;
    padding: 0 20px;
  }
}

@media (max-width: 992px) {
  .app-container {
    padding: 20px;
  }

  .tags-wrapper {
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
</style>
