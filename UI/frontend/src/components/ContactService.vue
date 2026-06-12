<template>
  <el-dialog
    v-model="dialogVisible"
    title="联系客服"
    width="500px"
    :close-on-click-modal="false"
  >
    <el-form :model="form" :rules="rules" ref="formRef" label-width="80px">
      <el-form-item label="姓名" prop="userName">
        <el-input v-model="form.userName" placeholder="请输入您的姓名" />
      </el-form-item>
      
      <el-form-item label="联系电话" prop="contactPhone">
        <el-input v-model="form.contactPhone" placeholder="请输入您的联系电话" />
      </el-form-item>
      
      <el-form-item label="邮箱">
        <el-input v-model="form.contactEmail" placeholder="请输入您的邮箱（选填）" />
      </el-form-item>
      
      <el-form-item label="咨询类型" prop="category">
        <el-select v-model="form.category" placeholder="请选择咨询类型" style="width: 100%">
          <el-option label="一般咨询" value="GENERAL" />
          <el-option label="投诉建议" value="COMPLAINT" />
          <el-option label="功能建议" value="SUGGESTION" />
          <el-option label="技术问题" value="TECHNICAL" />
        </el-select>
      </el-form-item>
      
      <el-form-item label="咨询主题" prop="subject">
        <el-input v-model="form.subject" placeholder="请简要描述您的问题" />
      </el-form-item>
      
      <el-form-item label="详细描述" prop="content">
        <el-input
          v-model="form.content"
          type="textarea"
          :rows="5"
          placeholder="请详细描述您的问题，以便我们更好地为您服务"
        />
      </el-form-item>
    </el-form>
    
    <template #footer>
      <el-button @click="dialogVisible = false">取消</el-button>
      <el-button type="primary" @click="handleSubmit" :loading="loading">提交</el-button>
    </template>
  </el-dialog>
</template>

<script setup>
// ============================================================
// 代码贡献统计（项目总计 3000 行）
// 文件: UI/frontend/src/components/ContactService.vue
// hangu: 0 行 | kelei: 0 行 | 本文件合计: 0 行
// ============================================================
import { ref, reactive } from 'vue'
import { ElMessage } from 'element-plus'
import { useUserStore } from '@/stores/user'
import request from '@/utils/request'

const userStore = useUserStore()
const dialogVisible = ref(false)
const formRef = ref(null)
const loading = ref(false)

const form = reactive({
  userName: '',
  contactPhone: '',
  contactEmail: '',
  category: 'GENERAL',
  subject: '',
  content: ''
})

const rules = {
  userName: [
    { required: true, message: '请输入您的姓名', trigger: 'blur' }
  ],
  contactPhone: [
    { required: true, message: '请输入您的联系电话', trigger: 'blur' },
    { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号码', trigger: 'blur' }
  ],
  category: [
    { required: true, message: '请选择咨询类型', trigger: 'change' }
  ],
  subject: [
    { required: true, message: '请输入咨询主题', trigger: 'blur' }
  ],
  content: [
    { required: true, message: '请详细描述您的问题', trigger: 'blur' },
    { min: 10, message: '请至少输入10个字符', trigger: 'blur' }
  ]
}

const open = () => {
  // 自动填充登录用户信息
  if (userStore.isLoggedIn && userStore.userInfo) {
    form.userName = userStore.userInfo.nickname || userStore.userInfo.username || ''
    form.contactPhone = userStore.userInfo.phone || ''
    form.contactEmail = userStore.userInfo.email || ''
  }
  
  dialogVisible.value = true
}

const handleSubmit = async () => {
  try {
    await formRef.value.validate()
    loading.value = true
    
    await request.post('/customer-service/submit', form)
    
    ElMessage.success('提交成功！我们会尽快处理您的咨询')
    dialogVisible.value = false
    
    // 重置表单
    formRef.value.resetFields()
    
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('提交失败，请稍后重试')
    }
  } finally {
    loading.value = false
  }
}

defineExpose({
  open
})
</script>

<style scoped>
:deep(.el-dialog) {
  border-radius: 8px;
}
</style>










