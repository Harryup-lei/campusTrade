<template>
  <div class="orders-page">
    <el-card>
      <template #header>
        <h2>我的订单</h2>
      </template>
      <el-table :data="orders" style="width: 100%">
        <el-table-column prop="orderNo" label="订单号" width="180"></el-table-column>
        <el-table-column prop="product.title" label="商品"></el-table-column>
        <el-table-column prop="totalAmount" label="金额" width="120">
          <template #default="{ row }">
            ¥{{ row.totalAmount }}
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="120">
          <template #default="{ row }">
            <el-tag :type="getStatusType(row.status)">
              {{ getStatusText(row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="下单时间" width="180">
          <template #default="{ row }">
            {{ formatDate(row.createTime) }}
          </template>
        </el-table-column>
      </el-table>

      <div class="pagination">
        <el-pagination
          v-model:current-page="page"
          v-model:page-size="size"
          :total="total"
          layout="total, prev, pager, next"
          @current-change="fetchOrders"
        />
      </div>
    </el-card>
  </div>
</template>

<script setup>
// ============================================================
// 代码贡献统计（项目总计 3000 行）
// 文件: UI/frontend/src/views/Orders.vue
// hangu: 0 行 | kelei: 0 行 | 本文件合计: 0 行
// ============================================================
import { ref, onMounted } from 'vue'
import { useUserStore } from '@/stores/user'
import request from '@/utils/request'

const userStore = useUserStore()
const orders = ref([])
const page = ref(1)
const size = ref(10)
const total = ref(0)

const fetchOrders = async () => {
  try {
    const res = await request.get('/orders/buyer', {
      params: {
        buyerId: userStore.userInfo.id,
        page: page.value - 1,
        size: size.value
      }
    })
    orders.value = res.data?.content || []
    total.value = res.data?.totalElements || 0
  } catch (error) {
    console.error(error)
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
  const map = {
    'PENDING': 'warning',
    'PAID': 'success',
    'SHIPPED': 'primary',
    'COMPLETED': 'info',
    'CANCELLED': 'danger'
  }
  return map[status] || 'info'
}

const formatDate = (dateString) => {
  if (!dateString) return ''
  return new Date(dateString).toLocaleString('zh-CN')
}

onMounted(() => {
  fetchOrders()
})
</script>

<style scoped>
h2 {
  margin: 0;
}

.pagination {
  margin-top: 20px;
  display: flex;
  justify-content: center;
}
</style>










