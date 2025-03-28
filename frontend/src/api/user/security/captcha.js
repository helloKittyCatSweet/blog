import request from "@/utils/request";
import { userPrefix } from "@/constants/api-constants.js";

let currentSessionId = null;

// 生成图片验证码
export const getCaptcha = async () => {
  const response = await request.get(`${userPrefix}/auth/captcha`, {
    responseType: "blob",
    withCredentials: true,
    headers: {
      'Access-Control-Expose-Headers': 'Session-Id',
    }
  });
  // 保存从响应头中获取的 sessionId
  currentSessionId = response.headers['Session-Id'] ||
    response.headers['session-id'] ||
    response.headers.get('Session-Id') || // Axios 有时需要这样访问
    response.headers.get('session-id');
  // console.log('获取到的 Session-Id:', currentSessionId);
  // console.log('获取到的响应头:', response.headers);
  return response;
};

// 校验图片验证码
export const checkCaptcha = (code) => {
  if (!currentSessionId) {
    throw new Error("Session ID not found");
  }
  return request.get(`${userPrefix}/auth/verify`, {
    params: {
      code,
      sessionId: currentSessionId
    },
    withCredentials: true,
  });
};

// 导出当前的 sessionId（用于调试）
export const getCurrentSessionId = () => currentSessionId;