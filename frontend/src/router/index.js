import { createRouter, createWebHistory } from 'vue-router';
import DefaultLayout from '@/shared/layouts/DefaultLayout.vue';
import HomeView from '@/features/cattle/views/HomeView.vue';
import TransactionVisualizationView from '@/features/transaction/views/TransactionsVisualizationView.vue';
import UploadPurchaseView from '@/features/transaction/views/RegisterPurchaseView.vue';
import SellCattleView from '@/features/transaction/views/RegisterSaleView.vue';
import RegisterWeighingView from '@/features/weighing/views/RegisterWeighingView.vue';
import CattlePerformanceSummary from '@/features/cattle/views/CattlePerformanceView.vue';
import WeighingGroupView from '@/features/weighing/views/WeighingGroupView.vue';

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
        name: 'CattlePerformanceSummary',
        component: CattlePerformanceSummary,
      },
      {
        path: '/transactions-visualization',
        name: 'TransactionsVisualization',
        component: TransactionVisualizationView,
      },
      {
        path: '/weighings',
        name: 'WeighingsGroupView',
        component: WeighingGroupView,
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
