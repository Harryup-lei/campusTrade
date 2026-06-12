<template>
  <div class="categories-page">
    <!-- 搜索栏 -->
    <el-card class="search-card">
      <el-button type="primary" @click="showDialog()">
        <el-icon><Plus /></el-icon>
        新增分类
      </el-button>
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
        :data="categoryList"
        v-loading="loading"
        style="width: 100%"
        :header-cell-style="{ background: '#f5f7fa', color: '#606266' }"
        stripe
      >
        <el-table-column prop="id" label="ID" width="80" align="center"></el-table-column>
        <el-table-column prop="name" label="分类名称" min-width="150"></el-table-column>
        <el-table-column prop="description" label="描述" min-width="200"></el-table-column>
        <el-table-column prop="sortOrder" label="排序" width="100" align="center"></el-table-column>
        <el-table-column prop="status" label="状态" width="100" align="center">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'info'" effect="plain" size="small">
              {{ row.status === 1 ? '启用' : '禁用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="280" align="center" fixed="right">
          <template #default="{ row }">
            <div class="action-buttons">
              <el-button size="small" type="primary" @click="showDialog(row)">
                <el-icon><Edit /></el-icon>
                编辑
              </el-button>
              <el-button 
                size="small" 
                :type="row.status === 1 ? 'warning' : 'success'" 
                @click="toggleStatus(row)"
              >
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
      <el-empty v-if="!loading && categoryList.length === 0" description="暂无分类数据">
        <el-button type="primary" @click="fetchCategories">刷新</el-button>
      </el-empty>

      <!-- 分页 -->
      <div class="pagination" v-if="categoryList.length > 0">
        <el-pagination
          v-model:current-page="page"
          v-model:page-size="size"
          :total="total"
          :page-sizes="[10, 20, 50, 100]"
          layout="total, sizes, prev, pager, next, jumper"
          @current-change="fetchCategories"
          @size-change="fetchCategories"
        />
      </div>
    </el-card>

    <!-- 新增/编辑对话框 -->
    <el-dialog v-model="dialogVisible" :title="form.id ? '编辑分类' : '新增分类'" width="600px">
      <el-form :model="form" label-width="100px" :rules="rules" ref="formRef">
        <el-form-item label="分类名称" prop="name">
          <el-input v-model="form.name" placeholder="请输入分类名称"></el-input>
        </el-form-item>
        <el-form-item label="分类描述" prop="description">
          <el-input 
            v-model="form.description" 
            type="textarea" 
            :rows="3" 
            placeholder="请输入分类描述"
          ></el-input>
        </el-form-item>
        <el-form-item label="排序序号" prop="sortOrder">
          <el-input-number v-model="form.sortOrder" :min="0" :max="999"></el-input-number>
          <span style="margin-left: 10px; font-size: 12px; color: #999;">数字越小越靠前</span>
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
        <el-button type="primary" @click="handleSubmit">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
// ============================================================
// 代码贡献统计（项目总计 3000 行）
// 文件: UI/admin/src/views/NewsCategories.vue
// hangu: 0 行 | kelei: 0 行 | 本文件合计: 0 行
// ============================================================
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus, Folder, Edit, Delete } from '@element-plus/icons-vue'
import request from '@/utils/request'

const categoryList = ref([])
const page = ref(1)
const size = ref(10)
const total = ref(0)
const loading = ref(false)
const dialogVisible = ref(false)
const formRef = ref(null)
const form = ref({
  id: null,
  name: '',
  description: '',
  sortOrder: 0,
  status: 1
})

const rules = {
  name: [
    { required: true, message: '请输入分类名称', trigger: 'blur' },
    { min: 2, max: 20, message: '长度在 2 到 20 个字符', trigger: 'blur' }
  ],
  sortOrder: [
    { required: true, message: '请输入排序序号', trigger: 'blur' }
  ]
}

const fetchCategories = async () => {
  loading.value = true
  try {
    const res = await request.get('/news-categories', {
      params: { page: page.value - 1, size: size.value }
    })
    categoryList.value = res.data?.records || res.data?.content || []
    total.value = res.data?.total || res.data?.totalElements || 0
  } catch (error) {
    console.error('获取分类失败:', error)
    ElMessage.error('获取分类列表失败')
  } finally {
    loading.value = false
  }
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
    
    if (form.value.id) {
      await request.put(`/news-categories/${form.value.id}`, form.value)
      ElMessage.success('更新成功')
    } else {
      await request.post('/news-categories', form.value)
      ElMessage.success('创建成功')
    }
    
    dialogVisible.value = false
    fetchCategories()
  } catch (error) {
    if (error !== 'cancel') {
      console.error('提交失败:', error)
      ElMessage.error(error.response?.data?.message || '操作失败')
    }
  }
}

const toggleStatus = async (category) => {
  const newStatus = category.status === 1 ? 0 : 1
  const action = newStatus === 1 ? '启用' : '禁用'
  
  try {
    await ElMessageBox.confirm(`确定要${action}该分类吗？`, '提示', {
      type: 'warning',
      confirmButtonText: '确定',
      cancelButtonText: '取消'
    })
    
    await request.put(`/news-categories/${category.id}/status`, null, {
      params: { status: newStatus }
    })
    
    ElMessage.success(`${action}成功`)
    fetchCategories()
  } catch (error) {
    if (error !== 'cancel') {
      console.error('更新状态失败:', error)
      ElMessage.error('操作失败')
    }
  }
}

const deleteCategory = async (id) => {
  try {
    await ElMessageBox.confirm('确定要删除该分类吗？删除后无法恢复！', '提示', {
      type: 'warning',
      confirmButtonText: '确定',
      cancelButtonText: '取消'
    })
    
    await request.delete(`/news-categories/${id}`)
    ElMessage.success('删除成功')
    fetchCategories()
  } catch (error) {
    if (error !== 'cancel') {
      console.error('删除失败:', error)
      ElMessage.error(error.response?.data?.message || '删除失败')
    }
  }
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

.table-card :deep(.el-button--warning) {
  background-color: #e6a23c;
  border-color: #e6a23c;
}

.table-card :deep(.el-button--warning:hover) {
  background-color: #ebb563;
  border-color: #ebb563;
}

.table-card :deep(.el-button--success) {
  background-color: #67c23a;
  border-color: #67c23a;
}

.table-card :deep(.el-button--success:hover) {
  background-color: #85ce61;
  border-color: #85ce61;
}
</style>










