<script setup>
import { ref, onMounted } from "vue";
import { useRouter } from "vue-router";
import { ElMessage, ElMessageBox } from "element-plus";
import { Search, Edit, Delete, InfoFilled } from "@element-plus/icons-vue";
import PageContainer from "@/components/PageContainer.vue";
import { findByUserList, update, searchReports, deleteById } from "@/api/post/report";

const router = useRouter();

// 表格数据
const loading = ref(false);
const tableData = ref([]);
const total = ref(0);
const currentPage = ref(1);
const pageSize = ref(10);

// 搜索条件
const searchKey = ref("");
const statusFilter = ref("");

// 编辑表单
const dialogVisible = ref(false);
const formRef = ref(null);
const form = ref({
  reportId: null,
  reason: "",
});

const rules = {
  reason: [{ required: true, message: "请输入举报原因", trigger: "blur" }],
};

// 获取数据
const getReportList = async () => {
  loading.value = true;
  try {
    const res = await findByUserList();
    if (res.data.status === 200) {
      tableData.value = res.data.data;
      total.value = res.data.data.length;
    }
  } catch (error) {
    ElMessage.error("获取举报列表失败");
  }
  loading.value = false;
};

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
    APPROVED: "已处理",
    REJECTED: "已驳回",
  };
  return map[status];
};

// 编辑举报
const handleEdit = (row) => {
  form.value = {
    reportId: row.reportId,
    reason: row.reason,
    status: row.status,
  };
  dialogVisible.value = true;
};

// 提交表单
const submitForm = async () => {
  if (!formRef.value) return;
  await formRef.value.validate(async (valid) => {
    if (valid) {
      try {
        await update(form.value);
        ElMessage.success("修改成功");
        dialogVisible.value = false;
        getReportList();
      } catch (error) {
        ElMessage.error("修改失败");
      }
    }
  });
};

/**
 * 搜索
 */
const reasonFilter = ref("");

// 搜索
const handleSearch = async () => {
  loading.value = true;
  try {
    const params = {};
    if (searchKey.value.trim()) {
      params.keyword = searchKey.value.trim();
    }
    if (statusFilter.value) {
      params.status = statusFilter.value;
    }
    params.isAdmin = false;
    params.reason = reasonFilter.value;
    console.log("搜索参数：", params); // 调试用
    const res = await searchReports(params);
    if (res.data.status === 200) {
      tableData.value = res.data.data;
      total.value = res.data.data.length;
    }
  } catch (error) {
    console.error("搜索错误：", error.response?.data || error); // 增加错误详情
    ElMessage.error("搜索失败");
  }
  loading.value = false;
};

// 重置搜索
const resetSearch = () => {
  searchKey.value = "";
  statusFilter.value = "";
  reasonFilter.value = "";
  getReportList(); // 重置后调用获取列表接口
};

/**
 * 详情对话框
 */
const detailDialogVisible = ref(false);
const currentDetail = ref(null);

// 添加查看详情方法
const showDetail = (row) => {
  detailDialogVisible.value = true;
  currentDetail.value = row;
};

// 添加获取原因标签方法
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

// 分页处理也需要修改
const handleSizeChange = (val) => {
  pageSize.value = val;
  handleSearch(); // 改用搜索接口
};

const handleCurrentChange = (val) => {
  currentPage.value = val;
  handleSearch(); // 改用搜索接口
};

// 查看文章
const viewPost = (postId) => {
  router.push(`/post/${postId}`);
};

onMounted(() => {
  getReportList();
});

/**
 * 删除
 */
const handleDelete = (row) => {
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
};
</script>

<template>
  <page-container title="我的举报">
    <!-- 搜索栏 -->

    <el-card class="search-card">
      <el-row :gutter="20">
        <el-col :span="5">
          <el-input
            v-model="searchKey"
            placeholder="搜索举报内容"
            clearable
            @keyup.enter="handleSearch"
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
          <el-button type="primary" :icon="Search" @click="handleSearch">搜索</el-button>
          <el-button @click="resetSearch">重置</el-button>
        </el-col>
      </el-row>
    </el-card>

    <!-- 表格 -->
    <el-card class="table-card">
      <el-table v-loading="loading" :data="tableData" border stripe>
        <template #empty>
          <el-empty description="暂无举报数据" />
        </template>
        <el-table-column type="index" label="序号" width="80" align="center" />
        <el-table-column prop="reason" label="举报原因" show-overflow-tooltip>
          <template #default="{ row }">
            {{ getReasonLabel(row.reason) }}
          </template>
        </el-table-column>
        <el-table-column prop="postTitle" label="被举报文章" show-overflow-tooltip>
          <template #default="{ row }">
            <router-link
              :to="`/post/${row.postId}`"
              target="_blank"
              class="el-link el-link--primary"
            >
              {{ row.postTitle || "文章已删除" }}
            </router-link>
          </template>
        </el-table-column>
        <el-table-column prop="createdAt" label="举报时间" width="180">
          <template #default="{ row }">
            {{ row.createdAt }}
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="getStatusType(row.status)">
              {{ getStatusLabel(row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="comment" label="处理意见" show-overflow-tooltip />
        <el-table-column label="操作" width="150" align="center">
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
              :icon="Edit"
              circle
              @click="handleEdit(row)"
              title="编辑举报"
            />
            <el-button
              type="danger"
              :icon="Delete"
              circle
              @click="handleDelete(row)"
              title="删除举报"
            />
          </template>
        </el-table-column>
      </el-table>

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
    </el-card>

    <!-- 添加详情对话框 -->
    <el-dialog v-model="detailDialogVisible" title="举报详情" width="500px">
      <div v-if="currentDetail" class="report-detail">
        <div class="detail-item">
          <span class="label">被举报文章：</span>
          <el-link type="primary" @click="viewPost(currentDetail.postId)">
            {{ currentDetail.post?.title || "文章已删除" }}
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
        <template v-if="currentDetail.status !== 'PENDING'">
          <div class="detail-item">
            <span class="label">处理结果：</span>
            <el-tag :type="currentDetail.status === 'APPROVED' ? 'success' : 'danger'">
              {{ getStatusLabel(currentDetail.status) }}
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

    <!-- 编辑对话框 -->
    <el-dialog v-model="dialogVisible" title="修改举报" width="500px">
      <el-form ref="formRef" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="举报原因" prop="reason">
          <el-input
            v-model="form.reason"
            type="textarea"
            :rows="3"
            placeholder="请输入举报原因"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="dialogVisible = false">取消</el-button>
          <el-button type="primary" @click="submitForm">确定</el-button>
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
}

.pagination {
  margin-top: 20px;
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
