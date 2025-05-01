<script setup>
import { computed } from "vue";
import * as baseRoutes from "@/constants/routes/base";
import * as userRoutes from "@/constants/routes/user";
import * as adminRoutes from "@/constants/routes/admin";

const props = defineProps({
  menuConfig: {
    type: Array,
    required: true,
  },
});

// 解析路由常量
const getRouteValue = (routeName) => {
  // 从不同的路由文件中查找常量
  return (
    baseRoutes[routeName] || userRoutes[routeName] || adminRoutes[routeName] || routeName
  );
};
</script>

<template>
  <template v-for="menu in menuConfig" :key="menu.index">
    <!-- 子菜单 -->
    <el-sub-menu v-if="menu.children" :index="getRouteValue(menu.index)">
      <template #title>
        <el-icon><component :is="menu.icon" /></el-icon>
        <span>{{ menu.title }}</span>
      </template>
      <menu-renderer :menu-config="menu.children" />
    </el-sub-menu>
    <!-- 菜单项 -->
    <el-menu-item v-else :index="getRouteValue(menu.index)">
      <el-icon><component :is="menu.icon" /></el-icon>
      <span>{{ menu.title }}</span>
    </el-menu-item>
  </template>
</template>

<style scoped>
.el-menu-item,
.el-sub-menu {
  background-color: var(--menu-bg) !important;
  position: relative;
  z-index: 2;
}

.el-menu-item:hover,
.el-sub-menu:hover {
  background-color: var(--hover-bg) !important;
}
</style>