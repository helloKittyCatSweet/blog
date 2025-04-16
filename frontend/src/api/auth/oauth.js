import request from '@/utils/request'
import { authPrefix, oauth2 } from '@/constants/api-constants'

export const getGithubLoginUrl = () => request.get(`${authPrefix}${oauth2}/github/url`)

export const handleGithubCallback = (code, password, email) => request.post(`${authPrefix}${oauth2}/github/callback`, { code, password, email })
