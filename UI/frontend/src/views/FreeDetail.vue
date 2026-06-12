<template>
  <div class="free-detail-page">
    <div class="main-container">
      <el-skeleton :loading="loading" animated :count="1">
        <template #template>
          <div class="skeleton-box">
            <el-skeleton-item variant="image" style="width: 600px; height: 600px" />
            <div style="padding: 14px">
              <el-skeleton-item variant="h1" style="width: 50%" />
              <el-skeleton-item variant="text" style="margin-top: 16px" />
            </div>
          </div>
        </template>

        <template #default>
          <div v-if="freeItem" class="detail-content">
            <!-- 左侧：图片 -->
            <div class="left-section">
              <div class="image-section">
                <el-image 
                  :src="currentImage" 
                  fit="contain"
                  :preview-src-list="imageList"
                  class="main-image"
                >
                  <template #error>
                    <div class="image-slot">
                      <el-icon><Picture /></el-icon>
                    </div>
                  </template>
                </el-image>
                
                <!-- 缩略图 -->
                <div v-if="imageList.length > 1" class="thumbnail-list">
                  <div
                    v-for="(img, index) in imageList"
                    :key="index"
                    class="thumbnail-item"
                    :class="{ active: currentImage === img }"
                    @click="currentImage = img"
                  >
                    <el-image :src="img" fit="cover" />
                  </div>
                </div>
              </div>
            </div>

            <!-- 右侧：信息 -->
            <div class="right-section">
              <div class="header-info">
                <h1 class="title">{{ freeItem.title }}</h1>
                <el-tag type="success" effect="dark" size="large">免费赠送</el-tag>
              </div>

              <div class="price-box">
                <div class="price">¥0.00</div>
                <div class="price-tip">完全免费，自取或邮费自付</div>
              </div>

              <div class="info-list">
                <div class="info-item">
                  <span class="label">
                    <el-icon><LocationFilled /></el-icon>
                    地点
                  </span>
                  <span class="value">{{ freeItem.location || '未填写' }}</span>
                </div>
                
                <div class="info-item">
                  <span class="label">
                    <el-icon><Phone /></el-icon>
                    联系方式
                  </span>
                  <span class="value contact">{{ freeItem.contact || '未填写' }}</span>
                </div>

                <div class="info-item">
                  <span class="label">
                    <el-icon><View /></el-icon>
                    浏览次数
                  </span>
                  <span class="value">{{ freeItem.viewCount || 0 }} 次</span>
                </div>

                <div class="info-item">
                  <span class="label">
                    <el-icon><Calendar /></el-icon>
                    发布时间
                  </span>
                  <span class="value">{{ formatTime(freeItem.createTime) }}</span>
                </div>
              </div>

              <!-- 赠送者信息 -->
              <div class="seller-info">
                <div class="seller-header">
                  <el-avatar :size="50" :src="freeItem.user?.avatar" icon="UserFilled" />
                  <div class="seller-detail">
                    <div class="name">{{ freeItem.user?.nickname || '用户' }}</div>
                    <div class="desc">赠送者</div>
                  </div>
                </div>
              </div>

              <!-- 操作按钮 -->
              <div class="action-buttons">
                <el-button 
                  type="success" 
                  size="large" 
                  :icon="ChatDotSquare"
                  @click="handleContact"
                  round
                >
                  联系赠送者
                </el-button>
                <el-button 
                  v-if="isOwner"
                  size="large"
                  @click="handleEdit"
                  round
                >
                  编辑信息
                </el-button>
              </div>
            </div>
          </div>

          <!-- 商品描述 -->
          <div v-if="freeItem" class="description-section">
            <h3>物品描述</h3>
            <div class="description-content">
              {{ freeItem.description || '暂无描述' }}
            </div>
          </div>
        </template>
      </el-skeleton>
    </div>
  </div>
</template>

<script setup>
// ============================================================
// 代码贡献统计（项目总计 3000 行）
// 文件: UI/frontend/src/views/FreeDetail.vue
// hangu: 0 行 | kelei: 0 行 | 本文件合计: 0 行
// ============================================================
import { ref, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { 
  Picture, 
  LocationFilled, 
  Phone, 
  View, 
  Calendar, 
  UserFilled, 
  ChatDotSquare 
} from '@element-plus/icons-vue'
import { getFreeItem } from '@/api/free'
import { useUserStore } from '@/stores/user'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()

const loading = ref(true)
const freeItem = ref(null)
const currentImage = ref('')
const imageList = ref([])

// 是否是赠送者本人
const isOwner = computed(() => {
  return userStore.isLoggedIn && 
         freeItem.value && 
         freeItem.value.userId === userStore.userInfo?.id
})

// 获取赠送详情
const fetchDetail = async () => {
  loading.value = true
  try {
    const res = await getFreeItem(route.params.id)
    
    if (res && res.data) {
      freeItem.value = res.data
      
      // 处理图片列表
      const images = res.data.images
      if (images) {
        if (Array.isArray(images)) {
          imageList.value = images.map(formatImageUrl)
        } else if (typeof images === 'string') {
          imageList.value = images.split(',').map(img => formatImageUrl(img.trim()))
        }
      }
      
      if (imageList.value.length > 0) {
        currentImage.value = imageList.value[0]
      }
    }
  } catch (error) {
    console.error('获取赠送详情失败:', error)
    ElMessage.error('获取详情失败')
  } finally {
    loading.value = false
  }
}

// 格式化图片URL
const formatImageUrl = (url) => {
  if (!url) return ''
  if (url.startsWith('http://') || url.startsWith('https://')) {
    return url
  }
  if (url.startsWith('/api/')) {
    return url
  }
  if (!url.startsWith('/')) {
    url = '/' + url
  }
  return '/api' + url
}

// 格式化时间
const formatTime = (timeStr) => {
  if (!timeStr) return '-'
  const date = new Date(timeStr)
  return date.toLocaleDateString('zh-CN', { 
    year: 'numeric', 
    month: '2-digit', 
    day: '2-digit',
    hour: '2-digit',
    minute: '2-digit'
  })
}

// 联系赠送者
const handleContact = () => {
  if (!userStore.isLoggedIn) {
    ElMessage.warning('请先登录')
    router.push('/login')
    return
  }

  if (!freeItem.value.contact) {
    ElMessage.warning('赠送者未填写联系方式')
    return
  }

  ElMessageBox.alert(
    `联系方式：${freeItem.value.contact}`,
    '联系赠送者',
    {
      confirmButtonText: '好的',
      type: 'success'
    }
  )
}

// 编辑信息
const handleEdit = () => {
  router.push(`/publish-free?id=${freeItem.value.id}`)
}

onMounted(() => {
  fetchDetail()
})
</script>

<style scoped>
.free-detail-page {
  min-height: 100vh;
  background: #f5f5f5;
  padding: 20px 0;
}

.main-container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 20px;
}

.skeleton-box {
  display: flex;
  gap: 40px;
}

.detail-content {
  display: flex;
  gap: 40px;
  background: white;
  border-radius: 12px;
  padding: 30px;
  margin-bottom: 20px;
}

/* 左侧图片区域 */
.left-section {
  flex: 1;
  min-width: 0;
}

.image-section {
  position: sticky;
  top: 80px;
}

.main-image {
  width: 100%;
  height: 500px;
  border-radius: 12px;
  overflow: hidden;
  background: #fafafa;
  border: 1px solid #e8e8e8;
}

.main-image :deep(.el-image__inner) {
  object-fit: contain;
}

.image-slot {
  display: flex;
  justify-content: center;
  align-items: center;
  width: 100%;
  height: 100%;
  font-size: 80px;
  color: #ccc;
}

.thumbnail-list {
  display: flex;
  gap: 10px;
  margin-top: 15px;
  overflow-x: auto;
  padding-bottom: 5px;
}

.thumbnail-item {
  width: 80px;
  height: 80px;
  border-radius: 8px;
  overflow: hidden;
  cursor: pointer;
  border: 2px solid transparent;
  transition: all 0.3s;
  flex-shrink: 0;
}

.thumbnail-item:hover {
  border-color: #67c23a;
}

.thumbnail-item.active {
  border-color: #67c23a;
}

.thumbnail-item .el-image {
  width: 100%;
  height: 100%;
}

/* 右侧信息区域 */
.right-section {
  flex: 1;
  min-width: 0;
}

.header-info {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  gap: 20px;
  margin-bottom: 20px;
}

.title {
  margin: 0;
  font-size: 28px;
  color: #333;
  font-weight: 600;
  line-height: 1.4;
  flex: 1;
}

.price-box {
  padding: 20px;
  background: linear-gradient(135deg, #67c23a 0%, #85ce61 100%);
  border-radius: 12px;
  margin-bottom: 25px;
}

.price {
  font-size: 36px;
  color: white;
  font-weight: 700;
  margin-bottom: 5px;
}

.price-tip {
  color: rgba(255, 255, 255, 0.9);
  font-size: 14px;
}

.info-list {
  border: 1px solid #e8e8e8;
  border-radius: 8px;
  overflow: hidden;
  margin-bottom: 25px;
}

.info-item {
  display: flex;
  padding: 15px 20px;
  border-bottom: 1px solid #f0f0f0;
}

.info-item:last-child {
  border-bottom: none;
}

.info-item .label {
  width: 120px;
  color: #999;
  font-size: 14px;
  display: flex;
  align-items: center;
  gap: 6px;
}

.info-item .value {
  flex: 1;
  color: #333;
  font-size: 14px;
  word-break: break-all;
}

.info-item .value.contact {
  color: #67c23a;
  font-weight: 600;
}

.seller-info {
  padding: 20px;
  background: #fafafa;
  border-radius: 8px;
  margin-bottom: 25px;
}

.seller-header {
  display: flex;
  align-items: center;
  gap: 15px;
}

.seller-detail .name {
  font-size: 16px;
  color: #333;
  font-weight: 600;
  margin-bottom: 4px;
}

.seller-detail .desc {
  font-size: 13px;
  color: #999;
}

.action-buttons {
  display: flex;
  gap: 15px;
}

.action-buttons .el-button {
  flex: 1;
}

/* 描述区域 */
.description-section {
  background: white;
  border-radius: 12px;
  padding: 30px;
}

.description-section h3 {
  margin: 0 0 20px 0;
  font-size: 20px;
  color: #333;
  padding-bottom: 15px;
  border-bottom: 1px solid #e8e8e8;
}

.description-content {
  color: #666;
  line-height: 1.8;
  font-size: 15px;
  white-space: pre-wrap;
  word-break: break-word;
}

/* 响应式 */
@media (max-width: 768px) {
  .detail-content {
    flex-direction: column;
    padding: 20px;
  }

  .main-image {
    height: 300px;
  }

  .title {
    font-size: 22px;
  }

  .price {
    font-size: 28px;
  }

  .action-buttons {
    flex-direction: column;
  }
}
</style>










