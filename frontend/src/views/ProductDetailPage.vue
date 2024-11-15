<template>
	<div class="wide">
		<NavBar />
		<div class="card flex wrapper product-details">
			<div class="main flex feature-image-wrapper">
				<img :src="selectedFeatureImage" alt="Product Feature Image" class="feature-image" />
				<div class="flex thumbnail-wrapper">
					<img v-for="(image, index) in productDetails.imageSrcs" :key="index" :src="image"
						:class="{ 'active-thumbnail': selectedFeatureImage === image }" @click="selectImage(image)"
						class="thumbnail" alt="Thumbnail" />
				</div>
			</div>
			<div class="sidebar flex product-info">
				<h2 class="name">{{ productDetails.title }}</h2>
				<p class="price">${{ productDetails.price }}</p>
				<p class="description">{{ productDetails.description }}</p>
				<button class="primary-btn add-to-cart">Add to Cart</button>

				<!-- Ratings and Reviews Section -->
				<div class="rating">
					<p class="rating-score">
						{{ productDetails.averageRating }}★ / <span class="rating-length">{{
							productDetails.reviews?.length || 0 }} ratings</span>
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
import axios from 'axios';
import NavBar from '@/components/NavBar.vue';
import Env from '@/utils/Env';

export default {
	name: 'ProductDetailPage',
	components: {
		NavBar,
	},
	data() {
		return {
			productId: null,
			productDetails: {},
			selectedFeatureImage: '',
			newReview: {
				comment: '',
				rating: 0,
				user: 'Tim Nguyen'
			}
		};
	},
	created() {
		this.productId = this.$route.params.id; // Get the product ID from the route parameter
		this.fetchProductDetails(this.productId); // Fetch product details
	},
	methods: {
		async fetchProductDetails(id) {
			try {
				const response = await axios.get(`${Env.API_BASE_URL}/products/${id}`);
				this.productDetails = response.data;
				this.selectedFeatureImage = this.productDetails.imageSrcs[0]; // Set initial image
			} catch (error) {
				console.error("Error fetching product details:", error);
			}
		},
		selectImage(image) {
			this.selectedFeatureImage = image; // Update the main image on thumbnail click
		},
		submitReview() {
			if (this.newReview.comment.trim() && this.newReview.rating > 0) {
				this.productDetails.reviews.push({
					name: this.newReview.user,
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
.product-details {

	.feature-image-wrapper {
		flex: 2;
		padding: 3em;
		flex-direction: column;
		gap: 1em;

		img {
			width: 100%;
		}

		.thumbnail-wrapper {
			gap: 1em;

			.thumbnail {
				width: 50px;
				height: 50px;
				// padding: 0.3em;
				border: 2px solid var(--border-color);
				border-radius: 4px;
				cursor: pointer;

				&:hover {
					border-color: var(--light-text-color);
				}

				&.active-thumbnail {
					border-color: var(--primary-color);

					&:hover {
						border-color: var(--primary-color);
					}
				}
			}

		}
	}

	.product-info {
		padding: 3em;
		flex: 1;
		flex-direction: column;
		gap: 1em;
		align-items: start;
		justify-content: start;

		.name {
			margin: 0;
		}

		.price {
			font-size: 2.5em;
			color: var(--success-color);
			font-weight: 300;
			margin: 0.5em 0;
		}

		.description {
			color: #666;
			font-size: 1em;
			margin: 0;
		}

		.add-to-cart {
			padding: 20px;
			font-size: 16px;
			margin: 0.5em 0 0 0;
			width: 100%;
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
