<script setup>
import { User, Clock, View } from "@element-plus/icons-vue";

defineProps({
  title: String,
  author: String,
  createdTime: String,
  viewCount: Number,
  category: Object,
  tags: Array,
});
</script>

<template>
  <div class="post-header">
    <h1 class="post-title">{{ title }}</h1>
    <div class="post-meta">
      <el-space wrap>
        <div class="meta-item">
          <el-icon><User /></el-icon>
          <span>{{ author }}</span>
        </div>
        <div class="meta-item">
          <el-icon><Clock /></el-icon>
          <span>{{ createdTime }}</span>
        </div>
        <div class="meta-item">
          <el-icon><View /></el-icon>
          <span>{{ viewCount }} 次阅读</span>
        </div>
      </el-space>
    </div>

    <div class="post-tags">
      <el-tag v-if="category" type="success" effect="plain" class="category">
        {{ category.categoryName }}
      </el-tag>
      <el-tag
        v-for="tag in tags"
        :key="tag.tagId"
        class="tag"
        effect="plain"
        size="small"
      >
        {{ tag.tagName }}
      </el-tag>
    </div>
  </div>
</template>

<style scoped>
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
</style>
