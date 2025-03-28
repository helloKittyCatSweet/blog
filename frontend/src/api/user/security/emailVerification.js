import request from '@/utils/request'
import { userPrefix, emailPrefix } from '@/constants/api-constants.js'

// 验证发送的验证码
export const verify = (data) =>
  request.post(`${userPrefix}${emailPrefix}/verify`, data)

// 发送验证码
export const send = (email) => request.post(
  `${userPrefix}${emailPrefix}/send`,
  email,
  {
    headers: {
      'Content-Type': 'text/plain' // 强制指定为纯文本格式
    }
  }
);
