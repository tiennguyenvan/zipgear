<template>
	<div class="wide">
		<NavBar />

		<div class="card user-profile flex wrapper">
			<div class="cart main">				
				<!--your product loop here using the dummy data from the data() script-->
			</div>
			
			<div class="checkout sidebar">
				<h2>Please search filer here please</h2>
				<p>For Admin, this sidebar will become the edit/add-new form if needed</p>
			</div>
		</div>
	</div>
</template>


<script>
import NavBar from '@/components/NavBar.vue';
import Env from '@/utils/Env';

export default {
	name: 'CheckoutPage',
	components: {
		NavBar,		
	},
	data() {
		return {
			adminEmail: Env.ADMIN_EMAIL,			
		};
	},
	mounted() {
		console.log(process.env);
	}
};
</script>

<style scoped lang="scss"></style>