<script setup>
import { ref, onMounted, onUnmounted, computed } from "vue";
import { ElMessage, ElDrawer, ElButton, ElInput, ElPagination } from "element-plus";
import { useUserStore } from "@/stores/modules/user";
import {
  sendSystemMessage,
  connectWebSocket,
  disconnectWebSocket,
  getAdminSystemMessages,
  deleteSystemMessage,
  checkWebSocketConnection,
} from "@/api/user/message";
import { ROLES } from "@/constants/role-constants";

// 表单数据
const form = ref({
  message: "",
  targetRole: ROLES.USER, // 默认发送给所有用户
});

// 当前用户角色
const userStore = useUserStore();
const userRoles = userStore.user.roles;

// 角色选项，根据用户角色进行过滤
const roleOptions = computed(() => {
  if (userRoles.some((role) => role.authority === ROLES.SYSTEM_ADMINISTRATOR)) {
    return [
      { label: "普通用户", value: ROLES.USER },
      { label: "分类管理员", value: ROLES.CATEGORY_MANAGER },
      { label: "标签管理员", value: ROLES.TAG_MANAGER },
      { label: "文章管理员", value: ROLES.POST_MANAGER },
      { label: "评论管理员", value: ROLES.COMMENT_MANAGER },
      { label: "消息管理员", value: ROLES.MESSAGE_MANAGER },
      { label: "举报管理员", value: ROLES.REPORT_MANAGER },
      { label: "角色管理员", value: ROLES.ROLE_MANAGER },
      { label: "系统管理员", value: ROLES.SYSTEM_ADMINISTRATOR },
    ];
  } else {
    const availableRoles = [{ label: "普通用户", value: ROLES.USER }];

    userRoles.forEach((role) => {
      if (roleOptionsMap[role.authority]) {
        availableRoles.push({
          label: roleOptionsMap[role.authority],
          value: role.authority,
        });
      }
    });

    return availableRoles;
  }
});

// 角色选项映射
const roleOptionsMap = {
  [ROLES.USER]: "普通用户",
  [ROLES.CATEGORY_MANAGER]: "分类管理员",
  [ROLES.PERMISSION_MANAGER]: "权限管理员",
  [ROLES.PERMISSION_MAPPING_MANAGER]: "权限映射管理员",
  [ROLES.TAG_MANAGER]: "标签管理员",
  [ROLES.POST_MANAGER]: "文章管理员",
  [ROLES.USER_ACTIVITY_MANAGER]: "用户活动管理员",
  [ROLES.COMMENT_MANAGER]: "评论管理员",
  [ROLES.MESSAGE_MANAGER]: "消息管理员",
  [ROLES.REPORT_MANAGER]: "举报管理员",
  [ROLES.ROLE_MANAGER]: "角色管理员",
  [ROLES.SYSTEM_ADMINISTRATOR]: "系统管理员",
};

/**
 * 抽屉
 */
const drawerVisible = ref(false);

// 发送系统消息
const handleSend = async () => {
  if (!form.value.message.trim()) {
    ElMessage.warning("请输入消息内容");
    return;
  }

  try {
    console.log("正在发送系统消息:", {
      message: form.value.message.trim(),
      targetRole: form.value.targetRole,
    });

    // 添加确认对话框
    const targetRoleLabel = roleOptions.value.find(
      (option) => option.value === form.value.targetRole
    )?.label;

    await ElMessageBox.confirm(
      `确定要向 ${targetRoleLabel} 发送以下消息吗？\n\n${form.value.message.trim()}`,
      "发送确认",
      {
        confirmButtonText: "确定发送",
        cancelButtonText: "取消",
        type: "info",
        dangerouslyUseHTMLString: false,
      }
    );

    const result = await sendSystemMessage({
      message: form.value.message.trim(),
      targetRole: form.value.targetRole,
    });

    if (result) {
      ElMessage.success("系统消息发送成功");
      form.value.message = "";
      drawerVisible.value = false; // 关闭抽屉
      loadMessageHistory(); // 重新加载消息历史
    } else {
      ElMessage.error("系统消息发送失败");
    }
  } catch (error) {
    console.error("系统消息发送失败:", error);
    ElMessage.error(error.message || "系统消息发送失败");
  }
};

// 初始化WebSocket连接
onMounted(async () => {
  loadMessageHistory();

  try {
    // 检查是否已经存在连接
    const hasConnection = await checkWebSocketConnection();
    if (hasConnection) {
      // 如果存在连接，先断开
      disconnectWebSocket();
      await new Promise((resolve) => setTimeout(resolve, 1000));
    }

    await connectWebSocket({
      userId: userStore.user.id,
      messageCallback: (message) => {
        console.log("收到消息:", message);
      },
      statusCallback: (status) => {
        console.log("状态更新:", status);
      },
    });
  } catch (error) {
    console.error("WebSocket连接失败:", error);
    ElMessage.error("WebSocket连接失败，系统消息可能无法发送");
  }

  console.log("ROLES.SYSTEM_ADMINISTRATOR:", ROLES.SYSTEM_ADMINISTRATOR);
  console.log("用户角色列表:", userRoles);
  console.log("是否包含系统管理员:", userRoles.includes(ROLES.SYSTEM_ADMINISTRATOR));
});

// 组件卸载时断开连接
onUnmounted(() => {
  disconnectWebSocket();
});

/**
 * 管理员发送的消息历史
 */
const messageHistory = ref([]);

// 加载系统消息历史
const loadMessageHistory = async () => {
  try {
    const response = await getAdminSystemMessages();
    messageHistory.value = response.data.data.content;
    totalMessages.value = response.data.data.totalElements;
  } catch (error) {
    console.error("获取消息历史失败:", error);
    ElMessage.error("获取消息历史失败");
  }
};

/**
 * 搜索
 */
const searchKeyword = ref("");
const searchTargetRole = ref("");

// 搜索功能
const handleSearch = () => {
  // 触发搜索逻辑
  console.log("搜索:", searchKeyword.value, searchTargetRole.value);
};

// 重置搜索条件
const resetSearch = () => {
  searchKeyword.value = "";
  searchTargetRole.value = "";
};

// 过滤消息历史
const filteredMessages = computed(() => {
  return messageHistory.value.filter(
    (message) =>
      message.message.includes(searchKeyword.value) &&
      (searchTargetRole.value === "" || message.targetRole === searchTargetRole.value)
  );
});

/**
 * 分页
 */
const currentPage = ref(1);
const pageSize = ref(10);
const totalMessages = ref(0);

// 当前页消息
const paginatedMessages = computed(() => {
  const start = (currentPage.value - 1) * pageSize.value;
  const end = start + pageSize.value;
  return filteredMessages.value.slice(start, end);
});

/**
 * 删除消息
 */
// 删除消息
const deleteMessage = async (messageId) => {
  try {
    // 添加确认对话框
    await ElMessageBox.confirm("确定要删除这条消息吗？", "提示", {
      confirmButtonText: "确定",
      cancelButtonText: "取消",
      type: "warning",
    });

    await deleteSystemMessage(messageId);
    ElMessage.success("消息删除成功");
    loadMessageHistory();
  } catch (error) {
    if (error !== "cancel") {
      // 用户取消时不显示错误提示
      console.error("删除消息失败:", error);
      ElMessage.error("删除消息失败");
    }
  }
};

/**
 * 导出
 */
const exportMessages = () => {
  const headers = ["消息内容", "接收角色", "发送时间", "发送者", "发送者角色"];
  const csvContent = paginatedMessages.value.map((message) => {
    // 使用 roleOptionsMap 获取映射后的角色名称
    const targetRoleLabel = roleOptionsMap[message.targetRole] || "普通用户";
    const senderRolesLabel = message.senderRoles
      .filter((role) => role.authority !== ROLES.USER)
      .map((role) => roleOptionsMap[role.authority] || role.authority)
      .join(", ");
    return [
      message.message,
      targetRoleLabel,
      message.createdAt,
      message.senderName,
      senderRolesLabel,
    ].join(",");
  });

  const csvData = [headers.join(","), ...csvContent].join("\n");
  const bom = "\uFEFF"; // BOM for UTF-8
  const blob = new Blob([bom + csvData], { type: "text/csv;charset=utf-8;" });
  const link = document.createElement("a");
  const url = URL.createObjectURL(blob);
  link.setAttribute("href", url);
  link.setAttribute("download", "系统消息记录.csv");
  link.style.visibility = "hidden";
  document.body.appendChild(link);
  link.click();
  document.body.removeChild(link);
};

/**
 * 重新连接WebSocket
 */
// 添加重新连接WebSocket的处理函数
// 重新连接WebSocket
const handleReconnectWebSocket = async () => {
  try {
    // 检查是否已经存在连接
    const hasConnection = await checkWebSocketConnection();
    if (hasConnection) {
      // 如果存在连接，先断开
      disconnectWebSocket();
      await new Promise((resolve) => setTimeout(resolve, 1000));
    }

    // 重新建立连接
    await connectWebSocket({
      userId: userStore.user.id,
      messageCallback: (message) => {
        console.log("收到消息:", message);
      },
      statusCallback: (status) => {
        console.log("状态更新:", status);
      },
    });
    ElMessage.success("消息服务重新连接成功");
  } catch (error) {
    console.error("WebSocket重新连接失败:", error);
    ElMessage.error("消息服务重新连接失败，请稍后再试");
  }
};
</script>

<template>
  <!-- 添加面包屑导航 -->
  <PageContainer title="系统消息管理" breadcrumb>
    <el-card class="system-message">
      <template #header>
        <div class="card-header">
          <h3>系统消息管理</h3>
          <el-button type="primary" @click="drawerVisible = true">发送系统消息</el-button>
        </div>
      </template>

      <!-- 搜索框 -->
      <div class="search-bar">
        <el-input
          v-model="searchKeyword"
          placeholder="搜索消息内容"
          clearable
          class="search-input"
        />
        <el-select
          v-model="searchTargetRole"
          placeholder="选择接收角色"
          clearable
          class="search-select"
        >
          <el-option
            v-for="item in roleOptions"
            :key="item.value"
            :label="item.label"
            :value="item.value"
          />
        </el-select>
        <el-button type="primary" @click="handleSearch">搜索</el-button>
        <el-button @click="resetSearch">重置</el-button>
        <el-button type="primary" @click="exportMessages">导出消息记录</el-button>
      </div>

      <!-- 消息历史列表 -->
      <el-card class="message-history">
        <template #header>
          <div class="card-header">
            <h3>发送历史</h3>
          </div>
        </template>

        <el-empty
          v-if="paginatedMessages.length === 0"
          :image-size="100"
          description="暂无发送记录"
        />

        <el-table v-else :data="paginatedMessages" style="width: 100%">
          <el-table-column
            prop="message"
            label="消息内容"
            min-width="300"
            show-overflow-tooltip
          />
          <el-table-column prop="targetRole" label="接收角色" width="200">
            <template #default="{ row }">
              {{
                roleOptions.find((option) => option.value === row.targetRole)?.label ||
                row.targetRole
              }}
            </template>
          </el-table-column>
          <el-table-column prop="createdAt" label="发送时间" width="180" />
          <el-table-column prop="senderName" label="发送者" width="150" />
          <el-table-column label="发送者角色" width="200">
            <template #default="{ row }">
              {{
                row.senderRoles
                  .filter((role) => role !== ROLES.USER)
                  .map((role) => roleOptionsMap[role] || role)
                  .join(", ")
              }}
            </template>
          </el-table-column>
          <!-- 新增操作列 -->
          <el-table-column label="操作" width="150">
            <template #default="{ row }">
              <el-button
                v-if="
                  userRoles.some((role) => role.authority === ROLES.SYSTEM_ADMINISTRATOR)
                "
                type="danger"
                link
                @click="deleteMessage(row.id)"
              >
                删除
              </el-button>
            </template>
          </el-table-column>
        </el-table>

        <!-- 分页 -->
        <el-pagination
          v-model:current-page="currentPage"
          v-model:page-size="pageSize"
          :total="filteredMessages.length"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="pageSize = $event"
          @current-change="currentPage = $event"
        />
      </el-card>

      <!-- 发送系统消息抽屉 -->
      <el-drawer
        v-model="drawerVisible"
        title="发送系统消息"
        size="50%"
        :with-header="true"
      >
        <el-form :model="form" label-width="100px">
          <el-form-item label="接收角色" required>
            <el-select
              v-model="form.targetRole"
              placeholder="请选择接收角色"
              class="w-full"
            >
              <el-option
                v-for="item in roleOptions"
                :key="item.value"
                :label="item.label"
                :value="item.value"
              />
            </el-select>
          </el-form-item>

          <el-form-item label="消息内容" required>
            <el-input
              v-model="form.message"
              type="textarea"
              :rows="5"
              placeholder="请输入系统消息内容"
              maxlength="500"
              show-word-limit
            />
          </el-form-item>

          <el-form-item>
            <el-button
              type="primary"
              @click="handleSend"
              :disabled="!form.message.trim()"
            >
              发送消息
            </el-button>
            <el-button type="warning" @click="handleReconnectWebSocket">
              重新连接消息服务
            </el-button>
          </el-form-item>
        </el-form>
      </el-drawer>
    </el-card>
  </PageContainer>
</template>

<style lang="scss" scoped>
.system-message {
  max-width: 800px;
  margin: 20px auto;

  .card-header {
    display: flex;
    justify-content: space-between;
    align-items: center;

    h3 {
      margin: 0;
      font-size: 18px;
      font-weight: 500;
    }
  }

  :deep(.el-form-item__content) {
    flex-wrap: nowrap;
  }

  .w-full {
    width: 100%;
  }
}

.message-history {
  max-width: 800px;
  margin: 20px auto;

  .card-header {
    display: flex;
    justify-content: space-between;
    align-items: center;

    h3 {
      margin: 0;
      font-size: 18px;
      font-weight: 500;
    }
  }
}

.search-bar {
  display: flex;
  align-items: center;
  margin-bottom: 20px;

  .search-input,
  .search-select {
    margin-right: 10px;
  }
}
</style>
