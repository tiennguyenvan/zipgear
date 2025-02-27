// Env.js: Frontend environment variables manager
export default class Env {
    static ADMIN_EMAIL = process.env.VUE_APP_ADMIN_EMAIL;
	static API_BASE_URL = process.env.VUE_APP_API_BASE_URL;
	static K_EMAIL = "email";
	static K_CODE = "code";
	static SERVER_URL = process.env.VUE_APP_SERVER_URL;
}
