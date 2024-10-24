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
}
