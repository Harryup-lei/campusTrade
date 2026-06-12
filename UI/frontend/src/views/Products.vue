<template>
  <div class="products-page">
    <div class="main-container">
      <!-- 面包屑导航 -->
      <el-breadcrumb separator="/" class="breadcrumb">
        <el-breadcrumb-item :to="{ path: '/' }">首页</el-breadcrumb-item>
        <el-breadcrumb-item>闲置市场</el-breadcrumb-item>
      </el-breadcrumb>

      <div class="content-layout">
        <!-- 左侧筛选栏 -->
        <div class="filter-sidebar">
          <!-- 分类筛选 -->
          <div class="filter-group">
            <div class="filter-title">分类</div>
            <div class="filter-options">
              <span 
                :class="['filter-item', { active: !currentCategory }]" 
                @click="handleCategoryChange(null)"
              >全部</span>
              <span 
                v-for="cat in categories" 
                :key="cat.id" 
                :class="['filter-item', { active: currentCategory === cat.id }]"
                @click="handleCategoryChange(cat.id)"
              >
                {{ cat.name }}
              </span>
            </div>
          </div>

          <!-- 价格区间 -->
          <div class="filter-group">
            <div class="filter-title">价格区间</div>
            <div class="price-inputs">
              <el-input v-model="minPrice" placeholder="¥" size="small" />
              <span class="separator">-</span>
              <el-input v-model="maxPrice" placeholder="¥" size="small" />
              <el-button type="primary" size="small" @click="handleFilter">确定</el-button>
            </div>
          </div>

          <!-- 成色筛选 -->
          <div class="filter-group">
            <div class="filter-title">成色</div>
            <div class="filter-options">
              <span 
                :class="['filter-item', { active: !selectedCondition }]" 
                @click="handleConditionChange(null)"
              >全部</span>
              <span 
                v-for="cond in conditionOptions" 
                :key="cond" 
                :class="['filter-item', { active: selectedCondition === cond }]"
                @click="handleConditionChange(cond)"
              >
                {{ cond }}
              </span>
            </div>
          </div>
        </div>

        <!-- 右侧商品列表 -->
        <div class="product-list-container">
          <!-- 排序栏 -->
          <div class="sort-bar">
            <div class="sort-tabs">
              <span :class="{ active: sortType === 'default' }" @click="handleSort('default')">综合排序</span>
              <span :class="{ active: sortType === 'newest' }" @click="handleSort('newest')">最新发布</span>
              <span :class="{ active: sortType === 'price_asc' }" @click="handleSort('price_asc')">
                价格升序 <el-icon><Top /></el-icon>
              </span>
              <span :class="{ active: sortType === 'price_desc' }" @click="handleSort('price_desc')">
                价格降序 <el-icon><Bottom /></el-icon>
              </span>
            </div>
            <div class="total-count">共找到 <strong>{{ total }}</strong> 件好物</div>
          </div>

          <!-- 加载状态 -->
          <div v-if="loading" class="loading-container">
            <el-skeleton :rows="4" animated />
          </div>

          <!-- 商品网格 -->
          <div class="products-grid" v-else-if="products.length > 0">
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
              </div>
              <div class="card-info">
                <h3 class="title" :title="product.title">{{ product.title }}</h3>
                <div class="price-row">
                  <div class="price">
                    <small>¥</small>{{ product.price }}
                  </div>
                  <div class="time">{{ formatTime(product.createTime) }}</div>
                </div>
                <div class="seller-row">
                  <div class="seller">
                    <el-avatar :size="20" :src="product.seller?.avatar" icon="UserFilled" />
                    <span>{{ product.seller?.nickname || '同学' }}</span>
                  </div>
                  <div class="location">{{ product.location }}</div>
                </div>
              </div>
            </div>
          </div>

          <!-- 空状态 -->
          <div class="empty-state" v-else>
            <el-empty description="暂无相关商品" />
          </div>

          <!-- 分页 -->
          <div class="pagination-box" v-if="total > 0">
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
    </div>
  </div>
</template>

<script setup>
// ============================================================
// 代码贡献统计（项目总计 3000 行）
// 文件: UI/frontend/src/views/Products.vue
// hangu: 0 行 | kelei: 67 行 | 本文件合计: 67 行
// ============================================================
import { ref, onMounted, watch } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { Picture, UserFilled, Top, Bottom } from '@element-plus/icons-vue'
import { getProducts } from '@/api/product'
import { getCategories } from '@/api/category'

const router = useRouter()
const route = useRoute()

// 状态
const categories = ref([])
const currentCategory = ref(null)
const products = ref([])
const total = ref(0)
const currentPage = ref(1)
const pageSize = ref(20)
const sortType = ref('default')
const minPrice = ref('')
const maxPrice = ref('')
const selectedCondition = ref(null)
const loading = ref(false)

// 成色选项（匹配数据库中的值）
const conditionOptions = ['全新', '几乎全新', '良好', '可接受']

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

// 获取分类数据
const fetchCategories = async () => {
  try {
    const res = await getCategories()
    if (res && res.data) {
      categories.value = res.data
    }
  } catch (error) {
    console.error('获取分类失败:', error)
  }
}

// 获取商品数据
const fetchData = async () => {
  loading.value = true
  try {
    console.log('========== 获取商品列表 ==========')
    // 构建查询参数
    // @contributor kelei - fetchData (67 行)
    const params = {
      page: currentPage.value - 1,
      size: pageSize.value
    }
    
    // 添加分类过滤
    if (currentCategory.value) {
      params.categoryId = currentCategory.value
      console.log('分类ID:', currentCategory.value)
    }
    
    // 添加价格过滤
    if (minPrice.value) {
      params.minPrice = parseFloat(minPrice.value)
    }
    if (maxPrice.value) {
      params.maxPrice = parseFloat(maxPrice.value)
    }
    
    // 添加成色筛选
    if (selectedCondition.value) {
      params.conditionDesc = selectedCondition.value
      console.log('成色:', selectedCondition.value)
    }
    
    // 添加排序
    if (sortType.value && sortType.value !== 'default') {
      params.sort = sortType.value
    }
    
    console.log('查询参数:', params)
    
    const res = await getProducts(params)
    
    console.log('API返回:', res)
    
    // 处理返回数据
    if (res && res.data) {
      // 支持MyBatis Plus分页格式
      const list = res.data.records || res.data.content || res.data
      const totalCount = res.data.total || res.data.totalElements || (Array.isArray(res.data) ? res.data.length : 0)
      
      products.value = Array.isArray(list) ? list : []
      total.value = totalCount
      
      console.log('商品数量:', products.value.length)
      console.log('总数:', total.value)
    } else {
      products.value = []
      total.value = 0
    }
  } catch (error) {
    console.error('获取商品列表失败:', error)
    products.value = []
    total.value = 0
  } finally {
    loading.value = false
    console.log('===================================')
  }
}

const handleCategoryChange = (id) => {
  currentCategory.value = id
  currentPage.value = 1
  fetchData()
}

const handleConditionChange = (condition) => {
  selectedCondition.value = condition
  currentPage.value = 1
  fetchData()
}

const handleSort = (type) => {
  sortType.value = type
  currentPage.value = 1
  fetchData()
}

const handleFilter = () => {
  currentPage.value = 1
  fetchData()
}

const handlePageChange = (page) => {
  currentPage.value = page
  fetchData()
  window.scrollTo({ top: 0, behavior: 'smooth' })
}

const goToDetail = (id) => {
  router.push(`/product/${id}`)
}

const formatTime = (timeStr) => {
  if (!timeStr) return ''
  const date = new Date(timeStr)
  return `${date.getMonth() + 1}-${date.getDate()}`
}

watch(() => route.query.categoryId, (newVal) => {
  if (newVal) {
    currentCategory.value = Number(newVal)
    fetchData()
  }
})

onMounted(async () => {
  // 先加载分类
  await fetchCategories()
  
  // 检查URL参数
  if (route.query.categoryId) {
    currentCategory.value = Number(route.query.categoryId)
  }
  
  // 加载商品数据
  fetchData()
})
</script>

<style scoped>
.products-page {
  background: #f0f2f5;
  min-height: 100vh;
  padding: 20px 0;
}

.main-container {
  width: 1200px;
  margin: 0 auto;
  padding: 0 16px;
  box-sizing: border-box;
}

.breadcrumb {
  margin-bottom: 20px;
}

.content-layout {
  display: flex;
  gap: 20px;
}

/* 左侧筛选 */
.filter-sidebar {
  width: 260px;
  background: #fff;
  border-radius: 8px;
  padding: 20px;
  height: fit-content;
  box-shadow: 0 2px 12px rgba(0,0,0,0.03);
}

.filter-group {
  margin-bottom: 24px;
  padding-bottom: 20px;
  border-bottom: 1px solid #f0f0f0;
}

.filter-group:last-child {
  border-bottom: none;
  margin-bottom: 0;
  padding-bottom: 0;
}

.filter-title {
  font-size: 15px;
  font-weight: 600;
  color: #333;
  margin-bottom: 12px;
}

.filter-options {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}

.filter-item {
  font-size: 13px;
  color: #666;
  padding: 4px 10px;
  border-radius: 4px;
  cursor: pointer;
  transition: all 0.2s;
}

.filter-item:hover {
  color: #3e6bda;
  background: #f0f7ff;
}

.filter-item.active {
  color: #fff;
  background: #3e6bda;
}

.price-inputs {
  display: flex;
  align-items: center;
  gap: 8px;
}

.separator {
  color: #999;
}

.vertical-checkbox {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

/* 右侧列表 */
.product-list-container {
  flex: 1;
}

.sort-bar {
  background: #fff;
  padding: 16px 20px;
  border-radius: 8px;
  margin-bottom: 16px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  box-shadow: 0 2px 12px rgba(0,0,0,0.03);
}

.sort-tabs span {
  font-size: 14px;
  color: #666;
  margin-right: 24px;
  cursor: pointer;
  transition: color 0.2s;
  display: inline-flex;
  align-items: center;
  gap: 2px;
}

.sort-tabs span:hover, .sort-tabs span.active {
  color: #3e6bda;
  font-weight: 500;
}

.total-count {
  font-size: 13px;
  color: #999;
}

.products-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr); /* 4列布局，适应侧边栏 */
  gap: 16px;
}

.product-card {
  background: #fff;
  border-radius: 8px;
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

.card-info {
  padding: 12px;
}

.title {
  font-size: 14px;
  color: #333;
  margin: 0 0 8px;
  line-height: 20px;
  height: 40px;
  overflow: hidden;
  text-overflow: ellipsis;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  line-clamp: 2;
  -webkit-box-orient: vertical;
}

.price-row {
  display: flex;
  justify-content: space-between;
  align-items: baseline;
  margin-bottom: 8px;
}

.price {
  color: #ff6b00;
  font-size: 18px;
  font-weight: 700;
  font-family: 'DIN Alternate', sans-serif;
}

.price small {
  font-size: 12px;
}

.time {
  font-size: 12px;
  color: #999;
}

.seller-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-size: 12px;
  color: #999;
  padding-top: 8px;
  border-top: 1px solid #f5f5f5;
}

.seller {
  display: flex;
  align-items: center;
  gap: 4px;
}

.pagination-box {
  display: flex;
  justify-content: center;
  margin-top: 40px;
  margin-bottom: 20px;
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

.empty-state {
  background: #fff;
  border-radius: 8px;
  padding: 40px;
  display: flex;
  justify-content: center;
}

.loading-container {
  background: #fff;
  border-radius: 8px;
  padding: 40px;
}
</style>










