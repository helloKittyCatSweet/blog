<script setup>
import { defineProps, defineEmits } from "vue";
import { ElMessage } from "element-plus";
import { Pointer } from "@element-plus/icons-vue";

const props = defineProps({
  comment: {
    type: Object,
    required: true,
  },
  isReply: {
    type: Boolean,
    default: false,
  },
  canManage: {
    type: Boolean,
    required: true,
  },
  isLoggedIn: {
    type: Boolean,
    required: true,
  },
  currentUserId: {
    type: Number,
    required: true,
  },
  editing: {
    type: Boolean,
    default: false,
  },
  editContent: {
    type: String,
    default: "",
  },
});

const emit = defineEmits([
  "edit",
  "delete",
  "like",
  "reply",
  "submit-reply",
  "cancel-reply",
  "save-edit",
  "cancel-edit",
  "update:editContent",
]);

// 添加获取所有回复的方法
const getAllReplies = (comment) => {
  let replies = [...comment.children];
  comment.children.forEach((child) => {
    if (child.children?.length > 0) {
      replies = replies.concat(child.children);
    }
  });
  return replies;
};
</script>

<template>
  <div :class="['comment-container', { 'reply-item': isReply }]">
    <div :class="[isReply ? 'reply-header' : 'comment-header']">
      <el-avatar :size="isReply ? 24 : 32">
        {{ comment.username?.charAt(0) }}
      </el-avatar>
      <span :class="[isReply ? 'reply-username' : 'comment-username']">
        {{ comment.username }}
      </span>
      <!-- 回复对象显示 -->
      <span v-if="comment.parentContent" class="reply-to">
        回复 <span class="reply-target">{{ comment.parentUsername }}</span
        >：
        <span class="parent-content">{{ comment.parentContent }}</span>
      </span>
      <span :class="[isReply ? 'reply-time' : 'comment-time']">
        {{ comment.createdAt }}
      </span>

      <div v-if="canManage" :class="[isReply ? 'reply-actions' : 'comment-actions']">
        <el-button
          v-if="comment.userId === currentUserId"
          link
          @click="emit('edit', comment)"
        >
          编辑
        </el-button>
        <el-button link @click="emit('delete', comment.commentId)">删除</el-button>
      </div>
    </div>

    <div :class="[isReply ? 'reply-content' : 'comment-content']" v-if="!editing">
      {{ comment.content }}
    </div>

    <!-- 添加编辑框 -->
    <div v-else :class="[isReply ? 'reply-content' : 'comment-content']">
      <el-input
        :modelValue="editContent"
        @update:modelValue="(newValue) => emit('update:editContent', newValue)"
        type="textarea"
        :rows="isReply ? 2 : 3"
        resize="none"
      />
      <div class="edit-actions">
        <el-button size="small" @click="emit('cancel-edit', comment)">取消</el-button>
        <el-button type="primary" size="small" @click="emit('save-edit', comment)">
          保存
        </el-button>
      </div>
    </div>

    <div :class="[isReply ? 'reply-operations' : 'comment-operations']">
      <el-button
        link
        :type="comment.isLiked ? 'primary' : 'default'"
        @click="emit('like', comment)"
      >
        <el-icon><Pointer /></el-icon>
        {{ comment.likes || 0 }}
      </el-button>

      <el-button v-if="isLoggedIn" link @click="emit('reply', comment)"> 回复 </el-button>
    </div>

    <!-- 修改子评论列表的渲染逻辑 -->
    <div v-if="!isReply && comment.children?.length > 0" class="nested-replies">
      <CommentItem
        v-for="reply in getAllReplies(comment)"
        :key="reply.commentId"
        :comment="reply"
        :is-reply="true"
        :can-manage="canManage"
        :is-logged-in="isLoggedIn"
        :current-user-id="currentUserId"
        :editing="editing"
        :edit-content="editContent"
        @edit="(comment) => emit('edit', comment)"
        @delete="(commentId) => emit('delete', commentId)"
        @like="(comment) => emit('like', comment)"
        @reply="(comment) => emit('reply', comment)"
        @save-edit="(comment) => emit('save-edit', comment)"
        @cancel-edit="(comment) => emit('cancel-edit', comment)"
        @update:editContent="(content) => emit('update:editContent', content)"
      />
    </div>
  </div>
</template>

<style scoped>
.comment-container {
  padding: 16px 0;
  border-bottom: 1px solid var(--el-border-color-lighter);
}

.comment-header,
.reply-header {
  display: flex;
  align-items: center;
  margin-bottom: 8px;
  position: relative;
}

.comment-username,
.reply-username {
  margin-left: 8px;
  font-weight: 500;
}

.comment-time,
.reply-time {
  margin-left: 12px;
  color: #999;
  font-size: 12px;
}

.comment-content,
.reply-content {
  margin-left: 40px;
  line-height: 1.6;
  color: var(--el-text-color-primary);
  word-break: break-all;
  white-space: pre-wrap;
}

.reply-content {
  margin-left: 32px;
  font-size: 14px;
}

.comment-actions,
.reply-actions {
  margin-left: auto;
  display: flex;
  gap: 8px;
}

.comment-operations,
.reply-operations {
  margin-left: 40px;
  margin-top: 8px;
  display: flex;
  gap: 16px;
}

.reply-operations {
  margin-left: 32px;
}

/* 回复项的特殊样式 */
.reply-item {
  margin-bottom: 16px;
  padding: 12px 0;
}

.reply-item:last-child {
  margin-bottom: 0;
}

/* 按钮样式调整 */
:deep(.el-button--link) {
  padding: 0 4px;
  height: auto;
  font-size: 13px;
}

:deep(.el-button--link:hover) {
  color: var(--el-color-primary);
}

.edit-actions {
  display: flex;
  justify-content: flex-end;
  gap: 8px;
  margin-top: 8px;
}

.reply-to {
  margin-left: 8px;
  color: #999;
  font-size: 13px;
}

.reply-target {
  color: var(--el-color-primary);
}

/* 只保留第一级嵌套的样式 */
.nested-replies {
  margin-left: 40px;
  margin-top: 12px;
  padding-left: 16px;
  border-left: 2px solid var(--el-border-color-lighter);
}
</style>
