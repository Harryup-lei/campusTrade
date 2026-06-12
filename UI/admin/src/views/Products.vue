<template>
  <div class="products-page">
    <!-- 搜索栏 -->
    <el-card class="search-card">
      <el-form :inline="true" :model="searchForm" class="search-form">
        <el-form-item>
          <el-input
            v-model="searchForm.keyword"
            placeholder="搜索商品名称"
            clearable
            @clear="handleSearch"
          >
            <template #prefix>
              <el-icon><Search /></el-icon>
            </template>
          </el-input>
        </el-form-item>
        <el-form-item>
          <el-select v-model="searchForm.categoryId" placeholder="选择分类" clearable @change="handleSearch">
            <el-option label="全部分类" :value="null"></el-option>
            <el-option
              v-for="cat in categories"
              :key="cat.id"
              :label="cat.name"
              :value="cat.id"
            ></el-option>
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-select v-model="searchForm.status" placeholder="选择状态" clearable @change="handleSearch">
            <el-option label="全部状态" :value="null"></el-option>
            <el-option label="在售" value="ON_SALE"></el-option>
            <el-option label="已售" value="SOLD"></el-option>
            <el-option label="下架" value="OFF_SALE"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">
            <el-icon><Search /></el-icon>
            搜索
          </el-button>
          <el-button @click="handleReset">重置</el-button>
          <el-button type="success" @click="handleAdd">
            <el-icon><Plus /></el-icon>
            添加商品
          </el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <!-- 数据表格 -->
    <el-card class="table-card">
      <template #header>
        <div class="card-header">
          <div class="header-left">
            <el-icon class="title-icon"><ShoppingBag /></el-icon>
            <span class="title">商品列表</span>
            <el-tag class="count-tag">共 {{ total }} 件</el-tag>
          </div>
        </div>
      </template>

      <el-table
        :data="products"
        v-loading="loading"
        style="width: 100%"
        :header-cell-style="{ background: '#f5f7fa', color: '#606266' }"
        stripe
      >
        <el-table-column prop="id" label="ID" width="80" align="center"></el-table-column>
        
        <el-table-column label="商品信息" min-width="280">
          <template #default="{ row }">
            <div class="product-info">
              <el-image
                :src="row.images && row.images.length > 0 ? row.images[0] : defaultImage"
                class="product-image"
                fit="cover"
              >
                <template #error>
                  <div class="image-error">
                    <el-icon><Picture /></el-icon>
                  </div>
                </template>
              </el-image>
              <div class="product-text">
                <div class="product-title">{{ row.title }}</div>
                <div class="product-desc">{{ row.description || '-' }}</div>
              </div>
            </div>
          </template>
        </el-table-column>

        <el-table-column prop="category.name" label="分类" width="100" align="center">
          <template #default="{ row }">
            <el-tag type="primary" effect="plain" size="small">
              {{ row.category?.name || '-' }}
            </el-tag>
          </template>
        </el-table-column>

        <el-table-column label="价格信息" width="140" align="center">
          <template #default="{ row }">
            <div class="price-info">
              <span class="price">¥{{ row.price }}</span>
              <span v-if="row.originalPrice" class="original-price">¥{{ row.originalPrice }}</span>
            </div>
          </template>
        </el-table-column>

        <el-table-column prop="conditionDesc" label="成色" width="100" align="center">
          <template #default="{ row }">
            <el-tag type="success" effect="plain" size="small">
              {{ row.conditionDesc || '-' }}
            </el-tag>
          </template>
        </el-table-column>

        <el-table-column prop="stock" label="库存" width="80" align="center">
          <template #default="{ row }">
            <el-tag :type="row.stock > 0 ? 'success' : 'danger'" effect="plain" size="small">
              {{ row.stock }}
            </el-tag>
          </template>
        </el-table-column>

        <el-table-column prop="location" label="交易位置" width="120" align="center">
          <template #default="{ row }">
            <span style="font-size: 12px;">{{ row.location || '-' }}</span>
          </template>
        </el-table-column>

        <el-table-column prop="seller.nickname" label="卖家" width="120" align="center">
          <template #default="{ row }">
            <span>{{ row.seller?.nickname || row.seller?.username || '-' }}</span>
          </template>
        </el-table-column>

        <el-table-column prop="status" label="状态" width="100" align="center">
          <template #default="{ row }">
            <el-tag
              :type="row.status === 'ON_SALE' ? 'success' : row.status === 'SOLD' ? 'info' : 'danger'"
              effect="plain"
              size="small"
            >
              {{ getStatusText(row.status) }}
            </el-tag>
          </template>
        </el-table-column>

        <el-table-column prop="createTime" label="发布时间" width="150">
          <template #default="{ row }">
            <div class="time-info">
              <el-icon><Clock /></el-icon>
              <span>{{ formatTime(row.createTime) }}</span>
            </div>
          </template>
        </el-table-column>

        <el-table-column label="操作" width="340" align="center" fixed="right">
          <template #default="{ row }">
            <div class="action-buttons">
              <el-button size="small" type="primary" @click="handleView(row)">
                <el-icon><View /></el-icon>
                查看
              </el-button>
              <el-button size="small" type="warning" @click="handleEdit(row)">
                <el-icon><Edit /></el-icon>
                编辑
              </el-button>
              <el-button size="small" :type="getStatusBtnType(row.status)" @click="toggleStatus(row)">
                {{ getStatusBtnText(row.status) }}
              </el-button>
              <el-button size="small" type="danger" @click="deleteProduct(row.id)">
                <el-icon><Delete /></el-icon>
                删除
              </el-button>
            </div>
          </template>
        </el-table-column>
      </el-table>

      <!-- 空状态 -->
      <el-empty v-if="!loading && products.length === 0" description="暂无商品数据">
        <el-button type="primary" @click="fetchProducts">刷新</el-button>
      </el-empty>

      <!-- 分页 -->
      <div class="pagination" v-if="products.length > 0">
        <el-pagination
          v-model:current-page="page"
          v-model:page-size="size"
          :total="total"
          :page-sizes="[10, 20, 50, 100]"
          layout="total, sizes, prev, pager, next, jumper"
          @current-change="fetchProducts"
          @size-change="fetchProducts"
        />
      </div>
    </el-card>

    <!-- 查看详情对话框 -->
    <el-dialog v-model="viewDialogVisible" title="商品详情" width="800px">
      <el-descriptions :column="2" border v-if="currentProduct">
        <el-descriptions-item label="商品ID">{{ currentProduct.id }}</el-descriptions-item>
        <el-descriptions-item label="商品标题">{{ currentProduct.title }}</el-descriptions-item>
        <el-descriptions-item label="分类">{{ currentProduct.category?.name || '-' }}</el-descriptions-item>
        <el-descriptions-item label="出售价格">
          <span class="price">¥{{ currentProduct.price }}</span>
        </el-descriptions-item>
        <el-descriptions-item label="原价">
          <span v-if="currentProduct.originalPrice" class="original-price">¥{{ currentProduct.originalPrice }}</span>
          <span v-else>-</span>
        </el-descriptions-item>
        <el-descriptions-item label="成色">
          <el-tag type="success" size="small">{{ currentProduct.conditionDesc || '-' }}</el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="库存">{{ currentProduct.stock }}</el-descriptions-item>
        <el-descriptions-item label="状态">
          <el-tag :type="currentProduct.status === 'ON_SALE' ? 'success' : currentProduct.status === 'SOLD' ? 'info' : 'danger'">
            {{ getStatusText(currentProduct.status) }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="交易位置">{{ currentProduct.location || '-' }}</el-descriptions-item>
        <el-descriptions-item label="卖家">{{ currentProduct.seller?.nickname || currentProduct.seller?.username || '-' }}</el-descriptions-item>
        <el-descriptions-item label="发布时间" :span="2">{{ formatTime(currentProduct.createTime) }}</el-descriptions-item>
        <el-descriptions-item label="商品描述" :span="2">
          {{ currentProduct.description || '-' }}
        </el-descriptions-item>
        <el-descriptions-item label="商品图片" :span="2">
          <div class="image-gallery">
            <el-image
              v-for="(img, index) in currentProduct.images"
              :key="index"
              :src="img"
              :preview-src-list="currentProduct.images"
              class="gallery-image"
              fit="cover"
            >
              <template #error>
                <div class="image-error">
                  <el-icon><Picture /></el-icon>
                </div>
              </template>
            </el-image>
          </div>
        </el-descriptions-item>
      </el-descriptions>
      <template #footer>
        <el-button @click="viewDialogVisible = false">关闭</el-button>
      </template>
    </el-dialog>

    <!-- 添加商品对话框 -->
    <el-dialog v-model="addDialogVisible" title="添加商品" width="700px">
      <el-form :model="addForm" :rules="addRules" ref="addFormRef" label-width="100px">
        <el-form-item label="商品标题" prop="title">
          <el-input v-model="addForm.title" placeholder="品牌型号 + 关键参数"></el-input>
        </el-form-item>
        <el-form-item label="商品描述" prop="description">
          <el-input
            v-model="addForm.description"
            type="textarea"
            :rows="4"
            placeholder="描述商品的转手原因、入手渠道、新旧程度和使用感受"
          ></el-input>
        </el-form-item>
        <el-form-item label="分类" prop="categoryId">
          <el-select v-model="addForm.categoryId" placeholder="请选择分类" style="width: 100%">
            <el-option
              v-for="cat in categories"
              :key="cat.id"
              :label="cat.name"
              :value="cat.id"
            ></el-option>
          </el-select>
        </el-form-item>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="出售价格" prop="price">
              <el-input-number v-model="addForm.price" :min="0" :precision="2" :step="0.01" style="width: 100%"></el-input-number>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="原价">
              <el-input-number v-model="addForm.originalPrice" :min="0" :precision="2" :step="0.01" style="width: 100%"></el-input-number>
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="成色" prop="conditionDesc">
          <el-radio-group v-model="addForm.conditionDesc">
            <el-radio-button label="全新">全新</el-radio-button>
            <el-radio-button label="几乎全新">几乎全新</el-radio-button>
            <el-radio-button label="良好">良好</el-radio-button>
            <el-radio-button label="可接受">可接受</el-radio-button>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="库存" prop="stock">
          <el-input-number v-model="addForm.stock" :min="1" :max="9999" style="width: 100%"></el-input-number>
        </el-form-item>
        <el-form-item label="交易位置" prop="location">
          <el-input v-model="addForm.location" placeholder="如：图书馆门口、三食堂"></el-input>
        </el-form-item>
        <el-form-item label="商品图片">
          <el-upload
            v-model:file-list="fileList"
            :action="uploadAction"
            :headers="uploadHeaders"
            list-type="picture-card"
            :on-success="handleUploadSuccess"
            :on-remove="handleRemove"
            :before-upload="beforeImageUpload"
            accept="image/*"
            :limit="5"
          >
            <el-icon><Plus /></el-icon>
          </el-upload>
          <div style="color: #909399; font-size: 12px; margin-top: 5px;">
            最多上传5张图片，第一张将作为封面，支持 jpg/png 格式，单张不超过 2MB
          </div>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="addDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleAddSubmit" :loading="adding">确定</el-button>
      </template>
    </el-dialog>

    <!-- 编辑商品对话框 -->
    <el-dialog v-model="editDialogVisible" title="编辑商品" width="700px">
      <el-form :model="editForm" :rules="addRules" ref="editFormRef" label-width="100px">
        <el-form-item label="商品标题" prop="title">
          <el-input v-model="editForm.title" placeholder="品牌型号 + 关键参数"></el-input>
        </el-form-item>
        <el-form-item label="商品描述" prop="description">
          <el-input
            v-model="editForm.description"
            type="textarea"
            :rows="4"
            placeholder="描述商品的转手原因、入手渠道、新旧程度和使用感受"
          ></el-input>
        </el-form-item>
        <el-form-item label="分类" prop="categoryId">
          <el-select v-model="editForm.categoryId" placeholder="请选择分类" style="width: 100%">
            <el-option
              v-for="cat in categories"
              :key="cat.id"
              :label="cat.name"
              :value="cat.id"
            ></el-option>
          </el-select>
        </el-form-item>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="出售价格" prop="price">
              <el-input-number v-model="editForm.price" :min="0" :precision="2" :step="0.01" style="width: 100%"></el-input-number>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="原价">
              <el-input-number v-model="editForm.originalPrice" :min="0" :precision="2" :step="0.01" style="width: 100%"></el-input-number>
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="成色" prop="conditionDesc">
          <el-radio-group v-model="editForm.conditionDesc">
            <el-radio-button label="全新">全新</el-radio-button>
            <el-radio-button label="几乎全新">几乎全新</el-radio-button>
            <el-radio-button label="良好">良好</el-radio-button>
            <el-radio-button label="可接受">可接受</el-radio-button>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="库存" prop="stock">
          <el-input-number v-model="editForm.stock" :min="1" :max="9999" style="width: 100%"></el-input-number>
        </el-form-item>
        <el-form-item label="交易位置" prop="location">
          <el-input v-model="editForm.location" placeholder="如：图书馆门口、三食堂"></el-input>
        </el-form-item>
        <el-form-item label="商品图片">
          <el-upload
            v-model:file-list="editFileList"
            :action="uploadAction"
            :headers="uploadHeaders"
            list-type="picture-card"
            :on-success="handleEditUploadSuccess"
            :on-remove="handleEditRemove"
            :before-upload="beforeImageUpload"
            accept="image/*"
            :limit="5"
          >
            <el-icon><Plus /></el-icon>
          </el-upload>
          <div style="color: #909399; font-size: 12px; margin-top: 5px;">
            最多上传5张图片，第一张将作为封面，支持 jpg/png 格式，单张不超过 2MB
          </div>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="editDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleEditSubmit" :loading="updating">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
// ============================================================
// 代码贡献统计（项目总计 3000 行）
// 文件: UI/admin/src/views/Products.vue
// hangu: 36 行 | kelei: 0 行 | 本文件合计: 36 行
// ============================================================
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Search, ShoppingBag, View, Edit, Delete, Clock, Picture, Plus } from '@element-plus/icons-vue'
import request from '@/utils/request'

const products = ref([])
const categories = ref([])
const page = ref(1)
const size = ref(10)
const total = ref(0)
const loading = ref(false)
const viewDialogVisible = ref(false)
const addDialogVisible = ref(false)
const editDialogVisible = ref(false)
const currentProduct = ref(null)
const addFormRef = ref(null)
const editFormRef = ref(null)
const adding = ref(false)
const updating = ref(false)
const fileList = ref([])
const editFileList = ref([])
const defaultImage = 'https://cube.elemecdn.com/6/94/4d3ea53c084bad6931a56d5158a48jpeg.jpeg'

const searchForm = ref({
  keyword: '',
  categoryId: null,
  status: null
})

const addForm = ref({
  title: '',
  description: '',
  categoryId: null,
  price: 0,
  originalPrice: null,
  stock: 1,
  conditionDesc: '良好',
  location: '',
  images: []
})

const editForm = ref({
  id: null,
  title: '',
  description: '',
  categoryId: null,
  price: 0,
  originalPrice: null,
  stock: 1,
  conditionDesc: '良好',
  location: '',
  images: []
})

const addRules = {
  title: [
    { required: true, message: '请输入商品标题', trigger: 'blur' },
    { min: 2, max: 100, message: '标题长度在 2 到 100 个字符', trigger: 'blur' }
  ],
  description: [
    { required: true, message: '请输入商品描述', trigger: 'blur' },
    { max: 500, message: '描述不能超过 500 个字符', trigger: 'blur' }
  ],
  categoryId: [
    { required: true, message: '请选择分类', trigger: 'change' }
  ],
  price: [
    { required: true, message: '请输入价格', trigger: 'blur' },
    { type: 'number', min: 0, message: '价格不能为负数', trigger: 'blur' }
  ],
  stock: [
    { required: true, message: '请输入库存', trigger: 'blur' },
    { type: 'number', min: 1, message: '库存至少为1', trigger: 'blur' }
  ],
  conditionDesc: [
    { required: true, message: '请选择成色', trigger: 'change' }
  ],
  location: [
    { required: true, message: '请输入交易位置', trigger: 'blur' }
  ]
}

// 上传配置
const uploadAction = '/api/files/upload'
const uploadHeaders = {
  Authorization: `Bearer ${localStorage.getItem('token') || ''}`
}

const fetchProducts = async () => {
  loading.value = true
  try {
    const params = {
      page: page.value - 1,
      size: size.value
    }
    
    if (searchForm.value.keyword) params.keyword = searchForm.value.keyword
    if (searchForm.value.categoryId) params.categoryId = searchForm.value.categoryId
    if (searchForm.value.status) params.status = searchForm.value.status
    
    const res = await request.get('/products', { params })
    console.log('商品列表响应数据：', res)
    products.value = res.data?.records || []
    total.value = res.data?.total || 0
    console.log('商品数量：', products.value.length, '总数：', total.value)
  } catch (error) {
    console.error('获取商品列表错误：', error)
    ElMessage.error(error.response?.data?.message || '获取商品列表失败')
  } finally {
    loading.value = false
  }
}

const fetchCategories = async () => {
  try {
    const res = await request.get('/categories/all')
    categories.value = res.data || []
  } catch (error) {
    console.error('获取分类列表错误：', error)
  }
}

const handleSearch = () => {
  page.value = 1
  fetchProducts()
}

const handleReset = () => {
  searchForm.value = {
    keyword: '',
    categoryId: null,
    status: null
  }
  handleSearch()
}

const handleView = (product) => {
  currentProduct.value = product
  viewDialogVisible.value = true
}

// 编辑商品
const handleEdit = (product) => {
  editForm.value = {
    id: product.id,
    title: product.title,
    description: product.description,
    categoryId: product.categoryId,
    price: product.price,
    originalPrice: product.originalPrice,
    stock: product.stock,
    conditionDesc: product.conditionDesc || '良好',
    location: product.location,
    images: product.images || []
  }
  
  // 构造编辑时的文件列表
  editFileList.value = (product.images || []).map((url, index) => ({
    name: `image-${index}`,
    url: url,
    status: 'success',
    uid: Date.now() + index
  }))
  
  editDialogVisible.value = true
}

// 添加商品
const handleAdd = () => {
  // 重置表单
  addForm.value = {
    title: '',
    description: '',
    categoryId: null,
    price: 0,
    originalPrice: null,
    stock: 1,
    conditionDesc: '良好',
    location: '',
    images: []
  }
  fileList.value = []
  addDialogVisible.value = true
}

// 提交新增商品
const handleAddSubmit = async () => {
  try {
    await addFormRef.value.validate()
    
    if (addForm.value.images.length === 0) {
      ElMessage.warning('请至少上传一张商品图片')
      return
    }
    
    adding.value = true
    
    await request.post('/products', addForm.value)
    
    ElMessage.success('添加成功')
    addDialogVisible.value = false
    handleSearch()
  } catch (error) {
    if (error !== false) {
      console.error(error)
      ElMessage.error(error.response?.data?.message || '添加失败')
    }
  } finally {
    adding.value = false
  }
}

// 提交编辑商品
const handleEditSubmit = async () => {
  try {
    await editFormRef.value.validate()
    
    if (editForm.value.images.length === 0) {
      ElMessage.warning('请至少上传一张商品图片')
      return
    }
    
    updating.value = true
    
    const { id, ...updateData } = editForm.value
    await request.put(`/products/${id}`, updateData)
    
    ElMessage.success('更新成功')
    editDialogVisible.value = false
    fetchProducts()
  } catch (error) {
    if (error !== false) {
      console.error(error)
      ElMessage.error(error.response?.data?.message || '更新失败')
    }
  } finally {
    updating.value = false
  }
}

// 图片上传前验证
const beforeImageUpload = (file) => {
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

// 图片上传成功
const handleUploadSuccess = (response, file, fileList) => {
  if (response.code === 200) {
    const url = response.data.startsWith('/api') ? response.data : '/api' + response.data
    addForm.value.images.push(url)
    ElMessage.success('图片上传成功')
  } else {
    ElMessage.error(response.message || '上传失败')
    // 移除上传失败的文件
    const index = fileList.findIndex(f => f.uid === file.uid)
    if (index > -1) {
      fileList.splice(index, 1)
    }
  }
}

// 移除图片
const handleRemove = (file, fileList) => {
  const url = file.response?.data
  if (url) {
    const fullUrl = url.startsWith('/api') ? url : '/api' + url
    const index = addForm.value.images.indexOf(fullUrl)
    if (index > -1) {
      addForm.value.images.splice(index, 1)
    }
  }
}

// 编辑图片上传成功
const handleEditUploadSuccess = (response, file, fileList) => {
  if (response.code === 200) {
    const url = response.data.startsWith('/api') ? response.data : '/api' + response.data
    editForm.value.images.push(url)
    ElMessage.success('图片上传成功')
  } else {
    ElMessage.error(response.message || '上传失败')
    const index = fileList.findIndex(f => f.uid === file.uid)
    if (index > -1) {
      fileList.splice(index, 1)
    }
  }
}

// 编辑移除图片
const handleEditRemove = (file, fileList) => {
  // 如果是已存在的图片（有url）
  if (file.url) {
    const index = editForm.value.images.indexOf(file.url)
    if (index > -1) {
      editForm.value.images.splice(index, 1)
    }
  }
  // 如果是新上传的图片（有response）
  const url = file.response?.data
  if (url) {
    const fullUrl = url.startsWith('/api') ? url : '/api' + url
    const index = editForm.value.images.indexOf(fullUrl)
    if (index > -1) {
      editForm.value.images.splice(index, 1)
    }
  }
}
const toggleStatus = async (product) => {
  try {
    let newStatus
    let action
    if (product.status === 'ON_SALE') {
      // @contributor hangu - toggleStatus (36 行)
      newStatus = 'OFF_SALE'
      action = '下架'
    } else if (product.status === 'OFF_SALE') {
      newStatus = 'ON_SALE'
      action = '上架'
    } else {
      ElMessage.warning('已售商品无法更改状态')
      return
    }
    
    await ElMessageBox.confirm(`确定要${action}该商品吗？`, '提示', {
      type: 'warning',
      confirmButtonText: '确定',
      cancelButtonText: '取消'
    })
    
    await request.put(`/products/${product.id}/status`, null, {
      params: { status: newStatus }
    })
    
    ElMessage.success(`${action}成功`)
    fetchProducts()
  } catch (error) {
    if (error !== 'cancel') {
      console.error(error)
      ElMessage.error(error.response?.data?.message || '操作失败')
    }
  }
}

const deleteProduct = async (id) => {
  try {
    await ElMessageBox.confirm('确定要删除该商品吗？此操作不可恢复！', '警告', {
      type: 'error',
      confirmButtonText: '确定删除',
      cancelButtonText: '取消'
    })
    
    await request.delete(`/products/${id}`)
    ElMessage.success('删除成功')
    
    if (products.value.length === 1 && page.value > 1) {
      page.value--
    }
    fetchProducts()
  } catch (error) {
    if (error !== 'cancel') {
      console.error(error)
      ElMessage.error(error.response?.data?.message || '删除失败')
    }
  }
}

const getStatusText = (status) => {
  const statusMap = {
    'ON_SALE': '在售',
    'SOLD': '已售',
    'OFF_SALE': '下架'
  }
  return statusMap[status] || '-'
}

const getStatusBtnType = (status) => {
  if (status === 'ON_SALE') return 'warning'
  if (status === 'OFF_SALE') return 'success'
  return 'info'
}

const getStatusBtnText = (status) => {
  if (status === 'ON_SALE') return '下架'
  if (status === 'OFF_SALE') return '上架'
  return '已售出'
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
  fetchProducts()
})
</script>

<style scoped>
.products-page {
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

/* 商品信息样式 */
.product-info {
  display: flex;
  align-items: center;
  gap: 12px;
}

.product-image {
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

.product-text {
  display: flex;
  flex-direction: column;
  gap: 4px;
  flex: 1;
  min-width: 0;
}

.product-title {
  font-weight: 600;
  color: #333;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.product-desc {
  font-size: 12px;
  color: #909399;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.price-info {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 4px;
}

.price {
  font-size: 15px;
  font-weight: 600;
  color: #ff6b00;
}

.original-price {
  font-size: 12px;
  color: #909399;
  text-decoration: line-through;
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

/* 图片画廊 */
.image-gallery {
  display: flex;
  gap: 10px;
  flex-wrap: wrap;
}

.gallery-image {
  width: 100px;
  height: 100px;
  border-radius: 4px;
  cursor: pointer;
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

.table-card :deep(.el-button--info) {
  background-color: #909399;
  border-color: #909399;
}

.table-card :deep(.el-button--info:hover) {
  background-color: #a6a9ad;
  border-color: #a6a9ad;
}
</style>










