<script setup>
import { Edit, Delete, View, Document } from "@element-plus/icons-vue";

defineProps({
  postId: {
    type: [String, Number],
    required: true,
  },
  row: {
    type: Object,
    required: true,
  },
});

const emit = defineEmits(["view", "edit", "version", "delete"]);
</script>

<template>
  <el-space :size="10" wrap>
    <el-button
      type="primary"
      :icon="View"
      circle
      @click="emit('view', postId)"
      title="查看"
    />
    <el-button
      type="warning"
      :icon="Edit"
      circle
      @click="emit('edit', postId)"
      title="编辑"
    />
    <el-button
      type="info"
      :icon="Document"
      circle
      @click="emit('version', row)"
      title="版本"
    />
    <el-button
      type="danger"
      :icon="Delete"
      circle
      @click="emit('delete', postId)"
      title="删除"
    />
  </el-space>
</template>
