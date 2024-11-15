<template>
	<nav class="navbar flex">
		<!-- Categories -->
		<ul class="links flex">
			<li>
				<SiteLogo />
			</li>
			<li v-for="category in categories" :key="category.categoryId">
				<router-link :to="`/category/${category.categoryId}`">{{ category.name }}</router-link>
			</li>
		</ul>

		<!-- User and Cart Links -->
		<ul class="actions flex">
			<li>
				<router-link :to="userLink">{{ userText }}</router-link>
			</li>
			<li>
				<router-link to="/checkout" class="cart">
					Checkout <span class="count">(0)</span>
				</router-link>
			</li>
		</ul>
	</nav>
</template>

<script>
import axios from 'axios';
import Env from '@/utils/Env';
import SiteLogo from './SiteLogo.vue';

export default {
	name: 'NavBar',
	components: {
		SiteLogo,
	},
	data() {
		return {
			categories: [], // To store categories from backend
			adminEmail: Env.ADMIN_EMAIL, // Read ADMIN_EMAIL from env file
			userEmail: localStorage.getItem('email'), // Get email from local storage
		};
	},
	computed: {
		userText() {
			if (this.userEmail === this.adminEmail) {
				return 'Orders';
			} else if (this.userEmail) {
				return 'Profile';
			} else {
				return 'Login';
			}
		},
		userLink() {
			if (this.userEmail === this.adminEmail) {
				return '/orders';
			} else if (this.userEmail) {
				return '/profile';
			} else {
				return '/login';
			}
		},
	},
	mounted() {
		this.fetchCategories();
	},
	methods: {
		async fetchCategories() {
			try {
				const response = await axios.get(`${Env.API_BASE_URL}/categories`);
				this.categories = response.data;
			} catch (error) {
				console.error('Failed to fetch categories:', error);
			}
		},
	},
};
</script>

<style scoped lang="scss">
.navbar {
	align-items: stretch;

	ul {
		justify-content: start;
		align-items: stretch;
		gap: var(--spacing-element-large);

		li {
			display: flex;
			align-items: stretch;

			a {
				display: flex;
				align-items: center;
			}
		}
	}

	.actions {
		.cart {
			gap: var(--spacing-element-small);
			.count {
				color: var(--primary-color);				
			}
		}
	}

	.router-link-exact-active {
		border-bottom: 2px solid var(--primary-color);
	}
}

</style>