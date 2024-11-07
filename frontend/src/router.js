import { createRouter, createWebHistory } from 'vue-router';
import LoginPage from '@/views/LoginPage.vue';
import ProfilePage from '@/views/ProfilePage.vue';
import HomePage from '@/views/HomePage.vue';
import CheckoutPage from '@/views/CheckoutPage.vue';

// import ProductListPage from '@/views/ProductListPage.vue'; // Product list route
// import ProductFormPage from '@/views/ProductFormPage.vue'; // Product form route

const routes = [
	{ path: '/', name: 'home', component: HomePage },
	{ path: '/login', name: 'login', component: LoginPage },
	{ path: '/profile', name: 'profile', component: ProfilePage }, // Profile route
	{ path: '/checkout', name: 'checkout', component: CheckoutPage }, // Checkout route

	// { path: '/products', name: 'productList', component: ProductListPage }, // Product list route
	// { path: '/products/new', name: 'productCreate', component: ProductFormPage }, // Create product route
	// { path: '/products/edit/:id', name: 'productEdit', component: ProductFormPage, props: true }, // Edit product route
];

const router = createRouter({
	history: createWebHistory(),
	routes,
});

export default router;
