<!-- src/components/Breadcrumb.vue -->
<script setup>
import { useRoute, useRouter } from "vue-router";
import { computed } from "vue";

const route = useRoute();
const router = useRouter();

const breadcrumbList = computed(() => {
  // 获取当前路由的完整匹配记录
  const matched = route.matched;

  // 提取有效的面包屑项
  const items = [];
  matched.forEach((item) => {
    if (item.meta?.breadcrumb) {
      // 如果路由配置了breadcrumb数组，直接使用
      items.push(...item.meta.breadcrumb);
    } else if (item.meta?.title && item.path) {
      // 否则使用title和path自动生成
      items.push({
        title: item.meta.title,
        path: item.path,
      });
    }
  });

  // 去重处理（避免重复项）
  const uniqueItems = [];
  const pathSet = new Set();
  items.forEach((item) => {
    if (!pathSet.has(item.path)) {
      pathSet.add(item.path);
      uniqueItems.push({
        ...item,
        disabled: item.path === route.path, // 当前页禁用点击
      });
    }
  });

  return uniqueItems;
});

const handleNavigate = (path, disabled) => {
  if (!disabled && path !== route.path) {
    router.push(path);
  }
};
</script>

<template>
  <el-breadcrumb separator="/">
    <el-breadcrumb-item v-for="item in breadcrumbList" :key="item.path">
      <span
        class="breadcrumb-item"
        :class="{ disabled: item.disabled }"
        @click="handleNavigate(item.path, item.disabled)"
      >
        {{ item.title }}
      </span>
    </el-breadcrumb-item>
  </el-breadcrumb>
</template>

<style scoped>
.breadcrumb-item {
  cursor: pointer;
  color: var(--el-text-color-regular);
  transition: color 0.3s;

  &:hover:not(.disabled) {
    color: var(--el-color-primary);
  }

  &.disabled {
    color: var(--el-text-color-secondary);
    cursor: default;
  }
}
</style>
