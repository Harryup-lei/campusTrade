<template>
  <div class="free-page">
    <!-- 搜索栏 -->
    <el-card class="search-card">
      <el-form :inline="true" :model="searchForm" class="search-form">
        <el-form-item>
          <el-input
            v-model="searchForm.keyword"
            placeholder="搜索赠送标题"
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
            <el-option label="可领取" value="AVAILABLE"></el-option>
            <el-option label="已送出" value="COMPLETED"></el-option>
            <el-option label="已关闭" value="CLOSED"></el-option>
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
            <el-icon class="title-icon"><Present /></el-icon>
            <span class="title">赠送列表</span>
            <el-tag class="count-tag">共 {{ total }} 条</el-tag>
          </div>
        </div>
      </template>

      <el-table
        :data="freeList"
        v-loading="loading"
        style="width: 100%"
        :header-cell-style="{ background: '#f5f7fa', color: '#606266' }"
        stripe
      >
        <el-table-column prop="id" label="ID" width="80" align="center"></el-table-column>
        
        <el-table-column label="赠送信息" min-width="300">
          <template #default="{ row }">
            <div class="free-info">
              <el-image 
                v-if="getImage(row.images)"
                :src="getImage(row.images)" 
                class="free-image"
                fit="cover"
              >
                <template #error>
                  <div class="image-error">
                    <el-icon><Picture /></el-icon>
                  </div>
                </template>
              </el-image>
              <div class="free-text">
                <div class="free-title">{{ row.title }}</div>
                <div class="free-desc">{{ row.description || '-' }}</div>
              </div>
            </div>
          </template>
        </el-table-column>

        <el-table-column prop="categoryId" label="分类" width="100" align="center">
          <template #default="{ row }">
            <el-tag type="success" effect="plain" size="small">
              {{ row.category?.name || '-' }}
            </el-tag>
          </template>
        </el-table-column>

        <el-table-column prop="location" label="交接地点" width="120" align="center">
          <template #default="{ row }">
            <span style="font-size: 12px;">{{ row.location || '-' }}</span>
          </template>
        </el-table-column>

        <el-table-column prop="user.nickname" label="发布人" width="120" align="center">
          <template #default="{ row }">
            <span>{{ row.user?.nickname || '-' }}</span>
          </template>
        </el-table-column>

        <el-table-column prop="status" label="状态" width="100" align="center">
          <template #default="{ row }">
            <el-tag 
              :type="row.status === 'AVAILABLE' ? 'success' : row.status === 'RESERVED' ? 'warning' : 'info'" 
              effect="plain" 
              size="small"
            >
              {{ getStatusText(row.status) }}
            </el-tag>
          </template>
        </el-table-column>

        <el-table-column prop="createTime" label="发布时间" width="150" align="center">
          <template #default="{ row }">
            <div class="time-info">
              <el-icon><Clock /></el-icon>
              <span>{{ formatTime(row.createTime) }}</span>
            </div>
          </template>
        </el-table-column>

        <el-table-column label="操作" width="180" align="center" fixed="right">
          <template #default="{ row }">
            <div class="action-buttons">
              <el-button size="small" type="primary" @click="viewDetail(row)">
                <el-icon><View /></el-icon>
                查看
              </el-button>
              <el-button size="small" type="danger" @click="deleteFree(row.id)">
                <el-icon><Delete /></el-icon>
                删除
              </el-button>
            </div>
          </template>
        </el-table-column>
      </el-table>

      <!-- 空状态 -->
      <el-empty v-if="!loading && freeList.length === 0" description="暂无赠送数据">
        <el-button type="primary" @click="fetchFreeItems">刷新</el-button>
      </el-empty>

      <!-- 分页 -->
      <div class="pagination" v-if="freeList.length > 0">
        <el-pagination
          v-model:current-page="page"
          v-model:page-size="size"
          :total="total"
          :page-sizes="[10, 20, 50, 100]"
          layout="total, sizes, prev, pager, next, jumper"
          @current-change="fetchFreeItems"
          @size-change="fetchFreeItems"
        />
      </div>
    </el-card>

    <!-- 详情对话框 -->
    <el-dialog v-model="detailVisible" title="赠送详情" width="600px">
      <div v-if="currentFree" class="free-detail">
        <el-descriptions :column="1" border>
          <el-descriptions-item label="标题">{{ currentFree.title }}</el-descriptions-item>
          <el-descriptions-item label="图片">
            <div class="images-preview">
              <el-image 
                v-for="(img, index) in getImages(currentFree.images)" 
                :key="index"
                :src="img" 
                style="width: 100px; height: 100px; margin-right: 8px; border-radius: 4px;"
                fit="cover"
                :preview-src-list="getImages(currentFree.images)"
              />
            </div>
          </el-descriptions-item>
          <el-descriptions-item label="详细描述">{{ currentFree.description }}</el-descriptions-item>
          <el-descriptions-item label="分类">{{ currentFree.category?.name || '-' }}</el-descriptions-item>
          <el-descriptions-item label="联系方式">{{ currentFree.contact || '-' }}</el-descriptions-item>
          <el-descriptions-item label="交接地点">{{ currentFree.location }}</el-descriptions-item>
          <el-descriptions-item label="浏览次数">{{ currentFree.viewCount || 0 }}</el-descriptions-item>
          <el-descriptions-item label="发布人">{{ currentFree.user?.nickname }}</el-descriptions-item>
          <el-descriptions-item label="状态">
            <el-tag :type="currentFree.status === 'AVAILABLE' ? 'success' : 'info'">
              {{ getStatusText(currentFree.status) }}
            </el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="发布时间">{{ formatTime(currentFree.createTime) }}</el-descriptions-item>
        </el-descriptions>
      </div>
    </el-dialog>
  </div>
</template>

<script setup>
// ============================================================
// 代码贡献统计（项目总计 3000 行）
// 文件: UI/admin/src/views/Free.vue
// hangu: 0 行 | kelei: 0 行 | 本文件合计: 0 行
// ============================================================
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Search, Present, Picture, Clock, View, Delete } from '@element-plus/icons-vue'
import request from '@/utils/request'

const freeList = ref([])
const page = ref(1)
const size = ref(10)
const total = ref(0)
const loading = ref(false)
const searchForm = ref({
  keyword: '',
  status: null
})
const detailVisible = ref(false)
const currentFree = ref(null)

const fetchFreeItems = async () => {
  loading.value = true
  try {
    const params = {
      page: page.value - 1,
      size: size.value
    }
    
    if (searchForm.value.keyword) {
      params.keyword = searchForm.value.keyword
    }
    
    if (searchForm.value.status) {
      params.status = searchForm.value.status
    }
    
    const res = await request.get('/admin/free', { params })
    freeList.value = res.data?.records || res.data?.content || []
    total.value = res.data?.total || res.data?.totalElements || 0
  } catch (error) {
    console.error(error)
    ElMessage.error('获取数据失败')
  } finally {
    loading.value = false
  }
}

const handleSearch = () => {
  page.value = 1
  fetchFreeItems()
}

const handleReset = () => {
  searchForm.value.keyword = ''
  searchForm.value.status = null
  page.value = 1
  fetchFreeItems()
}

const getImage = (images) => {
  if (!images) return null
  const arr = typeof images === 'string' ? images.split(',') : images
  const img = arr[0]
  if (!img) return null
  if (img.startsWith('http')) return img
  return `/api${img.startsWith('/') ? '' : '/'}${img}`
}

const getImages = (images) => {
  if (!images) return []
  const arr = typeof images === 'string' ? images.split(',').filter(i => i) : images
  return arr.map(img => {
    if (img.startsWith('http')) return img
    return `/api${img.startsWith('/') ? '' : '/'}${img}`
  })
}

const getStatusText = (status) => {
  const map = {
    'AVAILABLE': '可领取',
    'COMPLETED': '已送出',
    'CLOSED': '已关闭'
  }
  return map[status] || status
}

const viewDetail = (free) => {
  currentFree.value = free
  detailVisible.value = true
}

const deleteFree = async (id) => {
  try {
    await ElMessageBox.confirm('确定要删除该赠送吗？', '提示', { type: 'warning' })
    await request.delete(`/admin/free/${id}`)
    ElMessage.success('删除成功')
    fetchFreeItems()
  } catch (error) {
    if (error !== 'cancel') {
      console.error(error)
      ElMessage.error('删除失败')
    }
  }
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
  fetchFreeItems()
})
</script>

<style scoped>
.free-page {
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

/* 赠送信息样式 */
.free-info {
  display: flex;
  align-items: center;
  gap: 12px;
}

.free-image {
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

.free-text {
  display: flex;
  flex-direction: column;
  gap: 4px;
  flex: 1;
  min-width: 0;
}

.free-title {
  font-weight: 600;
  color: #333;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.free-desc {
  font-size: 12px;
  color: #909399;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
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

.table-card :deep(.el-button--danger) {
  background-color: #f56c6c;
  border-color: #f56c6c;
}

.table-card :deep(.el-button--danger:hover) {
  background-color: #f78989;
  border-color: #f78989;
}

.free-detail {
  padding: 10px 0;
}

.images-preview {
  display: flex;
  flex-wrap: wrap;
}
</style>










