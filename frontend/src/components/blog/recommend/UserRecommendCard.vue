<script setup>
import { ref, onMounted} from "vue";
import { useRouter } from "vue-router";
import { ElMessage } from "element-plus";
import { User, Plus } from "@element-plus/icons-vue";
import { getRecommendList } from "@/api/user/userRecommend";
import { followUser } from "@/api/user/userFollow";

const router = useRouter();
const loading = ref(false);
const recommendedUsers = ref([]);

const getRecommendations = async () => {
  loading.value = true;
  try {
    const response = await getRecommendList(5);
    if (response.data.status === 200) {
      recommendedUsers.value = response.data.data;
    } else {
      ElMessage.error(response.data.message || "获取推荐用户失败");
    }
  } catch (error) {
    ElMessage.error("获取推荐用户失败");
  } finally {
    loading.value = false;
  }
};

const handleFollow = async (userId) => {
  try {
    await followUser(userId);
    ElMessage.success("关注成功");
    // 从推荐列表中移除
    recommendedUsers.value = recommendedUsers.value.filter(
      (user) => user.userId !== userId
    );
  } catch (error) {
    ElMessage.error("关注失败");
  }
};

const goToUserProfile = (userId) => {
  router.push(`/user/${userId}`);
};

onMounted(() => {
  getRecommendations();
});

const getUsernameFontSize = (username) => {
  return username.length > 10? "12px" : "14px";
}
</script>

<template>
  <el-card class="user-recommend-card" v-loading="loading">
    <template #header>
      <div class="card-header">
        <el-icon><User /></el-icon>
        <span>推荐关注</span>
      </div>
    </template>

    <div v-if="recommendedUsers.length" class="user-list">
      <div v-for="user in recommendedUsers" :key="user.userId" class="user-item">
        <el-avatar
          :src="user.avatar || null"
          @click="goToUserProfile(user.userId)"
          class="clickable"
        >
          {{ user.username.charAt(0).toUpperCase() }}
        </el-avatar>
        <div class="user-info">
          <div class="username clickable" @click="goToUserProfile(user.userId)" :style="{fontSize:getUsernameFontSize(user.nickname || user.username)}">
            {{ user.nickname || user.username }}
          </div>
          <div class="bio">{{ user.introduction || "这个用户很懒，什么都没写~" }}</div>
        </div>
        <el-button
          type="primary"
          :icon="Plus"
          circle
          @click="handleFollow(user.userId)"
        />
      </div>
    </div>
    <el-empty v-else description="暂无推荐" />
  </el-card>
</template>

<style scoped>
.user-recommend-card {
  margin-bottom: 20px;
}

.card-header {
  display: flex;
  align-items: center;
  gap: 8px;
  font-weight: bold;
}

.user-list {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.user-item {
  display: flex;
  align-items: center;
  gap: 12px;
}

.user-info {
  flex: 1;
  min-width: 0;
}

.username {
  font-weight: 500;
  margin-bottom: 4px;
}

.bio {
  font-size: 12px;
  color: var(--el-text-color-secondary);
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.clickable {
  cursor: pointer;
}

.clickable:hover {
  color: var(--el-color-primary);
}
</style>
