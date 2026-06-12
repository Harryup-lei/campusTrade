// ============================================================
// 代码贡献统计（项目总计 3000 行）
// 文件: UI/frontend/src/api/product.js
// hangu: 0 行 | kelei: 0 行 | 本文件合计: 0 行
// ============================================================
import request from '@/utils/request'

export const getProducts = (params) => {
  return request.get('/products', { params })
}

export const getProduct = (id) => {
  return request.get(`/products/${id}`)
}

export const getSellerProducts = (sellerId, params) => {
  return request.get(`/products/seller/${sellerId}`, { params })
}

export const createProduct = (data) => {
  return request.post('/products', data)
}

export const updateProduct = (id, data) => {
  return request.put(`/products/${id}`, data)
}

export const deleteProduct = (id) => {
  return request.delete(`/products/${id}`)
}

// 上架商品
export const onSaleProduct = (id) => {
  return request.put(`/products/${id}/on-sale`)
}

// 下架商品
export const offSaleProduct = (id) => {
  return request.put(`/products/${id}/off-sale`)
}










