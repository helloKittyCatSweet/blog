<script setup>
import { ref, onMounted, watch } from "vue";
import { ElMessage } from "element-plus";
import { findByPostId } from "@/api/post/postVersion.js";

const props = defineProps({
  modelValue: {
    type: Object,
    default: () => ({}),
  },
  postId: {
    type: Number,
    required: true,
  },
});

const emit = defineEmits(["update:modelValue"]);

const versions = ref([]);
const selectedVersion = ref(null);

// 监听 modelValue 变化
watch(
  () => props.modelValue,
  (newVal) => {
    selectedVersion.value = newVal?.versionId;
  },
  { immediate: true }
);

// 获取文章所有版本
const getAllVersions = async () => {
  try {
    const response = await findByPostId(props.postId);
    if (response.data.status === 200) {
      versions.value = response.data.data;
    }
  } catch (error) {
    ElMessage.error("获取版本列表失败");
  }
};

// 处理版本变化
const handleVersionChange = (val) => {
  const selectedVersionObject = versions.value.find(
    (version) => version.versionId === val
  );
  emit("update:modelValue", selectedVersionObject);
};

onMounted(() => {
  getAllVersions();
});
</script>

<template>
  <div class="version-select-container">
    <el-select
      v-model="selectedVersion"
      placeholder="请选择版本"
      style="width: 100%"
      @change="handleVersionChange"
    >
      <el-option
        v-for="item in versions"
        :key="item.versionId"
        :label="`版本 ${item.version} (${item.versionAt})`"
        :value="item.versionId"
      >
        <span>版本 {{ item.version }}</span>
        <span style="color: var(--el-text-color-secondary); font-size: 13px">
          ({{ item.versionAt }})
        </span>
      </el-option>
    </el-select>
  </div>
</template>

<style scoped>
.version-select-container {
  width: 100%;
}
</style>
