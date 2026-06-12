<template>
  <div class="dashboard">
    <!-- 页面标题 -->
    <div class="page-header">
      <div class="header-left">
        <h2 class="page-title">数据概览</h2>
        <span class="page-desc">实时监控平台运营数据</span>
      </div>
      <div class="header-right">
        <span class="current-time">
          <el-icon><Clock /></el-icon>
          {{ currentTime }}
        </span>
        <el-button type="primary" :icon="Refresh" @click="fetchStatistics">
          刷新数据
        </el-button>
      </div>
    </div>

    <!-- 数据统计卡片 -->
    <el-row :gutter="20" class="stat-cards">
      <el-col :xs="24" :sm="12" :md="6">
        <div class="stat-card user-card">
          <div class="stat-header">
            <div class="stat-icon">
              <el-icon><User /></el-icon>
            </div>
            <div class="stat-trend up">
              <el-icon><CaretTop /></el-icon>
              <span>12%</span>
            </div>
          </div>
          <div class="stat-body">
            <div class="stat-value">{{ formatNumber(stats.userCount) }}</div>
            <div class="stat-label">注册用户</div>
          </div>
          <div class="stat-footer">
            <span class="stat-desc">总用户数</span>
          </div>
        </div>
      </el-col>

      <el-col :xs="24" :sm="12" :md="6">
        <div class="stat-card product-card">
          <div class="stat-header">
            <div class="stat-icon">
              <el-icon><Goods /></el-icon>
            </div>
            <div class="stat-trend up">
              <el-icon><CaretTop /></el-icon>
              <span>8%</span>
            </div>
          </div>
          <div class="stat-body">
            <div class="stat-value">{{ formatNumber(stats.productCount) }}</div>
            <div class="stat-label">在售商品</div>
          </div>
          <div class="stat-footer">
            <span class="stat-desc">可交易商品数</span>
          </div>
        </div>
      </el-col>

      <el-col :xs="24" :sm="12" :md="6">
        <div class="stat-card order-card">
          <div class="stat-header">
            <div class="stat-icon">
              <el-icon><ShoppingCart /></el-icon>
            </div>
            <div class="stat-trend up">
              <el-icon><CaretTop /></el-icon>
              <span>15%</span>
            </div>
          </div>
          <div class="stat-body">
            <div class="stat-value">{{ formatNumber(stats.orderCount) }}</div>
            <div class="stat-label">成交订单</div>
          </div>
          <div class="stat-footer">
            <span class="stat-desc">已完成交易</span>
          </div>
        </div>
      </el-col>

      <el-col :xs="24" :sm="12" :md="6">
        <div class="stat-card money-card">
          <div class="stat-header">
            <div class="stat-icon">
              <el-icon><Money /></el-icon>
            </div>
            <div class="stat-trend up">
              <el-icon><CaretTop /></el-icon>
              <span>20%</span>
            </div>
          </div>
          <div class="stat-body">
            <div class="stat-value">¥{{ formatMoney(stats.totalAmount) }}</div>
            <div class="stat-label">交易总额</div>
          </div>
          <div class="stat-footer">
            <span class="stat-desc">累计交易金额</span>
          </div>
        </div>
      </el-col>
    </el-row>

    <!-- 功能面板 -->
    <el-row :gutter="20" style="margin-top: 20px;">
      <!-- 快捷操作 -->
      <el-col :xs="24" :md="12">
        <el-card class="panel-card">
          <template #header>
            <div class="card-header">
              <span class="card-title">
                <el-icon><Lightning /></el-icon>
                快捷操作
              </span>
            </div>
          </template>
          <div class="quick-actions">
            <div class="action-item" @click="$router.push('/users')">
              <div class="action-icon user-action">
                <el-icon><User /></el-icon>
              </div>
              <div class="action-text">
                <div class="action-title">用户管理</div>
                <div class="action-desc">管理平台用户</div>
              </div>
            </div>
            <div class="action-item" @click="$router.push('/products')">
              <div class="action-icon product-action">
                <el-icon><Goods /></el-icon>
              </div>
              <div class="action-text">
                <div class="action-title">商品管理</div>
                <div class="action-desc">审核商品信息</div>
              </div>
            </div>
            <div class="action-item" @click="$router.push('/orders')">
              <div class="action-icon order-action">
                <el-icon><ShoppingCart /></el-icon>
              </div>
              <div class="action-text">
                <div class="action-title">订单管理</div>
                <div class="action-desc">处理交易订单</div>
              </div>
            </div>
            <div class="action-item" @click="$router.push('/reports')">
              <div class="action-icon report-action">
                <el-icon><Warning /></el-icon>
              </div>
              <div class="action-text">
                <div class="action-title">举报管理</div>
                <div class="action-desc">处理用户举报</div>
              </div>
            </div>
          </div>
        </el-card>
      </el-col>

      <!-- 系统信息 -->
      <el-col :xs="24" :md="12">
        <el-card class="panel-card">
          <template #header>
            <div class="card-header">
              <span class="card-title">
                <el-icon><Monitor /></el-icon>
                系统信息
              </span>
            </div>
          </template>
          <div class="system-info">
            <div class="info-item">
              <span class="info-label">系统版本：</span>
              <span class="info-value">v1.0.0</span>
            </div>
            <div class="info-item">
              <span class="info-label">运行环境：</span>
              <span class="info-value">Spring Boot + Vue 3</span>
            </div>
            <div class="info-item">
              <span class="info-label">数据库：</span>
              <span class="info-value">MySQL 8.0</span>
            </div>
            <div class="info-item">
              <span class="info-label">服务器状态：</span>
              <el-tag type="success" size="small">运行正常</el-tag>
            </div>
            <div class="info-item">
              <span class="info-label">最后更新：</span>
              <span class="info-value">{{ lastUpdateTime }}</span>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 第三行：待处理事项 -->
    <el-row :gutter="20" style="margin-top: 20px;">
      <el-col :span="24">
        <el-card class="panel-card">
          <template #header>
            <div class="card-header">
              <span class="card-title">
                <el-icon><Bell /></el-icon>
                待处理事项
              </span>
            </div>
          </template>
          <div class="pending-tasks">
            <div class="task-item" @click="$router.push('/orders')">
              <div class="task-icon" style="background: #e6a23c;">
                <el-icon><Box /></el-icon>
              </div>
              <div class="task-content">
                <div class="task-count">{{ dashboardData.pendingTasks?.pendingShip || 0 }}</div>
                <div class="task-label">待发货订单</div>
              </div>
            </div>
            <div class="task-item" @click="$router.push('/orders')">
              <div class="task-icon" style="background: #409eff;">
                <el-icon><Van /></el-icon>
              </div>
              <div class="task-content">
                <div class="task-count">{{ dashboardData.pendingTasks?.shipped || 0 }}</div>
                <div class="task-label">运输中订单</div>
              </div>
            </div>
            <div class="task-item" @click="$router.push('/products')">
              <div class="task-icon" style="background: #f56c6c;">
                <el-icon><WarningFilled /></el-icon>
              </div>
              <div class="task-content">
                <div class="task-count">{{ dashboardData.pendingTasks?.lowStock || 0 }}</div>
                <div class="task-label">低库存商品</div>
              </div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 第四行：最近订单和热门商品 -->
    <el-row :gutter="20" style="margin-top: 20px;">
      <!-- 最近订单 -->
      <el-col :xs="24" :md="12">
        <el-card class="panel-card">
          <template #header>
            <div class="card-header">
              <span class="card-title">
                <el-icon><Document /></el-icon>
                最近订单
              </span>
              <el-link type="primary" :underline="false" @click="$router.push('/orders')">
                查看更多
              </el-link>
            </div>
          </template>
          <el-table :data="dashboardData.recentOrders" style="width: 100%" size="small">
            <el-table-column prop="orderNo" label="订单号" width="120" />
            <el-table-column prop="totalAmount" label="金额" width="100">
              <template #default="{ row }">
                <span style="color: #f56c6c; font-weight: 600;">¥{{ row.totalAmount }}</span>
              </template>
            </el-table-column>
            <el-table-column prop="status" label="状态" width="100">
              <template #default="{ row }">
                <el-tag :type="getStatusType(row.status)" size="small">
                  {{ getStatusText(row.status) }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="createTime" label="时间">
              <template #default="{ row }">
                {{ formatTime(row.createTime) }}
              </template>
            </el-table-column>
          </el-table>
        </el-card>
      </el-col>

      <!-- 热门商品 -->
      <el-col :xs="24" :md="12">
        <el-card class="panel-card">
          <template #header>
            <div class="card-header">
              <span class="card-title">
                <el-icon><TrendCharts /></el-icon>
                热门商品
              </span>
              <el-link type="primary" :underline="false" @click="$router.push('/products')">
                查看更多
              </el-link>
            </div>
          </template>
          <div class="hot-products">
            <div 
              v-for="(product, index) in dashboardData.hotProducts" 
              :key="product.id"
              class="hot-product-item"
              @click="$router.push(`/products`)"
            >
              <div class="rank" :class="'rank-' + (index + 1)">{{ index + 1 }}</div>
              <div class="product-info">
                <div class="product-title">{{ product.title }}</div>
                <div class="product-meta">
                  <span class="price">¥{{ product.price }}</span>
                  <span class="views">
                    <el-icon><View /></el-icon>
                    {{ product.viewCount || 0 }}
                  </span>
                </div>
              </div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
// ============================================================
// 代码贡献统计（项目总计 3000 行）
// 文件: UI/admin/src/views/Dashboard.vue
// hangu: 0 行 | kelei: 0 行 | 本文件合计: 0 行
// ============================================================
import { ref, onMounted, computed } from 'vue'
import request from '@/utils/request'
import { 
  User, Goods, ShoppingCart, ChatDotSquare, Money,
  Clock, CaretTop, Lightning, Warning, Monitor, Refresh,
  Bell, Box, Van, WarningFilled, Document, TrendCharts, View
} from '@element-plus/icons-vue'

const stats = ref({
  userCount: 0,
  productCount: 0,
  orderCount: 0,
  totalAmount: 0
})

const currentTime = ref('')
const lastUpdateTime = ref('')
const dashboardData = ref({
  recentOrders: [],
  hotProducts: [],
  recentUsers: [],
  pendingTasks: {
    pendingShip: 0,
    shipped: 0,
    lowStock: 0
  }
})

// 获取Dashboard详细数据
const fetchDashboardData = async () => {
  try {
    const res = await request.get('/statistics/dashboard')
    if (res && res.data) {
      dashboardData.value = res.data
    }
  } catch (error) {
    console.error('获取Dashboard数据失败:', error)
  }
}

// 格式化数字
const formatNumber = (num) => {
  if (!num) return '0'
  return num.toLocaleString('zh-CN')
}

// 格式化金额
const formatMoney = (amount) => {
  if (!amount) return '0.00'
  return parseFloat(amount).toLocaleString('zh-CN', {
    minimumFractionDigits: 2,
    maximumFractionDigits: 2
  })
}

// 更新时间
const updateTime = () => {
  const now = new Date()
  currentTime.value = now.toLocaleString('zh-CN', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit',
    hour: '2-digit',
    minute: '2-digit',
    second: '2-digit',
    hour12: false
  })
}

// 获取统计数据
const fetchStatistics = async () => {
  try {
    const res = await request.get('/statistics/overview')
    if (res && res.data) {
      stats.value = res.data
      lastUpdateTime.value = new Date().toLocaleString('zh-CN')
    }
  } catch (error) {
    console.error('获取统计数据失败:', error)
  }
}

// 格式化时间
const formatTime = (timeStr) => {
  if (!timeStr) return '-'
  const date = new Date(timeStr)
  const now = new Date()
  const diff = now - date
  const minutes = Math.floor(diff / 60000)
  const hours = Math.floor(diff / 3600000)
  const days = Math.floor(diff / 86400000)
  
  if (minutes < 1) return '刚刚'
  if (minutes < 60) return `${minutes}分钟前`
  if (hours < 24) return `${hours}小时前`
  if (days < 7) return `${days}天前`
  
  return date.toLocaleDateString('zh-CN')
}

// 获取订单状态类型
const getStatusType = (status) => {
  const typeMap = {
    'PENDING': 'info',
    'PAID': 'warning',
    'SHIPPED': 'primary',
    'COMPLETED': 'success',
    'CANCELLED': 'danger'
  }
  return typeMap[status] || 'info'
}

// 获取订单状态文本
const getStatusText = (status) => {
  const textMap = {
    'PENDING': '待付款',
    'PAID': '待发货',
    'SHIPPED': '已发货',
    'COMPLETED': '已完成',
    'CANCELLED': '已取消'
  }
  return textMap[status] || status
}

onMounted(() => {
  fetchStatistics()
  fetchDashboardData()
  updateTime()
  // 每秒更新时间
  setInterval(updateTime, 1000)
  // 每30秒更新统计数据
  setInterval(() => {
    fetchStatistics()
    fetchDashboardData()
  }, 30000)
})
</script>

<style scoped>
.dashboard {
  padding: 0;
}

/* 页面头部 */
.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
  padding-bottom: 16px;
  border-bottom: 1px solid #e8e8e8;
}

.header-left {
  display: flex;
  flex-direction: column;
  gap: 6px;
}

.page-title {
  font-size: 24px;
  font-weight: 600;
  color: #333;
  margin: 0;
}

.page-desc {
  font-size: 14px;
  color: #999;
}

.header-right {
  display: flex;
  align-items: center;
  gap: 16px;
}

.current-time {
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 14px;
  color: #666;
  padding: 8px 16px;
  background: #f5f7fa;
  border-radius: 6px;
}

/* 统计卡片 */
.stat-cards {
  margin-bottom: 24px;
}

.stat-card {
  background: white;
  border-radius: 12px;
  padding: 24px;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  cursor: pointer;
  border: 2px solid transparent;
  position: relative;
  overflow: hidden;
}

.stat-card::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  height: 4px;
  background: currentColor;
  transform: scaleX(0);
  transition: transform 0.3s;
}

.stat-card:hover {
  transform: translateY(-8px);
  box-shadow: 0 12px 32px rgba(0,0,0,0.12);
  border-color: currentColor;
}

.stat-card:hover::before {
  transform: scaleX(1);
}

.user-card {
  color: #409eff;
}

.product-card {
  color: #67c23a;
}

.order-card {
  color: #e6a23c;
}

.money-card {
  color: #f56c6c;
}

.stat-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.stat-icon {
  width: 56px;
  height: 56px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 28px;
  background: currentColor;
  color: white;
  opacity: 0.9;
  transition: all 0.3s;
}

.stat-card:hover .stat-icon {
  transform: scale(1.1) rotate(5deg);
  opacity: 1;
}

.stat-trend {
  display: flex;
  align-items: center;
  gap: 4px;
  font-size: 14px;
  font-weight: 600;
}

.stat-trend.up {
  color: #67c23a;
}

.stat-body {
  margin-bottom: 16px;
}

.stat-value {
  font-size: 36px;
  font-weight: 700;
  color: #333;
  margin-bottom: 8px;
  font-family: 'DIN', 'Helvetica Neue', Arial, sans-serif;
}

.stat-label {
  font-size: 16px;
  color: #666;
  font-weight: 500;
}

.stat-footer {
  padding-top: 16px;
  border-top: 1px solid #f0f0f0;
}

.stat-desc {
  font-size: 13px;
  color: #999;
}

/* 面板卡片 */
.panel-card {
  box-shadow: 0 2px 12px rgba(0,0,0,0.04);
  border-radius: 12px;
}

.card-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.card-title {
  font-size: 16px;
  font-weight: 600;
  color: #333;
  display: flex;
  align-items: center;
  gap: 8px;
}

/* 快捷操作 */
.quick-actions {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 16px;
}

.action-item {
  display: flex;
  align-items: center;
  gap: 16px;
  padding: 16px;
  border-radius: 12px;
  background: #f8f9fa;
  cursor: pointer;
  transition: all 0.3s;
  border: 2px solid transparent;
}

.action-item:hover {
  background: #fff;
  transform: translateY(-4px);
  box-shadow: 0 8px 16px rgba(0,0,0,0.08);
  border-color: currentColor;
}

.action-icon {
  width: 48px;
  height: 48px;
  border-radius: 10px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  font-size: 24px;
  flex-shrink: 0;
}

.user-action {
  background: #409eff;
}

.product-action {
  background: #67c23a;
}

.order-action {
  background: #e6a23c;
}

.report-action {
  background: #f56c6c;
}

.action-text {
  flex: 1;
}

.action-title {
  font-size: 15px;
  font-weight: 600;
  color: #333;
  margin-bottom: 4px;
}

.action-desc {
  font-size: 13px;
  color: #999;
}

/* 系统信息 */
.system-info {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.info-item {
  display: flex;
  align-items: center;
  padding: 12px 16px;
  background: #f8f9fa;
  border-radius: 8px;
  transition: all 0.3s;
}

.info-item:hover {
  background: #eef1f6;
  transform: translateX(4px);
}

.info-label {
  font-size: 14px;
  color: #666;
  font-weight: 500;
  min-width: 100px;
}

.info-value {
  font-size: 14px;
  color: #333;
  font-weight: 600;
}

/* 待处理事项 */
.pending-tasks {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 20px;
}

.task-item {
  display: flex;
  align-items: center;
  gap: 16px;
  padding: 20px;
  background: #f8f9fa;
  border-radius: 12px;
  cursor: pointer;
  transition: all 0.3s;
  border: 2px solid transparent;
}

.task-item:hover {
  background: #fff;
  transform: translateY(-4px);
  box-shadow: 0 8px 16px rgba(0,0,0,0.08);
  border-color: currentColor;
}

.task-icon {
  width: 56px;
  height: 56px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  font-size: 28px;
  flex-shrink: 0;
}

.task-content {
  flex: 1;
  text-align: center;
}

.task-count {
  font-size: 32px;
  font-weight: 700;
  color: #333;
  margin-bottom: 4px;
  font-family: 'DIN', 'Helvetica Neue', Arial, sans-serif;
}

.task-label {
  font-size: 14px;
  color: #666;
}

/* 热门商品 */
.hot-products {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.hot-product-item {
  display: flex;
  align-items: center;
  gap: 16px;
  padding: 16px;
  background: #f8f9fa;
  border-radius: 10px;
  cursor: pointer;
  transition: all 0.3s;
}

.hot-product-item:hover {
  background: #fff;
  transform: translateX(4px);
  box-shadow: 0 4px 12px rgba(0,0,0,0.08);
}

.rank {
  width: 32px;
  height: 32px;
  border-radius: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 18px;
  font-weight: 700;
  color: white;
  background: #909399;
  flex-shrink: 0;
}

.rank-1 {
  background: linear-gradient(135deg, #f5af19 0%, #f12711 100%);
  box-shadow: 0 4px 12px rgba(245, 175, 25, 0.4);
}

.rank-2 {
  background: linear-gradient(135deg, #c0c0aa 0%, #a7a7a7 100%);
}

.rank-3 {
  background: linear-gradient(135deg, #cd7f32 0%, #b8860b 100%);
}

.product-info {
  flex: 1;
}

.product-title {
  font-size: 15px;
  font-weight: 600;
  color: #333;
  margin-bottom: 8px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.product-meta {
  display: flex;
  align-items: center;
  gap: 16px;
  font-size: 13px;
  color: #999;
}

.product-meta .price {
  color: #f56c6c;
  font-weight: 600;
  font-size: 16px;
}

.product-meta .views {
  display: flex;
  align-items: center;
  gap: 4px;
}

/* 响应式 */
@media (max-width: 768px) {
  .page-header {
    flex-direction: column;
    align-items: flex-start;
    gap: 16px;
  }
  
  .header-right {
    width: 100%;
    justify-content: space-between;
  }
  
  .page-title {
    font-size: 20px;
  }
  
  .stat-value {
    font-size: 28px;
  }
  
  .quick-actions {
    grid-template-columns: 1fr;
  }
  
  .pending-tasks {
    grid-template-columns: 1fr;
  }
}
</style>










