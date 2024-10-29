package com.example.demo.service;

import io.github.cdimascio.dotenv.Dotenv;

public class Env {
	private static final Dotenv dotenv = Dotenv.configure().directory("../").load();	
	public static final String SENDGRID_API_KEY = dotenv.get("VUE_APP_SENDGRID_API_KEY", "");
	public static final String SENDGRID_FROM_EMAIL = dotenv.get("VUE_APP_SENDGRID_FROM_EMAIL", "contact@sneeit.com");
	public static final String ADMIN_EMAIL = dotenv.get("VUE_APP_ADMIN_EMAIL", "nguyentient@gmail.com");
	public static final boolean SKIP_SENDING_LOGIN_EMAIL = Boolean.parseBoolean(dotenv.get("VUE_APP_SKIP_SENDING_LOGIN_EMAIL", "true"));
	public static final int VALIDATION_CODE_LENGTH = Integer.parseInt(dotenv.get("VUE_APP_VALIDATION_CODE_LENGTH", "8"));
}
