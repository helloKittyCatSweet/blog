<script setup>
import { Pointer, Document, Edit, Download } from "@element-plus/icons-vue";
import { ElMessage } from "element-plus";
import { exportToPDF, exportToMarkdown } from "@/api/post/export.js";

const props = defineProps({
  post: {
    type: Object,
    required: true,
  },
  author: {
    type: String,
    required: true,
  },
});

const handleExport = async (type) => {
  try {
    if (type === "pdf") {
      const response = await exportToPDF(props.post.postId);
      // 创建 Blob 对象并下载
      const blob = new Blob([response.data], { type: "application/pdf" });
      const url = window.URL.createObjectURL(blob);
      const link = document.createElement("a");
      link.href = url;
      link.download = `${props.post.title}.pdf`;
      link.click();
      window.URL.revokeObjectURL(url);
      ElMessage.success("PDF导出成功");
    } else if (type === "md") {
      const response = await exportToMarkdown(props.post.postId);
      // 创建 Blob 对象并下载
      const blob = new Blob([response.data], { type: "text/markdown;charset=utf-8" });
      const url = window.URL.createObjectURL(blob);
      const link = document.createElement("a");
      link.href = url;
      link.download = `${props.post.title}.md`;
      link.click();
      window.URL.revokeObjectURL(url);
      ElMessage.success("Markdown导出成功");
    }
  } catch (error) {
    console.error("导出失败:", error);
    ElMessage.error("导出失败");
  }
};
</script>

<template>
  <div class="export-actions">
    <el-dropdown trigger="click" @command="handleExport">
      <el-button type="primary">
        <el-icon><Download /></el-icon>
        导出文章
      </el-button>
      <template #dropdown>
        <el-dropdown-menu>
          <el-dropdown-item command="pdf"
            ><el-icon><Document /></el-icon>导出为PDF</el-dropdown-item
          >
          <el-dropdown-item command="md"
            ><el-icon><Edit /></el-icon>导出为Markdown</el-dropdown-item
          >
        </el-dropdown-menu>
      </template>
    </el-dropdown>
  </div>
</template>

<style scoped>
.export-actions {
  margin: 16px 0;
  text-align: right;
}
</style>
