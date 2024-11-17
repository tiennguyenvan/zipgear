<template>
	<div class="wide">
		<NavBar />

		<div class="card user-profile flex wrapper">

			<div class="orders main">

				<h2>{{ isAdmin() ? "Orders" : "All Orders" }}</h2>
				<div v-if="orders.length === 0">No orders yet.</div>
				<div v-for="(order, index) in orders" :key="index" class="item">
					<div class="status-wrapper">
						<span v-if="!isAdmin()" :class="['status', order.status.toLowerCase()]">{{ order.status }}</span>

						<select v-if="isAdmin()" v-model="order.status"
							@change="updateOrderStatus(order.orderId, order.status)">
							<option v-for="(status, sindex) in orderStatuses" :key="sindex" :value="status"
								:selected="order.status == status">{{ status }}</option>
						</select>

					</div>
					<div class="info">
						<span class="detail">
							{{ order.date }} {{ order.amount }}: {{ order.items }}

							<a v-if="!isAdmin() && order.status === 'Processing'" href="#" class="cancel action"
								@click.prevent="confirmCancelOrder(order.orderId)">Cancel</a>
						</span>
						<p class="address">To: {{ order.address }}</p>
					</div>
				</div>
			</div>

			<div class="info sidebar">
				<h3 v-if="isAdmin()">Admin Dashboard</h3>
				<h2 class="user-name">{{ isAdmin() ? email.split('@')[0] : email }}</h2>
				<p class="dim-text">Joined {{ joinedDate }}</p>

				<div v-if="!isAdmin()" class="addresses section">
					<h3>
						Address
						<a class="action" href="#" @click.prevent="showAddressInput = !showAddressInput">
							+ New
						</a>
					</h3>
					<div v-if="showAddressInput" class="new-address">
						<input v-model="newAddress" type="text" placeholder="700 Royal Ave, New Westminster, BC"
							@input="checkAddressEmpty" />
					</div>
					<ul v-if="addresses.length">
						<li v-for="(address, index) in addresses" :key="index">
							{{ address }} <a href="#" class="action" title="delete"
								@click.prevent="removeAddress(address)">X</a>
						</li>
					</ul>
				</div>

				<button v-if="!isAdmin()" class="primary-btn update-profile-btn" @click="updateProfile"
					:disabled="isUpdateProfileDisabled">
					{{ submitButtonText }}
				</button>
				<button class="logout-profile-btn" @click="logoutProfile">
					Logout
				</button>

				<p v-if="updateMessage.length > 0">{{ updateMessage }}</p>
			</div>
		</div>
	</div>
</template>

<script>
import NavBar from '@/components/NavBar.vue';
import Env from '@/utils/Env';
import User from '@/utils/User';

export default {
	name: 'UserProfile',
	components: {
		NavBar,
	},
	data() {
		return {
			email: "",
			joinedDate: '',
			addresses: [],
			originalAddresses: [],
			showAddressInput: false,
			isAddressEmpty: true,
			newAddress: '',
			updateMessage: '',
			isUpdateProfileDisabled: true,
			submitButtonText: 'Update Profile',
			// Sync with backend, don't change
			orderStatuses: ['PROCESSING', 'SHIPPING', 'DELIVERED', 'RETURNED', 'CANCELLED'],
			orders: [],
		};
	},
	async mounted() {
		await this.fetchUserData();
		await this.fetchOrders();
	},
	methods: {
		async confirmCancelOrder(orderId) {
			const confirmCancel = window.confirm("Are you sure you want to cancel this order?");
			if (confirmCancel) {
				await this.cancelOrder(orderId);
			}
		},
		async updateOrderStatus(orderId, newStatus) {
			try {
				const { email, code } = User.getUserEmailCode();
				const response = await fetch(
					`${Env.API_BASE_URL}/orders/${orderId}`,
					{
						method: "PUT",
						headers: { "Content-Type": "application/json" },
						body: JSON.stringify({ orderStatus: newStatus, email, code }),
					}
				);
				if (!response.ok) throw new Error("Failed to update order status.");
				this.updateMessage = "Order status updated successfully.";
				await this.fetchOrders(); // Refresh orders after update
			} catch (error) {
				console.error("Error updating order status:", error);
				this.updateMessage = "Failed to update order status.";
			}
		},
		isAdmin() {
			if (!this.$route.params.id) {
				return false;
			}
			console.log(this.$route.params.id)
			return User.isLoggedInAdmin();
		},
		async fetchUserData() {
			// console.log(this.$router);
			try {
				const { email, code } = User.getUserEmailCode();
				const response = await fetch(`${Env.API_BASE_URL}/users?email=${email}&code=${code}`);
				if (!response.ok) throw new Error("Failed to fetch user data");

				const userData = await response.json();
				this.email = userData.email;
				this.joinedDate = new Date(userData.createdAt).toLocaleDateString("en-US", {
					year: "numeric",
					month: "long",
					day: "numeric",
				});
				this.addresses = userData.addresses || [];
				// this.originalAddresses = [...this.addresses];
			} catch (error) {
				console.log("Error fetching user data:", error);
				// console.log(this.$router);

				User.logoutAtLoginPage(this.$router);
				// this.$router.push("/login");
			}
		},
		async fetchOrders() {
			try {
				const { email, code } = User.getUserEmailCode();
				const response = await fetch(`${Env.API_BASE_URL}/orders?email=${email}&code=${code}`);
				if (!response.ok) throw new Error("Failed to fetch orders");

				const orderData = await response.json();
				this.orders = orderData.map((order) => ({
					orderId: order.orderId,
					status: order.orderStatus,
					date: new Date(order.createdAt).toLocaleDateString("en-US"),
					amount: `$${order.totalPrice.toFixed(2)}`,
					items: JSON.parse(order.productListJson)
						.map((item) => `${item.quantity}x ${item.title}`)
						.join(", "),
					address: order.deliveryAddress,
				}));
			} catch (error) {
				console.error("Error fetching orders:", error);
			}
		},
		checkAddressEmpty() {
			this.isAddressEmpty = this.newAddress.trim() === '';
			if (!this.isAddressEmpty) {
				this.isUpdateProfileDisabled = false;
			}
			if (!this.isAddressEmpty) {
				this.submitButtonText = 'Add New Address';
			}
			this.updateMessage = '';
		},

		addAddress() {
			if (this.isAddressEmpty) {
				return;
			}
			if (this.newAddress.trim() === '') {
				return;
			}
			if (this.addresses.includes(this.newAddress.trim())) {
				this.updateMessage = 'Address already exists.';
				this.isUpdateProfileDisabled = true;
				return;
			}
			this.addresses.push(this.newAddress.trim()); // Add locally
			this.newAddress = ''; // Clear input
			this.showAddressInput = false; // Hide input field			
		},
		removeAddress(addressToRemove) {
			if (this.addresses.length <= 1) {
				this.updateMessage = 'You must have at least one address.';
				return;
			}
			this.addresses = this.addresses.filter(addr => addr !== addressToRemove); // Remove locally
			this.isUpdateProfileDisabled = false;
			this.updateMessage = '';
			if (this.isAddressEmpty) {
				this.submitButtonText = 'Update Profile';
				return;
			}
			this.submitButtonText = 'Add Address and Update';
		},

		logoutProfile() {
			User.logoutAtLoginPage(this.$router);
		},
		async updateProfile() {
			this.originalAddresses = [...this.addresses]
			this.addAddress();
			if (this.isUpdateProfileDisabled) return;

			try {
				const { email, code } = User.getUserEmailCode();
				const response = await fetch(`${Env.API_BASE_URL}/users`, {
					method: "PATCH",
					headers: { "Content-Type": "application/json" },
					body: JSON.stringify({ addresses: this.addresses, email, code }),
				});
				if (!response.ok) throw new Error("Failed to update profile");

				this.originalAddresses = [...this.addresses];
				this.newAddress = '';
				this.isUpdateProfileDisabled = true;
				this.updateMessage = "Profile updated successfully.";
				this.isUpdateProfileDisabled = true;
				this.submitButtonText = "Update Profile";
			} catch (error) {
				console.log("Error updating profile:", error);
				this.addresses = [...this.originalAddresses];
				this.updateMessage = "Failed to update profile.";
			}
		},
		async cancelOrder(orderId) {
			try {
				const { email, code } = User.getUserEmailCode();
				const response = await fetch(
					`${Env.API_BASE_URL}/orders/${orderId}?email=${email}&code=${code}`,
					{ method: "PUT" }
				);
				if (!response.ok) throw new Error("Failed to cancel order");

				this.updateMessage = "Order canceled successfully.";
				await this.fetchOrders();
			} catch (error) {
				console.log("Error canceling order:", error);
				this.updateMessage = "Failed to cancel order.";
			}
		},
	},
};
</script>

<style scoped lang="scss">
.user-profile {
	align-items: start;

	.orders {

		.item {
			display: flex;
			align-items: start;
			margin-top: var(--spacing-element-big);			

			.status-wrapper {
				width: 120px;
				font-weight: bold;
				font-size: 12px;

				.status {
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
				select {
					font-size: 12px;
					font-weight: bold;
				}
			}

			.info {
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

		.addresses {
			ul {
				padding: 0;

				li {
					margin-top: var(--spacing-element-small);
					display: flex;
					align-items: center;
					gap: 0.5em;
				}
			}


		}
	}

	.update-profile-btn {
		width: 100%;
		margin-top: var(--spacing-element);
	}

	.logout-profile-btn {
		width: 100%;
		margin-top: var(--spacing-element);
	}
}
</style>
