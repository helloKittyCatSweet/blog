<script setup>
import { ref } from "vue";
import { ChatLineRound, Star, Pointer } from "@element-plus/icons-vue";
import { ElMessage } from "element-plus";
import {
  create as createUserActivity,
  deleteById as deleteUserActivity,
} from "@/api/user/userActivity.js";
import { useUserStore } from "@/stores/modules/user.js";
import {
  create as createFavorite,
  deleteById as deleteFavorite,
  getFolderNames,
  findByUserIdAndPostId,
} from "@/api/post/favorite.js";

const props = defineProps({
  postId: Number,
  title: String,
  isLiked: Boolean,
  isFavorited: Boolean,
  likeActivityId: Number,
  favoriteActivityId: Number,
});

const emit = defineEmits([
  "update:isLiked",
  "update:isFavorited",
  "update:likeActivityId",
  "update:favoriteActivityId",
  "refresh",
]);

const userStore = useUserStore();

/**
 * 点赞
 */
// 处理点赞
const handleLike = async () => {
  try {
    if (props.isLiked) {
      if (!props.likeActivityId) {
        ElMessage.error("点赞记录不存在");
        return;
      }
      console.log("取消点赞:likeActivityId", props.likeActivityId);
      await deleteUserActivity(props.likeActivityId);
      emit("update:isLiked", false);
      // 清除点赞活动ID
      emit("update:likeActivityId", null);
      ElMessage.success("取消点赞成功");
    } else {
      const response = await createUserActivity({
        userId: userStore.user.id,
        activityType: "LIKE",
        postId: props.postId,
        activityDetail: `${userStore.user.username}点赞了文章《${props.title}》`,
      });

      if (response.data.status === 200) {
        const activityId = response.data.data.activityId;
        emit("update:isLiked", true);
        // 更新点赞活动ID
        emit("update:likeActivityId", activityId);
        ElMessage.success("点赞成功");
      }
    }
    emit("refresh");
  } catch (error) {
    console.error("点赞操作失败:", error);
    ElMessage.error(props.isLiked ? "取消点赞失败" : "点赞失败");
  }
};

/**
 * 收藏夹
 */

const folders = ref([]);
const showFolderDialog = ref(false);
const selectedFolder = ref("默认收藏夹");
const newFolderName = ref("");

// 获取收藏夹列表
const getFolders = async () => {
  try {
    const response = await getFolderNames();
    if (response.data?.status === 200) {
      folders.value = response.data.data || [];  // 确保有数据
      if (!folders.value.includes("默认收藏夹")) {
        folders.value = ["默认收藏夹", ...folders.value];
      }
    } else {
      folders.value = ["默认收藏夹"];  // 如果获取失败，至少显示默认收藏夹
    }
  } catch (error) {
    console.error("获取收藏夹列表失败:", error);
    folders.value = ["默认收藏夹"];  // 发生错误时也显示默认收藏夹
    ElMessage.warning("获取收藏夹列表失败，将使用默认收藏夹");
  }
};

// 收藏文章
const handleFavorite = async () => {
  if (props.isFavorited) {
    try {
      // 先获取收藏记录
      console.log("获取收藏记录:userId", userStore.user.id, "postId", props.postId);
      const favoriteResponse = await findByUserIdAndPostId(
        userStore.user.id,
        props.postId
      );
      const favoriteId = favoriteResponse.data.data.favoriteId;

      // 删除收藏记录
      if (favoriteId) {
        await deleteFavorite(favoriteId);
        emit("update:isFavorited", false);
        // 清除收藏活动ID
        emit("update:favoriteActivityId", null);
        ElMessage.success("取消收藏成功");
        emit("refresh");
      } else {
        ElMessage.error("收藏记录不存在");
      }
    } catch (error) {
      console.error("取消收藏失败:", error);
      ElMessage.error("取消收藏失败");
    }
  } else {
    showFolderDialog.value = true;
    await getFolders();
  }
};

// 确认收藏
const confirmFavorite = async () => {
  try {
    // 检查文件夹名称
    if (newFolderName.value && newFolderName.value.trim().length === 0) {
      ElMessage.warning("收藏夹名称不能为空");
      return;
    }

    const folderName = newFolderName.value.trim() || selectedFolder.value;
    console.log("发送收藏请求，参数：", {
      userId: userStore.user.id,
      postId: props.postId,
      folderName: folderName,
    });

    // 添加到收藏夹
    const favoriteResponse = await createFavorite({
      userId: Number(userStore.user.id),
      postId: Number(props.postId),
      folderName: folderName,
    });

    if (favoriteResponse.data.status === 200) {
      // 创建收藏活动
      const activityResponse = await createUserActivity({
        userId: userStore.user.id,
        activityType: "FAVORITE",
        postId: props.postId,
        activityDetail: `${userStore.user.username}将文章《${props.title}》收藏到"${folderName}"`,
      });

      if (activityResponse.data.status === 200) {
        const activityId = activityResponse.data.data.activityId;
        emit("update:isFavorited", true);
        // 更新收藏活动ID
        emit("update:favoriteActivityId", activityId);
        ElMessage.success("收藏成功");
        emit("refresh");
        showFolderDialog.value = false;
        newFolderName.value = "";
        selectedFolder.value = "默认收藏夹";
      }
    }
  } catch (error) {
    console.error("收藏失败:", error.response?.data || error);
    ElMessage.error(error.response?.data?.message || "收藏失败");
  }
};
</script>

<template>
  <div class="post-interaction">
    <div class="interaction-buttons">
      <el-button
        :type="isLiked ? 'primary' : 'default'"
        :class="{ 'interaction-button': true, 'liked-button': isLiked }"
        @click="handleLike"
      >
        <el-icon class="interaction-icon"><Pointer /></el-icon>
        <span class="interaction-text">{{ isLiked ? "已点赞" : "点赞" }}</span>
      </el-button>
      <el-button
        :type="isFavorited ? 'primary' : 'default'"
        :class="{ 'interaction-button': true, 'favorited-button': isFavorited }"
        @click="handleFavorite"
      >
        <el-icon class="interaction-icon"><Star /></el-icon>
        <span class="interaction-text">{{ isFavorited ? "已收藏" : "收藏" }}</span>
      </el-button>
    </div>

    <!-- 收藏夹选择对话框 -->
    <el-dialog
      v-model="showFolderDialog"
      title="选择收藏夹"
      width="400px"
      :close-on-click-modal="false"
    >
      <el-form @keyup.enter="confirmFavorite">
        <el-form-item label="选择收藏夹">
          <el-select v-model="selectedFolder" style="width: 100%">
            <el-option
              v-for="folder in folders"
              :key="folder"
              :label="folder"
              :value="folder"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="或创建新收藏夹">
          <el-input v-model="newFolderName" placeholder="输入新收藏夹名称" clearable />
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="showFolderDialog = false">取消</el-button>
          <el-button type="primary" @click="confirmFavorite">确定</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<style scoped>
.interaction-buttons {
  display: flex;
  gap: 12px;
  justify-content: center;
}

.interaction-buttons :deep(.el-button--link) {
  height: auto;
  padding: 0;
  color: var(--el-text-color-regular);
}

.interaction-buttons :deep(.el-button--link:hover) {
  color: var(--el-color-primary);
}

.interaction-buttons :deep(.el-button--link.el-button--primary) {
  color: var(--el-color-primary);
}

.comment-input {
  margin-top: 16px;
}

.comment-actions {
  margin-top: 12px;
  display: flex;
  justify-content: flex-end;
  gap: 8px;
}

.liked-button {
  position: relative;
  overflow: hidden;
}

.liked-button::before {
  content: "";
  position: absolute;
  top: 50%;
  left: 50%;
  width: 120%;
  height: 120%;
  background: rgba(255, 255, 255, 0.2);
  transform: translate(-50%, -50%) scale(0);
  border-radius: 50%;
  transition: transform 0.3s ease-out;
}

.liked-button:hover::before {
  transform: translate(-50%, -50%) scale(1);
}

.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
}

.interaction-buttons {
  display: flex;
  gap: 16px;
  justify-content: center;
}

.interaction-button {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 8px 16px;
  border-radius: 20px;
  transition: all 0.3s ease;
}

.interaction-button:hover {
  transform: translateY(-2px);
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.interaction-icon {
  font-size: 18px;
}

.interaction-text {
  font-size: 14px;
  margin: 0 4px;
}

.interaction-count {
  font-size: 14px;
  font-weight: 500;
  min-width: 20px;
  text-align: center;
}

.liked-button,
.favorited-button {
  position: relative;
  overflow: hidden;
}

.liked-button::before,
.favorited-button::before {
  content: "";
  position: absolute;
  top: 50%;
  left: 50%;
  width: 120%;
  height: 120%;
  background: rgba(64, 158, 255, 0.1);
  transform: translate(-50%, -50%) scale(0);
  border-radius: 50%;
  transition: transform 0.3s ease-out;
}

.liked-button:hover::before,
.favorited-button:hover::before {
  transform: translate(-50%, -50%) scale(1);
}
</style>
