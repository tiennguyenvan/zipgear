<template>
	<router-link to="/" class="site-logo flex" title="Home Page">
		<span class="hash">#</span>
		<span class="brand-name">zipGear</span>
	</router-link>
</template>

<script>
export default {
	name: 'SiteLogo',
};
</script>

<style scoped lang="scss">
.site-logo {
	font-family: var(--font-family);
	font-size: var(--font-size-title);
	font-weight: var(--font-weight-title);
	justify-content: start;
	padding-bottom: var(--spacing-element-small);
}
.hash {
	color: var(--primary-color);
}
.brand-name {
	margin-left: 8px;
}
</style>