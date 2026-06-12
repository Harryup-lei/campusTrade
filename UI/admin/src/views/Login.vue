<template>
  <div class="login-container">
    <!-- 左侧品牌展示区 -->
    <div class="brand-section">
      <div class="brand-content">
        <div class="logo-box">

        </div>
        <h1 class="brand-title">校园二手交易平台</h1>
        <p class="brand-subtitle">一站式解忧 后台管理系统</p>
        <div class="feature-list">
          <div class="feature-item">
            <el-icon><Check /></el-icon>
            <span>用户权限管理</span>
          </div>
          <div class="feature-item">
            <el-icon><Check /></el-icon>
            <span>商品审核管理</span>
          </div>
          <div class="feature-item">
            <el-icon><Check /></el-icon>
            <span>订单数据统计</span>
          </div>
          <div class="feature-item">
            <el-icon><Check /></el-icon>
            <span>平台内容管理</span>
          </div>
        </div>
      </div>
      <div class="wave-decoration">
        <svg viewBox="0 0 1200 120" preserveAspectRatio="none">
          <path d="M321.39,56.44c58-10.79,114.16-30.13,172-41.86,82.39-16.72,168.19-17.73,250.45-.39C823.78,31,906.67,72,985.66,92.83c70.05,18.48,146.53,26.09,214.34,3V0H0V27.35A600.21,600.21,0,0,0,321.39,56.44Z" class="shape-fill"></path>
        </svg>
      </div>
    </div>

    <!-- 右侧登录表单区 -->
    <div class="form-section">
      <div class="form-wrapper">
        <div class="form-header">
          <el-icon :size="48" class="admin-icon">
            <UserFilled />
          </el-icon>
          <h2>管理员登录</h2>
          <p>欢迎回来，请登录您的管理员账户</p>
        </div>

        <el-form 
          :model="loginForm" 
          :rules="rules" 
          ref="formRef" 
          class="login-form"
          @keyup.enter="handleLogin"
        >
          <el-form-item prop="username">
            <el-input 
              v-model="loginForm.username" 
              placeholder="请输入管理员用户名"
              size="large"
              clearable
            >
              <template #prefix>
                <el-icon><User /></el-icon>
              </template>
            </el-input>
          </el-form-item>

          <el-form-item prop="password">
            <el-input 
              v-model="loginForm.password" 
              type="password" 
              placeholder="请输入密码"
              size="large"
              show-password
            >
              <template #prefix>
                <el-icon><Lock /></el-icon>
              </template>
            </el-input>
          </el-form-item>

          <el-form-item>
            <div class="form-options">
              <el-checkbox v-model="rememberMe">记住密码</el-checkbox>
              <el-link type="primary" :underline="false">忘记密码？</el-link>
            </div>
          </el-form-item>

          <el-form-item>
            <el-button 
              type="primary" 
              size="large"
              @click="handleLogin" 
              :loading="loading" 
              class="login-btn"
            >
              <span v-if="!loading">立即登录</span>
              <span v-else>登录中...</span>
            </el-button>
          </el-form-item>
        </el-form>


      </div>
    </div>

    <!-- 背景装饰 -->
    <div class="bg-decoration">
      <div class="circle circle-1"></div>
      <div class="circle circle-2"></div>
      <div class="circle circle-3"></div>
    </div>
  </div>
</template>

<script setup>
// ============================================================
// 代码贡献统计（项目总计 3000 行）
// 文件: UI/admin/src/views/Login.vue
// hangu: 42 行 | kelei: 0 行 | 本文件合计: 42 行
// ============================================================
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'
import request from '@/utils/request'
import { ElMessage } from 'element-plus'
import { 
  ShoppingCart, 
  UserFilled, 
  User, 
  Lock, 
  Check,
  WarnTriangleFilled 
} from '@element-plus/icons-vue'

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
  username: [
    { required: true, message: '请输入管理员用户名', trigger: 'blur' },
    { min: 3, message: '用户名至少3个字符', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, message: '密码至少6个字符', trigger: 'blur' }
  ]
}

// 从 localStorage 读取记住的密码
onMounted(() => {
  const savedUsername = localStorage.getItem('admin_username')
  const savedPassword = localStorage.getItem('admin_password')
  if (savedUsername && savedPassword) {
    loginForm.value.username = savedUsername
    loginForm.value.password = savedPassword
    rememberMe.value = true
  }
})
const handleLogin = async () => {
  try {
    await formRef.value.validate()
  } catch {
    return
  // @contributor hangu - handleLogin (42 行)
  }
  
  loading.value = true
  
  try {
    const res = await request.post('/auth/login', loginForm.value)
    
    // 验证管理员权限
    if (res.data.user.role !== 'ADMIN') {
      ElMessage.error('无权访问管理后台，仅限管理员登录')
      return
    }
    
    // 记住密码
    if (rememberMe.value) {
      localStorage.setItem('admin_username', loginForm.value.username)
      localStorage.setItem('admin_password', loginForm.value.password)
    } else {
      localStorage.removeItem('admin_username')
      localStorage.removeItem('admin_password')
    }
    
    userStore.setToken(res.data.token)
    userStore.setUserInfo(res.data.user)
    ElMessage.success('登录成功，欢迎回来！')
    
    setTimeout(() => {
      router.push('/')
    }, 500)
  } catch (error) {
    console.error(error)
    ElMessage.error(error.response?.data?.message || '登录失败，请检查用户名和密码')
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.login-container {
  min-height: 100vh;
  display: flex;
  position: relative;
  overflow: hidden;
  background: linear-gradient(135deg, #e3f2fd 0%, #bbdefb 100%);
}

/* 背景装饰 */
.bg-decoration {
  position: absolute;
  width: 100%;
  height: 100%;
  overflow: hidden;
  z-index: 0;
}

.circle {
  position: absolute;
  border-radius: 50%;
  background: rgba(62, 107, 218, 0.08);
  animation: float 20s infinite ease-in-out;
}

.circle-1 {
  width: 300px;
  height: 300px;
  top: -150px;
  left: -150px;
  animation-delay: 0s;
}

.circle-2 {
  width: 200px;
  height: 200px;
  bottom: -100px;
  right: 10%;
  animation-delay: 2s;
}

.circle-3 {
  width: 150px;
  height: 150px;
  top: 50%;
  left: 5%;
  animation-delay: 4s;
}

@keyframes float {
  0%, 100% {
    transform: translateY(0) rotate(0deg);
  }
  50% {
    transform: translateY(-30px) rotate(180deg);
  }
}

/* 左侧品牌区 */
.brand-section {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: center;
  position: relative;
  z-index: 1;
  padding: 60px;
  color: #3e6bda;
}

.brand-content {
  max-width: 500px;
  animation: fadeInLeft 1s ease-out;
}

.logo-box {
  display: inline-block;
  padding: 20px;
  background: rgba(62, 107, 218, 0.1);
  border-radius: 20px;
  backdrop-filter: blur(10px);
  margin-bottom: 30px;
  box-shadow: 0 8px 32px rgba(62, 107, 218, 0.15);
}

.logo-icon {
  color: #3e6bda;
}

.brand-title {
  font-size: 42px;
  font-weight: bold;
  margin: 0 0 10px 0;
  color: #3e6bda;
  text-shadow: 2px 2px 4px rgba(62, 107, 218, 0.1);
}

.brand-subtitle {
  font-size: 20px;
  margin: 0 0 40px 0;
  color: #5c7cfa;
  opacity: 0.9;
}

.feature-list {
  display: grid;
  gap: 16px;
}

.feature-item {
  display: flex;
  align-items: center;
  gap: 12px;
  font-size: 16px;
  padding: 12px 20px;
  background: rgba(255, 255, 255, 0.8);
  border-radius: 10px;
  backdrop-filter: blur(5px);
  transition: all 0.3s;
  color: #3e6bda;
  box-shadow: 0 2px 8px rgba(62, 107, 218, 0.08);
}

.feature-item:hover {
  background: rgba(255, 255, 255, 0.95);
  transform: translateX(10px);
  box-shadow: 0 4px 16px rgba(62, 107, 218, 0.15);
}

.feature-item .el-icon {
  font-size: 20px;
  color: #ff6b00;
}

/* 波浪装饰 */
.wave-decoration {
  position: absolute;
  bottom: 0;
  left: 0;
  width: 100%;
  overflow: hidden;
  line-height: 0;
}

.wave-decoration svg {
  position: relative;
  display: block;
  width: calc(100% + 1.3px);
  height: 80px;
}

.wave-decoration .shape-fill {
  fill: rgba(62, 107, 218, 0.1);
}

/* 右侧表单区 */
.form-section {
  flex: 0 0 500px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: white;
  position: relative;
  z-index: 1;
  box-shadow: -10px 0 50px rgba(0, 0, 0, 0.08);
}

.form-wrapper {
  width: 100%;
  max-width: 400px;
  padding: 40px;
  animation: fadeInRight 1s ease-out;
}

.form-header {
  text-align: center;
  margin-bottom: 40px;
}

.admin-icon {
  color: #3e6bda;
  margin-bottom: 20px;
}

.form-header h2 {
  font-size: 28px;
  color: #333;
  margin: 0 0 8px 0;
}

.form-header p {
  color: #999;
  font-size: 14px;
  margin: 0;
}

.login-form {
  margin-top: 30px;
}

.login-form :deep(.el-input) {
  height: 48px;
}

.login-form :deep(.el-input__wrapper) {
  border-radius: 8px;
  box-shadow: 0 0 0 1px #dcdfe6 inset;
  transition: all 0.3s;
}

.login-form :deep(.el-input__wrapper:hover) {
  box-shadow: 0 0 0 1px #3e6bda inset;
}

.login-form :deep(.el-input__wrapper.is-focus) {
  box-shadow: 0 0 0 1px #3e6bda inset;
}

.form-options {
  width: 100%;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.login-btn {
  width: 100%;
  height: 48px;
  font-size: 16px;
  font-weight: bold;
  background: linear-gradient(135deg, #ff6b00 0%, #ff9f43 100%);
  border: none;
  transition: all 0.3s;
  border-radius: 8px;
}

.login-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 8px 20px rgba(255, 107, 0, 0.3);
}

.login-btn:active {
  transform: translateY(0);
}

.form-footer {
  margin-top: 30px;
  text-align: center;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  color: #f56c6c;
  font-size: 13px;
  padding: 12px;
  background: #fef0f0;
  border-radius: 8px;
}

.form-footer .el-icon {
  font-size: 16px;
}

/* 动画 */
@keyframes fadeInLeft {
  from {
    opacity: 0;
    transform: translateX(-50px);
  }
  to {
    opacity: 1;
    transform: translateX(0);
  }
}

@keyframes fadeInRight {
  from {
    opacity: 0;
    transform: translateX(50px);
  }
  to {
    opacity: 1;
    transform: translateX(0);
  }
}

/* 响应式设计 */
@media (max-width: 1024px) {
  .brand-section {
    display: none;
  }
  
  .form-section {
    flex: 1;
  }
}

@media (max-width: 576px) {
  .form-section {
    padding: 20px;
  }
  
  .form-wrapper {
    padding: 20px;
  }
  
  .brand-title {
    font-size: 32px;
  }
}
</style>










