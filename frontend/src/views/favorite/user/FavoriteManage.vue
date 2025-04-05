<script setup>
import { ref, onMounted } from "vue";
import { ElMessage, ElMessageBox } from "element-plus";
import { Search, Delete, View } from "@element-plus/icons-vue";
import PageContainer from "@/components/PageContainer.vue";
import { formatDateTime } from "@/utils/format";

const loading = ref(false);
const tableData = ref([]);
const total = ref(0);

// 搜索条件
const searchForm = ref({
  keyword: "",
  currentPage: 1,
  pageSize: 10,
});

// 获取收藏列表
const getFavoriteList = async () => {
  loading.value = true;
  try {
    // TODO: 调用获取收藏列表API
    loading.value = false;
  } catch (error) {
    ElMessage.error("获取收藏列表失败");
    loading.value = false;
  }
};

// 取消收藏
const handleDelete = (row) => {
  ElMessageBox.confirm("确认取消收藏该文章吗？", "提示", {
    confirmButtonText: "确定",
    cancelButtonText: "取消",
    type: "warning",
  }).then(async () => {
    try {
      // TODO: 调用取消收藏API
      ElMessage.success("取消收藏成功");
      getFavoriteList();
    } catch (error) {
      ElMessage.error("取消收藏失败");
    }
  });
};

// 查看文章
const handleView = (row) => {
  window.open(`/post/${row.postId}`, "_blank");
};

// 处理搜索
const handleSearch = () => {
  searchForm.value.currentPage = 1;
  getFavoriteList();
};

// 重置搜索
const resetSearch = () => {
  searchForm.value.keyword = "";
  handleSearch();
};

// 处理分页
const handleSizeChange = (val) => {
  searchForm.value.pageSize = val;
  getFavoriteList();
};

const handleCurrentChange = (val) => {
  searchForm.value.currentPage = val;
  getFavoriteList();
};

onMounted(() => {
  getFavoriteList();
});
</script>

<template>
  <page-container title="我的收藏">
    <!-- 搜索栏 -->
    <el-card class="search-card">
      <el-row :gutter="20">
        <el-col :span="6">
          <el-input
            v-model="searchForm.keyword"
            placeholder="搜索文章标题"
            clearable
            @keyup.enter="handleSearch"
          >
            <template #suffix>
              <el-icon class="el-input__icon">
                <Search />
              </el-icon>
            </template>
          </el-input>
        </el-col>
        <el-col :span="18" style="text-align: right">
          <el-button type="primary" :icon="Search" @click="handleSearch">搜索</el-button>
          <el-button @click="resetSearch">重置</el-button>
        </el-col>
      </el-row>
    </el-card>

    <!-- 表格 -->
    <el-card class="table-card">
      <el-table v-loading="loading" :data="tableData" border stripe>
        <template #empty>
          <el-empty description="暂无收藏" />
        </template>
        <el-table-column type="index" label="序号" width="80" align="center" />
        <el-table-column prop="post.title" label="文章标题" show-overflow-tooltip>
          <template #default="{ row }">
            <el-link type="primary" @click="handleView(row)">
              {{ row.post?.title || "文章已删除" }}
            </el-link>
          </template>
        </el-table-column>
        <el-table-column prop="post.user.username" label="作者" width="120" />
        <el-table-column label="文章数据" width="250">
          <template #default="{ row }">
            <el-space>
              <el-tag type="info">浏览 {{ row.post?.views || 0 }}</el-tag>
              <el-tag type="success">点赞 {{ row.post?.likes || 0 }}</el-tag>
              <el-tag type="warning">收藏 {{ row.post?.favorites || 0 }}</el-tag>
            </el-space>
          </template>
        </el-table-column>
        <el-table-column prop="createdAt" label="收藏时间" width="180">
          <template #default="{ row }">
            {{ formatDateTime(row.createdAt) }}
          </template>
        </el-table-column>
        <el-table-column label="操作" width="150" align="center">
          <template #default="{ row }">
            <el-button
              type="primary"
              :icon="View"
              circle
              @click="handleView(row)"
              title="查看文章"
            />
            <el-button
              type="danger"
              :icon="Delete"
              circle
              @click="handleDelete(row)"
              title="取消收藏"
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
