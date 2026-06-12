<template>
  <div class="credits-page">
    <!-- 搜索区域 -->
    <el-card class="search-card">
      <el-form :inline="true" :model="searchForm" class="search-form">
        <el-form-item label="关键词">
          <el-input
            v-model="searchForm.keyword"
            placeholder="用户名/昵称"
            clearable
            @clear="handleSearch"
          >
            <template #prefix>
              <el-icon><Search /></el-icon>
            </template>
          </el-input>
        </el-form-item>
        <el-form-item label="信用等级">
          <el-select v-model="searchForm.level" placeholder="全部等级" clearable @change="handleSearch">
            <el-option label="全部" value="" />
            <el-option label="优秀" value="EXCELLENT" />
            <el-option label="良好" value="GOOD" />
            <el-option label="一般" value="NORMAL" />
            <el-option label="较差" value="BAD" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch" :icon="Search">搜索</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <!-- 数据表格 -->
    <el-card class="table-card">
      <template #header>
        <div class="card-header">
          <div class="header-left">
            <el-icon class="title-icon"><Medal /></el-icon>
            <span class="title">信用列表</span>
            <el-tag class="count-tag">共 {{ total }} 人</el-tag>
          </div>
        </div>
      </template>

      <!-- 用户信用列表 -->
      <el-table 
        :data="creditList" 
        v-loading="loading"
        style="width: 100%"
        :header-cell-style="{ background: '#f5f7fa', color: '#606266' }"
        stripe
      >
        <el-table-column prop="userId" label="用户ID" width="80" align="center" />
        <el-table-column label="用户信息" min-width="180">
          <template #default="{ row }">
            <div class="user-info" v-if="row.user">
              <el-avatar :size="40" :src="row.user.avatar || defaultAvatar" />
              <div class="user-text">
                <div class="username">{{ row.user.username }}</div>
                <div class="nickname">{{ row.user.nickname || '未设置昵称' }}</div>
              </div>
            </div>
          </template>
        </el-table-column>
        <el-table-column label="信用分" width="120" align="center">
          <template #default="{ row }">
            <div class="score-badge" :class="getScoreClass(row.creditScore)">
              <strong>{{ row.creditScore }}</strong>
            </div>
          </template>
        </el-table-column>
        <el-table-column label="等级" width="140" align="center">
          <template #default="{ row }">
            <el-tag :type="getLevelType(row.level)" effect="plain" size="small">
              {{ getLevelText(row.level) }} {{ getLevelIcon(row.level) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="统计" width="180">
          <template #default="{ row }">
            <div class="stats-info">
              <div class="stat-item">
                <span class="stat-label">获得：</span>
                <span class="stat-value positive">+{{ row.totalEarned }}</span>
              </div>
              <div class="stat-item">
                <span class="stat-label">扣除：</span>
                <span class="stat-value negative">-{{ row.totalDeducted }}</span>
              </div>
            </div>
          </template>
        </el-table-column>
        <el-table-column label="认证" width="100" align="center">
          <template #default="{ row }">
            <el-tag v-if="row.user?.isVerified" type="success" effect="plain" size="small">
              <el-icon><CircleCheckFilled /></el-icon>
              已认证
            </el-tag>
            <el-tag v-else type="info" effect="plain" size="small">未认证</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="更新时间" width="180">
          <template #default="{ row }">
            {{ formatTime(row.updateTime) }}
          </template>
        </el-table-column>
        <el-table-column label="操作" width="240" fixed="right" align="center">
          <template #default="{ row }">
            <el-button-group>
              <el-button size="small" @click="viewRecords(row)" :icon="Document">
                记录
              </el-button>
              <el-button size="small" type="warning" @click="adjustCredit(row)" :icon="Edit">
                调整
              </el-button>
              <el-dropdown trigger="click" @command="(cmd) => handleCommand(cmd, row)">
                <el-button size="small" :icon="More">
                  更多
                </el-button>
                <template #dropdown>
                  <el-dropdown-menu>
                    <el-dropdown-item command="resetVerification" :disabled="!row.user?.isVerified">
                      <el-icon><RefreshLeft /></el-icon>
                      重置认证
                    </el-dropdown-item>
                    <el-dropdown-item command="resetCredit" divided>
                      <el-icon><RefreshLeft /></el-icon>
                      重置信用分
                    </el-dropdown-item>
                  </el-dropdown-menu>
                </template>
              </el-dropdown>
            </el-button-group>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页 -->
      <div class="pagination">
        <el-pagination
          v-model:current-page="currentPage"
          v-model:page-size="pageSize"
          :page-sizes="[10, 20, 50, 100]"
          :total="total"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="fetchData"
          @current-change="fetchData"
        />
      </div>
    </el-card>

    <!-- 信用记录对话框 -->
    <el-dialog
      v-model="recordsDialogVisible"
      title="信用记录"
      width="70%"
      :close-on-click-modal="false"
    >
      <el-table :data="records" stripe max-height="400">
        <el-table-column prop="reason" label="变更原因" width="150" />
        <el-table-column label="分数变化" width="100" align="center">
          <template #default="{ row }">
            <el-tag :type="row.scoreChange > 0 ? 'success' : 'danger'">
              {{ row.scoreChange > 0 ? '+' : '' }}{{ row.scoreChange }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="变化" width="120" align="center">
          <template #default="{ row }">
            {{ row.beforeScore }} → {{ row.afterScore }}
          </template>
        </el-table-column>
        <el-table-column prop="changeType" label="类型" width="100">
          <template #default="{ row }">
            <el-tag size="small">{{ getChangeTypeText(row.changeType) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="时间" width="180">
          <template #default="{ row }">
            {{ formatTime(row.createTime) }}
          </template>
        </el-table-column>
      </el-table>
    </el-dialog>

    <!-- 调整信用对话框 -->
    <el-dialog
      v-model="adjustDialogVisible"
      title="调整信用分"
      width="500px"
      :close-on-click-modal="false"
    >
      <el-form :model="adjustForm" :rules="adjustRules" ref="adjustFormRef" label-width="100px">
        <el-form-item label="当前用户">
          <span>{{ currentUser?.username }} ({{ currentUser?.nickname }})</span>
        </el-form-item>
        <el-form-item label="当前信用分">
          <el-tag type="primary" size="large">{{ currentCredit?.creditScore }}</el-tag>
        </el-form-item>
        <el-form-item label="调整方式" prop="type">
          <el-radio-group v-model="adjustForm.type">
            <el-radio label="add">增加</el-radio>
            <el-radio label="deduct">扣除</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="调整分数" prop="score">
          <el-input-number v-model="adjustForm.score" :min="1" :max="100" />
        </el-form-item>
        <el-form-item label="调整原因" prop="reason">
          <el-input
            v-model="adjustForm.reason"
            type="textarea"
            :rows="3"
            placeholder="请输入调整原因"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="adjustDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="confirmAdjust" :loading="adjusting">确认调整</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
// ============================================================
// 代码贡献统计（项目总计 3000 行）
// 文件: UI/admin/src/views/Credits.vue
// hangu: 0 行 | kelei: 0 行 | 本文件合计: 0 行
// ============================================================
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Search, RefreshLeft, Document, Edit, CircleCheckFilled, Medal, More } from '@element-plus/icons-vue'
import request from '@/utils/request'

const loading = ref(false)
const creditList = ref([])
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)
const searchForm = ref({
  keyword: '',
  level: ''
})
const defaultAvatar = 'https://cube.elemecdn.com/0/88/03b0d39583f48206768a7534e55bcpng.png'

const recordsDialogVisible = ref(false)
const records = ref([])

const adjustDialogVisible = ref(false)
const adjusting = ref(false)
const currentUser = ref(null)
const currentCredit = ref(null)
const adjustForm = ref({
  type: 'add',
  score: 5,
  reason: ''
})
const adjustFormRef = ref(null)

const adjustRules = {
  score: [{ required: true, message: '请输入调整分数', trigger: 'blur' }],
  reason: [{ required: true, message: '请输入调整原因', trigger: 'blur' }]
}

// 获取信用列表
const fetchData = async () => {
  loading.value = true
  try {
    const res = await request.get('/admin/credits', {
      params: {
        page: currentPage.value - 1,
        size: pageSize.value,
        keyword: searchForm.value.keyword,
        level: searchForm.value.level
      }
    })
    if (res && res.data) {
      creditList.value = res.data.content || res.data.records || []
      total.value = res.data.totalElements || res.data.total || 0
    }
  } catch (error) {
    console.error('获取信用列表失败:', error)
    ElMessage.error('获取数据失败')
  } finally {
    loading.value = false
  }
}

// 搜索
const handleSearch = () => {
  currentPage.value = 1
  fetchData()
}

// 重置
const handleReset = () => {
  searchForm.value = {
    keyword: '',
    level: ''
  }
  currentPage.value = 1
  fetchData()
}

// 查看记录
const viewRecords = async (row) => {
  try {
    const res = await request.get(`/admin/credits/${row.userId}/records`, {
      params: { page: 0, size: 100 }
    })
    if (res && res.data) {
      records.value = res.data.content || res.data.records || []
      recordsDialogVisible.value = true
    }
  } catch (error) {
    console.error('获取信用记录失败:', error)
    ElMessage.error('获取记录失败')
  }
}

// 调整信用
const adjustCredit = (row) => {
  currentUser.value = row.user
  currentCredit.value = row
  adjustForm.value = {
    type: 'add',
    score: 5,
    reason: ''
  }
  adjustDialogVisible.value = true
}

// 确认调整
const confirmAdjust = async () => {
  try {
    await adjustFormRef.value.validate()
  } catch (error) {
    return
  }

  adjusting.value = true
  try {
    const endpoint = adjustForm.value.type === 'add' 
      ? `/admin/credits/${currentCredit.value.userId}/add`
      : `/admin/credits/${currentCredit.value.userId}/deduct`
    
    const res = await request.post(endpoint, {
      score: adjustForm.value.score,
      reason: `[管理员调整] ${adjustForm.value.reason}`,
      changeType: 'ADMIN'
    })
    
    if (res.code === 200) {
      ElMessage.success('调整成功')
      adjustDialogVisible.value = false
      fetchData()
    } else {
      ElMessage.error(res.message || '调整失败')
    }
  } catch (error) {
    ElMessage.error(error.response?.data?.message || '调整失败')
  } finally {
    adjusting.value = false
  }
}

// 处理更多操作
const handleCommand = async (command, row) => {
  if (command === 'resetVerification') {
    await handleResetVerification(row)
  } else if (command === 'resetCredit') {
    await handleResetCredit(row)
  }
}

// 重置实名认证
const handleResetVerification = async (row) => {
  try {
    await ElMessageBox.confirm(
      `确定要重置用户 "${row.user?.username}" 的实名认证吗？重置后用户可以重新进行认证获得 +20 积分。`,
      '重置实名认证',
      {
        confirmButtonText: '确定重置',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )
    
    const res = await request.post(`/admin/credits/${row.userId}/reset-verification`)
    if (res.code === 200) {
      ElMessage.success('实名认证已重置')
      fetchData()
    } else {
      ElMessage.error(res.message || '重置失败')
    }
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('重置失败')
    }
  }
}

// 重置信用分
const handleResetCredit = async (row) => {
  try {
    await ElMessageBox.confirm(
      `确定要重置用户 "${row.user?.username}" 的信用分吗？将重置为初始值 100 分，并清空所有统计数据。`,
      '重置信用分',
      {
        confirmButtonText: '确定重置',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )
    
    const res = await request.post(`/admin/credits/${row.userId}/reset`)
    if (res.code === 200) {
      ElMessage.success('信用分已重置')
      fetchData()
    } else {
      ElMessage.error(res.message || '重置失败')
    }
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('重置失败')
    }
  }
}

// 辅助方法
const getScoreClass = (score) => {
  if (score >= 150) return 'excellent'
  if (score >= 100) return 'good'
  if (score >= 60) return 'normal'
  return 'bad'
}

const getLevelType = (level) => {
  const map = {
    'EXCELLENT': 'success',
    'GOOD': 'primary',
    'NORMAL': 'warning',
    'BAD': 'danger'
  }
  return map[level] || 'info'
}

const getLevelText = (level) => {
  const map = {
    'EXCELLENT': '优秀',
    'GOOD': '良好',
    'NORMAL': '一般',
    'BAD': '较差'
  }
  return map[level] || level
}

const getLevelIcon = (level) => {
  const map = {
    'EXCELLENT': '⭐⭐⭐⭐⭐',
    'GOOD': '⭐⭐⭐⭐',
    'NORMAL': '⭐⭐⭐',
    'BAD': '⭐⭐'
  }
  return map[level] || ''
}

const getChangeTypeText = (type) => {
  const map = {
    'TRADE': '交易',
    'REVIEW': '评价',
    'TASK': '任务',
    'BEHAVIOR': '行为',
    'VIOLATION': '违规',
    'ADMIN': '管理员'
  }
  return map[type] || type
}

// 格式化时间
const formatTime = (timeStr) => {
  if (!timeStr) return '-'
  
  // 将ISO格式的时间字符串转换为本地时间
  const date = new Date(timeStr)
  
  // 格式化为 YYYY-MM-DD HH:mm:ss
  const year = date.getFullYear()
  const month = String(date.getMonth() + 1).padStart(2, '0')
  const day = String(date.getDate()).padStart(2, '0')
  const hours = String(date.getHours()).padStart(2, '0')
  const minutes = String(date.getMinutes()).padStart(2, '0')
  const seconds = String(date.getSeconds()).padStart(2, '0')
  
  return `${year}-${month}-${day} ${hours}:${minutes}:${seconds}`
}

onMounted(() => {
  fetchData()
})
</script>

<style scoped>
.credits-page {
  padding: 20px;
}

/* 搜索卡片 */
.search-card {
  margin-bottom: 20px;
}

.search-form {
  margin: 0;
}

.search-form :deep(.el-form-item) {
  margin-bottom: 0;
}

/* 表格卡片 */
.table-card {
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.header-left {
  display: flex;
  align-items: center;
  gap: 8px;
}

.title-icon {
  font-size: 18px;
  color: #409eff;
}

.title {
  font-size: 16px;
  font-weight: 500;
  color: #303133;
}

.count-tag {
  margin-left: 8px;
}

/* 用户信息 */
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
  font-weight: 500;
  color: #303133;
  font-size: 14px;
}

.nickname {
  font-size: 12px;
  color: #909399;
}

/* 信用分徽章 */
.score-badge {
  display: inline-block;
  padding: 8px 16px;
  border-radius: 20px;
  font-size: 18px;
  font-weight: bold;
  font-family: 'DIN Alternate', 'Arial', sans-serif;
}

.score-badge.excellent {
  background: linear-gradient(135deg, #67c23a 0%, #85ce61 100%);
  color: white;
  box-shadow: 0 2px 8px rgba(103, 194, 58, 0.3);
}

.score-badge.good {
  background: linear-gradient(135deg, #409eff 0%, #79bbff 100%);
  color: white;
  box-shadow: 0 2px 8px rgba(64, 158, 255, 0.3);
}

.score-badge.normal {
  background: linear-gradient(135deg, #e6a23c 0%, #f0c78a 100%);
  color: white;
  box-shadow: 0 2px 8px rgba(230, 162, 60, 0.3);
}

.score-badge.bad {
  background: linear-gradient(135deg, #f56c6c 0%, #f89898 100%);
  color: white;
  box-shadow: 0 2px 8px rgba(245, 108, 108, 0.3);
}

/* 统计信息 */
.stats-info {
  display: flex;
  flex-direction: column;
  gap: 6px;
  font-size: 13px;
}

.stat-item {
  display: flex;
  align-items: center;
  gap: 4px;
}

.stat-label {
  color: #909399;
}

.stat-value {
  font-weight: 500;
  font-family: 'DIN Alternate', 'Arial', sans-serif;
}

.stat-value.positive {
  color: #67c23a;
}

.stat-value.negative {
  color: #f56c6c;
}

/* 分页 */
.pagination {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}

/* 对话框表格样式 */
:deep(.el-dialog) {
  border-radius: 8px;
}

:deep(.el-dialog__header) {
  background: #f5f7fa;
  border-bottom: 1px solid #e4e7ed;
  padding: 16px 20px;
}

:deep(.el-dialog__title) {
  font-size: 16px;
  font-weight: 500;
}

/* 调整表单 */
:deep(.el-form-item__label) {
  font-weight: 500;
  color: #606266;
}

:deep(.el-input-number) {
  width: 100%;
}

/* 响应式 */
@media (max-width: 768px) {
  .search-form {
    display: flex;
    flex-direction: column;
  }
  
  .search-form :deep(.el-form-item) {
    width: 100%;
    margin-bottom: 12px;
  }
}
</style>










