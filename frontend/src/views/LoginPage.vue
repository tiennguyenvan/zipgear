<template>
	<div class="login-container">
		<SiteLogo />

		<!-- <button @click="loginWithGoogle">Login with Google</button> -->

		<form @submit.prevent="handleSubmit" class="card">
			<div v-if="!showCodeInput">
				<h3>Login with Email</h3>
				<input type="email" placeholder="Email Address" v-model="email" :disabled="isLoading" required />
				<button type="submit" :disabled="isLoading">
					<span v-if="isLoading">Loading...</span>
					<span v-else>Get Validation Code</span>
				</button>
				<p v-if="errorMessage" class="error">{{ errorMessage }}</p>
			</div>

			<div v-else>
				<h3>Validation Code</h3>
				<input type="text" placeholder="Validation Code" v-model="validationCode" required />
				<button type="submit">Login</button>

				<p class="resend">
					Didn’t receive a code?
					<a href="#" @click.prevent="resendCode">Resend Code</a>
				</p>
				<p v-if="errorMessage" class="error">{{ errorMessage }}</p>
			</div>
		</form>
	</div>
</template>

<script>
import SiteLogo from '@/components/SiteLogo.vue';
import Env from '@/utils/Env';
import User from '@/utils/User';



export default {
	data() {
		return {
			email: '',
			validationCode: '',
			showCodeInput: false,
			isLoading: false,
			errorMessage: '',
		};
	},
	components: {
		SiteLogo
	},
	methods: {
		// loginWithGoogle() {
		//     console.log('Google Login clicked');
		//     // alert('Google Login is not implemented yet. Mock test');
		// },

		async handleSubmit() {
			if (!this.showCodeInput) {
				await this.getValidationCode();
			} else {
				await this.login();
			}
		},

		async getValidationCode() {
			this.isLoading = true;
			this.errorMessage = '';
			console.log(Env.API_BASE_URL + '/users/request-code');
			try {
				const response = await fetch(Env.API_BASE_URL + '/users/request-code', {
					method: 'POST',
					headers: { 'Content-Type': 'application/json' },
					body: JSON.stringify({ email: this.email }),
				});

				if (response.ok) {
					this.showCodeInput = true;
					// alert('Validation code sent! Check your email.');
				} else {
					const data = await response.json();
					this.errorMessage = data.message || 'Failed to send validation code.';
				}
			} catch (error) {
				console.log('Network Error:', error);
				this.errorMessage = 'Network error. Please try again.';
			} finally {
				this.isLoading = false;
			}
		},

		async login() {
			this.errorMessage = '';
			try {
				const response = await fetch(Env.API_BASE_URL + '/users/verify-code', {
					method: 'POST',
					headers: { 'Content-Type': 'application/json' },
					body: JSON.stringify({ code: this.validationCode, email: this.email }),
				});

				if (response.ok) {
					// alert('Login successful!');
					User.login(this.email, this.validationCode);
					const productId = this.$route.params.productId; // Check if there's a product ID to add
					if (productId) {
						// Add product to cart
						await User.addToCart(productId, this.$router);

						// Determine redirection based on the previous route
						const previousPage = this.$router?.history?.state?.back;
						if (!previousPage || previousPage === '/login') {
							// Redirect to main page if no previous page or came from login
							this.$router.push('/');
						} else {
							// Go back to the previous page
							this.$router.push(previousPage);
						}
					} else {
						// Redirect to profile if no productId provided
						console.log("Go to profile", User.getUserEmailCode());
						this.$router.push('/profile');
					}
					return;
				}

				const data = await response.json();
				this.errorMessage = data.message || 'Invalid validation code. Please try again.';
			} catch (error) {
				console.log('Network Error:', error);
				this.errorMessage = 'Network error. Please try again.';
			}
		},

		resendCode() {
			console.log('Resending validation code...');
			this.getValidationCode();
		},
	},
};
</script>

<style scoped lang="scss">
.login-container {
	// background-color: var(--white-bg-color);
	// padding: var(--padding-container);
	// border-radius: var(--border-radius);
	max-width: var(--width-side);
	box-sizing: content-box;
	margin: 0 auto;
	text-align: center;

	form {
		padding: var(--padding-container-small);
		box-sizing: content-box;

		h3 {
			margin-top: 0;
		}


		input {
			margin-bottom: var(--spacing-element);
		}

		button {
			// position: relative;
			width: 100%;
		}

		button[disabled] {
			cursor: not-allowed;
			opacity: 0.6;
		}

	}

	.resend {
		font-size: 0.8em;
		a {
			color: var(--primary-color);
			text-decoration: underline;
		}
	}


	.error {
		color: var(--primary-color);
		margin-top: var(--spacing-element);
	}
}
</style>
