<script setup>
import { ref } from "vue";
import { ElMessage } from "element-plus";
import { create } from "@/api/post/report.js";
import { useUserStore } from "@/stores/modules/user.js";
import { debounce } from "lodash-es";

const props = defineProps({
  postId: {
    type: Number,
    required: true,
  },
  title: {
    type: String,
    required: true,
  },
});

const dialogVisible = ref(false);
const loading = ref(false);

const reportForm = ref({
  reason: "",
  description: "",
});

const rules = {
  reason: { required: true, message: "请选择举报原因", trigger: "change" },
  description: { required: true, message: "请填写举报说明", trigger: "blur" },
};

const reasonOptions = [
  { value: "SPAM", label: "垃圾广告" },
  { value: "INAPPROPRIATE", label: "不当内容" },
  { value: "PLAGIARISM", label: "抄袭内容" },
  { value: "ILLEGAL", label: "违法内容" },
  { value: "OTHER", label: "其他原因" },
];

const formRef = ref();
const userStore = useUserStore();

const handleSubmit = debounce(async () => {
  if (!formRef.value) return;

  await formRef.value.validate(async (valid) => {
    if (valid) {
      loading.value = true;
      try {
        const { data } = await create({
          postId: props.postId,
          userId: userStore.user.id,
          reason: reportForm.value.reason,
          description: reportForm.value.description,
        });

        if (data.status === 200) {
          ElMessage.success("举报提交成功");
          dialogVisible.value = false;
          reportForm.value = { reason: "", description: "" };
        }
      } catch (error) {
        ElMessage.error("举报提交失败");
      } finally {
        loading.value = false;
      }
    }
  });
}, 500);
</script>

<template>
  <div class="post-report">
    <el-button type="danger" link @click="dialogVisible = true">
      <el-icon><Warning /></el-icon>
      举报
    </el-button>

    <el-dialog
      v-model="dialogVisible"
      title="举报文章"
      width="500px"
      :close-on-click-modal="false"
    >
      <el-form
        ref="formRef"
        :model="reportForm"
        :rules="rules"
        label-width="80px"
        v-loading="loading"
      >
        <div class="report-target">举报文章：{{ title }}</div>

        <el-form-item label="举报原因" prop="reason">
          <el-select
            v-model="reportForm.reason"
            placeholder="请选择举报原因"
            style="width: 100%"
          >
            <el-option
              v-for="item in reasonOptions"
              :key="item.value"
              :label="item.label"
              :value="item.value"
            />
          </el-select>
        </el-form-item>

        <el-form-item label="详细说明" prop="description">
          <el-input
            v-model="reportForm.description"
            type="textarea"
            :rows="4"
            placeholder="请详细说明举报原因..."
          />
        </el-form-item>
      </el-form>

      <template #footer>
        <span class="dialog-footer">
          <el-button @click="dialogVisible = false">取消</el-button>
          <el-button type="primary" @click="handleSubmit">提交举报</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<style scoped>
.post-report {
  display: inline-block;
}

.report-target {
  margin-bottom: 20px;
  padding: 10px;
  background-color: var(--el-fill-color-lighter);
  border-radius: 4px;
  color: var(--el-text-color-regular);
  font-size: 14px;
}
</style>
