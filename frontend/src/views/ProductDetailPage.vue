<template>
	<div class="wide">
		<NavBar />
		<div class="card flex wrapper product-details">
			<div class="main flex feature-image-wrapper">
				<img :src="selectedFeatureImage" alt="Product Feature Image" class="feature-image" />

				<div class="flex thumbnail-wrapper">
					<div class="thumbnail" v-for="(image, index) in productDetails.imageSrcs" :key="index"
						:class="{ 'active-thumbnail': selectedFeatureImage === imageSrc(image) }">
						<img :src="imageSrc(image)" class="thumbnail" alt="Thumbnail"
							@click="selectImage(image.url || image)" />

						<button v-if="isEditorEnabled()" class="remove-thumbnail" @click="removeImage(index)">✕</button>
					</div>
					<div v-if="isEditorEnabled()" class="thumbnail add-thumbnail">
						<button @click="triggerFileInput">+</button>
						<input type="file" ref="fileInput" class="hidden-file-input" @change="addImage" />
					</div>
				</div>
			</div>
			<div class="sidebar flex product-info">
				<h2 class="name">
					<span v-if="!isEditorEnabled()">{{ productDetails.title }}</span>
					<input v-else v-model="productDetails.title" type="text" placeholder="Product Title" />
				</h2>
				<p class="price">
					<span v-if="!isEditorEnabled()">{{ formatCurrency(productDetails.price) }}</span>
					<span v-else class="price-input-wrapper">
						$ <input v-model="productDetails.price" type="number" min="0" placeholder="Price" />
					</span>
				</p>

				<p class="description">
					<span v-if="!isEditorEnabled()">{{ productDetails.description }}</span>
					<textarea v-else v-model="productDetails.description" placeholder="Product Description"
						rows="3"></textarea>
				</p>
				<p class="stock">
					<span v-if="!isEditorEnabled()">STOCK: {{ productDetails.stock }}</span>
					<span v-else class="stock-input-wrapper">
						Stock: <input v-model="productDetails.stock" type="number" min="0" placeholder="Stock" />
					</span>
				</p>

				<ul class="error-messages" v-if="formErrors.length">
					<li v-for="(error, index) in formErrors" :key="index" class="error-item">
						{{ error }}
					</li>
				</ul>

				<button v-if="productDetails.stock > 0 && (!isAdmin() || isEditorEnabled())"
					class="primary-btn add-to-cart" @click="addToCart">
					{{ addToCartButtonText }}
				</button>
				<button v-else disabled>Out of stock</button>

				<button v-if="isAdmin() && !isEditingProduct && !isNewProduct" class="second-btn" @click="editProduct">
					Edit Product
				</button>
				<button v-if="isAdmin() && isEditingProduct && !isNewProduct" class="second-btn" @click="deleteProduct">
					Delete Product
				</button>

				<!-- Ratings and Reviews Section -->
				<div v-if="!isEditorEnabled()" class="rating">
					<p class="rating-score">
						{{ productDetails.averageRating }}★ /
						<span class="rating-length">{{ productDetails.reviews?.length || 0 }} ratings</span>
					</p>
					<div class="reviews">
						<div v-for="(review, index) in productDetails.reviews" :key="index" class="user-review">
							<div class="review-header">
								<div class="review-rating">
									<span v-for="star in 5" :key="star"
										:class="{ 'filled': star <= review.ratingStars, 'unfilled': star > review.ratingStars }">★</span>
								</div>
								<strong class="review-user">{{ userName(review.user.email) }}</strong>
								<div v-if="isReviewOwner(review.user.email)" class="review-actions">
									<!-- <button @click="editReview">Edit</button> -->
									<button @click="deleteReview">Delete</button>
								</div>
							</div>
							<p class="review-comment">{{ review.ratingDescription }}</p>
						</div>
					</div>


					<!-- Add New Review Section -->
					<div v-if="canAddReview" class="add-review">
						<div class="review-header">
							<div class="star-rating">
								<span v-for="star in 5" :key="star" @click="newReview.rating = star"
									:class="{ 'selected': star <= newReview.rating }">★</span>
							</div>
							<strong class="review-user-name">{{ userName() }}</strong>
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
			isNewProduct: false,
			isEditingProduct: false,
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
			formErrors: [],
		};
	},
	async created() {
		const productId = this.$route.params.productId;
		const categoryId = this.$route.params.categoryId;
		if (productId === "new") {
			if (!categoryId) {
				alert("Missing category Id");
				this.$router.push(`/`);
				return;
			}
			this.isNewProduct = true;
			this.productDetails = {
				title: "",
				description: "",
				price: 0,
				imageSrcs: [],
				categoryId: categoryId,
				stock: 0,
			};
			this.selectedFeatureImage = require("@/assets/img/mock-feature-image.jpg");
			this.addToCartButtonText = "Create Product";
		} else {
			this.isNewProduct = false;
			this.productId = parseInt(productId, 10);
			await User.init();
			await this.fetchProductDetails(this.productId);
			this.selectedFeatureImage = this.productDetails.imageSrcs?.[0] ? this.imageSrc(this.productDetails.imageSrcs[0]) : require("@/assets/img/mock-feature-image.jpg");
			this.updateAddToCartText();
			console.log("Product Images:", this.productDetails.imageSrcs);
		}
	},
	methods: {
		isEditorEnabled() {
			return this.isAdmin() && (this.isEditingProduct || this.isNewProduct)
		},
		isAdmin() {
			return User.isLoggedInAdmin();
		},
		formatCurrency(value) {
			return Lib.formatCurrency(value);
		},
		editProduct() {
			this.isEditingProduct = true;
			this.addToCartButtonText = "Update Product";
		},
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
				this.selectedFeatureImage = this.imageSrc(this.productDetails.imageSrcs[0]); // Set initial image

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
				} else {
					this.canAddReview = false;

				}
			} catch (error) {
				console.error("Error fetching product details:", error);
			}
		},
		triggerFileInput() {
			this.$refs.fileInput.click(); // Trigger the hidden file input
		},
		imageSrc(image) {
			if (typeof image === "string") {
				if (image.startsWith("http")) {
					return image;
				}
				// Existing image from the server
				return Env.SERVER_URL + "/" + image;
			} else if (image.url) {
				// Newly added image with a temporary URL
				return image.url;
			}
			return ""; // Fallback if no image
		}
		,
		selectImage(image) {
			this.selectedFeatureImage = this.imageSrc(image); // Update the main image on thumbnail click
		},
		isReviewOwner(email) {
			return email == User.getUserEmailCode().email
		},
		userName(email) {
			if (email == null) {
				email = User.getUserEmailCode().email;
			}
			return (email.split('@')[0]).split('').map((c, i) => i == 0 ? c.toUpperCase() : c).join('');
		},
		
		async submitReview() {
			if (this.newReview.rating > 0 && this.newReview.comment.trim()) {
				try {
					await axios.put(`${Env.API_BASE_URL}/ratings/${this.productId}`, {
						ratingStars: this.newReview.rating,
						ratingDescription: this.newReview.comment,
						...User.getUserEmailCode()
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
		async editReview() {
			if (this.newReview.comment.trim() && this.newReview.rating > 0) {
				try {
					await axios.put(`${Env.API_BASE_URL}/ratings/${this.productId}`, {
						ratingStars: this.newReview.rating,
						ratingDescription: this.newReview.comment,
						...User.getUserEmailCode(),
					});
					alert("Your review has been updated.");
					await this.fetchProductDetails(this.productId); // Refresh product details
				} catch (error) {
					console.error("Error updating review:", error);
				}
			} else {
				alert("Please provide a valid rating and comment.");
			}
		},
		async deleteReview() {
			try {
				await axios.delete(`${Env.API_BASE_URL}/ratings/${this.productId}`, {
					data: User.getUserEmailCode(),
				});
				alert("Your review has been deleted.");
				await this.fetchProductDetails(this.productId); // Refresh product details
			} catch (error) {
				console.error("Error deleting review:", error);
			}
		},

		validateForm() {
			this.formErrors = []; // Reset errors

			if (!this.productDetails.title || this.productDetails.title.trim() === "") {
				this.formErrors.push("Product title cannot be empty.");
			}
			if (!this.productDetails.price || isNaN(this.productDetails.price)) {
				this.formErrors.push("Product price cannot be empty and must be a valid number.");
			}
			if (!this.productDetails.description || this.productDetails.description.trim() === "") {
				this.formErrors.push("Product description cannot be empty.");
			}

			// if (this.productDetails.stock == "" || isNaN(this.productDetails.stock)) {
			// 	this.formErrors.push("Product ");
			// }
			if (!this.productDetails.imageSrcs || this.productDetails.imageSrcs.length === 0) {
				this.formErrors.push("Please upload at least one feature image.");
			}

			return this.formErrors.length === 0; // Return true if no errors
		},
		async submitProduct() {
			if (!this.validateForm()) {
				return;
			}

			const formData = new FormData();
			formData.append("title", this.productDetails.title);
			formData.append("description", this.productDetails.description);
			formData.append("price", this.productDetails.price);
			formData.append("categoryId", this.productDetails.category?.id || this.$route.params.categoryId);
			formData.append("stock", this.productDetails.stock || 0);

			// Append selected images
			if (this.productDetails.imageSrcs && this.productDetails.imageSrcs.length > 0) {
				this.productDetails.imageSrcs.forEach((image) => {
					// Handle files only, skip URLs
					if (image.file) {
						formData.append("images", image.file); // Use `image.file` if it's wrapped						
						console.log("Appending image:", image.file.name);
					}
					else if (image instanceof File) {
						formData.append(`images`, image);
						console.log("Appending image:", image.name);
					}

				});
			}
			// Append removed images
			if (this.productDetails.removedImages && this.productDetails.removedImages.length > 0) {
				this.productDetails.removedImages.forEach((image) => {
					formData.append("removedImages", image);
				})
			}

			try {
				const { email, code } = User.getUserEmailCode();
				const headers = {
					"Content-Type": "multipart/form-data",
					'X-User-Email': email,
					'X-User-Code': code
				}
				if (this.isNewProduct) {
					const response = await axios.post(`${Env.API_BASE_URL}/products`, formData, { headers });
					alert("Product created successfully.");
					// console.log(response.data);
					this.isNewProduct = false;
					this.$router.push(`/category/${this.$route.params.categoryId}/product/${response.data.productId}`);
				} else {
					// Update existing product
					this.isEditingProduct = false;
					await axios.put(`${Env.API_BASE_URL}/products/${this.productId}`, formData, { headers });
					alert("Product updated successfully.");
				}
			} catch (error) {
				console.error("Error saving product:", error);
				alert("Failed to save product.");

			}
		},
		async deleteProduct() {
			const confirmation = prompt(`Type "DELETE" to delete this product":`);
			if (confirmation !== 'DELETE') {
				return;
			}
			const { email, code } = User.getUserEmailCode();
			const headers = {
				'X-User-Email': email,
				'X-User-Code': code
			}
			try {
				await axios.delete(`${Env.API_BASE_URL}/products/${this.productId}`, { headers });
				alert("Product deleted successfully.");
				this.$router.push("/");
			} catch (error) {
				console.error("Error deleting product:", error);
			}
		},
		addImage(event) {
			const file = event.target.files[0];
			if (file) {
				console.log("Selected file:", file);

				const validTypes = ["image/jpeg", "image/png", "image/gif"];
				if (!validTypes.includes(file.type)) {
					alert("Invalid file type. Only JPG, PNG, and GIF are allowed.");
					return;
				}
				if (file.size > 5 * 1024 * 1024) { // 5 MB size limit
					alert("File size exceeds the 5MB limit.");
					return;
				}

				// Generate a URL for the file to display in the UI
				const fileUrl = URL.createObjectURL(file);

				// Push both the file and the URL to manage preview and uploading
				this.productDetails.imageSrcs.push({ file, url: fileUrl });

				// Update the selected feature image to the newly added image
				this.selectedFeatureImage = fileUrl;

				// Clear the file input to allow re-adding the same file if needed
				this.$refs.fileInput.value = "";
			}
		},

		removeImage(index) {
			const removedImage = this.productDetails.imageSrcs.splice(index, 1)[0];
			if (typeof removedImage === "string") {
				// Keep track of images that need to be removed on the backend
				this.productDetails.removedImages = this.productDetails.removedImages || [];
				this.productDetails.removedImages.push(removedImage);
			}

			// If the current selected image is removed, reset it
			if (this.selectedFeatureImage === removedImage) {
				this.selectedFeatureImage = this.productDetails.imageSrcs[0] ? this.imageSrc(this.productDetails.imageSrcs[0]) : require("@/assets/img/mock-feature-image.jpg");
			}
		},

		async addToCart() {
			if (this.isEditorEnabled()) {
				this.submitProduct();
				return;
			}
			if (this.isAdmin()) {
				return;
			}
			try {
				await User.addToCart(this.productId, this.$router); // Add product to the cart
			} catch (error) {
				console.error("Error adding product to cart:", error);
			}
			this.updateAddToCartText(); // Update button text after adding to cart
		},
		updateAddToCartText() {
			if (this.isNewProduct) {
				return 'Create New Product';
			}
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
				// padding: 0.3em;
				border: 2px solid var(--border-color);
				border-radius: 6px;
				position: relative;
				cursor: pointer;
				padding: 1px;

				&:hover {
					border-color: var(--light-text-color);
				}

				&.active-thumbnail {
					border-color: var(--primary-color);

					&:hover {
						border-color: var(--primary-color);
					}
				}

				img,
				button {
					background: none;
					font-size: 2em;
					font-weight: 100;
					padding: 0;
					display: block;
					border-radius: 4px;
					width: 50px;
					height: 50px;
					object-fit: cover;
					border: none;
				}

				input {
					display: none;
				}

				button.remove-thumbnail {
					position: absolute;
					top: -1em;
					right: -1em;
					background-color: var(--white-bg-color);
					color: var(--primary-color);
					border: 1px solid var(--primary-color);
					font-size: 0.5em;
					height: 1em;
					width: 1em;
					padding: 0.5em;
					box-sizing: content-box;
					border-radius: 100%;

					&:hover {
						background-color: var(--primary-color);
						color: var(--white-bg-color);
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
			// color: var(--light-text-color);
			font-size: 1em;
			margin: 0;
			line-height: 1.6em;
		}

		.stock {
			color: var(--light-text-color);
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
			gap: 0.5em;
			align-items: center;
			margin-bottom: var(--spacing-element-small);
		}

		.review-rating {
			display: flex;
			// margin-right: 8px;
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

		.review-actions {
			display: inherit;
			gap: inherit;

			button {
				padding: 0;
				border: none;
				background: none;
			}
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
					// margin-left: 8px;
					/* Space between stars and username */
					color: var(--text-color);
				}
			}
		}
	}

	/// editor	
	input,
	textarea {
		border: none;
		font-weight: inherit;
		font-size: inherit;
		color: inherit;
		border-bottom: 1px solid var(--light-text-color);
		border-radius: 0;
		padding: 0;
		width: 100%;
	}

	.price-input-wrapper,
	.stock-input-wrapper {
		font-weight: inherit;
		font-size: inherit;
		color: inherit;
		width: 100%;
		display: flex;
		gap: 0.2em;
		border-bottom: 1px solid var(--light-text-color);

		input {
			border: none;
		}
	}

	.error-messages {
		padding: 0;
		color: var(--primary-color);

		li {
			display: block;
			margin-bottom: 1em;
		}
	}

}
</style>
