// src/stores/productStore.js
// import { defineStore } from 'pinia';

// export const useProductStore = defineStore('productStore', {
//   state: () => ({
//     products: JSON.parse(localStorage.getItem('products')) || []
//   }),
  
//   actions: {
//     addProduct(product) {
//       this.products.push(product);
//       this.saveToLocalStorage();
//     },
//     editProduct(updatedProduct) {
//       const index = this.products.findIndex(p => p.id === updatedProduct.id);
//       if (index !== -1) {
//         this.products[index] = updatedProduct;
//         this.saveToLocalStorage();
//       }
//     },
//     deleteProduct(productId) {
//       this.products = this.products.filter(p => p.id !== productId);
//       this.saveToLocalStorage();
//     },
//     loadDemoData() {
//       if (this.products.length === 0) {
//         this.products = [
//           { id: 1, name: 'Laptop', description: 'Powerful laptop', price: 1000, image: 'url1', category: 'Electronics' },
//           { id: 2, name: 'Headphones', description: 'Noise-canceling', price: 200, image: 'url2', category: 'Audio' },
//           { id: 3, name: 'Coffee Maker', description: 'Brews fresh coffee', price: 80, image: 'url3', category: 'Appliances' }
//         ];
//         this.saveToLocalStorage();
//       }
//     },
//     saveToLocalStorage() {
//       localStorage.setItem('products', JSON.stringify(this.products));
//     }
//   }
// });
