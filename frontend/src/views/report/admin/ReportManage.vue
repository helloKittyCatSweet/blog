<script setup>
import { ref, onMounted } from "vue";
import { ElMessage, ElMessageBox } from "element-plus";
import { Search, View, Delete, InfoFilled } from "@element-plus/icons-vue";
import PageContainer from "@/components/PageContainer.vue";
import { review, findAll, searchReports, deleteById } from "@/api/post/report";
import * as XLSX from "xlsx";
import table2Excel from "js-table2excel";
import { formatDate } from "@/utils/date.js";
import { debounce } from "lodash-es";

// 表格数据
const loading = ref(false);
const tableData = ref([]);
const total = ref(0);
const currentPage = ref(1);
const pageSize = ref(10);

// 搜索条件
const searchKey = ref("");
const statusFilter = ref("");

// 审核对话框
const reviewDialogVisible = ref(false);
const formRef = ref(null);
const reviewForm = ref({
  reportId: null,
  approved: true,
  comment: "",
});

const rules = {
  comment: [{ required: true, message: "请输入审核意见", trigger: "blur" }],
};

// 获取举报列表
const getReportList = async () => {
  loading.value = true;
  try {
    let res;
    const params = {
      page: currentPage.value - 1, // 前端页码从1开始，后端从0开始
      size: pageSize.value,
      sort: 'createdAt,desc'
    };

    if (searchKey.value.trim() || statusFilter.value) {
      params.keyword = searchKey.value.trim();
      params.status = statusFilter.value;
      params.isAdmin = true;
      res = await searchReports(params);
    } else {
      res = await findAll(params);
    }

    if (res.data.status === 200) {
      if (Array.isArray(res.data.data.content)) {
        // 处理分页数据
        tableData.value = res.data.data.content;
        total.value = res.data.data.totalElements;
      } else if (Array.isArray(res.data.data)) {
        // 处理非分页数据
        tableData.value = res.data.data;
        total.value = res.data.data.length;
      }
    } else {
      ElMessage.error("获取数据失败：" + res.data.message);
      tableData.value = [];
      total.value = 0;
    }
  } catch (error) {
    ElMessage.error("获取数据失败：" + error.message);
    tableData.value = [];
    total.value = 0;
  }
  loading.value = false;
};

const debouncedGetList = debounce(getReportList, 300);

// 状态处理
const getStatusType = (status) => {
  const map = {
    PENDING: "warning",
    REVIEWING: "info",
    APPROVED: "success",
    REJECTED: "danger",
  };
  return map[status];
};

const getStatusLabel = (status) => {
  const map = {
    PENDING: "待处理",
    REVIEWING: "审核中",
    APPROVED: "已通过",
    REJECTED: "已驳回",
  };
  return map[status];
};

// 处理审核
const handleReview = (row) => {
  reviewForm.value = {
    reportId: row.reportId,
    approved: true,
    comment: "",
  };
  reviewDialogVisible.value = true;
};

// 提交审核
const submitReview = async () => {
  if (!formRef.value) return;
  await formRef.value.validate(async (valid) => {
    if (valid) {
      try {
        const res = await review(reviewForm.value.reportId, {
          approved: reviewForm.value.approved,
          comment: reviewForm.value.comment,
        });
        if (res.data.status === 200) {
          ElMessage.success("审核成功");
          reviewDialogVisible.value = false;
          formRef.value.resetFields();
          await getReportList();
        }
      } catch (error) {
        ElMessage.error("审核失败");
      }
    }
  });
};

// 查看文章
const viewPost = (postId) => {
  window.open(`/post/${postId}`, "_blank");
};

// 分页处理
const handleSizeChange = (val) => {
  pageSize.value = val;
  getReportList();
};

const handleCurrentChange = (val) => {
  currentPage.value = val;
  getReportList();
};

onMounted(() => {
  getReportList();
});

/**
 * 搜索
 */
const reasonFilter = ref("");

const handleSearch = async () => {
  loading.value = true;
  try {
    const params = {
      keyword: searchKey.value.trim(),
      status: statusFilter.value,
      reason: reasonFilter.value,
      isAdmin: true,
      page: currentPage.value,
      pageSize: pageSize.value,
    };
    console.log("搜索参数：", params);
    const res = await searchReports(params);
    if (res.data.status === 200) {
      tableData.value = res.data.data || [];
      total.value = Number(res.data.data.length) || 0;
    }
  } catch (error) {
    console.error("搜索错误：", error.response?.data || error);
    ElMessage.error("搜索失败");
  }
  loading.value = false;
};

// 重置搜索
const resetSearch = () => {
  searchKey.value = "";
  statusFilter.value = "";
  reasonFilter.value = "";
  getReportList();
};

/**
 * 删除举报
 */
// 删除举报记录
const deleteReport = async (row) => {
  // 只有状态为已通过或已驳回时才允许删除
  if (["APPROVED", "REJECTED"].includes(row.status)) {
    ElMessageBox.confirm("确定要删除这条举报吗？", "提示", {
      confirmButtonText: "确定",
      cancelButtonText: "取消",
      type: "warning",
    }).then(async () => {
      try {
        console.log("reportId:", row.reportId);
        const response = await deleteById(row.reportId);
        if (response.data.status === 200) {
          ElMessage.success("删除成功");
          handleSearch();
        } else {
          ElMessage.error("删除失败");
        }
      } catch (error) {
        ElMessage.error("删除失败");
      }
    });
  } else {
    ElMessage.warning("只有已通过或已驳回的举报才能删除");
  }
};

const batchDelete = async () => {
  if (multipleSelection.value.length === 0) return;

  // 检查选中项的状态
  const invalidItems = multipleSelection.value.filter(
    (item) => !["APPROVED", "REJECTED"].includes(item.status)
  );

  if (invalidItems.length > 0) {
    ElMessage.warning("只能删除已通过或已驳回的举报");
    return;
  }

  ElMessageBox.confirm(
    `确定要删除这${multipleSelection.value.length}条举报吗？`,
    "提示",
    {
      confirmButtonText: "确定",
      cancelButtonText: "取消",
      type: "warning",
    }
  ).then(async () => {
    try {
      const ids = multipleSelection.value.map((item) => item.reportId);
      await Promise.all(ids.map((id) => deleteById(id)));
      ElMessage.success(`已删除${ids.length}条举报`);
      getReportList();
    } catch (error) {
      ElMessage.error("批量删除失败");
    }
  });
};

/**
 * 导出举报
 */
// 导出 Excel
const exportExcel = () => {
  if (tableData.value.length === 0) {
    ElMessage.warning("暂无数据可供导出");
    return;
  }
  const exportConfig = [
    { title: "序号", key: "index", type: "text" },
    { title: "用户名称", key: "username", type: "text" },
    { title: "举报原因", key: "reason", type: "text" },
    { title: "被举报文章", key: "postTitle", type: "text" },
    { title: "举报时间", key: "createdAt", type: "text" },
    { title: "状态", key: "status", type: "text" },
    { title: "处理意见", key: "comment", type: "text" },
  ];

  const data = tableData.value.map((item, index) => ({
    ...item,
    index: index + 1,
    createdAt: item.createdAt,
    status: getStatusLabel(item.status),
    reason: getReasonLabel(item.reason),
  }));

  const fileName = `举报数据_${new Date().toLocaleDateString()}`; // 添加日期后缀
  table2Excel(exportConfig, data, fileName);
};

/**
 * 批量操作
 */
const multipleSelection = ref([]);
const handleSelectionChange = (val) => {
  multipleSelection.value = val;
};

/**
 * 详情
 */
const detailDialogVisible = ref(false);
const currentDetail = ref(null);

const showDetail = (row) => {
  detailDialogVisible.value = true;
  currentDetail.value = row;
};

const getReasonLabel = (reason) => {
  const map = {
    SPAM: "垃圾广告",
    INAPPROPRIATE: "不当内容",
    PLAGIARISM: "抄袭内容",
    ILLEGAL: "违法内容",
    OTHER: "其他原因",
  };
  return map[reason] || reason;
};
</script>

<template>
  <page-container title="举报管理">
    <!-- 搜索栏 -->
    <el-card class="search-card">
      <el-row :gutter="20">
        <el-col :span="5">
          <el-input
            v-model="searchKey"
            placeholder="搜索举报内容"
            clearable
            @keyup.enter="handleSearch"
            @input="debouncedGetList"
          >
            <template #suffix>
              <el-icon class="el-input__icon">
                <Search />
              </el-icon>
            </template>
          </el-input>
        </el-col>
        <el-col :span="4">
          <el-select v-model="statusFilter" placeholder="状态筛选" clearable>
            <el-option label="待处理" value="PENDING" />
            <el-option label="审核中" value="REVIEWING" />
            <el-option label="已通过" value="APPROVED" />
            <el-option label="已驳回" value="REJECTED" />
          </el-select>
        </el-col>
        <el-col :span="4">
          <el-select v-model="reasonFilter" placeholder="举报原因" clearable>
            <el-option label="垃圾广告" value="SPAM" />
            <el-option label="不当内容" value="INAPPROPRIATE" />
            <el-option label="抄袭内容" value="PLAGIARISM" />
            <el-option label="违法内容" value="ILLEGAL" />
            <el-option label="其他原因" value="OTHER" />
          </el-select>
        </el-col>
        <el-col :span="11" style="text-align: right">
          <el-button type="primary" :icon="Search" @click="getReportList">搜索</el-button>
          <el-button @click="resetSearch">重置</el-button>
          <el-button type="success" @click="exportExcel">导出为 Excel</el-button>
          <el-button
            v-if="multipleSelection.length > 0"
            type="danger"
            @click="batchDelete"
          >
            批量删除({{ multipleSelection.length }})
          </el-button>
        </el-col>
      </el-row>
    </el-card>

    <!-- 表格 -->
    <el-card class="table-card">
      <el-table
        v-loading="loading"
        :data="tableData"
        border
        stripe
        @selection-change="handleSelectionChange"
      >
        <template #empty>
          <el-empty description="暂无举报数据" />
        </template>
        <el-table-column type="selection" width="55">
          <template #default="{ row }">
            <el-checkbox
              v-model="row.isSelected"
              :disabled="!['APPROVED', 'REJECTED'].includes(row.status)"
              @change="(val) => handleSelectionChange(row, val)"
            />
          </template>
        </el-table-column>
        <el-table-column type="index" label="序号" width="80" align="center" />
        <el-table-column prop="username" label="用户名称" width="120" />
        <el-table-column prop="reason" label="举报原因" show-overflow-tooltip width="120">
          <template #default="{ row }">
            {{ getReasonLabel(row.reason) }}
          </template>
        </el-table-column>
        <el-table-column
          prop="postTitle"
          label="被举报文章"
          width="200"
          show-overflow-tooltip
        >
          <template #default="{ row }">
            <el-link type="primary" @click="viewPost(row.postId)">
              {{ row.postTitle || "文章已删除" }}
            </el-link>
          </template>
        </el-table-column>
        <el-table-column prop="createdAt" label="举报时间" width="180">
          <template #default="{ row }">
            {{ formatDate(row.createdAt) }}
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag
              :type="getStatusType(row.status)"
              @click="
                statusFilter = row.status;
                getReportList();
              "
              style="cursor: pointer"
            >
              {{ getStatusLabel(row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column
          prop="comment"
          label="处理意见"
          show-overflow-tooltip
          width="120"
        />
        <el-table-column label="操作" width="180" align="center" fixed="right">
          <template #default="{ row }">
            <el-button
              type="info"
              :icon="InfoFilled"
              circle
              @click="showDetail(row)"
              title="查看详情"
            />
            <el-button
              v-if="row.status === 'PENDING'"
              type="primary"
              :icon="View"
              circle
              @click="handleReview(row)"
              title="审核举报"
            />
            <el-button
              v-if="['APPROVED', 'REJECTED'].includes(row.status)"
              type="danger"
              :icon="Delete"
              circle
              @click="deleteReport(row)"
              title="删除举报"
            />
          </template>
        </el-table-column>
      </el-table>
    </el-card>

          <!-- 分页 -->
    <div class="pagination">
      <el-pagination
        v-model:current-page="currentPage"
        v-model:page-size="pageSize"
        :total="total"
        :page-sizes="[10, 20, 50, 100]"
        layout="total, sizes, prev, pager, next, jumper"
        @size-change="handleSizeChange"
        @current-change="handleCurrentChange"
      />
    </div>

    <!-- 详情对话框 -->
    <el-dialog v-model="detailDialogVisible" title="举报详情" width="500px">
      <div v-if="currentDetail" class="report-detail">
        <div class="detail-item">
          <span class="label">被举报文章：</span>
          <el-link type="primary" @click="viewPost(currentDetail.postId)">
            {{ currentDetail.postTitle || "文章已删除" }}
          </el-link>
        </div>
        <div class="detail-item">
          <span class="label">举报原因：</span>
          <span>{{ getReasonLabel(currentDetail.reason) }}</span>
        </div>
        <div class="detail-item">
          <span class="label">详细说明：</span>
          <div class="description">{{ currentDetail.description || "无" }}</div>
        </div>
        <!-- 添加处理结果和处理意见 -->
        <template v-if="currentDetail.status !== 'PENDING'">
          <div class="detail-item">
            <span class="label">处理结果：</span>
            <el-tag :type="currentDetail.status === 'APPROVED' ? 'success' : 'danger'">
              {{ currentDetail.status === "APPROVED" ? "已通过" : "已驳回" }}
            </el-tag>
          </div>
          <div class="detail-item">
            <span class="label">处理意见：</span>
            <div class="description">{{ currentDetail.comment || "无" }}</div>
          </div>
        </template>
      </div>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="detailDialogVisible = false">关闭</el-button>
        </span>
      </template>
    </el-dialog>

    <!-- 审核对话框 -->
    <el-dialog v-model="reviewDialogVisible" title="举报审核" width="500px">
      <el-form ref="formRef" :model="reviewForm" :rules="rules" label-width="100px">
        <el-form-item label="审核结果">
          <el-radio-group v-model="reviewForm.approved">
            <el-radio :value="true">通过</el-radio>
            <el-radio :value="false">驳回</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="审核意见" prop="comment">
          <el-input
            v-model="reviewForm.comment"
            type="textarea"
            :rows="4"
            placeholder="请输入审核意见"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="reviewDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="submitReview">确定</el-button>
        </span>
      </template>
    </el-dialog>
  </page-container>
</template>

<style scoped>
.search-card {
  margin-bottom: 20px;
}

.table-card {
  margin-bottom: 20px;
  height: calc(100vh - 280px); /* 设置固定高度 */
  display: flex;
  flex-direction: column;
}

/* 固定表格高度 */
.table-card :deep(.el-table) {
  flex: 1;
  height: 100% !important;;
}

/* 禁用表格纵向滚动 */
.table-card :deep(.el-table__body-wrapper) {
  overflow-y: hidden;
}

.pagination-container {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
  background: #fff;
  padding: 20px;
  border-radius: 4px;
  box-shadow: 0 1px 4px rgba(0, 0, 0, 0.1);
}

.dialog-footer {
  text-align: right;
}

.report-detail {
  padding: 20px;
}

.detail-item {
  margin-bottom: 20px;
}

.detail-item .label {
  font-weight: bold;
  margin-right: 10px;
  color: var(--el-text-color-regular);
}

.detail-item .description {
  margin-top: 10px;
  padding: 10px;
  background-color: var(--el-fill-color-lighter);
  border-radius: 4px;
  white-space: pre-wrap;
  line-height: 1.5;
}
</style>
