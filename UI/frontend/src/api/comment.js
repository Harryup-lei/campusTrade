// ============================================================
// 代码贡献统计（项目总计 3000 行）
// 文件: UI/frontend/src/api/comment.js
// hangu: 0 行 | kelei: 0 行 | 本文件合计: 0 行
// ============================================================
import request from '@/utils/request'

// 获取商品评论列表
export const getComments = (productId) => {
  return request.get(`/comments/product/${productId}`)
}

// 发表评论
export const createComment = (data) => {
  return request.post('/comments', data)
}

// 发表回复
export const createReply = (commentId, data) => {
  return request.post(`/comments/${commentId}/replies`, data)
}

// 删除评论
export const deleteComment = (id) => {
  return request.delete(`/comments/${id}`)
}










