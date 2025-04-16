<script setup>
import { ref, onMounted } from 'vue';
import { useRouter } from 'vue-router';
import { useUserStore } from '@/stores/modules/user';
import { useMessageStore } from '@/stores/modules/message';
import { ElMessage } from 'element-plus';
import {
  getFollowingList,
  getFollowersList,
  followUser,
  unfollowUser
} from '@/api/user/userFollow';
import { USER_MESSAGE_DETAIL_PATH } from '@/constants/routes-constants';

const router = useRouter();
const userStore = useUserStore();
const messageStore = useMessageStore();

const activeTab = ref('following');
const followingList = ref([]);
const followersList = ref([]);

// 加载数据
const loadData = async () => {
  try {
    const [followingRes, followersRes] = await Promise.all([
      getFollowingList(userStore.user.id),
      getFollowersList(userStore.user.id)
    ]);

    if (followingRes.data?.status === 200) {
      followingList.value = followingRes.data.data;
    }
    if (followersRes.data?.status === 200) {
      followersList.value = followersRes.data.data;
    }
  } catch (error) {
    ElMessage.error('加载数据失败');
  }
};

// 检查是否已关注
const isFollowing = (userId) => {
  return followingList.value.some(user => user.userId === userId);
};

// 处理关注
const handleFollow = async (user) => {
  try {
    await followUser(user.userId);
    ElMessage.success('关注成功');
    await loadData();
  } catch (error) {
    ElMessage.error('关注失败');
  }
};

// 处理取消关注
const handleUnfollow = async (user) => {
  try {
    await unfollowUser(user.userId);
    ElMessage.success('取消关注成功');
    await loadData();
  } catch (error) {
    ElMessage.error('取消关注失败');
  }
};

// 跳转到聊天
const goToChat = (user) => {
  messageStore.setCurrentReceiver({
    receiverId: user.userId,
    receiverName: user.nickname || user.username,
    receiverAvatar: user.avatar
  });

  router.push({
    path: USER_MESSAGE_DETAIL_PATH,
    query: { receiverId: user.userId }
  });
};

onMounted(() => {
  loadData();
});
</script>

<template>
  <page-container>
    <el-tabs v-model="activeTab">
      <el-tab-pane label="我的关注" name="following">
        <el-table :data="followingList" style="width: 100%">
          <el-table-column prop="username" label="用户名" />
          <el-table-column prop="nickname" label="昵称" />
          <el-table-column label="操作" width="200">
            <template #default="{ row }">
              <el-button type="primary" size="small" @click="goToChat(row)">
                发消息
              </el-button>
              <el-button type="danger" size="small" @click="handleUnfollow(row)">
                取消关注
              </el-button>
            </template>
          </el-table-column>
        </el-table>
      </el-tab-pane>

      <el-tab-pane label="我的粉丝" name="followers">
        <el-table :data="followersList" style="width: 100%">
          <el-table-column prop="username" label="用户名" />
          <el-table-column prop="nickname" label="昵称" />
          <el-table-column label="操作" width="200">
            <template #default="{ row }">
              <el-button type="primary" size="small" @click="goToChat(row)">
                发消息
              </el-button>
              <el-button
                type="success"
                size="small"
                @click="handleFollow(row)"
                v-if="!isFollowing(row.userId)"
              >
                关注
              </el-button>
            </template>
          </el-table-column>
        </el-table>
      </el-tab-pane>
    </el-tabs>
  </page-container>
</template>

<style scoped>
.el-table {
  margin-top: 20px;
}
</style>