<template>
	<div class="wide">
		<NavBar />
		<div class="card home">
			{{ adminEmail }}
		</div>
	</div>
</template>

<script>
import NavBar from '@/components/NavBar.vue';
import Env from '@/utils/Env';

export default {
	name: 'HomePage',
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