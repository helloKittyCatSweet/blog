import request from '@/utils/request'
import { postPrefix, recommendationPrefix } from '@/constants/api-constants.js'

// 获取个性化推荐
export const getPersonalizedRecommendation = () =>
  request.get(`${postPrefix}${recommendationPrefix}/public/personal`,
    { params: { limit: 5 } }
  )

// 获取热门文章推荐
export const getHotArticleRecommendation = () =>
  request.get(`${postPrefix}${recommendationPrefix}/public/hot`, { params: { limit: 5 } })
