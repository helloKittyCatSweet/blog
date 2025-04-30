const WS_BASE_URL = import.meta.env.PROD 
  ? `ws://${window.location.hostname}/ws`  // 生产环境使用当前域名
  : 'ws://localhost:8080/ws';  // 开发环境

export const getWsUrl = (path) => {
  return `${WS_BASE_URL}${path}`;
};