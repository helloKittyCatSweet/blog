<script setup>
import { ref, onMounted, onUnmounted, watch, computed } from "vue";
import { ElMessage, ElMessageBox } from "element-plus";
import {
  create as createComment,
  update,
  deleteById,
  findByPostId,
  likeComment,
} from "@/api/post/comment.js";
import {
  create as createUserActivity,
  deleteById as deleteUserActivity,
  findCommentExplicit,
} from "@/api/user/userActivity.js";
import { useUserStore } from "@/stores/modules/user.js";

import CommentItem from "./CommentItem.vue";

// 添加 emoji 相关导入
import EmojiPicker from "vue3-emoji-picker";
import "vue3-emoji-picker/css";

import { Pointer } from "@element-plus/icons-vue";

const props = defineProps({
  postId: {
    type: Number,
    required: true,
    default: 0,
  },
  title: {
    type: String,
    required: true,
    default: "",
  },
  userId: {
    type: Number,
    required: true,
    default: 0,
  },
  displayLimit: {
    type: Number,
    required: false,
    default: 5,
  },
  showViewMore: {
    type: Boolean,
    required: false,
    default: true,
  },
});

const emit = defineEmits(["refresh"]);
const userStore = useUserStore();

// 添加评论列表状态
const comments = ref([]);

// 添加评论列表获取函数
const fetchComments = async () => {
  try {
    const response = await findByPostId(props.postId);
    if (response.data.status === 200) {
      const commentsData = response.data.data;
      const processedComments = await Promise.all(commentsData.map(processCommentLikes));
      comments.value = sortComments(processedComments);
    }
  } catch (error) {
    console.error("获取评论列表失败:", error);
    ElMessage.error("获取评论列表失败");
  }
};

/**
 * 编辑
 */

const editingComment = ref(null);
const editContent = ref("");

// 添加编辑评论函数
const startEdit = (comment) => {
  editingComment.value = comment.commentId;
  editContent.value = comment.content;
};

const cancelEdit = () => {
  editingComment.value = null;
  editContent.value = "";
};

const saveEdit = async (comment) => {
  if (!editContent.value.trim()) {
    ElMessage.warning("评论内容不能为空");
    return;
  }
  console.log("更新的评论内容：", editContent.value.trim());

  try {
    await update({
      commentId: comment.commentId,
      content: editContent.value.trim(),
    });
    ElMessage.success("修改成功");
    editingComment.value = null;
    await fetchComments();
  } catch (error) {
    ElMessage.error("修改失败");
  }
};

// 添加删除评论函数
const handleDelete = async (commentId) => {
  try {
    await ElMessageBox.confirm("确定要删除这条评论吗？", "提示", {
      confirmButtonText: "确定",
      cancelButtonText: "取消",
      type: "warning",
    });

    await deleteById(commentId);
    ElMessage.success("删除成功");
    await fetchComments();
  } catch (error) {
    if (error !== "cancel") {
      ElMessage.error("删除失败");
    }
  }
};

const commentContent = ref("");

// 提交评论
const submitComment = async () => {
  if (!commentContent.value.trim()) {
    ElMessage.warning("评论内容不能为空");
    return;
  }

  try {
    // 创建评论
    const response = await createComment({
      userId: userStore.user.id,
      postId: props.postId,
      content: commentContent.value.trim(),
      parentCommentId: 0,
    });

    // 创建评论活动
    await createUserActivity({
      userId: userStore.user.id,
      activityType: "COMMENT",
      postId: props.postId,
      commentId: response.data.data.commentId,
      activityDetail: `${userStore.user.username}评论了文章《${props.title}》`,
    });

    ElMessage.success("评论成功");
    commentContent.value = "";
    await fetchComments();
  } catch (error) {
    console.error("评论失败:", error);
    ElMessage.error("评论失败");
  }
};

/**
 * emoji
 */
// 添加 emoji 相关状态
const showEmojiPicker = ref(false);

// 添加 emoji 选择处理函数
const onEmojiSelect = (emoji) => {
  commentContent.value += emoji.i;
  showEmojiPicker.value = false;
};

const emojiPickerRef = ref(null);

// 添加点击外部关闭的处理函数
const handleClickOutside = (event) => {
  if (
    emojiPickerRef.value &&
    !emojiPickerRef.value.contains(event.target) &&
    !event.target.closest(".emoji-trigger-btn")
  ) {
    showEmojiPicker.value = false;
  }
};

// 添加和移除事件监听
onMounted(() => {
  fetchComments();
  document.addEventListener("click", handleClickOutside);
});

onUnmounted(() => {
  document.removeEventListener("click", handleClickOutside);
});

// 判断是否可以管理评论
const canManageComment = (comment) => {
  // console.log("canManageComment:prop.userId", props.userId);
  return (
    userStore.isLoggedIn &&
    (comment.userId === userStore.user.id || // 评论作者
      props.userId === userStore.user.id) // 文章作者
  );
};

/**
 * 回复评论
 */
// 添加回复相关状态
const replyingTo = ref(null);
const replyContent = ref("");

// 添加回复处理函数
const startReply = (comment) => {
  replyingTo.value = comment;
  replyContent.value = "";
};

const cancelReply = () => {
  replyingTo.value = null;
  replyContent.value = "";
};

const submitReply = async (parentComment) => {
  if (!replyContent.value.trim()) {
    ElMessage.warning("回复内容不能为空");
    return;
  }

  try {
    const response = await createComment({
      userId: userStore.user.id,
      postId: props.postId,
      content: replyContent.value.trim(),
      parentCommentId: parentComment.commentId,
    });

    // 创建回复活动
    await createUserActivity({
      userId: userStore.user.id,
      activityType: "REPLY",
      postId: props.postId,
      commentId: response.data.data.commentId,
      activityDetail: `${userStore.user.username}回复了${parentComment.username}的评论`,
    });

    ElMessage.success("回复成功");
    replyingTo.value = null;
    replyContent.value = "";
    await fetchComments();
  } catch (error) {
    console.error("回复失败:", error);
    ElMessage.error("回复失败");
  }
};

// 添加点赞处理函数
const toggleLike = async (comment) => {
  if (!userStore.isLoggedIn) {
    ElMessage.warning("请先登录");
    return;
  }

  try {
    // 先获取当前评论的点赞状态
    const likeResponse = await findCommentExplicit(
      userStore.user.id,
      comment.commentId,
      "LIKE_COMMENT"
    );
    // console.log("likeResponse", likeResponse);
    const isLiked =
      likeResponse.data.status === 200 &&
      likeResponse.data.data &&
      likeResponse.data.data.activityId !== null;

    if (isLiked) {
      await likeComment(comment.commentId, -1);
      comment.likes = Math.max(0, comment.likes - 1);
      // 添加取消点赞活动
      const activityId = likeResponse.data.data.activityId;
      await deleteUserActivity(Number(activityId));
      ElMessage.success("已取消评论点赞");
    } else {
      await likeComment(comment.commentId, 1);
      comment.likes = (comment.likes || 0) + 1;
      // 添加点赞活动
      await createUserActivity({
        userId: userStore.user.id,
        activityType: "LIKE_COMMENT",
        postId: props.postId,
        commentId: comment.commentId,
        activityDetail: `${userStore.user.username}点赞了${comment.username}在${props.title}的评论${comment.content}`,
      });
      ElMessage.success("点赞评论成功");
    }
    comment.isLiked = !isLiked;
  } catch (error) {
    console.error("点赞操作失败:", error);
    ElMessage.error("操作失败");
  }
};

/**
 * 排序
 */
const sortType = ref("time");

const sortComments = (commentsToSort, type = sortType.value) => {
  if (!commentsToSort || !Array.isArray(commentsToSort)) return [];

  // 对当前层级的评论进行排序
  const sorted = [...commentsToSort].sort((a, b) => {
    if (type === "time") {
      return (
        new Date(b.createdAt || b.createdTime) - new Date(a.createdAt || a.createdTime)
      );
    } else {
      const likesA = parseInt(a.likes || 0);
      const likesB = parseInt(b.likes || 0);
      if (likesA === likesB) {
        return (
          new Date(b.createdAt || b.createdTime) - new Date(a.createdAt || a.createdTime)
        );
      }
      return likesB - likesA;
    }
  });

  // 递归处理子评论，子评论始终按时间排序
  return sorted.map((comment) => ({
    ...comment,
    children: comment.children ? sortComments(comment.children, "time") : [],
  }));
};

// 修改 watch，避免递归更新
watch(sortType, () => {
  const sorted = sortComments([...comments.value]);
  comments.value = sorted;
});

/**
 * 评论统计相关的计算属性
 */
const total = computed(() => {
  const countComments = (commentList) => {
    return commentList.reduce((acc, comment) => {
      // 计算当前评论
      let count = 1;
      // 如果有子评论，递归计算子评论数量
      if (comment.children && comment.children.length > 0) {
        count += countComments(comment.children);
      }
      return acc + count;
    }, 0);
  };

  return countComments(comments.value);
});

const activeUsers = computed(() => {
  const userSet = new Set();
  comments.value.forEach((comment) => {
    userSet.add(comment.userId);
    comment.children?.forEach((reply) => {
      userSet.add(reply.userId);
    });
  });
  return Array.from(userSet);
});

// 添加计算属性获取当前用户ID
const currentUserId = computed(() => userStore.user?.id || 0);

/**
 * 递归处理评论点赞状态
 */
const processCommentLikes = async (comment) => {
  if (!userStore.isLoggedIn) {
    return {
      ...comment,
      isLiked: false,
      children: comment.children || [],
    };
  }

  try {
    const likeResponse = await findCommentExplicit(
      userStore.user.id,
      comment.commentId,
      "LIKE_COMMENT"
    );

    // 递归处理子评论
    const children = await Promise.all((comment.children || []).map(processCommentLikes));

    return {
      ...comment,
      isLiked:
        likeResponse.data.status === 200 &&
        likeResponse.data.data &&
        likeResponse.data.data.activityId !== null,
      children,
    };
  } catch (error) {
    return {
      ...comment,
      isLiked: false,
      children: comment.children || [],
    };
  }
};

/**
 * 抽屉
 */
// 添加计算属性来控制显示的评论
const displayComments = computed(() => {
  if (!props.displayLimit || comments.value.length <= props.displayLimit) {
    return comments.value;
  }
  return comments.value.slice(0, props.displayLimit);
});
</script>

<template>
  <div class="post-comments">
    <!-- 评论统计信息 -->
    <div class="comment-stats" v-if="comments.length > 0">
      <span>共 {{ total }} 条评论</span>
      <span>{{ activeUsers.length }} 人参与讨论</span>
    </div>

    <!-- 评论输入区域 -->
    <div class="comment-input-area" v-if="userStore.isLoggedIn">
      <div class="comment-toolbar">
        <el-button
          size="small"
          @click="showEmojiPicker = !showEmojiPicker"
          :class="{ 'is-active': showEmojiPicker }"
          class="emoji-trigger-btn"
        >
          😊 表情
        </el-button>
      </div>

      <!-- Emoji 选择器 -->
      <div v-if="showEmojiPicker" class="emoji-picker-container" ref="emojiPickerRef">
        <EmojiPicker @select="onEmojiSelect" />
      </div>

      <el-input
        v-model="commentContent"
        type="textarea"
        :rows="3"
        placeholder="写下你的评论..."
        resize="none"
      />
      <div class="comment-actions">
        <el-button type="primary" @click="submitComment">
          发表评论
        </el-button>
      </div>
    </div>
    <div v-else class="login-tip">登录后即可评论</div>

    <!-- 排序选项 -->
    <div class="comment-sort">
      <el-radio-group v-model="sortType" size="small">
        <el-radio-button value="time">最新</el-radio-button>
        <el-radio-button value="likes">最热</el-radio-button>
      </el-radio-group>
    </div>

    <!-- 评论列表 -->
    <div class="comment-list" v-if="comments.length > 0">
      <!-- 只显示限制数量的评论 -->
      <div v-for="comment in displayComments" :key="comment.commentId">
        <CommentItem
          :comment="comment"
          :can-manage="canManageComment(comment)"
          :is-logged-in="userStore.isLoggedIn"
          :current-user-id="currentUserId"
          :editing="editingComment === comment.commentId"
          v-model:edit-content="editContent"
          @edit="startEdit"
          @delete="handleDelete"
          @like="toggleLike"
          @reply="startReply"
          @save-edit="saveEdit"
          @cancel-edit="cancelEdit"
        />

        <!-- 回复框 -->
        <div v-if="replyingTo?.commentId === comment.commentId" class="reply-box">
          <el-input
            v-model="replyContent"
            type="textarea"
            :rows="2"
            :placeholder="`回复 ${comment.username}`"
            resize="none"
          />
          <div class="reply-actions">
            <el-button size="small" @click="cancelReply">取消</el-button>
            <el-button type="primary" size="small" @click="submitReply(comment)">
              回复
            </el-button>
          </div>
        </div>
      </div>

      <!-- 显示查看更多按钮 -->
      <div v-if="showViewMore && comments.length > displayLimit" class="view-more">
        <el-button type="primary" text @click="$emit('view-all')">
          查看全部评论
        </el-button>
      </div>
    </div>

    <!-- 空评论提示 -->
    <div v-else class="no-comments">暂无评论，来说两句吧~</div>
  </div>
</template>

<style scoped>
.post-comments {
  margin-top: 24px;
  border-top: 1px solid #eee;
  padding-top: 24px;
}

.no-comments {
  text-align: center;
  color: #999;
  padding: 32px 0;
}

.comment-input-area {
  margin-bottom: 24px;
}

.comment-toolbar {
  margin-bottom: 8px;
  display: flex;
  gap: 8px;
}

.comment-toolbar .el-button.is-active {
  background-color: var(--el-color-primary-light-9);
  color: var(--el-color-primary);
}

.emoji-picker-container {
  position: absolute;
  z-index: 1000;
  margin-top: 4px;
  background: white;
  border-radius: 8px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
}

/* 添加表情选择器样式覆盖 */
:deep(.emoji-picker) {
  --ep-color-border: var(--el-border-color);
  --ep-color-bg: var(--el-bg-color);
  border-color: var(--el-border-color);
}

.comment-list {
  margin-top: 24px;
}

.comment-edit {
  margin-left: 40px;
  margin-top: 8px;
}

.edit-actions {
  display: flex;
  justify-content: flex-end;
  gap: 8px;
  margin-top: 8px;
}

.comment-actions {
  margin-top: 12px;
  display: flex;
  justify-content: flex-end;
  gap: 8px;
}

.comment-actions .el-button {
  margin-left: 16px;
}

.comment-operations {
  margin-left: 40px;
  margin-top: 8px;
  display: flex;
  gap: 16px;
}

.reply-box {
  margin-left: 40px;
  margin-top: 12px;
  margin-bottom: 16px;
}

.reply-actions {
  display: flex;
  justify-content: flex-end;
  gap: 8px;
  margin-top: 8px;
}

.reply-list {
  margin-left: 40px;
  margin-top: 12px;
  padding-left: 16px;
  border-left: 2px solid var(--el-border-color-lighter);
  background-color: var(--el-fill-color-lighter);
  border-radius: 4px;
}

.reply-box {
  margin-left: 40px;
  margin-top: 12px;
  margin-bottom: 16px;
  padding: 12px;
  background-color: var(--el-fill-color-lighter);
  border-radius: 4px;
}

.view-more {
  text-align: center;
  margin-top: 16px;
  padding-top: 16px;
  border-top: 1px solid var(--el-border-color-lighter);
}

.comment-stats {
  display: flex;
  gap: 16px;
  margin-bottom: 16px;
  color: var(--el-text-color-regular);
}
</style>
