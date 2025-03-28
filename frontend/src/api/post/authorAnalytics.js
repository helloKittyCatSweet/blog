import request from '@/utils/request';
import { postPrefix } from '@/constants/api-constants.js';

// 获取作者分析报告
export const getAuthorAnalytics = (authorId) =>
  request.get(`${postPrefix}/analytics/public/author/${authorId}`);
