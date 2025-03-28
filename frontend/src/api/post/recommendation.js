import request from '@/api/request'
import { postPrefix, recommendationPrefix } from '@/constants/api-constants.js'

// 获取个性化推荐
export const getPersonalizedRecommendation = (userId) =>
  request.get(`${postPrefix}${recommendationPrefix}/personal/${userId}`,
    { params: { limit: 10 } }
  )

// 获取热门文章推荐
export const getHotArticleRecommendation = () =>
  request.get(`${postPrefix}${recommendationPrefix}/hot`, { params: { limit: 10 } })
