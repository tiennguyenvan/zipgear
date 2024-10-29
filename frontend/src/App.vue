<template>
	<!-- <img alt="Vue logo" src="./assets/logo.png"> -->
	<!-- <HelloWorld msg="Welcome to Your Vue.js App" /> -->
	<router-view />
</template>

<script>
// import HelloWorld from './components/HelloWorld.vue'
// import LoginPage from './views/LoginPage.vue';

export default {
	name: 'App',
	// components: {
	// 	// LoginPage,
	// 	// HelloWorld,
	// }
}
</script>

<style lang="scss">
@use "@/assets/theme.scss";
#app {	
	-webkit-font-smoothing: antialiased;
	-moz-osx-font-smoothing: grayscale;		
	margin-top: var(--padding-container);
}
</style>
