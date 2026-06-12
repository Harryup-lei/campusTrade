<template>
  <div class="main-layout">
    <el-container>
      <!-- 顶部导航 -->
      <el-header class="header">
        <div class="header-content">
          <div class="logo">
            <h2>校园二手交易综合平台</h2>
          </div>
          <el-menu
            mode="horizontal"
            :default-active="activeMenu"
            router
            class="nav-menu"
            :ellipsis="false"
          >
            <el-menu-item index="/">首页</el-menu-item>
            <el-menu-item index="/products">闲置市场</el-menu-item>
            <el-menu-item index="/wants">求购专区</el-menu-item>
            <el-menu-item index="/free">免费赠送</el-menu-item>
            <el-menu-item index="/news">校园资讯</el-menu-item>
            <el-sub-menu index="/more">
              <template #title>更多服务</template>
              <el-menu-item index="/credit">信用中心</el-menu-item>
              <el-menu-item index="/help">帮助中心</el-menu-item>
              <el-menu-item index="/about">关于我们</el-menu-item>
            </el-sub-menu>
          </el-menu>
          <div class="user-actions">
            <el-button type="danger" round class="publish-btn-nav" @click="handlePublish">
              <el-icon><Plus /></el-icon> 我要发布
            </el-button>
            
            <el-divider direction="vertical" class="nav-divider" />
            
            <div class="user-section">
              <template v-if="userStore.token">
                <!-- 通知图标 -->
                <el-badge :value="unreadCount" :hidden="unreadCount === 0" class="notification-badge">
                  <el-icon class="notification-icon" @click="showNotifications">
                    <Bell />
                  </el-icon>
                </el-badge>
                
                <el-dropdown trigger="click" @command="handleCommand">
                  <div class="user-info">
                    <el-avatar :size="32" :src="userStore.userInfo?.avatar || 'https://cube.elemecdn.com/0/88/03b0d39583f48206768a7534e55bcpng.png'" />
                    <span class="nickname">{{ userStore.userInfo?.nickname || userStore.userInfo?.username }}</span>
                    <el-icon class="arrow"><ArrowDown /></el-icon>
                  </div>
                  <template #dropdown>
                    <el-dropdown-menu>
                      <el-dropdown-item @click="$router.push('/profile')">
                        <el-icon><User /></el-icon> 个人中心
                      </el-dropdown-item>
                      <el-dropdown-item @click="$router.push('/credit')">
                        <el-icon><Star /></el-icon> 信用中心
                      </el-dropdown-item>
                      <el-dropdown-item divided @click="handleLogout">
                        <el-icon><SwitchButton /></el-icon> 退出登录
                      </el-dropdown-item>
                    </el-dropdown-menu>
                  </template>
                </el-dropdown>
              </template>
              <template v-else>
                <div class="login-actions">
                  <el-button text @click="$router.push('/login')">登录</el-button>
                  <el-button type="primary" round @click="$router.push('/register')">注册</el-button>
                </div>
              </template>
            </div>
          </div>
        </div>
      </el-header>

      <!-- 主体内容 -->
      <el-main class="main-content">
        <router-view></router-view>
      </el-main>

      <!-- 底部 - 简约版 -->
      <el-footer class="footer-simple" height="auto">
        <div class="footer-inner">
          <div class="footer-links">
            <a href="#">关于我们</a>
            <el-divider direction="vertical" />
            <a href="#">帮助中心</a>
            <el-divider direction="vertical" />
            <a href="#">服务条款</a>
            <el-divider direction="vertical" />
            <a href="#">隐私政策</a>
            <el-divider direction="vertical" />
            <a href="#">联系客服</a>
          </div>
          <div class="footer-copyright">
            <p>&copy; 2024 校园二手交易平台 Campus Trade</p>
            <p class="beian">京ICP备12345678号-1</p>
          </div>
        </div>
      </el-footer>
    </el-container>
  </div>
</template>

<script setup>
// ============================================================
// 代码贡献统计（项目总计 3000 行）
// 文件: UI/frontend/src/layouts/MainLayout.vue
// hangu: 0 行 | kelei: 0 行 | 本文件合计: 0 行
// ============================================================
import { ref, computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { ElMessage } from 'element-plus'
import { 
  ArrowDown, Plus, User, List, Goods, SwitchButton, Star, Medal, Bell
} from '@element-plus/icons-vue'
import request from '@/utils/request'
import { onMounted } from 'vue'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()

const activeMenu = computed(() => route.path)
const unreadCount = ref(0)

// 获取未读通知数量
const fetchUnreadCount = async () => {
  if (!userStore.token) return
  
  try {
    const res = await request.get('/notifications/unread-count')
    if (res && res.data) {
      unreadCount.value = res.data.unreadCount || 0
    }
  } catch (error) {
    console.error('获取未读通知数量失败', error)
  }
}

// 显示通知
const showNotifications = () => {
  router.push('/profile?tab=notifications')
}

const handleLogout = () => {
  userStore.logout()
  ElMessage.success('退出成功')
  router.push('/')
}

const handlePublish = () => {
  if (userStore.token) {
    router.push('/publish')
  } else {
    ElMessage.warning('请先登录')
    router.push('/login')
  }
}

// 定期刷新未读通知数量
onMounted(() => {
  fetchUnreadCount()
  // 每30秒刷新一次
  setInterval(() => {
    fetchUnreadCount()
  }, 30000)
})
</script>

<style scoped>
.main-layout {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
}

.header {
  background: #fff;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
  padding: 0;
  position: sticky;
  top: 0;
  z-index: 100;
}

.header-content {
  max-width: 1200px;
  margin: 0 auto;
  display: flex;
  align-items: center;
  height: 100%;
  padding: 0 20px 0 0;
}

.logo {
  padding-left: 0;
  margin-left: -100px;
  flex-shrink: 0;
}

.logo h2 {
  margin: 0;
  color: #3e6bda;
  font-size: 20px;
  font-weight: 700;
  letter-spacing: 1px;
  white-space: nowrap;
}

.nav-menu {
  flex: 1;
  margin-left: 40px;
  border-bottom: none !important;
  background: transparent;
}

.nav-menu :deep(.el-menu-item) {
  font-size: 15px;
  color: #606266;
}

.nav-menu :deep(.el-menu-item.is-active) {
  color: #3e6bda;
  font-weight: 600;
  background: transparent !important;
}

.nav-menu :deep(.el-menu-item:hover) {
  color: #3e6bda;
  background: transparent !important;
}

.user-actions {
  display: flex;
  gap: 16px;
  align-items: center;
}

.user-section {
  display: flex;
  align-items: center;
  gap: 16px;
}

.notification-badge {
  display: flex;
  align-items: center;
  height: 40px;
}

.notification-badge :deep(.el-badge__content) {
  transform: translateY(-50%) translateX(50%);
}

.notification-icon {
  font-size: 22px;
  color: #606266;
  cursor: pointer;
  transition: all 0.3s;
  display: flex;
  align-items: center;
  justify-content: center;
  width: 40px;
  height: 40px;
  border-radius: 50%;
}

.notification-icon:hover {
  color: #3e6bda;
  background: #f0f2f5;
}

.publish-btn-nav {
  font-weight: 600;
  padding: 8px 20px;
  background: linear-gradient(135deg, #ff6b00 0%, #ff9f43 100%);
  border: none;
  transition: transform 0.2s;
}

.publish-btn-nav:hover {
  transform: translateY(-1px);
  box-shadow: 0 4px 12px rgba(255, 107, 0, 0.3);
}

.nav-divider {
  height: 24px;
  border-color: #e4e7ed;
}

.user-info {
  cursor: pointer;
  display: flex;
  align-items: center;
  gap: 8px;
  color: #606266;
  font-size: 14px;
  padding: 4px 12px;
  border-radius: 20px;
  transition: background 0.2s;
  height: 40px;
}

.user-info:hover {
  background: #f5f7fa;
}

.user-info .el-avatar {
  flex-shrink: 0;
}

.nickname {
  max-width: 100px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  line-height: 1;
}

.arrow {
  font-size: 12px;
}

.login-actions {
  display: flex;
  gap: 12px;
  align-items: center;
  height: 40px;
}

.main-content {
  padding: 0; /* 让子页面自己控制padding */
  min-height: calc(100vh - 60px - 300px); /* 减去头部和底部的大致高度 */
}

.footer-simple {
  background: #fff;
  border-top: 1px solid #f0f0f0;
  padding: 30px 0;
  color: #909399;
  font-size: 13px;
  margin-top: auto; /* 确保footer在内容不足时也沉底 */
}

.footer-inner {
  max-width: 1200px;
  margin: 0 auto;
  text-align: center;
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.footer-links {
  display: flex;
  justify-content: center;
  align-items: center;
  gap: 8px;
}

.footer-links a {
  color: #606266;
  text-decoration: none;
  transition: color 0.2s;
}

.footer-links a:hover {
  color: #409eff;
}

.footer-copyright {
  display: flex;
  justify-content: center;
  gap: 20px;
  color: #c0c4cc;
  font-size: 12px;
}

.footer-copyright p {
  margin: 0;
}
</style>










