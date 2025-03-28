import request from '@/utils/request'
import { rolePermissionPrefix, userPrefix } from '@/constants/api-constants.js'

// 保存映射
export const save = (data) => request.post(`${userPrefix}${rolePermissionPrefix}/admin/save`, data)

// 根据角色id查询角色权限
export const findRolePermissionByRoleId = (roleId) =>
  request.get(`${userPrefix}${rolePermissionPrefix}/admin/find/role/${roleId}`)

// 根据权限id查询角色权限
export const findRolePermissionByPermissionId = (permissionId) =>
  request.get(`${userPrefix}${rolePermissionPrefix}/admin/find/permission/${permissionId}`)

// 根据id查找角色权限
export const findRolePermissionById = (id) =>
  request.get(`${userPrefix}${rolePermissionPrefix}/admin/find/id/${id}`)

// 查询所有角色权限
export const findAll = () => request.get(`${userPrefix}${rolePermissionPrefix}/admin/find/all`)

// 判断角色是否存在指定权限
export const hasPermission = (roleId, permissionId) =>
  request.get(`${userPrefix}${rolePermissionPrefix}/admin/exist/explicit/${roleId}/${permissionId}`)

// 查询角色权限数量
export const count = () => request.get(`${userPrefix}${rolePermissionPrefix}/admin/count`)

// 根据角色id和权限id查询角色权限
export const findExplicit = (roleId, permissionId) =>
  request.get(`${userPrefix}${rolePermissionPrefix}/admin/find/explicit/${roleId}/${permissionId}`)

// 根据id删除角色权限
export const deleteById = (id) =>
  request.delete(`${userPrefix}${rolePermissionPrefix}/admin/delete/id/${id}`)
