<script setup>
import { defineEmits, defineProps, computed } from "vue";

const props = defineProps({
  views: {
    type: Number,
    default: 0,
  },
  likes: {
    type: Number,
    default: 0,
  },
  favorites: {
    type: Number,
    default: 0,
  },
  sortKey: {
    type: String,
    default: "", // 'views', 'likes', 'favorites'
  },
  sortOrder: {
    type: String,
    default: "ascending",
  },
});

const emit = defineEmits(["sort"]);

const handleSort = (key) => {
  const newOrder =
    props.sortKey === key && props.sortOrder === "ascending" ? "descending" : "ascending";
  emit("sort", { prop: key, order: newOrder });
};

const total = computed(() => {
  return props.views + props.likes + props.favorites;
});
</script>

<template>
  <el-space>
    <el-tag
      :type="sortKey === 'views' ? 'success' : 'info'"
      class="sortable-tag"
      @click="handleSort('views')"
    >
      浏览 {{ views }}
      <el-icon v-if="sortKey === 'views'" :style="{ marginLeft: '4px' }">
        <CaretTop v-if="sortOrder === 'ascending'" />
        <CaretBottom v-else />
      </el-icon>
    </el-tag>
    <el-tag
      :type="sortKey === 'likes' ? 'success' : 'info'"
      class="sortable-tag"
      @click="handleSort('likes')"
    >
      点赞 {{ likes }}
      <el-icon v-if="sortKey === 'likes'" :style="{ marginLeft: '4px' }">
        <CaretTop v-if="sortOrder === 'ascending'" />
        <CaretBottom v-else />
      </el-icon>
    </el-tag>
    <el-tag
      :type="sortKey === 'favorites' ? 'success' : 'info'"
      class="sortable-tag"
      @click="handleSort('favorites')"
    >
      收藏 {{ favorites }}
      <el-icon v-if="sortKey === 'likes'" :style="{ marginLeft: '4px' }">
        <CaretTop v-if="sortOrder === 'ascending'" />
        <CaretBottom v-else />
      </el-icon>
    </el-tag>
    <!-- 新增总和标签 -->
    <el-tag
      :type="sortKey === 'sum' ? 'success' : 'info'"
      class="sortable-tag"
      @click="handleSort('sum')"
    >
      总合 {{ total }}
      <el-icon v-if="sortKey === 'sum'" :style="{ marginLeft: '4px' }">
        <CaretTop v-if="sortOrder === 'ascending'" />
        <CaretBottom v-else />
      </el-icon>
    </el-tag>
  </el-space>
</template>

<style scoped>
.sortable-tag {
  cursor: pointer;
  transition: all 0.3s;
}

.sortable-tag:hover {
  transform: scale(1.05);
}
</style>
