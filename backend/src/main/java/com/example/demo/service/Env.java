package com.example.demo.service;

import io.github.cdimascio.dotenv.Dotenv;

public class Env {
	private static final Dotenv dotenv = Dotenv.load();	
	public static final String GMAIL_ADDRESS = dotenv.get("GMAIL_ADDRESS");
	public static final String GMAIL_CLIENT_ID = dotenv.get("GMAIL_CLIENT_ID");
	public static final String GMAIL_CLIENT_SECRET = dotenv.get("GMAIL_CLIENT_SECRET");
	public static final String GMAIL_REFRESH_TOKEN = dotenv.get("GMAIL_REFRESH_TOKEN");
	public static final String SENDGRID_API_KEY = dotenv.get("SENDGRID_API_KEY");
}
