<script setup>
import { ref, onMounted, computed } from "vue";
import CategorySelect from "@/components/category/CategorySelect.vue";
import { Search } from "@element-plus/icons-vue";
import { findAllUser } from "@/api/user/user.js";
import { ElMessage } from "element-plus";

const props = defineProps({
  modelValue: {
    type: Object,
    required: true,
  },
  tags: {
    type: Array,
    default: () => [],
  },
  showUsers: {
    type: Boolean,
    default: false,
  },
  showStatus: {
    type: Boolean,
    default: true,
  },
  showVisibility: {
    type: Boolean,
    default: true,
  },
  dateRange: {
    type: Array,
    default: () => [],
  },
});

const emit = defineEmits([
  "update:modelValue",
  "search",
  "reset",
  "date-range-change",
  "update:category",
  "update:dateRange",
]);

const dateRange = ref([]);

const handleDateRangeChange = (val) => {
  emit("update:dateRange", val);
  emit("date-range-change", val);
};

// 用户列表
const users = ref([]);

// 获取所有用户
const fetchUsers = async () => {
  try {
    const response = await findAllUser();
    if (response.data.status === 200) {
      users.value = response.data.data.map((item) => item.user);
    }
  } catch (error) {
    console.error("获取用户列表失败:", error);
    ElMessage.error("获取用户列表失败");
  }
};

onMounted(() => {
  if (props.showUsers) {
    fetchUsers();
  }
});

/**
 * 动态调整布局
 */
// 计算每行的搜索项
const firstRowItems = computed(() => {
  const items = [
    { type: "title", span: 6 },
    { type: "content", span: 6 },
  ];

  // 当没有可见性和状态时，将作者放在第一行
  if (props.showUsers && !props.showVisibility && !props.showStatus) {
    items.push({ type: "author", span: 6 });
  } else {
    // 原有的逻辑
    if (props.showVisibility) {
      items.push({ type: "visibility", span: 6 });
    }
    if (props.showStatus) {
      items.push({ type: "status", span: 6 });
    }
  }

  return items;
});

const secondRowItems = computed(() => {
  const items = [
    { type: "category", span: 6 },
    { type: "tag", span: 6 },
  ];

  // 当有可见性或状态时，作者显示在第二行
  if (props.showUsers && (props.showVisibility || props.showStatus)) {
    items.push({ type: "author", span: 6 });
  }

  // 添加日期选择器
  items.push({ type: "date", span: 12 });

  return items;
});
</script>

<template>
  <el-form :model="modelValue" label-width="80px">
    <!-- 第一行 -->
    <el-row :gutter="20">
      <template v-for="item in firstRowItems" :key="item.type">
        <el-col :span="item.span">
          <!-- 标题搜索 -->
          <el-form-item v-if="item.type === 'title'" label="标题">
            <el-input
              v-model="modelValue.title"
              placeholder="搜索文章标题"
              clearable
              @keyup.enter="emit('search')"
            />
          </el-form-item>

          <!-- 内容搜索 -->
          <el-form-item v-if="item.type === 'content'" label="内容">
            <el-input
              v-model="modelValue.content"
              placeholder="搜索文章内容"
              clearable
              @keyup.enter="emit('search')"
            />
          </el-form-item>

          <!-- 作者搜索 -->
          <el-form-item v-if="item.type === 'author'" label="作者">
            <el-select v-model="modelValue.userId" placeholder="选择作者" clearable>
              <el-option
                v-for="user in users"
                :key="user.userId"
                :value="user.userId"
                :label="user.nickname || user.username"
              >
                <template #default>
                  <span v-if="user.nickname">
                    {{ user.nickname }}(<strong>{{ user.username }}</strong
                    >)
                  </span>
                  <span v-else>{{ user.username }}</span>
                </template>
              </el-option>
            </el-select>
          </el-form-item>

          <!-- 状态搜索 -->
          <el-form-item v-if="item.type === 'status'" label="状态">
            <el-select v-model="modelValue.isPublished" placeholder="发布状态" clearable>
              <el-option label="已发布" :value="true" />
              <el-option label="草稿" :value="false" />
            </el-select>
          </el-form-item>

          <!-- 可见性搜索 -->
          <el-form-item v-if="item.type === 'visibility'" label="可见性">
            <el-select v-model="modelValue.visibility" placeholder="可见性" clearable>
              <el-option label="公开" value="PUBLIC" />
              <el-option label="私密" value="PRIVATE" />
            </el-select>
          </el-form-item>
        </el-col>
      </template>
    </el-row>

    <!-- 第二行 -->
    <el-row :gutter="20">
      <template v-for="item in secondRowItems" :key="item.type">
        <el-col :span="item.span">
          <!-- 分类搜索 -->
          <el-form-item v-if="item.type === 'category'" label="分类">
            <category-select
              v-model="modelValue.categoryId"
              @update:category="$emit('update:category', $event)"
            />
          </el-form-item>

          <!-- 标签搜索 -->
          <el-form-item v-if="item.type === 'tag'" label="标签">
            <el-select v-model="modelValue.tagId" placeholder="选择标签" clearable>
              <el-option
                v-for="item in tags"
                :key="item.tagId"
                :label="item.name"
                :value="item.tagId"
              />
            </el-select>
          </el-form-item>

          <!-- 日期选择 -->
          <el-form-item v-if="item.type === 'date'" label="创建时间">
            <el-date-picker
              v-model="dateRange"
              type="daterange"
              range-separator="至"
              start-placeholder="开始日期"
              end-placeholder="结束日期"
              value-format="YYYY-MM-DD"
            />
          </el-form-item>
        </el-col>
      </template>
    </el-row>

    <!-- 按钮行 -->
    <el-row>
      <el-col :span="24" style="text-align: right">
        <slot name="extra-buttons"></slot>
        <el-button type="primary" :icon="Search" @click="emit('search')">搜索</el-button>
        <el-button @click="emit('reset')">重置</el-button>
      </el-col>
    </el-row>
  </el-form>
</template>

<style scoped>
.el-form-item {
  margin-bottom: 18px;
}

.el-date-picker {
  width: 100%;
}
</style>
