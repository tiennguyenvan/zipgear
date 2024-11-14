<template>
  <div class="cart-page">
    <h1>Your Cart</h1>

    <!-- Loading message -->
    <div v-if="loading">Loading your cart...</div>
    <!-- Message when cart is empty -->
    <div v-else-if="cart.items.length === 0">Your cart is empty.</div>

    <!-- Cart items and controls -->
    <div v-else>
      <div class="cart-item" v-for="item in cart.items" :key="item.product.productId">
        <div class="product-info">
          <h3>{{ item.product.name }}</h3>
          <p>{{ item.product.description }}</p>
          <p>Price: {{ item.product.price | currency }}</p>
        </div>

        <!-- Quantity control buttons -->
        <div class="quantity">
          <button @click="updateQuantity(item.product.productId, -1)" :disabled="item.quantity <= 1">-</button>
          <span>{{ item.quantity }}</span>
          <button @click="updateQuantity(item.product.productId, 1)">+</button>
        </div>
      </div>

      <!-- Cart summary section -->
      <div class="cart-summary">
        <p>Total items: {{ totalItems }}</p>
        <p>Total cost: {{ totalCost | currency }}</p>
      </div>
    </div>
  </div>
</template>

<script>
import axios from 'axios';

export default {
  data() {
    return {
      cart: { items: [] },
      loading: true,
      userId: null, // Placeholder for user ID; adjust this as needed
    };
  },
  computed: {
    // Computes total quantity of items in cart
    totalItems() {
      return this.cart.items.reduce((acc, item) => acc + item.quantity, 0);
    },
    // Computes total cost of all items in cart
    totalCost() {
      return this.cart.items.reduce((acc, item) => acc + item.quantity * item.product.price, 0);
    },
  },
  filters: {
    // Format price as currency
    currency(value) {
      return `$${parseFloat(value).toFixed(2)}`;
    },
  },
  created() {
    // Fetch the cart when the component is created
    this.fetchCart();
  },
  methods: {
    // Fetch the cart for the logged-in user
    async fetchCart() {
      try {
        const response = await axios.get("/api/carts", {
          data: { userId: this.userId }, // Adjust as needed to provide user ID in request
        });
        this.cart = response.data;
      } catch (error) {
        console.error("Failed to load cart:", error);
      } finally {
        this.loading = false;
      }
    },
    // Update item quantity in the cart
    async updateQuantity(productId, changeNumber) {
      try {
        const response = await axios.put(`/api/carts/${productId}`, {
          userId: this.userId, // Adjust to include user ID if necessary
          changeNumber,
        });
        this.cart = response.data; // Update cart with response
      } catch (error) {
        console.error("Failed to update quantity:", error);
      }
    },
  },
};
</script>

<style scoped>
.cart-page {
  padding: 20px;
}

.cart-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 15px;
  padding: 15px;
  border: 1px solid #ccc;
  border-radius: 5px;
}

.product-info {
  flex: 3;
}

.quantity {
  display: flex;
  align-items: center;
  gap: 10px;
}

.quantity button {
  padding: 5px 10px;
  font-size: 1rem;
}

.cart-summary {
  margin-top: 20px;
  font-weight: bold;
}
</style>
