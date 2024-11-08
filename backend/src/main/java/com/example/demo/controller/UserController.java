package com.example.demo.controller;

import com.example.demo.model.User;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.EmailService;
import com.example.demo.service.Lib;

import org.eclipse.angus.mail.handlers.message_rfc822;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@RestController
@RequestMapping("/api/users")
public class UserController {

	@Autowired
	private EmailService emailService;

	@Autowired
	private UserRepository userRepository;

	// Store validation codes temporarily (In-memory storage)
	private final Map<String, String> validationCodes = new ConcurrentHashMap<>();
	private final Map<String, String> activeSessions = new ConcurrentHashMap<>();

	// POST api/user/request-code
	@PostMapping("/request-code")
	public ResponseEntity<Map<String, String>> requestValidationCode(@RequestBody Map<String, String> request) {
		String email = request.get("email");
		if (email == null || email.isEmpty()) {
			return ResponseEntity.badRequest().body(Lib.ResponseMessage("Email is required."));
		}

		// Generate validation code
		String code = Lib.ValidationCode();
		System.out.println("Generated code: " + code);
		validationCodes.put(email, code);

		// Send the code via email
		try {
			emailService.sendEmail(email, "Your Validation Code", "Your code is: " + code);
			return ResponseEntity.ok(Lib.ResponseMessage("Validation code sent successfully."));
		} catch (IOException e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
					Lib.ResponseMessage("Failed to send validation code."));
		}
	}

	/**
	 * 
	 * @param request
	 * @return
	 */
	@PostMapping("/verify-code")
	public ResponseEntity<Map<String, String>> verifyCode(@RequestBody Map<String, String> request) {
		String email = request.get("email");
		String code = request.get("code");

		if (email == null || code == null) {

			return ResponseEntity.badRequest().body(Lib.ResponseMessage("Email and code are required."));
		}
		String storedCode = validationCodes.get(email);

		if (storedCode == null || !storedCode.equals(code)) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Lib.ResponseMessage("Invalid validation code."));
		}

		// System.out.println("storedCode: " + storedCode);
		validationCodes.remove(email); // Remove the code after successful verification

		// Store the code as a session (email -> code)
		activeSessions.put(email, code);

		// Create the user if not already exists
		if (userRepository.findByEmail(email) == null) {
			User user = new User(email, null); // No addresses for now
			userRepository.save(user);
		}
		return ResponseEntity.ok(Lib.ResponseMessage("Login successful."));
	}

	@GetMapping("/")
	public ResponseEntity<?> getUserData(
			@RequestParam String email,
			@RequestParam String code,
			@RequestParam(required = false) List<String> fields) {

		// Step 1: Validate the session (email + code)
		String sessionCode = activeSessions.get(email);
		if (sessionCode == null || !sessionCode.equals(code)) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
					.body(Lib.ResponseMessage("Session expired. Please login again."));
		}

		// Step 2: Retrieve the user from the database
		User user = userRepository.findByEmail(email);
		if (user == null) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Lib.ResponseMessage("User not found."));
		}

		// Step 3: Return all data if no fields are specified
		if (fields == null || fields.isEmpty()) {
			return ResponseEntity.ok(user); // Return full user object
		}

		// Step 4: Create a response with only the requested fields
		Map<String, Object> responseData = new ConcurrentHashMap<>();
		for (String field : fields) {
			switch (field.toLowerCase()) {
				case "userid":
					responseData.put("userId", user.getUserId());
					break;
				case "email":
					responseData.put("email", user.getEmail());
					break;
				case "addresses":
					responseData.put("addresses", user.getAddresses());
					break;
				case "createdat":
					responseData.put("createdAt", user.getCreatedAt());
					break;
				case "updatedat":
					responseData.put("updatedAt", user.getUpdatedAt());
					break;
				default:
					// Ignore unknown fields or return an error message (your choice)
					responseData.put(field, "Field not found");
			}
		}

		return ResponseEntity.ok(responseData);
	}

	@PatchMapping("/")
	public ResponseEntity<Map<String, String>> updateUserProfile(@RequestBody Map<String, Object> userUpdateMap) {
		// Extract fields from the map
		String email = (String) userUpdateMap.get("email");
		String code = (String) userUpdateMap.get("code");
		List<String> newAddresses = (List<String>) userUpdateMap.get("addresses");

		// Validate session
		String sessionCode = activeSessions.get(email);
		if (sessionCode == null || !sessionCode.equals(code)) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
					.body(Lib.ResponseMessage("Session expired. Please login again."));
		}

		// Validate email
		if (email == null || email.isEmpty()) {
			return ResponseEntity.badRequest().body(Lib.ResponseMessage("Email is required."));
		}

		User existingUser = userRepository.findByEmail(email);
		if (existingUser == null) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Lib.ResponseMessage("User not found."));
		}

		// Validate addresses
		if (newAddresses == null || newAddresses.isEmpty()) {
			return ResponseEntity.badRequest().body(Lib.ResponseMessage("Invalid update request."));
		}

		for (String address : newAddresses) {
			if (!Lib.isValidAddress(address)) {
				return ResponseEntity.badRequest().body(Lib.ResponseMessage("Invalid address: " + address));
			}
		}

		// Update user addresses
		existingUser.setAddresses(newAddresses);
		userRepository.save(existingUser);

		return ResponseEntity.ok(Lib.ResponseMessage("User profile updated successfully."));
	}

	/**
	 * validateAddress() {
	 * const addressParts = this.newAddress.trim().split(',');
	 * const isValidFormat =
	 * addressParts.length >= 3 &&
	 * /\d+\s+\w+/.test(addressParts[0]) && // Block number + street
	 * addressParts[1].trim().length >= 3 && // City name >= 3 chars
	 * addressParts[2].trim().length === 2; // Province name = 2 chars
	 * 
	 * const isDuplicate = this.addresses.includes(this.newAddress.trim());
	 * 
	 * this.isAddressValid = isValidFormat && !isDuplicate;
	 * },
	 */

	/**
	 * Google login
	 * Frontend redirects the user to Google’s OAuth Login Page.
	 * Google authenticates the user and redirects the user to backend with an
	 * [authorization code].
	 * Backend exchanges the authorization code for an [access token] with Google
	 * Backend uses the access token to fetch the user’s email from Google.
	 * Backend processes the login (creating the user in the database if necessary)
	 * and sends a success response
	 * to the frontend.
	 */
}
