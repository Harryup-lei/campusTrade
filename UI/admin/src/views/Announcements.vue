<template>
  <div class="announcements-container">
    <el-card>
      <template #header>
        <div class="card-header">
          <span class="title">平台公告管理</span>
          <el-button type="primary" @click="handleAdd">
            <el-icon><Plus /></el-icon>
            新增公告
          </el-button>
        </div>
      </template>

      <!-- 筛选栏 -->
      <div class="filter-bar">
        <el-select v-model="filterStatus" placeholder="状态筛选" clearable style="width: 150px" @change="fetchAnnouncements">
          <el-option label="全部" value="" />
          <el-option label="启用" value="ACTIVE" />
          <el-option label="停用" value="INACTIVE" />
        </el-select>
        <el-select v-model="filterType" placeholder="类型筛选" clearable style="width: 150px" @change="fetchAnnouncements">
          <el-option label="全部" value="" />
          <el-option label="热门" value="HOT" />
          <el-option label="最新" value="NEW" />
          <el-option label="普通" value="NORMAL" />
        </el-select>
        <el-button type="primary" @click="fetchAnnouncements">
          <el-icon><Search /></el-icon>
          查询
        </el-button>
      </div>

      <!-- 公告列表 -->
      <el-table :data="announcements" v-loading="loading" stripe>
        <el-table-column prop="id" label="ID" width="80" />
        
        <el-table-column prop="title" label="标题" min-width="200" show-overflow-tooltip />
        
        <el-table-column label="类型" width="100">
          <template #default="{ row }">
            <el-tag v-if="row.type === 'HOT'" type="danger" size="small">
              <el-icon><HotWater /></el-icon> 热门
            </el-tag>
            <el-tag v-else-if="row.type === 'NEW'" type="success" size="small">
              <el-icon><Star /></el-icon> 最新
            </el-tag>
            <el-tag v-else type="info" size="small">普通</el-tag>
          </template>
        </el-table-column>
        
        <el-table-column prop="priority" label="优先级" width="100" sortable>
          <template #default="{ row }">
            <el-tag type="warning">{{ row.priority }}</el-tag>
          </template>
        </el-table-column>
        
        <el-table-column label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="row.status === 'ACTIVE' ? 'success' : 'info'">
              {{ row.status === 'ACTIVE' ? '启用' : '停用' }}
            </el-tag>
          </template>
        </el-table-column>
        
        <el-table-column prop="viewCount" label="浏览次数" width="120" sortable>
          <template #default="{ row }">
            <el-text type="primary">
              <el-icon><View /></el-icon> {{ row.viewCount || 0 }}
            </el-text>
          </template>
        </el-table-column>
        
        <el-table-column label="有效期" width="200" show-overflow-tooltip>
          <template #default="{ row }">
            <div v-if="row.startTime || row.endTime" class="time-range">
              <div v-if="row.startTime">
                <el-icon><Calendar /></el-icon> {{ formatDate(row.startTime) }}
              </div>
              <div v-if="row.endTime">
                <el-icon><Calendar /></el-icon> {{ formatDate(row.endTime) }}
              </div>
            </div>
            <span v-else class="text-muted">永久有效</span>
          </template>
        </el-table-column>
        
        <el-table-column label="创建时间" width="180">
          <template #default="{ row }">
            {{ formatDate(row.createTime) }}
          </template>
        </el-table-column>
        
        <el-table-column label="操作" width="280" fixed="right">
          <template #default="{ row }">
            <el-button 
              size="small" 
              :type="row.status === 'ACTIVE' ? 'warning' : 'success'"
              @click="toggleStatus(row)"
            >
              {{ row.status === 'ACTIVE' ? '停用' : '启用' }}
            </el-button>
            <el-button size="small" type="primary" @click="handleEdit(row)">
              编辑
            </el-button>
            <el-popconfirm 
              title="确定删除这条公告吗？" 
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
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
        />
      </div>
    </el-card>

    <!-- 新增/编辑对话框 -->
    <el-dialog 
      v-model="dialogVisible" 
      :title="dialogTitle"
      width="700px"
      :close-on-click-modal="false"
    >
      <el-form :model="formData" :rules="rules" ref="formRef" label-width="100px">
        <el-form-item label="公告标题" prop="title">
          <el-input v-model="formData.title" placeholder="请输入公告标题" maxlength="200" show-word-limit />
        </el-form-item>
        
        <el-form-item label="公告内容" prop="content">
          <el-input 
            v-model="formData.content" 
            type="textarea" 
            :rows="5"
            placeholder="请输入公告内容"
            maxlength="1000"
            show-word-limit
          />
        </el-form-item>
        
        <el-form-item label="公告类型" prop="type">
          <el-radio-group v-model="formData.type">
            <el-radio label="HOT">
              <el-icon color="#f56c6c"><HotWater /></el-icon> 热门
            </el-radio>
            <el-radio label="NEW">
              <el-icon color="#67c23a"><Star /></el-icon> 最新
            </el-radio>
            <el-radio label="NORMAL">普通</el-radio>
          </el-radio-group>
        </el-form-item>
        
        <el-form-item label="优先级" prop="priority">
          <el-input-number v-model="formData.priority" :min="0" :max="999" />
          <span class="tip">数字越大越靠前</span>
        </el-form-item>
        
        <el-form-item label="跳转链接">
          <el-input v-model="formData.linkUrl" placeholder="请输入跳转链接（选填）" />
          <span class="tip">可选，点击公告时跳转的链接</span>
        </el-form-item>
        
        <el-form-item label="状态" prop="status">
          <el-radio-group v-model="formData.status">
            <el-radio label="ACTIVE">启用</el-radio>
            <el-radio label="INACTIVE">停用</el-radio>
          </el-radio-group>
        </el-form-item>
        
        <el-form-item label="有效期">
          <el-date-picker
            v-model="timeRange"
            type="datetimerange"
            range-separator="至"
            start-placeholder="开始时间"
            end-placeholder="结束时间"
            format="YYYY-MM-DD HH:mm"
            value-format="YYYY-MM-DDTHH:mm:ss"
          />
          <span class="tip">可选，不设置则永久有效</span>
        </el-form-item>
      </el-form>
      
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit" :loading="submitting">
          确定
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
// ============================================================
// 代码贡献统计（项目总计 3000 行）
// 文件: UI/admin/src/views/Announcements.vue
// hangu: 0 行 | kelei: 34 行 | 本文件合计: 34 行
// ============================================================
import { ref, reactive, onMounted, computed } from 'vue'
import { ElMessage } from 'element-plus'
import { Plus, Search, HotWater, Star, View, Calendar } from '@element-plus/icons-vue'
import request from '@/utils/request'

const loading = ref(false)
const announcements = ref([])
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)
const filterStatus = ref('')
const filterType = ref('')

const dialogVisible = ref(false)
const dialogTitle = computed(() => formData.id ? '编辑公告' : '新增公告')
const formRef = ref(null)
const submitting = ref(false)
const timeRange = ref(null)

const formData = reactive({
  id: null,
  title: '',
  content: '',
  type: 'NORMAL',
  priority: 0,
  linkUrl: '',
  status: 'ACTIVE',
  startTime: null,
  endTime: null
})

const rules = {
  title: [{ required: true, message: '请输入公告标题', trigger: 'blur' }],
  content: [{ required: true, message: '请输入公告内容', trigger: 'blur' }],
  type: [{ required: true, message: '请选择公告类型', trigger: 'change' }],
  priority: [{ required: true, message: '请输入优先级', trigger: 'blur' }],
  status: [{ required: true, message: '请选择状态', trigger: 'change' }]
}

// 获取公告列表
const fetchAnnouncements = async () => {
  loading.value = true
  try {
    const res = await request.get('/admin/announcements', {
      params: {
        page: currentPage.value - 1,
        size: pageSize.value,
        status: filterStatus.value,
        type: filterType.value
      }
    })
    if (res && res.data) {
      announcements.value = res.data.records || res.data.content || []
      total.value = res.data.total || res.data.totalElements || 0
    }
  } catch (error) {
    console.error('获取公告失败:', error)
    ElMessage.error('获取公告列表失败')
  } finally {
    loading.value = false
  }
}

// 切换状态
const toggleStatus = async (row) => {
  const newStatus = row.status === 'ACTIVE' ? 'INACTIVE' : 'ACTIVE'
  const statusText = newStatus === 'ACTIVE' ? '启用' : '停用'
  
  try {
    const res = await request.put(`/admin/announcements/${row.id}/status`, null, {
      params: { status: newStatus }
    })
    if (res && res.code === 200) {
      ElMessage.success(`${statusText}成功`)
      fetchAnnouncements()
    }
  } catch (error) {
    ElMessage.error(`${statusText}失败`)
  }
}

// 新增
const handleAdd = () => {
  resetForm()
  dialogVisible.value = true
}

// 编辑
const handleEdit = (row) => {
  Object.assign(formData, {
    id: row.id,
    title: row.title,
    content: row.content,
    type: row.type,
    priority: row.priority,
    linkUrl: row.linkUrl,
    status: row.status,
    startTime: row.startTime,
    endTime: row.endTime
  })
  
  if (row.startTime && row.endTime) {
    timeRange.value = [row.startTime, row.endTime]
  } else {
    timeRange.value = null
  }
  
  dialogVisible.value = true
}

// 删除
const handleDelete = async (id) => {
  try {
    const res = await request.delete(`/admin/announcements/${id}`)
    if (res && res.code === 200) {
      ElMessage.success('删除成功')
      fetchAnnouncements()
    }
  } catch (error) {
    ElMessage.error('删除失败')
  }
}

// 提交
const handleSubmit = async () => {
  if (!formRef.value) return
  
  await formRef.value.validate(async (valid) => {
    if (!valid) return
    // @contributor kelei - handleSubmit (34 行)
    
    submitting.value = true
    // 处理时间范围
    if (timeRange.value && timeRange.value.length === 2) {
      formData.startTime = timeRange.value[0]
      formData.endTime = timeRange.value[1]
    } else {
      formData.startTime = null
      formData.endTime = null
    }
    
    try {
      const url = formData.id ? `/admin/announcements/${formData.id}` : '/admin/announcements'
      const method = formData.id ? 'put' : 'post'
      const res = await request[method](url, formData)
      
      if (res && res.code === 200) {
        ElMessage.success(formData.id ? '更新成功' : '添加成功')
        dialogVisible.value = false
        fetchAnnouncements()
      }
    } catch (error) {
      ElMessage.error(formData.id ? '更新失败' : '添加失败')
    } finally {
      submitting.value = false
    }
  })
}

// 重置表单
const resetForm = () => {
  Object.assign(formData, {
    id: null,
    title: '',
    content: '',
    type: 'NORMAL',
    priority: 0,
    linkUrl: '',
    status: 'ACTIVE',
    startTime: null,
    endTime: null
  })
  timeRange.value = null
  formRef.value?.resetFields()
}

// 分页处理
const handleSizeChange = () => {
  currentPage.value = 1
  fetchAnnouncements()
}

const handleCurrentChange = () => {
  fetchAnnouncements()
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
  fetchAnnouncements()
})
</script>

<style scoped>
.announcements-container {
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

.text-muted {
  color: #909399;
  font-size: 13px;
}

.tip {
  margin-left: 10px;
  font-size: 12px;
  color: #909399;
}

.time-range {
  font-size: 12px;
  color: #606266;
}

.time-range > div {
  display: flex;
  align-items: center;
  gap: 4px;
  margin-bottom: 4px;
}

.time-range .el-icon {
  font-size: 14px;
}
</style>










