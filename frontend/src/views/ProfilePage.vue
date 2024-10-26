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
					<h3>
						Address
						<a class="action" href="#" @click.prevent="showAddressInput = !showAddressInput">
							+ New
						</a>
					</h3>
					<div v-if="showAddressInput" class="new-address">
						<input v-model="newAddress" type="text" placeholder="Enter new address"
							@input="checkAddressEmpty" />
					</div>
					<ul v-if="addresses.length">
						<li v-for="(address, index) in addresses" :key="index">
							{{ address }} <a href="#" @click.prevent="removeAddress(index)">X</a>
						</li>
					</ul>
				</div>

				<!-- Update Profile Button -->
				<button class="primary-btn" @click="updateProfile" :disabled="isUpdateProfileDisabled">Update Profile</button>
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
			originalAddresses: [],
			code: localStorage.getItem('code'),
			userEmail: localStorage.getItem('email'),
			showAddressInput: false,
			isAddressEmpty: true,
			newAddress: '',

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
		checkAddressEmpty() {
			this.isAddressEmpty = this.newAddress.trim() === '';
		},

		addAddress() {
			if (this.isAddressEmpty) {
				alert('Please enter a valid address.');
				return;
			}
			if (this.addresses.includes(this.newAddress.trim())) {
				alert('Address already exists.');
				return;
			}
			this.addresses.push(this.newAddress.trim()); // Add locally
			this.newAddress = ''; // Clear input
			this.showAddressInput = false; // Hide input field
		},
		removeAddress(addressToRemove) {
			if (this.addresses.length <= 1) {
				alert('You must have at least one address.');
				return;
			}
			this.addresses = this.addresses.filter(addr => addr !== addressToRemove); // Remove locally
		},
		isUpdateProfileDisabled() {
			return true;
			// if (this.isUpdateProfileDisabled) {

			// }
			// originalAddresses
		},
		async updateProfile() {
			this.originalAddresses = [...this.addresses];
			this.addAddress();
			try {
				const updatedUser = {
					email: this.userEmail,
					addresses: this.addresses, // Use current addresses
				};

				const response = await fetch(
					`http://localhost:8080/api/user/update?email=${this.userEmail}&code=${this.code}`, // Send email and code as query params
					{
						method: 'PATCH',
						headers: { 'Content-Type': 'application/json' },
						body: JSON.stringify(updatedUser),
					}
				);

				if (!response.ok) throw new Error('Failed to update profile.');

				alert('Profile updated successfully!');
			} catch (error) {
				console.error('Error updating profile:', error);
				this.addresses = this.originalAddresses;
				alert('Failed to update profile.');
			}
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
