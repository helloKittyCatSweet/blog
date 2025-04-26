import request from '@/utils/request'
import { postPrefix, exportPrefix } from '@/constants/api-constants.js'

// 导出为PDF
export const exportToPDF = (postId) =>
  request.get(`${postPrefix}${exportPrefix}/public/pdf/${postId}`, {
    responseType: 'blob'
  });

// 导出为Markdown
export const exportToMarkdown = (postId) =>
  request.get(`${postPrefix}${exportPrefix}/public/markdown/${postId}`, {
    responseType: 'blob'
  });