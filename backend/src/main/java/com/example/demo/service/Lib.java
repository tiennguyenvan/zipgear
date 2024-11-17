package com.example.demo.service;

import java.security.SecureRandom;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.example.demo.model.User;
import com.example.demo.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;

public class Lib {
	private static final ObjectMapper objectMapper = new ObjectMapper();

	private static final Map<String, String> validationCodes = new ConcurrentHashMap<>();
	private static final Map<String, String> activeSessions = new ConcurrentHashMap<>();
	public static ResponseEntity<?> userRestResponseErr = null;
	public static String loggedInEmail = "";

	@Autowired
	private static UserRepository userRepository;

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

	///////////////////////////////////////
	/// User validation
	// --- Utility Method to Validate Session and Return User ---
	public static User getRequestingUser(
			Map<String, ?> request, UserRepository userRepository) {
		String email = (String) request.get("email");
		String code = (String) request.get("code");

		return getRequestingUser(email, code, userRepository);
	}

	public static User getRequestingUser(String email, String code, UserRepository userRepository) {
		// Step 1: Check if email and code are provided
		if (email == null) {
			userRestResponseErr = RestBadRequest("Email is required.");
			return null;
		}
		if (!Env.IS_DEVELOPING) {
			if (code == null) {
				userRestResponseErr = RestBadRequest("Code is required.");
				return null;
			}
			if (!isSessionValid(email, code)) {
				userRestResponseErr = RestUnauthorized("Session expired. Please login again.");
				return null;
			}
		}

		User user = userRepository.findByEmail(email);
		if (user == null) {
			userRestResponseErr = RestNotFound("User not found.");
			return null;
		}
		loggedInEmail = email;
		return user;
	}

	public static boolean isUserAdmin() {		
		return loggedInEmail.equals(Env.ADMIN_EMAIL);
	}

	public static void storeValidationCode(String email, String code) {
		validationCodes.put(email, code);
	}

	public static boolean isVerifyValidationCodeSuccess(String email, String code) {
		return code != null && code.equals(validationCodes.get(email));
	}

	public static void removeValidationCode(String email) {
		validationCodes.remove(email);
	}

	// --- Active Session Methods ---
	public static void storeActiveSession(String email, String code) {
		activeSessions.put(email, code);
	}

	public static boolean isSessionValid(String email, String code) {
		return code != null && code.equals(activeSessions.get(email));
	}

	public static void removeActiveSession(String email) {
		activeSessions.remove(email);
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

	//////////////////////////////////////////
	// utitlities
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
