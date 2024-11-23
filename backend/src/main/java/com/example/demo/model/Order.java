package com.example.demo.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.example.demo.config.OrderStatus;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Entity
@Table(name = "orders")
public class Order {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long orderId;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "user_id", nullable = false)
	private User user;

	// JSON string to store product details and quantities
	// we save this to make the information of order permanent
	// regardless of changes of product details later
	@Column(name = "product_list_json", columnDefinition = "TEXT", nullable = false)
	private String productListJson;
	/*
	 * [
	 * {
	 * "id": 1,
	 * "title": "Wireless Headphones",
	 * "quantity": 2,
	 * "price": 59.99
	 * },
	 * {
	 * "title": "Smartphone Case",
	 * "quantity": 1,
	 * "price": 15.99
	 * },
	 * {
	 * "title": "Bluetooth Speaker",
	 * "quantity": 1,
	 * "price": 29.99
	 * }
	 * ]
	 */

	@Column(name = "delivery_address", nullable = false, length = 500)
	private String deliveryAddress;

	@Enumerated(EnumType.STRING)
	@Column(name = "orderStatus", nullable = false)
	private OrderStatus orderStatus = OrderStatus.PROCESSING;

	@Column(name = "total_price", nullable = false)
	private BigDecimal totalPrice;

	@Column(name = "created_at", nullable = false, updatable = false)
	private LocalDateTime createdAt;

	@Column(name = "updated_at", nullable = false)
	private LocalDateTime updatedAt;

	public Order() {
		this.createdAt = LocalDateTime.now();
		this.updatedAt = LocalDateTime.now();
		this.orderStatus = OrderStatus.PROCESSING;
	}

	public Order(User user, String productListJson, String deliveryAddress, BigDecimal totalPrice) {
		this.user = user;
		this.productListJson = productListJson;
		this.deliveryAddress = deliveryAddress;
		this.orderStatus = OrderStatus.PROCESSING;
		this.totalPrice = totalPrice;
		this.createdAt = LocalDateTime.now();
		this.updatedAt = LocalDateTime.now();
	}

	@PreUpdate
	protected void onUpdate() {
		this.updatedAt = LocalDateTime.now();
	}

	// Getters and Setters
	public Long getOrderId() {
		return orderId;
	}

	public void setOrderId(Long orderId) {
		this.orderId = orderId;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public String getProductListJson() {
		return productListJson;
	}

	public void setProductListJson(String productListJson) {
		this.productListJson = productListJson;
	}

	public String getDeliveryAddress() {
		return deliveryAddress;
	}

	public void setDeliveryAddress(String deliveryAddress) {
		this.deliveryAddress = deliveryAddress;
	}

	public OrderStatus getOrderStatus() {
		return orderStatus;
	}

	public void setOrderStatus(OrderStatus status) {
		this.orderStatus = status;
	}

	public BigDecimal getTotalPrice() {
		return totalPrice;
	}

	public void setTotalPrice(BigDecimal totalPrice) {
		this.totalPrice = totalPrice;
	}

	public LocalDateTime getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(LocalDateTime createdAt) {
		this.createdAt = createdAt;
	}

	public LocalDateTime getUpdatedAt() {
		return updatedAt;
	}

	public void setUpdatedAt(LocalDateTime updatedAt) {
		this.updatedAt = updatedAt;
	}

	public void setProductList(List<CartItem> cartItems) {
		try {
			// Create a list of simplified maps representing the product details
			List<Map<String, Object>> simplifiedItems = cartItems.stream()
					.map(item -> {
						Map<String, Object> map = new HashMap<>();
						map.put("productId", item.getProduct().getProductId());
						map.put("title", item.getProduct().getTitle());
						map.put("quantity", item.getQuantity());
						map.put("price", item.getProduct().getPrice());
						return map;
					})
					.collect(Collectors.toList());

			// Serialize the simplified list of maps into JSON
			this.productListJson = new ObjectMapper().writeValueAsString(simplifiedItems);
		} catch (Exception e) {
			throw new RuntimeException("Failed to encode product list to JSON", e);
		}
	}

	public List<CartItem> getProductList() {
		try {
			// Deserialize JSON into a list of maps
			List<Map<String, Object>> simplifiedItems = new ObjectMapper()
					.readValue(this.productListJson, List.class);

			// Map the deserialized data back to CartItem objects
			return simplifiedItems.stream().map(map -> {
				Product product = new Product();
				product.setProductId(((Number) map.get("productId")).longValue());
				product.setTitle((String) map.get("title"));
				product.setPrice(new BigDecimal(map.get("price").toString()));

				CartItem cartItem = new CartItem();
				cartItem.setProduct(product);
				cartItem.setQuantity((Integer) map.get("quantity"));

				return cartItem;
			}).collect(Collectors.toList());
		} catch (Exception e) {
			throw new RuntimeException("Failed to decode product list from JSON", e);
		}
	}
}
