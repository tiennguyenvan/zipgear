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
@font-face {
    font-family: 'Lexend';
    src: url('@/assets/font/Lexend/static/Lexend-Black.ttf') format('truetype');
    font-weight: 900;
    font-style: normal;
}

@font-face {
    font-family: 'Lexend';
    src: url('@/assets/font/Lexend/static/Lexend-Bold.ttf') format('truetype');
    font-weight: 700;
    font-style: normal;
}

@font-face {
    font-family: 'Lexend';
    src: url('@/assets/font/Lexend/static/Lexend-SemiBold.ttf') format('truetype');
    font-weight: 600;
    font-style: normal;
}

@font-face {
    font-family: 'Lexend';
    src: url('@/assets/font/Lexend/static/Lexend-Medium.ttf') format('truetype');
    font-weight: 500;
    font-style: normal;
}

@font-face {
    font-family: 'Lexend';
    src: url('@/assets/font/Lexend/static/Lexend-Regular.ttf') format('truetype');
    font-weight: 400;
    font-style: normal;
}

@font-face {
    font-family: 'Lexend';
    src: url('@/assets/font/Lexend/static/Lexend-Light.ttf') format('truetype');
    font-weight: 300;
    font-style: normal;
}

@font-face {
    font-family: 'Lexend';
    src: url('@/assets/font/Lexend/static/Lexend-ExtraLight.ttf') format('truetype');
    font-weight: 200;
    font-style: normal;
}

@font-face {
    font-family: 'Lexend';
    src: url('@/assets/font/Lexend/static/Lexend-Thin.ttf') format('truetype');
    font-weight: 100;
    font-style: normal;
}


#app {	
	-webkit-font-smoothing: antialiased;
	-moz-osx-font-smoothing: grayscale;		
	margin-top: var(--padding-container);
}

/* ====== COLORS ====== */
:root {
	--primary-color: #C21715;
	--secondary-color: #d01a17;
	--bg-color: #EAEAEE;
	--light-bg-color: #F4F5F9;
	--white-bg-color: #FFFFFF;
	--text-color: #000000;
	--light-text-color: #969696;
	--border-color: #ddd;
	--success-color: #34A853;

	/* ====== TYPOGRAPHY ====== */
	// --font-family: 'Poppins', sans-serif;
	--font-family: 'Lexend', sans-serif;
	--font-size-title: 32px;
	--font-size-subtitle: 24px;
	--font-size-content: 16px;
	--font-size-card: 14px;
	--font-size-button: 14px;
	--font-size-input: 14px;
	--font-weight-title: 700;
	--font-weight-button: 600;

	/* ====== SPACING ====== */	
	--padding-container-small: 32px;
	--padding-container: 48px;
	--padding-button: 12px 24px;
	--spacing-element-small: 8px;
	--spacing-element: 16px;
	--spacing-element-big: 32px;
	--spacing-element-large: 48px;
	--border-radius: 8px;

	/* ====== DIMENSIONS ====== */
	--width-wide: 1200px;
	--width-content: 800px;
	--width-side: 300px;
}

/* ====== GENERAL STYLES ====== */
body {
	font-family: var(--font-family);
	font-size: var(--font-size-content);
	background-color: var(--bg-color);
	color: var(--text-color);
	margin: 0;
	padding: 0;
	word-wrap: break-word;
}

h1 {
	font-size: var(--font-size-title);
}
h2 {
	font-size: var(--font-size-subtitle);
}
h3 {
	font-size: var(--font-size-content);
}
h4 {
	font-size: var(--font-size-card);
}
/* ====== LINK ====== */

a {
	color: var(--text-color);
	text-decoration: none;

	&:hover {
		transform: scale(1.02);
	}

	&:active {
		transform: scale(0.98);
	}
}


/* ====== BUTTON ====== */
button {
	background-color: var(--white-bg-color);
	color: var(--primary-color);
	padding: var(--padding-button);
	border: 1px solid var(--primary-color);
	border-radius: var(--border-radius);
	font-size: var(--font-size-button);
	font-weight: var(--font-weight-button);
	cursor: pointer;

	// transition: background-color 0.2s ease;
	&:hover {
		transform: scale(1.02);
		/* Slight click effect */
	}

	&:active {
		transform: scale(0.98);
		/* Slight click effect */
	}
	position: relative;
}

.primary-btn, button[type="submit"] {
	background-color: var(--primary-color);
	color: var(--white-bg-color);
	border-color: transparent;	
}
.primary-btn:hover, button[type="submit"]:hover {
	background-color: var(--secondary-color);
}

button[disabled],
button[disabled]:hover,
button[disabled]:active,
button.disable-btn,
button.disable-btn:hover,
button.disable-btn:active {
	background-color: var(--light-text-color);
	color: var(--white-bg-color);
	border-color: transparent;
	cursor: not-allowed;
	/* Cursor shows as disabled */
	opacity: 0.6;
	/* Slightly faded to indicate disabled state */
	transform: none;
	/* No click effect */
}

/* ====== INPUT STYLES ====== */
input {
	background-color: var(--white-bg-color);
	border: 1px solid var(--border-color);
	padding: 12px;
	font-size: var(--font-size-input);
	color: var(--text-color);
	border-radius: var(--border-radius);
	width: 100%;
	box-sizing: border-box;
}

input::placeholder {
	color: var(--light-text-color);
}

input:focus {
	border-color: var(--primary-color);
	outline: none;
}
a.action, button.action {
	font-weight: normal;
	font-size: var(--font-size-card);
	color: var(--primary-color);
	&:hover {
		text-decoration: underline;
	}
}

a.selected,
a.selected:hover,
a.selected:active,
button.selected,
button.selected:hover,
button.selected:active,
a.active,
a.active:hover,
a.active:active,
button.active,
button.active:hover,
button.active:active {
	color: var(--light-text-color);
}

/* ====== BORDER UTILITIES ====== */
.border {
	border: 1px solid var(--border-color);
	border-radius: var(--border-radius);
}
.border-radius {
	border-radius: var(--border-radius);
}
.no-border {
	border: none;
}

/* ====== DESIGN SPECIALITY ====== */
.card {
	background-color: var(--white-bg-color);
	border-radius: var(--border-radius);
	box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
	min-height: 1vh;
	border: 1px solid transparent;
	font-size: var(--font-size-card);
}
.dim-text {
	font-size: var(--font-size-card);
	color: var(--light-text-color);
}
.flex {
	display: flex;
	justify-content: space-between;
	align-items: center;
	padding: 0;
	margin: 0;
}

/* ====== LAYOUT WIDTH CLASSES ====== */
.content {
	width: var(--width-content);
	margin: 0 auto;
}
.wide {
	width: var(--width-wide);
	margin: 0 auto;
}
.wrapper {
	align-items: stretch;
}
.main {
	background-color: var(--light-bg-color);
	flex-grow: 1;
	width: 0;
	padding: var(--padding-container);	
	height: 100%;
	min-height: 70vh;
}
.sidebar {
	width: var(--width-side);
	padding: var(--padding-container);
	height: 100%;
}

</style>
