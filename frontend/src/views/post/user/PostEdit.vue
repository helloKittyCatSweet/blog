<script setup>
import { ref, onMounted, watch } from "vue";
import { useRoute, useRouter } from "vue-router";
import { ElMessage } from "element-plus";
import PageContainer from "@/components/PageContainer.vue";
import { MdEditor } from "md-editor-v3";
import "md-editor-v3/lib/style.css";
import { findById, create, update, uploadAttachment } from "@/api/post/post.js";
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
const form = ref({
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
});

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
      };
    }
    loading.value = false;
  } catch (error) {
    ElMessage.error("获取文章详情失败");
    loading.value = false;
  }
};

const handleUploadImg = async (files, callback) => {
  try {
    let currentPostId = form.value.postId;
    if (!currentPostId) {
      const createResponse = await create({
        title: form.value.title || "未命名文章",
        content: form.value.content || "正在编辑...",
        isDraft: true,
        isPublished: false,
        visibility: form.value.visibility,
        version: 1,
      });
      if (createResponse.data.status === 200) {
        currentPostId = createResponse.data.data.postId;
        form.value.postId = currentPostId;
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

const userStore = useUserStore();

// 保存文章
const handleSave = async (isDraft = true) => {
  if (!formRef.value) return;

  await formRef.value.validate(async (valid) => {
    if (valid) {
      loading.value = true;
      try {
        // console.log("category:", form.value.category, "-----", form.value.categoryId);

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
const handleUploadSuccess = (res) => {
  form.value.coverImage = res.url;
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

// 添加版本变更处理函数
const handleVersionChange = (version) => {
  if (version) {
    form.value = {
      ...form.value,
      version: version.version,
      title: version.title,
      content: version.content,
      coverImage: version.coverImage,
      visibility: version.visibility,
    };
  }
};
</script>

<template>
  <page-container :title="form.postId ? '编辑文章' : '写文章'">
    <el-card v-loading="loading">
      <el-form ref="formRef" :model="form" :rules="rules" label-width="100px">
        <!-- 在标题之前添加版本选择器 -->
        <el-form-item v-if="form.postId" label="历史版本">
          <version-select
            v-model="selectedVersion"
            :post-id="form.postId"
            @update:modelValue="handleVersionChange"
          />
        </el-form-item>

        <el-form-item label="文章标题" prop="title">
          <el-input v-model="form.title" placeholder="请输入文章标题" />
        </el-form-item>

        <el-form-item label="文章分类">
          <category-select v-model="form.categoryId" v-model:category="form.category" />
        </el-form-item>

        <el-form-item label="文章标签">
          <tag-select v-model="form.tags" />
        </el-form-item>

        <el-form-item label="文章封面">
          <el-upload
            class="cover-uploader"
            :show-file-list="false"
            :on-success="handleUploadSuccess"
          >
            <img v-if="form.coverImage" :src="form.coverImage" class="cover" />
            <el-icon v-else class="cover-uploader-icon"><Plus /></el-icon>
          </el-upload>
        </el-form-item>

        <el-form-item label="可见性">
          <el-radio-group v-model="form.visibility">
            <el-radio :value="PUBLIC">公开</el-radio>
            <el-radio :value="PRIVATE">私密</el-radio>
          </el-radio-group>
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

        <el-form-item>
          <el-tooltip content="直接发布文章，文章将立即在博客中公开显示" placement="top">
            <el-button type="primary" @click="handleSave(false)">
              {{ form.postId ? "更新文章" : "发布文章" }}
            </el-button>
          </el-tooltip>

          <el-tooltip
            content="保存为草稿，文章将保存在草稿箱中，不会公开显示"
            placement="top"
          >
            <el-button @click="handleSave(true)">
              {{ form.postId ? "保存更新" : "保存草稿" }}
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
</style>
