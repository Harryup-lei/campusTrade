// ============================================================
// 代码贡献统计（项目总计 3000 行）
// 文件: UI/frontend/src/api/file.js
// hangu: 0 行 | kelei: 0 行 | 本文件合计: 0 行
// ============================================================
import request from '@/utils/request'

// 上传文件
export const uploadFile = (data) => {
  return request.post('/files/upload', data, {
    headers: {
      'Content-Type': 'multipart/form-data'
    }
  })
}










