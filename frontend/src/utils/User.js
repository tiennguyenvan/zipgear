import { reactive } from "vue";
import axios from "axios";
import Env from "./Env";

const User = {
    state: reactive({
        userCartProductIds: [], // Stores IDs of products in the cart
        isInitialized: false,  // Tracks if initialization has been done
    }),

    isLoggedIn() {
        return !!localStorage.getItem(Env.K_EMAIL) && !!localStorage.getItem(Env.K_CODE);
    },

    login(email, code) {
        localStorage.setItem(Env.K_EMAIL, email);
        localStorage.setItem(Env.K_CODE, code);
    },

    logout(router) {
        localStorage.removeItem(Env.K_EMAIL);
        localStorage.removeItem(Env.K_CODE);
        this.state.userCartProductIds = [];
        this.state.isInitialized = false;
        router.push("/");
    },

    getUser() {
        return {
            email: localStorage.getItem(Env.K_EMAIL),
            code: localStorage.getItem(Env.K_CODE),
        };
    },

    async ensureInitialized(router) {
        if (!this.state.isInitialized) {
            if (!this.isLoggedIn()) {
                router.push("/login");
                return;
            }
            await this.fetchCartData();
            this.state.isInitialized = true;
        }
    },

    async fetchCartData() {
        const { email, code } = this.getUser();

        if (!email || !code) {
            console.error("Missing email or code for cart request:", { email, code });
            return [];
        }

        try {
            const response = await axios.get(`${Env.API_BASE_URL}/carts`, {
                params: { email, code },
            });
            this.state.userCartProductIds = response.data.items.map(
                (item) => item.product.productId
            );
        } catch (error) {
            console.error("Failed to fetch cart data:", error);
        }
    },

    async addToCart(productId, router) {
        await this.ensureInitialized(router);

        try {
            const { email, code } = this.getUser();
            await axios.put(`${Env.API_BASE_URL}/carts/${productId}`, {
                email,
                code,
                changeNumber: 1,
            });

            await this.fetchCartData(); // Refresh cart data after adding a product
        } catch (error) {
            console.error("Failed to add product to cart:", error);
        }
    },

    async isProductInCart(productId, router) {
        await this.ensureInitialized(router);
        return this.state.userCartProductIds.includes(productId);
    },

    async getAddToCartText(productId, router) {
        await this.ensureInitialized(router);

        if (!this.isLoggedIn()) {
            return "Login to Add";
        } else if (await this.isProductInCart(productId, router)) {
            return "✓ Add More to Cart";
        } else {
            return "Add to Cart";
        }
    },

    get cartCount() {
        return this.state.userCartProductIds.length;
    },
};

export default User;
