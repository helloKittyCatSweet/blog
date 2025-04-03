<script setup>
import { ref, onMounted } from "vue";
import { ElMessage, ElMessageBox } from "element-plus";
import { Search, Edit } from "@element-plus/icons-vue";
import PageContainer from "@/components/PageContainer.vue";
import { findByUserList } from "@/api/post/report";

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
    PROCESSED: "success",
    REJECTED: "danger",
  };
  return map[status];
};

const getStatusLabel = (status) => {
  const map = {
    PENDING: "待处理",
    PROCESSED: "已处理",
    REJECTED: "已驳回",
  };
  return map[status];
};

// 编辑举报
const handleEdit = (row) => {
  form.value = {
    reportId: row.reportId,
    reason: row.reason,
  };
  dialogVisible.value = true;
};

// 提交表单
const submitForm = async () => {
  if (!formRef.value) return;
  await formRef.value.validate(async (valid) => {
    if (valid) {
      try {
        await updateReport(form.value.reportId, form.value.reason);
        ElMessage.success("修改成功");
        dialogVisible.value = false;
        getReportList();
      } catch (error) {
        ElMessage.error("修改失败");
      }
    }
  });
};

// 搜索
const handleSearch = () => {
  getReportList();
};

// 重置搜索
const resetSearch = () => {
  searchKey.value = "";
  statusFilter.value = "";
  getReportList();
};

// 分页
const handleSizeChange = (val) => {
  pageSize.value = val;
  getReportList();
};

const handleCurrentChange = (val) => {
  currentPage.value = val;
  getReportList();
};

// 查看文章
const viewPost = (postId) => {
  // 实现查看文章逻辑，可以跳转到文章详情页
  window.open(`/post/${postId}`, "_blank");
};

onMounted(() => {
  getReportList();
});
</script>

<template>
  <page-container title="我的举报">
    <!-- 搜索栏 -->
    <el-card class="search-card">
      <el-row :gutter="20">
        <el-col :span="6">
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
        <el-col :span="6">
          <el-select v-model="statusFilter" placeholder="状态筛选" clearable>
            <el-option label="待处理" value="PENDING" />
            <el-option label="已处理" value="PROCESSED" />
            <el-option label="已驳回" value="REJECTED" />
          </el-select>
        </el-col>
        <el-col :span="12" style="text-align: right">
          <el-button type="primary" :icon="Search" @click="handleSearch">搜索</el-button>
          <el-button @click="resetSearch">重置</el-button>
        </el-col>
      </el-row>
    </el-card>

    <!-- 表格 -->
    <el-card class="table-card">
      <el-table v-loading="loading" :data="tableData" border stripe>
        <el-table-column type="index" label="序号" width="80" align="center" />
        <el-table-column prop="reason" label="举报原因" show-overflow-tooltip />
        <el-table-column prop="post.title" label="被举报文章" show-overflow-tooltip>
          <template #default="{ row }">
            <el-link type="primary" @click="viewPost(row.postId)">
              {{ row.post?.title || "文章已删除" }}
            </el-link>
          </template>
        </el-table-column>
        <el-table-column prop="createdAt" label="举报时间" width="180" />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="getStatusType(row.status)">
              {{ getStatusLabel(row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="comment" label="处理意见" show-overflow-tooltip />
        <el-table-column label="操作" width="120" align="center">
          <template #default="{ row }">
            <el-button
              v-if="row.status === 'PENDING'"
              type="primary"
              :icon="Edit"
              circle
              @click="handleEdit(row)"
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
</style>
