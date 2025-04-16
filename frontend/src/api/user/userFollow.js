import request from "@/utils/request";
import { userPrefix, userFollowPrefix } from "@/constants/api-constants";

// 关注用户
export const followUser = (userId) => request.post(`${userPrefix}${userFollowPrefix}/public/${userId}`);

// 取消关注用户
export const unfollowUser = (userId) => request.delete(`${userPrefix}${userFollowPrefix}/public/${userId}`);

// 获取用户的关注列表
export const getFollowingList = (userId) => request.get(`${userPrefix}${userFollowPrefix}/public/following/${userId}`);

// 获取用户的粉丝列表
export const getFollowersList = (userId) => request.get(`${userPrefix}${userFollowPrefix}/public/followers/${userId}`);

// 检查是否关注
/**
 * 检查是否关注
 * @param {number} followerId - 关注者ID
 * @param {number} followingId - 被关注者ID
 * @returns {Promise} 请求Promise
 */
export const checkFollowing = (followerId, followingId) => request.get(`${userPrefix}${userFollowPrefix}/public/check/${followerId}/${followingId}`);