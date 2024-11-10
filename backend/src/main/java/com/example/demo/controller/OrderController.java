package com.example.demo.controller;

import com.example.demo.model.Cart;
import com.example.demo.model.CartItem;
import com.example.demo.model.Order;
import com.example.demo.model.User;
import com.example.demo.config.OrderStatus;
import com.example.demo.repository.CartRepository;
import com.example.demo.repository.OrderRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.Lib;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api")
public class OrderController {

	@Autowired
	private OrderRepository orderRepository;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private CartRepository cartRepository;

	// GET: Get all orders by a user
	@GetMapping("/orders")
	public ResponseEntity<?> getAllOrders(@RequestBody Map<String, Object> request) {
		User user = Lib.getRequestingUser(request, userRepository);
		if (user == null) {
			return Lib.userRestResponseErr;
		}

		return ResponseEntity.ok(orderRepository.findByUser(user));

		// @fixme: Only allow admin access all orders
		// List<Order> orders = orderRepository.findAll();
		// return ResponseEntity.ok(orders);
	}

	@GetMapping("/orders/{orderId}")
	public ResponseEntity<?> getOrderById(@PathVariable Long orderId) {
		Optional<Order> orderOpt = orderRepository.findById(orderId);
		if (orderOpt.isPresent()) {
			return ResponseEntity.ok(orderOpt.get());
		}
		return Lib.RestNotFound("Order not found");
	}

	@PostMapping("/orders")
	public ResponseEntity<?> createOrder(@RequestBody Map<String, Object> request) {
		User user = Lib.getRequestingUser(request, userRepository);
		if (user == null) {
			return Lib.userRestResponseErr;
		}

		// Retrieve the user's cart
		Cart cart = cartRepository.findByUser(user);
		if (cart == null || cart.getItems().isEmpty()) {
			return Lib.RestBadRequest("User does not have a cart or the cart is empty");
		}

		// Extract delivery address
		String deliveryAddress = (String) request.get("deliveryAddress");
		if (deliveryAddress == null || deliveryAddress.isBlank()) {
			return Lib.RestBadRequest("Delivery address is required.");
		}
		if (!Lib.isValidAddress(deliveryAddress)) {
			return Lib.RestBadRequest("Invalid delivery address.");
		}

		// Build the product list JSON from cart items
		StringBuilder productListJsonBuilder = new StringBuilder("[");
		BigDecimal totalPrice = BigDecimal.ZERO;

		for (CartItem item : cart.getItems()) {
			BigDecimal itemTotalPrice = item.getProduct().getPrice().multiply(BigDecimal.valueOf(item.getQuantity()));
			totalPrice = totalPrice.add(itemTotalPrice);

			productListJsonBuilder.append("{")
					.append("\"title\": \"").append(item.getProduct().getTitle()).append("\",")
					.append("\"quantity\": ").append(item.getQuantity()).append(",")
					.append("\"price\": ").append(item.getProduct().getPrice())
					.append("},");
		}

		// Remove the last comma and close the JSON array
		if (productListJsonBuilder.charAt(productListJsonBuilder.length() - 1) == ',') {
			productListJsonBuilder.deleteCharAt(productListJsonBuilder.length() - 1);
		}
		productListJsonBuilder.append("]");

		Order newOrder = new Order(user, productListJsonBuilder.toString(), deliveryAddress, totalPrice);
		orderRepository.save(newOrder);

		// Clear the user's cart after placing the order
		cart.getItems().clear();
		cartRepository.save(cart);

		return ResponseEntity.ok(newOrder);
	}

	// PUT: Update an existing order
	@PutMapping("/orders/{orderId}")
	public ResponseEntity<?> updateOrder(@PathVariable Long orderId, @RequestBody Map<String, Object> request) {
		User user = Lib.getRequestingUser(request, userRepository);
		if (user == null) {
			return Lib.userRestResponseErr;
		}

		Optional<Order> orderOpt = orderRepository.findById(orderId);
		if (orderOpt.isEmpty()) {
			return Lib.RestNotFound("Order not found.");
		}

		Order order = orderOpt.get();

		// Check if the order is in a cancelable state (PROCESSING)
		if (order.getOrderStatus() != OrderStatus.PROCESSING) {
			return ResponseEntity.badRequest()
					.body("Order cannot be canceled as it is already " + order.getOrderStatus());
		}

		order.setOrderStatus(OrderStatus.CANCELLED);
		return ResponseEntity.ok("Order has been canceled successfully.");

		// @fixme: admin can change status to other types
	}

	// DELETE: Delete an order by ID
	@DeleteMapping("/orders/{orderId}")
	public ResponseEntity<?> deleteOrder(@PathVariable Long orderId) {
		// @fixme: only admin can delete
		if (orderRepository.existsById(orderId)) {
			orderRepository.deleteById(orderId);
		}
		return ResponseEntity.ok("Order deleted successfully.");
	}
}
