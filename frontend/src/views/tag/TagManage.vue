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
  adminWeight: 10,
});

// 表单校验规则
const rules = {
  name: [
    { required: true, message: "请输入标签名称", trigger: "blur" },
    { min: 1, max: 20, message: "长度在 1 到 20 个字符", trigger: "blur" },
  ],
  adminWeight: [
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
  currentPage.value = 1;
  pageSize.value = 10;
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
        page: currentPage.value - 1,
        size: pageSize.value,
        sort: ['useCount,desc']
      });
      if (res.data.status === 200) {
        tableData.value = res.data.data.content;
        total.value = res.data.data.totalElements;
      }
    } else {
      // 无搜索条件时使用findAll
      const res = await findAll({
        page: currentPage.value - 1,
        size: pageSize.value,
        sort: ['weight,desc']
      });
      if (res.data.status === 200) {
        tableData.value = res.data.data.content;
        total.value = res.data.data.totalElements;
      }
    }
  } catch (error) {
    console.error("获取标签列表失败:", error);
    ElMessage.error("获取标签列表失败");
  }
  loading.value = false;
};

// 新增/编辑标签
const handleEdit = (row) => {
  dialogTitle.value = row ? "编辑标签" : "新增标签";
  if (row) {
    // 修改
    form.value = {
      tagId: row.tagId,
      name: row.name,
      adminWeight: Number(row.adminWeight),
    };
  } else {
    form.value = {
      tagId: null,
      name: "",
      adminWeight: 10,
    };
  }
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
          response = await update({
            tagId: form.value.tagId,
            name: form.value.name,
            adminWeight: form.value.adminWeight,
          });
          if (response.data.status === 200) {
            // 检查 data.status
            await getTagList();
            ElMessage.success("更新成功");
            dialogVisible.value = false;
          } else {
            ElMessage.error(response.data.message || "更新失败");
          }
        } else {
          response = await create({
            name: form.value.name,
            adminWeight: form.value.adminWeight,
          });
          if (response.data.status === 200) {
            // 检查 data.status
            await getTagList();
            ElMessage.success("创建成功");
            dialogVisible.value = false;
          } else {
            ElMessage.error(response.data.message || "创建失败");
          }
        }
      } catch (error) {
        console.error("保存失败:", error);
        ElMessage.error("保存失败");
      }
    }
  });
};

// 监听页面加载
onMounted(() => {
  getTagList();
});

// 标签权重计算规则
const weightRuleDialogVisible = ref(false);

// 权重详细说明数据
const weightDetails = [
  {
    title: "基础权重",
    content: "每个标签的基础分值，固定为10分。",
    tag: "固定值",
    tagType: "info",
  },
  {
    title: "使用权重",
    content: "标签被文章使用时累计，每次使用增加1.0分。",
    tag: "累计值",
    tagType: "success",
  },
  {
    title: "点击权重",
    content: "标签被用户点击时累计，每次点击增加0.5分。",
    tag: "累计值",
    tagType: "success",
  },
  {
    title: "时间权重",
    content: "基于最后使用时间计算，距今时间越久，权重越低（衰减系数0.9）。",
    tag: "动态值",
    tagType: "warning",
  },
  {
    title: "管理员权重",
    content: "由管理员手动设置的权重值，用于人工干预标签排序。",
    tag: "可调节",
    tagType: "primary",
  },
];

// 注意事项数据
const notes = [
  "最终权重会被自动限制在0-100之间",
  "权重会在标签被使用或点击时自动更新",
  "管理员可以通过设置管理员权重来手动调整标签的整体权重",
];
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
        <div class="left">
          <el-button type="info" link @click="weightRuleDialogVisible = true">
            查看标签权重计算规则
          </el-button>
        </div>
        <div class="right">
          <el-button type="primary" :icon="Plus" @click="handleEdit()"
            >新增标签</el-button
          >
        </div>
      </div>
      <el-table :data="tableData" v-loading="loading" border stripe>
        <el-table-column type="index" label="序号" width="80" align="center" />
        <el-table-column prop="name" label="标签名称" />
        <el-table-column prop="weight" label="权重" width="100" align="center" />
        <el-table-column
          prop="adminWeight"
          label="管理员权重"
          width="100"
          align="center"
        />
        <el-table-column prop="useCount" label="使用次数" width="100" align="center" />
        <el-table-column prop="clickCount" label="点击次数" width="100" align="center" />
        <el-table-column label="最后使用时间" width="160" align="center">
          <template #default="{ row }">
            {{ row.lastUsedAt ? new Date(row.lastUsedAt).toLocaleString() : "-" }}
          </template>
        </el-table-column>
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
      <el-form ref="formRef" :model="form" :rules="rules" label-width="100px">
        <el-form-item label="标签名称" prop="name">
          <el-input v-model="form.name" placeholder="请输入标签名称" />
        </el-form-item>
        <el-form-item label="管理员权重" prop="adminWeight">
          <el-input-number v-model="form.adminWeight" :min="0" :max="100" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitForm">确定</el-button>
      </template>
    </el-dialog>

    <!-- 权重计算规则说明对话框 -->
    <el-dialog v-model="weightRuleDialogVisible" title="标签权重计算规则" width="600px">
      <div class="weight-rule">
        <div class="formula-box">
          <h4>权重计算公式</h4>
          <div class="formula">
            <span class="result">最终权重</span>
            <span class="equal">=</span>
            <span class="item">基础权重(10)</span>
            <span class="operator">+</span>
            <span class="item">使用次数×1.0</span>
            <span class="operator">+</span>
            <span class="item">点击次数×0.5</span>
            <span class="operator">+</span>
            <span class="item">时间权重×5</span>
            <span class="operator">+</span>
            <span class="item">管理员权重</span>
          </div>
        </div>

        <el-divider />

        <div class="weight-details">
          <h4>权重详细说明</h4>
          <el-card
            v-for="(item, index) in weightDetails"
            :key="index"
            class="detail-card"
          >
            <template #header>
              <div class="card-header">
                <span>{{ item.title }}</span>
                <el-tag :type="item.tagType" effect="plain" size="small">
                  {{ item.tag }}
                </el-tag>
              </div>
            </template>
            <div class="card-content">{{ item.content }}</div>
          </el-card>
        </div>

        <el-divider />

        <div class="notes">
          <h4>注意事项</h4>
          <el-alert
            v-for="(note, index) in notes"
            :key="index"
            :title="note"
            type="info"
            :closable="false"
            class="note-item"
          />
        </div>
      </div>
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

.weight-rule {
  padding: 10px;

  .formula-box {
    background-color: var(--el-bg-color-page);
    padding: 20px;
    border-radius: 8px;

    .formula {
      display: flex;
      align-items: center;
      flex-wrap: wrap;
      gap: 8px;
      margin-top: 12px;
      padding: 15px;
      background: var(--el-bg-color);
      border-radius: 6px;
      box-shadow: 0 2px 4px rgba(0, 0, 0, 0.05);

      span {
        font-size: 15px;
        &.result {
          color: var(--el-color-primary);
          font-weight: bold;
        }
        &.equal {
          color: var(--el-text-color-secondary);
          margin: 0 4px;
        }
        &.operator {
          color: var(--el-text-color-secondary);
        }
        &.item {
          color: var(--el-text-color-regular);
          background: var(--el-bg-color-page);
          padding: 4px 8px;
          border-radius: 4px;
        }
      }
    }
  }

  .weight-details {
    margin: 20px 0;

    .detail-card {
      margin-bottom: 12px;

      .card-header {
        display: flex;
        justify-content: space-between;
        align-items: center;
      }

      .card-content {
        color: var(--el-text-color-regular);
        font-size: 14px;
        line-height: 1.6;
      }
    }
  }

  .notes {
    .note-item {
      margin-bottom: 8px;
    }
  }

  h4 {
    font-size: 16px;
    margin: 16px 0 12px;
    color: var(--el-text-color-primary);
    &:first-child {
      margin-top: 0;
    }
  }
}
</style>
