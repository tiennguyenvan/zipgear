import { createRouter, createWebHistory } from 'vue-router';
import LoginPage from '@/views/LoginPage.vue';
import ProfilePage from '@/views/ProfilePage.vue';
import ProductListPage from '@/views/ProductListPage.vue';
// import CheckoutPage from '@/views/CheckoutPage.vue';
import ProductDetailPage from './views/ProductDetailPage.vue';
import CartPage from './views/CartPage.vue';

const routes = [
	{ path: '/', name: 'home', component: ProductListPage },
	{ path: '/category/:categoryId', name: 'category-page', component: ProductListPage,	},
	{ path: '/login', name: 'login', component: LoginPage },
	{ path: '/login/add-to-cart/:productId', name: 'login-add-to-cart', component: LoginPage },
	{ path: '/profile', name: 'profile', component: ProfilePage },
	{ path: '/cart', name: 'cart', component: CartPage },
	{ path: '/category/:categoryId/product/:productId', name: 'product-detail', component: ProductDetailPage },	
	// { path: '/category/:categoryId/:productId', name: 'add-new-product', component: ProductDetailPage },	
];

const router = createRouter({
	history: createWebHistory(),
	routes,
});

export default router;
