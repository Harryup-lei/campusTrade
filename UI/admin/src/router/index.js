// ============================================================
// 代码贡献统计（项目总计 3000 行）
// 文件: UI/admin/src/router/index.js
// hangu: 0 行 | kelei: 142 行 | 本文件合计: 142 行
// ============================================================
// @contributor kelei - index (142 行)
import { createRouter, createWebHistory } from 'vue-router'
import { useUserStore } from '@/stores/user'

const routes = [
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/Login.vue')
  },
  {
    path: '/',
    component: () => import('@/layouts/AdminLayout.vue'),
    redirect: '/dashboard',
    meta: { requiresAuth: true },
    children: [
      {
        path: 'dashboard',
        name: 'Dashboard',
        component: () => import('@/views/Dashboard.vue')
      },
      {
        path: 'users',
        name: 'Users',
        component: () => import('@/views/Users.vue')
      },
      {
        path: 'categories',
        name: 'Categories',
        component: () => import('@/views/Categories.vue')
      },
      {
        path: 'products',
        name: 'Products',
        component: () => import('@/views/Products.vue')
      },
      {
        path: 'orders',
        name: 'Orders',
        component: () => import('@/views/Orders.vue')
      },
      {
        path: 'comments',
        name: 'Comments',
        component: () => import('@/views/Comments.vue')
      },
      {
        path: 'wants',
        name: 'Wants',
        component: () => import('@/views/Wants.vue')
      },
      {
        path: 'free',
        name: 'Free',
        component: () => import('@/views/Free.vue')
      },
      {
        path: 'news',
        name: 'News',
        component: () => import('@/views/News.vue')
      },
      {
        path: 'news-categories',
        name: 'NewsCategories',
        component: () => import('@/views/NewsCategories.vue')
      },
      {
        path: 'credits',
        name: 'Credits',
        component: () => import('@/views/Credits.vue')
      },
      {
        path: 'reports',
        name: 'Reports',
        component: () => import('@/views/Reports.vue')
      },
      {
        path: 'banners',
        name: 'Banners',
        component: () => import('@/views/Banners.vue')
      },
      {
        path: 'announcements',
        name: 'Announcements',
        component: () => import('@/views/Announcements.vue')
      },
      {
        path: 'want-offers',
        name: 'WantOffers',
        component: () => import('@/views/WantOffers.vue')
      },
      {
        path: 'logs',
        name: 'Logs',
        component: () => import('@/views/Logs.vue')
      },
      {
        path: 'data-charts',
        name: 'DataCharts',
        component: () => import('@/views/DataCharts.vue')
      },
      {
        path: 'customer-service',
        name: 'CustomerService',
        component: () => import('@/views/CustomerService.vue')
      }
    ]
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

router.beforeEach((to, from, next) => {
  const userStore = useUserStore()
  
  if (to.meta.requiresAuth && !userStore.token) {
    next('/login')
  } else if (to.path === '/login' && userStore.token) {
    next('/')
  } else {
    next()
  }
})

export default router










