<script setup>
import { ref, onMounted } from "vue";
import { useRoute } from "vue-router";
import { ElMessage } from "element-plus";
import PageContainer from "@/components/PageContainer.vue";
import { findUserById } from "@/api/user/user.js";
import { Clock } from "@element-plus/icons-vue";
import { GENDER_OPTIONS } from "@/constants/user-constants";

const route = useRoute();
const userInfo = ref({
  username: "",
  nickname: "",
  avatar: null,
  gender: 2,
  birthday: "",
  address: "",
  introduction: "",
  tags: [],
  signature: "",
  createdAt: "",
});

const loadUserInfo = async () => {
  try {
    const userId = route.params.id;
    const { data } = await findUserById(userId);
    if (data.status === 200) {
      Object.assign(userInfo.value, {
        ...data.data,
        tags: data.data.tags || [],
      });
    }
  } catch (error) {
    ElMessage.error("加载用户数据失败");
  }
};

onMounted(() => {
  loadUserInfo();
});
</script>

<template>
  <page-container>
    <el-row :gutter="40">
      <el-col :span="16">
        <el-card class="user-detail-card" shadow="never">
          <div class="user-header">
            <div class="user-avatar">
              <el-avatar
                :size="120"
                :src="userInfo.avatar"
                fit="cover"
              >
                {{ userInfo.nickname?.charAt(0) || userInfo.username?.charAt(0) }}
              </el-avatar>
            </div>
            <div class="user-basic-info">
              <h2 class="nickname">{{ userInfo.nickname || userInfo.username }}</h2>
              <p class="introduction">{{ userInfo.introduction || '这个用户很懒，还没有写简介' }}</p>
              <div class="tags-container">
                <el-tag
                  v-for="tag in userInfo.tags"
                  :key="tag"
                  class="user-tag"
                  effect="plain"
                >
                  {{ tag }}
                </el-tag>
              </div>
            </div>
          </div>

          <el-divider />

          <div class="user-details">
            <el-descriptions :column="2" border>
              <el-descriptions-item label="性别">
                {{ GENDER_OPTIONS.find(opt => opt.value === userInfo.gender)?.label || '保密' }}
              </el-descriptions-item>
              <el-descriptions-item label="居住地">
                {{ userInfo.address || '未设置' }}
              </el-descriptions-item>
              <el-descriptions-item label="注册时间">
                {{ userInfo.createdAt || '-' }}
              </el-descriptions-item>
            </el-descriptions>
          </div>
        </el-card>
      </el-col>

      <el-col :span="8">
        <el-card class="signature-card" shadow="never">
          <template #header>
            <div class="signature-header">
              <span>个人签名</span>
            </div>
          </template>
          <div class="signature-content" v-if="userInfo.signature">
            <img :src="userInfo.signature" alt="个人签名" class="signature-image" />
          </div>
          <div class="signature-placeholder" v-else>
            <span class="text-muted">暂无签名</span>
          </div>
        </el-card>
      </el-col>
    </el-row>
  </page-container>
</template>

<style lang="scss" scoped>
.user-detail-card {
  background-color: var(--el-bg-color);
  border: none;

  .user-header {
    display: flex;
    gap: 24px;
    padding: 20px 0;

    .user-basic-info {
      flex: 1;

      .nickname {
        margin: 0 0 12px;
        font-size: 24px;
        font-weight: 600;
        color: var(--el-text-color-primary);
      }

      .introduction {
        margin: 0 0 16px;
        color: var(--el-text-color-regular);
        font-size: 14px;
        line-height: 1.6;
      }
    }
  }

  .tags-container {
    display: flex;
    flex-wrap: wrap;
    gap: 8px;
    margin-top: 12px;

    .user-tag {
      margin-right: 8px;
    }
  }

  .user-details {
    margin-top: 24px;
  }
}

.signature-card {
  background-color: var(--el-bg-color);
  border: none;

  .signature-header {
    font-weight: 500;
    color: var(--el-text-color-primary);
  }

  .signature-content {
    display: flex;
    justify-content: center;
    align-items: center;
    min-height: 100px;

    .signature-image {
      max-width: 100%;
      max-height: 100px;
      object-fit: contain;
    }
  }

  .signature-placeholder {
    display: flex;
    justify-content: center;
    align-items: center;
    min-height: 100px;
    color: var(--el-text-color-secondary);
    background-color: var(--el-fill-color-lighter);
    border-radius: 4px;
  }
}

.text-muted {
  color: var(--el-text-color-secondary);
}
</style>