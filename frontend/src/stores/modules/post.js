import { defineStore } from 'pinia'
import { ref } from 'vue'

export const usePostStore = defineStore('free-share-post', () => {
  const postViews = ref({})  // 存储文章浏览量的映射

  // 更新文章浏览量
  const updatePostViews = (postId, views) => {
    postViews.value[postId] = views
  }

  // 获取文章浏览量
  const getPostViews = (postId) => {
    return postViews.value[postId] || 0
  }

  return {
    postViews,
    updatePostViews,
    getPostViews
  }
})