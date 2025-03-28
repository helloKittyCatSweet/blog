<script setup>
// import { artAddCategoryService, artEditCategoryService } from "@/api/category";
import { ElMessage } from "element-plus";
import { ref } from "vue";
const dialogVisible = ref(false);
const formModel = ref({
  cate_name: "",
  cate_alias: "",
});
const rules = {
  cate_name: [
    { required: true, message: "请输入分类名称", trigger: "blur" },
    {
      pattern: /^\S{1,10}$/,
      message: "分类名必须是1-10位的非空字符",
      trigger: "blur",
    },
  ],
  cate_alias: [
    { required: true, message: "请输入分类别名", trigger: "blur" },
    {
      pattern: /^[a-zA-Z0-9]{1,15}$/,
      message: "分类别名必须是1-15位的字母或数字",
      trigger: "blur",
    },
  ],
};

const formRef = ref();

// 组件对外暴露一个方法open，基于open传来的参数，区分添加还是编辑
// open({}) => 表单无需渲染，说明是添加
// open({id, cate_name, ...}) => 表达需要渲染，说明是编辑
// open调用后，可以打开弹窗
const open = async (row) => {
  dialogVisible.value = true;
  console.log(row);
  formModel.value = { ...row }; // 添加->重置表单内容，编辑->存储了需要回显的数据
};

const emit = defineEmits(["sucess"]);
const onSubmit = async () => {
  // await formRef.value.validate();
  // const isEdit = formModel.value.id;
  // if (isEdit) {
  //   await artEditCategoryService(formModel.value);
  //   ElMessage.success("编辑成功");
  // } else {
  //   await artAddCategoryService(formRef.value);
  //   ElMessage.success("添加成功");
  // }

  // 关弹层
  dialogVisible.value = false;
  emit("sucess");
};

defineExpose({
  open,
});
</script>

<template>
  <el-dialog
    v-model="dialogVisible"
    :title="formModel.id ? '编辑分类' : '添加分类'"
    width="30%"
  >
    <el-form
      :model="formModel"
      :rules="rules"
      label-width="100px"
      style="padding-right: 30px"
      ref="formRef"
    >
      <el-form-item label="分类名称" prop="cate_name">
        <el-input v-model="formModel.cate_name" placeholder="请输入分类名称"></el-input>
      </el-form-item>
      <el-form-item label="分类别名" prop="cate_alias">
        <el-input v-model="formModel.cate_alias" placeholder="请输入分类别名"></el-input>
      </el-form-item>
    </el-form>
    <template #footer>
      <span class="dialog-footer">
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="onSubmit"> 确认 </el-button>
      </span>
    </template>
  </el-dialog>
</template>
