<template>
  <div class="report-manage">
    <el-card class="search-card">
      <div class="search-section">
        <el-input
          v-model="searchKeyword"
          placeholder="搜索举报内容"
          class="search-input"
          clearable
        />
        <el-select v-model="searchStatus" placeholder="选择状态" clearable>
          <el-option
            v-for="item in statusOptions"
            :key="item.value"
            :label="item.label"
            :value="item.value"
          />
        </el-select>
        <el-button type="primary" @click="searchReports">搜索</el-button>
      </div>
    </el-card>

    <el-card class="report-list">
      <el-table :data="reports" style="width: 100%">
        <el-table-column prop="reportId" label="ID" width="80" />
        <el-table-column prop="userId" label="举报人ID" width="100" />
        <el-table-column prop="postId" label="文章ID" width="100" />
        <el-table-column prop="reason" label="举报原因" />
        <el-table-column prop="description" label="详细描述" />
        <el-table-column prop="status" label="状态" width="120">
          <template #default="scope">
            <el-tag :type="getStatusType(scope.row.status)">
              {{ getStatusLabel(scope.row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createdAt" label="举报时间" width="180" />
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="scope">
            <el-button
              v-if="scope.row.status === 'PENDING'"
              type="primary"
              size="small"
              @click="handleReview(scope.row)"
            >
              审核
            </el-button>
            <el-button
              type="info"
              size="small"
              @click="viewDetails(scope.row)"
            >
              详情
            </el-button>
          </template>
        </el-table-column>
      </el-table>

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

    <!-- 审核对话框 -->
    <el-dialog
      v-model="reviewDialogVisible"
      title="举报审核"
      width="500px"
    >
      <div class="review-form">
        <el-form :model="reviewForm" label-width="100px">
          <el-form-item label="审核结果">
            <el-radio-group v-model="reviewForm.approved">
              <el-radio :label="true">通过</el-radio>
              <el-radio :label="false">驳回</el-radio>
            </el-radio-group>
          </el-form-item>
          <el-form-item label="审核意见">
            <el-input
              v-model="reviewForm.comment"
              type="textarea"
              rows="4"
              placeholder="请输入审核意见"
            />
          </el-form-item>
        </el-form>
      </div>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="reviewDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="submitReview">确认</el-button>
        </span>
      </template>
    </el-dialog>

    <!-- 详情对话框 -->
    <el-dialog
      v-model="detailsDialogVisible"
      title="举报详情"
      width="600px"
    >
      <div v-if="selectedReport" class="report-details">
        <p><strong>举报ID：</strong>{{ selectedReport.reportId }}</p>
        <p><strong>举报人ID：</strong>{{ selectedReport.userId }}</p>
        <p><strong>文章ID：</strong>{{ selectedReport.postId }}</p>
        <p><strong>举报原因：</strong>{{ selectedReport.reason }}</p>
        <p><strong>详细描述：</strong>{{ selectedReport.description }}</p>
        <p><strong>状态：</strong>{{ getStatusLabel(selectedReport.status) }}</p>
        <p><strong>举报时间：</strong>{{ selectedReport.createdAt }}</p>
        <p v-if="selectedReport.comment"><strong>审核意见：</strong>{{ selectedReport.comment }}</p>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import axios from 'axios'

export default {
  name: 'ReportManage',
  setup() {
    const reports = ref([])
    const searchKeyword = ref('')
    const searchStatus = ref('')
    const currentPage = ref(1)
    const pageSize = ref(10)
    const total = ref(0)
    const reviewDialogVisible = ref(false)
    const detailsDialogVisible = ref(false)
    const selectedReport = ref(null)
    const reviewForm = ref({
      approved: true,
      comment: ''
    })

    const statusOptions = [
      { value: 'PENDING', label: '待处理' },
      { value: 'APPROVED', label: '已通过' },
      { value: 'REJECTED', label: '已驳回' }
    ]

    const getStatusType = (status) => {
      const statusMap = {
        PENDING: 'warning',
        APPROVED: 'success',
        REJECTED: 'danger'
      }
      return statusMap[status] || 'info'
    }

    const getStatusLabel = (status) => {
      const statusMap = {
        PENDING: '待处理',
        APPROVED: '已通过',
        REJECTED: '已驳回'
      }
      return statusMap[status] || status
    }

    const searchReports = async () => {
      try {
        const response = await axios.get('/api/post/report/admin/search', {
          params: {
            keyword: searchKeyword.value,
            status: searchStatus.value,
            isAdmin: true
          }
        })
        reports.value = response.data.data
      } catch (error) {
        ElMessage.error('获取举报列表失败')
      }
    }

    const handleReview = (report) => {
      selectedReport.value = report
      reviewForm.value = {
        approved: true,
        comment: ''
      }
      reviewDialogVisible.value = true
    }

    const submitReview = async () => {
      try {
        await axios.post(`/api/post/report/admin/review/${selectedReport.value.reportId}`, null, {
          params: {
            approved: reviewForm.value.approved,
            comment: reviewForm.value.comment
          }
        })
        ElMessage.success('审核成功')
        reviewDialogVisible.value = false
        searchReports()
      } catch (error) {
        ElMessage.error('审核失败')
      }
    }

    const viewDetails = (report) => {
      selectedReport.value = report
      detailsDialogVisible.value = true
    }

    const handleSizeChange = (val) => {
      pageSize.value = val
      searchReports()
    }

    const handleCurrentChange = (val) => {
      currentPage.value = val
      searchReports()
    }

    onMounted(() => {
      searchReports()
    })

    return {
      reports,
      searchKeyword,
      searchStatus,
      statusOptions,
      currentPage,
      pageSize,
      total,
      reviewDialogVisible,
      detailsDialogVisible,
      selectedReport,
      reviewForm,
      getStatusType,
      getStatusLabel,
      searchReports,
      handleReview,
      submitReview,
      viewDetails,
      handleSizeChange,
      handleCurrentChange
    }
  }
}
</script>

<style scoped>
.report-manage {
  padding: 20px;
}

.search-card {
  margin-bottom: 20px;
}

.search-section {
  display: flex;
  gap: 15px;
}

.search-input {
  width: 200px;
}

.report-list {
  margin-top: 20px;
}

.pagination {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}

.review-form {
  padding: 20px;
}

.report-details {
  padding: 20px;
}

.report-details p {
  margin: 10px 0;
  line-height: 1.5;
}
</style>