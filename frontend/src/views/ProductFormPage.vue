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
      },
      isEditMode: false,
    };
  },
  methods: {
    fetchProduct(id) {
      axios.get(`/api/products/${id}`)
        .then(response => {
          this.product = response.data;
        })
        .catch(error => {
          console.error('Error fetching product:', error);
        });
    },
    submitForm() {
      if (this.isEditMode) {
        axios.put(`/api/products/${this.product.id}`, this.product)
          .then(() => {
            this.$router.push({ name: 'ProductListPage' });
          })
          .catch(error => {
            console.error('Error updating product:', error);
          });
      } else {
        axios.post('/api/products', this.product)
          .then(() => {
            this.$router.push({ name: 'ProductListPage' });
          })
          .catch(error => {
            console.error('Error creating product:', error);
          });
      }
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
/* Basic styling */
</style>
