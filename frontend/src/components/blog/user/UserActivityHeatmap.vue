<script setup>
import { computed } from "vue";
import VChart, { THEME_KEY } from "vue-echarts";
import { use } from "echarts/core";
import { CanvasRenderer } from "echarts/renderers";
import { HeatmapChart } from "echarts/charts";
import {
  GridComponent,
  TooltipComponent,
  VisualMapComponent,
  CalendarComponent,
} from "echarts/components";

// 注册必要的组件
use([
  CanvasRenderer,
  HeatmapChart,
  GridComponent,
  TooltipComponent,
  VisualMapComponent,
  CalendarComponent,
]);

const props = defineProps({
  posts: {
    type: Array,
    required: true,
  },
});

const activityData = computed(() => {
  const data = [];
  const today = new Date();
  const oneYearAgo = new Date();
  oneYearAgo.setFullYear(today.getFullYear() - 1);

  props.posts.forEach((post) => {
    const date = new Date(post.createdAt);
    if (date >= oneYearAgo) {
      data.push({
        date: post.createdAt.split("T")[0],
        count: 1,
      });
    }
  });
  return data;
});

const heatmapOption = computed(() => {
  const currentYear = new Date().getFullYear();
  return {
    tooltip: {
      position: "top",
      formatter: function (p) {
        return `${p.data[0]}: ${p.data[1]} 篇文章`;
      },
    },
    visualMap: {
      min: 0,
      max: 10,
      calculable: true,
      orient: "horizontal",
      left: "center",
      top: "top",
    },
    calendar: {
      top: 60,
      left: 30,
      right: 30,
      cellSize: ["auto", 15],
      range: String(currentYear),
      itemStyle: {
        borderWidth: 0.5,
      },
      yearLabel: { show: false },
    },
    series: {
      type: "heatmap",
      coordinateSystem: "calendar",
      data: activityData.value.map((item) => [item.date, item.count]),
    },
  };
});
</script>

<template>
  <el-card class="activity-card" shadow="never">
    <template #header>
      <div class="card-header">
        <h3>活跃度</h3>
      </div>
    </template>
    <div class="heatmap-container">
      <el-empty v-if="activityData.length === 0" description="暂无活动数据" />
      <v-chart v-else class="chart" :option="heatmapOption" :autoresize="true" />
    </div>
  </el-card>
</template>

<style scoped>
.heatmap-container {
  height: 200px;
}

.chart {
  width: 100%;
  height: 100%;
}
</style>
