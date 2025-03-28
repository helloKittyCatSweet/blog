<script setup>
import { Delete, Edit } from "@element-plus/icons-vue";
import CategorySelect from "@/views/post/components/CategorySelect.vue";
import { ref } from "vue";
import { formatTime } from "@/utils/format";
import PostEdit from "@/views/post/components/PostEdit.vue";
import { ElMessageBox } from "element-plus";

const articleList = ref([]); // 文章列表
const total = ref(0); // 默认总条数是0
const loading = ref(false);

// 定义请求参数对象
const params = ref({
  pagenum: 1, // 当前页
  pagesize: 5, // 当前生效的每页条数
  cate_id: "",
  state: "",
});

// 基于params参数，获取文章列表
const getArticleList = async () => {
  loading.value = true;

  loading.value = false;
};
getArticleList();

// 处理分页逻辑
const onSizeChange = (size) => {
  // 只要是每页条数变化了，那么原本正在访问的当前页意义不大了，数据大概率已经不在原来那一页了
  // 重新从第一页渲染即可
  params.value.pagenum = 1;
  params.value.pagesize = size;
  // 基于最新的当前页和每页条数，渲染数据
  getArticleList();
};
const onCurrentChange = (page) => {
  // 更新当前页
  params.value.pagenum = page;
  // 基于最新的当前页，渲染数据
  getArticleList();
};

// 编辑逻辑
const onEditArticle = (row) => {
  articleEditRef.value.open({ row });
};
// 删除逻辑
const onDeleteArticle = async (row) => {
  // 提示用户是否要删除
  await ElMessageBox.confirm("此操作将永久删除该文章，是否继续？", "提示", {
    confirmButtonText: "确定",
    cancleButtonText: "取消",
    type: "warning",
  });
  await artDeleteService(row.id);
  ElMessageBox.success("删除成功");
  // 刷新列表
  getArticleList();
  console.log(row);
};

// 搜索逻辑 => 按照最新的条件，重新请求，从第一页开始展示
const onSearch = () => {
  params.value.pagenum = 1; // 重置页码
  getArticleList();
};

// 重置逻辑 => 将筛选条件清空，重新检索，从第一页开始展示
const onReset = () => {
  params.value.pagenum = 1; // 重置页码
  params.value.cate_id = "";
  params.value.state = "";
  getArticleList();
};

const articleEditRef = ref();
// 添加逻辑
const onAddArticle = () => {
  articleEditRef.value.open({});
};

// 添加或者编辑成功的回调
const onSuccess = (type) => {
  if (type === "add") {
    // 如果是添加，需要跳转渲染最后一页，编辑直接渲染当前页
    const lastPage = Math.ceil((total.value + 1) / params.value.pagesize);
    // 更新成最大页码数，再渲染
    params.value.pagenum = lastPage;
  }
  getArticleList();
};
</script>

<template>
  <page-container title="文章管理">
    <template #extra>
      <el-button type="primary" @click="onAddArticle"> 添加文章 </el-button>
    </template>

    <!-- 表单区域 -->
    <!-- inline控制组件在一行内显示 -->
    <el-form inline>
      <el-form-item label="文章分类：">
        <!-- Vue2 => v-model :value和@input的简写 -->
        <!-- Vue3 => v-model :modelValue和@update:modelValue的简写 -->
        <!-- Vue3 => v-model:cid :cid和@update:cid的简写 -->
        <CategorySelect v-model="params.cate_id"></CategorySelect>
      </el-form-item>
      <el-form-item label="发布状态：">
        <el-select v-model="params.state">
          <el-option label="已发布" value="已发布"></el-option>
          <el-option label="草稿" value="草稿"></el-option>
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button @click="onSearch" type="primary">搜索</el-button>
        <el-button @click="onReset">重置</el-button>
      </el-form-item>
    </el-form>

    <!-- 表格区域 -->
    <el-table :data="articleList" v-loading="loading">
      <el-table-column label="文章标题" prop="title">
        <template #default="{ row }">
          <el-link type="primary" :underline="false">{{ row.title }}</el-link>
        </template>
      </el-table-column>
      <el-table-column label="分类" prop="cate_name"></el-table-column>
      <el-table-column label="发表时间" prop="pub_date">
        <template #default="{ row }">
          {{ formatTime(row.pub_date) }}
        </template>
      </el-table-column>
      <el-table-column label="状态" prop="state"></el-table-column>
      <el-table-column label="操作">
        <!-- 利用作用域插槽可以获取当前行的数据 => v-for时的item-->
        <template #default="{ row }">
          <el-button
            :icon="Edit"
            circle
            plain
            type="primary"
            @click="onEditArticle(row)"
          ></el-button>
          <el-button
            :icon="Delete"
            circle
            plain
            type="danger"
            @click="onDeleteArticle(row)"
          ></el-button>
        </template>
      </el-table-column>
    </el-table>

    <!-- 分页区域 -->
    <el-pagination
      v-model:current-page="params.pagenum"
      v-model:page-size="params.pagesize"
      :page-sizes="[2, 3, 4, 5, 10]"
      layout="jumper, total, sizes, prev, pager, next"
      background
      :total="total"
      @size-change="onSizeChange"
      @current-change="onCurrentChange"
      style="margin-top: 20px; justify-content: flex-end"
    />

    <!-- 抽屉区域 -->
    <PostEdit ref="articleEditRef" @success="onSuccess"></PostEdit>
  </page-container>
</template>
