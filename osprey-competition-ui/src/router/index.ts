// import LightboxView from '@/views/LightboxView.vue'
import ImageDisplay from '@/views/ImageDisplay.vue'
import ManageCompetitionsView from '@/views/ManageCompetitionsView.vue'
import RunCompetitionView from '@/views/RunCompetitionView.vue'
import { createRouter, createWebHashHistory } from 'vue-router'
import HomeView from '../views/HomeView.vue'

const router = createRouter({
  history: createWebHashHistory(),
  routes: [
    {
      path: '/',
      name: 'home',
      component: HomeView,
    },

    {
      path: '/display',
      name: 'display',
      component: ImageDisplay,
    },
    {
      path: '/runcomp',
      name: 'runcomp',
      component: RunCompetitionView,
    },{
      path: '/managecomp',
      name: 'managecomp',
      component: ManageCompetitionsView,
    }
  ],
})

export default router
