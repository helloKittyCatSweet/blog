<script setup>
import { ref } from "vue";
// import { artDeleteCategoryService, artGetCategoriesService } from "@/api/category.js";
import CategoryEdit from "@/views/post/components/CategoryEdit.vue";
import { ElMessage, ElMessageBox } from "element-plus";

const categoryList = ref([]);
const loading = ref(false);
const dialog = ref();

const getCategoryList = async () => {
  loading.value = true;
  const res = await artGetCategoriesService();
  categoryList.value = res.data.data;
  loading.value = false;
};
getCategoryList();

const onEditCategory = (row) => {
  dialog.value.open(row);
};

const onDeleteCategory = async (row) => {
  await ElMessageBox.confirm("你确认要删除该分类么", "温馨提示", {
    type: "warning",
    confirmButtonText: "确认",
    cancelButtonText: "取消",
  });

  await artDeleteCategoryService(row.id);
  ElMessage.success("删除成功");
  getCategoryList();
};

const onAddCategory = () => {
  dialog.value.open({});
};

const onSuccess = () => {
  getCategoryList();
};
</script>

<template>
  <page-container title="文章分类">
    <template #extra>
      <el-button @click="onAddCategory" type="primary"> 添加分类 </el-button>
    </template>

    <el-table v-loading="loading" :data="categoryList" style="width: 100%">
      <el-table-column label="序号"></el-table-column>
      <el-table-column label="分类名称"></el-table-column>
      <el-table-column label="分类别名"></el-table-column>
      <el-table-column label="操作">
        <!-- row就是categoryList的一项，$index下标 -->
        <template #default="{ row, index }">
          <el-button
            :icon="Edit"
            circle
            plain
            type="primary"
            @click="onEditCategory(row, index)"
          >
            编辑
          </el-button>
          <el-button
            :icon="Delete"
            circle
            plain
            type="danger"
            @click="onDeleteCategory(row, index)"
          >
            删除
          </el-button>
        </template>
      </el-table-column>

      <template #empty>
        <el-empty description="没有数据"></el-empty>
      </template>
    </el-table>

    <channel-edit ref="dialog" @success="onSuccess"></channel-edit>
  </page-container>
</template>
