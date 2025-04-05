<template>
  <div class="posts-container">
    <div class="posts-header">
      <div class="filter-bar">
        <el-select v-model="filter.category" placeholder="选择分类" clearable>
          <el-option
            v-for="item in categories"
            :key="item.id"
            :label="item.name"
            :value="item.id"
          />
        </el-select>
        <el-select v-model="filter.tag" placeholder="选择标签" clearable>
          <el-option
            v-for="item in tags"
            :key="item.id"
            :label="item.name"
            :value="item.id"
          />
        </el-select>
        <el-select v-model="filter.sort" placeholder="排序方式">
          <el-option label="最新发布" value="newest" />
          <el-option label="最多浏览" value="views" />
          <el-option label="最多点赞" value="likes" />
        </el-select>
      </div>
    </div>

    <div class="posts-list">
      <el-card v-for="post in posts" :key="post.id" class="post-card">
        <div class="post-cover" v-if="post.cover">
          <img :src="post.cover" :alt="post.title" />
        </div>
        <div class="post-content">
          <div class="post-meta">
            <el-tag size="small" type="success">{{ post.category }}</el-tag>
            <span class="post-date">{{ formatDate(post.createTime) }}</span>
          </div>
          <h2 class="post-title">
            <router-link :to="`/post/${post.id}`">{{ post.title }}</router-link>
          </h2>
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
              <span
                ><el-icon><View /></el-icon> {{ post.views }}</span
              >
              <span
                ><el-icon><ChatRound /></el-icon> {{ post.comments }}</span
              >
              <span
                ><el-icon><Star /></el-icon> {{ post.likes }}</span
              >
            </div>
          </div>
        </div>
      </el-card>
    </div>

    <div class="pagination">
      <el-pagination
        v-model:current-page="currentPage"
        v-model:page-size="pageSize"
        :total="total"
        :page-sizes="[10, 20, 30, 50]"
        layout="total, sizes, prev, pager, next, jumper"
        @size-change="handleSizeChange"
        @current-change="handleCurrentChange"
      />
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, watch } from "vue";
import { useRoute } from "vue-router";
import { View, ChatRound, Star } from "@element-plus/icons-vue";
import { formatDate } from "@/utils/format";

const route = useRoute();
const currentPage = ref(1);
const pageSize = ref(10);
const total = ref(0);
const posts = ref([]);
const categories = ref([]);
const tags = ref([]);

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

const fetchPosts = async () => {
  // 这里调用获取文章列表的 API
};

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
  // 获取分类和标签列表
});
</script>

<style scoped>
.posts-container {
  max-width: 1000px;
  margin: 0 auto;
  padding: 20px;
}

.posts-header {
  margin-bottom: 24px;
}

.filter-bar {
  display: flex;
  gap: 16px;
  margin-bottom: 20px;
}

.post-card {
  margin-bottom: 20px;
  transition: transform 0.3s;
}

.post-card:hover {
  transform: translateY(-5px);
}

.post-cover {
  height: 200px;
  overflow: hidden;
  margin: -20px -20px 20px;
}

.post-cover img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.post-meta {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12px;
}

.post-date {
  color: var(--el-text-color-secondary);
  font-size: 14px;
}

.post-title {
  margin: 0 0 16px;
  font-size: 1.5rem;
}

.post-title a {
  color: var(--el-text-color-primary);
  text-decoration: none;
  transition: color 0.3s;
}

.post-title a:hover {
  color: var(--el-color-primary);
}

.post-excerpt {
  color: var(--el-text-color-regular);
  margin-bottom: 16px;
  line-height: 1.6;
}

.post-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.post-tags {
  display: flex;
  gap: 8px;
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
</style>
