import { createRouter, createWebHistory } from 'vue-router';
import DefaultLayout from '@/layouts/DefaultLayout.vue';
import HomeView from '@/views/HomeView.vue';
import CattleVisualizationView from '@/views/CattleVisualizationView.vue';
import TransactionVisualizationView from '@/views/TransactionsVisualizationView.vue';
import UploadPurchaseView from '@/views/UploadPurchaseView.vue';
import SellCattleView from '@/views/SellCattleView.vue';
import RegisterWeighingView from '@/views/RegisterWeighingView.vue';
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
      {
        path: '/cattle-visualization',
        name: 'CattleVisualization',
        component: CattleVisualizationView,
      },
      {
        path: '/transactions-visualization',
        name: 'TransactionsVisualization',
        component: TransactionVisualizationView,
      },
      {
        path: '/register-purchase',
        name: 'UploadPurchase',
        component: UploadPurchaseView,
      },
      {
        path: '/register-sale',
        name: 'SellCattleView',
        component: SellCattleView,
      },
      { 
        path: '/register-weighing',
        name: 'RegisterWeighing',
        component: RegisterWeighingView,
      },

    ],
  },
];

const router = createRouter({
  history: createWebHistory(),
  routes,
});

export default router;
