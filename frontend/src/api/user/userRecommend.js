import request from "@/utils/request";
import { userPrefix, userRecommendPrefix } from "@/constants/api-constants";

// 获取用户的推荐列表
export const getRecommendList = (limit) => request.get(`${userPrefix}${userRecommendPrefix}/public/list`, { paranms: { limit } });