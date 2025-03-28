<script setup>
import { ref } from "vue";
import CategorySelect from "@/views/post/components/CategorySelect.vue";
import { Plus } from "@element-plus/icons-vue";
import { QuillEditor } from "@vueup/vue-quill";
import "@vueup/vue-quill/dist/vue-quill.snow.css";
import { baseURL } from "@/utils/request";

const visibleDrawer = ref(false);

// 默认数据
const defaultForm = ref({
  id: "", // 文章id
  title: "", // 标题
  cate_id: "", // 分类id
  cover_img: "", // 封面图片 file对象
  content: "", // 内容
  state: "", // 状态
});

// 准备数据
const formModel = ref({
  ...defaultForm.value,
});

// 图片上传相关逻辑
const imgUrl = ref("");
const onSelectFile = (uploadFile) => {
  imgUrl.value = URL.createObjectURL(uploadFile.raw); // 预览图片
};

const editorRef = ref(); // 编辑器的ref

// 组件对外暴露一个方法open，基于open传来的参数，区分添加还是编辑
// open({}) => 表单无需渲染，说明是添加
// open({id, cate_name, ...}) => 表达需要渲染，说明是编辑
// open调用后，可以打开抽屉
const open = async (row) => {
  visibleDrawer.value = true; // 显示抽屉

  if (row.id) {
    // 需要基于row.id发送请求，获取编辑对应的详情数据，进行回显
    const res = await artGetDetailService(row.id);
    formModel.value = res.data.data;
    // 图片需要单独处理回显
    imgUrl.value = baseURL + formModel.value.cover_img;
    // 注意：提交给后台，需要的数据格式是file对象格式，
    // 需要将网络图片地址转换成file对象存储起来
    formModel.value.cover_img = await imageUrlToFile(
      imgUrl.value,
      formModel.value.cover_img
    );
  } else {
    formModel.value = {
      ...defaultForm, // 基于默认的数据重置form数据
    };
    // 这里重置了表单的数据，但是图片上传img地址，富文本编辑器内容需要手动重置
    imgUrl.value = "";
    editorRef.value.setHTML("");
  }
  console.log(row);
};

// 将网络图片地址转换为File对象
async function imageUrlToFile(url, fileName) {
  try {
    // 第一步：使用axios获取网络图片数据
    const response = await axios.get(url, { responseType: "arraybuffer" });
    const imageData = response.data;

    // 第二步：将图片数据转换为Blob对象
    const blob = new Blob([imageData], { type: response.headers["content-type"] });

    // 第三步：创建一个新的File对象
    const file = new File([blob], fileName, { type: blob.type });

    return file;
  } catch (error) {
    console.error("将图片转换为File对象时发生错误:", error);
    throw error;
  }
}

defineExpose({
  open,
});

// 发布文章
const emit = defineEmits(["success"]);
const onPublish = async (state) => {
  // 将已发布还是草稿状态，存入 state
  formModel.value.state = state;

  // 转换 formData 数据
  const fd = new FormData();
  for (let key in formModel.value) {
    fd.append(key, formModel.value[key]);
  }

  if (formModel.value.id) {
    // await artEditService(fd);
    ElMessage.success("编辑成功");
    visibleDrawer.value = false;
    emit("success", "edit");
  } else {
    // 添加请求
    await artPublishService(fd);
    ElMessage.success("添加成功");
    visibleDrawer.value = false;
    // 通知到父组件，添加成功了
    emit("success", "add");
  }
};
</script>

<template>
  <!-- 抽屉 -->
  <el-drawer
    v-model="visibleDrawer"
    :title="formModel.id ? '编辑文章' : '添加文章'"
    direction="rtl"
    size="50%"
  >
    <!-- 发表文章表单 -->
    <el-form :model="formModel" ref="formRef" label-width="100px">
      <el-form-item label="文章标题" prop="title">
        <el-input v-model="formModel.title" placeholder="请输入标题"></el-input>
      </el-form-item>
      <el-form-item label="文章分类" prop="cate_id">
        <category-select v-model="formModel.cate_id" width="100%"></category-select>
      </el-form-item>
      <el-form-item label="文章封面" prop="cover_img">
        <!-- 此处需要关闭element-plus的自动上传，不需要配置action等参数
           只需要做前端的本地预览图片即可，无需在提交前上传图标
           语法：URL.createObjectURL(...) 创建本地预览的地址来预览
       -->
        <el-upload
          class="avatar-uploader"
          :auto-upload="false"
          :show-file-list="false"
          :on-change="onSelectFile"
        >
          <img v-if="imgUrl" :src="imgUrl" class="avatar" />
          <el-icon v-else class="avatar-uploader-icon"><Plus /></el-icon>
        </el-upload>
      </el-form-item>
      <el-form-item label="文章内容" prop="content">
        <div class="editor">
          <QuillEditor
            ref="editorRef"
            v-model:content="formModel.content"
            theme="snow"
            content-type="html"
          ></QuillEditor>
        </div>
      </el-form-item>
      <el-form-item>
        <el-button @click="onPublish('已发布')" type="primary">发布</el-button>
        <el-button @click="onPublish('草稿')" type="info">草稿</el-button>
      </el-form-item>
    </el-form>
  </el-drawer>
</template>

<style lang="scss" scoped>
.avatar-uploader {
  :deep() {
    .avatar {
      width: 178px;
      height: 178px;
      display: block;
    }
    .el-upload {
      border: 1px dashed var(--el-border-color);
      border-radius: 6px;
      cursor: pointer;
      position: relative;
      overflow: hidden;
      transition: var(--el-transition-duration-fast);
    }
    .el-upload:hover {
      border-color: var(--el-color-primary);
    }
    .el-icon.avatar-uploader-icon {
      font-size: 28px;
      color: #8c939d;
      width: 178px;
      height: 178px;
      text-align: center;
    }
  }
}

.editor {
  width: 100%;
  :deep(.ql-editor) {
    min-height: 200px;
  }
}
</style>
