import { defineStore } from 'pinia'
import { ref } from 'vue'

export const useFavoriteStore = defineStore('free-share-favorite', () => {
  const needRefresh = ref(false)

  const setNeedRefresh = (value) => {
    needRefresh.value = value
  }

  return {
    needRefresh,
    setNeedRefresh
  }
})