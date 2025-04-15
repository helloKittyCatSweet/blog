import request from '@/utils/request'
import { commentPrefix } from '@/constants/api-constants'

// 点赞
export const likeComment = (commentId, count) =>
  request.put(`${commentPrefix}/public/add/${commentId}/like?count=${count}`)

// 修改评论
export const update = (data) => request.put(`${commentPrefix}/public/update`, data)

// 新增评论
export const create = (data) => request.post(`${commentPrefix}/public/create`, data)

// 获取评论列表
export const findByPostId = (postId) => request.get(`${commentPrefix}/public/find/${postId}/list`)

// 获取评论详情
export const findById = (commentId) => request.get(`${commentPrefix}/public/find/${commentId}`)

// 获取评论总数
export const countByPostId = (postId) => request.get(`${commentPrefix}/public/count/${postId}`)

// 判断评论是否存在
export const existById = (commentId) => request.get(`${commentPrefix}/admin/exists/${commentId}`)

// 获取评论总数
export const count = () => request.get(`${commentPrefix}/admin/count`)

// 获取评论列表
export const findAll = () => request.get(`${commentPrefix}/admin/find/all`)

// 删除评论
export const deleteById = (commentId, userId) =>
  request.delete(`${commentPrefix}/public/delete/id/${commentId}/${userId}`)

// 批量删除评论
export const batchDelete = (commentIds) =>
  request.delete(`${commentPrefix}/public/delete/batch`, { data: commentIds })

// 根据作者获取评论
export const findByAuthor = () => request.get(`${commentPrefix}/public/find/author`)

// 根据用户id获取评论
export const findByUserId = () => request.get(`${commentPrefix}/public/find/user`)