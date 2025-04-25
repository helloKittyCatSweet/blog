<script setup>
import { ref, onMounted, onUnmounted, computed, nextTick } from "vue";
import { useRoute, useRouter } from "vue-router";
import { ElMessage, ElIcon } from "element-plus";
import {
  findConversation,
  create,
  connectWebSocket,
  disconnectWebSocket,
  sendMessageWs,
  findContactedUserNames,
  deleteById,
  update,
  readMessage,
} from "@/api/user/message";
import { useUserStore } from "@/stores/modules/user";
import { useMessageStore } from "@/stores/modules/message";
import { findUserById } from "@/api/user/user";
import { ArrowDown, ArrowLeft } from "@element-plus/icons-vue";
import { useSettingsStore } from "@/stores/modules/setting";
import { USER_MESSAGE_DETAIL_PATH } from "@/constants/routes/user";
import MessageItem from "@/views/message/user/MessageItem.vue";

// WebSocket 状态
const wsConnected = ref(false);

const route = useRoute();
const router = useRouter();
const userStore = useUserStore();
const messageStore = useMessageStore();
const messages = ref([]);
const loading = ref(false);

// 获取当前用户和聊天对象的信息
const currentUser = userStore.user;
// 直接从路由获取参数
const receiverId = computed(
  () => route.query.receiverId || messageStore.currentReceiver?.receiverId
);

const receiverInfo = ref({});
const receiverName = computed(() => {
  if (!receiverId.value) {
    return "请选择联系人";
  }
  return (
    messageStore.currentReceiver.receiverName ||
    receiverInfo.value.username ||
    "请选择联系人"
  );
});
const receiverAvatar = computed(
  () => messageStore.currentReceiver.receiverAvatar || receiverInfo.value.avatar
);

// 获取聊天对象信息
const loadReceiverInfo = async () => {
  if (!receiverId.value) {
    return;
  }

  try {
    const { data } = await findUserById(receiverId.value);
    if (data.status === 200 && data.data) {
      receiverInfo.value = data.data;
      // 更新 messageStore 中的当前接收者信息
      messageStore.setCurrentReceiver({
        receiverId: data.data.id,
        receiverName: data.data.nickname || data.data.username,
        receiverAvatar: data.data.avatar,
      });
    }
  } catch (error) {
    ElMessage.error("获取用户信息失败");
  }
};

// 加载聊天记录
const loadConversation = async () => {
  if (!receiverId.value || !currentUser.id) {
    messages.value = [];
    return;
  }

  loading.value = true;
  try {
    const response = await findConversation({
      userId: currentUser.id,
      receiverId: receiverId.value,
    });

    if (response.data && response.data.data) {
      messages.value = response.data.data;
      nextTick(() => scrollToBottom());
    } else {
      messages.value = []; // 如果没有聊天记录，清空消息列表
    }
  } catch (error) {
    ElMessage.error("加载聊天记录失败");
    messages.value = [];
  } finally {
    loading.value = false;
  }
};

/**
 * 发消息
 */

const newMessage = ref("");

const sendMessage = async () => {
  if (!newMessage.value.trim()) {
    return;
  }

  try {
    const lastMessageId =
      messages.value.length > 0 ? messages.value[messages.value.length - 1].messageId : 0;

    const messageData = {
      senderId: currentUser.id,
      receiverId: receiverId.value,
      content: newMessage.value,
      parentId: lastMessageId,
    };

    const response = await create(messageData);

    // 添加新消息到消息列表
    if (response.data && response.data.data) {
      messages.value.push({
        ...response.data.data,
        createdAt: new Date().toLocaleString(), // 临时显示当前时间
        senderId: currentUser.id,
        receiverId: receiverId.value,
        content: newMessage.value,
      });

      messageStore.updateContactMessage(receiverId.value, newMessage.value);
      // 重新加载联系人列表
      await loadContactList();
      nextTick(() => scrollToBottom());
    }

    newMessage.value = "";
  } catch (error) {
    ElMessage.error("发送消息失败");
  }
};

// 处理接收到的消息
const handleReceivedMessage = (message) => {
  // 只处理当前会话的消息
  if (message.senderId === receiverId || message.receiverId === receiverId) {
    messages.value.push(message);
    // 更新联系人列表中的消息状态
    messageStore.updateContactMessage(
      message.senderId === currentUser.id ? message.receiverId : message.senderId,
      message.content,
      message.messageType
    );

    if (!isAtBottom.value) {
      unreadCount.value++;
    } else {
      nextTick(() => scrollToBottom());
    }
  }
};

// 处理 WebSocket 状态变化
const handleStatusChange = (status) => {
  wsConnected.value = status.connected;
  if (!status.connected) {
    ElMessage.warning("聊天连接已断开，正在重新连接...");
  }
};

/**
 * 添加emoji
 */
const showEmoji = ref(false);

// 添加表情数据
const emojis = ref([
  "😀",
  "😃",
  "😄",
  "😁",
  "😆",
  "😅",
  "😂",
  "🤣",
  "😊",
  "😇",
  "🙂",
  "🙃",
  "😉",
  "😌",
  "😍",
  "🥰",
  "😘",
  "😗",
  "😙",
  "😚",
  "😋",
  "😛",
  "😝",
  "😜",
  "🤪",
  "🤨",
  "🧐",
  "🤓",
  "😎",
  "🤩",
  "🥳",
  "😏",
  "❤️",
  "🧡",
  "💛",
  "💚",
  "💙",
  "💜",
  "🤎",
  "🖤",
  "👍",
  "👎",
  "👊",
  "✊",
  "🤛",
  "🤜",
  "🤝",
  "👏",
]);

// 修改选择表情的处理函数
const onSelectEmoji = (emoji) => {
  newMessage.value += emoji;
  showEmoji.value = false;
  isClickOpen.value = false;
};

// 添加 ref 用于获取 DOM 元素
const emojiPickerRef = ref(null);
const emojiButtonRef = ref(null);
// 添加一个状态来区分是点击打开还是悬停打开
const isClickOpen = ref(false);

// 添加鼠标悬停处理函数
const handleMouseEnter = () => {
  if (!isClickOpen.value) {
    showEmoji.value = true;
  }
};

const handleMouseLeave = () => {
  if (!isClickOpen.value) {
    showEmoji.value = false;
  }
};

// 修改按钮点击处理
const handleEmojiButtonClick = () => {
  showEmoji.value = !showEmoji.value;
  isClickOpen.value = showEmoji.value;
};

// 添加点击外部关闭功能
const handleClickOutside = (event) => {
  const picker = emojiPickerRef.value;
  const button = emojiButtonRef.value;

  if (
    picker &&
    button &&
    !picker.contains(event.target) &&
    !button.contains(event.target)
  ) {
    showEmoji.value = false;
    isClickOpen.value = false;
  }
};

// 在组件挂载时添加点击事件监听
onMounted(async () => {
  // 如果路由中有 receiverId，优先加载该用户信息
  if (receiverId.value) {
    await loadReceiverInfo();
    await loadConversation();
  }
  // 加载联系人列表
  loadContactList();

  // 连接 WebSocket
  connectWebSocket({
    userId: currentUser.id,
    messageCallback: handleReceivedMessage,
    statusCallback: handleStatusChange,
  });

  document.addEventListener("click", handleClickOutside);

  messagesContainer.value?.addEventListener("scroll", checkIfAtBottom);
});

// 在组件卸载时移除事件监听
onUnmounted(() => {
  disconnectWebSocket();
  document.removeEventListener("click", handleClickOutside);
  if (messagesContainer.value) {
    messagesContainer.value.removeEventListener("scroll", checkIfAtBottom);
  }
});

/**
 * 消息界面滑动
 */
const messagesContainer = ref(null);

// 滚动到底部的方法
const scrollToBottom = () => {
  if (messagesContainer.value) {
    messagesContainer.value.scrollTop = messagesContainer.value.scrollHeight;
  }
};

/**
 * 导航到底部
 */
const showScrollButton = ref(false);
const unreadCount = ref(0);
const isAtBottom = ref(true);

// 检查是否还在底部
const checkIfAtBottom = () => {
  if (!messagesContainer.value) return true;
  const { scrollTop, scrollHeight, clientHeight } = messagesContainer.value;
  const atBottom = Math.abs(scrollHeight - scrollTop - clientHeight) < 50;
  isAtBottom.value = atBottom;
  showScrollButton.value = !atBottom;
  if (atBottom) {
    unreadCount.value = 0;
  }
};

// 滑动到底部时重置计数
const scrollToBottomWithReset = () => {
  scrollToBottom();
  unreadCount.value = 0;
};

/**
 * 切换联系人
 */
// 获取联系人列表
const chatList = computed(() => messageStore.contactList);

// 添加加载联系人列表的方法
const loadContactList = async () => {
  if (chatList.value.length === 0) {
    try {
      const { data } = await findContactedUserNames();
      const formattedList = data.data.map((item) => ({
        receiverId: item.userId,
        receiverName: item.username,
        receiverAvatar: item.avatar,
        lastMessage: item.message,
        unreadCount: item.unreadCount || 0,
        lastMessageTime: item.lastMessageTime || "-",
      }));
      messageStore.setContactList(formattedList);
    } catch (error) {
      ElMessage.error("加载联系人列表失败");
    }
  }
};

// 添加切换联系人的方法
const switchContact = async (contact) => {
  if (contact.receiverId === receiverId.value) return;

  messageStore.setCurrentReceiver({
    receiverId: contact.receiverId,
    receiverName: contact.receiverName,
    receiverAvatar: contact.receiverAvatar,
  });

  // 标记该联系人发来的所有消息为已读
  try {
    await readMessage(contact.receiverId);
  } catch (error) {
    console.error("标记消息已读失败:", error);
  }

  // 清除未读消息计数
  messageStore.clearUnreadCount(contact.receiverId);

  // 先切换路由
  await router.push({
    path: USER_MESSAGE_DETAIL_PATH,
    query: {
      receiverId: contact.receiverId,
    },
  });

  // 然后重新加载聊天记录和用户信息
  await Promise.all([loadReceiverInfo(), loadConversation()]);
};

/**
 * 调整联系人列表背景颜色
 */
const settingStore = useSettingsStore();
const backgroundColor = computed(() => {
  // 根据主题返回对应的背景色
  return settingStore.theme === "dark" ? "#1a1a1a" : "#f5f5f5";
});

/**
 * 操作消息
 */
const handleDeleteMessage = async (messageId) => {
  try {
    console.log("Deleting message:", messageId); // 添加日志
    const response = await deleteById(messageId);
    if (response.status === 200) {
      messages.value = messages.value.filter((msg) => msg.messageId !== messageId);
      ElMessage.success("删除成功");
    } else {
      ElMessage.error("删除失败");
    }
  } catch (error) {
    ElMessage.error("出现未知错误，删除失败");
  }
};

const handleEditMessage = async (updateMessage) => {
  try {
    const response = await update(updateMessage);
    if (response.status === 200) {
      const message = messages.value.find(
        (msg) => msg.messageId === updateMessage.messageId
      );
      if (message) {
        message.content = updateMessage.content;
      }
      ElMessage.success("修改成功");
    } else {
      ElMessage.error("修改失败");
    }
  } catch (error) {
    ElMessage.error("出现未知错误，修改失败");
  }
};

/**
 * 返回按钮
 */
const handleBack = () => {
  router.push({
    path: USER_MESSAGE_DETAIL_PATH,
    query: {}, // 清除 receiverId 参数
  });
  // 清除当前接收者信息
  messageStore.setCurrentReceiver({});
  // 清空消息列表
  messages.value = [];
};
</script>

<template>
  <PageContainer>
    <div class="chat-layout">
      <!-- 联系人列表面板 -->
      <div class="contacts-panel" :style="{ backgroundColor }">
        <div class="contacts-header">
          <h3>联系人列表</h3>
        </div>
        <div class="contacts-list">
          <div
            v-for="contact in chatList"
            :key="contact.receiverId"
            :class="['contact-item', contact.receiverId === receiverId ? 'active' : '']"
            @click="switchContact(contact)"
          >
            <div class="contact-info">
              <el-avatar :size="32" :src="contact.receiverAvatar">
                {{ contact.receiverName?.charAt(0) }}
              </el-avatar>
              <div class="contact-details">
                <div class="contact-name">{{ contact.receiverName }}</div>
                <div class="last-message">{{ contact.lastMessage || "暂无消息" }}</div>
              </div>
            </div>
            <div class="contact-meta">
              <span class="time">{{ contact.lastMessageTime }}</span>

              <el-badge
                v-if="contact.unreadCount"
                :value="contact.unreadCount"
                class="contact-badge"
              />
            </div>
          </div>
        </div>
      </div>

      <!-- 聊天窗口 -->
      <div class="chat-window">
        <!-- 添加导航栏 -->
        <div class="chat-header">
          <el-button link @click="handleBack" class="back-button">
            <el-icon><ArrowLeft /></el-icon>
            返回
          </el-button>
          <div class="receiver-info">
            <span class="receiver-name">{{ receiverName }}</span>
          </div>
        </div>

        <!-- 无选中时显示提示 -->
        <div v-if="!receiverId" class="empty-chat-container">
          <el-empty description="请从左侧选择聊天对象"></el-empty>
        </div>

        <!-- 有选中用户时显示聊天内容 -->
        <template v-else>
          <div ref="messagesContainer" class="chat-messages" v-loading="loading">
            <MessageItem
              v-for="message in messages"
              :key="message.messageId"
              :message="message"
              :is-sender="message.senderId === currentUser.id"
              :sender-avatar="
                message.senderId === currentUser.id ? currentUser.avatar : receiverAvatar
              "
              :sender-name="
                message.senderId === currentUser.id
                  ? currentUser.username
                  : receiverName || '未知用户'
              "
              @delete="handleDeleteMessage"
              @edit="handleEditMessage"
            />
          </div>
          <!-- 添加滚动到底部按钮 -->
          <div
            v-show="showScrollButton"
            class="scroll-bottom-button"
            @click="scrollToBottomWithReset"
          >
            <el-badge :value="unreadCount" :hidden="!unreadCount">
              <el-button circle>
                <el-icon><ArrowDown /></el-icon>
              </el-button>
            </el-badge>
          </div>

          <!-- 发送消息区域 -->
          <div class="message-input-wrapper">
            <div class="message-toolbar">
              <el-button
                ref="emojiButtonRef"
                type="primary"
                text
                @click="handleEmojiButtonClick"
                @mouseenter="handleMouseEnter"
                class="emoji-btn"
              >
                <el-icon>
                  <svg viewBox="0 0 1024 1024" xmlns="http://www.w3.org/2000/svg">
                    <path
                      fill="currentColor"
                      d="M512 64a448 448 0 1 1 0 896 448 448 0 0 1 0-896zm0 64a384 384 0 1 0 0 768 384 384 0 0 0 0-768zm176.111 259.889a48 48 0 1 1 0 96 48 48 0 0 1 0-96zm-352.222 0a48 48 0 1 1 0 96 48 48 0 0 1 0-96zM512 512a144 144 0 0 1 144 144h-48a96 96 0 0 0-192 0h-48a144 144 0 0 1 144-144z"
                    />
                  </svg>
                </el-icon>
              </el-button>
            </div>

            <div class="input-area">
              <el-input
                v-model="newMessage"
                type="textarea"
                :rows="3"
                placeholder="请输入消息..."
                @keyup.enter.ctrl="sendMessage"
                resize="none"
              />
              <el-button
                type="primary"
                @click="sendMessage"
                :disabled="!newMessage.trim()"
              >
                发送
              </el-button>
            </div>

            <!-- emoji选择器 -->
            <div
              v-if="showEmoji"
              ref="emojiPickRef"
              class="emoji-picker"
              @mouseenter="handleMouseEnter"
              @mouseleave="handleMouseLeave"
            >
              <div class="emoji-list">
                <span
                  v-for="emoji in emojis"
                  :key="emoji"
                  class="emoji-item"
                  @click="onSelectEmoji(emoji)"
                >
                  {{ emoji }}
                </span>
              </div>
            </div>
          </div>
        </template>
      </div>
    </div>
  </PageContainer>
</template>

<style lang="scss" scoped>
.chat-layout {
  display: flex;
  height: 100vh;
  background-color: #f5f5f5;

  .contacts-panel {
    width: 280px;
    border-right: 1px solid #eee;
    display: flex;
    flex-direction: column;

    .contacts-header {
      padding: 16px;
      border-bottom: 1px solid #eee;

      h3 {
        margin: 0;
        font-size: 16px;
        color: var(--el-text-color-primary);
      }
    }

    .contacts-list {
      flex: 1;
      overflow-y: auto;

      .contact-item {
        padding: 12px 16px;
        cursor: pointer;
        display: flex;
        align-items: center;
        justify-content: space-between;
        border-bottom: 1px solid #f5f5f5;
        background-color: #fff;

        &:hover {
          background-color: var(--el-fill-color-light);
        }

        &.active {
          background-color: var(--el-color-primary-light-9);
        }

        .contact-info {
          flex: 1;
          min-width: 0;

          .contact-name {
            font-size: 14px;
            font-weight: 500;
            margin-bottom: 4px;
            color: var(--el-text-color-primary);
          }

          .last-message {
            font-size: 12px;
            color: var(--el-text-color-secondary);
            white-space: nowrap;
            overflow: hidden;
            text-overflow: ellipsis;
          }
        }

        .contact-badge {
          margin-left: 8px;
        }
      }
    }
  }

  .chat-window {
    flex: 1;
    min-width: 0;
    height: 100vh;
    border-radius: 0;
  }
}

.chat-window {
  display: flex;
  flex-direction: column;
  background-color: #f5f5f5;
  border-radius: 8px;
  overflow: hidden;

  .chat-messages {
    flex: 1;
    padding: 20px;
    overflow-y: auto;
    scroll-behavior: smooth; // 添加平滑滚动效果

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
      }

      &.received {
        .message-content {
          background-color: #fff;

          &::before {
            left: -8px;
            border-right-color: #fff;
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

        .message-text {
          font-size: 14px;
          line-height: 1.5;
          word-break: break-all;
        }
      }

      /* 自定义滚动条样式 */
      &::-webkit-scrollbar {
        width: 6px;
      }

      &::-webkit-scrollbar-thumb {
        background-color: rgba(0, 0, 0, 0.2);
        border-radius: 3px;
      }

      &::-webkit-scrollbar-track {
        background-color: transparent;
      }
    }
  }

  .message-input-wrapper {
    padding: 12px;
    background-color: #fff;
    border-top: 1px solid #eee;
    position: relative;

    .message-toolbar {
      padding: 8px;
      border-bottom: 1px solid #f0f0f0;
      display: flex;
      align-items: center;
      gap: 8px;

      .emoji-btn {
        font-size: 20px;
        padding: 4px;

        &:hover {
          background-color: var(--el-fill-color-light);
          border-radius: 4px;
        }
      }
    }

    .input-area {
      padding: 12px 0;
      display: flex;
      gap: 12px;

      .el-textarea {
        flex: 1;

        :deep(.el-textarea__inner) {
          border-radius: 8px;
          transition: all 0.3s;
          resize: none;

          &:focus {
            box-shadow: 0 0 0 2px var(--el-color-primary-light-8);
          }
        }
      }

      .el-button {
        align-self: flex-end;
        min-width: 80px;
      }
    }

    .emoji-picker {
      position: absolute;
      bottom: 100%;
      left: 12px;
      z-index: 1000;
      background: white;
      box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
      border-radius: 8px;
      padding: 12px;
      width: 400px;

      .emoji-list {
        display: grid;
        grid-template-columns: repeat(8, 1fr);
        gap: 8px;

        .emoji-item {
          cursor: pointer;
          font-size: 24px;
          text-align: center;
          padding: 4px;
          border-radius: 4px;

          &:hover {
            background-color: var(--el-fill-color-light);
          }
        }
      }
    }
  }

  .scroll-bottom-button {
    position: fixed;
    right: 40px;
    bottom: 120px;
    z-index: 100;
    transition: all 0.3s;

    .el-badge {
      :deep(.el-badge__content) {
        background-color: var(--el-color-danger);
      }
    }

    .el-button {
      box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
    }
  }

  .chat-header {
    padding: 16px 20px;
    background-color: #fff;
    border-bottom: 1px solid #eee;
    display: flex;
    align-items: center;
    gap: 20px;

    .back-button {
      display: flex;
      align-items: center;
      gap: 4px;
      font-size: 14px;

      &:hover {
        color: var(--el-color-primary);
      }
    }

    .receiver-info {
      .receiver-name {
        font-size: 16px;
        font-weight: 500;
      }
    }
  }
}

.contact-meta {
  display: flex;
  flex-direction: column;
  align-items: flex-end;
  gap: 4px;

  .time {
    font-size: 12px;
    color: var(--el-text-color-secondary);
  }
}

// 添加空状态样式
.empty-chat-container {
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
  background-color: #fff;
}
</style>
