// ============================================================
// 代码贡献统计（项目总计 3000 行）
// 文件: UI/frontend/src/api/free.js
// hangu: 0 行 | kelei: 0 行 | 本文件合计: 0 行
// ============================================================
import request from '@/utils/request'

// 获取免费赠送列表
export const getFreeList = (params) => {
  return request.get('/free', { params })
}

// 获取赠送详情
export const getFreeItem = (id) => {
  return request.get(`/free/${id}`)
}

// 发布赠送
export const createFree = (data) => {
  return request.post('/free', data)
}

// 更新赠送
export const updateFree = (id, data) => {
  return request.put(`/free/${id}`, data)
}

// 标记为已送出
export const completeFree = (id) => {
  return request.put(`/free/${id}/complete`)
}

// 重新上架
export const reopenFree = (id) => {
  return request.put(`/free/${id}/reopen`)
}

// 删除赠送
export const deleteFree = (id) => {
  return request.delete(`/free/${id}`)
}

// 获取我的赠送
export const getMyFree = (params) => {
  return request.get('/free/my', { params })
}










