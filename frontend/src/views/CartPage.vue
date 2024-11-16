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
							<p>${{ item.product.price }}</p>
						</div>
						<div class="quantity-controls flex">
							<button class="quantity-btn" @click="updateCart(item.product.productId, -1)">
								-
							</button>
							<input type="number" class="quantity-input" v-model.number="item.quantity"
								@change="updateCart(item.product.productId, 0, item.quantity)" min="1" />
							<button class="quantity-btn" @click="updateCart(item.product.productId, 1)">
								+
							</button>
						</div>
						<div class="product-total">
							<span>Total</span> <strong>${{ (item.product.price * item.quantity).toFixed(2) }}</strong>
						</div>
					</div>
				</div>
				<p v-else>Your cart is empty.</p>
			</div>

			<!-- Checkout Form -->
			<div class="checkout sidebar">
				<h3>Total: </h3>
				<h2 class="final-price">${{ cartTotal.toFixed(2) }}</h2>
				<p>Cash on Delivery</p>

				<label for="address">Delivery Address</label>
				<input type="text" id="address" v-model="userAddress" placeholder="Enter delivery address" />
				<button class="checkout-btn primary-btn" :disabled="cartItems.length === 0 || !userAddress.trim()"
					@click="checkout">
					Check Out
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

export default {
	name: "CartPage",
	components: {
		NavBar,
	},
	data() {
		return {
			cartItems: [], // Items in the user's cart
			userAddress: "", // User's address for delivery
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
		await this.loadUserAddress(); // Load the user's saved address
	},
	methods: {
		// Fetch cart data from the backend
		async fetchCartItems() {
			try {
				const { email, code } = User.getUser();
				const response = await axios.get(`${Env.API_BASE_URL}/carts`, {
					params: { email, code },
				});
				this.cartItems = response.data.items;
			} catch (error) {
				console.error("Failed to fetch cart items:", error);
			}
		},
		// Fetch user address from the backend
		async loadUserAddress() {
			try {
				const { email } = User.getUser();
				const response = await axios.get(`${Env.API_BASE_URL}/users/${email}/address`);
				this.userAddress = response.data.address || "";
			} catch (error) {
				console.error("Failed to load user address:", error);
			}
		},
		// Update cart item quantity
		async updateCart(productId, change, quantity = null) {
			try {
				const { email, code } = User.getUser();
				const requestData = {
					email,
					code,
					changeNumber: change,
					quantity: quantity !== null ? quantity : undefined,
				};
				const response = await axios.put(
					`${Env.API_BASE_URL}/carts/${productId}`,
					requestData
				);
				this.cartItems = response.data.items; // Update cart items
			} catch (error) {
				console.error("Failed to update cart:", error);
			}
		},
		// Checkout the cart and create an order
		async checkout() {
			try {
				const { email, code } = User.getUser();
				await axios.post(`${Env.API_BASE_URL}/orders`, {
					email,
					code,
					deliveryAddress: this.userAddress,
				});
				// const response = await axios.post(`${Env.API_BASE_URL}/orders`, {
				//     email,
				//     code,
				//     deliveryAddress: this.userAddress,
				// });

				// alert("Order placed successfully!");
				this.cartItems = []; // Clear the cart
				this.userAddress = ""; // Clear the address
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
			gap: 10px;

			.quantity-btn {
				background: none;
				border: none;
				font-size: 32px;
				cursor: pointer;
				font-weight: 100;
				padding: 0;
			}

			.quantity-input {
				width: 2em;
				text-align: center;
				border: 1px solid var(--border-color);
				border-radius: 5px;
				box-sizing: content-box;
				padding: 10px 0px 10px 10px;
			}
		}

		.product-total {
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

}
</style>
