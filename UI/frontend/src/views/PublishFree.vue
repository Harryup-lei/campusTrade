<template>
  <div class="publish-free-page">
    <div class="main-container">
      <el-card class="publish-card">
        <template #header>
          <div class="card-header">
            <h2>{{ isEdit ? '编辑赠送' : '发布免费赠送' }}</h2>
            <p>赠人玫瑰，手有余香。让闲置物品找到新主人</p>
          </div>
        </template>

        <el-form
          ref="formRef"
          :model="form"
          :rules="rules"
          label-width="100px"
          class="publish-form"
        >
          <el-form-item label="赠送标题" prop="title">
            <el-input
              v-model="form.title"
              placeholder="简单描述你要赠送的物品，如：闲置台灯免费送"
              maxlength="100"
              show-word-limit
            />
          </el-form-item>

          <el-form-item label="物品图片" prop="images">
            <el-upload
              class="upload-demo"
              :action="uploadUrl"
              :headers="uploadHeaders"
              list-type="picture-card"
              :on-success="handleUploadSuccess"
              :on-remove="handleRemove"
              :file-list="fileList"
              :limit="6"
            >
              <el-icon><Plus /></el-icon>
            </el-upload>
            <div class="upload-tip">最多上传6张图片，第一张为封面</div>
          </el-form-item>

          <el-form-item label="详细描述" prop="description">
            <el-input
              v-model="form.description"
              type="textarea"
              :rows="6"
              placeholder="详细说明物品的状态、使用情况、交接方式等"
              maxlength="500"
              show-word-limit
            />
          </el-form-item>

          <el-form-item label="物品成色" prop="conditionDesc">
            <el-radio-group v-model="form.conditionDesc">
              <el-radio label="全新">全新</el-radio>
              <el-radio label="几乎全新">几乎全新</el-radio>
              <el-radio label="良好">良好</el-radio>
              <el-radio label="可接受">可接受</el-radio>
            </el-radio-group>
          </el-form-item>

          <el-form-item label="交接地点" prop="location">
            <el-input
              v-model="form.location"
              placeholder="如：东区宿舍楼下、图书馆门口等"
              maxlength="50"
            />
          </el-form-item>

          <el-form-item>
            <el-button type="success" size="large" @click="handleSubmit" :loading="loading">
              {{ isEdit ? '保存修改' : '发布赠送' }}
            </el-button>
            <el-button size="large" @click="$router.back()">取消</el-button>
          </el-form-item>
        </el-form>
      </el-card>
    </div>
  </div>
</template>

<script setup>
// ============================================================
// 代码贡献统计（项目总计 3000 行）
// 文件: UI/frontend/src/views/PublishFree.vue
// hangu: 42 行 | kelei: 0 行 | 本文件合计: 42 行
// ============================================================
import { ref, reactive, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import { Plus } from '@element-plus/icons-vue'
import { createFree, updateFree, getFreeItem } from '@/api/free'
import { useUserStore } from '@/stores/user'

const router = useRouter()
const route = useRoute()
const userStore = useUserStore()

const formRef = ref()
const loading = ref(false)
const isEdit = ref(false)
const freeId = ref(null)

const uploadUrl = '/api/files/upload'
const uploadHeaders = {
  'Authorization': `Bearer ${userStore.token}`
}

const form = reactive({
  title: '',
  description: '',
  images: '',
  location: '',
  conditionDesc: '良好'
})

const fileList = ref([])
const uploadedImages = ref([])

const rules = {
  title: [
    { required: true, message: '请输入赠送标题', trigger: 'blur' },
    { min: 5, max: 100, message: '标题长度在5到100个字符', trigger: 'blur' }
  ],
  description: [
    { required: true, message: '请输入详细描述', trigger: 'blur' },
    { min: 10, max: 500, message: '描述长度在10到500个字符', trigger: 'blur' }
  ],
  conditionDesc: [
    { required: true, message: '请选择物品成色', trigger: 'change' }
  ],
  location: [
    { required: true, message: '请输入交接地点', trigger: 'blur' }
  ]
}

// 图片上传成功
const handleUploadSuccess = (response, file) => {
  if (response.code === 200) {
    uploadedImages.value.push(response.data)
    form.images = uploadedImages.value.join(',')
    ElMessage.success('图片上传成功')
  } else {
    ElMessage.error(response.message || '图片上传失败')
    // 移除上传失败的文件
    const index = fileList.value.findIndex(f => f.uid === file.uid)
    if (index > -1) {
      fileList.value.splice(index, 1)
    }
  }
}

// 移除图片
const handleRemove = (file) => {
  const index = fileList.value.findIndex(f => f.uid === file.uid)
  if (index > -1) {
    uploadedImages.value.splice(index, 1)
    form.images = uploadedImages.value.join(',')
  }
}

// 加载赠送数据（编辑模式）
const loadFreeData = async (id) => {
  try {
    const res = await getFreeItem(id)
    if (res && res.data) {
      const data = res.data
      form.title = data.title
      form.description = data.description
      form.location = data.location
      form.conditionDesc = data.conditionDesc || '良好'
      
      // 处理图片
      if (data.images) {
        const images = data.images.split(',').filter(img => img)
        uploadedImages.value = images
        form.images = data.images
        
        fileList.value = images.map((img, index) => ({
          name: `image-${index}`,
          url: img.startsWith('http') ? img : `/api${img.startsWith('/') ? '' : '/'}${img}`,
          uid: Date.now() + index
        }))
      }
    }
  } catch (error) {
    console.error('加载赠送数据失败:', error)
    ElMessage.error('加载失败')
    router.back()
  }
}

// 提交
const handleSubmit = async () => {
  try {
    await formRef.value?.validate()
    
    // 验证图片
    // @contributor hangu - handleSubmit (42 行)
    if (!form.images || uploadedImages.value.length === 0) {
      ElMessage.warning('请至少上传一张图片')
      return
    }
    
    loading.value = true
    const data = {
      title: form.title,
      description: form.description,
      images: form.images,
      location: form.location,
      conditionDesc: form.conditionDesc
    }
    
    let res
    if (isEdit.value && freeId.value) {
      res = await updateFree(freeId.value, data)
    } else {
      res = await createFree(data)
    }
    
    if (res && res.code === 200) {
      ElMessage.success(isEdit.value ? '修改成功' : '发布成功')
      router.push('/free')
    } else {
      ElMessage.error(res?.message || '操作失败')
    }
  } catch (error) {
    if (error !== false) {
      console.error('提交失败:', error)
      ElMessage.error('操作失败')
    }
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  // 检查是否为编辑模式
  if (route.query.id) {
    isEdit.value = true
    freeId.value = route.query.id
    loadFreeData(freeId.value)
  }
})
</script>

<style scoped>
.publish-free-page {
  background: #f0f2f5;
  min-height: 100vh;
  padding: 20px 0;
}

.main-container {
  width: 800px;
  margin: 0 auto;
  padding: 0 16px;
}

.publish-card {
  border-radius: 12px;
}

.card-header h2 {
  margin: 0 0 8px 0;
  font-size: 24px;
  color: #333;
}

.card-header p {
  margin: 0;
  color: #67c23a;
  font-size: 14px;
}

.publish-form {
  margin-top: 20px;
}

.upload-demo {
  display: inline-block;
}

.upload-tip {
  margin-top: 8px;
  font-size: 12px;
  color: #999;
}

:deep(.el-upload-list--picture-card .el-upload-list__item) {
  width: 100px;
  height: 100px;
}

:deep(.el-upload--picture-card) {
  width: 100px;
  height: 100px;
  line-height: 100px;
}
</style>










