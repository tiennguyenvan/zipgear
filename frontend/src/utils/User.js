import axios from "axios";
import Env from "./Env";
import { ref } from "vue";

const User = {
	// Plain properties
	userCartProductIds: [], // IDs of products in the cart
	userCartItems: [], // Complete cart items
	isInitialized: false, // Tracks if initialization has been done
	cartCount: ref(0),

	// Check if user is logged in
	isLoggedIn() {
		return !!localStorage.getItem(Env.K_EMAIL) && !!localStorage.getItem(Env.K_CODE);
	},
	isLoggedInAdmin() {
		return this.isLoggedIn && this.getUserEmailCode().email == Env.ADMIN_EMAIL
	},

	// Login/Logout methods
	login(email, code) {
		localStorage.setItem(Env.K_EMAIL, email);
		localStorage.setItem(Env.K_CODE, code);
	},

	logout() {
		localStorage.removeItem(Env.K_EMAIL);
		localStorage.removeItem(Env.K_CODE);
		this.userCartProductIds = [];
		this.userCartItems = [];
		this.isInitialized = false;
		this.cartCount.value = 0;
	},
	logoutAtLoginPage(router, productId) {		
		this.logout();
		if (!router) {
			console.log("empty router")
			return;
		}
		if (productId) {
			router.push("/login/add-to-cart/" + productId);
		} else {
			router.push("/login");
		}
	},

	// Get user credentials
	getUserEmailCode() {
		return {
			email: localStorage.getItem(Env.K_EMAIL),
			code: localStorage.getItem(Env.K_CODE),
		};
	},

	// Ensure cart data is initialized
	async init() {
		if (!this.isInitialized) {
			await this.fetchCartData();
			this.isInitialized = true;
		}
	},

	// Fetch cart data from the backend
	async fetchCartData() {
		const { email, code } = this.getUserEmailCode();

		if (!email || !code) {
			// console.error("Missing email or code for cart request:", { email, code });
			return;
		}

		try {
			const response = await axios.get(`${Env.API_BASE_URL}/carts`, {
				params: { email, code },
			});

			// Update cart data
			this.userCartItems = response.data.items;
			this.userCartProductIds = response.data.items.map((item) => item.product.productId);
			this.cartCount.value = this.userCartProductIds.length;

		} catch (error) {
			console.error("Failed to fetch cart data:", error);
		}
	},

	// Add a product to the cart
	async removeFromCart(productId, router) {
		await this.addToCart(productId, router, -1);
	},
	async addToCart(productId, router, change = 1) {
		if (!this.isLoggedIn()) {
			this.logoutAtLoginPage(router, productId);
			return;
		}
		await this.init();

		const { email, code } = this.getUserEmailCode();
		try {
			await axios.put(`${Env.API_BASE_URL}/carts/${productId}`, {
				email,
				code,
				changeNumber: change, // Add one item by default
			});

			await this.fetchCartData(); // Refresh cart data
		} catch (error) {
			console.error("Failed to add product to cart:", error);
		}
	},

	// Utility to check if a product is in the cart
	isProductInCart(productId) {
		return this.userCartProductIds.includes(productId);
	},

	// Utility to get button text for "Add to Cart"
	getAddToCartText(productId) {
		if (!this.isLoggedIn()) {
			return "Login to Add";
		} else if (this.isProductInCart(productId)) {
			return "✓ Add More to Cart";
		} else {
			return "Add to Cart";
		}
	},

	// Cart count
	getCartCount() {
		return this.userCartProductIds.length;
	},
};

export default User;
