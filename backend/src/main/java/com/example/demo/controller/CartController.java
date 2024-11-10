package com.example.demo.controller;

import com.example.demo.model.*;
import com.example.demo.repository.*;
import com.example.demo.service.Lib;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api")
public class CartController {

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private CartRepository cartRepository;

	@Autowired
	private ProductRepository productRepository;

	// Get cart by user ID
	@GetMapping("/carts")
	public ResponseEntity<?> getCart(@RequestBody Map<String, String> request) {
		User user = Lib.getRequestingUser(request, userRepository);
		if (user == null) {
			return Lib.userRestResponse;
		}

		Optional<Cart> cart = cartRepository.findByUser(user);
		if (cart.isEmpty()) {
			return Lib.RestNotFound("Cart not found");
		}

		return ResponseEntity.ok(cart.get());
	}

	@PutMapping("/carts")
	public Object updateCart(@RequestBody Map<String, Object> request) {
		User user = Lib.getRequestingUser(request, userRepository);
		if (user == null) {
			return Lib.userRestResponse;
		}

		// Check if the cart exists; if not, create a new one
		Cart cart = cartRepository.findByUser(user).orElse(new Cart(user));

		// Extract items list from request
		List<Map<String, Object>> items = (List<Map<String, Object>>) request.get("items");
		if (items == null) {
			return Lib.RestBadRequest("No items provided");
		}

		// Clear existing items if any
		cart.getItems().clear();

		// Add new items or update existing ones
		for (Map<String, Object> itemData : items) {
			Long productId = ((Number) itemData.get("productId")).longValue();
			Integer quantity = (Integer) itemData.get("quantity");

			Product product = productRepository.findById(productId).orElse(null);
			if (product == null) {
				return Lib.RestBadRequest("Invalid product ID: " + productId);
			}

			// Add item to cart
			CartItem cartItem = new CartItem(cart, product, quantity);
			cart.getItems().add(cartItem);
		}

		cartRepository.save(cart);
		return ResponseEntity.ok(cart);
	}

}
