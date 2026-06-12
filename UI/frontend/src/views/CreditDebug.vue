<template>
  <div class="debug-page">
    <div class="container">
      <el-card>
        <template #header>
          <h2>🔧 信用系统调试工具</h2>
        </template>

        <!-- 当前用户状态 -->
        <el-card shadow="hover" style="margin-bottom: 20px;">
          <template #header>
            <h3>当前用户状态</h3>
          </template>

          <el-descriptions :column="2" border>
            <el-descriptions-item label="用户ID">{{ userStore.userInfo?.id }}</el-descriptions-item>
            <el-descriptions-item label="用户名">{{ userStore.userInfo?.username }}</el-descriptions-item>
            <el-descriptions-item label="昵称">{{ userStore.userInfo?.nickname || '未设置' }}</el-descriptions-item>
            <el-descriptions-item label="手机号">{{ userStore.userInfo?.phone || '未设置' }}</el-descriptions-item>
            <el-descriptions-item label="邮箱">{{ userStore.userInfo?.email || '未设置' }}</el-descriptions-item>
            <el-descriptions-item label="头像">{{ userStore.userInfo?.avatar ? '已设置' : '未设置' }}</el-descriptions-item>
            <el-descriptions-item label="学号">
              <el-tag v-if="userStore.userInfo?.studentId" type="success">{{ userStore.userInfo?.studentId }}</el-tag>
              <el-tag v-else type="info">未认证</el-tag>
            </el-descriptions-item>
            <el-descriptions-item label="认证状态">
              <el-tag v-if="userStore.userInfo?.isVerified" type="success">已认证</el-tag>
              <el-tag v-else type="warning">未认证</el-tag>
            </el-descriptions-item>
            <el-descriptions-item label="资料完善">
              <el-tag v-if="userStore.userInfo?.profileCompleted" type="success">已完善</el-tag>
              <el-tag v-else type="warning">未完善</el-tag>
            </el-descriptions-item>
          </el-descriptions>

          <div style="margin-top: 20px;">
            <el-button type="primary" @click="refreshUserInfo" :loading="refreshing">
              <el-icon><Refresh /></el-icon>
              刷新用户信息
            </el-button>
          </div>
        </el-card>

        <!-- 快速操作 -->
        <el-card shadow="hover" style="margin-bottom: 20px;">
          <template #header>
            <h3>快速操作</h3>
          </template>

          <el-space direction="vertical" style="width: 100%;">
            <el-alert
              title="提示"
              type="warning"
              :closable="false"
            >
              如果你已经完成认证但想重新测试，需要在数据库中手动修改 is_verified 字段为 0
            </el-alert>

            <div>
              <h4>重置方法（需要访问数据库）：</h4>
              <pre style="background: #f5f5f5; padding: 15px; border-radius: 4px; overflow-x: auto;">-- 重置指定用户的认证状态
UPDATE `user` 
SET `is_verified` = 0, 
    `student_id` = NULL 
WHERE `id` = {{ userStore.userInfo?.id }};

-- 重置指定用户的资料完善状态
UPDATE `user` 
SET `profile_completed` = 0
WHERE `id` = {{ userStore.userInfo?.id }};</pre>
            </div>

            <el-divider />

            <div>
              <h4>测试信用记录：</h4>
              <el-button type="success" @click="testAddCredit">测试增加积分 (+5)</el-button>
              <el-button type="danger" @click="testDeductCredit">测试扣除积分 (-5)</el-button>
            </div>
          </el-space>
        </el-card>

        <!-- 信用记录查询 -->
        <el-card shadow="hover">
          <template #header>
            <h3>最近信用记录</h3>
          </template>

          <el-button type="primary" @click="fetchRecords" :loading="loading" style="margin-bottom: 15px;">
            <el-icon><RefreshRight /></el-icon>
            刷新记录
          </el-button>

          <el-table :data="records" stripe style="width: 100%;">
            <el-table-column prop="reason" label="原因" width="150" />
            <el-table-column label="分数变化" width="100" align="center">
              <template #default="{ row }">
                <el-tag :type="row.scoreChange > 0 ? 'success' : 'danger'">
                  {{ row.scoreChange > 0 ? '+' : '' }}{{ row.scoreChange }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column label="变化" width="150">
              <template #default="{ row }">
                {{ row.beforeScore }} → {{ row.afterScore }}
              </template>
            </el-table-column>
            <el-table-column prop="createTime" label="时间" />
          </el-table>
        </el-card>

        <div style="margin-top: 20px; text-align: center;">
          <el-button type="primary" @click="$router.push('/profile')">返回个人中心</el-button>
          <el-button type="success" @click="$router.push('/credit')">查看信用中心</el-button>
        </div>
      </el-card>
    </div>
  </div>
</template>

<script setup>
// ============================================================
// 代码贡献统计（项目总计 3000 行）
// 文件: UI/frontend/src/views/CreditDebug.vue
// hangu: 0 行 | kelei: 0 行 | 本文件合计: 0 行
// ============================================================
import { ref } from 'vue'
import { ElMessage } from 'element-plus'
import { Refresh, RefreshRight } from '@element-plus/icons-vue'
import { useUserStore } from '@/stores/user'
import request from '@/utils/request'

const userStore = useUserStore()
const refreshing = ref(false)
const loading = ref(false)
const records = ref([])

// 刷新用户信息
const refreshUserInfo = async () => {
  refreshing.value = true
  try {
    await userStore.fetchUserInfo()
    ElMessage.success('用户信息已刷新')
  } catch (error) {
    ElMessage.error('刷新失败')
  } finally {
    refreshing.value = false
  }
}

// 获取信用记录
const fetchRecords = async () => {
  loading.value = true
  try {
    const res = await request.get('/credit/records', {
      params: { page: 1, size: 10 }
    })
    if (res && res.data) {
      records.value = res.data.records || []
    }
  } catch (error) {
    console.error('获取记录失败:', error)
  } finally {
    loading.value = false
  }
}

// 测试增加积分
const testAddCredit = async () => {
  try {
    // 这需要后端提供测试接口，这里仅作演示
    ElMessage.info('请通过实际操作（发布商品、完成交易等）来增加积分')
  } catch (error) {
    ElMessage.error('操作失败')
  }
}

// 测试扣除积分
const testDeductCredit = async () => {
  try {
    // 这需要后端提供测试接口，这里仅作演示
    ElMessage.info('请通过取消订单等操作来扣除积分')
  } catch (error) {
    ElMessage.error('操作失败')
  }
}

// 页面加载时获取记录
fetchRecords()
</script>

<style scoped>
.debug-page {
  background: #f0f2f5;
  min-height: 100vh;
  padding: 20px;
}

.container {
  max-width: 1000px;
  margin: 0 auto;
}

h2, h3, h4 {
  margin: 0;
  color: #333;
}

pre {
  font-size: 12px;
  line-height: 1.5;
}
</style>










