<script setup>
import { computed } from "vue";
import VChart, { THEME_KEY } from "vue-echarts";
import { use } from "echarts/core";
import { CanvasRenderer } from "echarts/renderers";
import { PieChart } from "echarts/charts";
import { GridComponent, TooltipComponent, LegendComponent } from "echarts/components";

// 注册必要的组件
use([CanvasRenderer, PieChart, GridComponent, TooltipComponent, LegendComponent]);

const props = defineProps({
  posts: {
    type: Array,
    required: true,
  },
});

const categoryStats = computed(() => {
  const stats = {};
  props.posts.forEach((post) => {
    if (post.category) {
      stats[post.category] = (stats[post.category] || 0) + 1;
    }
  });
  return Object.entries(stats).map(([name, value]) => ({ name, value }));
});

const pieOption = computed(() => ({
  tooltip: {
    trigger: "item",
    formatter: "{b}: {c} ({d}%)",
  },
  legend: {
    orient: "vertical",
    left: "left",
  },
  series: [
    {
      type: "pie",
      radius: "50%",
      data: categoryStats.value.map((item) => ({
        name: item.name,
        value: item.value,
      })),
      emphasis: {
        itemStyle: {
          shadowBlur: 10,
          shadowOffsetX: 0,
          shadowColor: "rgba(0, 0, 0, 0.5)",
        },
      },
    },
  ],
}));
</script>

<template>
  <el-card v-if="categoryStats.length > 0" class="category-stats-card" shadow="never">
    <template #header>
      <div class="card-header">
        <h3>文章分类统计</h3>
      </div>
    </template>
    <div class="chart-container">
      <v-chart class="chart" :option="pieOption" :autoresize="true" />
    </div>
  </el-card>
</template>

<style scoped>
.chart-container {
  height: 300px;
}

.chart {
  width: 100%;
  height: 100%;
}
</style>
