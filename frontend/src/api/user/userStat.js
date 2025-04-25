import request from "@/utils/request"
import { statPrefix } from "@/constants/api-constants"

/**
 * 用户个人数据统计
 * @returns
 */

export const getUserDashboard = () =>
  request.get(`${statPrefix}/public/dashboard/stats`)

export const getUserMonthlyStats = () =>
  request.get(`${statPrefix}/public/dashboard/monthly-stats`)

export const getUserRecentPosts = () =>
  request.get(`${statPrefix}/public/dashboard/recent-posts`)

export const getUserInteractions = () =>
  request.get(`${statPrefix}/public/dashboard/interaction-stats`)

/**
 * 系统数据统计
 * @returns
 */

export const getAdminDashboard = () =>
  request.get(`${statPrefix}/admin/dashboard/stats`)

export const getAdminMonthlyStats = () =>
  request.get(`${statPrefix}/admin/dashboard/monthly-stats`)

export const getAdminRecentPosts = () =>
  request.get(`${statPrefix}/admin/dashboard/recent-posts`)

export const getAdminInteractions = () =>
  request.get(`${statPrefix}/admin/dashboard/interaction-stats`)