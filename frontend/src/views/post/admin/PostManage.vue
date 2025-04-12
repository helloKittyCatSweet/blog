<script setup>
import { ref, onMounted, computed, onUnmounted, watch } from "vue";
import { ElMessage, ElMessageBox } from "element-plus";
import { Search, Edit, Delete, View, Download } from "@element-plus/icons-vue";
import { useRouter } from "vue-router";
import PageContainer from "@/components/PageContainer.vue";
import { findAll, deleteById, searchPosts } from "@/api/post/post.js";
import { findByPostId } from "@/api/post/postVersion";
import { findAll as getAllTags } from "@/api/common/tag.js";
import { findAll as getAllUsers } from "@/api/user/user.js";

import PostSearchForm from "@/components/post/PostSearchForm.vue";
import PostActions from "@/components/post/PostActions.vue";
import PostStats from "@/components/post/PostStats.vue";

import * as XLSX from "xlsx";
import * as echarts from "echarts";

const router = useRouter();
const loading = ref(false);
const tableData = ref([]);
const total = ref(0);
const users = ref([]); // 所有用户列表

// 搜索条件
const searchForm = ref({
  title: "",
  content: "",
  isPublished: null,
  visibility: "",
  categoryId: null,
  tagId: null,
  userId: null, // 新增：按用户筛选
  currentPage: 1,
  pageSize: 10,
});

const dateRange = ref([]);
const tags = ref([]);

// 获取文章列表
const getPostList = async () => {
  loading.value = true;
  try {
    const response = await findAll();
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

// 初始化数据
const initData = async () => {
  try {
    // 获取所有用户
    const usersResponse = await getAllUsers();
    if (usersResponse.data.status === 200) {
      users.value = usersResponse.data.data;
    }

    // 获取所有标签
    const tagsResponse = await getAllTags();
    if (tagsResponse.data.status === 200) {
      tags.value = tagsResponse.data.data;
    }

    await getPostList();
  } catch (error) {
    console.error("初始化数据失败:", error);
    ElMessage.error("初始化数据失败");
  }
};

onMounted(() => {
  initData();
  initChart();
});

// ... 其他代码与 PostList.vue 类似，包括图表、导出、排序等功能 ...
</script>

<template>
  <page-container title="文章管理（管理员）">
    <!-- 搜索栏 -->
    <el-card class="search-card">
      <post-search-form
        v-model="searchForm"
        :tags="tags"
        @search="handleSearch"
        @reset="handleReset"
        @date-range-change="handleDateRangeChange"
      >
        <!-- 添加用户筛选 -->
        <template #extra-filters>
          <el-form-item label="作者">
            <el-select v-model="searchForm.userId" placeholder="选择作者" clearable>
              <el-option
                v-for="user in users"
                :key="user.id"
                :label="user.username"
                :value="user.id"
              />
            </el-select>
          </el-form-item>
        </template>

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
        <el-table-column prop="title" label="标题" show-overflow-tooltip />

        <!-- 新增作者列 -->
        <el-table-column prop="author.username" label="作者" width="120" />

        <el-table-column label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="row.isPublished ? 'success' : 'info'">
              {{ row.isPublished ? "已发布" : "草稿" }}
            </el-tag>
          </template>
        </el-table-column>

        <!-- ... 其他列与 PostList.vue 类似 ... -->

        <!-- 操作列 -->
        <el-table-column label="操作" width="180" align="center">
          <template #default="{ row }">
            <post-actions
              :post-id="row.postId"
              :row="row"
              :is-admin="true"
              @view="handleView"
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
      <div class="chart-container" ref="chartRef"></div>
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
</style>
