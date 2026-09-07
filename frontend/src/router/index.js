import { createRouter, createWebHistory } from 'vue-router';
import DefaultLayout from '@/layouts/DefaultLayout.vue';
import HomeView from '@/views/HomeView.vue';

const routes = [
  {
    path: '/',
    component: DefaultLayout,
    children: [
      {
        path: '',
        name: 'Home',
        component: HomeView,
      },
      // Outras páginas podem ser adicionadas aqui
    ],
  },
];

const router = createRouter({
  history: createWebHistory(),
  routes,
});

export default router;
