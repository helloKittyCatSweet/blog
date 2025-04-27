<script setup>
import { ref, onMounted, computed, onUnmounted, watch, nextTick } from "vue";
import { ElMessage, ElMessageBox } from "element-plus";
import {
  Search,
  Edit,
  Delete,
  View,
  Download,
  ArrowUp,
  ArrowDown,
} from "@element-plus/icons-vue";
import { useRouter } from "vue-router";
import PageContainer from "@/components/PageContainer.vue";
import {
  findAll,
  deleteById,
  searchPosts,
  updateCategory,
  updateTags,
} from "@/api/post/post.js";
import { findAll as getAllTags } from "@/api/common/tag.js";
import PostSearchForm from "@/components/post/PostSearchForm.vue";
import PostStatsChart from "@/components/post/PostStatsChart.vue";
import CategorySelect from "@/components/category/CategorySelect.vue";
import TagSelect from "@/components/tag/TagSelect.vue";

import * as XLSX from "xlsx";

const router = useRouter();
const loading = ref(false);
const tableData = ref([]);
const total = ref(0);

// 搜索条件
const searchForm = ref({
  title: "",
  content: "",
  categoryId: null,
  tagId: null,
  userId: null,
  startDate: null, // 添加开始日期
  endDate: null, // 添加结束日期
  currentPage: 1,
  pageSize: 10,
});

const dateRange = ref([]);
const tags = ref([]);

// 添加统一的响应处理函数
const handlePostResponse = (response) => {
  tableData.value = response.data.data.content.map((item) => ({
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
};

// 获取文章列表
const getPostList = async () => {
  loading.value = true;
  try {
    const response = await findAll();
    if (response.data.status === 200) {
      handlePostResponse(response);
    }
  } catch (error) {
    console.error("获取文章列表失败:", error);
    ElMessage.error("获取文章列表失败");
  } finally {
    loading.value = false;
  }
};

// 删除文章
const handleDelete = (postId) => {
  ElMessageBox.confirm("确认删除该文章吗？此操作不可恢复！", "警告", {
    confirmButtonText: "确定",
    cancelButtonText: "取消",
    type: "warning",
  }).then(async () => {
    try {
      const response = await deleteById(postId);
      if (response.data.status === 200) {
        ElMessage.success("删除成功");
        getPostList();
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

onMounted(() => {
  getPostList();
  fetchTags();
});

/**
 * 搜索文章
 */
// 处理日期范围变化
const handleDateRangeChange = (val) => {
  if (val) {
    searchForm.value.startDate = val[0];
    searchForm.value.endDate = val[1];
  } else {
    searchForm.value.startDate = null;
    searchForm.value.endDate = null;
  }
};

// 重置搜索条件
const handleReset = () => {
  searchForm.value = {
    title: "",
    content: "",
    categoryId: null,
    tagId: null,
    userId: null, // 保留用户筛选字段
    currentPage: 1,
    pageSize: 10,
    startDate: null,
    endDate: null,
  };
  dateRange.value = [];
  getPostList(); // 重置后重新获取数据
};

// 排序相关
const sortBy = ref("");
const sortOrder = ref("ascending");

// 排序后的表格数据
const sortedTableData = computed(() => {
  const data = [...tableData.value];
  if (sortBy.value) {
    data.sort((a, b) => {
      let aValue = a[sortBy.value];
      let bValue = b[sortBy.value];

      // 处理嵌套属性（比如 author.username）
      if (sortBy.value.includes(".")) {
        const props = sortBy.value.split(".");
        aValue = props.reduce((obj, prop) => obj?.[prop], a);
        bValue = props.reduce((obj, prop) => obj?.[prop], b);
      }

      if (sortOrder.value === "descending") {
        [aValue, bValue] = [bValue, aValue];
      }

      return aValue > bValue ? 1 : -1;
    });
  }
  return data;
});

// 处理表格排序变化
const handleSortChange = ({ prop, order }) => {
  sortBy.value = prop;
  sortOrder.value = order;
};

// 处理每页显示数量变化
const handleSizeChange = (val) => {
  searchForm.value.pageSize = val;
  searchForm.value.currentPage = 1;
  getPostList();
};

// 处理当前页变化
const handleCurrentChange = (val) => {
  searchForm.value.currentPage = val;
  getPostList();
};

// 处理搜索
const handleSearch = async () => {
  try {
    loading.value = true;
    const response = await searchPosts({
      ...searchForm.value,
      isPrivate: false,
    });
    if (response.data.status === 200) {
      handlePostResponse(response);
      total.value = tableData.value.length;
    }
  } catch (error) {
    console.error("搜索文章失败:", error);
    ElMessage.error("搜索文章失败");
  } finally {
    loading.value = false;
  }
};

// 导出Excel
const exportToExcel = () => {
  const data = tableData.value.map((item) => ({
    标题: item.title,
    作者: item.author.username,
    浏览量: item.views,
    点赞数: item.likes,
    收藏数: item.favorites,
    更新时间: item.updatedAt,
    分类: item.category?.name || "无分类",
    标签: item.tags.map((tag) => tag.name).join(", ") || "无标签",
    内容: item.content || "无内容", // 添加文章内容字段
  }));

  const ws = XLSX.utils.json_to_sheet(data);

  // 设置内容列的宽度
  ws["!cols"] = [
    { wch: 20 }, // 标题
    { wch: 10 }, // 作者
    { wch: 8 }, // 状态
    { wch: 8 }, // 浏览量
    { wch: 8 }, // 点赞数
    { wch: 8 }, // 收藏数
    { wch: 12 }, // 更新时间
    { wch: 15 }, // 分类
    { wch: 20 }, // 标签
    { wch: 50 }, // 内容
  ];

  const wb = XLSX.utils.book_new();
  XLSX.utils.book_append_sheet(wb, ws, "文章列表");
  XLSX.writeFile(wb, "文章管理列表.xlsx");
};

/**
 * 图表部分
 */
// 添加状态
const isChartVisible = ref(false);
const statsChartRef = ref(null);

// 监听图表显示状态
watch(isChartVisible, (visible) => {
  if (visible) {
    // 使用 nextTick 确保 DOM 已更新
    nextTick(() => {
      statsChartRef.value?.initChart();
    });
  }
});

// 添加导出图表方法
const handleExportChart = () => {
  if (statsChartRef.value) {
    statsChartRef.value.exportChart();
  }
};

/**
 * 标签搜索
 */
// 获取所有标签
const fetchTags = async () => {
  try {
    const response = await getAllTags();
    if (response.data.status === 200) {
      tags.value = response.data.data.content;
    }
  } catch (error) {
    console.error("获取标签列表失败:", error);
    ElMessage.error("获取标签列表失败");
  }
};

/**
 * 管理员修改
 */
// 添加弹窗相关的响应式变量
const editDialogVisible = ref(false);
const currentEditPost = ref(null);

// 添加编辑弹窗的处理方法
const handleEdit = (row) => {
  currentEditPost.value = { ...row };
  editDialogVisible.value = true;
};

// 处理保存
const handleSave = async () => {
  try {
    const postId = currentEditPost.value.postId;
    const categoryId = currentEditPost.value.category?.categoryId;
    const tags = currentEditPost.value.tags?.map((tag) => tag.tagId);

    const promises = [];
    const responses = [];

    // 只有当分类有值时才调用更新分类接口
    if (categoryId !== undefined) {
      promises.push(updateCategory(postId, categoryId));
    }

    // 只有当标签数组有值时才调用更新标签接口
    if (tags && tags.length > 0) {
      promises.push(updateTags(postId, tags));
    }

    // 如果没有任何需要更新的内容，直接返回
    if (promises.length === 0) {
      ElMessage.warning("没有需要更新的内容");
      return;
    }

    // 执行所有需要更新的请求
    const results = await Promise.all(promises);

    // 检查所有响应是否成功
    const isAllSuccess = results.every((res) => res.data.status === 200);

    if (isAllSuccess) {
      ElMessage.success("更新成功");
      editDialogVisible.value = false;
      getPostList(); // 刷新列表
    }
  } catch (error) {
    console.error("更新失败:", error);
    ElMessage.error("更新失败");
  }
};
</script>

<template>
  <page-container title="文章管理（管理员）">
    <!-- 搜索栏 -->
    <el-card class="search-card">
      <post-search-form
        v-model="searchForm"
        :tags="tags"
        :show-users="true"
        :show-status="false"
        :show-visibility="false"
        :date-range="dateRange"
        @search="handleSearch"
        @reset="handleReset"
        @date-range-change="handleDateRangeChange"
        @update:date-range="(val) => (dateRange = val)"
      >
        <template #extra-buttons>
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

        <!-- 标题列 -->
        <el-table-column
          prop="title"
          label="标题"
          min-width="200"
          show-overflow-tooltip
        />

        <!-- 作者列 -->
        <el-table-column prop="author" label="作者" width="100">
          <template #default="{ row }">
            {{ row.author }}
          </template>
        </el-table-column>

        <!-- 分类列 -->
        <el-table-column
          prop="category.name"
          label="分类"
          width="120"
          show-overflow-tooltip
        >
          <template #default="{ row }">
            {{ row.category?.name || "无分类" }}
          </template>
        </el-table-column>

        <!-- 标签列 -->
        <el-table-column label="标签" width="150">
          <template #default="{ row }">
            <div class="tag-wrapper">
              <el-tag
                v-for="tag in row.tags"
                :key="tag.tagId"
                size="small"
                class="tag-item"
              >
                {{ tag.name }}
              </el-tag>
            </div>
          </template>
        </el-table-column>

        <!-- 数据统计列 -->
        <el-table-column label="数据统计" width="280">
          <template #default="{ row }">
            <post-stats
              :views="row.views"
              :likes="row.likes"
              :favorites="row.favorites"
            />
          </template>
        </el-table-column>

        <!-- 更新时间列 -->
        <el-table-column prop="updatedAt" label="更新时间" width="120" sortable />

        <!-- 操作列 -->
        <el-table-column label="操作" width="240" align="center" fixed="right">
          <template #default="{ row }">
            <div class="action-buttons">
              <el-button
                type="primary"
                :icon="View"
                size="small"
                @click="handleView(row.postId)"
                title="查看文章"
              >
                查看
              </el-button>
              <el-button
                type="warning"
                :icon="Edit"
                size="small"
                @click="handleEdit(row)"
                title="编辑分类和标签"
              >
                编辑
              </el-button>
              <el-button
                type="danger"
                :icon="Delete"
                size="small"
                @click="handleDelete(row.postId)"
                title="删除文章"
              >
                删除
              </el-button>
            </div>
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

    <el-dialog v-model="editDialogVisible" title="编辑文章分类和标签" width="500px">
      <el-form label-width="80px">
        <el-form-item label="分类">
          <category-select
            v-model="currentEditPost.category.categoryId"
            @error="(err) => ElMessage.error('分类加载失败：' + err)"
          />
        </el-form-item>
        <el-form-item label="标签">
          <tag-select
            v-model="currentEditPost.tags"
            multiple
            @error="(err) => ElMessage.error('标签加载失败：' + err)"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="editDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="handleSave"> 确认 </el-button>
        </span>
      </template>
    </el-dialog>

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
      <el-collapse-transition>
        <div v-show="isChartVisible">
          <post-stats-chart ref="statsChartRef" :posts="tableData" />
        </div>
      </el-collapse-transition>
    </el-card>
  </page-container>
</template>

<style scoped>
/* 样式与 PostList.vue 相同 */
.table-card {
  margin-bottom: 20px;
}

.pagination {
  margin-top: 20px;
  text-align: right;
}

.search-card {
  margin-bottom: 20px;
}

.chart-card {
  margin-top: 20px;
}

.chart-container {
  height: 400px;
  width: 100%;
}

.chart-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.action-buttons {
  display: flex;
  justify-content: space-between;
  gap: 5px; /* 控制按钮之间的间距 */
}

.tag-wrapper {
  display: flex;
  flex-wrap: wrap;
  gap: 4px;
}

.tag-item {
  margin: 2px;
  max-width: 100%;
}

/* 其他已有样式保持不变 */
.table-card {
  margin-bottom: 20px;
}
</style>
