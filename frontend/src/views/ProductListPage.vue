<template>
  <div class="wide">
    <NavBar />

    <div class="card flex wrapper">
      <div class="products main">
        <div v-for="product in products" :key="product.id" class="product-card">
          <img
            :src="product.imageSrcs[0]"
            :alt="product.name"
            class="product-image"
          />
          <p class="product-price">${{ product.price }}</p>
          <h3 class="product-name">{{ product.title }}</h3>
         
          <p class="product-rating">{{ product.averageRating }} ★</p>
          <a href="#" class="add-to-cart">Add to Cart</a>
        </div>
      </div>

      <div class="sidebar">
        <div class="product-filters">
          <!-- Search Keywords -->
          <input
            type="text"
            v-model="filters.keyword"
            placeholder="Search Keywords"
            class="search-input"
          />

          <!-- Price Filter -->
          <div class="filter-group">
            <label>Price:</label>
            <button
              @click="sortPrice('all')"
              :class="{ active: filters.priceSort === 'all' }"
            >
              All
            </button>
            <button
              @click="sortPrice('high')"
              :class="{ active: filters.priceSort === 'high' }"
            >
              High First
            </button>
            <button
              @click="sortPrice('low')"
              :class="{ active: filters.priceSort === 'low' }"
            >
              Low First
            </button>
            <div class="range-inputs">
              <input
                type="number"
                v-model="filters.minPrice"
                placeholder="Min"
                @input="validateNonNegative('minPrice')"
              />
              <input
                type="number"
                v-model="filters.maxPrice"
                placeholder="Max"
                @input="validateNonNegative('maxPrice')"
              />
            </div>
          </div>

          <!-- Rating Filter -->
          <div class="filter-group">
            <label>Rating:</label>
            <button
              @click="sortRating('all')"
              :class="{ active: filters.ratingSort === 'all' }"
            >
              All
            </button>
            <button
              @click="sortRating('high')"
              :class="{ active: filters.ratingSort === 'high' }"
            >
              High First
            </button>
            <button
              @click="sortRating('low')"
              :class="{ active: filters.ratingSort === 'low' }"
            >
              Low First
            </button>
            <div class="range-inputs">
              <input
                type="number"
                v-model="filters.minRating"
                placeholder="Min"
                @input="validateNonNegative('minRating')"
              />
              <input
                type="number"
                v-model="filters.maxRating"
                placeholder="Max"
                @input="validateNonNegative('maxRating')"
              />
            </div>
          </div>

          <!-- Stock Filter -->
          <div class="filter-group">
            <label>Stock:</label>
            <button
              @click="setStock('all')"
              :class="{ active: filters.stock === 'all' }"
            >
              All
            </button>
            <button
              @click="setStock('in')"
              :class="{ active: filters.stock === 'in' }"
            >
              In-Stock
            </button>
            <button
              @click="setStock('out')"
              :class="{ active: filters.stock === 'out' }"
            >
              Out-Stock
            </button>
          </div>

          <!-- Clear Filters Button -->
          <button @click="clearFilters" class="clear-button">
            Clear All Filters
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import axios from "axios";
import NavBar from "@/components/NavBar.vue";
import Env from "@/utils/Env";

export default {
  name: "ProductListPage",
  components: {
    NavBar,
  },
  data() {
    return {
      adminEmail: Env.ADMIN_EMAIL,
      // Products JSON data
      products: [],
      filters: {
        keyword: "",
        priceSort: "all",
        minPrice: null,
        maxPrice: null,
        ratingSort: "all",
        minRating: null,
        maxRating: null,
        stock: "all",
      },
    };
  },
  mounted() {
    this.fetchProducts();
  },
  methods: {
    async fetchProducts() {
      // Build query parameters based on filters
      let params = {
        keyword: this.filters.keyword || undefined,
        priceOrder:
          this.filters.priceSort === "high"
            ? "desc"
            : this.filters.priceSort === "low"
            ? "asc"
            : undefined,
        ratingOrder:
          this.filters.ratingSort === "high"
            ? "desc"
            : this.filters.ratingSort === "low"
            ? "asc"
            : undefined,
        minPrice: this.filters.minPrice || undefined,
        maxPrice: this.filters.maxPrice || undefined,
        minRating: this.filters.minRating || undefined,
        maxRating: this.filters.maxRating || undefined,
        inStock:
          this.filters.stock === "in"
            ? true
            : this.filters.stock === "out"
            ? false
            : undefined,
      };

      try {
        // Make the GET request with query parameters
        const response = await axios.get("http://localhost:8080/api/products", {
          params,
        });

        const productIds = response.data.map((product) => product.productId);

        // Fetch each product by ID
        const productsData = await Promise.all(
          productIds.map(async (id) => {
            const productResponse = await axios.get(
              `http://localhost:8080/api/products/${id}`
            );
            return productResponse.data;
          })
        );

        // Set the fetched products to the products data property
        this.products = productsData;
      } catch (error) {
        console.error("Error fetching products:", error);
      }
    },

    sortPrice(order) {
      this.filters.priceSort = order;
      this.fetchProducts();
    },
    sortRating(order) {
      this.filters.ratingSort = order;
      this.fetchProducts();
    },
    setStock(status) {
      this.filters.stock = status;
      this.fetchProducts();
    },
    clearFilters() {
      this.filters = {
        keyword: "",
        priceSort: "all",
        minPrice: null,
        maxPrice: null,
        ratingSort: "all",
        minRating: null,
        maxRating: null,
        stock: "all",
      };
      this.fetchProducts();
    },
    validateNonNegative(field) {
      if (this.filters[field] < 0) {
        this.filters[field] = 0;
      }
    },
  },
};
</script>

<style scoped lang="scss">
.product-list {
  text-align: center;
  padding: 20px;
}

.products {
  display: flex;
  flex-wrap: wrap; /* Allows items to wrap to the next line */
  justify-content: center; /* Centers items horizontally */
  gap: 20px; /* Space between cards */
}

.product-card {
  text-align: center;
  width: 200px;
  margin: 10px; /* Optional: Space around each card */
}

.product-card:hover {
  transform: scale(1.05);
}

.product-image {
  width: 100%;
  height: auto;
  border-radius: 8px;
}

.product-name {
  font-size: 16px;
  font-weight: bold;
  margin-top: 5px;
  margin-bottom: 10px;
}

.product-price {
  font-size: 18px;
  color: #555;
  margin-top: 20px;
  margin-bottom: 0px;
}

.add-to-cart {
  color: #d9534f;
  text-decoration: none;
  font-weight: bold;
  display: inline-block;
  margin-top: 10px;
}

.product-rating {
  font-size: 14px;
  color: #888;
  margin-top: 5px;
}
.sidebar {
  position: sticky; // Make sure this is set to sticky
  top: 0px; // Adjust if needed
}
.product-filters {
  padding: 20px;
  width: 250px;
  position: sticky;
  top: 0px;
}

.search-input {
  width: 100%;
  padding: 8px;
  margin-bottom: 15px;
  border: 1px solid #ccc;
  border-radius: 4px;
}

.filter-group {
  margin-bottom: 20px;
}

.filter-group label {
  font-weight: bold;
  margin-bottom: 5px;
  display: block;
}

.filter-group button {
  background: none;
  border: none;
  color: #d9534f;
  cursor: pointer;
  margin-right: 5px;
  padding: 5px;
}

.filter-group button.active {
  font-weight: bold;
  text-decoration: underline;
}

.range-inputs {
  display: flex;
  gap: 10px;
  margin-top: 8px;
}

.range-inputs input {
  width: 50%;
  padding: 5px;
  border: 1px solid #ccc;
  border-radius: 4px;
}

.clear-button {
  width: 100%;
  padding: 10px;
  border: 1px solid #d9534f;
  color: #d9534f;
  border-radius: 4px;
  background: none;
  cursor: pointer;
  font-weight: bold;
}
</style>
