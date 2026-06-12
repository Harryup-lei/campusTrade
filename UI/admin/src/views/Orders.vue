<template>
  <div class="orders-page">
    <!-- 搜索栏 -->
    <el-card class="search-card">
      <el-form :inline="true" :model="searchForm" class="search-form">
        <el-form-item>
          <el-input
            v-model="searchForm.orderNo"
            placeholder="搜索订单号"
            clearable
            @clear="handleSearch"
          >
            <template #prefix>
              <el-icon><Search /></el-icon>
            </template>
          </el-input>
        </el-form-item>
        <el-form-item>
          <el-select v-model="searchForm.status" placeholder="选择状态" clearable @change="handleSearch">
            <el-option label="全部状态" :value="null"></el-option>
            <el-option label="待支付" value="PENDING"></el-option>
            <el-option label="已支付" value="PAID"></el-option>
            <el-option label="已发货" value="SHIPPED"></el-option>
            <el-option label="已完成" value="COMPLETED"></el-option>
            <el-option label="已取消" value="CANCELLED"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">
            <el-icon><Search /></el-icon>
            搜索
          </el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <!-- 数据表格 -->
    <el-card class="table-card">
      <template #header>
        <div class="card-header">
          <div class="header-left">
            <el-icon class="title-icon"><List /></el-icon>
            <span class="title">订单列表</span>
            <el-tag class="count-tag">共 {{ total }} 单</el-tag>
          </div>
        </div>
      </template>

      <el-table
        :data="orders"
        v-loading="loading"
        style="width: 100%"
        :header-cell-style="{ background: '#f5f7fa', color: '#606266' }"
        stripe
      >
        <el-table-column prop="id" label="ID" width="80" align="center"></el-table-column>
        
        <el-table-column prop="orderNo" label="订单号" width="200" align="center">
          <template #default="{ row }">
            <span style="color: #409eff; font-weight: 600;">{{ row.orderNo }}</span>
          </template>
        </el-table-column>

        <el-table-column label="商品信息" min-width="250">
          <template #default="{ row }">
            <div class="product-info" v-if="row.product">
              <el-image
                :src="row.product.images && row.product.images.length > 0 ? row.product.images[0] : defaultImage"
                class="product-image"
                fit="cover"
              >
                <template #error>
                  <div class="image-error">
                    <el-icon><Picture /></el-icon>
                  </div>
                </template>
              </el-image>
              <div class="product-text">
                <div class="product-title">{{ row.product.title }}</div>
                <div class="product-price">¥{{ row.totalAmount }}</div>
              </div>
            </div>
            <span v-else>-</span>
          </template>
        </el-table-column>

        <el-table-column label="买家" width="120" align="center">
          <template #default="{ row }">
            <span>{{ row.buyer?.nickname || row.buyer?.username || '-' }}</span>
          </template>
        </el-table-column>

        <el-table-column label="卖家" width="120" align="center">
          <template #default="{ row }">
            <span>{{ row.seller?.nickname || row.seller?.username || '-' }}</span>
          </template>
        </el-table-column>

        <el-table-column prop="quantity" label="数量" width="80" align="center">
          <template #default="{ row }">
            <el-tag size="small">{{ row.quantity }}</el-tag>
          </template>
        </el-table-column>

        <el-table-column prop="totalAmount" label="金额" width="100" align="center">
          <template #default="{ row }">
            <span class="price">¥{{ row.totalAmount }}</span>
          </template>
        </el-table-column>

        <el-table-column prop="status" label="状态" width="100" align="center">
          <template #default="{ row }">
            <el-tag :type="getStatusType(row.status)" effect="plain" size="small">
              {{ getStatusText(row.status) }}
            </el-tag>
          </template>
        </el-table-column>

        <el-table-column prop="createTime" label="下单时间" width="150">
          <template #default="{ row }">
            <div class="time-info">
              <el-icon><Clock /></el-icon>
              <span>{{ formatTime(row.createTime) }}</span>
            </div>
          </template>
        </el-table-column>

        <el-table-column label="操作" width="380" align="center" fixed="right">
          <template #default="{ row }">
            <div class="action-buttons">
              <el-button size="small" type="primary" @click="handleView(row)">
                <el-icon><View /></el-icon>
                查看
              </el-button>
              
              <!-- 待支付/已支付：可以编辑 -->
              <el-button v-if="['PENDING', 'PAID'].includes(row.status)" size="small" type="warning" @click="handleEdit(row)">
                <el-icon><Edit /></el-icon>
                编辑
              </el-button>
              
              <!-- 待支付：可以取消 -->
              <el-button v-if="row.status === 'PENDING'" size="small" type="danger" @click="handleCancel(row)">
                取消
              </el-button>
              
              <!-- 已支付：可以发货 -->
              <el-button v-if="row.status === 'PAID'" size="small" type="success" @click="handleShip(row)">
                <el-icon><Promotion /></el-icon>
                发货
              </el-button>
              
              <!-- 已发货：可以完成 -->
              <el-button v-if="row.status === 'SHIPPED'" size="small" type="warning" @click="handleComplete(row)">
                <el-icon><CircleCheck /></el-icon>
                完成
              </el-button>
              
              <!-- 已取消/已完成：可以删除 -->
              <el-button v-if="['CANCELLED', 'COMPLETED'].includes(row.status)" size="small" type="danger" @click="handleDelete(row)">
                <el-icon><Delete /></el-icon>
                删除
              </el-button>
            </div>
          </template>
        </el-table-column>
      </el-table>

      <!-- 空状态 -->
      <el-empty v-if="!loading && orders.length === 0" description="暂无订单数据">
        <el-button type="primary" @click="fetchOrders">刷新</el-button>
      </el-empty>

      <!-- 分页 -->
      <div class="pagination" v-if="orders.length > 0">
        <el-pagination
          v-model:current-page="page"
          v-model:page-size="size"
          :total="total"
          :page-sizes="[10, 20, 50, 100]"
          layout="total, sizes, prev, pager, next, jumper"
          @current-change="fetchOrders"
          @size-change="fetchOrders"
        />
      </div>
    </el-card>

    <!-- 订单详情对话框 -->
    <el-dialog v-model="viewDialogVisible" title="订单详情" width="800px">
      <el-descriptions :column="2" border v-if="currentOrder">
        <el-descriptions-item label="订单号" :span="2">
          <span style="color: #409eff; font-weight: 600;">{{ currentOrder.orderNo }}</span>
        </el-descriptions-item>
        <el-descriptions-item label="订单状态">
          <el-tag :type="getStatusType(currentOrder.status)">
            {{ getStatusText(currentOrder.status) }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="订单金额">
          <span class="price">¥{{ currentOrder.totalAmount }}</span>
        </el-descriptions-item>
        <el-descriptions-item label="商品名称" :span="2">
          {{ currentOrder.product?.title || '-' }}
        </el-descriptions-item>
        <el-descriptions-item label="购买数量">
          {{ currentOrder.quantity }}
        </el-descriptions-item>
        <el-descriptions-item label="单价">
          ¥{{ currentOrder.product?.price || '-' }}
        </el-descriptions-item>
        <el-descriptions-item label="买家">
          {{ currentOrder.buyer?.nickname || currentOrder.buyer?.username || '-' }}
        </el-descriptions-item>
        <el-descriptions-item label="卖家">
          {{ currentOrder.seller?.nickname || currentOrder.seller?.username || '-' }}
        </el-descriptions-item>
        <el-descriptions-item label="收货地址" :span="2">
          {{ currentOrder.shippingAddress || '-' }}
        </el-descriptions-item>
        <el-descriptions-item label="联系电话" :span="2">
          {{ currentOrder.contactPhone || '-' }}
        </el-descriptions-item>
        <el-descriptions-item label="备注" :span="2">
          {{ currentOrder.remark || '-' }}
        </el-descriptions-item>
        <el-descriptions-item label="下单时间" :span="2">
          {{ formatTime(currentOrder.createTime) }}
        </el-descriptions-item>
        <el-descriptions-item label="支付时间" v-if="currentOrder.payTime" :span="2">
          {{ formatTime(currentOrder.payTime) }}
        </el-descriptions-item>
        <el-descriptions-item label="发货时间" v-if="currentOrder.shipTime" :span="2">
          {{ formatTime(currentOrder.shipTime) }}
        </el-descriptions-item>
        <el-descriptions-item label="完成时间" v-if="currentOrder.completeTime" :span="2">
          {{ formatTime(currentOrder.completeTime) }}
        </el-descriptions-item>
      </el-descriptions>
      <template #footer>
        <div style="display: flex; justify-content: space-between; align-items: center;">
          <div style="display: flex; gap: 10px;">
            <!-- 待支付：可以取消 -->
            <el-button v-if="currentOrder?.status === 'PENDING'" type="danger" @click="handleCancelFromDialog">
              取消订单
            </el-button>
            
            <!-- 已支付：可以发货 -->
            <el-button v-if="currentOrder?.status === 'PAID'" type="success" @click="handleShipFromDialog">
              <el-icon><Promotion /></el-icon>
              发货
            </el-button>
            
            <!-- 已发货：可以完成 -->
            <el-button v-if="currentOrder?.status === 'SHIPPED'" type="warning" @click="handleCompleteFromDialog">
              <el-icon><CircleCheck /></el-icon>
              完成订单
            </el-button>
          </div>
          <el-button @click="viewDialogVisible = false">关闭</el-button>
        </div>
      </template>
    </el-dialog>

    <!-- 编辑订单对话框 -->
    <el-dialog v-model="editDialogVisible" title="编辑订单" width="600px">
      <el-form :model="editForm" :rules="editRules" ref="editFormRef" label-width="100px">
        <el-form-item label="订单号">
          <el-input v-model="editForm.orderNo" disabled></el-input>
        </el-form-item>
        <el-form-item label="订单状态">
          <el-tag :type="getStatusType(editForm.status)">
            {{ getStatusText(editForm.status) }}
          </el-tag>
        </el-form-item>
        <el-form-item label="购买数量" prop="quantity">
          <el-input-number v-model="editForm.quantity" :min="1" :max="99" style="width: 100%"></el-input-number>
        </el-form-item>
        <el-form-item label="收货地址" prop="shippingAddress">
          <el-input v-model="editForm.shippingAddress" placeholder="请输入收货地址"></el-input>
        </el-form-item>
        <el-form-item label="联系电话" prop="contactPhone">
          <el-input v-model="editForm.contactPhone" placeholder="请输入联系电话"></el-input>
        </el-form-item>
        <el-form-item label="备注">
          <el-input
            v-model="editForm.remark"
            type="textarea"
            :rows="3"
            placeholder="请输入备注信息"
          ></el-input>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="editDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleEditSubmit" :loading="updating">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
// ============================================================
// 代码贡献统计（项目总计 3000 行）
// 文件: UI/admin/src/views/Orders.vue
// hangu: 0 行 | kelei: 0 行 | 本文件合计: 0 行
// ============================================================
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Search, List, View, Edit, Delete, Clock, Picture, Promotion, CircleCheck } from '@element-plus/icons-vue'
import request from '@/utils/request'

const orders = ref([])
const page = ref(1)
const size = ref(10)
const total = ref(0)
const loading = ref(false)
const viewDialogVisible = ref(false)
const editDialogVisible = ref(false)
const currentOrder = ref(null)
const editFormRef = ref(null)
const updating = ref(false)
const defaultImage = 'https://cube.elemecdn.com/6/94/4d3ea53c084bad6931a56d5158a48jpeg.jpeg'

const searchForm = ref({
  orderNo: '',
  status: null
})

const editForm = ref({
  id: null,
  orderNo: '',
  status: '',
  quantity: 1,
  shippingAddress: '',
  contactPhone: '',
  remark: ''
})

const editRules = {
  quantity: [
    { required: true, message: '请输入购买数量', trigger: 'blur' },
    { type: 'number', min: 1, message: '数量至少为1', trigger: 'blur' }
  ],
  shippingAddress: [
    { required: true, message: '请输入收货地址', trigger: 'blur' }
  ],
  contactPhone: [
    { required: true, message: '请输入联系电话', trigger: 'blur' },
    { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号码', trigger: 'blur' }
  ]
}

const fetchOrders = async () => {
  loading.value = true
  try {
    const params = {
      page: page.value - 1,
      size: size.value
    }
    
    if (searchForm.value.orderNo) params.orderNo = searchForm.value.orderNo
    if (searchForm.value.status) params.status = searchForm.value.status
    
    const res = await request.get('/orders', { params })
    console.log('订单列表响应数据：', res)
    orders.value = res.data?.records || []
    total.value = res.data?.total || 0
    console.log('订单数量：', orders.value.length, '总数：', total.value)
  } catch (error) {
    console.error('获取订单列表错误：', error)
    ElMessage.error(error.response?.data?.message || '获取订单列表失败')
  } finally {
    loading.value = false
  }
}

const handleSearch = () => {
  page.value = 1
  fetchOrders()
}

const handleReset = () => {
  searchForm.value = {
    orderNo: '',
    status: null
  }
  handleSearch()
}

const handleView = (order) => {
  currentOrder.value = order
  viewDialogVisible.value = true
}

// 编辑订单
const handleEdit = (order) => {
  editForm.value = {
    id: order.id,
    orderNo: order.orderNo,
    status: order.status,
    quantity: order.quantity,
    shippingAddress: order.shippingAddress || '',
    contactPhone: order.contactPhone || '',
    remark: order.remark || ''
  }
  editDialogVisible.value = true
}

// 提交编辑
const handleEditSubmit = async () => {
  try {
    await editFormRef.value.validate()
    
    updating.value = true
    
    const { id, orderNo, status, ...updateData } = editForm.value
    
    // 重新计算总金额（如果需要）
    await request.put(`/orders/${id}`, updateData)
    
    ElMessage.success('订单更新成功')
    editDialogVisible.value = false
    fetchOrders()
  } catch (error) {
    if (error !== false) {
      console.error(error)
      ElMessage.error(error.response?.data?.message || '更新失败')
    }
  } finally {
    updating.value = false
  }
}

// 删除订单
const handleDelete = async (order) => {
  try {
    await ElMessageBox.confirm(
      `确定要删除订单 ${order.orderNo} 吗？此操作不可恢复！`,
      '删除订单',
      {
        type: 'error',
        confirmButtonText: '确定删除',
        cancelButtonText: '取消'
      }
    )
    
    await request.delete(`/orders/${order.id}`)
    ElMessage.success('订单删除成功')
    fetchOrders()
  } catch (error) {
    if (error !== 'cancel') {
      console.error(error)
      ElMessage.error(error.response?.data?.message || '删除失败')
    }
  }
}

// 发货
const handleShip = async (order) => {
  try {
    await ElMessageBox.confirm('确定要对该订单发货吗？', '发货确认', {
      type: 'warning',
      confirmButtonText: '确定发货',
      cancelButtonText: '取消'
    })
    
    await request.put(`/orders/${order.id}/ship`)
    ElMessage.success('发货成功')
    fetchOrders()
  } catch (error) {
    if (error !== 'cancel') {
      console.error(error)
      ElMessage.error(error.response?.data?.message || '发货失败')
    }
  }
}

// 完成订单
const handleComplete = async (order) => {
  try {
    await ElMessageBox.confirm('确定要完成该订单吗？', '完成确认', {
      type: 'warning',
      confirmButtonText: '确定完成',
      cancelButtonText: '取消'
    })
    
    await request.put(`/orders/${order.id}/complete`)
    ElMessage.success('订单已完成')
    fetchOrders()
  } catch (error) {
    if (error !== 'cancel') {
      console.error(error)
      ElMessage.error(error.response?.data?.message || '操作失败')
    }
  }
}

// 取消订单
const handleCancel = async (order) => {
  try {
    await ElMessageBox.confirm('确定要取消该订单吗？此操作不可恢复！', '取消订单', {
      type: 'error',
      confirmButtonText: '确定取消',
      cancelButtonText: '我再想想'
    })
    
    await request.put(`/orders/${order.id}/cancel`)
    ElMessage.success('订单已取消')
    fetchOrders()
  } catch (error) {
    if (error !== 'cancel') {
      console.error(error)
      ElMessage.error(error.response?.data?.message || '取消失败')
    }
  }
}

// 从详情对话框发货
const handleShipFromDialog = async () => {
  if (currentOrder.value) {
    await handleShip(currentOrder.value)
    viewDialogVisible.value = false
  }
}

// 从详情对话框完成订单
const handleCompleteFromDialog = async () => {
  if (currentOrder.value) {
    await handleComplete(currentOrder.value)
    viewDialogVisible.value = false
  }
}

// 从详情对话框取消订单
const handleCancelFromDialog = async () => {
  if (currentOrder.value) {
    await handleCancel(currentOrder.value)
    viewDialogVisible.value = false
  }
}

const getStatusText = (status) => {
  const map = {
    'PENDING': '待支付',
    'PAID': '已支付',
    'SHIPPED': '已发货',
    'COMPLETED': '已完成',
    'CANCELLED': '已取消'
  }
  return map[status] || status
}

const getStatusType = (status) => {
  const typeMap = {
    'PENDING': 'warning',
    'PAID': 'primary',
    'SHIPPED': 'success',
    'COMPLETED': 'info',
    'CANCELLED': 'danger'
  }
  return typeMap[status] || ''
}

const formatTime = (time) => {
  if (!time) return '-'
  const date = new Date(time)
  return date.toLocaleDateString('zh-CN', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit'
  })
}

onMounted(() => {
  fetchOrders()
})
</script>

<style scoped>
.orders-page {
  padding: 0;
  overflow-x: hidden;
}

.search-card {
  margin-bottom: 20px;
}

.search-form {
  margin: 0;
}

.search-form :deep(.el-form-item) {
  margin-bottom: 0;
}

.table-card {
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.08);
  overflow-x: hidden;
}

.table-card :deep(.el-card__body) {
  scrollbar-gutter: stable;
  overflow-x: hidden;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.header-left {
  display: flex;
  align-items: center;
  gap: 12px;
}

.title-icon {
  font-size: 20px;
  color: #3e6bda;
}

.title {
  font-size: 16px;
  font-weight: 600;
  color: #333;
}

.count-tag {
  background: linear-gradient(135deg, #3e6bda 0%, #5c7cfa 100%);
  color: #fff;
  border: none;
}

/* 商品信息样式 */
.product-info {
  display: flex;
  align-items: center;
  gap: 12px;
}

.product-image {
  width: 60px;
  height: 60px;
  border-radius: 4px;
  flex-shrink: 0;
}

.image-error {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 100%;
  height: 100%;
  background-color: #f5f7fa;
  color: #909399;
}

.product-text {
  display: flex;
  flex-direction: column;
  gap: 4px;
  flex: 1;
  min-width: 0;
}

.product-title {
  font-weight: 600;
  color: #333;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.product-price {
  font-size: 12px;
  color: #ff6b00;
  font-weight: 600;
}

.price {
  font-size: 15px;
  font-weight: 600;
  color: #ff6b00;
}

.time-info {
  display: flex;
  align-items: center;
  gap: 6px;
  color: #909399;
  font-size: 13px;
}

.time-info .el-icon {
  font-size: 14px;
}

/* 操作按钮容器 */
.action-buttons {
  display: flex;
  justify-content: center;
  align-items: center;
  gap: 6px;
  flex-wrap: nowrap;
}

/* 分页 */
.pagination {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}

.pagination :deep(.el-pagination) {
  font-weight: 400;
}

.pagination :deep(.el-pager li.is-active) {
  background: linear-gradient(135deg, #ff6b00 0%, #ff9f43 100%);
  color: #fff;
}

.pagination :deep(.el-pager li:hover) {
  color: #3e6bda;
}

/* 对话框优化 */
:deep(.el-dialog__header) {
  background: linear-gradient(135deg, #3e6bda 0%, #5c7cfa 100%);
  padding: 16px 20px;
  margin: 0;
}

:deep(.el-dialog__title) {
  color: #fff;
  font-weight: 600;
}

:deep(.el-dialog__headerbtn .el-dialog__close) {
  color: #fff;
  font-size: 20px;
}

:deep(.el-dialog__headerbtn .el-dialog__close:hover) {
  color: #fff;
}

:deep(.el-dialog__body) {
  padding: 20px;
}

/* 搜索卡片按钮优化 */
.search-card :deep(.el-button--primary) {
  background: linear-gradient(135deg, #ff6b00 0%, #ff9f43 100%);
  border: none;
}

.search-card :deep(.el-button--primary:hover) {
  opacity: 0.9;
  transform: translateY(-1px);
  box-shadow: 0 4px 12px rgba(255, 107, 0, 0.3);
}

/* 表格内按钮样式优化 */
.table-card :deep(.el-button--small) {
  padding: 5px 10px;
  font-size: 12px;
  white-space: nowrap;
}

.table-card :deep(.el-button--primary) {
  background-color: #409eff;
  border-color: #409eff;
}

.table-card :deep(.el-button--primary:hover) {
  background-color: #66b1ff;
  border-color: #66b1ff;
}

.table-card :deep(.el-button--success) {
  background: linear-gradient(135deg, #67c23a 0%, #85ce61 100%);
  border: none;
}

.table-card :deep(.el-button--success:hover) {
  opacity: 0.9;
  transform: translateY(-1px);
}

.table-card :deep(.el-button--warning) {
  background: linear-gradient(135deg, #e6a23c 0%, #f0c78a 100%);
  border: none;
}

.table-card :deep(.el-button--warning:hover) {
  opacity: 0.9;
  transform: translateY(-1px);
}

.table-card :deep(.el-button--danger) {
  background: linear-gradient(135deg, #f56c6c 0%, #f89898 100%);
  border: none;
}

.table-card :deep(.el-button--danger:hover) {
  opacity: 0.9;
  transform: translateY(-1px);
}

/* 对话框按钮样式 */
:deep(.el-dialog__footer) .el-button--success {
  background: linear-gradient(135deg, #67c23a 0%, #85ce61 100%);
  border: none;
  color: #fff;
}

:deep(.el-dialog__footer) .el-button--success:hover {
  opacity: 0.9;
}

:deep(.el-dialog__footer) .el-button--warning {
  background: linear-gradient(135deg, #e6a23c 0%, #f0c78a 100%);
  border: none;
  color: #fff;
}

:deep(.el-dialog__footer) .el-button--warning:hover {
  opacity: 0.9;
}

:deep(.el-dialog__footer) .el-button--danger {
  background: linear-gradient(135deg, #f56c6c 0%, #f89898 100%);
  border: none;
  color: #fff;
}

:deep(.el-dialog__footer) .el-button--danger:hover {
  opacity: 0.9;
}
</style>










