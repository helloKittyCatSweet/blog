import request from '@/utils/request'
import { tagPrefix } from '@/constants/api-constants.js'

// 更新标签
export const update = (data) => request.put(`${tagPrefix}/public/update`, data)

// 创建标签
export const create = (data) => request.post(`${tagPrefix}/public/create`, data)

// 根据权重查询标签
export const findByWeight = ({ weight, compare, page = 0, size = 10, sorts = ["useCount", "desc"] } = {}) =>
  request.get(`${tagPrefix}/public/find/weight/${weight}/${compare}`, {params: {page, size, sorts}})

// 根据名称查询标签
export const findByName = (name) => request.get(`${tagPrefix}/public/find/name/${name}`)

// 根据id查询标签
export const findById = (tagId) => request.get(`${tagPrefix}/admin/find/id/${tagId}`)

// 查询所有标签
export const findAll = ({page = 0, size = 10, sorts = "useCount,desc"} = {}) =>
  request.get(`${tagPrefix}/public/find/all`, {params: {page, size, sorts}})

// 根据标签名称和权值查询
export const findByCombined = (params) =>
  request.get(`${tagPrefix}/public/find/combined`, { params})

// 根据id查询标签是否存在
export const existsById = (tagId) => request.get(`${tagPrefix}/public/exists/id/${tagId}`)

// 查询标签数量
export const count = () => request.get(`${tagPrefix}/public/count`)

// 根据id删除标签
export const deleteById = (tagId) => request.delete(`${tagPrefix}/admin/delete/id/${tagId}`)
