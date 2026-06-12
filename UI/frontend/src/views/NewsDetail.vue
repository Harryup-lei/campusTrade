<template>
  <div class="news-detail-page">
    <div class="main-container">
      <!-- 返回按钮 -->
      <div class="back-bar">
        <el-button @click="goBack" icon="ArrowLeft">返回列表</el-button>
      </div>

      <!-- 加载状态 -->
      <div v-if="loading" class="loading-container">
        <el-skeleton :rows="8" animated />
      </div>

      <!-- 资讯详情 -->
      <el-card v-else-if="news" class="detail-card">
        <!-- 标签和类别 -->
        <div class="tags-row" v-if="news.category">
          <el-tag type="primary" size="large" effect="plain">{{ news.category }}</el-tag>
        </div>

        <!-- 标题 -->
        <h1 class="title">{{ news.title }}</h1>
        
        <!-- 元信息 -->
        <div class="meta-bar">
          <div class="meta-item">
            <el-icon><User /></el-icon>
            <span>{{ news.author?.nickname || '管理员' }}</span>
          </div>
          <div class="meta-item">
            <el-icon><Clock /></el-icon>
            <span>{{ formatDate(news.createTime) }}</span>
          </div>
          <div class="meta-item">
            <el-icon><View /></el-icon>
            <span>{{ news.viewCount }} 次阅读</span>
          </div>
        </div>

        <el-divider />

        <!-- 封面图 -->
        <div class="cover-wrapper" v-if="news.coverImage">
          <el-image :src="news.coverImage" class="cover-image" fit="cover">
            <template #error>
              <div class="image-error">
                <el-icon><Picture /></el-icon>
              </div>
            </template>
          </el-image>
        </div>

        <!-- 正文内容 -->
        <div class="content">
          {{ news.content }}
        </div>

        <el-divider />

        <!-- 底部操作 -->
        <div class="bottom-actions">
          <el-button type="primary" plain @click="goBack">返回列表</el-button>
        </div>
      </el-card>

      <!-- 错误状态 -->
      <el-card v-else class="error-card">
        <el-empty description="资讯不存在或已被删除">
          <el-button type="primary" @click="goBack">返回列表</el-button>
        </el-empty>
      </el-card>
    </div>
  </div>
</template>

<script setup>
// ============================================================
// 代码贡献统计（项目总计 3000 行）
// 文件: UI/frontend/src/views/NewsDetail.vue
// hangu: 0 行 | kelei: 0 行 | 本文件合计: 0 行
// ============================================================
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ArrowLeft, User, Clock, View, Picture } from '@element-plus/icons-vue'
import { getNewsDetail } from '@/api/news'

const route = useRoute()
const router = useRouter()
const news = ref(null)
const loading = ref(false)

const fetchNews = async () => {
  loading.value = true
  try {
    const res = await getNewsDetail(route.params.id)
    news.value = res.data
  } catch (error) {
    console.error('获取资讯详情失败:', error)
    news.value = null
  } finally {
    loading.value = false
  }
}

const formatDate = (dateString) => {
  if (!dateString) return ''
  const date = new Date(dateString)
  return date.toLocaleString('zh-CN', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit',
    hour: '2-digit',
    minute: '2-digit'
  })
}

const goBack = () => {
  router.push('/news')
}

onMounted(() => {
  fetchNews()
})
</script>

<style scoped>
.news-detail-page {
  background: #f0f2f5;
  min-height: 100vh;
  padding: 20px 0;
}

.main-container {
  width: 900px;
  margin: 0 auto;
  padding: 0 16px;
}

.back-bar {
  margin-bottom: 20px;
}

.detail-card {
  padding: 40px;
  box-shadow: 0 2px 12px rgba(0,0,0,0.05);
}

.loading-container {
  background: #fff;
  border-radius: 12px;
  padding: 40px;
  box-shadow: 0 2px 12px rgba(0,0,0,0.05);
}

.error-card {
  padding: 60px 40px;
}

/* 标签行 */
.tags-row {
  margin-bottom: 20px;
  text-align: center;
}

/* 标题 */
.title {
  font-size: 28px;
  font-weight: 700;
  color: #1a1a1a;
  line-height: 1.5;
  margin: 0 0 24px 0;
  text-align: center;
  letter-spacing: 1px;
}

/* 元信息栏 */
.meta-bar {
  display: flex;
  gap: 32px;
  flex-wrap: wrap;
  margin-bottom: 24px;
  justify-content: center;
}

.meta-item {
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 14px;
  color: #999;
}

.meta-item .el-icon {
  font-size: 16px;
}

/* 封面图 */
.cover-wrapper {
  margin: 24px auto;
  max-width: 700px;
  border-radius: 12px;
  overflow: hidden;
  box-shadow: 0 4px 12px rgba(0,0,0,0.08);
}

.cover-image {
  width: 100%;
  max-height: 380px;
  display: block;
}

.image-error {
  display: flex;
  align-items: center;
  justify-content: center;
  height: 250px;
  background: #f5f7fa;
  color: #ccc;
  font-size: 48px;
}

/* 正文内容 */
.content {
  font-size: 16px;
  line-height: 1.8;
  color: #333;
  white-space: pre-wrap;
  word-wrap: break-word;
  margin: 32px 0;
  min-height: 200px;
  text-align: justify;
  letter-spacing: 0.5px;
}

/* 底部操作 */
.bottom-actions {
  display: flex;
  justify-content: center;
  margin-top: 32px;
}

/* 分隔线 */
:deep(.el-divider) {
  margin: 32px 0;
}
</style>










