<script setup>
import { ref, onMounted } from "vue";
import { useRouter } from "vue-router";
import { View, Star, ArrowRight } from "@element-plus/icons-vue";
import { formatDate, formatDateTime } from "@/utils/date";

const router = useRouter();

// 模拟数据
const featuredPosts = ref([]);
const latestPosts = ref([]);
const categories = ref([]);
const tags = ref([]);

// 在实际应用中，这里需要调用后端 API 获取数据
onMounted(() => {
  // 获取数据的逻辑
});
</script>

<template>
  <div class="home">
    <section class="featured-posts">
      <h2 class="section-title">精选文章</h2>
      <el-row :gutter="20">
        <el-col v-for="post in featuredPosts" :key="post.id" :span="8">
          <el-card class="post-card" :body-style="{ padding: '0px' }">
            <img :src="post.cover" class="post-image" />
            <div class="post-info">
              <div class="post-meta">
                <el-tag size="small">{{ post.category }}</el-tag>
                <span class="post-date">{{ formatDate(post.createTime) }}</span>
              </div>
              <h3 class="post-title">{{ post.title }}</h3>
              <p class="post-excerpt">{{ post.excerpt }}</p>
              <div class="post-footer">
                <el-button text @click="router.push(`/post/${post.id}`)">
                  阅读更多
                </el-button>
                <div class="post-stats">
                  <span
                    ><el-icon><View /></el-icon> {{ post.views }}</span
                  >
                  <span
                    ><el-icon><Star /></el-icon> {{ post.likes }}</span
                  >
                </div>
              </div>
            </div>
          </el-card>
        </el-col>
      </el-row>
    </section>

    <section class="latest-posts">
      <div class="section-header">
        <h2 class="section-title">最新文章</h2>
        <router-link to="/posts" class="view-all">
          查看全部 <el-icon><ArrowRight /></el-icon>
        </router-link>
      </div>
      <el-timeline>
        <el-timeline-item
          v-for="post in latestPosts"
          :key="post.id"
          :timestamp="formatDate(post.createTime)"
          placement="top"
        >
          <el-card class="timeline-card">
            <div class="post-brief">
              <h3 class="post-title">
                <router-link :to="`/post/${post.id}`">{{ post.title }}</router-link>
              </h3>
              <p class="post-excerpt">{{ post.excerpt }}</p>
              <div class="post-tags">
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
          </el-card>
        </el-timeline-item>
      </el-timeline>
    </section>

    <aside class="sidebar">
      <el-card class="category-card">
        <template #header>
          <div class="card-header">
            <span>分类</span>
            <router-link to="/categories">更多</router-link>
          </div>
        </template>
        <div class="category-list">
          <router-link
            v-for="category in categories"
            :key="category.id"
            :to="`/posts?category=${category.id}`"
            class="category-item"
          >
            <span>{{ category.name }}</span>
            <span class="count">{{ category.count }}</span>
          </router-link>
        </div>
      </el-card>

      <el-card class="tag-card">
        <template #header>
          <div class="card-header">
            <span>标签云</span>
            <router-link to="/tags">更多</router-link>
          </div>
        </template>
        <div class="tag-cloud">
          <router-link
            v-for="tag in tags"
            :key="tag.id"
            :to="`/posts?tag=${tag.id}`"
            class="tag-item"
            :style="{ fontSize: `${tag.size}px` }"
          >
            {{ tag.name }}
          </router-link>
        </div>
      </el-card>
    </aside>
  </div>
</template>

<style scoped>
.home {
  display: grid;
  grid-template-columns: 1fr 300px;
  gap: 2rem;
}

.section-title {
  font-size: 1.5rem;
  margin-bottom: 1.5rem;
  color: var(--el-text-color-primary);
}

.featured-posts {
  margin-bottom: 3rem;
}

.post-card {
  transition: transform 0.3s;
  margin-bottom: 1rem;
}

.post-card:hover {
  transform: translateY(-5px);
}

.post-image {
  width: 100%;
  height: 200px;
  object-fit: cover;
}

.post-info {
  padding: 1rem;
}

.post-meta {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 0.5rem;
}

.post-date {
  color: var(--el-text-color-secondary);
  font-size: 0.9rem;
}

.post-title {
  margin: 0.5rem 0;
  font-size: 1.2rem;
}

.post-excerpt {
  color: var(--el-text-color-regular);
  margin-bottom: 1rem;
  line-height: 1.5;
}

.post-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.post-stats {
  display: flex;
  gap: 1rem;
  color: var(--el-text-color-secondary);
}

.timeline-card {
  margin-bottom: 1rem;
}

.post-tags {
  margin-top: 0.5rem;
}

.tag {
  margin-right: 0.5rem;
}

.sidebar {
  position: sticky;
  top: 100px;
}

.category-card,
.tag-card {
  margin-bottom: 1rem;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.category-list {
  display: flex;
  flex-direction: column;
  gap: 0.5rem;
}

.category-item {
  display: flex;
  justify-content: space-between;
  padding: 0.5rem;
  text-decoration: none;
  color: var(--el-text-color-regular);
  border-radius: 4px;
  transition: background-color 0.3s;
}

.category-item:hover {
  background-color: var(--el-bg-color-page);
  color: var(--el-color-primary);
}

.tag-cloud {
  display: flex;
  flex-wrap: wrap;
  gap: 0.5rem;
}

.tag-item {
  color: var(--el-text-color-regular);
  text-decoration: none;
  transition: color 0.3s;
}

.tag-item:hover {
  color: var(--el-color-primary);
}

.view-all {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  color: var(--el-color-primary);
  text-decoration: none;
}
</style>
