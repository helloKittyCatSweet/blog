import request from '@/utils/request'
import { categoryPrefix } from '@/constants/api-constants.js'

// 更新分类
export const update = (data) => request.put(`${categoryPrefix}/public/update`, data)

// 创建分类
export const create = (data) => request.post(`${categoryPrefix}/public/create`, data)

// 根据父分类id查询子分类
export const findSubCategoriesByParentName = (name) =>
  request.get(`${categoryPrefix}/public/find/parent/${name}`)

// 根据名称查询分类
export const findCategoryByName = (name) =>
  request.get(`${categoryPrefix}/public/find/name/${name}`)

// 根据名称模糊查询分类
export const findCategoryByNameLike = (name) =>
  request.get(`${categoryPrefix}/public/find/name/like/${name}`)

// 根据id查询分类
export const findCategoryById = (id) => request.get(`${categoryPrefix}/public/find/id/${id}`)

// 根据父分类id查询所有子分类（包含子孙）
export const findDescendantsByParentName = (name) =>
  request.get(`${categoryPrefix}/public/find/descendants/${name}`)

// 查询所有分类
export const findAll = () => request.get(`${categoryPrefix}/public/find/all`)

// 检查分类是否存在
export const existById = (id) => request.get(`${categoryPrefix}/public/exists/${id}`)

// 查询分类数量
export const count = () => request.get(`${categoryPrefix}/public/count`)

// 删除分类及其子分类
export const deleteSubCategories = (id) =>
  request.delete(`${categoryPrefix}/admin/delete/sub/${id}`)

// 删除叶子分类
export const deleteLeafCategory = (id) =>
  request.delete(`${categoryPrefix}/admin/delete/leaf/${id}`)

// 根据id删除分类
export const deleteById = (id) => request.delete(`${categoryPrefix}/admin/delete/id/${id}`)
