<script setup>
import { ref, watch, computed, defineEmits } from "vue";
import { ElMessage } from "element-plus";
import { MdEditor } from "md-editor-v3";
import "md-editor-v3/lib/style.css";
import { findByPostId, activateVersion } from "@/api/post/postVersion";
import VersionSelect from "./VersionSelect.vue";
import { diff_match_patch } from "diff-match-patch";

const props = defineProps({
  postData: {
    type: Object,
    required: true,
  },
});

const versionLoading = ref(false);
const selectedVersion = ref(null);
const versionList = ref([]);
const activeTab = ref("preview");
const compareVersion = ref(null);

// 加载版本列表
const loadVersions = async () => {
  versionLoading.value = true;
  try {
    const response = await findByPostId(props.postData.postId);
    if (response.data.status === 200) {
      versionList.value = response.data.data;
      // 找到当前版本
      const currentVersion = versionList.value.find(
        (v) => v.version === props.postData.version
      );
      if (currentVersion) {
        selectedVersion.value = currentVersion;
      }
    }
  } catch (error) {
    ElMessage.error("获取版本列表失败");
  } finally {
    versionLoading.value = false;
  }
};

watch(
  () => props.postData,
  () => {
    if (props.postData) {
      loadVersions();
    }
  },
  { immediate: true }
);

// 添加预览功能
const handlePreview = (version) => {
  selectedVersion.value = version;
  activeTab.value = "preview";
};

const isCurrentVersion = computed(() => {
  return selectedVersion.value?.version === props.postData.version;
});

const emit = defineEmits(["version-changed"]);
const activeVersion = ref(props.postData?.version);

// 修改版本切换函数
const handleRestore = async (version) => {
  try {
    const response = await activateVersion(props.postData.postId, version.version);
    if (response.data.status === 200) {
      ElMessage.success("版本切换成功");
      // 更新当前文章的版本和内容
      props.postData.version = version.version;
      props.postData.content = version.content;
      // 更新激活版本
      activeVersion.value = version.version;
      // 更新选中的版本
      selectedVersion.value = version;
      // 重新加载版本列表
      await loadVersions();
    } else {
      ElMessage.error(`版本切换失败: ${response.data.message || "未知错误"}`);
    }
  } catch (error) {
    console.error("版本切换错误:", error);
    ElMessage.error(
      `版本切换失败: ${error.response?.data?.message || error.message || "未知错误"}`
    );
  }
};

/**
 * 版本差异比较
 */
const baseVersion = ref(null);
const targetVersion = ref(null);

const versionOptions = computed(() => {
  return versionList.value.map((version) => ({
    value: version.version, // 修改为只使用版本号作为值
    label: `版本 ${version.version}`,
    description: version.versionAt.slice(0, 10),
    version: version, // 保存完整的版本对象
  }));
});

// 修改 diffResult 计算属性
const diffResult = computed(() => {
  if (!baseVersion.value || !targetVersion.value) return "";

  const dmp = new diff_match_patch();
  // 修改对比方向：从目标版本到基准版本
  const diff = dmp.diff_main(targetVersion.value.content, baseVersion.value.content);
  dmp.diff_cleanupSemantic(diff);

  // 修改版本对比说明的顺序
  const versionInfo = `基准版本 ${baseVersion.value.version} 与版本 ${targetVersion.value.version} 的对比：\n`;
  const diffText = diff
    .map(([type, text]) => {
      const escapedText = text.replace(
        /[<>&]/g,
        (c) =>
          ({
            "<": "&lt;",
            ">": "&gt;",
            "&": "&amp;",
          }[c])
      );

      switch (type) {
        case -1: // 修改：交换插入和删除的显示
          return `<span class="diff-add">${escapedText}</span>`;
        case 1: // 修改：交换插入和删除的显示
          return `<span class="diff-del">${escapedText}</span>`;
        default:
          return escapedText;
      }
    })
    .join("");

  return versionInfo + diffText;
});

// 修改版本切换到对比 tab 时的初始化逻辑
watch(
  () => activeTab.value,
  async (newTab) => {
    if (props.postData && newTab === "compare") {
      // 清空之前的选择
      baseVersion.value = null;
      targetVersion.value = null;

      await loadVersions();

      // 设置默认的目标版本为当前版本
      const currentVersion = versionList.value.find(
        (v) => v.version === props.postData.version
      );
      if (currentVersion) {
        targetVersion.value = currentVersion;
      }

      // 设置默认的基准版本为最新版本（非当前版本）
      if (versionList.value.length > 1) {
        const latestVersion = versionList.value
          .filter((v) => v.version !== props.postData.version)
          .sort((a, b) => new Date(b.versionAt) - new Date(a.versionAt))[0];
        if (latestVersion) {
          baseVersion.value = latestVersion;
        }
      }
    }
  }
);

// 过滤目标版本选项，排除已选的基准版本
const filteredTargetVersions = computed(() => {
  if (!baseVersion.value) return versionOptions.value;
  return versionOptions.value.filter((option) => option.value !== baseVersion.value);
});

const selectedBaseVersion = computed({
  get: () => baseVersion.value?.version,
  set: (value) => {
    baseVersion.value = versionList.value.find((v) => v.version === value) || null;
  },
});

const selectedTargetVersion = computed({
  get: () => targetVersion.value?.version,
  set: (value) => {
    targetVersion.value = versionList.value.find((v) => v.version === value) || null;
  },
});
</script>

<template>
  <div v-loading="versionLoading">
    <el-tabs v-model="activeTab">
      <!-- 版本预览 tab -->
      <el-tab-pane label="版本预览" name="preview">
        <div class="version-preview-header">
          <div class="version-info">
            <span class="version-title">{{ props.postData?.title }}</span>
          </div>
          <version-select
            v-if="props.postData"
            v-model="selectedVersion"
            :post-id="props.postData.postId"
            :current-version="props.postData.version"
            :current-content="selectedVersion?.content || ''"
            :active-version="activeVersion"
          />
        </div>

        <div class="version-meta-info" v-if="selectedVersion">
          <el-descriptions :column="3" border>
            <el-descriptions-item label="创建时间">
              {{ selectedVersion.versionAt }}
            </el-descriptions-item>
            <el-descriptions-item label="内容长度">
              {{ selectedVersion.content?.length || 0 }} 字符
            </el-descriptions-item>
          </el-descriptions>
        </div>

        <md-editor
          v-if="selectedVersion"
          v-model="selectedVersion.content"
          preview-only
          style="height: 500px; margin-top: 16px"
        />
      </el-tab-pane>

      <!-- 添加版本历史 tab -->
      <el-tab-pane label="版本历史" name="history">
        <el-timeline>
          <el-timeline-item
            v-for="version in versionList"
            :key="version.versionId"
            :timestamp="version.versionAt"
            :type="version.version === props.postData.version ? 'success' : 'info'"
          >
            <el-card>
              <template #header>
                <div class="version-header">
                  <span>版本 {{ version.version }}</span>
                  <div class="version-actions">
                    <el-tag
                      v-if="version.version === props.postData.version"
                      type="success"
                      >当前版本</el-tag
                    >
                    <el-tag v-else type="info">历史版本</el-tag>
                    <el-button type="primary" text @click="handlePreview(version)">
                      预览内容
                    </el-button>
                    <el-button
                      v-if="version.version !== props.postData.version"
                      type="warning"
                      text
                      @click="handleRestore(version)"
                      :disabled="!isCurrentVersion"
                    >
                      切换到此版本
                    </el-button>
                  </div>
                </div>
              </template>
              <div class="version-info">
                <p>版本号：{{ version.version }}</p>
                <p>修改时间：{{ version.versionAt }}</p>
                <p>内容长度：{{ version.content?.length || 0 }} 字符</p>
              </div>
            </el-card>
          </el-timeline-item>
        </el-timeline>
      </el-tab-pane>

      <!-- 添加版本对比 tab -->
      <el-tab-pane label="版本对比" name="compare">
        <div class="version-compare">
          <div class="compare-header">
            <div class="version-selectors">
              <el-select
                v-model="selectedBaseVersion"
                placeholder="选择基准版本"
                style="width: 250px; margin-right: 16px"
                filterable
                clearable
              >
                <el-option
                  v-for="option in versionOptions"
                  :key="option.value"
                  :label="option.label"
                  :value="option.value"
                >
                  <div class="flex justify-between items-center">
                    <span>{{ option.label }}</span>
                    <span class="text-gray-400 text-sm">{{ option.description }}</span>
                  </div>
                </el-option>
              </el-select>

              <el-select
                v-model="selectedTargetVersion"
                placeholder="选择目标版本"
                style="width: 250px"
                filterable
                clearable
                :disabled="!baseVersion"
              >
                <el-option
                  v-for="option in filteredTargetVersions"
                  :key="option.value"
                  :label="option.label"
                  :value="option.value"
                >
                  <div class="flex justify-between items-center">
                    <span>{{ option.label }}</span>
                    <span class="text-gray-400 text-sm">{{ option.description }}</span>
                  </div>
                </el-option>
              </el-select>
            </div>

            <el-alert
              v-if="
                baseVersion &&
                targetVersion &&
                baseVersion.version === targetVersion.version
              "
              title="请选择两个不同的版本进行比较"
              type="warning"
              show-icon
              style="margin-top: 10px"
            />
          </div>

          <div
            class="diff-container"
            v-if="
              baseVersion &&
              targetVersion &&
              baseVersion.version !== targetVersion.version
            "
          >
            <div class="diff-header">
              <span>版本对比说明：</span>
              <ul class="diff-legend">
                <li><span class="diff-add-dot"></span> 绿色表示在目标版本中新增的内容</li>
                <li><span class="diff-del-dot"></span> 红色表示在目标版本中删除的内容</li>
              </ul>
            </div>
            <pre class="diff-content" v-html="diffResult"></pre>
          </div>

          <div v-else-if="baseVersion && targetVersion" class="empty-diff">
            <el-empty description="两个版本内容相同，没有差异" />
          </div>

          <div v-else class="empty-diff">
            <el-empty description="请选择基准版本和目标版本进行比较" />
          </div>
        </div>
      </el-tab-pane>
    </el-tabs>
  </div>
</template>

<style scoped>
.version-preview-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
}

.version-info p {
  margin: 8px 0;
  color: var(--el-text-color-secondary);
  font-size: 14px;
  line-height: 1.5;
}

.version-actions {
  display: flex;
  gap: 12px;
  align-items: center;
}

.version-title {
  font-size: 16px;
  font-weight: bold;
}

.version-meta-info {
  margin: 16px 0;
}

.version-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.version-actions {
  display: flex;
  gap: 8px;
  align-items: center;
}

.version-compare {
  padding: 16px 0;
}

.compare-header {
  margin-bottom: 16px;
}

.compare-content {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 16px;
}

.compare-panel {
  border: 1px solid var(--el-border-color);
  border-radius: 4px;
  padding: 16px;
}

.compare-panel h3 {
  margin-top: 0;
  margin-bottom: 16px;
  font-size: 16px;
  color: var(--el-text-color-primary);
}

.version-selectors {
  display: flex;
  align-items: center;
  margin-bottom: 16px;
}

.diff-container {
  border: 1px solid var(--el-border-color);
  border-radius: 4px;
  padding: 16px;
  margin-top: 16px;
  background-color: #fff;
}

.diff-content {
  padding: 16px;
  margin: 0;
  background-color: #f8f9fa;
  border-radius: 4px;
  font-size: 14px;
}

.diff-header {
  margin-bottom: 16px;
  font-weight: bold;
}

.diff-content {
  white-space: pre-wrap;
  word-wrap: break-word;
  font-family: monospace;
  line-height: 1.5;
}

.diff-content :deep(.diff-add) {
  background-color: #e6ffe6;
  color: #0a0;
}

.diff-content :deep(.diff-del) {
  background-color: #ffe6e6;
  color: #a00;
  text-decoration: line-through;
}

.diff-legend {
  list-style: none;
  padding: 0;
  margin: 8px 0;
}

.diff-legend li {
  display: flex;
  align-items: center;
  margin: 4px 0;
}

.diff-add-dot,
.diff-del-dot {
  display: inline-block;
  width: 12px;
  height: 12px;
  border-radius: 50%;
  margin-right: 8px;
}

.diff-add-dot {
  background-color: #0a0;
}

.diff-del-dot {
  background-color: #a00;
}

/* 添加空状态样式 */
.empty-diff {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 300px;
  border: 1px dashed var(--el-border-color);
  border-radius: 4px;
  margin-top: 16px;
}

/* 优化选项显示 */
.el-select-dropdown__item {
  display: flex;
  justify-content: space-between;
}

.flex {
  display: flex;
}

.justify-between {
  justify-content: space-between;
}

.items-center {
  align-items: center;
}

.text-gray-400 {
  color: #9ca3af;
}

.text-sm {
  font-size: 0.875rem;
}
</style>
