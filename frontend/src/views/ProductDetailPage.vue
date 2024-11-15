<template>
	<div class="wide">
		<NavBar />
		<div class="product-detail-container">
			<div class="product-image">
				<img :src="selectedImage" alt="Product Image" class="main-image" />
				<div class="thumbnail-container">
					<img v-for="(image, index) in productDetails.images" :key="index" :src="image"
						:class="{ 'active-thumbnail': selectedImage === image }" @click="selectImage(image)"
						class="thumbnail" alt="Thumbnail" />
				</div>
			</div>
			<div class="product-info">
				<!-- <h2>Open Box - Apple MacBook Air (2022) 13.6” w/ Touch ID (2022) - Midnight</h2>
				<p class="price">$1,199.77</p>
				<p class="description">
					The new MacBook Air is powered by the Apple M2 chip, which provides enhanced performance, battery life, and more.
				</p> -->
				<h2 class="product-name">{{ productDetails.name }}</h2>
				<p class="product-price">{{ productDetails.price }}</p>
				<p class="description">{{ productDetails.description }}</p>
				<button class="add-to-cart-btn">Add to Cart</button>

				<!-- Ratings and Reviews Section -->
				<div class="rating">
					<p class="rating-score">{{ productDetails.rating }}★ / <span class="rating-length">{{
						productDetails.reviews.length }} ratings</span>
					</p>
					<div class="reviews">
						<div v-for="(review, index) in productDetails.reviews" :key="index" class="user-review">
							<div class="review-header">
								<div class="review-rating">
									<span v-for="star in 5" :key="star"
										:class="{ 'filled': star <= review.rating, 'unfilled': star > review.rating }">★</span>
								</div>
								<strong class="review-user">{{ review.name }}</strong>
							</div>
							<p class="review-comment">{{ review.comment }}</p>
						</div>
					</div>

					<!-- Add New Review Section -->
					<div class="add-review">
						<button class="add-rating-btn" @click="submitReview">Add Your Rating</button>
						<div class="star-rating">
							<span v-for="star in 5" :key="star" @click="newReview.rating = star"
								:class="{ 'selected': star <= newReview.rating }">★</span>
							<h4 class="review-user-name">Tim Nguyen</h4>
						</div>
						<textarea v-model="newReview.comment" placeholder="Your review here..." required></textarea>
					</div>
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
			selectedImage: '', // Holds the selected image for the product
			newReview: {
				comment: '',
				rating: 0,
				user: 'Tim Nguyen'
			}
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
			// // Placeholder for an API call to fetch product details by id
			// // Replace this with your actual API call logic
			// console.log(`Fetching details for product ID: ${id}`);
			// // Example: Simulate fetching data
			// this.productDetails = {
			//     id: id,
			//     name: "Sample Product",
			//     description: "This is a sample product description.",
			//     // Add more fields as needed
			// };
			const products = [
				{
					id: 1,
					name: "zPhone 10",
					price: "$1,300",
					description: "High-performance smartphone with advanced features.",
					images: [
						require('@/assets/img/image.png'),
						require('@/assets/img/image2.jpeg'),
						require('@/assets/img/image3.jpeg')
					],
					rating: 4.2,
					reviews: [
						{ name: "Tim Nguyen", rating: 5, comment: "Excellent product with great value!" },
						{ name: "Jane Doe", rating: 4, comment: "Good quality, but a bit pricey." }
					]
				},
				{
					id: 2,
					name: "zNote W8",
					price: "$2,700",
					description: "High-performance tablet with sleek design.",
					images: [
						require('@/assets/img/image.png'),
						require('@/assets/img/image2.jpeg'),
						require('@/assets/img/image3.jpeg')
					],
					rating: 4.5,
					reviews: [
						{ name: "Alice", rating: 4, comment: "Solid performance and design." },
						{ name: "Bob", rating: 5, comment: "Best tablet I’ve owned." }
					]
				},
				{
					id: 3,
					name: "zBook M5",
					price: "$4,700",
					images: [
						require('@/assets/img/image.png'),
						require('@/assets/img/image2.jpeg'),
						require('@/assets/img/image3.jpeg')
					],
					rating: 4.5,
					reviews: [
						{ name: "Alice", rating: 4, comment: "Solid performance and design." },
						{ name: "Bob", rating: 5, comment: "Best tablet I’ve owned." }
					]
				},
				{
					id: 4,
					name: "zBook M5",
					price: "$4,700",
					images: [
						require('@/assets/img/image.png'),
						require('@/assets/img/image2.jpeg'),
						require('@/assets/img/image3.jpeg')
					],
					rating: 4.5,
					reviews: [
						{ name: "Alice", rating: 4, comment: "Solid performance and design." },
						{ name: "Bob", rating: 5, comment: "Best tablet I’ve owned." }
					]
				},
				{
					id: 5,
					name: "zHeadphone OV9",
					price: "$1,300",
					images: [
						require('@/assets/img/image.png'),
						require('@/assets/img/image2.jpeg'),
						require('@/assets/img/image3.jpeg')
					],
					rating: 4.5,
					reviews: [
						{ name: "Alice", rating: 4, comment: "Solid performance and design." },
						{ name: "Bob", rating: 5, comment: "Best tablet I’ve owned." }
					]
				},
				{
					id: 6,
					name: "zNote W8",
					price: "$2,700",
					images: [
						require('@/assets/img/image.png'),
						require('@/assets/img/image2.jpeg'),
						require('@/assets/img/image3.jpeg')
					],
					rating: 4.5,
					reviews: [
						{ name: "Alice", rating: 4, comment: "Solid performance and design." },
						{ name: "Bob", rating: 5, comment: "Best tablet I’ve owned." }
					]
				}
			];

			// Find the product by ID and set `productDetails`
			const product = products.find(p => p.id === parseInt(id));
			if (product) {
				this.productDetails = product;
				this.selectedImage = product.images[0]; // Set initial main image to the first one
				console.log("Product Details:", this.productDetails);
				console.log("Images:", this.productDetails.images); // Check if images array is correctly set
			}
		},
		selectImage(image) {
			this.selectedImage = image; // Update main image when a thumbnail is clicked
		},
		submitReview() {
			if (this.newReview.comment.trim() && this.newReview.rating > 0) {
				this.productDetails.reviews.push({
					rating: this.newReview.rating,
					comment: this.newReview.comment.trim()
				});
				this.newReview.comment = '';
				this.newReview.rating = 0;
			}
		}

	}

};
</script>

<style scoped lang="scss">
.product-detail-container {
	display: flex;
	background-color: var(--white-bg-color);
	border-radius: 8px;
	// padding: 2em;
	gap: 2em;
	align-items: stretch; /* Make both sections stretch to the same height */

	.product-image {
		flex: 2;
		padding: 2em;
		display: flex;
		flex-direction: column;
		align-items: center;
		background-color: #f9f9f9; /* Light gray background */
		margin-right: var(--spacing-element);
		justify-content: center; /* Center content vertically */

		img {
			gap: 2em;
			width: 100%;

		}

		.thumbnail-container {
			display: flex;
			gap: 8px;
			justify-content: center;
			/* Centers the thumbnails horizontally */
			margin-top: 10px;

			.thumbnail {
				width: 50px;
				height: 50px;
				border: 1px solid var(--border-color);
				border-radius: 4px;
				cursor: pointer;

				&:hover {
					border-color: var(--primary-color);
				}
			}

			.active-thumbnail {
				border-color: var(--primary-color);
			}
		}
	}

	.product-info {
		padding: 2em;
		flex: 1;
		display: flex;
		flex-direction: column;
		gap: var(--spacing-element-small);
		margin-right: var(--spacing-element);
		background-color: var(--white-bg-color); /* Ensure it stays white */
		-name {
			gap: 2em;
			font-size: var(--font-size-title);
			font-weight: var(--font-weight-title);
			margin-bottom: var(--spacing-element-small);
		}

		.product-price {
			font-size: 1.5em;
			color: var(--success-color);
			font-weight: var(--font-weight-button);
		}

		.description {
			color: #666;
			font-size: 1em;
		}

		.add-to-cart-btn {
			background-color: var(--primary-color);
			color: var(--white-bg-color);
			padding: var(--padding-button);
			border: none;
			border-radius: var(--border-radius);
			cursor: pointer;
			text-align: center;
			font-size: var(--font-size-button);
			font-weight: var(--font-weight-button);
			margin-top: var(--spacing-element);
			width: 100%;
			
		}

		.add-to-cart-btn:hover {
			background-color: var(--secondary-color);
		}

		/* Rating Section */
		.rating-length {
			color: var(--light-text-color);
		}

		.review-header {
			display: flex;
			align-items: center;
			margin-bottom: var(--spacing-element-small);
		}

		.review-rating {
			display: flex;
			margin-right: 8px;
			/* Space between stars and username */
			margin-bottom: var(--spacing-element-small);
			color: var(--primary-color);
			font-weight: var(--font-weight-button);

			.star {
				font-size: var(--font-size-content);
				margin-right: 2px;
			}

			.filled {
				color: #C21715
					/* Gold color for filled stars */
			}

			.unfilled {
				color: #d3d3d3;
				/* Grey color for unfilled stars */
			}
		}

		.rating {
			font-size: var(--font-size-content);
			color: var(--text-color);
			margin-top: var(--spacing-element);
		}

		.rating-score {
			font-weight: var(--font-weight-button);
			color: var(--primary-color);
			margin-bottom: var(--spacing-element-small);
		}

		.user-review {
			margin-top: var(--spacing-element-small);
			font-size: var(--font-size-card);
		}

		.review-stars .star {
			/* Gold color for stars */
			font-size: var(--font-size-content);
			margin-right: 2px;
		}

		/* Add Your Rating Button */
		.add-rating-btn {
			background: none;
			color: var(--primary-color);
			border: none;
			cursor: pointer;
			font-size: var(--font-size-content);
			font-weight: var(--font-weight-button);
			margin-top: var(--spacing-element-small);
		}

		.add-rating-btn:hover {
			text-decoration: underline;
		}

		textarea {
			width: 100%;
			padding: var(--spacing-element-small);
			font-size: var(--font-size-input);
			border: 1px solid var(--border-color);
			border-radius: var(--border-radius);
			resize: vertical;
			min-height: 60px;
			box-sizing: border-box;
			margin-bottom: var(--spacing-element-small);
		}

		.add-review {
			margin-top: var(--spacing-element);
			padding-top: var(--spacing-element-small);

			.star-rating {
				display: flex;
				align-items: center;
				/* Aligns items vertically centered */
				margin-bottom: var(--spacing-element-small);
				cursor: pointer;

				.star {
					color: #C21715;
					/* Primary color for stars */
					font-size: var(--font-size-content);
					margin-right: 4px;
				}

				.selected {
					color: #C21715;
					/* Color for selected stars */
				}

				.review-user-name {
					font-size: var(--font-size-card);
					font-weight: bold;
					margin-left: 8px;
					/* Space between stars and username */
					color: var(--text-color);
				}
			}
		}
	}
}
</style>
