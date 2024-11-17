<template>
    <nav class="navbar flex">
        <!-- Categories -->
        <ul class="links flex">
            <li>
                <SiteLogo />
            </li>
            <li v-for="category in categories" :key="category.categoryId">
				<div v-if="isAdmin" class="admin-actions">
					<button class="edit" title="Edit">Edit</button>
					<button class="delete" title="Delete">Del</button>
				</div>
                <router-link :to="`/category/${category.categoryId}`">{{ category.name }}</router-link>				
            </li>
        </ul>

        <!-- User and Cart Links -->
        <ul class="actions flex">
            <li>
                <router-link :to="userLink">{{ userText }}</router-link>
            </li>
            <li v-if="!isAdmin">
                <router-link to="/cart" class="cart">
                    Cart <strong class="count">({{ cartCount }})</strong>
                </router-link>
            </li>
        </ul>
    </nav>
</template>
<script>
import axios from 'axios';
import Env from '@/utils/Env';
import SiteLogo from './SiteLogo.vue';
import User from '@/utils/User';

export default {
    name: 'NavBar',
    components: {
        SiteLogo,
    },
    data() {
        return {
            categories: [], // To store categories from backend
        };
    },
    computed: {
		isAdmin() {
			return User.isLoggedInAdmin();
		},		
        userText() {
            if (User.isLoggedInAdmin()) {
                return 'Admin Dashboard';
            } else if (User.isLoggedIn()) {
                return 'Profile';
            } else {
                return 'Login';
            }
        },
        userLink() {
            if (User.isLoggedIn()) {
                return '/profile';
            } else {
                return '/login';
            }
        },
        cartCount() {
            return User.cartCount.value; // Dynamically fetch cart count
        },
    },

    mounted() {
        this.fetchCategories();
    },
	async created() {
		await User.init();
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
	padding: 0 0.5em;

	ul {
		justify-content: start;
		align-items: stretch;
		gap: var(--spacing-element-large);

		li {
			display: flex;
			align-items: stretch;
			position: relative;
			a {
				display: flex;
				align-items: center;
			}

			.admin-actions {
				visibility: hidden;		
				z-index: -1;		
				position: absolute;
				left: 0;
				right: 0;
				top: 0;
				bottom: 0;
				border: 1px dashed var(--primary-color);
				button {
					background-color: transparent;
					padding: 2px;							
					// font-weight: 200;
					font-size: 0.5em;
					border-radius: 0;
					top: -2em;									
					float: right;
					&:hover {
						background: var(--primary-color);
						color: var(--white-bg-color)
					}
				}
				button:last-child {
					border-right: none;
				}
			}
			&:hover {
				.admin-actions {
					visibility: visible;
				}
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