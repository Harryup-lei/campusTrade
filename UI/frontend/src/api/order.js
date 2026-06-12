// ============================================================
// 代码贡献统计（项目总计 3000 行）
// 文件: UI/frontend/src/api/order.js
// hangu: 0 行 | kelei: 0 行 | 本文件合计: 0 行
// ============================================================
import request from '@/utils/request'

// 创建订单
export const createOrder = (data) => {
  return request.post('/orders', data)
}

// 获取买家订单
export const getBuyerOrders = (params) => {
  return request.get('/orders/buyer', { params })
}

// 获取卖家订单
export const getSellerOrders = (params) => {
  return request.get('/orders/seller', { params })
}

// 支付订单
export const payOrder = (id) => {
  return request.put(`/orders/${id}/pay`)
}

// 发货
export const shipOrder = (id) => {
  return request.put(`/orders/${id}/ship`)
}

// 完成订单
export const completeOrder = (id) => {
  return request.put(`/orders/${id}/complete`)
}

// 取消订单
export const cancelOrder = (id) => {
  return request.put(`/orders/${id}/cancel`)
}










