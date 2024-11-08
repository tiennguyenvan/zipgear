<template>
	<div class="products main">				
		<ProductListItem/>
		<ProductListItem/>
		<ProductListItem/>
		<ProductListItem/>
		<ProductListItem/>
	</div>
</template>

<script>
import ProductListItem from './ProductListItem.vue';

export default {
	name: 'UserProfile',
	components: {		
		ProductListItem,
	},
};
</script>

<style scoped lang="scss">
</style>
