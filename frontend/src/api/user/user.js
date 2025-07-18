import request from '@/utils/request'
import { userPrefix } from '@/constants/api-constants.js'

// 更新用户信息
export const update = (data) => request.put(`${userPrefix}/public/update`, data)

// 重置密码
export const resetPassword = ({ userId, password }) =>
  request.put(`${userPrefix}/auth/password/reset`, { userId, password })

// 检查密码是否匹配
export const verifyPassword = (data) =>
  request.post(`${userPrefix}/auth/password/verify`, data)

// 判断该邮箱是否已注册
export const existsByEmail = (email) =>
  request.get(`${userPrefix}/auth/exist/email/${email}`)

// 激活账户
export const activate = (userId, isActive) =>
  request.put(`${userPrefix}/admin/activate?userId=${userId}&isActive=${isActive}`)

// 验证token
export const verifyToken = (token) => request.post(`${userPrefix}/public/validate`, token,
  {
    headers: {
      'Content-Type': 'text/plain',
    }
  })

// 上传头像
export const uploadAvatar = (formData) => {
  return request.post(`${userPrefix}/public/upload/avatar`, formData, {
    headers: {
      'Content-Type': 'multipart/form-data'
    }
  })
}

// 根据邮箱查询用户
export const findUserByEmail = (email) =>
  request.get(`${userPrefix}/auth/find/email/${email}`)

// 注册
export const register = (data) => request.post(`${userPrefix}/auth/register`, data)

// 登录
export const login = (data) => request.post(`${userPrefix}/auth/login`, data)

// 查询激活用户
export const findActivatedUser = (isActive) => request.get(`${userPrefix}/admin/find/${isActive}`)

// 根据用户名查询用户
export const findUserByUsername = (username) =>
  request.get(`${userPrefix}/public/find/username/${username}`)

// 根据邮箱（模糊搜索）查询用户
export const findByEmail = (email) => request.get(`${userPrefix}/public/find/email/${email}`)

// 根据邮箱后缀查询用户
export const findByEmailSuffix = (emailSuffix) =>
  request.get(`${userPrefix}/public/find/email/suffix/${emailSuffix}`)

// 根据用户id查询用户
export const findUserById = (userId) => request.get(`${userPrefix}/public/find/id/${userId}`)

// 查询所有用户
export const findAllUser = ({page = 0, size = 10, sort = "createdAt,desc"} = {}) =>
  request.get(`${userPrefix}/admin/find/all`, {params: {page, size, sort}})

// 判断用户是否存在
export const existsById = (userId) => request.get(`${userPrefix}/admin/exist/${userId}`)

// 查询用户数量
export const count = () => request.get(`${userPrefix}/admin/count`)

// 删除用户
export const deleteById = (userId) => request.delete(`${userPrefix}/admin/delete/${userId}`)

// 根据用户名或者邮箱查找用户
export const findByKeywordForAdmin = (keyword) =>
  request.get(`${userPrefix}/admin/find/keyword?keyword=${encodeURIComponent(keyword)}`)

// 导入用户数据
export const importUserData = (file) => {
  const formData = new FormData();
  formData.append('file', file);
  return request.post(`${userPrefix}/admin/import`, formData, {
    headers: {
      'Content-Type': 'multipart/form-data'
    }
  });
};

// 获取签名
export const getSignature = (userId) => request.get(`${userPrefix}/public/signature/${userId}`)

// 生成默认签名
export const generateDefaultSignature = (userId) => request.post(`${userPrefix}/public/signature/generate/${userId}`)

/**
 * 上传用户签名
 * @param {FormData} formData 包含签名文件和用户ID的表单数据
 * @returns {Promise} 上传结果
 */
export function uploadSignature(formData) {
  return request({
    url: `${userPrefix}/public/upload/signature`,
    method: 'post',
    data: formData,
    headers: {
      'Content-Type': 'multipart/form-data'
    }
  });
}