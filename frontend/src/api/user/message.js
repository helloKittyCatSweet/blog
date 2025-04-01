import 'websocket-polyfill'
import request from '@/utils/request'
import { userPrefix, messagePrefix } from '@/constants/api-constants.js'
import SockJS from 'sockjs-client'
import { Client } from '@stomp/stompjs'

import { useUserStore } from '@/stores'

// 更新消息
export const update = (data) => request.put(`${userPrefix}${messagePrefix}/public/update`, data)

// 阅读消息
export const readMessage = (messageId, isRead) =>
  request.put(`${userPrefix}${messagePrefix}/public/read/${messageId}/${isRead}`)

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
export const findContentForSender = (data) =>
  request.post(`${userPrefix}${messagePrefix}/public/find/sender/${data.content}`)

// 查询发送方消息列表
export const findSenderList = (data) =>
  request.post(`${userPrefix}${messagePrefix}/public/find/sender/list/${data}`)

// 根据内容为接收方查询消息
export const findContentForReceiver = (data) =>
  request.post(`${userPrefix}${messagePrefix}/public/find/receiver/${data.content}`)

// 查询接收方消息列表
export const findReceiverList = (data) =>
  request.post(`${userPrefix}${messagePrefix}/public/find/receiver/list/${data}`)

// 根据用户查找联系人列表
export const findContactedUserNames = () =>
  request.get(`${userPrefix}${messagePrefix}/public/find/contacted`)

// 根据id查询消息
export const findById = (id) => request.get(`${userPrefix}${messagePrefix}/admin/find/${id}`)

// 查询对话
export const findConversation = (data) =>
  request.get(`${userPrefix}${messagePrefix}/public/find/conversation/
    ${data.userId}/${data.receiverId}`)

// 查询所有消息
export const findAll = () => request.get(`${userPrefix}${messagePrefix}/admin/find/all`)

// 查询消息是否存在
export const existsById = (id) => request.get(`${userPrefix}${messagePrefix}/admin/exists/${id}`)

// 查询消息数量
export const count = () => request.get(`${userPrefix}${messagePrefix}/admin/count`)

// 删除消息
export const deleteById = (id) => request.delete(`${userPrefix}${messagePrefix}
  /admin/delete/id/${id}`)

// Websocket相关
let stompClient = null
const wsPrefix = 'http://localhost:8080'

export const connectWebSocket = async ({ userId, messageCallback, statusCallback }) => {
  try {
    // 获取WebSocket连接信息
    const userStore = useUserStore();
    const token = userStore.user.token;

    if (!token) {
      console.error('未获取到token');
      return;
    }

    // 创建Sockjs链接
    const socket = new SockJS(`${wsPrefix}/ws?token=${token}`, null, {
      transports: ['websocket', 'xhr-streaming', 'xhr-polling'],
    });
    console.log(token)

    // 创建STOMP客户端
    stompClient = new Client({
      webSocketFactory: () => socket,
      connectHeaders: {
        'Authorization': `Bearer ${token}`,
        'userId': userId
      },
      debug: (str) => console.log('[STOMP]', str),
      reconnectDelay: 5000,
      heartbeatIncoming: 4000,
      heartbeatOutgoing: 4000,
      onConnect: () => {
        console.log('WebSocket连接成功');

        // 订阅个人消息
        stompClient.subscribe(`/user/${userId}/queue/messages`, (message) => {
          try {
            const receivedMessage = JSON.parse(message.body);
            messageCallback(receivedMessage);
          } catch (error) {
            console.error('消息解析错误:', error);
          }
        });

        // 订阅对话消息
        stompClient.subscribe(`/topic/chat/${userId}`, (message) => {
          try {
            const receivedMessage = JSON.parse(message.body);
            messageCallback(receivedMessage);
          } catch (error) {
            console.error('消息解析错误:', error);
          }
        });

        // 订阅消息状态更新
        stompClient.subscribe(`/user/${userId}/queue/message-status`, (status) => {
          try {
            const statusUpdate = JSON.parse(status.body);
            statusCallback(statusUpdate);
          } catch (error) {
            console.error('状态更新解析错误:', error);
          }
        });
      },
      onDisconnect: () => {
        console.log('WebSocket断开连接');
      },
      onStompError: (frame) => {
        console.error('STOMP错误:', frame);
        // 可以在这里添加重连逻辑
      }
    });

    stompClient.activate();
  } catch (error) {
    console.error('WebSocket连接失败:', error);
    throw error;
  }
};

// 断开WebSocket连接
export const disconnectWebSocket = () => {
  if (stompClient) {
    stompClient.deactivate();
    stompClient = null
  }
}

// 通过WebSocket发送消息
export const sendMessageWs = (message) => {
  return new Promise((resolve, reject) => {
    if (!stompClient) {
      resolve(false);
      return;
    }

    const userStore = useUserStore();
    const token = userStore.user.token;

    // 检查连接状态并等待连接
    const waitForConnection = (callback) => {
      if (stompClient.connected) {
        callback();
      } else {
        setTimeout(() => {
          if (stompClient.connected) {
            callback();
          } else {
            waitForConnection(callback);
          }
        }, 100);
      }
    };

    waitForConnection(() => {
      try {
        stompClient.publish({
          destination: '/app/chat',
          body: JSON.stringify(message),
          headers: {
            'Authorization': `Bearer ${token}`,
            'priority': '9'
          }
        });
        resolve(true);
      } catch (error) {
        console.error('WebSocket发送消息失败:', error);
        resolve(false);
      }
    }, 100);
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