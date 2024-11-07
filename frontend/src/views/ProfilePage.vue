<template>
	<div class="wide">
		<NavBar />
		<div class="profile-container">
			<div class="orders">
				<h2>Orders</h2>
				<div v-for="(order, index) in orders" :key="index" class="order-item">
					<span :class="['status', order.status.toLowerCase()]">{{ order.status }}</span>
					<div class="order-content">
						<p class="order-detail">
							{{ order.date }} - {{ order.amount }}: {{ order.items }}
							<a v-if="order.status === 'Processing'" href="#" class="cancel-action">Cancel</a>
						</p>
						<p class="order-address">To: {{ order.address }}</p>
					</div>
				</div>
			</div>
			
			<div class="user-info">
				<h2>{{ email }}</h2>
				<p class="join-date">Joined {{ joinedDate }}</p>
				<div class="addresses">
					<h3>Address <a href="#" class="new-address-btn" @click.prevent="showAddressInput = !showAddressInput">+ New</a></h3>
					<div v-if="showAddressInput" class="new-address">
						<input v-model="newAddress" type="text" placeholder="Enter new address" @input="checkAddressEmpty" />
					</div>
					<ul v-if="addresses.length">
						<li v-for="(address, index) in addresses" :key="index">
							{{ address }} <a href="#" class="delete-address-btn" @click.prevent="removeAddress(address)">X</a>
						</li>
					</ul>
				</div>
				<button class="update-profile-btn" @click="updateProfile" :disabled="isUpdateProfileDisabled">
					{{ submitButtonText }}
				</button>
				<p v-if="updateMessage.length > 0" class="update-message">{{ updateMessage }}</p>
			</div>
		</div>
	</div>
</template>

<script>
import NavBar from '@/components/NavBar.vue';

export default {
	name: 'ProfilePage',
	components: {
		NavBar,
	},
	data() {
		return {
			// existing data properties
		};
	},
	methods: {
		// existing methods
	},
};
</script>

<style scoped lang="scss">
.profile-container {
	display: flex;
	gap: 2em;
	padding: 2em;
	background-color: #f9f9f9;
	border-radius: 8px;

	.orders {
		flex: 2;

		h2 {
			font-size: 1.5em;
			margin-bottom: 1em;
		}

		.order-item {
			display: flex;
			align-items: flex-start;
			padding: 1em;
			border-bottom: 1px solid #ddd;

			.status {
				width: 100px;
				font-weight: bold;
				text-transform: capitalize;

				&.shipped { color: gray; }
				&.shipping { color: green; }
				&.processing { color: black; }
				&.canceled { color: red; }
			}

			.order-content {
				flex-grow: 1;

				.order-detail {
					font-weight: bold;
					margin: 0 0 0.5em;
				}

				.order-address {
					color: #666;
				}

				.cancel-action {
					color: red;
					margin-left: 10px;
					text-decoration: none;
				}
			}
		}
	}

	.user-info {
		flex: 1;

		h2 {
			font-size: 1.5em;
		}

		.join-date {
			color: #666;
			font-size: 0.9em;
			margin-bottom: 1em;
		}

		.addresses {
			margin-top: 1em;

			.new-address-btn, .delete-address-btn {
				color: #0073e6;
				cursor: pointer;
				text-decoration: none;
			}

			.new-address input {
				width: 100%;
				padding: 0.5em;
				margin-top: 0.5em;
			}
		}

		.update-profile-btn {
			width: 100%;
			padding: 0.75em;
			background-color: #0073e6;
			color: #fff;
			border: none;
			border-radius: 4px;
			cursor: pointer;
			margin-top: 1em;
		}

		.update-message {
			margin-top: 0.5em;
			color: green;
		}
	}
}
</style>
