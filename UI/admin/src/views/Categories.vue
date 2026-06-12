<template>
  <div class="categories-page">
    <!-- 搜索栏 -->
    <el-card class="search-card">
      <el-form :inline="true" class="search-form">
        <el-form-item>
          <el-input
            v-model="searchKeyword"
            placeholder="搜索分类名称"
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
          <el-button type="success" @click="showDialog()">
            <el-icon><Plus /></el-icon>
            添加分类
          </el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <!-- 数据表格 -->
    <el-card class="table-card">
      <template #header>
        <div class="card-header">
          <div class="header-left">
            <el-icon class="title-icon"><Folder /></el-icon>
            <span class="title">分类列表</span>
            <el-tag class="count-tag">共 {{ total }} 个</el-tag>
          </div>
        </div>
      </template>

      <el-table
        :data="categories"
        v-loading="loading"
        style="width: 100%"
        :header-cell-style="{ background: '#f5f7fa', color: '#606266' }"
        stripe
      >
        <el-table-column prop="id" label="ID" width="80" align="center"></el-table-column>
        <el-table-column prop="name" label="分类名称" min-width="150">
          <template #default="{ row }">
            <div class="category-name">
              <el-icon class="category-icon"><Folder /></el-icon>
              <span>{{ row.name }}</span>
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="description" label="描述" min-width="200">
          <template #default="{ row }">
            <span>{{ row.description || '-' }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="sortOrder" label="排序" width="100" align="center">
          <template #default="{ row }">
            <el-tag type="info" effect="plain" size="small">{{ row.sortOrder }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="100" align="center">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'danger'" effect="plain" size="small">
              {{ row.status === 1 ? '启用' : '禁用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" width="150">
          <template #default="{ row }">
            <div class="time-info">
              <el-icon><Clock /></el-icon>
              <span>{{ formatTime(row.createTime) }}</span>
            </div>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="250" align="center">
          <template #default="{ row }">
            <div class="action-buttons">
              <el-button size="small" type="primary" @click="showDialog(row)">
                <el-icon><Edit /></el-icon>
                编辑
              </el-button>
              <el-button size="small" :type="row.status === 1 ? 'warning' : 'success'" @click="toggleStatus(row)">
                {{ row.status === 1 ? '禁用' : '启用' }}
              </el-button>
              <el-button size="small" type="danger" @click="deleteCategory(row.id)">
                <el-icon><Delete /></el-icon>
                删除
              </el-button>
            </div>
          </template>
        </el-table-column>
      </el-table>

      <!-- 空状态 -->
      <el-empty v-if="!loading && categories.length === 0" description="暂无分类数据">
        <el-button type="primary" @click="showDialog()">添加第一个分类</el-button>
      </el-empty>
    </el-card>

    <!-- 添加/编辑对话框 -->
    <el-dialog v-model="dialogVisible" :title="form.id ? '编辑分类' : '添加分类'" width="600px">
      <el-form :model="form" :rules="formRules" ref="formRef" label-width="80px">
        <el-form-item label="名称" prop="name">
          <el-input v-model="form.name" placeholder="请输入分类名称"></el-input>
        </el-form-item>
        <el-form-item label="描述" prop="description">
          <el-input
            v-model="form.description"
            type="textarea"
            :rows="3"
            placeholder="请输入分类描述"
          ></el-input>
        </el-form-item>
        <el-form-item label="排序" prop="sortOrder">
          <el-input-number v-model="form.sortOrder" :min="0" :max="9999"></el-input-number>
          <span style="margin-left: 10px; color: #909399; font-size: 12px;">数字越小越靠前</span>
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-radio-group v-model="form.status">
            <el-radio :label="1">启用</el-radio>
            <el-radio :label="0">禁用</el-radio>
          </el-radio-group>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit" :loading="submitting">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
// ============================================================
// 代码贡献统计（项目总计 3000 行）
// 文件: UI/admin/src/views/Categories.vue
// hangu: 0 行 | kelei: 0 行 | 本文件合计: 0 行
// ============================================================
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Search, Folder, Edit, Delete, Clock, Plus } from '@element-plus/icons-vue'
import request from '@/utils/request'

const categories = ref([])
const total = ref(0)
const loading = ref(false)
const submitting = ref(false)
const dialogVisible = ref(false)
const searchKeyword = ref('')
const formRef = ref(null)

const form = ref({
  id: null,
  name: '',
  description: '',
  sortOrder: 0,
  status: 1
})

const formRules = {
  name: [
    { required: true, message: '请输入分类名称', trigger: 'blur' },
    { min: 2, max: 20, message: '长度在 2 到 20 个字符', trigger: 'blur' }
  ],
  description: [
    { max: 200, message: '描述不能超过 200 个字符', trigger: 'blur' }
  ]
}

const fetchCategories = async () => {
  loading.value = true
  try {
    const params = {}
    if (searchKeyword.value) {
      params.keyword = searchKeyword.value
    }
    
    const res = await request.get('/categories/all', { params })
    categories.value = res.data || []
    total.value = categories.value.length
  } catch (error) {
    console.error('获取分类列表错误：', error)
    ElMessage.error(error.response?.data?.message || '获取分类列表失败')
  } finally {
    loading.value = false
  }
}

const handleSearch = () => {
  fetchCategories()
}

const handleReset = () => {
  searchKeyword.value = ''
  fetchCategories()
}

const showDialog = (category = null) => {
  if (category) {
    form.value = { ...category }
  } else {
    form.value = { id: null, name: '', description: '', sortOrder: 0, status: 1 }
  }
  dialogVisible.value = true
}

const handleSubmit = async () => {
  try {
    await formRef.value.validate()
    submitting.value = true
    
    if (form.value.id) {
      await request.put(`/categories/${form.value.id}`, form.value)
      ElMessage.success('更新成功')
    } else {
      await request.post('/categories', form.value)
      ElMessage.success('添加成功')
    }
    
    dialogVisible.value = false
    fetchCategories()
  } catch (error) {
    if (error !== false) {
      console.error(error)
      ElMessage.error(error.response?.data?.message || '操作失败')
    }
  } finally {
    submitting.value = false
  }
}

const toggleStatus = async (category) => {
  try {
    const newStatus = category.status === 1 ? 0 : 1
    const action = newStatus === 0 ? '禁用' : '启用'
    
    await ElMessageBox.confirm(`确定要${action}该分类吗？`, '提示', {
      type: 'warning',
      confirmButtonText: '确定',
      cancelButtonText: '取消'
    })
    
    await request.put(`/categories/${category.id}`, {
      ...category,
      status: newStatus
    })
    
    ElMessage.success(`${action}成功`)
    fetchCategories()
  } catch (error) {
    if (error !== 'cancel') {
      console.error(error)
      ElMessage.error('操作失败')
    }
  }
}

const deleteCategory = async (id) => {
  try {
    await ElMessageBox.confirm('确定要删除该分类吗？此操作不可恢复！', '警告', {
      type: 'error',
      confirmButtonText: '确定删除',
      cancelButtonText: '取消'
    })
    
    await request.delete(`/categories/${id}`)
    ElMessage.success('删除成功')
    fetchCategories()
  } catch (error) {
    if (error !== 'cancel') {
      console.error(error)
      ElMessage.error(error.response?.data?.message || '删除失败')
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
  fetchCategories()
})
</script>

<style scoped>
.categories-page {
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

/* 分类名称样式 */
.category-name {
  display: flex;
  align-items: center;
  gap: 8px;
}

.category-icon {
  font-size: 16px;
  color: #ff9f43;
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

/* 按钮优化 */
.search-card :deep(.el-button--primary) {
  background: linear-gradient(135deg, #ff6b00 0%, #ff9f43 100%);
  border: none;
}

.search-card :deep(.el-button--primary:hover) {
  opacity: 0.9;
  transform: translateY(-1px);
  box-shadow: 0 4px 12px rgba(255, 107, 0, 0.3);
}

.search-card :deep(.el-button--success) {
  background: linear-gradient(135deg, #67c23a 0%, #85ce61 100%);
  border: none;
}

.search-card :deep(.el-button--success:hover) {
  opacity: 0.9;
  transform: translateY(-1px);
  box-shadow: 0 4px 12px rgba(103, 194, 58, 0.3);
}

/* 操作按钮容器 */
.action-buttons {
  display: flex;
  justify-content: center;
  align-items: center;
  gap: 6px;
  flex-wrap: nowrap;
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
  background-color: #67c23a;
  border-color: #67c23a;
}

.table-card :deep(.el-button--success:hover) {
  background-color: #85ce61;
  border-color: #85ce61;
}

.table-card :deep(.el-button--warning) {
  background-color: #e6a23c;
  border-color: #e6a23c;
}

.table-card :deep(.el-button--warning:hover) {
  background-color: #ebb563;
  border-color: #ebb563;
}

.table-card :deep(.el-button--danger) {
  background-color: #f56c6c;
  border-color: #f56c6c;
}

.table-card :deep(.el-button--danger:hover) {
  background-color: #f78989;
  border-color: #f78989;
}

/* 对话框内按钮 */
.el-dialog :deep(.el-button--primary) {
  background: linear-gradient(135deg, #ff6b00 0%, #ff9f43 100%);
  border: none;
}

.el-dialog :deep(.el-button--primary:hover) {
  opacity: 0.9;
  transform: translateY(-1px);
  box-shadow: 0 4px 12px rgba(255, 107, 0, 0.3);
}
</style>










