import request from '@/utils/request';
import { postPrefix, analyticsPrefix } from '@/constants/api-constants.js';

// 获取作者分析报告
export const getAuthorAnalytics = () => request.get(`${postPrefix}${analyticsPrefix}/public/author`);
