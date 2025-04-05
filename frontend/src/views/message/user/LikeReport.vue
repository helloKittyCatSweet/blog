<script setup>
import { ref, onMounted } from "vue";
import { ElMessage, ElMessageBox } from "element-plus";
import { Delete } from "@element-plus/icons-vue";
import PageContainer from "@/components/PageContainer.vue";
import { getInteractions, deleteById, findByType } from "@/api/user/userActivity.js";

const loading = ref(false);
// 过滤类型
const filterType = ref("");
const activities = ref([]);

// 获取用户活动记录
const getLikeReport = async () => {
  loading.value = true;
  try {
    const res = filterType.value
      ? await findByType(filterType.value)
      : await getInteractions();
    if (res.data.status === 200) {
      activities.value = res.data.data;
      console.log("activities:", res.data.data);
    }
  } catch (error) {
    ElMessage.error("获取互动记录失败");
  }
  loading.value = false;
};

// 处理类型切换
const handleTypeChange = (type) => {
  filterType.value = type;
  getLikeReport();
};

// 获取活动类型配置
const getActivityConfig = (type) => {
  const map = {
    LIKE: {
      type: "success",
      label: "点赞",
      icon: "Thumb",
      color: "#67C23A",
    },
    COMMENT: {
      type: "info",
      label: "评论",
      icon: "ChatDotRound",
      color: "#909399",
    },
    FAVORITE: {
      type: "warning",
      label: "收藏",
      icon: "Star",
      color: "#E6A23C",
    },
  };
  return (
    map[type] || {
      type: "info",
      label: type || "未知类型",
      icon: "Info",
      color: "#909399",
    }
  );
};

// 删除互动记录
const handleDelete = (activity) => {
  ElMessageBox.confirm("确认删除该互动记录吗？", "提示", {
    confirmButtonText: "确定",
    cancelButtonText: "取消",
    type: "warning",
  }).then(async () => {
    try {
      const response = await deleteById(activity.activityId);
      if (response.data.status === 200) {
        ElMessage.success("删除成功");
      } else {
        ElMessage.error("删除失败");
      }
      getLikeReport();
    } catch (error) {
      ElMessage.error("删除失败");
    }
  });
};

onMounted(() => {
  getLikeReport();
});
</script>

<template>
  <page-container title="互动记录">
    <el-card v-loading="loading" class="activity-card">
      <template #header>
        <div class="card-header">
          <span>最近互动</span>
          <el-radio-group v-model="filterType" size="small" @change="handleTypeChange">
            <el-radio-button value="">全部</el-radio-button>
            <el-radio-button value="LIKE">点赞</el-radio-button>
            <el-radio-button value="COMMENT">评论</el-radio-button>
            <el-radio-button value="FAVORITE">收藏</el-radio-button>
          </el-radio-group>
        </div>
      </template>

      <div v-if="activities.length === 0" class="empty-state">
        <el-empty description="暂无互动记录" />
      </div>

      <el-timeline v-else>
        <el-timeline-item
          v-for="activity in activities"
          :key="activity.postId"
          :type="getActivityConfig(activity.activityType.toLowerCase()).type"
          :color="getActivityConfig(activity.activityType.toLowerCase()).color"
          :timestamp="activity.createdAt"
          placement="top"
        >
          <div class="activity-item">
            <div class="activity-header">
              <el-tag
                :type="getActivityConfig(activity.activityType.toLowerCase()).type"
                size="small"
              >
                {{ getActivityConfig(activity.activityType.toLowerCase()).label }}
              </el-tag>
              <el-button
                type="danger"
                :icon="Delete"
                circle
                size="small"
                @click="handleDelete(activity)"
              />
            </div>

            <div class="activity-content">
              <div class="post-title">
                <el-link
                  type="primary"
                  @click="window.open(`/post/${activity.postId}`, '_blank')"
                >
                  {{ activity.postTitle || "文章已删除" }}
                </el-link>
              </div>
              <div class="activity-detail" v-if="activity.activityDetail">
                <span class="username">{{ activity.username }}</span>
                {{ activity.activityDetail }}
              </div>
            </div>
          </div>
        </el-timeline-item>
      </el-timeline>
    </el-card>
  </page-container>
</template>

<style scoped>
.activity-card {
  margin-bottom: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.activity-item {
  padding: 8px;
  background-color: var(--el-bg-color-page);
  border-radius: 4px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.05);
}

.activity-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 8px;
}

.activity-content {
  padding: 8px;
  background-color: var(--el-bg-color);
  border-radius: 4px;
}

.post-title {
  font-size: 14px;
  margin-bottom: 8px;
}

.activity-detail {
  font-size: 13px;
  color: var(--el-text-color-secondary);
  line-height: 1.5;
}

.empty-state {
  padding: 40px 0;
}

.username {
  color: var(--el-color-primary);
  margin-right: 8px;
  font-weight: bold;
}
</style>
