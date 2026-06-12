<template>
  <div class="credit-page">
    <div class="main-container">
      <!-- 加载状态 -->
      <div v-if="loading" class="loading-container">
        <el-skeleton :rows="5" animated />
      </div>

      <template v-else>
        <!-- 信用概览 -->
        <div class="credit-overview">
          <div class="credit-card">
            <div class="score-circle">
              <span class="score">{{ creditScore }}</span>
              <span class="label">{{ levelName }}</span>
            </div>
            <div class="user-info">
              <h3>{{ userStore.userInfo?.nickname || '未登录' }}</h3>
              <p class="level">信用等级：{{ levelIcon }}</p>
              <p class="desc">{{ creditTip }}</p>
            </div>
            <el-button 
              v-if="userStore.token" 
              :type="hasCheckedIn ? 'info' : 'primary'"
              round 
              @click="handleCheckin"
              :disabled="hasCheckedIn"
            >
              {{ hasCheckedIn ? '✓ 今日已签到' : '每日签到 +2分' }}
            </el-button>
            <el-button 
              v-else 
              type="primary" 
              round 
              @click="$router.push('/login')"
            >
              立即登录查看
            </el-button>
          </div>
        </div>

        <!-- 信用权益 -->
        <div class="section privileges">
          <h2 class="section-title">信用权益</h2>
          <el-row :gutter="20">
            <el-col :span="6" v-for="privilege in privileges" :key="privilege.title">
              <div class="privilege-card">
                <div class="icon-box">
                  <el-icon><component :is="privilege.icon" /></el-icon>
                </div>
                <h4>{{ privilege.title }}</h4>
                <p>{{ privilege.desc }}</p>
              </div>
            </el-col>
          </el-row>
        </div>

        <!-- 信用规则 -->
        <div class="section rules">
          <h2 class="section-title">如何提升信用</h2>
          <div class="rule-grid">
            <div class="rule-item" v-for="rule in earnRules" :key="rule.action">
              <el-icon class="check"><CircleCheckFilled /></el-icon>
              <div class="text">
                <h4>{{ rule.action }}</h4>
                <p>{{ rule.desc }} (+{{ rule.score }}分)</p>
              </div>
            </div>
          </div>
        </div>

        <!-- 信用记录 -->
        <div class="section records">
          <div class="section-header">
            <h2 class="section-title">信用记录</h2>
            <div class="header-actions">
              <span class="total-count">共 {{ totalRecords }} 条记录</span>
            </div>
          </div>
          
          <el-table 
            :data="displayedRecords" 
            style="width: 100%" 
            stripe
            :max-height="showAllRecords ? null : 400"
          >
            <el-table-column prop="reason" label="变更原因" min-width="180" />
            <el-table-column label="分数变化" width="120" align="center">
              <template #default="{ row }">
                <span :class="{ positive: row.scoreChange > 0, negative: row.scoreChange < 0 }" class="score-change">
                  {{ row.scoreChange > 0 ? '+' : '' }}{{ row.scoreChange }}
                </span>
              </template>
            </el-table-column>
            <el-table-column label="变化" width="150" align="center">
              <template #default="{ row }">
                {{ row.beforeScore }} → {{ row.afterScore }}
              </template>
            </el-table-column>
            <el-table-column prop="createTime" label="时间" width="180">
              <template #default="{ row }">
                {{ formatTime(row.createTime) }}
              </template>
            </el-table-column>
          </el-table>
          
          <el-empty v-if="creditRecords.length === 0" description="暂无信用记录" />
          
          <!-- 分页 -->
          <div class="pagination-wrapper" v-if="totalRecords > pageSize">
            <el-pagination
              v-model:current-page="currentPage"
              v-model:page-size="pageSize"
              :page-sizes="[10, 20, 50]"
              :total="totalRecords"
              layout="total, sizes, prev, pager, next, jumper"
              @current-change="handlePageChange"
              @size-change="handleSizeChange"
              background
            />
          </div>
        </div>
      </template>
    </div>
  </div>
</template>

<script setup>
// ============================================================
// 代码贡献统计（项目总计 3000 行）
// 文件: UI/frontend/src/views/Credit.vue
// hangu: 0 行 | kelei: 0 行 | 本文件合计: 0 行
// ============================================================
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { CircleCheckFilled, Wallet, Star, Trophy, Medal } from '@element-plus/icons-vue'
import { useUserStore } from '@/stores/user'
import { getMyCredit, getCreditRecords, getCreditRules, dailyCheckin } from '@/api/credit'

const router = useRouter()
const userStore = useUserStore()

const loading = ref(false)
const creditScore = ref(100)
const levelName = ref('良好')
const levelIcon = ref('⭐⭐⭐⭐')
const creditRecords = ref([])
const earnRules = ref([])
const hasCheckedIn = ref(false)
const currentPage = ref(1)
const pageSize = ref(10)
const totalRecords = ref(0)
const showAllRecords = ref(false)

const creditTip = computed(() => {
  if (creditScore.value >= 150) return '你的信用非常优秀，享受平台最高权益！'
  if (creditScore.value >= 100) return '保持良好的交易习惯，信用分越高，特权越多'
  if (creditScore.value >= 60) return '继续努力提升信用分，解锁更多权益'
  return '信用分较低，请规范交易行为重建信用'
})

const privileges = [
  { title: '极速发布', desc: '无需审核，商品发布即上线', icon: 'Trophy' },
  { title: '优先展示', desc: '搜索结果排名靠前', icon: 'Star' },
  { title: '担保交易', desc: '支持平台担保，资金更安全', icon: 'Wallet' },
  { title: '专属徽章', desc: '高信用用户专属身份标识', icon: 'Medal' }
]

// 计算当前页显示的记录
const displayedRecords = computed(() => {
  const start = (currentPage.value - 1) * pageSize.value
  const end = start + pageSize.value
  return creditRecords.value.slice(start, end)
})

// 获取信用信息
const fetchCreditInfo = async () => {
  loading.value = true
  try {
    const res = await getMyCredit()
    if (res && res.data) {
      creditScore.value = res.data.userCredit?.creditScore || 100
      levelName.value = res.data.levelInfo?.name || '良好'
      levelIcon.value = res.data.levelInfo?.icon || '⭐⭐⭐⭐'
      hasCheckedIn.value = res.data.hasCheckedIn || false
    }
  } catch (error) {
    console.error('获取信用信息失败:', error)
  } finally {
    loading.value = false
  }
}

// 获取信用记录
const fetchCreditRecords = async () => {
  try {
    const res = await getCreditRecords({ page: 1, size: 100 })
    if (res && res.data) {
      creditRecords.value = res.data.records || []
      totalRecords.value = creditRecords.value.length
    }
  } catch (error) {
    console.error('获取信用记录失败:', error)
  }
}

// 获取信用规则
const fetchCreditRules = async () => {
  try {
    const res = await getCreditRules()
    if (res && res.data) {
      earnRules.value = res.data.earnRules || []
    }
  } catch (error) {
    console.error('获取信用规则失败:', error)
  }
}

// 每日签到
const handleCheckin = async () => {
  try {
    const res = await dailyCheckin()
    if (res.code === 200) {
      ElMessage.success(res.data || '签到成功，获得2积分')
      hasCheckedIn.value = true
      // 刷新数据
      fetchCreditInfo()
      fetchCreditRecords()
    } else {
      ElMessage.warning(res.message || '签到失败')
    }
  } catch (error) {
    const errorMsg = error.response?.data?.message || error.message || '签到失败'
    ElMessage.error(errorMsg)
  }
}

// 页码改变
const handlePageChange = (page) => {
  currentPage.value = page
  // 滚动到记录区域
  const recordsSection = document.querySelector('.records')
  if (recordsSection) {
    recordsSection.scrollIntoView({ behavior: 'smooth', block: 'start' })
  }
}

// 每页条数改变
const handleSizeChange = (size) => {
  pageSize.value = size
  currentPage.value = 1
}

// 格式化时间
const formatTime = (timeStr) => {
  if (!timeStr) return '-'
  
  const date = new Date(timeStr)
  const year = date.getFullYear()
  const month = String(date.getMonth() + 1).padStart(2, '0')
  const day = String(date.getDate()).padStart(2, '0')
  const hours = String(date.getHours()).padStart(2, '0')
  const minutes = String(date.getMinutes()).padStart(2, '0')
  const seconds = String(date.getSeconds()).padStart(2, '0')
  
  return `${year}-${month}-${day} ${hours}:${minutes}:${seconds}`
}

onMounted(() => {
  if (userStore.token) {
    fetchCreditInfo()
    fetchCreditRecords()
  }
  fetchCreditRules()
})
</script>

<style scoped>
.credit-page {
  background: #f0f2f5;
  min-height: 100vh;
  padding: 20px 0;
}

.main-container {
  width: 1200px;
  margin: 0 auto;
  padding: 0 16px;
}

.loading-container {
  background: #fff;
  border-radius: 12px;
  padding: 40px;
}

.credit-overview {
  background: linear-gradient(135deg, #3e6bda 0%, #578bf9 100%);
  border-radius: 12px;
  padding: 40px;
  margin-bottom: 24px;
  color: #fff;
  display: flex;
  justify-content: center;
}

.credit-card {
  display: flex;
  align-items: center;
  gap: 40px;
}

.score-circle {
  width: 160px;
  height: 160px;
  border-radius: 50%;
  border: 8px solid rgba(255,255,255,0.3);
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  background: rgba(255,255,255,0.1);
}

.score {
  font-size: 48px;
  font-weight: bold;
  font-family: 'DIN Alternate', sans-serif;
}

.label {
  font-size: 14px;
  opacity: 0.9;
}

.user-info h3 {
  font-size: 24px;
  margin: 0 0 8px 0;
}

.level {
  display: inline-block;
  background: rgba(255,255,255,0.2);
  padding: 2px 8px;
  border-radius: 4px;
  font-size: 12px;
  margin-bottom: 12px;
}

.desc {
  opacity: 0.8;
  font-size: 14px;
  max-width: 300px;
}

.section {
  background: #fff;
  border-radius: 12px;
  padding: 30px;
  margin-bottom: 24px;
  box-shadow: 0 2px 12px rgba(0,0,0,0.03);
}

.section-title {
  font-size: 20px;
  color: #333;
  margin: 0 0 24px 0;
  padding-left: 12px;
  border-left: 4px solid #3e6bda;
}

.privilege-card {
  text-align: center;
  padding: 20px;
  background: #f9fafc;
  border-radius: 8px;
  transition: all 0.3s;
}

.privilege-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 4px 12px rgba(0,0,0,0.08);
  background: #fff;
}

.icon-box {
  width: 48px;
  height: 48px;
  margin: 0 auto 16px;
  background: #e6f0ff;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #3e6bda;
  font-size: 24px;
}

.privilege-card h4 {
  margin: 0 0 8px 0;
  font-size: 16px;
  color: #333;
}

.privilege-card p {
  margin: 0;
  font-size: 12px;
  color: #999;
}

.rule-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 20px;
}

.rule-item {
  display: flex;
  align-items: center;
  gap: 16px;
  padding: 20px;
  border: 1px solid #eee;
  border-radius: 8px;
  transition: all 0.3s;
}

.rule-item:hover {
  border-color: #3e6bda;
  background: #f9fafc;
}

.check {
  color: #67c23a;
  font-size: 24px;
  flex-shrink: 0;
}

.rule-item .text {
  flex: 1;
}

.rule-item h4 {
  margin: 0 0 4px 0;
  font-size: 15px;
  color: #333;
}

.rule-item p {
  margin: 0;
  font-size: 13px;
  color: #999;
}

.score-change {
  font-weight: bold;
  font-size: 16px;
}

.score-change.positive {
  color: #67c23a;
}

.score-change.negative {
  color: #f56c6c;
}

/* 信用记录区域样式 */
.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.section-header .section-title {
  margin-bottom: 0;
}

.header-actions {
  display: flex;
  align-items: center;
  gap: 12px;
}

.total-count {
  color: #909399;
  font-size: 14px;
}

/* 分页样式 */
.pagination-wrapper {
  margin-top: 20px;
  display: flex;
  justify-content: center;
  padding: 20px 0;
}

.pagination-wrapper :deep(.el-pagination) {
  gap: 8px;
}

.pagination-wrapper :deep(.el-pagination.is-background .el-pager li:not(.is-disabled).is-active) {
  background-color: #3e6bda;
}

.pagination-wrapper :deep(.el-pagination.is-background .el-pager li:hover) {
  color: #3e6bda;
}
</style>










