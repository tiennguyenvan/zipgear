import { createRouter, createWebHistory } from 'vue-router';
import LoginPage from '../views/LoginPage.vue'; // Import the Login page
import HelloWorld from '@/components/HelloWorld.vue';
console.log('Router is being initialized'); // Debug message

const routes = [
	{
		path: '/',
		name: 'home',
		component: HelloWorld,
	},
	{
		path: '/login',
		name: 'login',
		component: LoginPage,
	},
	// In the future, you can add more routes here for Home, Profile, etc.
];

const router = createRouter({
	history: createWebHistory(), // Use HTML5 history mode
	routes,
});

export default router;
