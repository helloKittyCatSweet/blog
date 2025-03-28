import request from '@/utils/request'
import { postPrefix, postVersionPrefix } from '@/constants/api-constants.js'

// 保存版本
export const save = (data) => request.post(`${postPrefix}${postVersionPrefix}/public/save`, data)

// 根据文章id查询版本列表
export const findByPostId = (postId) =>
  request.get(`${postPrefix}${postVersionPrefix}/public/find/post/${postId}`)

// 根据版本id查询版本详情
export const findById = (versionId) =>
  request.get(`${postPrefix}${postVersionPrefix}/admin/find/${versionId}`)

// 查询所有版本
export const findAll = () => request.get(`${postPrefix}${postVersionPrefix}/admin/find/all`)

// 判断版本是否存在
export const exists = (versionId) =>
  request.get(`${postPrefix}${postVersionPrefix}/admin/exists/id/${versionId}`)

// 统计版本数量
export const count = () => request.get(`${postPrefix}${postVersionPrefix}/admin/count`)

// 删除版本
export const deleteById = (versionId) =>
  request.delete(`${postPrefix}${postVersionPrefix}/admin/delete/id/${versionId}`)
