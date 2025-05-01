import request from "@/utils/request"
import { searchPrefix } from "@/constants/api-constants"

// 搜索文章
export const searchPosts = (keyword, { page = 0, size = 10, sorts = "createdAt,desc" } = {}) =>
  request.get(`${searchPrefix}/public/posts`, { params: { keyword, page, size, sorts } })

// 搜索建议
export const searchPostSuggestions = (keyword) =>
  request.get(`${searchPrefix}/public/suggest/post`, { params: { keyword } })

// 分类搜索建议
export const searchCategorySuggestions = (keyword) =>
  request.get(`${searchPrefix}/public/suggest/category`, { params: { keyword } })

// 标签搜索建议
export const searchTagSuggestions = (keyword) =>
  request.get(`${searchPrefix}/public/suggest/tag`, { params: { keyword } })