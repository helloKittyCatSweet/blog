<script setup>
import {
  Expand,
  Fold,
} from "@element-plus/icons-vue";

import { useUserStore } from "@/stores";
import { markRaw, onMounted } from "vue";
import router from "@/router";

import { ref, computed } from "vue";
import logo from "@/assets/logo.png";
import logoCherry from "@/assets/logo_cherry.png";

import MenuRenderer from "@/components/MenuRenderer.vue";
import { generateMenus } from "@/utils/menu";
import { ROLES } from "@/constants/role-constants";

import UserDropdown from "@/components/user/UserDropdown.vue";
import { USER_PROFILE_PATH, USER_PASSWORD_PATH } from "@/constants/routes/user";
import AppFooter from "@/components/layout/AppFooter.vue";

const userStore = useUserStore();
onMounted(() => {
  userStore.user;
});

// 控制菜单伸缩
const isCollapse = ref(true);
const ExpandIcon = markRaw(Expand);
const FoldIcon = markRaw(Fold);

// 计算菜单配置项
const menus = computed(() => {
  return generateMenus(userStore);
});

// 处理用户下拉菜单命令
const handleUserCommand = async (command) => {
  switch (command) {
    case "profile":
      router.push(USER_PROFILE_PATH);
      break;
    case "password":
      router.push(USER_PASSWORD_PATH);
      break;
    case "logout":
      try {
        await ElMessageBox.confirm("你确认要进行退出么", "温馨提示", {
          type: "warning",
          confirmButtonText: "确认",
          cancelButtonText: "取消",
        });
        userStore.removeToken();
        userStore.setUser({});
        router.push("/login");
      } catch {
        // 用户取消操作
      }
      break;
  }
};
</script>

<template>
  <!--
    el-menu 整个菜单组件
      active-text-color: 在被激活时的颜色
      :default-active="$route.path" 默认高亮的菜单项
      router                        router选项开启，el-menu-item的index就是点击跳转的路径

    el-menu-item菜单项
     index="/article/channel"配置的是访问的跳转路径，配合default-active的值实现高亮
  -->
  <div class="layout-container">
    <!-- 主包装器 -->
    <div class="main-wrapper">
      <el-aside :width="isCollapse ? '64px' : '200px'" class="sidebar-container">
        <!-- 侧边栏头部区域 -->
        <div class="sidebar-header">
          <div :class="!isCollapse ? 'logo-container' : 'logo-container collapsed'" @click="router.push('/')"
            style="cursor: pointer">
            <img :src="!isCollapse ? logo : logoCherry" :alt="!isCollapse ? 'Logo' : 'Collapsed Logo'"
              class="logo-image" />
            <el-tooltip :content="isCollapse ? '展开菜单' : '折叠菜单'" placement="right">
              <el-button :icon="isCollapse ? ExpandIcon : FoldIcon" circle @click.stop="isCollapse = !isCollapse"
                class="toggle-btn" />
            </el-tooltip>
          </div>
        </div>
        <!-- 菜单部分 - 添加no-scroll类防止滚动 -->
        <el-menu 
          class="no-scroll" 
          :default-active="$route.path" 
          :collapse="isCollapse" 
          :collapse-transition="true" 
          text-color="#1e40af"
          active-text-color="#1e40af"
          background-color="transparent"
          :unique-opened="true"
          router>
          <menu-renderer :menu-config="menus" :is-collapsed="isCollapse" />
        </el-menu>
      </el-aside>

      <el-container>
        <el-header>
          <div class="header-right">
            <UserDropdown @command="handleUserCommand" />
          </div>
        </el-header>
        <el-main>
          <router-view></router-view>
        </el-main>
        <AppFooter />
      </el-container>
    </div>
  </div>
</template>

<style lang="scss" scoped>
.layout-container {
  height: 100vh;
  display: flex;
  overflow-y: auto; // 最外层容器控制整体滚动
  flex-direction: column;

  /* 隐藏所有滚动条 */
  ::-webkit-scrollbar {
    width: 0 !important;
    height: 0 !important;
  }

  // 主包装器
  .main-wrapper {
    display: flex;
    flex: 1;
    overflow-y: auto;
    scrollbar-width: none; // Firefox
    -ms-overflow-style: none; // IE 10+

    &::-webkit-scrollbar {
      display: none; // Chrome and Safari
    }
  }

  .el-aside {
    display: flex;
    flex-direction: column;
    width: auto;
    height: auto; // 高度由内容撑开
    min-height: 100vh; // 至少填满屏幕高度
    position: relative;
    /* 添加相对定位 */
    z-index: 1000;
    /* 提高层级 */
    background-color: rgba(119, 95, 255, 0.5);
    transition: width 0.3s ease;
    box-shadow: 2px 0 10px rgba(0, 0, 0, 0.1);
    min-height: 100%; // 至少填满屏幕高度

    .sidebar-header {
      padding: 16px;
      flex-shrink: 0;
      display: flex;
      justify-content: space-between;
      align-items: center;
      border-bottom: 1px solid rgba(0, 0, 0, 0.1);

      .logo-container {
        display: flex;
        align-items: center;
        justify-content: space-between;
        height: 48px;
        transition: all 0.3s ease;

        .logo-image {
          width: auto;
          height: 100%;
          max-width: 160px;
          object-fit: contain;
          transition: all 0.3s ease;
        }

        .toggle-btn {
          width: 32px;
          height: 32px;
          flex-shrink: 0;
          background-color: rgba(255, 255, 255, 0.2);
          color: #fff;
          border: none;
          transition: all 0.3s ease;

          &:hover {
            background-color: rgba(255, 255, 255, 0.3);
            transform: scale(1.1);
          }
        }
      }

      .collapsed {
        .logo-image {
          width: auto;
          height: 24px;
          max-width: 160px;
          object-fit: contain;
          transition: all 0.3s ease;
        }
      }
    }

    .el-menu {
      flex: 1;
      border-right: none;
      background-color: transparent !important;
      transition: all 0.3s ease;
      overflow: visible;
      scrollbar-width: none;
      position: relative;
      z-index: 1001;

      &.el-menu--collapse {
        width: 64px;
      }

      :deep(.el-menu--popup) {
        background-color: rgba(119, 95, 255, 0.5) !important;
        min-width: 200px;
        padding: 4px 0;
        border: none;
        border-radius: 4px;
        box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);

        .el-menu-item {
          height: 40px;
          line-height: 40px;
          padding: 0 16px;
          margin: 4px;
          color: #1e40af;

          .el-icon {
            margin-right: 8px;
            color: #1e40af;
          }

          span {
            color: #1e40af;
          }

          &:hover {
            background-color: rgba(255, 255, 255, 0.2);
          }

          &.is-active {
            background-color: rgba(255, 255, 255, 0.3);
            font-weight: bold;
          }
        }
      }

      .el-menu-item,
      .el-sub-menu__title {
        height: 48px;
        line-height: 48px;
        color: #1e40af;
        text-align: left;
        border-radius: 6px;
        font-weight: 500;
        display: flex;
        align-items: center;

        .el-icon {
          color: #1e40af;
        }

        &:hover {
          background-color: rgba(255, 255, 255, 0.2);
        }

        &.is-active {
          background-color: rgba(255, 255, 255, 0.3);
          font-weight: bold;
          border-right: 3px solid #1e40af;
        }
      }
    }

    .el-menu--collapse {
      width: auto;

      .el-menu-item,
      .el-sub-menu__title {
        padding: 0 12px !important;
      }
    }
  }

  .el-container {
    flex: 1;
    display: flex;
    flex-direction: column;
    min-height: 100%; // 至少填满屏幕高度
    overflow: visible; // 不单独滚动

    .el-header {
      height: 60px;
      padding: 0 20px;
      background-color: #fff;
      border-bottom: 1px solid #eee;
      display: flex;
      align-items: center;
      justify-content: flex-end;
      position: sticky;
      top: 0;
      z-index: 100;
      box-shadow: 0 2px 4px rgba(0, 0, 0, 0.05);

      .header-right {
        display: flex;
        align-items: center;
        gap: 16px;
      }

      .el-dropdown__box {
        display: flex;
        align-items: center;

        .el-icon {
          color: #999;
          margin-left: 10px;
        }

        &:active,
        &:focus {
          outline: none;
        }
      }
    }

    .el-main {
      flex: 1;
      overflow: visible; // 主内容不单独滚动
    }

    .el-footer {
      display: flex;
      align-items: center;
      justify-content: center;
      font-size: 14px;
      color: #666;
      flex-shrink: 0;
    }
  }
}

:deep(.el-tooltip__popper) {
  background-color: rgba(119, 95, 255, 0.9) !important;
  color: #1e40af !important;
  font-weight: 500;
  padding: 8px 12px;
  border-radius: 4px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
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
