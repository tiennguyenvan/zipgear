<template>
	<div class="wide">
		<NavBar />

		<div class="card user-profile flex wrapper">
			<div class="products main">				
				<!--your product loop here using the dummy data from the data() script-->
			</div>
			
			<div class="info sidebar">
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
	name: 'ProductListPage',
	components: {
		NavBar,		
	},
	data() {
		return {
			adminEmail: Env.ADMIN_EMAIL,
			// Products JSON data
			products: [
			{
					title: '____',
					price: '$1,299.99',
					rating: '4.2',
					images: [],					
				},
			],
		};
	},
	mounted() {
		console.log(process.env);
	}
};
</script>

<style scoped lang="scss"></style>