// ============================================================
// 代码贡献统计（项目总计 3000 行）
// 文件: UI/frontend/src/api/want.js
// hangu: 0 行 | kelei: 0 行 | 本文件合计: 0 行
// ============================================================
import request from '@/utils/request'

// 获取求购列表
export const getWants = (params) => {
  return request.get('/wants', { params })
}

// 获取求购详情
export const getWant = (id) => {
  return request.get(`/wants/${id}`)
}

// 发布求购
export const createWant = (data) => {
  return request.post('/wants', data)
}

// 更新求购
export const updateWant = (id, data) => {
  return request.put(`/wants/${id}`, data)
}

// 关闭求购
export const closeWant = (id) => {
  return request.put(`/wants/${id}/close`)
}

// 重新开启求购
export const reopenWant = (id) => {
  return request.put(`/wants/${id}/reopen`)
}

// 删除求购
export const deleteWant = (id) => {
  return request.delete(`/wants/${id}`)
}

// 获取我的求购
export const getMyWants = (params) => {
  return request.get('/wants/my', { params })
}

// 提交出价
export const submitOffer = (data) => {
  return request.post('/want-offers', data)
}

// 获取某个求购的所有出价
export const getOffersByWantId = (wantId) => {
  return request.get(`/want-offers/want/${wantId}`)
}

// 获取我的出价列表（卖家视角）
export const getMyOffers = (params) => {
  return request.get('/want-offers/my', { params })
}

// 获取我收到的出价（买家视角）
export const getReceivedOffers = (params) => {
  return request.get('/want-offers/received', { params })
}

// 接受出价
export const acceptOffer = (offerId) => {
  return request.put(`/want-offers/${offerId}/accept`)
}

// 拒绝出价
export const rejectOffer = (offerId) => {
  return request.put(`/want-offers/${offerId}/reject`)
}










