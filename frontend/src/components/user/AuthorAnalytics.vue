<script setup>
import { ref, onMounted, computed } from "vue";
import { useRouter } from "vue-router";
import { Reading, Timer, EditPen, DataAnalysis, User } from "@element-plus/icons-vue";
import { getAuthorAnalytics } from "@/api/post/authorAnalytics";
import { BLOG_USER_DETAIL_PATH } from "@/constants/routes/blog";

// 作者分析数据
const analytics = ref({
  userBehavior: {},
  writingSuggestions: [],
});

// 加载状态
const loading = ref(false);

// 获取作者分析数据
const fetchAuthorAnalytics = async () => {
  loading.value = true;
  try {
    const res = await getAuthorAnalytics();
    if (res.data?.status === 200) {
      analytics.value = res.data.data;
    }
  } catch (error) {
    ElMessage.error("获取作者分析数据失败");
  } finally {
    loading.value = false;
  }
};

// 组件挂载时获取数据
onMounted(() => {
  fetchAuthorAnalytics();
});

const getTimelineItemType = (index) => {
  const types = ["primary", "success", "warning", "danger"];
  return types[index % types.length];
};

const getTimelineItemIcon = (index) => {
  const icons = [Reading, Timer, EditPen, DataAnalysis];
  return icons[index % icons.length];
};

// 获取活动类型标签
const getActivityLabel = (key) => {
  const labels = {
    COMMENT: "评论",
    FAVORITE: "收藏",
    LIKE_COMMENT: "评论点赞",
    REPLY: "回复",
    LIKE: "文章点赞",
  };
  return labels[key] || key;
};

// 计算活动总数
const getTotalActivity = () => {
  const distribution = analytics.value?.userBehavior?.activityDistribution || {};
  return Object.values(distribution).reduce((sum, value) => sum + value, 0);
};

const getActivityPercentage = (value) => {
  const total = getTotalActivity();
  if (total === 0) return 0;
  return ((value / total) * 100).toFixed(1);
};

// 获取活动颜色
const getActivityColor = (key) => {
  const colors = {
    COMMENT: "#409EFF",
    FAVORITE: "#67C23A",
    LIKE_COMMENT: "#E6A23C",
    REPLY: "#F56C6C",
    LIKE: "#909399",
  };
  return colors[key] || "#409EFF";
};

// 获取读者标签类型
const getEngagerTagType = (index) => {
  const types = ["", "success", "warning"];
  return types[index] || "info";
};

/**
 * 路由点击跳转
 */
const router = useRouter();

// 跳转到用户详情页
const goToUserDetail = (userId) => {
  router.push(BLOG_USER_DETAIL_PATH.replace(":id", userId));
};


// 添加计算属性判断是否有文章
const hasPublishedPosts = computed(() => {
  return analytics.value?.writingSuggestions?.[0] !== "您还没有发表任何文章，开始创作第一篇文章吧!";
});
</script>

<template>
  <div class="author-analytics" v-loading="loading">
    <!-- 用户行为分析卡片 -->
    <el-card class="behavior-card" shadow="hover" v-if="analytics.userBehavior && hasPublishedPosts">
      <template #header>
        <div class="card-header">
          <div class="header-left">
            <el-icon class="header-icon"><User /></el-icon>
            <span>读者互动分析</span>
          </div>
        </div>
      </template>
      <div class="behavior-content">
        <el-row :gutter="24">
          <el-col :span="12">
            <h4 class="behavior-title">活动分布</h4>
            <div class="activity-list">
              <div
                v-for="(value, key) in analytics.userBehavior.activityDistribution"
                :key="key"
                class="activity-item"
              >
                <span class="activity-label">{{ getActivityLabel(key) }}</span>
                <el-progress
                  :percentage="Number(getActivityPercentage(value))"
                  :color="getActivityColor(key)"
                />
              </div>
            </div>
          </el-col>
          <el-col :span="12">
            <h4 class="behavior-title">互动数据</h4>
            <div class="interaction-info">
              <div class="info-item">
                <span class="info-label">平均互动时间</span>
                <span class="info-value"
                  >{{ analytics.userBehavior.avgInteractionTime }}分钟</span
                >
              </div>
              <div class="info-item">
                <span class="info-label">活跃读者</span>
                <div class="top-engagers">
                  <el-tag
                    v-for="(name, index) in analytics.userBehavior.topEngagerNames"
                    :key="index"
                    :type="getEngagerTagType(index)"
                    size="small"
                    class="engager-tag"
                    @click="goToUserDetail(analytics.userBehavior.topEngagers[index])"
                    style="cursor: pointer"
                  >
                    {{ name }}
                  </el-tag>
                </div>
              </div>
            </div>
          </el-col>
        </el-row>
      </div>
    </el-card>

     <!-- 添加无文章提示 -->
     <el-empty
      v-if="!hasPublishedPosts && !loading"
      description="暂无数据分析，发表文章后即可查看"
    >
      <template #image>
        <el-icon :size="60" style="margin-bottom: 15px"><DataAnalysis /></el-icon>
      </template>
    </el-empty>

    <!-- 写作建议卡片 -->
    <el-card
      class="analytics-card"
      shadow="hover"
      v-if="analytics.writingSuggestions?.length"
    >
      <template #header>
        <div class="card-header">
          <div class="header-left">
            <el-icon class="header-icon"><Reading /></el-icon>
            <span>写作建议</span>
          </div>
        </div>
      </template>
      <div class="analytics-content">
        <el-row :gutter="20">
          <el-col :span="24">
            <div class="suggestions-list">
              <el-timeline>
                <el-timeline-item
                  v-for="(suggestion, index) in analytics.writingSuggestions"
                  :key="index"
                  :type="getTimelineItemType(index)"
                  :icon="getTimelineItemIcon(index)"
                  :hollow="true"
                  :timestamp="`建议 ${index + 1}`"
                >
                  <el-card class="suggestion-card" :class="getTimelineItemType(index)">
                    <p class="suggestion-text">{{ suggestion }}</p>
                  </el-card>
                </el-timeline-item>
              </el-timeline>
            </div>
          </el-col>
        </el-row>
      </div>
    </el-card>
  </div>
</template>

<style scoped>
/* 复制原有的相关样式 */
.author-analytics {
  margin-bottom: 24px;
}

/* 行为分析卡片样式 */
.behavior-card {
  margin-bottom: 24px;
  border-radius: 12px;
}

.behavior-content {
  padding: 20px;
}

.behavior-title {
  margin: 0 0 16px;
  font-size: 16px;
  font-weight: 600;
  color: var(--el-text-color-primary);
}

.activity-list {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.activity-item {
  .activity-label {
    display: block;
    margin-bottom: 8px;
    color: var(--el-text-color-regular);
    font-size: 14px;
  }
}

.interaction-info {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.info-item {
  .info-label {
    display: block;
    margin-bottom: 8px;
    color: var(--el-text-color-regular);
    font-size: 14px;
  }

  .info-value {
    font-size: 24px;
    font-weight: 600;
    color: var(--el-color-primary);
  }
}

.top-engagers {
  display: flex;
  gap: 8px;
  flex-wrap: wrap;
}

.engager-tag {
  margin: 4px;
  transition: all 0.3s ease;

  &:hover {
    transform: translateY(-2px);
  }
}

/* 写作建议卡片样式 */
.analytics-card {
  margin-bottom: 24px;
  border-radius: 12px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.05);
}

.analytics-content {
  padding: 20px 16px;
}

.suggestions-list {
  .suggestion-card {
    margin-bottom: 8px;
    border-radius: 8px;
    transition: all 0.3s ease;

    &:hover {
      transform: translateX(5px);
    }

    &.primary {
      border-left: 4px solid var(--el-color-primary);
    }

    &.success {
      border-left: 4px solid var(--el-color-success);
    }

    &.warning {
      border-left: 4px solid var(--el-color-warning);
    }

    &.danger {
      border-left: 4px solid var(--el-color-danger);
    }
  }

  .suggestion-text {
    margin: 0;
    line-height: 1.8;
    color: var(--el-text-color-primary);
    white-space: pre-line;
    font-size: 14px;
  }
}

:deep(.el-timeline) {
  padding-left: 16px;
}

:deep(.el-timeline-item__node) {
  width: 16px;
  height: 16px;
  left: -1px;
}

:deep(.el-timeline-item__tail) {
  left: 7px;
}

:deep(.el-timeline-item__timestamp) {
  font-size: 13px;
  color: var(--el-text-color-secondary);
  margin-bottom: 8px;
}

:deep(.el-timeline-item__content) {
  margin-left: 24px;
}

/* 适配暗色主题 */
:deep(.dark) {
  .suggestion-card {
    background-color: var(--el-bg-color);
  }
}
</style>
