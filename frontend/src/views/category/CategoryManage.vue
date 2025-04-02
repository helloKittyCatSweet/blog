<script setup>
import { ref, onMounted } from "vue";
import { ElMessage, ElMessageBox } from "element-plus";
import { Search, Plus, Edit, Delete } from "@element-plus/icons-vue";
import { create, update, deleteById, findAll } from "@/api/common/category.js";

// 表格数据
const tableData = ref([]);
const loading = ref(false);
const categoryOptions = ref([]); // 用于存储分类选项

// 分页
const total = ref(0);
const currentPage = ref(1);
const pageSize = ref(10);

// 搜索条件
const searchKey = ref("");

// 对话框控制
const dialogVisible = ref(false);
const dialogTitle = ref("新增分类");
const formRef = ref();
const form = ref({
  categoryId: null,
  name: "",
  description: "",
  parentCategoryId: null,
});

// 表单校验规则
const rules = {
  name: [
    { required: true, message: "请输入分类名称", trigger: "blur" },
    { min: 1, max: 50, message: "长度在 1 到 50 个字符", trigger: "blur" },
  ],
  description: [
    { required: true, message: "请输入分类描述", trigger: "blur" },
    { max: 200, message: "描述不能超过200个字符", trigger: "blur" },
  ],
  parentCategoryId: [{ required: true, message: "请选择父分类", trigger: "change" }],
};

// 获取父分类名称
const getCategoryName = (categoryId) => {
  if (!categoryId) return "无";
  const category = categoryOptions.value.find((item) => item.categoryId === categoryId);
  return category ? category.name : "未知分类";
};

// Reset search conditions
const resetSearch = () => {
  searchKey.value = "";
  getCategoryList();
};

// Get category list
const getCategoryList = async () => {
  loading.value = true;
  try {
    const res = await findAll();
    console.log("getCategoryList res: ", res);
    if (res.data.status === 200) {
      // 直接使用返回的树状结构数据
      tableData.value = res.data.data;
      // 获取所有分类用于父分类选择
      categoryOptions.value = flattenCategories(res.data.data);
      total.value = categoryOptions.value.length;
    }
  } catch (error) {
    ElMessage.error("获取分类列表失败");
  }
  loading.value = false;
};

// 添加一个扁平化处理树状数据的方法
const flattenCategories = (trees) => {
  const result = [];
  const flatten = (items) => {
    items.forEach((item) => {
      result.push(item.category);
      if (item.children && item.children.length > 0) {
        flatten(item.children);
      }
    });
  };
  flatten(trees);
  return result;
};

// Add/Edit category
const handleEdit = (row) => {
  dialogTitle.value = row ? "编辑分类" : "新增分类";
  form.value = row
    ? {
        categoryId: row.categoryId,
        name: row.name,
        description: row.description,
        parentCategoryId: row.parentCategoryId || null,
      }
    : { categoryId: null, name: "", description: "", parentCategoryId: null };
  dialogVisible.value = true;
};

// Delete category
const handleDelete = async (row) => {
  try {
    // 检查是否存在子分类
    const hasChildren = tableData.value.some(
      (item) =>
        item.children &&
        item.children.some((child) => child.category.parentCategoryId === row.categoryId)
    );

    if (hasChildren) {
      ElMessage.warning("该分类下还有子分类，请先删除子分类");
      return;
    }

    await ElMessageBox.confirm("确认删除该分类吗？", "提示", {
      type: "warning",
    });

    const response = await deleteById(row.categoryId);
    if (response.status === 200) {
      ElMessage.success("删除成功");
      getCategoryList(); // 重新获取列表以更新树形结构
    }
  } catch (error) {
    if (error !== "cancel") {
      ElMessage.error("删除失败");
    }
  }
};

// Submit form
const submitForm = async () => {
  if (!formRef.value) return;
  await formRef.value.validate(async (valid) => {
    if (valid) {
      try {
        let response;
        if (form.value.categoryId) {
          response = await update(form.value);
        } else {
          console.log("create category description: ", form.value.description);
          response = await create({
            name: form.value.name,
            description: form.value.description,
            parentCategoryId: form.value.parentCategoryId,
          });
        }

        if (response.status === 200) {
          ElMessage.success(form.value.categoryId ? "更新成功" : "创建成功");
          dialogVisible.value = false;
          getCategoryList(); // Refresh the list
        }
      } catch (error) {
        ElMessage.error("保存失败");
      }
    }
  });
};

// Load data when component mounted
onMounted(() => {
  getCategoryList();
});
</script>

<template>
  <page-container title="分类管理">
    <!-- 搜索栏 -->
    <el-card class="search-card">
      <el-row :gutter="20">
        <el-col :span="6">
          <el-input
            v-model="searchKey"
            placeholder="请输入分类名称"
            clearable
            @keyup.enter="getCategoryList"
          >
            <template #suffix>
              <el-icon class="el-input__icon">
                <Search />
              </el-icon>
            </template>
          </el-input>
        </el-col>
        <el-col :span="10" style="text-align: right">
          <el-button type="primary" :icon="Search" @click="getCategoryList"
            >搜索</el-button
          >
          <el-button @click="resetSearch">重置</el-button>
        </el-col>
      </el-row>
    </el-card>

    <!-- 表格 -->
    <el-card class="table-card">
      <div class="table-header">
        <div class="left">
          <span>共 {{ total }} 条</span>
          <span class="separator">/</span>
          <span>{{ pageSize }}条/页</span>
        </div>
        <div class="right">
          <el-button type="primary" :icon="Plus" @click="handleEdit()"
            >新增分类</el-button
          >
        </div>
      </div>
      <el-table
        :data="tableData"
        v-loading="loading"
        border
        stripe
        default-expand-all
        row-key="category.categoryId"
        :tree-props="{
          children: 'children',
          hasChildren: (row) => row.children && row.children.length > 0,
        }"
      >
        <el-table-column type="index" label="序号" width="80" align="center" />
        <el-table-column label="分类名称">
          <template #default="{ row }">
            {{ row.category.name }}
          </template>
        </el-table-column>
        <el-table-column label="描述" show-overflow-tooltip>
          <template #default="{ row }">
            {{ row.category.description || "-" }}
          </template>
        </el-table-column>
        <el-table-column label="操作" width="200" align="center">
          <template #default="{ row }">
            <el-button
              type="primary"
              :icon="Edit"
              circle
              @click="handleEdit(row.category)"
            />
            <el-button
              type="danger"
              :icon="Delete"
              circle
              :disabled="row.children && row.children.length > 0"
              @click="handleDelete(row.category)"
            />
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页部分 -->
      <div class="pagination">
        <el-pagination
          v-model:current-page="currentPage"
          v-model:page-size="pageSize"
          :total="total"
          :page-sizes="[10, 20, 50, 100]"
          :background="true"
          layout="total, sizes, prev, pager, next, jumper"
          @update:current-page="getCategoryList"
          @update:page-size="getCategoryList"
        />
      </div>
    </el-card>

    <!-- 新增/编辑对话框 -->
    <el-dialog
      v-model="dialogVisible"
      :title="dialogTitle"
      width="500px"
      @close="formRef?.resetFields()"
    >
      <el-form
        ref="formRef"
        :model="form"
        :rules="rules"
        label-width="80px"
        @keyup.enter="submitForm"
      >
        <el-form-item label="父分类" prop="parentCategoryId">
          <el-select
            v-model="form.parentCategoryId"
            filterable
            placeholder="请选择父分类"
            style="width: 100%"
          >
            <el-option key="0" label="自立门户" :value="0" />
            <el-option
              v-for="item in categoryOptions"
              :key="item.categoryId"
              :label="item.name"
              :value="item.categoryId"
              :disabled="form.categoryId === item.categoryId"
            >
              <span>{{ item.name }}</span>
              <span
                v-if="item.description"
                style="color: var(--el-text-color-secondary); font-size: 13px"
              >
                ({{ item.description }})
              </span>
            </el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="分类名称" prop="name">
          <el-input v-model="form.name" placeholder="请输入分类名称" />
        </el-form-item>
        <el-form-item label="描述" prop="description">
          <el-input
            v-model="form.description"
            type="textarea"
            :rows="3"
            placeholder="请输入分类描述"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitForm">确定</el-button>
      </template>
    </el-dialog>
  </page-container>
</template>

<style lang="scss" scoped>
.search-card {
  margin-bottom: 20px;
}

.table-card {
  .table-header {
    margin-bottom: 20px;
    display: flex;
    justify-content: space-between;
    align-items: center;

    .left {
      color: var(--el-text-color-secondary);
      font-size: 14px;

      .separator {
        margin: 0 8px;
      }
    }
  }

  .pagination {
    margin-top: 20px;
    display: flex;
    justify-content: flex-end;
  }
}
</style>
