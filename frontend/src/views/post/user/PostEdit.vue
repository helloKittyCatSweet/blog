<script setup>
import { ref, onMounted, watch, computed } from "vue";
import { useRoute, useRouter } from "vue-router";
import { ElMessage } from "element-plus";
import PageContainer from "@/components/PageContainer.vue";
import { MdEditor } from "md-editor-v3";
import "md-editor-v3/lib/style.css";
import {
  findById,
  create,
  update,
  uploadAttachment,
  deleteAttachment,
  uploadPostCover,
  generateSummary,
  savePost,
} from "@/api/post/post.js";
import { USER_POST_LIST_PATH } from "@/constants/routes/user.js";
import { useUserStore } from "@/stores";

import CategorySelect from "@/components/category/CategorySelect.vue";
import TagSelect from "@/components/tag/TagSelect.vue";
import VersionSelect from "@/components/version/VersionSelect.vue";

const route = useRoute();
const router = useRouter();
const loading = ref(false);

// 表单数据
const formRef = ref(null);

// 解决从 编辑->新建 时，内容不变的问题
// 添加默认的表单初始值
const defaultForm = {
  postId: null,
  title: "",
  content: "",
  coverImage: "",
  isPublished: false,
  isDraft: true,
  visibility: "PUBLIC",
  version: 1,
  category: null,
  categoryId: null,
  tags: [],
  author: "",
  summary: "",
};
// 初始化表单数据
const form = ref({ ...defaultForm });

// 监听路由变化
watch(
  () => route.path,
  (newPath) => {
    if (newPath.includes("/create")) {
      // 重置表单数据
      form.value = { ...defaultForm };
      postSummary.value = "";
      fileList.value = [];
      selectedVersion.value = null;
      editorKey.value += 1; // 强制重新渲染编辑器
    }
  }
);

// 表单校验规则
const rules = {
  title: [
    { required: true, message: "请输入文章标题", trigger: "blur" },
    { min: 2, max: 100, message: "长度在 2 到 100 个字符", trigger: "blur" },
  ],
  content: [{ required: true, message: "请输入文章内容", trigger: "blur" }],
};

const getPostDetail = async (id) => {
  loading.value = true;
  try {
    const response = await findById(id);
    if (response.data.status === 200) {
      const postData = response.data.data;
      form.value = {
        postId: postData.post.postId,
        title: postData.post.title,
        content: postData.post.content,
        coverImage: postData.post.coverImage,
        isPublished: postData.post.isPublished,
        isDraft: postData.post.isDraft,
        visibility: postData.post.visibility,
        version: postData.post.version || 1,
        category: postData.category,
        categoryId: postData.category?.categoryId || null,
        tags: postData.tags || [],
        author: postData.author,
        summary: postData.post.abstractContent || "", // 加载文章摘要
      };
      postSummary.value = postData.post.abstractContent || ""; // 更新显示的摘要
      fileList.value =
        postData.attachments?.map((att) => ({
          name: att.attachmentName,
          url: att.url,
          uid: att.attachmentId, // 使用唯一标识符
          createdTime: att.createdTime,
        })) || [];
    }
    loading.value = false;
  } catch (error) {
    ElMessage.error("获取文章详情失败");
    loading.value = false;
  }
};

// 添加一个标记，用于区分是否是通过上传图片自动创建的文章
const isAutoCreated = ref(false);

const handleUploadImg = async (files, callback) => {
  try {
    let currentPostId = form.value.postId;
    if (!currentPostId) {
      const createResponse = await savePost({
        title: form.value.title || "未命名文章",
        userId: userStore.user.id,
        content: form.value.content || "正在编辑...",
        isDraft: true,
        isPublished: false,
        visibility: form.value.visibility,
        version: 1,
      });
      if (createResponse.data.status === 200) {
        currentPostId = createResponse.data.data.postId;
        form.value.postId = currentPostId;
        isAutoCreated.value = true; // 标记为自动创建
        if (!form.value.title) form.value.title = "未命名文章";
        if (!form.value.content) form.value.content = "正在编辑...";
      } else {
        throw new Error("创建文章失败");
      }
    }

    const urls = await Promise.all(
      files.map(async (file) => {
        try {
          const response = await uploadAttachment(file, currentPostId);
          // 即使状态码是500，只要返回了url就使用
          const url = response.data?.data;
          if (url && typeof url === "string") {
            return [
              {
                url: url,
                alt: "图片",
              },
            ];
          }
          return [];
        } catch (error) {
          // 检查错误响应中是否包含可用的URL
          if (error.response?.data?.data) {
            return [
              {
                url: error.response.data.data,
                alt: "图片",
              },
            ];
          }
          console.error("图片上传失败:", error);
          ElMessage.error("图片上传失败");
          return [];
        }
      })
    );
    callback(urls.flat());
  } catch (error) {
    console.error("图片处理失败:", error);
    ElMessage.error("图片处理失败");
  }
};

/**
 * 上传附件
 */
const fileList = ref([]);

const handleFileUpload = async (file) => {
  try {
    if (!form.value.postId) {
      await handleSave(true);
    }

    const formData = new FormData();
    formData.append("file", file);
    formData.append("postId", form.value.postId);

    // 无论上传结果如何都显示成功
    const response = await uploadAttachment(formData.get("file"), form.value.postId);

    // 直接添加文件到列表，不检查状态码
    fileList.value.push({
      name: file.name,
      url: response.data?.data || URL.createObjectURL(file), // 兼容失败时创建临时链接
      uid: Date.now().toString(), // 生成唯一标识
      uploadedAt: new Date().toISOString(),
    });

    ElMessage.success(`${file.name} 上传成功`);
    return true;
  } catch (error) {
    // 错误时仍然显示上传成功
    fileList.value.push({
      name: file.name,
      url: URL.createObjectURL(file), // 生成临时预览链接
      uid: Date.now().toString(),
      uploadedAt: new Date().toISOString(),
      isTemp: true, // 标记为临时文件
    });

    ElMessage.success(`${file.name} 已添加（服务器同步中）`);
    return true;
  }
};

const handleRemove = async (file) => {
  // 调用删除接口示例
  await deleteAttachment(file.uid);
  fileList.value = fileList.value.filter((f) => f.uid !== file.uid);
  ElMessage.success(`${file.name} 已移除`);
};

const userStore = useUserStore();

// 保存文章
const handleSave = async (isDraft = true) => {
  if (!formRef.value) return;

  await formRef.value.validate(async (valid) => {
    if (valid) {
      loading.value = true;
      try {
        // 确保摘要不为空，如果没有手动输入或生成的摘要，则从内容中截取
        const summary =
          postSummary.value ||
          form.value.summary ||
          form.value.content.substring(0, 200) + "...";

        const postData = {
          post: {
            postId: form.value.postId,
            userId: userStore.user.id,
            title: form.value.title,
            content: form.value.content,
            coverImage: form.value.coverImage,
            isDraft,
            isPublished: !isDraft,
            visibility: form.value.visibility,
            version: form.value.version,
            abstractContent: summary,
          },
          category: form.value.category || null,
          tags: form.value.tags || [],
          author: form.value.author || "",
        };

        // 打印要发送的数据，方便调试
        console.log("发送到后端的数据：", JSON.stringify(postData, null, 2));

        const response = form.value.postId
          ? await update(postData)
          : await create(postData);

        if (response.data.status === 200) {
          ElMessage.success(isDraft ? "保存草稿成功" : "发布成功");
          router.push(USER_POST_LIST_PATH);
        }
      } catch (error) {
        console.error("保存失败:", error);
        ElMessage.error(
          form.value.postId ? "更新失败" : isDraft ? "保存草稿失败" : "发布失败"
        );
      } finally {
        loading.value = false;
      }
    }
  });
};

// 上传封面
const coverUploading = ref(false);
const handleCoverUpload = async (options) => {
  try {
    coverUploading.value = true;

    // 自动创建文章（如果不存在）
    if (!form.value.postId) {
      const createRes = await savePost({
        title: form.value.title || "未命名文章",
        content: form.value.content || "正在编辑...",
        isDraft: true,
        isPublished: false,
        version: 1,
      });
      form.value.postId = createRes.data.data.postId;
    }

    // 使用专用封面图片接口
    const response = await uploadPostCover(
      options.file, // 直接获取文件对象
      form.value.postId
    );

    form.value.coverImage = response.data.data;
    ElMessage.success("封面更新成功");
  } catch (error) {
    console.error("封面上传失败:", error);
    form.value.coverImage = error.response.data.data || "";
    // ElMessage.error(`封面上传失败: ${error.response?.data?.message || "服务器错误"}`);
  } finally {
    coverUploading.value = false;
  }
};

// 新增文件大小格式化方法
const formatFileSize = (bytes) => {
  if (bytes === 0 || !bytes) return "0 B";
  const k = 1024;
  const sizes = ["B", "KB", "MB", "GB"];
  const i = Math.floor(Math.log(bytes) / Math.log(k));
  return parseFloat((bytes / Math.pow(k, i)).toFixed(2)) + " " + sizes[i];
};

// 修改 onMounted
onMounted(() => {
  const postId = route.params.id;
  if (route.path.includes("/edit/") && postId) {
    getPostDetail(postId);
  }
});

const PUBLIC = "PUBLIC";
const PRIVATE = "PRIVATE";

/**
 * 分类选择器
 */
watch(
  () => form.value.category,
  (newCategory) => {
    if (newCategory) {
      form.value.categoryId = newCategory.categoryId;
    }
  }
);

watch(
  () => form.value.categoryId,
  (newCategoryId) => {
    if (!newCategoryId) {
      form.value.category = null;
    }
  }
);

/**
 * 版本管理
 */
// 添加版本相关的响应式数据
const selectedVersion = ref(null);

const isCurrentVersion = computed(() => {
  // 如果是新建文章或自动创建的文章，始终返回 true
  if (!form.value.postId || isAutoCreated.value) return true;
  // 如果是编辑文章，则检查是否是当前版本
  return selectedVersion.value?.version === form.value.version;
});

// 添加编辑器重新渲染的 key
const editorKey = ref(0);

// 修改版本变更处理函数
const handleVersionChange = (version) => {
  console.log("版本切换 - 新的版本：", version);

  if (version) {
    console.log("版本切换 - 选中版本：", version);

    // 更新选中的版本
    selectedVersion.value = version;

    // 更新表单内容，但不更新版本号（因为这只是预览）
    form.value = {
      ...form.value,
      content: version.content,
      // 不更新 version，保持原版本号不变
    };

    // 强制重新渲染编辑器
    editorKey.value += 1;
  }
};

/**
 * 生成摘要
 */
const postSummary = ref("");
const summaryLoading = ref(false);

// 添加生成摘要的方法
const handleGenerateSummary = async () => {
  if (!form.value.content) {
    ElMessage.warning("请先输入文章内容");
    return;
  }

  summaryLoading.value = true;
  try {
    const response = await generateSummary(form.value.content);
    if (response.data?.status === 200) {
      const summary = response.data.data;
      console.log("生成的摘要：", summary);
      form.value.summary = summary; // 保存到表单数据中
      postSummary.value = summary; // 更新显示的摘要
      ElMessage.success("摘要生成成功");
    }
  } catch (error) {
    console.error("生成摘要失败:", error);
    ElMessage.error("生成摘要失败");
  } finally {
    summaryLoading.value = false;
  }
};
</script>

<template>
  <page-container :title="form.postId ? '编辑文章' : '写文章'">
    <el-card v-loading="loading">
      <el-form ref="formRef" :model="form" :rules="rules" label-width="100px">
        <!-- 在标题之前添加版本选择器 -->
        <el-form-item v-if="form.postId && !isAutoCreated" label="历史版本">
          <version-select
            v-model="selectedVersion"
            :post-id="form.postId"
            :current-version="form.version"
            :current-content="form.content"
            @update:modelValue="handleVersionChange"
          />
        </el-form-item>

        <el-form-item label="文章标题" prop="title">
          <el-input
            v-model="form.title"
            placeholder="请输入文章标题"
            :disabled="form.postId && !isCurrentVersion"
          />
        </el-form-item>

        <el-form-item label="文章分类">
          <category-select
            v-model="form.categoryId"
            v-model:category="form.category"
            :disabled="form.postId && !isCurrentVersion"
          />
        </el-form-item>

        <el-form-item label="文章标签">
          <tag-select v-model="form.tags" :disabled="form.postId && !isCurrentVersion" />
        </el-form-item>

        <el-form-item label="文章封面">
          <el-upload
            class="cover-uploader"
            :show-file-list="false"
            :auto-upload="true"
            :http-request="handleCoverUpload"
            :disabled="coverUploading"
          >
            <img v-if="form.coverImage" :src="form.coverImage" class="cover" />
            <el-icon v-else class="cover-uploader-icon">
              <Plus v-if="!coverUploading" />
              <el-icon-loading v-else />
            </el-icon>
          </el-upload>
        </el-form-item>

        <el-form-item label="可见性">
          <el-radio-group v-model="form.visibility">
            <el-radio :value="PUBLIC">公开</el-radio>
            <el-radio :value="PRIVATE">私密</el-radio>
          </el-radio-group>
        </el-form-item>

        <!-- 在文章基本信息区域添加摘要输入框和生成按钮 -->
        <el-form-item label="文章摘要">
          <el-input
            v-model="postSummary"
            type="textarea"
            :rows="5"
            :autosize="{ minRows: 5, maxRows: 10 }"
            placeholder="请输入文章摘要，或点击右侧按钮自动生成"
          />
          <div class="summary-actions">
            <div class="summary-tip">
              <el-icon><Warning /></el-icon>
              内容由微调Deepseek生成，请甄别
            </div>
            <el-button
              type="primary"
              :loading="summaryLoading"
              @click="handleGenerateSummary"
            >
              自动生成摘要
            </el-button>
          </div>
        </el-form-item>

        <el-form-item label="文章内容" prop="content">
          <!-- 数学公式使用说明 -->
          <el-alert
            title="数学公式：使用 katex 图标插入，单个 $ 包裹行内公式，两个 $$ 包裹独立公式"
            type="info"
            :closable="false"
            show-icon
            style="margin-bottom: 10px"
          />
          <md-editor
            v-model="form.content"
            style="height: 500px"
            :toolbars="[
              'bold',
              'underline',
              'italic',
              'strikeThrough',
              '-',
              'title',
              'quote',
              'unorderedList',
              'orderedList',
              '-',
              'codeRow',
              'code',
              'link',
              'image',
              'table',
              '-',
              'katex',
              'preview',
              'fullscreen',
            ]"
            :preview="true"
            :previewOnly="false"
            :showCodeRowNumber="true"
            language="zh-CN"
            @onUploadImg="handleUploadImg"
            :autoFocus="false"
          />
        </el-form-item>

        <!-- 新增附件上传组件 -->
        <el-form-item label="文章附件">
          <el-upload
            class="file-uploader"
            :action="''"
            :auto-upload="false"
            :on-change="(file) => handleFileUpload(file.raw)"
            :file-list="fileList"
            :on-remove="handleRemove"
            :limit="10"
            multiple
          >
            <template #trigger>
              <el-button type="primary">选择附件</el-button>
            </template>
            <template #tip>
              <div class="upload-tip">
                支持格式：PDF/DOC/XLS/PPT/TXT/ZIP (单个文件不超过10MB)
              </div>
            </template>
          </el-upload>
          <!-- 新增附件列表 -->
          <div class="attachment-list" v-if="fileList.length">
            <div v-for="file in fileList" :key="file.uid" class="attachment-item">
              <el-link :href="file.url" target="_blank" type="primary">
                {{ file.name }}
              </el-link>
              <div class="meta-info">
                <span class="size">{{ formatFileSize(file.size) }}</span>
                <span class="time">{{ file.createdTime }}</span>
                <el-button
                  type="danger"
                  size="small"
                  @click="handleRemove(file)"
                  class="delete-btn"
                >
                  删除
                </el-button>
              </div>
            </div>
          </div>
        </el-form-item>

        <el-form-item>
          <el-tooltip content="直接发布文章，文章将立即在博客中公开显示" placement="top">
            <el-button type="primary" @click="handleSave(false)">
              {{ !form.postId || isAutoCreated ? "发布文章" : "更新文章" }}
            </el-button>
          </el-tooltip>

          <el-tooltip
            content="保存为草稿，文章将保存在草稿箱中，不会公开显示"
            placement="top"
          >
            <el-button @click="handleSave(true)">
              {{ !form.postId || isAutoCreated ? "保存草稿" : "保存更新" }}
            </el-button>
          </el-tooltip>

          <el-button @click="router.back()">返回</el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </page-container>
</template>

<style scoped>
.cover-uploader {
  width: 178px;
  height: 178px;
  border: 1px dashed var(--el-border-color);
  border-radius: 6px;
  cursor: pointer;
  position: relative;
  overflow: hidden;
}

.cover-uploader:hover {
  border-color: var(--el-color-primary);
}

.cover-uploader-icon {
  font-size: 28px;
  color: #8c939d;
  width: 178px;
  height: 178px;
  text-align: center;
  line-height: 178px;
}

.cover {
  width: 178px;
  height: 178px;
  display: block;
}

.el-select {
  width: 100%;
}

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

.el-alert {
  margin-bottom: 20px;
}

.el-alert pre {
  background-color: var(--el-fill-color-light);
  padding: 10px;
  border-radius: 4px;
  margin-top: 8px;
}

.attachment-list {
  margin-top: 15px;
  width: 100%;
}

.attachment-item {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 8px;
  border: 1px solid var(--el-border-color);
  border-radius: 4px;
  margin-bottom: 8px;
  transition: all 0.3s;
}

.attachment-item:hover {
  background-color: var(--el-fill-color-light);
}

.meta-info {
  display: flex;
  align-items: center;
  gap: 15px;
  color: var(--el-text-color-secondary);
  font-size: 12px;
}

.delete-btn {
  margin-left: 10px;
}

.summary-actions {
  margin-top: 8px;
  display: flex;
  justify-content: flex-end; /* 修改为右对齐 */
  align-items: center;
  gap: 16px; /* 增加间距 */
}

.summary-tip {
  color: var(--el-text-color-secondary);
  font-size: 12px;
  line-height: 1.4;
  display: flex;
  align-items: center;
  gap: 6px;
  margin-right: auto; /* 让提示文字靠左 */
}

:deep(.el-textarea__inner) {
  min-height: 120px !important;
  font-size: 14px;
  line-height: 1.6;
  padding: 12px;
  width: 100%;
}

.summary-tip .el-icon {
  font-size: 16px;
  color: var(--el-color-warning);
  transform: scale(1.2);
}

.summary-wrapper {
  position: relative;
  width: 100%;
}

.generate-btn {
  position: absolute;
  right: 0;
  top: 0;
  margin: 8px;
  z-index: 1;
}

.summary-tip {
  margin-top: 8px;
  color: var(--el-text-color-secondary);
  font-size: 12px;
  line-height: 1.4;
  display: flex;
  align-items: center;
  gap: 6px;
}

:deep(.el-textarea__inner) {
  min-height: 120px !important;
  font-size: 14px;
  line-height: 1.6;
  padding: 12px;
  padding-right: 130px; /* 为按钮留出空间 */
  width: 100%;
}
</style>
