<script setup>
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
        <el-icon><component :is="menu.icon" /></el-icon>
        <span>{{ menu.title }}</span>
      </template>
      <!-- 递归渲染子菜单 -->
      <menu-renderer :menu-config="menu.children" :is-collapsed="isCollapsed" />
    </el-sub-menu>

    <!-- 普通菜单项 -->
    <el-menu-item v-else :index="getRouteValue(menu.index)">
      <el-icon><component :is="menu.icon" /></el-icon>
      <template #title>
        <span>{{ menu.title }}</span>
      </template>
    </el-menu-item>
  </template>
</template>

<style scoped>
/* 基础变量定义 */
:root {
  --menu-bg: transparent;
  --hover-bg: rgba(255, 255, 255, 0.2);
  --active-bg: rgba(255, 255, 255, 0.3);
  --text-color: #1e40af;
  --hover-text: #1e40af;
  --active-text: #1e40af;
  --border-color: #e6e6e6;
}

.el-menu-item,
.el-sub-menu {
  background-color: transparent !important;
}

.el-menu-item,
:deep(.el-sub-menu__title) {
  color: var(--text-color) !important;
  display: flex;
  align-items: center;
  padding: 0 16px !important;
  height: 50px;
  line-height: 50px;
  margin: 4px 8px;

  .el-icon {
    color: var(--text-color);
    margin-right: 8px;
  }

  span {
    color: var(--text-color) !important;
    margin-left: 4px;
    display: inline-block !important;
  }
}

/* 菜单项悬停效果 */
.el-menu-item:hover,
.el-sub-menu:hover :deep(.el-sub-menu__title) {
  background-color: var(--hover-bg) !important;
}

/* 激活菜单项样式 */
.el-menu-item.is-active {
  background-color: var(--active-bg) !important;
  color: var(--active-text);
}

/* 子菜单标题样式 */
:deep(.el-sub-menu__title) {
  background-color: transparent !important;
}

/* 图标样式 */
.el-icon {
  font-size: 18px;
  width: 24px;
  height: 24px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: var(--text-color);
}

/* 菜单文字样式 */
.el-menu-item span,
:deep(.el-sub-menu__title span) {
  font-size: 14px;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
  color: var(--text-color) !important;
  display: inline-block !important;
}

/* 确保折叠时tooltip正常显示 */
.el-tooltip {
  display: flex;
  align-items: center;
  width: 100%;
}

/* 子菜单箭头样式 */
:deep(.el-sub-menu__icon-arrow) {
  color: var(--text-color);
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

/* 激活状态的颜色 */
.el-menu-item.is-active {
  .el-icon,
  span {
    color: var(--active-text) !important;
  }
}

/* 弹出菜单样式 */
:deep(.el-menu--popup) {
  background-color: rgba(119, 95, 255, 0.5) !important;
  min-width: 200px;
  padding: 4px 0;

  .el-menu-item {
    padding: 0 16px !important;
    margin: 4px !important;
    color: var(--text-color) !important;

    span {
      color: var(--text-color) !important;
    }

    &:hover {
      background-color: var(--hover-bg) !important;
    }

    &.is-active {
      background-color: var(--active-bg) !important;
      color: var(--active-text) !important;

      span {
        color: var(--active-text) !important;
      }
    }
  }
}

:deep(.el-menu--popup-right-start) {
  margin-left: 1px !important;
}

:deep(.el-popper.is-light) {
  border: none !important;
  
  .el-popper__arrow::before {
    background: rgba(119, 95, 255, 0.5) !important;
    border: none !important;
  }
}

/* 折叠状态下的子菜单样式 */
:deep(.el-menu--collapse) {
  .el-sub-menu.is-opened > .el-menu--popup {
    display: block !important;
  }

  .el-menu-item, 
  .el-sub-menu__title {
    span {
      display: none;
    }
  }
}

/* 折叠状态下的子菜单容器样式 */
:deep(.el-menu--collapse) {
  .el-sub-menu {
    &.is-opened {
      > .el-menu--popup {
        display: block !important;
      }
    }
  }
}

:deep(.el-popper.is-light) {
  border: none !important;
  background: rgba(119, 95, 255, 0.5) !important;
  
  .el-popper__arrow::before {
    background: rgba(119, 95, 255, 0.5) !important;
    border: none !important;
  }
}
</style>