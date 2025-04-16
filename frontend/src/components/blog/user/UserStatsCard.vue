<script setup>
import { computed } from "vue";

const props = defineProps({
  posts: {
    type: Array,
    required: true,
  },
});

const stats = computed(() => ({
  totalPosts: props.posts.length,
  totalViews: props.posts.reduce((sum, post) => sum + (post.views || 0), 0),
  totalLikes: props.posts.reduce((sum, post) => sum + (post.likes || 0), 0),
  totalComments: props.posts.reduce((sum, post) => sum + (post.comments || 0), 0),
}));
</script>

<template>
  <div class="user-stats">
    <el-row :gutter="20">
      <el-col :span="6">
        <el-statistic title="文章数" :value="stats.totalPosts" />
      </el-col>
      <el-col :span="6">
        <el-statistic title="总浏览量" :value="stats.totalViews" />
      </el-col>
      <el-col :span="6">
        <el-statistic title="总点赞数" :value="stats.totalLikes" />
      </el-col>
      <el-col :span="6">
        <el-statistic title="总评论数" :value="stats.totalComments" />
      </el-col>
    </el-row>
  </div>
</template>
