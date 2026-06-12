<template>
  <div class="logs-container">
    <!-- 搜索筛选 -->
    <el-card class="search-card">
      <el-form :inline="true" :model="searchForm" class="search-form">
        <el-form-item label="操作类型">
          <el-select v-model="searchForm.operationType" placeholder="全部" clearable style="width: 120px">
            <el-option label="全部" value=""></el-option>
            <el-option label="新增" value="CREATE"></el-option>
            <el-option label="修改" value="UPDATE"></el-option>
            <el-option label="删除" value="DELETE"></el-option>
            <el-option label="查询" value="QUERY"></el-option>
            <el-option label="登录" value="LOGIN"></el-option>
          </el-select>
        </el-form-item>
        
        <el-form-item label="操作模块">
          <el-select v-model="searchForm.module" placeholder="全部" clearable style="width: 120px">
            <el-option label="全部" value=""></el-option>
            <el-option label="用户" value="USER"></el-option>
            <el-option label="商品" value="PRODUCT"></el-option>
            <el-option label="订单" value="ORDER"></el-option>
            <el-option label="评论" value="COMMENT"></el-option>
            <el-option label="举报" value="REPORT"></el-option>
            <el-option label="公告" value="ANNOUNCEMENT"></el-option>
            <el-option label="轮播图" value="BANNER"></el-option>
            <el-option label="系统" value="SYSTEM"></el-option>
          </el-select>
        </el-form-item>
        
        <el-form-item label="管理员">
          <el-input v-model="searchForm.adminName" placeholder="管理员名称" clearable style="width: 150px" />
        </el-form-item>
        
        <el-form-item label="关键词">
          <el-input v-model="searchForm.keyword" placeholder="搜索操作内容" clearable style="width: 200px" />
        </el-form-item>
        
        <el-form-item>
          <el-button type="primary" :icon="Search" @click="handleSearch">搜索</el-button>
          <el-button :icon="Refresh" @click="handleReset">重置</el-button>
          <el-dropdown @command="handleCleanCommand">
            <el-button type="danger" :icon="Delete">
              清理日志<el-icon class="el-icon--right"><ArrowDown /></el-icon>
            </el-button>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item command="clean-7">清理7天前</el-dropdown-item>
                <el-dropdown-item command="clean-30">清理30天前</el-dropdown-item>
                <el-dropdown-item command="clean-all" divided>清空所有日志</el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </el-form-item>
      </el-form>
    </el-card>

    <!-- 日志列表 -->
    <el-card class="table-card">
      <template #header>
        <div class="card-header">
          <span class="title">
            <el-icon><DocumentCopy /></el-icon>
            操作日志列表
          </span>
          <span class="total">共 {{ total }} 条记录</span>
        </div>
      </template>
      
      <el-table 
        :data="logList" 
        v-loading="loading"
        style="width: 100%"
        :height="tableHeight"
      >
        <el-table-column prop="id" label="ID" width="80" align="center" />
        
        <el-table-column prop="adminName" label="管理员" width="120" align="center">
          <template #default="{ row }">
            <el-tag type="info" size="small">{{ row.adminName }}</el-tag>
          </template>
        </el-table-column>
        
        <el-table-column prop="operationType" label="操作类型" width="100" align="center">
          <template #default="{ row }">
            <el-tag :type="getOperationTypeTag(row.operationType)" size="small">
              {{ getOperationTypeText(row.operationType) }}
            </el-tag>
          </template>
        </el-table-column>
        
        <el-table-column prop="module" label="模块" width="100" align="center">
          <template #default="{ row }">
            <el-tag type="primary" effect="plain" size="small">
              {{ getModuleText(row.module) }}
            </el-tag>
          </template>
        </el-table-column>
        
        <el-table-column prop="content" label="操作内容" min-width="300">
          <template #default="{ row }">
            <span class="content-text">{{ row.content }}</span>
          </template>
        </el-table-column>
        
        <el-table-column prop="ip" label="IP地址" width="140" align="center" />
        
        <el-table-column prop="createTime" label="操作时间" width="180" align="center">
          <template #default="{ row }">
            {{ formatTime(row.createTime) }}
          </template>
        </el-table-column>
        
        <el-table-column label="操作" width="150" align="center" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link size="small" @click="handleView(row)">
              <el-icon><View /></el-icon>
              详情
            </el-button>
            <el-button type="danger" link size="small" @click="handleDelete(row.id)">
              <el-icon><Delete /></el-icon>
              删除
            </el-button>
          </template>
        </el-table-column>
      </el-table>
      
      <!-- 分页 -->
      <div class="pagination">
        <el-pagination
          v-model:current-page="searchForm.page"
          v-model:page-size="searchForm.size"
          :page-sizes="[10, 20, 50, 100]"
          :total="total"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="fetchLogs"
          @current-change="fetchLogs"
        />
      </div>
    </el-card>

    <!-- 详情对话框 -->
    <el-dialog v-model="dialogVisible" title="日志详情" width="600px">
      <el-descriptions :column="1" border v-if="currentLog">
        <el-descriptions-item label="ID">{{ currentLog.id }}</el-descriptions-item>
        <el-descriptions-item label="管理员">{{ currentLog.adminName }}</el-descriptions-item>
        <el-descriptions-item label="操作类型">
          <el-tag :type="getOperationTypeTag(currentLog.operationType)">
            {{ getOperationTypeText(currentLog.operationType) }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="操作模块">{{ getModuleText(currentLog.module) }}</el-descriptions-item>
        <el-descriptions-item label="操作内容">{{ currentLog.content }}</el-descriptions-item>
        <el-descriptions-item label="IP地址">{{ currentLog.ip }}</el-descriptions-item>
        <el-descriptions-item label="浏览器">{{ currentLog.browser || '-' }}</el-descriptions-item>
        <el-descriptions-item label="操作系统">{{ currentLog.os || '-' }}</el-descriptions-item>
        <el-descriptions-item label="操作时间">{{ formatTime(currentLog.createTime) }}</el-descriptions-item>
      </el-descriptions>
    </el-dialog>
  </div>
</template>

<script setup>
// ============================================================
// 代码贡献统计（项目总计 3000 行）
// 文件: UI/admin/src/views/Logs.vue
// hangu: 35 行 | kelei: 0 行 | 本文件合计: 35 行
// ============================================================
import { ref, onMounted } from 'vue'
import request from '@/utils/request'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Search, Refresh, Delete, DocumentCopy, View, ArrowDown } from '@element-plus/icons-vue'

const loading = ref(false)
const logList = ref([])
const total = ref(0)
const tableHeight = ref(600)
const dialogVisible = ref(false)
const currentLog = ref(null)

const searchForm = ref({
  page: 1,
  size: 20,
  operationType: '',
  module: '',
  adminName: '',
  keyword: ''
})

// 获取日志列表
const fetchLogs = async () => {
  loading.value = true
  try {
    const res = await request.get('/logs', { params: searchForm.value })
    logList.value = res.data.records || []
    total.value = res.data.total || 0
  } catch (error) {
    console.error('获取日志失败:', error)
    ElMessage.error('获取日志失败')
  } finally {
    loading.value = false
  }
}

// 搜索
const handleSearch = () => {
  searchForm.value.page = 1
  fetchLogs()
}

// 重置
const handleReset = () => {
  searchForm.value = {
    page: 1,
    size: 20,
    operationType: '',
    module: '',
    adminName: '',
    keyword: ''
  }
  fetchLogs()
}

// 查看详情
const handleView = (row) => {
  currentLog.value = row
  dialogVisible.value = true
}

// 删除日志
const handleDelete = async (id) => {
  try {
    await ElMessageBox.confirm('确定要删除该日志吗？', '提示', { type: 'warning' })
    await request.delete(`/logs/batch`, { data: [id] })
    ElMessage.success('删除成功')
    fetchLogs()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('删除失败')
    }
  }
}

// 清理日志命令处理
const handleCleanCommand = async (command) => {
  let days = 30
  let message = '确定要清理30天前的日志吗？'
  let url = '/logs/clean'
  if (command === 'clean-7') {
    // @contributor hangu - handleCleanCommand (35 行)
    days = 7
    message = '确定要清理7天前的日志吗？'
    url = '/logs/clean?days=7'
  } else if (command === 'clean-30') {
    days = 30
    message = '确定要清理30天前的日志吗？'
    url = '/logs/clean?days=30'
  } else if (command === 'clean-all') {
    message = '⚠️ 确定要清空所有日志吗？此操作不可恢复！'
    url = '/logs/clean-all'
  }
  
  try {
    await ElMessageBox.confirm(message, '提示', { 
      type: 'warning',
      confirmButtonText: '确定清理',
      cancelButtonText: '取消'
    })
    const res = await request.delete(url)
    ElMessage.success(res.data || res.message || '清理成功')
    fetchLogs()
  } catch (error) {
    if (error !== 'cancel') {
      console.error('清理失败:', error)
      ElMessage.error('清理失败')
    }
  }
}

// 获取操作类型标签
const getOperationTypeTag = (type) => {
  const map = {
    'CREATE': 'success',
    'UPDATE': 'warning',
    'DELETE': 'danger',
    'QUERY': 'info',
    'LOGIN': 'primary'
  }
  return map[type] || 'info'
}

// 获取操作类型文本
const getOperationTypeText = (type) => {
  const map = {
    'CREATE': '新增',
    'UPDATE': '修改',
    'DELETE': '删除',
    'QUERY': '查询',
    'LOGIN': '登录'
  }
  return map[type] || type
}

// 获取模块文本
const getModuleText = (module) => {
  const map = {
    'USER': '用户',
    'PRODUCT': '商品',
    'ORDER': '订单',
    'COMMENT': '评论',
    'REPORT': '举报',
    'ANNOUNCEMENT': '公告',
    'BANNER': '轮播图',
    'CATEGORY': '分类',
    'NEWS': '资讯',
    'SYSTEM': '系统',
    'LOG': '日志'
  }
  return map[module] || module
}

// 格式化时间
const formatTime = (timeStr) => {
  if (!timeStr) return '-'
  return new Date(timeStr).toLocaleString('zh-CN')
}

onMounted(() => {
  fetchLogs()
  // 计算表格高度
  tableHeight.value = window.innerHeight - 320
})
</script>

<style scoped>
.logs-container {
  padding: 20px;
}

.search-card {
  margin-bottom: 20px;
}

.search-form {
  margin-bottom: 0;
}

.table-card {
  min-height: 500px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.card-header .title {
  font-size: 16px;
  font-weight: 600;
  display: flex;
  align-items: center;
  gap: 8px;
}

.card-header .total {
  font-size: 14px;
  color: #999;
}

.content-text {
  color: #333;
  line-height: 1.5;
}

.pagination {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}
</style>










