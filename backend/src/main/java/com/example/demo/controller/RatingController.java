package com.example.demo.controller;

import com.example.demo.model.Rating;
import com.example.demo.model.User;
import com.example.demo.model.Product;
import com.example.demo.repository.RatingRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.repository.ProductRepository;
import com.example.demo.service.Lib;

import org.eclipse.angus.mail.imap.Rights.Right;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api")
public class RatingController {

	@Autowired
	private RatingRepository ratingRepository;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private ProductRepository productRepository;

	// get all ratings or ratings from a user
	// if email & code are provided
	@GetMapping("/ratings")
	public ResponseEntity<?> getAllRatings(@RequestBody Map<String, Object> request) {
		// User user = Lib.getRequestingUser(request, userRepository);
		// if (user != null) {
		// return ResponseEntity.ok(ratingRepository.findByUser(user));
		// }
		return ResponseEntity.ok(ratingRepository.findAll());
	}

	// get all ratings of a product
	// if email & code are provided,
	// get only ratings from that user for the product
	@GetMapping("/ratings/{productId}")
	public ResponseEntity<?> getRatingsByProductId(@RequestBody Map<String, Object> request,
			@PathVariable Long productId) {
		Product product = productRepository.findById(productId).orElse(null);
		if (product == null) {
			return Lib.RestBadRequest("Invalid product ID: " + productId);
		}

		// User user = Lib.getRequestingUser(request, userRepository);
		// if (user != null) {
		// return ResponseEntity.ok(ratingRepository.findByUserAndProduct(user,
		// product));
		// }

		return ResponseEntity.ok(ratingRepository.findByProduct(product));
	}

	// POST: Create rating for a user on a product
	@PostMapping("/ratings/{productId}")
	public ResponseEntity<?> createRatingByProductId(@RequestBody Map<String, Object> request,
			@PathVariable Long productId) {
		Product product = productRepository.findById(productId).orElse(null);
		if (product == null) {
			return Lib.RestBadRequest("Invalid product ID: " + productId);
		}
		User user = Lib.getRequestingUser(request, userRepository);
		if (user == null) {
			return Lib.userRestResponse;
		}

		Integer ratingStars = (Integer) request.get("ratingStars");
		String ratingDescription = (String) request.get("ratingDescription");
		Rating rating = new Rating(product, user, ratingStars, ratingDescription);
		ratingRepository.save(rating);
		return Lib.RestOk("Rating created successfully.");
	}

	// PUT: Update rating for a user
	@PutMapping("/ratings/{productId}")
	public ResponseEntity<?> updateRatingByProductId(@RequestBody Map<String, Object> request, @PathVariable Long productId) {
		Product product = productRepository.findById(productId).orElse(null);
		if (product == null) {
			return Lib.RestBadRequest("Invalid product ID: " + productId);
		}
		User user = Lib.getRequestingUser(request, userRepository);
		if (user == null) {
			return Lib.userRestResponse;
		}

		Integer ratingStars = (Integer) request.get("ratingStars");
		String ratingDescription = (String) request.get("ratingDescription");
		List<Rating> ratings = ratingRepository.findByUserAndProduct(user, product);
		if (ratings.size() == 0) {
			return Lib.RestNotFound("Rating not found for product ID: " + productId);
		}

		Rating rating = ratings.get(0);
		rating.setRatingStars(ratingStars);
		rating.setRatingDescription(ratingDescription);
		ratingRepository.save(rating);
		return Lib.RestOk("Rating updated successfully.");
	}

	// DELETE rating by product ID
	@DeleteMapping("/ratings/{productId}")
	public ResponseEntity<?> deleteRatingByProductId(@RequestBody Map<String, String> request,
			@PathVariable Long productId) {
		Product product = productRepository.findById(productId).orElse(null);
		if (product == null) {
			return Lib.RestBadRequest("Invalid product ID: " + productId);
		}
		User user = Lib.getRequestingUser(request, userRepository);
		if (user == null) {
			return Lib.userRestResponse;
		}

		List<Rating> ratings = ratingRepository.findByUserAndProduct(user, product);
		if (ratings.size() == 0) {
			return Lib.RestNotFound("Rating not found for product ID: " + productId);
		}
		ratingRepository.delete(ratings.get(0));
		return Lib.RestOk("Rating deleted for product ID: " + productId);
	}
}
