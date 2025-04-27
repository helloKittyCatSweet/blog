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
export const findByUserList = (page = 0, size = 10, sorts = "createdAt,desc") =>
  request.get(`${postPrefix}${reportPrefix}/public/find/user/list`, { params: { page, size, sorts } })

// 根据状态查询报告列表
export const findByStatus = (status, { page = 0, size = 10, sort = ["createdAt,desc"] } = {}) =>
  request.get(`${postPrefix}${reportPrefix}/admin/find/status/${status}`, { params: { page, size, sort } })

// 根据原因查询报告列表
export const findByReason = (reason, { page = 0, size = 10, sort = ["createdAt,desc"] } = {}) =>
  request.get(`${postPrefix}${reportPrefix}/admin/find/reason/${reason}`, { params: { page, size, sort } })

// 根据id查询举报详情
export const findById = (id) => request.get(`${postPrefix}${reportPrefix}/admin/find/id/${id}`)

// 根据文章id查询报告列表
export const findByPostId = (postId, { page = 0, size = 10, sort = ["createdAt,desc"] } = {}) =>
  request.get(`${postPrefix}${reportPrefix}/admin/find/article/${postId}`, { params: { page, size, sort } })

// 查询所有报告列表
export const findAll = ({ page = 0, size = 10, sort = "createdAt,desc" } = {}) =>
  request.get(`${postPrefix}${reportPrefix}/admin/find/all`, { params: { page, size, sort } })

// 根据举报id删除举报
export const deleteById = (id) => request.delete(`${postPrefix}${reportPrefix}/public/delete/id/${id}`)

// 搜索举报信息
export const searchReports = (params) => request.get(`${postPrefix}${reportPrefix}/public/search`, {
  params
})

// 审核举报
export const review = (reportId, data) =>
  request.post(`${postPrefix}${reportPrefix}/admin/review/${reportId}`, null, {
    params: {
      approved: data.approved,
      comment: data.comment
    }
  })