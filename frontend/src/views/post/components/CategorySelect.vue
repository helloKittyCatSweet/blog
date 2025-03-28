<script setup>
// import { artGetCategoriesService } from "@/api/category";
import { ref } from "vue";

defineProps({
  modelValue: {
    type: [Number, String],
  },
  width: {
    type: String,
  },
});
const emit = defineEmits(["update:modelValue"]);

const categoryList = ref([]);
const getCategoriesList = async () => {
  // const res = await artGetCategoriesService();
  // categoryList.value = res.data.data;
};

getCategoriesList();
</script>

<template>
  <!-- $event代表选中的项的参数，就是改了cateId的值 -->
  <el-select
    :modelValue="modelValue"
    @update:modelValue="emit('update:modelValue', $event)"
    :style="{ width }"
  >
    <!-- label是展示给用户看的，value是提交给后台的（可以理解为id） -->
    <el-option
      v-for="category in categoryList"
      :key="category.id"
      :label="category.cate_name"
      :value="category.id"
    ></el-option>
  </el-select>
</template>

<style></style>
