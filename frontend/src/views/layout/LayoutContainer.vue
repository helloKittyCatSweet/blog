<script setup>
import { Expand, Fold } from "@element-plus/icons-vue";
import {
  UserFilled,
  User,
  Crop,
  EditPen,
  SwitchButton,
  CaretBottom,
  MessageBox,
  DataAnalysis,
  MagicStick,
  Comment,
  Bell,
  Files,
  List,
  Notebook,
  Edit,
  Document,
  StarFilled,
  Avatar,
  Connection,
  Setting,
  Key,
  CollectionTag,
  Orange,
  SetUp,
  HelpFilled,
  InfoFilled,
} from "@element-plus/icons-vue";

import avatar from "@/assets/default.png";
import { useUserStore } from "@/stores";
import { markRaw, onMounted } from "vue";
import router from "@/router";
import {
  COMMENT_MANAGE_PATH,
  FAVORITE_MANAGE_PATH,
  MESSAGE_MANAGE_PATH,
  PERMISSION_MANAGE_PATH,
  POST_MANAGE_PATH,
  POST_LIST_PATH,
  POST_VERSION_MANAGE_PATH,
  POST_EDIT_PATH,
  POST_ATTACHMENT_LIST_PATH,
  REPORT_MANAGE_PATH,
  ROLE_MANAGE_PATH,
  TAG_MANAGE_PATH,
  USER_MANAGE,
  USER_BEHAVIOR_MANAGE_PATH,
  USER_PROFILE_PATH,
  USER_AVATAR_PATH,
  USER_PASSWORD_PATH,
  USER_SETTING_PATH,
  SYSTEM_MANAGE_PATH,
  CATEGORY_MANAGE_PATH,
  ROLE_PERMISSION_MANAGE_PATH,
  ROLE_USER_MANAGE_PATH,
  CONTROL_PANEL_INDEX_PATH,
} from "@/constants/routes-constants.js";
import { ref } from "vue";
import logo from "@/assets/logo.png";
import logoCherry from "@/assets/logo_cherry.png";

const userStore = useUserStore();
onMounted(() => {
  userStore.user;
});

const handleCommand = async (key) => {
  if (key === "logout") {
    // 退出操作
    await ElMessageBox.confirm("你确认要进行退出么", "温馨提示", {
      type: "warning",
      confirmButtonText: "确认",
      cancelButtonText: "取消",
    });
    // 清除本地的数据（token + user信息）
    userStore.removeToken();
    userStore.setUser({});
    router.push("/login");
  } else {
    // 跳转操作
    router.push(`/user/${key}`);
  }
};

const isCollapse = ref(true);
const ExpandIcon = markRaw(Expand);
const FoldIcon = markRaw(Fold);
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
      <el-aside width="200px" class="sidebar-container">
        <!-- 侧边栏头部区域 -->
        <div class="sidebar-header">
          <div :class="!isCollapse ? 'logo-container' : 'logo-container collapsed'">
            <img
              :src="!isCollapse ? logo : logoCherry"
              :alt="!isCollapse ? 'Logo' : 'Collapsed Logo'"
              class="logo-image"
            />
            <el-tooltip :content="isCollapse ? '展开菜单' : '折叠菜单'" placement="right">
              <el-button
                :icon="isCollapse ? ExpandIcon : FoldIcon"
                circle
                @click="isCollapse = !isCollapse"
                class="toggle-btn"
              />
            </el-tooltip>
          </div>
        </div>
        <!-- 菜单部分 - 添加no-scroll类防止滚动 -->
        <el-menu
          class="no-scroll"
          :default-active="$route.path"
          active-text-color="#fff"
          router
          :collapse="isCollapse"
        >
          <!-- 用户登录进来的首页 -->
          <el-menu-item :index="CONTROL_PANEL_INDEX_PATH">
            <el-icon><DataAnalysis /></el-icon>
            <span>首页</span>
          </el-menu-item>
          <!-- 用户行为管理 -->
          <el-menu-item :index="USER_BEHAVIOR_MANAGE_PATH">
            <el-icon><MagicStick /></el-icon>
            <span>用户行为管理</span>
          </el-menu-item>
          <!-- 消息管理 -->
          <el-menu-item :index="MESSAGE_MANAGE_PATH">
            <el-icon><Comment /></el-icon>
            <span>消息管理</span>
          </el-menu-item>
          <!-- 举报管理 -->
          <el-menu-item :index="REPORT_MANAGE_PATH">
            <el-icon><Bell /></el-icon>
            <span>举报管理</span>
          </el-menu-item>

          <!-- 文章管理 -->
          <el-sub-menu :index="POST_MANAGE_PATH">
            <!-- 多级菜单的标题  具名插槽 title-->
            <template #title>
              <el-icon><Files /></el-icon>
              <span>文章管理</span>
            </template>

            <!-- 展开的内容 默认插槽-->
            <!-- 文章列表 -->
            <el-menu-item :index="POST_LIST_PATH">
              <el-icon><List /></el-icon>
              <span>文章列表</span>
            </el-menu-item>
            <!-- 文章版本管理 -->
            <el-menu-item :index="POST_VERSION_MANAGE_PATH">
              <el-icon><Notebook /></el-icon>
              <span>文章版本管理</span>
            </el-menu-item>
            <!-- 修改文章 -->
            <el-menu-item :index="POST_EDIT_PATH">
              <el-icon><Edit /></el-icon>
              <span>修改文章</span>
            </el-menu-item>
            <!-- 文章附件管理 -->
            <el-menu-item :index="POST_ATTACHMENT_LIST_PATH">
              <el-icon><Document /></el-icon>
              <span>文章附件管理</span>
            </el-menu-item>
            <!-- 评论管理 -->
            <el-menu-item :index="COMMENT_MANAGE_PATH">
              <el-icon><MessageBox /></el-icon>
              <span>评论管理</span>
            </el-menu-item>
          </el-sub-menu>

          <!-- 个人信息设置 -->
          <el-sub-menu :index="USER_MANAGE">
            <template #title>
              <el-icon><User /></el-icon>
              <span>我的</span>
            </template>

            <!-- 收藏夹管理 -->
            <el-menu-item :index="FAVORITE_MANAGE_PATH">
              <el-icon><StarFilled /></el-icon>
              <span>收藏夹管理</span>
            </el-menu-item>
            <!-- 用户信息管理 -->
            <el-menu-item :index="USER_PROFILE_PATH">
              <el-icon><Edit /></el-icon>
              <span>用户信息管理</span>
            </el-menu-item>
            <!-- 用户头像管理 -->
            <el-menu-item :index="USER_AVATAR_PATH">
              <el-icon><Avatar /></el-icon>
              <span>用户头像管理</span>
            </el-menu-item>
            <!-- 用户重置密码 -->
            <el-menu-item :index="USER_PASSWORD_PATH">
              <el-icon><Key /></el-icon>
              <span>用户重置密码</span>
            </el-menu-item>
            <!-- 用户设置 -->
            <el-menu-item :index="USER_SETTING_PATH">
              <el-icon><Connection /></el-icon>
              <span>用户设置</span>
            </el-menu-item>
          </el-sub-menu>

          <!-- 系统设置 -->
          <el-sub-menu :index="SYSTEM_MANAGE_PATH">
            <template #title>
              <el-icon><Setting /></el-icon>
              <span>系统设置</span>
            </template>
            <!-- 分类管理 -->
            <el-menu-item :index="CATEGORY_MANAGE_PATH">
              <el-icon><Orange /></el-icon>
              <span>分类管理</span>
            </el-menu-item>
            <!-- 标签管理 -->
            <el-menu-item :index="TAG_MANAGE_PATH">
              <el-icon><CollectionTag /></el-icon>
              <span>标签管理</span>
            </el-menu-item>
          </el-sub-menu>

          <!-- 用户设置 -->
          <el-sub-menu :index="ROLE_MANAGE_PATH">
            <!-- 多级菜单的标题  具名插槽 title-->
            <template #title>
              <el-icon>
                <UserFilled />
              </el-icon>
              <span>用户设置</span>
            </template>
            <!-- 角色管理 -->
            <el-menu-item :index="ROLE_MANAGE_PATH">
              <el-icon>
                <User />
              </el-icon>
              <span>角色管理</span>
            </el-menu-item>
            <!-- 角色权限映射管理 -->
            <el-menu-item :index="ROLE_PERMISSION_MANAGE_PATH">
              <el-icon><SetUp /></el-icon>
              <span>角色权限映射管理</span>
            </el-menu-item>
            <!-- 用户角色管理 -->
            <el-menu-item :index="ROLE_USER_MANAGE_PATH">
              <el-icon><HelpFilled /></el-icon>
              <span>用户角色管理</span>
            </el-menu-item>
            <!-- 权限管理 -->
            <el-menu-item :index="PERMISSION_MANAGE_PATH">
              <el-icon><InfoFilled /></el-icon>
              <span>权限管理</span>
            </el-menu-item>
          </el-sub-menu>
        </el-menu>
      </el-aside>

      <el-container>
        <el-header>
          <div>
            <strong>
              {{ userStore.username }}
            </strong>
          </div>

          <!-- 展示给用户默认看到的 -->
          <el-dropdown placement="bottom-end" @command="handleCommand">
            <span class="el-dropdown__box">
              <el-avatar :src="userStore.avatar || avatar" />
              <el-icon>
                <CaretBottom />
              </el-icon>
            </span>
            <!-- 折叠的下拉部分 -->
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item command="profile" :icon="User"
                  >基本资料</el-dropdown-item
                >
                <el-dropdown-item command="avatar" :icon="Crop"
                  >更换头像</el-dropdown-item
                >
                <el-dropdown-item command="password" :icon="EditPen"
                  >重置密码</el-dropdown-item
                >
                <el-dropdown-item command="logout" :icon="SwitchButton"
                  >退出登录</el-dropdown-item
                >
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </el-header>
        <el-main>
          <router-view></router-view>
        </el-main>
        <el-footer>FreeShare ©2025 Created by helloKittyCatSweet</el-footer>
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
    background-color: hwb(220 5% 5% / 0.475);
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
      background-color: transparent;
      transition: all 0.3s ease;
      overflow: visible; // 菜单不单独滚动
      scrollbar-width: none; // 隐藏侧边栏滚动条

      .el-menu-item,
      .el-sub-menu__title {
        height: 48px;
        line-height: 48px;
        color: #000000;
        text-align: center;
        border-radius: 6px;

        &:hover {
          background-color: rgba(255, 255, 255, 0.9);
          color: #000;
          transform: translateX(4px);
        }

        &.is-active {
          background-color: #4a90e2;
          color: #fff;
        }

        .el-icon {
          width: 24px;
          height: 24px;
          font-size: 24px;
        }

        &::-webkit-scrollbar {
          display: none;
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
      background-color: #fff;
      display: flex;
      align-items: center;
      justify-content: space-between;
      position: sticky;
      top: 0;
      z-index: 10;
      flex-shrink: 0;

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
</style>
