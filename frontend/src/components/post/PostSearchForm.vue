<script setup>
import { ref } from "vue";
import CategorySelect from "@/components/category/CategorySelect.vue";
import { Search } from "@element-plus/icons-vue";

const props = defineProps({
  modelValue: {
    type: Object,
    required: true,
  },
  tags: {
    type: Array,
    default: () => [],
  },
});

const emit = defineEmits(["update:modelValue", "search", "reset", "date-range-change"]);

const dateRange = ref([]);

const handleDateRangeChange = (val) => {
  emit("date-range-change", val);
};
</script>

<template>
  <el-form :model="modelValue" label-width="80px">
    <el-row :gutter="20">
      <el-col :span="6">
        <el-form-item label="标题">
          <el-input
            v-model="modelValue.title"
            placeholder="搜索文章标题"
            clearable
            @keyup.enter="emit('search')"
          />
        </el-form-item>
      </el-col>
      <el-col :span="6">
        <el-form-item label="内容">
          <el-input
            v-model="modelValue.content"
            placeholder="搜索文章内容"
            clearable
            @keyup.enter="emit('search')"
          />
        </el-form-item>
      </el-col>
      <el-col :span="6">
        <el-form-item label="状态">
          <el-select v-model="modelValue.isPublished" placeholder="发布状态" clearable>
            <el-option label="已发布" :value="true" />
            <el-option label="草稿" :value="false" />
          </el-select>
        </el-form-item>
      </el-col>
      <el-col :span="6">
        <el-form-item label="可见性">
          <el-select v-model="modelValue.visibility" placeholder="可见性" clearable>
            <el-option label="公开" value="PUBLIC" />
            <el-option label="私密" value="PRIVATE" />
          </el-select>
        </el-form-item>
      </el-col>
    </el-row>

    <el-row :gutter="20">
      <el-col :span="6">
        <el-form-item label="分类">
          <category-select
            v-model="modelValue.categoryId"
            @update:category="$emit('update:category', $event)"
          />
        </el-form-item>
      </el-col>
      <el-col :span="6">
        <el-form-item label="标签">
          <el-select v-model="modelValue.tagId" placeholder="选择标签" clearable>
            <el-option
              v-for="item in tags"
              :key="item.tagId"
              :label="item.name"
              :value="item.tagId"
            />
          </el-select>
        </el-form-item>
      </el-col>
      <el-col :span="12">
        <el-form-item label="创建时间">
          <el-date-picker
            v-model="dateRange"
            type="daterange"
            range-separator="至"
            start-placeholder="开始日期"
            end-placeholder="结束日期"
            value-format="YYYY-MM-DD"
            @change="handleDateRangeChange"
          />
        </el-form-item>
      </el-col>
    </el-row>

    <el-row>
      <el-col :span="24" style="text-align: right">
        <slot name="extra-buttons"></slot>
        <el-button type="primary" :icon="Search" @click="emit('search')">搜索</el-button>
        <el-button @click="emit('reset')">重置</el-button>
      </el-col>
    </el-row>
  </el-form>
</template>

<style scoped>
.el-form-item {
  margin-bottom: 18px;
}

.el-date-picker {
  width: 100%;
}
</style>
