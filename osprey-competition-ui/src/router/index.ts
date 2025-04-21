// import LightboxView from '@/views/LightboxView.vue'
import { createRouter, createWebHashHistory } from 'vue-router'
import HomeView from '../views/HomeView.vue'
import SummaryView from '@/views/SummaryView.vue'
import ImageDisplay from '@/views/ImageDisplay.vue'

const router = createRouter({
  history: createWebHashHistory(),
  routes: [
    {
      path: '/',
      name: 'home',
      component: HomeView,
    },
    // {
    //   path: '/lightbox',
    //   name: 'lightbox',
    //   component: LightboxView,
    // },
    {
      path: '/display',
      name: 'display',
      component: ImageDisplay,
    },
    {
      path: '/summary',
      name: 'summary',
      component: SummaryView,
    },
  ],
})

export default router
