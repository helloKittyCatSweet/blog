import 'websocket-polyfill'
import request from '@/utils/request'
import { userPrefix, messagePrefix } from '@/constants/api-constants.js'
import SockJS from 'sockjs-client'
import { Client } from '@stomp/stompjs'
import {getWsUrl} from "@/config/websocket"

import { useUserStore } from '@/stores'
import { ROLES } from '@/constants/role-constants'

// 更新消息
export const update = (data) => request.put(`${userPrefix}${messagePrefix}/public/update`, data, {
  headers: {
    'Content-Type': 'application/json'
  }
})

// 已读消息
export const readMessage = (senderId) =>
  request.put(`${userPrefix}${messagePrefix}/public/read/${senderId}`)

// 未读消息
export const unReadMessage = (senderId) =>
  request.put(`${userPrefix}${messagePrefix}/public/unread/${senderId}`)

// 创建消息，支持实时消息
export const create = async (data) => {
  try {
    if (stompClient && stompClient.connected) {
      const success = await sendMessageWs(data);
      if (success) {
        return { data: { data } };
      }
    }
    // WebSocket 发送失败或未连接时，使用 HTTP
    return request.post(`${userPrefix}${messagePrefix}/public/create`, data);
  } catch (error) {
    console.error('发送消息失败:', error);
    throw error;
  }
};

// 根据内容为发送方查询消息
export const findContentForSender = (data, { page = 0, size = 10, sort = ["createdAt,desc"] } = {}) =>
  request.post(`${userPrefix}${messagePrefix}/public/find/sender/${data.content}`, { params: { page, size, sort } })

// 查询发送方消息列表
export const findSenderList = (data, { page = 0, size = 10, sort = ["createdAt,desc"] } = {}) =>
  request.post(`${userPrefix}${messagePrefix}/public/find/sender/list/${data}`, { params: { page, size, sort } })

// 根据内容为接收方查询消息
export const findContentForReceiver = (data, { page = 0, size = 10, sort = ["createdAt,desc"] } = {}) =>
  request.post(`${userPrefix}${messagePrefix}/public/find/receiver/${data.content}`, { params: { page, size, sort } })

// 查询接收方消息列表
export const findReceiverList = (data, { page = 0, size = 10, sort = ["createdAt,desc"] } = {}) =>
  request.post(`${userPrefix}${messagePrefix}/public/find/receiver/list/${data}`, { params: { page, size, sort } })

// 根据用户查找联系人列表
export const findContactedUserNames = () =>
  request.get(`${userPrefix}${messagePrefix}/public/find/contacted`)

// 根据id查询消息
export const findById = (id) => request.get(`${userPrefix}${messagePrefix}/admin/find/${id}`)

// 查询对话
export const findConversation = (data) =>
  request.get(`${userPrefix}${messagePrefix}/public/find/conversation/${data.userId}/${data.receiverId}`)

// 查询所有消息
export const findAll = ({ page = 0, size = 10, sort = "createdAt,desc" } = {}) =>
  request.get(`${userPrefix}${messagePrefix}/admin/find/all`, { params: { page, size, sort } })

// 查询消息是否存在
export const existsById = (id) => request.get(`${userPrefix}${messagePrefix}/admin/exists/${id}`)

// 查询消息数量
export const count = () => request.get(`${userPrefix}${messagePrefix}/admin/count`)

// 删除消息
export const deleteById = (id) => {
  const url = `${userPrefix}${messagePrefix}/admin/delete/id/${id}`;
  console.log('Delete url:', url)
  return request.delete(url);
}

// 搜索
export const findMessagePage = (params) => request.get(`${userPrefix}${messagePrefix}/public/page`, { params })

/**
 * 搜索消息
 * @param {Object} params 搜索参数
 * @returns {Promise} 返回搜索结果
 */
export const searchMessages = (params) => request.get(`${userPrefix}${messagePrefix}/admin/search`, { params });

// 设置消息审核状态
export const setMessageOperation = (messageId, operation) =>
  request.put(`${userPrefix}${messagePrefix}/admin/operate/${messageId}/${operation}`)

/**
 * Websocket相关
 */
let stompClient = null;
let reconnectTimeout = null;
const MAX_RECONNECT_ATTEMPTS = 5;
let reconnectAttempts = 0;

// 添加一个变量来跟踪是否是主动断开连接
let isManualDisconnect = false;

// 获取WebSocket连接
const getStompClient = () => {
  return new Promise((resolve, reject) => {
    if (stompClient?.connected) {
      resolve(stompClient);
      return;
    }

    // 如果客户端存在但未连接，先断开旧连接
    if (stompClient) {
      stompClient.deactivate();
      stompClient = null;
    }

    // 如果正在重连，返回错误
    if (reconnectTimeout) {
      reject(new Error('正在尝试重新连接'));
      return;
    }

    const userStore = useUserStore();
    const token = userStore.user.token;

    if (!token) {
      reject(new Error('未获取到token'));
      return;
    }

    try {
      const wsUrl = getWsUrl(`?token=${token}`);
      const socket = new SockJS(wsUrl);
      const client = new Client({
        webSocketFactory: () => socket,
        connectHeaders: {
          'Authorization': `Bearer ${token}`
        },
        debug: (str) => console.log('[STOMP]', str),
        reconnectDelay: 5000,
        heartbeatIncoming: 4000,
        heartbeatOutgoing: 4000,
        onConnect: () => {
          console.log('WebSocket连接成功');
          reconnectAttempts = 0;
          reconnectTimeout = null;
          resolve(client);
        },
        onDisconnect: () => {
          console.log('WebSocket断开连接');
          // 只有在非主动断开的情况下才重连
          if (!isManualDisconnect) {
            handleReconnect();
          }
        },
        onStompError: (frame) => {
          console.error('STOMP错误:', frame);
          handleReconnect();
          reject(new Error('STOMP连接错误'));
        }
      });

      client.activate();
      stompClient = client;
    } catch (error) {
      console.error('WebSocket连接失败:', error);
      handleReconnect();
      reject(error);
    }
  });
};


// 处理重连逻辑
const handleReconnect = () => {
  if (reconnectAttempts >= MAX_RECONNECT_ATTEMPTS) {
    console.error('达到最大重连次数，停止重连');
    return;
  }

  if (reconnectTimeout) {
    clearTimeout(reconnectTimeout);
  }

  reconnectTimeout = setTimeout(async () => {
    try {
      reconnectAttempts++;
      console.log(`尝试重新连接 (${reconnectAttempts}/${MAX_RECONNECT_ATTEMPTS})`);
      await getStompClient();
      reconnectTimeout = null;
    } catch (error) {
      console.error('重连失败:', error);
    }
  }, 5000 * Math.min(reconnectAttempts + 1, 5)); // 递增重连延迟，最大25秒
};

export const connectWebSocket = async ({ userId, messageCallback, statusCallback }) => {
  try {
    const client = await getStompClient();

    // 订阅消息
    const subscriptions = []

    // 订阅个人消息
    subscriptions.push(
      client.subscribe(`/user/${userId}/queue/messages`, (message) => {
        try {
          const receivedMessage = JSON.parse(message.body);
          messageCallback(receivedMessage);
        } catch (error) {
          console.error('消息解析错误:', error);
        }
      })
    );


    // 订阅对话消息
    subscriptions.push(
      client.subscribe(`/topic/chat/${userId}`, (message) => {
        try {
          const receivedMessage = JSON.parse(message.body);
          messageCallback(receivedMessage);
        } catch (error) {
          console.error('消息解析错误:', error);
        }
      })
    );

    // 订阅消息状态更新
    subscriptions.push(
      client.subscribe(`/user/${userId}/queue/message-status`, (status) => {
        try {
          const statusUpdate = JSON.parse(status.body);
          statusCallback(statusUpdate);
        } catch (error) {
          console.error('状态更新解析错误:', error);
        }
      })
    );


    const userStore = useUserStore();
    const roles = userStore.user.roles;
    // 判断是否是超级管理
    const isSystemAdmin = roles.includes(ROLES.SYSTEM_ADMINISTRATOR);

    // 订阅所有可能的系统消息通道
    if (isSystemAdmin) {
      // 超级管理员订阅所有角色的消息
      Object.values(ROLES).forEach(role => {
        subscriptions.push(
          client.subscribe(`/topic/system-message/${role}`, (message) => {
            try {
              console.log('收到系统消息:', message);
              const systemMessage = JSON.parse(message.body);
              messageCallback(systemMessage);
            } catch (error) {
              console.error('系统消息解析错误:', error);
            }
          })
        );
      });
    } else {
      // 其他用户只订阅自己拥有的角色的消息
      roles.forEach(role => {
        subscriptions.push(
          client.subscribe(`/topic/system-message/${role}`, (message) => {
            try {
              console.log('收到系统消息:', message);
              const systemMessage = JSON.parse(message.body);
              messageCallback(systemMessage);
            } catch (error) {
              console.error('系统消息解析错误:', error);
            }
          })
        );
      });
    }
    return () => {
      // 返回清理函数，用于取消订阅
      subscriptions.forEach(subscription => subscription.unsubscribe());
    };
  } catch (error) {
    console.error('WebSocket连接失败:', error);
    throw error;
  }
};

// 断开WebSocket连接

export const disconnectWebSocket = () => {
  if (stompClient) {
    try {
      // 标记为主动断开
      isManualDisconnect = true;
      stompClient.deactivate();
      stompClient = null;

      if (reconnectTimeout) {
        clearTimeout(reconnectTimeout);
        reconnectTimeout = null;
      }
      reconnectAttempts = 0;
    } catch (error) {
      console.error('断开WebSocket连接时发生错误:', error);
    }
  }
};

// 通过WebSocket发送消息
export const sendMessageWs = (message) => {
  return new Promise((resolve, reject) => {
    if (!stompClient || !stompClient.connected) {
      console.log('WebSocket未连接，返回false');
      resolve(false);
      return;
    }

    const userStore = useUserStore();
    const token = userStore.user.token;

    try {
      stompClient.publish({
        destination: '/app/chat',
        body: JSON.stringify(message),
        headers: {
          'Authorization': `Bearer ${token}`,
          'priority': '9',
          'Content-Type': 'application/json'
        }
      });

      // 添加延时检查
      setTimeout(() => {
        if (stompClient.connected) {
          resolve(true);
        } else {
          console.log('WebSocket连接已断开，返回false');
          resolve(false);
        }
      }, 500);

    } catch (error) {
      console.error('WebSocket发送消息失败:', error);
      resolve(false);
    }
  });
};

// 更新消息状态也需要添加认证头
export const updateMessageStatusWs = (statusUpdate) => {
  if (stompClient?.active) {
    const userStore = useUserStore();
    const token = userStore.user.token;

    stompClient.publish({
      destination: '/app/message-status',
      body: JSON.stringify(statusUpdate),
      headers: {
        'Authorization': `Bearer ${token}`
      }
    });
  }
};


// 导出stompClient状态
export const getWebSocketStatus = () => {
  return stompClient?.active || false;
};

/**
 * 检查系统消息状态
 * @param {string} messageId 消息ID
 * @returns {Promise} 返回消息状态
 */
export const checkSystemMessageStatus = (messageId) =>
  request.get(`${userPrefix}${messagePrefix}/admin/system-message/status/${messageId}`);


export function sendSystemMessage(data) {
  return new Promise(async (resolve, reject) => {
    if (!stompClient || !stompClient.connected) {
      console.error('WebSocket未连接');
      reject(new Error('WebSocket未连接'));
      return;
    }

    const userStore = useUserStore();
    const token = userStore.user.token;

    try {
      const messageData = {
        message: data.message.trim(),
        targetRole: data.targetRole,
      };

      console.log('发送系统消息:', messageData);

      stompClient.publish({
        destination: '/app/system-message',
        body: JSON.stringify(messageData),
        headers: {
          'Authorization': `Bearer ${token}`,
          'Content-Type': 'application/json'
        },
        skipContentLengthHeader: true
      });

      // 等待一段时间检查消息是否发送成功
      setTimeout(() => {
        if (stompClient.connected) {
          resolve(true);
        } else {
          reject(new Error('消息发送失败'));
        }
      }, 1000);

    } catch (error) {
      console.error('WebSocket发送系统消息失败:', error);
      reject(error);
    }
  });
}

// 检查WebSocket连接状态
export function checkWebSocketConnection() {
  return new Promise((resolve) => {
    // 根据你的WebSocket实现来检查连接状态

    const client = getStompClient();
    resolve(client && client.connected);
  });
}

/**
 * 获取管理员发送的系统消息历史
 * @returns {Promise} 返回系统消息列表
 */
export const getAdminSystemMessages = ({ page = 0, size = 10, sort = "createdAt,desc", keyword = '', targetRole = '' } = {}) =>
  request.get(`/ws/admin/system-messages`, { params: { page, size, sort, keyword, targetRole } });

/**
 * 获取用户收到的系统消息
 * @returns {Promise} 返回系统消息列表
 */
export const getUserSystemMessages = ({ page = 0, size = 10, sort = "createdAt,desc", keyword = '', targetRole = '' } = {}) =>
  request.get(`/ws/system-messages`, { params: { page, size, sort, keyword, targetRole } });

export const deleteSystemMessage = (messageId) =>
  request.delete(`/ws/system-message/${messageId}`);

export const markSystemMessageAsRead = (messageId) =>
  request.put(`/ws/system-message/${messageId}/read`);

export const markSystemMessageAsUnread = (messageId) =>
  request.put(`/ws/system-message/${messageId}/unread`);