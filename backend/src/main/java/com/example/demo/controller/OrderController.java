package com.example.demo.controller;

import com.example.demo.model.Cart;
import com.example.demo.model.CartItem;
import com.example.demo.model.Order;
import com.example.demo.model.Product;
import com.example.demo.model.Rating;
import com.example.demo.model.User;
import com.example.demo.config.OrderStatus;
import com.example.demo.repository.CartRepository;
import com.example.demo.repository.OrderRepository;
import com.example.demo.repository.ProductRepository;
import com.example.demo.repository.RatingRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.Env;
import com.example.demo.service.Lib;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api")
public class OrderController {

	@Autowired
	private OrderRepository orderRepository;

	@Autowired
	private ProductRepository productRepository;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private RatingRepository ratingRepository;

	@Autowired
	private CartRepository cartRepository;

	// GET: Get all orders by a user
	@GetMapping("/orders")
	public ResponseEntity<?> getAllOrders(@RequestParam(required = true) String email,
			@RequestParam(required = true) String code) {
		User user = Lib.getRequestingUser(email, code, userRepository);
		if (user == null) {
			return Lib.userRestResponseErr;
		}

		if (Lib.isUserAdmin()) {
			return ResponseEntity.ok(orderRepository.findAll());
		}

		return ResponseEntity.ok(orderRepository.findByUser(user));
	}

	@GetMapping("/orders/{orderId}")
	public ResponseEntity<?> getOrderById(@PathVariable Long orderId, @RequestParam(required = true) String email,
			@RequestParam(required = true) String code) {
		// Validate the user
		User user = Lib.getRequestingUser(email, code, userRepository);
		if (user == null) {
			return Lib.userRestResponseErr;
		}

		// Retrieve the order by ID
		Optional<Order> orderOpt = orderRepository.findById(orderId);
		if (orderOpt.isEmpty()) {
			return Lib.RestNotFound("Order not found");
		}

		Order order = orderOpt.get();

		// Ensure the user can only access their own orders, unless they are admin
		if (!order.getUser().equals(user) /* && !user.isAdmin() */) { // Assuming isAdmin() checks for admin status
			return Lib.RestUnauthorized("You are not authorized to view this order");
		}

		return ResponseEntity.ok(order);
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
		// StringBuilder productListJsonBuilder = new StringBuilder("[");
		BigDecimal totalPrice = BigDecimal.ZERO;

		for (CartItem item : cart.getItems()) {
			Product product = item.getProduct();
			if (product.getStock() < item.getQuantity()) {
				return Lib.RestBadRequest("Insufficient stock for product: " + product.getTitle());
			}
			product.setStock(product.getStock() - item.getQuantity());
        	productRepository.save(product);

			BigDecimal itemTotalPrice = item.getProduct().getPrice().multiply(BigDecimal.valueOf(item.getQuantity()));
			totalPrice = totalPrice.add(itemTotalPrice);

			// productListJsonBuilder.append("{")
			// .append("\"id\": \"").append(item.getProduct().getProductId()).append("\",")
			// .append("\"title\": \"").append(item.getProduct().getTitle()).append("\",")
			// .append("\"quantity\": ").append(item.getQuantity()).append(",")
			// .append("\"price\": ").append(item.getProduct().getPrice())
			// .append("},");
		}

		// Remove the last comma and close the JSON array
		// if (productListJsonBuilder.charAt(productListJsonBuilder.length() - 1) ==
		// ',') {
		// productListJsonBuilder.deleteCharAt(productListJsonBuilder.length() - 1);
		// }
		// productListJsonBuilder.append("]");

		// Order newOrder = new Order(user, productListJsonBuilder.toString(),
		// deliveryAddress, totalPrice);
		// Create a new order and use setProductList to encode the cart items
		Order newOrder = new Order();
		newOrder.setUser(user);
		newOrder.setProductList(cart.getItems()); // Encode cart items as JSON
		newOrder.setDeliveryAddress(deliveryAddress);
		newOrder.setTotalPrice(totalPrice);
		newOrder.setOrderStatus(OrderStatus.PROCESSING); // Set the default status

		orderRepository.save(newOrder);

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
		String newStatus = (String) request.get("orderStatus");
		if (newStatus.isEmpty()) {
			return Lib.RestBadRequest("Invalid Status");
		}

		Optional<Order> orderOpt = orderRepository.findById(orderId);

		if (orderOpt.isEmpty()) {
			return Lib.RestNotFound("Order not found.");
		}

		Order order = orderOpt.get();

		if (Lib.isUserAdmin()) {
			try {
				OrderStatus updatedStatus = OrderStatus.valueOf(newStatus);
				order.setOrderStatus(updatedStatus);

				// If the order is marked as DELIVERED, create placeholder reviews
				if (updatedStatus == OrderStatus.DELIVERED) {
					List<CartItem> productList = order.getProductList();
					for (CartItem cartItem : productList) {
						Long productId = cartItem.getProduct().getProductId();
						Product product = productRepository.findById(productId).orElse(null);

						if (product != null) {
							// Check if the user already has a review for this product
							if (ratingRepository.findByUserAndProduct(order.getUser(), product).isEmpty()) {
								Rating placeholderRating = new Rating(product, order.getUser(), 0, "");
								ratingRepository.save(placeholderRating);
							}
						}
					}
				}
			} catch (IllegalArgumentException e) {
				return Lib.RestBadRequest("Invalid order status.");
			}

			orderRepository.save(order);
			return Lib.RestOk("Order status updated successfully.");
		}

		if (order.getOrderStatus() != OrderStatus.PROCESSING) {
			return Lib.RestBadRequest("Order cannot be canceled as it is already " + order.getOrderStatus());
		}

		order.setOrderStatus(OrderStatus.CANCELLED);
		orderRepository.save(order);
		return Lib.RestOk("Order has been canceled successfully.");
	}

	// DELETE: Delete an order by ID
	// @DeleteMapping("/orders/{orderId}")
	// public ResponseEntity<?> deleteOrder(@PathVariable Long orderId) {
	// // @fixme: only admin can delete
	// if (orderRepository.existsById(orderId)) {
	// orderRepository.deleteById(orderId);
	// }
	// return Lib.RestOk("Order deleted successfully.");
	// }
}
