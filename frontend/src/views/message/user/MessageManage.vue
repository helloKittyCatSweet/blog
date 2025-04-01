<script setup>
import { ref, onMounted } from "vue";
import { ElMessage } from "element-plus";
import PageContainer from "@/components/PageContainer.vue";
import { Search, Message, ChatDotRound } from "@element-plus/icons-vue";
import { findContactedUserNames } from "@/api/user/message";
import { useRouter } from "vue-router";
import { USER_MESSAGE_DETAIL_PATH } from "@/constants/routes-constants";
import { useMessageStore } from "@/stores/modules/message";

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

// 加载消息列表
const loadMessages = async () => {
  loading.value = true;
  try {
    const { data } = await findContactedUserNames();
    console.log(data);
    // 将用户数据转为换聊天列表格式
    chatList.value = data.data.map((item) => ({
      receiverId: item.userId,
      receiverName: item.username,
      receiverAvatar: item.avatar,
      lastMessage: item.message,
      unreadCount: item.unreadCount,
      lastMessageTime: item.lastMessageTime == null ? "-" : item.lastMessageTime,
    }));

    messageStore.setContactList(chatList.value);
  } catch (error) {
    ElMessage.error("加载消息列表失败");
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
  pagination.value.currentPage = 1;
  loadMessages();
};

// 删除消息
const handleDelete = async (row) => {
  try {
    await ElMessageBox.confirm("确定要删除这条消息吗？", "提示", {
      type: "warning",
    });
    // TODO: 调用删除API
    // await deleteMessage(row.messageId);
    ElMessage.success("删除成功");
    loadMessages();
  } catch (error) {
    if (error !== "cancel") {
      ElMessage.error("删除失败");
    }
  }
};

onMounted(() => {
  loadMessages();
});

const router = useRouter();
// 处理聊天按钮点击
const messageStore = useMessageStore();
const handleChat = (row) => {
  messageStore.setCurrentReceiver({
    receiverId: row.receiverId,
    receiverName: row.receiverName,
    receiverAvatar: row.receiverAvatar,
  });
  router.push({
    path: USER_MESSAGE_DETAIL_PATH.replace(":receiverId", row.receiverId),
  });
};
</script>

<template>
  <PageContainer title="我的消息">
    <el-card class="message-manage">
      <!-- 搜索表单 -->
      <el-form :model="searchForm" inline class="search-form">
        <el-form-item label="联系人">
          <el-input
            v-model="searchForm.receiverName"
            placeholder="请输入联系人姓名"
            clearable
          />
        </el-form-item>
        <el-form-item label="消息内容">
          <el-input v-model="searchForm.content" placeholder="搜索消息内容" clearable />
        </el-form-item>
        <el-form-item label="时间范围">
          <el-date-picker
            v-model="searchForm.startDate"
            type="date"
            placeholder="开始日期"
          />
          <span class="date-separator">-</span>
          <el-date-picker
            v-model="searchForm.endDate"
            type="date"
            placeholder="结束日期"
          />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" :icon="Search" @click="handleSearch">搜索</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>

      <!-- 消息列表 -->
      <el-table
        :data="chatList"
        v-loading="loading"
        style="width: 100%"
        class="chat-list-table"
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
        <el-table-column prop="lastMessage" label="最新消息" min-width="300">
          <template #default="{ row }">
            <div class="message-preview">
              <el-text class="message-content" truncated>
                {{ row.lastMessage }}
              </el-text>
            </div>
          </template>
        </el-table-column>

        <!-- 未读消息数 -->
        <el-table-column prop="unreadCount" label="未读消息" width="100" align="center">
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
        <el-table-column prop="lastMessageTime" label="最后联系时间" width="180" />

        <!-- 操作列 -->
        <el-table-column label="操作" width="120" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" :icon="ChatDotRound" link @click="handleChat(row)">
              聊天
            </el-button>
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

    :deep(.el-table__cell) {
      overflow: visible;
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
}
</style>
