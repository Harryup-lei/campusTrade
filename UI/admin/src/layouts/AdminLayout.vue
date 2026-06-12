<template>
  <el-container class="admin-layout">
    <!-- 侧边栏 -->
    <el-aside :width="isCollapse ? '64px' : '220px'" class="sidebar">
      <!-- Logo区域 -->
      <div class="logo-box" :class="{ collapse: isCollapse }">
        <transition name="fade">
          <h2 v-show="!isCollapse" class="logo-title">后台管理系统</h2>
        </transition>
        <transition name="fade">
          <div v-show="isCollapse" class="logo-collapsed">后台</div>
        </transition>
      </div>

      <!-- 菜单 -->
      <el-menu
        :default-active="activeMenu"
        :collapse="isCollapse"
        :collapse-transition="false"
        router
        class="sidebar-menu"
      >
        <!-- 数据概览 -->
        <el-menu-item index="/dashboard">
          <el-icon><DataAnalysis /></el-icon>
          <template #title>数据概览</template>
        </el-menu-item>

        <!-- 数据报表 -->
        <el-sub-menu index="data-reports">
          <template #title>
            <el-icon><DataBoard /></el-icon>
            <span>数据报表</span>
          </template>
          <el-menu-item index="/data-charts">
            <el-icon><TrendCharts /></el-icon>
            <template #title>图表分析</template>
          </el-menu-item>
        </el-sub-menu>

        <!-- 用户管理 -->
        <el-sub-menu index="user-management">
          <template #title>
            <el-icon><User /></el-icon>
            <span>用户管理</span>
          </template>
          <el-menu-item index="/users">
            <el-icon><User /></el-icon>
            <template #title>用户列表</template>
          </el-menu-item>
          <el-menu-item index="/credits">
            <el-icon><Medal /></el-icon>
            <template #title>信用管理</template>
          </el-menu-item>
        </el-sub-menu>

        <!-- 交易管理 -->
        <el-sub-menu index="trade-management">
          <template #title>
            <el-icon><ShoppingCart /></el-icon>
            <span>交易管理</span>
          </template>
          <el-menu-item index="/products">
            <el-icon><Goods /></el-icon>
            <template #title>商品管理</template>
          </el-menu-item>
          <el-menu-item index="/orders">
            <el-icon><ShoppingCart /></el-icon>
            <template #title>订单管理</template>
          </el-menu-item>
          <el-menu-item index="/wants">
            <el-icon><Search /></el-icon>
            <template #title>求购管理</template>
          </el-menu-item>
          <el-menu-item index="/want-offers">
            <el-icon><Money /></el-icon>
            <template #title>出价管理</template>
          </el-menu-item>
          <el-menu-item index="/free">
            <el-icon><Present /></el-icon>
            <template #title>赠送管理</template>
          </el-menu-item>
        </el-sub-menu>

        <!-- 内容管理 -->
        <el-sub-menu index="content-management">
          <template #title>
            <el-icon><Document /></el-icon>
            <span>内容管理</span>
          </template>
          <el-menu-item index="/news">
            <el-icon><Document /></el-icon>
            <template #title>资讯管理</template>
          </el-menu-item>
          <el-menu-item index="/news-categories">
            <el-icon><Folder /></el-icon>
            <template #title>资讯分类</template>
          </el-menu-item>
          <el-menu-item index="/comments">
            <el-icon><ChatDotSquare /></el-icon>
            <template #title>评论管理</template>
          </el-menu-item>
        </el-sub-menu>

        <!-- 客服管理 -->
        <el-sub-menu index="service-management">
          <template #title>
            <el-icon><Headset /></el-icon>
            <span>客服管理</span>
          </template>
          <el-menu-item index="/customer-service">
            <el-icon><ChatDotSquare /></el-icon>
            <template #title>咨询管理</template>
          </el-menu-item>
        </el-sub-menu>

        <!-- 系统管理 -->
        <el-sub-menu index="system-management">
          <template #title>
            <el-icon><Setting /></el-icon>
            <span>系统管理</span>
          </template>
          <el-menu-item index="/categories">
            <el-icon><Menu /></el-icon>
            <template #title>分类管理</template>
          </el-menu-item>
          <el-menu-item index="/banners">
            <el-icon><Picture /></el-icon>
            <template #title>轮播图管理</template>
          </el-menu-item>
          <el-menu-item index="/announcements">
            <el-icon><Bell /></el-icon>
            <template #title>平台公告</template>
          </el-menu-item>
          <el-menu-item index="/logs">
            <el-icon><DocumentCopy /></el-icon>
            <template #title>操作日志</template>
          </el-menu-item>
        </el-sub-menu>

        <!-- 安全管理 -->
        <el-sub-menu index="security-management">
          <template #title>
            <el-icon><Lock /></el-icon>
            <span>安全管理</span>
          </template>
          <el-menu-item index="/reports">
            <el-icon><Warning /></el-icon>
            <template #title>举报管理</template>
          </el-menu-item>
        </el-sub-menu>
      </el-menu>
    </el-aside>

    <!-- 主体容器 -->
    <el-container class="main-container">
      <!-- 顶部栏 -->
      <el-header class="header">
        <div class="header-left">
          <!-- 折叠按钮 -->
          <div class="collapse-btn" @click="toggleCollapse">
            <el-icon :size="20">
              <Fold v-if="!isCollapse" />
              <Expand v-else />
            </el-icon>
          </div>

          <!-- 标签页历史 -->
          <el-tabs
            v-model="activeTab"
            type="card"
            class="route-tabs"
            @tab-click="handleTabClick"
            @tab-remove="removeTab"
          >
            <el-tab-pane
              v-for="tab in visitedTabs"
              :key="tab.path"
              :label="tab.title"
              :name="tab.path"
              :closable="visitedTabs.length > 1"
            >
              <template #label>
                <span class="tab-label">
                  {{ tab.title }}
                </span>
              </template>
            </el-tab-pane>
          </el-tabs>
        </div>

        <div class="header-right">
          <!-- 工具栏 -->
          <div class="toolbar">
            <el-icon class="toolbar-icon" @click="handleRefresh"><Refresh /></el-icon>
            <el-icon class="toolbar-icon" @click="handleFullScreen"><FullScreen /></el-icon>
          </div>

          <!-- 用户信息 -->
          <el-dropdown @command="handleCommand">
            <div class="user-info">
              <el-avatar :size="32" :src="userStore.userInfo?.avatar || defaultAvatar" />
              <span class="username">{{ userStore.userInfo?.nickname || '管理员' }}</span>
              <el-icon><ArrowDown /></el-icon>
            </div>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item command="setting">
                  <el-icon><Setting /></el-icon>
                  个人设置
                </el-dropdown-item>
                <el-dropdown-item divided command="logout">
                  <el-icon><SwitchButton /></el-icon>
                  退出登录
                </el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
      </el-header>

      <!-- 主内容区 -->
      <el-main class="main-content">
        <router-view v-slot="{ Component }">
          <transition name="fade-transform" mode="out-in">
            <keep-alive>
              <component :is="Component" :key="route.path" />
            </keep-alive>
          </transition>
        </router-view>
      </el-main>
    </el-container>
  </el-container>
</template>

<script setup>
// ============================================================
// 代码贡献统计（项目总计 3000 行）
// 文件: UI/admin/src/layouts/AdminLayout.vue
// hangu: 0 行 | kelei: 0 行 | 本文件合计: 0 行
// ============================================================
import { ref, computed, watch, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { ElMessage } from 'element-plus'
import {
  DataAnalysis,
  User,
  Menu,
  Goods,
  ShoppingCart,
  ChatDotSquare,
  Document,
  Fold,
  Expand,
  ArrowDown,
  Refresh,
  FullScreen,
  Setting,
  SwitchButton,
  Search,
  Present,
  Folder,
  Medal,
  Warning,
  Picture,
  Bell,
  Money,
  Lock,
  DocumentCopy,
  DataBoard,
  TrendCharts,
  Headset
} from '@element-plus/icons-vue'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()

const isCollapse = ref(false)
const visitedTabs = ref([])
const activeTab = ref('')
const defaultAvatar = 'https://cube.elemecdn.com/0/88/03b0d39583f48206768a7534e55bcpng.png'

// 当前激活的菜单
const activeMenu = computed(() => route.path)

// 菜单配置
const menuConfig = {
  '/dashboard': { title: '数据概览', icon: 'DataAnalysis' },
  '/users': { title: '用户管理', icon: 'User' },
  '/categories': { title: '分类管理', icon: 'Menu' },
  '/products': { title: '商品管理', icon: 'Goods' },
  '/orders': { title: '订单管理', icon: 'ShoppingCart' },
  '/comments': { title: '评论管理', icon: 'ChatDotSquare' },
  '/wants': { title: '求购管理', icon: 'Search' },
  '/free': { title: '赠送管理', icon: 'Present' },
  '/news': { title: '资讯管理', icon: 'Document' },
  '/news-categories': { title: '资讯分类管理', icon: 'Folder' },
  '/credits': { title: '信用管理', icon: 'Medal' },
  '/reports': { title: '举报管理', icon: 'Warning' },
  '/banners': { title: '轮播图管理', icon: 'Picture' },
  '/announcements': { title: '平台公告', icon: 'Bell' },
  '/want-offers': { title: '出价管理', icon: 'Money' },
  '/logs': { title: '操作日志', icon: 'DocumentCopy' },
  '/data-charts': { title: '数据报表', icon: 'DataBoard' },
  '/customer-service': { title: '客服管理', icon: 'Headset' }
}

// 当前菜单名称
const currentMenu = computed(() => menuConfig[route.path]?.title || '')

// 切换侧边栏折叠状态
const toggleCollapse = () => {
  isCollapse.value = !isCollapse.value
  localStorage.setItem('sidebar-collapse', isCollapse.value)
}

// 添加标签页
const addTab = (path) => {
  const config = menuConfig[path]
  if (!config) return

  // 检查是否已存在
  const existTab = visitedTabs.value.find(tab => tab.path === path)
  if (!existTab) {
    visitedTabs.value.push({
      path,
      title: config.title,
      icon: config.icon
    })
    saveTabsToStorage()
  }
  activeTab.value = path
}

// 点击标签页
const handleTabClick = (pane) => {
  const path = pane.props.name
  if (route.path !== path) {
    router.push(path)
  }
}

// 移除标签页
const removeTab = (targetPath) => {
  const tabs = visitedTabs.value
  const index = tabs.findIndex(tab => tab.path === targetPath)

  if (index === -1) return

  tabs.splice(index, 1)
  saveTabsToStorage()

  // 如果关闭的是当前页，跳转到前一个或后一个标签页
  if (activeTab.value === targetPath) {
    const nextTab = tabs[index] || tabs[index - 1]
    if (nextTab) {
      router.push(nextTab.path)
    }
  }
}

// 保存标签页到本地存储
const saveTabsToStorage = () => {
  localStorage.setItem('visited-tabs', JSON.stringify(visitedTabs.value))
}

// 刷新当前页面
const handleRefresh = () => {
  window.location.reload()
}

// 全屏
const handleFullScreen = () => {
  if (!document.fullscreenElement) {
    document.documentElement.requestFullscreen()
  } else {
    document.exitFullscreen()
  }
}

// 处理用户菜单命令
const handleCommand = (command) => {
  if (command === 'logout') {
    userStore.logout()
    router.push('/login')
    ElMessage.success('退出登录成功')
  } else if (command === 'setting') {
    ElMessage.info('个人设置功能开发中...')
  }
}

// 监听路由变化，自动添加标签
watch(
  () => route.path,
  (newPath) => {
    if (menuConfig[newPath]) {
      addTab(newPath)
    }
  },
  { immediate: true }
)

// 初始化
onMounted(() => {
  // 恢复折叠状态
  const savedCollapse = localStorage.getItem('sidebar-collapse')
  if (savedCollapse !== null) {
    isCollapse.value = savedCollapse === 'true'
  }

  // 恢复标签历史
  const savedTabs = localStorage.getItem('visited-tabs')
  if (savedTabs) {
    try {
      visitedTabs.value = JSON.parse(savedTabs)
    } catch (e) {
      visitedTabs.value = []
    }
  }

  // 如果没有标签，添加当前页面
  if (visitedTabs.value.length === 0 && menuConfig[route.path]) {
    addTab(route.path)
  }
})
</script>

<style>
/* 全局设置，防止滚动条导致的抖动 */
html, body {
  scrollbar-gutter: stable;
  /* 禁用横向滚动 */
  overflow-x: hidden;
}
</style>

<style scoped>
.admin-layout {
  height: 100vh;
  overflow: hidden;
}

/* 侧边栏 */
.sidebar {
  background: linear-gradient(180deg, #2c3e50 0%, #34495e 100%);
  transition: width 0.3s;
  overflow-x: hidden;
  box-shadow: 2px 0 8px rgba(0, 0, 0, 0.1);
}

.logo-box {
  height: 60px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #3e6bda 0%, #5c7cfa 100%);
  box-shadow: 0 2px 8px rgba(62, 107, 218, 0.2);
  transition: all 0.3s;
}

.logo-title {
  color: white;
  font-size: 18px;
  font-weight: bold;
  margin: 0;
  white-space: nowrap;
  letter-spacing: 2px;
}

.logo-collapsed {
  color: white;
  font-size: 16px;
  font-weight: bold;
  writing-mode: vertical-lr;
  letter-spacing: 4px;
}

.sidebar-menu {
  border: none;
  background: transparent;
}

.sidebar-menu :deep(.el-menu-item) {
  color: #bfcbd9;
  transition: all 0.3s;
  margin: 4px 8px;
  border-radius: 8px;
}

.sidebar-menu :deep(.el-menu-item:hover) {
  background: rgba(62, 107, 218, 0.1);
  color: #fff;
}

.sidebar-menu :deep(.el-menu-item.is-active) {
  background: linear-gradient(135deg, #ff6b00 0%, #ff9f43 100%);
  color: #fff;
  font-weight: 600;
  box-shadow: 0 4px 12px rgba(255, 107, 0, 0.3);
}

/* 子菜单父标题样式 */
.sidebar-menu :deep(.el-sub-menu__title) {
  color: #bfcbd9;
  transition: all 0.3s;
  margin: 4px 8px;
  border-radius: 8px;
  font-weight: 500;
}

.sidebar-menu :deep(.el-sub-menu__title:hover) {
  background: rgba(62, 107, 218, 0.1);
  color: #fff;
}

.sidebar-menu :deep(.el-sub-menu.is-opened > .el-sub-menu__title) {
  color: #fff;
  background: rgba(62, 107, 218, 0.08);
}

/* 子菜单容器 - 使用渐变背景和圆角 */
.sidebar-menu :deep(.el-menu--inline) {
  background: linear-gradient(180deg, rgba(26, 35, 46, 0.4) 0%, rgba(20, 28, 36, 0.2) 100%);
  border-radius: 0 0 8px 8px;
  margin: 0 8px 4px 8px;
  padding: 6px 0;
  backdrop-filter: blur(10px);
}

/* 子菜单项 */
.sidebar-menu :deep(.el-sub-menu .el-menu-item) {
  background: transparent;
  color: #a3b5c9;
  margin: 3px 8px;
  padding-left: 48px !important;
  border-radius: 6px;
  position: relative;
  font-size: 13px;
  min-height: 40px;
  line-height: 40px;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  border-left: 2px solid transparent;
}

/* 子菜单项左侧圆点指示器 */
.sidebar-menu :deep(.el-sub-menu .el-menu-item::before) {
  content: '';
  position: absolute;
  left: 26px;
  top: 50%;
  transform: translateY(-50%);
  width: 4px;
  height: 4px;
  border-radius: 50%;
  background: #6b7a89;
  transition: all 0.3s;
}

/* 子菜单项悬停 */
.sidebar-menu :deep(.el-sub-menu .el-menu-item:hover) {
  background: rgba(255, 255, 255, 0.06);
  color: #fff;
  transform: translateX(2px);
  border-left-color: #3e6bda;
}

.sidebar-menu :deep(.el-sub-menu .el-menu-item:hover::before) {
  background: #3e6bda;
  width: 6px;
  height: 6px;
  box-shadow: 0 0 8px rgba(62, 107, 218, 0.5);
}

/* 子菜单项激活状态 */
.sidebar-menu :deep(.el-sub-menu .el-menu-item.is-active) {
  background: linear-gradient(90deg, rgba(255, 107, 0, 0.15) 0%, rgba(255, 159, 67, 0.08) 100%);
  color: #fff;
  font-weight: 500;
  border-left-color: #ff6b00;
  transform: translateX(0);
}

.sidebar-menu :deep(.el-sub-menu .el-menu-item.is-active::before) {
  background: #ff6b00;
  width: 8px;
  height: 8px;
  box-shadow: 0 0 12px rgba(255, 107, 0, 0.6);
}

/* 主容器 */
.main-container {
  background: #f0f2f5;
}

/* 顶部栏 */
.header {
  background: #fff;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 20px;
  z-index: 10;
}

.header-left {
  display: flex;
  align-items: center;
  gap: 20px;
  flex: 1;
}

.collapse-btn {
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  width: 32px;
  height: 32px;
  border-radius: 6px;
  transition: all 0.3s;
  color: #606266;
}

.collapse-btn:hover {
  background: #f0f2f5;
  color: #3e6bda;
}

/* 标签页 */
.route-tabs {
  flex: 1;
}

.route-tabs :deep(.el-tabs__header) {
  margin: 0;
  border: none;
}

.route-tabs :deep(.el-tabs__nav) {
  border: none;
}

.route-tabs :deep(.el-tabs__item) {
  border: none;
  border-radius: 6px 6px 0 0;
  transition: all 0.3s;
  margin-right: 4px;
  color: #606266;
}

.route-tabs :deep(.el-tabs__item:hover) {
  color: #3e6bda;
  background: #f0f2f5;
}

.route-tabs :deep(.el-tabs__item.is-active) {
  background: linear-gradient(135deg, #ff6b00 0%, #ff9f43 100%);
  color: #fff;
}

.tab-label {
  font-size: 13px;
}

.header-right {
  display: flex;
  align-items: center;
  gap: 20px;
}

.toolbar {
  display: flex;
  gap: 12px;
}

.toolbar-icon {
  font-size: 18px;
  cursor: pointer;
  color: #606266;
  transition: all 0.3s;
}

.toolbar-icon:hover {
  color: #3e6bda;
}

.user-info {
  display: flex;
  align-items: center;
  gap: 10px;
  cursor: pointer;
  padding: 8px 12px;
  border-radius: 8px;
  transition: all 0.3s;
}

.user-info:hover {
  background: #f0f2f5;
}

.username {
  font-size: 14px;
  color: #606266;
}

/* 主内容 */
.main-content {
  padding: 20px;
  overflow-y: auto;
  height: calc(100vh - 60px);
}

/* 过渡动画 */
.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.3s;
}

.fade-enter-from,
.fade-leave-to {
  opacity: 0;
}

.fade-transform-enter-active,
.fade-transform-leave-active {
  transition: all 0.3s;
}

.fade-transform-enter-from {
  opacity: 0;
  transform: translateX(-20px);
}

.fade-transform-leave-to {
  opacity: 0;
  transform: translateX(20px);
}
</style>










