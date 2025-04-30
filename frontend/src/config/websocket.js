const WS_BASE_URL = import.meta.env.PROD
  ? `http://${window.location.hostname}/ws`  // 修改为 http 协议
  : 'http://localhost:8080/ws';  // 修改为 http 协议

export const getWsUrl = (path) => {
  return `${WS_BASE_URL}${path}`;
};