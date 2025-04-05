<script setup>
import { ref, onMounted } from "vue";
import { ElMessage, ElMessageBox } from "element-plus";
import { Search, Edit, Delete, Plus, View } from "@element-plus/icons-vue";
import { useRouter } from "vue-router";
import PageContainer from "@/components/PageContainer.vue";
import { formatDateTime } from "@/utils/format";

const router = useRouter();
const loading = ref(false);
const tableData = ref([]);
const total = ref(0);

// 搜索条件
const searchForm = ref({
  keyword: "",
  status: "",
  currentPage: 1,
  pageSize: 10,
});

// 状态选项
const statusOptions = [
  { label: "已发布", value: "PUBLISHED" },
  { label: "草稿", value: "DRAFT" },
];

// 获取文章列表
const getPostList = async () => {
  loading.value = true;
  try {
    // TODO: 调用获取文章列表API
    loading.value = false;
  } catch (error) {
    ElMessage.error("获取文章列表失败");
    loading.value = false;
  }
};

// 新建文章
const handleCreate = () => {
  router.push("/user/post/edit");
};

// 编辑文章
const handleEdit = (row) => {
  router.push(`/user/post/edit/${row.postId}`);
};

// 删除文章
const handleDelete = (row) => {
  ElMessageBox.confirm("确认删除该文章吗？", "警告", {
    confirmButtonText: "确定",
    cancelButtonText: "取消",
    type: "warning",
  }).then(async () => {
    try {
      // TODO: 调用删除API
      ElMessage.success("删除成功");
      getPostList();
    } catch (error) {
      ElMessage.error("删除失败");
    }
  });
};

// 查看文章
const handleView = (row) => {
  window.open(`/post/${row.postId}`, "_blank");
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
</script>

<template>
  <page-container title="文章管理">
    <!-- 搜索栏 -->
    <el-card class="search-card">
      <el-row :gutter="20">
        <el-col :span="6">
          <el-input
            v-model="searchForm.keyword"
            placeholder="搜索文章标题"
            clearable
            @keyup.enter="getPostList"
          >
            <template #suffix>
              <el-icon class="el-input__icon">
                <Search />
              </el-icon>
            </template>
          </el-input>
        </el-col>
        <el-col :span="6">
          <el-select v-model="searchForm.status" placeholder="文章状态" clearable>
            <el-option
              v-for="item in statusOptions"
              :key="item.value"
              :label="item.label"
              :value="item.value"
            />
          </el-select>
        </el-col>
        <el-col :span="12" style="text-align: right">
          <el-button type="primary" :icon="Plus" @click="handleCreate">写文章</el-button>
        </el-col>
      </el-row>
    </el-card>

    <!-- 表格 -->
    <el-card class="table-card">
      <el-table v-loading="loading" :data="tableData" border stripe>
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
        <el-table-column label="数据统计" width="250">
          <template #default="{ row }">
            <el-space>
              <el-tag type="info">浏览 {{ row.views || 0 }}</el-tag>
              <el-tag type="success">点赞 {{ row.likes || 0 }}</el-tag>
              <el-tag type="warning">收藏 {{ row.favorites || 0 }}</el-tag>
            </el-space>
          </template>
        </el-table-column>
        <el-table-column prop="updatedAt" label="更新时间" width="180">
          <template #default="{ row }">
            {{ formatDateTime(row.updatedAt) }}
          </template>
        </el-table-column>
        <el-table-column label="操作" width="180" align="center">
          <template #default="{ row }">
            <el-button
              type="primary"
              :icon="View"
              circle
              @click="handleView(row)"
              title="查看"
            />
            <el-button
              type="warning"
              :icon="Edit"
              circle
              @click="handleEdit(row)"
              title="编辑"
            />
            <el-button
              type="danger"
              :icon="Delete"
              circle
              @click="handleDelete(row)"
              title="删除"
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
  </page-container>
</template>

<style scoped>
.search-card {
  margin-bottom: 20px;
}

.table-card {
  margin-bottom: 20px;
}

.pagination {
  margin-top: 20px;
  text-align: right;
}
</style>
