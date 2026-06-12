// ============================================================
// 代码贡献统计（项目总计 3000 行）
// 文件: UI/frontend/src/api/statistics.js
// hangu: 0 行 | kelei: 0 行 | 本文件合计: 0 行
// ============================================================
import request from '@/utils/request'

/**
 * 获取首页统计数据
 */
export function getOverviewStatistics() {
  return request({
    url: '/statistics/overview',
    method: 'get'
  })
}

/**
 * 获取个人统计数据
 */
export function getMyStatistics() {
  return request({
    url: '/statistics/user/my',
    method: 'get'
  })
}










