<script setup>
import { ref, onMounted } from "vue";
import { ElMessage, ElMessageBox } from "element-plus";
import { useRoute, useRouter } from "vue-router";
import { View, RefreshRight } from "@element-plus/icons-vue";
import PageContainer from "@/components/PageContainer.vue";
import { formatDateTime } from "@/utils/format";
import MdEditor from "md-editor-v3";
import "md-editor-v3/lib/style.css";

const route = useRoute();
const router = useRouter();
const loading = ref(false);
const postId = ref(route.params.id);

// 版本列表数据
const versionList = ref([]);
const currentVersion = ref(null);

// 预览对话框
const previewVisible = ref(false);
const previewContent = ref("");

// 获取版本列表
const getVersionList = async () => {
  loading.value = true;
  try {
    // TODO: 调用获取版本列表API
    loading.value = false;
  } catch (error) {
    ElMessage.error("获取版本列表失败");
    loading.value = false;
  }
};

// 预览版本内容
const handlePreview = (row) => {
  previewContent.value = row.content;
  previewVisible.value = true;
};

// 恢复到指定版本
const handleRestore = (row) => {
  ElMessageBox.confirm("确认恢复到该版本吗？", "警告", {
    confirmButtonText: "确定",
    cancelButtonText: "取消",
    type: "warning",
  }).then(async () => {
    try {
      // TODO: 调用恢复版本API
      ElMessage.success("恢复成功");
      getVersionList();
    } catch (error) {
      ElMessage.error("恢复失败");
    }
  });
};

onMounted(() => {
  if (postId.value) {
    getVersionList();
  } else {
    ElMessage.error("缺少文章ID参数");
    router.push("/user/post/list");
  }
});
</script>

<template>
  <page-container title="文章版本管理">
    <el-card v-loading="loading">
      <template #header>
        <div class="card-header">
          <span>版本历史</span>
          <el-button :icon="RefreshRight" circle @click="getVersionList" title="刷新" />
        </div>
      </template>

      <el-timeline>
        <el-timeline-item
          v-for="item in versionList"
          :key="item.versionId"
          :timestamp="formatDateTime(item.versionAt)"
          placement="top"
        >
          <el-card>
            <template #header>
              <div class="version-header">
                <span>版本 {{ item.version }}</span>
                <div class="version-actions">
                  <el-button
                    type="primary"
                    :icon="View"
                    @click="handlePreview(item)"
                    text
                  >
                    预览
                  </el-button>
                  <el-button
                    v-if="item.version !== currentVersion"
                    type="warning"
                    @click="handleRestore(item)"
                    text
                  >
                    恢复到此版本
                  </el-button>
                </div>
              </div>
            </template>
            <div class="version-info">
              <p>修改人：{{ item.user?.username || "未知用户" }}</p>
              <p>修改时间：{{ formatDateTime(item.versionAt) }}</p>
            </div>
          </el-card>
        </el-timeline-item>
      </el-timeline>

      <!-- 预览对话框 -->
      <el-dialog
        v-model="previewVisible"
        title="版本预览"
        width="80%"
        :destroy-on-close="true"
      >
        <md-editor v-model="previewContent" preview-only style="height: 500px" />
      </el-dialog>
    </el-card>
  </page-container>
</template>

<style scoped>
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.version-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.version-actions {
  display: flex;
  gap: 10px;
}

.version-info {
  color: var(--el-text-color-secondary);
  font-size: 14px;
}

.version-info p {
  margin: 5px 0;
}
</style>
