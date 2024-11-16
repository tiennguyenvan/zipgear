package com.example.demo.controller;

import com.example.demo.model.*;
import com.example.demo.repository.*;
import com.example.demo.service.Lib;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
	public ResponseEntity<?> getCart(@RequestParam(required = true) String email,
			@RequestParam(required = true) String code) {
		User user = Lib.getRequestingUser(email, code, userRepository);
		if (user == null) {
			return Lib.userRestResponseErr;
		}

		Cart cart = cartRepository.findByUser(user);
		if (cart == null) {
			cart = new Cart(user);
		}

		return ResponseEntity.ok(cart);
	}

	// add a product to carts
	@PutMapping("/carts/{productId}")
	public ResponseEntity<?> updateCartByProductId(
			@PathVariable Long productId,
			@RequestBody Map<String, Object> request) {

		User user = Lib.getRequestingUser(request, userRepository);
		if (user == null) {
			return Lib.userRestResponseErr;
		}

		Integer changeNumber = (Integer) request.get("changeNumber");
		if (changeNumber == null) {
			changeNumber = 1; // Default change number, could be positive or negative
		}

		// Fetch or create a cart for the user
		Cart cart = cartRepository.findByUser(user);
		if (cart == null) {
			cart = new Cart(user);
		}

		// Check if the product exists in the cart
		Optional<CartItem> cartItemOpt = cart.getItems().stream()
				.filter(item -> item.getProduct().getProductId().equals(productId))
				.findFirst();

		if (cartItemOpt.isPresent()) {
			CartItem cartItem = cartItemOpt.get();
			int updatedQuantity = cartItem.getQuantity() + changeNumber;

			if (updatedQuantity <= 0) {
				cart.getItems().remove(cartItem);
			} else {
				cartItem.setQuantity(updatedQuantity);
			}
			cartRepository.save(cart);
			return ResponseEntity.ok(cart);
		}

		// if the product is not available in the cart then change number is the
		// quantity
		if (changeNumber > 0) {

			Optional<Product> productOpt = productRepository.findById(productId);
			if (productOpt.isEmpty()) {
				return Lib.RestBadRequest("Product with ID " + productId + " not found");
			}

			Product product = productOpt.get();
			// Add the new item to the cart
			CartItem newCartItem = new CartItem(cart, product,
					changeNumber);
			cart.getItems().add(newCartItem);
			cartRepository.save(cart);
			return ResponseEntity.ok(cart);
		}
		// reduce a non-exist product to 0 does not change anything of the cart
		cartRepository.save(cart);
		return ResponseEntity.ok(cart);
	}

}
