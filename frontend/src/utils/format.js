import { dayjs } from 'element-plus'

// 日期格式化
export const formatTime = (time) => dayjs(time).format('YYYY年MM月DD日')

// 日期时间格式化
export const formatDateTime = (time) => dayjs(time).format('YYYY年MM月DD日 HH:mm:ss')

// 简短日期时间格式化
export const formatShortDateTime = (time) => dayjs(time).format('MM-DD HH:mm')

// 相对时间格式化（如：3小时前）
export const formatRelativeTime = (time) => {
  const now = dayjs()
  const target = dayjs(time)
  const diff = now.diff(target, 'minute')

  if (diff < 60) {
    return `${diff}分钟前`
  } else if (diff < 1440) {
    return `${Math.floor(diff / 60)}小时前`
  } else if (diff < 43200) {
    return `${Math.floor(diff / 1440)}天前`
  } else {
    return formatTime(time)
  }
}

// 自定义格式化
export const formatCustomTime = (time, format) => dayjs(time).format(format)