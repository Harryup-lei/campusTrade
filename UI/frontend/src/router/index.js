// ============================================================
// 代码贡献统计（项目总计 3000 行）
// 文件: UI/frontend/src/router/index.js
// hangu: 150 行 | kelei: 0 行 | 本文件合计: 150 行
// ============================================================
// @contributor hangu - index (150 行)
import { createRouter, createWebHistory } from 'vue-router'
import { useUserStore } from '@/stores/user'

const routes = [
  {
    path: '/',
    component: () => import('@/layouts/MainLayout.vue'),
    children: [
      {
        path: '',
        name: 'Home',
        component: () => import('@/views/Home.vue')
      },
      {
        path: 'products',
        name: 'Products',
        component: () => import('@/views/Products.vue')
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
        path: 'free/:id',
        name: 'FreeDetail',
        component: () => import('@/views/FreeDetail.vue')
      },
      {
        path: 'product/:id',
        name: 'ProductDetail',
        component: () => import('@/views/ProductDetail.vue')
      },
      {
        path: 'news',
        name: 'News',
        component: () => import('@/views/News.vue')
      },
      {
        path: 'news/:id',
        name: 'NewsDetail',
        component: () => import('@/views/NewsDetail.vue')
      },
      {
        path: 'credit',
        name: 'Credit',
        component: () => import('@/views/Credit.vue')
      },
      {
        path: 'credit-test',
        name: 'CreditTest',
        component: () => import('@/views/CreditTest.vue'),
        meta: { requiresAuth: true }
      },
      {
        path: 'credit-debug',
        name: 'CreditDebug',
        component: () => import('@/views/CreditDebug.vue'),
        meta: { requiresAuth: true }
      },
      {
        path: 'help',
        name: 'Help',
        component: () => import('@/views/Help.vue')
      },
      {
        path: 'about',
        name: 'About',
        component: () => import('@/views/About.vue')
      },
      {
        path: 'profile',
        name: 'Profile',
        component: () => import('@/views/Profile.vue'),
        meta: { requiresAuth: true }
      },
      {
        path: 'orders',
        name: 'Orders',
        component: () => import('@/views/Orders.vue'),
        meta: { requiresAuth: true }
      },
      {
        path: 'publish',
        name: 'Publish',
        component: () => import('@/views/Publish.vue'),
        meta: { requiresAuth: true }
      },
      {
        path: 'publish-want',
        name: 'PublishWant',
        component: () => import('@/views/PublishWant.vue'),
        meta: { requiresAuth: true }
      },
      {
        path: 'publish-free',
        name: 'PublishFree',
        component: () => import('@/views/PublishFree.vue'),
        meta: { requiresAuth: true }
      }
    ]
  },
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/Login.vue')
  },
  {
    path: '/register',
    name: 'Register',
    component: () => import('@/views/Register.vue')
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
  } else {
    next()
  }
})

export default router










