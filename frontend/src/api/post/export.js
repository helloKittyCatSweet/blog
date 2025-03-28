import request from '@/utils/request'
import { postPrefix } from '@/constants/api-constants.js'

// 导出为PDF
export const exportToPDF = (postId) =>
  request.get(`${postPrefix}/export/public/pdf/${postId}`);

// 导出为Markdown
export const exportToMarkdown = (postId) =>
  request.get(`${postPrefix}/export/public/markdown/${postId}`);
