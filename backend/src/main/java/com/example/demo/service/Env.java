package com.example.demo.service;

import io.github.cdimascio.dotenv.Dotenv;

public class Env {
	private static final Dotenv dotenv = Dotenv.configure().directory("../").load();	
	public static final String ADMIN_EMAIL = dotenv.get("VUE_APP_ADMIN_EMAIL");
	public static final String SENDGRID_API_KEY = dotenv.get("VUE_APP_SENDGRID_API_KEY");

	public static final String SKIP_SENDING_LOGIN_EMAIL = dotenv.get("VUE_APP_SKIP_SENDING_LOGIN_EMAIL");
}
