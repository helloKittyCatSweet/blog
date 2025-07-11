<script setup>
import { ref, onMounted } from "vue";
import { ElMessage } from "element-plus";
import PageContainer from "@/components/PageContainer.vue";
import { Search, Message, ChatDotRound, Check, Remove } from "@element-plus/icons-vue";
import {
  findContactedUserNames,
  findMessagePage,
  readMessage,
  unReadMessage,
  getUserSystemMessages,
  markSystemMessageAsRead,
  markSystemMessageAsUnread,
} from "@/api/user/message";
import { useRouter } from "vue-router";
import { USER_MESSAGE_DETAIL_PATH } from "@/constants/routes-constants";
import { useMessageStore } from "@/stores/modules/message";
import { findUserById } from "@/api/user/user.js";
import { useUserStore } from "@/stores/modules/user";

// 消息列表数据
const chatList = ref([]);
const loading = ref(false);

// 分页参数
const pagination = ref({
  currentPage: 1,
  pageSize: 10,
  total: 0,
});

// 搜索条件
const searchForm = ref({
  receiverName: "", // 改为接收者名称搜索
  content: "",
  startDate: "",
  endDate: "",
  page: 0,
  size: 10,
});

// 表格列配置
const columns = [
  {
    prop: "receiver",
    label: "联系人",
    minWidth: "200",
    slot: true, // 使用自定义插槽
  },
  {
    prop: "lastMessage",
    label: "最新消息",
    minWidth: "300",
    slot: true,
  },
  {
    prop: "unreadCount",
    label: "未读消息",
    width: "100",
    slot: true,
  },
  {
    prop: "lastMessageTime",
    label: "最后联系时间",
    width: "180",
  },
];

// 添加一个标记来区分是否处于搜索模式
const isSearchMode = ref(false);

// 修改加载消息列表函数
const loadMessages = async () => {
  loading.value = true;
  try {
    // 判断是否有搜索条件
    isSearchMode.value = !!(
      searchForm.value.receiverName ||
      searchForm.value.content ||
      searchForm.value.startDate ||
      searchForm.value.endDate
    );

    if (isSearchMode.value) {
      const params = {
        ...searchForm.value,
        page: pagination.value.currentPage - 1,
        size: pagination.value.pageSize,
      };

      const { data } = await findMessagePage(params);
      if (data.status === 200) {
        chatList.value = await Promise.all(
          data.data.content.map(async (message) => {
            let receiverName = message.receiver?.username;
            let receiverAvatar = message.receiver?.avatar;

            if (!receiverName || !receiverAvatar) {
              try {
                const userResponse = await findUserById(message.receiverId);
                if (userResponse.data.status === 200) {
                  receiverName = userResponse.data.data.username;
                  receiverAvatar = userResponse.data.data.avatar;
                }
              } catch (error) {
                console.error("获取用户信息失败:", error);
              }
            }

            return {
              messageId: message.messageId,
              receiverId: message.receiverId,
              senderId: message.senderId,
              content: message.content,
              createdAt: message.createdAt,
              isRead: message.isRead,
              receiverName: receiverName || "未知用户",
              receiverAvatar: receiverAvatar,
            };
          })
        );

        pagination.value.total = data.data.totalElements;
      } else {
        ElMessage.error(data.message || "获取消息列表失败");
      }
    } else {
      const { data } = await findContactedUserNames();
      if (data.status === 200) {
        chatList.value = data.data.map((item) => ({
          receiverId: item.userId,
          receiverName: item.username,
          receiverAvatar: item.avatar,
          lastMessage: item.message,
          unreadCount: Number(item.unreadCount) || 0,
          lastMessageTime: item.lastMessageTime == null ? "-" : item.lastMessageTime,
        }));
        pagination.value.total = chatList.value.length;
      } else {
        ElMessage.error(data.message || "获取联系人列表失败");
      }
    }
  } catch (error) {
    console.error("加载消息列表失败:", error);
    ElMessage.error("加载失败");
  } finally {
    loading.value = false;
  }
};

// 处理搜索
const handleSearch = () => {
  pagination.value.currentPage = 1;
  loadMessages();
};

// 处理重置
const handleReset = () => {
  searchForm.value = {
    receiverName: "", // 修改为与搜索表单一致的字段
    content: "",
    startDate: "",
    endDate: "",
  };
  isSearchMode.value = false; // 确保切换回聊天记录模式
  pagination.value.currentPage = 1; // 重置页码
  pagination.value.total = 0; // 重置总数
  handleSearch();
};

// 处理页面变化
const handlePageChange = (page) => {
  pagination.value.currentPage = page;
  loadMessages();
};

// 处理每页条数变化
const handleSizeChange = (size) => {
  pagination.value.pageSize = size;
  pagination.value.currentPage = 1; // 切换每页条数时重置为第一页
  loadMessages();
};

onMounted(() => {
  loadMessages();
  loadSystemMessages();
  // 移除键盘事件监听
  window.removeEventListener("keydown", handleKeydown);
});

const router = useRouter();
// 处理聊天按钮点击
const messageStore = useMessageStore();
const handleChat = (row) => {
  // 设置当前接收者信息
  messageStore.setCurrentReceiver({
    receiverId: row.receiverId,
    receiverName: row.receiverName,
    receiverAvatar: row.receiverAvatar,
  });

  // 标记消息为已读（如果有未读消息）
  if(row.unreadCount > 0) {
    readMessage(row.receiverId).then(() => {
      loadMessages(); // 刷新消息列表
    });
  }

  // 跳转到聊天窗口，并传递 receiverId 作为查询参数
  router.push({
    path: USER_MESSAGE_DETAIL_PATH,
    query: {
      receiverId: row.receiverId
    }
  });
};

/**
 * 搜索状态监听回车
 */
const handleKeydown = (event) => {
  if (event.key === "Enter") {
    handleSearch();
  }
};

// 获取当前用户信息
const userStore = useUserStore();
const currentUserId = userStore.user.id;

/**
 * 标为已读
 */
const handleMarkAsRead = async (row) => {
  try {
    const count = row.unreadCount;
    const response = await readMessage(row.receiverId);

    if (response.data.status === 200) {
      ElMessage.success(`已将 ${count} 条消息标记为已读`);
      // 等待消息列表刷新完成
      await loadMessages();
    } else {
      ElMessage.error(response.data.message || "标记已读失败");
    }
  } catch (error) {
    console.error("标记已读失败:", error);
    ElMessage.error("标记已读失败");
  }
};

const handleMarkAsUnread = async (row) => {
  try {
    const response = await unReadMessage(row.receiverId);

    if (response.data.status === 200) {
      ElMessage.success("已标记为未读");
      // 等待消息列表刷新完成
      await loadMessages();
    } else {
      ElMessage.error(response.data.message || "标记未读失败");
    }
  } catch (error) {
    console.error("标记未读失败:", error);
    ElMessage.error("标记未读失败");
  }
};

/**
 * 系统消息
 */
// 添加标签页激活状态
const activeTab = ref("chat");

// 添加系统消息列表
const systemMessages = ref([]);
const systemMessageLoading = ref(false);

// 添加系统消息分页
const systemPagination = ref({
  currentPage: 1,
  pageSize: 10,
  total: 0,
});

// 加载系统消息
const loadSystemMessages = async () => {
  systemMessageLoading.value = true;
  try {
    const { data } = await getUserSystemMessages({
      page: systemPagination.value.currentPage - 1,
      size: systemPagination.value.pageSize,
    });
    systemMessages.value = data.data.content;
    systemPagination.value.total = data.data.totalElements; // 设置总数为数组长度
  } catch (error) {
    ElMessage.error("加载系统消息失败");
  } finally {
    systemMessageLoading.value = false;
  }
};

// 添加系统消息已读处理函数
const handleSystemMessageRead = async (row) => {
  try {
    await markSystemMessageAsRead(row.id);
    ElMessage.success("标记已读成功");
    loadSystemMessages(); // 重新加载系统消息列表
  } catch (error) {
    ElMessage.error("标记已读失败");
  }
};

// 添加系统消息未读处理函数
const handleSystemMessageUnread = async (row) => {
  try {
    await markSystemMessageAsUnread(row.id);
    ElMessage.success("标记未读成功");
    loadSystemMessages(); // 重新加载系统消息列表
  } catch (error) {
    ElMessage.error("标记未读失败");
  }
};

const handleSystemPageChange = (page) => {
  systemPagination.value.currentPage = page;
  loadSystemMessages();
};

const handleSystemSizeChange = (size) => {
  systemPagination.value.pageSize = size;
  systemPagination.value.currentPage = 1;
  loadSystemMessages();
};

// 处理行点击
const handleRowClick = (row) => {
  // 只有当点击的不是操作按钮时才执行跳转
  if (!event.target.closest('.el-button')) {
    handleChat(row);
  }
};

/**
 * 查看系统消息全文
 */
// 添加对话框控制变量和当前选中的消息
const dialogVisible = ref(false);
const currentMessage = ref(null);

// 添加查看全文方法
const handleViewFullMessage = (row) => {
  currentMessage.value = row;
  dialogVisible.value = true;
};
</script>

<template>
  <PageContainer title="我的消息">
    <el-card class="message-manage">
      <!-- 添加标签页 -->
      <el-tabs v-model="activeTab" class="message-tabs">
        <el-tab-pane label="聊天消息" name="chat">
          <!-- 搜索表单 -->
          <el-form :model="searchForm" inline class="search-form">
            <el-form-item label="联系人">
              <el-input
                v-model="searchForm.receiverName"
                placeholder="请输入联系人姓名"
                clearable
                @keydown.enter="handleSearch"
              />
            </el-form-item>
            <el-form-item label="消息内容">
              <el-input
                v-model="searchForm.content"
                placeholder="搜索消息内容"
                clearable
                @keydown.enter="handleSearch"
              />
            </el-form-item>
            <el-form-item label="时间范围">
              <el-date-picker
                v-model="searchForm.startDate"
                type="date"
                placeholder="开始日期"
                @keydown.enter="handleSearch"
              />
              <span class="date-separator">-</span>
              <el-date-picker
                v-model="searchForm.endDate"
                type="date"
                placeholder="结束日期"
                @keydown.enter="handleSearch"
              />
            </el-form-item>
            <el-form-item>
              <el-button type="primary" :icon="Search" @click="handleSearch"
                >搜索</el-button
              >
              <el-button @click="handleReset">重置</el-button>
            </el-form-item>
          </el-form>

          <!-- 消息列表 -->
          <el-table
            :data="chatList"
            v-loading="loading"
            style="width: 100%"
            class="chat-list-table"
            @row-click="handleRowClick"
          >
            <!-- 联系人列 -->
            <el-table-column prop="receiver" label="联系人" min-width="200">
              <template #default="{ row }">
                <div class="contact-info">
                  <el-avatar :size="40" :src="row.receiverAvatar">
                    {{ row.receiverName?.charAt(0) }}
                  </el-avatar>
                  <div class="contact-detail">
                    <div class="contact-name">{{ row.receiverName }}</div>
                    <div class="contact-id">ID: {{ row.receiverId }}</div>
                  </div>
                </div>
              </template>
            </el-table-column>

            <!-- 最新消息列 -->
            <el-table-column
              :label="isSearchMode ? '消息内容' : '最新消息'"
              min-width="300"
            >
              <template #default="{ row }">
                <div class="message-preview">
                  <el-text class="message-content" :truncated="!isSearchMode">
                    {{ isSearchMode ? row.content : row.lastMessage }}
                  </el-text>
                  <div v-if="isSearchMode" class="message-meta">
                    <span class="message-time">{{ row.createdAt }}</span>
                    <el-tag v-if="row.isRead" size="small" type="info">已读</el-tag>
                    <el-tag v-else size="small" type="danger">未读</el-tag>
                  </div>
                </div>
              </template>
            </el-table-column>

            <!-- 未读消息数 -->
            <el-table-column
              v-if="!isSearchMode"
              prop="unreadCount"
              label="未读消息"
              width="100"
              align="center"
            >
              <template #default="{ row }">
                <el-badge
                  :value="row.unreadCount"
                  :hidden="!row.unreadCount"
                  class="unread-badge"
                >
                  <el-icon><Message /></el-icon>
                </el-badge>
              </template>
            </el-table-column>

            <!-- 最后联系时间 -->
            <el-table-column
              :prop="isSearchMode ? 'createdAt' : 'lastMessageTime'"
              :label="isSearchMode ? '发送时间' : '最后联系时间'"
              width="180"
            />

            <!-- 操作列 -->
            <el-table-column label="操作" width="240" fixed="right">
              <template #default="{ row }">
                <el-button
                  v-if="row.receiverId !== currentUserId"
                  type="primary"
                  :icon="ChatDotRound"
                  link
                  @click="handleChat(row)"
                >
                  聊天
                </el-button>
                <el-tooltip
                  v-if="!isSearchMode && row.unreadCount"
                  :content="`有 ${row.unreadCount} 条未读消息`"
                  placement="top"
                >
                  <el-button
                    type="success"
                    :icon="Check"
                    link
                    @click="handleMarkAsRead(row)"
                  >
                    标为已读
                  </el-button>
                </el-tooltip>
                <el-tooltip
                  v-if="!isSearchMode && (!row.unreadCount || row.unreadCount === 0)"
                  content="标记为未读"
                  placement="top"
                >
                  <el-button
                    type="warning"
                    :icon="Remove"
                    link
                    @click="handleMarkAsUnread(row)"
                  >
                    标为未读
                  </el-button>
                </el-tooltip>
              </template>
            </el-table-column>
          </el-table>

          <!-- 分页 -->
          <div class="pagination-container">
            <el-pagination
              v-model:current-page="pagination.currentPage"
              v-model:page-size="pagination.pageSize"
              :page-sizes="[10, 20, 50, 100]"
              :total="pagination.total"
              layout="total, sizes, prev, pager, next, jumper"
              @size-change="handleSizeChange"
              @current-change="handlePageChange"
            />
          </div>
        </el-tab-pane>

        <!-- 添加系统消息标签页 -->
        <el-tab-pane label="系统消息" name="system">
          <el-table
            :data="systemMessages"
            v-loading="systemMessageLoading"
            style="width: 100%"
          >
            <el-table-column
              prop="message"
              label="消息内容"
              min-width="300"
              show-overflow-tooltip
            >
            <template #default="{ row }">
      <div class="message-content-wrapper">
        {{ row.message.length > 50 ? row.message.slice(0, 50) + '...' : row.message }}
        <el-button
          v-if="row.message.length > 50"
          type="primary"
          link
          @click.stop="handleViewFullMessage(row)"
        >
          查看全文
        </el-button>
      </div>
    </template>
            </el-table-column>
            <el-table-column prop="createdAt" label="发送时间" width="180" />
            <el-table-column prop="senderName" label="发送者" width="150" />
            <el-table-column label="状态" width="100">
              <template #default="{ row }">
                <el-tag v-if="row.read" size="small" type="info">已读</el-tag>
                <el-tag v-else size="small" type="danger">未读</el-tag>
              </template>
            </el-table-column>
            <el-table-column label="操作" width="120">
              <template #default="{ row }">
                <el-button
                  v-if="!row.read"
                  type="info"
                  :icon="Check"
                  link
                  @click="handleSystemMessageRead(row)"
                >
                  标为已读
                </el-button>
                <el-button
                  v-if="row.read"
                  type="warning"
                  :icon="Remove"
                  link
                  @click="handleSystemMessageUnread(row)"
                >
                  标为未读
                </el-button>
              </template>
            </el-table-column>
          </el-table>

          <!-- 系统消息分页 -->
          <div class="pagination-container">
            <el-pagination
              v-model:current-page="systemPagination.currentPage"
              v-model:page-size="systemPagination.pageSize"
              :page-sizes="[10, 20, 50, 100]"
              :total="systemPagination.total"
              layout="total, sizes, prev, pager, next, jumper"
              @size-change="handleSystemSizeChange"
              @current-change="handleSystemPageChange"
            />
          </div>

          <!-- 添加查看全文对话框 -->
  <el-dialog
    v-model="dialogVisible"
    title="消息详情"
    width="50%"
    :close-on-click-modal="false"
    class="message-dialog"
  >
    <div class="message-detail">
      <div class="message-info">
        <span class="label">发送时间：</span>
        <span>{{ currentMessage?.createdAt }}</span>
      </div>
      <div class="message-info">
        <span class="label">发送者：</span>
        <span>{{ currentMessage?.senderName }}</span>
      </div>
      <div class="message-content">
        <div class="label">消息内容：</div>
        <div class="content">{{ currentMessage?.message }}</div>
      </div>
    </div>
  </el-dialog>
        </el-tab-pane>
      </el-tabs>
    </el-card>
  </PageContainer>
</template>

<style lang="scss" scoped>
.message-manage {
  .search-form {
    margin-bottom: 20px;
    .date-separator {
      margin: 0 8px;
    }
  }

  .chat-list-table {
    :deep(.el-table__row) {
      cursor: pointer;
      transition: background-color 0.3s;

      &:hover {
        background-color: var(--el-fill-color-lighter);
      }
    }

    /* 防止操作按钮区域触发行点击 */
  :deep(.el-table__cell:last-child) {
    pointer-events: none;

    .cell {
      pointer-events: auto;
    }
  }
  }

  .contact-info {
    display: flex;
    align-items: center;
    gap: 12px;

    .contact-detail {
      .contact-name {
        font-weight: 500;
        margin-bottom: 4px;
      }

      .contact-id {
        font-size: 12px;
        color: var(--el-text-color-secondary);
      }
    }
  }

  .message-preview {
    .message-content {
      color: var(--el-text-color-regular);
      font-size: 14px;
    }
  }

  .unread-badge {
    position: relative;
    display: inline-flex;
    align-items: center;

    :deep(.el-badge__content) {
      position: absolute;
      top: 4px;
      right: 8px;
      z-index: 10;
      background-color: var(--el-color-danger);
      color: white;
      font-weight: bold;
      min-width: 10px;
      height: 12px;
      line-height: 10px;
      padding: 0 4px;
    }

    .el-icon {
      font-size: 18px;
      color: var(--el-text-color-secondary);
    }
  }

  .pagination-container {
    margin-top: 20px;
    display: flex;
    justify-content: flex-end;
  }

  .message-tabs {
    :deep(.el-tabs__content) {
      padding-top: 20px;
    }
  }
}

.message-preview {
  .message-content {
    color: var(--el-text-color-regular);
    font-size: 14px;
  }

  .message-meta {
    margin-top: 8px;
    display: flex;
    align-items: center;
    gap: 8px;

    .message-time {
      color: var(--el-text-color-secondary);
      font-size: 12px;
    }
  }
}

.message-content-wrapper {
  display: flex;
  align-items: center;
  gap: 8px;
}

.message-dialog {
  .message-detail {
    padding: 20px;

    .message-info {
      margin-bottom: 16px;
      display: flex;
      align-items: center;

      .label {
        font-weight: 500;
        margin-right: 8px;
        color: var(--el-text-color-regular);
        width: 80px;
      }
    }

    .message-content {
      .label {
        font-weight: 500;
        color: var(--el-text-color-regular);
        margin-bottom: 8px;
      }

      .content {
        line-height: 1.6;
        white-space: pre-wrap;
        word-break: break-all;
        background-color: var(--el-fill-color-lighter);
        padding: 16px;
        border-radius: 4px;
      }
    }
  }
}
</style>
