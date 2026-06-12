// ============================================================
// 代码贡献统计（项目总计 3000 行）
// 文件: UI/frontend/src/stores/user.js
// hangu: 0 行 | kelei: 0 行 | 本文件合计: 0 行
// ============================================================
import { defineStore } from 'pinia'
import { ref, computed } from 'vue'

export const useUserStore = defineStore('user', () => {
  const token = ref(localStorage.getItem('token') || '')
  const userInfo = ref(JSON.parse(localStorage.getItem('userInfo') || 'null'))

  // 判断是否已登录
  const isLoggedIn = computed(() => {
    return !!token.value && !!userInfo.value
  })

  const setToken = (newToken) => {
    token.value = newToken
    localStorage.setItem('token', newToken)
  }

  const setUserInfo = (info) => {
    userInfo.value = info
    localStorage.setItem('userInfo', JSON.stringify(info))
  }

  const logout = () => {
    token.value = ''
    userInfo.value = null
    localStorage.removeItem('token')
    localStorage.removeItem('userInfo')
  }

  const fetchUserInfo = async () => {
    if (!token.value) return
    
    try {
      const response = await fetch('/api/auth/info', {
        headers: {
          'Authorization': `Bearer ${token.value}`
        }
      })
      const res = await response.json()
      if (res.code === 200 && res.data) {
        setUserInfo(res.data)
      }
    } catch (error) {
      console.error('获取用户信息失败:', error)
    }
  }

  return {
    token,
    userInfo,
    isLoggedIn,
    setToken,
    setUserInfo,
    logout,
    fetchUserInfo
  }
})










