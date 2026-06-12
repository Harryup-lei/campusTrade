<template>
  <div class="want-offers-container">
    <el-card>
      <template #header>
        <div class="card-header">
          <span class="title">求购出价管理</span>
        </div>
      </template>

      <!-- 筛选栏 -->
      <div class="filter-bar">
        <el-select v-model="filterStatus" placeholder="状态筛选" clearable style="width: 150px" @change="fetchOffers">
          <el-option label="全部" value="" />
          <el-option label="待处理" value="PENDING" />
          <el-option label="已接受" value="ACCEPTED" />
          <el-option label="已拒绝" value="REJECTED" />
        </el-select>
        <el-button type="primary" @click="fetchOffers">
          <el-icon><Search /></el-icon>
          查询
        </el-button>
      </div>

      <!-- 出价列表 -->
      <el-table :data="offers" v-loading="loading" stripe>
        <el-table-column prop="id" label="ID" width="80" />
        
        <el-table-column label="求购信息" min-width="200">
          <template #default="{ row }">
            <div>{{ row.wantTitle || `求购ID: ${row.wantId}` }}</div>
          </template>
        </el-table-column>
        
        <el-table-column label="出价人" width="150">
          <template #default="{ row }">
            <div>{{ row.sellerName || `用户ID: ${row.sellerId}` }}</div>
          </template>
        </el-table-column>
        
        <el-table-column prop="price" label="出价金额" width="120">
          <template #default="{ row }">
            <span style="color: #ff6b00; font-weight: 600;">¥{{ row.price }}</span>
          </template>
        </el-table-column>
        
        <el-table-column prop="description" label="商品描述" min-width="200" show-overflow-tooltip />
        
        <el-table-column prop="contact" label="联系方式" width="150" show-overflow-tooltip />
        
        <el-table-column prop="location" label="交易地点" width="150" show-overflow-tooltip />
        
        <el-table-column label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="getStatusType(row.status)">
              {{ getStatusText(row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        
        <el-table-column label="创建时间" width="180">
          <template #default="{ row }">
            {{ formatDate(row.createTime) }}
          </template>
        </el-table-column>
        
        <el-table-column label="操作" width="180" fixed="right">
          <template #default="{ row }">
            <el-button size="small" @click="handleViewDetail(row)">
              查看详情
            </el-button>
            <el-popconfirm 
              title="确定删除这条出价吗？" 
              @confirm="handleDelete(row.id)"
              confirm-button-text="确定"
              cancel-button-text="取消"
            >
              <template #reference>
                <el-button size="small" type="danger">删除</el-button>
              </template>
            </el-popconfirm>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页 -->
      <div class="pagination-wrapper">
        <el-pagination
          v-model:current-page="currentPage"
          v-model:page-size="pageSize"
          :page-sizes="[10, 20, 50]"
          :total="total"
          layout="total, sizes, prev, pager, next, jumper"
          background
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
        />
      </div>
    </el-card>

    <!-- 详情对话框 -->
    <el-dialog 
      v-model="detailVisible" 
      title="出价详情"
      width="600px"
    >
      <div v-if="currentOffer" class="offer-detail">
        <el-descriptions :column="1" border>
          <el-descriptions-item label="出价ID">{{ currentOffer.id }}</el-descriptions-item>
          <el-descriptions-item label="求购信息">{{ currentOffer.wantTitle || `求购ID: ${currentOffer.wantId}` }}</el-descriptions-item>
          <el-descriptions-item label="出价人">{{ currentOffer.sellerName || `用户ID: ${currentOffer.sellerId}` }}</el-descriptions-item>
          <el-descriptions-item label="出价金额">
            <span style="color: #ff6b00; font-weight: 600; font-size: 18px;">¥{{ currentOffer.price }}</span>
          </el-descriptions-item>
          <el-descriptions-item label="商品描述">{{ currentOffer.description }}</el-descriptions-item>
          <el-descriptions-item label="联系方式">{{ currentOffer.contact }}</el-descriptions-item>
          <el-descriptions-item label="交易地点">{{ currentOffer.location }}</el-descriptions-item>
          <el-descriptions-item label="状态">
            <el-tag :type="getStatusType(currentOffer.status)">
              {{ getStatusText(currentOffer.status) }}
            </el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="创建时间">{{ formatDate(currentOffer.createTime) }}</el-descriptions-item>
          <el-descriptions-item label="更新时间">{{ formatDate(currentOffer.updateTime) }}</el-descriptions-item>
        </el-descriptions>
      </div>
    </el-dialog>
  </div>
</template>

<script setup>
// ============================================================
// 代码贡献统计（项目总计 3000 行）
// 文件: UI/admin/src/views/WantOffers.vue
// hangu: 0 行 | kelei: 0 行 | 本文件合计: 0 行
// ============================================================
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { Search } from '@element-plus/icons-vue'
import request from '@/utils/request'

const loading = ref(false)
const offers = ref([])
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)
const filterStatus = ref('')
const detailVisible = ref(false)
const currentOffer = ref(null)

// 获取出价列表
const fetchOffers = async () => {
  loading.value = true
  try {
    const params = {
      page: currentPage.value - 1,
      size: pageSize.value
    }
    
    if (filterStatus.value) {
      params.status = filterStatus.value
    }
    
    const res = await request.get('/admin/want-offers', { params })
    
    if (res && res.data) {
      offers.value = res.data.records || res.data.content || []
      total.value = res.data.total || res.data.totalElements || 0
    }
  } catch (error) {
    console.error('获取出价列表失败:', error)
    ElMessage.error('获取出价列表失败')
  } finally {
    loading.value = false
  }
}

// 查看详情
const handleViewDetail = (row) => {
  currentOffer.value = row
  detailVisible.value = true
}

// 删除
const handleDelete = async (id) => {
  try {
    const res = await request.delete(`/admin/want-offers/${id}`)
    if (res && res.code === 200) {
      ElMessage.success('删除成功')
      fetchOffers()
    }
  } catch (error) {
    ElMessage.error('删除失败')
  }
}

// 分页处理
const handleSizeChange = () => {
  currentPage.value = 1
  fetchOffers()
}

const handleCurrentChange = () => {
  fetchOffers()
}

// 状态文本
const getStatusText = (status) => {
  const statusMap = {
    'PENDING': '待处理',
    'ACCEPTED': '已接受',
    'REJECTED': '已拒绝'
  }
  return statusMap[status] || '未知'
}

// 状态类型
const getStatusType = (status) => {
  const typeMap = {
    'PENDING': 'warning',
    'ACCEPTED': 'success',
    'REJECTED': 'info'
  }
  return typeMap[status] || 'info'
}

// 格式化日期
const formatDate = (dateStr) => {
  if (!dateStr) return '-'
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
  fetchOffers()
})
</script>

<style scoped>
.want-offers-container {
  padding: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.title {
  font-size: 18px;
  font-weight: 600;
  color: #303133;
}

.filter-bar {
  display: flex;
  gap: 12px;
  margin-bottom: 20px;
}

.pagination-wrapper {
  margin-top: 20px;
  display: flex;
  justify-content: center;
}

.offer-detail {
  padding: 10px 0;
}
</style>










