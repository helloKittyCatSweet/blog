<script setup>
import { ref, onMounted } from "vue";
import { useRoute } from "vue-router";
import { ElMessage } from "element-plus";
import PageContainer from "@/components/PageContainer.vue";
import { MdEditor } from "md-editor-v3";
import "md-editor-v3/lib/style.css";
import { findById } from "@/api/post/post.js";

const route = useRoute();
const loading = ref(false);

// 文章数据
const post = ref({
  postId: null,
  title: "",
  content: "",
  coverImage: "",
  isPublished: false,
  isDraft: true,
  visibility: "PUBLIC",
  version: 1,
  category: null,
  tags: [],
  author: "",
  summary: "",
  createdTime: "",
  updatedTime: "",
  viewCount: 0,
});

// 获取文章详情
const getPostDetail = async (id) => {
  loading.value = true;
  try {
    const response = await findById(id);
    if (response.data.status === 200) {
      const postData = response.data.data;
      post.value = {
        postId: postData.post.postId,
        title: postData.post.title,
        content: postData.post.content,
        coverImage: postData.post.coverImage,
        isPublished: postData.post.isPublished,
        isDraft: postData.post.isDraft,
        visibility: postData.post.visibility,
        version: postData.post.version,
        category: postData.category,
        tags: postData.tags || [],
        author: postData.author,
        summary: postData.post.summary,
        createdTime: postData.post.createdTime,
        updatedTime: postData.post.updatedTime,
        viewCount: postData.post.viewCount || 0,
      };
    }
  } catch (error) {
    ElMessage.error("获取文章详情失败");
  } finally {
    loading.value = false;
  }
};

onMounted(() => {
  const postId = route.params.id;
  if (postId) {
    getPostDetail(postId);
  }
});
</script>

<template>
  <page-container>
    <el-card v-loading="loading" class="post-view">
      <!-- 文章头部信息 -->
      <div class="post-header">
        <h1 class="post-title">{{ post.title }}</h1>
        <div class="post-meta">
          <el-space wrap>
            <div class="meta-item">
              <el-icon><User /></el-icon>
              <span>{{ post.author }}</span>
            </div>
            <div class="meta-item">
              <el-icon><Clock /></el-icon>
              <span>{{ post.createdTime }}</span>
            </div>
            <div class="meta-item">
              <el-icon><View /></el-icon>
              <span>{{ post.viewCount }} 次阅读</span>
            </div>
          </el-space>
        </div>

        <!-- 分类和标签 -->
        <div class="post-tags">
          <el-tag v-if="post.category" type="success" effect="plain" class="category">
            {{ post.category.categoryName }}
          </el-tag>
          <el-tag
            v-for="tag in post.tags"
            :key="tag.tagId"
            class="tag"
            effect="plain"
            size="small"
          >
            {{ tag.tagName }}
          </el-tag>
        </div>
      </div>

      <!-- 文章封面 -->
      <div v-if="post.coverImage" class="post-cover">
        <el-image :src="post.coverImage" fit="cover" />
      </div>

      <!-- 文章摘要 -->
      <div v-if="post.summary" class="post-summary">
        <div class="summary-title">摘要</div>
        <div class="summary-content">{{ post.summary }}</div>
      </div>

      <!-- 文章内容 -->
      <div class="post-content">
        <md-editor
          v-model="post.content"
          :preview="true"
          :previewOnly="true"
          :showCodeRowNumber="true"
          language="zh-CN"
        />
      </div>

      <!-- 文章底部信息 -->
      <div class="post-footer">
        <div class="update-time">最后更新于：{{ post.updatedTime }}</div>
      </div>
    </el-card>
  </page-container>
</template>

<style scoped>
.post-view {
  max-width: 900px;
  margin: 0 auto;
}

.post-header {
  margin-bottom: 24px;
}

.post-title {
  font-size: 2em;
  font-weight: bold;
  margin: 0 0 16px;
  color: var(--el-text-color-primary);
}

.post-meta {
  margin-bottom: 16px;
  color: var(--el-text-color-secondary);
}

.meta-item {
  display: flex;
  align-items: center;
  gap: 4px;
}

.post-tags {
  display: flex;
  gap: 8px;
  flex-wrap: wrap;
}

.category {
  margin-right: 8px;
}

.post-cover {
  margin-bottom: 24px;
  border-radius: 8px;
  overflow: hidden;
}

.post-cover :deep(.el-image) {
  width: 100%;
  max-height: 400px;
}

.post-summary {
  margin-bottom: 24px;
  padding: 16px;
  background-color: var(--el-fill-color-lighter);
  border-radius: 4px;
}

.summary-title {
  font-weight: bold;
  margin-bottom: 8px;
  color: var(--el-text-color-primary);
}

.summary-content {
  color: var(--el-text-color-regular);
  line-height: 1.6;
}

.post-content {
  margin-bottom: 24px;
}

.post-content :deep(.md-editor) {
  border: none;
}

.post-footer {
  padding-top: 16px;
  border-top: 1px solid var(--el-border-color-lighter);
  color: var(--el-text-color-secondary);
  font-size: 14px;
}

.update-time {
  text-align: right;
}
</style>
