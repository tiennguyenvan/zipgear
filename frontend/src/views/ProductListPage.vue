<template>
	<div class="wide">
		<NavBar />

		<div class="card flex wrapper">
			<!-- Product List -->
			<div class="product-list main">
				<div v-for="product in products" :key="product.productId" class="product-item">
					<img :src="product.imageSrcs[0]" :alt="product.title" class="image" />
					<p class="price ">${{ product.price }}</p>
					<h3 class="name">{{ product.title }}</h3>
					<a href="#" class="action add-to-cart">Add to Cart</a>
					<p class="rating">{{ product.averageRating }}★</p>
				</div>
			</div>

			<!-- Filter Sidebar -->
			<div class="sidebar">
				<div class="product-filters">
					<!-- Search Keywords -->
					<input type="text" v-model="filters.keyword" placeholder="Search Keywords" class="search-input"
						@input="onFilterChange" />

					<!-- Price Filter -->
					<div class="filter-group">
						<div class="flex action-links">
							<label>Price:</label>
							<button @click="sortPrice('all')"
								:class="{ active: filters.priceSort === 'all' }">All</button>
							<button @click="sortPrice('high')" :class="{ active: filters.priceSort === 'high' }">High
								First</button>
							<button @click="sortPrice('low')" :class="{ active: filters.priceSort === 'low' }">Low
								First</button>
						</div>
						<div class="range-inputs price-filters">
							<input type="number" v-model="filters.minPrice" placeholder="Min" @input="onFilterChange" min="1"/>
							<input type="number" v-model="filters.maxPrice" placeholder="Max" @input="onFilterChange" min="1"/>
						</div>
					</div>


					<!-- Rating Filter -->
					<div class="filter-group">
						<div class="flex action-links">
							<label>Rating:</label>
							<button @click="sortRating('all')"
								:class="{ active: filters.ratingSort === 'all' }">All</button>
							<button @click="sortRating('high')" :class="{ active: filters.ratingSort === 'high' }">High
								First</button>
							<button @click="sortRating('low')" :class="{ active: filters.ratingSort === 'low' }">Low
								First</button>
						</div>
						<div class="range-inputs rating-filters">
							<input type="number" v-model="filters.minRating" placeholder="Min"
								@input="onFilterChange" min="1" />
							<input type="number" v-model="filters.maxRating" placeholder="Max"
								@input="onFilterChange" min="1"/>
						</div>
					</div>

					<!-- Stock Filter -->
					<div class="filter-group">
						<div class="flex action-links">
							<label>Stock:</label>
							<button @click="setStock('all')" :class="{ active: filters.stock === 'all' }">All</button>
							<button @click="setStock('in')"
								:class="{ active: filters.stock === 'in' }">In-Stock</button>
							<button @click="setStock('out')"
								:class="{ active: filters.stock === 'out' }">Out-Stock</button>
						</div>
					</div>

					<!-- Clear Filters Button -->
					<button @click="clearFilters" class="clear-filter-button">Clear All Filters</button>
				</div>
			</div>
		</div>
	</div>
</template>

<script>
import axios from "axios";
import NavBar from "@/components/NavBar.vue";
import Env from "@/utils/Env";
import debounce from "lodash/debounce";

export default {
	name: "ProductListPage",
	components: { NavBar },
	data() {
		return {
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
			debounceFilterChange: null,
		};
	},
	watch: {
		"$route.params.id": "fetchProducts" // Watch for category changes
	},
	created() {
		this.debounceFilterChange = debounce(this.fetchProducts, 500); // 500ms delay
		this.fetchProducts();
	},
	methods: {
		async fetchProducts() {
			try {
				// Set up request parameters
				const params = {
					keyword: this.filters.keyword || null,
					priceOrder: this.filters.priceSort === "high" ? "desc" : this.filters.priceSort === "low" ? "asc" : null,
					ratingOrder: this.filters.ratingSort === "high" ? "desc" : this.filters.ratingSort === "low" ? "asc" : null,
					minPrice: this.filters.minPrice || null,
					maxPrice: this.filters.maxPrice || null,
					minRating: this.filters.minRating || null,
					maxRating: this.filters.maxRating || null,
					inStock: this.filters.stock === "all" ? null : this.filters.stock === "in",
					categoryId: this.$route.params.id || null, // Category filter
				};

				// Fetch data from the backend
				const response = await axios.get(`${Env.API_BASE_URL}/products`, { params });
				this.products = response.data;
			} catch (error) {
				console.error("Failed to fetch products:", error);
			}
		},
		onFilterChange() {
			this.debounceFilterChange();
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
	},
};
</script>


<style scoped lang="scss">
.product-list {
	display: flex;
	flex-wrap: wrap;
	/* Allows items to wrap to the next line */
	justify-content: center;
	/* Centers items horizontally */
	gap: 20px;
	/* Space between cards */

	.product-item {
		text-align: center;
		width: 200px;
		margin: 10px;

		/* Optional: Space around each card */
		&:hover {
			transform: scale(1.05);
		}

		.image {
			width: 100%;
			height: auto;
			border-radius: 8px;
		}

		.price {
			font-size: 14px;
			font-weight: bold;
			color: var(--light-text-color);
			margin-top: 20px;
			margin-bottom: 0px;
		}

		.name {
			font-size: 16px;
			font-weight: bold;
			margin-top: 5px;
			margin-bottom: 10px;
		}

		.rating {
			font-size: 14px;
			color: var(--light-text-color);
			margin-top: 5px;
		}

	}
}



.sidebar {
	position: sticky; // Make sure this is set to sticky
	top: 0px; // Adjust if needed

	.product-filters {
		padding: 20px;
		width: 250px;
		position: sticky;
		top: 0px;
		gap: 30px;
		display: flex;
		flex-direction: column;


		.filter-group {
			.action-links {
				gap: 1em;
				justify-content: start;
				font-weight: bold;
				font-size: 13px;

				label {
					font-weight: 600;
				}

				button {
					font-weight: inherit;
					font-size: inherit;
					background: none;
					border: none;
					padding: 0px;
				}
			}


			.range-inputs {
				display: flex;				
				margin-top: 15px;

				input:first-child {
					border-right: 0;
					border-top-right-radius: 0;
					border-bottom-right-radius: 0;
				}
				input:last-child {
					border-top-left-radius: 0;
					border-bottom-left-radius: 0;
				}
			}



		}

	}


	.clear-filter-button {
		width: 100%;
	}
}
</style>