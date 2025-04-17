<script>
import { ref, watch } from "vue";
import { useRouter } from "vue-router";
import { searchSuggestions } from "@/api/search/es";
import { Search, CircleClose } from "@element-plus/icons-vue";
import { BLOG_POSTS_PATH } from "@/constants/routes/blog";

export default {
  name: "SearchBar",
  components: { Search, CircleClose },
  props: {
    modelValue: {
      type: String,
      default: "",
    },
  },
  emits: ["update:modelValue", "search"],
  setup(props, { emit }) {
    const router = useRouter();
    const searchKeyword = ref(props.modelValue || "");

    // 监听 props 变化
    watch(
      () => props.modelValue,
      (newValue) => {
        if (newValue !== searchKeyword.value) {
          searchKeyword.value = newValue;
        }
      },
      { immediate: true } // 添加 immediate: true 确保首次加载时也执行
    );

    // 监听内部值变化
    watch(searchKeyword, (newValue) => {
      emit("update:modelValue", newValue);
    });

    // 处理建议选择
    const handleSelect = (item) => {
      // 修改为使用 query 参数跳转到文章列表页
      router.push({
        path: BLOG_POSTS_PATH,
        query: { search: item.value },
      });
      emit("search");
    };

    // 处理搜索
    const handleSearch = () => {
      if (searchKeyword.value) {
        // 修改为使用 query 参数跳转到文章列表页
        router.push({
          path: BLOG_POSTS_PATH,
          query: { search: searchKeyword.value },
        });
        emit("search");
      }
    };

    // 加载搜索建议
    const loadSuggestions = async (queryString, callback) => {
      if (queryString.length === 0) {
        callback([]);
        return;
      }
      try {
        const response = await searchSuggestions(queryString);
        if (response.data?.status === 200) {
          const suggestions = response.data.data.map((item) => ({
            value: item,
          }));
          callback(suggestions);
        } else {
          callback([]);
        }
      } catch (error) {
        console.error("获取搜索建议失败:", error);
        callback([]);
      }
    };

    // 添加清空搜索方法
    const clearSearch = () => {
      searchKeyword.value = "";
      emit("update:modelValue", "");
      // 更新路由，移除搜索参数
      router.push({
        path: BLOG_POSTS_PATH,
        query: { ...router.currentRoute.value.query, search: undefined },
      });
      emit("search");
    };

    return {
      searchKeyword,
      loadSuggestions,
      handleSelect,
      handleSearch,
      clearSearch,
    };
  },
};
</script>

<template>
  <div class="search-bar">
    <el-autocomplete
      v-model="searchKeyword"
      :fetch-suggestions="loadSuggestions"
      placeholder="搜索文章..."
      @select="handleSelect"
      @keyup.enter="handleSearch"
      class="search-input"
      :clearable="false"
    >
      <template #prefix>
        <el-icon><Search /></el-icon>
      </template>
      <template #suffix>
        <el-icon v-if="searchKeyword" class="clear-icon" @click="clearSearch">
          <CircleClose />
        </el-icon>
      </template>
    </el-autocomplete>
  </div>
</template>

<style scoped>
.search-bar {
  width: 100%;
  max-width: 500px;
  margin: 0 auto;
}

.search-input {
  width: 100%;
}

.clear-icon {
  cursor: pointer;
  color: var(--el-text-color-secondary);
}

.clear-icon:hover {
  color: var(--el-text-color-primary);
}
</style>
