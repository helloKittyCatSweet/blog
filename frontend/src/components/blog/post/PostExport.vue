<script setup>
import { Pointer, Document, Edit, Download } from "@element-plus/icons-vue";
import { jsPDF } from "jspdf";
// 导入中文字体
import { font } from "@/assets/fonts/NotoSansSC-normal.js";
import { ElMessage } from "element-plus";
import { getSignature } from "@/api/user/user.js";

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
      // 创建 PDF 实例，设置较小的边距
      const doc = new jsPDF({
        orientation: "portrait",
        unit: "mm",
        format: "a4",
        margins: { top: 20, bottom: 20, left: 20, right: 20 },
      });

      // 添加中文字体支持
      doc.addFileToVFS("NotoSansSC-normal.ttf", font);
      doc.addFont("NotoSansSC-normal.ttf", "NotoSansSC", "normal");
      doc.setFont("NotoSansSC");

      // 获取用户签名
      let signature = "";
      try {
        const response = await getSignature(props.post.userId);
        if (response.data.status === 200) {
          signature = response.data.data;
          // 加载签名图片
          const img = new Image();
          img.crossOrigin = "anonymous"; // 改为小写
          // 设置请求头
          const xhr = new XMLHttpRequest();
          xhr.open("GET", signature, true);
          xhr.responseType = "blob";
          xhr.setRequestHeader("accept", "image/png");
          xhr.setRequestHeader("content-type", "image/png");
          xhr.setRequestHeader("x-requested-with", "XMLHttpRequest");

          await new Promise((resolve, reject) => {
            xhr.onload = function () {
              if (xhr.status === 200) {
                const url = URL.createObjectURL(xhr.response);
                img.onload = () => {
                  const canvas = document.createElement("canvas");
                  canvas.width = img.width;
                  canvas.height = img.height;
                  const ctx = canvas.getContext("2d");
                  ctx.drawImage(img, 0, 0);
                  const imgData = canvas.toDataURL("image/png");
                  URL.revokeObjectURL(url);

                  // 在每一页添加签名图片
                  const pageCount = doc.internal.getNumberOfPages();
                  for (let i = 1; i <= pageCount; i++) {
                    doc.setPage(i);
                    const imgWidth = 40;
                    const imgHeight = (img.height * imgWidth) / img.width;
                    doc.addImage(
                      imgData,
                      "PNG",
                      20,
                      doc.internal.pageSize.height - 35,
                      imgWidth,
                      imgHeight
                    );
                  }
                  resolve();
                };
                img.src = url;
              } else {
                reject(new Error("Failed to load image"));
              }
            };
            xhr.onerror = reject;
            xhr.send();
          });
        }
      } catch (error) {
        console.error("获取签名失败:", error);
      }

      // 添加页眉
      doc.setFillColor(245, 245, 245);
      doc.rect(0, 0, 210, 15, "F");
      doc.setFontSize(10);
      doc.setTextColor(100, 100, 100);
      doc.text("FreeShare", 20, 10);
      doc.text(new Date().toLocaleDateString(), 170, 10);

      // 添加文章标题
      doc.setFontSize(24);
      doc.setTextColor(0, 0, 0);
      doc.text(props.post.title, 20, 35, { maxWidth: 170 });

      // 添加元信息区域
      doc.setFillColor(250, 250, 250);
      doc.rect(20, 45, 170, 15, "F");
      doc.setFontSize(10);
      doc.setTextColor(100, 100, 100);
      doc.text(`作者: ${props.author}`, 25, 53);
      doc.text(`发布时间: ${props.post.createdTime}`, 85, 53);
      doc.text(`来源: FreeShare`, 145, 53);

      // 添加分隔线
      doc.setDrawColor(200, 200, 200);
      doc.line(20, 65, 190, 65);

      // 添加文章内容
      doc.setFontSize(12);
      doc.setTextColor(50, 50, 50);
      const splitText = doc.splitTextToSize(props.post.content, 170);
      let y = 75;

      for (let i = 0; i < splitText.length; i++) {
        if (y > 270) {
          doc.addPage();
          // 在新页面也添加页眉
          doc.setFillColor(245, 245, 245);
          doc.rect(0, 0, 210, 15, "F");
          doc.setFontSize(10);
          doc.setTextColor(100, 100, 100);
          doc.text("FreeShare", 20, 10);
          doc.text(new Date().toLocaleDateString(), 170, 10);
          y = 30;
        }
        doc.text(splitText[i], 20, y);
        y += 7; // 增加行间距
      }

      // 添加页脚
      const pageCount = doc.internal.getNumberOfPages();
      for (let i = 1; i <= pageCount; i++) {
        doc.setPage(i);
        // 添加页脚背景
        doc.setFillColor(245, 245, 245);
        doc.rect(0, doc.internal.pageSize.height - 15, 210, 15, "F");
        // 添加页码和版权信息
        doc.setFontSize(8);
        doc.setTextColor(100, 100, 100);
        doc.text(`第 ${i} 页，共 ${pageCount} 页`, 20, doc.internal.pageSize.height - 7);
        doc.text(
          "© FreeShare. All rights reserved.",
          150,
          doc.internal.pageSize.height - 7
        );
      }

      // 保存 PDF
      doc.save(`${props.post.title}.pdf`);
      ElMessage.success("PDF导出成功");
    } else if (type === "md") {
      let signature = "";
      try {
        const response = await getSignature(props.post.userId);
        if (response.data.status === 200) {
          signature = response.data.data;
        }
      } catch (error) {
        console.error("获取签名失败:", error);
      }

      const watermark = `\n\n---\n\n> 作者: ${props.author}\n> 来源: FreeShare\n> 链接: ${
        window.location.href
      }\n> 发布时间: ${
        props.post.createdTime
      }\n> 版权声明: 本文遵循 CC BY-NC-SA 4.0 协议，转载请注明出处。${
        signature ? `\n\n![签名](${signature})` : ""
      }`;

      const markdown = generateMarkdownContent(watermark);
      downloadFile(markdown, `${props.post.title}.md`, "text/markdown;charset=utf-8");
      ElMessage.success("Markdown导出成功");
    }
  } catch (error) {
    console.error("导出失败:", error);
    ElMessage.error("导出失败");
  }
};

const generateMarkdownContent = (watermark) => {
  return `# ${props.post.title}\n\n` + `${props.post.content}\n\n` + watermark;
};

// 添加下载文件的函数
const downloadFile = (content, fileName, contentType) => {
  const blob = new Blob([content], { type: contentType });
  const url = window.URL.createObjectURL(blob);
  const link = document.createElement("a");
  link.href = url;
  link.download = fileName;
  link.click();
  window.URL.revokeObjectURL(url);
};
</script>

<template>
  <div class="export-actions">
    <el-dropdown trigger="click" @command="handleExport">
      <el-button type="primary" plain>
        <el-icon><Pointer /></el-icon>
        <span>导出文章</span>
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
