package com.example.demo.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.example.demo.config.OrderStatus;

@Entity
@Table(name = "orders")
public class Order {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long orderId;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id", nullable = false)
	private User user;

	// JSON string to store product details and quantities
	// we save this to make the information of order permanent
	// regardless of changes of product details later
	@Column(name = "product_list_json", columnDefinition = "TEXT", nullable = false)
	private String productListJson; 
	/*
	 * [
	 * 	{
	 * 		"title": "Wireless Headphones",
	 * 		"quantity": 2,
	 * 		"price": 59.99
	 * 	},
	 * 	{
	 * 		"title": "Smartphone Case",
	 * 		"quantity": 1,
	 * 		"price": 15.99
	 * 	},
	 * 	{
	 * 		"title": "Bluetooth Speaker",
	 * 		"quantity": 1,
	 * 		"price": 29.99
	 * 	}
	 * ]
	 */

	@Column(name = "delivery_address", nullable = false, length = 500)
	private String deliveryAddress;

	@Enumerated(EnumType.STRING)
	@Column(name = "orderStatus", nullable = false)
	private OrderStatus orderStatus;

	@Column(name = "total_price", nullable = false)
	private BigDecimal totalPrice;

	@Column(name = "created_at", nullable = false, updatable = false)
	private LocalDateTime createdAt;

	@Column(name = "updated_at", nullable = false)
	private LocalDateTime updatedAt;

	public Order() {
		this.createdAt = LocalDateTime.now();
		this.updatedAt = LocalDateTime.now();
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
}
