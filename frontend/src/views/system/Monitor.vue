<template>
  <div class="monitor">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>系统监控</span>
          <el-button @click="refreshData">刷新</el-button>
        </div>
      </template>

      <el-row :gutter="20">
        <el-col :span="6">
          <el-card>
            <h3>系统状态</h3>
            <div class="status-item">
              <span>健康状态：</span>
              <el-tag :type="systemStatus.health?.status === 'UP' ? 'success' : 'danger'">
                {{ systemStatus.health?.status }}
              </el-tag>
            </div>
          </el-card>
        </el-col>

        <el-col :span="6">
          <el-card>
            <h3>内存使用</h3>
            <el-progress
              :percentage="
                (
                  (systemStatus.memory?.measurements[0]?.value / 1024 / 1024 / 1024) *
                  100
                ).toFixed(2)
              "
              :format="format"
            />
          </el-card>
        </el-col>

        <el-col :span="6">
          <el-card>
            <h3>线程数</h3>
            <div class="status-item">
              {{ systemStatus.threads?.measurements[0]?.value }}
            </div>
          </el-card>
        </el-col>

        <el-col :span="6">
          <el-card>
            <h3>磁盘空间</h3>
            <div class="status-item">{{ systemStatus.diskSpace }}</div>
          </el-card>
        </el-col>
      </el-row>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from "vue";
import { getSystemStatus } from "@/api/monitor";

const systemStatus = ref({});

const refreshData = async () => {
  try {
    const { data } = await getSystemStatus();
    systemStatus.value = data;
  } catch (error) {
    console.error("获取系统状态失败:", error);
  }
};

const format = (percentage) => {
  return percentage + "GB";
};

onMounted(() => {
  refreshData();
  // 每60秒自动刷新一次
  setInterval(refreshData, 60000);
});
</script>

<style scoped>
.monitor {
  padding: 20px;
}
.status-item {
  margin: 10px 0;
  font-size: 16px;
}
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
</style>
