<template>
	<div class="wide">
		<NavBar />
		<div class="product-detail-container">
			<div class="product-image">
				<img src="placeholder.jpg" alt="Product Image" />
			</div>
			<div class="product-info">
				<h2>Open Box - Apple MacBook Air (2022) 13.6” w/ Touch ID (2022) - Midnight</h2>
				<p class="price">$1,199.77</p>
				<p class="description">
					The new MacBook Air is powered by the Apple M2 chip, which provides enhanced performance, battery life, and more.
				</p>
				<button class="add-to-cart-btn">Add to Cart</button>
				<div class="rating">
					<p>4.2★ / 12 ratings</p>
					<div class="user-review">
						<strong>Tim Nguyen:</strong> Great product with good value for the price!
					</div>
					<button class="add-rating-btn">Add Your Rating</button>
				</div>
			</div>
		</div>
	</div>
</template>

<script>
import NavBar from '@/components/NavBar.vue';

export default {
	name: 'ProductDetailPage',
	components: {
		NavBar,
	},
	props: ['id'], 
    data() {
        return {
            productId: null, // Holds the id parameter
            productDetails: {}, // Holds the product details (fetched data)
        };
    },
    created() {
        // Get the route parameter
        this.productId = this.$route.params.id;

        // Example: Fetch product details based on the id
        this.fetchProductDetails(this.productId);
    },
    methods: {
        fetchProductDetails(id) {
            // Placeholder for an API call to fetch product details by id
            // Replace this with your actual API call logic
            console.log(`Fetching details for product ID: ${id}`);
            // Example: Simulate fetching data
            this.productDetails = {
                id: id,
                name: "Sample Product",
                description: "This is a sample product description.",
                // Add more fields as needed
            };
        }
    }
};
</script>

<style scoped lang="scss">
.product-detail-container {
	display: flex;
	padding: 2em;
	background-color: #f9f9f9;
	border-radius: 8px;
	gap: 2em;

	.product-image {
		flex: 1;

		img {
			width: 100%;
			border-radius: 8px;
		}
	}

	.product-info {
		flex: 1;
		display: flex;
		flex-direction: column;
		gap: 1em;

		h2 {
			font-size: 1.5em;
			margin: 0;
		}

		.price {
			font-size: 1.25em;
			color: green;
			font-weight: bold;
		}

		.description {
			color: #666;
			font-size: 1em;
		}

		.add-to-cart-btn {
			background-color: #ff0000;
			color: #fff;
			border: none;
			padding: 0.75em 1.5em;
			border-radius: 4px;
			cursor: pointer;
			font-size: 1em;
		}

		.rating {
			font-size: 0.9em;
			color: #333;

			.user-review {
				margin-top: 0.5em;
				color: #666;
			}

			.add-rating-btn {
				margin-top: 0.5em;
				background: none;
				color: #0073e6;
				border: none;
				cursor: pointer;
				font-size: 0.9em;
			}
		}
	}
}
</style>
