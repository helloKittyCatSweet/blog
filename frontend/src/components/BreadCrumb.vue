<!-- src/components/Breadcrumb.vue -->
<script setup>
import { useRoute, useRouter } from "vue-router";
import { computed } from "vue";

const route = useRoute();
const router = useRouter();

const breadcrumbList = computed(() => {
  const matched = route.matched;
  const items = [];

  matched.forEach((item) => {
    if (item.meta?.breadcrumb) {
      if (Array.isArray(item.meta.breadcrumb)) {
        // 如果是数组，直接使用配置的面包屑
        items.push(...item.meta.breadcrumb);
      } else if (item.meta?.title && item.path) {
        // 如果 breadcrumb 为 true，使用路由信息生成面包屑
        items.push({
          title: item.meta.title,
          path: item.path,
        });
      }
    }
  });

  // 去重处理
  const uniqueItems = [];
  const pathSet = new Set();
  items.forEach((item) => {
    if (!pathSet.has(item.path)) {
      pathSet.add(item.path);
      uniqueItems.push({
        title: item.title || "",
        path: item.path || "",
        disabled: item.path === route.path,
      });
    }
  });

  return uniqueItems;
});

// 添加导航处理函数
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

<style lang="scss" scoped>
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
