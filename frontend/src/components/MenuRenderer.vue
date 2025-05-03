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
  isCollapsed: {
    type: Boolean,
    default: false
  }
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
        <el-tooltip
          v-if="isCollapsed"
          :content="menu.title"
          placement="right"
          :show-after="200"
        >
          <el-icon><component :is="menu.icon" /></el-icon>
        </el-tooltip>
        <template v-else>
          <el-icon><component :is="menu.icon" /></el-icon>
          <span>{{ menu.title }}</span>
        </template>
      </template>
      <!-- 递归渲染子菜单 -->
      <menu-renderer :menu-config="menu.children" :is-collapsed="isCollapsed" />
    </el-sub-menu>

    <!-- 普通菜单项 -->
    <el-menu-item v-else :index="getRouteValue(menu.index)">
      <el-tooltip
        v-if="isCollapsed"
        :content="menu.title"
        placement="right"
        :show-after="200"
      >
        <el-icon><component :is="menu.icon" /></el-icon>
      </el-tooltip>
      <template v-else>
        <el-icon><component :is="menu.icon" /></el-icon>
        <span>{{ menu.title }}</span>
      </template>
    </el-menu-item>
  </template>
</template>

<style scoped>
/* 基础变量定义 */
:root {
  --menu-bg: #ffffff;
  --hover-bg: #f5f7fa;
  --active-bg: #ecf5ff;
  --text-color: #303133;
  --active-text: #409eff;
  --border-color: #e6e6e6;
}

.el-menu-item,
.el-sub-menu {
  background-color: transparent !important;
}

/* 菜单项悬停效果 */
.el-menu-item:hover,
.el-sub-menu:hover :deep(.el-sub-menu__title) {
  background-color: rgba(255, 255, 255, 0.1) !important;
}

/* 激活菜单项样式 */
.el-menu-item.is-active {
  background-color: rgba(255, 255, 255, 0.2) !important;
  color: #1e40af;
}

/* 子菜单标题样式 */
:deep(.el-sub-menu__title) {
  z-index: 1000;
  position: relative;
  transition: all 0.3s ease;
}

/* 弹出菜单样式 */
:deep(.el-menu--popup) {
  z-index: 1001;
  background-color: var(--menu-bg) !important;
  border: 1px solid var(--border-color);
  border-radius: 8px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.12);
  padding: 6px 0;
}

/* 图标样式 */
.el-icon {
  margin-right: 12px;
  font-size: 18px;
  width: 24px;
  height: 24px;
  display: flex;
  align-items: center;
  justify-content: center;
}

/* 菜单文字样式 */
span {
  font-size: 14px;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
  color: inherit;
}

/* 子菜单箭头动画 */
:deep(.el-sub-menu__icon-arrow) {
  transition: transform 0.3s ease;
}
.el-sub-menu.is-opened :deep(.el-sub-menu__icon-arrow) {
  transform: rotate(180deg);
}

/* 添加分隔线效果 */
.el-menu-item:not(:last-child),
.el-sub-menu:not(:last-child) {
  border-bottom: 1px dashed var(--border-color);
}

/* 添加菜单项进入动画 */
@keyframes slideIn {
  from {
    opacity: 0;
    transform: translateX(-10px);
  }
  to {
    opacity: 1;
    transform: translateX(0);
  }
}

.el-menu-item {
  animation: slideIn 0.3s ease forwards;
}

/* 为不同层级的菜单项设置延迟动画 */
.el-menu-item:nth-child(1) { animation-delay: 0.1s; }
.el-menu-item:nth-child(2) { animation-delay: 0.2s; }
.el-menu-item:nth-child(3) { animation-delay: 0.3s; }
/* 可根据实际菜单项数量继续添加 */

/* 悬停时图标放大效果 */
.el-menu-item:hover .el-icon,
.el-sub-menu:hover .el-icon {
  transform: scale(1.1);
}

/* 激活菜单项的下划线效果 */
.el-menu-item.is-active::after {
  content: '';
  position: absolute;
  left: 0;
  bottom: 0;
  width: 3px;
  height: 100%;
  background-color: var(--active-text);
  border-radius: 3px;
}

.el-tooltip {
  display: flex;
  align-items: center;
  width: 100%;
}

.el-menu-item,
:deep(.el-sub-menu__title) {
  display: flex;
  align-items: center;
  padding: 0 16px !important;
  height: 50px;
  line-height: 50px;
  margin: 4px 8px;
}
</style>