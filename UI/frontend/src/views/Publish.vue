<template>
  <div class="publish-page">
    <div class="main-container">
      <div class="page-header">
        <h2>{{ isEdit ? '编辑商品' : '发布商品' }}</h2>
        <p>{{ isEdit ? '修改商品信息' : '填写商品信息，快速出手闲置好物' }}</p>
      </div>

      <div class="publish-content">
        <el-form :model="form" :rules="rules" ref="formRef" label-position="top" size="large">
          <div class="form-grid">
            <!-- 左侧：基本信息 -->
            <div class="form-section basic-info">
              <h3 class="section-title">基本信息</h3>
              
              <el-form-item label="商品标题" prop="title">
                <el-input v-model="form.title" placeholder="品牌型号 + 关键参数，如：iPhone 13 256G 蓝色"></el-input>
              </el-form-item>

              <el-form-item label="商品描述" prop="description">
                <el-input 
                  v-model="form.description" 
                  type="textarea" 
                  :rows="6" 
                  placeholder="描述一下商品的转手原因、入手渠道、新旧程度和使用感受..."
                ></el-input>
              </el-form-item>

              <el-form-item label="商品图片" prop="images">
                <div class="upload-wrapper">
                  <el-upload
                    v-model:file-list="fileList"
                    action="#"
                    list-type="picture-card"
                    :http-request="customUpload"
                    :on-preview="handlePictureCardPreview"
                    :on-remove="handleRemove"
                    multiple
                    :limit="5"
                  >
                    <el-icon><Plus /></el-icon>
                  </el-upload>
                  <el-dialog v-model="dialogVisible">
                    <img w-full :src="dialogImageUrl" alt="Preview Image" />
                  </el-dialog>
                  <div class="upload-tip">最多上传 5 张图片，第一张将作为封面</div>
                </div>
              </el-form-item>
            </div>

            <!-- 右侧：交易信息 -->
            <div class="form-section trade-info">
              <h3 class="section-title">交易信息</h3>

              <el-form-item label="分类" prop="categoryId">
                <el-select 
                  v-model="form.categoryId" 
                  placeholder="请选择商品分类" 
                  style="width: 100%"
                  :loading="categoriesLoading"
                  clearable
                >
                  <el-option 
                    v-for="cat in categories" 
                    :key="cat.id" 
                    :label="cat.name" 
                    :value="cat.id"
                  >
                    <span>{{ cat.name }}</span>
                    <span v-if="cat.description" style="color: #8492a6; font-size: 13px; margin-left: 8px;">
                      {{ cat.description }}
                    </span>
                  </el-option>
                  <template #empty>
                    <div style="padding: 10px; text-align: center; color: #909399;">
                      <p v-if="categoriesLoading">加载中...</p>
                      <p v-else>暂无可用分类</p>
                    </div>
                  </template>
                </el-select>
              </el-form-item>

              <div class="price-group">
                <el-form-item label="出售价格" prop="price">
                  <el-input-number 
                    v-model="form.price" 
                    :min="0" 
                    :precision="2" 
                    :step="1"
                    controls-position="right"
                    style="width: 100%"
                  >
                    <template #prefix>¥</template>
                  </el-input-number>
                </el-form-item>

                <el-form-item label="原价（可选）">
                  <el-input-number 
                    v-model="form.originalPrice" 
                    :min="0" 
                    :precision="2" 
                    :step="1"
                    controls-position="right"
                    style="width: 100%"
                  >
                    <template #prefix>¥</template>
                  </el-input-number>
                </el-form-item>
              </div>

              <el-form-item label="成色" prop="conditionDesc">
                <el-radio-group v-model="form.conditionDesc">
                  <el-radio-button label="全新">全新</el-radio-button>
                  <el-radio-button label="几乎全新">几乎全新</el-radio-button>
                  <el-radio-button label="良好">良好</el-radio-button>
                  <el-radio-button label="可接受">可接受</el-radio-button>
                </el-radio-group>
              </el-form-item>

              <el-form-item label="库存" prop="stock">
                <el-input-number v-model="form.stock" :min="1" style="width: 100%"></el-input-number>
              </el-form-item>

              <el-form-item label="交易位置" prop="location">
                <el-input v-model="form.location" placeholder="如：图书馆门口、三食堂"></el-input>
              </el-form-item>

              <div class="form-actions">
                <el-button type="primary" size="large" @click="handleSubmit" :loading="loading" class="submit-btn">
                  {{ isEdit ? '保存修改' : '立即发布' }}
                </el-button>
                <el-button size="large" @click="$router.back()">取消</el-button>
              </div>
            </div>
          </div>
        </el-form>
      </div>
    </div>
  </div>
</template>

<script setup>
// ============================================================
// 代码贡献统计（项目总计 3000 行）
// 文件: UI/frontend/src/views/Publish.vue
// hangu: 97 行 | kelei: 0 行 | 本文件合计: 97 行
// ============================================================
import { ref, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { getCategories } from '@/api/category'
import { createProduct, updateProduct, getProduct } from '@/api/product'
import { uploadFile } from '@/api/file'
import { ElMessage } from 'element-plus'
import { Plus } from '@element-plus/icons-vue'

const router = useRouter()
const route = useRoute()
const userStore = useUserStore()
const isEdit = ref(false)
const productId = ref(null)
const formRef = ref(null)
const loading = ref(false)
const categories = ref([])
const categoriesLoading = ref(false)
const fileList = ref([])
const dialogImageUrl = ref('')
const dialogVisible = ref(false)

const form = ref({
  title: '',
  categoryId: null,
  price: undefined,
  originalPrice: undefined,
  stock: 1,
  conditionDesc: '良好',
  location: '',
  description: '',
  images: []
})

const rules = {
  title: [{ required: true, message: '请输入商品标题', trigger: 'blur' }],
  categoryId: [{ required: true, message: '请选择分类', trigger: 'change' }],
  price: [{ required: true, message: '请输入价格', trigger: 'blur' }],
  description: [{ required: true, message: '请输入商品描述', trigger: 'blur' }],
  location: [{ required: true, message: '请输入交易位置', trigger: 'blur' }]
}

const fetchCategories = async () => {
  categoriesLoading.value = true
  try {
    const res = await getCategories()
    if (res && res.data) {
      categories.value = res.data
      if (categories.value.length === 0) {
        ElMessage.warning('暂无可用分类，请联系管理员添加')
      }
    } else {
      categories.value = []
      ElMessage.error('获取分类失败')
    }
  } catch (error) {
    console.error('获取分类失败：', error)
    ElMessage.error('获取分类列表失败，请刷新页面重试')
    categories.value = []
  } finally {
    categoriesLoading.value = false
  }
}

const customUpload = async (options) => {
  const formData = new FormData()
  formData.append('file', options.file)
  formData.append('type', 'product')
  
  try {
    const res = await uploadFile(formData)
    if (res.code === 200) {
      // 添加 /api 前缀，与后台管理保持一致
      const url = res.data.startsWith('/api') ? res.data : '/api' + res.data
      // 更新 fileList 中的 url
      const fileIndex = fileList.value.findIndex(f => f.uid === options.file.uid)
      if (fileIndex !== -1) {
        fileList.value[fileIndex].url = url
        fileList.value[fileIndex].status = 'success'
      }
      ElMessage.success('图片上传成功')
    } else {
      options.onError(new Error(res.message))
      ElMessage.error(res.message || '上传失败')
    }
  } catch (error) {
    options.onError(error)
    ElMessage.error('上传出错')
  }
}

const handleRemove = (uploadFile, uploadFiles) => {
  // 图片移除逻辑，通常不需要调后端删除，只需要更新前端列表
  console.log(uploadFile, uploadFiles)
}

const handlePictureCardPreview = (uploadFile) => {
  dialogImageUrl.value = uploadFile.url
  dialogVisible.value = true
}

// 加载商品数据（编辑模式）
const loadProductData = async (id) => {
  try {
    console.log('加载商品数据，ID:', id)
    const res = await getProduct(id)
    if (res.code === 200) {
      // @contributor hangu - loadProductData (49 行)
      const product = res.data
      
      // 填充表单数据
      form.value = {
        title: product.title,
        categoryId: product.categoryId,
        price: product.price,
        originalPrice: product.originalPrice,
        stock: product.stock,
        conditionDesc: product.conditionDesc,
        location: product.location,
        description: product.description,
        images: []
      }
      
      // 处理图片列表
      let images = []
      if (Array.isArray(product.images)) {
        images = product.images
      } else if (typeof product.images === 'string') {
        images = product.images.split(',').filter(img => img)
      }
      
      // 填充fileList用于显示
      fileList.value = images.map((url, index) => ({
        uid: Date.now() + index,
        name: `image-${index}.jpg`,
        status: 'success',
        url: url
      }))
      
      console.log('商品数据加载成功:', form.value)
      console.log('图片列表:', fileList.value)
    } else {
      ElMessage.error('加载商品数据失败')
      router.push('/profile?tab=my-products')
    }
  } catch (error) {
    console.error('加载商品数据失败:', error)
    ElMessage.error('加载失败')
    router.push('/profile?tab=my-products')
  }
}
const handleSubmit = async () => {
  if (!formRef.value) return
  await formRef.value.validate(async (valid) => {
    if (valid) {
      // 处理图片 - 改为数组格式
      const imageUrls = fileList.value
        // @contributor hangu - handleSubmit (48 行)
        .filter(f => f.status === 'success' && f.url)
        .map(f => f.url)
      if (imageUrls.length === 0) {
        ElMessage.warning('请至少上传一张图片')
        return
      }
      loading.value = true
      
      try {
        const data = {
          ...form.value,
          images: imageUrls, // 直接使用数组
          sellerId: userStore.userInfo?.id
        }
        
        console.log(isEdit.value ? '更新商品数据：' : '发布商品数据：', data)
        
        let res
        if (isEdit.value && productId.value) {
          // 编辑模式 - 更新
          res = await updateProduct(productId.value, data)
        } else {
          // 新增模式 - 创建
          res = await createProduct(data)
        }
        
        if (res.code === 200) {
          ElMessage.success(isEdit.value ? '更新成功' : '发布成功')
          router.push('/profile?tab=my-products')
        } else {
          ElMessage.error(res.message || (isEdit.value ? '更新失败' : '发布失败'))
        }
      } catch (error) {
        console.error(error)
        ElMessage.error(isEdit.value ? '更新请求失败' : '发布请求失败')
      } finally {
        loading.value = false
      }
    }
  })
}

onMounted(async () => {
  if (!userStore.token) {
    ElMessage.warning('请先登录')
    router.push('/login')
    return
  }
  
  // 检查是否是编辑模式
  if (route.query.id) {
    isEdit.value = true
    productId.value = route.query.id
    console.log('编辑模式，商品ID:', productId.value)
  }
  
  // 先加载分类数据
  await fetchCategories()
  
  // 如果是编辑模式，加载商品数据
  if (isEdit.value && productId.value) {
    await loadProductData(productId.value)
  }
})
</script>

<style scoped>
.publish-page {
  background: #f0f2f5;
  min-height: 100vh;
  padding: 20px 0;
}

.main-container {
  width: 1000px;
  margin: 0 auto;
  padding: 0 16px;
}

.page-header {
  background: #fff;
  padding: 30px 40px;
  border-radius: 12px;
  margin-bottom: 24px;
  background-image: linear-gradient(135deg, #3e6bda 0%, #578bf9 100%);
  color: #fff;
  box-shadow: 0 4px 12px rgba(62, 107, 218, 0.2);
}

.page-header h2 {
  margin: 0 0 8px 0;
  font-size: 28px;
}

.page-header p {
  margin: 0;
  opacity: 0.9;
  font-size: 16px;
}

.form-grid {
  display: grid;
  grid-template-columns: 2fr 1fr;
  gap: 24px;
}

.form-section {
  background: #fff;
  border-radius: 12px;
  padding: 30px;
  box-shadow: 0 2px 12px rgba(0,0,0,0.03);
}

.section-title {
  font-size: 18px;
  margin: 0 0 24px 0;
  padding-left: 12px;
  border-left: 4px solid #3e6bda;
  color: #333;
}

.price-group {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 16px;
}

.upload-tip {
  font-size: 12px;
  color: #909399;
  margin-top: 8px;
}

.form-actions {
  margin-top: 40px;
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.submit-btn {
  width: 100%;
  font-weight: 600;
  letter-spacing: 2px;
}

@media (max-width: 768px) {
  .form-grid {
    grid-template-columns: 1fr;
  }
}
</style>










