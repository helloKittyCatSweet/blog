import request from '@/utils/request'
import { postPrefix, favoritePrefix } from '@/constants/api-constants.js'

// 创建收藏
export const create = (data) => request.post(`${postPrefix}${favoritePrefix}/public/create`, data)

// 更新收藏
export const update = (data) => request.put(`${postPrefix}${favoritePrefix}/public/update`, data)

// 根据用户id查询所有收藏
export const findByUserId = () => request.get(`${postPrefix}${favoritePrefix}/public/find/list`)

// 获取用户的所有收藏夹名称
export const getFolderNames = () => request.get(`${postPrefix}${favoritePrefix}/public/find/folder`)

// 获取用户特定收藏夹中的文章
export const getPostsByFolder = (folderName) =>
  request.get(`${postPrefix}${favoritePrefix}/public/find/folder/${folderName}`)

// 移动收藏夹到指定文件夹
export const moveToFolder = (favoriteIds, folderName) =>
  request.put(`${postPrefix}${favoritePrefix}/public/update/folder/${favoriteIds}?folderName=${folderName}`)

// 查询用户收藏数量
export const countByUserId = (userId) =>
  request.get(`${postPrefix}${favoritePrefix}/public/find/${userId}/count`)

// 查询文章收藏数量
export const countByPostId = (postId) =>
  request.get(`${postPrefix}${favoritePrefix}/public/find/${postId}/count`)

// 查询特定收藏
export const findByUserIdAndPostId = (userId, postId) =>
  request.get(`${postPrefix}${favoritePrefix}/public/find/${userId}/${postId}`)

// 查询所有收藏数量
export const count = () => request.get(`${postPrefix}${favoritePrefix}/admin/count`)

// 根据id查询收藏
export const findById = (id) => request.get(`${postPrefix}${favoritePrefix}/admin/find/id/${id}`)

// 查询所有收藏
export const findAll = () => request.get(`${postPrefix}${favoritePrefix}/admin/find/all`)

// 查询收藏是否存在
export const exists = (id) => request.get(`${postPrefix}${favoritePrefix}/admin/exists/id/${id}`)

// 删除收藏
export const deleteById = (id) => request.delete(`${postPrefix}${favoritePrefix}/public/delete/id/${id}`)
