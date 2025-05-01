<script setup>
import { ref, onMounted } from "vue";
import { ElMessage, ElMessageBox } from "element-plus";
import {
  Search,
  Edit,
  Delete,
  Plus,
  User,
  Upload,
  Download,
  InfoFilled,
} from "@element-plus/icons-vue";
import PageContainer from "@/components/PageContainer.vue";
import { findAll, update } from "@/api/user/role";
import { save, deleteRole, findRoleUsers, importRoleData } from "@/api/user/userRole";
import { findAllUser } from "@/api/user/user";
import * as XLSX from "xlsx";

const loading = ref(false);
const tableData = ref([]);

// 对话框控制
const dialogVisible = ref(false);
const dialogTitle = ref("新增角色");
const formRef = ref(null);
const form = ref({
  roleId: null,
  roleName: "",
  description: "",
  administratorName: "",
});

// 表单校验规则
const rules = {
  roleName: [
    { required: true, message: "请输入角色名称", trigger: "blur" },
    { min: 2, max: 50, message: "长度在 2 到 50 个字符", trigger: "blur" },
  ],
  administratorName: [{ required: true, message: "请输入管理员名称", trigger: "blur" }],
};

// 处理编辑
const handleEdit = (row) => {
  dialogTitle.value = "编辑角色";
  form.value = { ...row };
  dialogVisible.value = true;
};

// 提交表单
const submitForm = async () => {
  if (!formRef.value) return;
  await formRef.value.validate(async (valid) => {
    if (valid) {
      try {
        const res = await update(form.value);
        if (res.data.status === 200) {
          ElMessage.success("更新成功");
          dialogVisible.value = false;
          getRoleList();
        }
      } catch (error) {
        ElMessage.error("更新失败");
      }
    }
  });
};

/**
 * 用户列表相关状态
 */
const userDrawerVisible = ref(false);
const currentRole = ref(null);
const roleUsers = ref([]);
const roleUsersLoading = ref(false);

// 获取角色列表，简化逻辑
const getRoleList = async () => {
  loading.value = true;
  try {
    const res = await findAll();
    if (res.data.status === 200) {
      tableData.value = res.data.data;
    } else {
      ElMessage.warning(res.data.message || "获取角色列表失败");
    }
  } catch (error) {
    console.error("获取角色列表失败:", error);
    ElMessage.error("获取角色列表失败");
  } finally {
    loading.value = false;
  }
};

// 查看角色用户
const handleViewUsers = async (row) => {
  currentRole.value = row;
  userDrawerVisible.value = true;
  currentPage.value = 1;
  pageSize.value = 10;
  console.log('Loading users for role:', row.roleId);
  await loadRoleUsers(row.roleId);
};

const currentPage = ref(1);
const pageSize = ref(10);
const total = ref(0);

// 加载角色用户列表
const loadRoleUsers = async (roleId) => {
  roleUsersLoading.value = true;
  try {
    const response = await findRoleUsers(roleId, {
      page: currentPage.value - 1,
      size: pageSize.value,
    });
    console.log('Role users response:', response);
    if (response.data.status === 200) {
      roleUsers.value = response.data.data.content;
      total.value = response.data.data.totalElements;
    } else {
      ElMessage.warning(response.data.message || "获取用户列表失败");
    }
  } catch (error) {
    console.error('Failed to load role users:', error);
    ElMessage.error("获取用户列表失败");
  } finally {
    roleUsersLoading.value = false;
  }
};

const handleSizeChange = (val) => {
  pageSize.value = val;
  loadRoleUsers(currentRole.value.roleId);
};

const handleCurrentChange = (val) => {
  currentPage.value = val;
  loadRoleUsers(currentRole.value.roleId);
};

/**
 * 给用户添加角色
 */
// 添加新的状态
const addUserDialogVisible = ref(false);
const allUsers = ref([]); // 所有可选用户
const selectedUsers = ref([]); // 选中的用户

// 添加用户到角色
const handleAddUsers = async () => {
  addUserDialogVisible.value = true;
  try {
    const res = await findAllUser();
    if (res.data.status === 200) {
      // 处理嵌套的用户数据结构
      const allUsersData = res.data.data.content.map(item => item.user);
      // 过滤掉已经有这个角色的用户
      allUsers.value = allUsersData.filter(user =>
        !roleUsers.value.some(roleUser => roleUser.userId === user.userId)
      );
    } else {
      ElMessage.warning(res.data.message || "获取用户列表失败");
    }
  } catch (error) {
    console.error('获取用户列表失败:', error);
    ElMessage.error("获取用户列表失败");
  }
};

// 确认添加用户
const confirmAddUsers = async () => {
  if (!selectedUsers.value.length) {
    ElMessage.warning("请选择要添加的用户");
    return;
  }
  try {
    await Promise.all(
      selectedUsers.value.map((user) =>
        save({
          userId: user.userId,
          roleId: currentRole.value.roleId,
        })
      )
    );

    ElMessage.success("添加成功");
    addUserDialogVisible.value = false;
    await loadRoleUsers(currentRole.value.roleId);
  } catch (error) {
    ElMessage.error("添加失败");
  }
};

onMounted(() => {
  getRoleList();
});

/**
 * 删除用户角色
 */
const handleRemoveUser = (row) => {
  ElMessageBox.confirm(`确认将用户 ${row.username} 从该角色中移除吗？`, "警告", {
    confirmButtonText: "确定",
    cancelButtonText: "取消",
    type: "warning",
  }).then(async () => {
    try {
      const res = await deleteRole({
        userId: row.userId,
        roleId: currentRole.value.roleId,
      });
      if (res.data.status === 200) {
        ElMessage.success("移除成功");
        await loadRoleUsers(currentRole.value.roleId);
      }
    } catch (error) {
      ElMessage.error("移除失败");
    }
  });
};

/**
 * 上传和下载
 */
// 导出角色数据
const handleExportRoles = async () => {
  try {
    loading.value = true;
    // 获取所有角色的用户数据
    const roleDataPromises = tableData.value.map(async (role) => {
      const response = await findRoleUsers(role.roleId);
      const users = response.data.status === 200 ? response.data.data.content : [];
      return {
        role,
        users,
      };
    });

    const rolesWithUsers = await Promise.all(roleDataPromises);

    // 准备工作簿
    const wb = XLSX.utils.book_new();

    // 创建角色总表
    const roleSheet = XLSX.utils.json_to_sheet(
      rolesWithUsers.map((item) => ({
        角色名称: item.role.roleName,
        角色描述: item.role.description || "",
        管理员: item.role.administratorName,
        用户数量: item.role.count || 0,
      }))
    );
    XLSX.utils.book_append_sheet(wb, roleSheet, "角色列表");

    // 为每个角色创建用户明细表
    rolesWithUsers.forEach((item) => {
      const userSheet = XLSX.utils.json_to_sheet(
        item.users.map((user) => ({
          用户名: user.username,
          邮箱: user.email,
          昵称: user.nickname || "",
          状态: user.isActive ? "已激活" : "已禁用",
          最后登录时间: user.lastLoginTime || "",
        }))
      );
      XLSX.utils.book_append_sheet(wb, userSheet, `${item.role.roleName}-用户列表`);
    });

    // 生成文件并下载
    XLSX.writeFile(wb, `角色数据_${new Date().toLocaleDateString()}.xlsx`);
    ElMessage.success("导出成功");
  } catch (error) {
    console.error("导出失败:", error);
    ElMessage.error("导出失败");
  } finally {
    loading.value = false;
  }
};

// 处理文件上传
const handleFileUpload = async (file) => {
  try {
    const res = await importRoleData(file);
    if (res.data.status === 200) {
      ElMessage.success("导入成功");
      getRoleList(); // 刷新列表
    } else {
      ElMessage.error(res.data.message || "导入失败");
    }
    return false; // 阻止默认上传
  } catch (error) {
    console.error("导入失败:", error);
    ElMessage.error("导入失败");
    return false;
  }
};

// 上传前校验
const beforeUpload = (file) => {
  const isExcel =
    file.type === "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet" ||
    file.type === "application/vnd.ms-excel";
  if (!isExcel) {
    ElMessage.error("只能上传 Excel 文件！");
    return false;
  }
  return true;
};

// 导入成功处理
const handleImportSuccess = (response) => {
  if (response.status === 200) {
    ElMessage.success("导入成功");
    getRoleList(); // 刷新列表
  } else {
    ElMessage.error(response.message || "导入失败");
  }
};

// 导入失败处理
const handleImportError = () => {
  ElMessage.error("导入失败");
};
</script>

<template>
  <page-container title="角色管理">
    <!-- 表格 -->
    <el-card class="table-card">
      <!-- 添加操作按钮区 -->
      <div class="table-operations">
        <el-button type="success" :icon="Download" @click="handleExportRoles">导出角色数据</el-button>
        <el-upload class="upload-button" :show-file-list="false" :before-upload="handleFileUpload"
          :on-success="handleImportSuccess" :on-error="handleImportError">
          <el-button type="primary" :icon="Upload">导入角色数据</el-button>
        </el-upload>
        <el-popover placement="bottom" :width="400" trigger="hover" title="Excel 导入格式说明" :show-after="100">
          <template #reference>
            <el-button :icon="InfoFilled" circle type="info" />
          </template>
          <div class="import-guide">
            <div class="guide-section">
              <h4>工作表要求：</h4>
              <div class="guide-item">
                <div class="sheet-name">1. 角色列表（第一个工作表）</div>
                <div class="columns">
                  <span class="required">角色名称</span>
                  <span class="optional">角色描述</span>
                  <span class="required">管理员</span>
                </div>
              </div>
              <div class="guide-item">
                <div class="sheet-name">2. 用户列表（格式：角色名称-用户列表）</div>
                <div class="columns">
                  <span class="required">用户名</span>
                  <span class="required">邮箱</span>
                  <span class="optional">昵称</span>
                  <span class="optional">状态</span>
                </div>
              </div>
            </div>
            <div class="guide-section">
              <h4>导入说明：</h4>
              <div class="guide-item">
                <div class="sheet-name">角色处理规则</div>
                <div class="rule-item">
                  <span class="warning">· 只能更新已存在的角色，不会创建新角色</span>
                  <span class="warning">· 只会更新角色的描述字段</span>
                  <span class="warning">· 管理员字段不会被更新</span>
                </div>
              </div>
              <div class="guide-item">
                <div class="sheet-name">用户处理规则</div>
                <div class="rule-item">
                  <span class="warning">· 只处理系统中已存在的用户</span>
                  <span class="warning">· 不会创建新用户</span>
                  <span class="warning">· 只会创建用户与角色的关联关系</span>
                </div>
              </div>
            </div>
            <div class="guide-tip">提示：建议先导出现有数据作为模板使用</div>
          </div>
        </el-popover>
      </div>

      <el-table v-loading="loading" :data="tableData" border stripe>
        <template #empty>
          <el-empty description="暂无数据" />
        </template>
        <el-table-column type="index" label="序号" width="80" align="center" />
        <el-table-column prop="roleName" label="角色名称" show-overflow-tooltip />
        <el-table-column prop="description" label="角色描述" show-overflow-tooltip />
        <el-table-column prop="administratorName" label="管理员" show-overflow-tooltip />
        <el-table-column prop="userCount" label="用户数量" width="100" align="center">
          <template #default="{ row }">
            <el-tag>{{ row.count || 0 }}</el-tag>
          </template>
        </el-table-column>
        <!-- 修改角色列表的操作列 -->
        <el-table-column label="操作" width="220" align="center">
          <template #default="{ row }">
            <el-tooltip :content="row.description || '暂无描述'" placement="top" effect="dark">
              <el-button type="primary" :icon="Edit" circle @click="handleEdit(row)" title="编辑" />
            </el-tooltip>
            <el-button type="info" :icon="User" circle @click="handleViewUsers(row)" title="查看用户" />
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <!-- 编辑对话框 -->
    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="500px">
      <el-form ref="formRef" :model="form" :rules="rules" label-width="100px">
        <el-form-item label="角色名称" prop="roleName">
          <el-input v-model="form.roleName" placeholder="请输入角色名称" />
        </el-form-item>
        <el-form-item label="角色描述" prop="description">
          <el-input v-model="form.description" type="textarea" :rows="3" placeholder="请输入角色描述" />
        </el-form-item>
        <el-form-item label="管理员" prop="administratorName">
          <!-- 该字段的值不可修改 -->
          <el-input v-model="form.administratorName" placeholder="请输入管理员名称" :disabled="form.roleId !== null" />
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="dialogVisible = false">取消</el-button>
          <el-button type="primary" @click="submitForm">确定</el-button>
        </span>
      </template>
    </el-dialog>

    <!-- 添加用户列表抽屉 -->
    <el-drawer 
      v-model="userDrawerVisible"
      :title="`${currentRole?.roleName || ''} - 用户列表`"
      size="80%"
      :destroy-on-close="false"
    >
      <div class="drawer-header" style="padding: 16px; display: flex; justify-content: space-between; align-items: center; border-bottom: 1px solid #dcdfe6;">
        <span class="title">{{ currentRole?.roleName || "" }} - 用户列表</span>
        <el-button 
          v-if="currentRole && currentRole.roleName && currentRole.roleName !== '普通用户'"
          type="primary"
          @click="handleAddUsers"
        >
          添加用户
        </el-button>
      </div>

      <el-table
        v-loading="roleUsersLoading"
        :data="roleUsers"
        border
        stripe
        style="width: 100%; margin-top: 16px;"
      >
        <template #empty>
          <el-empty description="暂无用户" />
        </template>
        <el-table-column type="index" label="序号" width="80" align="center" />
        <el-table-column prop="username" label="用户名" />
        <el-table-column prop="email" label="邮箱" />
        <el-table-column prop="lastLoginTime" label="最后登录时间" />
        <el-table-column prop="isActive" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="row.isActive ? 'success' : 'danger'">
              {{ row.isActive ? "已激活" : "已禁用" }}
            </el-tag>
          </template>
        </el-table-column>
        <!-- 添加操作列 -->
        <el-table-column v-if="currentRole?.roleName !== '普通用户'" label="操作" width="100" align="center">
          <template #default="{ row }">
            <el-button type="danger" :icon="Delete" circle @click="handleRemoveUser(row)" title="移除用户" />
          </template>
        </el-table-column>
      </el-table>

      <!-- 添加分页组件 -->
      <div class="pagination" style="margin-top: 20px; text-align: right;">
        <el-pagination v-model:current-page="currentPage" v-model:page-size="pageSize" :page-sizes="[10, 20, 50, 100]"
          :total="total" layout="total, sizes, prev, pager, next, jumper" @size-change="handleSizeChange"
          @current-change="handleCurrentChange" />
      </div>

      <!-- 添加用户对话框 -->
      <el-dialog
        v-model="addUserDialogVisible"
        title="添加用户"
        width="50%"
        append-to-body
        destroy-on-close
      >
        <el-table
          :data="allUsers"
          @selection-change="(val) => (selectedUsers = val)"
          style="width: 100%"
        >
          <el-table-column type="selection" width="55" />
          <el-table-column prop="username" label="用户名" />
          <el-table-column prop="email" label="邮箱" show-overflow-tooltip />
          <el-table-column prop="lastLoginTime" label="最后登录时间" show-overflow-tooltip>
            <template #default="{ row }">
              {{ row.lastLoginTime || '-' }}
            </template>
          </el-table-column>
          <el-table-column prop="isActive" label="状态" width="100">
            <template #default="{ row }">
              <el-tag :type="row.isActive ? 'success' : 'danger'">
                {{ row.isActive ? '已激活' : '已禁用' }}
              </el-tag>
            </template>
          </el-table-column>
        </el-table>
        <template #footer>
          <span class="dialog-footer">
            <el-button @click="addUserDialogVisible = false">取消</el-button>
            <el-button 
              type="primary" 
              @click="confirmAddUsers"
              :disabled="selectedUsers.length === 0"
            >确定</el-button>
          </span>
        </template>
      </el-dialog>
    </el-drawer>
  </page-container>
</template>

<style scoped>
.search-card {
  margin-bottom: 20px;
}

.table-card {
  margin-bottom: 20px;
}

.table-operations {
  margin-bottom: 16px;
  display: flex;
  gap: 10px;
}

.upload-button {
  display: inline-block;
}

.import-guide {
  padding: 8px;
}

.guide-section {
  margin-bottom: 16px;
}

.guide-section h4 {
  margin: 0 0 12px 0;
  color: #303133;
}

.guide-item {
  margin-bottom: 12px;
  padding: 8px;
  background: #f5f7fa;
  border-radius: 4px;
}

.sheet-name {
  font-weight: bold;
  margin-bottom: 8px;
  color: #409eff;
}

.columns {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}

.columns span {
  padding: 2px 8px;
  border-radius: 4px;
  font-size: 13px;
}

.required {
  background: #f56c6c20;
  color: #f56c6c;
  border: 1px solid #f56c6c50;
}

.optional {
  background: #67c23a20;
  color: #67c23a;
  border: 1px solid #67c23a50;
}

.guide-tip {
  font-size: 13px;
  color: #909399;
  padding: 8px;
  background: #f4f4f5;
  border-radius: 4px;
}

.rule-item {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.rule-item span {
  font-size: 13px;
  line-height: 1.4;
}

.warning {
  color: #e6a23c;
}

.import-guide {
  padding: 8px;
  max-height: 400px;
  /* 设置最大高度 */
  overflow-y: auto;
  /* 添加垂直滚动 */
}

.guide-section {
  margin-bottom: 16px;
  padding-right: 8px;
  /* 为滚动条预留空间 */
}

/* 自定义滚动条样式 */
.import-guide::-webkit-scrollbar {
  width: 6px;
}

.import-guide::-webkit-scrollbar-thumb {
  background-color: #909399;
  border-radius: 3px;
}

.import-guide::-webkit-scrollbar-track {
  background-color: #f4f4f5;
  border-radius: 3px;
}
</style>
