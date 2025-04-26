import request from '@/utils/request'
import { authPrefix, oauth2 } from '@/constants/api-constants'

export const getGithubLoginUrl = () => request.get(`${authPrefix}${oauth2}/github/url`)

// 已注册用户的GitHub登录回调
export const handleGithubCallback = (code) => request.get(`${authPrefix}${oauth2}/github/callback`, { params: { code } })

// 新用户完成GitHub注册
export const completeGithubRegistration = (password, email) => 
  request({
    url: `${authPrefix}${oauth2}/github/complete-registration`,
    method: 'post',
    data: {
      password,
      email
    }
  })
