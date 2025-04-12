<script setup>
import { ref, onMounted } from "vue";
import { ElMessage, ElMessageBox } from "element-plus";
import { Search, Warning, Download } from "@element-plus/icons-vue";
import PageContainer from "@/components/PageContainer.vue";
import {
  findAll,
  deleteById,
  searchMessages,
  setMessageOperation,
} from "@/api/user/message";

// 消息列表数据
const messageList = ref([]);
const loading = ref(false);

// 分页参数
const pagination = ref({
  currentPage: 1,
  pageSize: 10,
  total: 0,
});

// 搜索条件
const searchForm = ref({
  senderName: "",
  receiverName: "",
  content: "",
  startDate: "",
  endDate: "",
});

// 加载消息列表
const loadMessages = async () => {
  loading.value = true;
  try {
    const res = await findAll({});
    if (res.data.status === 200) {
      messageList.value = res.data.data;
      pagination.value.total = res.data.data.length;
    } else {
      ElMessage.error("加载失败");
    }
  } catch (error) {
    ElMessage.error("加载失败");
  } finally {
    loading.value = false;
  }
};

// 处理搜索
const handleSearch = async () => {
  loading.value = true;
  try {
    const today = new Date();
    today.setHours(0, 0, 0, 0);

    let startDate = searchForm.value.startDate;
    let endDate = searchForm.value.endDate;

    // 如果只有开始时间，结束时间设为今天
    if (startDate && !endDate) {
      endDate = today;
    }
    // 如果只有结束时间，开始时间设为今天
    else if (!startDate && endDate) {
      startDate = today;
    }

    const params = {
      senderName: searchForm.value.senderName,
      receiverName: searchForm.value.receiverName,
      content: searchForm.value.content,
      startDate: startDate,
      endDate: endDate,
      page: pagination.value.currentPage - 1,
      size: pagination.value.pageSize,
    };

    const res = await searchMessages(params);
    if (res.data.status === 200) {
      messageList.value = res.data.data.content;
      pagination.value.total = res.data.data.totalElements;
    } else {
      ElMessage.error("搜索失败");
    }
  } catch (error) {
    ElMessage.error("搜索失败");
  } finally {
    loading.value = false;
  }
};
// 处理重置
const handleReset = () => {
  searchForm.value = {
    senderName: "",
    receiverName: "",
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
const deleteMessage = async (row) => {
  try {
    await ElMessageBox.confirm("确定要删除这条消息吗？", "提示", {
      confirmButtonText: "确定",
      cancelButtonText: "取消",
      type: "warning",
    });
    // 修正API调用名称
    await deleteById(row.messageId);
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

/**
 * 查看可疑原因
 */
const viewDetail = (row) => {
  ElMessageBox.alert(row.reason, "消息详情", {
    confirmButtonText: "确定",
    customClass: "message-detail-dialog",
  });
};

/**
 * 审核考虑因素列表
 */

// 添加折叠状态控制
const reviewCollapse = ref([]);
// 审核考虑因素
const reviewFactors = ref([
  {
    title: "敏感词检测",
    description: "基于本地敏感词库和百度云API的双重检测，确保消息不包含非法、违规内容。",
    icon: "Warning",
  },
  {
    title: "消息长度",
    description: "单条消息长度限制为500字符，防止过长的垃圾信息。",
    icon: "Document",
  },
  {
    title: "链接检测",
    description: "每条消息最多允许3个链接，且链接格式必须合法。",
    icon: "Link",
  },
  {
    title: "特殊字符",
    description: "限制特殊字符数量，防止恶意代码注入。",
    icon: "Star",
  },
  {
    title: "连续字符",
    description: "检测过多连续相同字符，防止刷屏行为。",
    icon: "CopyDocument",
  },
  {
    title: "段落结构",
    description: "限制段落数量，防止恶意分段发送。",
    icon: "Notebook",
  },
]);

/**
 * 处理消息审核状态
 */
const handleOperation = async (row) => {
  const newStatus = !row.operation;
  try {
    await setMessageOperation(row.messageId, newStatus);
    // 直接更新当前行的状态，无需重新加载整个列表
    row.operation = newStatus;
    ElMessage.success(`消息已${newStatus ? "标记为已审核" : "标记为未审核"}`);
    loadMessages(); // 重新加载列表
  } catch (error) {
    ElMessage.error("操作失败");
  }
};

/**
 * 导出功能
 */
const handleExport = () => {
  // 准备CSV数据
  const headers = [
    "发送者",
    "发送者ID",
    "接收者",
    "接收者ID",
    "消息内容",
    "可疑度",
    "可疑原因",
    "审核状态",
    "发送时间",
  ];
  const csvContent = messageList.value.map((item) => {
    return [
      item.senderName,
      item.senderId,
      item.receiverName,
      item.receiverId,
      item.content,
      item.score + "%",
      item.reason || "无",
      item.operation ? "已审核" : "未审核",
      item.createTime,
    ].join(",");
  });

  // 组合CSV内容
  const csv = [headers.join(","), ...csvContent].join("\n");

  // 创建Blob对象
  const blob = new Blob(["\uFEFF" + csv], { type: "text/csv;charset=utf-8;" });

  // 创建下载链接
  const link = document.createElement("a");
  link.href = URL.createObjectURL(blob);
  link.download = `消息记录_${new Date().toLocaleDateString()}.csv`;

  // 触发下载
  document.body.appendChild(link);
  link.click();
  document.body.removeChild(link);
};
</script>

<template>
  <PageContainer title="消息管理">
    <!-- 审核说明卡片 -->
    <el-collapse v-model="reviewCollapse" class="review-section">
      <el-collapse-item name="review">
        <template #title>
          <div class="review-header">
            <el-icon><Warning /></el-icon>
            <span>消息审核标准说明</span>
          </div>
        </template>
        <el-row :gutter="20">
          <el-col
            v-for="(factor, index) in reviewFactors"
            :key="index"
            :xs="24"
            :sm="12"
            :md="8"
            :lg="6"
          >
            <el-card class="factor-card" shadow="hover">
              <div class="factor-header">
                <el-icon :size="24">
                  <component :is="factor.icon" />
                </el-icon>
                <h4>{{ factor.title }}</h4>
              </div>
              <p class="factor-description">{{ factor.description }}</p>
            </el-card>
          </el-col>
        </el-row>
      </el-collapse-item>
    </el-collapse>

    <el-card class="message-manage">
      <!-- 搜索表单 -->
      <el-form :model="searchForm" inline class="search-form">
        <el-form-item label="发送者">
          <el-input
            v-model="searchForm.senderName"
            placeholder="请输入发送者姓名"
            clearable
            @keydown.enter="handleSearch"
          />
        </el-form-item>
        <el-form-item label="接收者">
          <el-input
            v-model="searchForm.receiverName"
            placeholder="请输入接收者姓名"
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
          <el-button type="primary" :icon="Search" @click="handleSearch">搜索</el-button>
          <el-button @click="handleReset">重置</el-button>
          <el-button
            type="success"
            :icon="Download"
            @click="handleExport"
            :disabled="!messageList.length"
          >
            导出记录
          </el-button>
        </el-form-item>
      </el-form>

      <!-- 消息列表 -->
      <el-table
        :data="messageList"
        v-loading="loading"
        style="width: 100%"
        class="chat-list-table"
      >
        <!-- 发送者列 -->
        <el-table-column prop="sender" label="发送者" min-width="200">
          <template #default="{ row }">
            <div class="contact-info">
              <el-avatar :size="40" :src="row.senderAvatar">
                {{ row.senderName?.charAt(0) }}
              </el-avatar>
              <div class="contact-detail">
                <div class="contact-name">{{ row.senderName }}</div>
                <div class="contact-id">ID: {{ row.senderId }}</div>
              </div>
            </div>
          </template>
        </el-table-column>

        <!-- 接收者列 -->
        <el-table-column prop="receiver" label="接收者" min-width="200">
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

        <!-- 消息内容列 -->
        <el-table-column prop="content" label="消息内容" min-width="300">
          <template #default="{ row }">
            <div class="message-preview">
              <div class="message-content-row">
                <el-text class="message-content">{{ row.content }}</el-text>
                <div class="message-tags">
                  <el-tag type="danger" size="small">可疑度: {{ row.score }}%</el-tag>
                </div>
              </div>
            </div>
          </template>
        </el-table-column>

        <!-- 操作列 -->
        <el-table-column label="操作" width="280" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link @click="viewDetail(row)">查看详情</el-button>
            <el-button
              :type="row.operation ? 'warning' : 'success'"
              link
              @click="handleOperation(row)"
            >
              {{ row.operation ? "标记未审核" : "标记已审核" }}
            </el-button>
            <el-button type="danger" link @click="deleteMessage(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页 -->
      <div class="pagination-container">
        <el-pagination
          :current-page="pagination.currentPage"
          :page-size="pagination.pageSize"
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

  .pagination-container {
    margin-top: 20px;
    display: flex;
    justify-content: flex-end;
  }
}

.review-intro {
  margin-bottom: 20px;

  h3 {
    margin-bottom: 20px;
    color: var(--el-text-color-primary);
  }
}

.factor-card {
  margin-bottom: 20px;
  height: 100%;

  .factor-header {
    display: flex;
    align-items: center;
    margin-bottom: 10px;

    h4 {
      margin-left: 10px;
      margin-bottom: 0;
    }
  }

  .factor-description {
    color: var(--el-text-color-secondary);
    font-size: 14px;
    line-height: 1.6;
  }
}
:deep(.message-detail-dialog) {
  .el-message-box__content {
    max-height: 400px;
    overflow-y: auto;
  }
}

.message-preview {
  .message-content-row {
    display: flex;
    align-items: center;
    gap: 12px;

    .message-content {
      flex: 1;
      font-size: 14px;
    }

    .message-tags {
      flex-shrink: 0;
    }
  }
}
</style>
