<script setup>
import { ref, onMounted, watch, computed } from "vue";
import { ElMessage } from "element-plus";
import { findByPostId, activateVersion } from "@/api/post/postVersion";
import { addVersion, deleteVersion } from "@/api/post/post.js";
import { useUserStore } from "@/stores/modules/user.js";

const props = defineProps({
  postId: {
    type: [String, Number],
    required: true,
  },
  currentVersion: {
    type: Number,
    required: true,
  },
  modelValue: {
    type: Object,
    default: () => null,
  },
  currentContent: {
    type: String,
    default: "",
  },
  activeVersion: {
    type: Number,
    required: false,
  },
});

const emit = defineEmits(["update:modelValue"]);

// 添加本地响应式变量
const selectedVersion = ref(null);

const versions = ref([]);
const loading = ref(false);

// 获取版本列表
const loadVersions = async () => {
  loading.value = true;
  try {
    const response = await findByPostId(props.postId);
    if (response.data.status === 200) {
      versions.value = response.data.data.sort((a, b) => b.version - a.version);

      // 找到当前激活版本的数据并设置为选中值
      const currentVersionData = versions.value.find(
        (v) => v.version === props.currentVersion
      );
      if (currentVersionData) {
        selectedVersion.value = currentVersionData;
        emit("update:modelValue", currentVersionData);
      }
    }
  } catch (error) {
    ElMessage.error("获取版本列表失败");
  } finally {
    loading.value = false;
  }
};

const userStore = useUserStore();

// 版本数量的计算属性
const versionCount = computed(() => versions.value.length);

// 创建新版本
const handleCreateVersion = async (command) => {
  try {
    if (versionCount.value >= 5) {
      ElMessage.warning("版本数量已达上限（5个），请先删除不需要的版本");
      return;
    }

    const response = await addVersion({
      postId: props.postId,
      content: command === "keep" ? props.currentContent : " ",
      userId: userStore.user.id,
    });
    if (response.data.status === 200) {
      ElMessage.success("创建新版本成功");
      await loadVersions();
      // 自动选中新版本
      const newVersion = response.data.data;
      emit("update:modelValue", newVersion);
    }
  } catch (error) {
    ElMessage.error("创建新版本失败");
  }
};

// 监听 activeVersion 的变化
watch(
  () => props.activeVersion,
  (newVal) => {
    if (newVal) {
      postVersion.value = newVal;
    }
  }
);

const postVersion = ref(props.currentVersion); // 文章真正的版本

// 设置当前版本为活动版本
const handleSetActive = async (version) => {
  try {
    const response = await activateVersion(props.postId, version.version);
    if (response.data.status === 200) {
      ElMessage.success("设置活动版本成功");
      // 更新当前激活版本号
      postVersion.value = version.version;
      await loadVersions();
      // 更新选中的版本
      selectedVersion.value = version;
      emit("update:modelValue", { ...version, isActivating: true });
    }
  } catch (error) {
    ElMessage.error("设置活动版本失败");
  }
};

// 版本变更处理
const handleVersionChange = (version) => {
  console.log("选中的版本对象:", version);

  if (version) {
    console.log("找到匹配的版本");
    selectedVersion.value = version;
    emit("update:modelValue", version);
  } else {
    console.log("未找到匹配的版本");
  }
};

// 删除版本
const handleDeleteVersion = async (version) => {
  if (version.version === postVersion) {
    ElMessage.warning("不能删除当前激活的版本");
    return;
  }

  if (versionCount.value <= 1) {
    ElMessage.warning("文章至少保留一个版本，如要删除文章请至文章管理界面");
    return;
  }

  try {
    const response = await deleteVersion({
      postId: props.postId,
      versionId: version.versionId,
    });
    if (response.data.status === 200) {
      ElMessage.success("删除版本成功");
      await loadVersions();
      if (props.modelValue?.version === version.version) {
        emit("update:modelValue", null);
      }
    }
  } catch (error) {
    ElMessage.error("删除版本失败");
  }
};

onMounted(() => {
  loadVersions();
});

// 监听modelValue变化
watch(
  () => props.modelValue,
  (newVal) => {
    if (newVal) {
      selectedVersion.value = newVal;
    }
  },
  { immediate: true }
);
</script>

<template>
  <div class="version-select">
    <!-- 显示当前激活的版本 -->
    <div class="active-version">激活版本：{{ postVersion }}</div>

    <!-- 版本选择下拉框 -->
    <el-select
      v-model="selectedVersion"
      placeholder="选择要编辑的版本"
      :loading="loading"
      @change="handleVersionChange"
      style="width: 200px"
      value-key="versionId"
    >
      <el-option
        v-for="version in versions"
        :key="version.versionId"
        :label="`版本 ${version.version}${
          version.version === postVersion ? ' (已激活)' : ''
        }`"
        :value="version"
      >
        <div class="version-option">
          <span>版本 {{ version.version }}</span>
          <div class="version-actions">
            <el-tag v-if="version.version === postVersion" size="small" type="success"
              >已激活</el-tag
            >
            <el-tag v-else size="small" type="info">未激活</el-tag>
            <el-button
              v-if="version.version !== postVersion"
              type="danger"
              size="small"
              link
              @click.stop="handleDeleteVersion(version)"
            >
              删除
            </el-button>
          </div>
        </div>
      </el-option>
    </el-select>

    <el-dropdown trigger="click" @command="handleCreateVersion">
      <el-button type="primary" class="ml-2">
        新建版本
        <el-icon class="el-icon--right"><arrow-down /></el-icon>
      </el-button>
      <template #dropdown>
        <el-dropdown-menu>
          <el-dropdown-item :command="'keep'">保留当前内容</el-dropdown-item>
          <el-dropdown-item :command="'clear'">清空内容</el-dropdown-item>
        </el-dropdown-menu>
      </template>
    </el-dropdown>

    <!-- 激活按钮 -->
    <el-button
      v-if="selectedVersion && selectedVersion.version !== postVersion"
      class="ml-2"
      type="primary"
      @click="handleSetActive(selectedVersion)"
    >
      激活此版本
    </el-button>
  </div>
</template>

<style scoped>
.version-select {
  display: flex;
  align-items: center;
  gap: 8px;
}

.current-version {
  color: var(--el-text-color-secondary);
  margin-right: 8px;
}

.version-option {
  display: flex;
  align-items: center;
  justify-content: space-between;
  width: 100%;
}

.version-actions {
  display: flex;
  align-items: center;
  gap: 8px;
}

.ml-2 {
  margin-left: 8px;
}
</style>
