import request from "@/utils/request"
import { searchPrefix } from "@/constants/api-constants"

// 搜索文章
export const searchPosts = (keyword, page = 0, size = 10) =>
  request.get(`${searchPrefix}/public/posts`, { params: { keyword, page, size } })

// 搜索建议
export const searchSuggestions = (keyword) =>
  request.get(`${searchPrefix}/public/suggest`, { params: { keyword } })