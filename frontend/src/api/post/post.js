import request from '@/utils/request'
import { postPrefix } from '@/constants/api-constants.js'

// 更新文章
export const update = (data) => request.put(`${postPrefix}/public/update`, data)

// 点赞文章
export const likePost = ({ postId, count }) =>
  request.post(`${postPrefix}/public/like`, { postId, count })

// 收藏文章
export const collectPost = ({ postId, count }) =>
  request.post(`${postPrefix}/public/collect`, { postId, count })

// 上传文件
export const uploadAttachment = async (file, postId) => {
  const formData = new FormData()
  formData.append('file', file)
  if (postId) {
    formData.append('postId', parseInt(postId))
  }

  try {
    const response = await request.post(`${postPrefix}/public/upload/attachment`, formData, {
      headers: {
        'Content-Type': 'multipart/form-data'
      }
    })
    console.log('上传响应:', response)
    return response
  } catch (error) {
    console.error('上传错误:', error.response || error)
    throw error
  }
}

// 上传文章封面
export const uploadPostCover = async (file, postId) => {
  const formData = new FormData()
  formData.append('file', file)
  if (postId) {
    formData.append('postId', parseInt(postId))
  }

  try {
    const response = await request.post(`${postPrefix}/public/upload/cover`, formData, {
      headers: {
        'Content-Type': 'multipart/form-data'
      }
    })
    console.log('上传响应:', response)
    return response
  } catch (error) {
    console.error('上传错误:', error.response || error)
    throw error
  }
}

export const deleteAttachment = (attachmentId) =>
  request.delete(`${postPrefix}/public/delete/attachment/${attachmentId}`)


// 创建文章
export const create = (data) => request.post(`${postPrefix}/public/create`, data)

// 添加文章版本
export const addVersion = ({ postId, content, userId }) =>
  request.post(`${postPrefix}/public/add/version`, {
    postId: parseInt(postId),
    content,
    userId: parseInt(userId),
  })

// 添加文章标签
export const addTag = (data) => request.post(`${postPrefix}/public/add/tag`, data)

// 添加文章分类
export const addCategory = (data) => request.post(`${postPrefix}/public/add/category`, data)

// 根据标签查询文章列表
export const findByTag = (tag) => request.get(`${postPrefix}/public/find/tag/${tag}`)

// 根据分类查询文章列表
export const findByCategory = (category) =>
  request.get(`${postPrefix}/public/find/category/${category}`)

// 根据用户名和发布状态查询文章列表
export const findByUserAndStatus = ({ username, isPublished }) =>
  request.get(`${postPrefix}/public/find/user/${username}/${isPublished}`)

// 根据用户名查找文章列表
export const findByUsername = (username) => request.get(`${postPrefix}/public/find/user/${username}`)

// 根据标题关键字查询文章列表

export const findByKeysInTitle = async (keyword, isPublished = null) => {
  const url = `${postPrefix}/public/find/title/${keyword}`
  // 如果提供了发布状态，在前端进行过滤
  const response = await request.get(url)
  if (isPublished !== null && response.data?.data) {
    response.data.data = response.data.data.filter(post => post.isPublished === isPublished)
  }
  return response
}

// 根据内容关键字查询文章列表
export const findByKeysInContent = (keyword) => request.get(`${postPrefix}/public/find/content/${keyword}`)

// 获取文章最新版本号
export const getLatestVersion = (postId) => request.get(`${postPrefix}/admin/getLatestVersion/${postId}`)

// 根据文章id查询文章详情
export const findById = (postId) => request.get(`${postPrefix}/public/find/id/${postId}`)

// 查询所有文章
export const findAll = () => request.get(`${postPrefix}/admin/find/all`)

// 根据id查询文章是否存在
export const existById = (postId) => request.get(`${postPrefix}/admin/exists/id/${postId}`)

// 查询文章总数
export const count = () => request.get(`${postPrefix}/admin/count`)

// 删除文章版本
export const deleteVersion = (data) => request.delete(`${postPrefix}/public/delete/version`, { data })

// 删除文章标签
export const deleteTag = (data) => request.delete(`${postPrefix}/public/delete/tag`, data)

// 删除文章分类
export const deleteCategory = (data) => request.delete(`${postPrefix}/public/delete/category`, data)

// 根据id删除文章
export const deleteById = (data) => request.delete(`${postPrefix}/public/delete/id/${data}`)


// 获取仪表盘统计数据
export const getDashboardStats = () => request.get(`${postPrefix}/public/dashboard/stats`)

// 获取月度统计数据
export const getMonthlyStats = (year, month) =>
  request.get(`${postPrefix}/public/dashboard/monthly-stats`, { params: { year, month } })

// 获取互动数据统计
export const getInteractionStats = () => request.get(`${postPrefix}/public/dashboard/interaction-stats`)

// 获取最近文章
export const getRecentPosts = (limit = 5) =>
  request.get(`${postPrefix}/public/dashboard/recent-posts`, { params: { limit } })

// 搜索文章
export const searchPosts = (criteria) => request.post(`${postPrefix}/public/search`, criteria)

// 根据用户id查询附件列表
export const findAttachmentsByUserId = () => request.get(`${postPrefix}/public/find/attachments`)

// 生成文章摘要
export const generateSummary = (content) =>
  request.post(`${postPrefix}/public/summary`, { content }, {
    headers: {
      'Content-Type': 'application/json'
    }
  })