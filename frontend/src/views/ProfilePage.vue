<template>
    <div class="wide">
        <NavBar />

        <div class="card user-profile flex wrapper">
            <!-- Orders Section -->
            <div class="orders main">
                <h2>Orders</h2>
                <div v-if="orders.length === 0">No orders yet.</div>
                <div v-for="(order, index) in orders" :key="index" class="item">
                    <span :class="['status', order.status.toLowerCase()]">{{ order.status }}</span>
                    <div class="content">
                        <span class="detail">
                            {{ order.date }} - {{ order.amount }}: {{ order.items }}
                            <a v-if="order.status === 'Processing'" href="#" class="cancel action"
                                @click.prevent="cancelOrder(order.orderId)">Cancel</a>
                        </span>
                        <p class="address">To: {{ order.address }}</p>
                    </div>
                </div>
            </div>

            <!-- Profile Information Section -->
            <div class="info sidebar">
                <h2>{{ email }}</h2>
                <p class="dim-text">Joined {{ joinedDate }}</p>

                <div class="addresses section">
                    <h3>
                        Address
                        <a class="action" href="#" @click.prevent="toggleAddressInput">+ New</a>
                    </h3>
                    <div v-if="showAddressInput" class="new-address">
                        <input v-model="newAddress" type="text" placeholder="Enter your address"
                            @input="validateAddress" />
                    </div>
                    <ul v-if="addresses.length">
                        <li v-for="(address, index) in addresses" :key="index">
                            {{ address }}
                            <a href="#" class="action" title="delete" @click.prevent="removeAddress(address)">X</a>
                        </li>
                    </ul>
                </div>

                <button class="primary-btn update-profile-btn" @click="updateProfile"
                    :disabled="isUpdateProfileDisabled">
                    {{ submitButtonText }}
                </button>
				<button class="update-profile-btn" @click="logout">
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
            email: '',
            joinedDate: '',
            addresses: [],
            orders: [],
            newAddress: '',
            showAddressInput: false,
            isUpdateProfileDisabled: true,
            submitButtonText: 'Update Profile',
            updateMessage: '',
        };
    },
    async created() {
        if (!User.isLoggedIn()) {
            User.logout();
            this.$router.push('/login');
            return;
        }
        await this.fetchUserData();
        await this.fetchOrders();
    },
    methods: {
        async fetchUserData() {
            try {
                const { email, code } = User.getUser();
                const response = await fetch(`${Env.API_BASE_URL}/users?email=${email}&code=${code}`);
                if (!response.ok) throw new Error('Failed to fetch user data');

                const userData = await response.json();
                this.email = userData.email;
                this.joinedDate = new Date(userData.createdAt).toLocaleDateString('en-US', {
                    year: 'numeric',
                    month: 'long',
                    day: 'numeric',
                });
                this.addresses = userData.addresses || [];
            } catch (error) {
                console.error('Error fetching user data:', error);
                User.logout();
                this.$router.push('/login');
            }
        },
        async fetchOrders() {
            try {
                const { email, code } = User.getUser();
                const response = await fetch(`${Env.API_BASE_URL}/orders?email=${email}&code=${code}`);
                if (!response.ok) throw new Error('Failed to fetch orders');

                const orderData = await response.json();
                this.orders = orderData.map(order => ({
                    orderId: order.orderId,
                    status: order.orderStatus,
                    date: new Date(order.createdAt).toLocaleDateString('en-US'),
                    amount: `$${order.totalPrice.toFixed(2)}`,
                    items: JSON.parse(order.productListJson) // Parse product list JSON
                        .map(item => `${item.quantity}x ${item.title}`)
                        .join(', '),
                    address: order.deliveryAddress,
                }));
            } catch (error) {
                console.error('Error fetching orders:', error);
            }
        },
        toggleAddressInput() {
            this.showAddressInput = !this.showAddressInput;
        },
        validateAddress() {
            this.isUpdateProfileDisabled = this.newAddress.trim() === '';
            this.submitButtonText = this.newAddress.trim() ? 'Add Address' : 'Update Profile';
            this.updateMessage = '';
        },
        addAddress() {
            if (!this.newAddress.trim() || this.addresses.includes(this.newAddress.trim())) {
                this.updateMessage = 'Address is invalid or already exists.';
                return;
            }
            this.addresses.push(this.newAddress.trim());
            this.newAddress = '';
            this.showAddressInput = false;
            this.isUpdateProfileDisabled = false;
        },
        removeAddress(address) {
            this.addresses = this.addresses.filter(a => a !== address);
            this.isUpdateProfileDisabled = false;
            this.updateMessage = '';
        },
        async updateProfile() {
            try {
                const { email, code } = User.getUser();
                const response = await fetch(`${Env.API_BASE_URL}/users?email=${email}&code=${code}`, {
                    method: 'PATCH',
                    headers: { 'Content-Type': 'application/json' },
                    body: JSON.stringify({ addresses: this.addresses }),
                });
                if (!response.ok) throw new Error('Failed to update profile');

                this.updateMessage = 'Profile updated successfully.';
                this.isUpdateProfileDisabled = true;
                this.submitButtonText = 'Update Profile';
            } catch (error) {
                console.error('Error updating profile:', error);
                this.updateMessage = 'Failed to update profile.';
            }
        },
		logout() {
			User.logout(this.$router);
		},
        async cancelOrder(orderId) {
            try {
                const { email, code } = User.getUser();
                const response = await fetch(`${Env.API_BASE_URL}/orders/${orderId}?email=${email}&code=${code}`, {
                    method: 'PUT',
                    headers: { 'Content-Type': 'application/json' },
                });
                if (!response.ok) throw new Error('Failed to cancel order');

                this.updateMessage = 'Order canceled successfully.';
                await this.fetchOrders();
            } catch (error) {
                console.error('Error canceling order:', error);
                this.updateMessage = 'Failed to cancel order.';
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
}
</style>
