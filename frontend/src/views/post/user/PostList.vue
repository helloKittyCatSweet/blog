<script setup>
import { ref, onMounted, computed, onUnmounted, watch, nextTick } from "vue";
import { ElMessage, ElMessageBox, ElDialog } from "element-plus";
import {
  Search,
  Edit,
  Delete,
  Plus,
  View,
  Document,
  Download,
  ArrowDown,
  ArrowUp,
} from "@element-plus/icons-vue";
import { useRouter } from "vue-router";
import PageContainer from "@/components/PageContainer.vue";
import {
  findByUserId,
  deleteById,
  searchPosts,
  findByKeysInTitle,
} from "@/api/post/post.js";
import { useUserStore } from "@/stores/modules/user.js";
import { USER_POST_EDIT_PATH, USER_POST_CREATE_PATH } from "@/constants/routes/user.js";
import { findByPostId } from "@/api/post/postVersion";
import "md-editor-v3/lib/style.css";
import VersionManager from "@/components/version/VersionManager.vue";
import { findAll as getAllTags } from "@/api/common/tag.js";

import PostSearchForm from "@/components/post/PostSearchForm.vue";
import PostActions from "@/components/post/PostActions.vue";
import PostStats from "@/components/post/PostStats.vue";

import * as XLSX from "xlsx";
import * as echarts from "echarts";

const router = useRouter();
const loading = ref(false);
const tableData = ref([]);
const total = ref(0);

// 搜索条件
const searchForm = ref({
  title: "",
  content: "",
  isPublished: null,
  visibility: "",
  categoryId: null,
  tagId: null,
  currentPage: 1,
  pageSize: 10,
  startDate: null,
  endDate: null,
});

// 状态选项
const statusOptions = [
  { label: "已发布", value: "PUBLISHED" },
  { label: "草稿", value: "DRAFT" },
];

const dateRange = ref([]);
const tags = ref([]);

// 处理日期范围变化
const handleDateRangeChange = (val) => {
  dateRange.value = val; // 更新日期范围
  if (val) {
    searchForm.value.startDate = val[0];
    searchForm.value.endDate = val[1];
  } else {
    searchForm.value.startDate = null;
    searchForm.value.endDate = null;
  }
};

const userStore = useUserStore();

// 获取文章列表
const getPostList = async () => {
  loading.value = true;
  try {
    let response;

    // 根据搜索条件决定使用哪个 API
    if (searchForm.value.keyword) {
      // 如果有关键词，使用标题搜索，并在前端处理状态过滤
      response = await findByKeysInTitle(searchForm.value.keyword);
      if (response.data.status === 200) {
        let filteredData = response.data.data;
        // 如果选择了状态，进行过滤
        if (searchForm.value.status) {
          filteredData = filteredData.filter((item) =>
            searchForm.value.status === "PUBLISHED"
              ? item.post.isPublished
              : !item.post.isPublished
          );
        }
        // 只显示当前用户的文章
        filteredData = filteredData.filter(
          (item) => item.post.userId === userStore.user.id
        );
        response.data.data = filteredData;
      }
    } else {
      // 没有关键词时使用用户名查询
      response = await findByUserId(userStore.user.id);
      if (response.data.status === 200 && searchForm.value.status) {
        // 如果选择了状态，进行过滤
        response.data.data = response.data.content.filter((item) =>
          searchForm.value.status === "PUBLISHED"
            ? item.post.isPublished
            : !item.post.isPublished
        );
      }
    }

    if (response.data.status === 200) {
      // 转换数据格式以适配表格显示
      tableData.value = response.data.data.map((item) => ({
        postId: item.post.postId,
        title: item.post.title,
        isPublished: item.post.isPublished,
        views: item.post.views,
        likes: item.post.likes,
        favorites: item.post.favorites,
        version: item.post.version,
        updatedAt: item.post.updatedAt ? item.post.updatedAt.split(" ")[0] : "-",
        category: item.category,
        author: item.author,
        visibility: item.post.visibility,
        tags: item.tags || [],
        content: item.post.content || "无内容",
      }));
      total.value = tableData.value.length;
    }
  } catch (error) {
    console.error("获取文章列表失败:", error);
    ElMessage.error("获取文章列表失败");
  } finally {
    loading.value = false;
  }
};

// 新建文章
const handleCreate = () => {
  router.push(USER_POST_CREATE_PATH);
};

// 编辑文章
const handleEdit = (id) => {
  router.push(`${USER_POST_EDIT_PATH}/${id}`);
};

// 删除文章
const handleDelete = (postId) => {
  ElMessageBox.confirm("确认删除该文章吗？", "警告", {
    confirmButtonText: "确定",
    cancelButtonText: "取消",
    type: "warning",
  }).then(async () => {
    try {
      const response = await deleteById(postId);
      if (response.data.status === 200) {
        ElMessage.success("删除成功");
        getPostList(); // 刷新列表
      } else {
        ElMessage.error(response.data.message || "删除失败");
      }
    } catch (error) {
      ElMessage.error("删除失败");
    }
  });
};

// 查看文章
const handleView = (postId) => {
  window.open(`/post/${postId}`, "_blank");
};

// 处理分页
const handleSizeChange = (val) => {
  searchForm.value.pageSize = val;
  getPostList();
};

const handleCurrentChange = (val) => {
  searchForm.value.currentPage = val;
  getPostList();
};

onMounted(() => {
  getPostList();
});

/**
 * 版本管理
 */

const versionDialogVisible = ref(false);
const currentVersionData = ref(null);
const versionLoading = ref(false);
const selectedVersion = ref(null);

// 快速预览版本
const handleVersion = async (row) => {
  currentVersionData.value = row;
  versionDialogVisible.value = true;
  versionLoading.value = true;
  try {
    const response = await findByPostId(row.postId);
    if (response.data.status === 200) {
      const versions = response.data.data;
      // 找到当前激活的版本
      const activeVersion = versions.find((v) => v.isActive);
      if (activeVersion) {
        selectedVersion.value = activeVersion;
      }
    }
  } catch (error) {
    ElMessage.error("获取版本列表失败");
  } finally {
    versionLoading.value = false;
  }
};

/**
 * 搜索重置
 */

// 添加搜索和重置功能
const handleSearch = async () => {
  searchForm.value.currentPage = 1; // 搜索时重置页码
  try {
    const response = await searchPosts({
      ...searchForm.value,
      isPrivate: true,
    });
    if (response.data.status === 200) {
      tableData.value = response.data.data.map((item) => ({
        postId: item.post.postId,
        title: item.post.title,
        isPublished: item.post.isPublished,
        views: item.post.views,
        likes: item.post.likes,
        favorites: item.post.favorites,
        version: item.post.version,
        updatedAt: item.post.updatedAt ? item.post.updatedAt.split(" ")[0] : "-",
        category: item.category,
        author: item.author,
        visibility: item.post.visibility,
        content: item.post.content || "无内容",
      }));
      total.value = response.data.total || tableData.value.length;
    }
  } catch (error) {
    console.error("搜索失败:", error);
    ElMessage.error("搜索失败");
  }
};
const handleReset = () => {
  searchForm.value = {
    title: "",
    content: "",
    isPublished: null,
    visibility: "",
    categoryId: null,
    tagId: null,
    currentPage: 1,
    pageSize: 10,
    startDate: null,
    endDate: null,
  };
  dateRange.value = [];
  selectedCategory.value = null; // 重置选中的分类
  getPostList(); // 重置后需要重新获取数据
};

// 获取分类和标签数据
const fetchCategoriesAndTags = async () => {
  try {
    const tagsRes = await getAllTags();
    if (tagsRes.data.status === 200) {
      tags.value = tagsRes.data.data.content;
    }
  } catch (error) {
    console.error("获取标签失败:", error);
    ElMessage.error("获取标签失败");
  }
};

onMounted(() => {
  fetchCategoriesAndTags();
});

const selectedCategory = ref(null);

/**
 * 导出表格数据函数
 */
const exportToExcel = () => {
  const exportData = tableData.value.map((item) => ({
    标题: item.title,
    内容: item.content || "无内容", // 添加内容字段
    状态: item.isPublished ? "已发布" : "草稿",
    可见性: item.visibility === "PUBLIC" ? "公开" : "私密",
    分类: item.category?.name || "未分类",
    标签: item.tags?.map((tag) => tag.name).join(", ") || "无标签",
    浏览量: item.views || 0,
    点赞数: item.likes || 0,
    收藏数: item.favorites || 0,
    更新时间: item.updatedAt,
  }));

  const ws = XLSX.utils.json_to_sheet(exportData);
  const wb = XLSX.utils.book_new();
  XLSX.utils.book_append_sheet(wb, ws, "文章列表");

  // 更新列宽以适应内容字段
  const colWidths = [
    { wch: 40 }, // 标题
    { wch: 100 }, // 内容
    { wch: 10 }, // 状态
    { wch: 10 }, // 可见性
    { wch: 15 }, // 分类
    { wch: 30 }, // 标签
    { wch: 10 }, // 浏览量
    { wch: 10 }, // 点赞数
    { wch: 10 }, // 收藏数
    { wch: 20 }, // 更新时间
  ];
  ws["!cols"] = colWidths;

  // 生成文件名
  const fileName = `文章列表_${
    userStore.user.username
  }_${new Date().toLocaleDateString()}.xlsx`;

  // 导出文件
  XLSX.writeFile(wb, fileName);
};

/**
 * 排序
 */
const sortBy = ref("");
const sortOrder = ref("ascending");

// 计算排序后的数据
const sortedTableData = computed(() => {
  const data = [...tableData.value];
  if (!sortBy.value) return data;

  return data.sort((a, b) => {
    let compareResult = 0;
    switch (sortBy.value) {
      case "views":
        compareResult = (a.views || 0) - (b.views || 0);
        break;
      case "likes":
        compareResult = (a.likes || 0) - (b.likes || 0);
        break;
      case "favorites":
        compareResult = (a.favorites || 0) - (b.favorites || 0);
        break;
      case "updatedAt":
        compareResult = new Date(a.updatedAt) - new Date(b.updatedAt);
        break;
      case "sum":
        const sumA = (a.views || 0) + (a.likes || 0) + (a.favorites || 0);
        const sumB = (b.views || 0) + (b.likes || 0) + (b.favorites || 0);
        compareResult = sumA - sumB;
        break;
      default:
        return 0;
    }
    return sortOrder.value === "ascending" ? compareResult : -compareResult;
  });
});

// 处理排序变化
const handleSortChange = ({ prop, order }) => {
  sortBy.value = prop;
  sortOrder.value = order;
  if (prop === "sum") {
    sortedTableData.value = [...tableData.value].sort((a, b) => {
      const sumA = (a.views || 0) + (a.likes || 0) + (a.favorites || 0);
      const sumB = (b.views || 0) + (b.likes || 0) + (b.favorites || 0);
      return sortOrder.value === "ascending" ? sumA - sumB : sumB - sumA;
    });
  }
};

/**
 * 图表数据
 */
// 添加图表相关的数据和方法
const chartRef = ref(null);
let chart = null;

// 添加图表显示状态控制
const isChartVisible = ref(false);

// 初始化图表
const initChart = async () => {
  if (!chartRef.value) return;

  // 销毁旧的图表实例
  if (chart) {
    chart.dispose();
  }

  // 等待 DOM 更新完成
  await nextTick();

  // 确保容器可见时才初始化
  if (isChartVisible.value) {
    chart = echarts.init(chartRef.value);
    updateChart();
  }
};

// 监听图表显示状态变化
watch(isChartVisible, async (visible) => {
  if (visible) {
    await initChart();
  }
});

// 监听数据变化更新图表
watch(tableData, () => {
  if (isChartVisible.value && chart) {
    updateChart();
  }
});

// 更新图表数据
const updateChart = () => {
  if (!chart) return;

  const option = {
    title: {
      text: "文章数据统计",
      left: "center",
    },
    tooltip: {
      trigger: "axis",
      axisPointer: {
        type: "shadow",
      },
    },
    legend: {
      top: "30px",
      data: ["浏览量", "点赞数", "收藏数"],
    },
    grid: {
      left: "3%",
      right: "4%",
      bottom: "3%",
      containLabel: true,
    },
    xAxis: {
      type: "category",
      data: tableData.value.map((item) => item.title),
      axisLabel: {
        interval: 0,
        rotate: 45,
      },
    },
    yAxis: {
      type: "value",
    },
    series: [
      {
        name: "浏览量",
        type: "bar",
        data: tableData.value.map((item) => item.views || 0),
        itemStyle: {
          color: "#409EFF",
        },
      },
      {
        name: "点赞数",
        type: "bar",
        data: tableData.value.map((item) => item.likes || 0),
        itemStyle: {
          color: "#67C23A",
        },
      },
      {
        name: "收藏数",
        type: "bar",
        data: tableData.value.map((item) => item.favorites || 0),
        itemStyle: {
          color: "#E6A23C",
        },
      },
    ],
  };

  chart.setOption(option);
};

// 监听数据变化更新图表
watch(tableData, () => {
  updateChart();
});

// 在组件挂载时初始化图表
onMounted(() => {
  getPostList();
  fetchCategoriesAndTags();
});

// 在组件卸载时销毁图表
onUnmounted(() => {
  if (chart) {
    chart.dispose();
    chart = null;
  }
});

// 添加图表导出方法
const handleExportChart = () => {
  if (!chart) return;

  const dataURL = chart.getDataURL({
    type: "png",
    pixelRatio: 2,
    backgroundColor: "#fff",
  });

  const link = document.createElement("a");
  link.download = `文章统计图表_${
    userStore.user.username
  }_${new Date().toLocaleDateString()}.png`;
  link.href = dataURL;
  link.click();
};
</script>

<template>
  <page-container title="文章管理">
    <!-- 搜索栏 -->
    <el-card class="search-card">
      <post-search-form
        v-model="searchForm"
        :tags="tags"
        v-model:date-range="dateRange"
        @search="handleSearch"
        @reset="handleReset"
        @date-range-change="handleDateRangeChange"
        @update:category="(val) => (selectedCategory = val)"
      >
        <template #extra-buttons>
          <el-button type="primary" :icon="Plus" @click="handleCreate">
            写文章
          </el-button>
          <el-button type="success" :icon="Download" @click="exportToExcel">
            导出Excel
          </el-button>
        </template>
      </post-search-form>
    </el-card>

    <!-- 表格 -->
    <el-card class="table-card">
      <el-table
        v-loading="loading"
        :data="sortedTableData"
        border
        stripe
        @sort-change="handleSortChange"
      >
        <template #empty>
          <el-empty description="暂无文章" />
        </template>
        <el-table-column prop="title" label="标题" show-overflow-tooltip />
        <el-table-column label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="row.isPublished ? 'success' : 'info'">
              {{ row.isPublished ? "已发布" : "草稿" }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="可见性" width="100">
          <template #default="{ row }">
            <el-tag :type="row.visibility === 'PUBLIC' ? 'success' : 'warning'">
              {{ row.visibility === "PUBLIC" ? "公开" : "私密" }}
            </el-tag>
          </template>
        </el-table-column>

        <el-table-column label="分类" width="120">
          <template #default="{ row }">
            <el-tag size="small" :type="row.category?.name ? 'info' : 'warning'">
              {{ row.category?.name || "未分类" }}
            </el-tag>
          </template>
        </el-table-column>

        <el-table-column label="标签" width="200">
          <template #default="{ row }">
            <el-space wrap v-if="row.tags && row.tags.length">
              <el-tag
                v-for="tag in row.tags"
                :key="tag.tagId"
                size="small"
                type="success"
              >
                {{ tag.name }}
              </el-tag>
            </el-space>
            <el-tag v-else size="small" type="info">无标签</el-tag>
          </template>
        </el-table-column>

        <!-- 数据统计 -->
        <el-table-column
          label="数据统计"
          width="280"
          sortable
          :sort-orders="['ascending', 'descending']"
        >
          <template #default="{ row }">
            <post-stats
              :views="row.views"
              :likes="row.likes"
              :favorites="row.favorites"
              :sort-key="sortBy"
              :sort-order="sortOrder"
              @sort="handleSortChange"
            />
          </template>
        </el-table-column>

        <el-table-column
          prop="updatedAt"
          label="更新时间"
          width="180"
          sortable
          :sort-orders="['ascending', 'descending']"
        >
          <template #default="{ row }">
            {{ row.updatedAt }}
          </template>
        </el-table-column>
        <!-- 操作列 -->
        <el-table-column label="操作" width="220" align="center">
          <template #default="{ row }">
            <post-actions
              :post-id="row.postId"
              :row="row"
              @view="handleView"
              @edit="handleEdit"
              @version="handleVersion"
              @delete="handleDelete"
            />
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页 -->
      <div class="pagination">
        <el-pagination
          v-model:current-page="searchForm.currentPage"
          v-model:page-size="searchForm.pageSize"
          :total="total"
          :page-sizes="[10, 20, 50, 100]"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
        />
      </div>
    </el-card>

    <!-- 数据分析图表 -->
    <el-card class="chart-card">
      <template #header>
        <div class="chart-header">
          <span>数据统计</span>
          <div>
            <el-button
              type="primary"
              :icon="Download"
              size="small"
              @click="handleExportChart"
            >
              导出图表
            </el-button>
            <el-button
              :icon="isChartVisible ? ArrowUp : ArrowDown"
              @click="isChartVisible = !isChartVisible"
            />
          </div>
        </div>
      </template>
      <div v-show="isChartVisible">
        <div class="chart-container" ref="chartRef"></div>
      </div>
    </el-card>

    <!-- 版本预览对话框 -->
    <el-dialog
      v-model="versionDialogVisible"
      title="版本管理"
      width="80%"
      destroy-on-close
    >
      <version-manager :post-data="currentVersionData" />
    </el-dialog>
  </page-container>
</template>

<style scoped>
.table-card {
  margin-bottom: 20px;
}

.pagination {
  margin-top: 20px;
  text-align: right;
}

.version-preview-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
}

.version-info {
  display: flex;
  align-items: center;
}

.version-title {
  font-size: 16px;
  font-weight: bold;
}

.version-option {
  display: flex;
  justify-content: space-between;
  align-items: center;
  width: 100%;
}

.version-meta {
  display: flex;
  align-items: center;
  gap: 8px;
}

.version-date {
  color: var(--el-text-color-secondary);
  font-size: 13px;
}

.version-meta-info {
  margin: 16px 0;
}

.ml-2 {
  margin-left: 8px;
}

.search-card {
  margin-bottom: 20px;
}

.el-form-item {
  margin-bottom: 18px;
}

.el-date-picker {
  width: 100%;
}

.chart-card {
  margin-top: 20px;
}

.chart-container {
  height: 400px;
  width: 100%;
}

:deep(.hidden-column) {
  display: none;
}

.post-actions {
  display: flex;
  justify-content: space-around; /* 或者使用 space-between */
  align-items: center;
  gap: 8px;
}

.chart-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.chart-container {
  height: 400px;
  width: 100%;
  margin: 10px 0;
}
</style>
