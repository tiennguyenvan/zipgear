<template>
  <div class="product-list">
    <h1>Product Management</h1>
    <div class="filters">
      <input v-model="keyword" placeholder="Search by keyword" @input="fetchProducts" />
      <select v-model="priceOrder" @change="fetchProducts">
        <option value="">Sort by Price</option>
        <option value="asc">Price: Low to High</option>
        <option value="desc">Price: High to Low</option>
      </select>
      <select v-model="ratingOrder" @change="fetchProducts">
        <option value="">Sort by Rating</option>
        <option value="asc">Rating: Low to High</option>
        <option value="desc">Rating: High to Low</option>
      </select>
    </div>
    <button @click="navigateToCreate">Add New Product</button>
    <table>
      <thead>
        <tr>
          <th>Name</th>
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
// import axios from 'axios';

// export default {
//   data() {
//     return {
//       products: [],
//       keyword: '',
//       priceOrder: '',
//       ratingOrder: '',
//     };
//   },
//   methods: {
//     fetchProducts() {
//       axios.get('/api/product/get-all', {
//         params: {
//           keyword: this.keyword,
//           priceOrder: this.priceOrder,
//           ratingOrder: this.ratingOrder,
//         },
//       })
//         .then(response => {
//           this.products = response.data;
//         })
//         .catch(error => {
//           console.error('Error fetching products:', error);
//         });
//     },
//     navigateToCreate() {
//       this.$router.push({ name: 'productCreate' });
//     },
//     editProduct(productId) {
//       this.$router.push({ name: 'productEdit', params: { id: productId } });
//     },
//     deleteProduct(productId) {
//       axios.delete(`/api/product/delete/${productId}`)
//         .then(() => {
//           this.fetchProducts();
//         })
//         .catch(error => {
//           console.error('Error deleting product:', error);
//         });
//     },
//   },
//   mounted() {
//     this.fetchProducts();
//   },
// };
</script>

<style scoped>
/* Add styling as needed */
</style>

