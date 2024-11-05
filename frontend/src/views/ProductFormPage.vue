<template>
  <div class="product-form">
    <h1>{{ isEditMode ? 'Edit Product' : 'Add New Product' }}</h1>
    <form @submit.prevent="submitForm">
      <label for="name">Name:</label>
      <input type="text" v-model="product.name" required />

      <label for="price">Price:</label>
      <input type="number" v-model="product.price" required />

      <label for="description">Description:</label>
      <textarea v-model="product.description"></textarea>

      <label for="category">Category:</label>
      <input type="text" v-model="product.category" required />

      <label for="image">Image URL:</label>
      <input type="text" v-model="product.image" required />

      <button type="submit">{{ isEditMode ? 'Update Product' : 'Create Product' }}</button>
    </form>
  </div>
</template>

<script>
import axios from 'axios';

export default {
  data() {
    return {
      product: {
        name: '',
        price: '',
        description: '',
        category: '',
        image: '',
      },
      isEditMode: false,
    };
  },
  methods: {
    fetchProduct(id) {
      axios.get(`/api/product/get/${id}`)
        .then(response => {
          this.product = response.data;
        })
        .catch(error => {
          console.error('Error fetching product:', error);
        });
    },
    submitForm() {
      const url = this.isEditMode ? `/api/product/edit/${this.product.id}` : '/api/product/create';
      const method = this.isEditMode ? 'put' : 'post';

      axios({ method, url, data: this.product })
        .then(() => {
          this.$router.push({ name: 'productList' });
        })
        .catch(error => {
          console.error('Error submitting product:', error);
        });
    },
  },
  created() {
    if (this.$route.params.id) {
      this.isEditMode = true;
      this.fetchProduct(this.$route.params.id);
    }
  },
};
</script>

<style scoped>
/* Add styling as needed */
</style>
