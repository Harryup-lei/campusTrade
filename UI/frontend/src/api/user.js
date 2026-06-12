// ============================================================
// 代码贡献统计（项目总计 3000 行）
// 文件: UI/frontend/src/api/user.js
// hangu: 0 行 | kelei: 0 行 | 本文件合计: 0 行
// ============================================================
import request from '@/utils/request'

// 更新用户信息
export const updateUserInfo = (data) => {
  return request.put('/users/info', data)
}

// 修改密码
export const updatePassword = (data) => {
  return request.put('/users/password', data)
}

// 实名认证
export const verifyIdentity = (data) => {
  return request.post('/users/verify', data)
}










