<script setup>
import { ref, onMounted } from "vue";
import { useRouter } from "vue-router";
import * as echarts from "echarts";
import { Document, View, ChatRound, Star, User } from "@element-plus/icons-vue";

const router = useRouter();
const postChartRef = ref(null);
const interactionChartRef = ref(null);

// 统计数据
const statistics = ref({
  totalPosts: 0,
  totalViews: 0,
  totalComments: 0,
  totalLikes: 0,
  totalFavorites: 0,
});

// 最近文章列表
const recentPosts = ref([]);

// 初始化文章数据图表
const initPostChart = () => {
  const chart = echarts.init(postChartRef.value);
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
      data: ["1月", "2月", "3月", "4月", "5月", "6月"],
    },
    yAxis: {
      type: "value",
    },
    series: [
      {
        name: "发布量",
        type: "bar",
        data: [10, 15, 8, 12, 9, 11],
      },
      {
        name: "访问量",
        type: "line",
        data: [120, 200, 150, 180, 230, 190],
      },
    ],
  };
  chart.setOption(option);
};

// 初始化互动数据图表
const initInteractionChart = () => {
  const chart = echarts.init(interactionChartRef.value);
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
          { value: 1048, name: "点赞" },
          { value: 735, name: "评论" },
          { value: 580, name: "收藏" },
        ],
      },
    ],
  };
  chart.setOption(option);
};

onMounted(() => {
  initPostChart();
  initInteractionChart();
  // 这里添加获取实际数据的API调用
});
</script>

<template>
  <div class="dashboard">
    <!-- 数据概览卡片 -->
    <div class="stat-cards">
      <el-card class="stat-card">
        <template #header>
          <div class="card-header">
            <el-icon><Document /></el-icon>
            <span>文章数量</span>
          </div>
        </template>
        <div class="stat-value">{{ statistics.totalPosts }}</div>
      </el-card>

      <el-card class="stat-card">
        <template #header>
          <div class="card-header">
            <el-icon><View /></el-icon>
            <span>总访问量</span>
          </div>
        </template>
        <div class="stat-value">{{ statistics.totalViews }}</div>
      </el-card>

      <el-card class="stat-card">
        <template #header>
          <div class="card-header">
            <el-icon><ChatRound /></el-icon>
            <span>评论数</span>
          </div>
        </template>
        <div class="stat-value">{{ statistics.totalComments }}</div>
      </el-card>

      <el-card class="stat-card">
        <template #header>
          <div class="card-header">
            <el-icon><Star /></el-icon>
            <span>获赞数</span>
          </div>
        </template>
        <div class="stat-value">{{ statistics.totalLikes }}</div>
      </el-card>
    </div>

    <!-- 图表区域 -->
    <div class="charts-container">
      <el-card class="chart-card">
        <div ref="postChartRef" class="chart"></div>
      </el-card>

      <el-card class="chart-card">
        <div ref="interactionChartRef" class="chart"></div>
      </el-card>
    </div>

    <!-- 最近文章 -->
    <el-card class="recent-posts">
      <template #header>
        <div class="card-header">
          <span>最近发布</span>
          <el-button text @click="router.push('/posts')">查看全部</el-button>
        </div>
      </template>
      <el-table :data="recentPosts" style="width: 100%">
        <el-table-column prop="title" label="标题" />
        <el-table-column prop="category" label="分类" width="120" />
        <el-table-column prop="views" label="浏览" width="100" />
        <el-table-column prop="createTime" label="发布时间" width="180" />
      </el-table>
    </el-card>
  </div>
</template>

<style scoped>
.dashboard {
  padding: 20px;
}

.stat-cards {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 20px;
  margin-bottom: 24px;
}

.stat-card {
  transition: transform 0.3s;
}

.stat-card:hover {
  transform: translateY(-5px);
}

.card-header {
  display: flex;
  align-items: center;
  gap: 8px;
}

.stat-value {
  font-size: 24px;
  font-weight: bold;
  color: var(--el-color-primary);
  text-align: center;
  padding: 10px 0;
}

.charts-container {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 20px;
  margin-bottom: 24px;
}

.chart {
  height: 400px;
}

.recent-posts {
  margin-bottom: 24px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
</style>
