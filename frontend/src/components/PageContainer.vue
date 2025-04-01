<script setup>
import { ArrowLeft, RefreshRight, More } from "@element-plus/icons-vue";
import { useRoute, useRouter } from "vue-router";
import { computed } from "vue";
import BreadCrumb from "@/components/BreadCrumb.vue";

const route = useRoute();
const router = useRouter();

defineProps({
  showBack: {
    type: Boolean,
    default: false,
  },
  compact: {
    type: Boolean,
    default: false,
  },
  showRefresh: {
    type: Boolean,
    default: true,
  },
  pageTitle: {
    type: String,
    default: "",
  },
});

const emit = defineEmits(["back", "refresh"]);

// 默认刷新方法
const handleRefresh = () => {
  // 触发自定义刷新事件，如果父组件没有监听，则执行默认刷新
  if (!emit("refresh")) {
    // 刷新当前页面
    window.location.reload();
  }
};

// 自动生成更友好的面包屑
const breadcrumbs = computed(() => {
  const matched = route.matched.filter((item) => item.meta?.title);
  return matched.length > 0 ? matched : [{ path: "/", meta: { title: "首页" } }];
});
</script>

<template>
  <div class="admin-container">
    <!-- 增强型头部区域 -->
    <div class="admin-header">
      <div class="header-left">
        <el-button v-if="showBack" class="back-btn" link @click="emit('back')">
          <el-icon><ArrowLeft /></el-icon>
          <span class="back-text">返回</span>
        </el-button>

        <!-- 增强的面包屑导航 -->
        <BreadCrumb />

        <!-- 页面标题（可选） -->
        <h2 v-if="pageTitle" class="page-title">{{ pageTitle }}</h2>
      </div>

      <div class="header-actions">
        <!-- 刷新按钮 -->
        <el-tooltip v-if="showRefresh" content="刷新" placement="top">
          <el-button circle plain @click="handleRefresh">
            <el-icon><RefreshRight /></el-icon>
          </el-button>
        </el-tooltip>

        <!-- 更多操作下拉菜单 -->
        <el-dropdown v-if="$slots['more-actions']" trigger="click">
          <el-button circle plain>
            <el-icon><More /></el-icon>
          </el-button>
          <template #dropdown>
            <slot name="more-actions"></slot>
          </template>
        </el-dropdown>

        <!-- 主操作按钮组 -->
        <slot name="actions"></slot>
      </div>
    </div>

    <!-- 增强的内容区域 -->
    <el-card
      class="admin-content"
      :class="{ compact }"
      :body-style="{ padding: compact ? '16px' : '24px' }"
    >
      <!-- 内容顶部插槽 -->
      <div v-if="$slots['content-header']" class="content-header">
        <slot name="content-header"></slot>
      </div>

      <!-- 主要内容 -->
      <div class="content-main">
        <slot></slot>
      </div>

      <!-- 内容底部插槽 -->
      <div v-if="$slots['content-footer']" class="content-footer">
        <slot name="content-footer"></slot>
      </div>
    </el-card>

    <!-- 增强的底部操作栏 -->
    <div v-if="$slots.footer" class="admin-footer">
      <div class="footer-container">
        <slot name="footer"></slot>
      </div>
    </div>
  </div>
</template>

<style lang="scss" scoped>
.admin-container {
  height: 100%;
  padding: 16px 24px;
  background-color: var(--el-bg-color-page);
  display: flex;
  flex-direction: column;
  box-sizing: border-box;
}

.admin-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
  padding: 8px 0;
  position: relative;

  .header-left {
    display: flex;
    align-items: center;
    gap: 12px;
    flex-wrap: wrap;
    row-gap: 8px;

    .back-btn {
      font-size: 16px;
      padding: 0 8px 0 0;
      color: var(--el-text-color-secondary);
      transition: all 0.3s;

      &:hover {
        color: var(--el-color-primary);
      }

      .back-text {
        margin-left: 4px;
        font-size: 14px;
      }
    }

    .breadcrumb {
      :deep(.el-breadcrumb__inner) {
        font-weight: normal;
        color: var(--el-text-color-secondary);

        &.is-link {
          color: var(--el-text-color-regular);
          &:hover {
            color: var(--el-color-primary);
          }
        }
      }
    }

    .page-title {
      margin: 0 0 0 12px;
      font-size: 18px;
      font-weight: 600;
      color: var(--el-text-color-primary);
    }
  }

  .header-actions {
    display: flex;
    align-items: center;
    gap: 12px;
    flex-wrap: wrap;
    justify-content: flex-end;

    .el-button {
      transition: all 0.3s;
    }
  }
}

.admin-content {
  flex: 1;
  border-radius: 8px;
  box-shadow: var(--el-box-shadow-light);
  border: none;
  margin-bottom: 16px;
  display: flex;
  flex-direction: column;
  overflow: hidden;

  .content-header {
    margin-bottom: 16px;
    padding-bottom: 12px;
    border-bottom: 1px solid var(--el-border-color-light);
  }

  .content-main {
    flex: 1;
    min-height: 0;
    overflow: auto;
  }

  .content-footer {
    margin-top: 16px;
    padding-top: 12px;
    border-top: 1px dashed var(--el-border-color-light);
  }

  &.compact {
    .content-header,
    .content-footer {
      margin: 0;
      padding: 8px 0;
    }
  }
}

.admin-footer {
  background-color: var(--el-bg-color);
  border-radius: 8px;
  padding: 12px 16px;
  box-shadow: var(--el-box-shadow-light);

  .footer-container {
    display: flex;
    justify-content: space-between;
    align-items: center;
    gap: 16px;
  }
}

/* 响应式调整 */
@media (max-width: 768px) {
  .admin-container {
    padding: 12px;
  }

  .admin-header {
    flex-direction: column;
    align-items: flex-start;
    gap: 12px;

    .header-actions {
      width: 100%;
      justify-content: flex-start;
    }
  }
}
</style>
