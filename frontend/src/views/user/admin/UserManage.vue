<template>
  <page-container>
    <el-card class="box-card">
      <!-- 搜索和操作栏 -->
      <div class="search-bar">
        <el-input
          v-model="searchKey"
          placeholder="搜索用户名/邮箱"
          class="search-input"
          clearable
          @clear="handleSearch"
        >
          <template #prefix>
            <el-icon><Search /></el-icon>
          </template>
        </el-input>
        <el-select v-model="activeFilter" placeholder="状态筛选" clearable @change="handleSearch">
          <el-option label="已激活" :value="true" />
          <el-option label="已禁用" :value="false" />
        </el-select>
      </div>

      <!-- 用户列表 -->
      <el-table :data="tableData" v-loading="loading" style="width: 100%">
        <el-table-column prop="userId" label="ID" width="80" />
        <el-table-column prop="username" label="用户名" width="120" />
        <el-table-column prop="nickname" label="昵称" width="120" />
        <el-table-column prop="email" label="邮箱" width="180" />
        <el-table-column prop="createdAt" label="注册时间" width="180" />
        <el-table-column prop="lastLoginTime" label="最后登录" width="180" />
        <el-table-column prop="isActive" label="状态" width="100">
          <template #default="scope">
            <el-tag :type="scope.row.isActive ? 'success' : 'danger'">
              {{ scope.row.isActive ? '已激活' : '已禁用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="200">
          <template #default="scope">
            <el-button
              :type="scope.row.isActive ? 'warning' : 'success'"
              size="small"
              @click="handleActivate(scope.row)"
            >
              {{ scope.row.isActive ? '禁用' : '激活' }}
            </el-button>
            <el-button
              type="danger"
              size="small"
              @click="handleDelete(scope.row)"
            >
              删除
            </el-button>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页 -->
      <div class="pagination">
        <el-pagination
          v-model:current-page="currentPage"
          v-model:page-size="pageSize"
          :page-sizes="[10, 20, 50, 100]"
          layout="total, sizes, prev, pager, next, jumper"
          :total="total"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
        />
      </div>
    </el-card>
  </page-container>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Search } from '@element-plus/icons-vue'
import PageContainer from '@/components/PageContainer.vue'
import { findAll, activateUser, deleteById } from '@/api/user'

// 表格数据
const loading = ref(false)
const tableData = ref([])
const total = ref(0)
const currentPage = ref(1)
const pageSize = ref(10)

// 搜索条件
const searchKey = ref('')
const activeFilter = ref('')

// 获取用户列表
const getUserList = async () => {
  loading.value = true
  try {
    const res = await findAll()
    if (res.data.status === 200) {
      tableData.value = res.data.data
      total.value = res.data.data.length
    }
  } catch (error) {
    ElMessage.error('获取用户列表失败')
  }
  loading.value = false
}

// 激活/禁用用户
const handleActivate = async (row) => {
  try {
    const res = await activateUser({
      userId: row.userId,
      isActive: !row.isActive
    })
    if (res.data.status === 200) {
      ElMessage.success(row.isActive ? '用户已禁用' : '用户已激活')
      getUserList()
    }
  } catch (error) {
    ElMessage.error('操作失败')
  }
}

// 删除用户
const handleDelete = (row) => {
  ElMessageBox.confirm('确定要删除该用户吗？', '警告', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      const res = await deleteById(row.userId)
      if (res.data.status === 200) {
        ElMessage.success('删除成功')
        getUserList()
      }
    } catch (error) {
      ElMessage.error('删除失败')
    }
  })
}

// 搜索
const handleSearch = () => {
  getUserList()
}

// 分页
const handleSizeChange = (val) => {
  pageSize.value = val
  getUserList()
}

const handleCurrentChange = (val) => {
  currentPage.value = val
  getUserList()
}

onMounted(() => {
  getUserList()
})
</script>

<style scoped>
.search-bar {
  display: flex;
  margin-bottom: 20px;
  gap: 10px;
}

.search-input {
  width: 300px;
}

.pagination {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}
</style>