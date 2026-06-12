<template>
  <div class="customer-service-container">
    <el-page-header @back="$router.back()" content="客服咨询管理" />
    
    <!-- 统计卡片 -->
    <el-row :gutter="20" class="statistics-cards">
      <el-col :span="6" v-for="(stat, key) in statistics" :key="key">
        <el-card :body-style="{ padding: '20px' }">
          <div class="stat-item">
            <div class="stat-icon" :class="`stat-${key}`">
              <el-icon><component :is="getStatIcon(key)" /></el-icon>
            </div>
            <div class="stat-content">
              <div class="stat-value">{{ stat }}</div>
              <div class="stat-label">{{ getStatLabel(key) }}</div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 筛选 -->
    <el-card class="filter-card">
      <el-form :inline="true" :model="searchForm">
        <el-form-item label="状态">
          <el-select v-model="searchForm.status" placeholder="全部状态" clearable style="width: 150px">
            <el-option label="待处理" value="PENDING" />
            <el-option label="处理中" value="PROCESSING" />
            <el-option label="已回复" value="REPLIED" />
            <el-option label="已关闭" value="CLOSED" />
          </el-select>
        </el-form-item>
        
        <el-form-item label="类型">
          <el-select v-model="searchForm.category" placeholder="全部类型" clearable style="width: 150px">
            <el-option label="一般咨询" value="GENERAL" />
            <el-option label="投诉建议" value="COMPLAINT" />
            <el-option label="功能建议" value="SUGGESTION" />
            <el-option label="技术问题" value="TECHNICAL" />
          </el-select>
        </el-form-item>
        
        <el-form-item label="关键词">
          <el-input 
            v-model="searchForm.keyword" 
            placeholder="搜索主题/内容/用户名" 
            clearable 
            style="width: 200px"
          />
        </el-form-item>
        
        <el-form-item>
          <el-button type="primary" :icon="Search" @click="handleSearch">搜索</el-button>
          <el-button :icon="Refresh" @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <!-- 列表 -->
    <el-card>
      <el-table :data="consultations" style="width: 100%" v-loading="loading">
        <el-table-column prop="id" label="ID" width="60" />
        
        <el-table-column label="咨询信息" min-width="200">
          <template #default="{ row }">
            <div class="consultation-info">
              <div class="subject">{{ row.subject }}</div>
              <div class="content">{{ row.content }}</div>
            </div>
          </template>
        </el-table-column>
        
        <el-table-column label="用户信息" width="150">
          <template #default="{ row }">
            <div>{{ row.userName || '游客' }}</div>
            <div style="font-size: 12px; color: #999">{{ row.contactPhone }}</div>
          </template>
        </el-table-column>
        
        <el-table-column label="类型" width="100">
          <template #default="{ row }">
            <el-tag :type="getCategoryType(row.category)" size="small">
              {{ getCategoryText(row.category) }}
            </el-tag>
          </template>
        </el-table-column>
        
        <el-table-column label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="getStatusType(row.status)" size="small">
              {{ getStatusText(row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        
        <el-table-column label="回复信息" min-width="200">
          <template #default="{ row }">
            <div v-if="row.reply" class="reply-info">
              <div class="reply-content">{{ row.reply }}</div>
              <div class="reply-meta">
                {{ row.adminName }} · {{ formatTime(row.replyTime) }}
              </div>
            </div>
            <div v-else style="color: #999">暂未回复</div>
          </template>
        </el-table-column>
        
        <el-table-column label="提交时间" width="160">
          <template #default="{ row }">
            {{ formatTime(row.createTime) }}
          </template>
        </el-table-column>
        
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="{ row }">
            <el-button 
              v-if="row.status !== 'CLOSED'" 
              type="primary" 
              size="small" 
              @click="handleReply(row)"
            >
              {{ row.status === 'REPLIED' ? '再次回复' : '回复' }}
            </el-button>
            <el-dropdown @command="(cmd) => handleCommand(cmd, row)" style="margin-left: 8px">
              <el-button size="small">
                更多<el-icon class="el-icon--right"><ArrowDown /></el-icon>
              </el-button>
              <template #dropdown>
                <el-dropdown-menu>
                  <el-dropdown-item command="processing" v-if="row.status === 'PENDING'">
                    标记处理中
                  </el-dropdown-item>
                  <el-dropdown-item command="close" v-if="row.status !== 'CLOSED'">
                    关闭咨询
                  </el-dropdown-item>
                  <el-dropdown-item command="delete" divided>删除</el-dropdown-item>
                </el-dropdown-menu>
              </template>
            </el-dropdown>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页 -->
      <div class="pagination">
        <el-pagination
          v-model:current-page="currentPage"
          v-model:page-size="pageSize"
          :total="total"
          :page-sizes="[10, 20, 50, 100]"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="fetchConsultations"
          @current-change="fetchConsultations"
        />
      </div>
    </el-card>

    <!-- 回复对话框 -->
    <el-dialog v-model="replyDialogVisible" title="回复客服咨询" width="600px">
      <div class="consultation-detail">
        <div class="detail-row">
          <span class="label">用户：</span>
          <span>{{ currentConsultation.userName || '游客' }}</span>
        </div>
        <div class="detail-row">
          <span class="label">联系方式：</span>
          <span>{{ currentConsultation.contactPhone }}</span>
        </div>
        <div class="detail-row">
          <span class="label">咨询主题：</span>
          <span>{{ currentConsultation.subject }}</span>
        </div>
        <div class="detail-row">
          <span class="label">详细内容：</span>
          <div class="content-box">{{ currentConsultation.content }}</div>
        </div>
      </div>
      
      <el-divider />
      
      <el-form :model="replyForm">
        <el-form-item label="回复内容">
          <el-input
            v-model="replyForm.reply"
            type="textarea"
            :rows="6"
            placeholder="请输入回复内容"
          />
        </el-form-item>
      </el-form>
      
      <template #footer>
        <el-button @click="replyDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmitReply" :loading="submitting">提交回复</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
// ============================================================
// 代码贡献统计（项目总计 3000 行）
// 文件: UI/admin/src/views/CustomerService.vue
// hangu: 45 行 | kelei: 0 行 | 本文件合计: 45 行
// ============================================================
import { ref, reactive, onMounted } from 'vue'
import request from '@/utils/request'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Search, Refresh, ArrowDown, Clock, Loading, Check, Lock, InfoFilled } from '@element-plus/icons-vue'

const loading = ref(false)
const consultations = ref([])
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)

const searchForm = reactive({
  status: '',
  category: '',
  keyword: ''
})

const statistics = ref({
  pending: 0,
  processing: 0,
  replied: 0,
  closed: 0,
  total: 0
})

const replyDialogVisible = ref(false)
const currentConsultation = ref({})
const replyForm = reactive({
  reply: ''
})
const submitting = ref(false)

// 获取咨询列表
const fetchConsultations = async () => {
  loading.value = true
  try {
    const res = await request.get('/customer-service/admin/list', {
      params: {
        page: currentPage.value - 1,
        size: pageSize.value,
        ...searchForm
      }
    })
    consultations.value = res.data.records
    total.value = res.data.total
  } catch (error) {
    ElMessage.error('获取咨询列表失败')
  } finally {
    loading.value = false
  }
}

// 获取统计数据
const fetchStatistics = async () => {
  try {
    const res = await request.get('/customer-service/admin/statistics')
    statistics.value = res.data
  } catch (error) {
    console.error('获取统计数据失败:', error)
  }
}

// 搜索
const handleSearch = () => {
  currentPage.value = 1
  fetchConsultations()
}

// 重置
const handleReset = () => {
  searchForm.status = ''
  searchForm.category = ''
  searchForm.keyword = ''
  handleSearch()
}

// 回复咨询
const handleReply = (row) => {
  currentConsultation.value = row
  replyForm.reply = row.reply || ''
  replyDialogVisible.value = true
}

// 提交回复
const handleSubmitReply = async () => {
  if (!replyForm.reply.trim()) {
    ElMessage.warning('请输入回复内容')
    return
  }
  
  submitting.value = true
  try {
    await request.put(`/customer-service/admin/${currentConsultation.value.id}/reply`, {
      reply: replyForm.reply
    })
    ElMessage.success('回复成功')
    replyDialogVisible.value = false
    fetchConsultations()
    fetchStatistics()
  } catch (error) {
    ElMessage.error('回复失败')
  } finally {
    submitting.value = false
  }
}

// 处理命令
const handleCommand = async (command, row) => {
  if (command === 'delete') {
    try {
      await ElMessageBox.confirm('确定要删除这条咨询吗？', '提示', {
        type: 'warning'
      // @contributor hangu - handleCommand (45 行)
      })
      await request.delete(`/customer-service/admin/${row.id}`)
      ElMessage.success('删除成功')
      fetchConsultations()
      fetchStatistics()
    } catch (error) {
      if (error !== 'cancel') {
        ElMessage.error('删除失败')
      }
    }
  } else if (command === 'processing') {
    try {
      await request.put(`/customer-service/admin/${row.id}/status`, null, {
        params: { status: 'PROCESSING' }
      })
      ElMessage.success('状态更新成功')
      fetchConsultations()
      fetchStatistics()
    } catch (error) {
      ElMessage.error('状态更新失败')
    }
  } else if (command === 'close') {
    try {
      await ElMessageBox.confirm('确定要关闭这条咨询吗？', '提示', {
        type: 'warning'
      })
      await request.put(`/customer-service/admin/${row.id}/status`, null, {
        params: { status: 'CLOSED' }
      })
      ElMessage.success('咨询已关闭')
      fetchConsultations()
      fetchStatistics()
    } catch (error) {
      if (error !== 'cancel') {
        ElMessage.error('操作失败')
      }
    }
  }
}

// 格式化时间
const formatTime = (time) => {
  if (!time) return '-'
  return new Date(time).toLocaleString('zh-CN')
}

// 获取状态类型
const getStatusType = (status) => {
  const map = {
    'PENDING': 'warning',
    'PROCESSING': 'info',
    'REPLIED': 'success',
    'CLOSED': ''
  }
  return map[status] || ''
}

// 获取状态文本
const getStatusText = (status) => {
  const map = {
    'PENDING': '待处理',
    'PROCESSING': '处理中',
    'REPLIED': '已回复',
    'CLOSED': '已关闭'
  }
  return map[status] || status
}

// 获取类型样式
const getCategoryType = (category) => {
  const map = {
    'GENERAL': '',
    'COMPLAINT': 'danger',
    'SUGGESTION': 'success',
    'TECHNICAL': 'warning'
  }
  return map[category] || ''
}

// 获取类型文本
const getCategoryText = (category) => {
  const map = {
    'GENERAL': '一般咨询',
    'COMPLAINT': '投诉建议',
    'SUGGESTION': '功能建议',
    'TECHNICAL': '技术问题'
  }
  return map[category] || category
}

// 获取统计图标
const getStatIcon = (key) => {
  const map = {
    'pending': Clock,
    'processing': Loading,
    'replied': Check,
    'closed': Lock,
    'total': InfoFilled
  }
  return map[key]
}

// 获取统计标签
const getStatLabel = (key) => {
  const map = {
    'pending': '待处理',
    'processing': '处理中',
    'replied': '已回复',
    'closed': '已关闭',
    'total': '总咨询数'
  }
  return map[key]
}

onMounted(() => {
  fetchConsultations()
  fetchStatistics()
})
</script>

<style scoped lang="scss">
.customer-service-container {
  padding: 20px;
  
  .statistics-cards {
    margin: 20px 0;
    
    .stat-item {
      display: flex;
      align-items: center;
      gap: 15px;
      
      .stat-icon {
        width: 50px;
        height: 50px;
        border-radius: 10px;
        display: flex;
        align-items: center;
        justify-content: center;
        font-size: 24px;
        color: white;
        
        &.stat-pending {
          background: linear-gradient(135deg, #ffc107 0%, #ff9800 100%);
        }
        
        &.stat-processing {
          background: linear-gradient(135deg, #2196f3 0%, #1976d2 100%);
        }
        
        &.stat-replied {
          background: linear-gradient(135deg, #4caf50 0%, #388e3c 100%);
        }
        
        &.stat-closed {
          background: linear-gradient(135deg, #9e9e9e 0%, #757575 100%);
        }
        
        &.stat-total {
          background: linear-gradient(135deg, #673ab7 0%, #512da8 100%);
        }
      }
      
      .stat-content {
        flex: 1;
        
        .stat-value {
          font-size: 24px;
          font-weight: 600;
          color: #303133;
        }
        
        .stat-label {
          font-size: 14px;
          color: #909399;
          margin-top: 4px;
        }
      }
    }
  }
  
  .filter-card {
    margin-bottom: 20px;
  }
  
  .consultation-info {
    .subject {
      font-weight: 600;
      color: #303133;
      margin-bottom: 4px;
    }
    
    .content {
      font-size: 13px;
      color: #909399;
      overflow: hidden;
      text-overflow: ellipsis;
      display: -webkit-box;
      -webkit-line-clamp: 2;
      line-clamp: 2;
      -webkit-box-orient: vertical;
    }
  }
  
  .reply-info {
    .reply-content {
      font-size: 13px;
      color: #606266;
      margin-bottom: 4px;
    }
    
    .reply-meta {
      font-size: 12px;
      color: #909399;
    }
  }
  
  .pagination {
    margin-top: 20px;
    display: flex;
    justify-content: flex-end;
  }
  
  .consultation-detail {
    .detail-row {
      margin-bottom: 12px;
      display: flex;
      
      .label {
        font-weight: 600;
        color: #606266;
        width: 90px;
        flex-shrink: 0;
      }
      
      .content-box {
        flex: 1;
        padding: 12px;
        background: #f5f7fa;
        border-radius: 4px;
        line-height: 1.6;
        color: #606266;
      }
    }
  }
}

:deep(.el-page-header) {
  margin-bottom: 20px;
}
</style>










