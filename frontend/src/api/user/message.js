import request from '@/utils/request'
import { userPrefix, messagePrefix } from '@/constants/api-constants.js'

// 更新消息
export const update = (data) => request.put(`${userPrefix}${messagePrefix}/public/update`)

// 阅读消息
export const readMessage = (messageId, isRead) =>
  request.put(`${userPrefix}${messagePrefix}/public/read/${messageId}/${isRead}`)

// 创建消息
export const create = (data) => request.post(`${userPrefix}${messagePrefix}/public/create`)

// 根据内容为发送方查询消息
export const findContentForSender = (data) =>
  request.post(`${userPrefix}${messagePrefix}/public/find/sender/${data.senderId}/${data.content}`)

// 查询发送方消息列表
export const findSenderList = (data) =>
  request.post(`${userPrefix}${messagePrefix}/public/find/sender/list/${data}`)

// 根据内容为接收方查询消息
export const findContentForReceiver = (data) =>
  request.post(`${userPrefix}${messagePrefix}/public/find/receiver/${data.receiverId}/${data.content}`)

// 查询接收方消息列表
export const findReceiverList = (data) =>
  request.post(`${userPrefix}${messagePrefix}/public/find/receiver/list/${data}`)

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
