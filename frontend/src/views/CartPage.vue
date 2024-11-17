<template>
	<div class="wide">
		<NavBar />

		<div class="card user-profile flex wrapper">
			<!-- Cart Items -->
			<div class="cart main">
				<div v-if="cartItems.length > 0">
					<div v-for="(item, index) in cartItems" :key="index" class="cart-item flex">
						<img :src="item.product.imageSrcs[0]" alt="Product Image" class="product-image" />
						<div class="product-info">
							<h3>{{ item.product.title }}</h3>
							<p>{{ formatCurrency(item.product.price) }}</p>
						</div>
						<div class="quantity-controls flex">
							<button class="quantity-btn" @click="updateCart(item.product.productId, -1)">
								−
							</button>
							<input class="quantity-input" v-model.number="item.quantity"
								@change="updateCart(item.product.productId, -item.quantity /*0 , item.quantity*/)"
								min="0" disabled />
							<button class="quantity-btn" @click="updateCart(item.product.productId, 1)">
								+
							</button>
						</div>
						<div class="product-total">
							<span>Total</span> <strong>{{ formatCurrency((item.product.price *
								item.quantity).toFixed(2)) }}</strong>
						</div>
					</div>
				</div>
				<p v-else>Your cart is empty.</p>
			</div>

			<!-- Checkout Form -->
			<div class="checkout sidebar">
				<h3>Total: </h3>
				<h2 class="final-price">{{ formatCurrency(cartTotal.toFixed(2)) }}</h2>
				<p>Cash on Delivery</p>

				<label for="address">Delivery Address</label>
				<input type="text" id="address" v-model="userAddress" placeholder="Enter delivery address"
					@input="validateAddress" />
				<div class="saved-addresses">
					<ul>
						<li v-for="(address, index) in addresses" :key="index">
							<div v-if="address !== userAddress">
								{{ address }}
								<button class="select-btn" :disabled="address === userAddress"
									@click="selectAddress(address)">
									Select
								</button>
							</div>
						</li>
					</ul>
				</div>

				<button class="checkout-btn primary-btn" :disabled="cartItems.length === 0 || !userAddress.trim()"
					@click="handleCheckout">
					placeholder
				</button>

			</div>
		</div>
	</div>
</template>

<script>
import NavBar from "@/components/NavBar.vue";
import User from "@/utils/User";
import axios from "axios";
import Env from "@/utils/Env";
import Lib from "@/utils/Lib";

export default {
	name: "CartPage",
	components: {
		NavBar,
	},
	data() {
		return {
			cartItems: [], // Items in the user's cart
			userAddress: "", // User's selected address for delivery
			addresses: [], // current addresses from user data
		};
	},
	computed: {
		// Calculate the total cost of the items in the cart
		cartTotal() {
			return this.cartItems.reduce((total, item) => {
				return total + item.product.price * item.quantity;
			}, 0);
		},
	},
	async mounted() {
		await this.fetchCartItems(); // Load the cart data
		await this.loadUserData(); // Load the user's saved address
	},
	methods: {
		formatCurrency(value) {
			return Lib.formatCurrency(value);
		},
		// Fetch cart data from the backend
		async fetchCartItems() {
			try {
				await User.fetchCartData(); // Fetch cart data via User
				this.cartItems = User.userCartItems; // Sync with User's cart items
			} catch (error) {
				console.error("Failed to fetch cart items:", error);
			}
		},
		// Fetch user address from the backend
		async loadUserData() {
			try {
				const { email, code } = User.getUserEmailCode();
				const response = await axios.get(`${Env.API_BASE_URL}/users`, {
					params: { email, code },
				});
				const user = response.data;
				this.addresses = user.addresses || [];
				this.userAddress = this.addresses.length > 0 ? this.addresses[0] : "";
			} catch (error) {
				console.error("Failed to load user data:", error);
			}
		},
		selectAddress(address) {
			this.userAddress = address;
		},
		validateAddress() {
			// Optional validation logic (e.g., check format) can be added here
		},
		// Update cart item quantity
		async updateCart(productId, change /*, quantity = null*/) {
			// console.log(change);
			try {
				// Use User.js to update the cart
				if (change < 0) {
					await User.removeFromCart(productId, this.$router);
				} else {
					await User.addToCart(productId, this.$router, change);
				}
				// Refresh local cart data after update
				this.cartItems = User.userCartItems;
			} catch (error) {
				console.error("Failed to update cart:", error);
			}
		},
		// Checkout the cart and create an order
		async handleCheckout() {
			if (!this.addresses.includes(this.userAddress)) {
				try {
					const { email, code } = User.getUserEmailCode();
					await axios.patch(`${Env.API_BASE_URL}/users`, {
						email,
						code,
						addresses: [...this.addresses, this.userAddress],
					});
					this.addresses.push(this.userAddress); // Update locally after successful update
				} catch (error) {
					console.error("Failed to update address:", error);
					alert("Failed to update address. Please try again.");
					return;
				}
			}
			await this.checkout();
		},
		async checkout() {
			try {
				const { email, code } = User.getUserEmailCode();
				await axios.post(`${Env.API_BASE_URL}/orders`, {
					email,
					code,
					deliveryAddress: this.userAddress,
				});

				this.cartItems = []; // Clear the cart
				this.userAddress = ""; // Clear the address
				this.$router.push("/profile")
			} catch (error) {
				console.error("Failed to place order:", error);
				alert("Failed to place the order. Please try again.");
			}
		},
	},
};
</script>
<style scoped lang="scss">
.cart {
	.cart-item {
		display: flex;
		align-items: center;
		margin-bottom: 30px;
		padding-bottom: 30px;
		border-bottom: 1px solid var(--border-color);

		.product-image {
			width: 100px;
			height: 100px;
			object-fit: cover;
			border-radius: 5px;
			margin-right: 20px;
		}

		.product-info {
			flex: 1;

			h3 {
				font-size: var(--font-size-content);
				margin: 0 0 5px 0;
			}

			p {
				font-size: var(--font-size-card);
				color: var(--light-text-color);
				margin: 0;
			}
		}

		.quantity-controls {
			display: flex;
			align-items: center;
			margin-right: 50px;
			gap: 15px;

			.quantity-btn {
				background: none;
				border: none;
				font-size: 20px;
				cursor: pointer;
				font-weight: 100;
				padding: 0;
				user-select: none;
			}

			.quantity-input {
				// width: 3em;				
				text-align: center;
				// border: 1px solid var(--border-color);
				border-radius: 5px;
				box-sizing: content-box;
				// padding: 10px 0px 10px 10px;
				font-size: 1.2em;
				font-weight: 600;
				width: 2em;
				padding: 0;
				background: none;
				border: none;
			}
		}

		.product-total {
			width: 100px;
			align-items: end;
			font-size: var(--font-size-content);
			font-weight: 400;
			display: flex;
			// column-gap: 1em;
			flex-direction: column;

			strong {
				font-weight: 400;
				color: var(--success-color);
				// font-size: 1.2em;
			}
		}
	}

	.cart-item:last-child {
		margin-bottom: 0;
		padding-bottom: 0;
		border-bottom: none;
	}
}

.checkout {
	h3 {
		font-size: 1.5em;
		margin: 0;
	}

	h2.final-price {
		font-size: var(--font-size-title);
		color: var(--success-color);
		font-weight: 300;
		margin: 0.5em 0 1em 0;
	}

	p {
		font-size: var(--font-size-content);
		margin: 2em 0 3em 0;
	}

	label {
		font-weight: bold;
	}

	input {
		width: 100%;
		margin: 1em 0;
	}

	.checkout-btn {
		width: 100%;
	}


	.saved-addresses {
		ul {
			list-style: none;
			padding: 0;
		}

		li {
			display: flex;
			justify-content: space-between;
			align-items: center;
			margin-bottom: 0.5em;
		}

		.select-btn {
			margin-left: 1em;
			padding: 0;
			border: none;
			background: none;
		}
	}


}
</style>
