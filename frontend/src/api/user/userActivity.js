import request from '@/utils/request'
import { userActivityPrefix, userPrefix } from '@/constants/api-constants.js'

// 更新用户动态
export const update = (data) => request.put(`${userPrefix}${userActivityPrefix}/admin/update`, data)

// 创建用户动态
export const create = (data) =>
  request.post(`${userPrefix}${userActivityPrefix}/public/create`, data)

// 根据用户id、文章id、互动类型获取用户动态
export const findExplicit = (userId, postId, activityType) =>
  request.get(`${userPrefix}${userActivityPrefix}/public/find/explicit/${userId}/${postId}/${activityType}`)

// 根据用户id获取用户动态列表
export const findByUserId = (userId) =>
  request.get(`${userPrefix}${userActivityPrefix}/admin/find/user/${userId}`)

// 根据互动类型获取用户动态列表
export const findByType = (activityType) =>
  request.get(`${userPrefix}${userActivityPrefix}/public/find/type/${activityType}`)

// 根据文章id获取用户动态列表
export const findByPostId = (postId) =>
  request.get(`${userPrefix}${userActivityPrefix}/admin/find/post/${postId}`)

// 获取用户动态详情
export const findById = (activityId) =>
  request.get(`${userPrefix}${userActivityPrefix}/admin/find/id/${activityId}`)

// 获取用户动态列表
export const findAll = () => request.get(`${userPrefix}${userActivityPrefix}/admin/find/all`)

// 判断用户动态是否存在
export const existById = (activityId) =>
  request.get(`${userPrefix}${userActivityPrefix}/admin/exists/${activityId}`)

// 获取用户动态数量
export const count = () => request.get(`${userPrefix}${userActivityPrefix}/admin/count`)

// 删除用户动态
export const deleteById = (activityId) =>
  request.delete(`${userPrefix}${userActivityPrefix}/public/delete/id/${activityId}`)

// 获取其他用户对该用户的动态
export const getInteractions = () => request.get(`${userPrefix}${userActivityPrefix}/public/interactions`)