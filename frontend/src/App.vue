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

<style scoped lang="scss">
@use "@/assets/theme.scss";
#app {
	font-family: Avenir, Helvetica, Arial, sans-serif;
	-webkit-font-smoothing: antialiased;
	-moz-osx-font-smoothing: grayscale;
	text-align: center;
	color: #2c3e50;
	margin-top: 60px;
}
</style>
