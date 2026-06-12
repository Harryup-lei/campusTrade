// ============================================================
// 代码贡献统计（项目总计 3000 行）
// 文件: UI/frontend/src/api/credit.js
// hangu: 0 行 | kelei: 0 行 | 本文件合计: 0 行
// ============================================================
import request from '@/utils/request'

// 获取我的信用信息
export const getMyCredit = () => {
  return request.get('/credit/my')
}

// 获取信用记录
export const getCreditRecords = (params) => {
  return request.get('/credit/records', { params })
}

// 获取信用规则
export const getCreditRules = () => {
  return request.get('/credit/rules')
}

// 每日签到
export const dailyCheckin = () => {
  return request.post('/credit/daily-checkin')
}










