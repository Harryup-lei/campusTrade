<template>
  <div class="reports-page">
    <!-- 搜索区域 -->
    <el-card class="search-card">
      <el-form :inline="true" :model="searchForm" class="search-form">
        <el-form-item label="状态">
          <el-select v-model="searchForm.status" placeholder="全部状态" clearable @change="handleSearch">
            <el-option label="全部" value="" />
            <el-option label="待处理" value="PENDING" />
            <el-option label="已受理" value="ACCEPTED" />
            <el-option label="已驳回" value="REJECTED" />
            <el-option label="已解决" value="RESOLVED" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch" :icon="Search">搜索</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <!-- 数据表格 -->
    <el-card class="table-card">
      <template #header>
        <div class="card-header">
          <div class="header-left">
            <el-icon class="title-icon"><Warning /></el-icon>
            <span class="title">举报列表</span>
            <el-tag class="count-tag">共 {{ total }} 条</el-tag>
          </div>
        </div>
      </template>

      <el-table 
        :data="reports" 
        v-loading="loading"
        style="width: 100%"
        :header-cell-style="{ background: '#f5f7fa', color: '#606266' }"
        stripe
      >
        <el-table-column prop="id" label="ID" width="80" align="center" />
        
        <el-table-column label="被举报商品" min-width="200">
          <template #default="{ row }">
            <div v-if="row.product" class="product-info">
              <div class="product-title">{{ row.product.title }}</div>
              <div class="product-seller">卖家：{{ row.product.seller?.nickname || row.product.seller?.username }}</div>
            </div>
            <span v-else>商品已删除</span>
          </template>
        </el-table-column>

        <el-table-column label="举报人" width="120">
          <template #default="{ row }">
            {{ row.reporter?.nickname || row.reporter?.username }}
          </template>
        </el-table-column>

        <el-table-column prop="reason" label="举报原因" width="120" />

        <el-table-column label="详细描述" min-width="150">
          <template #default="{ row }">
            <el-text line-clamp="2" style="font-size: 12px;">
              {{ row.description || '-' }}
            </el-text>
          </template>
        </el-table-column>

        <el-table-column label="状态" width="100" align="center">
          <template #default="{ row }">
            <el-tag :type="getStatusType(row.status)" effect="plain" size="small">
              {{ getStatusText(row.status) }}
            </el-tag>
          </template>
        </el-table-column>

        <el-table-column label="举报时间" width="180">
          <template #default="{ row }">
            <div class="time-info">
              <el-icon><Clock /></el-icon>
              <span>{{ formatTime(row.createTime) }}</span>
            </div>
          </template>
        </el-table-column>

        <el-table-column label="操作" width="350" fixed="right" align="center">
          <template #default="{ row }">
            <div class="action-buttons">
              <el-button size="small" @click="viewDetail(row)">
                <el-icon><View /></el-icon>
                详情
              </el-button>
              <template v-if="row.status === 'PENDING'">
                <el-button size="small" type="success" @click="handleAction(row, 'accept')">
                  <el-icon><Select /></el-icon>
                  受理
                </el-button>
                <el-button size="small" type="warning" @click="handleAction(row, 'reject')">
                  <el-icon><Close /></el-icon>
                  驳回
                </el-button>
              </template>
              <template v-else-if="row.status === 'ACCEPTED'">
                <el-button size="small" type="primary" @click="handleAction(row, 'resolve')">
                  <el-icon><Check /></el-icon>
                  解决
                </el-button>
              </template>
              <el-button size="small" type="danger" @click="handleDelete(row.id)">
                <el-icon><Delete /></el-icon>
                删除
              </el-button>
            </div>
          </template>
        </el-table-column>
      </el-table>

      <!-- 空状态 -->
      <el-empty v-if="!loading && reports.length === 0" description="暂无举报数据">
        <el-button type="primary" @click="fetchData">刷新</el-button>
      </el-empty>

      <!-- 分页 -->
      <div class="pagination" v-if="reports.length > 0">
        <el-pagination
          v-model:current-page="currentPage"
          v-model:page-size="pageSize"
          :page-sizes="[10, 20, 50, 100]"
          :total="total"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="fetchData"
          @current-change="fetchData"
          background
        />
      </div>
    </el-card>

    <!-- 详情对话框 -->
    <el-dialog
      v-model="detailDialogVisible"
      title="举报详情"
      width="600px"
    >
      <el-descriptions :column="1" border v-if="currentReport">
        <el-descriptions-item label="举报ID">{{ currentReport.id }}</el-descriptions-item>
        <el-descriptions-item label="被举报商品">
          {{ currentReport.product?.title || '商品已删除' }}
        </el-descriptions-item>
        <el-descriptions-item label="商品卖家">
          {{ currentReport.product?.seller?.nickname || currentReport.product?.seller?.username || '-' }}
        </el-descriptions-item>
        <el-descriptions-item label="举报人">
          {{ currentReport.reporter?.nickname || currentReport.reporter?.username }}
        </el-descriptions-item>
        <el-descriptions-item label="举报原因">{{ currentReport.reason }}</el-descriptions-item>
        <el-descriptions-item label="详细描述">
          {{ currentReport.description || '-' }}
        </el-descriptions-item>
        <el-descriptions-item label="状态">
          <el-tag :type="getStatusType(currentReport.status)">
            {{ getStatusText(currentReport.status) }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="举报时间">
          {{ formatTime(currentReport.createTime) }}
        </el-descriptions-item>
        <el-descriptions-item label="处理人" v-if="currentReport.handler">
          {{ currentReport.handler.nickname || currentReport.handler.username }}
        </el-descriptions-item>
        <el-descriptions-item label="处理时间" v-if="currentReport.handleTime">
          {{ formatTime(currentReport.handleTime) }}
        </el-descriptions-item>
        <el-descriptions-item label="处理结果" v-if="currentReport.handleResult">
          {{ currentReport.handleResult }}
        </el-descriptions-item>
      </el-descriptions>
    </el-dialog>

    <!-- 处理对话框 -->
    <el-dialog
      v-model="actionDialogVisible"
      :title="actionTitle"
      width="500px"
    >
      <el-alert
        v-if="actionType === 'resolve'"
        title="提示"
        type="warning"
        :closable="false"
        style="margin-bottom: 20px;"
      >
        标记为已解决后，该举报将归档，可选择是否下架被举报商品。
      </el-alert>
      <el-form :model="actionForm" label-width="100px">
        <el-form-item label="处理结果" required>
          <el-input
            v-model="actionForm.result"
            type="textarea"
            :rows="4"
            placeholder="请输入处理结果说明"
            maxlength="500"
            show-word-limit
          />
        </el-form-item>
        <el-form-item v-if="actionType === 'resolve'" label="下架商品">
          <el-switch v-model="actionForm.offlineProduct" active-text="下架" inactive-text="保留" />
          <div style="margin-top: 8px; font-size: 12px; color: #909399;">
            <el-icon><Warning /></el-icon>
            开启后将自动下架被举报的商品
          </div>
        </el-form-item>
      </el-form>
      <template #footer>
        <div style="display: flex; justify-content: flex-end; gap: 10px;">
          <el-button @click="actionDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="submitAction" :loading="submitting">
            <el-icon><Check /></el-icon>
            确认提交
          </el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
// ============================================================
// 代码贡献统计（项目总计 3000 行）
// 文件: UI/admin/src/views/Reports.vue
// hangu: 0 行 | kelei: 0 行 | 本文件合计: 0 行
// ============================================================
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Search, Warning, View, Select, Close, Check, Delete, Clock } from '@element-plus/icons-vue'
import request from '@/utils/request'

const loading = ref(false)
const reports = ref([])
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)
const searchForm = ref({
  status: ''
})

const detailDialogVisible = ref(false)
const currentReport = ref(null)

const actionDialogVisible = ref(false)
const actionType = ref('')
const actionTitle = ref('')
const actionForm = ref({
  result: '',
  offlineProduct: false
})
const submitting = ref(false)

// 获取举报列表
const fetchData = async () => {
  loading.value = true
  try {
    const res = await request.get('/admin/reports', {
      params: {
        page: currentPage.value - 1,
        size: pageSize.value,
        status: searchForm.value.status
      }
    })
    if (res && res.data) {
      reports.value = res.data.content || res.data.records || []
      total.value = res.data.totalElements || res.data.total || 0
    }
  } catch (error) {
    console.error('获取举报列表失败:', error)
    ElMessage.error('获取数据失败')
  } finally {
    loading.value = false
  }
}

// 搜索
const handleSearch = () => {
  currentPage.value = 1
  fetchData()
}

// 重置
const handleReset = () => {
  searchForm.value = {
    status: ''
  }
  currentPage.value = 1
  fetchData()
}

// 查看详情
const viewDetail = (row) => {
  currentReport.value = row
  detailDialogVisible.value = true
}

// 处理操作
const handleAction = (row, type) => {
  currentReport.value = row
  actionType.value = type
  
  const titleMap = {
    'accept': '受理举报',
    'reject': '驳回举报',
    'resolve': '解决举报'
  }
  actionTitle.value = titleMap[type]
  
  actionForm.value = {
    result: '',
    offlineProduct: false
  }
  
  actionDialogVisible.value = true
}

// 提交处理
const submitAction = async () => {
  if (!actionForm.value.result.trim()) {
    ElMessage.warning('请输入处理结果说明')
    return
  }
  
  submitting.value = true
  try {
    const res = await request.post(`/admin/reports/${currentReport.value.id}/${actionType.value}`, {
      result: actionForm.value.result,
      offlineProduct: actionForm.value.offlineProduct.toString()
    })
    
    if (res.code === 200) {
      ElMessage.success('处理成功')
      actionDialogVisible.value = false
      fetchData()
    } else {
      ElMessage.error(res.message || '处理失败')
    }
  } catch (error) {
    ElMessage.error('处理失败')
  } finally {
    submitting.value = false
  }
}

// 删除举报
const handleDelete = async (id) => {
  try {
    await ElMessageBox.confirm('确定要删除这条举报吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    
    const res = await request.delete(`/admin/reports/${id}`)
    if (res.code === 200) {
      ElMessage.success('删除成功')
      fetchData()
    } else {
      ElMessage.error(res.message || '删除失败')
    }
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('删除失败')
    }
  }
}

// 状态类型
const getStatusType = (status) => {
  const map = {
    'PENDING': 'warning',
    'ACCEPTED': 'primary',
    'REJECTED': 'danger',
    'RESOLVED': 'success'
  }
  return map[status] || 'info'
}

// 状态文本
const getStatusText = (status) => {
  const map = {
    'PENDING': '待处理',
    'ACCEPTED': '已受理',
    'REJECTED': '已驳回',
    'RESOLVED': '已解决'
  }
  return map[status] || status
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
  fetchData()
})
</script>

<style scoped>
.reports-page {
  padding: 20px;
}

/* 搜索卡片 */
.search-card {
  margin-bottom: 20px;
}

.search-form {
  margin: 0;
}

.search-form :deep(.el-form-item) {
  margin-bottom: 0;
}

/* 表格卡片 */
.table-card {
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.header-left {
  display: flex;
  align-items: center;
  gap: 8px;
}

.title-icon {
  font-size: 18px;
  color: #f56c6c;
}

.title {
  font-size: 16px;
  font-weight: 500;
  color: #303133;
}

.count-tag {
  margin-left: 8px;
}

/* 商品信息 */
.product-info {
  display: flex;
  flex-direction: column;
  gap: 6px;
  padding: 4px 0;
}

.product-title {
  font-weight: 500;
  color: #303133;
  font-size: 14px;
  line-height: 1.4;
}

.product-seller {
  font-size: 12px;
  color: #909399;
  display: flex;
  align-items: center;
  gap: 4px;
}

.product-seller::before {
  content: '•';
  color: #dcdfe6;
}

/* 操作按钮 */
.action-buttons {
  display: flex;
  gap: 8px;
  justify-content: center;
  flex-wrap: wrap;
}

.action-buttons .el-button {
  margin: 0;
}

/* 分页 */
.pagination {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}

/* 时间信息 */
.time-info {
  display: flex;
  align-items: center;
  gap: 4px;
  font-size: 13px;
  color: #606266;
}

.time-info .el-icon {
  font-size: 14px;
  color: #909399;
}

/* 对话框样式 */
:deep(.el-dialog) {
  border-radius: 8px;
}

:deep(.el-dialog__header) {
  background: #f5f7fa;
  border-bottom: 1px solid #e4e7ed;
  padding: 16px 20px;
}

:deep(.el-dialog__title) {
  font-size: 16px;
  font-weight: 500;
}

:deep(.el-dialog__body) {
  padding: 20px;
}

/* 表单样式 */
:deep(.el-form-item__label) {
  font-weight: 500;
  color: #606266;
}

:deep(.el-descriptions__label) {
  font-weight: 500;
  color: #606266;
}
</style>










