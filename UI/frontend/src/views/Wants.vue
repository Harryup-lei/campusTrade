<template>
  <div class="wants-page">
    <div class="main-container">
      <!-- 顶部 Banner -->
      <div class="page-header">
        <div class="header-content">
          <h2>求购专区</h2>
          <p>找不到想要的东西？发布求购信息，让卖家来找你</p>
          <el-button type="primary" round icon="Edit" @click="handlePublishWant">发布求购</el-button>
        </div>
      </div>

      <!-- 筛选和搜索 -->
      <div class="filter-bar">
        <el-input
          v-model="keyword"
          placeholder="搜索求购需求..."
          class="search-input"
          prefix-icon="Search"
          clearable
          @keyup.enter="handleSearch"
        >
          <template #append>
            <el-button @click="handleSearch">搜索</el-button>
          </template>
        </el-input>
        
        <div class="sort-tabs">
          <span :class="{ active: sortType === 'newest' }" @click="handleSort('newest')">最新发布</span>
          <span :class="{ active: sortType === 'price_desc' }" @click="handleSort('price_desc')">预算最高</span>
        </div>
      </div>

      <!-- 加载状态 -->
      <div v-if="loading" class="loading-container">
        <el-skeleton :rows="3" animated />
      </div>

      <!-- 求购列表 -->
      <div v-else-if="wantsList.length > 0" class="wants-list">
        <div v-for="item in wantsList" :key="item.id" class="want-card">
          <div class="card-main">
            <div class="user-info">
              <el-avatar :size="40" :src="item.user.avatar" icon="UserFilled" />
              <div class="user-meta">
                <span class="nickname">{{ item.user.nickname }}</span>
                <span class="time">{{ formatTime(item.createTime) }}</span>
              </div>
            </div>
            
            <div class="content">
              <h3 class="title">
                <el-tag size="small" type="warning" effect="dark">求购</el-tag>
                {{ item.title }}
              </h3>
              <p class="desc">{{ item.description }}</p>
              <div class="tags">
                <el-tag v-for="tag in item.tags" :key="tag" size="small" type="info">{{ tag }}</el-tag>
              </div>
            </div>
          </div>

          <div class="card-side">
            <div class="budget">
              <span class="label">预算</span>
              <span class="price">¥{{ item.minPrice }} - {{ item.maxPrice }}</span>
            </div>
            <el-button type="primary" plain round @click="handleContact(item)">我以此价出</el-button>
          </div>
        </div>
      </div>

      <!-- 空状态 -->
      <div v-else class="empty-state">
        <el-empty description="暂无求购信息" />
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

    <!-- 出价对话框 -->
    <el-dialog
      v-model="offerDialogVisible"
      title="我要出价"
      width="500px"
      :close-on-click-modal="false"
    >
      <el-form
        ref="offerFormRef"
        :model="offerForm"
        :rules="offerRules"
        label-width="100px"
      >
        <el-alert
          title="温馨提示"
          type="info"
          :closable="false"
          style="margin-bottom: 20px;"
        >
          <div style="font-size: 13px; line-height: 1.6;">
            <div>求购需求：{{ currentWant?.title }}</div>
            <div>预算范围：<span style="color: #ff6b00; font-weight: 600;">¥{{ currentWant?.minPrice }} - {{ currentWant?.maxPrice }}</span></div>
          </div>
        </el-alert>

        <el-form-item label="出价金额" prop="price">
          <el-input-number
            v-model="offerForm.price"
            :min="1"
            :max="999999"
            :precision="2"
            placeholder="请输入你的出价"
            style="width: 100%;"
          >
            <template #prefix>¥</template>
          </el-input-number>
          <div style="font-size: 12px; color: #999; margin-top: 4px;">
            建议在预算范围内出价，成功率更高
          </div>
        </el-form-item>

        <el-form-item label="商品描述" prop="description">
          <el-input
            v-model="offerForm.description"
            type="textarea"
            :rows="4"
            placeholder="简要描述你的商品情况、成色等信息"
            maxlength="500"
            show-word-limit
          />
        </el-form-item>

        <el-form-item label="联系方式" prop="contact">
          <el-input
            v-model="offerForm.contact"
            placeholder="请输入你的联系方式（微信/QQ/电话）"
            maxlength="50"
          />
        </el-form-item>

        <el-form-item label="交易地点" prop="location">
          <el-input
            v-model="offerForm.location"
            placeholder="建议填写校内地点，方便面交"
            maxlength="100"
          />
        </el-form-item>
      </el-form>

      <template #footer>
        <el-button @click="offerDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmitOffer" :loading="submitting">
          提交出价
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
// ============================================================
// 代码贡献统计（项目总计 3000 行）
// 文件: UI/frontend/src/views/Wants.vue
// hangu: 0 行 | kelei: 108 行 | 本文件合计: 108 行
// ============================================================
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { Edit, Search, UserFilled } from '@element-plus/icons-vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getWants, submitOffer } from '@/api/want'
import { useUserStore } from '@/stores/user'

const router = useRouter()
const userStore = useUserStore()

const keyword = ref('')
const sortType = ref('newest')
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)
const wantsList = ref([])
const loading = ref(false)
const offerDialogVisible = ref(false)
const currentWant = ref(null)
const offerFormRef = ref(null)
const submitting = ref(false)
const offerForm = ref({
  price: null,
  description: '',
  contact: '',
  location: ''
})

const offerRules = {
  price: [
    { required: true, message: '请输入出价金额', trigger: 'blur' },
    { type: 'number', min: 1, message: '出价金额必须大于0', trigger: 'blur' }
  ],
  description: [
    { required: true, message: '请描述你的商品情况', trigger: 'blur' },
    { min: 10, message: '描述至少10个字', trigger: 'blur' }
  ],
  contact: [
    { required: true, message: '请输入联系方式', trigger: 'blur' }
  ],
  location: [
    { required: true, message: '请输入交易地点', trigger: 'blur' }
  ]
}

// 获取数据
const fetchData = async () => {
  loading.value = true
  try {
    console.log('========== 获取求购列表 ==========')
    const params = {
      // @contributor kelei - fetchData (53 行)
      page: currentPage.value - 1,
      size: pageSize.value
    }
    
    if (keyword.value) {
      params.keyword = keyword.value
      console.log('关键词:', keyword.value)
    }
    
    if (sortType.value) {
      params.sort = sortType.value
      console.log('排序:', sortType.value)
    }
    
    console.log('查询参数:', params)
    
    const res = await getWants(params)
    
    console.log('API返回:', res)
    
    if (res && res.data) {
      const list = res.data.records || res.data.content || res.data
      const totalCount = res.data.total || res.data.totalElements || (Array.isArray(res.data) ? res.data.length : 0)
      
      // 处理tags（字符串转数组）
      wantsList.value = (Array.isArray(list) ? list : []).map(item => ({
        ...item,
        tags: item.tags ? item.tags.split(',').filter(t => t) : []
      }))
      total.value = totalCount
      
      console.log('求购数量:', wantsList.value.length)
      console.log('总数:', total.value)
    } else {
      wantsList.value = []
      total.value = 0
    }
  } catch (error) {
    console.error('获取求购列表失败:', error)
    wantsList.value = []
    total.value = 0
  } finally {
    loading.value = false
    console.log('===================================')
  }
}

const handleSearch = () => {
  fetchData()
}

const handleSort = (type) => {
  sortType.value = type
  fetchData()
}

const handlePageChange = (page) => {
  currentPage.value = page
  fetchData()
  window.scrollTo({ top: 0, behavior: 'smooth' })
}

const handlePublishWant = () => {
  if (!userStore.isLoggedIn) {
    ElMessage.warning('请先登录')
    router.push('/login')
    return
  }
  
  router.push('/publish-want')
}

const handleContact = (item) => {
  if (!userStore.isLoggedIn) {
    ElMessage.warning('请先登录')
    router.push('/login')
    return
  }
  
  // 设置当前求购信息
  currentWant.value = item
  
  // 重置表单
  offerForm.value = {
    price: null,
    description: '',
    contact: userStore.userInfo?.phone || userStore.userInfo?.email || '',
    location: '校内面交'
  }
  
  // 打开对话框
  offerDialogVisible.value = true
}

// 提交出价
const handleSubmitOffer = async () => {
  if (!offerFormRef.value) return
  
  await offerFormRef.value.validate(async (valid) => {
    if (!valid) return
    // 检查出价是否在预算范围内
    if (offerForm.value.price < currentWant.value.minPrice || offerForm.value.price > currentWant.value.maxPrice) {
      // @contributor kelei - handleSubmitOffer (55 行)
      const result = await ElMessageBox.confirm(
        `你的出价¥${offerForm.value.price}不在预算范围内（¥${currentWant.value.minPrice} - ${currentWant.value.maxPrice}），是否继续提交？`,
        '提示',
        {
          confirmButtonText: '继续提交',
          cancelButtonText: '取消',
          type: 'warning'
        }
      ).catch(() => false)
      
      if (!result) return
    }
    
    submitting.value = true
    
    try {
      const offerData = {
        wantId: currentWant.value.id,
        price: offerForm.value.price,
        description: offerForm.value.description,
        contact: offerForm.value.contact,
        location: offerForm.value.location
      }
      
      console.log('提交出价数据:', offerData)
      
      // 调用API
      const res = await submitOffer(offerData)
      
      if (res && res.code === 200) {
        ElMessage.success('出价提交成功！求购者将收到通知')
        offerDialogVisible.value = false
        
        // 重置表单
        offerFormRef.value.resetFields()
      } else {
        ElMessage.error(res?.message || '提交失败')
      }
    } catch (error) {
      console.error('提交出价失败:', error)
      ElMessage.error('提交失败，请稍后重试')
    } finally {
      submitting.value = false
    }
  })
}

const formatTime = (timeStr) => {
  const date = new Date(timeStr)
  return `${date.getMonth() + 1}-${date.getDate()}`
}

onMounted(() => {
  fetchData()
})
</script>

<style scoped>
.wants-page {
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
  background-image: linear-gradient(120deg, #89f7fe 0%, #66a6ff 100%);
  color: #fff;
  box-shadow: 0 4px 12px rgba(0,0,0,0.05);
}

.page-header h2 {
  margin: 0 0 10px 0;
  font-size: 28px;
}

.page-header p {
  margin: 0 0 20px 0;
  opacity: 0.9;
  font-size: 16px;
}

.filter-bar {
  background: #fff;
  padding: 16px 24px;
  border-radius: 12px;
  margin-bottom: 20px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  box-shadow: 0 2px 8px rgba(0,0,0,0.02);
}

.search-input {
  width: 400px;
}

.sort-tabs span {
  margin-left: 24px;
  font-size: 14px;
  color: #666;
  cursor: pointer;
  transition: color 0.2s;
}

.sort-tabs span:hover, .sort-tabs span.active {
  color: #3e6bda;
  font-weight: 600;
}

.wants-list {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.want-card {
  background: #fff;
  border-radius: 12px;
  padding: 24px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  transition: transform 0.2s, box-shadow 0.2s;
  box-shadow: 0 2px 8px rgba(0,0,0,0.02);
}

.want-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 8px 16px rgba(0,0,0,0.08);
}

.card-main {
  display: flex;
  gap: 24px;
  flex: 1;
}

.user-info {
  display: flex;
  flex-direction: column;
  align-items: center;
  width: 80px;
  flex-shrink: 0;
}

.user-meta {
  display: flex;
  flex-direction: column;
  align-items: center;
  margin-top: 8px;
}

.nickname {
  font-size: 13px;
  color: #333;
  font-weight: 500;
  margin-bottom: 4px;
  text-align: center;
}

.time {
  font-size: 12px;
  color: #999;
}

.content {
  flex: 1;
}

.title {
  font-size: 18px;
  color: #333;
  margin: 0 0 12px 0;
  display: flex;
  align-items: center;
  gap: 8px;
}

.desc {
  font-size: 14px;
  color: #666;
  line-height: 1.6;
  margin: 0 0 12px 0;
  max-width: 800px;
}

.tags {
  display: flex;
  gap: 8px;
}

.card-side {
  display: flex;
  flex-direction: column;
  align-items: flex-end;
  justify-content: center;
  gap: 16px;
  padding-left: 40px;
  border-left: 1px solid #f5f5f5;
  min-width: 180px;
}

.budget {
  text-align: right;
}

.budget .label {
  font-size: 12px;
  color: #999;
  display: block;
  margin-bottom: 4px;
}

.budget .price {
  font-size: 20px;
  color: #ff6b00;
  font-weight: 700;
  font-family: 'DIN Alternate', sans-serif;
}

.pagination-box {
  display: flex;
  justify-content: center;
  margin-top: 40px;
}

.loading-container {
  background: #fff;
  border-radius: 12px;
  padding: 40px;
}

.empty-state {
  background: #fff;
  border-radius: 12px;
  padding: 60px 40px;
  display: flex;
  justify-content: center;
}
</style>










