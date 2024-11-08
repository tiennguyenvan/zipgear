import { createRouter, createWebHistory } from 'vue-router';
import LoginPage from '@/views/LoginPage.vue';
import ProfilePage from '@/views/ProfilePage.vue';
import ProductListPage from '@/views/ProductListPage.vue';
import CheckoutPage from '@/views/CheckoutPage.vue';
import ProductDetailPage from './views/ProductDetailPage.vue';

const routes = [
	{ path: '/', name: 'home', component: ProductListPage },
	{ path: '/login', name: 'login', component: LoginPage },
	{ path: '/profile', name: 'profile', component: ProfilePage },
	{ path: '/checkout', name: 'checkout', component: CheckoutPage },
	{ path: '/product/:id', name: 'checkout', component: ProductDetailPage },
];

const router = createRouter({
	history: createWebHistory(),
	routes,
});

export default router;
