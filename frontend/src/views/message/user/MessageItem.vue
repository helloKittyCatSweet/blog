<script setup>
import { ref } from "vue";
import { MoreFilled } from "@element-plus/icons-vue";

const props = defineProps({
  message: {
    type: Object,
    required: true,
  },
  isSender: {
    type: Boolean,
    required: true,
  },
  senderAvatar: {
    type: String,
    default: "",
  },
  senderName: {
    type: String,
    required: true,
    default: "未知用户", // 添加默认值
  },
});

const emit = defineEmits(["delete", "edit"]);

const isEditing = ref(false);
const editedContent = ref("");

const startEdit = () => {
  editedContent.value = props.message.content;
  isEditing.value = true;
};

const saveEdit = () => {
  if (editedContent.value.trim() === "") {
    ElMessage.warning("消息内容不能为空");
    return;
  }
  // 发送完整的消息对象
  emit("edit", {
    messageId: props.message.messageId,
    senderId: props.message.senderId,
    receiverId: props.message.receiverId,
    content: editedContent.value,
    parentId: props.message.parentId,
  });
  isEditing.value = false;
};

const cancelEdit = () => {
  isEditing.value = false;
};

const confirmDelete = () => {
  ElMessageBox.confirm("确定要删除这条消息吗？", "提示", {
    confirmButtonText: "确定",
    cancelButtonText: "取消",
    type: "warning",
    distinguishCancelAndClose: true,
    customClass: "delete-message-box",
    showClose: false,
    center: true,
  })
    .then(() => {
      emit("delete", props.message.messageId);
    })
    .catch(() => {});
};
</script>

<template>
  <div :class="['message-item', isSender ? 'sent' : 'received']">
    <el-avatar :size="40" :src="senderAvatar">
      {{ senderName?.charAt(0) }}
    </el-avatar>

    <div class="message-content">
      <div class="message-info">
        <span class="sender-name">{{ senderName }}</span>
        <span class="message-time">{{ message.createdAt }}</span>
      </div>

      <div class="message-text-wrapper">
        <template v-if="isEditing">
          <el-input
            v-model="editedContent"
            type="textarea"
            :rows="2"
            resize="none"
            @keyup.enter="saveEdit"
            @keyup.esc="cancelEdit"
          />
          <div class="edit-actions">
            <el-button size="small" @click="cancelEdit">取消</el-button>
            <el-button size="small" type="primary" @click="saveEdit">保存</el-button>
          </div>
        </template>
        <template v-else>
          <div class="message-text">{{ message.content }}</div>
          <el-dropdown trigger="click" class="message-actions">
            <el-button link class="more-button">
              <el-icon><MoreFilled /></el-icon>
            </el-button>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item @click="startEdit">编辑</el-dropdown-item>
                <el-dropdown-item @click="confirmDelete" divided>删除</el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </template>
      </div>
    </div>
  </div>
</template>

<style lang="scss">
.message-item {
  display: flex;
  margin-bottom: 20px;
  gap: 12px;

  &.sent {
    flex-direction: row-reverse;

    .message-content {
      background-color: #95ec69;

      &::before {
        right: -8px;
        border-left-color: #95ec69;
      }
    }

    .message-actions {
      left: -24px;
    }
  }

  &.received {
    .message-content {
      background-color: #fff;

      &::before {
        left: -8px;
        border-right-color: #fff;
      }
    }

    .message-actions {
      right: -24px;
    }
  }
}

.message-content {
  max-width: 60%;
  padding: 12px;
  border-radius: 8px;
  position: relative;

  &::before {
    content: "";
    position: absolute;
    top: 12px;
    border: 8px solid transparent;
  }
}

.message-info {
  margin-bottom: 4px;

  .sender-name {
    font-weight: 500;
    margin-right: 8px;
  }

  .message-time {
    font-size: 12px;
    color: #999;
  }
}

.message-text-wrapper {
  position: relative;

  .message-text {
    font-size: 14px;
    line-height: 1.5;
    word-break: break-all;
  }

  .message-actions {
    position: absolute;
    top: 50%;
    transform: translateY(-50%);
    opacity: 0;
    transition: opacity 0.2s;
  }

  &:hover .message-actions {
    opacity: 1;
  }
}

.more-button {
  padding: 4px;
  color: var(--el-text-color-secondary);

  &:hover {
    color: var(--el-text-color-primary);
  }
}

.edit-actions {
  margin-top: 8px;
  display: flex;
  justify-content: flex-end;
  gap: 8px;
}

.custom-message-box {
  .el-message-box__btns {
    padding: 12px;
    text-align: right;

    button {
      margin-left: 10px;
      padding: 8px 20px;
      min-width: 80px;
      border: 2px solid red; // 调试样式
    }

    .el-button--default {
      border: 1px solid var(--el-border-color);
      background-color: var(--el-button-bg-color, var(--el-color-white));
    }

    .el-button--primary {
      background-color: var(--el-color-danger);
      border-color: var(--el-color-danger);
      color: blue;
    }
  }
}
</style>
