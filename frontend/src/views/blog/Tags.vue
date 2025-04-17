<script setup>
import { ref, onMounted } from "vue";
import { useRouter } from "vue-router";
import { ElMessage } from "element-plus";
import { Search, View, Star } from "@element-plus/icons-vue";
import { findAll, findByName } from "@/api/common/tag";
import { findByTag } from "@/api/post/post";
import { formatDate } from "@/utils/date";

const router = useRouter();
const searchQuery = ref("");
const tags = ref([]);
const selectedTag = ref(null);
const tagPosts = ref([]);
const sortBy = ref("newest");

// 加载所有标签
const loadTags = async () => {
  try {
    const response = await findAll();
    if (response.data?.status === 200) {
      tags.value = response.data.data;
    }
  } catch (error) {
    ElMessage.error("加载标签失败");
  }
};

// 处理标签点击
const handleTagClick = async (tag) => {
  selectedTag.value = tag;
  await loadTagPosts(tag.name);
};

// 加载标签下的文章
const loadTagPosts = async (tagName) => {
  try {
    const response = await findByTag(tagName);
    if (response.data?.status === 200) {
      tagPosts.value = response.data.data;
    }
  } catch (error) {
    ElMessage.error("加载文章失败");
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
const handleSearch = async () => {
  if (!searchQuery.value) {
    await loadTags();
    return;
  }
  try {
    const response = await findByName(searchQuery.value);
    if (response.data?.status === 200) {
      tags.value = [response.data.data];
    }
  } catch (error) {
    ElMessage.error("搜索失败");
  }
};

onMounted(() => {
  loadTags();
});
</script>

<template>
  <page-container class="tag-container">
    <el-row :gutter="20" class="full-height">
      <!-- 标签云卡片 -->
      <el-col :span="6" class="left-sidebar">
        <el-card shadow="never" class="tags-cloud">
          <template #header>
            <div class="card-header">
              <h3>标签云</h3>
              <el-input
                v-model="searchQuery"
                placeholder="搜索标签"
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

          <div class="tags-wrapper">
            <el-empty v-if="tags.length === 0" description="暂无标签" />
            <template v-else>
              <el-tag
                v-for="tag in tags"
                :key="tag.id"
                :type="getTagType(tag.weight)"
                :effect="selectedTag?.id === tag.id ? 'dark' : 'light'"
                class="tag-item"
                @click="handleTagClick(tag)"
              >
                {{ tag.name }}
                <template #suffix>
                  <span class="tag-count">({{ tag.postCount || 0 }})</span>
                </template>
              </el-tag>
            </template>
          </div>
        </el-card>
      </el-col>

      <!-- 标签文章列表 -->
      <el-col :span="18" class="main-content">
        <el-card v-if="selectedTag" shadow="never" class="posts-list">
          <template #header>
            <div class="card-header">
              <h3>{{ selectedTag.name }} 的文章</h3>
              <div class="header-actions">
                <el-select v-model="sortBy" placeholder="排序方式" size="small">
                  <el-option label="最新发布" value="newest" />
                  <el-option label="最多浏览" value="views" />
                  <el-option label="最多点赞" value="likes" />
                </el-select>
              </div>
            </div>
          </template>

          <el-empty v-if="tagPosts.length === 0" description="暂无文章" />

          <div v-else class="posts-grid">
            <el-card
              v-for="post in tagPosts"
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
        <el-empty v-else description="请选择一个标签" />
      </el-col>
    </el-row>
  </page-container>
</template>

<style lang="scss" scoped>
.tags-cloud {
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

  .tags-wrapper {
    display: flex;
    flex-wrap: wrap;
    gap: 12px;
    padding: 20px 0;

    .tag-item {
      cursor: pointer;
      font-size: 14px;
      padding: 8px 16px;

      .tag-count {
        margin-left: 4px;
        font-size: 12px;
        opacity: 0.8;
      }

      &:hover {
        transform: translateY(-2px);
        transition: transform 0.2s ease;
      }
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

.tags-container {
  height: calc(100vh - 180px);
  margin: -2rem;
  padding: 2rem;
}

.full-height {
  height: 100%;
}

.left-sidebar {
  height: 100%;
  .tags-cloud {
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
