<template>
  <div class="news-page">
    <el-card>
      <template #header>
        <div style="display: flex; justify-content: space-between; align-items: center;">
          <h2>资讯管理</h2>
          <el-button type="primary" @click="showDialog()">发布资讯</el-button>
        </div>
      </template>
      
      <el-table :data="newsList" style="width: 100%">
        <el-table-column prop="id" label="ID" width="80" align="center"></el-table-column>
        
        <el-table-column label="封面图" width="120" align="center">
          <template #default="{ row }">
            <el-image 
              v-if="row.coverImage" 
              :src="row.coverImage" 
              style="width: 80px; height: 60px; border-radius: 4px;"
              fit="cover"
            >
              <template #error>
                <div class="image-slot">
                  <el-icon><Picture /></el-icon>
                </div>
              </template>
            </el-image>
            <span v-else style="font-size: 12px; color: #999;">暂无图片</span>
          </template>
        </el-table-column>
        
        <el-table-column prop="title" label="标题" min-width="180"></el-table-column>
        <el-table-column prop="category" label="分类" width="120" align="center"></el-table-column>
        <el-table-column prop="author.nickname" label="作者" width="120" align="center"></el-table-column>
        <el-table-column prop="viewCount" label="浏览" width="80" align="center"></el-table-column>
        <el-table-column prop="status" label="状态" width="100" align="center">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'info'">
              {{ row.status === 1 ? '已发布' : '草稿' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="200" align="center">
          <template #default="{ row }">
            <el-button size="small" @click="showDialog(row)">编辑</el-button>
            <el-button size="small" type="danger" @click="deleteNews(row.id)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <div class="pagination">
        <el-pagination
          v-model:current-page="page"
          v-model:page-size="size"
          :total="total"
          layout="total, prev, pager, next"
          @current-change="fetchNews"
        />
      </div>
    </el-card>

    <el-dialog v-model="dialogVisible" :title="form.id ? '编辑资讯' : '发布资讯'" width="800px">
      <el-form :model="form" label-width="100px">
        <el-form-item label="标题">
          <el-input v-model="form.title" placeholder="请输入资讯标题"></el-input>
        </el-form-item>
        <el-form-item label="分类">
          <el-select v-model="form.category" placeholder="请选择分类" style="width: 100%;">
            <el-option 
              v-for="cat in categories" 
              :key="cat.id" 
              :label="cat.name" 
              :value="cat.name"
            ></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="封面图片">
          <el-upload
            class="cover-uploader"
            :action="uploadUrl"
            :headers="uploadHeaders"
            :show-file-list="false"
            :on-success="handleUploadSuccess"
            :on-error="handleUploadError"
            :before-upload="beforeUpload"
            accept="image/*"
          >
            <img v-if="form.coverImage" :src="form.coverImage" class="cover-preview" />
            <el-icon v-else class="cover-uploader-icon"><Plus /></el-icon>
          </el-upload>
          <div class="upload-tip">建议尺寸：800x450px，支持 jpg/png/gif 格式</div>
        </el-form-item>
        <el-form-item label="资讯内容">
          <el-input v-model="form.content" type="textarea" :rows="10" placeholder="请输入资讯内容"></el-input>
        </el-form-item>
        <el-form-item label="状态">
          <el-radio-group v-model="form.status">
            <el-radio :label="1">发布</el-radio>
            <el-radio :label="0">草稿</el-radio>
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
// 文件: UI/admin/src/views/News.vue
// hangu: 0 行 | kelei: 0 行 | 本文件合计: 0 行
// ============================================================
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus, Picture } from '@element-plus/icons-vue'
import { useUserStore } from '@/stores/user'
import request from '@/utils/request'

const userStore = useUserStore()
const newsList = ref([])
const categories = ref([])
const page = ref(1)
const size = ref(10)
const total = ref(0)
const dialogVisible = ref(false)
const form = ref({
  id: null,
  title: '',
  content: '',
  category: '',
  coverImage: '',
  status: 1
})

// 上传配置
const uploadUrl = '/api/files/upload'
const uploadHeaders = {
  'Authorization': `Bearer ${localStorage.getItem('token')}`
}

// 获取分类列表
const fetchCategories = async () => {
  try {
    const res = await request.get('/news-categories/enabled')
    categories.value = res.data || []
  } catch (error) {
    console.error('获取分类失败:', error)
  }
}

const fetchNews = async () => {
  try {
    console.log('========== 获取资讯列表 ==========')
    console.log('page:', page.value, 'size:', size.value)
    
    const res = await request.get('/news', {
      params: { page: page.value - 1, size: size.value }
    })
    
    console.log('API返回:', res)
    
    // MyBatis Plus 返回格式：records 和 total
    newsList.value = res.data?.records || res.data?.content || []
    total.value = res.data?.total || res.data?.totalElements || 0
    
    console.log('资讯数量:', newsList.value.length)
    console.log('总数:', total.value)
    console.log('===================================')
  } catch (error) {
    console.error('获取资讯失败:', error)
    ElMessage.error('获取资讯列表失败')
  }
}

const showDialog = (news = null) => {
  if (news) {
    form.value = { ...news }
  } else {
    form.value = { id: null, title: '', content: '', category: '', coverImage: '', status: 1 }
  }
  dialogVisible.value = true
}

// 上传前验证
const beforeUpload = (file) => {
  const isImage = file.type.startsWith('image/')
  const isLt5M = file.size / 1024 / 1024 < 5

  if (!isImage) {
    ElMessage.error('只能上传图片文件！')
    return false
  }
  if (!isLt5M) {
    ElMessage.error('图片大小不能超过 5MB！')
    return false
  }
  return true
}

// 上传成功
const handleUploadSuccess = (response) => {
  console.log('上传成功:', response)
  if (response.code === 200) {
    // 后端返回的URL
    form.value.coverImage = '/api' + response.data
    ElMessage.success('图片上传成功')
  } else {
    ElMessage.error(response.message || '上传失败')
  }
}

// 上传失败
const handleUploadError = (error) => {
  console.error('上传失败:', error)
  ElMessage.error('图片上传失败，请重试')
}

const handleSubmit = async () => {
  try {
    const data = {
      ...form.value,
      author: { id: userStore.userInfo.id }
    }
    
    if (form.value.id) {
      await request.put(`/news/${form.value.id}`, data)
    } else {
      await request.post('/news', data)
    }
    ElMessage.success('操作成功')
    dialogVisible.value = false
    fetchNews()
  } catch (error) {
    console.error(error)
  }
}

const deleteNews = async (id) => {
  try {
    await ElMessageBox.confirm('确定要删除该资讯吗？', '提示', { type: 'warning' })
    await request.delete(`/news/${id}`)
    ElMessage.success('删除成功')
    fetchNews()
  } catch (error) {
    if (error !== 'cancel') {
      console.error(error)
    }
  }
}

onMounted(() => {
  fetchCategories()
  fetchNews()
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

/* 封面图上传 */
.cover-uploader {
  border: 1px dashed #d9d9d9;
  border-radius: 6px;
  cursor: pointer;
  position: relative;
  overflow: hidden;
  transition: all 0.3s;
  width: 360px;
  height: 200px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.cover-uploader:hover {
  border-color: #409eff;
}

.cover-uploader-icon {
  font-size: 28px;
  color: #8c939d;
}

.cover-preview {
  width: 100%;
  height: 100%;
  object-fit: cover;
  display: block;
}

.upload-tip {
  margin-top: 8px;
  font-size: 12px;
  color: #999;
}

:deep(.el-upload) {
  width: 100%;
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
}

/* 表格图片错误 */
.image-slot {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 80px;
  height: 60px;
  background: #f5f7fa;
  color: #909399;
  font-size: 20px;
  border-radius: 4px;
}
</style>










