package com.example.demo.controller;

import com.example.demo.model.User;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.EmailService;
import com.example.demo.service.Env;
import com.example.demo.service.Lib;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@RestController
@RequestMapping("/api")
public class UserController {

	@Autowired
	private EmailService emailService;

	@Autowired
	private UserRepository userRepository;

	// Store validation codes temporarily (In-memory storage)

	// POST api/user/request-code
	@PostMapping("/users/request-code")
	public ResponseEntity<Map<String, String>> requestValidationCode(@RequestBody Map<String, String> request) {
		String email = request.get("email");
		if (email == null || email.isEmpty()) {
			return Lib.RestBadRequest("Email is required.");
		}

		// Generate validation code
		String code = Lib.ValidationCode();
		// System.out.println("Generated code: " + code);
		// validationCodes.put(email, code);
		Lib.storeValidationCode(email, code);

		// Send the code via email
		try {
			emailService.sendEmail(email, "Your Validation Code", "Your code is: " + code);
			return Lib.RestOk("Validation code sent successfully.");
		} catch (IOException e) {
			e.printStackTrace();
			return Lib.RestServerError("Failed to send validation code.");
		}
	}

	/**
	 * 
	 * @param request
	 * @return
	 */
	@PostMapping("/users/verify-code")
	public ResponseEntity<Map<String, String>> verifyCode(@RequestBody Map<String, String> request) {
		String email = request.get("email");
		String code = request.get("code");

		if (email == null || code == null) {
			return Lib.RestBadRequest("Email and code are required.");
		}

		if (!Env.IS_DEVELOPING) {			
			if (!Lib.isVerifyValidationCodeSuccess(email, code)) {
				return Lib.RestUnauthorized("Invalid validation code.");
			}
		}

		// System.out.println("storedCode: " + storedCode);
		Lib.removeValidationCode(email);
		Lib.storeActiveSession(email, code);

		// Create the user if not already exists
		if (userRepository.findByEmail(email) == null) {
			User user = new User(email, null); // No addresses for now
			userRepository.save(user);
		}
		return Lib.RestOk("Login successful.");
	}

	@GetMapping("/users")
	public ResponseEntity<?> getUserData(
			@RequestParam String email,
			@RequestParam String code,
			@RequestParam(required = false) List<String> fields) {
		User requestingUser = Lib.getRequestingUser(email, code, userRepository);
		if (requestingUser == null) {
			return Lib.userRestResponseErr;
		}

		// Step 3: Return all data if no fields are specified
		if (fields == null || fields.isEmpty()) {
			return ResponseEntity.ok(requestingUser); // Return full user object
		}

		// Step 4: Create a response with only the requested fields
		Map<String, Object> responseData = new ConcurrentHashMap<>();
		for (String field : fields) {
			switch (field.toLowerCase()) {
				case "userid":
					responseData.put("userId", requestingUser.getUserId());
					break;
				case "email":
					responseData.put("email", requestingUser.getEmail());
					break;
				case "addresses":
					responseData.put("addresses", requestingUser.getAddresses());
					break;
				case "createdat":
					responseData.put("createdAt", requestingUser.getCreatedAt());
					break;
				case "updatedat":
					responseData.put("updatedAt", requestingUser.getUpdatedAt());
					break;
				default:
					// Ignore unknown fields or return an error message (your choice)
					responseData.put(field, "Field not found");
			}
		}

		return ResponseEntity.ok(responseData);
	}

	@PatchMapping("/users")
	public ResponseEntity<?> updateUserProfile(@RequestBody Map<String, Object> request) {
		// Extract fields from the map
		
		List<String> newAddresses = (List<String>) request.get("addresses");

		User existingUser = (User) Lib.getRequestingUser(request, userRepository);
		if (existingUser == null) {
			return Lib.userRestResponseErr;
		}

		// Validate addresses
		if (newAddresses == null || newAddresses.isEmpty()) {
			return Lib.RestBadRequest("Invalid update request.");
		}

		for (String address : newAddresses) {
			if (!Lib.isValidAddress(address)) {
				return Lib.RestBadRequest("Invalid address: " + address);
			}
		}

		// Update user addresses
		existingUser.setAddresses(newAddresses);
		userRepository.save(existingUser);
		return Lib.RestOk("User profile updated successfully.");
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
