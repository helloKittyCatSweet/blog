/**
 * 格式化日期
 * @param {Date|string} date - 日期对象或日期字符串
 * @param {string} format - 格式字符串，默认 'YYYY-MM-DD'
 * @returns {string} 格式化后的日期字符串
 */
export function formatDate(date, format = 'YYYY-MM-DD') {
  if (!date) return ''

  let d = new Date(date)
  if (isNaN(d.getTime())) return date // 如果无法解析为日期，返回原值

  const padZero = (num) => (num < 10 ? `0${num}` : num)

  const replacements = {
    YYYY: d.getFullYear(),
    MM: padZero(d.getMonth() + 1),
    DD: padZero(d.getDate()),
    HH: padZero(d.getHours()),
    mm: padZero(d.getMinutes()),
    ss: padZero(d.getSeconds())
  }

  return format.replace(/YYYY|MM|DD|HH|mm|ss/g, (match) => replacements[match])
}

/**
 * 格式化日期时间（简短格式）
 * @param {Date|string} date - 日期对象或日期字符串
 * @returns {string} 格式为 'YYYY-MM-DD HH:mm' 的字符串
 */
export function formatDateTime(date) {
  return formatDate(date, 'YYYY-MM-DD HH:mm')
}
