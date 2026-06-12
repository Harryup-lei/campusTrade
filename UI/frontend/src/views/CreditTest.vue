<template>
  <div class="credit-test-page">
    <div class="container">
      <el-card>
        <template #header>
          <h2>🧪 信用分功能测试页面</h2>
        </template>

        <!-- 实名认证测试 -->
        <el-card shadow="hover" style="margin-bottom: 20px;">
          <template #header>
            <div style="display: flex; justify-content: space-between; align-items: center;">
              <h3>1️⃣ 实名认证 (+20分)</h3>
              <el-tag v-if="userInfo?.isVerified" type="success">已认证</el-tag>
              <el-tag v-else type="info">未认证</el-tag>
            </div>
          </template>

          <el-form :model="verifyForm" label-width="100px">
            <el-alert 
              title="认证规则" 
              type="info" 
              :closable="false"
              style="margin-bottom: 20px;"
            >
              学号必须是10位数字，且以2开头（例如：2024123456）
            </el-alert>

            <el-form-item label="学号">
              <el-input 
                v-model="verifyForm.studentId" 
                placeholder="请输入学号（10位数字，以2开头）"
                maxlength="10"
                :disabled="userInfo?.isVerified"
              />
            </el-form-item>

            <el-form-item>
              <el-button 
                type="primary" 
                @click="handleVerify"
                :loading="verifying"
                :disabled="userInfo?.isVerified"
              >
                {{ userInfo?.isVerified ? '已认证' : '提交认证' }}
              </el-button>
              <span style="margin-left: 10px; color: #67c23a;">认证成功后获得 +20 信用分</span>
            </el-form-item>
          </el-form>
        </el-card>

        <!-- 完善资料测试 -->
        <el-card shadow="hover" style="margin-bottom: 20px;">
          <template #header>
            <div style="display: flex; justify-content: space-between; align-items: center;">
              <h3>2️⃣ 完善资料 (+10分)</h3>
              <el-tag v-if="userInfo?.profileCompleted" type="success">已完善</el-tag>
              <el-tag v-else type="warning">未完善</el-tag>
            </div>
          </template>

          <el-form :model="profileForm" label-width="100px">
            <el-alert 
              title="完善规则" 
              type="info" 
              :closable="false"
              style="margin-bottom: 20px;"
            >
              需要填写：昵称、手机号、邮箱、头像（全部填写完整才算完善）
            </el-alert>

            <el-form-item label="昵称">
              <el-input 
                v-model="profileForm.nickname" 
                placeholder="请输入昵称"
              />
            </el-form-item>

            <el-form-item label="手机号">
              <el-input 
                v-model="profileForm.phone" 
                placeholder="请输入手机号"
              />
            </el-form-item>

            <el-form-item label="邮箱">
              <el-input 
                v-model="profileForm.email" 
                placeholder="请输入邮箱"
              />
            </el-form-item>

            <el-form-item label="头像">
              <el-input 
                v-model="profileForm.avatar" 
                placeholder="请输入头像URL（可用测试URL）"
              />
              <el-button 
                size="small" 
                style="margin-top: 8px;" 
                @click="useTestAvatar"
              >
                使用测试头像
              </el-button>
            </el-form-item>

            <el-form-item>
              <el-button 
                type="primary" 
                @click="handleUpdateProfile"
                :loading="updating"
              >
                保存资料
              </el-button>
              <span style="margin-left: 10px; color: #67c23a;">首次完善全部资料获得 +10 信用分</span>
            </el-form-item>
          </el-form>
        </el-card>

        <!-- 获得好评测试 -->
        <el-card shadow="hover">
          <template #header>
            <h3>3️⃣ 获得好评 (+10分)</h3>
          </template>

          <el-alert 
            title="测试方法" 
            type="success" 
            :closable="false"
            style="margin-bottom: 20px;"
          >
            <p><strong>前端测试：</strong></p>
            <ol style="margin: 10px 0; padding-left: 20px;">
              <li>访问任意商品详情页</li>
              <li>在评论区发表评论</li>
              <li>商品卖家自动获得 +10 信用分</li>
            </ol>

            <p style="margin-top: 15px;"><strong>API测试：</strong></p>
            <pre style="background: #f5f5f5; padding: 10px; border-radius: 4px; margin-top: 5px;">POST /comments
{
  "productId": 1,
  "userId": 当前用户ID,
  "content": "商品很好！"
}</pre>
          </el-alert>

          <el-button type="primary" @click="$router.push('/')">
            去首页找商品评论
          </el-button>
        </el-card>

        <!-- 查看信用记录 -->
        <div style="margin-top: 30px; text-align: center;">
          <el-button type="success" size="large" @click="$router.push('/credit')">
            查看我的信用中心
          </el-button>
        </div>
      </el-card>
    </div>
  </div>
</template>

<script setup>
// ============================================================
// 代码贡献统计（项目总计 3000 行）
// 文件: UI/frontend/src/views/CreditTest.vue
// hangu: 0 行 | kelei: 0 行 | 本文件合计: 0 行
// ============================================================
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { useUserStore } from '@/stores/user'
import request from '@/utils/request'

const router = useRouter()
const userStore = useUserStore()

const verifying = ref(false)
const updating = ref(false)
const userInfo = ref(null)

const verifyForm = reactive({
  studentId: ''
})

const profileForm = reactive({
  nickname: '',
  phone: '',
  email: '',
  avatar: ''
})

// 获取用户信息
const fetchUserInfo = async () => {
  try {
    const res = await request.get(`/users/${userStore.userInfo.id}`)
    if (res && res.data) {
      userInfo.value = res.data
      // 填充表单
      profileForm.nickname = res.data.nickname || ''
      profileForm.phone = res.data.phone || ''
      profileForm.email = res.data.email || ''
      profileForm.avatar = res.data.avatar || ''
    }
  } catch (error) {
    console.error('获取用户信息失败:', error)
  }
}

// 实名认证
const handleVerify = async () => {
  if (!verifyForm.studentId) {
    ElMessage.warning('请输入学号')
    return
  }

  if (!/^2\d{9}$/.test(verifyForm.studentId)) {
    ElMessage.error('学号格式不正确，应为10位数字且以2开头')
    return
  }

  verifying.value = true
  try {
    const res = await request.post('/users/verify', {
      studentId: verifyForm.studentId
    })
    
    if (res.code === 200) {
      ElMessage.success(res.data || '认证成功，获得20信用分！')
      fetchUserInfo()
      userStore.fetchUserInfo()
    } else {
      ElMessage.error(res.message || '认证失败')
    }
  } catch (error) {
    ElMessage.error(error.response?.data?.message || '认证失败')
  } finally {
    verifying.value = false
  }
}

// 完善资料
const handleUpdateProfile = async () => {
  if (!profileForm.nickname || !profileForm.phone || !profileForm.email || !profileForm.avatar) {
    ElMessage.warning('请填写完整所有信息')
    return
  }

  updating.value = true
  try {
    const res = await request.put('/users/info', profileForm)
    
    if (res.code === 200) {
      ElMessage.success('资料保存成功！')
      fetchUserInfo()
      userStore.fetchUserInfo()
      
      // 检查是否是首次完善
      if (!userInfo.value.profileCompleted && res.data.profileCompleted) {
        ElMessage.success('首次完善资料，获得10信用分！', { duration: 3000 })
      }
    } else {
      ElMessage.error(res.message || '保存失败')
    }
  } catch (error) {
    ElMessage.error(error.response?.data?.message || '保存失败')
  } finally {
    updating.value = false
  }
}

// 使用测试头像
const useTestAvatar = () => {
  profileForm.avatar = 'https://cube.elemecdn.com/0/88/03b0d39583f48206768a7534e55bcpng.png'
  ElMessage.success('已填充测试头像URL')
}

onMounted(() => {
  fetchUserInfo()
})
</script>

<style scoped>
.credit-test-page {
  background: #f0f2f5;
  min-height: 100vh;
  padding: 20px;
}

.container {
  max-width: 900px;
  margin: 0 auto;
}

h2 {
  margin: 0;
  color: #333;
}

h3 {
  margin: 0;
  color: #409eff;
}

pre {
  font-size: 12px;
  line-height: 1.5;
  overflow-x: auto;
}

ol {
  line-height: 1.8;
}
</style>










