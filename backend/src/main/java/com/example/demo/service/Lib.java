package com.example.demo.service;

import java.security.SecureRandom;
import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class Lib {
	public static Map<String, String> RestMsg(String message) {
		Map<String, String> response = new HashMap<>();
		response.put("message", message);
		return response;
	}

	public static ResponseEntity<Map<String, String>> RestBadRequest(String message) {
		return ResponseEntity.badRequest().body(RestMsg(message));
	}

	public static ResponseEntity<Map<String, String>> RestOk(String message) {
		return ResponseEntity.ok(Lib.RestMsg(message));
	}

	public static ResponseEntity<Map<String, String>> RestServerError(String message) {
		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
				Lib.RestMsg(message));
	}

	public static ResponseEntity<Map<String, String>> RestUnauthorized(String message) {
		return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Lib.RestMsg(message));
	}
	public static ResponseEntity<Map<String, String>> RestNotFound(String message) {
		return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Lib.RestMsg(message));
	}

	public static String ValidationCode() {
		if (Env.IS_DEVELOPING) {
			return "123456";
		}
		return ValidationCode(Env.VALIDATION_CODE_LENGTH);
	}

	private static final String CHARACTERS =
			// "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!@#$%^&*()";
			"ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
	private static final SecureRandom RANDOM = new SecureRandom();

	public static String ValidationCode(int length) {
		StringBuilder result = new StringBuilder(length);

		for (int i = 0; i < length; i++) {
			int index = RANDOM.nextInt(CHARACTERS.length());
			result.append(CHARACTERS.charAt(index));
		}

		return result.toString();
	}

	public static boolean isValidAddress(String address) {
		if (address == null || address.trim().isEmpty()) {
			return false;
		}

		String[] parts = address.trim().split(",");
		return parts.length >= 3 &&
				parts[0].contains(" ") && // Check if the first part has at least one space
				parts[1].trim().length() >= 3 && // City name >= 3 chars
				parts[2].trim().length() == 2; // Province name = 2 chars

	}
}
