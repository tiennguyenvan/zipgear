<template>
  <div class="product-list">
    <h1>Product Management</h1>
    <button @click="navigateToCreate">Add New Product</button>
    <table>
      <thead>
        <tr>
          <th>Product Name</th>
          <th>Price</th>
          <th>Actions</th>
        </tr>
      </thead>
      <tbody>
        <tr v-for="product in products" :key="product.id">
          <td>{{ product.name }}</td>
          <td>{{ product.price }}</td>
          <td>
            <button @click="editProduct(product.id)">Edit</button>
            <button @click="deleteProduct(product.id)">Delete</button>
          </td>
        </tr>
      </tbody>
    </table>
  </div>
</template>

<script>
import axios from 'axios';

export default {
  data() {
    return {
      products: [],
    };
  },
  methods: {
    fetchProducts() {
      axios.get('/api/products')
        .then(response => {
          this.products = response.data;
        })
        .catch(error => {
          console.error('Error fetching products:', error);
        });
    },
    navigateToCreate() {
      this.$router.push({ name: 'ProductFormPage' });
    },
    editProduct(productId) {
      this.$router.push({ name: 'ProductFormPage', params: { id: productId } });
    },
    deleteProduct(productId) {
      axios.delete(`/api/products/${productId}`)
        .then(() => {
          this.fetchProducts();
        })
        .catch(error => {
          console.error('Error deleting product:', error);
        });
    },
  },
  mounted() {
    this.fetchProducts();
  },
};
</script>

<style scoped>
/* Basic styling */
</style>
