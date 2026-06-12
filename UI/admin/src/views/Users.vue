<template>
  <div class="users-page">
    <!-- 搜索区域 -->
    <el-card class="search-card">
      <el-form :inline="true" :model="searchForm" class="search-form">
        <el-form-item label="关键词">
          <el-input
            v-model="searchForm.keyword"
            placeholder="用户名/邮箱/手机号"
            clearable
            @clear="handleSearch"
          >
            <template #prefix>
              <el-icon><Search /></el-icon>
            </template>
          </el-input>
        </el-form-item>
        <el-form-item label="角色">
          <el-select v-model="searchForm.role" placeholder="全部角色" clearable @change="handleSearch">
            <el-option label="全部" value=""></el-option>
            <el-option label="普通用户" value="USER"></el-option>
            <el-option label="管理员" value="ADMIN"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="searchForm.status" placeholder="全部状态" clearable @change="handleSearch">
            <el-option label="全部" :value="null"></el-option>
            <el-option label="正常" :value="1"></el-option>
            <el-option label="禁用" :value="0"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch" :icon="Search">搜索</el-button>
          <el-button @click="handleReset">重置</el-button>
          <el-button type="success" @click="handleAdd">
            <el-icon><Plus /></el-icon>
            添加用户
          </el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <!-- 数据表格 -->
    <el-card class="table-card">
      <template #header>
        <div class="card-header">
          <div class="header-left">
            <el-icon class="title-icon"><User /></el-icon>
            <span class="title">用户列表</span>
            <el-tag class="count-tag">共 {{ total }} 人</el-tag>
          </div>
        </div>
      </template>

      <el-table 
        :data="users" 
        v-loading="loading"
        style="width: 100%"
        :header-cell-style="{ background: '#f5f7fa', color: '#606266' }"
        stripe
      >
        <el-table-column prop="id" label="ID" width="80" align="center"></el-table-column>
        
        <el-table-column label="用户信息" min-width="180">
          <template #default="{ row }">
            <div class="user-info">
              <el-avatar :size="40" :src="row.avatar || defaultAvatar" />
              <div class="user-text">
                <div class="username">{{ row.username }}</div>
                <div class="nickname">{{ row.nickname || '未设置昵称' }}</div>
              </div>
            </div>
          </template>
        </el-table-column>

        <el-table-column prop="email" label="邮箱" min-width="160">
          <template #default="{ row }">
            <span>{{ row.email || '-' }}</span>
          </template>
        </el-table-column>

        <el-table-column prop="phone" label="手机号" width="120">
          <template #default="{ row }">
            <span>{{ row.phone || '-' }}</span>
          </template>
        </el-table-column>

        <el-table-column prop="role" label="角色" width="100" align="center">
          <template #default="{ row }">
            <el-tag :type="row.role === 'ADMIN' ? 'danger' : 'primary'" effect="plain" size="small">
              {{ row.role === 'ADMIN' ? '管理员' : '用户' }}
            </el-tag>
          </template>
        </el-table-column>

        <el-table-column prop="status" label="状态" width="80" align="center">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'danger'" effect="plain" size="small">
              {{ row.status === 1 ? '正常' : '禁用' }}
            </el-tag>
          </template>
        </el-table-column>

        <el-table-column prop="createTime" label="注册时间" width="150">
          <template #default="{ row }">
            <div class="time-info">
              <el-icon><Clock /></el-icon>
              <span>{{ formatTime(row.createTime) }}</span>
            </div>
          </template>
        </el-table-column>

        <el-table-column label="操作" width="320" align="center">
          <template #default="{ row }">
            <div class="action-buttons">
              <el-button size="small" type="primary" @click="handleView(row)">
                <el-icon><View /></el-icon>
                查看
              </el-button>
              <el-button size="small" type="primary" @click="handleEdit(row)">
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
              <el-button size="small" type="danger" @click="deleteUser(row.id)">
                <el-icon><Delete /></el-icon>
                删除
              </el-button>
            </div>
          </template>
        </el-table-column>
      </el-table>

      <!-- 空状态 -->
      <el-empty v-if="!loading && users.length === 0" description="暂无用户数据">
        <el-button type="primary" @click="fetchUsers">刷新</el-button>
      </el-empty>

      <!-- 分页 -->
      <div class="pagination" v-if="users.length > 0">
        <el-pagination
          v-model:current-page="page"
          v-model:page-size="size"
          :page-sizes="[10, 20, 50, 100]"
          :total="total"
          layout="total, sizes, prev, pager, next, jumper"
          @current-change="fetchUsers"
          @size-change="fetchUsers"
        />
      </div>
    </el-card>

    <!-- 查看详情对话框 -->
    <el-dialog v-model="viewDialogVisible" title="用户详情" width="600px">
      <el-descriptions :column="2" border v-if="currentUser">
        <el-descriptions-item label="用户ID">{{ currentUser.id }}</el-descriptions-item>
        <el-descriptions-item label="用户名">{{ currentUser.username }}</el-descriptions-item>
        <el-descriptions-item label="昵称">{{ currentUser.nickname || '-' }}</el-descriptions-item>
        <el-descriptions-item label="角色">
          <el-tag :type="currentUser.role === 'ADMIN' ? 'danger' : 'primary'">
            {{ currentUser.role === 'ADMIN' ? '管理员' : '普通用户' }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="邮箱">{{ currentUser.email || '-' }}</el-descriptions-item>
        <el-descriptions-item label="手机号">{{ currentUser.phone || '-' }}</el-descriptions-item>
        <el-descriptions-item label="状态">
          <el-tag :type="currentUser.status === 1 ? 'success' : 'info'">
            {{ currentUser.status === 1 ? '正常' : '禁用' }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="注册时间">{{ formatTime(currentUser.createTime) }}</el-descriptions-item>
        <el-descriptions-item label="头像" :span="2">
          <el-avatar :size="80" :src="currentUser.avatar || defaultAvatar" />
        </el-descriptions-item>
      </el-descriptions>
      <template #footer>
        <el-button @click="viewDialogVisible = false">关闭</el-button>
        <el-button type="primary" @click="handleEdit(currentUser)">编辑</el-button>
      </template>
    </el-dialog>

    <!-- 编辑对话框 -->
    <el-dialog v-model="editDialogVisible" title="编辑用户" width="600px">
      <el-form :model="editForm" :rules="editRules" ref="editFormRef" label-width="80px">
        <el-form-item label="用户名">
          <el-input v-model="editForm.username" disabled></el-input>
        </el-form-item>
        <el-form-item label="昵称" prop="nickname">
          <el-input v-model="editForm.nickname" placeholder="请输入昵称"></el-input>
        </el-form-item>
        <el-form-item label="邮箱" prop="email">
          <el-input v-model="editForm.email" placeholder="请输入邮箱"></el-input>
        </el-form-item>
        <el-form-item label="手机号" prop="phone">
          <el-input v-model="editForm.phone" placeholder="请输入手机号"></el-input>
        </el-form-item>
        <el-form-item label="头像">
          <div class="avatar-upload">
            <el-upload
              class="avatar-uploader"
              :show-file-list="false"
              :http-request="customUpload"
              :before-upload="beforeAvatarUpload"
            >
              <img v-if="editForm.avatar" :src="editForm.avatar" class="avatar" />
              <el-icon v-else class="avatar-uploader-icon"><Plus /></el-icon>
            </el-upload>
            <div class="upload-tip">点击上传头像，支持 jpg/png 格式，大小不超过 2MB</div>
          </div>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="editDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleUpdate" :loading="updating">保存</el-button>
      </template>
    </el-dialog>

    <!-- 添加用户对话框 -->
    <el-dialog v-model="addDialogVisible" title="添加用户" width="600px">
      <el-form :model="addForm" :rules="addRules" ref="addFormRef" label-width="80px">
        <el-form-item label="用户名" prop="username">
          <el-input v-model="addForm.username" placeholder="请输入用户名"></el-input>
        </el-form-item>
        <el-form-item label="密码" prop="password">
          <el-input v-model="addForm.password" type="password" placeholder="请输入密码" show-password></el-input>
        </el-form-item>
        <el-form-item label="昵称" prop="nickname">
          <el-input v-model="addForm.nickname" placeholder="请输入昵称"></el-input>
        </el-form-item>
        <el-form-item label="邮箱" prop="email">
          <el-input v-model="addForm.email" placeholder="请输入邮箱"></el-input>
        </el-form-item>
        <el-form-item label="手机号" prop="phone">
          <el-input v-model="addForm.phone" placeholder="请输入手机号"></el-input>
        </el-form-item>
        <el-form-item label="角色" prop="role">
          <el-radio-group v-model="addForm.role">
            <el-radio label="USER">普通用户</el-radio>
            <el-radio label="ADMIN">管理员</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="头像">
          <div class="avatar-upload">
            <el-upload
              class="avatar-uploader"
              :show-file-list="false"
              :http-request="customUploadForAdd"
              :before-upload="beforeAvatarUpload"
            >
              <img v-if="addForm.avatar" :src="addForm.avatar" class="avatar" />
              <el-icon v-else class="avatar-uploader-icon"><Plus /></el-icon>
            </el-upload>
            <div class="upload-tip">点击上传头像，支持 jpg/png 格式，大小不超过 2MB</div>
          </div>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="addDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleAddSubmit" :loading="adding">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
// ============================================================
// 代码贡献统计（项目总计 3000 行）
// 文件: UI/admin/src/views/Users.vue
// hangu: 0 行 | kelei: 0 行 | 本文件合计: 0 行
// ============================================================
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { 
  Search, 
  User, 
  View, 
  Edit, 
  Delete, 
  Clock,
  Plus
} from '@element-plus/icons-vue'
import request from '@/utils/request'

const users = ref([])
const page = ref(1)
const size = ref(10)
const total = ref(0)
const loading = ref(false)
const viewDialogVisible = ref(false)
const editDialogVisible = ref(false)
const addDialogVisible = ref(false)
const currentUser = ref(null)
const editFormRef = ref(null)
const addFormRef = ref(null)
const updating = ref(false)
const adding = ref(false)
const defaultAvatar = 'https://cube.elemecdn.com/0/88/03b0d39583f48206768a7534e55bcpng.png'

const searchForm = reactive({
  keyword: '',
  role: '',
  status: null
})

const editForm = reactive({
  id: null,
  username: '',
  nickname: '',
  email: '',
  phone: '',
  avatar: ''
})

const addForm = reactive({
  username: '',
  password: '',
  nickname: '',
  email: '',
  phone: '',
  role: 'USER',
  avatar: ''
})

const editRules = {
  nickname: [{ max: 50, message: '昵称长度不能超过50个字符', trigger: 'blur' }],
  email: [{ type: 'email', message: '请输入正确的邮箱地址', trigger: 'blur' }],
  phone: [{ pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号', trigger: 'blur' }]
}

const addRules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
    { min: 3, max: 20, message: '用户名长度在 3 到 20 个字符', trigger: 'blur' },
    { pattern: /^[a-zA-Z0-9_]+$/, message: '用户名只能包含字母、数字和下划线', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, max: 20, message: '密码长度在 6 到 20 个字符', trigger: 'blur' }
  ],
  nickname: [{ max: 50, message: '昵称长度不能超过50个字符', trigger: 'blur' }],
  email: [{ type: 'email', message: '请输入正确的邮箱地址', trigger: 'blur' }],
  phone: [{ pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号', trigger: 'blur' }],
  role: [{ required: true, message: '请选择角色', trigger: 'change' }]
}

// 获取用户列表
const fetchUsers = async () => {
  loading.value = true
  try {
    const params = {
      page: page.value - 1,
      size: size.value
    }
    
    // 添加搜索条件
    if (searchForm.keyword) params.keyword = searchForm.keyword
    if (searchForm.role) params.role = searchForm.role
    if (searchForm.status !== null) params.status = searchForm.status

    console.log('请求参数：', params)
    const res = await request.get('/users', { params })
    console.log('返回数据：', res)
    
    // MyBatis Plus 返回的分页对象：records(数据列表), total(总数)
    users.value = res.data?.records || res.data?.content || []
    total.value = res.data?.total || res.data?.totalElements || 0
    
    console.log('用户列表：', users.value)
    console.log('总数：', total.value)
  } catch (error) {
    console.error('获取用户列表错误：', error)
    console.error('错误详情：', error.response)
    ElMessage.error(error.response?.data?.message || '获取用户列表失败')
  } finally {
    loading.value = false
  }
}

// 搜索
const handleSearch = () => {
  page.value = 1
  fetchUsers()
}

// 重置
const handleReset = () => {
  searchForm.keyword = ''
  searchForm.role = ''
  searchForm.status = null
  handleSearch()
}

// 查看详情
const handleView = (row) => {
  currentUser.value = row
  viewDialogVisible.value = true
}

// 编辑
const handleEdit = (row) => {
  editForm.id = row.id
  editForm.username = row.username
  editForm.nickname = row.nickname
  editForm.email = row.email
  editForm.phone = row.phone
  editForm.avatar = row.avatar
  editDialogVisible.value = true
  viewDialogVisible.value = false
}

// 添加用户
const handleAdd = () => {
  // 重置表单
  Object.assign(addForm, {
    username: '',
    password: '',
    nickname: '',
    email: '',
    phone: '',
    role: 'USER',
    avatar: ''
  })
  addDialogVisible.value = true
}

// 提交新增用户
const handleAddSubmit = async () => {
  try {
    await addFormRef.value.validate()
    adding.value = true
    
    await request.post('/users', addForm)
    
    ElMessage.success('添加成功')
    addDialogVisible.value = false
    fetchUsers()
  } catch (error) {
    if (error !== false) {
      console.error(error)
      ElMessage.error(error.response?.data?.message || '添加失败')
    }
  } finally {
    adding.value = false
  }
}

// 头像上传前验证
const beforeAvatarUpload = (file) => {
  const isImage = file.type.startsWith('image/')
  const isLt2M = file.size / 1024 / 1024 < 2

  if (!isImage) {
    ElMessage.error('只能上传图片文件!')
    return false
  }
  if (!isLt2M) {
    ElMessage.error('图片大小不能超过 2MB!')
    return false
  }
  return true
}

// 自定义上传 - 编辑时使用
const customUpload = async (options) => {
  const formData = new FormData()
  formData.append('file', options.file)
  formData.append('type', 'avatar')
  
  console.log('开始上传文件：', options.file.name)
  console.log('上传路径：/files/upload')
  
  try {
    const res = await request.post('/files/upload', formData, {
      headers: { 
        'Content-Type': 'multipart/form-data'
      }
    })
    
    console.log('上传成功，返回数据：', res)
    // request拦截器已经处理了res.code的判断
    // 处理返回的URL
    const url = res.data.startsWith('/api') ? res.data : '/api' + res.data
    editForm.avatar = url
    ElMessage.success('头像上传成功')
  } catch (error) {
    console.error('上传错误详情：', error)
    console.error('错误响应：', error.response)
    ElMessage.error(error.response?.data?.message || error.message || '上传失败')
  }
}

// 自定义上传 - 新增时使用
const customUploadForAdd = async (options) => {
  const formData = new FormData()
  formData.append('file', options.file)
  formData.append('type', 'avatar')
  
  try {
    const res = await request.post('/files/upload', formData, {
      headers: { 
        'Content-Type': 'multipart/form-data'
      }
    })
    
    // 处理返回的URL
    const url = res.data.startsWith('/api') ? res.data : '/api' + res.data
    addForm.avatar = url
    ElMessage.success('头像上传成功')
  } catch (error) {
    console.error('上传错误详情：', error)
    ElMessage.error(error.response?.data?.message || error.message || '上传失败')
  }
}

// 更新用户
const handleUpdate = async () => {
  try {
    await editFormRef.value.validate()
    updating.value = true
    
    await request.put(`/users/${editForm.id}`, {
      nickname: editForm.nickname,
      email: editForm.email,
      phone: editForm.phone,
      avatar: editForm.avatar
    })
    
    ElMessage.success('更新成功')
    editDialogVisible.value = false
    fetchUsers()
  } catch (error) {
    if (error !== false) {
      console.error(error)
      ElMessage.error('更新失败')
    }
  } finally {
    updating.value = false
  }
}

// 切换状态
const toggleStatus = async (user) => {
  try {
    const newStatus = user.status === 1 ? 0 : 1
    const action = newStatus === 0 ? '禁用' : '启用'
    
    await ElMessageBox.confirm(`确定要${action}该用户吗？`, '提示', { 
      type: 'warning',
      confirmButtonText: '确定',
      cancelButtonText: '取消'
    })
    
    await request.put(`/users/${user.id}/status`, null, { params: { status: newStatus } })
    ElMessage.success(`${action}成功`)
    fetchUsers()
  } catch (error) {
    if (error !== 'cancel') {
      console.error(error)
      ElMessage.error('操作失败')
    }
  }
}

// 删除用户
const deleteUser = async (id) => {
  try {
    await ElMessageBox.confirm('确定要删除该用户吗？此操作不可恢复！', '警告', { 
      type: 'error',
      confirmButtonText: '确定删除',
      cancelButtonText: '取消'
    })
    
    await request.delete(`/users/${id}`)
    ElMessage.success('删除成功')
    
    // 如果当前页没有数据了，返回上一页
    if (users.value.length === 1 && page.value > 1) {
      page.value--
    }
    fetchUsers()
  } catch (error) {
    if (error !== 'cancel') {
      console.error(error)
      ElMessage.error('删除失败')
    }
  }
}

// 格式化时间
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
  fetchUsers()
})
</script>

<style scoped>
.users-page {
  padding: 0;
  /* 禁用横向滚动 */
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
  /* 禁用横向滚动 */
  overflow-x: hidden;
}

.table-card :deep(.el-card__body) {
  /* 预留滚动条空间，防止滚动条出现/消失导致宽度变化 */
  scrollbar-gutter: stable;
  /* 禁用横向滚动 */
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

/* 用户信息样式 */
.user-info {
  display: flex;
  align-items: center;
  gap: 12px;
}

.user-text {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.username {
  font-weight: 600;
  color: #333;
}

.nickname {
  font-size: 12px;
  color: #909399;
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

/* 头像上传样式 */
.avatar-upload {
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.avatar-uploader {
  border: 1px dashed #d9d9d9;
  border-radius: 6px;
  cursor: pointer;
  position: relative;
  overflow: hidden;
  transition: border-color 0.3s;
  width: 120px;
  height: 120px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.avatar-uploader:hover {
  border-color: #3e6bda;
}

.avatar-uploader-icon {
  font-size: 28px;
  color: #8c939d;
  width: 120px;
  height: 120px;
  text-align: center;
  line-height: 120px;
}

.avatar {
  width: 120px;
  height: 120px;
  display: block;
  object-fit: cover;
}

.upload-tip {
  font-size: 12px;
  color: #909399;
  line-height: 1.5;
}
</style>










