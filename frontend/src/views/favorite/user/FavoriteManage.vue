<script setup>
import { ref, onMounted, computed } from "vue";
import { ElMessage, ElMessageBox } from "element-plus";
import {
  Search,
  Delete,
  View,
  Star,
  ChatRound,
  Calendar,
  More,
} from "@element-plus/icons-vue";
import PageContainer from "@/components/PageContainer.vue";
import { formatDateTime } from "@/utils/format";
import {
  getFolderNames,
  getPostsByFolder,
  moveToFolder,
  findByUserId,
  deleteById,
} from "@/api/post/favorite";
import { useRoute } from "vue-router";

const route = useRoute();

const loading = ref(false);
const favorites = ref([]);
const total = ref(0);

// 搜索条件
const searchForm = ref({
  keyword: "",
  currentPage: 1,
  pageSize: 10,
  isGlobalSearch: false, // 全局搜索标记
});

// 收藏夹列表
const folders = ref([]);
const currentFolder = ref("默认收藏夹");

// 获取收藏夹列表
const getFolders = async () => {
  try {
    const { data } = await getFolderNames();
    // console.log("folderNames:", data);
    folders.value = data.data;
    // 如果后端没有返回默认收藏夹，才添加
    if (!folders.value.includes("默认收藏夹")) {
      folders.value = ["默认收藏夹", ...folders.value];
    }
  } catch (error) {
    ElMessage.error("获取收藏夹列表失败");
  }
};

// 切换收藏夹
const handleFolderChange = async (folder) => {
  searchForm.value.keyword = ""; // 清空搜索关键词
  searchForm.value.isGlobalSearch = false;
  currentFolder.value = folder;
  await getFavoriteList();
};

// 移动到其他收藏夹
const handleMove = (item, targetFolder) => {
  if (!item?.favoriteId) {
    ElMessage.error("获取收藏记录失败，请刷新后重试");
    return;
  }

  ElMessageBox.confirm(`确认将该文章移动到"${targetFolder}"收藏夹吗？`, "提示", {
    confirmButtonText: "确定",
    cancelButtonText: "取消",
    type: "warning",
  }).then(async () => {
    try {
      await moveToFolder(item.favoriteId, targetFolder);
      ElMessage.success("移动成功");
      await getFavoriteList();
      await getFolderStats();
    } catch (error) {
      ElMessage.error("移动失败");
    }
  });
};

// 计算属性：过滤后的收藏列表
const filteredFavorites = computed(() => {
  if (!searchForm.value.keyword) return favorites.value;
  if (!Array.isArray(favorites.value)) return [];

  const keyword = searchForm.value.keyword.toLowerCase();
  return favorites.value.filter(
    (item) =>
      item.post?.title?.toLowerCase().includes(keyword) ||
      item.post?.content?.toLowerCase().includes(keyword)
  );
});

// 获取收藏列表
const getFavoriteList = async () => {
  loading.value = true;
  try {
    const { data } = await getPostsByFolder(currentFolder.value, {
      page: searchForm.value.currentPage - 1,  // 后端分页从0开始
      size: searchForm.value.pageSize,
      sort: ['createdTime,desc']
    });
    // console.log("收藏列表数据:", data);
    // 修改数据获取逻辑
    if (data.status === 200) {
      favorites.value = data.data.content;
      total.value = data.data.totalElements;
    }
  } catch (error) {
    console.error("获取收藏列表失败:", error);
    ElMessage.error("获取收藏列表失败");
    favorites.value = [];
    total.value = 0;
  } finally {
    loading.value = false;
  }
};

// 处理搜索
const handleSearch = async () => {
  if (!searchForm.value.keyword) {
    currentFolder.value = "默认收藏夹";
    await getFavoriteList();
    return;
  }

  loading.value = true;
  try {
    const { data } = await findByUserId({
      page: searchForm.value.currentPage - 1,
      size: searchForm.value.pageSize,
      keyword: searchForm.value.keyword,
      sorts: "createdAt,desc"
    });
    if (data.status === 200) {
      favorites.value = data.data.content;
      total.value = data.data.totalElements;
      currentFolder.value = "";
      searchForm.value.isGlobalSearch = true;

      if (favorites.value.length === 0) {
        ElMessage.info("未找到匹配的文章");
      }
    }
  } catch (error) {
    console.error("搜索出错:", error);
    ElMessage.error("搜索失败");
  } finally {
    loading.value = false;
  }
};

// 修改重置搜索
const resetSearch = async () => {
  searchForm.value = {
    keyword: "",
    currentPage: 1,
    pageSize: searchForm.value.pageSize,
    isGlobalSearch: false
  };
  currentFolder.value = "默认收藏夹";
  await getFavoriteList();
};

// 取消收藏
const handleDelete = (item) => {
  if (!item?.favoriteId) {
    ElMessage.error("获取收藏记录失败，请刷新后重试");
    return;
  }

  ElMessageBox.confirm("确认取消收藏该文章吗？", "提示", {
    confirmButtonText: "确定",
    cancelButtonText: "取消",
    type: "warning",
  }).then(async () => {
    try {
      await deleteById(item.favoriteId);
      ElMessage.success("取消收藏成功");
      await getFavoriteList();
      await getFolderStats();
    } catch (error) {
      ElMessage.error("取消收藏失败");
    }
  });
};

// 查看文章
const handleView = (row) => {
  const postId = row?.post?.postId || row?.postId;
  if (!postId) {
    ElMessage.warning("文章信息不完整或已删除");
    return;
  }
  window.open(`/post/${postId}`, "_blank");
};

// 处理分页
const handleSizeChange = (val) => {
  searchForm.value.pageSize = val;
  searchForm.value.currentPage = 1; // 切换每页显示数量时重置为第一页
  getFavoriteList();
};

const handleCurrentChange = (val) => {
  searchForm.value.currentPage = val;
  getFavoriteList();
};

onMounted(async () => {
  await getFolders();

  // Add query parameter handling
  const folderFromQuery = route.query.folder;
  if (folderFromQuery && folders.value.includes(folderFromQuery)) {
    currentFolder.value = folderFromQuery;
  }

  await getFavoriteList();
  await getFolderStats();
});

/**
 * 收藏信息统计
 */
// 添加一个计算属性来统计收藏数量
const folderStats = ref({
  total: 0,
  byFolder: {},
});

// 获取收藏统计信息
const getFolderStats = async () => {
  try {
    const { data } = await findByUserId();
    const stats = {
      total: 0,
      byFolder: {},
    };

    // console.log("收藏数据:", data); // Log the data to check its structure
    const items = data.data.content;
    items.forEach((item) => {
      stats.total++;
      const folder = item.folderName || "默认收藏夹";
      stats.byFolder[folder] = (stats.byFolder[folder] || 0) + 1;
    });

    folderStats.value = stats;
  } catch (error) {
    console.error("获取收藏统计失败:", error);
  }
};
</script>

<template>
  <page-container title="我的收藏">
    <!-- 搜索栏 -->
    <div class="favorite-header">
      <div class="header-left">
        <h2 class="page-title">我收藏的文章</h2>
        <div class="folder-stats">
          <el-tag type="info" size="small">总收藏: {{ folderStats.total }}</el-tag>
        </div>
      </div>
      <div class="search-bar">
        <el-input v-model="searchForm.keyword" placeholder="搜索文章标题" clearable class="search-input"
          @keyup.enter="handleSearch">
          <template #prefix>
            <el-icon>
              <Search />
            </el-icon>
          </template>
        </el-input>
        <el-button v-if="searchForm.isGlobalSearch" @click="resetSearch" type="primary" plain>
          退出搜索
        </el-button>
      </div>
    </div>

    <!-- 在搜索栏和列表之间添加收藏夹选择器 -->
    <div class="folder-bar">
      <el-radio-group v-model="currentFolder" @change="handleFolderChange" :disabled="searchForm.isGlobalSearch">
        <el-radio-button v-for="folder in folders" :key="folder" :value="folder">
          {{ folder }}({{ folderStats.byFolder[folder] || 0 }})
        </el-radio-button>
      </el-radio-group>
    </div>

    <!-- 收藏列表 -->
    <div v-loading="loading" class="favorite-grid">
      <el-empty v-if="favorites.length === 0" description="暂无收藏" />

      <el-card v-for="item in filteredFavorites" :key="item?.post?.postId || item?.defineModel" class="favorite-card">
        <div class="card-content">
          <div class="card-header">
            <h3 class="card-title" @click="handleView(item?.post || item)">
              {{ item?.post?.title || item?.title || "文章已删除" }}
            </h3>
            <el-tag v-if="searchForm.isGlobalSearch" size="small" type="info">
              {{ item.folderName || currentFolder }}
            </el-tag>
          </div>

          <!-- 修改卡片操作区域 -->
          <div class="card-actions">
            <div class="action-left">
              <el-tag v-if="searchForm.isGlobalSearch" size="small" type="info">
                {{ item.folderName || currentFolder }}
              </el-tag>
            </div>
            <div class="action-right">
              <!-- 添加条件：只有当有多个文件夹且不是全局搜索时才显示下拉菜单 -->
              <el-dropdown v-if="!searchForm.isGlobalSearch && folders.length > 1" trigger="click">
                <el-button type="primary" link>
                  操作<el-icon class="el-icon--right">
                    <More />
                  </el-icon>
                </el-button>
                <template #dropdown>
                  <el-dropdown-menu>
                    <!-- 只显示不是当前文件夹的选项 -->
                    <el-dropdown-item v-for="folder in folders.filter(f => f !== currentFolder)" :key="folder"
                      @click="handleMove(item, folder)">
                      移动到 {{ folder }}
                    </el-dropdown-item>
                    <el-dropdown-item divided @click="handleDelete(item)">
                      <span style="color: var(--el-color-danger)">取消收藏</span>
                    </el-dropdown-item>
                  </el-dropdown-menu>
                </template>
              </el-dropdown>
              <!-- 当只有一个文件夹时，只显示删除按钮 -->
              <el-button v-else-if="!searchForm.isGlobalSearch" type="danger" link @click="handleDelete(item)">
                取消收藏
              </el-button>
            </div>
          </div>
          <p class="card-excerpt">{{ item?.post?.content || item?.content }}</p>

          <div class="card-meta">
            <div class="meta-stats">
              <span><el-icon>
                  <View />
                </el-icon>
                {{ item?.post?.views || item?.views || 0 }}</span>
              <span><el-icon>
                  <Star />
                </el-icon>
                {{ item?.post?.likes || item?.likes || 0 }}</span>
              <span><el-icon>
                  <ChatRound />
                </el-icon>
                {{ item?.post?.favorites || item.favorites || 0 }}</span>
            </div>
            <div class="meta-time">
              <el-icon>
                <Calendar />
              </el-icon>
              {{ formatDateTime(item?.post?.createdAt || item?.createdAt).split(" ")[0] }}
            </div>
          </div>
        </div>
      </el-card>
    </div>

    <!-- 分页 -->
    <div class="pagination-container">
      <el-pagination v-model:current-page="searchForm.currentPage" v-model:page-size="searchForm.pageSize"
        :total="total" :page-sizes="[12, 24, 36, 48]" layout="total, sizes, prev, pager, next, jumper"
        @size-change="handleSizeChange" @current-change="handleCurrentChange" />
    </div>
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

.favorite-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 2rem;
}

.page-title {
  font-size: 1.75rem;
  color: var(--el-text-color-primary);
  margin: 0;
}

.search-bar {
  display: flex;
  gap: 1rem;
  align-items: center;
}

.favorite-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
  gap: 1.5rem;
  margin-bottom: 2rem;
}

.card-cover {
  position: relative;
  height: 200px;
  overflow: hidden;
  cursor: pointer;
}

.card-cover img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform 0.3s ease;
}

.card-cover:hover img {
  transform: scale(1.05);
}

.card-category {
  position: absolute;
  top: 1rem;
  right: 1rem;
}

.card-title {
  margin: 0 0 0.75rem;
  font-size: 1.25rem;
  color: var(--el-text-color-primary);
  cursor: pointer;
  overflow: hidden;
  text-overflow: ellipsis;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
}

.card-title:hover {
  color: var(--el-color-primary);
}

.card-excerpt {
  color: var(--el-text-color-regular);
  font-size: 0.875rem;
  margin-bottom: 1rem;
  overflow: hidden;
  text-overflow: ellipsis;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
}

.card-meta {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 1rem;
  font-size: 0.875rem;
  color: var(--el-text-color-secondary);
}

.meta-stats {
  display: flex;
  gap: 1rem;
}

.meta-stats span {
  display: flex;
  align-items: center;
  gap: 0.25rem;
}

.meta-time {
  display: flex;
  align-items: center;
  gap: 0.25rem;
}

.card-actions {
  display: flex;
  justify-content: space-between;
  align-items: center;
  border-top: 1px solid var(--el-border-color-lighter);
  padding-top: 1rem;
  margin-top: 1rem;
}

.action-left {
  display: flex;
  align-items: center;
  gap: 0.5rem;
}

.action-right {
  display: flex;
  align-items: center;
  gap: 0.5rem;
}

.pagination-container {
  display: flex;
  justify-content: center;
  margin-top: 2rem;
}

/* 添加收藏夹选择器样式 */
.folder-bar {
  margin: 1rem 0 2rem;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 0.75rem;
}

.card-title {
  margin: 0;
  flex: 1;
  margin-right: 1rem;
}

.header-left {
  display: flex;
  align-items: center;
  gap: 1rem;
}

.folder-stats {
  display: flex;
  gap: 0.5rem;
  align-items: center;
}
</style>
