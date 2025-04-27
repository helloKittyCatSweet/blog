<script setup>
import { ref, onMounted } from "vue";
import { useRouter, useRoute } from "vue-router";
import { ElMessage } from "element-plus";
import { Star, Folder } from "@element-plus/icons-vue";
import { getFolderNames, getPostsByFolder } from "@/api/post/favorite";
import { BLOG_POST_DETAIL_PATH } from "@/constants/routes/blog";
import { USER_FAVORITE_MANAGE_PATH } from "@/constants/routes/user";

const router = useRouter();
const route = useRoute();
const loading = ref(false);
const folders = ref([]);
const favorites = ref({}); // 用对象存储每个文件夹的收藏文章

const getFavorites = async () => {
  loading.value = true;
  try {
    const response = await getFolderNames();
    if (response.data?.status === 200) {
      folders.value = response.data.data;
      if (!folders.value.includes("默认收藏夹")) {
        folders.value = ["默认收藏夹", ...folders.value];
      }

      // 获取每个文件夹的收藏
      for (const folder of folders.value) {
        try {
          const response = await getPostsByFolder(folder);
          if (response.data?.status === 200 && response.data.data?.content) {
            favorites.value[folder] = response.data.data.content.slice(0, 5); // 只显示前5篇文章
          } else {
            favorites.value[folder] = []; // 如果没有内容，设置为空数组
          }
        } catch (error) {
          console.error(`获取文件夹 ${folder} 的文章失败:`, error);
          favorites.value[folder] = []; // 设置为空数组，避免undefined
          if (error.response?.status === 401) {
            ElMessage.warning(`获取 ${folder} 的文章需要登录`);
          }
        }
      }
    }
  } catch (error) {
    ElMessage.error("获取收藏夹失败");
  } finally {
    loading.value = false;
  }
};

const goToPost = (postId) => {
  router.push({
    path: BLOG_POST_DETAIL_PATH.replace(":id", postId),
    query: { from: route.fullPath },
  });
};

const goToFavoriteManage = () => {
  router.push({
    path: USER_FAVORITE_MANAGE_PATH,
    query: { from: route.fullPath },
  });
};

const goToFolder = (folder) => {
  router.push({
    path: USER_FAVORITE_MANAGE_PATH,
    query: {
      folder: folder,
      from: route.fullPath
    }
  });
};

onMounted(() => {
  getFavorites();
});
</script>

<template>
  <el-dropdown trigger="hover">
    <span class="favorite-trigger" :style="{fontSize:'16px'}">
      收藏夹
      <el-icon class="el-icon--right"><Star /></el-icon>
    </span>

    <template #dropdown>
      <el-dropdown-menu v-loading="loading">
        <template v-if="Object.keys(favorites).length">
          <div v-for="(posts, folder) in favorites" :key="folder">
            <el-dropdown-item divided @click.native="goToFolder(folder)">
              <div class="folder-title">
                <el-icon><Folder /></el-icon>
                {{ folder }}
              </div>
            </el-dropdown-item>
            <template v-if="posts.length">
              <el-dropdown-item
                v-for="post in posts"
                :key="post.post.postId"
                :command="post.post.postId"
                @click.native="goToPost(post.post.postId)"
              >
                {{ post.post.title }}
              </el-dropdown-item>
            </template>
            <el-dropdown-item v-else disabled>暂无收藏</el-dropdown-item>
          </div>
        </template>
        <el-dropdown-item v-else disabled>暂无收藏</el-dropdown-item>
        <el-dropdown-item divided>
          <el-button type="primary" link @click.stop="goToFavoriteManage">
            管理收藏夹
          </el-button>
        </el-dropdown-item>
      </el-dropdown-menu>
    </template>
  </el-dropdown>
</template>

<style scoped>
.favorite-trigger {
  display: flex;
  align-items: center;
  gap: 4px;
  cursor: pointer;
  color: var(--el-text-color-regular);
}

.favorite-trigger:hover {
  color: var(--el-color-primary);
}

.folder-title {
  display: flex;
  align-items: center;
  gap: 8px;
  font-weight: bold;
  color: var(--el-text-color-primary);
}

:deep(.el-dropdown-menu__item) {
  min-width: 200px;
  padding: 8px 16px;
}
</style>
