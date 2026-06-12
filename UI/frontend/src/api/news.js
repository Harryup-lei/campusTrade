// ============================================================
// 代码贡献统计（项目总计 3000 行）
// 文件: UI/frontend/src/api/news.js
// hangu: 0 行 | kelei: 0 行 | 本文件合计: 0 行
// ============================================================
import request from '@/utils/request'

// 获取资讯列表
export const getNewsList = (params) => {
  return request.get('/news', { params })
}

// 获取资讯详情
export const getNewsDetail = (id) => {
  return request.get(`/news/${id}`)
}

// 发布资讯（管理员）
export const createNews = (data) => {
  return request.post('/news', data)
}

// 更新资讯（管理员）
export const updateNews = (id, data) => {
  return request.put(`/news/${id}`, data)
}

// 删除资讯（管理员）
export const deleteNews = (id) => {
  return request.delete(`/news/${id}`)
}

// 获取我的资讯（管理员）
export const getMyNews = (params) => {
  return request.get('/news/my', { params })
}

// 获取启用的资讯分类
export const getNewsCategories = () => {
  return request.get('/news-categories/enabled')
}










