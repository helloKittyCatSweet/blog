import request from '@/utils/request'
import { userPrefix, userRolePrefix } from '@/constants/api-constants.js'

// 保存角色
export const save = (data) => request.post(`${userPrefix}${userRolePrefix}/admin/save`, data)

// 查找一个用户都有什么角色
export const findUserRoles = (userId) =>
  request.get(`${userPrefix}${userRolePrefix}/admin/find/user/${userId}`)

// 查询一个角色都有哪些用户
export const findRoleUsers = (roleId, {page = 0, size = 10, sort = "ur.id.userId,desc"} = {}) =>
  request.get(`${userPrefix}${userRolePrefix}/admin/find/role/${roleId}`, {params: {page, size, sort}})

// 查询所有角色
export const findAll = () => request.get(`${userPrefix}${userRolePrefix}/admin/find/all`)

// 判断角色是否存在
export const exist = (userId, roleId) =>
  request.get(`${userPrefix}${userRolePrefix}/admin/exist/id/${userId}/${roleId}`)

// 查询用户角色关系数量
export const count = () => request.get(`${userPrefix}${userRolePrefix}/admin/count`)

// 删除角色
export const deleteRole = (userId, roleId) =>
  request.delete(`${userPrefix}${userRolePrefix}/admin/delete/${userId}/${roleId}`)

// 导入角色数据
export const importRoleData = (file) => {
  const formData = new FormData()
  formData.append('file', file)
  return request.post(`${userPrefix}${userRolePrefix}/admin/import`, formData, {
    headers: {
      'Content-Type': 'multipart/form-data'
    }
  })
}
