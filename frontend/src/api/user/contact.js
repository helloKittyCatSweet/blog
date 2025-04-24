import request from '@/utils/request'
import { contactPrefix } from '@/constants/api-constants'

export const sendContactMessage = (data) => request.post(`${contactPrefix}/send`, data, {
  timeout: 65000,  // 65 seconds timeout
  headers: {
    'Content-Type': 'application/json'
  }
})


