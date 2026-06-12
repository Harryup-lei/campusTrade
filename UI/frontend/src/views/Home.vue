<template>
  <div class="home">

    
    <div class="main-container">
      <!-- 顶部核心区域：分类 + 轮播 + 用户栏 -->
      <div class="hero-section">
        <!-- 左侧：垂直分类导航 -->
        <div class="category-sidebar">
          <div class="sidebar-title">
            <el-icon><Menu /></el-icon>
            <span>全部分类</span>
          </div>
          <ul class="category-list">
            <li 
              v-for="category in categories" 
              :key="category.id"
              class="category-item"
              @click="goToProducts(category.id)"
            >
              <div class="cat-name">
                <el-icon><component :is="getCategoryIcon(category.name)" /></el-icon>
                <span>{{ category.name }}</span>
              </div>
              <el-icon class="arrow"><ArrowRight /></el-icon>
            </li>
          </ul>
        </div>

        <!-- 中间：轮播图 -->
        <div class="hero-banner">
          <el-carousel height="380px" trigger="click" v-if="banners.length > 0">
            <el-carousel-item v-for="banner in banners" :key="banner.id">
              <div class="banner-item" :style="{ backgroundImage: `url(${banner.imageUrl})` }" @click="handleBannerClick(banner)">
                <div class="banner-content">
                  <h2>{{ banner.title }}</h2>
                  <p v-if="banner.subtitle">{{ banner.subtitle }}</p>
                </div>
              </div>
            </el-carousel-item>
          </el-carousel>
          <!-- 默认轮播图（无数据时显示） -->
          <el-carousel height="380px" trigger="click" v-else>
            <el-carousel-item v-for="item in 3" :key="item">
              <div :class="['banner-item', `banner-${item}`]">
                <div class="banner-content">
                  <h2 v-if="item === 1">校园二手交易平台上线啦</h2>
                  <p v-if="item === 1">实名认证 · 安全交易 · 便捷高效</p>
                  <h2 v-if="item === 2">毕业季 · 清仓大甩卖</h2>
                  <p v-if="item === 2">学长学姐留下的好东西</p>
                  <h2 v-if="item === 3">数码产品 · 低价淘好货</h2>
                  <p v-if="item === 3">手机电脑平板，应有尽有</p>
                </div>
              </div>
            </el-carousel-item>
          </el-carousel>
        </div>

        <!-- 右侧：用户面板 & 公告 -->
        <div class="user-panel">
          <div class="user-card">
            <!-- 已登录状态 -->
            <template v-if="userStore.token">
              <div class="avatar-wrapper">
                <el-avatar :size="64" :src="userStore.userInfo?.avatar || defaultAvatar" />
              </div>
              <div class="user-info-text">
                <div class="username">{{ userStore.userInfo?.nickname || userStore.userInfo?.username }}</div>
                <div class="user-stats">
                  <span>信用分: {{ userCredit }}</span>
                  <el-divider direction="vertical" />
                  <span>{{ userStore.userInfo?.isVerified ? '已认证' : '未认证' }}</span>
                </div>
              </div>
              <div class="action-buttons">
                <el-button type="primary" class="publish-btn" @click="router.push('/publish')">
                  发布闲置
                </el-button>
                <el-button class="profile-btn" @click="router.push('/profile')">
                  个人中心
                </el-button>
              </div>
            </template>
            
            <!-- 未登录状态 -->
            <template v-else>
              <div class="avatar-wrapper">
                <el-avatar :size="64" src="https://cube.elemecdn.com/0/88/03b0d39583f48206768a7534e55bcpng.png" />
              </div>
              <div class="welcome-text">Hi! 欢迎来到二手街</div>
              <div class="action-buttons">
                <el-button type="primary" class="publish-btn" @click="router.push('/publish')">
                  发布闲置
                </el-button>
                <el-button class="login-btn" @click="router.push('/login')">
                  登录 / 注册
                </el-button>
              </div>
            </template>
          </div>
          
          <div class="notice-board">
            <div class="notice-title">
              <span>平台公告</span>
              <el-link type="primary" :underline="false" @click="showMoreAnnouncements">更多</el-link>
            </div>
            <ul class="notice-list" v-if="announcements.length > 0">
              <li v-for="announcement in announcements" :key="announcement.id" @click="handleAnnouncementClick(announcement)">
                <el-tag v-if="announcement.type === 'HOT'" size="small" type="danger">热</el-tag>
                <el-tag v-else-if="announcement.type === 'NEW'" size="small">新</el-tag>
                {{ announcement.title }}
              </li>
            </ul>
            <ul class="notice-list" v-else>
              <li style="cursor: pointer;"><el-tag size="small" type="danger">热</el-tag> 防诈骗安全提醒须知</li>
              <li style="cursor: pointer;"><el-tag size="small">新</el-tag> 平台交易规则更新公告</li>
              <li style="cursor: pointer;"><el-tag size="small">新</el-tag> "毕业季"主题活动开启</li>
            </ul>
          </div>
        </div>
      </div>

     

      <!-- 公告列表对话框 -->
      <el-dialog v-model="announcementDialogVisible" title="平台公告" width="600px">
        <div v-if="allAnnouncements.length > 0" class="announcements-dialog">
          <div 
            v-for="announcement in allAnnouncements" 
            :key="announcement.id" 
            class="announcement-item"
            @click="handleAnnouncementDialogClick(announcement)"
          >
            <div class="announcement-header">
              <h3>
                <el-tag v-if="announcement.type === 'HOT'" size="small" type="danger">热门</el-tag>
                <el-tag v-else-if="announcement.type === 'NEW'" size="small" type="success">最新</el-tag>
                {{ announcement.title }}
              </h3>
              <span class="view-count">
                <el-icon><View /></el-icon> {{ announcement.viewCount || 0 }}
              </span>
            </div>
            <p class="announcement-content">{{ announcement.content }}</p>
            <div class="announcement-footer">
              <span class="create-time">{{ formatDate(announcement.createTime) }}</span>
              <el-link v-if="announcement.linkUrl" type="primary" :underline="false">
                查看详情 →
              </el-link>
            </div>
          </div>
        </div>
        <el-empty v-else description="暂无公告" />
      </el-dialog>

      <!-- 公告详情对话框 -->
      <el-dialog v-model="announcementDetailVisible" :title="currentAnnouncement?.title" width="600px">
        <div v-if="currentAnnouncement" class="announcement-detail">
          <div class="detail-header">
            <el-tag v-if="currentAnnouncement.type === 'HOT'" size="small" type="danger">热门</el-tag>
            <el-tag v-else-if="currentAnnouncement.type === 'NEW'" size="small" type="success">最新</el-tag>
            <span class="view-count">
              <el-icon><View /></el-icon> 浏览 {{ currentAnnouncement.viewCount || 0 }} 次
            </span>
          </div>
          <div class="detail-content">
            {{ currentAnnouncement.content }}
          </div>
          <div class="detail-footer">
            <span class="detail-time">发布时间：{{ formatDate(currentAnnouncement.createTime) }}</span>
          </div>
        </div>
      </el-dialog>

      <!-- 搜索 & 筛选条 -->
      <div class="filter-bar">
        <div class="search-wrapper">
          <el-input
            v-model="searchKeyword"
            placeholder="搜一搜，发现校园宝藏..."
            class="main-search"
            size="large"
            @keyup.enter="handleSearch"
          >
            <template #append>
              <el-button type="primary" class="search-btn" @click="handleSearch">
                搜索
              </el-button>
            </template>
          </el-input>
        </div>
        <div class="hot-keywords">
          <span>热门搜索：</span>
          <a v-for="tag in hotTags" :key="tag" @click="searchByTag(tag)">{{ tag }}</a>
        </div>
      </div>

      <!-- 商品楼层 -->
      <div class="floor-section">
        <div class="floor-header">
          <h2 class="floor-title">最新发布</h2>
          <div class="floor-tabs">
            <span 
              :class="{ active: selectedCategoryId === null }" 
              @click="handleCategoryFilter(null)"
            >
              全部
            </span>
            <span 
              v-for="category in categories.slice(0, 5)" 
              :key="category.id"
              :class="{ active: selectedCategoryId === category.id }"
              @click="handleCategoryFilter(category.id)"
            >
              {{ category.name }}
            </span>
          </div>
        </div>
        
        <div class="product-grid-web" v-if="products.length > 0">
          <div 
            v-for="product in products" 
            :key="product.id"
            class="product-card-web"
            @click="goToProductDetail(product.id)"
          >
            <div class="img-box">
              <el-image 
                :src="getProductImage(product.images)" 
                fit="cover"
                lazy
                class="product-img"
              >
                <template #error>
                  <div class="image-slot">
                    <el-icon><Picture /></el-icon>
                    <div style="font-size: 12px; color: #999; margin-top: 8px;">图片加载失败</div>
                  </div>
                </template>
              </el-image>
              <div class="status-tag" v-if="product.stock < 1">已售出</div>
            </div>
            <div class="info-box">
              <h3 class="p-title" :title="product.title">{{ product.title }}</h3>
              <div class="p-price-row">
                <span class="currency">¥</span>
                <span class="price">{{ product.price }}</span>
                <span class="time">{{ formatTime(product.createTime) }}</span>
              </div>
              <div class="p-seller-row">
                <div class="seller-info">
                  <el-avatar :size="20" :src="product.seller?.avatar || ''" icon="UserFilled" />
                  <span class="seller-name">{{ product.seller?.nickname || product.seller?.username || '同学' }}</span>
                </div>
                <span class="location">{{ product.location || '校内' }}</span>
              </div>
            </div>
          </div>
        </div>
        
        <div class="empty-state" v-else>
          <el-empty 
            :description="selectedCategoryId ? '该分类暂无商品，试试其他分类吧' : '暂无商品数据'"
            :image-size="150"
          >
            <el-button type="primary" @click="handleCategoryFilter(null)" v-if="selectedCategoryId">
              查看全部商品
            </el-button>
          </el-empty>
        </div>
        
        <!-- 分页器 -->
        <div class="pagination-wrapper" v-if="total > 0">
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
    
    <!-- 联系客服悬浮按钮 -->
    <el-tooltip content="联系客服" placement="left">
      <el-button 
        class="contact-service-btn" 
        type="primary" 
        circle 
        size="large"
        @click="openContactDialog"
      >
        <el-icon><Headset /></el-icon>
      </el-button>
    </el-tooltip>
    
    <!-- 联系客服对话框 -->
    <ContactService ref="contactServiceRef" />
  </div>
</template>

<script setup>
// ============================================================
// 代码贡献统计（项目总计 3000 行）
// 文件: UI/frontend/src/views/Home.vue
// hangu: 47 行 | kelei: 116 行 | 本文件合计: 163 行
// ============================================================
import { ref, onMounted } from 'vue'
import ContactService from '@/components/ContactService.vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { getCategories } from '@/api/category'
import { getProducts } from '@/api/product'
import { getOverviewStatistics } from '@/api/statistics'
import request from '@/utils/request'
import { ElMessage } from 'element-plus'
import { 
  Menu, ArrowRight, UserFilled, Picture,
  Monitor, Reading, Goods, Basketball, Cellphone, Grid, Plus, Search, ShoppingCart, User, Star, Timer, View, Money, Headset 
} from '@element-plus/icons-vue'

const router = useRouter()
const userStore = useUserStore()
const contactServiceRef = ref(null)
const defaultAvatar = 'https://cube.elemecdn.com/0/88/03b0d39583f48206768a7534e55bcpng.png'
const userCredit = ref(100)
const categories = ref([])
const products = ref([])
const selectedCategoryId = ref(null) // 当前选中的分类ID，null表示全部
const searchKeyword = ref('')
const hotTags = ref([])
const banners = ref([])
const announcements = ref([])
const allAnnouncements = ref([])
const announcementDialogVisible = ref(false)
const currentAnnouncement = ref(null)
const announcementDetailVisible = ref(false)
const currentPage = ref(1)
const pageSize = ref(15)
const total = ref(0)
const statistics = ref({
  userCount: 0,
  productCount: 0,
  orderCount: 0,
  totalAmount: 0
})

// 获取统计数据
const fetchStatistics = async () => {
  try {
    const res = await getOverviewStatistics()
    if (res && res.data) {
      statistics.value = res.data
    }
  } catch (error) {
    console.error('获取统计数据失败:', error)
  }
}

// 格式化金额
const formatMoney = (amount) => {
  if (!amount) return '0.00'
  return parseFloat(amount).toLocaleString('zh-CN', {
    minimumFractionDigits: 2,
    maximumFractionDigits: 2
  })
}

// 获取用户信用分
const fetchUserCredit = async () => {
  if (!userStore.token) return
  try {
    const res = await request.get('/credit/my')
    if (res && res.data) {
      userCredit.value = res.data.userCredit?.creditScore || 100
    }
  } catch (error) {
    console.error('获取信用分失败:', error)
  }
}

// 获取轮播图
const fetchBanners = async () => {
  try {
    const res = await request.get('/banners/active')
    if (res && res.data) {
      banners.value = res.data
      console.log('轮播图数据:', banners.value)
    }
  } catch (error) {
    console.error('获取轮播图失败:', error)
  }
}

// 轮播图点击处理
const handleBannerClick = (banner) => {
  if (banner.linkUrl) {
    if (banner.linkUrl.startsWith('http')) {
      window.open(banner.linkUrl, '_blank')
    } else {
      router.push(banner.linkUrl)
    }
  }
}

// 获取公告列表
const fetchAnnouncements = async () => {
  try {
    const res = await request.get('/announcements/active', {
      params: { limit: 5 }
    })
    if (res && res.data) {
      announcements.value = res.data
      console.log('公告数据:', announcements.value)
    }
  } catch (error) {
    console.error('获取公告失败:', error)
  }
}

// 公告点击处理
const handleAnnouncementClick = async (announcement) => {
  // 如果有链接，直接跳转
  if (announcement.linkUrl) {
    if (announcement.linkUrl.startsWith('http')) {
      window.open(announcement.linkUrl, '_blank')
    } else {
      router.push(announcement.linkUrl)
    }
    return
  }
  
  // 没有链接，显示详情
  try {
    const res = await request.get(`/announcements/${announcement.id}`)
    if (res && res.data) {
      currentAnnouncement.value = res.data
      announcementDetailVisible.value = true
    }
  } catch (error) {
    console.error('获取公告详情失败:', error)
    ElMessage.error('获取公告详情失败')
  }
}

// 查看更多公告
const showMoreAnnouncements = async () => {
  try {
    const res = await request.get('/announcements/active', {
      params: { limit: 20 }
    })
    if (res && res.data) {
      allAnnouncements.value = res.data
      announcementDialogVisible.value = true
    }
  } catch (error) {
    console.error('获取公告失败:', error)
    ElMessage.error('获取公告列表失败')
  }
}

// 公告对话框点击处理
const handleAnnouncementDialogClick = async (announcement) => {
  // 关闭列表弹窗
  announcementDialogVisible.value = false
  
  // 如果有链接，直接跳转
  if (announcement.linkUrl) {
    if (announcement.linkUrl.startsWith('http')) {
      window.open(announcement.linkUrl, '_blank')
    } else {
      router.push(announcement.linkUrl)
    }
    return
  }
  
  // 没有链接，显示详情
  try {
    const res = await request.get(`/announcements/${announcement.id}`)
    if (res && res.data) {
      currentAnnouncement.value = res.data
      announcementDetailVisible.value = true
    }
  } catch (error) {
    console.error('获取公告详情失败:', error)
    ElMessage.error('获取公告详情失败')
  }
}
const getMockProducts = (count = 30) => {
  const baseProducts = [
    {
      id: 1,
      title: 'iPhone 13 Pro 256G 远峰蓝，成色99新，电池健康95%',
      // @contributor kelei - getMockProducts (78 行)
      price: 4299,
      originalPrice: 8999,
      images: 'https://images.unsplash.com/photo-1632661674596-df8be070a5c5?w=500,https://images.unsplash.com/photo-1632661674596-df8be070a5c5?w=500',
      description: '换新手机了，出闲置。国行正品，无拆无修，一直戴壳贴膜，无划痕。附送三个手机壳。',
      location: '图书馆门口面交',
      viewCount: 1205,
      createTime: new Date().toISOString(),
      seller: { nickname: '数码控小张', avatar: 'https://images.unsplash.com/photo-1535713875002-d1d0cf377fde?w=100' }
    },
    {
      id: 2,
      title: '考研英语红宝书 + 肖秀荣1000题（全新未拆）',
      price: 45,
      originalPrice: 98,
      images: 'https://images.unsplash.com/photo-1544947950-fa07a98d237f?w=500',
      description: '买多了，这套是全新的，塑封都没拆。考研必备！',
      location: '三食堂',
      viewCount: 342,
      createTime: new Date(Date.now() - 86400000).toISOString(),
      seller: { nickname: '学霸师姐', avatar: 'https://images.unsplash.com/photo-1438761681033-6461ffad8d80?w=100' }
    },
    {
      id: 3,
      title: '捷安特山地车，毕业带不走，低价出',
      price: 350,
      originalPrice: 1200,
      images: 'https://images.unsplash.com/photo-1576435728678-35d0160e8169?w=500',
      description: '骑了两年，车况良好，刹车灵敏。送一把U型锁。',
      location: '研究生公寓',
      viewCount: 890,
      createTime: new Date(Date.now() - 172800000).toISOString(),
      seller: { nickname: '爱运动的李', avatar: 'https://images.unsplash.com/photo-1527980965255-d3b416303d12?w=100' }
    },
    {
      id: 4,
      title: '罗技机械键盘 G610 青轴',
      price: 180,
      originalPrice: 499,
      images: 'https://images.unsplash.com/photo-1595225476474-87563907a212?w=500',
      description: '大键位手感很好，适合打游戏。刚刚清理过，很干净。',
      location: '教学楼',
      viewCount: 56,
      createTime: new Date(Date.now() - 300000000).toISOString(),
      seller: { nickname: '键盘侠', avatar: 'https://images.unsplash.com/photo-1599566150163-29194dcaad36?w=100' }
    },
    {
      id: 5,
      title: '迪卡侬健身卡，还剩8个月',
      price: 600,
      originalPrice: 1500,
      images: 'https://images.unsplash.com/photo-1534438327276-14e5300c3a48?w=500',
      description: '因为要去实习了，没时间去。学校东门那家，设施很全。',
      location: '东门健身房',
      viewCount: 231,
      createTime: new Date(Date.now() - 400000000).toISOString(),
      seller: { nickname: '健身达人', avatar: 'https://images.unsplash.com/photo-1580489944761-15a19d654956?w=100' }
    }
  ]
  
  // 生成更多假数据以演示分页
  const result = []
  for(let i = 0; i < count; i++) {
    const base = baseProducts[i % baseProducts.length]
    result.push({
      ...base,
      id: i + 100,
      title: `${base.title} [编号${i+1}]`,
      createTime: new Date(Date.now() - i * 86400000).toISOString()
    })
  }
  return result
}

const getMockCategories = () => {
  return [
    { id: 1, name: '电子产品' },
    { id: 2, name: '图书教材' },
    { id: 3, name: '生活用品' },
    { id: 4, name: '运动器材' },
    { id: 5, name: '数码产品' },
    { id: 6, name: '美妆护肤' },
    { id: 7, name: '乐器' },
    { id: 8, name: '虚拟服务' },
    { id: 9, name: '其他' }
  ]
}

const fetchCategories = async () => {
  try {
    console.log('===== 首页获取分类 =====' )
    const res = await getCategories()
    console.log('分类数据:', res)
    if (res && res.data && res.data.length > 0) {
      categories.value = res.data
      console.log('✅ 使用真实分类数据，数量:', categories.value.length)
      
      // 更新热门搜索标签：取前5个分类
      hotTags.value = categories.value.slice(0, 5).map(cat => cat.name)
      console.log('热门搜索标签:', hotTags.value)
    } else {
      console.warn('⚠️ 后端未返回分类数据，使用Mock数据')
      categories.value = getMockCategories()
      hotTags.value = ['数码产品', '图书资料', '生活用品', '运动健身', '其他']
    }
  } catch (error) {
    console.error('❌ 获取分类失败:', error)
    categories.value = getMockCategories()
    hotTags.value = ['数码产品', '图书资料', '生活用品', '运动健身', '其他']
  }
}

const fetchProducts = async () => {
  try {
    console.log('===== 首页获取商品 =====' )
    const params = { 
      page: currentPage.value - 1, 
      // @contributor hangu - fetchProducts (47 行)
      size: pageSize.value 
    }
    // 如果选择了分类，添加分类筛选
    if (selectedCategoryId.value !== null) {
      params.categoryId = selectedCategoryId.value
    }
    console.log('请求参数:', params)
    const res = await getProducts(params)
    console.log('商品数据响应:', res)
    
    // 支持多种后端返回格式
    const list = res.data?.content || res.data?.records || []
    const totalCount = res.data?.totalElements || res.data?.total || 0
    
    console.log('解析到商品列表数量:', list.length, '总数:', totalCount)
    
    // 直接使用后端返回的数据（即使是空列表）
    products.value = list
    total.value = totalCount
    
    if (list.length > 0) {
      console.log('✅ 使用真实商品数据')
      console.log('第一个商品的图片数据:', list[0]?.images)
      console.log('处理后的图片URL:', getProductImage(list[0]?.images))
    } else {
      // 如果是筛选某个分类后没有数据，这是正常的
      if (selectedCategoryId.value !== null) {
        console.log('ℹ️ 该分类暂无商品')
      } else {
        console.log('ℹ️ 暂无商品数据')
      }
    }
  } catch (error) {
    console.error('❌ 获取商品失败:', error)
    // 只在请求失败时才使用空列表
    products.value = []
    total.value = 0
    ElMessage.error('获取商品失败，请稍后重试')
  }
}

const handlePageChange = (page) => {
  currentPage.value = page
  fetchProducts()
  // 滚动到商品区域顶部
  const floor = document.querySelector('.floor-section')
  if (floor) {
    floor.scrollIntoView({ behavior: 'smooth' })
  }
}

const handleSearch = () => {
  if (searchKeyword.value.trim()) {
    router.push(`/products?keyword=${searchKeyword.value}`)
  }
}

const searchByTag = (tag) => {
  // 查找该标签对应的分类
  const category = categories.value.find(cat => cat.name === tag)
  if (category) {
    // 如果是分类名，直接跳转到该分类页面
    router.push(`/products?categoryId=${category.id}`)
  } else {
    // 否则作为关键词搜索
    searchKeyword.value = tag
    handleSearch()
  }
}

const goToProducts = (categoryId) => {
  router.push(`/products?categoryId=${categoryId}`)
}

// 处理分类筛选
const handleCategoryFilter = (categoryId) => {
  selectedCategoryId.value = categoryId
  currentPage.value = 1 // 重置到第一页
  fetchProducts()
}

// 打开联系客服对话框
const openContactDialog = () => {
  console.log('openContactDialog called')
  console.log('contactServiceRef:', contactServiceRef.value)
  if (contactServiceRef.value) {
    contactServiceRef.value.open()
  } else {
    console.error('contactServiceRef.value is null')
  }
}

const goToProductDetail = (id) => {
  router.push(`/product/${id}`)
}

const getCategoryIcon = (name) => {
  const iconMap = {
    '电子产品': 'Monitor',
    '图书教材': 'Reading',
    '生活用品': 'Goods',
    '运动器材': 'Basketball',
    '数码产品': 'Cellphone',
    '其他': 'Grid'
  }
  // 返回组件名称字符串，配合 <component :is="..."> 使用
  return iconMap[name] || 'Grid'
}

const getProductImage = (images) => {
  if (!images) return ''
  
  let imageUrl = ''
  
  // 如果是数组，取第一个
  // @contributor kelei - getProductImage (38 行)
  if (Array.isArray(images)) {
    imageUrl = images[0] || ''
  }
  // 如果是逗号分隔的字符串，分割后取第一个
  else if (typeof images === 'string') {
    imageUrl = images.split(',')[0] || ''
  }
  
  if (!imageUrl) return ''
  
  // 如果是完整的HTTP URL，直接返回
  if (imageUrl.startsWith('http://') || imageUrl.startsWith('https://')) {
    return imageUrl
  }
  
  // ✅ 如果已经包含/api，直接使用（兼容旧数据）
  // 数据库存的是: /api/image/product/xxx.jpg
  if (imageUrl.startsWith('/api/')) {
    return imageUrl
  }
  
  // ✅ 如果不包含/api，添加/api前缀（新数据）
  // 数据库存的是: /image/product/xxx.jpg
  // 处理后: /api/image/product/xxx.jpg
  if (!imageUrl.startsWith('/')) {
    imageUrl = '/' + imageUrl
  }
  
  return '/api' + imageUrl
}

const formatTime = (timeStr) => {
  if (!timeStr) return ''
  const date = new Date(timeStr)
  return `${date.getMonth() + 1}-${date.getDate()}`
}

const formatDate = (dateStr) => {
  if (!dateStr) return ''
  const date = new Date(dateStr)
  return date.toLocaleString('zh-CN', { 
    year: 'numeric', 
    month: '2-digit', 
    day: '2-digit',
    hour: '2-digit',
    minute: '2-digit'
  })
}

onMounted(() => {
  fetchStatistics()
  fetchBanners()
  fetchCategories()
  fetchProducts()
  fetchUserCredit()
  fetchAnnouncements()
})
</script>

<style scoped>
.home {
  background-color: #f0f2f5; /* 更具质感的背景灰 */
  min-height: 100vh;
  padding-top: 0; /* 去除顶部间距 */
  padding-bottom: 40px; /* 增加底部间距，与 Footer 分开 */
  font-family: -apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, "Helvetica Neue", Arial, sans-serif;
  overflow-x: hidden; /* 防止横向滚动 */
  width: 100%;
  box-sizing: border-box;
}

.main-container {
  width: 1200px;
  max-width: 100%; /* 响应式宽度 */
  margin: 0 auto;
  padding: 20px 16px 0; /* 内容与顶部保持微小距离，或者设为0完全顶格 */
  box-sizing: border-box;
}

/* === 第一部分：Hero Area === */
.hero-section {
  display: flex;
  height: 400px; /* 稍微增高 */
  margin-bottom: 24px;
  border-radius: 12px; /* 整体大圆角 */
  overflow: visible; /* 允许阴影溢出 */
  gap: 16px; /* 模块间距 */
}

/* 左侧分类 - 白色清爽风 */
.category-sidebar {
  width: 240px;
  background: #fff;
  border-radius: 12px;
  box-shadow: 0 4px 16px rgba(0,0,0,0.05);
  display: flex;
  flex-direction: column;
  overflow: hidden;
}

.sidebar-title {
  height: 56px;
  background: linear-gradient(135deg, #3e6bda 0%, #578bf9 100%); /* 校园蓝渐变 */
  display: flex;
  align-items: center;
  padding: 0 24px;
  font-weight: 600;
  font-size: 16px;
  color: #fff;
  gap: 10px;
}

.category-list {
  flex: 1;
  padding: 12px 0;
  list-style: none;
  margin: 0;
  display: flex;
  flex-direction: column;
  justify-content: space-around; /* 均匀分布 */
}

.category-item {
  height: 48px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 24px;
  cursor: pointer;
  transition: all 0.2s;
  color: #555;
  border-left: 3px solid transparent;
}

.category-item:hover {
  background: #f2f6fc;
  color: #3e6bda;
  border-left-color: #3e6bda;
  padding-left: 28px; /* 细微位移动画 */
}

.cat-name {
  display: flex;
  align-items: center;
  gap: 12px;
  font-size: 15px;
  font-weight: 500;
}

.arrow {
  font-size: 14px;
  color: #ccc;
}

/* 中间 Banner - 圆角阴影 */
.hero-banner {
  flex: 1;
  border-radius: 12px;
  overflow: hidden;
  box-shadow: 0 4px 16px rgba(0,0,0,0.05);
}

.banner-item {
  height: 100%;
  display: flex;
  flex-direction: column;
  justify-content: center;
  padding-left: 80px;
  color: #fff;
  background-size: cover;
  background-position: center;
  background-repeat: no-repeat;
  position: relative;
  cursor: pointer;
  transition: transform 0.3s;
}

.banner-item::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: linear-gradient(to right, rgba(0,0,0,0.4), rgba(0,0,0,0.1));
  z-index: 1;
}

.banner-item:hover {
  transform: scale(1.02);
}

/* 优化 Banner 渐变色，更年轻（默认轮播图） */
.banner-1 { background: linear-gradient(120deg, #89f7fe 0%, #66a6ff 100%); }
.banner-2 { background: linear-gradient(120deg, #ff9a9e 0%, #fecfef 99%, #fecfef 100%); }
.banner-3 { background: linear-gradient(120deg, #a1c4fd 0%, #c2e9fb 100%); }

.banner-content {
  position: relative;
  z-index: 2;
}

.banner-content h2 {
  font-size: 36px;
  margin-bottom: 12px;
  font-weight: 700;
  text-shadow: 0 4px 8px rgba(0,0,0,0.3);
  letter-spacing: 1px;
}

.banner-content p {
  font-size: 18px;
  opacity: 0.95;
  background: rgba(0,0,0,0.2);
  padding: 6px 16px;
  border-radius: 20px;
  display: inline-block;
  backdrop-filter: blur(10px);
}

/* 右侧用户面板 - 悬浮卡片 */
.user-panel {
  width: 260px;
  background: #fff;
  border-radius: 12px;
  box-shadow: 0 4px 16px rgba(0,0,0,0.05);
  display: flex;
  flex-direction: column;
  overflow: hidden;
}

.user-card {
  padding: 24px;
  text-align: center;
  background: url('https://img.alicdn.com/imgextra/i2/O1CN01Z5y4lJ1eW2w9sX2xX_!!6000000003880-2-tps-284-160.png') no-repeat top center/contain; /* 装饰背景 */
  background-color: #fff;
}

.avatar-wrapper {
  margin: 10px 0 15px;
  padding: 4px;
  background: #fff;
  border-radius: 50%;
  display: inline-block;
  box-shadow: 0 4px 12px rgba(0,0,0,0.1);
}

.welcome-text {
  font-size: 16px;
  color: #333;
  margin-bottom: 20px;
  font-weight: 500;
}

.user-info-text {
  margin-bottom: 20px;
}

.username {
  font-size: 18px;
  font-weight: 600;
  color: #333;
  margin-bottom: 8px;
}

.user-stats {
  font-size: 13px;
  color: #666;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
}

.user-stats span {
  white-space: nowrap;
}

.user-stats .el-divider {
  margin: 0;
  height: 12px;
}

.action-buttons {
  display: flex;
  gap: 10px;
}

.pagination-wrapper {
  display: flex;
  justify-content: center;
  margin-top: 60px;
  margin-bottom: 20px;
}

.publish-btn, .login-btn, .profile-btn {
  flex: 1;
  border-radius: 20px !important; /* 胶囊按钮 */
  font-weight: 600;
}

.profile-btn {
  background: #f5f7fa;
  color: #606266;
  border-color: #e4e7ed;
}

.profile-btn:hover {
  background: #e4e7ed;
  color: #3e6bda;
  border-color: #3e6bda;
}

.notice-board {
  flex: 1;
  padding: 0 20px 20px;
  border-top: 1px solid #f5f5f5;
}

.notice-title {
  padding: 15px 0;
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-size: 14px;
  color: #333;
  font-weight: 600;
}

.notice-list {
  list-style: none;
  padding: 0;
  margin: 0;
}

.notice-list li {
  font-size: 13px;
  color: #666;
  margin-bottom: 10px;
  padding: 8px 12px;
  position: relative;
  cursor: pointer;
  transition: all 0.3s;
  border-radius: 4px;
  display: flex;
  align-items: center;
  gap: 6px;
}

.notice-list li:hover {
  color: #3e6bda;
  background: #f0f5ff;
  transform: translateX(4px);
}

.notice-list li:active {
  transform: translateX(2px);
}

/* === 数据统计卡片 === */
.statistics-section {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 20px;
  margin-bottom: 24px;
}

.stat-card {
  background: #fff;
  border-radius: 12px;
  padding: 24px;
  display: flex;
  align-items: center;
  gap: 16px;
  box-shadow: 0 2px 12px rgba(0,0,0,0.04);
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  cursor: pointer;
  position: relative;
  overflow: hidden;
}

.stat-card::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  height: 3px;
  background: linear-gradient(90deg, transparent, currentColor, transparent);
  opacity: 0;
  transition: opacity 0.3s;
}

.stat-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 8px 24px rgba(0,0,0,0.12);
}

.stat-card:hover::before {
  opacity: 1;
}

.stat-icon {
  width: 56px;
  height: 56px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 28px;
  flex-shrink: 0;
  transition: transform 0.3s;
}

.stat-card:hover .stat-icon {
  transform: scale(1.1) rotate(5deg);
}

.user-icon {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: #fff;
}

.product-icon {
  background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);
  color: #fff;
}

.order-icon {
  background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%);
  color: #fff;
}

.money-icon {
  background: linear-gradient(135deg, #43e97b 0%, #38f9d7 100%);
  color: #fff;
}

.stat-content {
  flex: 1;
}

.stat-value {
  font-size: 28px;
  font-weight: 700;
  color: #333;
  margin-bottom: 4px;
  font-family: 'DIN', 'Helvetica Neue', Arial, sans-serif;
  letter-spacing: -0.5px;
}

.stat-label {
  font-size: 13px;
  color: #999;
  font-weight: 500;
}

/* 响应式适配 */
@media (max-width: 1200px) {
  .statistics-section {
    grid-template-columns: repeat(2, 1fr);
  }
}

@media (max-width: 768px) {
  .statistics-section {
    grid-template-columns: 1fr;
  }
  
  .stat-card {
    padding: 20px;
  }
  
  .stat-icon {
    width: 48px;
    height: 48px;
    font-size: 24px;
  }
  
  .stat-value {
    font-size: 24px;
  }
}

/* === 筛选条 === */
.filter-bar {
  background: #fff;
  padding: 20px 24px;
  margin-bottom: 24px;
  border-radius: 12px;
  box-shadow: 0 2px 12px rgba(0,0,0,0.03);
  display: flex;
  align-items: center;
  gap: 24px;
}

.search-wrapper {
  width: 480px;
}

.search-wrapper :deep(.el-input__wrapper) {
  border-radius: 24px 0 0 24px;
  box-shadow: none;
  border: 2px solid #3e6bda;
  border-right: none;
}

.search-wrapper :deep(.el-input-group__append) {
  border-radius: 0 24px 24px 0;
  background-color: #3e6bda;
  border: 2px solid #3e6bda;
  color: white;
  box-shadow: none;
}

.search-btn {
  font-weight: 600;
  letter-spacing: 1px;
}

.hot-keywords {
  flex: 1;
  font-size: 14px;
  color: #909399;
  display: flex;
  align-items: center;
  flex-wrap: wrap;
}

.hot-keywords > span {
  margin-right: 12px;
  font-weight: 500;
  color: #666;
}

.hot-keywords a {
  display: inline-block;
  padding: 6px 16px;
  margin-right: 10px;
  margin-bottom: 4px;
  background: linear-gradient(135deg, #f5f7fa 0%, #e4e7ed 100%);
  border-radius: 16px;
  color: #606266;
  font-size: 13px;
  transition: all 0.3s;
  cursor: pointer;
  border: 1px solid transparent;
  font-weight: 500;
}

.hot-keywords a:hover {
  color: #fff;
  background: linear-gradient(135deg, #3e6bda 0%, #5b7fe8 100%);
  border-color: #3e6bda;
  transform: translateY(-2px);
  box-shadow: 0 4px 8px rgba(62, 107, 218, 0.3);
}

/* === 商品楼层 === */
.floor-section {
  background: #fff;
  padding: 24px;
  border-radius: 12px;
  box-shadow: 0 2px 12px rgba(0,0,0,0.03);
  min-height: 600px;
}

.floor-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-end;
  margin-bottom: 24px;
  padding-bottom: 16px;
  border-bottom: 1px solid #eee;
  position: relative;
}

.floor-header::after {
  content: '';
  position: absolute;
  bottom: -1px;
  left: 0;
  width: 96px;
  height: 3px;
  background: #3e6bda;
  border-radius: 3px;
}

.floor-title {
  font-size: 22px;
  color: #333;
  font-weight: 700;
  margin: 0;
}

.floor-tabs span {
  font-size: 15px;
  cursor: pointer;
  color: #666;
  padding: 6px 16px;
  border-radius: 16px;
  transition: all 0.2s;
}

.floor-tabs span:hover {
  color: #3e6bda;
  background: #f0f7ff;
}

.floor-tabs span.active {
  color: #fff;
  background: #3e6bda;
  font-weight: 600;
}

/* Web端商品网格 - 优化版 */
.product-grid-web {
  display: grid;
  grid-template-columns: repeat(5, 1fr);
  gap: 20px; /* 增加间距 */
}

.product-card-web {
  background: #fff;
  border-radius: 12px; /* 更大圆角 */
  overflow: hidden;
  transition: all 0.3s ease;
  border: 1px solid #f0f0f0;
  position: relative;
}

.product-card-web:hover {
  transform: translateY(-6px);
  box-shadow: 0 12px 24px rgba(0,0,0,0.08);
  border-color: transparent;
  z-index: 2;
}

.img-box {
  width: 100%;
  height: 0;
  padding-bottom: 100%; /* 1:1 正方形 */
  position: relative;
  background: #f8f9fa;
  overflow: hidden; /* 确保图片不溢出容器 */
}

.img-box img {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform 0.5s ease;
}

.product-card-web:hover .img-box :deep(.el-image__inner) {
  transform: scale(1.08);
}

.product-img {
  width: 100%;
  height: 100%;
  position: absolute;
  top: 0;
  left: 0;
}

/* el-image组件内部样式 */
.product-img :deep(.el-image__inner) {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.product-img :deep(.el-image__wrapper) {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
}

.image-slot {
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: #f5f7fa;
  color: #909399;
  font-size: 24px;
}

.empty-state {
  padding: 60px 0;
  display: flex;
  justify-content: center;
  min-height: 400px;
  align-items: center;
  background: #fafafa;
  border-radius: 8px;
  margin: 20px 0;
}

.status-tag {
  z-index: 2;
}

.info-box {
  padding: 14px;
}

.p-title {
  font-size: 15px;
  color: #333;
  margin: 0 0 8px;
  height: 42px;
  line-height: 21px;
  font-weight: 500;
  transition: color 0.2s;
}

.product-card-web:hover .p-title {
  color: #3e6bda;
}

.p-price-row {
  margin-bottom: 12px;
  display: flex;
  align-items: baseline;
}

.currency {
  color: #ff6b00; /* 活力橙 */
  font-size: 12px;
  margin-right: 2px;
}

.price {
  color: #ff6b00;
  font-size: 20px;
  font-weight: 700;
  font-family: "DIN Alternate", "Roboto", sans-serif; /* 数字字体 */
}

.time {
  font-size: 12px;
  color: #bbb;
  background: #f8f8f8;
  padding: 2px 6px;
  border-radius: 4px;
}

.p-seller-row {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding-top: 10px;
  border-top: 1px solid #f8f8f8;
}

.seller-info {
  display: flex;
  align-items: center;
  gap: 6px;
}

.seller-name {
  font-size: 12px;
  color: #666;
}

.location {
  font-size: 12px;
  color: #999;
  transform: scale(0.9);
  transform-origin: right center;
}

.load-more-wrapper {
  margin-top: 48px;
}

.load-more-wrapper .el-button {
  width: 200px;
  height: 44px;
  font-size: 15px;
  border-radius: 22px;
  border-color: #e0e0e0;
  color: #666;
}

.load-more-wrapper .el-button:hover {
  border-color: #3e6bda;
  color: #3e6bda;
  background: #ecf5ff;
}

/* === 公告对话框样式 === */
.announcements-dialog {
  max-height: 500px;
  overflow-y: auto;
}

.announcement-item {
  padding: 20px;
  border: 1px solid #eee;
  border-radius: 8px;
  margin-bottom: 16px;
  cursor: pointer;
  transition: all 0.3s;
}

.announcement-item:hover {
  border-color: #3e6bda;
  box-shadow: 0 2px 12px rgba(62, 107, 218, 0.1);
  transform: translateY(-2px);
}

.announcement-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12px;
}

.announcement-header h3 {
  font-size: 16px;
  font-weight: 600;
  color: #333;
  margin: 0;
  display: flex;
  align-items: center;
  gap: 8px;
}

.view-count {
  display: flex;
  align-items: center;
  gap: 4px;
  font-size: 13px;
  color: #999;
}

.announcement-content {
  font-size: 14px;
  color: #666;
  line-height: 1.6;
  margin: 12px 0;
  overflow: hidden;
  text-overflow: ellipsis;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  line-clamp: 2;
  -webkit-box-orient: vertical;
}

.announcement-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-size: 12px;
  color: #999;
}

.create-time {
  font-size: 12px;
  color: #999;
}

/* === 公告详情对话框样式 === */
.announcement-detail {
  padding: 10px 0;
}

.detail-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding-bottom: 16px;
  border-bottom: 1px solid #eee;
  margin-bottom: 20px;
}

.detail-content {
  font-size: 15px;
  color: #333;
  line-height: 1.8;
  white-space: pre-wrap;
  word-break: break-word;
  padding: 10px 0;
  min-height: 100px;
}

.detail-footer {
  margin-top: 20px;
  padding-top: 16px;
  border-top: 1px solid #eee;
}

.detail-time {
  font-size: 13px;
  color: #999;
}

/* 联系客服悬浮按钮 */
.contact-service-btn {
  position: fixed;
  right: 30px;
  bottom: 80px;
  z-index: 9999 !important;
  width: 56px;
  height: 56px;
  font-size: 24px;
  box-shadow: 0 4px 12px rgba(64, 158, 255, 0.4);
  transition: all 0.3s ease;
  pointer-events: auto !important;
}

.contact-service-btn:hover {
  transform: translateY(-4px);
  box-shadow: 0 6px 16px rgba(64, 158, 255, 0.5);
}

.contact-service-btn:active {
  transform: translateY(-2px);
}
</style>










