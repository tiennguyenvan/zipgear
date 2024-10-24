<template>
	<div>
		<h1># zipGear</h1>

		<!-- Google Login Button -->
		<button @click="loginWithGoogle">Login with Google</button>

		<!-- Email Login Section -->
		<div>
			<h3>Login with Email</h3>
			<input type="email" placeholder="Email Address" v-model="email" />
			<button @click="getValidationCode">Get Validation Code</button>
		</div>

		<!-- Show response message -->
		<p v-if="message">{{ message }}</p>
	</div>
</template>

<script>
export default {
	data() {
		return {
			email: '', // Store the user email
			message: '', // Store any message from the backend
		};
	},
	methods: {		
		loginWithGoogle() {
			console.log('Google Login clicked. We will implement later');
			alert('Google Login is not implemented yet.');
		},

		// Call Backend API to Get Validation Code
		async getValidationCode() {
			if (!this.email) {
				alert('Please enter a valid email address.');
				return;
			}

			try {
				// Example: Call backend API (replace with your real API URL)
				const response = await fetch('http://localhost:8080/api/send-code', {
					method: 'POST',
					headers: {
						'Content-Type': 'application/json',
					},
					body: JSON.stringify({ email: this.email }),
				});

				// Check if the response is OK
				if (response.ok) {
					const data = await response.json();
					this.message = `Validation code sent to ${this.email}`;
					console.log('Backend Response:', data);
				} else {
					this.message = 'Failed to send validation code.';
					console.error('Error:', response.statusText);
				}
			} catch (error) {
				this.message = 'Error connecting to the server.';
				console.error('Network Error:', error);
			}
		},
	},
};
</script>