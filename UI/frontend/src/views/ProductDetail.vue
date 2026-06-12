<template>
  <div class="product-detail-page">
    <div class="main-container" v-if="product">
      <!-- 面包屑导航 -->
      <el-breadcrumb separator="/" class="breadcrumb">
        <el-breadcrumb-item :to="{ path: '/' }">首页</el-breadcrumb-item>
        <el-breadcrumb-item :to="{ path: '/products' }">全部商品</el-breadcrumb-item>
        <el-breadcrumb-item v-if="product.category">{{ product.category.name }}</el-breadcrumb-item>
        <el-breadcrumb-item>{{ product.title }}</el-breadcrumb-item>
      </el-breadcrumb>
      <el-row :gutter="40">
        <!-- 左侧：图片展示 -->
        <el-col :span="14">
          <div class="gallery">
            <div class="main-image-box">
              <el-image 
                :src="activeImage" 
                :preview-src-list="images"
                fit="contain"
                class="main-image"
              />
            </div>
            <div class="thumbnail-list" v-if="images.length > 1">
              <div 
                v-for="(img, index) in images" 
                :key="index"
                class="thumbnail-item"
                :class="{ active: activeImage === img }"
                @mouseenter="activeImage = img"
              >
                <el-image :src="img" fit="cover" class="thumbnail-img" />
              </div>
            </div>
          </div>
        </el-col>

        <!-- 右侧：商品信息 -->
        <el-col :span="10">
          <div class="product-info">
            <div class="title-row">
              <h1 class="title">{{ product.title }}</h1>
              <div class="title-actions">
                <el-button circle icon="Share" size="small" @click="handleShare" title="分享"></el-button>
                <el-button circle icon="Warning" size="small" @click="handleReport" title="举报" type="danger" plain></el-button>
              </div>
            </div>
            <div class="meta-info">
              <span class="views"><el-icon><View /></el-icon> {{ product.viewCount || 0 }}人浏览</span>
              <span class="time">发布于 {{ formatTime(product.createTime) }}</span>
            </div>
            
            <div class="price-box">
              <span class="currency">¥</span>
              <span class="price">{{ product.price }}</span>
              <span class="original-price" v-if="product.originalPrice">原价 ¥{{ product.originalPrice }}</span>
            </div>

            <div class="info-tags">
              <el-tag effect="plain" type="success" v-if="product.condition">
                {{ getConditionText(product.condition) }}
              </el-tag>
              <el-tag effect="plain" type="warning" v-if="product.location">
                <el-icon><Location /></el-icon> {{ product.location }}
              </el-tag>
              <el-tag effect="plain" type="info">
                库存: {{ product.stock || 0 }}
              </el-tag>
              <el-tag effect="plain" v-if="product.category">
                {{ product.category.name }}
              </el-tag>
            </div>

            <!-- 卖家卡片 -->
            <el-card class="seller-card" shadow="never">
              <div class="seller-info">
                <el-avatar :size="50" :src="product.seller?.avatar || defaultAvatar" />
                <div class="seller-text">
                  <div class="seller-name">
                    {{ product.seller?.nickname || product.seller?.username }}
                    <el-tag v-if="product.seller?.isVerified" type="success" size="small" style="margin-left: 8px;">
                      已认证
                    </el-tag>
                  </div>
                  <div class="seller-credit" v-if="sellerCredit">
                    <span class="credit-level" :style="{ color: sellerCreditColor, fontWeight: 'bold' }">
                      {{ sellerCreditLevel }}
                    </span>
                    <span class="credit-divider">|</span>
                    <span class="credit-score">信用分: {{ sellerCreditScore }}</span>
                  </div>
                  <div class="seller-credit" v-else>
                    <span class="credit-loading">加载中...</span>
                  </div>
                </div>
              </div>
            </el-card>

            <!-- 购买操作 -->
            <div class="actions">
              <div class="buy-quantity">
                <span>购买数量：</span>
                <el-input-number v-model="quantity" :min="1" :max="product.stock" :disabled="product.stock <= 0" />
              </div>
              <div class="btn-group">
                <el-button 
                  type="primary" 
                  size="large" 
                  class="buy-btn" 
                  @click="handleBuy" 
                  :disabled="product.stock <= 0"
                  :loading="buying"
                >
                  {{ product.stock > 0 ? '立即购买' : '已售罄' }}
                </el-button>
                <el-button size="large" icon="Star" @click="toggleWant">想要</el-button>
              </div>
            </div>
          </div>
        </el-col>
      </el-row>

      <!-- 底部：详细描述 -->
      <div class="detail-section">
        <el-tabs v-model="activeTab">
          <el-tab-pane label="商品详情" name="detail">
            <div class="description-content">
              <div class="detail-block">
                <h3>商品描述</h3>
                <p style="white-space: pre-wrap;">{{ product.description }}</p>
              </div>
              
              <div class="detail-block" v-if="product.conditionDesc">
                <h3>成色说明</h3>
                <p>{{ product.conditionDesc }}</p>
              </div>
              
              <div class="detail-block" v-if="images.length > 0">
                <h3>商品图片</h3>
                <div class="desc-images">
                  <el-image 
                    v-for="(img, idx) in images" 
                    :key="idx" 
                    :src="img" 
                    :preview-src-list="images"
                    fit="cover"
                    lazy 
                  />
                </div>
              </div>
            </div>
          </el-tab-pane>
          <el-tab-pane label="留言提问" name="comments">
            <div class="comments-section">
              <!-- 发表留言 -->
              <div class="comment-form">
                <el-input
                  v-model="newComment"
                  type="textarea"
                  :rows="3"
                  placeholder="有什么问题想问卖家？"
                  maxlength="500"
                  show-word-limit
                />
                <div class="form-actions">
                  <el-button type="primary" @click="handleSubmitComment" :loading="submittingComment">
                    发表留言
                  </el-button>
                </div>
              </div>

              <!-- 留言列表 -->
              <div class="comments-list">
                <div class="comments-header">
                  <span>全部留言 ({{ comments.length }})</span>
                </div>
                
                <div v-if="comments.length === 0" class="empty-comments">
                  <el-empty description="还没有留言，快来抢沙发吧~" />
                </div>

                <div v-else class="comment-item" v-for="comment in comments" :key="comment.id">
                  <div class="comment-header">
                    <el-avatar :size="40" :src="comment.user?.avatar || defaultAvatar" />
                    <div class="comment-user-info">
                      <div class="user-name">{{ comment.user?.nickname || comment.user?.username }}</div>
                      <div class="comment-time">{{ formatCommentTime(comment.createTime) }}</div>
                    </div>
                  </div>
                  <div class="comment-content">{{ comment.content }}</div>
                  
                  <!-- 回复 -->
                  <div class="comment-actions">
                    <el-button text size="small" @click="toggleReply(comment.id)">
                      <el-icon><ChatDotRound /></el-icon> 回复
                    </el-button>
                  </div>

                  <!-- 回复列表 -->
                  <div class="replies" v-if="comment.replies && comment.replies.length > 0">
                    <div class="reply-item" v-for="reply in comment.replies" :key="reply.id">
                      <div class="reply-header">
                        <el-avatar :size="32" :src="reply.user?.avatar || defaultAvatar" />
                        <div class="reply-user-info">
                          <span class="user-name">{{ reply.user?.nickname || reply.user?.username }}</span>
                          <span class="reply-time">{{ formatCommentTime(reply.createTime) }}</span>
                        </div>
                      </div>
                      <div class="reply-content">{{ reply.content }}</div>
                    </div>
                  </div>

                  <!-- 回复输入框 -->
                  <div class="reply-form" v-if="replyingTo === comment.id">
                    <el-input
                      v-model="replyContent"
                      type="textarea"
                      :rows="2"
                      placeholder="写下你的回复..."
                      maxlength="200"
                    />
                    <div class="reply-actions">
                      <el-button size="small" @click="cancelReply">取消</el-button>
                      <el-button size="small" type="primary" @click="handleSubmitReply(comment.id)" :loading="submittingReply">
                        发表回复
                      </el-button>
                    </div>
                  </div>
                </div>
              </div>
            </div>
          </el-tab-pane>
        </el-tabs>
      </div>

      <!-- 相似商品推荐 -->
      <div class="recommend-section" v-if="similarProducts.length > 0">
        <h2 class="section-title">相似商品推荐</h2>
        <div class="product-grid">
          <div 
            v-for="item in similarProducts" 
            :key="item.id"
            class="product-card"
            @click="goToProduct(item.id)"
          >
            <div class="product-image">
              <el-image :src="getProductImage(item.images)" fit="cover" />
            </div>
            <div class="product-info-mini">
              <h3>{{ item.title }}</h3>
              <div class="price">¥{{ item.price }}</div>
            </div>
          </div>
        </div>
      </div>
    </div>
    
    <div class="loading-box" v-else v-loading="true"></div>
    
    <!-- 举报对话框 -->
    <el-dialog
      v-model="reportDialogVisible"
      title="举报商品"
      width="500px"
      :close-on-click-modal="false"
    >
      <el-form :model="reportForm" label-width="80px">
        <el-form-item label="举报原因" required>
          <el-select v-model="reportForm.reason" placeholder="请选择举报原因" style="width: 100%;">
            <el-option
              v-for="reason in reportReasons"
              :key="reason"
              :label="reason"
              :value="reason"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="详细描述">
          <el-input
            v-model="reportForm.description"
            type="textarea"
            :rows="4"
            placeholder="请详细描述举报原因（选填）"
            maxlength="500"
            show-word-limit
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="reportDialogVisible = false">取消</el-button>
        <el-button type="danger" @click="submitReport">提交举报</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
// ============================================================
// 代码贡献统计（项目总计 3000 行）
// 文件: UI/frontend/src/views/ProductDetail.vue
// hangu: 38 行 | kelei: 84 行 | 本文件合计: 122 行
// ============================================================
import { ref, computed, onMounted, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { getProduct, getProducts } from '@/api/product'
import { createOrder } from '@/api/order'
import { getComments, createComment, createReply } from '@/api/comment'
import { useUserStore } from '@/stores/user'
import { ElMessage, ElMessageBox } from 'element-plus'
import request from '@/utils/request'
import { View, ChatDotRound, Star, Location, Share, Warning } from '@element-plus/icons-vue'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()

const product = ref(null)
const activeImage = ref('')
const activeTab = ref('detail')
const quantity = ref(1)
const buying = ref(false)
const defaultAvatar = 'https://cube.elemecdn.com/0/88/03b0d39583f48206768a7534e55bcpng.png'
const similarProducts = ref([])
const comments = ref([])
const newComment = ref('')
const submittingComment = ref(false)
const replyingTo = ref(null)
const replyContent = ref('')
const submittingReply = ref(false)
const sellerCredit = ref(null)
const sellerCreditScore = computed(() => sellerCredit.value?.creditScore || 100)
const sellerCreditLevel = computed(() => {
  const level = sellerCredit.value?.level || 'GOOD'
  const levelMap = {
    'EXCELLENT': '信誉优秀',
    'GOOD': '信誉良好',
    'NORMAL': '信誉一般',
    'BAD': '信誉较差'
  }
  return levelMap[level] || '信誉良好'
})
const sellerCreditColor = computed(() => {
  const level = sellerCredit.value?.level || 'GOOD'
  const colorMap = {
    'EXCELLENT': '#67c23a',
    'GOOD': '#409eff',
    'NORMAL': '#e6a23c',
    'BAD': '#f56c6c'
  }
  return colorMap[level] || '#409eff'
})

const images = computed(() => {
  if (!product.value?.images) return []
  
  // 处理图片数组
  let imageList = []
  
  if (Array.isArray(product.value.images)) {
    imageList = product.value.images
  } else if (typeof product.value.images === 'string') {
    imageList = product.value.images.split(',').filter(img => img)
  }
  
  // 处理图片URL，添加/api前缀
  return imageList.map(img => {
    if (img.startsWith('http://') || img.startsWith('https://')) {
      return img
    }
    if (img.startsWith('/api/')) {
      return img
    }
    if (!img.startsWith('/')) {
      img = '/' + img
    }
    return '/api' + img
  })
})
const fetchProduct = async () => {
  try {
    console.log('===== 获取商品详情 =====')
    console.log('商品ID:', route.params.id)
    const res = await getProduct(route.params.id)
    // @contributor kelei - fetchProduct (3 行)
    console.log('商品详情响应:', res)
    product.value = res.data
    console.log('处理后的图片列表:', images.value)
    
    if (images.value.length > 0) {
      activeImage.value = images.value[0]
    }
    
    // 获取卖家信用
    if (product.value.seller?.id) {
      fetchSellerCredit(product.value.seller.id)
    }
    
    // 获取相似商品
    if (product.value.categoryId) {
      fetchSimilarProducts()
    }
  } catch (error) {
    console.error('获取商品详情失败:', error)
    ElMessage.error('获取商品详情失败')
    // 如果是404，返回首页
    if (error.response?.status === 404) {
      setTimeout(() => {
        router.push('/')
      }, 2000)
    }
  }
}

// 获取卖家信用
const fetchSellerCredit = async (sellerId) => {
  try {
    const res = await request.get(`/credit/user/${sellerId}`)
    if (res && res.data) {
      sellerCredit.value = res.data
    }
  } catch (error) {
    console.error('获取卖家信用失败:', error)
  }
}

const handleBuy = async () => {
  if (!userStore.token) {
    ElMessage.warning('请先登录')
    router.push(`/login?redirect=${route.fullPath}`)
    return
  // @contributor kelei - handleBuy (43 行)
  }
  
  if (product.value.seller?.id === userStore.userInfo?.id) {
    ElMessage.warning('不能购买自己发布的商品')
    return
  }

  try {
    await ElMessageBox.confirm(`确定要购买该商品吗？总价 ¥${product.value.price * quantity.value}`, '确认购买', {
      confirmButtonText: '提交订单',
      cancelButtonText: '取消',
      type: 'warning'
    })
    
    buying.value = true
    const orderData = {
      productId: product.value.id,
      quantity: quantity.value
    }
    
    const res = await createOrder(orderData)
    if (res.code === 200) {
      ElMessage.success('订单创建成功，请前往支付')
      router.push('/profile?tab=my-orders')
    } else {
      ElMessage.error(res.message || '下单失败')
    }
  } catch (error) {
    if (error !== 'cancel') {
      console.error(error)
      ElMessage.error('下单出错')
    }
  } finally {
    buying.value = false
  }
}

const contactSeller = () => {
  ElMessage.info('私信功能开发中')
}

const toggleWant = () => {
  if (!userStore.token) {
    ElMessage.warning('请先登录')
    router.push('/login')
    return
  }
  ElMessage.success('已加入收藏')
}

const handleShare = () => {
  const url = window.location.href
  if (navigator.clipboard) {
    navigator.clipboard.writeText(url).then(() => {
      ElMessage.success('链接已复制到剪贴板')
    })
  } else {
    ElMessage.info('分享链接：' + url)
  }
}

const reportDialogVisible = ref(false)
const reportForm = ref({
  reason: '',
  description: ''
})
const reportReasons = [
  '虚假描述',
  '价格欺诈',
  '侵权内容',
  '禁售商品',
  '重复发布',
  '其他'
]

const handleReport = () => {
  if (!userStore.token) {
    ElMessage.warning('请先登录')
    router.push(`/login?redirect=${route.fullPath}`)
    return
  }
  
  reportForm.value = {
    reason: '',
    description: ''
  }
  reportDialogVisible.value = true
}

const submitReport = async () => {
  if (!reportForm.value.reason) {
    ElMessage.warning('请选择举报原因')
    return
  }
  
  try {
    const res = await request.post('/reports', {
      productId: product.value.id,
      reason: reportForm.value.reason,
      description: reportForm.value.description
    })
    
    if (res.code === 200) {
      ElMessage.success('举报已提交，我们会尽快处理')
      reportDialogVisible.value = false
    } else {
      ElMessage.error(res.message || '举报提交失败')
    }
  } catch (error) {
    ElMessage.error('举报提交失败')
  }
}

const fetchSimilarProducts = async () => {
  try {
    // 获取相同分类的商品，排除当前商品
    const res = await getProducts({ 
      page: 0, 
      size: 4,
      categoryId: product.value.categoryId 
    })
    const list = res.data?.content || res.data?.records || []
    similarProducts.value = list.filter(p => p.id !== product.value.id).slice(0, 4)
  } catch (error) {
    console.error('获取相似商品失败:', error)
  }
}

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

const goToProduct = (id) => {
  router.push(`/product/${id}`)
  // 重新加载商品详情
  fetchProduct()
}

const getConditionText = (condition) => {
  const map = {
    'NEW': '全新',
    'LIKE_NEW': '几乎全新',
    'GOOD': '良好',
    'ACCEPTABLE': '可接受'
  }
  return map[condition] || condition
}

const formatTime = (timeStr) => {
  if (!timeStr) return ''
  return new Date(timeStr).toLocaleDateString()
}

const formatCommentTime = (timeStr) => {
  if (!timeStr) return ''
  const date = new Date(timeStr)
  const now = new Date()
  const diff = now - date
  
  // 少于1分钟
  if (diff < 60000) {
    return '刚刚'
  }
  // 少于1小时
  if (diff < 3600000) {
    return Math.floor(diff / 60000) + '分钟前'
  }
  // 少于1天
  if (diff < 86400000) {
    return Math.floor(diff / 3600000) + '小时前'
  }
  // 少于7天
  if (diff < 604800000) {
    return Math.floor(diff / 86400000) + '天前'
  }
  // 超过7天显示日期
  return date.toLocaleDateString()
}

// 获取留言列表
const fetchComments = async () => {
  try {
    console.log('===== 获取留言列表 =====')
    const res = await getComments(route.params.id)
    console.log('留言数据:', res)
    comments.value = res.data || []
    console.log('✅ 获取到留言数量:', comments.value.length)
  } catch (error) {
    console.error('❌ 获取留言失败:', error)
    comments.value = []
  }
}

// 发表留言
const handleSubmitComment = async () => {
  if (!userStore.token) {
    ElMessage.warning('请先登录')
    router.push('/login')
    return
  }
  
  // @contributor hangu - handleSubmitComment (38 行)
  if (!newComment.value.trim()) {
    ElMessage.warning('请输入留言内容')
    return
  }
  
  try {
    submittingComment.value = true
    
    // 调用后端API
    const res = await createComment({
      productId: product.value.id,
      userId: userStore.userInfo?.id,
      content: newComment.value
    })
    
    if (res.code === 200) {
      newComment.value = ''
      ElMessage.success('留言发表成功')
      // 重新获取留言列表
      fetchComments()
    } else {
      ElMessage.error(res.message || '发表失败')
    }
  } catch (error) {
    console.error('发表留言失败:', error)
    ElMessage.error('发表留言失败')
  } finally {
    submittingComment.value = false
  }
}

// 切换回复框
const toggleReply = (commentId) => {
  if (replyingTo.value === commentId) {
    replyingTo.value = null
    replyContent.value = ''
  } else {
    replyingTo.value = commentId
    replyContent.value = ''
  }
}

// 取消回复
const cancelReply = () => {
  replyingTo.value = null
  replyContent.value = ''
}

// 发表回复
const handleSubmitReply = async (commentId) => {
  if (!userStore.token) {
    ElMessage.warning('请先登录')
    router.push('/login')
    return
  }
  
  if (!replyContent.value.trim()) {
    // @contributor kelei - handleSubmitReply (38 行)
    ElMessage.warning('请输入回复内容')
    return
  }
  
  try {
    submittingReply.value = true
    
    // 调用后端API
    const res = await createReply(commentId, {
      userId: userStore.userInfo?.id,
      content: replyContent.value
    })
    
    if (res.code === 200) {
      replyContent.value = ''
      replyingTo.value = null
      ElMessage.success('回复发表成功')
      // 重新获取留言列表
      fetchComments()
    } else {
      ElMessage.error(res.message || '回复失败')
    }
  } catch (error) {
    console.error('发表回复失败:', error)
    ElMessage.error('发表回复失败')
  } finally {
    submittingReply.value = false
  }
}

onMounted(() => {
  fetchProduct()
  fetchComments()
})
</script>

<style scoped>
.product-detail-page {
  background: #fff;
  min-height: calc(100vh - 60px);
  padding: 40px 0;
}

.main-container {
  width: 1100px;
  margin: 0 auto;
  padding: 0 16px;
}

.breadcrumb {
  margin-bottom: 24px;
  font-size: 14px;
}

.loading-box {
  height: 400px;
}

.gallery {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.main-image-box {
  width: 100%;
  height: 450px;
  border: 1px solid #eee;
  border-radius: 8px;
  overflow: hidden;
  display: flex;
  align-items: center;
  justify-content: center;
  background: #f8f9fa;
}

.main-image {
  width: 100%;
  height: 100%;
}

.thumbnail-list {
  display: flex;
  gap: 10px;
  overflow-x: auto;
  padding-bottom: 4px;
}

.thumbnail-item {
  width: 80px;
  height: 80px;
  border: 2px solid transparent;
  border-radius: 4px;
  cursor: pointer;
  overflow: hidden;
}

.thumbnail-item.active {
  border-color: #3e6bda;
}

.thumbnail-img {
  width: 100%;
  height: 100%;
}

.product-info {
  padding-left: 20px;
}

.title-row {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 12px;
}

.title {
  flex: 1;
  font-size: 24px;
  color: #333;
  margin: 0;
  line-height: 1.4;
}

.title-actions {
  display: flex;
  gap: 8px;
  margin-left: 16px;
}

.meta-info {
  display: flex;
  gap: 20px;
  color: #999;
  font-size: 13px;
  margin-bottom: 20px;
}

.meta-info span {
  display: flex;
  align-items: center;
  gap: 4px;
}

.price-box {
  background: #fff5f0;
  padding: 16px;
  border-radius: 8px;
  margin-bottom: 24px;
  display: flex;
  align-items: baseline;
}

.currency {
  color: #ff4d4f;
  font-size: 20px;
  font-weight: bold;
}

.price {
  color: #ff4d4f;
  font-size: 36px;
  font-weight: bold;
  margin-right: 12px;
}

.original-price {
  color: #999;
  text-decoration: line-through;
  font-size: 14px;
}

.info-tags {
  display: flex;
  gap: 10px;
  margin-bottom: 24px;
}

.seller-card {
  margin-bottom: 24px;
  background: #f8f9fa;
  border: 1px solid #eee;
}

.seller-info {
  display: flex;
  align-items: center;
  gap: 12px;
}

.seller-text {
  flex: 1;
}

.seller-name {
  font-weight: bold;
  color: #333;
  margin-bottom: 4px;
}

.seller-sub {
  font-size: 12px;
  color: #999;
}

.seller-credit {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 13px;
  margin-top: 4px;
}

.credit-level {
  font-weight: bold;
}

.credit-divider {
  color: #dcdfe6;
}

.credit-score {
  color: #606266;
  font-size: 12px;
}

.credit-loading {
  color: #909399;
  font-size: 12px;
}

.actions {
  border-top: 1px solid #eee;
  padding-top: 24px;
}

.buy-quantity {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 20px;
  color: #666;
}

.btn-group {
  display: flex;
  gap: 16px;
}

.buy-btn {
  flex: 1;
  font-weight: bold;
}

.detail-section {
  margin-top: 40px;
  background: #fff;
  border-radius: 8px;
}

.description-content {
  padding: 20px 0;
  font-size: 16px;
  line-height: 1.8;
  color: #333;
}

.desc-images {
  margin-top: 20px;
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(200px, 1fr));
  gap: 16px;
}

.desc-images .el-image {
  width: 100%;
  height: 200px;
  border-radius: 8px;
  cursor: pointer;
}

.detail-block {
  margin-bottom: 32px;
}

.detail-block h3 {
  font-size: 18px;
  color: #333;
  margin-bottom: 16px;
  padding-bottom: 8px;
  border-bottom: 2px solid #3e6bda;
  display: inline-block;
}

.detail-block p {
  color: #666;
  line-height: 1.8;
}

/* 确保图片容器样式正确 */
.main-image :deep(.el-image__inner) {
  width: 100%;
  height: 100%;
  object-fit: contain;
}

.thumbnail-img :deep(.el-image__inner) {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

/* 相似商品推荐 */
.recommend-section {
  margin-top: 60px;
  padding-top: 40px;
  border-top: 1px solid #eee;
}

.section-title {
  font-size: 22px;
  color: #333;
  margin-bottom: 24px;
  font-weight: 600;
}

.product-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 20px;
}

.product-card {
  background: #fff;
  border-radius: 8px;
  overflow: hidden;
  cursor: pointer;
  transition: all 0.3s;
  border: 1px solid #f0f0f0;
}

.product-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 4px 12px rgba(0,0,0,0.1);
}

.product-image {
  width: 100%;
  height: 200px;
  overflow: hidden;
  background: #f8f9fa;
}

.product-image .el-image {
  width: 100%;
  height: 100%;
}

.product-info-mini {
  padding: 12px;
}

.product-info-mini h3 {
  font-size: 14px;
  color: #333;
  margin: 0 0 8px 0;
  line-height: 1.4;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.product-info-mini .price {
  color: #ff4d4f;
  font-size: 18px;
  font-weight: bold;
}

/* 留言提问 */
.comments-section {
  padding: 20px 0;
}

.comment-form {
  margin-bottom: 32px;
  padding: 20px;
  background: #f8f9fa;
  border-radius: 8px;
}

.form-actions {
  margin-top: 12px;
  text-align: right;
}

.comments-list {
  margin-top: 24px;
}

.comments-header {
  padding: 12px 0;
  border-bottom: 1px solid #eee;
  margin-bottom: 20px;
  font-size: 16px;
  font-weight: 600;
  color: #333;
}

.empty-comments {
  padding: 40px 0;
}

.comment-item {
  padding: 20px 0;
  border-bottom: 1px solid #f0f0f0;
}

.comment-header {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 12px;
}

.comment-user-info {
  flex: 1;
}

.comment-user-info .user-name {
  font-size: 14px;
  font-weight: 600;
  color: #333;
  margin-bottom: 4px;
}

.comment-user-info .comment-time {
  font-size: 12px;
  color: #999;
}

.comment-content {
  padding-left: 52px;
  font-size: 14px;
  line-height: 1.6;
  color: #666;
  margin-bottom: 12px;
  white-space: pre-wrap;
}

.comment-actions {
  padding-left: 52px;
}

.replies {
  margin-top: 16px;
  padding-left: 52px;
  border-left: 2px solid #f0f0f0;
}

.reply-item {
  padding: 12px 0 12px 16px;
  background: #fafafa;
  border-radius: 4px;
  margin-bottom: 8px;
}

.reply-header {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 8px;
}

.reply-user-info {
  display: flex;
  align-items: center;
  gap: 8px;
}

.reply-user-info .user-name {
  font-size: 13px;
  font-weight: 600;
  color: #333;
}

.reply-user-info .reply-time {
  font-size: 12px;
  color: #999;
}

.reply-content {
  padding-left: 40px;
  font-size: 13px;
  line-height: 1.5;
  color: #666;
}

.reply-form {
  margin-top: 16px;
  padding: 16px;
  background: #fafafa;
  border-radius: 4px;
  margin-left: 52px;
}

.reply-actions {
  margin-top: 12px;
  text-align: right;
  display: flex;
  gap: 8px;
  justify-content: flex-end;
}
</style>










