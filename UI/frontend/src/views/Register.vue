<template>
  <div class="register-page">
    <div class="register-container">
      <div class="register-header">
        <div class="logo">
          <div class="logo-circle">🎓</div>
          <h1>加入我们</h1>
        </div>
        <p class="subtitle">开启您的校园交易之旅</p>
      </div>

      <el-card class="register-card">
        <el-form :model="registerForm" :rules="rules" ref="formRef" size="large" class="register-form">
          <!-- 头像上传 -->
          <div class="avatar-upload-container">
            <el-upload
              class="avatar-uploader"
              action="#"
              :show-file-list="false"
              :http-request="customUpload"
              :before-upload="beforeAvatarUpload"
            >
              <img v-if="registerForm.avatar" :src="registerForm.avatar" class="avatar" />
              <el-icon v-else class="avatar-uploader-icon"><Plus /></el-icon>
              <div class="upload-tip" v-if="!registerForm.avatar">点击上传头像</div>
            </el-upload>
          </div>

          <el-form-item prop="username">
            <el-input 
              v-model="registerForm.username" 
              placeholder="设置用户名"
              :prefix-icon="User"
            />
          </el-form-item>

          <el-form-item prop="nickname">
            <el-input 
              v-model="registerForm.nickname" 
              placeholder="你的昵称"
              :prefix-icon="Edit"
            />
          </el-form-item>

          <el-form-item prop="phone">
            <el-input 
              v-model="registerForm.phone" 
              placeholder="手机号码"
              :prefix-icon="Iphone"
            />
          </el-form-item>

          <el-form-item prop="email">
            <el-input 
              v-model="registerForm.email" 
              placeholder="校园邮箱"
              :prefix-icon="Message"
            />
          </el-form-item>
          
          <el-form-item prop="password">
            <el-input 
              v-model="registerForm.password" 
              type="password" 
              placeholder="设置密码 (至少6位)"
              :prefix-icon="Lock"
              show-password
            />
          </el-form-item>

          <el-form-item prop="confirmPassword">
            <el-input 
              v-model="registerForm.confirmPassword" 
              type="password" 
              placeholder="确认密码"
              :prefix-icon="Lock"
              show-password
            />
          </el-form-item>

          <el-form-item>
            <el-button type="primary" @click="handleRegister" :loading="loading" class="submit-btn">
              立即注册
            </el-button>
          </el-form-item>

          <div class="form-footer">
            <span>已有账号？</span>
            <el-link type="primary" @click="$router.push('/login')">去登录</el-link>
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
// 文件: UI/frontend/src/views/Register.vue
// hangu: 0 行 | kelei: 0 行 | 本文件合计: 0 行
// ============================================================
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { register } from '@/api/auth'
import { uploadFile } from '@/api/file'
import { ElMessage } from 'element-plus'
import { User, Lock, Message, Edit, Plus, Iphone, HomeFilled } from '@element-plus/icons-vue'

const router = useRouter()
const formRef = ref(null)
const loading = ref(false)

const registerForm = ref({
  username: '',
  nickname: '',
  phone: '',
  email: '',
  password: '',
  confirmPassword: '',
  avatar: ''
})

const customUpload = async (options) => {
  const formData = new FormData()
  formData.append('file', options.file)
  formData.append('type', 'avatar')
  
  try {
    const res = await uploadFile(formData)
    if (res.code === 200) {
      // 拼接 /api 前缀以通过 Vite 代理访问图片
      const avatarUrl = res.data.startsWith('/api') ? res.data : '/api' + res.data
      registerForm.value.avatar = avatarUrl
      ElMessage.success('头像上传成功')
    } else {
      ElMessage.error(res.message || '头像上传失败')
    }
  } catch (error) {
    console.error(error)
    ElMessage.error('头像上传出错')
  }
}

const beforeAvatarUpload = (rawFile) => {
  const isJPG = rawFile.type === 'image/jpeg' || rawFile.type === 'image/png'
  const isLt2M = rawFile.size / 1024 / 1024 < 2

  if (!isJPG) {
    ElMessage.error('头像只能是 JPG/PNG 格式!')
    return false
  }
  if (!isLt2M) {
    ElMessage.error('头像大小不能超过 2MB!')
    return false
  }
  return true
}

const validatePassword = (rule, value, callback) => {
  if (value !== registerForm.value.password) {
    callback(new Error('两次输入密码不一致'))
  } else {
    callback()
  }
}

const rules = {
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  nickname: [{ required: true, message: '请输入昵称', trigger: 'blur' }],
  phone: [
    { required: true, message: '请输入手机号', trigger: 'blur' },
    { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号', trigger: ['blur', 'change'] }
  ],
  email: [
    { required: true, message: '请输入邮箱', trigger: 'blur' },
    { type: 'email', message: '请输入正确的邮箱格式', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, message: '密码长度不能小于6位', trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, message: '请再次输入密码', trigger: 'blur' },
    { validator: validatePassword, trigger: 'blur' }
  ]
}

const handleRegister = async () => {
  if (!formRef.value) return
  await formRef.value.validate(async (valid) => {
    if (valid) {
      loading.value = true
      try {
        const res = await register(registerForm.value)
        if (res.code === 200) {
          ElMessage.success('注册成功，请登录')
          router.push('/login')
        } else {
          ElMessage.error(res.message || '注册失败')
        }
      } catch (error) {
        console.error(error)
        ElMessage.error('注册请求失败')
      } finally {
        loading.value = false
      }
    }
  })
}
</script>

<style scoped>
.register-page {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: #f0f2f5;
  background-image: url('https://gw.alipayobjects.com/zos/rmsportal/TVYTbAXWheQpRcWDaDMu.svg');
  background-repeat: no-repeat;
  background-position: center 110px;
  background-size: 100%;
  padding: 40px 0;
}

.register-container {
  width: 440px;
  padding: 20px;
}

.register-header {
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

.register-header h1 {
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

.register-card {
  border-radius: 12px;
  box-shadow: 0 8px 24px rgba(0,0,0,0.05);
  border: none;
}

.register-card :deep(.el-card__body) {
  padding: 40px;
}

.avatar-upload-container {
  display: flex;
  justify-content: center;
  margin-bottom: 24px;
}

.avatar-uploader :deep(.el-upload) {
  border: 1px dashed #d9d9d9;
  border-radius: 50%;
  cursor: pointer;
  position: relative;
  overflow: hidden;
  transition: var(--el-transition-duration-fast);
  width: 80px;
  height: 80px;
  background: #fafafa;
}

.avatar-uploader :deep(.el-upload:hover) {
  border-color: #3e6bda;
}

.avatar-uploader-icon {
  font-size: 24px;
  color: #8c939d;
  width: 80px;
  height: 80px;
  text-align: center;
  display: flex;
  justify-content: center;
  align-items: center;
}

.avatar {
  width: 80px;
  height: 80px;
  display: block;
  object-fit: cover;
}

.upload-tip {
  position: absolute;
  bottom: 0;
  left: 0;
  width: 100%;
  font-size: 10px;
  color: #fff;
  background: rgba(0,0,0,0.5);
  text-align: center;
  line-height: 20px;
}

.submit-btn {
  width: 100%;
  font-weight: 600;
  letter-spacing: 2px;
  margin-top: 10px;
}

.form-footer {
  text-align: center;
  font-size: 14px;
  color: #606266;
}
</style>










