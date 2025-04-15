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

const emit = defineEmits(["update:isLiked", "update:isFavorited", "refresh"]);

const userStore = useUserStore();

/**
 * 点赞
 */
// 处理点赞
const handleLike = async () => {
  try {
    if (props.isLiked) {
      console.log("取消点赞:activityId", props.activityId);
      await deleteUserActivity(props.activityId);
      emit("update:isLiked", false);
      ElMessage.success("取消点赞成功");
    } else {
      await createUserActivity({
        userId: userStore.user.id,
        activityType: "LIKE",
        postId: props.postId,
        activityDetail: `${userStore.user.username}点赞了文章《${props.title}》`,
        isDeleted: false,
      });
      emit("update:isLiked", true);
      ElMessage.success("点赞成功");
    }
    emit("refresh");
  } catch (error) {
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
    const { data } = await getFolderNames();
    folders.value = data;
    if (!folders.value.includes("默认收藏夹")) {
      folders.value = ["默认收藏夹", ...folders.value];
    }
  } catch (error) {
    console.error("获取收藏夹列表失败:", error);
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
      }
      // 删除收藏活动
      await deleteUserActivity(props.favoriteActivityId);

      emit("update:isFavorited", false);
      ElMessage.success("取消收藏成功");
      emit("refresh");
    } catch (error) {
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
      await createUserActivity({
        userId: userStore.user.id,
        activityType: "FAVORITE",
        postId: props.postId,
        activityDetail: `${userStore.user.username}将文章《${props.title}》收藏到"${folderName}"`,
      });

      emit("update:isFavorited", true);
      ElMessage.success("收藏成功");
      emit("refresh");
      showFolderDialog.value = false;
      newFolderName.value = "";
      selectedFolder.value = "默认收藏夹";
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
        :class="{ 'liked-button': isLiked }"
        @click="handleLike"
      >
        <el-icon><Pointer /></el-icon>
        {{ isLiked ? "已点赞" : "点赞" }}
      </el-button>
      <el-button :type="isFavorited ? 'warning' : 'default'" @click="handleFavorite">
        <el-icon><Star /></el-icon>
        {{ isFavorited ? "已收藏" : "收藏" }}
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
        <el-button @click="showFolderDialog = false">取消</el-button>
        <el-button type="primary" @click="confirmFavorite">确定</el-button>
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
</style>
