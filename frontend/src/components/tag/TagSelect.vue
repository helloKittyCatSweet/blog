<script setup>
import { ref, onMounted, watch, computed, nextTick } from "vue";
import { ElMessage } from "element-plus";
import { Plus } from "@element-plus/icons-vue";
import { findAll as findAllTags, create as createTag } from "@/api/common/tag.js";

const props = defineProps({
  modelValue: {
    type: Array,
    default: () => [],
  },
  disabled: {
    type: Boolean,
    default: false
  }
});

const emit = defineEmits(["update:modelValue"]);

const tags = ref([]);
const selectedTags = ref([]);
// 创建标签相关
const dialogVisible = ref(false);
const formRef = ref();
const form = ref({
  name: "",
});

// 表单校验规则
const rules = {
  name: [
    { required: true, message: "请输入标签名称", trigger: "blur" },
    { min: 1, max: 20, message: "长度在 1 到 20 个字符", trigger: "blur" },
    {
      pattern: /^[\u4e00-\u9fa5a-zA-Z]+$/,
      message: "标签名称只能包含中文或英文字母",
      trigger: ["blur", "change"],
    },
  ],
};

// 监听 modelValue 变化
watch(
  () => props.modelValue,
  (newVal) => {
    selectedTags.value = newVal.map((tag) => tag.tagId);
  },
  { immediate: true }
);

// 获取所有标签
const getAllTags = async () => {
  try {
    const response = await findAllTags();
    if (response.data.status === 200) {
      tags.value = response.data.data;
    }
  } catch (error) {
    ElMessage.error("获取标签列表失败");
  }
};

// 处理标签变化
const handleTagChange = (val) => {
  const selectedTagObjects = val.map((tagId) => {
    if (typeof tagId === "string") {
      return {
        tagId: null,
        name: tagId,
        weight: 0,
      };
    }
    return tags.value.find((tag) => tag.tagId === tagId);
  });
  emit("update:modelValue", selectedTagObjects);
};

// 创建新标签
const handleCreateTag = async () => {
  if (!formRef.value) return;
  await formRef.value.validate(async (valid) => {
    if (valid) {
      try {
        const response = await createTag({
          name: form.value.name,
        });

        if (response.data.status === 201 || response.data.status === 200) {
          ElMessage.success("创建成功");
          dialogVisible.value = false;
          await getAllTags();
          form.value.name = "";
        } else {
          ElMessage.error(response.data.message || "创建失败");
        }
      } catch (error) {
        console.error("创建标签失败:", error);
        ElMessage.error(error.response?.data?.message || "创建标签失败");
      }
    }
  });
};

onMounted(() => {
  getAllTags();
});

const searchQuery = ref("");

// 过滤标签
const filteredTags = computed(() => {
  const query = searchQuery.value.toLowerCase();
  return tags.value.filter((tag) => !query || tag.name.toLowerCase().includes(query));
});

// 过滤方法只用于获取搜索词
const filterMethod = (query) => {
  searchQuery.value = query;
  return true;
};

/**
 * 对话框自动获取焦点
 */
const inputRef = ref(null);

const handleDialogOpen = () => {
  nextTick(() => {
    inputRef.value?.input?.focus();
  });
};
</script>

<template>
  <div class="tag-select-container">
    <el-select
      v-model="selectedTags"
      multiple
      filterable
      :filter-method="filterMethod"
      :reserve-keyword="false"
      placeholder="请选择标签"
      style="width: 100%"
      @change="handleTagChange"
      :disabled="disabled"
    >
      <template #prefix>
        <el-button 
          type="primary" 
          link 
          :icon="Plus" 
          @click.stop="dialogVisible = true"
          :disabled="disabled"
        >
          创建标签
        </el-button>
      </template>

      <el-option
        v-for="item in filteredTags"
        :key="item.tagId"
        :label="item.name"
        :value="item.tagId"
      >
        <span>{{ item.name }}</span>
        <span
          v-if="item.weight"
          style="color: var(--el-text-color-secondary); font-size: 13px"
        >
          (权重: {{ item.weight }})
        </span>
      </el-option>
    </el-select>

    <!-- 创建标签对话框 -->
    <el-dialog
      v-model="dialogVisible"
      title="创建新标签"
      width="400px"
      @close="form.name = ''"
      @open="handleDialogOpen"
      @submit.prevent
    >
      <el-form ref="formRef" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="标签名称" prop="name">
          <el-input
            ref="inputRef"
            v-model="form.name"
            placeholder="请输入标签名称"
            @keydown.enter.prevent="handleCreateTag"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleCreateTag">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<style scoped>
.tag-select-container {
  width: 100%;
}
</style>
