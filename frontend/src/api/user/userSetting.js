import request from '@/utils/request'
import { userSettingPrefix, userPrefix } from '@/constants/api-constants.js'

// 保存用户设置
export const save = (data) => request.post(`${userPrefix}${userSettingPrefix}/public/save`, data)

// 根据用户id查询用户设置
export const findByUserId = (userId) =>
  request.get(`${userPrefix}${userSettingPrefix}/public/find/user/${userId}`)

// 根据id查询用户设置
export const findById = (id) => request.get(`${userPrefix}${userSettingPrefix}admin/find/id/${id}`)

// 查询所有用户设置
export const findAll = () => request.get(`${userPrefix}${userSettingPrefix}/admin/find/all`)

// 根据id判断用户设置是否存在
export const existsById = (id) => request.get(`${userPrefix}${userSettingPrefix}/admin/exist/id/${id}`)

// 查询用户设置数量
export const count = () => request.get(`${userPrefix}${userSettingPrefix}/admin/count`)

// 根据id删除用户设置
export const deleteById = (id) => request.delete(`${userPrefix}${userSettingPrefix}/admin/delete/id/${id}`)
