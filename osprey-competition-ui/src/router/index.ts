import LightboxView from '@/views/LightboxView.vue'
import { createRouter, createWebHistory } from 'vue-router'
import HomeView from '../views/HomeView.vue'
import SummaryView from '@/views/SummaryView.vue'
import SingleDisplay from '@/views/SingleDisplay.vue'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/',
      name: 'home',
      component: HomeView,
    },
    {
      path: '/lightbox',
      name: 'lightbox',
      component: LightboxView,
    },
    {
      path: '/singledisplay',
      name: 'singledisplay',
      component: SingleDisplay,
    },
    {
      path: '/summary',
      name: 'summary',
      component: SummaryView,
    },
  ],
})

export default router
