import request from '@/utils/request'
import { rolePrefix, userPrefix } from '@/constants/api-constants.js'

// 更新角色
export const update = (data) => request.put(`${userPrefix}${rolePrefix}/admin/update`, data)

// 创建角色
export const create = (data) => request.post(`${userPrefix}${rolePrefix}/admin/create`, data)

// 根据角色名查询角色
export const findByName = (name) => request.get(`${userPrefix}${rolePrefix}/admin/find/name/${name}`)

// 根据角色名模糊查询角色
export const findByNameLike = (name) =>
  request.get(`${userPrefix}${rolePrefix}/admin/find/name/like/${name}`)

// 根据角色id查询角色
export const findById = (id) => request.get(`${userPrefix}${rolePrefix}/admin/find/id/${id}`)

// 根据描述模糊查询角色
export const findByDescription = (description) =>
  request.get(`${userPrefix}${rolePrefix}/admin/find/des/${description}`)

// 查询所有角色
export const findAll = () => request.get(`${userPrefix}${rolePrefix}/admin/find/all`)

// 根据角色id判断角色是否存在
export const existById = (id) => request.get(`${userPrefix}${rolePrefix}/admin/exist/id/${id}`)

// 查询用户角色关系数量
export const count = () => request.get(`${userPrefix}${rolePrefix}/admin/count`)

// 根据角色名称删除角色
export const deleteByName = (name) =>
  request.delete(`${userPrefix}${rolePrefix}/admin/delete/name/${name}`)

// 根据角色id删除角色
export const deleteById = (id) => request.delete(`${userPrefix}${rolePrefix}/admin/delete/id/${id}`)
