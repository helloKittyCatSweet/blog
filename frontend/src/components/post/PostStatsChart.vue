<script setup>
import { ref, onMounted, onUnmounted, watch, defineExpose, nextTick } from "vue";
import * as echarts from "echarts";

const props = defineProps({
  posts: {
    type: Array,
    required: true,
  },
});

const chartRef = ref(null);
let chart = null;

// 处理统计数据
const processChartData = () => {
  const categoryStats = {};
  const tagStats = {};

  props.posts.forEach((item) => {
    const categoryName = item.category?.name || "无分类";
    categoryStats[categoryName] = (categoryStats[categoryName] || 0) + 1;

    item.tags.forEach((tag) => {
      tagStats[tag.name] = (tagStats[tag.name] || 0) + 1;
    });
  });

  return {
    categoryData: Object.entries(categoryStats).map(([name, value]) => ({ name, value })),
    tagData: Object.entries(tagStats).map(([name, value]) => ({ name, value })),
  };
};

// 初始化图表
const initChart = async () => {
  if (!chartRef.value) return;

  // 等待下一帧确保 DOM 已更新
  await nextTick();

  // 确保容器有尺寸
  const container = chartRef.value;
  if (!container.offsetHeight) {
    container.style.height = "400px";
  }

  // 等待一小段时间确保容器尺寸已计算
  await new Promise((resolve) => setTimeout(resolve, 100));

  // 销毁旧的图表
  if (chart) {
    chart.dispose();
  }

  // 延迟初始化以确保容器尺寸已计算
  setTimeout(() => {
    chart = echarts.init(chartRef.value);
    const { categoryData, tagData } = processChartData();

    const option = {
      title: [
        {
          text: "文章趋势统计",
          left: "25%",
          top: "5%",
          textAlign: "center",
        },
        {
          text: "分类统计",
          left: "75%",
          top: "5%",
          textAlign: "center",
        },
        {
          text: "标签统计",
          left: "75%",
          top: "50%",
          textAlign: "center",
        },
      ],
      tooltip: {
        trigger: "axis",
      },
      grid: {
        left: "5%",
        right: "50%",
        top: "20%",
        bottom: "10%",
        containLabel: true,
      },
      legend: {
        top: "10%",
        left: "5%",
        data: ["浏览量", "点赞数", "收藏数"],
      },
      xAxis: {
        type: "category",
        data: props.posts.map((item) => item.updatedAt),
      },
      yAxis: {
        type: "value",
      },
      series: [
        {
          name: "浏览量",
          type: "line",
          data: props.posts.map((item) => item.views),
          smooth: true,
          itemStyle: {
            color: "#409EFF",
          },
        },
        {
          name: "点赞数",
          type: "line",
          data: props.posts.map((item) => item.likes),
          smooth: true,
          itemStyle: {
            color: "#67C23A",
          },
        },
        {
          name: "收藏数",
          type: "line",
          data: props.posts.map((item) => item.favorites),
          smooth: true,
          itemStyle: {
            color: "#E6A23C",
          },
        },
        {
          name: "分类统计",
          type: "pie",
          radius: ["25%", "35%"],
          center: ["75%", "30%"],
          avoidLabelOverlap: true,
          itemStyle: {
            borderRadius: 4,
            borderColor: "#fff",
            borderWidth: 2,
          },
          label: {
            show: true,
            position: "outside",
            formatter: "{b}: {c}篇",
            color: "#666",
          },
          labelLine: {
            show: true,
            length: 10,
            length2: 10,
          },
          emphasis: {
            label: {
              show: true,
              fontSize: "14",
              fontWeight: "bold",
            },
          },
          data: categoryData.map((item) => ({
            name: item.name,
            value: item.value,
            itemStyle: {
              color: item.name === "无分类" ? "#909399" : undefined,
            },
          })),
        },
        {
          name: "标签统计",
          type: "pie",
          radius: ["25%", "35%"],
          center: ["75%", "75%"],
          avoidLabelOverlap: true,
          itemStyle: {
            borderRadius: 4,
            borderColor: "#fff",
            borderWidth: 2,
          },
          label: {
            show: true,
            position: "outside",
            formatter: "{b}: {c}篇",
            color: "#666",
          },
          labelLine: {
            show: true,
            length: 10,
            length2: 10,
          },
          emphasis: {
            label: {
              show: true,
              fontSize: "14",
              fontWeight: "bold",
            },
          },
          data: tagData,
        },
      ],
    };

    chart.setOption(option);
  }, 0);
};

// 监听数据变化
watch(
  () => props.posts,
  async () => {
    initChart();
  },
  { deep: true }
);

// 监听容器尺寸变化
const resizeObserver = new ResizeObserver(() => {
  if (chart) {
    chart.resize();
  }
});

// 处理窗口大小变化
const handleResize = () => {
  if (chart) {
    chart.resize();
  }
};

onMounted(() => {
  if (chartRef.value) {
    resizeObserver.observe(chartRef.value);
  }
});

onUnmounted(() => {
  resizeObserver.disconnect();
  if (chart) {
    chart.dispose();
    chart = null;
  }
});

// 添加导出方法
const exportChart = () => {
  if (!chart) return;

  const dataURL = chart.getDataURL({
    type: "png",
    pixelRatio: 2,
    backgroundColor: "#fff",
  });

  const link = document.createElement("a");
  link.download = "文章统计图表.png";
  link.href = dataURL;
  link.click();
};

// 暴露方法给父组件
defineExpose({
  initChart,
  exportChart,
});
</script>

<template>
  <div class="chart-container" ref="chartRef"></div>
</template>

<style scoped>
.chart-container {
  height: 400px;
  width: 100%;
  margin: 10px 0;
}
</style>
