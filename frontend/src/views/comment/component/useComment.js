import { ref } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { formatDate } from '@/utils/date.js'
import { deleteById } from '@/api/post/comment'

export function useComment(fetchCommentsFn) {
  const pagination = ref({
    current: 1,
    size: 10,
    total: 0
  })

  const commentList = ref([])
  const loading = ref(false)
  const selectedIds = ref([])
  const replyCountCache = ref(new Map())
  const titleOptions = ref([])
  const replyDialogVisible = ref(false)
  const currentReplies = ref([])

  // 搜索表单
  const searchForm = ref({
    content: '',
    title: '',
    dateRange: []
  })

  // 日期快捷选项
  const dateShortcuts = [
    {
      text: '最近一周',
      value: () => {
        const end = new Date()
        const start = new Date()
        start.setTime(start.getTime() - 3600 * 1000 * 24 * 7)
        return [start, end]
      }
    },
    {
      text: '最近一月',
      value: () => {
        const end = new Date()
        const start = new Date()
        start.setMonth(start.getMonth() - 1)
        return [start, end]
      }
    },
    {
      text: '最近三月',
      value: () => {
        const end = new Date()
        const start = new Date()
        start.setMonth(start.getMonth() - 3)
        return [start, end]
      }
    }
  ]

  // 获取评论列表
  const fetchComments = async () => {
    try {
      loading.value = true
      const params = {
        ...searchForm.value,
        page: pagination.value.current,
        size: pagination.value.size,
        startDate: searchForm.value.dateRange?.[0],
        endDate: searchForm.value.dateRange?.[1]
      }

      const res = await fetchCommentsFn(params)
      if (res.data.status === 200) {
        replyCountCache.value.clear()
        commentList.value = res.data.data
        pagination.value.total = res.data.data.length

        const uniqueTitles = [...new Set(res.data.data.map(item => item.title))]
        titleOptions.value = uniqueTitles.map(title => ({
          value: title,
          label: title
        }))
      }
    } catch (error) {
      console.error('获取评论列表失败:', error)
      ElMessage.error('获取评论列表失败')
    } finally {
      loading.value = false
    }
  }

  // CSV导出相关
  const escapeCsvContent = (content) => {
    if (!content) return ''
    const escaped = content.toString().replace(/"/g, '""')
    return /[,"\n\r]/.test(escaped) ? `"${escaped}"` : escaped
  }

  // 导出评论
  const handleExport = () => {
    try {
      loading.value = true
      const headers = ['序号', '评论内容', '评论人', '文章标题', '点赞数', '评论时间']
      const rows = commentList.value.map((item, index) => [
        index + 1,
        escapeCsvContent(item.content),
        escapeCsvContent(item.username),
        escapeCsvContent(item.title),
        item.likes,
        formatDate(item.createdAt)
      ])

      const csvContent = [headers.join(','), ...rows.map(row => row.join(','))].join('\n')
      const blob = new Blob(['\ufeff' + csvContent], {
        type: 'text/csv;charset=utf-8;'
      })

      const link = document.createElement('a')
      link.href = URL.createObjectURL(blob)
      link.download = `评论列表_${formatDate(new Date())}.csv`
      document.body.appendChild(link)
      link.click()
      document.body.removeChild(link)

      ElMessage.success('导出成功')
    } catch (error) {
      console.error('导出失败:', error)
      ElMessage.error('导出失败')
    } finally {
      loading.value = false
    }
  }

  // 回复数量计算
  const getReplyCount = (commentId) => {
    if (replyCountCache.value.has(commentId)) {
      return replyCountCache.value.get(commentId)
    }
    const count = commentList.value.filter(item => item.parentId === commentId).length
    replyCountCache.value.set(commentId, count)
    return count
  }

  // 单个删除
  const handleDelete = async (id) => {
    try {
      await ElMessageBox.confirm('确定要删除这条评论吗？', '提示', {
        type: 'warning'
      })

      const res = await deleteById(id)
      if (res.data.status === 200) {
        ElMessage.success('删除成功')
        fetchComments()
      } else {
        ElMessage.error(res.data.message || '删除失败')
      }
    } catch (error) {
      if (error !== 'cancel') {
        ElMessage.error('删除失败')
      }
    }
  }

  // 批量删除
  const handleBatchDelete = async () => {
    try {
      await ElMessageBox.confirm(
        `确定要删除选中的 ${selectedIds.value.length} 条评论吗？`,
        '提示',
        {
          type: 'warning'
        }
      )

      const promises = selectedIds.value.map(id => deleteById(id))
      const results = await Promise.all(promises)
      const allSuccess = results.every(res => res.data.status === 200)

      if (allSuccess) {
        ElMessage.success('批量删除成功')
        fetchComments()
      } else {
        ElMessage.error('部分评论删除失败')
      }
    } catch (error) {
      if (error !== 'cancel') {
        console.error('批量删除失败:', error)
        ElMessage.error('批量删除失败')
      }
    }
  }

  // 查看回复
  const handleViewReplies = (comment) => {
    const allReplies = commentList.value
      .filter(item => {
        let current = item
        while (current.parentId) {
          if (current.parentId === comment.commentId) {
            return true
          }
          current = commentList.value.find(c => c.commentId === current.parentId)
          if (!current) break
        }
        return false
      })
      .sort((a, b) => new Date(a.createdAt) - new Date(b.createdAt))

    currentReplies.value = allReplies.map(reply => {
      let level = 0
      let current = reply
      while (current.parentId && current.parentId !== comment.commentId) {
        level++
        current = commentList.value.find(c => c.commentId === current.parentId)
        if (!current) break
      }
      return { ...reply, level }
    })

    replyDialogVisible.value = true
  }

  // 搜索
  const handleSearch = () => {
    pagination.value.current = 1;
    const filteredComments = commentList.value.filter((comment) => {
      const contentMatch =
        !searchForm.value.content ||
        comment.content.toLowerCase().includes(searchForm.value.content.toLowerCase()); // 转换为小写进行模糊匹配
      const titleMatch =
        !searchForm.value.title ||
        comment.title.toLowerCase().includes(searchForm.value.title.toLowerCase()); // 转换为小写进行模糊匹配
      const dateMatch =
        !searchForm.value.dateRange?.length ||
        (comment.createdAt >= searchForm.value.dateRange[0] &&
          comment.createdAt <= searchForm.value.dateRange[1]);
      return contentMatch && titleMatch && dateMatch;
    });
    commentList.value = filteredComments;
    pagination.value.total = filteredComments.length;
  };


  // 重置搜索
  const resetSearch = () => {
    searchForm.value = {
      content: '',
      title: '',
      dateRange: []
    }
    fetchComments()
  }

  // 选择项变化
  const handleSelectionChange = (selection) => {
    selectedIds.value = selection.map(item => item.commentId)
  }

  return {
    pagination,
    commentList,
    loading,
    selectedIds,
    searchForm,
    titleOptions,
    dateShortcuts,
    replyDialogVisible,
    currentReplies,
    fetchComments,
    getReplyCount,
    handleExport,
    handleDelete,
    handleBatchDelete,
    handleViewReplies,
    handleSearch,
    resetSearch,
    handleSelectionChange
  }
}