<template>
  <div class="login-page">
    <div class="login-container">
      <!-- 左侧插图/Logo区域 -->
      <div class="login-header">
        <div class="logo">
          <div class="logo-circle">🎓</div>
          <h1>校园二手交易平台</h1>
        </div>
        <p class="subtitle">安全 · 便捷 · 信任</p>
      </div>

      <!-- 登录卡片 -->
      <el-card class="login-card">
        <h2 class="form-title">账号登录</h2>
        
        <el-form :model="loginForm" :rules="rules" ref="formRef" size="large" class="login-form">
          <el-form-item prop="username">
            <el-input 
              v-model="loginForm.username" 
              placeholder="用户名/手机号"
              :prefix-icon="User"
            />
          </el-form-item>
          
          <el-form-item prop="password">
            <el-input 
              v-model="loginForm.password" 
              type="password" 
              placeholder="密码"
              :prefix-icon="Lock"
              show-password
              @keyup.enter="handleLogin"
            />
          </el-form-item>

          <div class="form-options">
            <el-checkbox v-model="rememberMe">记住我</el-checkbox>
            <el-link type="primary" :underline="false">忘记密码?</el-link>
          </div>

          <el-form-item>
            <el-button type="primary" @click="handleLogin" :loading="loading" class="submit-btn">
              登 录
            </el-button>
          </el-form-item>

          <div class="form-footer">
            <span>还没有账号？</span>
            <el-link type="primary" @click="$router.push('/register')">立即注册</el-link>
            <el-divider direction="vertical" />
            <el-link type="info" @click="$router.push('/')">返回首页</el-link>
          </div>
        </el-form>
      </el-card>
    </div>
  </div>
</template>

<script setup>
// ============================================================
// 代码贡献统计（项目总计 3000 行）
// 文件: UI/frontend/src/views/Login.vue
// hangu: 0 行 | kelei: 0 行 | 本文件合计: 0 行
// ============================================================
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { login } from '@/api/auth'
import { ElMessage } from 'element-plus'
import { User, Lock, HomeFilled } from '@element-plus/icons-vue'

const router = useRouter()
const userStore = useUserStore()
const formRef = ref(null)
const loading = ref(false)
const rememberMe = ref(false)

const loginForm = ref({
  username: '',
  password: ''
})

const rules = {
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }]
}

const handleLogin = async () => {
  if (!formRef.value) return
  await formRef.value.validate(async (valid) => {
    if (valid) {
      loading.value = true
      try {
        const res = await login(loginForm.value)
        if (res.code === 200) {
          userStore.setToken(res.data.token)
          userStore.setUserInfo(res.data.user)
          ElMessage.success('登录成功')
          router.push('/')
        } else {
          ElMessage.error(res.message || '登录失败')
        }
      } catch (error) {
        console.error(error)
        ElMessage.error('登录请求失败')
      } finally {
        loading.value = false
      }
    }
  })
}
</script>

<style scoped>
.login-page {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: #f0f2f5;
  background-image: url('https://gw.alipayobjects.com/zos/rmsportal/TVYTbAXWheQpRcWDaDMu.svg');
  background-repeat: no-repeat;
  background-position: center 110px;
  background-size: 100%;
}

.login-container {
  width: 400px;
  padding: 20px;
}

.login-header {
  text-align: center;
  margin-bottom: 40px;
}

.logo {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 12px;
  margin-bottom: 12px;
}

.logo-circle {
  width: 48px;
  height: 48px;
  background: linear-gradient(135deg, #3e6bda 0%, #578bf9 100%);
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 24px;
  color: #fff;
  box-shadow: 0 4px 12px rgba(62, 107, 218, 0.3);
}

.login-header h1 {
  margin: 0;
  font-size: 28px;
  color: #333;
  font-weight: 600;
}

.subtitle {
  margin: 0;
  color: #909399;
  font-size: 14px;
}

.login-card {
  border-radius: 12px;
  box-shadow: 0 8px 24px rgba(0,0,0,0.05);
  border: none;
}

.login-card :deep(.el-card__body) {
  padding: 40px;
}

.form-title {
  margin: 0 0 30px 0;
  font-size: 20px;
  color: #333;
  text-align: center;
  font-weight: 500;
}

.submit-btn {
  width: 100%;
  font-weight: 600;
  letter-spacing: 2px;
}

.form-options {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
}

.form-footer {
  text-align: center;
  font-size: 14px;
  color: #606266;
}
</style>










