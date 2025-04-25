<script setup>
import { ref, onMounted, onUnmounted } from "vue";
import * as echarts from "echarts";
import { ElMessage } from "element-plus";
import { Document, View, ChatRound, Star, ArrowRight } from "@element-plus/icons-vue";

import {
  getAdminDashboard,
  getAdminMonthlyStats,
  getAdminRecentPosts,
} from "@/api/user/userStat";

import { useRouter } from "vue-router";
import { BLOG_POST_DETAIL_PATH } from "@/constants/routes-constants.js";

const dashboardStats = ref({
  totalPosts: 0,
  totalViews: 0,
  totalComments: 0,
  totalLikes: 0,
});
const monthlyStats = ref({});
const recentPosts = ref([]);
const monthlyChartRef = ref(null);
let monthlyChart = null;
const loading = ref(true);

const getDashboardStats = async () => {
  try {
    const response = await getAdminDashboard();
    if (response.data.status === 200) {
      dashboardStats.value = {
        totalPosts: response.data.data?.totalPosts || 0,
        totalViews: response.data.data?.totalViews || 0,
        totalComments: response.data.data?.totalComments || 0,
        totalLikes: response.data.data?.totalLikes || 0,
      };
    }
  } catch (error) {
    console.error("获取仪表盘统计数据失败:", error);
    ElMessage.error("获取仪表盘统计数据失败");
  }
};

const getMonthlyStats = async () => {
  try {
    const response = await getAdminMonthlyStats();
    if (response.data.status === 200) {
      monthlyStats.value = response.data.data;
      renderMonthlyChart();
    }
  } catch (error) {
    console.error("获取月度统计数据失败:", error);
    ElMessage.error("获取最近文章失败");
  }
};

const getRecentPosts = async () => {
  try {
    const response = await getAdminRecentPosts();
    if (response.data.status === 200) {
      recentPosts.value = response.data.data;
    }
  } catch (error) {
    console.error("获取最近文章失败:", error);
  }
};

const renderMonthlyChart = () => {
  if (!monthlyStats.value.months || !monthlyChartRef.value) return;
  if (monthlyChart) {
    monthlyChart.dispose();
  }
  monthlyChart = echarts.init(monthlyChartRef.value);

  const option = {
    title: {
      text: "月度数据统计",
      left: "center",
    },
    tooltip: {
      trigger: "axis",
      axisPointer: {
        type: "shadow",
      },
    },
    legend: {
      bottom: "0%",
      data: ["文章数", "浏览量"],
    },
    xAxis: {
      type: "category",
      data: monthlyStats.value.months || [],
    },
    yAxis: [
      {
        type: "value",
        name: "文章数",
        position: "left",
      },
      {
        type: "value",
        name: "浏览量",
        position: "right",
      },
    ],
    series: [
      {
        name: "文章数",
        type: "bar",
        data: monthlyStats.value.posts || [],
      },
      {
        name: "浏览量",
        type: "line",
        yAxisIndex: 1,
        data: monthlyStats.value.views || [],
      },
    ],
  };

  monthlyChart.setOption(option);
};

onMounted(async () => {
  loading.value = true;
  try {
    await Promise.all([getDashboardStats(), getMonthlyStats(), getRecentPosts()]);
  } catch (error) {
    ElMessage.error("加载数据失败");
  } finally {
    loading.value = false;
  }

  window.addEventListener("resize", () => {
    if (monthlyChart) {
      monthlyChart.resize();
    }
  });
});

onUnmounted(() => {
  if (monthlyChart) {
    monthlyChart.dispose();
  }
  window.removeEventListener("resize", () => {
    if (monthlyChart) {
      monthlyChart.resize();
    }
  });
});

// 点击文章跳转
const router = useRouter();
const goToPostDetail = (postId) => {
  router.push(BLOG_POST_DETAIL_PATH.replace(":id", postId));
};
</script>

<template>
  <div class="statistics-container" v-loading="loading" element-loading-text="加载中...">
    <el-row :gutter="20">
      <el-col
        :span="6"
        v-for="(item, index) in [
          {
            icon: Document,
            label: '总文章数',
            value: dashboardStats.totalPosts,
            type: 'primary',
          },
          {
            icon: View,
            label: '总浏览量',
            value: dashboardStats.totalViews,
            type: 'success',
          },
          {
            icon: ChatRound,
            label: '总评论数',
            value: dashboardStats.totalComments,
            type: 'warning',
          },
          {
            icon: Star,
            label: '总点赞数',
            value: dashboardStats.totalLikes,
            type: 'danger',
          },
        ]"
        :key="index"
      >
        <el-card :class="['stat-card', `${item.type}-card`]">
          <template #header>
            <div class="card-header">
              <el-icon><component :is="item.icon" /></el-icon>
              <span>{{ item.label }}</span>
            </div>
          </template>
          <div class="stat-value">{{ item.value || 0 }}</div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 图表区域 -->
    <el-row :gutter="20" class="chart-row">
      <el-col :span="24">
        <el-card class="chart-card" shadow="hover">
          <template #header>
            <div class="card-header">
              <span>月度统计</span>
            </div>
          </template>
          <div ref="monthlyChartRef" class="chart"></div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 最近文章表格 -->
    <el-row :gutter="20" class="chart-row">
      <el-col :span="24">
        <el-card class="recent-posts" shadow="hover">
          <template #header>
            <div class="card-header">
              <span>最近文章</span>
            </div>
          </template>
          <el-table :data="recentPosts" style="width: 100%">
            <el-table-column prop="post.title" label="标题">
              <template #default="{ row }">
                <el-link
                  type="primary"
                  :underline="false"
                  @click="goToPostDetail(row.post.postId)"
                >
                  {{ row.post.title }}
                </el-link>
              </template>
            </el-table-column>
            <el-table-column prop="post.views" label="浏览量" width="120" />
            <el-table-column prop="post.likes" label="点赞数" width="120" />
            <el-table-column prop="post.createdAt" label="创建时间" width="180">
              <template #default="scope">
                {{ scope.row.post.createdAt }}
              </template>
            </el-table-column>
          </el-table>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<style scoped>
.statistics-container {
  padding: 20px;
  min-height: 400px;
  background-color: var(--el-bg-color-page);
}

.stat-card {
  transition: transform 0.3s;
  border-radius: 8px;
  overflow: hidden;
  margin-bottom: 20px;
}

.stat-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 8px 16px rgba(0, 0, 0, 0.1);
}

.card-header {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 12px;
}

.stat-value {
  font-size: 28px;
  font-weight: bold;
  text-align: center;
  color: var(--el-text-color-primary);
  padding: 16px 0;
}

/* 统计卡片的不同主题色 */
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

.chart-row {
  margin-top: 20px;
}

.chart {
  height: 400px;
  padding: 16px;
}

.chart-card {
  border-radius: 8px;
  transition: all 0.3s ease;
}

.chart-card:hover {
  transform: translateY(-3px);
  box-shadow: 0 8px 16px rgba(0, 0, 0, 0.1);
}

.recent-posts {
  border-radius: 8px;
}

:deep(.el-table) {
  border-radius: 8px;
  overflow: hidden;
}

:deep(.el-table__row) {
  transition: all 0.3s ease;
}

:deep(.el-table__row:hover) {
  background-color: var(--el-color-primary-light-9) !important;
}
</style>
