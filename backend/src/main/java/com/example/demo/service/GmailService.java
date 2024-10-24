package com.example.demo.service;

import jakarta.mail.*;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import java.util.Base64;
import java.util.Properties;

public class GmailService {

	public static void sendEmail(String recipient, String subject, String body) throws Exception {
		// Fetch OAuth2 access token
		// String accessToken = OAuth2TokenFetcher.getAccessToken();
		// testing
		String accessToken = "ya29.a0AcM612x8lAvTDbKZhKnNeP2uGBOIbd-R6V2FtPAzhyQo38WXFTiUVNvGrqV4bJUtDYNSBRmouyxqRrMyDBTqLe9Fcpe0HKFia2HwrjXIBRdIFA9ittB84z-alftkNVfUq2aJxogZ6E6sMSIaZGhPemw-kGlzhCDGjJ1Xu-fdaCgYKAR4SARESFQHGX2MieTnGdMpY2D9ulivbsqq3Wg0175";
		System.out.println("Access Token: " + accessToken); // Debugging

		// Configure properties for Gmail SMTP
		Properties props = new Properties();
		props.put("mail.smtp.auth", "true");
		props.put("mail.smtp.starttls.enable", "true");
		props.put("mail.smtp.host", "smtp.gmail.com");
		props.put("mail.smtp.port", "587");

		// Create a new session
		Session session = Session.getInstance(props);

		// Create the email message
		MimeMessage message = new MimeMessage(session);
		message.setFrom(new InternetAddress(Env.GMAIL_ADDRESS));
		message.setRecipient(Message.RecipientType.TO, new InternetAddress(recipient));
		message.setSubject(subject);
		message.setText(body);

		// Use Transport with XOAUTH2 authentication
		try (Transport transport = session.getTransport("smtp")) {			
			String oauth2Token = buildOAuth2Token(Env.GMAIL_ADDRESS, accessToken);
			transport.connect("smtp.gmail.com", Env.GMAIL_ADDRESS, oauth2Token); // Use the token
			transport.sendMessage(message, message.getAllRecipients());
		}

		System.out.println("Email sent successfully to " + recipient);
	}

	private static String buildOAuth2Token(String email, String accessToken) {
		String authString = "user=" + email + "\u0001auth=Bearer " + accessToken + "\u0001\u0001";
		return Base64.getEncoder().encodeToString(authString.getBytes());
	}
}