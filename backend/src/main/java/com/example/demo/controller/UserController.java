package com.example.demo.controller;

import com.example.demo.model.User;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.EmailService;
import com.example.demo.service.Lib;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@RestController
@RequestMapping("/api/user")
public class UserController {

	@Autowired
	private EmailService emailService;

	@Autowired
	private UserRepository userRepository;

	// Store validation codes temporarily (In-memory storage)
	private final Map<String, String> validationCodes = new ConcurrentHashMap<>();
	private final Map<String, String> activeSessions = new ConcurrentHashMap<>();

	@PostMapping("/request-code")
	public ResponseEntity<String> requestValidationCode(@RequestBody Map<String, String> request) {
		String email = request.get("email");
		if (email == null || email.isEmpty()) {
			return ResponseEntity.badRequest().body("Email is required.");
		}

		// Generate validation code
		String code = Lib.ValidationCode();
		validationCodes.put(email, code);

		// Send the code via email
		try {
			emailService.sendEmail(email, "Your Validation Code", "Your code is: " + code);
			return ResponseEntity.ok("Validation code sent successfully.");
		} catch (IOException e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to send validation code.");
		}
	}

	/**
	 * 
	 * @param request
	 * @return
	 */
	@PostMapping("/verify-code")
	public ResponseEntity<String> verifyCode(@RequestBody Map<String, String> request) {
		String email = request.get("email");
		String code = request.get("code");

		if (email == null || code == null) {
			return ResponseEntity.badRequest().body("Email and code are required.");
		}

		// Check if the code matches
		String storedCode = validationCodes.get(email);
		if (storedCode != null && storedCode.equals(code)) {
			validationCodes.remove(email); // Remove the code after successful verification

			// Store the code as a session (email -> code)
			activeSessions.put(email, code);

			// Create the user if not already exists
			if (userRepository.findByEmail(email) == null) {
				User user = new User(email, null); // No addresses for now
				userRepository.save(user);
			}

			return ResponseEntity.ok("Login successful.");
		} else {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid validation code.");
		}
	}

	@GetMapping("/get-data")
	public ResponseEntity<?> getUserData(
			@RequestParam String email,
			@RequestParam String code,
			@RequestParam(required = false) List<String> fields) {

		// Step 1: Validate the session (email + code)
		String sessionCode = activeSessions.get(email);
		if (sessionCode == null || !sessionCode.equals(code)) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Session expired. Please login again.");
		}

		// Step 2: Retrieve the user from the database
		User user = userRepository.findByEmail(email);
		if (user == null) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found.");
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

		// Step 5: Return the response data
		return ResponseEntity.ok(responseData);
	}

	@PatchMapping("/update")
	public ResponseEntity<String> updateUserProfile(@RequestParam String email, @RequestParam String code,
			@RequestBody User userUpdates) {

		// Validate session
		String sessionCode = activeSessions.get(email);
		if (sessionCode == null || !sessionCode.equals(code)) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Session expired. Please login again.");
		}

		String updateUserEmail = userUpdates.getEmail();
		if (updateUserEmail == null || !updateUserEmail.equals(email)) {
			return ResponseEntity.badRequest().body("Mismatching user email.");
		}

		// Retrieve user
		User existingUser = userRepository.findByEmail(email);
		if (existingUser == null) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found.");
		}

		// Update addresses if provided
		List<String> newAddresses = userUpdates.getAddresses();
		if (newAddresses == null || newAddresses.isEmpty()) {
			return ResponseEntity.badRequest().body("Invalid update request");
		}

		for (String address : newAddresses) {
			if (!Lib.isValidAddress(address)) {
				return ResponseEntity.badRequest().body("Invalid address: " + address);
			}
		}

		existingUser.setAddresses(newAddresses);

		// Save updated user
		userRepository.save(existingUser);
		return ResponseEntity.ok("User profile updated successfully.");
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
