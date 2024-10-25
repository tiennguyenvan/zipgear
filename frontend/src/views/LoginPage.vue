<template>
	<div class="login-container">
		<h1># zipGear</h1>

		<!-- <button @click="loginWithGoogle">Login with Google</button> -->

		<form @submit.prevent="handleSubmit">
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

				<p>
					Didn’t receive a code?
					<a href="#" @click.prevent="resendCode">Resend Code</a>
				</p>
				<p v-if="errorMessage" class="error">{{ errorMessage }}</p>
			</div>
		</form>
	</div>
</template>

<script>


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

			try {
				const response = await fetch('http://localhost:8080/api/user/request-code', {
					method: 'POST',
					headers: { 'Content-Type': 'application/json' },
					body: JSON.stringify({ email: this.email }),
				});

				if (response.ok) {
					this.showCodeInput = true;
					alert('Validation code sent! Check your email.');
				} else {
					const data = await response.json();
					this.errorMessage = data.message || 'Failed to send validation code.';
				}
			} catch (error) {
				console.error('Network Error:', error);
				this.errorMessage = 'Network error. Please try again.';
			} finally {
				this.isLoading = false;
			}
		},

		async login() {
			this.errorMessage = '';
			try {
				const response = await fetch('http://localhost:8080/api/user/verify-code', {
					method: 'POST',
					headers: { 'Content-Type': 'application/json' },
					body: JSON.stringify({ code: this.validationCode, email: this.email }),
				});

				if (response.ok) {
					// alert('Login successful!');
					localStorage.setItem('email', this.email);
					localStorage.setItem('code', this.validationCode);
					this.$router.push('/profile');
					return;
				}

				const data = await response.json();
				this.errorMessage = data.message || 'Invalid validation code. Please try again.';
			} catch (error) {
				console.error('Network Error:', error);
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
	background-color: var(--white-bg-color);
	padding: var(--padding-container);
	border-radius: var(--border-radius);
	max-width: var(--width-content);
	margin: 0 auto;
	text-align: center;
}

input {
	margin-bottom: var(--spacing-element);
}

button {
	position: relative;
}

button[disabled] {
	cursor: not-allowed;
	opacity: 0.6;
}

.error {
	color: var(--primary-color);
	margin-top: var(--spacing-element);
}
</style>
