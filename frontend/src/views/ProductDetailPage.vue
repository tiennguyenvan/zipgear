<template>
	<div class="wide">
		<NavBar />

		<div class="card user-profile flex wrapper">
			<div class="product-detail main">
				<h2>Place Product Image here</h2>							
			</div>
			
			<div class="info sidebar">
				<h2>Place Produc INFORMATION here</h2>
				<p>Lorem ipsum dolor sit amet consectetur adipisicing elit. Veritatis, pariatur quasi. Eligendi pariatur atque corrupti, quis, totam architecto magnam inventore in enim consectetur voluptas error! Reprehenderit aperiam officia itaque officiis.
				</p>
			</div>
		</div>
	</div>
</template>
<script>
export default {
    name: 'ProductProfilePage',
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
