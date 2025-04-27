<script setup>
import { ref, onMounted, onUnmounted } from "vue";
import { useRouter } from "vue-router";
import * as echarts from "echarts";
import { Document, View, ChatRound, Star, ArrowDown, ArrowUp, DataLine, PieChart } from "@element-plus/icons-vue";
import {
  getUserDashboard,
  getUserMonthlyStats,
  getUserRecentPosts,
  getUserInteractions,
} from "@/api/user/userStat";
import { ElMessage } from "element-plus";
import {USER_POST_LIST_PATH} from "@/constants/routes/user.js"
import AuthorAnalytics from "@/components/user/AuthorAnalytics.vue";

const router = useRouter();
const postChartVisible = ref(true);
const postChartRef = ref(null);
const interactionChartRef = ref(null);
const interactionChartVisible = ref(true);
const analyticsVisible = ref(true);
const loading = ref(true);

// Add chart container refs
const postChartContainer = ref(null);
const interactionChartContainer = ref(null);

// 统计数据
const dashboardStats = ref({
  totalPosts: 0,
  totalViews: 0,
  totalComments: 0,
  totalLikes: 0,
});

// 最近文章列表
const recentPosts = ref([]);

// 获取统计数据
const fetchDashboardStats = async () => {
  try {
    const res = await getUserDashboard();
    if (res.data.status === 200) {
      dashboardStats.value = res.data.data;
    }
  } catch (error) {
    ElMessage.error("获取统计数据失败");
  }
};

// 获取最近文章
const fetchRecentPosts = async () => {
  try {
    const res = await getUserRecentPosts();
    if (res.data.status === 200) {
      // console.log("recent posts:", res.data.data);
      recentPosts.value = res.data.data;
    }
  } catch (error) {
    ElMessage.error("获取最近文章失败");
  }
};

// Initialize post chart with proper event handling
const initPostChart = async () => {
  try {
    const res = await getUserMonthlyStats();
    const monthlyData = res.data.data;

    const chart = echarts.init(postChartRef.value, null, {
      renderer: "canvas",
    });

    const option = {
      title: {
        text: "文章数据统计",
        left: "center",
      },
      tooltip: {
        trigger: "axis",
      },
      legend: {
        bottom: "0%",
        data: ["发布量", "访问量"],
      },
      xAxis: {
        type: "category",
        data: monthlyData.months || ["1月", "2月", "3月", "4月", "5月", "6月"],
      },
      yAxis: {
        type: "value",
      },
      series: [
        {
          name: "发布量",
          type: "bar",
          data: monthlyData.posts || [0, 0, 0, 0, 0, 0],
        },
        {
          name: "访问量",
          type: "line",
          data: monthlyData.views || [0, 0, 0, 0, 0, 0],
        },
      ],
    };
    chart.setOption(option);
    return chart;
  } catch (error) {
    ElMessage.error("获取文章统计数据失败");
  }
};

// Initialize interaction chart with proper event handling
const initInteractionChart = async () => {
  try {
    const res = await getUserInteractions();
    const interactionData = res.data.data;

    const chart = echarts.init(interactionChartRef.value, null, {
      renderer: "canvas",
    });

    const option = {
      title: {
        text: "互动数据分布",
        left: "center",
      },
      tooltip: {
        trigger: "item",
      },
      legend: {
        bottom: "0%",
      },
      series: [
        {
          type: "pie",
          radius: ["40%", "70%"],
          avoidLabelOverlap: false,
          itemStyle: {
            borderRadius: 10,
            borderColor: "#fff",
            borderWidth: 2,
          },
          label: {
            show: false,
            position: "center",
          },
          emphasis: {
            label: {
              show: true,
              fontSize: "20",
              fontWeight: "bold",
            },
          },
          labelLine: {
            show: false,
          },
          data: [
            { value: interactionData.likes || 0, name: "点赞" },
            { value: interactionData.comments || 0, name: "评论" },
            { value: interactionData.favorites || 0, name: "收藏" },
          ],
        },
      ],
    };
    chart.setOption(option);
    return chart;
  } catch (error) {
    ElMessage.error("获取互动统计数据失败");
  }
};

// 监听窗口大小变化，重绘图表
const handleResize = () => {
  if (postChartRef.value && postChartVisible.value) {
    const postChart = echarts.getInstanceByDom(postChartRef.value);
    if (postChart) {
      postChart.resize();
    }
  }

  if (interactionChartRef.value && interactionChartVisible.value) {
    const interactionChart = echarts.getInstanceByDom(interactionChartRef.value);
    if (interactionChart) {
      interactionChart.resize();
    }
  }
};

onMounted(async () => {
  loading.value = true;
  try {
    await Promise.all([
      fetchDashboardStats(),
      initPostChart(),
      initInteractionChart(),
      fetchRecentPosts(),
    ]);
  } catch (error) {
    ElMessage.error("加载数据失败");
  } finally {
    loading.value = false;
  }

  window.addEventListener("resize", handleResize);
});

onUnmounted(() => {
  window.removeEventListener("resize", handleResize);
});
</script>

<template>
  <div class="dashboard" v-loading="loading" element-loading-text="加载中...">
    <!-- 数据概览卡片 -->
    <div class="stat-cards">
      <el-card class="stat-card primary-card">
        <template #header>
          <div class="card-header">
            <el-icon><Document /></el-icon>
            <span>文章数量</span>
          </div>
        </template>
        <div class="stat-value">{{ dashboardStats.totalPosts }}</div>
      </el-card>

      <el-card class="stat-card success-card">
        <template #header>
          <div class="card-header">
            <el-icon><View /></el-icon>
            <span>总访问量</span>
          </div>
        </template>
        <div class="stat-value">{{ dashboardStats.totalViews }}</div>
      </el-card>

      <el-card class="stat-card warning-card">
        <template #header>
          <div class="card-header">
            <el-icon><ChatRound /></el-icon>
            <span>评论数</span>
          </div>
        </template>
        <div class="stat-value">{{ dashboardStats.totalComments }}</div>
      </el-card>

      <el-card class="stat-card danger-card">
        <template #header>
          <div class="card-header">
            <el-icon><Star /></el-icon>
            <span>获赞数</span>
          </div>
        </template>
        <div class="stat-value">{{ dashboardStats.totalLikes }}</div>
      </el-card>
    </div>

  <!-- 图表区域 -->
  <div class="charts-container">
    <el-card class="chart-card" shadow="hover">
      <template #header>
        <div class="card-header">
          <div class="header-left">
            <el-icon><DataLine /></el-icon>
            <span>文章数据统计</span>
          </div>
          <el-button type="primary" link @click="postChartVisible = !postChartVisible">
            {{ postChartVisible ? '收起' : '展开' }}
            <el-icon class="el-icon--right">
              <ArrowDown v-if="!postChartVisible" />
              <ArrowUp v-else />
            </el-icon>
          </el-button>
        </div>
      </template>
      <el-collapse-transition>
        <div v-show="postChartVisible" ref="postChartContainer" class="chart-wrapper">
          <div ref="postChartRef" class="chart"></div>
        </div>
      </el-collapse-transition>
    </el-card>

    <el-card class="chart-card" shadow="hover">
      <template #header>
        <div class="card-header">
          <div class="header-left">
            <el-icon><PieChart /></el-icon>
            <span>互动数据分布</span>
          </div>
          <el-button type="primary" link @click="interactionChartVisible = !interactionChartVisible">
            {{ interactionChartVisible ? '收起' : '展开' }}
            <el-icon class="el-icon--right">
              <ArrowDown v-if="!interactionChartVisible" />
              <ArrowUp v-else />
            </el-icon>
          </el-button>
        </div>
      </template>
      <el-collapse-transition>
        <div v-show="interactionChartVisible" ref="interactionChartContainer" class="chart-wrapper">
          <div ref="interactionChartRef" class="chart"></div>
        </div>
      </el-collapse-transition>
    </el-card>
  </div>

  <!-- 作者分析组件 -->
  <el-card class="analytics-card" shadow="hover">
    <template #header>
      <div class="card-header">
        <div class="header-left">
          <el-icon><User /></el-icon>
          <span>作者分析</span>
        </div>
        <el-button type="primary" link @click="analyticsVisible = !analyticsVisible">
          {{ analyticsVisible ? '收起' : '展开' }}
          <el-icon class="el-icon--right">
            <ArrowDown v-if="!analyticsVisible" />
            <ArrowUp v-else />
          </el-icon>
        </el-button>
      </div>
    </template>
    <el-collapse-transition>
      <div v-show="analyticsVisible">
        <AuthorAnalytics />
      </div>
    </el-collapse-transition>
  </el-card>

    <!-- 最近文章 -->
    <el-card class="recent-posts" shadow="hover">
      <template #header>
        <div class="card-header">
          <div class="header-left">
            <el-icon class="header-icon"><Document /></el-icon>
            <span>最近发布</span>
          </div>
          <el-button type="primary" link @click="router.push(USER_POST_LIST_PATH)">
            查看全部
            <el-icon class="el-icon--right"><arrow-right /></el-icon>
          </el-button>
        </div>
      </template>

      <el-table :data="recentPosts" style="width: 100%">
        <el-table-column prop="post.title" label="标题">
          <template #default="{ row }">
            <el-link
              type="primary"
              :underline="false"
              @click="router.push(`/post/${row.post.postId}`)"
            >
              {{ row.post.title }}
            </el-link>
          </template>
        </el-table-column>
        <el-table-column label="分类" width="120">
          <template #default="{ row }">
            <el-tag size="small" :type="row.category?.name ? 'info' : 'warning'">
              {{ row.category?.name || "未分类" }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="post.views" label="浏览" width="100" />
        <el-table-column prop="post.createdAt" label="发布时间" width="180" />
      </el-table>
    </el-card>
  </div>
</template>

<style scoped>
.dashboard {
  padding: 20px;
  min-height: 400px;
  background-color: var(--el-bg-color-page);
}

.stat-cards {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 20px;
  margin-bottom: 24px;
}

.stat-card {
  transition: transform 0.3s;
  border-radius: 8px;
  overflow: hidden;
}

.stat-card:hover {
  transform: translateY(-5px);
}

.primary-card :deep(.el-card__header) {
  background-color: var(--el-color-primary-light-8);
}

.success-card :deep(.el-card__header) {
  background-color: var(--el-color-success-light-8);
}

.warning-card :deep(.el-card__header) {
  background-color: var(--el-color-warning-light-8);
}

.danger-card :deep(.el-card__header) {
  background-color: var(--el-color-danger-light-8);
}

.card-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 12px;
}

.header-left {
  display: flex;
  align-items: center;
  gap: 8px;
}

.analytics-card {
  margin-bottom: 24px;
}

/* 添加过渡动画 */
.el-collapse-transition {
  transition: all 0.3s ease;
}

.header-icon {
  font-size: 18px;
}

.stat-value {
  font-size: 28px;
  font-weight: bold;
  color: var(--el-text-color-primary);
  text-align: center;
  padding: 16px 0;
}

.charts-container {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 24px;
  margin-bottom: 24px;
}

.chart-card {
  border-radius: 8px;
  transition: all 0.3s ease;
}

.chart-card:hover {
  box-shadow: 0 8px 16px rgba(0, 0, 0, 0.1);
}

.chart-wrapper {
  position: relative;
  width: 100%;
  height: 100%;
}

.chart {
  height: 400px;
  padding: 16px;
}

.recent-posts {
  margin-bottom: 24px;
}

.header-left {
  display: flex;
  align-items: center;
  gap: 8px;
}

:deep(.el-table__row) {
  cursor: pointer;
}

:deep(.el-table__row:hover) {
  background-color: var(--el-color-primary-light-9);
}

/* 添加作者分析组件的间距 */
:deep(.author-analytics) {
  margin-bottom: 24px;
}
</style>
