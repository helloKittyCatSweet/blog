import request from '@/utils/request'
import { permissionPrefix, userPrefix } from '@/constants/api-constants.js'

// 保存权限
export const save = (data) => request.post(`${userPrefix}${permissionPrefix}/admin/save`, data)

// 根据名称查询权限
export const findByName = (name) => request.get(`${userPrefix}
  ${permissionPrefix}/admin/find/name/${name}`)

// 根据名称模糊查询权限
export const findByNameLike = (name) => request.get(`${userPrefix}
  ${permissionPrefix}/admin/find/name/like/${name}`)

// 根据id查询权限
export const findById = (permissionId) =>
  request.get(`${userPrefix}${permissionPrefix}/admin/find/id/${permissionId}`)

// 查询所有权限
export const findAll = () => request.get(`${userPrefix}${permissionPrefix}/admin/find/all`)

// 判断权限是否存在
export const existById = (permissionId) =>
  request.get(`${userPrefix}${permissionPrefix}/admin/exists/id/${permissionId}`)

// 统计权限数量
export const count = () => request.get(`${userPrefix}${permissionPrefix}/admin/count`)

// 删除权限
export const deleteById = (permissionId) =>
  request.delete(`${userPrefix}${permissionPrefix}/admin/delete/id/${permissionId}`)
