<template>
  <div class="comments-page">
    <!-- 搜索栏 -->
    <el-card class="search-card">
      <el-form :inline="true" :model="searchForm" class="search-form">
        <el-form-item>
          <el-input
            v-model="searchForm.keyword"
            placeholder="搜索评论内容"
            clearable
            @clear="handleSearch"
          >
            <template #prefix>
              <el-icon><Search /></el-icon>
            </template>
          </el-input>
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
            <el-icon class="title-icon"><ChatDotSquare /></el-icon>
            <span class="title">评论列表</span>
            <el-tag class="count-tag">共 {{ total }} 条</el-tag>
          </div>
        </div>
      </template>

      <el-table
        :data="comments"
        v-loading="loading"
        style="width: 100%"
        :header-cell-style="{ background: '#f5f7fa', color: '#606266' }"
        stripe
      >
        <el-table-column prop="id" label="ID" width="80" align="center"></el-table-column>
        
        <el-table-column label="评论信息" min-width="300">
          <template #default="{ row }">
            <div class="comment-info">
              <div class="comment-text">
                <div class="comment-content">{{ row.content }}</div>
                <div class="comment-meta">
                  <span class="meta-item">
                    <el-icon><User /></el-icon>
                    {{ row.user?.nickname || '未知用户' }}
                  </span>
                  <span class="meta-item">
                    <el-icon><Goods /></el-icon>
                    {{ row.product?.title || '商品已删除' }}
                  </span>
                </div>
              </div>
            </div>
          </template>
        </el-table-column>

        <el-table-column prop="createTime" label="评论时间" width="160" align="center">
          <template #default="{ row }">
            <div class="time-info">
              <el-icon><Clock /></el-icon>
              <span>{{ formatTime(row.createTime) }}</span>
            </div>
          </template>
        </el-table-column>

        <el-table-column label="操作" width="120" align="center" fixed="right">
          <template #default="{ row }">
            <el-button size="small" type="danger" @click="deleteComment(row.id)">
              <el-icon><Delete /></el-icon>
              删除
            </el-button>
          </template>
        </el-table-column>
      </el-table>

      <!-- 空状态 -->
      <el-empty v-if="!loading && comments.length === 0" description="暂无评论数据">
        <el-button type="primary" @click="fetchComments">刷新</el-button>
      </el-empty>

      <!-- 分页 -->
      <div class="pagination" v-if="comments.length > 0">
        <el-pagination
          v-model:current-page="page"
          v-model:page-size="size"
          :total="total"
          :page-sizes="[10, 20, 50, 100]"
          layout="total, sizes, prev, pager, next, jumper"
          @current-change="fetchComments"
          @size-change="fetchComments"
        />
      </div>
    </el-card>
  </div>
</template>

<script setup>
// ============================================================
// 代码贡献统计（项目总计 3000 行）
// 文件: UI/admin/src/views/Comments.vue
// hangu: 0 行 | kelei: 0 行 | 本文件合计: 0 行
// ============================================================
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Search, ChatDotSquare, User, Goods, Clock, Delete } from '@element-plus/icons-vue'
import request from '@/utils/request'

const comments = ref([])
const page = ref(1)
const size = ref(10)
const total = ref(0)
const loading = ref(false)
const searchForm = ref({
  keyword: ''
})

const fetchComments = async () => {
  loading.value = true
  try {
    const params = {
      page: page.value - 1,
      size: size.value
    }
    
    if (searchForm.value.keyword) {
      params.keyword = searchForm.value.keyword
    }
    
    const res = await request.get('/comments/admin/all', { params })
    comments.value = res.data?.records || res.data?.content || []
    total.value = res.data?.total || res.data?.totalElements || 0
  } catch (error) {
    console.error(error)
    ElMessage.error('获取评论列表失败')
  } finally {
    loading.value = false
  }
}

const handleSearch = () => {
  page.value = 1
  fetchComments()
}

const handleReset = () => {
  searchForm.value.keyword = ''
  page.value = 1
  fetchComments()
}

const deleteComment = async (id) => {
  try {
    await ElMessageBox.confirm('确定要删除该评论吗？删除后无法恢复！', '提示', { 
      type: 'warning',
      confirmButtonText: '确定',
      cancelButtonText: '取消'
    })
    await request.delete(`/comments/admin/${id}`)
    ElMessage.success('删除成功')
    fetchComments()
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
    day: '2-digit',
    hour: '2-digit',
    minute: '2-digit'
  })
}

onMounted(() => {
  fetchComments()
})
</script>

<style scoped>
.comments-page {
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

/* 评论信息样式 */
.comment-info {
  display: flex;
  align-items: flex-start;
  gap: 12px;
}

.comment-text {
  display: flex;
  flex-direction: column;
  gap: 8px;
  flex: 1;
  min-width: 0;
}

.comment-content {
  font-size: 14px;
  color: #333;
  line-height: 1.6;
  word-break: break-word;
}

.comment-meta {
  display: flex;
  align-items: center;
  gap: 16px;
  flex-wrap: wrap;
}

.meta-item {
  display: flex;
  align-items: center;
  gap: 4px;
  font-size: 12px;
  color: #909399;
}

.meta-item .el-icon {
  font-size: 14px;
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

.table-card :deep(.el-button--danger) {
  background-color: #f56c6c;
  border-color: #f56c6c;
}

.table-card :deep(.el-button--danger:hover) {
  background-color: #f78989;
  border-color: #f78989;
}
</style>










