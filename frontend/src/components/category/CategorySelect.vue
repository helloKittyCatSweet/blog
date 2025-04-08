<script setup>
import { ref, onMounted, watch } from "vue";
import { ElMessage } from "element-plus";
import { findAll as findAllCategories } from "@/api/common/category.js";

const props = defineProps({
  modelValue: {
    type: Number,
    default: null,
  },
  category: {
    type: Object,
    default: null,
  },
});

const emit = defineEmits(["update:modelValue", "update:category"]);

const categories = ref([]);
// 确保初始值不为 undefined
const selectedValue = ref(props.modelValue ?? null);

// 深度处理树形数据，确保每个节点结构正确
const processTreeData = (items) => {
  if (!Array.isArray(items)) return [];

  return items.map((item) => ({
    value: item.category?.categoryId,
    label: item.category?.name,
    description: item.category?.description,
    children: processTreeData(item.children || []),
    // 保存原始数据用于回传
    rawCategory: item.category,
  }));
};

const getAllCategories = async () => {
  try {
    const response = await findAllCategories();
    console.log("获取到的分类数据:", response.data);
    if (response.data.status === 200) {
      categories.value = processTreeData(response.data.data);
      console.log("处理后的分类数据:", categories.value);
    } else {
      categories.value = [];
    }
  } catch (error) {
    console.error("获取分类列表失败:", error);
    ElMessage.error("获取分类列表失败");
    categories.value = [];
  }
};

const handleCategoryChange = (val, evt) => {
  console.log("handleCategoryChange 被触发, val:", val, "evt:", evt);

  // 如果是事件对象，说明是复选框点击触发
  if (val && val.target) {
    console.log("是复选框点击事件，返回");
    return;
  }

  selectedValue.value = val;
  console.log("selectedValue 已更新:", selectedValue.value);

  if (val !== null && val !== undefined) {
    const numericVal = Number(val);
    console.log("转换后的数值:", numericVal);
    const selectedCategory = findCategoryById(categories.value, numericVal);
    console.log("categories 数据:", categories.value);
    console.log("查找到的分类:", selectedCategory);

    emit("update:category", selectedCategory || null);
    emit("update:modelValue", numericVal);
  } else {
    console.log("清空选择");
    emit("update:category", null);
    emit("update:modelValue", null);
  }
};

// 优化后的查找方法
const findCategoryById = (categoriesArray, id) => {
  if (!categoriesArray || !Array.isArray(categoriesArray)) return null;

  for (const item of categoriesArray) {
    if (item.value === id) {
      return item.rawCategory;
    }
    if (item.children?.length) {
      const found = findCategoryById(item.children, id);
      if (found) return found;
    }
  }
  return null;
};

// 监听 props 变化
watch(
  () => props.modelValue,
  (newVal) => {
    selectedValue.value = newVal ?? null;
  },
  { immediate: true }
);

onMounted(() => {
  getAllCategories();
});
</script>

<template>
  <el-tree-select
    v-model="selectedValue"
    :data="categories"
    :check-strictly="true"
    :render-after-expand="false"
    node-key="value"
    ckeck-on-click-node
    placeholder="请选择分类"
    clearable
    filterable
    style="width: 100%"
    @change="handleCategoryChange"
    @select="handleCategoryChange"
  >
    <template #default="{ node, data }">
      <div class="custom-tree-node" :class="{ 'is-selected': node.isCurrent }">
        <span>{{ data.label }}</span>
        <span v-if="data.description" class="description">
          ({{ data.description }})
        </span>
      </div>
    </template>
  </el-tree-select>
</template>

<style scoped>
/* 保持原有样式不变 */
.custom-tree-node {
  display: flex;
  align-items: center;
  gap: 8px;
}

.custom-tree-node.is-selected {
  color: var(--el-color-primary);
  font-weight: 500;
}

.description {
  color: var(--el-text-color-secondary);
  font-size: 13px;
}

:deep(.el-tree-node.is-checked > .el-tree-node__content) {
  background-color: var(--el-color-primary-light-9);
}

:deep(.el-tree-node__content:hover) {
  background-color: var(--el-fill-color-light);
}
</style>
