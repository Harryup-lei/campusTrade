<template>
  <div class="news-page">
    <div class="main-container">
      <div class="page-header">
        <h2>校园资讯</h2>
        <p>了解最新校园动态，二手交易安全须知</p>
      </div>

      <!-- 分类筛选 -->
      <div class="category-bar">
        <div 
          class="category-item" 
          :class="{ active: currentCategory === '' }"
          @click="handleCategoryChange('')"
        >
          全部
        </div>
        <div 
          v-for="cat in categories" 
          :key="cat"
          class="category-item" 
          :class="{ active: currentCategory === cat }"
          @click="handleCategoryChange(cat)"
        >
          {{ cat }}
        </div>
      </div>

      <div class="news-content">
        <!-- 左侧列表 -->
        <div class="news-list">
          <!-- 加载状态 -->
          <div v-if="loading" class="loading-container">
            <el-skeleton :rows="3" animated />
            <el-skeleton :rows="3" animated style="margin-top: 20px;" />
          </div>

          <!-- 资讯列表 -->
          <div 
            v-else-if="newsList.length > 0"
            v-for="item in newsList" 
            :key="item.id" 
            class="news-card"
            @click="goToDetail(item.id)"
          >
            <div class="news-cover">
              <el-image :src="item.coverImage" fit="cover" lazy>
                <template #error>
                  <div class="image-slot"><el-icon><Picture /></el-icon></div>
                </template>
              </el-image>
            </div>
            <div class="news-info">
              <h3 class="title">{{ item.title }}</h3>
              <p class="summary">{{ getSummary(item.content) }}</p>
              <div class="meta">
                <span class="tag" v-if="item.category">{{ item.category }}</span>
                <span class="date">{{ formatTime(item.createTime) }}</span>
                <span class="views"><el-icon><View /></el-icon> {{ item.viewCount }}</span>
              </div>
            </div>
          </div>

          <!-- 空状态 -->
          <div v-else class="empty-state">
            <el-empty description="暂无资讯数据" />
          </div>
        </div>

        <!-- 右侧热门 -->
        <div class="sidebar">
          <div class="sidebar-card">
            <h3>热门阅读</h3>
            <ul class="hot-list">
              <li v-for="(item, index) in hotList" :key="item.id" @click="goToDetail(item.id)">
                <span class="rank" :class="{ top: index < 3 }">{{ index + 1 }}</span>
                <span class="text">{{ item.title }}</span>
              </li>
            </ul>
          </div>
        </div>
      </div>

      <!-- 分页 -->
      <div class="pagination-box" v-if="!loading && newsList.length > 0">
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
// 文件: UI/frontend/src/views/News.vue
// hangu: 2 行 | kelei: 0 行 | 本文件合计: 2 行
// ============================================================
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { Picture, View } from '@element-plus/icons-vue'
import { getNewsList, getNewsCategories } from '@/api/news'

const router = useRouter()
const newsList = ref([])
const hotList = ref([])
const loading = ref(false)
const currentCategory = ref('')
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)

// 分类列表
const categories = ref([])

// 获取分类列表
const fetchCategories = async () => {
  try {
    console.log('开始获取分类列表...')
    const res = await getNewsCategories()
    console.log('分类API返回:', res)
    
    if (res && res.data) {
      // 提取分类名称
      categories.value = Array.isArray(res.data) ? res.data.map(cat => cat.name) : []
      console.log('分类列表:', categories.value)
    } else {
      console.warn('分类数据格式不正确，使用默认分类')
      categories.value = ['校园通知', '校园活动', '学术交流', '生活服务', '其他']
    }
  } catch (error) {
    console.error('获取分类失败:', error)
    // 使用默认分类作为后备
    categories.value = ['校园通知', '校园活动', '学术交流', '生活服务', '其他']
  }
}

// 获取数据
const fetchData = async () => {
  loading.value = true
  try {
    const params = {
      page: currentPage.value - 1,
      // @contributor hangu - fetchData (2 行)
      size: pageSize.value
    }
    
    if (currentCategory.value) {
      params.category = currentCategory.value
    }
    
    const res = await getNewsList(params)
    
    if (res && res.data) {
      const list = res.data.records || res.data.content || res.data
      newsList.value = Array.isArray(list) ? list : []
      total.value = res.data.total || res.data.totalElements || (Array.isArray(list) ? list.length : 0)
      
      // 热门阅读：按浏览量排序（从全部数据中获取）
      fetchHotNews()
    } else {
      newsList.value = []
      total.value = 0
    }
  } catch (error) {
    console.error('获取资讯列表失败:', error)
    newsList.value = []
    total.value = 0
  } finally {
    loading.value = false
  }
}

// 获取热门资讯
const fetchHotNews = async () => {
  try {
    const res = await getNewsList({ page: 0, size: 10 })
    if (res && res.data) {
      const list = res.data.records || res.data.content || res.data
      hotList.value = Array.isArray(list) ? list.sort((a, b) => b.viewCount - a.viewCount).slice(0, 5) : []
    }
  } catch (error) {
    console.error('获取热门资讯失败:', error)
  }
}

const handleCategoryChange = (category) => {
  currentCategory.value = category
  currentPage.value = 1
  fetchData()
}

const handlePageChange = (page) => {
  currentPage.value = page
  fetchData()
  window.scrollTo({ top: 0, behavior: 'smooth' })
}

const goToDetail = (id) => {
  router.push(`/news/${id}`)
}

const getSummary = (content) => {
  if (!content) return ''
  // 生成摘要：取前100个字符
  return content.length > 100 ? content.substring(0, 100) + '...' : content
}

const formatTime = (timeStr) => {
  const date = new Date(timeStr)
  return `${date.getFullYear()}-${date.getMonth() + 1}-${date.getDate()}`
}

onMounted(() => {
  fetchCategories()
  fetchData()
})
</script>

<style scoped>
.news-page {
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
  padding: 24px;
  border-radius: 12px;
  margin-bottom: 20px;
  box-shadow: 0 2px 12px rgba(0,0,0,0.03);
}

.page-header h2 {
  margin: 0 0 8px 0;
  font-size: 24px;
  color: #333;
}

.page-header p {
  margin: 0;
  color: #666;
  font-size: 14px;
}

.news-content {
  display: flex;
  gap: 20px;
}

.news-list {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.news-card {
  background: #fff;
  border-radius: 12px;
  padding: 20px;
  display: flex;
  gap: 20px;
  cursor: pointer;
  transition: all 0.2s;
  box-shadow: 0 2px 8px rgba(0,0,0,0.02);
}

.news-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 8px 16px rgba(0,0,0,0.08);
}

.news-cover {
  width: 200px;
  height: 130px;
  border-radius: 8px;
  overflow: hidden;
  flex-shrink: 0;
}

.news-cover .el-image {
  width: 100%;
  height: 100%;
}

.news-info {
  flex: 1;
  display: flex;
  flex-direction: column;
}

.title {
  font-size: 18px;
  color: #333;
  margin: 0 0 12px 0;
  font-weight: 600;
}

.summary {
  font-size: 14px;
  color: #666;
  line-height: 1.6;
  margin: 0 0 auto 0;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.meta {
  display: flex;
  align-items: center;
  gap: 16px;
  font-size: 13px;
  color: #999;
}

.tag {
  padding: 2px 8px;
  border-radius: 4px;
  font-size: 12px;
  background: #e6f7ff;
  color: #1890ff;
}

.views {
  display: flex;
  align-items: center;
  gap: 4px;
}

.sidebar {
  width: 300px;
  flex-shrink: 0;
}

.sidebar-card {
  background: #fff;
  border-radius: 12px;
  padding: 20px;
  box-shadow: 0 2px 8px rgba(0,0,0,0.02);
}

.sidebar-card h3 {
  margin: 0 0 16px 0;
  font-size: 16px;
  border-left: 4px solid #3e6bda;
  padding-left: 10px;
}

.hot-list {
  list-style: none;
  padding: 0;
  margin: 0;
}

.hot-list li {
  display: flex;
  align-items: center;
  gap: 10px;
  margin-bottom: 12px;
  cursor: pointer;
}

.hot-list li:hover .text {
  color: #3e6bda;
}

.rank {
  width: 20px;
  height: 20px;
  background: #f0f0f0;
  color: #999;
  border-radius: 4px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 12px;
  font-weight: bold;
}

.rank.top {
  background: #ff6b00;
  color: #fff;
}

.text {
  flex: 1;
  font-size: 14px;
  color: #333;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  transition: color 0.2s;
}

.image-slot {
  display: flex;
  justify-content: center;
  align-items: center;
  width: 100%;
  height: 100%;
  background: #f5f7fa;
  color: #ccc;
  font-size: 24px;
}

/* 分类筛选栏 */
.category-bar {
  background: #fff;
  padding: 16px 24px;
  border-radius: 12px;
  margin-bottom: 20px;
  display: flex;
  gap: 12px;
  box-shadow: 0 2px 8px rgba(0,0,0,0.02);
  flex-wrap: wrap;
}

.category-item {
  padding: 8px 20px;
  border-radius: 20px;
  font-size: 14px;
  color: #666;
  background: #f5f7fa;
  cursor: pointer;
  transition: all 0.2s;
  user-select: none;
}

.category-item:hover {
  background: #e6f7ff;
  color: #1890ff;
}

.category-item.active {
  background: linear-gradient(135deg, #3e6bda 0%, #5c7cfa 100%);
  color: #fff;
  font-weight: 600;
  box-shadow: 0 2px 8px rgba(62, 107, 218, 0.3);
}

/* 加载状态 */
.loading-container {
  background: #fff;
  border-radius: 12px;
  padding: 20px;
}

/* 空状态 */
.empty-state {
  background: #fff;
  border-radius: 12px;
  padding: 60px 40px;
  display: flex;
  justify-content: center;
}

/* 分页 */
.pagination-box {
  display: flex;
  justify-content: center;
  margin-top: 30px;
}

.pagination-box :deep(.el-pagination) {
  background: #fff;
  padding: 16px;
  border-radius: 12px;
  box-shadow: 0 2px 8px rgba(0,0,0,0.02);
}

.pagination-box :deep(.el-pager li.is-active) {
  background: linear-gradient(135deg, #3e6bda 0%, #5c7cfa 100%);
  color: #fff;
}
</style>










