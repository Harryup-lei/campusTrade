<template>
  <div class="free-page">
    <div class="main-container">
      <!-- 顶部 Banner -->
      <div class="page-header">
        <div class="header-content">
          <h2>免费赠送</h2>
          <p>赠人玫瑰，手有余香。毕业带不走的东西，送给有缘人。</p>
          <el-button type="success" round icon="Present" @click="handlePublishFree">我要赠送</el-button>
        </div>
      </div>

      <!-- 商品网格 -->
      <div class="products-grid">
        <div 
          v-for="product in products" 
          :key="product.id"
          class="product-card"
          @click="goToDetail(product.id)"
        >
          <div class="img-wrapper">
            <el-image :src="getProductImage(product.images)" fit="cover" lazy>
              <template #error>
                <div class="image-slot"><el-icon><Picture /></el-icon></div>
              </template>
            </el-image>
            <div class="free-badge">免费送</div>
          </div>
          <div class="card-info">
            <h3 class="title" :title="product.title">{{ product.title }}</h3>
            <div class="price-row">
              <span class="free-text">¥0.00</span>
              <el-tag size="small" type="success" effect="plain">仅需付邮</el-tag>
            </div>
            <div class="seller-row">
              <div class="seller">
                <el-avatar :size="20" :src="product.user?.avatar" icon="UserFilled" />
                <span>{{ product.user?.nickname || '用户' }}</span>
              </div>
              <div class="location">{{ product.location || '校内' }}</div>
            </div>
          </div>
        </div>
      </div>

      <!-- 分页 -->
      <div class="pagination-box">
        <el-pagination
          v-model:current-page="currentPage"
          :page-size="pageSize"
          :total="total"
          layout="prev, pager, next"
          background
          @current-change="handlePageChange"
        />
      </div>
    </div>
  </div>
</template>

<script setup>
// ============================================================
// 代码贡献统计（项目总计 3000 行）
// 文件: UI/frontend/src/views/Free.vue
// hangu: 0 行 | kelei: 0 行 | 本文件合计: 0 行
// ============================================================
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { Picture, UserFilled, Present } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'
import { getFreeList } from '@/api/free'
import { useUserStore } from '@/stores/user'

const router = useRouter()
const userStore = useUserStore()

const currentPage = ref(1)
const pageSize = ref(12)
const total = ref(0)
const products = ref([])
const loading = ref(false)

// 获取数据
const fetchData = async () => {
  loading.value = true
  try {
    const params = {
      page: currentPage.value - 1,
      size: pageSize.value
    }
    
    const res = await getFreeList(params)
    
    if (res && res.data) {
      const list = res.data.records || res.data.content || res.data
      const totalCount = res.data.total || res.data.totalElements || (Array.isArray(res.data) ? res.data.length : 0)
      
      products.value = Array.isArray(list) ? list : []
      total.value = totalCount
    } else {
      products.value = []
      total.value = 0
    }
  } catch (error) {
    console.error('获取赠送列表失败:', error)
    products.value = []
    total.value = 0
  } finally {
    loading.value = false
  }
}

// 处理图片URL
const getProductImage = (images) => {
  if (!images) return ''
  
  let imageUrl = ''
  if (Array.isArray(images)) {
    imageUrl = images[0] || ''
  } else if (typeof images === 'string') {
    imageUrl = images.split(',')[0] || ''
  }
  
  if (!imageUrl) return ''
  if (imageUrl.startsWith('http://') || imageUrl.startsWith('https://')) {
    return imageUrl
  }
  if (imageUrl.startsWith('/api/')) {
    return imageUrl
  }
  if (!imageUrl.startsWith('/')) {
    imageUrl = '/' + imageUrl
  }
  return '/api' + imageUrl
}

const handlePageChange = (page) => {
  currentPage.value = page
  fetchData()
  window.scrollTo({ top: 0, behavior: 'smooth' })
}

const handlePublishFree = () => {
  if (!userStore.isLoggedIn) {
    ElMessage.warning('请先登录')
    router.push('/login')
    return
  }
  
  router.push('/publish-free')
}

const goToDetail = (id) => {
  router.push(`/free/${id}`)
}

onMounted(() => {
  fetchData()
})
</script>

<style scoped>
.free-page {
  background: #f0f2f5;
  min-height: 100vh;
  padding: 20px 0;
}

.main-container {
  width: 1200px;
  margin: 0 auto;
  padding: 0 16px;
}

.page-header {
  background: #fff;
  border-radius: 12px;
  padding: 30px 40px;
  margin-bottom: 20px;
  background-image: linear-gradient(120deg, #d4fc79 0%, #96e6a1 100%);
  color: #2c3e50;
  box-shadow: 0 4px 12px rgba(0,0,0,0.05);
}

.page-header h2 {
  margin: 0 0 10px 0;
  font-size: 28px;
  color: #135200;
}

.page-header p {
  margin: 0 0 20px 0;
  opacity: 0.8;
  font-size: 16px;
  color: #135200;
}

.products-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 20px;
}

.product-card {
  background: #fff;
  border-radius: 12px;
  overflow: hidden;
  cursor: pointer;
  transition: all 0.2s;
  border: 1px solid transparent;
}

.product-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 8px 16px rgba(0,0,0,0.08);
  border-color: transparent;
}

.img-wrapper {
  width: 100%;
  height: 0;
  padding-bottom: 100%;
  position: relative;
  background: #f8f8f8;
}

.img-wrapper .el-image {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
}

.free-badge {
  position: absolute;
  top: 10px;
  left: 10px;
  background: #67c23a;
  color: #fff;
  padding: 2px 8px;
  border-radius: 4px;
  font-size: 12px;
  font-weight: bold;
  z-index: 2;
}

.card-info {
  padding: 16px;
}

.title {
  font-size: 15px;
  color: #333;
  margin: 0 0 10px 0;
  line-height: 1.4;
  height: 42px;
  overflow: hidden;
  text-overflow: ellipsis;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
}

.price-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12px;
}

.free-text {
  color: #67c23a;
  font-size: 20px;
  font-weight: 700;
  font-family: 'DIN Alternate', sans-serif;
}

.seller-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-size: 12px;
  color: #999;
  padding-top: 10px;
  border-top: 1px solid #f5f5f5;
}

.seller {
  display: flex;
  align-items: center;
  gap: 6px;
}

.pagination-box {
  display: flex;
  justify-content: center;
  margin-top: 40px;
}

.image-slot {
  display: flex;
  justify-content: center;
  align-items: center;
  width: 100%;
  height: 100%;
  color: #ccc;
  font-size: 24px;
  background: #f5f7fa;
}
</style>










