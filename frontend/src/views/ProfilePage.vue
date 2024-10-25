<template>
	<div class="wide">
		<NavBar />

		<!-- Profile Card -->
		<div class="card user-profile flex">
			<!-- Orders Section -->
			<div class="orders">
				<h2>Orders</h2>

				<!-- Loop through orders using v-for -->
				<div v-for="(order, index) in orders" :key="index" class="item">
					<span :class="['status', order.status.toLowerCase()]">{{ order.status }}</span>
					<div class="content">
						<span class="detail">
							{{ order.date }} {{ order.amount }}: {{ order.items }}
							<a v-if="order.status === 'Processing'" href="#" class="cancel action">Cancel</a>
						</span>
						<p class="address">To: {{ order.address }}</p>
					</div>
				</div>
			</div>

			<!-- Sidebar Info -->
			<div class="info sidebar">
				<h2>{{ email }}</h2>
				<p class="dim-text">Joined {{ joinedDate }}</p>

				<div class="address section">
					<h3>Address <a class="action" href="#" @click.prevent="addAddress">+ New</a></h3>
					<ul>
						<li v-for="(address, index) in addresses" :key="index">
							{{ address }} <a href="#" @click.prevent="removeAddress(index)">Disable</a>
						</li>
					</ul>
				</div>

				<!-- Update Profile Button -->
				<button class="disable-btn" @click="updateProfile">Update Profile</button>
			</div>
		</div>
	</div>
</template>

<script>
import NavBar from '@/components/NavBar.vue';

export default {
	name: 'UserProfile',
	components: {
		NavBar,
	},
	data() {
		return {
			email: '',
			joinedDate: '',
			addresses: [],
			code: localStorage.getItem('code'),
			userEmail: localStorage.getItem('email'),

			// Orders JSON data
			orders: [
				{
					status: 'Shipped',
					date: 'Oct 21, 2024',
					amount: '$1,299.99',
					items: 'MacBook Air, Mouse',
					address: 'Just a long long address information here',
				},
				{
					status: 'Shipping',
					date: 'Oct 21, 2024',
					amount: '$1,299.99',
					items: 'MacBook Air, Mouse',
					address: 'Just a long long address information here',
				},
				{
					status: 'Processing',
					date: 'Oct 21, 2024',
					amount: '$1,299.99',
					items: 'MacBook Air, Mouse',
					address: 'Just a long long address information here',
				},
				{
					status: 'Canceled',
					date: 'Oct 21, 2024',
					amount: '$1,299.99',
					items: 'MacBook Air, Mouse',
					address: 'Just a long long address information here',
				},
			],
		};
	},
	async mounted() {
		await this.fetchUserData();
	},
	methods: {
		async fetchUserData() {
			try {
				const response = await fetch(
					`http://localhost:8080/api/user/get-data?email=${this.userEmail}&code=${this.code}`
				);

				if (!response.ok) {
					throw new Error('Failed to fetch user data');
				}

				const data = await response.json();
				this.email = data.email;
				this.joinedDate = new Date(data.createdAt).toLocaleDateString('en-US', {
					year: 'numeric',
					month: 'long',
					day: 'numeric',
				});

				this.addresses = data.addresses;
			} catch (error) {
				console.error('Error fetching user data:', error);
				alert('Session expired. Please log in again.');
				this.$router.push('/login');
			}
		},
		addAddress() {
			alert('Add Address functionality is not implemented yet.');
		},
		removeAddress(index) {
			this.addresses.splice(index, 1);
		},
		updateProfile() {
			alert('Update Profile functionality is not implemented yet.');
		},
	},
};
</script>

<style scoped lang="scss">
.user-profile {
	align-items: start;

	.orders {
		background-color: var(--light-bg-color);		
		flex-grow: 1;	
		width: 0;	
		padding: var(--padding-container);

		.item {
			display: flex;
			align-items: start;
			margin-top: var(--spacing-element-big);
			font-family: 'Arial', sans-serif;

			.status {
				width: 150px;
				font-weight: bold;
				font-size: 14px;

				&.shipped {
					color: gray;
				}

				&.shipping {
					color: green;
				}

				&.processing {
					color: black;
				}

				&.canceled {
					color: red;
				}
			}

			.content {
				display: flex;
				flex-direction: column;
				font-size: 14px;

				.detail {
					font-weight: bold;
					margin-bottom: 5px;

					a.cancel {
						margin-left: 5px;						
					}
				}

				.address {
					color: gray;
					margin: 0;
				}
			}
		}

	}

	.info {		
		padding: var(--padding-container);
	}
}
</style>
