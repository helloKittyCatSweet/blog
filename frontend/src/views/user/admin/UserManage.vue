<script setup>
import { ref, onMounted, computed } from "vue";
import { ElMessage, ElMessageBox } from "element-plus";
import {
  Search,
  ArrowDown,
  Edit,
  Delete,
  Warning,
  Check,
  Download,
  Upload,
  Refresh,
  Lock,
  Unlock,
  Key,
  InfoFilled
} from "@element-plus/icons-vue";
import PageContainer from "@/components/PageContainer.vue";
import {
  findAllUser,
  activate,
  deleteById,
  findActivatedUser,
  findByKeywordForAdmin,
  importUserData,
} from "@/api/user/user.js";
import { findAll } from "@/api/user/role.js";
import { save, deleteRole } from "@/api/user/userRole.js";
import * as XLSX from "xlsx";

// 表格数据
const loading = ref(false);
const tableData = ref([]);
const total = ref(0);
const currentPage = ref(1);
const pageSize = ref(10);

// 搜索条件
const searchKey = ref("");
const activeFilter = ref("");

const getUserList = async () => {
  loading.value = true;
  try {
    let res;
    if (searchKey.value) {
      // 搜索用户
      res = await findByKeywordForAdmin(searchKey.value);
      // 如果同时有状态筛选，对搜索结果进行过滤
      if (activeFilter.value !== "") {
        const responseData = res.data;
        if (responseData.status === 200 && responseData.data) {
          res.data.data = responseData.data.filter(
            (item) => item.user.isActive === activeFilter.value
          );
        }
      }
    } else if (activeFilter.value !== "") {
      // 只有状态筛选
      res = await findActivatedUser(activeFilter.value);
    } else {
      // 获取所有用户，使用分页参数
      res = await findAllUser({
        page: currentPage.value - 1, // 前端页码从1开始，后端从0开始
        size: pageSize.value,
        sort: "createdAt,desc"
      });
    }

    // 检查响应格式
    if (!res?.data) {
      throw new Error("响应数据为空");
    }

    const responseData = res.data;

    if (responseData.status === 200 && responseData.data) {
      if (Array.isArray(responseData.data.content)) {
        // 处理分页数据
        tableData.value = responseData.data.content;
        total.value = responseData.data.totalElements;
      } else if (Array.isArray(responseData.data)) {
        // 处理非分页数据（搜索结果）
        tableData.value = responseData.data;
        total.value = responseData.data.length;
      } else {
        tableData.value = [responseData.data];
        total.value = 1;
      }

      if (tableData.value.length === 0) {
        ElMessage.warning("没有找到符合条件的用户");
      }
    } else {
      tableData.value = [];
      total.value = 0;
      ElMessage.warning(responseData.message || "没有找到符合条件的用户");
    }
  } catch (error) {
    console.error("获取用户列表失败:", error);
    ElMessage.error(error.message || "获取用户列表失败");
    tableData.value = [];
    total.value = 0;
  } finally {
    loading.value = false;
  }
};
// 激活/禁用用户
const handleActivate = async (row) => {
  try {
    const res = await activate(row.userId, !row.isActive);
    if (res.data.status === 200) {
      ElMessage.success(row.isActive ? "用户已禁用" : "用户已激活");
      getUserList();
    }
  } catch (error) {
    ElMessage.error("操作失败");
  }
};

// 删除用户
const handleDelete = (row) => {
  ElMessageBox.confirm("确定要删除该用户吗？", "警告", {
    confirmButtonText: "确定",
    cancelButtonText: "取消",
    type: "warning",
  }).then(async () => {
    try {
      const res = await deleteById(row.userId);
      if (res.data.status === 200) {
        ElMessage.success("删除成功");
        getUserList();
      }
    } catch (error) {
      ElMessage.error("删除失败");
    }
  });
};

// 搜索
const handleSearch = () => {
  getUserList();
};

// 重置搜索
const handleReset = () => {
  searchKey.value = "";
  activeFilter.value = "";
  getUserList();
};

// 分页
const handleSizeChange = (val) => {
  pageSize.value = val;
  getUserList();
};

const handleCurrentChange = (val) => {
  currentPage.value = val;
  getUserList();
};

onMounted(() => {
  getUserList();
});

/**
 * 角色标签渲染
 */
const getRoleTagType = (roleName) => {
  switch (roleName) {
    case "ROLE_USER":
      return "info"; // Changed from "" to "info"
    case "ROLE_CATEGORY_MANAGER":
      return "warning";
    case "ROLE_SYSTEM_ADMINISTRATOR":
      return "danger";
    default:
      return "info"; // Changed from "" to "info"
  }
};

/**
 * 用户角色编辑
 */
const roleDialogVisible = ref(false);
const currentUser = ref(null);
const allRoles = ref([]);
const selectedRoles = ref([]);

// 处理编辑角色
const handleEditRoles = async (row) => {
  currentUser.value = row;
  roleDialogVisible.value = true;
  try {
    // 获取所有角色列表
    const res = await findAll();
    if (res.data.status === 200) {
      allRoles.value = res.data.data;
      // 设置当前用户已有的角色
      selectedRoles.value = row.roles.map((role) => role.roleId);
    }
  } catch (error) {
    ElMessage.error("获取角色列表失败");
  }
};

// 确认角色修改
const confirmRoleEdit = async () => {
  try {
    const currentRoles = currentUser.value.roles;
    // 转换为纯数字数组
    const selectedRoleIds = Array.from(selectedRoles.value);
    const currentRoleIds = currentRoles.map((role) => Number(role.roleId));
    console.log("selectedRoleIds:", selectedRoleIds);
    console.log("currentRoleIds:", currentRoleIds);

    // 找出要新增的角色
    const rolesToAdd = selectedRoleIds.filter(
      (roleId) => !currentRoleIds.includes(roleId)
    );

    // 找出要删除的角色
    const rolesToDelete = currentRoleIds.filter((roleId) => {
      const role = currentRoles.find((r) => r.roleId === roleId);
      if (role.administratorName === "ROLE_USER") {
        return false;
      }
      return !selectedRoleIds.includes(roleId);
    });

    // 执行新增操作
    for (const roleId of rolesToAdd) {
      // console.log("Adding role:", roleId);
      // console.log("userId:", currentUser.value.user.userId);
      if (!roleId) continue; // Skip if roleId is null
      const res = await save({
        // 包装成正确的数据结构
        userId: currentUser.value.user.userId,
        roleId: roleId,
      });
      if (res.data.status !== 200) {
        throw new Error(`添加角色失败: ${res.data.message}`);
      }
    }

    // 执行删除操作
    for (const roleId of rolesToDelete) {
      if (!roleId) continue; // Skip if roleId is null
      const res = await deleteRole(currentUser.value.user.userId, roleId);
      if (res.data.status !== 200) {
        throw new Error(`删除角色失败: ${res.data.message}`);
      }
    }

    ElMessage.success("角色修改成功");
    roleDialogVisible.value = false;
    getUserList();
  } catch (error) {
    console.error("修改角色失败:", error);
    ElMessage.error(error.message || "修改角色失败");
  }
};

// 额外功能扩展
const selectedUsers = ref([]);
const userDetailVisible = ref(false);

/**
 * 用户数据的导入和导出
 */

// Excel列说明数据
const excelColumns = [
  { column: "B列", field: "用户名", required: true, description: "用户的登录名" },
  { column: "C列", field: "昵称", required: false, description: "用户显示名称" },
  { column: "D列", field: "邮箱", required: true, description: "用户邮箱地址" },
  { column: "E列", field: "状态", required: false, description: "已激活/已禁用" },
  { column: "J列", field: "角色", required: false, description: "用户角色，逗号分隔" },
];

// 添加处理文件上传的方法
const handleFileUpload = async (file) => {
  try {
    const res = await importUserData(file);
    if (res.data.status === 200) {
      ElMessage.success("导入成功");
      getUserList(); // 刷新列表
    } else {
      // 处理后端返回的详细错误信息
      if (res.data.message && res.data.message.includes("\n")) {
        // 使用 ElMessageBox 展示格式化的错误信息
        ElMessageBox.alert(res.data.message, "导入失败", {
          type: "error",
          customClass: "import-error-dialog",
          dangerouslyHtml: true,
        });
      } else {
        ElMessage.error(res.data.message || "导入失败");
      }
    }
    return false;
  } catch (error) {
    console.error("导入失败:", error);
    ElMessage.error("导入失败，请检查文件格式是否正确");
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

// 导出用户数据
const handleExport = () => {
  const exportData = tableData.value.map((item) => ({
    用户ID: item.user.userId,
    用户名: item.user.username,
    昵称: item.user.nickname,
    邮箱: item.user.email,
    状态: item.user.isActive ? "已激活" : "已禁用",
    注册时间: item.user.createdAt,
    最后登录: item.user.lastLoginTime,
    最后登录IP: item.user.lastLoginIp,
    登录地点: item.user.lastLoginLocation,
    角色: item.roles.map((role) => role.roleName).join(","),
  }));

  const worksheet = XLSX.utils.json_to_sheet(exportData);
  const workbook = XLSX.utils.book_new();
  XLSX.utils.book_append_sheet(workbook, worksheet, "用户列表");
  XLSX.writeFile(workbook, `用户列表_${new Date().toLocaleDateString()}.xlsx`);
};

// 批量操作相关方法
const selectedRows = computed(() => selectedUsers.value || []);

const handleSelectionChange = (val) => {
  selectedUsers.value = val || [];
};

const handleBatchActivate = async (isActive) => {
  if (!selectedUsers.value?.length) {
    ElMessage.warning('请先选择用户');
    return;
  }

  try {
    const message = isActive ? "激活" : "禁用";
    await ElMessageBox.confirm(`确定要批量${message}选中的用户吗？`, "提示", {
      confirmButtonText: "确定",
      cancelButtonText: "取消",
      type: "warning",
    });

    for (const item of selectedUsers.value) {
      if (item?.user?.userId) {
        await activate(item.user.userId, isActive);
      }
    }
    ElMessage.success(`批量${message}成功`);
    getUserList();
  } catch (error) {
    if (error !== "cancel") {
      ElMessage.error(`批量${isActive ? "激活" : "禁用"}失败`);
    }
  }
};

const handleBatchDelete = async () => {
  if (!selectedUsers.value?.length) {
    ElMessage.warning('请先选择用户');
    return;
  }

  try {
    await ElMessageBox.confirm("确定要删除选中的用户吗？", "警告", {
      confirmButtonText: "确定",
      cancelButtonText: "取消",
      type: "warning",
    });

    for (const item of selectedUsers.value) {
      if (item?.user?.userId) {
        await deleteById(item.user.userId);
      }
    }
    ElMessage.success("批量删除成功");
    getUserList();
  } catch (error) {
    if (error !== "cancel") {
      ElMessage.error("批量删除失败");
    }
  }
};

// 用户详情
const showUserDetail = (row) => {
  currentUser.value = row;
  userDetailVisible.value = true;
};
</script>

<template>
  <page-container>
    <el-card class="box-card">
      <!-- 搜索区域 -->
      <div class="search-bar">
  <el-input
    v-model="searchKey"
    placeholder="搜索用户名/邮箱"
    class="search-input"
    clearable
    @keyup.enter="handleSearch"
  >
    <template #prefix>
      <el-icon><Search /></el-icon>
    </template>
  </el-input>
  <el-select
    v-model="activeFilter"
    placeholder="状态筛选"
    clearable
    class="status-select"
  >
    <el-option label="已激活" :value="true" />
    <el-option label="已禁用" :value="false" />
  </el-select>
  <el-button type="primary" :icon="Search" @click="handleSearch">搜索</el-button>
  <el-button @click="handleReset">重置</el-button>
  <el-button type="success" @click="handleExport">
    <el-icon><Download /></el-icon>
    导出用户
  </el-button>

  <el-upload
    class="upload-button"
    :show-file-list="false"
    :before-upload="handleFileUpload"
    accept=".xlsx,.xls"
  >
    <el-button type="primary" :icon="Upload">导入用户</el-button>
  </el-upload>

  <!-- 导入说明 -->
  <el-popover
    placement="bottom"
    :width="400"
    trigger="hover"
    title="Excel 导入说明"
    :show-after="100"
  >
    <template #reference>
      <el-button type="info" circle>
        <el-icon><info-filled /></el-icon>
      </el-button>
    </template>

    <div class="import-guide">
      <div class="guide-section">
        <h4>Excel 文件格式要求：</h4>
        <el-table :data="excelColumns" border size="small">
          <el-table-column prop="column" label="列" width="60" align="center" />
          <el-table-column prop="field" label="字段" width="100" />
          <el-table-column prop="required" label="必填" width="60" align="center">
            <template #default="scope">
              <el-tag :type="scope.row.required ? 'danger' : 'info'" size="small">
                {{ scope.row.required ? "是" : "否" }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="description" label="说明" />
        </el-table>
      </div>

      <div class="guide-section">
        <h4>注意事项：</h4>
        <ul class="guide-tips">
          <li>默认密码为：123456</li>
          <li>状态可填：已激活、已禁用（默认已激活）</li>
          <li>角色为可选项，多个角色用逗号分隔</li>
          <li>重复的用户名或邮箱将被自动跳过</li>
        </ul>
      </div>

      <div class="guide-footer">
        <el-alert type="warning" :closable="false" show-icon>
          建议先导出现有数据作为模板使用
        </el-alert>
      </div>
    </div>
  </el-popover>
</div>

      <!-- 用户列表 -->
      <el-table
        :data="tableData"
        v-loading="loading"
        style="width: 100%"
        @selection-change="handleSelectionChange"
      >
        <el-table-column type="selection" width="55" />
        <el-table-column prop="user.userId" label="ID" width="80" />
        <el-table-column prop="user.username" label="用户名" width="120">
  <template #default="scope">
    <el-tooltip
      :content="scope.row.user.username"
      placement="top"
      :show-after="200"
      :hide-after="2000"
    >
      <el-button
        link
        type="primary"
        class="custom-username text-ellipsis"
        @click="showUserDetail(scope.row)"
      >
        {{ scope.row.user.username }}
      </el-button>
    </el-tooltip>
  </template>
</el-table-column>
        <el-table-column prop="user.nickname" label="昵称" width="120" />
        <el-table-column prop="user.email" label="邮箱" width="180" />
        <el-table-column prop="user.lastLoginIp" label="最后登录IP" width="140" />
        <el-table-column prop="user.lastLoginLocation" label="登录地点" width="140" />
        <el-table-column prop="user.createdAt" label="注册时间" width="180" />
        <el-table-column prop="user.lastLoginTime" label="最后登录" width="180" />
        <!-- 角色列 -->
        <el-table-column label="角色" width="200">
          <template #default="scope">
            <el-popover placement="top" trigger="hover" :width="300" :show-after="100">
              <template #reference>
                <div class="role-tags-container">
                  <el-tag
                    v-for="role in scope.row.roles"
                    :key="role.roleId"
                    class="mx-1"
                    size="small"
                    :type="getRoleTagType(role.administratorName)"
                  >
                    {{ role.roleName }}
                  </el-tag>
                </div>
              </template>
              <!-- 弹出框中显示完整的角色列表 -->
              <div class="role-popover-content">
                <h4>所有角色：</h4>
                <div class="role-tags-full">
                  <el-tag
                    v-for="role in scope.row.roles"
                    :key="role.roleId"
                    class="mx-1 mb-1"
                    size="small"
                    :type="getRoleTagType(role.administratorName)"
                  >
                    {{ role.roleName }}
                  </el-tag>
                </div>
              </div>
            </el-popover>
          </template>
        </el-table-column>
        <el-table-column prop="user.isActive" label="状态" width="100">
          <template #default="scope">
            <el-tag :type="scope.row.user.isActive ? 'success' : 'danger'">
              {{ scope.row.user.isActive ? "已激活" : "已禁用" }}
            </el-tag>
          </template>
        </el-table-column>
        <!-- 操作列 -->
       <el-table-column label="操作" width="120" fixed="right">
  <template #default="scope">
    <el-dropdown trigger="click">
      <el-button type="primary" size="small">
        操作
        <el-icon class="el-icon--right"><arrow-down /></el-icon>
      </el-button>
      <template #dropdown>
        <el-dropdown-menu>
          <el-dropdown-item @click="handleActivate(scope.row.user)">
            <el-icon :class="scope.row.user.isActive ? 'warning-icon' : 'success-icon'">
              <warning v-if="scope.row.user.isActive" />
              <check v-else />
            </el-icon>
            {{ scope.row.user.isActive ? "禁用" : "激活" }}
          </el-dropdown-item>
          <el-dropdown-item @click="handleEditRoles(scope.row)">
            <el-icon><edit /></el-icon>
            编辑角色
          </el-dropdown-item>
          <el-dropdown-item divided @click="handleDelete(scope.row.user)">
            <el-icon class="danger-icon"><delete /></el-icon>
            删除
          </el-dropdown-item>
        </el-dropdown-menu>
      </template>
    </el-dropdown>
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

    <!-- 添加角色编辑对话框 -->
    <el-dialog
      v-model="roleDialogVisible"
      title="分配角色"
      width="500px"
    >
      <el-form label-width="80px">
        <el-form-item label="角色">
          <el-checkbox-group v-model="selectedRoles">
            <el-checkbox v-for="role in allRoles" :key="role.roleId" :value="role.roleId">
              {{ role.roleName }}
            </el-checkbox>
          </el-checkbox-group>
        </el-form-item>
      </el-form>

      <!-- 添加调试信息 -->
      <div v-if="false">
        选中的角色ID: {{ selectedRoles }} 当前用户角色:
        {{ currentUser?.roles?.map((r) => r.roleId) }}
      </div>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="roleDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="confirmRoleEdit">确定</el-button>
        </span>
      </template>
    </el-dialog>

    <!-- 用户详情对话框 -->
    <el-dialog
      v-model="userDetailVisible"
      :title="'用户详情 - ' + currentUser?.user?.username"
      width="600px"
    >
      <el-descriptions :column="2" border>
        <el-descriptions-item label="用户ID">{{
          currentUser?.user?.userId
        }}</el-descriptions-item>
        <el-descriptions-item label="用户名">{{
          currentUser?.user?.username
        }}</el-descriptions-item>
        <el-descriptions-item label="昵称">{{
          currentUser?.user?.nickname
        }}</el-descriptions-item>
        <el-descriptions-item label="邮箱">{{
          currentUser?.user?.email
        }}</el-descriptions-item>
        <el-descriptions-item label="状态">
          <el-tag :type="currentUser?.user?.isActive ? 'success' : 'danger'">
            {{ currentUser?.user?.isActive ? "已激活" : "已禁用" }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="角色">
          <el-tag
            v-for="role in currentUser?.roles"
            :key="role.roleId"
            class="mx-1"
            size="small"
            :type="getRoleTagType(role.administratorName)"
          >
            {{ role.roleName }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="注册时间">{{
          currentUser?.user?.createdAt
        }}</el-descriptions-item>
        <el-descriptions-item label="最后登录">{{
          currentUser?.user?.lastLoginTime
        }}</el-descriptions-item>
        <el-descriptions-item label="登录IP">{{
          currentUser?.user?.lastLoginIp
        }}</el-descriptions-item>
        <el-descriptions-item label="登录地点">{{
          currentUser?.user?.lastLoginLocation
        }}</el-descriptions-item>
      </el-descriptions>
    </el-dialog>
  </page-container>
</template>

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

.mx-1 {
  margin: 0 4px;
}

.search-bar {
  display: flex;
  margin-bottom: 20px;
  gap: 10px;
  flex-wrap: wrap;
}

.search-input {
  width: 200px;
}

.status-select {
  width: 120px;
}

.warning-icon {
  color: var(--el-color-warning);
}
.success-icon {
  color: var(--el-color-success);
}
.danger-icon {
  color: var(--el-color-danger);
}

:deep(.el-dropdown-menu__item i) {
  margin-right: 8px;
}

.batch-operations {
  margin-bottom: 16px;
  display: flex;
  gap: 8px;
}

.el-descriptions {
  margin: 20px 0;
}

.custom-username {
  font-size: 14px;
  font-weight: 500;
  padding: 2px 8px;
  height: auto;
  transition: all 0.3s;
  border-radius: 4px;
  color: #eb2f96;
}

.custom-username:hover {
  color: #c41d7f;
  text-decoration: underline;
}

.import-guide {
  padding: 10px;
}

.guide-section {
  margin-bottom: 20px;
}

.guide-section h4 {
  margin: 0 0 10px;
  color: var(--el-color-primary);
  font-size: 14px;
}

.guide-tips {
  margin: 0;
  padding-left: 20px;
  font-size: 13px;
  color: #666;
}

.guide-tips li {
  margin: 5px 0;
  line-height: 1.5;
}

.guide-footer {
  margin-top: 15px;
}

/* 美化上传按钮组 */
.upload-actions {
  display: flex;
  gap: 8px;
  align-items: center;
}

.upload-button {
  display: inline-block;
}

/* 调整表格内容的对齐方式 */
:deep(.el-table .cell) {
  white-space: nowrap;
}

:deep(.el-tag--small) {
  height: 20px;
  padding: 0 6px;
  font-size: 12px;
}

/* 添加导入错误弹窗样式 */
:deep(.import-error-dialog) {
  max-height: 60vh;
  overflow-y: auto;
}

:deep(.import-error-dialog .el-message-box__content) {
  white-space: pre-line;
  font-family: monospace;
  line-height: 1.6;
  padding: 12px;
  background: #f8f9fa;
  border-radius: 4px;
  margin: 10px 0;
}

:deep(.import-error-dialog .el-message-box__status) {
  font-size: 24px;
}

.role-tags-container {
  display: flex;
  flex-wrap: wrap;
  gap: 4px;
  min-height: 24px;
}

.role-popover-content {
  padding: 8px;
}

.role-popover-content h4 {
  margin: 0 0 8px 0;
  font-size: 14px;
  color: #606266;
}

.role-tags-full {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}

.mb-1 {
  margin-bottom: 4px;
}

.text-ellipsis {
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  max-width: 100%;
  display: block;
}
</style>
