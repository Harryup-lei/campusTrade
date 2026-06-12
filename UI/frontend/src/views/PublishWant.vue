<template>
  <div class="publish-want-page">
    <div class="main-container">
      <el-card class="publish-card">
        <template #header>
          <div class="card-header">
            <h2>{{ isEdit ? '编辑求购' : '发布求购' }}</h2>
            <p>发布你的求购需求，让卖家主动联系你</p>
          </div>
        </template>

        <el-form
          ref="formRef"
          :model="form"
          :rules="rules"
          label-width="100px"
          class="publish-form"
        >
          <el-form-item label="求购标题" prop="title">
            <el-input
              v-model="form.title"
              placeholder="简单描述你想要的物品，如：求购二手iPad 2021"
              maxlength="100"
              show-word-limit
            />
          </el-form-item>

          <el-form-item label="详细描述" prop="description">
            <el-input
              v-model="form.description"
              type="textarea"
              :rows="6"
              placeholder="详细说明你的需求，包括成色要求、交易方式等"
              maxlength="500"
              show-word-limit
            />
          </el-form-item>

          <el-form-item label="预算范围" required>
            <div class="price-range">
              <el-form-item prop="minPrice" class="inline-item">
                <el-input-number
                  v-model="form.minPrice"
                  :min="0"
                  :precision="2"
                  placeholder="最低价"
                  style="width: 180px"
                />
              </el-form-item>
              <span class="separator">-</span>
              <el-form-item prop="maxPrice" class="inline-item">
                <el-input-number
                  v-model="form.maxPrice"
                  :min="0"
                  :precision="2"
                  placeholder="最高价"
                  style="width: 180px"
                />
              </el-form-item>
            </div>
          </el-form-item>

          <el-form-item label="标签">
            <el-tag
              v-for="tag in form.tagList"
              :key="tag"
              closable
              @close="removeTag(tag)"
              style="margin-right: 8px"
            >
              {{ tag }}
            </el-tag>
            <el-input
              v-if="tagInputVisible"
              ref="tagInputRef"
              v-model="tagInputValue"
              size="small"
              style="width: 100px"
              @keyup.enter="handleTagConfirm"
              @blur="handleTagConfirm"
            />
            <el-button v-else size="small" @click="showTagInput">
              + 添加标签
            </el-button>
            <div class="tag-tips">添加标签可以让求购更容易被找到（最多5个）</div>
          </el-form-item>

          <el-form-item>
            <el-button type="primary" size="large" @click="handleSubmit" :loading="loading">
              {{ isEdit ? '保存修改' : '发布求购' }}
            </el-button>
            <el-button size="large" @click="$router.back()">取消</el-button>
          </el-form-item>
        </el-form>
      </el-card>
    </div>
  </div>
</template>

<script setup>
// ============================================================
// 代码贡献统计（项目总计 3000 行）
// 文件: UI/frontend/src/views/PublishWant.vue
// hangu: 42 行 | kelei: 0 行 | 本文件合计: 42 行
// ============================================================
import { ref, reactive, onMounted, nextTick } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import { createWant, updateWant, getWant } from '@/api/want'

const router = useRouter()
const route = useRoute()

const formRef = ref()
const tagInputRef = ref()
const loading = ref(false)
const isEdit = ref(false)
const wantId = ref(null)

const form = reactive({
  title: '',
  description: '',
  minPrice: null,
  maxPrice: null,
  tagList: []
})

const rules = {
  title: [
    { required: true, message: '请输入求购标题', trigger: 'blur' },
    { min: 5, max: 100, message: '标题长度在5到100个字符', trigger: 'blur' }
  ],
  description: [
    { required: true, message: '请输入详细描述', trigger: 'blur' },
    { min: 10, max: 500, message: '描述长度在10到500个字符', trigger: 'blur' }
  ],
  minPrice: [
    { required: true, message: '请输入最低预算', trigger: 'blur' }
  ],
  maxPrice: [
    { required: true, message: '请输入最高预算', trigger: 'blur' }
  ]
}

// 标签相关
const tagInputVisible = ref(false)
const tagInputValue = ref('')

const removeTag = (tag) => {
  form.tagList = form.tagList.filter(t => t !== tag)
}

const showTagInput = () => {
  if (form.tagList.length >= 5) {
    ElMessage.warning('最多只能添加5个标签')
    return
  }
  tagInputVisible.value = true
  nextTick(() => {
    tagInputRef.value?.focus()
  })
}

const handleTagConfirm = () => {
  const value = tagInputValue.value.trim()
  if (value && !form.tagList.includes(value)) {
    if (form.tagList.length < 5) {
      form.tagList.push(value)
    } else {
      ElMessage.warning('最多只能添加5个标签')
    }
  }
  tagInputVisible.value = false
  tagInputValue.value = ''
}

// 加载求购数据（编辑模式）
const loadWantData = async (id) => {
  try {
    const res = await getWant(id)
    if (res && res.data) {
      const data = res.data
      form.title = data.title
      form.description = data.description
      form.minPrice = data.minPrice
      form.maxPrice = data.maxPrice
      form.tagList = data.tags ? data.tags.split(',').filter(t => t) : []
    }
  } catch (error) {
    console.error('加载求购数据失败:', error)
    ElMessage.error('加载失败')
    router.back()
  }
}

// 提交
const handleSubmit = async () => {
  try {
    await formRef.value?.validate()
    
    // 验证价格范围
    // @contributor hangu - handleSubmit (42 行)
    if (form.maxPrice < form.minPrice) {
      ElMessage.warning('最高预算不能低于最低预算')
      return
    }
    
    loading.value = true
    const data = {
      title: form.title,
      description: form.description,
      minPrice: form.minPrice,
      maxPrice: form.maxPrice,
      tags: form.tagList.join(',')
    }
    
    let res
    if (isEdit.value && wantId.value) {
      res = await updateWant(wantId.value, data)
    } else {
      res = await createWant(data)
    }
    
    if (res && res.code === 200) {
      ElMessage.success(isEdit.value ? '修改成功' : '发布成功')
      router.push('/wants')
    } else {
      ElMessage.error(res?.message || '操作失败')
    }
  } catch (error) {
    if (error !== false) {
      console.error('提交失败:', error)
      ElMessage.error('操作失败')
    }
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  // 检查是否为编辑模式
  if (route.query.id) {
    isEdit.value = true
    wantId.value = route.query.id
    loadWantData(wantId.value)
  }
})
</script>

<style scoped>
.publish-want-page {
  background: #f0f2f5;
  min-height: 100vh;
  padding: 20px 0;
}

.main-container {
  width: 800px;
  margin: 0 auto;
  padding: 0 16px;
}

.publish-card {
  border-radius: 12px;
}

.card-header h2 {
  margin: 0 0 8px 0;
  font-size: 24px;
  color: #333;
}

.card-header p {
  margin: 0;
  color: #666;
  font-size: 14px;
}

.publish-form {
  margin-top: 20px;
}

.price-range {
  display: flex;
  align-items: center;
  gap: 16px;
}

.inline-item {
  margin-bottom: 0;
}

.separator {
  color: #999;
  font-size: 16px;
}

.tag-tips {
  margin-top: 8px;
  font-size: 12px;
  color: #999;
}
</style>










