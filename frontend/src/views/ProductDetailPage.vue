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
				<p class="price">{{ formatCurrency(productDetails.price) }}</p>
				<p class="description">{{ productDetails.description }}</p>

				<!-- Add to Cart Button -->
				<button class="primary-btn add-to-cart" @click="addToCart">
					{{ addToCartButtonText }}
				</button>

				<!-- Ratings and Reviews Section -->
				<div class="rating">
					<p class="rating-score">
						{{ productDetails.averageRating }}★ /
						<span class="rating-length">{{ productDetails.reviews?.length || 0 }} ratings</span>
					</p>
					<!-- <div class="reviews">
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
					</div> -->
					<div class="reviews">
						<div v-for="(review, index) in productDetails.reviews" :key="index" class="user-review">
							<div class="review-header">
								<div class="review-rating">
									<span v-for="star in 5" :key="star"
										:class="{ 'filled': star <= review.rating, 'unfilled': star > review.rating }">★</span>
								</div>
								<strong class="review-user">{{ review.user.email }}</strong>
							</div>
							<p class="review-comment">{{ review.ratingDescription }}</p>
						</div>
					</div>


					<!-- Add New Review Section -->
					<div v-if="canAddReview" class="add-review">
						<div class="star-rating">
							<span v-for="star in 5" :key="star" @click="newReview.rating = star"
								:class="{ 'selected': star <= newReview.rating }">★</span>
							<h4 class="review-user-name">Tim Nguyen</h4>
						</div>
						<textarea class="review-content-input" v-model="newReview.comment"
							placeholder="Your review here..." required></textarea>
						<button class="add-rating-btn" @click="submitReview">Add Your Rating</button>
					</div>
				</div>
			</div>
		</div>
	</div>
</template>

<script>
import axios from "axios";
import NavBar from "@/components/NavBar.vue";
import Env from "@/utils/Env";
import User from "@/utils/User";
import Lib from "@/utils/Lib";

export default {
	name: "ProductDetailPage",
	components: {
		NavBar,
	},
	data() {
		return {
			productId: null,
			productDetails: {},
			selectedFeatureImage: "",
			addToCartButtonText: "Add to Cart",
			newReview: {
				comment: "",
				rating: 0,
				user: "Tim Nguyen",
			},
			canAddReview: false,
		};
	},
	async created() {
		this.productId = parseInt(this.$route.params.id, 10); // Ensure the productId is a number
		await User.init();
		await this.fetchProductDetails(this.productId); // Fetch product details
		this.updateAddToCartText();
	},
	methods: {
		formatCurrency(value) {
			return Lib.formatCurrency(value);
		},
		// async fetchProductDetails(id) {
		//     try {
		//         const response = await axios.get(`${Env.API_BASE_URL}/products/${id}`);
		//         this.productDetails = response.data;
		//         this.selectedFeatureImage = this.productDetails.imageSrcs[0]; // Set initial image
		//     } catch (error) {
		//         console.error("Error fetching product details:", error);
		//     }
		// },
		async fetchProductDetails(id) {
			try {
				const response = await axios.get(`${Env.API_BASE_URL}/products/${id}`);
				const ratingsResponse = await axios.get(`${Env.API_BASE_URL}/ratings/${id}`);

				// Filter out ratings with `ratingStars === 0` for display
				const filteredReviews = ratingsResponse.data.filter((rating) => rating.ratingStars > 0);

				this.productDetails = {
					...response.data,
					reviews: filteredReviews, // Display only non-placeholder reviews
				};
				this.selectedFeatureImage = this.productDetails.imageSrcs[0]; // Set initial image

				// Check if the current user has a placeholder review (0 stars)
				const currentUserReview = ratingsResponse.data.find(
					(rating) => rating.ratingStars === 0 && rating.user.email === User.getUserEmailCode().email
				);				

				if (currentUserReview) {
					this.newReview = {
						comment: currentUserReview.ratingDescription || "", // Placeholder description
						rating: 0, // Allow user to update
						user: User.email,
					};
					this.canAddReview = true; // Allow user to add/update review
					console.log("Updated Review Permission")					
				}
			} catch (error) {
				console.error("Error fetching product details:", error);
			}
		}
		,

		selectImage(image) {
			this.selectedFeatureImage = image; // Update the main image on thumbnail click
		},
		// submitReview() {
		// 	if (this.newReview.comment.trim() && this.newReview.rating > 0) {
		// 		this.productDetails.reviews.push({
		// 			name: this.newReview.user,
		// 			rating: this.newReview.rating,
		// 			comment: this.newReview.comment.trim(),
		// 		});
		// 		this.newReview.comment = "";
		// 		this.newReview.rating = 0;
		// 	}
		// },

		async submitReview() {
			if (this.newReview.rating > 0 && this.newReview.comment.trim()) {
				try {
					await axios.put(`${Env.API_BASE_URL}/ratings/${this.productId}`, {
						ratingStars: this.newReview.rating,
						ratingDescription: this.newReview.comment,
						... User.getUserEmailCode()
					});
					// alert("Your review has been submitted!");
					await this.fetchProductDetails(this.productId); // Refresh product details
					this.canAddReview = false
				} catch (error) {
					console.error("Error submitting review:", error);
				}
			} else {
				alert("Please provide a rating and a comment.");
			}
		},
		async addToCart() {
			try {
				await User.addToCart(this.productId, this.$router); // Add product to the cart
			} catch (error) {
				console.error("Error adding product to cart:", error);
			}
			this.updateAddToCartText(); // Update button text after adding to cart
		},
		updateAddToCartText() {
			// Dynamically update the button text based on cart state
			this.addToCartButtonText = User.getAddToCartText(this.productId);
		},
	},
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
		align-items: stretch;
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
			color: var(--primary-color);
			font-weight: var(--font-weight-button);

			.star {
				font-size: var(--font-size-content);
				margin-right: 2px;
			}

			.filled {
				color: var(--primary-color);

			}

			.unfilled {
				color: var(--light-text-color);

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
			// padding: 0;
			// background: none;			
			// border: none;	
			width: 100%;
		}

		textarea.review-content-input {
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
			.star-rating {
				display: flex;
				align-items: center;
				/* Aligns items vertically centered */
				cursor: pointer;
				color: var(--light-text-color);

				.star {
					color: var(--primary-color);
					/* Primary color for stars */
					font-size: var(--font-size-content);
					margin-right: 4px;
				}

				.selected {
					color: var(--primary-color);
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
