import request from '@/utils/request'
import { reportPrefix, postPrefix } from '@/constants/api-constants.js'

// 更新举报
export const update = (data) => request.put(`${postPrefix}${reportPrefix}/public/update`, data)

// 修改举报状态
export const changeStatus = (id, status) =>
  request.put(`${postPrefix}${reportPrefix}/admin/change/status/${id}/${status}`)

// 创建报告
export const create = (data) => request.post(`${postPrefix}${reportPrefix}/public/create`, data)

// 判断举报是否存在
export const existById = (id) => request.get(`${postPrefix}${reportPrefix}/admin/exist/id/${id}`)

// 统计举报数量
export const count = () => request.get(`${postPrefix}${reportPrefix}/admin/count`)

// 根据用户名查询报告列表
export const findByUsername = (username) =>
  request.get(`${postPrefix}${reportPrefix}/admin/find/user/${username}`)

// 根据状态查询报告列表
export const findByStatus = (status) =>
  request.get(`${postPrefix}${reportPrefix}/admin/find/status/${status}`)

// 根据原因查询报告列表
export const findByReason = (reason) =>
  request.get(`${postPrefix}${reportPrefix}/admin/find/reason/${reason}`)

// 根据id查询举报详情
export const findById = (id) => request.get(`${postPrefix}${reportPrefix}/admin/find/id/${id}`)

// 根据文章id查询报告列表
export const findByPostId = (postId) =>
  request.get(`${postPrefix}${reportPrefix}/admin/find/article/${postId}`)

// 查询所有报告列表
export const findAll = () => request.get(`${postPrefix}${reportPrefix}/admin/find/all`)

// 根据举报id删除举报
export const deleteById = (id) => request.delete(`${postPrefix}${reportPrefix}/admin/delete/id/${id}`)
