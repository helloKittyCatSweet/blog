<script setup>
import { ref, onMounted } from "vue";
import { ElMessage, ElMessageBox } from "element-plus";
import { Search, Plus, Edit, Delete } from "@element-plus/icons-vue";

import { create, update, deleteById, findAll, findByCombined } from "@/api/common/tag.js";

// 表格数据
const tableData = ref([]);
const loading = ref(false);

// 分页
const total = ref(0);
const currentPage = ref(1);
const pageSize = ref(10);

// 对话框控制
const dialogVisible = ref(false);
const dialogTitle = ref("新增标签");
const formRef = ref();
const form = ref({
  tagId: null,
  name: "",
  weight: 0,
});

// 表单校验规则
const rules = {
  name: [
    { required: true, message: "请输入标签名称", trigger: "blur" },
    { min: 1, max: 20, message: "长度在 1 到 20 个字符", trigger: "blur" },
  ],
  weight: [
    { required: true, message: "请输入权重", trigger: "blur" },
    { type: "number", message: "权重必须为数字", trigger: "blur" },
  ],
};

/**
 * 搜索
 */
// 搜索条件
const searchKey = ref("");
const weightValue = ref(null);
const weightOperator = ref("GREATER_THAN"); // eq: 等于, gt: 大于, lt: 小于

// 重置搜索条件
const resetSearch = () => {
  searchKey.value = "";
  weightValue.value = null;
  weightOperator.value = "GREATER_THAN";
  getTagList();
};

// 获取标签列表
const getTagList = async () => {
  loading.value = true;
  try {
    // 判断是否有搜索条件
    const hasSearchConditions = searchKey.value || weightValue.value !== null;

    if (hasSearchConditions) {
      // 有搜索条件时使用组合查询
      const res = await findByCombined({
        name: searchKey.value,
        weightOperator: weightValue.value !== null ? weightOperator.value : null,
        weightValue: weightValue.value,
        pageNum: currentPage.value,
        pageSize: pageSize.value,
      });
      // 修改数据解析方式
      tableData.value = Object.values(res.data.data).filter(
        (item) => typeof item === "object"
      );
      total.value = res.data.data.length;
    } else {
      // 无搜索条件时使用findAll
      const res = await findAll();
      tableData.value = res.data.data;
      total.value = tableData.value.length;
    }
  } catch (error) {
    ElMessage.error("获取标签列表失败");
  }
  loading.value = false;
};

// 新增/编辑标签
const handleEdit = (row) => {
  dialogTitle.value = row ? "编辑标签" : "新增标签";
  form.value = row ? { ...row } : { tagId: null, name: "", weight: 0 };
  dialogVisible.value = true;
};

// 删除标签
const handleDelete = async (row) => {
  try {
    await ElMessageBox.confirm("确认删除该标签吗？", "提示", {
      type: "warning",
    });

    const response = await deleteById(row.tagId);
    if (response.status === 200) {
      // 使用 filter 直接在前端过滤数据
      tableData.value = tableData.value.filter((item) => item.tagId !== row.tagId);
      ElMessage.success("删除成功");
    }
  } catch (error) {
    if (error !== "cancel") {
      ElMessage.error("删除失败");
    }
  }
};

// 提交表单
const submitForm = async () => {
  if (!formRef.value) return;
  await formRef.value.validate(async (valid) => {
    if (valid) {
      try {
        let response;
        if (form.value.tagId) {
          response = await update(form.value);
          if (response.status === 200) {
            // 更新现有数据
            const index = tableData.value.findIndex(
              (item) => item.tagId === form.value.tagId
            );
            if (index !== -1) {
              tableData.value[index] = { ...form.value };
            }
            ElMessage.success("更新成功");
          }
        } else {
          response = await create({
            name: form.value.name,
            weight: form.value.weight,
          });
          if (response.status === 200) {
            // 添加新数据到列表
            tableData.value.push(response.data.data);
            ElMessage.success("创建成功");
          }
        }
        dialogVisible.value = false;
      } catch (error) {
        ElMessage.error("保存失败");
      }
    }
  });
};

// 监听页面加载
onMounted(() => {
  getTagList();
});
</script>

<template>
  <page-container title="标签管理">
    <!-- 搜索栏 -->
    <el-card class="search-card">
      <el-row :gutter="20">
        <el-col :span="6">
          <el-input
            v-model="searchKey"
            placeholder="请输入标签名称"
            clearable
            @keyup.enter="getTagList"
          >
            <template #suffix>
              <el-icon class="el-input__icon">
                <Search />
              </el-icon>
            </template>
          </el-input>
        </el-col>
        <el-col :span="8">
          <el-row :gutter="10">
            <el-col :span="8">
              <el-select v-model="weightOperator" placeholder="权重条件">
                <el-option label="等于" value="EQUAL" />
                <el-option label="大于" value="GREATER_THAN" />
                <el-option label="小于" value="LESS_THAN" />
              </el-select>
            </el-col>
            <el-col :span="16">
              <el-input-number
                v-model="weightValue"
                :min="0"
                :max="100"
                placeholder="权重值"
                clearable
              />
            </el-col>
          </el-row>
        </el-col>
        <el-col :span="10" style="text-align: right">
          <el-button type="primary" :icon="Search" @click="getTagList">搜索</el-button>
          <el-button @click="resetSearch">重置</el-button>
        </el-col>
      </el-row>
    </el-card>

    <!-- 表格 -->
    <el-card class="table-card">
      <div class="table-header">
        <div class="right">
          <el-button type="primary" :icon="Plus" @click="handleEdit()"
            >新增标签</el-button
          >
        </div>
      </div>
      <el-table :data="tableData" v-loading="loading" border stripe>
        <el-table-column type="index" label="序号" width="80" align="center" />
        <el-table-column prop="name" label="标签名称" />
        <el-table-column prop="weight" label="权重" width="120" align="center" />
        <el-table-column label="操作" width="200" align="center">
          <template #default="{ row }">
            <el-button type="primary" :icon="Edit" circle @click="handleEdit(row)" />
            <el-button type="danger" :icon="Delete" circle @click="handleDelete(row)" />
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
          @update:current-page="getTagList"
          @update:page-size="getTagList"
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
      <el-form ref="formRef" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="标签名称" prop="name">
          <el-input v-model="form.name" placeholder="请输入标签名称" />
        </el-form-item>
        <el-form-item label="权重" prop="weight">
          <el-input-number v-model="form.weight" :min="0" :max="100" />
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
      .total-count {
        font-size: 14px;
        color: var(--el-text-color-secondary);
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
