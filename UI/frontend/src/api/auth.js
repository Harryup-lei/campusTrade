// ============================================================
// 代码贡献统计（项目总计 3000 行）
// 文件: UI/frontend/src/api/auth.js
// hangu: 0 行 | kelei: 0 行 | 本文件合计: 0 行
// ============================================================
import request from '@/utils/request'

export const login = (data) => {
  return request.post('/auth/login', data)
}

export const register = (data) => {
  return request.post('/auth/register', data)
}

export const getUserInfo = () => {
  return request.get('/auth/info')
}










