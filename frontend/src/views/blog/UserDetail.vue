<script setup>
import { ref, onMounted, computed, provide } from "vue";
import { useRoute, useRouter } from "vue-router";
import { ElMessage } from "element-plus";

import PageContainer from "@/components/PageContainer.vue";
import UserStatsCard from "@/components/blog/user/UserStatsCard.vue";
import UserActivityHeatmap from "@/components/blog/user/UserActivityHeatmap.vue";
import UserCategoryStats from "@/components/blog/user/UserCategoryStats.vue";

import { findUserById } from "@/api/user/user.js";
import { findByUserListNoPage } from "@/api/post/post.js";
import { GENDER_OPTIONS } from "@/constants/user-constants";
import { USER_MESSAGE_DETAIL_PATH } from "@/constants/routes/user.js";
import { useUserStore, useMessageStore, useSettingsStore } from "@/stores";
import {
  followUser,
  unfollowUser,
  getFollowingList,
  getFollowersList,
  checkFollowing,
} from "@/api/user/userFollow";

import {
  Clock,
  Message,
  View,
  Star,
  ChatRound,
  Calendar,
  DataLine,
  Histogram,
} from "@element-plus/icons-vue";

// 添加 ECharts 相关导入
import { use } from "echarts/core";
import { CanvasRenderer } from "echarts/renderers";
import { PieChart, HeatmapChart } from "echarts/charts";
import {
  GridComponent,
  TooltipComponent,
  LegendComponent,
  VisualMapComponent,
  CalendarComponent,
} from "echarts/components";
import VChart, { THEME_KEY } from "vue-echarts";

// 注册必要的组件
use([
  CanvasRenderer,
  PieChart,
  HeatmapChart,
  GridComponent,
  TooltipComponent,
  LegendComponent,
  VisualMapComponent,
  CalendarComponent,
]);

// 提供主题
provide(THEME_KEY, "light");

const route = useRoute();
const router = useRouter();
const userStore = useUserStore();
const settingsStore = useSettingsStore();

const userInfo = ref({
  username: "",
  nickname: "",
  avatar: null,
  gender: 2,
  birthday: "",
  address: "",
  introduction: "",
  tags: [],
  signature: "",
  createdAt: "",
});

/**
 * 加载用户文章
 */
const posts = ref([]);

const loadUserPosts = async (userId) => {
  try {
    const response = await findByUserListNoPage(userId);
    if (response.data?.status === 200) {
      posts.value = response.data.data.map((item) => ({
        postId: item.post.postId,
        title: item.post.title,
        content: item.post.content,
        abstractContent: item.post.abstractContent,
        createdAt: item.post.createdAt,
        views: item.post.views,
        likes: item.post.likes,
        comments: item.comments?.length || 0,
        category: item.category?.name,
        tags: item.tags || [],
      }));
    }
  } catch (error) {
    ElMessage.error("加载用户文章失败");
  }
};

const loadUserInfo = async () => {
  try {
    const userId = route.params.id;
    const { data } = await findUserById(userId);
    if (data.status === 200) {
      Object.assign(userInfo.value, {
        ...data.data,
        tags: data.data.tags || [],
      });
      // 加载用户文章
      await loadUserPosts(userId);
      // 加载关注数据和检查关注状态
      await Promise.all([loadFollowData(userId), checkFollowStatus(userId)]);
    }
  } catch (error) {
    ElMessage.error("加载用户数据失败");
  }
};

// 添加检查关注状态的函数
const checkFollowStatus = async (userId) => {
  try {
    // 使用新的 API 检查关注状态
    const response = await checkFollowing(userStore.user?.id, userId);
    if (response.data?.status === 200) {
      isFollowing.value = response.data.data;
    }
  } catch (error) {
    console.error("检查关注状态失败:", error);
  }
};

const messageStore = useMessageStore();

// 跳转到聊天
const goToChat = () => {
  // 设置当前联系人信息
  messageStore.setCurrentReceiver({
    receiverId: route.params.id,
    receiverName: userInfo.value.nickname || userInfo.value.username,
    receiverAvatar: userInfo.value.avatar,
  });

  router.push({
    path: USER_MESSAGE_DETAIL_PATH,
    query: {
      receiverId: route.params.id,
    },
  });
};

onMounted(() => {
  loadUserInfo();
});

// 文章筛选
const timelineFilter = ref("all");
const sortBy = ref("newest");

// 关注状态
const isFollowing = ref(false);
const followersCount = ref(0);
const followingCount = ref(0);

// 处理关注/取消关注
const handleFollow = async () => {
  try {
    if (isFollowing.value) {
      await unfollowUser(route.params.id);
      followersCount.value--;
    } else {
      await followUser(route.params.id);
      followersCount.value++;
    }
    isFollowing.value = !isFollowing.value;
    ElMessage.success(isFollowing.value ? "关注成功" : "已取消关注");
  } catch (error) {
    ElMessage.error("操作失败");
  }
};

// 筛选文章
const filteredPosts = computed(() => {
  let filtered = [...posts.value];

  // 时间筛选
  const now = new Date();
  if (timelineFilter.value !== "all") {
    filtered = filtered.filter((post) => {
      const postDate = new Date(post.createdAt);
      switch (timelineFilter.value) {
        case "week":
          return now - postDate <= 7 * 24 * 60 * 60 * 1000;
        case "month":
          return now - postDate <= 30 * 24 * 60 * 60 * 1000;
        case "year":
          return now - postDate <= 365 * 24 * 60 * 60 * 1000;
        default:
          return true;
      }
    });
  }

  // 排序
  filtered.sort((a, b) => {
    switch (sortBy.value) {
      case "views":
        return (b.views || 0) - (a.views || 0);
      case "likes":
        return (b.likes || 0) - (a.likes || 0);
      case "comments":
        return (b.comments || 0) - (a.comments || 0);
      default:
        return new Date(b.createdAt) - new Date(a.createdAt);
    }
  });

  return filtered;
});

/**
 * 粉丝信息
 */
// 加载关注数据
const loadFollowData = async (userId) => {
  try {
    const [followingRes, followersRes] = await Promise.all([
      getFollowingList(userId),
      getFollowersList(userId),
    ]);

    if (followingRes.data?.status === 200) {
      followingCount.value = followingRes.data.data.length;
    }

    if (followersRes.data?.status === 200) {
      followersCount.value = followersRes.data.data.totalElements;
    }
  } catch (error) {
    console.error("加载关注数据失败:", error);
  }
};

/**
 * 粉丝和关注信息
 */
const followersDialogVisible = ref(false);
const followingDialogVisible = ref(false);
const followersList = ref([]);
const followingList = ref([]);

// 添加加载粉丝列表的方法
const loadFollowersList = async (userId) => {
  try {
    const response = await getFollowersList(userId);
    if (response.data?.status === 200) {
      followersList.value = response.data.data.content;
    }
  } catch (error) {
    console.error("加载粉丝列表失败:", error);
    ElMessage.error("加载粉丝列表失败");
  }
};

// 添加加载关注列表的方法
const loadFollowingList = async (userId) => {
  try {
    const response = await getFollowingList(userId);
    if (response.data?.status === 200) {
      followingList.value = response.data.data;
    }
  } catch (error) {
    console.error("加载关注列表失败:", error);
    ElMessage.error("加载关注列表失败");
  }
};

// 处理查看粉丝列表
const handleViewFollowers = async () => {
  await loadFollowersList(route.params.id);
  followersDialogVisible.value = true;
};

// 处理查看关注列表
const handleViewFollowing = async () => {
  await loadFollowingList(route.params.id);
  followingDialogVisible.value = true;
};

// 添加处理用户跳转的方法
const handleUserClick = async (userId) => {
  // 关闭对话框
  followersDialogVisible.value = false;
  followingDialogVisible.value = false;

  // 跳转到用户页面
  await router.push(`/user/${userId}`);

  // 重新加载数据
  await loadUserInfo();
};
</script>

<template>
  <page-container>
    <el-row>
      <!-- 用户信息卡片 -->
      <el-col :span="24">
        <el-card class="user-detail-card" shadow="never">
          <div class="user-header">
            <div class="user-avatar">
              <el-avatar :size="120" :src="userInfo.avatar" fit="cover">
                {{ userInfo.nickname?.charAt(0) || userInfo.username?.charAt(0) }}
              </el-avatar>
            </div>
            <div class="user-basic-info">
              <div class="name-and-actions">
                <div class="user-names">
                  <h2 class="nickname">{{ userInfo.nickname || userInfo.username }}</h2>
                  <span class="username" v-if="userInfo.nickname"
                    >@{{ userInfo.username }}</span
                  >
                </div>
                <el-button
                  v-if="userStore.user?.id !== route.params.id"
                  type="primary"
                  @click="goToChat"
                >
                  <el-icon><Message /></el-icon>
                  发消息
                </el-button>
                <div class="user-social">
                  <el-button-group>
                    <el-button @click="handleViewFollowers"
                      >粉丝 {{ followersCount }}</el-button
                    >
                    <el-button type="primary" @click="handleViewFollowing"
                      >关注 {{ followingCount }}</el-button
                    >
                  </el-button-group>
                  <el-button
                    v-if="userStore.user?.id !== route.params.id"
                    :type="isFollowing ? 'success' : 'primary'"
                    @click="handleFollow"
                  >
                    {{ isFollowing ? "已关注" : "关注" }}
                  </el-button>
                </div>
              </div>

              <p class="introduction">
                {{ userInfo.introduction || "这个用户很懒，还没有写简介" }}
              </p>

              <!-- 添加社交账号展示 -->
              <div class="social-accounts" v-if="settingsStore.settings">
                <a
                  v-if="settingsStore.settings.githubAccount"
                  :href="`https://github.com/${settingsStore.settings.githubAccount}`"
                  target="_blank"
                  class="social-link"
                >
                  <font-awesome-icon :icon="['fab', 'github']" />
                  {{ settingsStore.settings.githubAccount }}
                </a>
                <a
                  v-if="settingsStore.settings.csdnAccount"
                  :href="`https://blog.csdn.net/${settingsStore.settings.csdnAccount}`"
                  target="_blank"
                  class="social-link"
                >
                  <img src="@/assets/icons/csdn.svg" class="social-icon" alt="CSDN" />
                  {{ settingsStore.settings.csdnAccount }}
                </a>
                <a
                  v-if="settingsStore.settings.bilibiliAccount"
                  :href="`https://space.bilibili.com/${settingsStore.settings.bilibiliAccount}`"
                  target="_blank"
                  class="social-link"
                >
                  <img
                    src="@/assets/icons/bilibili.svg"
                    class="social-icon"
                    alt="bilibili"
                  />
                  {{ settingsStore.settings.bilibiliAccount }}
                </a>
              </div>

              <div class="tags-container">
                <el-tag
                  v-for="tag in userInfo.tags"
                  :key="tag"
                  class="user-tag"
                  effect="plain"
                >
                  {{ tag }}
                </el-tag>
              </div>
            </div>
          </div>

          <el-divider />

          <div class="user-details">
            <el-descriptions :column="2" border>
              <el-descriptions-item label="邮箱">
                {{ userInfo.email || "未设置" }}
              </el-descriptions-item>
              <el-descriptions-item label="注册时间">
                {{ userInfo.createdAt || "-" }}
              </el-descriptions-item>
              <el-descriptions-item label="最后登录地点">
                {{ userInfo.lastLoginLocation || "未知" }}
              </el-descriptions-item>
              <el-descriptions-item label="个性签名">
                <div v-if="userInfo.signature" class="signature-wrapper">
                  <el-image
                    :src="userInfo.signature"
                    fit="contain"
                    class="signature-image"
                  />
                </div>
                <span v-else class="text-muted">未设置</span>
              </el-descriptions-item>
            </el-descriptions>
          </div>

          <!-- 统计数据 -->
          <user-stats-card :posts="posts" />
        </el-card>
        <!-- 添加粉丝列表对话框 -->
        <el-dialog v-model="followersDialogVisible" title="粉丝列表" width="50%">
          <el-scrollbar height="400px">
            <el-empty v-if="followersList.length === 0" description="暂无粉丝" />
            <div v-else class="user-list">
              <div v-for="user in followersList" :key="user.userId" class="user-item">
                <div class="user-info">
                  <el-avatar :size="40" :src="user.avatar">
                    {{ user.nickname?.charAt(0) || user.username?.charAt(0) }}
                  </el-avatar>
                  <div class="user-detail">
                    <div class="nickname">
                      {{ user.nickname || user.username }}
                    </div>
                    <div class="username" v-if="user.nickname">@{{ user.username }}</div>
                  </div>
                </div>
                <el-button
                  v-if="userStore.user?.id !== user.userId"
                  type="primary"
                  size="small"
                  @click="handleUserClick(user.userId)"
                >
                  查看主页
                </el-button>
              </div>
            </div>
          </el-scrollbar>
        </el-dialog>

        <!-- 添加关注列表对话框 -->
        <el-dialog v-model="followingDialogVisible" title="关注列表" width="50%">
          <el-scrollbar height="400px">
            <el-empty v-if="followingList.length === 0" description="暂无关注" />
            <div v-else class="user-list">
              <div v-for="user in followingList" :key="user.userId" class="user-item">
                <div class="user-info">
                  <el-avatar :size="40" :src="user.avatar">
                    {{ user.nickname?.charAt(0) || user.username?.charAt(0) }}
                  </el-avatar>
                  <div class="user-detail">
                    <div class="nickname">
                      {{ user.nickname || user.username }}
                    </div>
                    <div class="username" v-if="user.nickname">@{{ user.username }}</div>
                  </div>
                </div>
                <el-button
                  v-if="userStore.user?.id !== user.userId"
                  type="primary"
                  size="small"
                  @click="handleUserClick(user.userId)"
                >
                  查看主页
                </el-button>
              </div>
            </div>
          </el-scrollbar>
        </el-dialog>
      </el-col>

      <!-- 活跃度热力图 -->
      <el-col :span="24">
        <user-activity-heatmap :posts="posts" />
      </el-col>

      <!-- 分类统计 -->
      <el-col :span="24">
        <user-category-stats :posts="posts" />
      </el-col>

      <!-- 文章时间线 -->
      <el-col :span="24" class="timeline-section">
        <el-card shadow="never">
          <template #header>
            <div class="card-header">
              <div class="header-content">
                <h3>发布的文章</h3>
                <div class="timeline-filters">
                  <el-select v-model="timelineFilter" placeholder="时间筛选">
                    <el-option label="全部文章" value="all" />
                    <el-option label="最近一周" value="week" />
                    <el-option label="最近一月" value="month" />
                    <el-option label="最近一年" value="year" />
                  </el-select>
                  <el-select v-model="sortBy" placeholder="排序方式">
                    <el-option label="最新发布" value="newest" />
                    <el-option label="最多浏览" value="views" />
                    <el-option label="最多点赞" value="likes" />
                    <el-option label="最多评论" value="comments" />
                  </el-select>
                </div>
              </div>
            </div>
          </template>

          <el-timeline v-if="posts.length > 0">
            <el-timeline-item
              v-for="post in filteredPosts"
              :key="post.postId"
              :timestamp="post.createdAt"
              placement="top"
            >
              <el-card class="timeline-post-card">
                <h4 class="post-title" @click="router.push(`/post/${post.postId}`)">
                  {{ post.title }}
                </h4>
                <p class="post-summary">
                  {{
                    post.abstractContent ||
                    (post.content && post.content.substring(0, 100)) ||
                    "暂无摘要"
                  }}
                </p>
                <div class="post-meta">
                  <span class="views" title="浏览量">
                    <el-icon><View /></el-icon>
                    {{ post.views || 0 }}
                  </span>
                  <span class="likes" title="点赞数">
                    <el-icon><Star /></el-icon>
                    {{ post.likes || 0 }}
                  </span>
                  <span class="comments" title="评论数">
                    <el-icon><ChatRound /></el-icon>
                    {{ post.comments }}
                  </span>
                </div>
              </el-card>
            </el-timeline-item>
          </el-timeline>
          <el-empty v-else description="暂无文章" />
        </el-card>
      </el-col>
    </el-row>
  </page-container>
</template>

<style lang="scss" scoped>
.user-detail-card {
  margin-bottom: 20px;

  .user-header {
    display: flex;
    gap: 24px;
    padding: 20px 0;

    .user-basic-info {
      flex: 1;

      .name-and-actions {
        display: flex;
        align-items: center;
        gap: 16px;
        margin-bottom: 12px;

        .nickname {
          margin: 0;
          font-size: 24px;
          font-weight: 600;
          color: var(--el-text-color-primary);
        }
      }
    }
  }

  .user-names {
    display: flex;
    flex-direction: column;
    gap: 4px;

    .nickname {
      margin: 0;
      font-size: 24px;
      font-weight: 600;
      color: var(--el-text-color-primary);
    }

    .username {
      font-size: 14px;
      color: var(--el-text-color-secondary);
    }
  }
}

.timeline-section {
  .card-header {
    h3 {
      margin: 0;
      font-size: 18px;
      font-weight: 500;
    }
  }

  .timeline-post-card {
    .post-title {
      margin: 0 0 12px;
      font-size: 16px;
      color: var(--el-color-primary);
      cursor: pointer;

      &:hover {
        color: var(--el-color-primary-light-3);
      }
    }

    .post-summary {
      margin: 0 0 12px;
      font-size: 14px;
      color: var(--el-text-color-regular);
      line-height: 1.5;
    }

    .post-meta {
      display: flex;
      align-items: center;
      gap: 16px;
      color: var(--el-text-color-secondary);
      font-size: 14px;

      .views {
        display: flex;
        align-items: center;
        gap: 4px;
      }
    }
  }
}

.tags-container {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  margin-top: 12px;
}

.introduction {
  margin: 12px 0;
  color: var(--el-text-color-regular);
  font-size: 14px;
  line-height: 1.6;
}

.user-stats {
  margin-top: 24px;
  padding: 20px 0;
  border-top: 1px solid var(--el-border-color-lighter);
}

.user-social {
  display: flex;
  gap: 16px;
  align-items: center;
}

.activity-card,
.category-stats-card {
  margin-bottom: 20px;
}

.header-content {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.timeline-filters {
  display: flex;
  gap: 12px;
}

.chart-container {
  width: 100%;
  height: 300px;
  padding: 20px;
  box-sizing: border-box;
}

.heatmap-container {
  width: 100%;
  height: 200px;
  padding: 20px;
  background-color: var(--el-bg-color-page);
  border-radius: 4px;
  box-sizing: border-box;
}

.card-header {
  h3 {
    margin: 0;
    font-size: 18px;
    font-weight: 500;
  }
}

.chart {
  width: 100%;
  height: 100%;
}

.chart-container {
  height: 300px;
  padding: 20px;
}

.heatmap-container {
  height: 200px;
  padding: 20px;
  background-color: var(--el-bg-color-page);
  border-radius: 4px;
}

.user-list {
  .user-item {
    display: flex;
    align-items: center;
    justify-content: space-between;
    padding: 12px;
    border-bottom: 1px solid var(--el-border-color-lighter);

    &:last-child {
      border-bottom: none;
    }

    .user-info {
      display: flex;
      align-items: center;
      gap: 12px;

      .user-detail {
        .nickname {
          font-size: 14px;
          font-weight: 500;
          color: var(--el-text-color-primary);
        }

        .username {
          font-size: 12px;
          color: var(--el-text-color-secondary);
        }
      }
    }
  }
}

.social-accounts {
  display: flex;
  gap: 16px;
  margin: 12px 0;
  flex-wrap: wrap;

  .social-link {
    display: flex;
    align-items: center;
    gap: 6px;
    color: var(--el-text-color-regular);
    text-decoration: none;
    font-size: 14px;
    padding: 6px 12px;
    border-radius: 4px;
    background-color: var(--el-fill-color-lighter);
    transition: all 0.3s ease;

    &:hover {
      color: var(--el-color-primary);
      background-color: var(--el-fill-color-light);
    }

    .social-icon {
      width: 16px;
      height: 16px;
    }
  }
}
</style>
