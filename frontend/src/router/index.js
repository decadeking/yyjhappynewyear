import { createRouter, createWebHistory } from 'vue-router'

const routes = [
  {
    path: '/',
    component: () => import('../views/student/StudentLayout.vue'),
    children: [
      { path: '', redirect: '/home' },
      { path: 'home', name: 'StudentHome', component: () => import('../views/student/Home.vue') },
      { path: 'items', name: 'ItemList', component: () => import('../views/student/ItemList.vue') },
      { path: 'item/:id', name: 'ItemDetail', component: () => import('../views/student/ItemDetail.vue') },
      { path: 'publish', name: 'Publish', component: () => import('../views/student/Publish.vue') },
      { path: 'profile', name: 'Profile', component: () => import('../views/student/Profile.vue') }
    ]
  },
  {
    path: '/admin',
    component: () => import('../views/admin/AdminLayout.vue'),
    children: [
      { path: '', redirect: '/admin/review' },
      { path: 'review', name: 'Review', component: () => import('../views/admin/Review.vue') },
      { path: 'blacklist', name: 'Blacklist', component: () => import('../views/admin/Blacklist.vue') },
      { path: 'statistics', name: 'Statistics', component: () => import('../views/admin/Statistics.vue') },
      { path: 'notice', name: 'Notice', component: () => import('../views/admin/Notice.vue') },
      { path: 'settings', name: 'Settings', component: () => import('../views/admin/Settings.vue') }
    ]
  },
  { path: '/login', name: 'Login', component: () => import('../views/common/Login.vue') },
  { path: '/register', name: 'Register', component: () => import('../views/common/Register.vue') }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

router.beforeEach((to, from, next) => {
  const token = localStorage.getItem('token')
  const user = JSON.parse(localStorage.getItem('user') || '{}')

  if (to.path === '/login' || to.path === '/register') {
    next()
    return
  }

  if (!token) {
    next('/login')
    return
  }

  if (to.path.startsWith('/admin') && user.role !== 1) {
    next('/home')
    return
  }

  next()
})

export default router
