<script setup>
import { ref } from "vue";
import { ElMessage, ElMessageBox } from "element-plus";
import { Search, Edit, Delete, Plus } from "@element-plus/icons-vue";
import PageContainer from "@/components/PageContainer.vue";

const loading = ref(false);
const tableData = ref([]);

// 搜索条件
const searchKey = ref("");

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

// 处理删除
const handleDelete = (row) => {
  ElMessageBox.confirm("确认删除该角色吗？", "警告", {
    confirmButtonText: "确定",
    cancelButtonText: "取消",
    type: "warning",
  }).then(() => {
    // TODO: 调用删除API
  });
};

// 处理新增
const handleAdd = () => {
  dialogTitle.value = "新增角色";
  form.value = {
    roleId: null,
    roleName: "",
    description: "",
    administratorName: "",
  };
  dialogVisible.value = true;
};

// 提交表单
const submitForm = async () => {
  if (!formRef.value) return;
  await formRef.value.validate((valid) => {
    if (valid) {
      // TODO: 调用保存API
    }
  });
};
</script>

<template>
  <page-container title="角色管理">
    <!-- 搜索栏 -->
    <el-card class="search-card">
      <el-row :gutter="20">
        <el-col :span="6">
          <el-input
            v-model="searchKey"
            placeholder="搜索角色名称"
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
        <el-col :span="18" style="text-align: right">
          <el-button type="primary" :icon="Plus" @click="handleAdd">新增角色</el-button>
        </el-col>
      </el-row>
    </el-card>

    <!-- 表格 -->
    <el-card class="table-card">
      <el-table v-loading="loading" :data="tableData" border stripe>
        <template #empty>
          <el-empty description="暂无数据" />
        </template>
        <el-table-column type="index" label="序号" width="80" align="center" />
        <el-table-column prop="roleName" label="角色名称" show-overflow-tooltip />
        <el-table-column prop="description" label="角色描述" show-overflow-tooltip />
        <el-table-column prop="administratorName" label="管理员" show-overflow-tooltip />
        <el-table-column label="操作" width="150" align="center">
          <template #default="{ row }">
            <el-button
              type="primary"
              :icon="Edit"
              circle
              @click="handleEdit(row)"
              title="编辑"
            />
            <el-button
              type="danger"
              :icon="Delete"
              circle
              @click="handleDelete(row)"
              title="删除"
            />
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
          <el-input
            v-model="form.description"
            type="textarea"
            :rows="3"
            placeholder="请输入角色描述"
          />
        </el-form-item>
        <el-form-item label="管理员" prop="administratorName">
          <el-input v-model="form.administratorName" placeholder="请输入管理员名称" />
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
</style>
